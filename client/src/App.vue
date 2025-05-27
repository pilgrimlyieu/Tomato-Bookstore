<template>
  <div class="min-h-screen flex flex-col">
    <Header />
    <main class="flex-grow">
      <PageTransition type="fade" mode="out-in">
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
