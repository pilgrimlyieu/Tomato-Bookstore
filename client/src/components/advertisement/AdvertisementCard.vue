<template>
  <div
    class="advert-card cursor-pointer overflow-hidden rounded-xl shadow-md transition-all hover:shadow-lg bg-white group"
    @click="navigateToProduct"
  >
    <div class="relative h-48 overflow-hidden">
      <!-- 广告图片 -->
      <img
        :src="advertisement.imageUrl"
        :alt="advertisement.title"
        class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
      />
      <!-- 广告标题横幅 -->
      <div class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/70 to-transparent p-4">
        <h3 class="text-white text-lg font-semibold line-clamp-1">{{ advertisement.title }}</h3>
      </div>
    </div>
    <div class="p-4">
      <p class="text-gray-600 line-clamp-2 h-12">{{ advertisement.content }}</p>
      <div class="mt-3 flex justify-end">
        <el-button type="primary" text class="rounded-lg group-hover:bg-tomato-50">
          查看详情
          <el-icon class="ml-1 group-hover:translate-x-1 transition-transform"><ArrowRight /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import type { Advertisement } from "@/types/advertisement";
import { buildRoute } from "@/utils/routeHelper";
import { ArrowRight } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";

const props = defineProps<{
  advertisement: Advertisement;
}>();

const router = useRouter();

/**
 * 跳转到广告对应的商品详情页
 */
const navigateToProduct = () => {
  router.push(
    buildRoute(Routes.PRODUCT_DETAIL, { id: props.advertisement.productId }),
  );
};
</script>
