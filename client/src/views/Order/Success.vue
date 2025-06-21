<template>
  <div class="container mx-auto py-16 px-4">
    <div class="max-w-3xl mx-auto text-center">
      <el-result
        icon="success"
        title="支付成功"
        sub-title="感谢您的购买，您的订单已支付成功"
      >
        <template #extra>
          <el-button type="primary" @click="router.push(Routes.PRODUCT_LIST)" class="rounded-lg">
            继续购物
          </el-button>
          <el-button @click="router.push(Routes.ORDER_LIST)" class="rounded-lg">
            查看我的订单
          </el-button>
        </template>

        <div class="mt-8 text-left bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6">
          <h3 class="text-lg font-medium mb-4 border-b pb-3">支付信息</h3>

          <div v-if="order" class="space-y-3">
            <div class="flex justify-between">
              <span class="text-gray-600">订单号：</span>
              <span>{{ order.orderId }}</span>
            </div>

            <div class="flex justify-between">
              <span class="text-gray-600">支付金额：</span>
              <span class="font-medium text-tomato-600">{{ formatPrice(order.totalAmount) }}</span>
            </div>

            <div class="flex justify-between">
              <span class="text-gray-600">支付时间：</span>
              <span>{{ formatDate(order.paymentTime) }}</span>
            </div>
          </div>

          <div v-else class="text-gray-500 text-center py-4">
            暂无订单信息
          </div>
        </div>
      </el-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useOrderStore } from "@/stores/order";
import { formatDate, formatPrice } from "@/utils/formatters";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();
const orderStore = useOrderStore();

// 当前订单信息
const order = computed(() => orderStore.currentOrder);

// 清空购物车
onMounted(async () => {
  // 如果 URL 有订单参数，加载订单信息
  const orderId = route.query.orderId;
  if (orderId && !isNaN(Number(orderId))) {
    await orderStore.fetchOrderById(Number(orderId));
  }
});
</script>
