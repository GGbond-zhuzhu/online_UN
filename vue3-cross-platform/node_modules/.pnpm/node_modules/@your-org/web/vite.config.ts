import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      '@campus/common': path.resolve(__dirname, '../common/src'),
      '@your-org/common': path.resolve(__dirname, '../common/src') // 兼容旧别名
    }
  },
  server: {
    port: 5173,
    open: true,
    cors: true,
    // 配置代理，解决跨域问题
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端API地址
        changeOrigin: true, // 改变请求头中的origin
        rewrite: (path) => path.replace(/^\/api/, '/api'), // 保持路径不变
        // 可以添加更多配置
        configure: (proxy, _options) => {
          proxy.on('error', (err, _req, _res) => {
            console.log('代理错误:', err);
          });
          proxy.on('proxyReq', (proxyReq, req, _res) => {
            console.log('代理请求:', req.method, req.url);
          });
        }
      },
      // 代理Knife4j文档
      '/doc.html': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/v3/api-docs': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  // 禁用依赖扫描（解决 TSConfig 解析警告）
  optimizeDeps: {
    disabled: true
  },
  // 定义环境变量
  define: {
    // 如果.env文件被忽略，可以在这里定义默认值
    'import.meta.env.VITE_API_BASE_URL': JSON.stringify(
      process.env.VITE_API_BASE_URL || 'http://localhost:8080'
    )
  }
});
