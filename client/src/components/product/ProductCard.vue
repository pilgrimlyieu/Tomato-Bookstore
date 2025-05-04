<template>
  <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md overflow-hidden hover-lift">
    <router-link :to="buildRoute(Routes.PRODUCT_DETAIL, { id: product.id! })">
      <div class="relative h-56 overflow-hidden">
        <img
          :src="product.cover || '/images/placeholder.svg'"
          :alt="product.title"
          referrerPolicy="no-referrer"
          class="w-full h-full object-cover transform transition duration-300 hover:scale-110"
        />
      </div>
    </router-link>

    <div class="p-4">
      <router-link :to="buildRoute(Routes.PRODUCT_DETAIL, { id: product.id! })">
        <h3 class="text-lg font-medium text-gray-800 hover:text-tomato-600 mb-2 line-clamp-2">
          {{ product.title }}
        </h3>
      </router-link>

      <!-- 评分 -->
      <div class="flex items-center mb-3">
        <el-rate
          disabled
          :max="5"
          :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
          text-color="#ff9900"
          :model-value="product.rate / 2"
          class="mr-2"
          size="small"
        />
        <span class="text-gray-500 text-sm">{{ product.rate }}/10</span>
      </div>

      <!-- 价格和操作 -->
      <div class="flex justify-between items-center mt-4">
        <span class="text-tomato-600 font-medium text-lg">{{ formatPrice(product.price) }}</span>
        <el-button size="small" type="primary" plain class="rounded-lg" @click.stop="handleAddToCart">
          <el-icon><Plus /></el-icon>
          加入购物车
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useCart } from "@/composables/useCart";
import { Routes } from "@/constants/routes";
import type { Product } from "@/types/product";
import { formatPrice } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import { Plus } from "@element-plus/icons-vue";

const props = defineProps<{
  product: Product;
}>();

const { addToCart } = useCart();

// 添加商品到购物车
const handleAddToCart = async (event: Event) => {
  // 阻止事件冒泡，防止触发父元素的点击事件
  event.stopPropagation();

  await addToCart(props.product.id!, 1, props.product.title);
};
</script>
