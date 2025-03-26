<template>
  <footer class="bg-gradient-to-b from-gray-50 to-gray-100 pt-12 pb-8">
    <div class="container mx-auto px-4">
      <!-- 顶部区域：快速链接和订阅 -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8 mb-10 footer-sections">
        <!-- 公司简介 -->
        <div class="md:col-span-1 footer-section">
          <div class="flex items-center mb-4">
            <div class="text-3xl mr-2">🍅</div>
            <div class="text-xl font-bold text-tomato-600">番茄书城</div>
          </div>
          <p class="text-gray-600 mb-4">
            您的线上阅读伙伴，提供海量精选图书，让阅读成为日常生活中的美好习惯。
          </p>
          <div class="flex space-x-3">
            <a href="#" class="text-gray-500 hover:text-tomato-500 transition-colors social-icon">
              <el-icon size="20">
                <ChatDotRound />
              </el-icon>
            </a>
            <a href="#" class="text-gray-500 hover:text-tomato-500 transition-colors social-icon">
              <el-icon size="20">
                <Promotion />
              </el-icon>
            </a>
            <a href="#" class="text-gray-500 hover:text-tomato-500 transition-colors social-icon">
              <el-icon size="20">
                <Share />
              </el-icon>
            </a>
          </div>
        </div>

        <!-- 链接区域 -->
        <div class="md:col-span-2 grid grid-cols-2 sm:grid-cols-3 gap-6 footer-section">
          <!-- 产品与服务 -->
          <div>
            <h4 class="text-sm font-semibold uppercase tracking-wider text-gray-800 mb-4 footer-heading">产品与服务</h4>
            <ul class="space-y-2 footer-links">
              <li v-for="(link, index) in productLinks" :key="index">
                <a href="#" class="text-gray-600 hover:text-tomato-500 transition-colors text-sm footer-link">{{ link }}</a>
              </li>
            </ul>
          </div>

          <!-- 关于我们 -->
          <div>
            <h4 class="text-sm font-semibold uppercase tracking-wider text-gray-800 mb-4 footer-heading">关于我们</h4>
            <ul class="space-y-2 footer-links">
              <li v-for="(link, index) in aboutLinks" :key="index">
                <a href="#" class="text-gray-600 hover:text-tomato-500 transition-colors text-sm footer-link">{{ link }}</a>
              </li>
            </ul>
          </div>

          <!-- 支持 -->
          <div>
            <h4 class="text-sm font-semibold uppercase tracking-wider text-gray-800 mb-4 footer-heading">客户支持</h4>
            <ul class="space-y-2 footer-links">
              <li v-for="(link, index) in supportLinks" :key="index">
                <a href="#" class="text-gray-600 hover:text-tomato-500 transition-colors text-sm footer-link">{{ link }}</a>
              </li>
            </ul>
          </div>
        </div>

        <!-- 订阅区域 -->
        <div class="md:col-span-1 footer-section">
          <h4 class="text-sm font-semibold uppercase tracking-wider text-gray-800 mb-4 footer-heading">订阅更新</h4>
          <p class="text-gray-600 text-sm mb-4">订阅我们的新书推荐和特惠信息</p>
          <div class="flex">
            <el-input v-model="email" placeholder="您的邮箱地址" class="modern-input rounded-r-none" :prefix-icon="Message" />
            <el-button type="primary" class="rounded-l-none" :loading="subscribing" @click="handleSubscribe">
              订阅
            </el-button>
          </div>
          <p class="text-xs text-gray-500 mt-2">
            我们尊重您的隐私，绝不会向第三方分享您的信息。
          </p>
        </div>
      </div>

      <!-- 分隔线 -->
      <div class="border-t border-gray-200 pt-8 flex flex-col sm:flex-row justify-between items-center footer-bottom">
        <p class="text-gray-600 text-sm mb-4 sm:mb-0">
          © {{ currentYear }} 番茄书城. 保留所有权利
        </p>
        <div class="flex space-x-6">
          <a href="#" class="text-gray-500 hover:text-tomato-500 transition text-sm">隐私政策</a>
          <a href="#" class="text-gray-500 hover:text-tomato-500 transition text-sm">服务条款</a>
          <a href="#" class="text-gray-500 hover:text-tomato-500 transition text-sm">Cookie 政策</a>
        </div>
      </div>
    </div>

    <!-- 回到顶部按钮 -->
    <div
      v-show="showBackToTop"
      ref="backToTopBtn"
      class="fixed bottom-8 right-8 bg-white p-3 rounded-full shadow-lg cursor-pointer hover:bg-tomato-50 transition-colors w-12 h-12 flex items-center justify-center"
      @click="scrollToTop">
      <el-icon size="20" class="text-tomato-500">
        <Top />
      </el-icon>
    </div>
  </footer>
