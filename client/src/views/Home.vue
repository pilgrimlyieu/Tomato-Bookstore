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

    <!-- 类别快速导航 -->
    <div class="bg-gray-50 py-10 animate-fade-in">
      <div class="container mx-auto px-4">
        <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
          <router-link v-for="(category, index) in categories" :key="index" :to="category.link"
            class="bg-white p-4 rounded-xl shadow-sm text-center hover-lift group">
            <div
              class="bg-gray-100 p-3 rounded-full w-16 h-16 mx-auto mb-3 flex items-center justify-center group-hover:bg-tomato-50 transition-colors">
              <img :src="category.icon" :alt="category.name" class="w-8 h-8">
            </div>
            <h3 class="text-gray-700 font-medium group-hover:text-tomato-600 transition-colors">{{ category.name }}</h3>
          </router-link>
        </div>
      </div>
    </div>

    <!-- 新书上架 -->
    <section class="py-16 px-4">
      <div class="container mx-auto">
        <div class="flex justify-between items-center mb-8">
          <h2 class="section-title">新书上架</h2>
          <router-link to="/new-releases" class="text-tomato-600 hover:text-tomato-700 flex items-center group">
            <span>查看全部</span>
            <el-icon class="ml-1 group-hover:translate-x-1 transition-transform">
              <ArrowRight />
            </el-icon>
          </router-link>
        </div>

        <el-carousel :interval="4000" type="card" height="320px">
          <el-carousel-item v-for="(book, index) in newBooks" :key="index">
            <div class="h-full rounded-lg overflow-hidden group">
              <div
                class="h-full flex flex-col bg-white rounded-lg shadow-md hover:shadow-xl transition-shadow duration-300">
                <div class="relative h-48 overflow-hidden">
                  <img :src="book.cover" :alt="book.title"
                    class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110">
                  <div class="absolute top-2 right-2 bg-tomato-500 text-white text-xs px-2 py-1 rounded-full">
                    新书
                  </div>
                </div>
                <div class="p-4 flex flex-col flex-grow">
                  <h3 class="text-lg font-semibold text-gray-800 mb-1 group-hover:text-tomato-600 transition-colors">{{
                    book.title }}</h3>
                  <p class="text-gray-600 text-sm mb-2">{{ book.author }}</p>
                  <div class="flex items-center mb-3">
                    <el-rate v-model="book.rating" disabled text-color="#ff9900" />
                    <span class="text-gray-500 text-xs ml-1">({{ book.reviewCount }})</span>
                  </div>
                  <div class="mt-auto flex justify-between items-center">
                    <span class="text-tomato-600 font-bold">¥{{ book.price.toFixed(2) }}</span>
                    <el-button size="small" type="primary" plain>
                      <el-icon>
                        <Plus />
                      </el-icon>
                      加入购物车
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>

    <!-- 特惠商品 -->
    <section class="py-16 px-4 bg-gray-50">
      <div class="container mx-auto">
        <div class="flex justify-between items-center mb-8">
          <h2 class="section-title">特惠活动</h2>
          <router-link to="/promotions" class="text-tomato-600 hover:text-tomato-700 flex items-center group">
            <span>查看全部</span>
            <el-icon class="ml-1 group-hover:translate-x-1 transition-transform">
              <ArrowRight />
            </el-icon>
          </router-link>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div v-for="(promo, index) in promotions" :key="index" class="card-gradient group relative overflow-hidden">
            <!-- 打折标签 -->
            <div class="absolute top-0 right-0 bg-tomato-600 text-white z-10 px-3 py-1 font-medium shadow-md">
              {{ promo.discount }}折
            </div>

            <!-- 商品图片 -->
            <div class="relative w-3/5 mx-auto mb-4">
              <img :src="promo.image" :alt="promo.title"
                class="w-full h-48 object-contain transition-transform duration-500 group-hover:scale-105">
            </div>

            <!-- 商品信息 -->
            <h3 class="text-lg font-semibold text-gray-800 mb-1 group-hover:text-tomato-600 transition-colors">{{
              promo.title }}</h3>
            <p class="text-gray-600 text-sm mb-3 line-clamp-2">{{ promo.description }}</p>

            <!-- 价格与计时器 -->
            <div class="mt-2 flex justify-between items-center">
              <div>
                <div class="text-tomato-600 font-bold text-lg">¥{{ promo.price.toFixed(2) }}</div>
                <div class="text-gray-500 line-through text-xs">¥{{ promo.originalPrice.toFixed(2) }}</div>
              </div>
              <div class="text-xs text-gray-600">
                剩余 {{ promo.timeLeft }}
              </div>
            </div>

            <!-- 加入购物车按钮 -->
            <el-button class="w-full mt-4" type="primary">立即抢购</el-button>
          </div>
        </div>
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
import { useUserStore } from "@/stores/user";
import {
  ArrowRight,
  Collection,
  Discount,
  Plus,
  Van,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { reactive, ref } from "vue";

const userStore = useUserStore();

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

// 分类数据
const categories = [
  {
    name: "文学小说",
    icon: "https://img.icons8.com/color/96/000000/literature.png",
    link: "/categories/fiction",
  },
  {
    name: "科学技术",
    icon: "https://img.icons8.com/color/96/000000/microscope.png",
    link: "/categories/science",
  },
  {
    name: "艺术设计",
    icon: "https://img.icons8.com/color/96/000000/paint-palette.png",
    link: "/categories/art",
  },
  {
    name: "历史传记",
    icon: "https://img.icons8.com/color/96/000000/historical.png",
    link: "/categories/history",
  },
  {
    name: "教育考试",
    icon: "https://img.icons8.com/color/96/000000/graduation-cap.png",
    link: "/categories/education",
  },
  {
    name: "少儿读物",
    icon: "https://img.icons8.com/color/96/000000/babys-room.png",
    link: "/categories/children",
  },
];

// 新书数据
const newBooks = reactive([
  {
    title: "云边有个小卖部",
    author: "张嘉佳",
    price: 38.6,
    rating: 4.5,
    reviewCount: 2467,
    cover: "https://picsum.photos/seed/book1/300/400",
  },
  {
    title: "深度学习",
    author: "Ian Goodfellow / 等",
    price: 168.0,
    rating: 5,
    reviewCount: 845,
    cover: "https://picsum.photos/seed/book2/300/400",
  },
  {
    title: "天才在左，疯子在右",
    author: "高铭",
    price: 29.8,
    rating: 4,
    reviewCount: 5412,
    cover: "https://picsum.photos/seed/book3/300/400",
  },
  {
    title: "活着",
    author: "余华",
    price: 20.0,
    rating: 4.5,
    reviewCount: 8954,
    cover: "https://picsum.photos/seed/book4/300/400",
  },
  {
    title: "人类简史",
    author: "尤瓦尔·赫拉利",
    price: 68.0,
    rating: 4.5,
    reviewCount: 3268,
    cover: "https://picsum.photos/seed/book5/300/400",
  },
]);

// 特惠商品数据
const promotions = [
  {
    title: "自控力",
    description: "斯坦福大学最受欢迎心理学课程",
    price: 39.8,
    originalPrice: 59.8,
    discount: 6.7,
    timeLeft: "2 天 23 小时",
    image: "https://picsum.photos/seed/promo1/300/400",
  },
  {
    title: "百年孤独",
    description: "魔幻现实主义文学代表作",
    price: 29.9,
    originalPrice: 39.9,
    discount: 7.5,
    timeLeft: "3 天 12 小时",
    image: "https://picsum.photos/seed/promo2/300/400",
  },
  {
    title: "JavaScript 高级程序设计",
    description: "JS 红宝书最新版",
    price: 99.0,
    originalPrice: 129.0,
    discount: 7.7,
    timeLeft: "1 天 8 小时",
    image: "https://picsum.photos/seed/promo3/300/400",
  },
  {
    title: "时间简史",
    description: "史蒂芬·霍金的宇宙学著作",
    price: 35.0,
    originalPrice: 45.0,
    discount: 7.8,
    timeLeft: "4 天 5 小时",
    image: "https://picsum.photos/seed/promo4/300/400",
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
