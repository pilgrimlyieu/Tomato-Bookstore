<template>
  <div class="container mx-auto py-8 px-4">
    <div v-if="productStore.loading" class="max-w-6xl mx-auto">
      <el-skeleton :rows="10" animated />
    </div>

    <div v-else-if="!product" class="max-w-6xl mx-auto py-16 text-center">
      <el-result
        icon="error"
        title="商品不存在"
        sub-title="找不到该商品或已被删除"
      >
        <template #extra>
          <el-button type="primary" @click="router.push(Routes.PRODUCT_LIST)" class="rounded-lg">
            查看所有商品
          </el-button>
        </template>
      </el-result>
    </div>

    <div v-else class="max-w-6xl mx-auto">
      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="mb-6">
        <el-breadcrumb-item :to="{ path: Routes.HOME }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: Routes.PRODUCT_LIST }">商品列表</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product.title }}</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 商品详情卡片 -->
      <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6">
        <div class="flex flex-col md:flex-row gap-8">
          <!-- 商品图片 -->
          <div class="md:w-2/5">
            <div class="bg-gray-100 rounded-xl aspect-w-3 aspect-h-4 overflow-hidden shadow-md">
              <img
                :src="product.cover || '@/assets/images/placeholder.svg'"
                :alt="product.title"
                referrerPolicy="no-referrer"
                class="w-full h-full object-cover object-center"
              />
            </div>
          </div>

          <!-- 商品信息 -->
          <div class="md:w-3/5">
            <h1 class="text-3xl font-bold text-gray-800 mb-3">{{ product.title }}</h1>

            <!-- 评分 -->
            <div class="flex items-center mb-4">
              <div class="flex items-center">
                <el-rate
                  disabled
                  :max="5"
                  :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                  text-color="#ff9900"
                  :model-value="product.rate / 2"
                  class="mr-2"
                />
              </div>
              <span class="text-gray-600">{{ product.rate }}/10</span>
                <span
                v-if="totalReviews > 0"
                class="text-blue-500 ml-2 cursor-pointer"
                @click="scrollToReviews"
                >
                （{{ totalReviews }} 条评价）
                </span>
                <span v-else class="text-gray-400 ml-2">（暂无评价）</span>
            </div>

            <!-- 价格 -->
            <div class="mb-6">
              <p class="text-3xl font-bold text-tomato-600">{{ formatPrice(product.price) }}</p>
            </div>

            <!-- 库存信息 -->
            <div v-if="stockpile" class="mb-6">
              <p class="text-gray-700 mb-1">
                <span class="font-medium">库存：</span>
                <span :class="{'text-green-600': stockpile.amount > 10, 'text-tomato-600': stockpile.amount <= 10}">
                  {{ stockpile.amount }}
                </span>
              </p>
            </div>

            <!-- 商品规格 -->
            <div v-if="product.specifications && product.specifications.length > 0" class="mb-6">
              <h3 class="text-lg font-medium text-gray-800 mb-2">商品规格</h3>
              <div class="grid grid-cols-2 gap-4">
                <div v-for="spec in product.specifications" :key="spec.id" class="flex">
                  <span class="font-medium text-gray-700">{{ spec.item }}:</span>
                  <span class="ml-2 text-gray-600">{{ spec.value }}</span>
                </div>
              </div>
            </div>

            <!-- 描述 -->
            <div v-if="product.description" class="mb-6">
              <h3 class="text-lg font-medium text-gray-800 mb-2">商品描述</h3>
              <p class="text-gray-600">{{ product.description }}</p>
            </div>

            <!-- 购买数量 -->
            <div class="mb-6">
              <h3 class="text-lg font-medium text-gray-800 mb-2">数量</h3>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="stockpile?.amount || 1"
                size="large"
                :disabled="!stockpile || stockpile.amount <= 0"
                class="w-32"
              />
              <span v-if="stockpile && stockpile.amount <= 0" class="ml-3 text-red-600 text-sm">
                该商品暂无库存
              </span>
              <span v-else-if="stockpile && stockpile.amount <= 10" class="ml-3 text-tomato-600 text-sm">
                库存紧张
              </span>
            </div>

            <!-- 操作按钮 -->
            <div class="mt-6 flex gap-4">
              <el-button
                type="primary"
                size="large"
                class="rounded-lg"
                :icon="ShoppingCart"
                @click="handleAddToCart"
                :disabled="!stockpile || stockpile.amount <= 0 || adding"
                :loading="adding"
              >
                加入购物车
              </el-button>
            </div>
          </div>
        </div>

        <!-- 商品详情 -->
        <div v-if="product.detail" class="mt-10">
          <el-divider>
            <span class="text-lg font-medium">详细内容</span>
          </el-divider>
            <div class="product-detail mt-6 prose max-w-none text-left" v-html="product.detail"></div>
        </div>

        <!-- 读书笔记部分 -->
        <div class="mt-10" ref="noteSection">
          <el-divider>
            <span class="text-lg font-medium">读书笔记</span>
          </el-divider>
          <div class="mt-6">
            <NoteList
              :notes="productNotes"
              :loading="notesLoading"
              :product-id="productId"
              :show-create-button="true"
              @view="handleViewNote"
              @edit="handleEditNote"
              @delete="handleDeleteNote"
              @create="handleCreateNote"
            />
          </div>
        </div>

        <!-- 书评部分 -->
        <div class="mt-10" ref="reviewSection">
          <el-divider>
            <span class="text-lg font-medium">用户评价</span>
          </el-divider>
          <div class="mt-6">
            <ReviewList :product-id="productId" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import NoteList from "@/components/note/NoteList.vue";
