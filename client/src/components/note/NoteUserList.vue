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
                    <el-icon><Pointer /></el-icon>
                    <span class="ml-1">{{ row.likeCount }}</span>
                  </div>
                </el-tooltip>

                <el-tooltip content="点踩">
                  <div class="flex items-center text-gray-600">
                    <el-icon class="transform rotate-180"><Pointer /></el-icon>
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
import { ChatDotRound, Plus } from "@element-plus/icons-vue";

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

.transform.rotate-180 {
  transform: rotate(180deg);
}
</style>
