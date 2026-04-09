/**
 * 批量下载所有商品占位图脚本 (ES Module 版本)
 * 下载所有SQL文件中使用的本地图片路径对应的占位图
 * 使用 Node.js 原生 https 和 fs/promises 模块
 */

import https from 'https';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

// 获取 __dirname (ES Module 中需要手动获取)
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// 配置
const IMAGES_DIR = path.join(__dirname, 'public', 'images', 'goods');

// 定义所有需要下载的图片（按类别组织）
const IMAGES_TO_DOWNLOAD = [
  // ========== Brand 品牌图片 ==========
  { url: 'https://picsum.photos/500/500?random=10', filename: 'brand-apple.jpg', desc: 'Apple品牌' },
  { url: 'https://picsum.photos/500/500?random=11', filename: 'brand-sony.jpg', desc: 'Sony品牌' },
  { url: 'https://picsum.photos/500/500?random=12', filename: 'brand-logitech.jpg', desc: 'Logitech品牌' },
  { url: 'https://picsum.photos/500/500?random=13', filename: 'brand-dji.jpg', desc: 'DJI品牌' },
  { url: 'https://picsum.photos/500/500?random=14', filename: 'brand-samsung.jpg', desc: 'Samsung品牌' },
  { url: 'https://picsum.photos/500/500?random=15', filename: 'brand-estee-lauder.jpg', desc: 'Estée Lauder品牌' },
  { url: 'https://picsum.photos/500/500?random=16', filename: 'brand-nike.jpg', desc: 'Nike品牌' },
  { url: 'https://picsum.photos/500/500?random=17', filename: 'brand-adidas.jpg', desc: 'Adidas品牌' },
  { url: 'https://picsum.photos/500/500?random=18', filename: 'brand-zara.jpg', desc: 'Zara品牌' },
  { url: 'https://picsum.photos/500/500?random=19', filename: 'brand-huawei.jpg', desc: 'Huawei品牌' },
  { url: 'https://picsum.photos/500/500?random=20', filename: 'brand-xiaomi.jpg', desc: 'Xiaomi品牌' },

  // ========== SPU 商品图片 - iPhone (SPU001) ==========
  { url: 'https://picsum.photos/800/800?random=101', filename: 'spu001-1.jpg', desc: 'iPhone主图1' },
  { url: 'https://picsum.photos/800/800?random=102', filename: 'spu001-2.jpg', desc: 'iPhone主图2' },

  // ========== SPU 商品图片 - MacBook (SPU002) ==========
  { url: 'https://picsum.photos/800/600?random=201', filename: 'spu002-1.jpg', desc: 'MacBook主图1' },
  { url: 'https://picsum.photos/800/600?random=202', filename: 'spu002-2.jpg', desc: 'MacBook主图2' },

  // ========== SPU 商品图片 - Sony耳机 (SPU003) ==========
  { url: 'https://picsum.photos/800/800?random=301', filename: 'spu003-1.jpg', desc: 'Sony耳机主图1' },
  { url: 'https://picsum.photos/800/800?random=302', filename: 'spu003-2.jpg', desc: 'Sony耳机主图2' },

  // ========== SPU 商品图片 - Logitech键盘 (SPU004) ==========
  { url: 'https://picsum.photos/800/600?random=401', filename: 'spu004-1.jpg', desc: 'Logitech键盘主图1' },
  { url: 'https://picsum.photos/800/600?random=402', filename: 'spu004-2.jpg', desc: 'Logitech键盘主图2' },

  // ========== SPU 商品图片 - 护肤品 (SPU005) ==========
  { url: 'https://picsum.photos/800/800?random=501', filename: 'spu005-1.jpg', desc: '护肤品主图1' },
  { url: 'https://picsum.photos/800/800?random=502', filename: 'spu005-2.jpg', desc: '护肤品主图2' },

  // ========== SPU 商品图片 - 鞋子 (SPU006) ==========
  { url: 'https://picsum.photos/800/800?random=601', filename: 'spu006-1.jpg', desc: 'Air Jordan主图1' },
  { url: 'https://picsum.photos/800/800?random=602', filename: 'spu006-2.jpg', desc: 'Air Jordan主图2' },

  // ========== SPU 商品图片 - 跑鞋 (SPU007) ==========
  { url: 'https://picsum.photos/800/800?random=701', filename: 'spu007-1.jpg', desc: 'Ultraboost主图1' },
  { url: 'https://picsum.photos/800/800?random=702', filename: 'spu007-2.jpg', desc: 'Ultraboost主图2' },

  // ========== SPU 商品图片 - 服装 (SPU008) ==========
  { url: 'https://picsum.photos/800/1000?random=801', filename: 'spu008-1.jpg', desc: '卫衣主图1' },
  { url: 'https://picsum.photos/800/1000?random=802', filename: 'spu008-2.jpg', desc: '卫衣主图2' },

  // ========== 其他手机SPU图片 (mall_goods_init.sql) ==========
  { url: 'https://picsum.photos/800/800?random=901', filename: 'iphone-huawei-1.jpg', desc: 'Huawei手机图1' },
  { url: 'https://picsum.photos/800/800?random=902', filename: 'iphone-huawei-2.jpg', desc: 'Huawei手机图2' },
  { url: 'https://picsum.photos/800/800?random=903', filename: 'iphone-xiaomi-1.jpg', desc: 'Xiaomi手机图1' },
  { url: 'https://picsum.photos/800/800?random=904', filename: 'iphone-xiaomi-2.jpg', desc: 'Xiaomi手机图2' },

  // ========== SKU单品图片 ==========
  { url: 'https://picsum.photos/800/800?random=1101', filename: 'sku-iphone-256.jpg', desc: 'iPhone 256GB SKU' },
  { url: 'https://picsum.photos/800/800?random=1102', filename: 'sku-iphone-512.jpg', desc: 'iPhone 512GB SKU' },
  { url: 'https://picsum.photos/800/800?random=1103', filename: 'sku-huawei-512.jpg', desc: 'Huawei 512GB SKU' },
  { url: 'https://picsum.photos/800/800?random=1104', filename: 'sku-xiaomi-256.jpg', desc: 'Xiaomi 256GB SKU' },

  // ========== 订单商品图片 ==========
  { url: 'https://picsum.photos/800/800?random=1201', filename: 'order-iphone15.jpg', desc: '订单iPhone15' },
  { url: 'https://picsum.photos/800/800?random=1202', filename: 'order-airpods.jpg', desc: '订单AirPods' },
  { url: 'https://picsum.photos/800/800?random=1203', filename: 'order-mate60.jpg', desc: '订单Mate60' },
  { url: 'https://picsum.photos/800/800?random=1204', filename: 'order-xiaomi14.jpg', desc: '订单Xiaomi14' },
  { url: 'https://picsum.photos/800/800?random=1205', filename: 'order-ipad.jpg', desc: '订单iPad' },

  // ========== 通用占位图（备用） ==========
  { url: 'https://picsum.photos/500/500?random=1', filename: 'pic1.jpg', desc: '通用占位图1' },
  { url: 'https://picsum.photos/500/500?random=2', filename: 'pic2.jpg', desc: '通用占位图2' },
  { url: 'https://picsum.photos/500/500?random=3', filename: 'pic3.jpg', desc: '通用占位图3' },
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
 * 下载单个图片（支持重定向）
 */
function downloadImage(url, filepath, maxRedirects = 5) {
  return new Promise((resolve, reject) => {
    // 检查文件是否已存在
    if (fs.existsSync(filepath)) {
      const stats = fs.statSync(filepath);
      resolve({ skipped: true, size: stats.size });
      return;
    }

    const file = fs.createWriteStream(filepath);

    https.get(url, { timeout: 30000 }, (response) => {
      // 处理重定向
      if ((response.statusCode === 302 || response.statusCode === 301) && maxRedirects > 0) {
        const redirectUrl = response.headers.location;
        file.close();
        if (fs.existsSync(filepath)) {
          fs.unlinkSync(filepath);
        }

        // 递归处理重定向
        downloadImage(redirectUrl, filepath, maxRedirects - 1)
          .then(resolve)
          .catch(reject);
        return;
      }

      if (response.statusCode !== 200) {
        file.close();
        if (fs.existsSync(filepath)) {
          fs.unlinkSync(filepath);
        }
        reject(new Error(`HTTP ${response.statusCode}`));
        return;
      }

      response.pipe(file);

      file.on('finish', () => {
        file.close();
        const stats = fs.statSync(filepath);
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
  console.log('  批量下载商品占位图脚本');
  console.log('========================================');
  console.log(`[INFO] 共需下载: ${IMAGES_TO_DOWNLOAD.length} 个图片\n`);

  try {
    // 1. 确保目录存在
    ensureDirectoryExists(IMAGES_DIR);
    console.log('');

    // 2. 统计分类
    const categories = {
      'brand': IMAGES_TO_DOWNLOAD.filter(i => i.filename.startsWith('brand-')),
      'spu': IMAGES_TO_DOWNLOAD.filter(i => i.filename.startsWith('spu')),
      'other': IMAGES_TO_DOWNLOAD.filter(i => !i.filename.startsWith('brand-') && !i.filename.startsWith('spu'))
    };

    console.log('[INFO] 图片分类统计:');
    console.log(`  - 品牌图片: ${categories.brand.length} 个`);
    console.log(`  - 商品图片: ${categories.spu.length} 个`);
    console.log(`  - 其他图片: ${categories.other.length} 个\n`);

    // 3. 下载所有图片
    let downloadedCount = 0;
    let skippedCount = 0;
    let failedCount = 0;
    const failedList = [];

    for (let i = 0; i < IMAGES_TO_DOWNLOAD.length; i++) {
      const image = IMAGES_TO_DOWNLOAD[i];
      const filepath = path.join(IMAGES_DIR, image.filename);

      process.stdout.write(`[${i + 1}/${IMAGES_TO_DOWNLOAD.length}] ${image.desc} (${image.filename}) ... `);

      try {
        const result = await downloadImage(image.url, filepath);
        if (result.skipped) {
          skippedCount++;
          console.log('已存在');
        } else {
          downloadedCount++;
          console.log(`下载成功 (${(result.size / 1024).toFixed(1)} KB)`);
        }
      } catch (error) {
        failedCount++;
        failedList.push({ filename: image.filename, error: error.message });
        console.log(`失败: ${error.message}`);
      }
    }

    // 4. 输出汇总
    console.log('\n========================================');
    console.log('  下载完成汇总');
    console.log('========================================');
    console.log(`  成功下载: ${downloadedCount} 个`);
    console.log(`  已存在跳过: ${skippedCount} 个`);
    console.log(`  失败: ${failedCount} 个`);

    if (failedCount > 0) {
      console.log('\n  失败列表:');
      failedList.forEach(item => {
        console.log(`    - ${item.filename}: ${item.error}`);
      });
    }

    console.log(`\n  图片目录: ${IMAGES_DIR}`);
    console.log('========================================');

    // 5. 列出目录内容
    const files = fs.readdirSync(IMAGES_DIR);
    console.log(`\n  目录文件列表 (共 ${files.length} 个):`);

    // 按类别分组显示
    const brandFiles = files.filter(f => f.startsWith('brand-')).sort();
    const spuFiles = files.filter(f => f.startsWith('spu')).sort();
    const otherFiles = files.filter(f => !f.startsWith('brand-') && !f.startsWith('spu')).sort();

    if (brandFiles.length > 0) {
      console.log(`\n  品牌图片 (${brandFiles.length}个):`);
      brandFiles.forEach(file => {
        const stats = fs.statSync(path.join(IMAGES_DIR, file));
        console.log(`    ✓ ${file} (${(stats.size / 1024).toFixed(1)} KB)`);
      });
    }

    if (spuFiles.length > 0) {
      console.log(`\n  商品图片 (${spuFiles.length}个):`);
      spuFiles.forEach(file => {
        const stats = fs.statSync(path.join(IMAGES_DIR, file));
        console.log(`    ✓ ${file} (${(stats.size / 1024).toFixed(1)} KB)`);
      });
    }

    console.log('\n[SUCCESS] 所有图片准备就绪！');
    console.log('[TIP] 现在可以执行SQL初始化，所有图片将使用本地路径');
    process.exit(failedCount > 0 ? 1 : 0);

  } catch (error) {
    console.error('\n[ERROR] 脚本执行失败:', error.message);
    process.exit(1);
  }
}

// 执行主函数
main();
