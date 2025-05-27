<template>
  <div class="min-h-screen flex flex-col md:flex-row py-12">
    <!-- 左侧图片区域（仅在中等尺寸以上显示） -->
    <div class="hidden md:flex md:w-1/2 bg-tomato-600 items-center justify-center p-8 animate-fade-in">
      <div class="text-white max-w-lg">
        <div class="text-5xl mb-6">📚</div>
        <h2 class="text-4xl font-bold mb-4">欢迎回到番茄书城</h2>
        <p class="text-xl mb-8 text-tomato-100">
          登录您的账户，探索更多精彩内容，享受阅读的乐趣。
        </p>

        <!-- 特色列表 -->
        <div class="space-y-4">
          <div v-for="(feature, index) in features" :key="index" class="flex items-start">
            <div class="bg-white/20 rounded-full p-1 mr-3">
              <el-icon class="text-white" size="18">
                <Check />
              </el-icon>
            </div>
            <p>{{ feature }}</p>
          </div>
        </div>

        <!-- 装饰元素 -->
        <div class="absolute bottom-10 right-10 opacity-10">
          <img src="@/assets/images/books-illustration.svg" alt="Books" class="w-64">
        </div>
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="w-full md:w-1/2 flex items-center justify-center p-6 animate-fade-in" style="animation-delay: 0.2s">
      <div class="w-full max-w-md">
        <div class="text-center mb-8">
          <div class="inline-block mb-4">
            <div
              class="flex items-center justify-center w-16 h-16 mx-auto bg-tomato-100 text-tomato-600 rounded-full text-3xl">
              🍅
            </div>
          </div>
          <h2 class="text-3xl font-bold text-gray-900 mb-2">登录</h2>
          <p class="text-gray-600">欢迎回来！请登录您的账号</p>
        </div>

        <!-- 社交登录选项 -->
        <div class="mb-6">
          <div class="grid grid-cols-3 gap-3">
            <button
              class="bg-white border border-gray-300 p-2 rounded-md flex items-center justify-center hover:bg-gray-50 transition-colors">
              <img src="https://img.icons8.com/color/48/000000/google-logo.png" alt="Google" class="w-6 h-6">
            </button>
            <button
              class="bg-white border border-gray-300 p-2 rounded-md flex items-center justify-center hover:bg-gray-50 transition-colors">
              <img src="https://img.icons8.com/color/48/000000/facebook-new.png" alt="Facebook" class="w-6 h-6">
            </button>
            <button
              class="bg-white border border-gray-300 p-2 rounded-md flex items-center justify-center hover:bg-gray-50 transition-colors">
              <img src="https://img.icons8.com/color/48/000000/twitter--v1.png" alt="Twitter" class="w-6 h-6">
            </button>
          </div>

          <div class="my-4 flex items-center">
            <div class="flex-grow border-t border-gray-300"></div>
            <span class="px-3 text-gray-500 text-sm">或使用账号登录</span>
            <div class="flex-grow border-t border-gray-300"></div>
          </div>
        </div>

        <LoginForm />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginForm from "@/components/user/LoginForm.vue";
import { Check } from "@element-plus/icons-vue";

// 特色列表
const features = [
  "海量图书资源，畅享阅读乐趣",
  "个性化推荐，发现您的阅读偏好",
  "会员专享价格，定期限时优惠",
  "安全便捷的购物体验",
];
</script>

<style scoped>
/* 自定义装饰效果 */
.bg-tomato-600 {
  position: relative;
  overflow: hidden;
}

.bg-tomato-600::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: radial-gradient(circle at 20% 150%, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
}
</style>
