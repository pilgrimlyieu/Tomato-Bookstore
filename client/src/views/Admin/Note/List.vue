<template>
  <div class="admin-notes-container">
    <div class="page-header mb-6">
      <h1 class="text-2xl font-bold">读书笔记管理</h1>
      <p class="text-gray-500 mt-1">管理用户发表的所有读书笔记</p>
    </div>

    <el-card class="mb-4">
      <div class="search-filters mb-4 flex flex-wrap gap-4 items-end">
        <el-form :model="filters" label-position="top" inline>
          <el-form-item label="用户 ID">
            <el-input
              v-model.number="tempUserId"
              type="number"
              placeholder="输入用户 ID"
              clearable
              @update:model-value="debouncedFilter"
              @clear="filters.userId = null; debouncedFilter();"
            />
          </el-form-item>

          <el-form-item label="商品 ID">
            <el-input
              v-model.number="tempProductId"
              type="number"
              placeholder="输入商品 ID"
              clearable
              @update:model-value="debouncedFilter"
              @clear="filters.productId = null; debouncedFilter()"
            />
          </el-form-item>

          <el-form-item label="笔记标题">
            <el-input
              v-model="filters.title"
              placeholder="搜索笔记标题"
              clearable
              @update:model-value="debouncedFilter"
            />
          </el-form-item>

          <el-form-item class="flex-shrink-0">
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="notes-table w-full overflow-x-auto">
        <NoteAdminList
          :notes="filteredNotes"
          :loading="loading"
          @edit="handleEdit"
          @delete="handleDelete"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="showEditDialog"
      title="编辑读书笔记"
      width="600px"
      destroy-on-close
    >
      <NoteForm
        v-if="showEditDialog && currentNote"
        :initial-data="currentNote"
        :is-edit="true"
        :loading="formLoading"
        :product-title="currentNote.productTitle"
        @submit="handleUpdateNote"
        @cancel="showEditDialog = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import NoteAdminList from "@/components/note/NoteAdminList.vue";
import NoteForm from "@/components/note/NoteForm.vue";
import { useNoteStore } from "@/stores/note";
import type { Note, NoteUpdateParams } from "@/types/note";
import { ElMessageBox } from "element-plus";
import { debounce } from "lodash-es";
import { useRouter } from "vue-router";

// store
const noteStore = useNoteStore();
const router = useRouter();

// 状态
const filters = reactive({
  userId: null as number | null,
  productId: null as number | null,
  title: "" as string,
});
const tempUserId = ref<number | null>(null);
const tempProductId = ref<number | null>(null);
const showEditDialog = ref(false);
const currentNote = ref<Note | null>(null);
const formLoading = ref(false);

// 创建去抖动处理函数
const debouncedFilter = debounce(() => {
  filters.userId = tempUserId.value;
  filters.productId = tempProductId.value;
}, 300);

// 计算属性
const loading = computed(() => noteStore.loading);
const allNotes = computed(() => noteStore.allNotes);

// 根据过滤条件筛选笔记
const filteredNotes = computed(() => {
  return allNotes.value.filter((note) => {
    const matchUserId =
      filters.userId === null || note.userId === filters.userId;
    const matchProductId =
      filters.productId === null || note.productId === filters.productId;
    const matchTitle =
      !filters.title ||
      note.title.toLowerCase().includes(filters.title.toLowerCase());

    return matchUserId && matchProductId && matchTitle;
  });
});

// 初始化：获取所有笔记
onMounted(async () => {
  await noteStore.fetchAllNotes();
});

// 搜索
const handleSearch = async () => {
  // 由于使用了计算属性自动过滤，这里不需要额外操作
};

// 重置筛选
const handleReset = () => {
  filters.userId = null;
  filters.productId = null;
  filters.title = "";
  tempUserId.value = null;
  tempProductId.value = null;
};

// 处理编辑
const handleEdit = (note: Note) => {
  currentNote.value = note;
  showEditDialog.value = true;
};

// 更新笔记
const handleUpdateNote = async (formData: NoteUpdateParams) => {
  if (!currentNote.value?.id) return;

  formLoading.value = true;

  try {
    // 管理员方式更新笔记
    const success = await noteStore.updateNoteByAdmin(
      currentNote.value.id,
      formData,
    );

    if (success) {
      showEditDialog.value = false;
      currentNote.value = null;
    }
  } finally {
    formLoading.value = false;
  }
};

// 删除笔记
const handleDelete = async (note: Note) => {
  if (!note.id) return;

  try {
    await ElMessageBox.confirm(
      "确定要删除这篇读书笔记吗？此操作不可撤销。",
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      },
    );

    await noteStore.deleteNoteByAdmin(note.id);
  } catch {
    // 用户取消删除
  }
};

// 监听页面改变重置搜索
watch(
  () => router.currentRoute.value.fullPath,
  () => {
    handleReset();
  },
);
</script>

<style scoped>
.admin-notes-container {
  padding: 1.5rem;
  max-width: 100%;
  overflow-x: hidden;
}

.notes-table {
  min-width: 0;  /* 确保 flex 子元素可以适当收缩 */
}
</style>
