<template>
  <div>
    <!-- 标题 -->
    <h3 class="text-xl font-medium mb-6">{{ isEdit ? '编辑广告' : '创建广告' }}</h3>

    <!-- 加载状态 -->
    <div v-if="loading && isEdit" class="py-8">
      <el-skeleton :rows="8" animated />
    </div>

    <!-- 编辑表单 -->
    <div v-else>
      <AdvertisementForm
        :advertisement="currentAdvertisement"
        :loading="advertisementStore.adminLoading"
        :submit-button-text="isEdit ? '更新广告' : '创建广告'"
        @submit="handleSubmit"
        @cancel="handleCancel"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import AdvertisementForm from "@/components/advertisement/AdvertisementForm.vue";
import { Routes } from "@/constants/routes";
import { useAdvertisementStore } from "@/stores/advertisement";
import type { Advertisement } from "@/types/advertisement";
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const advertisementStore = useAdvertisementStore();

const loading = ref(false);
const currentAdvertisement = ref<Advertisement | null>(null);

// 判断是编辑还是创建
const isEdit = computed(() => Boolean(route.params.id));

// 加载广告数据（如果是编辑模式）
onMounted(async () => {
  if (isEdit.value) {
    loading.value = true;
    const advertisementId = Number(route.params.id);
    await advertisementStore.fetchAdvertisementById(advertisementId);
    currentAdvertisement.value = advertisementStore.currentAdvertisement;
    loading.value = false;
  }
});

// 提交表单
const handleSubmit = async (advertisement: Advertisement) => {
  let success = false;

  if (isEdit.value) {
    // 更新广告
    success = await advertisementStore.updateAdvertisement(advertisement);
    if (success) {
      ElMessage.success("广告更新成功");
      router.push(Routes.ADMIN_ADVERTISEMENTS);
    }
  } else {
    // 创建广告
    success = await advertisementStore.createAdvertisement(advertisement);
    if (success) {
      ElMessage.success("广告创建成功");
      router.push(Routes.ADMIN_ADVERTISEMENTS);
    }
  }
};

// 取消操作
const handleCancel = () => {
  router.push(Routes.ADMIN_ADVERTISEMENTS);
};
</script>
