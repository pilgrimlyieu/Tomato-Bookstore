<template>
  <div class="container mx-auto py-8 px-4">
    <div class="max-w-6xl mx-auto">
      <!-- 订单列表标题 -->
      <h1 class="text-3xl font-bold text-gray-800 mb-8">我的订单</h1>

      <!-- 加载状态 -->
      <div v-if="loading" class="py-16 flex justify-center">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 订单为空 -->
      <el-empty
        v-else-if="orders.length === 0"
        description="您还没有订单"
        class="py-16"
      >
        <template #extra>
          <el-button type="primary" @click="router.push(Routes.PRODUCT_LIST)" class="rounded-lg">
            去购物
          </el-button>
        </template>
      </el-empty>

      <!-- 订单列表 -->
      <template v-else>
        <!-- 筛选器 -->
        <div class="mb-6 flex flex-wrap justify-between items-center gap-4">
          <div class="flex items-center gap-2">
            <span class="text-gray-600">状态：</span>
            <el-radio-group v-model="statusFilter" size="small">
              <el-radio-button label="ALL">全部</el-radio-button>
              <el-radio-button :label="OrderStatus.PENDING">待支付</el-radio-button>
              <el-radio-button :label="OrderStatus.PAID">已支付</el-radio-button>
              <el-radio-button :label="OrderStatus.TIMEOUT">已超时</el-radio-button>
              <el-radio-button :label="OrderStatus.CANCELLED">已取消</el-radio-button>
            </el-radio-group>
          </div>
          <el-input
            v-model="searchQuery"
            placeholder="订单号搜索…"
            class="w-48"
            :prefix-icon="Search"
            clearable
          />
        </div>

        <!-- 订单卡片 -->
        <div class="space-y-6">
          <div
            v-for="order in filteredOrders"
            :key="order.orderId"
            class="bg-white/90 backdrop-blur-md rounded-xl shadow-md overflow-hidden"
          >
            <!-- 订单头部 -->
            <div class="p-4 bg-gray-50 border-b flex justify-between items-center">
              <div class="flex items-center gap-3">
                <span class="text-gray-500">订单号：{{ order.orderId }}</span>
                <span class="text-xs text-gray-500">{{ formatDate(order.createTime) }}</span>
              </div>
              <el-tag :type="getOrderStatusType(order.status)" size="small">
                {{ getOrderStatusText(order.status) }}
              </el-tag>
            </div>

            <!-- 订单底部 -->
            <div class="p-4 border-t flex justify-between items-center bg-gray-50/80">
              <div>
                <span class="text-gray-600">收货地址：</span>
                <span class="text-gray-800">{{ order.shippingAddress }}</span>
              </div>
              <div class="flex items-center gap-4">
                <div class="text-right">
                  <div class="text-gray-500">总计：</div>
                  <div class="text-xl font-bold text-tomato-600">
                    ¥{{ order.totalAmount.toFixed(2) }}
                  </div>
                </div>
                <div class="space-x-2">
                  <el-button
                    v-if="order.orderId && order.status === OrderStatus.PENDING"
                    type="primary"
                    size="small"
                    @click="goToPayOrder(order.orderId)"
                    class="rounded-lg"
                  >
                    去支付
                  </el-button>
                  <el-button
                    v-if="order.orderId && order.status === OrderStatus.PENDING"
                    type="danger"
                    size="small"
                    @click="cancelOrder(order.orderId)"
                    plain
                    class="rounded-lg"
                  >
                    取消订单
                  </el-button>
                  <el-button
                    v-if="order.orderId"
                    type="info"
                    size="small"
                    @click="viewOrderDetail(order.orderId)"
                    plain
                    class="rounded-lg"
                  >
                    订单详情
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="mt-8 flex justify-center" v-if="orders.length > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            layout="prev, pager, next"
            :total="orders.length"
            background
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useOrderStore } from "@/stores/order";
import { type Order, OrderStatus } from "@/types/order";
import { formatDate } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import { getOrderStatusText, getOrderStatusType } from "@/utils/statusHelpers";
import { Search } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const orderStore = useOrderStore();

const loading = ref(true);
const orders = ref<Order[]>([]);
const statusFilter = ref("ALL");
const searchQuery = ref("");
const currentPage = ref(1);
const pageSize = ref(5);

// 加载订单列表
const loadOrders = async () => {
  loading.value = true;
  try {
    const result = await orderStore.fetchOrderList();
    if (result) {
      orders.value = orderStore.orderList;
    }
  } catch (error) {
    console.error("获取订单列表失败：", error);
    ElMessage.error("获取订单列表失败");
  } finally {
    loading.value = false;
  }
};

// 过滤订单列表
const filteredOrders = computed(() => {
  let result = [...orders.value];

  // 状态过滤
  if (statusFilter.value !== "ALL") {
    result = result.filter((order) => order.status === statusFilter.value);
  }

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter((order) =>
      order.orderId?.toString().includes(query),
    );
  }

  // 分页
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  return result.slice(startIndex, endIndex);
});

// 初始化加载
onMounted(() => {
  loadOrders();
});

// 查看订单详情
const viewOrderDetail = (orderId: number) => {
  router.push(buildRoute(Routes.ORDER_DETAIL, { orderId }));
};

// 支付订单
const goToPayOrder = (orderId: number) => {
  router.push(buildRoute(Routes.ORDER_PAY, { orderId }));
};

// 取消订单
const cancelOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm("确定要取消该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const success = await orderStore.cancelOrder(orderId);
    if (success) {
      // 刷新订单列表
      await loadOrders();
      ElMessage.success("订单已取消");
    }
  } catch {
    // 用户取消操作
  }
};
</script>
