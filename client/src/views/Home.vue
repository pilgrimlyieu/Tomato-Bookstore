<template>
  <div>
    <!-- 轮播图区域 -->
    <div class="relative bg-gray-100">
      <el-carousel height="500px" indicator-position="outside" arrow="always" :interval="5000">
        <el-carousel-item v-for="(slide, index) in carouselSlides" :key="index">
          <div class="h-full w-full relative overflow-hidden">
            <!-- 背景图片 -->
            <div class="absolute inset-0 bg-cover bg-center bg-no-repeat"
              :style="`background-image: url(${slide.image})`">
              <!-- 暗色渐变遮罩 -->
              <div class="absolute inset-0 bg-gradient-to-r from-black/60 to-black/30"></div>
            </div>

            <!-- 文本内容 -->
            <div class="container mx-auto h-full flex items-center px-4 relative z-10">
              <div class="w-full md:w-1/2 text-white animate-fade-in" style="animation-delay: 0.3s">
                <h2 class="text-4xl md:text-5xl font-bold mb-4">{{ slide.title }}</h2>
                <p class="text-lg md:text-xl mb-6 text-gray-200">{{ slide.description }}</p>
                <router-link :to="slide.link">
                  <el-button type="primary" size="large" class="btn-gradient">
                    {{ slide.buttonText }}
                  </el-button>
                </router-link>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 广告推荐 -->
    <section class="py-16 px-4">
      <div class="container mx-auto">
        <div class="flex justify-between items-center mb-8">
          <h2 class="section-title">精选图书推荐</h2>
        </div>

        <advertisement-list />
      </div>
    </section>

    <!-- 为什么选择番茄商城 -->
    <section class="py-16 px-4 bg-gradient-to-br from-white to-tomato-50">
      <div class="container mx-auto">
        <h2 class="text-3xl font-bold text-center mb-12">为什么选择番茄书城？</h2>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <!-- 特色 1 -->
          <div class="bg-white p-6 rounded-xl shadow-md text-center hover-lift transition-all relative">
            <div
              class="absolute -top-5 left-1/2 transform -translate-x-1/2 w-14 h-14 rounded-full bg-tomato-100 flex items-center justify-center text-tomato-600">
              <el-icon size="30">
                <Collection />
              </el-icon>
            </div>
            <div class="mt-6">
              <h3 class="text-xl font-semibold mb-2">丰富的图书资源</h3>
              <p class="text-gray-600">
                拥有超过 10,000 本精选图书，涵盖小说、科技、艺术、历史等各种类别。
              </p>
            </div>
          </div>
          <!-- 特色 2 -->
          <div class="bg-white p-6 rounded-xl shadow-md text-center hover-lift transition-all relative">
            <div
              class="absolute -top-5 left-1/2 transform -translate-x-1/2 w-14 h-14 rounded-full bg-tomato-100 flex items-center justify-center text-tomato-600">
              <el-icon size="30">
                <Van />
              </el-icon>
            </div>
            <div class="mt-6">
              <h3 class="text-xl font-semibold mb-2">快速配送</h3>
              <p class="text-gray-600">
                48 小时内发货，大部分地区支持次日达，让您尽快开始阅读之旅。
              </p>
            </div>
          </div>
          <!-- 特色 3 -->
          <div class="bg-white p-6 rounded-xl shadow-md text-center hover-lift transition-all relative">
            <div
              class="absolute -top-5 left-1/2 transform -translate-x-1/2 w-14 h-14 rounded-full bg-tomato-100 flex items-center justify-center text-tomato-600">
              <el-icon size="30">
                <Discount />
              </el-icon>
            </div>
            <div class="mt-6">
              <h3 class="text-xl font-semibold mb-2">超值优惠</h3>
              <p class="text-gray-600">
                定期推出折扣活动，会员专享价格，让您以最实惠的价格获取心仪图书。
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 客户评价轮播 -->
    <section class="py-16 px-4">
      <div class="container mx-auto">
        <h2 class="text-3xl font-bold text-center mb-2">读者心声</h2>
        <p class="text-gray-600 text-center mb-10">来自我们真实客户的评价</p>
        <el-carousel :interval="5000" indicator-position="none" height="250px" arrow="always"
          class="testimonial-carousel">
          <el-carousel-item v-for="(testimonial, index) in testimonials" :key="index">
            <div class="h-full flex items-center justify-center px-10">
              <div class="text-center">
                <div class="text-5xl text-tomato-200 mb-4">"</div>
                <p class="text-lg text-gray-700 mb-6 italic max-w-3xl mx-auto">{{ testimonial.text }}</p>
                <div class="flex items-center justify-center">
                  <el-avatar :size="48" :src="testimonial.avatar" class="mr-3 border-2 border-gray-200" />
                  <div class="text-left">
                    <div class="font-semibold text-gray-800">{{ testimonial.name }}</div>
                    <div class="text-gray-600 text-sm">{{ testimonial.title }}</div>
                  </div>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>

    <!-- 订阅通讯 -->
    <section class="py-16 px-4 bg-gradient-to-r from-tomato-600 to-tomato-700 text-white">
      <div class="container mx-auto max-w-4xl">
        <div class="text-center mb-8">
          <h2 class="text-3xl font-bold mb-2">订阅我们的通讯</h2>
          <p class="text-tomato-100">获取最新图书推荐、特别优惠和阅读灵感</p>
        </div>
        <div class="max-w-lg mx-auto">
          <el-form @submit.prevent="handleNewsletter">
            <div class="flex">
              <el-input v-model="newsletterEmail" placeholder="您的邮箱地址" class="flex-grow rounded-r-none border-none"
                size="large" />
              <el-button type="default" size="large" class="rounded-l-none font-medium" :loading="subscribingNewsletter"
                @click="handleNewsletter">
                订阅
              </el-button>
            </div>
          </el-form>
          <p class="text-tomato-100 text-sm mt-2 text-center">
            我们每月仅发送一次通讯，您可以随时取消订阅。
          </p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import AdvertisementList from "@/components/advertisement/AdvertisementList.vue";
