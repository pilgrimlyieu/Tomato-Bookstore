// src/router/router-meta.d.ts

import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    /**
     * 页面标题，会被设置为 document.title
     */
    title?: string

    /**
     * 权限控制：允许访问此页面的角色（如 ['manager', 'user']）
     */
    permission?: string[]
  }
}
