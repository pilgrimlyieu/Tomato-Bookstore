<template>
  <div class="note-detail">
    <div v-if="loading" class="w-full">
      <el-skeleton :rows="10" animated />
    </div>

    <template v-else-if="note">
      <!-- 返回链接 -->
      <div class="mb-4">
        <router-link
          :to="buildRoute(Routes.PRODUCT_DETAIL, { id: note.productId })"
          class="text-gray-500 hover:text-primary flex items-center m-2"
        >
          <el-icon class="mr-1"><ArrowLeft /></el-icon>
          返回《{{ note.productTitle }}》
        </router-link>
      </div>

      <!-- 笔记内容 -->
      <el-card class="m-4 mb-6" shadow="never">
        <div class="flex justify-between items-start mb-4">
          <!-- 用户信息 -->
          <div class="flex items-center">
              <el-avatar :size="40" :src="note.userAvatar" class="mr-3">
                {{
                  note.username
                    ? note.username.charAt(0).toUpperCase()
                    : "?"
                }}
              </el-avatar>
              <div>
                <div class="font-medium">{{ note.username }}</div>
                <div class="text-sm text-gray-500">
                  {{ formatDate(note.createdAt) }}
                  <template v-if="note.updatedAt !== note.createdAt">
                    （编辑于 {{ formatDate(note.updatedAt) }}）
                  </template>
                </div>
              </div>
          </div>

          <!-- 操作菜单 -->
          <div v-if="canEditNote(note) || canDeleteNote(note)">
            <el-dropdown trigger="click">
              <el-button type="text">
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="canEditNote(note)" @click="$emit('edit', note)">
                    编辑笔记
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="canDeleteNote(note)"
                    @click="$emit('delete', note)"
                    class="text-red-500"
                  >
                    删除笔记
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <!-- 笔记标题 -->
        <h1 class="text-2xl font-bold text-gray-800 mb-4">{{ note.title }}</h1>

        <!-- 笔记内容 -->
        <div class="whitespace-pre-line text-gray-700 mb-6 text-left">
          {{ note.content }}
        </div>

        <!-- 笔记互动区域 -->
        <div class="flex items-center justify-between border-t pt-4">
          <div class="flex items-center space-x-4">
            <!-- 点赞/点踩 -->
            <div class="flex items-center space-x-2">
              <el-button
                :type="note.userFeedback === 'LIKE' ? 'primary' : 'default'"
                @click="$emit('like', note)"
              >
                <el-icon class="mr-1"><ArrowUpBold /></el-icon>
                {{ note.likeCount || 0 }}
              </el-button>

              <el-button
                :type="note.userFeedback === 'DISLIKE' ? 'danger' : 'default'"
                @click="$emit('dislike', note)"
              >
                <el-icon class="mr-1"><ArrowDownBold /></el-icon>
                {{ note.dislikeCount || 0 }}
              </el-button>
            </div>

            <!-- 评论数 -->
            <div class="text-gray-500">
              <el-icon><ChatDotRound /></el-icon>
              {{ note.commentCount || 0 }} 条评论
            </div>
          </div>
        </div>
      </el-card>

      <!-- 评论区 -->
      <NoteCommentList
        :comments="comments"
        :loading="commentLoading"
        @delete="$emit('deleteComment', $event)"
      />

      <!-- 添加评论 -->
      <NoteCommentForm
        ref="commentFormRef"
        @submit="$emit('addComment', $event)"
        :loading="commentLoading"
      />
    </template>

    <el-empty description="笔记不存在" v-else>
      <router-link to="/notes">
        <el-button type="primary" class="rounded-lg">查看所有笔记</el-button>
      </router-link>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { usePermissions } from "@/composables/usePermissions";
import { Routes } from "@/constants/routes";
import type { Note, NoteComment } from "@/types/note";
import { formatDate } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import {
  ArrowDownBold,
  ArrowLeft,
  ArrowUpBold,
  ChatDotRound,
  MoreFilled,
} from "@element-plus/icons-vue";
import NoteCommentForm from "./NoteCommentForm.vue";
import NoteCommentList from "./NoteCommentList.vue";

// Props
const props = defineProps<{
  note: Note | null;
  comments: NoteComment[];
  loading?: boolean;
  commentLoading?: boolean;
}>();

// Emits
const emit = defineEmits<{
  (e: "edit", note: Note): void;
  (e: "delete", note: Note): void;
  (e: "like", note: Note): void;
  (e: "dislike", note: Note): void;
  (e: "addComment", content: string): void;
  (e: "deleteComment", comment: NoteComment): void;
}>();

const { canEditNote, canDeleteNote } = usePermissions();

const commentFormRef = ref();

defineExpose({
  commentFormRef,
});
</script>
