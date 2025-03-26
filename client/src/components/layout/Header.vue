<template>
  <header class="bg-white/90 backdrop-blur-md shadow-md sticky top-0 z-50 transition-all duration-300"
    :class="{ 'shadow-lg': scrolled }">
    <div class="container mx-auto px-4 py-3">
      <div class="flex justify-between items-center">
        <!-- Logo -->
        <div class="flex items-center">
          <router-link :to="Routes.HOME" class="text-2xl font-bold text-tomato-600 flex items-center group">
            <div class="mr-2 text-3xl group-hover:animate-pulse-custom transition-all duration-300">🍅</div>
            <span class="group-hover:text-tomato-500 transition-colors duration-300">番茄书城</span>
          </router-link>
        </div>

        <!-- 搜索框 (中等屏幕以上显示) -->
        <div class="hidden md:block mx-4 flex-grow max-w-md relative">
          <el-input v-model="searchQuery" placeholder="搜索图书、作者..." class="modern-search w-full" :prefix-icon="Search"
            @keyup.enter="handleSearch" clearable>
            <template #append>
              <el-button :icon="Search" @click="handleSearch" class="search-button" />
            </template>
          </el-input>
        </div>

        <!-- 导航链接 (中等屏幕以上显示) -->
        <nav class="hidden md:flex space-x-1">
          <router-link v-for="(item, index) in navItems" :key="index" :to="item.path"
            class="text-gray-700 hover:text-tomato-500 px-3 py-2 rounded-md transition-colors relative overflow-hidden group"
            :class="{ 'text-tomato-500 font-medium': isActiveRoute(item.path) }">
            <span>{{ item.label }}</span>
            <span
              class="absolute bottom-0 left-0 w-full h-0.5 bg-tomato-500 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-300 origin-left"
              :class="{ 'scale-x-100': isActiveRoute(item.path) }"></span>
          </router-link>
        </nav>

        <!-- 用户菜单 -->
        <div class="flex items-center space-x-4">
          <!-- 购物车图标 -->
          <router-link :to="Routes.CART" class="relative text-gray-700 hover:text-tomato-500 transition-colors">
            <el-badge :value="3" :max="99" class="item">
              <el-icon size="24">
                <ShoppingCart />
              </el-icon>
            </el-badge>
          </router-link>

          <template v-if="userStore.isLoggedIn">
            <el-dropdown trigger="click">
              <div class="flex items-center cursor-pointer group">
                <el-avatar :size="36" :src="userStore.user?.avatar || ''"
                  class="mr-2 border-2 border-white group-hover:border-tomato-300 transition-colors shadow"
                  :class="[userStore.user?.avatar ? '' : 'bg-gradient-to-r from-tomato-500 to-tomato-600 text-white']">
                  {{ userStore.username.substring(0, 1).toUpperCase() }}
                </el-avatar>
                <div class="flex flex-col items-start">
                  <span class="text-gray-800 font-medium group-hover:text-tomato-500 transition-colors">
                    {{ userStore.username }}
                  </span>
                  <span class="text-xs text-gray-500">
                    {{ userStore.isAdmin ? '管理员' : '普通会员' }}
                  </span>
                </div>
                <el-icon class="ml-1 transition-transform group-hover:rotate-180"><arrow-down /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>
                    <router-link :to="Routes.USER_PROFILE" class="block w-full">
                      <el-icon>
                        <User />
                      </el-icon>
                      <span class="ml-1">个人信息</span>
                    </router-link>
                  </el-dropdown-item>
                  <el-dropdown-item>
                    <router-link :to="Routes.ORDER" class="block w-full">
                      <el-icon>
                        <List />
                      </el-icon>
                      <span class="ml-1">我的订单</span>
                    </router-link>
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon>
                      <SwitchButton />
                    </el-icon>
                    <span class="ml-1">退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <template v-else>
            <router-link :to="Routes.USER_LOGIN">
              <el-button type="primary" size="small" class="rounded-full mr-2">
                <el-icon class="mr-1">
                  <Key />
                </el-icon>登录
              </el-button>
            </router-link>
            <router-link :to="Routes.USER_REGISTER">
              <el-button size="small" class="rounded-full">
                <el-icon class="mr-1">
                  <Plus />
                </el-icon>注册
              </el-button>
            </router-link>
          </template>

          <!-- 移动端菜单按钮 -->
          <button class="md:hidden text-gray-500 hover:text-tomato-500 focus:outline-none transition-colors"
            @click="isMobileMenuOpen = !isMobileMenuOpen">
            <el-icon size="24">
              <Menu />
            </el-icon>
          </button>
        </div>
      </div>

      <!-- 移动端搜索框 -->
      <div class="md:hidden mt-3">
        <el-input v-model="searchQuery" placeholder="搜索图书、作者..." class="modern-search w-full" :prefix-icon="Search"
          clearable>
          <template #append>
            <el-button :icon="Search" @click="handleSearch" class="search-button" />
          </template>
        </el-input>
      </div>

      <!-- 移动端导航菜单 -->
      <transition enter-active-class="transition duration-300 ease-out"
        enter-from-class="transform -translate-y-10 opacity-0" enter-to-class="transform translate-y-0 opacity-100"
        leave-active-class="transition duration-200 ease-in" leave-from-class="transform translate-y-0 opacity-100"
        leave-to-class="transform -translate-y-10 opacity-0">
        <nav v-if="isMobileMenuOpen" class="md:hidden py-3 mt-3 space-y-1 border-t border-gray-100">
          <router-link v-for="(item, index) in navItems" :key="index" :to="item.path"
            class="block px-3 py-2 rounded-md text-gray-700 hover:bg-tomato-50 hover:text-tomato-500 transition-colors"
            :class="{ 'bg-tomato-50 text-tomato-500 font-medium': isActiveRoute(item.path) }"
            @click="isMobileMenuOpen = false">
            {{ item.label }}
          </router-link>
        </nav>
      </transition>
    </div>
  </header>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useUserStore } from "@/stores/user";
