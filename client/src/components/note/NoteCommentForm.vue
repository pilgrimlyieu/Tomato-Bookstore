<template>
  <div class="note-comment-form mt-6">
    <h3 class="text-lg font-medium mb-4">发表评论</h3>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      @submit.prevent="handleSubmit"
      v-loading="!!loading"
    >
      <el-form-item prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          maxlength="500"
          show-word-limit
          resize="vertical"
        ></el-input>
      </el-form-item>

      <div class="flex justify-end">
        <el-button
          type="primary"
          @click="handleSubmit"
          :loading="loading"
          class="rounded-lg"
        >
          发表评论
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { getCommentRules } from "@/utils/validators";
import type { FormInstance } from "element-plus";
import { ElMessage } from "element-plus";

// Props
const props = defineProps<{
  loading?: boolean;
}>();

// Emits
const emit = defineEmits<{
  (e: "submit", content: string): void;
}>();

// 表单引用
const formRef = ref<FormInstance>();

// 表单数据
const form = reactive({
  content: "",
});

// 表单验证规则
const rules = getCommentRules();

// 获取用户 store
const userStore = useUserStore();

// 是否已登录
const isLoggedIn = computed(() => userStore.isLoggedIn);

// 处理表单提交
const handleSubmit = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning("请先登录后再发表评论");
    return;
  }

  if (!formRef.value) return;

  await formRef.value.validate((valid) => {
    if (valid) {
      emit("submit", form.content);
    }
  });
};

const resetForm = () => {
  form.content = ""; // 重置表单内容
  formRef.value?.resetFields(); // 重置表单验证状态
};

defineExpose({
  resetForm,
});
</script>

<style scoped>
.note-comment-form {
  width: 100%;
}
</style>
