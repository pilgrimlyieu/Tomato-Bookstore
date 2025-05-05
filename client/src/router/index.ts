import { Routes } from "@/constants/routes";
import { useUserStore } from "@/stores/user";
import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: Routes.HOME,
      name: "home",
      component: () => import("@/views/Home.vue"),
      meta: { title: "首页" },
    },
    {
      path: Routes.USER_LOGIN,
      name: "login",
      component: () => import("@/views/Login.vue"),
      meta: { title: "登录", guest: true },
    },
    {
      path: Routes.USER_REGISTER,
      name: "register",
      component: () => import("@/views/Register.vue"),
      meta: { title: "注册", guest: true },
    },
    {
      path: Routes.USER_PROFILE,
      name: "userProfile",
      component: () => import("@/views/User/Profile.vue"),
      meta: { title: "个人信息", requiresAuth: true },
    },
    {
      path: Routes.USER_REVIEWS,
      name: "userReviews",
      component: () => import("@/views/Review/UserReviews.vue"),
      meta: { title: "我的书评", requiresAuth: true },
    },
    {
      path: Routes.PRODUCT_LIST,
      name: "products",
      component: () => import("@/views/Product/List.vue"),
      meta: { title: "商品列表" },
    },
    {
      path: Routes.PRODUCT_DETAIL,
      name: "productDetail",
      component: () => import("@/views/Product/Detail.vue"),
      meta: { title: "商品详情" },
    },
    {
      path: Routes.ADMIN,
      name: "admin",
      component: () => import("@/views/Admin/Dashboard.vue"),
      meta: { title: "管理后台", requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: Routes.ADMIN_PRODUCTS,
          name: "adminProducts",
          component: () => import("@/views/Admin/Product/List.vue"),
          meta: { title: "商品管理", requiresAuth: true, requiresAdmin: true },
        },
        {
          path: Routes.ADMIN_PRODUCT_CREATE,
          name: "adminProductCreate",
          component: () => import("@/views/Admin/Product/Edit.vue"),
          meta: { title: "创建商品", requiresAuth: true, requiresAdmin: true },
        },
        {
          path: Routes.ADMIN_PRODUCT_EDIT,
          name: "adminProductEdit",
          component: () => import("@/views/Admin/Product/Edit.vue"),
          meta: { title: "编辑商品", requiresAuth: true, requiresAdmin: true },
        },
        {
          path: Routes.ADMIN_REVIEWS,
          name: "adminReviews",
          component: () => import("@/views/Admin/Review/List.vue"),
          meta: { title: "书评管理", requiresAuth: true, requiresAdmin: true },
        },
      ],
    },
    // 购物车路由
    {
      path: Routes.CART,
      name: "cart",
      component: () => import("@/views/Cart/Cart.vue"),
      meta: { title: "购物车", requiresAuth: true },
    },
    {
      path: Routes.CART_CHECKOUT,
      name: "checkout",
      component: () => import("@/views/Cart/Checkout.vue"),
      meta: { title: "结算", requiresAuth: true },
    },
    // 订单路由
    {
      path: Routes.ORDER_LIST,
      name: "orderList",
      component: () => import("@/views/Order/List.vue"),
      meta: { title: "我的订单", requiresAuth: true },
    },
    {
      path: Routes.ORDER_DETAIL,
      name: "orderDetail",
      component: () => import("@/views/Order/Detail.vue"),
      meta: { title: "订单详情", requiresAuth: true },
    },
    {
      path: Routes.ORDER_PAY,
      name: "orderPay",
      component: () => import("@/views/Order/Pay.vue"),
      meta: { title: "订单支付", requiresAuth: true },
    },
    {
      path: Routes.ORDER_SUCCESS,
      name: "orderSuccess",
      component: () => import("@/views/Order/Success.vue"),
      meta: { title: "支付成功", requiresAuth: true },
    },
    {
      path: Routes.ORDER_FAIL,
      name: "orderFail",
      component: () => import("@/views/Order/Fail.vue"),
      meta: { title: "支付失败", requiresAuth: true },
    },
    {
      path: "/:pathMatch(.*)*",
      name: "notFound",
      component: () => import("@/views/NotFound.vue"),
      meta: { title: "页面不存在" },
    },
  ],
});

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title || "首页"} - 番茄商城`;

  const userStore = useUserStore();

  // 如果用户已登录但没有用户信息，则获取用户信息
  if (userStore.token && !userStore.user) {
    await userStore.fetchUserInfo();
  }

  // 判断页面是否需要管理员权限
  if (to.meta.requiresAdmin === true && !userStore.isAdmin) {
    next({ name: "home" });
    return;
  }

  // 判断页面是否需要登录权限
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: "login", query: { redirect: to.fullPath } });
    return;
  }

  // 如果用户已登录，则不允许访问登录和注册页面
  if (to.meta.guest && userStore.isLoggedIn) {
    next({ name: "home" });
    return;
  }

  next();
});

export default router;
