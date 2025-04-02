<template>
  <header class="sticky top-0 z-50 bg-white/70 backdrop-blur-md shadow-sm">
    <div class="container mx-auto px-4 py-3">
      <div class="flex justify-between items-center">
        <!-- Logo -->
        <router-link to="/" class="flex items-center">
          <img src="@/assets/images/logo.svg" alt="番茄商城" class="h-10 mr-3" />
          <span class="text-xl font-bold text-tomato-600">番茄商城</span>
        </router-link>

        <!-- 主导航 -->
        <nav class="hidden md:flex space-x-6">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link :to="Routes.PRODUCT_LIST" class="nav-link">商品</router-link>
          <!-- 其他导航链接 -->
        </nav>

        <!-- 用户区域 -->
        <div class="flex items-center gap-4">
          <!-- 搜索按钮 -->
          <button @click="openSearch" class="p-2 text-gray-600 hover:text-tomato-600 transition-colors">
            <el-icon><Search /></el-icon>
          </button>

          <!-- 购物车 -->
          <router-link to="/cart" class="p-2 text-gray-600 hover:text-tomato-600 transition-colors relative">
            <el-icon><ShoppingCart /></el-icon>
            <span v-if="cartItemCount > 0"
                  class="absolute -top-1 -right-1 bg-tomato-600 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">
              {{ cartItemCount }}
            </span>
          </router-link>

          <!-- 用户菜单 -->
          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click">
              <el-avatar :size="32" class="cursor-pointer" :src="userStore.user?.avatar">
                {{ userStore.username ? userStore.username.substring(0, 1).toUpperCase() : 'U' }}
              </el-avatar>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push(Routes.USER_PROFILE)">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item v-if="userStore.isAdmin" @click="router.push(Routes.ADMIN)">
                    <el-icon><Setting /></el-icon>
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link :to="Routes.USER_LOGIN" class="text-sm text-gray-600 hover:text-tomato-600">登录</router-link>
            <router-link :to="Routes.USER_REGISTER"
                         class="text-sm bg-tomato-600 text-white px-3 py-1 rounded-full hover:bg-tomato-700">
              注册
            </router-link>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useUserStore } from "@/stores/user";
import {
  Search,
  Setting,
  ShoppingCart,
  SwitchButton,
  User,
} from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const userStore = useUserStore();
const cartItemCount = ref(0); // 这将来自购物车store

// 打开搜索框
const openSearch = () => {
  // 实现搜索功能
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
      router.push("/");
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
