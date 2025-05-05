<template>
  <div class="review-list">
    <div class="review-stats mb-6" v-if="reviews.length > 0">
      <div class="flex items-center gap-4">
        <div class="stats-item">
          <div class="text-lg font-medium">{{ averageRating.toFixed(1) }}</div>
          <div class="text-sm text-gray-500">平均评分</div>
        </div>
        <div class="stats-item">
          <div class="text-lg font-medium">{{ reviews.length }}</div>
          <div class="text-sm text-gray-500">评价数量</div>
        </div>
      </div>
      <el-progress
        :percentage="averageRating * 10"
        :status="getRatingStatus(averageRating)"
        :stroke-width="8"
        class="mt-2"
      />
    </div>

    <div class="new-review mb-6">
      <el-collapse v-if="isLoggedIn && !userReview" v-model="reviewFormActive">
        <el-collapse-item name="1">
          <template #title>
            <span class="text-primary">撰写书评</span>
          </template>
          <div class="p-2">
            <ReviewForm
              :product-id="productId"
              :loading="loading"
              @submit="handleCreateReview"
              @cancel="reviewFormActive = []"
            />
          </div>
        </el-collapse-item>
      </el-collapse>

      <el-alert
        v-else-if="!isLoggedIn"
        type="info"
        :closable="false"
        show-icon
      >
        <div class="flex gap-4 items-center">
          <span>登录后发表您的评价</span>
          <router-link :to="loginRoute" class="text-primary">
            <el-button type="primary" plain>登录</el-button>
          </router-link>
        </div>
      </el-alert>
    </div>

    <div class="review-sort mb-4 flex justify-between items-center">
      <div class="text-lg font-medium">用户评价 ({{ reviews.length }})</div>
      <el-select v-model="sortMethod" placeholder="排序方式" size="small">
        <el-option label="最新优先" value="latest" />
        <el-option label="最早优先" value="oldest" />
        <el-option label="评分高优先" value="highest" />
        <el-option label="评分低优先" value="lowest" />
      </el-select>
    </div>

    <div class="user-review mb-6" v-if="userReview">
      <div class="p-4 bg-primary-50 rounded-md border border-primary-100 mb-4">
        <div class="flex justify-between items-center mb-2">
          <div class="font-medium">我的评价</div>
          <div>
            <el-button type="primary" text @click="handleEditUserReview">
              编辑评价
            </el-button>
          </div>
        </div>
        <ReviewItem
          :review="userReview"
          @edit="handleEditUserReview"
          @delete="handleDeleteReview"
        />
      </div>
    </div>

    <div class="all-reviews" v-loading="loading">
      <template v-if="sortedReviews.length > 0">
        <ReviewItem
          v-for="review in sortedReviews"
          :key="review.id"
          :review="review"
          @edit="handleEditReview"
          @delete="handleDeleteReview"
        />
      </template>

      <el-empty
        v-else
        description="暂无评价"
        :image-size="100"
      >
        <template #description>
          <p>{{ isLoggedIn ? '成为第一个评价该商品的人吧' : '登录后评价该商品' }}</p>
        </template>
      </el-empty>

      <div class="pagination flex justify-center mt-6" v-if="reviews.length > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 20, 50]"
          layout="sizes, prev, pager, next"
          :total="reviews.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <el-dialog
      v-model="editDialogVisible"
      title="编辑评价"
      width="500px"
      destroy-on-close
    >
      <ReviewForm
        v-if="editDialogVisible && currentReview"
        :initial-data="currentReview"
        :is-edit="true"
        :loading="formLoading"
        @submit="handleUpdateReview"
        @cancel="editDialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useReviewStore } from "@/stores/review";
import { useUserStore } from "@/stores/user";
import type {
  Review,
  ReviewCreateParams,
  ReviewUpdateParams,
} from "@/types/review";
import { buildRoute } from "@/utils/routeHelper";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref, watch } from "vue";
import ReviewForm from "./ReviewForm.vue";
import ReviewItem from "./ReviewItem.vue";

// 属性
const props = defineProps<{
  productId: number;
}>();

// Stores
const userStore = useUserStore();
const reviewStore = useReviewStore();

// 状态
const currentPage = ref(1);
const pageSize = ref(10);
const sortMethod = ref<"latest" | "oldest" | "highest" | "lowest">("latest");
const reviewFormActive = ref<string[]>([]);
const editDialogVisible = ref(false);
const currentReview = ref<Review | null>(null);
const formLoading = ref(false);

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn);
const loading = computed(() => reviewStore.loading);
const reviews = computed(() => reviewStore.productReviews);

