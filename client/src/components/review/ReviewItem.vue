<template>
  <div class="review-item border-b border-gray-200 py-4 last:border-b-0">
    <div class="review-header flex justify-between items-start mb-3">
      <div class="user-info flex items-center">
        <el-avatar :size="32" :src="review.userAvatar" class="mr-3">
          {{ review.username ? review.username.substring(0, 1).toUpperCase() : '?' }}
        </el-avatar>
        <div>
          <div class="username font-medium">{{ review.username }}</div>
          <div class="text-sm text-gray-500">
            {{ formatDate(review.createdAt) }}
            <span v-if="review.updatedAt && review.updatedAt !== review.createdAt">
              (已编辑于 {{ formatDate(review.updatedAt) }})
            </span>
          </div>
        </div>
      </div>

      <div class="review-actions" v-if="isOwner || isAdmin">
        <el-dropdown trigger="hover">
          <el-button type="text">
            <el-icon class="mr-1"><MoreFilled /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleEdit">编辑</el-dropdown-item>
              <el-dropdown-item @click="handleDelete" class="text-red-500">删除</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="review-rating mb-2">
      <el-rate
        v-model="review.rating"
        disabled
        :max="10"
        show-score
        score-template="{value}"
      />
    </div>

    <div class="review-content mt-3" v-if="review.content">
      <p class="text-gray-800 whitespace-pre-line">{{ review.content }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import type { Review } from "@/types/review";
import { formatDate } from "@/utils/formatters";
import { MoreFilled } from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import { computed } from "vue";

// 属性
const props = defineProps<{
  review: Review;
}>();

// 事件
const emit = defineEmits<{
  (e: "edit", review: Review): void;
  (e: "delete", review: Review): void;
}>();

// store
const userStore = useUserStore();

// 计算属性
// 检查当前用户是否为书评作者
const isOwner = computed(() => {
  return userStore.user?.id === props.review.userId;
});

// 检查当前用户是否为管理员
const isAdmin = computed(() => {
  return userStore.isAdmin;
});

// 处理编辑
const handleEdit = () => {
  emit("edit", props.review);
};

// 处理删除
const handleDelete = async () => {
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

    emit("delete", props.review);
  } catch {
    // 用户取消删除
  }
};
</script>

<style scoped>
.review-item:hover {
  background-color: rgba(0, 0, 0, 0.01);
}

.review-item :deep(.el-rate__item) {
  margin-right: 5px;
}

.review-item :deep(.el-rate__text) {
  margin-left: 8px;
}
</style>
