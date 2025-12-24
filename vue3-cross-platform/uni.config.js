import { defineConfig } from '@dcloudio/uvm';
import path from 'path';

// Windows 绝对路径适配（关键！）
const rootPath = path.resolve(__dirname); // 项目根目录
const appPath = path.resolve(rootPath, 'packages/app');
const commonPath = path.resolve(rootPath, 'packages/common/src');

export default defineConfig({
  compiler: 'vite',
  vueVersion: '3',
  app: {
    entry: path.resolve(appPath, 'src/main.js'),
    pages: path.resolve(appPath, 'src/pages.json')
  },
  mp: {
    weixin: {
      output: path.resolve(appPath, 'unpackage/dist/dev/mp-weixin'),
      buildOutput: path.resolve(appPath, 'unpackage/dist/build/mp-weixin'),
      usingComponents: true
    }
  },
  // 核心：UniApp CLI 识别的别名（与 vite.config.js 对齐）
  alias: {
    '@': path.resolve(appPath, 'src'),
    '@/app': path.resolve(appPath, 'src'),
    '@/common': commonPath, // 绝对路径指向 common/src
    '@campus/common': commonPath, // 兼容原有别名
    '@your-org/common': commonPath // 兼容原有别名
  },
  css: {
    globalStyle: path.resolve(commonPath, 'styles/index.scss')
  }
});