import {createApp} from 'vue'
import {createPinia} from 'pinia'
import App from './App.vue'
import router from './router'

// 创建Vue应用实例
const app = createApp(App)

// 使用状态管理
app.use(createPinia())

// 使用路由
app.use(router)

// 挂载到DOM
app.mount('#app')
