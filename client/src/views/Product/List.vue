<template>
  <div class="container mx-auto py-8 px-4">
    <div class="max-w-7xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-800 mb-8">全部商品</h1>

      <!-- 搜索和筛选区 -->
      <div class="mb-8 p-6 bg-white/80 backdrop-blur-md rounded-xl shadow-md">
        <div class="flex flex-col md:flex-row md:items-center gap-4">
          <div class="flex-grow">
            <el-input
              v-model="searchQuery"
              placeholder="搜索商品名称……"
              class="w-full"
              :prefix-icon="Search"
              clearable
              @input="handleSearch"
            />
          </div>
          <div class="flex items-center gap-4">
            <el-select v-model="sortBy" placeholder="排序方式" class="w-full md:w-40" @change="handleSearch">
              <el-option label="默认排序" value="default" />
              <el-option label="价格从低到高" value="price_asc" />
              <el-option label="价格从高到低" value="price_desc" />
              <el-option label="评分从高到低" value="rate_desc" />
            </el-select>
          </div>
        </div>
      </div>

      <!-- 商品列表 -->
      <div>
        <div v-if="productStore.loading" class="flex justify-center items-center py-20">
          <el-skeleton :rows="5" animated />
        </div>

        <div v-else-if="filteredProducts.length === 0" class="text-center py-16">
          <el-empty description="暂无商品" />
        </div>

        <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
          <transition-group name="fade">
            <ProductCard
              v-for="product in paginatedProducts"
              :key="product.id"
              :product="product"
            />
          </transition-group>
        </div>
      </div>

      <!-- 分页 -->
      <div class="mt-8 flex justify-center">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredProducts.length"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import ProductCard from "@/components/product/ProductCard.vue";
import { useProductStore } from "@/stores/product";
import { Search } from "@element-plus/icons-vue";
import { computed, onMounted, ref } from "vue";

const productStore = useProductStore();
const searchQuery = ref("");
const sortBy = ref("default");
const currentPage = ref(1);
const pageSize = ref(12);

// 加载商品数据
onMounted(async () => {
  if (productStore.products.length === 0) {
    await productStore.fetchAllProducts();
  }
});

// 过滤和排序商品列表
const filteredProducts = computed(() => {
  let result = [...productStore.products];

  // 搜索过滤
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (product) =>
        product.title.toLowerCase().includes(query) ||
        (product.description &&
          product.description.toLowerCase().includes(query)),
    );
  }

  // 排序
  switch (sortBy.value) {
    case "price_asc":
      result.sort((a, b) => a.price - b.price);
      break;
    case "price_desc":
      result.sort((a, b) => b.price - a.price);
      break;
    case "rate_desc":
      result.sort((a, b) => b.rate - a.rate);
      break;
    default:
      // 默认排序，保持原顺序
      break;
  }

  return result;
});

// 分页后的商品列表
const paginatedProducts = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  return filteredProducts.value.slice(startIndex, endIndex);
});

// 搜索处理函数
const handleSearch = () => {
  currentPage.value = 1;
};
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
