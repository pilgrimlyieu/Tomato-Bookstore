# 前端开发指南

本文档详细介绍了番茄商城项目的前端架构、开发规范和最佳实践。

## 技术栈概览

番茄商城前端采用以下技术栈：

- **核心框架**：[Vue 3](https://vuejs.org/) + [TypeScript](https://www.typescriptlang.org/)
- **构建工具**：[Vite](https://vitejs.dev/)
- **包管理器**：[Bun](https://bun.sh/)
- **UI 框架**：[Element Plus](https://element-plus.org/)
- **CSS 框架**：[TailwindCSS](https://tailwindcss.com/)
- **状态管理**：[Pinia](https://pinia.vuejs.org/)
- **路由**：[Vue Router](https://router.vuejs.org/)
- **HTTP 客户端**：[Axios](https://axios-http.com/)
- **表单验证**：[VeeValidate](https://vee-validate.logaretm.com/)
- **测试**：[Vitest](https://vitest.dev/)

## 项目结构

```
client/
├── public/               # 静态资源
├── src/
│   ├── assets/           # 资源文件（图片、字体等）
│   ├── components/       # 可复用组件
│   │   ├── common/       # 通用组件
│   │   ├── layout/       # 布局组件
│   │   └── ...
│   ├── composables/      # 组合式函数
│   ├── config/           # 配置文件
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia 状态
│   ├── types/            # TypeScript 类型定义
│   ├── utils/            # 工具函数
│   ├── views/            # 页面组件
│   ├── App.vue           # 根组件
│   ├── main.ts           # 入口文件
│   └── ...
├── .eslintrc.js          # ESLint 配置
├── tailwind.config.js    # Tailwind 配置
├── tsconfig.json         # TypeScript 配置
├── vite.config.ts        # Vite 配置
└── ...
```

## 开发环境设置

### 安装依赖

```bash
# 安装 Bun（如果尚未安装）
$ powershell -Command "iwr https://bun.sh | iex"
# 安装项目依赖
$ cd client
$ bun install
```

### 启动开发服务器

```bash
$ bun run dev
```

服务器将在 http://localhost:5173 启动。

### 构建项目

```bash
$ bun run build
```

构建后的文件将位于 `dist` 目录中。

## 开发规范

### 命名约定

- **文件名**：
    - 组件文件：使用 PascalCase，如 `BookCard.vue`
    - 非组件文件：使用 kebab-case，如 `api-service.ts`
- **组件名**：
    - 使用 PascalCase，如 `BookCard`
- **变量名**：
    - 使用 camelCase，如 `bookTitle`
    - 布尔值前缀使用 `is`、`has` 或 `should`，如 `isLoading`
- **常量**：
    - 使用全大写下划线分隔，如 `MAX_ITEMS_PER_PAGE`

### 注释规范

代码应当尽可能自解释，但关键逻辑应添加适当注释：

```typescript
/**
 * 计算购物车中商品的总价
 * @param {CartItem[]} items - 购物车商品列表
 * @returns {number} 总价（不含运费和折扣）
 */
function calculateTotal(items: CartItem[]): number {
  // ...
}
```

## 组件开发指南

### 组件结构

我们使用 Vue 3 的组合式 API（Composition API）和 `<script setup>` 语法：

```vue
<template>
  <div class="book-card">
    <h3>{{ book.title }}</h3>
    <p>{{ book.author }}</p>
    <p>¥{{ book.price }}</p>
    <button @click="addToCart">添加到购物车</button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Book } from '@/types/book'

// 定义 props
const props = defineProps<{
  book: Book
}>()

// 定义 emits
const emit = defineEmits<{
  (e: 'add-to-cart', bookId: number): void
}>()

// 方法
const addToCart = () => {
  emit('add-to-cart', props.book.id)
}
</script>

<style scoped>
.book-card {
  /* 样式... */
}
</style>
```

### 组件复用

创建高度复用的组件以提高开发效率：

- **UI 组件**：如按钮、表单元素等，位于 `components/ui/`
- **业务组件**：特定功能的组件，如 `BookCard`、`ShoppingCart`
- **布局组件**：如 `Header`、`Footer`、`Sidebar`

### Props 和 Emits

- 使用 TypeScript 类型定义 props 和 emits
- Props 应该是只读的，不要在组件内部修改
- 使用 emits 向父组件传递事件

## 状态管理

我们使用 Pinia 进行状态管理。

### Store 结构

```typescript
// src/stores/cart.ts
import { defineStore } from 'pinia'
import type { Book } from '@/types/book'

interface CartItem {
  book: Book
  quantity: number
}

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [] as CartItem[]
  }),

  getters: {
    totalItems: (state) => state.items.reduce((total, item) => total + item.quantity, 0),
    totalPrice: (state) => state.items.reduce((total, item) => total + item.book.price * item.quantity, 0)
  },

  actions: {
    addToCart(book: Book, quantity: number = 1) {
      const existingItem = this.items.find(item => item.book.id === book.id)

      if (existingItem) {
        existingItem.quantity += quantity
      } else {
        this.items.push({ book, quantity })
      }
    },

    removeFromCart(bookId: number) {
      const index = this.items.findIndex(item => item.book.id === bookId)
      if (index > -1) {
        this.items.splice(index, 1)
      }
    }
  }
})
```

### 使用 Store

在组件中使用 store：

```typescript
<script setup lang="ts">
import { useCartStore } from '@/stores/cart'

const cartStore = useCartStore()

function addBookToCart(book) {
  cartStore.addToCart(book)
}
</script>

<template>
  <div>
    购物车商品数量: {{ cartStore.totalItems }}
    总价: ¥{{ cartStore.totalPrice }}
  </div>
</template>
```

## 路由管理

我们使用 Vue Router 进行路由管理。

### 路由配置

```typescript
// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import BookList from '@/views/BookList.vue'
import BookDetail from '@/views/BookDetail.vue'
import Cart from '@/views/Cart.vue'
import UserLogin from '@/views/UserLogin.vue'
import UserRegister from '@/views/UserRegister.vue'
import NotFound from '@/views/NotFound.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/books',
      name: 'books',
      component: BookList
    },
    {
      path: '/books/:id',
      name: 'book-detail',
      component: BookDetail,
      props: true
    },
    {
      path: '/cart',
      name: 'cart',
      component: Cart
    },
    {
      path: '/login',
      name: 'login',
      component: UserLogin
    },
    {
      path: '/register',
      name: 'register',
      component: UserRegister
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: NotFound
    }
  ]
})

export default router
```

### 路由导航

```html
<!-- 声明式导航 -->
<template>
  <nav>
    <router-link to="/">首页</router-link>
    <router-link to="/books">图书列表</router-link>
    <router-link to="/cart">购物车</router-link>
  </nav>
</template>

<!-- 编程式导航 -->
<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goToBookDetail(bookId) {
  router.push({ name: 'book-detail', params: { id: bookId } })
}
</script>
```

### 路由守卫

用于在路由跳转前进行权限验证：

```typescript
// src/router/index.ts
import { useAuthStore } from '@/stores/auth'

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // 需要登录的路由
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})
```

## 样式指南

我们结合使用 Tailwind CSS 和 Element Plus 进行样式开发。

### Tailwind CSS

使用 Tailwind 的实用工具类构建界面：

```html
<div class="flex items-center justify-between p-4 bg-white rounded-lg shadow">
  <h3 class="text-xl font-semibold text-gray-800">{{ book.title }}</h3>
  <span class="text-tomato-500 font-bold">¥{{ book.price }}</span>
</div>
```

### 自定义主题

项目使用「番茄红」作为主色调，已在 `tailwind.config.js` 中配置：

```js
// tailwind.config.js
module.exports = {
  theme: {
    extend: {
      colors: {
        "tomato": {
          50: '#fff5f5',
          100: '#ffe3e3',
          // ...其他色阶
          500: '#ff6b6b', // 主色调
          // ...其他色阶
        }
      }
    }
  }
}
```

### Element Plus 组件

使用 Element Plus 组件时，建议结合 Tailwind 进行布局：

```html
<div class="p-4">
  <el-card class="mb-4">
    <template #header>
      <div class="flex justify-between items-center">
        <span>商品详情</span>
        <el-button type="primary">添加到购物车</el-button>
      </div>
    </template>
    <div class="product-info">
      <!-- 内容 -->
    </div>
  </el-card>
</div>
```

## API 调用

我们使用 Axios 进行 API 调用。

### API 客户端配置

```typescript
// src/utils/api.ts
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response.status === 401) {
      // 处理未授权错误
      const authStore = useAuthStore()
      authStore.logout()
      // 重定向到登录页
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default apiClient
```

### API 服务

```typescript
// src/services/book-service.ts
import apiClient from '@/utils/api'
import type { Book } from '@/types/book'

export default {
  getBooks(page = 1, size = 10) {
    return apiClient.get<Book[]>('/books', { params: { page, size } })
  },

  getBookById(id: number) {
    return apiClient.get<Book>(`/books/${id}`)
  },

  searchBooks(query: string) {
    return apiClient.get<Book[]>('/books/search', { params: { query } })
  }
}
```

### 在组件中使用

```typescript
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import bookService from '@/services/book-service'
import type { Book } from '@/types/book'

const books = ref<Book[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const response = await bookService.getBooks()
    books.value = response.data
  } catch (err) {
    error.value = '获取图书列表失败'
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>
```

## 测试

我们使用 Vitest 进行单元测试。

### 组件测试

```typescript
// src/components/__tests__/BookCard.spec.ts
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import BookCard from '../BookCard.vue'

describe('BookCard', () => {
  const book = {
    id: 1,
    title: '测试图书',
    author: '测试作者',
    price: 39.9
  }

  it('renders properly', () => {
    const wrapper = mount(BookCard, { props: { book } })
    expect(wrapper.text()).toContain('测试图书')
    expect(wrapper.text()).toContain('测试作者')
    expect(wrapper.text()).toContain('39.9')
  })

  it('emits add-to-cart event when button is clicked', async () => {
    const wrapper = mount(BookCard, { props: { book } })
    await wrapper.find('button').trigger('click')
    expect(wrapper.emitted()).toHaveProperty('add-to-cart')
    expect(wrapper.emitted()['add-to-cart'][0]).toEqual([1])
  })
})
```

### 运行测试

```bash
bun run test
```

## 构建与部署

### 开发环境

```bash
bun run dev
```

### 生产环境构建

```bash
bun run build
```

### 环境变量配置

在项目根目录创建 `.env` 文件：

```
VITE_API_BASE_URL=http://localhost:8080/api
```

开发环境特定配置 (`.env.development`):

```
VITE_API_BASE_URL=http://localhost:8080/api
```

生产环境特定配置 (`.env.production`):

```
VITE_API_BASE_URL=https://api.example.com/api
```

### 在组件中使用环境变量

```typescript
const apiUrl = import.meta.env.VITE_API_BASE_URL
```

## 常见问题

### 解决跨域问题

在开发环境中，可以通过 Vite 的代理功能解决跨域问题：

```typescript
// vite.config.ts
export default defineConfig({
  // ...
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### TypeScript 类型问题

如果遇到第三方库没有类型定义：

1. 查找是否有对应的 `@types` 包：
   ```bash
   bun add -D @types/package-name
   ```

2. 自己创建类型定义文件：
   ```typescript
   // src/types/module.d.ts
   declare module 'some-module' {
     // 类型定义...
   }
   ```

### 常见性能优化

1. 对大组件使用异步加载：
   ```typescript
   const UserDashboard = () => import('@/views/UserDashboard.vue')
   ```

2. 使用 `v-once` 对只渲染一次的内容：
   ```html
   <div v-once>{{ staticContent }}</div>
   ```

3. 使用 `computed` 缓存计算结果：
   ```typescript
   const filteredBooks = computed(() => books.value.filter(book => book.price < 50))
   ```

4. 利用 `v-memo` 缓存部分模板：
   ```html
   <div v-memo="[item.id]">{{ expensiveComputation(item) }}</div>
   ```
