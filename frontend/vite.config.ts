import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 3000,
    proxy: {
      '/api/v1': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true,
        rewrite: (path) => path // 保持原始路径，因为后端需要/api/v1前缀
      }
    }
  },
  resolve: {
    alias: {
      '@': '/src'
    }
  }
})