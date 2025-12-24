import { createApp } from 'vue' // 从 Vue 引入创建应用的方法
import { createPinia } from 'pinia' // 引入 Pinia，用于全局状态管理
import App from './App.vue' // 引入根组件 App
import router from './router' // 引入前端路由配置

// 导入请求工具（初始化 axios 实例，统一处理 token、错误等）
import '@campus/common/utils/request' // 使用 common 包中的请求封装

// 导入公共样式（重置 + 变量 + 通用布局工具类），保证 web 端与 app 端视觉统一
import '@campus/common/styles/index.scss' // 使用 common 中的全局样式入口文件

// 引入 Font Awesome 图标样式（通过 npm 包统一管理，避免各页面重复 @import）
import '@fortawesome/fontawesome-free/css/all.min.css'

const app = createApp(App) // 创建 Vue 应用实例
const pinia = createPinia() // 创建 Pinia 实例用于状态管理

app.use(pinia) // 在应用中安装 Pinia 插件
app.use(router) // 在应用中安装路由插件

app.mount('#app') // 将应用挂载到 index.html 中 id 为 app 的根节点

// 打印环境信息到浏览器控制台，方便开发调试
console.log('🚀 Vue 3 + Vite 启动成功！') // 打印启动成功提示
console.log('📡 API地址:', import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080') // 打印当前 API 地址
console.log('📚 API文档:', import.meta.env.VITE_API_DOCS_URL || 'http://localhost:8080/doc.html') // 打印文档地址
