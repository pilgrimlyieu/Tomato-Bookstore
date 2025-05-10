<template>
  <div class="note-admin-list">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-semibold">读书笔记管理</h2>
      <div class="flex items-center gap-2">
        <el-input
          v-model="searchQuery"
          placeholder="搜索笔记标题或用户名"
          prefix-icon="Search"
          clearable
          @clear="resetSearch"
        ></el-input>
        <el-button type="primary" @click="handleSearch" class="rounded-lg">搜索</el-button>
      </div>
    </div>

    <el-table
      :data="paginatedNotes"
      style="width: 100%"
      v-loading="!!loading"
      border
      stripe
    >
      <el-table-column label="ID" prop="id" width="60" />

      <el-table-column label="笔记标题" width="160" prop="title">
        <template #default="{ row }">
          <el-tooltip
            :content="row.title"
            :disabled="row.title.length < 15"
            placement="top"
          >
            <router-link
              :to="buildRoute(Routes.NOTE_DETAIL, { noteId: row.id })"
              class="text-primary hover:underline"
            >
              {{ truncateText(row.title, 15) }}
            </router-link>
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column label="内容预览" width="180">
        <template #default="{ row }">
          <el-tooltip
            :content="row.content"
            placement="top"
            :show-after="500"
            :hide-after="0"
          >
            <span class="text-gray-600">{{ truncateText(row.content, 20) }}</span>
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column label="用户" width="100">
        <template #default="{ row }">
          <div class="items-center">
            <el-avatar :size="24" :src="row.userAvatar" class="mr-1">
              {{ row.username ? row.username.charAt(0).toUpperCase() : "?" }}
            </el-avatar>
            <span class="truncate" style="max-width: 60px;">{{ row.username }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="关联书籍" width="120">
        <template #default="{ row }">
          <router-link
            :to="buildRoute(Routes.PRODUCT_DETAIL, { id: row.productId })"
            class="text-primary hover:underline"
          >
            {{ truncateText(row.productTitle, 10) }}
          </router-link>
        </template>
      </el-table-column>

      <el-table-column label="互动数据" width="120">
        <template #default="{ row }">
          <div class="flex items-center gap-2">
            <el-tooltip content="点赞">
              <div class="flex items-center">
                <el-icon><ArrowUpBold /></el-icon>
                <span class="ml-1">{{ row.likeCount }}</span>
              </div>
            </el-tooltip>
            <el-tooltip content="点踩">
              <div class="flex items-center">
                <el-icon><ArrowDownBold /></el-icon>
                <span class="ml-1">{{ row.dislikeCount }}</span>
              </div>
            </el-tooltip>
            <el-tooltip content="评论">
              <div class="flex items-center">
                <el-icon><ChatDotRound /></el-icon>
                <span class="ml-1">{{ row.commentCount }}</span>
              </div>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="160">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="100" fixed="right">
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

    <div class="pagination-container flex justify-end mt-4">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        :total="filteredNotes.length"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
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
} from "@element-plus/icons-vue";
import { computed, ref } from "vue";

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

// 分页状态
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索状态
const searchQuery = ref("");

// 过滤后的笔记列表
const filteredNotes = computed(() => {
  if (!searchQuery.value) {
    return props.notes;
  }

  const query = searchQuery.value.toLowerCase();
  return props.notes.filter(
    (note) =>
      note.title.toLowerCase().includes(query) ||
      note.username.toLowerCase().includes(query) ||
      note.productTitle.toLowerCase().includes(query),
  );
});

// 当前页面显示的笔记
const paginatedNotes = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredNotes.value.slice(start, end);
});

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1; // 搜索时重置到第一页
};

// 重置搜索
const resetSearch = () => {
  searchQuery.value = "";
  currentPage.value = 1;
};

// 处理分页大小变化
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize;
  currentPage.value = 1;
};

// 处理当前页变化
const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage;
};

// 截断文本，添加省略号
const truncateText = (text: string, length: number): string => {
  if (!text) return "";
  if (text.length <= length) return text;
  return text.substring(0, length) + "...";
};
</script>

<style scoped>
.note-admin-list {
  width: 100%;
}

.pagination-container {
  white-space: nowrap;
}

.truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.detection-table-container {
  /* 确保表格容器和表格本身宽度一致 */
  width: 100%;
}

.detection-table {
  /* 强制表格头部和内容使用相同的显示模式 */
  width: 100% !important;
}

/* 调整表头样式 */
:deep(.el-table__header-wrapper) {
  width: 100%;
}

/* 调整表身样式 */
:deep(.el-table__body-wrapper) {
  width: 100%;
}

/* 确保表头和表身对齐 */
:deep(.el-table__header),
:deep(.el-table__body) {
  width: 100% !important;
  table-layout: fixed;
}
</style>
