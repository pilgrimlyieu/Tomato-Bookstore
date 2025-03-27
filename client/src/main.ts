// main.ts
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)

// ✅ 引入并恢复登录状态（必须在 router 前）
import { useAuthStore } from '@/stores/auth'
const authStore = useAuthStore()
authStore.restore()

app.use(ElementPlus)
app.use(router)

app.mount('#app')
