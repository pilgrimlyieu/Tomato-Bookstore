<template>
  <div class="min-h-screen flex flex-col">
    <Header />
    <main class="flex-grow">
      <PageTransition type="fade">
        <router-view />
      </PageTransition>
    </main>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Footer from "@/components/layout/Footer.vue";
import Header from "@/components/layout/Header.vue";
import PageTransition from "@/components/shared/PageTransition.vue";
import { useUserStore } from "@/stores/user";
import { applyRoundedStyle, setCSSVariables } from "@/utils/theme";
import { onMounted } from "vue";

const userStore = useUserStore();

// 页面加载时自动获取用户信息并设置主题
onMounted(() => {
  // 设置 CSS 变量主题
  setCSSVariables();

  // 应用圆角风格
  applyRoundedStyle("lg");

  // 获取用户信息
  if (userStore.token && !userStore.user) {
    userStore.fetchUserInfo();
  }
});
</script>

<style>
/* 自定义 Element Plus 主题颜色 */
:root {
  --el-color-primary: #ff6b6b;
  --el-color-primary-light-3: #ff8787;
  --el-color-primary-light-5: #ffa8a8;
  --el-color-primary-light-7: #ffc9c9;
  --el-color-primary-light-8: #ffe3e3;
  --el-color-primary-light-9: #fff5f5;
  --el-color-primary-dark-2: #fa5252;
}
</style>
