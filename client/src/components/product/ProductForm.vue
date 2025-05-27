<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-position="top"
  >
    <!-- 基本信息 -->
    <el-form-item label="商品名称" prop="title">
      <el-input v-model="form.title" placeholder="请输入商品名称" />
    </el-form-item>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <el-form-item label="商品价格" prop="price">
        <el-input-number
          v-model="form.price"
          :min="0"
          :precision="2"
          :step="0.1"
          class="w-full"
        />
      </el-form-item>

      <el-form-item label="商品评分" prop="rate">
        <el-rate
          v-model="form.rateDisplay"
          :max="5"
          :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
          :show-text="true"
          :allow-half="true"
          @change="handleRateChange"
        />
      </el-form-item>
    </div>

    <el-form-item label="商品封面" prop="cover">
      <el-input v-model="form.cover" placeholder="请输入封面图片 URL" />
      <div class="mt-2 flex items-center gap-4">
        <div v-if="form.cover" class="w-24 h-32 rounded overflow-hidden bg-gray-100 shadow-md">
          <img :src="form.cover" class="w-full h-full object-cover object-center" />
        </div>
        <div v-else class="w-24 h-32 rounded bg-gray-100 flex items-center justify-center text-gray-400 shadow-md">
          无封面
        </div>
      </div>
    </el-form-item>

    <el-form-item label="商品描述" prop="description">
      <el-input
        v-model="form.description"
        type="textarea"
        :rows="3"
        placeholder="请输入商品描述"
      />
    </el-form-item>

    <!-- 商品规格 -->
    <div class="mb-6">
      <div class="flex items-center justify-between mb-3">
        <h3 class="text-lg font-medium">商品规格</h3>
        <el-button size="small" @click="addSpecification" type="primary" class="rounded-lg" plain>
          添加规格
        </el-button>
      </div>

      <div v-if="form.specifications.length === 0" class="text-gray-400 text-center py-4 border rounded-lg mb-4">
        暂无规格信息，点击添加规格按钮添加
      </div>

      <div v-for="(spec, index) in form.specifications" :key="index" class="mb-3 p-4 border rounded-lg bg-gray-50">
        <div class="flex justify-between items-start mb-3">
          <h4 class="text-md font-medium">规格 #{{ index + 1 }}</h4>
          <el-button
            size="small"
            @click="removeSpecification(index)"
            type="danger"
            class="rounded-lg"
            plain
          >
            删除
          </el-button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <el-form-item :label="`规格名称`" :prop="`specifications.${index}.item`">
            <el-input v-model="spec.item" placeholder="例如：作者、出版社、装帧" />
          </el-form-item>

          <el-form-item :label="`规格值`" :prop="`specifications.${index}.value`">
            <el-input v-model="spec.value" placeholder="例如：鲁迅、人民文学出版社、精装" />
          </el-form-item>
        </div>
      </div>
    </div>

    <!-- 商品详情 -->
    <el-form-item label="商品详情" prop="detail">
      <el-input
        v-model="form.detail"
        type="textarea"
        :rows="8"
        placeholder="请输入商品详情，支持 HTML 格式"
      />
      <div class="text-gray-500 text-sm mt-1">
        提示：支持 HTML 格式，可以添加丰富的商品详情描述。
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
import type { Product, Specification } from "@/types/product";
import { getProductRules, getSpecificationRules } from "@/utils/validators";
import type { FormInstance, FormRules } from "element-plus";

const props = defineProps<{
  product?: Product | null;
  loading?: boolean;
  submitButtonText?: string;
}>();

const emit = defineEmits<{
  (e: "submit", product: Product): void;
  (e: "cancel"): void;
}>();

const formRef = ref<FormInstance>();

// 表单数据
const form = reactive<{
  id?: number;
  title: string;
  price: number;
  rate: number;
  rateDisplay: number; // 用于显示的 0-5 分制评分
  description: string;
  cover: string;
  detail: string;
  specifications: Specification[];
}>({
  title: "",
  price: 0,
  rate: 0,
  rateDisplay: 0,
  description: "",
  cover: "",
  detail: "",
  specifications: [],
});

// 表单验证规则
const rules = reactive<FormRules>(getProductRules());

// 监听规格数组变化，动态添加验证规则
watch(
  () => form.specifications.length,
  () => {
    Object.assign(rules, getProductRules());
    form.specifications.forEach((_, i) => {
      const specRules = getSpecificationRules(i);
      Object.assign(rules, specRules);
    });
  },
  { immediate: true },
);

// 处理评分变化（0-5 分转换为 0-10 分）
const handleRateChange = (value: number) => {
  form.rate = value * 2;
};

// 添加规格
const addSpecification = () => {
  form.specifications.push({
    item: "",
    value: "",
  });
};

// 删除规格
const removeSpecification = (index: number) => {
  form.specifications.splice(index, 1);
};

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return;

  await formRef.value.validate((valid) => {
    if (valid) {
      const productData: Product = {
        id: form.id,
        title: form.title,
        price: form.price,
        rate: form.rate,
        description: form.description,
        cover: form.cover,
        detail: form.detail,
        specifications: form.specifications,
      };

      emit("submit", productData);
    }
  });
};

// 处理取消
const handleCancel = () => {
  emit("cancel");
};

// 初始化表单数据
const initForm = () => {
  if (props.product) {
    form.id = props.product.id;
    form.title = props.product.title || "";
    form.price = props.product.price || 0;
    form.rate = props.product.rate || 0;
    form.rateDisplay = props.product.rate / 2 || 0;
    form.description = props.product.description || "";
    form.cover = props.product.cover || "";
    form.detail = props.product.detail || "";
    form.specifications = props.product.specifications
      ? [...props.product.specifications]
      : [];
  } else {
    // 重置表单
    form.id = undefined;
    form.title = "";
    form.price = 0;
    form.rate = 0;
    form.rateDisplay = 0;
    form.description = "";
    form.cover = "";
    form.detail = "";
    form.specifications = [];
  }
};

// 监听产品变化
watch(
  () => props.product,
  () => {
    initForm();
  },
  { deep: true },
);

onMounted(() => {
  initForm();
});
</script>
