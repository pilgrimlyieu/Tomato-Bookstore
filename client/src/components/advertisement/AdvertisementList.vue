<template>
  <div class="advertisement-list">
    <el-skeleton :loading="loading" animated :count="4">
      <template #template>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div v-for="i in 4" :key="i" class="bg-white rounded-xl shadow-sm p-4">
            <el-skeleton-item variant="image" class="w-full h-48 mb-4" />
            <el-skeleton-item variant="h3" class="w-3/4 mb-2" />
            <el-skeleton-item variant="p" class="w-full mb-2" />
            <el-skeleton-item variant="p" class="w-2/3" />
          </div>
        </div>
      </template>

      <template #default>
        <div v-if="advertisements.length === 0" class="py-10 text-center">
          <el-empty description="暂无广告数据" />
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <advertisement-card
            v-for="advertisement in advertisements"
            :key="advertisement.id"
            :advertisement="advertisement"
          />
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup lang="ts">
import {
  BusinessErrorCode,
  BusinessErrorMessages,
} from "@/constants/errorCode";
import advertisementService from "@/services/advertisement-service";
import type { Advertisement } from "@/types/advertisement";
import { ElMessage } from "element-plus";
import { onMounted, ref } from "vue";
import AdvertisementCard from "./AdvertisementCard.vue";

const advertisements = ref<Advertisement[]>([]);
const loading = ref(true);

/**
 * 加载广告数据
 */
const loadAdvertisements = async () => {
  try {
    loading.value = true;
    const response = await advertisementService.getAllAdvertisements();
    advertisements.value = response.data;
  } catch (error) {
    console.error("加载广告数据失败：", error);
    ElMessage.error(
      BusinessErrorMessages[BusinessErrorCode.SYSTEM_ERROR] ||
        "加载广告数据失败",
    );
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadAdvertisements();
});
</script>
