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
    try {
      const advertisementId = Number(route.params.id);
      if (isNaN(advertisementId) || advertisementId <= 0) {
        throw new Error("无效的广告 ID");
      }

      await advertisementStore.fetchAdvertisementById(advertisementId);
      currentAdvertisement.value = advertisementStore.currentAdvertisement;

      if (!currentAdvertisement.value) {
        throw new Error("广告不存在");
      }
    } catch (error) {
      ElMessage.error(error instanceof Error ? error.message : "加载广告失败");
      router.push(Routes.ADMIN_ADVERTISEMENTS);
    } finally {
      loading.value = false;
    }
  }
});

// 提交表单
const handleSubmit = async (advertisement: Advertisement) => {
try {
  if (isEdit.value) {
    // 更新广告
    await advertisementStore.updateAdvertisement(advertisement);
    ElMessage.success("广告更新成功");
  } else {
    // 创建广告
    await advertisementStore.createAdvertisement(advertisement);
    ElMessage.success("广告创建成功");
  }
  router.push(Routes.ADMIN_ADVERTISEMENTS);
} catch (error) {
  const action = isEdit.value ? "更新" : "创建";
  console.error("广告操作失败：", error);
  ElMessage.error(
    `${action}广告失败： ${error instanceof Error ? error.message : "未知错误"}`,
  );
}
};

// 取消操作
const handleCancel = () => {
  router.push(Routes.ADMIN_ADVERTISEMENTS);
};
</script>
