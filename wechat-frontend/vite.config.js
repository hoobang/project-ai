import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  define: {
    // 为浏览器环境提供global变量的替代方案
    'global': 'window'
  },
  server: {
    // 配置代理，解决跨域问题
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true
        // 保留/api前缀以匹配后端contextPath
      }
    }
  }
})