import { Collection, Discount, Van } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { ref } from "vue";

// 轮播数据
const carouselSlides = [
  {
    title: "探索阅读的新世界",
    description: "新书上架，限时折扣。立即浏览我们精选的图书系列。",
    buttonText: "立即探索",
    link: "/books",
    image:
      "https://images.unsplash.com/photo-1507842217343-583bb7270b66?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80",
  },
  {
    title: "夏季阅读大促",
    description: "精选好书 5 折起，多买多省，点击了解详情。",
    buttonText: "查看特惠",
    link: "/promotions",
    image:
      "https://images.unsplash.com/photo-1476275466078-4007374efbbe?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80",
  },
  {
    title: "发现您的下一本最爱",
    description: "基于您的阅读喜好，为您推荐定制化的图书。",
    buttonText: "获取推荐",
    link: "/recommendations",
    image:
      "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80",
  },
];

// 客户评价数据
const testimonials = [
  {
    text: "番茄书城是我买书的首选，不仅种类丰富，而且配送速度很快。最近买的《深度学习》图书质量非常好，价格也比实体书店优惠很多。",
    name: "张明",
    title: "软件工程师",
    avatar: "https://randomuser.me/api/portraits/men/32.jpg",
  },
  {
    text: "作为一个爱读书的人，番茄书城为我提供了很多难以在本地书店找到的书籍。他们的客户服务也很棒，有一次订单出了问题，客服很快就帮我解决了。",
    name: "李红",
    title: "大学教师",
    avatar: "https://randomuser.me/api/portraits/women/44.jpg",
  },
  {
    text: "孩子很喜欢这里的儿童读物，质量好、内容丰富。而且网站的推荐系统很智能，经常能发现适合孩子的新书。",
    name: "王强",
    title: "家长",
    avatar: "https://randomuser.me/api/portraits/men/15.jpg",
  },
];

// 通讯订阅
const newsletterEmail = ref("");
const subscribingNewsletter = ref(false);

const handleNewsletter = () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!newsletterEmail.value.trim()) {
    ElMessage.warning("请输入邮箱地址");
    return;
  }

  if (!emailRegex.test(newsletterEmail.value)) {
    ElMessage.error("请输入有效的邮箱地址");
    return;
  }

  subscribingNewsletter.value = true;

  // 模拟订阅请求
  setTimeout(() => {
    ElMessage({
      message: "订阅成功！感谢您的关注",
      type: "success",
    });
    newsletterEmail.value = "";
    subscribingNewsletter.value = false;
  }, 1000);
};
</script>

<style scoped>
/* 轮播箭头样式 */
:deep(.el-carousel__arrow) {
  background-color: rgba(255, 107, 107, 0.7);
}

:deep(.el-carousel__arrow:hover) {
  background-color: rgba(255, 107, 107, 0.9);
}

/* 卡片轮播样式 */
:deep(.el-carousel__item--card) {
  transform: scale(0.9);
  transition: all 0.4s ease;
  opacity: 0.8;
}

:deep(.el-carousel__item--card.is-active) {
  transform: scale(1);
  opacity: 1;
}
</style>
