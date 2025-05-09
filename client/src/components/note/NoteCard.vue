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
      <div v-if="canEdit || isAdmin">
        <el-dropdown trigger="hover">
          <el-button type="text">
            <el-icon><MoreFilled /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="canEdit" @click="$emit('edit', note)">
                编辑
              </el-dropdown-item>
              <el-dropdown-item v-if="canEdit || isAdmin" @click="$emit('delete', note)" class="text-red-500">
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
      <span>关联书籍: </span>
      <router-link
        :to="`/products/${note.productId}`"
        class="text-primary hover:underline"
      >
        {{ note.productTitle }}
      </router-link>
    </div>

    <!-- 底部操作栏 -->
    <div class="flex items-center justify-between mt-3">
      <div class="flex items-center space-x-4">
        <!-- 点赞/点踩 -->
        <div class="flex items-center space-x-1">
          <el-button
            :type="note.userFeedback === 'LIKE' ? 'primary' : 'default'"
            :icon="Pointer"
            size="small"
            @click="$emit('like', note)"
            text
          >
            {{ note.likeCount }}
          </el-button>
          <el-button
            :type="note.userFeedback === 'DISLIKE' ? 'danger' : 'default'"
            :icon="PointerDown"
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
import { useUserStore } from "@/stores/user";
import type { Note } from "@/types/note";
import { formatDate } from "@/utils/formatters";
import { ArrowRight, ChatDotRound, MoreFilled } from "@element-plus/icons-vue";
import { computed } from "vue";

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

const PointerDown = markRaw(
  h(
    "svg",
    {
      viewBox: "0 0 24 24",
      width: "1em",
      height: "1em",
      fill: "currentColor",
      style: "transform: rotate(180deg)",
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

// 获取用户 store
const userStore = useUserStore();

// 当前用户是否可以编辑该笔记
const canEdit = computed(() => {
  return userStore.user && userStore.user.id === props.note.userId;
});

// 当前用户是否为管理员
const isAdmin = computed(() => userStore.isAdmin);
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
