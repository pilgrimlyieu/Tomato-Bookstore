<template>
  <div class="admin-reviews-container">
    <div class="page-header mb-6">
      <h1 class="text-2xl font-bold">书评管理</h1>
      <p class="text-gray-500 mt-1">管理用户发表的所有书评</p>
    </div>

    <el-card class="mb-4">
      <div class="search-filters mb-4 flex flex-wrap gap-4 items-end">
        <el-form :model="filters" label-position="top" inline>
          <el-form-item label="用户 ID">
            <el-input
              v-model.number="tempUserId"
              type="number"
              placeholder="输入用户 ID"
              clearable
              @update:model-value="debouncedFilter"
              @clear="filters.userId = null; debouncedFilter();"
            />
          </el-form-item>

          <el-form-item label="商品 ID">
            <el-input
              v-model.number="tempProductId"
              type="number"
              placeholder="输入商品 ID"
              clearable
              @update:model-value="debouncedFilter"
              @clear="filters.productId = null; debouncedFilter()"
            />
          </el-form-item>

          <el-form-item class="flex-shrink-0">
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="reviews-table overflow-x-auto">
        <el-table :data="paginatedReviews" style="width: 100%" v-loading="loading" border>
          <el-table-column prop="id" label="ID" width="80" />

          <el-table-column label="用户" width="180">
            <template #default="{ row }">
              <div class="user-info items-center gap-2">
                <el-avatar :size="32" :src="row.userAvatar" class="cursor-pointer" @click="showUserDetail(row.userId)">
                  {{ row.username?.substring(0, 1).toUpperCase() }}
                </el-avatar>
                <span class="cursor-pointer hover:text-primary" @click="showUserDetail(row.userId)">{{ row.username }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="商品" width="100">
            <template #default="{ row }">
              <router-link
                :to="buildRoute(Routes.PRODUCT_DETAIL, { id: row.productId })"
                class="text-primary hover:underline"
              >
                ID: {{ row.productId }}
              </router-link>
            </template>
          </el-table-column>

          <el-table-column label="评分" width="150">
            <template #default="{ row }">
              <el-rate
                v-model="row.rating"
                :max="10"
                disabled
                show-score
                score-template="{value}"
              />
            </template>
          </el-table-column>

          <el-table-column prop="content" label="评论内容" min-width="250">
            <template #default="{ row }">
              <div v-if="row.content" class="content-preview">
                {{ row.content }}
              </div>
              <div v-else class="text-gray-400 italic">无评论内容</div>
            </template>
          </el-table-column>

          <el-table-column label="发表时间" width="160" show-overflow-tooltip>
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button-group>
                <el-button
                  type="primary"
                  link
                  @click="handleEdit(row)"
                >
                  编辑
                </el-button>
                <el-button
                  type="danger"
                  link
                  @click="handleDelete(row)"
                >
                  删除
                </el-button>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>

        <el-empty
          v-if="filteredReviews.length === 0 && !loading"
          description="未找到匹配的书评"
        />

        <div class="pagination-container flex justify-end mt-4">
          <el-pagination
            v-model:currentPage="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="filteredReviews.length"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="showEditDialog"
      title="编辑书评"
      width="500px"
      destroy-on-close
    >
      <ReviewForm
        v-if="showEditDialog && currentReview"
        :initial-data="currentReview"
        :loading="formLoading"
        :is-edit="true"
        @submit="handleUpdateReview"
        @cancel="showEditDialog = false"
      />
    </el-dialog>

    <el-dialog
      v-model="showUserDetailDialog"
      title="用户书评"
      width="800px"
      destroy-on-close
    >
      <div v-if="showUserDetailDialog && selectedUserId" v-loading="userReviewsLoading">
        <div class="user-reviews-header mb-4">
          <h3 class="text-lg font-medium">用户 ID：{{ selectedUserId }}</h3>
        </div>

        <div class="overflow-x-auto">
          <el-table :data="managedUserReviews" style="width: 100%" border>
            <el-table-column prop="id" label="ID" width="80" />

            <el-table-column label="商品" width="100">
              <template #default="{ row }">
                <router-link
                  :to="buildRoute(Routes.PRODUCT_DETAIL, { id: row.productId })"
                  class="text-primary hover:underline"
                >
                  ID: {{ row.productId }}
                </router-link>
              </template>
            </el-table-column>

            <el-table-column label="评分" width="150">
              <template #default="{ row }">
                <el-rate
                  v-model="row.rating"
                  :max="10"
                  disabled
                  show-score
                  score-template="{value}"
                />
              </template>
            </el-table-column>

            <el-table-column prop="content" label="评论内容" min-width="250">
              <template #default="{ row }">
                <div v-if="row.content" class="content-preview">
                  {{ row.content }}
                </div>
                <div v-else class="text-gray-400 italic">无评论内容</div>
              </template>
            </el-table-column>

            <el-table-column label="发表时间" width="160" show-overflow-tooltip>
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>

            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button-group>
                  <el-button
                    type="primary"
                    link
                    @click="handleEdit(row)"
                  >
                    编辑
                  </el-button>
                  <el-button
                    type="danger"
                    link
                    @click="handleDelete(row)"
                  >
                    删除
                  </el-button>
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-empty
          v-if="managedUserReviews.length === 0 && !userReviewsLoading"
          description="该用户没有发表任何书评"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import ReviewForm from "@/components/review/ReviewForm.vue";
import { Routes } from "@/constants/routes";
import { useReviewStore } from "@/stores/review";
import type { Review, ReviewUpdateParams } from "@/types/review";
import { formatDate } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import { ElMessageBox } from "element-plus";
import { debounce } from "lodash-es";

// store
const reviewStore = useReviewStore();

// 状态
const filters = reactive({
  userId: null as number | null,
  productId: null as number | null,
});
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
});
const showEditDialog = ref(false);
const showUserDetailDialog = ref(false);
const selectedUserId = ref<number | null>(null);
const currentReview = ref<Review | null>(null);
const formLoading = ref(false);
const userReviewsLoading = ref(false);

