<template>
  <div>
    <div class="mb-6 flex justify-between items-center">
      <h3 class="text-xl font-medium">广告列表</h3>
      <el-button type="primary" class="rounded-lg" @click="router.push(Routes.ADMIN_ADVERTISEMENT_CREATE)">
        <el-icon class="mr-1"><Plus /></el-icon>
        添加广告
      </el-button>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="mb-6">
      <el-input
        v-model="searchQuery"
        placeholder="搜索广告标题……"
        class="w-60"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="advertisementStore.loading"
      :data="paginatedAdvertisements"
      stripe
      border
      style="width: 100%"
      class="mb-4"
    >
      <el-table-column type="index" width="60" align="center" />
      <el-table-column label="广告图片" width="120" align="center">
        <template #default="scope">
          <el-image
            :src="scope.row.imageUrl"
            :preview-src-list="[scope.row.imageUrl]"
            fit="cover"
            class="h-16 w-16 object-cover rounded"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="广告标题" min-width="150" show-overflow-tooltip />
      <el-table-column prop="content" label="广告内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="productId" label="关联商品 ID" width="120" align="center" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
            size="small"
            @click="router.push(buildRoute(Routes.ADMIN_ADVERTISEMENT_EDIT, { id: scope.row.id }))"
            class="rounded-md"
          >
            编辑
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.row)"
            class="rounded-md"
            plain
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="flex justify-center">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="filteredAdvertisements.length"
        background
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useAdvertisementStore } from "@/stores/advertisement";
import type { Advertisement } from "@/types/advertisement";
import { buildRoute } from "@/utils/routeHelper";
import { Plus, Search } from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";

const router = useRouter();
const advertisementStore = useAdvertisementStore();
const searchQuery = ref("");
const currentPage = ref(1);
const pageSize = ref(10);

// 加载广告数据
onMounted(async () => {
  await advertisementStore.fetchAllAdvertisements();
});

// 搜索过滤广告列表
const filteredAdvertisements = computed(() => {
  if (!searchQuery.value.trim()) {
    return advertisementStore.advertisements;
  }

  const query = searchQuery.value.toLowerCase();
  return advertisementStore.advertisements.filter(
    (advertisement: Advertisement) =>
      advertisement.title.toLowerCase().includes(query) ||
      advertisement.content.toLowerCase().includes(query),
  );
});

// 分页处理数据
const paginatedAdvertisements = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  return filteredAdvertisements.value.slice(startIndex, endIndex);
});

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
};

// 删除广告
const handleDelete = (advertisement: Advertisement) => {
  ElMessageBox.confirm(
    `确定要删除广告「${advertisement.title}」吗？此操作不可逆`,
    "警告",
    {
      confirmButtonText: "确定删除",
      cancelButtonText: "取消",
      type: "warning",
    },
  )
    .then(async () => {
      if (advertisement.id) {
        try {
          await advertisementStore.deleteAdvertisement(advertisement.id);
          ElMessage.success("广告删除成功");
        } catch (error) {
          ElMessage.error("删除失败，请重试");
        }
      }
    })
    .catch(() => {
      // 取消删除
    });
};

// 监听页面改变重置搜索
watch(
  () => router.currentRoute.value.fullPath,
  () => {
    searchQuery.value = "";
    currentPage.value = 1;
  },
);
</script>
