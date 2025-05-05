<template>
  <div class="user-reviews-container p-6">
    <el-card class="w-full">
      <template #header>
        <div class="card-header">
          <h1 class="text-xl font-bold">我的书评</h1>
          <p class="text-gray-500 mt-2">管理您发表的所有书评</p>
        </div>
      </template>

      <div v-loading="loading">
        <template v-if="reviews.length > 0">
          <div class="mb-4 review-stats flex items-center gap-4">
            <div class="text-gray-600">
              共发表了 <span class="text-primary font-bold">{{ reviews.length }}</span> 条书评
            </div>
          </div>

          <el-table :data="reviewsWithProductNames" style="width: 100%" v-loading="loading">
            <el-table-column label="书名" min-width="150">
              <template #default="{ row }">
                <router-link
                  :to="buildRoute(Routes.PRODUCT_DETAIL, { id: row.productId })"
                  class="text-primary hover:text-primary-dark hover:underline"
                >
                  《{{ row.productName }}》
                </router-link>
              </template>
            </el-table-column>

            <el-table-column label="评分" width="200">
              <template #default="{ row }">
                <el-rate
                  v-model="row.rating"
                  disabled
                  :max="10"
                  show-score
                  score-template="{value}"
                />
              </template>
            </el-table-column>

            <el-table-column label="评论内容" min-width="250">
              <template #default="{ row }">
                <div class="truncate max-w-md" v-if="row.content">
                  {{ row.content }}
                </div>
                <div v-else class="text-gray-400 italic">无评论内容</div>
              </template>
            </el-table-column>

            <el-table-column label="发布时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>

            <el-table-column label="操作" width="150" align="center">
              <template #default="{ row }">
                <el-button-group>
                  <el-button
                    type="primary"
                    link
                    @click="handleEditReview(row)"
                  >
                    编辑
                  </el-button>
                  <el-button
                    type="danger"
                    link
                    @click="handleDeleteReview(row)"
                  >
                    删除
                  </el-button>
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>
        </template>

        <el-empty v-else description="您还没有发表过任何书评">
          <router-link :to="Routes.PRODUCT_LIST">
            <el-button type="primary">浏览商品</el-button>
          </router-link>
        </el-empty>
      </div>
    </el-card>

    <el-dialog
      v-model="showEditDialog"
      title="编辑书评"
      width="500px"
      destroy-on-close
    >
      <ReviewForm
        v-if="showEditDialog && currentEditReview"
        :initial-data="currentEditReview"
        :loading="formLoading"
        :is-edit="true"
        @submit="handleUpdateReview"
        @cancel="showEditDialog = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import ReviewForm from "@/components/review/ReviewForm.vue";
import { Routes } from "@/constants/routes";
import { useProductStore } from "@/stores/product";
import { useReviewStore } from "@/stores/review";
import type { Review, ReviewUpdateParams } from "@/types/review";
import { formatDate } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import { ElMessageBox } from "element-plus";
import { computed, onMounted, ref } from "vue";

// store
const reviewStore = useReviewStore();
const productStore = useProductStore();

// 状态变量
const showEditDialog = ref(false);
const currentEditReview = ref<Review | null>(null);
const formLoading = ref(false);

// 计算属性
const reviews = computed(() => reviewStore.userReviews);
const loading = computed(() => reviewStore.loading);

// 加载用户的所有书评
onMounted(async () => {
  await reviewStore.fetchUserReviews();
  await productStore.fetchAllProducts();
});

const reviewsWithProductNames = computed(() => {
  return reviewStore.userReviews.map((review) => ({
    ...review,
    productName:
      productStore.products.find((p) => p.id === review.productId)?.title ||
      "未知书名",
  }));
});

// 处理编辑书评
const handleEditReview = (review: Review) => {
  currentEditReview.value = review;
  showEditDialog.value = true;
};

// 更新书评
const handleUpdateReview = async (formData: ReviewUpdateParams) => {
  if (!currentEditReview.value?.id) return;

  formLoading.value = true;

  try {
    const success = await reviewStore.updateReview(
      currentEditReview.value.id,
      formData,
    );

    if (success) {
      showEditDialog.value = false;
      currentEditReview.value = null;
    }
  } finally {
    formLoading.value = false;
  }
};

// 处理删除书评
const handleDeleteReview = async (review: Review) => {
  if (!review.id) return;

  try {
    await ElMessageBox.confirm(
      "确定要删除这条书评吗？此操作不可撤销。",
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      },
    );

    await reviewStore.deleteReview(review.id);
  } catch {
    // 用户取消删除
  }
};
</script>

<style scoped>
.user-reviews-container {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
