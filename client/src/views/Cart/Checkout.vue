<template>
  <div class="container mx-auto py-8 px-4">
    <div class="max-w-4xl mx-auto">
      <!-- 结算标题 -->
      <h1 class="text-3xl font-bold text-gray-800 mb-8">订单结算</h1>

      <!-- 加载状态 -->
      <div v-if="loading" class="py-16 flex justify-center">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 购物车为空时的提示 -->
      <el-empty
        v-else-if="cartStore.isEmpty"
        description="您的购物车是空的，无法结算"
        class="py-16"
      >
        <template #extra>
          <el-button type="primary" @click="router.push(Routes.PRODUCT_LIST)" class="rounded-lg">
            去购物
          </el-button>
        </template>
      </el-empty>

      <!-- 结算表单 -->
      <template v-else>
        <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6 mb-6">
          <h2 class="text-lg font-medium border-b pb-3 mb-4">收货地址</h2>

          <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
            <el-form-item label="详细地址" prop="shippingAddress">
              <el-input
                v-model="form.shippingAddress"
                type="textarea"
                :rows="3"
                placeholder="请输入详细地址，包括省/市/区、街道、小区/楼栋、单元/门牌号等"
              />
            </el-form-item>
          </el-form>
        </div>

        <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6 mb-6">
          <h2 class="text-lg font-medium border-b pb-3 mb-4">支付方式</h2>

          <el-radio-group v-model="form.paymentMethod">
            <el-radio label="ALIPAY" border>
              <div class="flex items-center gap-2">
                <img src="@/assets/images/alipay-logo.svg" alt="支付宝" class="h-6" />
                <span>支付宝</span>
              </div>
            </el-radio>
          </el-radio-group>
        </div>

        <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6 mb-6">
          <h2 class="text-lg font-medium border-b pb-3 mb-4">订单商品</h2>

          <!-- 商品列表 -->
          <div v-for="item in cartStore.items" :key="item.cartItemId" class="py-4 flex items-center gap-4 border-b last:border-0">
            <div class="w-16 h-20 bg-gray-100 rounded-md overflow-hidden flex-shrink-0">
              <img
                :src="item.cover || '@/assets/images/placeholder.svg'"
                :alt="item.title"
                referrerPolicy="no-referrer"
                class="w-full h-full object-cover object-center"
              />
            </div>
            <div class="flex-grow">
              <div class="text-base font-medium text-gray-800 line-clamp-1">{{ item.title }}</div>
              <div class="text-sm text-gray-500">{{ item.quantity }}件</div>
            </div>
            <div class="text-tomato-600 font-medium">
              ¥{{ (item.price! * item.quantity).toFixed(2) }}
            </div>
          </div>
        </div>

        <!-- 订单汇总区域 -->
        <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md p-6">
          <div class="flex justify-between mb-2">
            <span>商品总金额：</span>
            <span>¥{{ cartStore.totalAmount.toFixed(2) }}</span>
          </div>

          <div class="border-t border-dashed mt-4 pt-4 flex justify-between">
            <span class="text-lg">应付总额：</span>
            <span class="text-xl font-bold text-tomato-600">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
          </div>

          <!-- 结算按钮 -->
          <div class="mt-6 flex justify-end">
            <el-button @click="router.push(Routes.CART)" class="rounded-lg mr-4">返回购物车</el-button>
            <el-button
              type="primary"
              size="large"
              @click="handleSubmitOrder"
              :loading="submitting"
              class="rounded-lg"
            >
              提交订单
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
import { useUserStore } from "@/stores/user";
import type { Checkout } from "@/types/cart";
import { PaymentMethod } from "@/types/order";
import { buildRoute } from "@/utils/routeHelper";
import { getCheckoutRules } from "@/utils/validators";
import type { FormInstance } from "element-plus";
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const cartStore = useCartStore();
const userStore = useUserStore();
const formRef = ref<FormInstance>();
const loading = ref(false);
const submitting = ref(false);

// 表单数据
const form = reactive({
  shippingAddress: "",
  paymentMethod: PaymentMethod.ALIPAY,
});

const rules = getCheckoutRules();

// 加载购物车数据和用户地址
onMounted(async () => {
  loading.value = true;
  await cartStore.fetchUserCart();

  // 获取用户信息并填充地址
  if (userStore.user && userStore.user.address) {
    form.shippingAddress = userStore.user.address;
  }

  loading.value = false;
});

// 提交订单
const handleSubmitOrder = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true;

        // 准备结算数据
        const checkout: Checkout = {
          cartItemIds: cartStore.selectedCartItemIds.filter(
            (id): id is number => id !== undefined,
          ),
          shippingAddress: form.shippingAddress,
          paymentMethod: form.paymentMethod,
        };

        // 提交订单
        const order = await cartStore.checkout(checkout);

        if (order && order.orderId) {
          // 跳转到支付页面
          router.push({
            path: buildRoute(Routes.ORDER_PAY, {
              orderId: order.orderId,
            }),
          });
        } else {
          ElMessage.error("创建订单失败");
        }
      } catch (error) {
        console.error("提交订单出错：", error);
        ElMessage.error("提交订单时出现错误");
      } finally {
        submitting.value = false;
      }
    }
  });
};
</script>
