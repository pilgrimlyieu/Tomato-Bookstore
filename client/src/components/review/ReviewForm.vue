<template>
  <div class="review-form">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      v-loading="!!loading"
    >
      <el-form-item label="评分" prop="rating">
        <el-rate
          v-model="form.rating"
          :max="10"
          show-score
          score-template="{value}"
          class="mb-2"
        />
        <div class="text-gray-500 text-sm">
          请为商品评分（0-10分）
        </div>
      </el-form-item>

      <el-form-item label="评论内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="5"
          placeholder="写下您对这本书的评价（选填）"
          resize="vertical"
          maxlength="1000"
          show-word-limit
        ></el-input>
      </el-form-item>

      <div class="form-actions mt-6 flex justify-end space-x-2">
        <el-button @click="handleCancel" plain>取消</el-button>
        <el-button
          type="primary"
          @click="handleSubmit"
          :loading="loading"
        >
          {{ isEdit ? '更新评价' : '发布评价' }}
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import type {
  Review,
  ReviewCreateParams,
  ReviewUpdateParams,
} from "@/types/review";
import { type FormInstance, type FormRules } from "element-plus";
import { onMounted, reactive, ref } from "vue";

// 属性
const props = defineProps<{
  productId?: number;
  initialData?: Review;
  isEdit?: boolean;
  loading?: boolean;
}>();

// 事件
const emit = defineEmits<{
  (e: "submit", data: ReviewCreateParams | ReviewUpdateParams): void;
  (e: "cancel"): void;
}>();

// 表单实例
const formRef = ref<FormInstance>();

// 表单数据
const form = reactive({
  rating: props.initialData?.rating ?? 8,
  content: props.initialData?.content ?? "",
});

// 表单校验规则
const rules = reactive<FormRules>({
  rating: [
    { required: true, message: "请选择评分", trigger: "change" },
    {
      type: "number",
      min: 0,
      max: 10,
      message: "评分范围为 0-10 分",
      trigger: "change",
    },
  ],
  content: [
    { max: 1000, message: "评论内容不能超过 1000 个字符", trigger: "blur" },
  ],
});

// 初始化
onMounted(() => {
  // 如果是编辑模式，使用初始数据填充表单
  if (props.isEdit && props.initialData) {
    form.rating = props.initialData.rating;
    form.content = props.initialData.content || "";
  }
});

// 取消操作
const handleCancel = () => {
  emit("cancel");
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    // 构建提交数据
    const submitData: ReviewCreateParams | ReviewUpdateParams = {
      rating: form.rating,
      content: form.content,
    };

    emit("submit", submitData);
  } catch (error) {
    console.error("表单验证失败：", error);
    ElMessage.error("表单验证失败，请检查输入是否正确");
  }
};
</script>

<style scoped>
.review-form :deep(.el-rate__item) {
  margin-right: 8px;
}

.review-form :deep(.el-rate__text) {
  margin-left: 10px;
}
</style>
