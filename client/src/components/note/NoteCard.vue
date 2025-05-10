<template>
  <el-card class="note-card relative transition-all duration-300 hover:shadow-md" shadow="hover">
    <div class="flex justify-between items-start">
      <!-- 用户信息 -->
      <div class="flex items-center mb-3">
        <el-avatar :size="32" :src="note.userAvatar" class="mr-3">
          {{ note.username ? note.username.charAt(0).toUpperCase() : "?" }}
        </el-avatar>
        <div>
          <div class="text-sm font-medium">{{ note.username }}</div>
          <div class="text-xs text-gray-500">{{ formatDate(note.createdAt) }}</div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div v-if="canEditNote(note) || canDeleteNote(note)">
        <el-dropdown trigger="hover">
          <el-button type="text">
            <el-icon><MoreFilled /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="canEditNote(note)" @click="$emit('edit', note)">
                编辑
              </el-dropdown-item>
              <el-dropdown-item v-if="canDeleteNote(note)" @click="$emit('delete', note)" class="text-red-500">
                删除
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 笔记标题 -->
    <h3 class="text-lg font-semibold mb-2 line-clamp-2">{{ note.title }}</h3>

    <!-- 笔记内容 -->
    <div class="text-gray-600 mb-4 line-clamp-3 whitespace-pre-line">
      {{ note.content }}
    </div>

    <!-- 笔记相关的商品 -->
    <div class="text-sm text-gray-500 mb-3">
      <span>关联书籍：</span>
      <router-link
        :to="{ name: Routes.PRODUCT_DETAIL, params: { id: note.productId } }"
        class="text-primary hover:underline"
      >
        《{{ note.productTitle }}》
      </router-link>
    </div>

    <!-- 底部操作栏 -->
    <div class="flex items-center justify-between mt-3">
      <div class="flex items-center space-x-4">
        <!-- 点赞/点踩 -->
        <div class="flex items-center space-x-1">
          <el-button
            :type="note.userFeedback === 'LIKE' ? 'primary' : 'default'"
            :icon="ArrowUpBold"
            size="small"
            @click="$emit('like', note)"
            text
          >
            {{ note.likeCount }}
          </el-button>
          <el-button
            :type="note.userFeedback === 'DISLIKE' ? 'danger' : 'default'"
            :icon="ArrowDownBold"
            size="small"
            @click="$emit('dislike', note)"
            text
          >
            {{ note.dislikeCount }}
          </el-button>
        </div>

        <!-- 评论 -->
        <div class="flex items-center">
          <el-button
            type="default"
            :icon="ChatDotRound"
            size="small"
            @click="$emit('view', note)"
            text
          >
            {{ note.commentCount }}
          </el-button>
        </div>
      </div>

      <!-- 查看详情 -->
      <el-button
        type="primary"
        size="small"
        class="rounded-lg"
        @click="$emit('view', note)"
        text
      >
        查看详情
        <el-icon class="ml-1"><ArrowRight /></el-icon>
      </el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { usePermissions } from "@/composables/usePermissions";
import { Routes } from "@/constants/routes";
import type { Note } from "@/types/note";
import { formatDate } from "@/utils/formatters";
import {
  ArrowDownBold,
  ArrowRight,
  ArrowUpBold,
  ChatDotRound,
  MoreFilled,
} from "@element-plus/icons-vue";

// Props
const props = defineProps<{
  note: Note;
}>();

// Emits
const emit = defineEmits<{
  (e: "view", note: Note): void;
  (e: "edit", note: Note): void;
  (e: "delete", note: Note): void;
  (e: "like", note: Note): void;
  (e: "dislike", note: Note): void;
}>();

// 获取权限相关功能
const { canEditNote, canDeleteNote, isAdmin } = usePermissions();
</script>

<style scoped>
.note-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.note-card .el-card__body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
