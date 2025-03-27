// src/stores/auth.ts
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    username: ''
  }),
  actions: {
    login(username: string) {
      this.isLoggedIn = true
      this.username = username
      localStorage.setItem('token', 'true') // ✅ 存储登录标志
    },
    logout() {
      this.isLoggedIn = false
      this.username = ''
      localStorage.removeItem('token')
    },
    restore() {
      // ✅ 启动时从 localStorage 读取
      const token = localStorage.getItem('token')
      this.isLoggedIn = token === 'true'
    }
  }
})
