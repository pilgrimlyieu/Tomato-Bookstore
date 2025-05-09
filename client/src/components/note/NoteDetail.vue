<template>
  <div class="note-detail">
    <div v-if="loading" class="w-full">
      <el-skeleton :rows="10" animated />
    </div>

    <template v-else-if="note">
      <!-- 返回链接 -->
      <div class="mb-4">
        <router-link
          :to="`/products/${note.productId}`"
          class="text-gray-500 hover:text-primary flex items-center"
        >
          <el-icon class="mr-1"><ArrowLeft /></el-icon>
          返回《{{ note.productTitle }}》
        </router-link>
      </div>

      <!-- 笔记内容 -->
      <el-card class="mb-6" shadow="never">
        <div class="flex justify-between items-start mb-4">
          <!-- 用户信息 -->
          <div class="flex items-center">
            <router-link :to="`/user/${note.userId}`" class="flex items-center">
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
                    (编辑于 {{ formatDate(note.updatedAt) }})
                  </template>
                </div>
              </div>
            </router-link>
          </div>

          <!-- 操作菜单 -->
          <div v-if="canEdit || isAdmin">
            <el-dropdown trigger="click">
              <el-button type="text">
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="canEdit" @click="$emit('edit', note)">
                    编辑笔记
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="canEdit || isAdmin"
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
        <div class="whitespace-pre-line text-gray-700 mb-6">
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
                <el-icon class="mr-1"><Pointer /></el-icon>
                {{ note.likeCount || 0 }}
              </el-button>

              <el-button
                :type="note.userFeedback === 'DISLIKE' ? 'danger' : 'default'"
                @click="$emit('dislike', note)"
              >
                <el-icon class="mr-1 transform rotate-180"><Pointer /></el-icon>
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
      <NoteCommentForm @submit="$emit('addComment', $event)" />
    </template>

    <el-empty description="笔记不存在" v-else>
      <router-link to="/notes">
        <el-button type="primary" class="rounded-lg">查看所有笔记</el-button>
      </router-link>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import type { Note, NoteComment } from "@/types/note";
import { formatDate } from "@/utils/formatters";
import { ArrowLeft, ChatDotRound, MoreFilled } from "@element-plus/icons-vue";
import { computed } from "vue";
import NoteCommentForm from "./NoteCommentForm.vue";
import NoteCommentList from "./NoteCommentList.vue";

// 自定义图标
const Pointer = markRaw(
  h(
    "svg",
    {
      viewBox: "0 0 24 24",
      width: "1em",
      height: "1em",
      fill: "currentColor",
    },
    [
      h("path", {
        d: "M7.96 12.73c-.19 0-.38-.07-.53-.22l-2.53-2.53c-.29-.29-.29-.77 0-1.06s.77-.29 1.06 0l2 2 5.35-5.37c.29-.29.77-.29 1.06 0s.29.77 0 1.06l-5.88 5.9c-.15.15-.34.22-.53.22zm9.07 4.82c-.12 0-.24-.02-.35-.06-.52-.22-.84-.81-.72-1.45l.3-1.57c.03-.15-.03-.31-.15-.43l-1.61-1.6c-.14-.14-.31-.19-.48-.14l-1.49.31c-.65.13-1.23-.2-1.45-.72-.21-.52-.06-1.22.46-1.65l5.87-4.83c.4-.33.9-.48 1.4-.43.54.06 1.03.36 1.34.84l.93 1.47c.17.27.25.58.25.89 0 .29-.08.57-.22.81l-3.35 7.16c-.25.56-.77.84-1.26.84-.08 0-.17-.01-.25-.02-.52-.05-.9-.14-1.09-.41z",
      }),
    ],
  ),
);

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

// 获取用户 store
const userStore = useUserStore();

// 当前用户是否可以编辑该笔记
const canEdit = computed(() => {
  if (!props.note || !userStore.user) return false;
  return userStore.user.id === props.note.userId;
});

// 当前用户是否为管理员
const isAdmin = computed(() => userStore.isAdmin);
</script>

<style scoped>
.transform.rotate-180 {
  transform: rotate(180deg);
}
</style>
