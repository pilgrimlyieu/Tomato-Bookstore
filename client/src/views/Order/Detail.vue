<template>
  <div class="container mx-auto py-8 px-4">
    <div class="max-w-4xl mx-auto">
      <!-- 订单标题 -->
      <h1 class="text-3xl font-bold text-gray-800 mb-8">订单详情</h1>

      <!-- 加载状态 -->
      <div v-if="loading" class="py-16 flex justify-center">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 订单不存在 -->
      <el-empty
        v-else-if="!order"
        description="订单不存在或已被删除"
        class="py-16"
      >
        <template #extra>
          <el-button type="primary" @click="router.push(Routes.PRODUCT_LIST)" class="rounded-lg">
            返回商城
          </el-button>
        </template>
      </el-empty>

      <!-- 订单详情内容 -->
      <template v-else>
        <!-- 订单状态和操作区域 -->
        <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6 mb-6">
          <div class="flex justify-between items-center">
            <div>
              <div class="text-lg mb-1">订单号：{{ order.orderId }}</div>
              <div class="text-sm text-gray-500">下单时间：{{ formatDate(order.createTime) }}</div>
            </div>
            <div class="flex items-center">
              <el-tag :type="getOrderStatusType(order.status) || undefined" size="large" class="mr-4">
                {{ getOrderStatusText(order.status) }}
              </el-tag>

              <template v-if="order.status === OrderStatus.PENDING">
                <el-button type="primary" @click="handlePayOrder" class="rounded-lg mr-2">
                  去支付
                </el-button>
                <el-button @click="handleCancelOrder" type="danger" plain class="rounded-lg">
                  取消订单
                </el-button>
              </template>

              <template v-else-if="order.status === OrderStatus.PAID">
                <div class="text-sm text-gray-500">
                  支付时间： {{ formatDate(order.paymentTime) }}
                </div>
              </template>
            </div>
          </div>
        </div>

        <!-- 收货地址 -->
        <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6 mb-6">
          <h2 class="text-lg font-medium border-b pb-3 mb-4">收货地址</h2>
          <div class="text-gray-700">{{ order.shippingAddress }}</div>
        </div>

        <!-- 订单汇总信息 -->
        <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6">
          <div class="flex justify-between mb-2">
            <span>商品总金额：</span>
            <span>{{ formatPrice(order.totalAmount) }}</span>
          </div>

          <div class="flex justify-between mb-2">
            <span>支付方式：</span>
            <span>{{ getPaymentMethodText(order.paymentMethod) }}</span>
          </div>

          <div class="border-t border-dashed mt-4 pt-4 flex justify-between">
            <span class="text-lg">实付金额：</span>
            <span class="text-xl font-bold text-tomato-600">{{ formatPrice(order.totalAmount) }}</span>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useOrderStore } from "@/stores/order";
import { OrderStatus } from "@/types/order";
import { formatDate, formatPrice } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import { getPaymentMethodText } from "@/utils/statusHelpers";
import { getOrderStatusText, getOrderStatusType } from "@/utils/statusHelpers";
import { ElMessageBox } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const orderStore = useOrderStore();
const loading = ref(true);

// 获取订单 ID
const orderId = computed(() => Number(route.params.orderId));

// 当前订单信息
const order = computed(() => orderStore.currentOrder);

// 加载订单数据
onMounted(async () => {
  loading.value = true;
  await orderStore.fetchOrderById(orderId.value);
  loading.value = false;
});

// 支付订单
const handlePayOrder = () => {
  if (order.value && order.value.orderId) {
    router.push(buildRoute(Routes.ORDER_PAY, { orderId: order.value.orderId }));
  }
};

// 取消订单
const handleCancelOrder = async () => {
  if (!order.value || !order.value.orderId) return;

  try {
    await ElMessageBox.confirm("确定要取消该订单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const success = await orderStore.cancelOrder(order.value.orderId);
    if (success) {
      // 刷新订单数据
      await orderStore.fetchOrderById(order.value.orderId);
    }
  } catch {
    // 用户取消操作
  }
};
</script>
