<template>
  <header class="sticky top-0 z-50 bg-white/70 backdrop-blur-md shadow-sm">
    <div class="container mx-auto px-4 py-3">
      <div class="flex justify-between items-center">
        <!-- Logo -->
        <router-link :to="Routes.HOME" class="flex items-center">
          <img src="@/assets/images/logo.svg" alt="番茄商城" class="h-10 mr-3" />
          <span class="text-xl font-bold text-tomato-600">番茄商城</span>
        </router-link>

        <!-- 导航菜单 -->
        <nav class="flex items-center gap-2 md:gap-3 lg:gap-5">
          <!-- 主导航链接 -->
          <div class="hidden md:flex items-center space-x-6 mr-4">
            <router-link :to="Routes.HOME" class="nav-link">首页</router-link>
            <router-link :to="Routes.PRODUCT_LIST" class="nav-link">商品浏览</router-link>
            <router-link v-if="userStore.isLoggedIn" :to="Routes.ORDER_LIST" class="nav-link">我的订单</router-link>
            <router-link v-if="userStore.isAdmin" :to="Routes.ADMIN" class="nav-link">管理后台</router-link>
          </div>

          <!-- 搜索 -->
          <el-button
            type="primary"
            size="large"
            :icon="Search"
            text
            @click="openSearch"
            class="hidden sm:flex"
          />

          <!-- 购物车 -->
          <router-link :to="Routes.CART" class="relative">
            <el-button type="primary" size="large" :icon="ShoppingCart" text />
            <span
              v-if="userStore.isLoggedIn && cartItemCount > 0"
              class="absolute -top-1 -right-1 bg-tomato-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center"
            >
              {{ cartItemCount > 99 ? '99+' : cartItemCount }}
            </span>
          </router-link>

          <!-- 下拉菜单 -->
          <el-dropdown trigger="click">
            <el-button type="primary" size="large" :icon="User" text />
            <template #dropdown>
              <el-dropdown-menu>
                <div v-if="userStore.isLoggedIn">
                  <el-dropdown-item>
                    <div class="flex gap-2 items-center">
                        <el-avatar
                        :size="24"
                        :src="userStore.user?.avatar"
                      />
                      <span>{{ userStore.user?.username }}</span>
                    </div>
                  </el-dropdown-item>
                  <el-dropdown-item divided>
                    <router-link :to="Routes.USER_PROFILE" class="flex w-full">
                      <el-icon class="mr-1"><Setting /></el-icon>
                      个人中心
                    </router-link>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <router-link :to="Routes.ORDER_LIST" class="flex w-full">
                      <el-icon class="mr-1"><List /></el-icon>
                      我的订单
                    </router-link>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <el-button class="flex w-full" @click="logout">
                      <el-icon class="mr-1"><SwitchButton /></el-icon>
                      退出登录
                    </el-button>
                  </el-dropdown-item>
                </div>
                <div v-else>
                  <el-dropdown-item>
                    <router-link :to="Routes.USER_LOGIN" class="flex w-full">
                      登录
                    </router-link>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <router-link :to="Routes.USER_REGISTER" class="flex w-full">
                      注册
                    </router-link>
                  </el-dropdown-item>
                </div>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </nav>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useCartStore } from "@/stores/cart";
import { useUserStore } from "@/stores/user";
import {
  List,
  Search,
  Setting,
  ShoppingCart,
  SwitchButton,
  User,
} from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";

const router = useRouter();
const userStore = useUserStore();
const cartStore = useCartStore();

// 购物车商品数量
const cartItemCount = computed(() => cartStore.totalItems);

// 初始化时获取购物车数据
onMounted(async () => {
  if (userStore.isLoggedIn) {
    await cartStore.fetchUserCart();
  }
});

// 打开搜索框
const openSearch = () => {
  // TODO: 实现搜索功能
};

// 退出登录
const logout = () => {
  ElMessageBox.confirm("确定要退出登录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      userStore.logout();
      router.push(Routes.HOME);
    })
    .catch(() => {});
};
</script>

<style scoped>
.nav-link {
  @apply text-gray-700 hover:text-tomato-600 transition-colors font-medium;
}

.router-link-active {
  @apply text-tomato-600 font-semibold;
}
</style>
