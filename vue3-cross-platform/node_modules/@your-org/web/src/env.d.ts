/// <reference types="vite/client" /> // 引入 Vite 提供的类型声明，支持 import.meta.env 等语法

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_API_DOCS_URL: string
  readonly VITE_APP_TITLE: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv // 扩展 ImportMeta 接口，显式声明 env 字段
}

// 声明 UniApp 全局 uni 对象，方便在 web 端复用 common 包中依赖 uni 的工具函数时通过类型检查
declare const uni: any // UniApp 全局对象，这里使用 any 简化类型，实际由运行时环境提供具体实现
