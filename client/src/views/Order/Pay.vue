<template>
  <div class="container mx-auto py-8 px-4">
    <div class="max-w-4xl mx-auto">
      <!-- 支付标题 -->
      <h1 class="text-3xl font-bold text-gray-800 mb-8">订单支付</h1>

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

      <!-- 订单已支付 -->
      <div v-else-if="order.status !== OrderStatus.PENDING" class="text-center py-16">
        <el-result
          icon="warning"
          title="该订单无法支付"
          :sub-title="`订单当前状态为${getOrderStatusText(order.status)}，不能进行支付操作`"
        >
          <template #extra>
            <el-button type="primary" @click="router.push(Routes.PRODUCT_LIST)" class="rounded-lg">
              返回商城
            </el-button>
            <el-button @click="goToOrderDetail()" class="rounded-lg">
              查看订单详情
            </el-button>
          </template>
        </el-result>
      </div>

      <!-- 支付表单 -->
      <template v-else>
        <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6 mb-6">
          <div class="flex justify-between items-center border-b pb-4 mb-4">
            <div>
              <div class="text-lg font-medium">订单号：{{ order.orderId }}</div>
              <div class="text-sm text-gray-500">请在 30 分钟内完成支付，超时订单将自动取消</div>
            </div>
            <div class="text-xl font-bold text-tomato-600">
              {{ formatPrice(order.totalAmount) }}
            </div>
          </div>

          <div class="text-center py-8">
            <p class="text-lg mb-6">请选择支付方式完成支付</p>

            <div class="flex justify-center space-x-4">
              <el-button
                type="primary"
                size="large"
                @click="handlePay"
                :loading="paying"
                :disabled="order.status !== OrderStatus.PENDING"
                class="rounded-lg"
              >
                <div class="flex items-center gap-2">
                  <img src="@/assets/images/alipay-logo.svg" alt="支付宝" class="h-6" />
                  <span>支付宝支付</span>
                </div>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 支付表单容器 -->
        <div v-if="paymentForm" class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6">
          <h3 class="text-lg font-medium mb-4">请在下方完成支付</h3>
          <div class="border p-4 rounded-lg payment-form-container min-h-[100px]" ref="formContainer"></div>

          <!-- 手动提交备用按钮 -->
          <div class="mt-4 text-center">
            <p class="text-yellow-600 mb-2">如果没有自动跳转，请点击下方按钮：</p>
            <el-button type="primary" @click="submitPaymentForm" class="rounded-lg">
              手动前往支付
            </el-button>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useCartStore } from "@/stores/cart";
import { useOrderStore } from "@/stores/order";
import { OrderStatus } from "@/types/order";
import { formatPrice } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import { getOrderStatusText } from "@/utils/statusHelpers";
import { ElMessage } from "element-plus";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const orderStore = useOrderStore();
const cartStore = useCartStore();
const loading = ref(true);
const paying = ref(false);
const paymentForm = ref("");
const formContainer = ref<HTMLElement | null>(null);

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
const handlePay = async () => {
  if (!order.value || !order.value.orderId) return;

  try {
    paying.value = true;
    const payment = await orderStore.payOrder(order.value.orderId);

    if (payment && payment.paymentForm) {
      // 显示支付表单
      paymentForm.value = payment.paymentForm;

      // 等待 DOM 更新后插入并执行表单
      await nextTick();
      insertPaymentForm();
    } else {
      ElMessage.error("获取支付信息失败");
    }
  } catch (error) {
    console.error("支付订单出错：", error);
    ElMessage.error("支付过程中出现错误");
  } finally {
    paying.value = false;
  }
};

// 插入并执行支付表单
const insertPaymentForm = () => {
  if (formContainer.value) {
    formContainer.value.innerHTML = paymentForm.value;

    // 尝试手动执行支付表单中的脚本
    const scriptContent =
      paymentForm.value.match(/<script>([\s\S]*?)<\/script>/)?.[1] || "";
    if (scriptContent) {
      try {
        setTimeout(() => {
          new Function(scriptContent)();
        }, 500);
      } catch (e) {
        console.error("执行支付脚本失败：", e);
      }
    }

    // 3 秒后检查是否需要手动提交
    setTimeout(() => {
      submitPaymentForm();
    }, 3000);
  }
};

// 手动提交表单的备用方法
const submitPaymentForm = () => {
  // 尝试多种方式查找表单
  const formElement =
    document.querySelector('form[name="punchout_form"]') ||
    formContainer.value?.querySelector("form") ||
    document.forms[0];

  if (formElement && formElement instanceof HTMLFormElement) {
    console.log("找到支付表单，正在提交…");
    try {
      formElement.submit();
      ElMessage.success("正在跳转到支付页面…");
    } catch (e) {
      console.error("提交表单失败：", e);
      ElMessage.error("提交支付表单失败，请刷新页面重试");
    }
  } else {
    console.error("未找到支付表单");
    ElMessage.error("支付表单加载失败，请刷新页面重试");
  }
};

// 跳转到订单详情
const goToOrderDetail = () => {
  if (order.value && order.value.orderId) {
    router.push(
      buildRoute(Routes.ORDER_DETAIL, { orderId: order.value.orderId }),
    );
  }
};

// 监听支付结果
watch(
  () => orderStore.currentOrder?.status,
  (newStatus) => {
    if (newStatus === OrderStatus.PAID) {
      ElMessage.success("支付成功！");
      // 支付成功后清空购物车
      cartStore.clearCart();
      router.push(Routes.ORDER_SUCCESS + "?orderId=" + order.value?.orderId);
    }
  },
);
</script>

<style scoped>
.payment-form-container {
  @apply min-h-[120px] relative;
}

.payment-form-container :deep(form) {
  @apply block w-full;
}

.payment-form-container :deep(input[type="submit"]) {
  @apply !block my-4 mx-auto py-2 px-6 bg-blue-500 hover:bg-blue-600 text-white border-0 rounded cursor-pointer transition-colors;
}
</style>
