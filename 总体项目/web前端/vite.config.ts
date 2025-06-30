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
      }
    },
    port: 5173,
    open: true
  },
  // 确保静态资源能正确访问
  publicDir: 'static'
});