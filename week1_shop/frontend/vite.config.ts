import { fileURLToPath, URL } from 'node:url'
import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
      '/health': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },

      // 后端的api是http://127.0.0.1:8080/api/user/list
      // 没有配置rewrite时, 代理只是把命中的主机前缀换成target, 变成了http://127.0.0.1:8080/api/user/list
      
      // 如果配置了rewrite比如rewrite: (path) => path.replace(/^\/api/, ''),
      // 会把/api进行rewrite, 变成空字符串, 前缀变成target, 最终变成了http://127.0.0.1:8080/user/list
    },
  },
})
