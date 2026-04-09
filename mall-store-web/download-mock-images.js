/**
 * 商品占位图下载脚本
 * 自动下载占位图片到本地静态资源目录
 * 使用 Node.js 原生 https 和 fs 模块，无需 npm install
 */

const https = require('https');
const fs = require('fs');
const path = require('path');

// 配置
const IMAGES_DIR = path.join(__dirname, 'public', 'images', 'goods');
const IMAGES_TO_DOWNLOAD = [
  { url: 'https://picsum.photos/500/500?random=1', filename: 'pic1.jpg' },
  { url: 'https://picsum.photos/500/500?random=2', filename: 'pic2.jpg' },
  { url: 'https://picsum.photos/500/500?random=3', filename: 'pic3.jpg' }
];

/**
 * 确保目录存在
 */
function ensureDirectoryExists(dirPath) {
  if (!fs.existsSync(dirPath)) {
    fs.mkdirSync(dirPath, { recursive: true });
    console.log(`[INFO] 创建目录: ${dirPath}`);
  } else {
    console.log(`[INFO] 目录已存在: ${dirPath}`);
  }
}

/**
 * 下载单个图片
 */
function downloadImage(url, filepath) {
  return new Promise((resolve, reject) => {
    // 检查文件是否已存在
    if (fs.existsSync(filepath)) {
      console.log(`[SKIP] 文件已存在: ${path.basename(filepath)}`);
      resolve({ skipped: true });
      return;
    }

    console.log(`[DOWNLOAD] 开始下载: ${url}`);
    console.log(`[DOWNLOAD] 保存路径: ${filepath}`);

    const file = fs.createWriteStream(filepath);
    
    https.get(url, { timeout: 30000 }, (response) => {
      // 处理重定向 (picsum 会重定向到实际图片地址)
      if (response.statusCode === 302 || response.statusCode === 301) {
        const redirectUrl = response.headers.location;
        console.log(`[REDIRECT] 跟随重定向: ${redirectUrl}`);
        file.close();
        fs.unlinkSync(filepath); // 删除空文件
        
        // 递归处理重定向
        downloadImage(redirectUrl, filepath).then(resolve).catch(reject);
        return;
      }

      // 检查响应状态
      if (response.statusCode !== 200) {
        file.close();
        fs.unlinkSync(filepath);
        reject(new Error(`HTTP ${response.statusCode}: ${url}`));
        return;
      }

      // 下载数据
      response.pipe(file);

      file.on('finish', () => {
        file.close();
        const stats = fs.statSync(filepath);
        console.log(`[SUCCESS] 下载完成: ${path.basename(filepath)} (${(stats.size / 1024).toFixed(2)} KB)`);
        resolve({ skipped: false, size: stats.size });
      });

      file.on('error', (err) => {
        file.close();
        if (fs.existsSync(filepath)) {
          fs.unlinkSync(filepath);
        }
        reject(err);
      });

    }).on('error', (err) => {
      file.close();
      if (fs.existsSync(filepath)) {
        fs.unlinkSync(filepath);
      }
      reject(err);
    });
  });
}

/**
 * 主函数
 */
async function main() {
  console.log('========================================');
  console.log('  商品占位图下载脚本');
  console.log('========================================\n');

  try {
    // 1. 确保目录存在
    ensureDirectoryExists(IMAGES_DIR);
    console.log('');

    // 2. 下载所有图片
    let downloadedCount = 0;
    let skippedCount = 0;

    for (const image of IMAGES_TO_DOWNLOAD) {
      const filepath = path.join(IMAGES_DIR, image.filename);
      try {
        const result = await downloadImage(image.url, filepath);
        if (result.skipped) {
          skippedCount++;
        } else {
          downloadedCount++;
        }
      } catch (error) {
        console.error(`[ERROR] 下载失败 ${image.filename}: ${error.message}`);
      }
      console.log(''); // 空行分隔
    }

    // 3. 输出汇总
    console.log('========================================');
    console.log('  下载完成汇总');
    console.log('========================================');
    console.log(`  新下载: ${downloadedCount} 个`);
    console.log(`  已跳过: ${skippedCount} 个`);
    console.log(`  总计: ${IMAGES_TO_DOWNLOAD.length} 个`);
    console.log(`\n  图片目录: ${IMAGES_DIR}`);
    console.log('========================================');

    // 4. 列出目录内容
    const files = fs.readdirSync(IMAGES_DIR);
    console.log('\n  目录文件列表:');
    files.forEach(file => {
      const stats = fs.statSync(path.join(IMAGES_DIR, file));
      console.log(`    - ${file} (${(stats.size / 1024).toFixed(2)} KB)`);
    });

    console.log('\n[SUCCESS] 所有图片准备就绪！');
    process.exit(0);

  } catch (error) {
    console.error('\n[ERROR] 脚本执行失败:', error.message);
    process.exit(1);
  }
}

// 执行主函数
main();
