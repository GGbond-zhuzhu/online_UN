import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// 导入请求工具（初始化axios实例）
import '@campus/common/utils/request'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

app.mount('#app')

// 打印环境信息
console.log('🚀 Vue 3 + Vite 启动成功！')
console.log('📡 API地址:', import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080')
console.log('📚 API文档:', import.meta.env.VITE_API_DOCS_URL || 'http://localhost:8080/doc.html')