import ReviewList from "@/components/review/ReviewList.vue";
import { useCart } from "@/composables/useCart";
import { useNote } from "@/composables/useNote";
import { Routes } from "@/constants/routes";
import { useProductStore } from "@/stores/product";
import { useReviewStore } from "@/stores/review";
import type { Note } from "@/types/note";
import { formatPrice } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import { ShoppingCart } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";

const productStore = useProductStore();
const reviewStore = useReviewStore();
const route = useRoute();
const router = useRouter();
const { addToCart } = useCart();
const { deleteNote } = useNote();
const { loading: notesLoading, productNotes, fetchProductNotes } = useNote();

const productId = computed(() => Number(route.params.id));
const product = computed(() => productStore.currentProduct);
const stockpile = computed(() => productStore.currentStockpile);
const quantity = ref(1);
const adding = ref(false);
const reviewSection = ref<HTMLElement | null>(null);
const noteSection = ref<HTMLElement | null>(null);

// 书评总数
const totalReviews = computed(() => {
  if (reviewStore.loading) return 0;
  return reviewStore.productReviews.length;
});

// 加载商品数据
const loadProductData = async () => {
  await productStore.fetchProductById(productId.value);
  if (productStore.currentProduct) {
    await Promise.all([
      productStore.fetchStockpile(productId.value),
      fetchProductNotes(productId.value),
    ]);
  }
};

onMounted(() => {
  loadProductData();
});

// 当路由参数变化时重新加载商品信息
watch(
  () => route.params.id,
  () => {
    productStore.clearCurrentProduct();
    quantity.value = 1;
    loadProductData();
  },
);

// 添加商品到购物车
const handleAddToCart = async () => {
  // 检查库存
  if (!stockpile.value || stockpile.value.amount < quantity.value) {
    ElMessage.error("商品库存不足");
    return;
  }

  try {
    adding.value = true;
    await addToCart(productId.value, quantity.value, product.value?.title);
  } finally {
    adding.value = false;
  }
};

// 滚动到评论区域
const scrollToReviews = () => {
  if (reviewSection.value) {
    reviewSection.value.scrollIntoView({ behavior: "smooth", block: "start" });
  }
};

// 查看笔记
const handleViewNote = (note: Note) => {
  router.push(buildRoute(Routes.NOTE_DETAIL, { noteId: note.id }));
};

// 编辑笔记
const handleEditNote = (note: Note) => {
  router.push(buildRoute(Routes.NOTE_EDIT, { noteId: note.id }));
};

// 删除笔记
const handleDeleteNote = async (note: Note) => {
  await deleteNote(note.id, productId.value);
};

// 创建笔记
const handleCreateNote = () => {
  router.push(buildRoute(Routes.NOTE_CREATE, { productId: productId.value }));
};
</script>

<style scoped>
.product-detail :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 0.5rem;
  margin: 1rem 0;
}

.product-detail :deep(h1),
.product-detail :deep(h2),
.product-detail :deep(h3),
.product-detail :deep(h4),
.product-detail :deep(h5),
.product-detail :deep(h6) {
  margin-top: 1.5rem;
  margin-bottom: 0.75rem;
  font-weight: 600;
}

.product-detail :deep(p) {
  margin-bottom: 1rem;
  line-height: 1.7;
}

.product-detail :deep(ul),
.product-detail :deep(ol) {
  padding-left: 1.5rem;
  margin-bottom: 1rem;
}

.product-detail :deep(li) {
  margin-bottom: 0.5rem;
}
</style>
