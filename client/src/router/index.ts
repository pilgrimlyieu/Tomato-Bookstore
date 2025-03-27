// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
// import Home from '@/views/Home.vue'
// import BookList from '@/views/BookList.vue'
// import BookDetail from '@/views/BookDetail.vue'
// import Cart from '@/views/Cart.vue'
// import UserRegister from '@/views/User/Register.vue'
// import NotFound from '@/views/NotFound.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/Home',
      name: 'Home',
      component: () => import('../views/Home.vue'),
      meta: {title: '主页'}
    },
    // {
    //   path: '/books',
    //   name: 'books',
    //   component: BookList
    // },
    // {
    //   path: '/books/:id',
    //   name: 'book-detail',
    //   component: BookDetail,
    //   props: true
    // },
    // {
    //   path: '/cart',
    //   name: 'cart',
    //   component: Cart
    // },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/user/Login.vue'),
      meta: {title: '用户登录'}
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/user/Register.vue'),
      meta: {title: '用户注册'}
    },
    {
      path: '/404',
      name: '404',
      component: () => import('../views/NotFound.vue'),
      meta: {title: '404'}
    }, {
      path: '/:catchAll(.*)',
      redirect: '/404'
    }]
})

// export default router
// router.beforeEach((to, _, next) => {
//   const token = sessionStorage.getItem('token')
//   const role: string | null = sessionStorage.getItem('role')
//   if (to.meta.title) {
//     document.title = to.meta.title;
//   }
//   if (token) {
//     if (to.meta.permission) {
//       if (to.meta.permission.includes(role!)) {
//         next();
//       } else {
//         next('/404');
//       }
//     } else {
//       next();
//     }
//   }
//   else {
//     if (to.path === '/login') {
//       next();
//     } else if (to.path === '/register') {
//       next();
//     } else {
//       next('/login');
//     }
//   }
//
// })
// import { useAuthStore } from '@/stores/auth'
//
// router.beforeEach((to, _, next) => {
//   const authStore = useAuthStore()
//
//   if (to.meta.requiresAuth && !authStore.isLoggedIn) {
//     next({ name: 'login', query: { redirect: to.fullPath } })
//   } else {
//     next()
//   }
// })
router.beforeEach((to, _, next) => {
  const authStore = useAuthStore()

  console.log('[导航守卫] to:', to.fullPath)
  console.log('[导航守卫] isLoggedIn:', authStore.isLoggedIn)

  if (to.meta.title) {
    document.title = to.meta.title
  }
  next()
  // if (to.meta.requiresAuth && !authStore.isLoggedIn) {
  //   next({ name: 'login', query: { redirect: to.fullPath } })
  // } else {
  //   next()
  // }
})

export default router
