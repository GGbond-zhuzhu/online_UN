# vue3-cross-platform（校园集成系统）
## 技术栈
- 双端前端：UniApp+Vue3+TS+Pinia（App）/ Vue3+Vite+TS+VueRouter（Web）
- 后端：SpringBoot3.x+MyBatis-Plus+MySQL+JWT+Knife4j

## 核心结构
- packages/app：UniApp 移动端子包（适配小程序/APP）
- packages/common：双端公共核心（组件/API/状态/权限）
- packages/web：Vue3+Vite 网页端子包（PC 端）

## 脚本说明
- pnpm dev:app：启动 App 端开发模式
- pnpm dev:web：启动 Web 端开发模式
- pnpm build:app：构建 App 端（UniApp 打包）
- pnpm build:web：构建 Web 端（Vite 打包）
