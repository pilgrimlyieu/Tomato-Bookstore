<template>
  <div class="note-comment-list mb-6">
    <h3 class="text-lg font-medium mb-4">
      评论 <span v-if="comments.length > 0" class="text-primary">({{ comments.length }})</span>
    </h3>

    <div v-if="loading" class="w-full">
      <el-skeleton :rows="3" animated />
    </div>

    <template v-else>
      <div v-if="comments.length > 0" class="space-y-4">
        <div
          v-for="comment in comments"
          :key="comment.id"
          class="bg-gray-50 p-4 rounded-lg"
        >
          <div class="flex justify-between items-start mb-2">
            <div class="flex items-center">
              <el-avatar :size="32" :src="comment.userAvatar" class="mr-3">
                {{ comment.username ? comment.username.charAt(0).toUpperCase() : "?" }}
              </el-avatar>
              <div>
                <div class="font-medium">{{ comment.username }}</div>
                <div class="text-xs text-gray-500">{{ formatDate(comment.createdAt) }}</div>
              </div>
            </div>

            <el-dropdown v-if="canDeleteComment(comment)" trigger="click">
              <el-button type="text">
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    @click="$emit('delete', comment)"
                    class="text-red-500"
                  >
                    删除评论
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <div class="whitespace-pre-line text-gray-700 pl-11">{{ comment.content }}</div>
        </div>
      </div>

      <el-empty
        v-else
        description="暂无评论，来抢沙发吧"
        :image-size="100"
      ></el-empty>
    </template>
  </div>
</template>

<script setup lang="ts">
import { usePermissions } from "@/composables/usePermissions";
import type { NoteComment } from "@/types/note";
import { formatDate } from "@/utils/formatters";
import { MoreFilled } from "@element-plus/icons-vue";

// Props
const props = defineProps<{
  comments: NoteComment[];
  loading?: boolean;
}>();

// 获取权限相关函数
const { canDeleteComment } = usePermissions();
</script>

<style scoped>
.note-comment-list {
  width: 100%;
}
</style>
