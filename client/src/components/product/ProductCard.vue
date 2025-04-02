<template>
  <div class="product-card group relative rounded-xl overflow-hidden shadow-md transition-all duration-300 hover:shadow-lg bg-white/90 backdrop-blur-sm">
    <!-- 商品图片 -->
    <div class="aspect-w-1 aspect-h-1 w-full overflow-hidden rounded-t-xl bg-gray-200">
      <img
        :src="product.cover || '/images/book-placeholder.jpg'"
        :alt="product.title"
        class="h-full w-full object-cover object-center transition-all duration-500 group-hover:scale-105"
      />
    </div>

    <!-- 商品信息 -->
    <div class="p-4">
      <h3 class="mt-1 text-lg font-medium text-gray-900 line-clamp-1">
        {{ product.title }}
      </h3>

      <!-- 评分 -->
      <div class="mt-1 flex items-center">
        <el-rate
          disabled
          :max="5"
          :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
          text-color="#ff9900"
          :model-value="product.rate / 2"
          class="mr-1"
        />
        <span class="text-sm text-gray-500">{{ product.rate }}/10</span>
      </div>

      <!-- 价格 -->
      <p class="mt-2 text-xl font-semibold text-tomato-600">¥{{ product.price }}</p>

      <!-- 操作按钮 -->
      <div class="mt-3 flex items-center justify-between">
        <el-button
          type="primary"
          size="small"
          class="rounded-lg"
          :icon="View"
          @click="viewDetail"
        >
          查看详情
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import type { Product } from "@/types/product";
import { buildRoute } from "@/utils/routeHelper";
import { View } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";

const props = defineProps<{
  product: Product;
}>();

const router = useRouter();

// 查看商品详情方法
const viewDetail = () => {
  router.push(buildRoute(Routes.PRODUCT_DETAIL, { id: props.product.id }));
};
</script>
