<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-position="top"
  >
    <!-- 基本信息 -->
    <el-form-item label="广告标题" prop="title">
      <el-input v-model="form.title" placeholder="请输入广告标题" maxlength="50" show-word-limit />
    </el-form-item>

    <el-form-item label="广告内容" prop="content">
      <el-input
        v-model="form.content"
        type="textarea"
        :rows="3"
        placeholder="请输入广告内容"
        maxlength="500"
        show-word-limit
      />
    </el-form-item>

    <el-form-item label="关联商品 ID" prop="productId">
      <el-input-number
        v-model="form.productId"
        :min="1"
        :step="1"
        class="w-full"
        placeholder="请输入商品 ID"
      />
      <div class="text-gray-500 text-sm mt-1">
        请确保输入的商品 ID 存在于系统中
      </div>
    </el-form-item>

    <el-form-item label="广告图片" prop="imageUrl">
      <div class="space-y-3">
        <div class="flex items-center gap-4">
          <el-upload
            class="upload-demo"
            :show-file-list="false"
            :before-upload="handleBeforeUpload"
            :http-request="handleUploadImage"
            accept="image/*"
          >
            <el-button type="primary" plain class="rounded-lg" :loading="uploadingImage">
              <el-icon class="mr-1"><Upload /></el-icon>
              {{ uploadingImage ? '上传中...' : '上传图片' }}
            </el-button>
          </el-upload>
          <span class="text-gray-500 text-sm">支持 JPG、PNG、GIF、WebP 格式，最大 5MB</span>
        </div>

        <el-input
          v-model="form.imageUrl"
          placeholder="或直接输入图片 URL"
          clearable
        />

        <div class="mt-2 flex items-center gap-4">
          <div v-if="form.imageUrl" class="w-32 h-24 rounded overflow-hidden bg-gray-100 shadow-md">
            <img :src="form.imageUrl" class="w-full h-full object-cover object-center" />
          </div>
          <div v-else class="w-32 h-24 rounded bg-gray-100 flex items-center justify-center text-gray-400 shadow-md">
            无图片
          </div>
        </div>
      </div>
    </el-form-item>

    <!-- 按钮 -->
    <div class="mt-6 flex justify-end gap-4">
      <el-button @click="handleCancel" class="rounded-lg">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading" class="rounded-lg">
        {{ submitButtonText }}
      </el-button>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import uploadService from "@/services/upload-service";
import type { Advertisement } from "@/types/advertisement";
import { getAdvertisementRules } from "@/utils/validators";
import { Upload } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import type { FormInstance, UploadRequestOptions } from "element-plus";

const props = defineProps<{
  advertisement?: Advertisement | null;
  loading?: boolean;
  submitButtonText?: string;
}>();

const emit = defineEmits<{
  (e: "submit", advertisement: Advertisement): void;
  (e: "cancel"): void;
}>();

const formRef = ref<FormInstance>();
const uploadingImage = ref(false);

// 表单数据
const form = reactive<{
  id?: number;
  title: string;
  content: string;
  imageUrl: string;
  productId: number | null;
}>({
  title: "",
  content: "",
  imageUrl: "",
  productId: null,
});

// 表单验证规则
const rules = getAdvertisementRules();

// 上传前验证
const handleBeforeUpload = (file: File) => {
  const isImage = file.type.startsWith("image/");
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error("只能上传图片文件！");
    return false;
  }
  if (!isLt5M) {
    ElMessage.error("图片大小不能超过 5MB！");
    return false;
  }
  return true;
};

// 处理图片上传
const handleUploadImage = async (options: UploadRequestOptions) => {
  try {
    uploadingImage.value = true;
    const response = await uploadService.uploadAdvertisementImage(
      options.file as File,
    );
    form.imageUrl = response.data;
    ElMessage.success("广告图片上传成功");
  } catch (error) {
    console.error("上传失败：", error);
    ElMessage.error("广告图片上传失败");
  } finally {
    uploadingImage.value = false;
  }
};

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate((valid) => {
    if (valid) {
      const advertisementData: Advertisement = {
        id: form.id,
        title: form.title,
        content: form.content,
        imageUrl: form.imageUrl,
        productId: form.productId!,
      };

      emit("submit", advertisementData);
    }
  });
};

// 处理取消
const handleCancel = () => {
  emit("cancel");
};

// 初始化表单数据
const initForm = () => {
  if (props.advertisement) {
    form.id = props.advertisement.id;
    form.title = props.advertisement.title || "";
    form.content = props.advertisement.content || "";
    form.imageUrl = props.advertisement.imageUrl || "";
    form.productId = props.advertisement.productId || null;
  } else {
    // 重置表单
    form.id = undefined;
    form.title = "";
    form.content = "";
    form.imageUrl = "";
    form.productId = null;
  }
};

// 监听广告变化
watch(
  () => props.advertisement,
  () => {
    initForm();
  },
  { deep: true },
);

onMounted(() => {
  initForm();
});
</script>
