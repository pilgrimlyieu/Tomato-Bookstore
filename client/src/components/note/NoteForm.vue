<template>
  <div class="note-form">
    <h2 class="text-xl font-semibold mb-4">
      {{ isEdit ? "编辑读书笔记" : "创建读书笔记" }}
    </h2>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      v-loading="!!loading"
    >
      <el-form-item label="笔记标题" prop="title">
        <el-input
          v-model="form.title"
          placeholder="请输入笔记标题（最多 50 个字符）"
          maxlength="50"
          show-word-limit
        ></el-input>
      </el-form-item>

      <el-form-item label="笔记内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          placeholder="请输入笔记内容（最多 2000 个字符）"
          :rows="8"
          maxlength="2000"
          show-word-limit
          resize="vertical"
        ></el-input>
      </el-form-item>

      <div class="flex justify-end gap-2 mt-4">
        <el-button @click="$emit('cancel')" plain class="rounded-lg">取消</el-button>
        <el-button
          type="primary"
          @click="handleSubmit"
          :loading="loading"
          class="rounded-lg"
        >
          {{ isEdit ? "更新笔记" : "发布笔记" }}
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import type { Note } from "@/types/note";
import type { FormInstance, FormRules } from "element-plus";

// Props
const props = defineProps<{
  initialData?: Note;
  isEdit?: boolean;
  loading?: boolean;
}>();

// Emits
const emit = defineEmits<{
  (e: "submit", formData: { title: string; content: string }): void;
  (e: "cancel"): void;
}>();

// 表单引用
const formRef = ref<FormInstance>();

// 表单数据
const form = reactive({
  title: props.initialData?.title || "",
  content: props.initialData?.content || "",
});

// 表单验证规则
const rules: FormRules = {
  title: [
    { required: true, message: "请输入笔记标题", trigger: "blur" },
    {
      min: 2,
      max: 50,
      message: "标题长度应在 2 到 50 个字符之间",
      trigger: "blur",
    },
  ],
  content: [
    { required: true, message: "请输入笔记内容", trigger: "blur" },
    {
      min: 10,
      max: 2000,
      message: "内容长度应在 10 到 2000 个字符之间",
      trigger: "blur",
    },
  ],
};

// 当初始数据变化时更新表单
watchEffect(() => {
  if (props.initialData) {
    form.title = props.initialData.title;
    form.content = props.initialData.content;
    if (formRef.value) {
      // 重置表单验证状态
      formRef.value.clearValidate();
    }
  }
});

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    const valid = await formRef.value.validate();
    if (valid) {
      emit("submit", {
        title: form.title,
        content: form.content,
      });
    }
  } catch (errors) {
    console.error("表单验证失败：", errors);
    ElMessage.warning("请正确填写表单内容");
  }
};
</script>

<style scoped>
.note-form {
  width: 100%;
}
</style>
