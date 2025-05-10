<template>
  <div class="note-user-list">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-semibold">我的读书笔记</h2>
      <router-link :to="{ path: Routes.PRODUCT_LIST }">
        <el-button type="primary" class="rounded-lg">
          <el-icon class="mr-1"><Plus /></el-icon>
          写新笔记
        </el-button>
      </router-link>
    </div>

    <div v-if="loading" class="w-full">
      <el-skeleton :rows="5" animated />
    </div>

    <template v-else>
      <div v-if="notes.length > 0">
        <el-table :data="notes" style="width: 100%" v-loading="!!loading">
          <el-table-column label="笔记标题" min-width="200" prop="title">
            <template #default="{ row }">
              <router-link
                :to="buildRoute(Routes.NOTE_DETAIL, { noteId: row.id })"
                class="text-primary hover:underline"
              >
                {{ row.title }}
              </router-link>
            </template>
          </el-table-column>

          <el-table-column label="关联书籍" min-width="180">
            <template #default="{ row }">
              <router-link
                :to="buildRoute(Routes.PRODUCT_DETAIL, { id: row.productId })"
                class="text-primary hover:underline"
              >
                {{ row.productTitle }}
              </router-link>
            </template>
          </el-table-column>

          <el-table-column label="互动" width="150">
            <template #default="{ row }">
              <div class="flex items-center space-x-2">
                <el-tooltip content="点赞">
                  <div class="flex items-center text-gray-600">
                    <el-icon><ArrowUpBold /></el-icon>
                    <span class="ml-1">{{ row.likeCount }}</span>
                  </div>
                </el-tooltip>

                <el-tooltip content="点踩">
                  <div class="flex items-center text-gray-600">
                    <el-icon><ArrowDownBold /></el-icon>
                    <span class="ml-1">{{ row.dislikeCount }}</span>
                  </div>
                </el-tooltip>

                <el-tooltip content="评论">
                  <div class="flex items-center text-gray-600">
                    <el-icon><ChatDotRound /></el-icon>
                    <span class="ml-1">{{ row.commentCount }}</span>
                  </div>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <div class="flex space-x-1">
                <el-button
                  type="primary"
                  link
                  @click="$emit('edit', row)"
                >
                  编辑
                </el-button>
                <el-button
                  type="danger"
                  link
                  @click="$emit('delete', row)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <el-empty v-else description="您还没有创建任何读书笔记">
        <router-link :to="{ path: Routes.PRODUCT_LIST }">
          <el-button type="primary" class="rounded-lg">浏览商品</el-button>
        </router-link>
      </el-empty>
    </template>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import type { Note } from "@/types/note";
import { formatDate } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import {
  ArrowDownBold,
  ArrowUpBold,
  ChatDotRound,
  Plus,
} from "@element-plus/icons-vue";

// Props
const props = defineProps<{
  notes: Note[];
  loading?: boolean;
}>();

// Emits
const emit = defineEmits<{
  (e: "edit", note: Note): void;
  (e: "delete", note: Note): void;
}>();
</script>

<style scoped>
.note-user-list {
  width: 100%;
}
</style>
