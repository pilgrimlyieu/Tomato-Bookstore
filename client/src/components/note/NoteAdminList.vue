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
      :data="filteredNotes"
      style="width: 100%"
      v-loading="!!loading"
      border
      stripe
    >
      <el-table-column label="ID" prop="id" width="80" sortable />

      <el-table-column label="笔记标题" min-width="180" prop="title">
        <template #default="{ row }">
          <el-tooltip
            :content="row.title"
            :disabled="row.title.length < 20"
            placement="top"
          >
            <router-link
              :to="buildRoute(Routes.NOTE_DETAIL, { noteId: row.id })"
              class="text-primary hover:underline"
            >
              {{ truncateText(row.title, 20) }}
            </router-link>
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column label="内容预览" min-width="200">
        <template #default="{ row }">
          <el-tooltip
            :content="row.content"
            placement="top"
            :show-after="500"
            :hide-after="0"
          >
            <span class="text-gray-600">{{ truncateText(row.content, 30) }}</span>
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column label="用户" min-width="120">
        <template #default="{ row }">
          <div class="flex items-center">
            <el-avatar :size="24" :src="row.userAvatar" class="mr-2">
              {{ row.username ? row.username.charAt(0).toUpperCase() : "?" }}
            </el-avatar>
            <span>{{ row.username }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="关联书籍" min-width="140">
        <template #default="{ row }">
          <router-link
            :to="buildRoute(Routes.PRODUCT_DETAIL, { id: row.productId })"
            class="text-primary hover:underline"
          >
            {{ truncateText(row.productTitle, 15) }}
          </router-link>
        </template>
      </el-table-column>

      <el-table-column label="互动数据" width="150">
        <template #default="{ row }">
          <div class="flex items-center gap-3">
            <el-tooltip content="点赞">
              <div class="flex items-center">
                <el-icon><Pointer /></el-icon>
                <span class="ml-1">{{ row.likeCount }}</span>
              </div>
            </el-tooltip>
            <el-tooltip content="点踩">
              <div class="flex items-center">
                <el-icon class="transform rotate-180"><Pointer /></el-icon>
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

      <el-table-column label="创建时间" width="180" sortable>
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

    <div class="flex justify-end mt-4">
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
import { ChatDotRound } from "@element-plus/icons-vue";
import { computed, ref } from "vue";

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

.transform.rotate-180 {
  transform: rotate(180deg);
}
</style>
