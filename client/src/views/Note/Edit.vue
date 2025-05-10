<template>
  <div class="note-edit-container p-6">
    <div class="max-w-4xl mx-auto">
      <el-card class="w-full">
        <template #header>
          <div class="card-header flex justify-between items-center">
            <div>
              <h1 class="text-xl font-bold">编辑读书笔记</h1>
              <p class="text-gray-500 mt-2" v-if="note">编辑《{{ note.productTitle }}》的读书笔记</p>
            </div>
            <el-button @click="router.back()" class="rounded-lg">返回</el-button>
          </div>
        </template>

        <div v-loading="loading">
          <template v-if="note">
            <NoteForm
              :initial-data="note"
              :is-edit="true"
              :loading="formLoading"
              :product-title="note.productTitle"
              @submit="handleSubmit"
              @cancel="router.back()"
            />
          </template>
          <el-empty
            v-else-if="!loading"
            description="笔记不存在或无权编辑"
          >
            <el-button @click="router.back()" class="rounded-lg">返回</el-button>
          </el-empty>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import NoteForm from "@/components/note/NoteForm.vue";
import { useNote } from "@/composables/useNote";
import { usePermissions } from "@/composables/usePermissions";
import { Routes } from "@/constants/routes";
import type { NoteCreateParams } from "@/types/note";
import { buildRoute } from "@/utils/routeHelper";
import { ElMessage } from "element-plus";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const noteId = Number(route.params.noteId);
const { canEditNote } = usePermissions();

const { loading, currentNote: note, fetchNoteById, updateNote } = useNote();
const formLoading = ref(false);

// 加载笔记详情
onMounted(async () => {
  if (noteId) {
    await loadNoteData();
  } else {
    router.push(Routes.USER_NOTES);
  }
});

// 加载笔记数据并检查权限
const loadNoteData = async () => {
  try {
    await fetchNoteById(noteId);
    if (!note.value) {
      ElMessage.error("笔记不存在或已被删除");
      router.push(Routes.USER_NOTES);
      return;
    }
    if (!canEditNote(note.value)) {
      // 检查是否有权限编辑
      ElMessage.error("您没有权限编辑这篇笔记");
      router.push(Routes.USER_NOTES);
    }
  } catch (error) {
    console.error("加载笔记数据失败:", error);
    ElMessage.error("加载笔记数据失败，请重试");
    router.push(Routes.USER_NOTES);
  }
};

// 提交表单
const handleSubmit = async (formData: NoteCreateParams) => {
  if (!note.value) return;

  formLoading.value = true;
  try {
    const success = await updateNote(note.value.id, formData);
    if (success) {
      router.push(buildRoute(Routes.NOTE_DETAIL, { noteId: note.value.id }));
    }
  } catch (error) {
    console.error("更新笔记失败:", error);
    if (error instanceof Error) {
      ElMessage.error(`更新笔记失败：${error.message || "请重试"}`);
    } else {
      ElMessage.error("更新笔记失败，请重试");
    }
  } finally {
    formLoading.value = false;
  }
};
</script>

<style scoped>
.note-edit-container {
  width: 100%;
  min-height: 70vh;
}
</style>
