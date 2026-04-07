import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    // 放行本地测试域名，解决 DNS 重绑定保护报错
    allowedHosts: ['shop1.whmall.test', 'shop2.whmall.test', 'shop1.localhost', 'shop2.localhost', 'localhost'],
    cors: true,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8088',  // 网关地址
        changeOrigin: true,
        // 保留原始路径转发到网关
        // rewrite: (path) => path.replace(/^\/api/, '/api'),
        // 允许跨域和不同源
        secure: false,
        ws: true
      }
    }
  }
})
