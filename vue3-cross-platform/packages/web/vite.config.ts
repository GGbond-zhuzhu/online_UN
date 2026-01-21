import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
  plugins: [vue()],
  // 添加更详细的日志输出，帮助调试
  logLevel: 'info',
  resolve: {
    alias: [
      {
        find: '@',
        replacement: path.resolve(__dirname, 'src')
      },
      {
        // 指向common包的源码目录，Vite会自动解析index.ts
        find: /^@campus\/common$/,
        replacement: path.resolve(__dirname, '../common/src/index.ts')
      },
      {
        // 处理common包内部的子路径导入（如 @campus/common/api/auth）
        find: /^@campus\/common\/(.+)$/,
        replacement: path.resolve(__dirname, '../common/src/$1')
      },
      {
        // 兼容旧别名
        find: /^@your-org\/common$/,
        replacement: path.resolve(__dirname, '../common/src/index.ts')
      },
      {
        find: /^@your-org\/common\/(.+)$/,
        replacement: path.resolve(__dirname, '../common/src/$1')
      },
      {
        // 兼容旧别名：@common/*
        find: /^@common\/(.+)$/,
        replacement: path.resolve(__dirname, '../common/src/$1')
      }
    ],
    // 确保Vite能正确解析TypeScript文件
    extensions: ['.mjs', '.js', '.mts', '.ts', '.jsx', '.tsx', '.json', '.vue'],
    // 添加dedupe配置，避免重复打包
    dedupe: ['vue', 'pinia', 'vue-router']
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
  // 优化依赖预构建配置
  optimizeDeps: {
    // 包含common包，确保Vite能正确预构建
    // Vite会自动处理common包内部的依赖关系
    include: [
      '@campus/common',
      '@your-org/common',
      'vue',
      'pinia',
      'vue-router'
    ],
    // 排除不需要预构建的包
    exclude: [],
    // 强制重新构建common包
    force: false
  },
  // 构建配置
  build: {
    commonjsOptions: {
      // 确保commonjs模块能正确转换
      include: [/common/, /node_modules/]
    }
  },
  // 定义全局常量与环境变量
  define: {
    // 修复部分第三方库（如 sockjs-client）在浏览器环境中直接访问 Node.js 的 global 导致
    // "ReferenceError: global is not defined" 的问题：将代码中的 global 替换为 window
    global: 'window'
  }
});
