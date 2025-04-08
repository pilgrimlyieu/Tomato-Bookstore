<template>
  <div class="container mx-auto py-16 px-4">
    <div class="max-w-3xl mx-auto text-center">
      <el-result
        icon="error"
        title="支付失败"
        sub-title="很抱歉，您的订单支付过程中出现了问题"
      >
        <template #extra>
          <el-button type="primary" @click="router.push(Routes.PRODUCT_LIST)" class="rounded-lg">
            返回商城
          </el-button>
          <el-button @click="goToOrderDetail()" class="rounded-lg">
            查看订单
          </el-button>
          <el-button @click="retryPayment()" type="warning" class="rounded-lg">
            重新支付
          </el-button>
        </template>

        <div class="mt-8 text-left bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6">
          <h3 class="text-lg font-medium mb-4 border-b pb-3">可能的原因</h3>
          <ul class="list-disc pl-6 space-y-2 text-gray-600">
            <li>您的支付已被取消</li>
            <li>订单已过期</li>
            <li>网络连接不稳定</li>
            <li>支付平台暂时无法处理您的请求</li>
          </ul>

          <div class="mt-6 text-gray-600">
            如需帮助，请联系客服：{{ AppConfig.CONTACT_PHONE }}
          </div>
        </div>
      </el-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { AppConfig } from "@/constants/config";
import { Routes } from "@/constants/routes";
import { useOrderStore } from "@/stores/order";
import { buildRoute } from "@/utils/routeHelper";
import { computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();
const orderStore = useOrderStore();

// 当前订单信息
const order = computed(() => orderStore.currentOrder);

// 加载订单信息（如果有提供）
onMounted(async () => {
  const orderId = route.query.orderId;
  if (orderId && !isNaN(Number(orderId))) {
    await orderStore.fetchOrderById(Number(orderId));
  }
});

// 跳转到订单详情
const goToOrderDetail = () => {
  if (order.value && order.value.orderId) {
    router.push({
      path: buildRoute(Routes.ORDER_DETAIL, {
        orderId: order.value.orderId,
      }),
    });
  } else {
    router.push(Routes.USER_PROFILE);
  }
};

// 重试支付
const retryPayment = () => {
  if (order.value && order.value.orderId) {
    router.push({
      path: Routes.ORDER_PAY.replace(
        ":orderId",
        order.value.orderId.toString(),
      ),
    });
  } else {
    router.push(Routes.USER_PROFILE);
  }
};
</script>
