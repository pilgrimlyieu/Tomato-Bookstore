<template>
  <div>
    <!-- 标题 -->
    <h3 class="text-xl font-medium mb-6">{{ isEdit ? '编辑商品' : '创建商品' }}</h3>

    <!-- 加载状态 -->
    <div v-if="loading && isEdit" class="py-8">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 编辑表单 -->
    <div v-else>
      <ProductForm
        :product="currentProduct"
        :loading="productStore.adminLoading"
        :submit-button-text="isEdit ? '更新商品' : '创建商品'"
        @submit="handleSubmit"
        @cancel="handleCancel"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import ProductForm from "@/components/product/ProductForm.vue";
import { Routes } from "@/constants/routes";
import { useProductStore } from "@/stores/product";
import type { Product } from "@/types/product";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const productStore = useProductStore();

const loading = ref(false);
const currentProduct = ref<Product | null>(null);

// 判断是编辑还是创建
const isEdit = computed(() => Boolean(route.params.id));

// 加载商品数据（如果是编辑模式）
onMounted(async () => {
  if (isEdit.value) {
    loading.value = true;
    const productId = Number(route.params.id);
    await productStore.fetchProductById(productId);
    currentProduct.value = productStore.currentProduct;
    loading.value = false;
  }
});

// 提交表单
const handleSubmit = async (product: Product) => {
  let success = false;

  if (isEdit.value) {
    // 更新商品
    success = await productStore.updateProduct(product);
    if (success) {
      ElMessage.success("商品更新成功");
      router.push(Routes.ADMIN_PRODUCTS);
    }
  } else {
    // 创建商品
    success = await productStore.createProduct(product);
    if (success) {
      ElMessage.success("商品创建成功");
      router.push(Routes.ADMIN_PRODUCTS);
    }
  }
};

// 取消操作
const handleCancel = () => {
  router.push(Routes.ADMIN_PRODUCTS);
};
</script>
