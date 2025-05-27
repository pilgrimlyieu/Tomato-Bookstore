<template>
  <div class="user-notes-container p-6">
    <el-card class="w-full">
      <template #header>
        <div class="card-header">
          <h1 class="text-xl font-bold">我的读书笔记</h1>
          <p class="text-gray-500 mt-2">管理您创建的所有读书笔记</p>
        </div>
      </template>

      <div v-loading="loading">
        <template v-if="notes.length > 0">
          <div class="mb-4 notes-stats flex items-center gap-4">
            <div class="text-gray-600">
              共发表了 <span class="text-primary font-bold">{{ notes.length }}</span> 篇读书笔记
            </div>
          </div>

          <NoteUserList
            :notes="userNotesWithProductNames"
            :loading="loading"
            @edit="handleEdit"
            @delete="handleDelete"
          />
        </template>

        <el-empty v-else description="您还没有创建任何读书笔记">
          <router-link :to="Routes.PRODUCT_LIST">
            <el-button type="primary" class="rounded-lg">浏览图书</el-button>
          </router-link>
        </el-empty>
      </div>
    </el-card>

    <!-- 编辑笔记对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentNote ? '编辑读书笔记' : '创建读书笔记'"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <NoteForm
        v-if="dialogVisible"
        :initial-data="currentNote!"
        :is-edit="!!currentNote"
        :loading="formLoading"
        :product-title="currentNote?.productTitle"
        @submit="handleSubmit"
        @cancel="dialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import NoteForm from "@/components/note/NoteForm.vue";
import NoteUserList from "@/components/note/NoteUserList.vue";
import { useNote } from "@/composables/useNote";
import { Routes } from "@/constants/routes";
import { useProductStore } from "@/stores/product";
import type { Note, NoteCreateParams } from "@/types/note";
import { ElMessage } from "element-plus";

const productStore = useProductStore();
const {
  loading,
  userNotes: notes,
  fetchUserNotes,
  updateNote,
  deleteNote,
  userNotesWithProductNames,
} = useNote();

const dialogVisible = ref(false);
const currentNote = ref<Note | null>(null);
const formLoading = ref(false);

// 加载用户读书笔记数据
onMounted(async () => {
  await productStore.fetchAllProducts();
  await fetchUserNotes();
});

// 编辑笔记
const handleEdit = (note: Note) => {
  currentNote.value = note;
  dialogVisible.value = true;
};

// 删除笔记
const handleDelete = async (note: Note) => {
  await deleteNote(note.id, note.productId);
};

// 提交表单
const handleSubmit = async (formData: NoteCreateParams) => {
  if (!currentNote.value) return;

  formLoading.value = true;
  try {
    await updateNote(currentNote.value.id, formData);
    dialogVisible.value = false;
  } catch (error) {
    console.error("更新笔记失败:", error);
    ElMessage.error("更新笔记失败，请重试");
  } finally {
    formLoading.value = false;
  }
};
</script>

<style scoped>
.user-notes-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}
</style>