import {
  ArrowDown,
  Key,
  List,
  Menu,
  Plus,
  Search,
  ShoppingCart,
  Star,
  SwitchButton,
  User,
} from "@element-plus/icons-vue";
import { computed, onMounted, onUnmounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

const searchQuery = ref("");
const scrolled = ref(false);
const isMobileMenuOpen = ref(false);

// 导航项目
const navItems = [
  { path: "/", label: "首页" },
  { path: "/books", label: "图书" },
  { path: "/categories", label: "分类" },
  { path: "/new-releases", label: "新书" },
  { path: "/promotions", label: "特惠" },
];

// 检查路由是否激活
const isActiveRoute = (path: string) => {
  if (path === "/") {
    return route.path === "/";
  }
  return route.path.startsWith(path);
};

// 搜索处理
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ path: "/search", query: { q: searchQuery.value } });
    searchQuery.value = "";
  }
};

// 退出登录
const handleLogout = () => {
  userStore.logout();
  router.push("/");
};

// 监听滚动事件
const handleScroll = () => {
  scrolled.value = window.scrollY > 20;
};

onMounted(() => {
  window.addEventListener("scroll", handleScroll);
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style scoped>
/* 确保下拉菜单中的路由链接占据整个宽度 */
:deep(.el-dropdown-menu__item) {
  padding: 0;
}

:deep(.el-dropdown-menu__item a) {
  padding: 10px 16px;
  display: flex;
  align-items: center;
}

/* 添加下拉图标的旋转动画 */
@keyframes spin-slow {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.animate-spin-slow {
  animation: spin-slow 2s linear infinite;
}

/* 美化搜索框 */
.modern-search :deep(.el-input__wrapper) {
  border-radius: 9999px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  background-color: rgba(255, 255, 255, 0.8);
}

.modern-search :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  background-color: rgba(255, 255, 255, 0.95);
}

.modern-search :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.3), 0 4px 12px rgba(0, 0, 0, 0.08);
  background-color: rgba(255, 255, 255, 1);
}

.modern-search :deep(.el-input-group__append) {
  border-top-right-radius: 9999px;
  border-bottom-right-radius: 9999px;
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}

.modern-search :deep(.search-button) {
  color: white;
  border: none;
  background-color: transparent;
  box-shadow: none;
  border-radius: 0;
}

.modern-search :deep(.search-button:hover) {
  background-color: transparent;
  color: rgba(255, 255, 255, 0.9);
}
</style>
