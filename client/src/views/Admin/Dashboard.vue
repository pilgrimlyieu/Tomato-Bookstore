<template>
  <div class="min-h-screen flex">
    <!-- 侧边栏 -->
    <div class="w-40 h-full bg-white/90 backdrop-blur-md shadow-md">
      <div class="p-6">
        <h1 class="text-xl font-bold text-tomato-600 flex items-center">
          <el-icon class="mr-2" size="24"><Setting /></el-icon>
          管理后台
        </h1>
      </div>
      <el-menu
        router
        class="border-none"
        :default-active="activeMenuItem"
      >
        <el-menu-item :index="Routes.ADMIN_PRODUCTS">
          <el-icon><Goods /></el-icon>
          <template #title>商品管理</template>
        </el-menu-item>
        <el-menu-item :index="Routes.ADMIN_ADVERTISEMENTS">
          <el-icon><Picture /></el-icon>
          <template #title>广告管理</template>
        </el-menu-item>
        <el-menu-item :index="Routes.ADMIN_REVIEWS">
          <el-icon><ChatLineRound /></el-icon>
          <template #title>书评管理</template>
        </el-menu-item>
        <el-menu-item :index="Routes.ADMIN_NOTES">
          <el-icon><Notebook /></el-icon>
          <template #title>笔记管理</template>
        </el-menu-item>

      </el-menu>
    </div>

    <!-- 主要内容 -->
    <div class="ml-4 flex-1 p-6">
      <!-- 顶部导航 -->
      <div class="mb-6 p-4 bg-white/90 backdrop-blur-md rounded-xl shadow-sm flex justify-between items-center">
        <h2 class="text-xl font-bold text-gray-800">{{ pageTitle }}</h2>
        <div class="flex items-center">
          <span class="mr-4 text-gray-700">{{ userStore.username }}</span>
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-avatar :size="32" :src="userStore.user?.avatar">
                {{ userStore.username ? userStore.username.substring(0, 1).toUpperCase() : 'A' }}
              </el-avatar>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push(Routes.HOME)">
                  <el-icon><House /></el-icon>
                  返回首页
                </el-dropdown-item>
                <el-dropdown-item @click="router.push(Routes.USER_PROFILE)">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 主要内容区 -->
      <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useUserStore } from "@/stores/user";
import {
  ChatLineRound,
  Goods,
  House,
  List,
  Notebook,
  Picture,
  Setting,
  SwitchButton,
  User,
} from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 根据当前路由获取页面标题
const pageTitle = computed(() => {
  if (route.path === Routes.ADMIN_PRODUCTS) {
    return "商品管理";
  } else if (route.path === Routes.ADMIN_PRODUCT_CREATE) {
    return "创建商品";
  } else if (route.path.includes(Routes.ADMIN_PRODUCTS)) {
    return "编辑商品";
  } else if (route.path === Routes.ADMIN_ADVERTISEMENTS) {
    return "广告管理";
  } else if (route.path === Routes.ADMIN_ADVERTISEMENT_CREATE) {
    return "创建广告";
  } else if (route.path.includes(Routes.ADMIN_ADVERTISEMENTS)) {
    return "编辑广告";
  } else if (route.path === Routes.ADMIN_REVIEWS) {
    return "书评管理";
  } else if (route.path === Routes.ADMIN_NOTES) {
    return "笔记管理";
  } else {
    return "管理后台";
  }
});

// 当前活动菜单项
const activeMenuItem = computed(() => {
  const path = route.path;
  if (
    path.startsWith(Routes.ADMIN_PRODUCTS) ||
    path === Routes.ADMIN_PRODUCT_CREATE
  ) {
    return Routes.ADMIN_PRODUCTS;
  } else if (
    path.startsWith(Routes.ADMIN_ADVERTISEMENTS) ||
    path === Routes.ADMIN_ADVERTISEMENT_CREATE
  ) {
    return Routes.ADMIN_ADVERTISEMENTS;
  }
  return path;
});

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
