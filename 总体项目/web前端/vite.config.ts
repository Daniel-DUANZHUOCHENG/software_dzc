import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': './src'
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:9049/',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      // 代理后端上传的轮播图
      '/carousel': {
        target: 'http://localhost:9049/',
        changeOrigin: true
      }
    },
    port: 5173,
    open: true
  },
  // 使用默认的 public 目录
  publicDir: 'public'
});