// 用户的书评（每个用户对同一商品只能有一条书评）
const userReview = computed(() => {
  if (!isLoggedIn.value || !userStore.user) return null;
  return (
    reviews.value.find((review) => review.userId === userStore.user?.id) || null
  );
});

// 剔除用户自己的书评后的其他用户书评
const otherReviews = computed(() => {
  if (!userReview.value) return reviews.value;
  return reviews.value.filter((review) => review.id !== userReview.value?.id);
});

// 按排序方法处理后的书评
const sortedReviews = computed(() => {
  const reviewsToSort = [...otherReviews.value];

  switch (sortMethod.value) {
    case "latest":
      return reviewsToSort.sort((a, b) => {
        return (
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
      });
    case "oldest":
      return reviewsToSort.sort((a, b) => {
        return (
          new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
        );
      });
    case "highest":
      return reviewsToSort.sort((a, b) => b.rating - a.rating);
    case "lowest":
      return reviewsToSort.sort((a, b) => a.rating - b.rating);
    default:
      return reviewsToSort;
  }
});

// 分页后的书评
const paginatedReviews = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return sortedReviews.value.slice(start, end);
});

// 平均评分
const averageRating = computed(() => {
  if (reviews.value.length === 0) return 0;
  const sum = reviews.value.reduce((acc, review) => acc + review.rating, 0);
  return sum / reviews.value.length;
});

// 登录路由（附带当前页面作为重定向）
const loginRoute = computed(() => {
  return {
    path: Routes.USER_LOGIN,
    query: {
      redirect: window.location.pathname + window.location.search,
    },
  };
});

// 监听商品ID变化，重新加载书评
watch(
  () => props.productId,
  async (newId) => {
    if (newId) {
      await fetchReviews();
    }
  },
  { immediate: true },
);

// 初始化
onMounted(async () => {
  if (props.productId) {
    await fetchReviews();
  }
});

// 获取书评列表
const fetchReviews = async () => {
  await reviewStore.fetchProductReviews(props.productId);
};

// 创建书评
const handleCreateReview = async (formData: ReviewCreateParams) => {
  const success = await reviewStore.createReview(props.productId, formData);

  if (success) {
    ElMessage.success("书评发表成功");
    reviewFormActive.value = [];
    await fetchReviews(); // 重新加载书评列表
  }
};

// 编辑用户自己的书评
const handleEditUserReview = () => {
  if (userReview.value) {
    currentReview.value = userReview.value;
    editDialogVisible.value = true;
  }
};

// 编辑书评（管理员）
const handleEditReview = (review: Review) => {
  currentReview.value = review;
  editDialogVisible.value = true;
};

// 更新书评
const handleUpdateReview = async (formData: ReviewUpdateParams) => {
  if (!currentReview.value?.id) return;

  formLoading.value = true;

  try {
    let success = false;

    // 判断是管理员操作还是普通用户操作
    if (
      userStore.isAdmin &&
      currentReview.value.userId !== userStore.user?.id
    ) {
      // 管理员编辑他人书评
      success = await reviewStore.updateReviewByAdmin(
        currentReview.value.id,
        formData,
      );
    } else {
      // 用户编辑自己的书评
      success = await reviewStore.updateReview(
        currentReview.value.id,
        formData,
      );
    }

    if (success) {
      ElMessage.success("书评更新成功");
      editDialogVisible.value = false;
      currentReview.value = null;
      await fetchReviews(); // 重新加载书评列表
    }
  } finally {
    formLoading.value = false;
  }
};

// 删除书评
const handleDeleteReview = async (review: Review) => {
  if (!review.id) return;

  let success = false;

  // 判断是管理员操作还是普通用户操作
  if (userStore.isAdmin && review.userId !== userStore.user?.id) {
    // 管理员删除他人书评
    success = await reviewStore.deleteReviewByAdmin(review.id);
  } else {
    // 用户删除自己的书评
    success = await reviewStore.deleteReview(review.id);
  }

  if (success) {
    ElMessage.success("书评已删除");
    await fetchReviews(); // 重新加载书评列表
  }
};

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
};

// 获取评分对应的状态
const getRatingStatus = (
  rating: number,
): "success" | "warning" | "exception" => {
  if (rating >= 7) return "success";
  if (rating >= 4) return "warning";
  return "exception";
};
</script>

<style scoped>
.review-list {
  max-width: 800px;
  margin: 0 auto;
}
</style>
