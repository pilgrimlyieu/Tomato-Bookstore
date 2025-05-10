<template>
  <div class="note-detail-container p-6">
    <div class="max-w-4xl mx-auto">
      <div v-loading="loading">
        <div v-if="note" class="bg-white/90 backdrop-blur-md rounded-xl shadow-md overflow-hidden">
          <!-- 评论区 -->
          <NoteDetail
            :note="note"
            :comments="noteComments"
            :loading="loading"
            :comment-loading="commentLoading"
            @edit="handleEdit"
            @delete="handleDelete"
            @like="handleLike"
            @dislike="handleDislike"
            @add-comment="handleAddComment"
            @delete-comment="handleDeleteComment"
          />
        </div>

        <el-empty
          v-else-if="!loading"
          description="笔记不存在或已被删除"
        >
          <router-link :to="Routes.PRODUCT_LIST">
            <el-button type="primary" class="rounded-lg">浏览图书</el-button>
          </router-link>
        </el-empty>
      </div>
    </div>

    <!-- 编辑笔记对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑读书笔记"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <NoteForm
        v-if="dialogVisible && note"
        :initial-data="note"
        :is-edit="true"
        :loading="formLoading"
        :product-title="note.productTitle"
        @submit="handleSubmit"
        @cancel="dialogVisible = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import NoteDetail from "@/components/note/NoteDetail.vue";
import NoteForm from "@/components/note/NoteForm.vue";
import { useNote } from "@/composables/useNote";
import { Routes } from "@/constants/routes";
import { FeedbackType } from "@/types/note";
import type { NoteComment, NoteCreateParams } from "@/types/note";
import { ElMessage } from "element-plus";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const noteId = ref<number>(Number(route.params.noteId));

const {
  loading,
  commentLoading,
  currentNote: note,
  noteComments,
  fetchNoteById,
  fetchNoteComments,
  updateNote,
  deleteNote,
  addFeedback,
  addComment,
  deleteComment,
  clearCurrentNote,
  clearComments,
} = useNote();

const dialogVisible = ref(false);
const formLoading = ref(false);
const commentFormRef = ref();

// 加载笔记详情和评论
onMounted(async () => {
  await loadNoteData();
});

// 加载笔记数据
const loadNoteData = async () => {
  if (!noteId.value) return;

  try {
    await fetchNoteById(noteId.value);
    await fetchNoteComments(noteId.value);
  } catch (error) {
    console.error("加载笔记数据失败:", error);
    ElMessage.error("加载笔记数据失败，请重试");
  }
};

// 编辑笔记
const handleEdit = () => {
  dialogVisible.value = true;
};

// 删除笔记
const handleDelete = async () => {
  if (!note.value) return;

  const success = await deleteNote(note.value.id, note.value.productId);
  if (success) {
    router.push(Routes.USER_NOTES);
  }
};

// 点赞
const handleLike = async () => {
  if (!note.value) return;
  await addFeedback(note.value.id, FeedbackType.LIKE);
};

// 点踩
const handleDislike = async () => {
  if (!note.value) return;
  await addFeedback(note.value.id, FeedbackType.DISLIKE);
};

// 添加评论
const handleAddComment = async (content: string) => {
  if (!note.value) return;
  await addComment(note.value.id, { content }, () => {
    // 提交成功后重置表单
    commentFormRef.value?.resetForm();
  });
};

// 删除评论
const handleDeleteComment = async (comment: NoteComment) => {
  if (!note.value) return;
  await deleteComment(note.value.id, comment.id);
};

// 提交编辑表单
const handleSubmit = async (formData: NoteCreateParams) => {
  if (!note.value) return;

  formLoading.value = true;
  try {
    await updateNote(note.value.id, formData);
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
.note-detail-container {
  width: 100%;
  min-height: 70vh;
}
</style>