const tempUserId = ref<number | null>(null);
const tempProductId = ref<number | null>(null);

// 创建真正的去抖动处理函数
const debouncedFilter = debounce(() => {
  filters.userId = tempUserId.value;
  filters.productId = tempProductId.value;
}, 300);

// 计算属性
const loading = computed(() => reviewStore.loading);
const managedUserReviews = computed(() => reviewStore.managedUserReviews);
const allReviews = computed(() => reviewStore.allReviews);

// 根据过滤条件筛选书评
const filteredReviews = computed(() => {
  return allReviews.value.filter(
    (review) =>
      (filters.userId === null || review.userId === filters.userId) &&
      (filters.productId === null || review.productId === filters.productId),
  );
});

// 当前分页的书评列表
const paginatedReviews = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  return filteredReviews.value.slice(start, end);
});

// 初始化：获取所有书评
onMounted(async () => {
  await reviewStore.fetchAllReviews();
});

// 搜索
const handleSearch = async () => {
  // 由于使用了计算属性自动过滤，这里只需重置页码即可
  pagination.currentPage = 1;
};

// 重置筛选
const handleReset = () => {
  filters.userId = null;
  filters.productId = null;
  pagination.currentPage = 1;
};

const showUserDetail = async (userId: number) => {
  selectedUserId.value = userId;
  showUserDetailDialog.value = true;
  userReviewsLoading.value = true;
  try {
    await reviewStore.fetchUserReviewsByAdmin(userId);
  } finally {
    userReviewsLoading.value = false;
  }
};

// 处理编辑
const handleEdit = (review: Review) => {
  currentReview.value = review;
  showEditDialog.value = true;
};

// 更新书评
const handleUpdateReview = async (formData: ReviewUpdateParams) => {
  if (!currentReview.value?.id) return;

  formLoading.value = true;

  try {
    // 管理员方式更新书评
    const success = await reviewStore.updateReviewByAdmin(
      currentReview.value.id,
      formData,
    );

    if (success) {
      // 更新本地数据
      const index = allReviews.value.findIndex(
        (r) => r.id === currentReview.value?.id,
      );
      if (index !== -1) {
        allReviews.value[index] = {
          ...allReviews.value[index],
          ...formData,
          updatedAt: new Date().toISOString(),
        };
      }

      if (
        selectedUserId.value &&
        selectedUserId.value === currentReview.value.userId
      ) {
        // 更新用户详情弹窗中的数据
        const userReviewIndex = managedUserReviews.value.findIndex(
          (r) => r.id === currentReview.value?.id,
        );
        if (userReviewIndex !== -1) {
          reviewStore.managedUserReviews[userReviewIndex] = {
            ...reviewStore.managedUserReviews[userReviewIndex],
            ...formData,
            updatedAt: new Date().toISOString(),
          };
        }
      }

      showEditDialog.value = false;
      currentReview.value = null;
    }
  } finally {
    formLoading.value = false;
  }
};

// 删除书评
const handleDelete = async (review: Review) => {
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

    await reviewStore.deleteReviewByAdmin(review.id);
  } catch {
    // 用户取消删除
  }
};

// 分页处理
const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
};

const handleCurrentChange = (val: number) => {
  pagination.currentPage = val;
};
</script>

<style scoped>
.admin-reviews-container {
  padding: 1.5rem;
  max-width: 100%;
}

.content-preview {
  max-height: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  line-clamp: 3;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
</style>
