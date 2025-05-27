<template>
  <div>
    <div class="mb-6 flex justify-between items-center">
      <h3 class="text-xl font-medium">商品列表</h3>
      <el-button type="primary" class="rounded-lg" @click="router.push(Routes.ADMIN_PRODUCT_CREATE)">
        <el-icon class="mr-1"><Plus /></el-icon>
        添加商品
      </el-button>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="mb-6">
      <el-input
        v-model="searchQuery"
        placeholder="搜索商品名称……"
        class="w-60"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
      />
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="productStore.loading"
      :data="paginatedProducts"
      stripe
      border
      style="width: 100%"
      class="mb-4"
    >
      <el-table-column type="index" width="60" align="center" />
      <el-table-column label="封面" width="120" align="center">
        <template #default="scope">
          <el-image
            :src="scope.row.cover || '@/assets/images/placeholder.svg'"
            :preview-src-list="[scope.row.cover || '@/assets/images/placeholder.svg']"
            fit="cover"
            referrerPolicy="no-referrer"
            class="h-16 w-12 object-cover rounded"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="商品名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="price" label="价格" width="130">
        <template #default="scope">
          <span class="text-tomato-600 font-medium">{{ formatPrice(scope.row.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="评分" width="120" align="center">
        <template #default="scope">
          <el-rate
            disabled
            :max="5"
            :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
            text-color="#ff9900"
            score-template="{value}"
            :show-score="true"
            :model-value="scope.row.rate / 2"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="scope">
          <el-button
            size="small"
            @click="viewStockpile(scope.row)"
            class="rounded-md"
            type="info"
            plain
          >
            库存
          </el-button>
          <el-button
            size="small"
            @click="router.push(buildRoute(Routes.PRODUCT_DETAIL, { id: scope.row.id }))"
            class="rounded-md"
            type="success"
            plain
          >
            查看
          </el-button>
          <el-button
            size="small"
            @click="router.push(buildRoute(Routes.ADMIN_PRODUCT_EDIT, { id: scope.row.id }))"
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
        :total="filteredProducts.length"
        background
      />
    </div>

    <!-- 库存管理对话框 -->
    <el-dialog
      v-model="stockpileDialogVisible"
      title="库存管理"
      width="500px"
      :close-on-click-modal="false"
    >
      <div v-if="currentEditingProduct">
        <h3 class="text-lg font-medium mb-4">{{ currentEditingProduct.title }}</h3>

        <el-form label-position="top" :model="stockpileForm" ref="stockpileFormRef">
          <el-form-item label="可售库存">
            <el-input-number
              v-model="stockpileForm.amount"
              :min="0"
              :step="1"
              class="w-full"
            />
          </el-form-item>
          <el-form-item label="冻结库存">
            <el-input-number
              v-model="stockpileForm.frozen"
              :min="0"
              :step="1"
              class="w-full"
            />
            <div class="text-gray-500 text-sm mt-1">
              冻结库存指已下单但尚未完成支付的库存
            </div>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="stockpileDialogVisible = false" class="rounded-lg">取消</el-button>
          <el-button
            type="primary"
            @click="handleUpdateStockpile"
            class="rounded-lg"
            :loading="productStore.adminLoading"
          >
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useProductStore } from "@/stores/product";
import type { Product } from "@/types/product";
import { formatPrice } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import { Plus, Search } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance } from "element-plus";
import { useRouter } from "vue-router";

const router = useRouter();
const productStore = useProductStore();
const searchQuery = ref("");
const currentPage = ref(1);
const pageSize = ref(10);

// 库存管理相关
const stockpileDialogVisible = ref(false);
const stockpileFormRef = ref<FormInstance>();
const stockpileForm = ref({
  amount: 0,
  frozen: 0,
});
const currentEditingProduct = ref<Product | null>(null);

// 加载商品数据
onMounted(async () => {
  await productStore.fetchAllProducts();
});

// 搜索过滤商品列表
const filteredProducts = computed(() => {
  if (!searchQuery.value.trim()) {
    return productStore.products;
  }

  const query = searchQuery.value.toLowerCase();
  return productStore.products.filter(
    (product) =>
      product.title.toLowerCase().includes(query) ||
      (product.description &&
        product.description.toLowerCase().includes(query)),
  );
});

// 分页处理数据
const paginatedProducts = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  return filteredProducts.value.slice(startIndex, endIndex);
});

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
};

// 查看并编辑库存
const viewStockpile = async (product: Product) => {
  currentEditingProduct.value = product;

  // 获取商品库存信息
  await productStore.fetchStockpile(product.id!);

  if (productStore.currentStockpile) {
    stockpileForm.value.amount = productStore.currentStockpile.amount;
    stockpileForm.value.frozen = productStore.currentStockpile.frozen;
    stockpileDialogVisible.value = true;
  } else {
    ElMessage.error("获取库存信息失败");
  }
};

// 更新库存
const handleUpdateStockpile = async () => {
  if (!currentEditingProduct.value) return;

  const success = await productStore.updateStockpile(
    currentEditingProduct.value.id!,
    {
      amount: stockpileForm.value.amount,
      frozen: stockpileForm.value.frozen,
    },
  );

  if (success) {
    stockpileDialogVisible.value = false;
  }
};

// 删除商品
const handleDelete = (product: Product) => {
  ElMessageBox.confirm(
    `确定要删除商品"${product.title}"吗？此操作不可逆`,
    "警告",
    {
      confirmButtonText: "确定删除",
      cancelButtonText: "取消",
      type: "warning",
    },
  )
    .then(async () => {
      if (product.id) {
        await productStore.deleteProduct(product.id);
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