</template>

<script setup lang="ts">
import {
  ChatDotRound,
  Message,
  Promotion,
  Share,
  Top,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { gsap } from "gsap";
import { onMounted, onUnmounted, ref } from "vue";

const currentYear = new Date().getFullYear();
const showBackToTop = ref(false);
const email = ref("");
const subscribing = ref(false);
const backToTopBtn = ref(null);

// 链接数据
const productLinks = ["图书分类", "特惠活动", "新书上架", "畅销榜单", "电子书"];
const aboutLinks = ["公司简介", "加入我们", "新闻资讯", "联系我们", "合作伙伴"];
const supportLinks = [
  "帮助中心",
  "配送方式",
  "退换政策",
  "会员福利",
  "常见问题",
];

// 监听滚动事件
const handleScroll = () => {
  const wasVisible = showBackToTop.value;
  showBackToTop.value = window.scrollY > 300;

  // 当按钮要显示且之前未显示时添加动画
  if (showBackToTop.value && !wasVisible && backToTopBtn.value) {
    gsap.fromTo(
      backToTopBtn.value,
      { scale: 0.5, opacity: 0 },
      { scale: 1, opacity: 1, duration: 0.4, ease: "back.out(1.7)" },
    );
  }
};

// 回到顶部
const scrollToTop = () => {
  // 使用原生滚动API，避免GSAP ScrollToPlugin的依赖问题
  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
};

// 处理订阅
const handleSubscribe = () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!email.value.trim()) {
    ElMessage.warning("请输入邮箱地址");
    return;
  }

  if (!emailRegex.test(email.value)) {
    ElMessage.error("请输入有效的邮箱地址");

    // 添加输入框抖动效果
    const inputEl = document.querySelector(".modern-input .el-input__wrapper");
    if (inputEl) {
      gsap.fromTo(
        inputEl,
        { x: 0 },
        { x: [-5, 5, -3, 3, 0], duration: 0.4, ease: "power2.out" },
      );
    }
    return;
  }

  subscribing.value = true;

  // 模拟订阅请求
  setTimeout(() => {
    ElMessage.success({
      message: "订阅成功！感谢您的关注",
      icon: "el-icon-success",
    });
    email.value = "";
    subscribing.value = false;

    // 成功动画效果
    const btnEl = document.querySelector(".modern-input + .el-button");
    if (btnEl) {
      gsap.fromTo(
        btnEl,
        { scale: 1 },
        {
          scale: 1.1,
          duration: 0.2,
          ease: "back.out(2)",
          repeat: 1,
          yoyo: true,
        },
      );
    }
  }, 1000);
};

onMounted(() => {
  window.addEventListener("scroll", handleScroll);

  // 添加页脚元素的入场动画
  gsap.fromTo(
    ".footer-section",
    { y: 30, opacity: 0 },
    {
      y: 0,
      opacity: 1,
      stagger: 0.1,
      duration: 0.6,
      ease: "power2.out",
    },
  );

  // 添加链接的入场动画
  gsap.fromTo(
    ".footer-link",
    { x: -10, opacity: 0 },
    {
      x: 0,
      opacity: 1,
      stagger: 0.03,
      delay: 0.3,
      duration: 0.4,
      ease: "power1.out",
    },
  );

  // 添加页脚底部的入场动画
  gsap.fromTo(
    ".footer-bottom",
    { y: 20, opacity: 0 },
    {
      y: 0,
      opacity: 1,
      delay: 0.5,
      duration: 0.5,
      ease: "power2.out",
    },
  );

  // 添加社交图标的入场动画
  gsap.fromTo(
    ".social-icon",
    { scale: 0, opacity: 0 },
    {
      scale: 1,
      opacity: 1,
      stagger: 0.1,
      delay: 0.4,
      duration: 0.4,
      ease: "back.out(1.7)",
    },
  );
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style scoped>
.modern-input :deep(.el-input__wrapper) {
  border-radius: 0.75rem 0 0 0.75rem;
  transition: all 0.3s ease;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  background-color: rgba(255, 255, 255, 0.8);
}

.modern-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
  background-color: rgba(255, 255, 255, 0.95);
}

.modern-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px rgba(255, 107, 107, 0.3), 0 3px 10px rgba(0, 0, 0, 0.08);
  background-color: rgba(255, 255, 255, 1);
}

.footer-heading {
  position: relative;
  padding-bottom: 0.5rem;
}

.footer-heading::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: 0;
  height: 2px;
  width: 2rem;
  background-color: var(--el-color-primary);
  border-radius: 1px;
}
</style>
