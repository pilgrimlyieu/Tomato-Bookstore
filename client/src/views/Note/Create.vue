<template>
  <div class="note-create-container p-6">
    <div class="max-w-4xl mx-auto">
      <el-card class="w-full">
        <template #header>
          <div class="card-header flex justify-between items-center">
            <div>
              <h1 class="text-xl font-bold">创建读书笔记</h1>
              <p class="text-gray-500 mt-2">为《{{ productTitle }}》创建读书笔记</p>
            </div>
            <el-button @click="router.back()" class="rounded-lg">返回</el-button>
          </div>
        </template>

        <div v-loading="loading">
          <template v-if="!hasUserNote">
            <NoteForm
              :loading="formLoading"
              :product-title="productTitle"
              @submit="handleSubmit"
              @cancel="router.back()"
            />
          </template>
          <el-alert
            v-else
            title="您已经为这本书创建过读书笔记"
            type="warning"
            :closable="false"
            show-icon
          >
            <template #default>
              <p class="mb-2">每本书只能创建一篇读书笔记。</p>
              <div class="flex gap-2 mt-4">
                <el-button
                  size="small"
                  @click="viewExistingNote"
                  type="primary"
                  class="rounded-lg"
                >
                  查看已有笔记
                </el-button>
                <el-button size="small" @click="router.back()" class="rounded-lg">返回</el-button>
              </div>
            </template>
          </el-alert>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import NoteForm from "@/components/note/NoteForm.vue";
import { useNote } from "@/composables/useNote";
import { Routes } from "@/constants/routes";
import { useProductStore } from "@/stores/product";
import { useUserStore } from "@/stores/user";
import type { NoteCreateParams } from "@/types/note";
import { buildRoute } from "@/utils/routeHelper";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const productStore = useProductStore();
const productId = Number(route.params.productId);

const { loading, productNotes, createNote, fetchProductNotes } = useNote();
const formLoading = ref(false);

// 加载商品信息和笔记数据
onMounted(async () => {
  if (productId) {
    await Promise.all([
      productStore.fetchProductById(productId),
      fetchProductNotes(productId),
    ]);
  } else {
    router.push(Routes.PRODUCT_LIST);
  }
});

// 商品标题
const productTitle = computed(() => {
  return productStore.currentProduct?.title || "未知图书";
});

// 检查用户是否已经为该商品创建了笔记
const hasUserNote = computed(() => {
  if (!productNotes.value.length) return false;

  const userStore = useUserStore();
  return productNotes.value.some(
    (note) =>
      note.userId === userStore.user?.id && note.productId === productId,
  );
});

// 查看已有笔记
const viewExistingNote = () => {
  const userStore = useUserStore();
  const userNote = productNotes.value.find(
    (note) =>
      note.userId === userStore.user?.id && note.productId === productId,
  );

  if (userNote) {
    router.push(buildRoute(Routes.NOTE_DETAIL, { noteId: userNote.id }));
  }
};

// 提交表单
const handleSubmit = async (formData: NoteCreateParams) => {
  formLoading.value = true;
  try {
    await createNote(productId, formData);
  } catch (error) {
    console.error("创建笔记失败:", error);
    ElMessage.error("创建笔记失败，请重试");
  } finally {
    formLoading.value = false;
  }
};
</script>

<style scoped>
.note-create-container {
  width: 100%;
  min-height: 70vh;
}
</style>
