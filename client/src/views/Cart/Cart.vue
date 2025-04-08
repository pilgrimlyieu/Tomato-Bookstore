<template>
  <div class="container mx-auto py-8 px-4">
    <div class="max-w-6xl mx-auto">
      <!-- 购物车标题 -->
      <h1 class="text-3xl font-bold text-gray-800 mb-8">我的购物车</h1>

      <!-- 购物车为空时的提示 -->
      <div v-if="loading" class="py-16 flex justify-center">
        <el-skeleton :rows="5" animated />
      </div>

      <el-empty
        v-else-if="isEmpty"
        description="您的购物车是空的"
        class="py-16"
      >
        <template #extra>
          <el-button type="primary" @click="router.push(Routes.PRODUCT_LIST)" class="rounded-lg">
            去购物
          </el-button>
        </template>
      </el-empty>

      <!-- 购物车商品列表 -->
      <div v-else>
        <div class="bg-white/90 backdrop-blur-md rounded-xl shadow-md overflow-hidden">
          <!-- 表头 -->
          <div class="grid grid-cols-12 gap-4 p-4 text-gray-600 bg-gray-50 font-medium border-b">
            <div class="col-span-6">商品</div>
            <div class="col-span-2 text-center">单价</div>
            <div class="col-span-2 text-center">数量</div>
            <div class="col-span-2 text-right">小计</div>
          </div>

          <!-- 商品列表 -->
          <div v-for="item in cartStore.items" :key="item.cartItemId" class="border-b">
            <div class="grid grid-cols-12 gap-4 p-4 items-center">
              <!-- 商品信息 -->
              <div class="col-span-6 flex items-center gap-4">
                <div class="w-20 h-24 bg-gray-100 rounded-md overflow-hidden flex-shrink-0">
                  <img
                    :src="item.cover || '@/assets/images/placeholder.svg'"
                    :alt="item.title"
                    class="w-full h-full object-cover object-center"
                  />
                </div>
                <div class="flex-grow">
                  <router-link
                    :to="buildRoute(Routes.PRODUCT_DETAIL, { id: item.productId })"
                    class="text-lg font-medium text-gray-800 hover:text-tomato-600 line-clamp-2"
                  >
                    {{ item.title }}
                  </router-link>
                  <div class="flex mt-2">
                    <el-button
                      size="small"
                      @click="handleRemove(item.cartItemId!)"
                      type="danger"
                      class="rounded-lg"
                      plain
                    >
                      删除
                    </el-button>
                  </div>
                </div>
              </div>

              <!-- 单价 -->
              <div class="col-span-2 text-center">
                <span class="text-tomato-600 font-medium">¥{{ item.price?.toFixed(2) }}</span>
              </div>

              <!-- 数量 -->
              <div class="col-span-2 flex justify-center">
                <el-input-number
                  v-model="item.quantity"
                  :min="1"
                  :max="99"
                  size="small"
                  @change="(value) => handleQuantityChange(item.cartItemId!, value ?? 0)"
                />
              </div>

              <!-- 小计 -->
              <div class="col-span-2 text-right">
                <span class="text-tomato-600 font-medium">
                  ¥{{ (item.price! * item.quantity).toFixed(2) }}
                </span>
              </div>
            </div>
          </div>

          <!-- 购物车底部操作区 -->
          <div class="p-6 flex justify-between items-center">
            <div>
              <el-button @click="router.push(Routes.PRODUCT_LIST)" class="rounded-lg">
                继续购物
              </el-button>
              <el-button
                type="danger"
                plain
                class="rounded-lg ml-2"
                @click="handleClearCart"
                :disabled="loading"
              >
                清空购物车
              </el-button>
            </div>
            <div class="flex items-center gap-6">
              <div class="text-right">
                <div class="text-gray-600">
                  共 <span class="font-medium">{{ cartStore.totalItems }}</span> 件商品，总计：
                </div>
                <div class="text-2xl font-bold text-tomato-600">
                  ¥{{ cartStore.totalAmount.toFixed(2) }}
                </div>
              </div>
              <el-button
                type="primary"
                size="large"
                @click="handleCheckout"
                :disabled="loading"
                class="rounded-lg"
              >
                去结算
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useCartStore } from "@/stores/cart";
import { buildRoute } from "@/utils/routeHelper";
import { ElMessageBox } from "element-plus";
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const cartStore = useCartStore();
const loading = ref(false);

// 购物车是否为空
const isEmpty = computed(() => cartStore.isEmpty);

// 加载购物车数据
onMounted(async () => {
  loading.value = true;
  await cartStore.fetchUserCart();
  loading.value = false;
});

// 修改商品数量
const handleQuantityChange = async (cartItemId: number, quantity: number) => {
  await cartStore.updateQuantity(cartItemId, quantity);
};

// 删除购物车商品
const handleRemove = async (cartItemId: number) => {
  try {
    await ElMessageBox.confirm("确定要将该商品从购物车中移除吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await cartStore.removeFromCart(cartItemId);
  } catch (e) {
    // 取消删除，不做任何操作
  }
};

// 清空购物车
const handleClearCart = async () => {
  try {
    await ElMessageBox.confirm("确定要清空购物车吗？此操作不可恢复", "警告", {
      confirmButtonText: "确定清空",
      cancelButtonText: "取消",
      type: "warning",
      confirmButtonClass: "el-button--danger",
    });

    await cartStore.clearCart();
  } catch (e) {
    // 取消清空，不做任何操作
  }
};

// 去结算
const handleCheckout = () => {
  if (cartStore.items.length > 0) {
    router.push(Routes.CART_CHECKOUT);
  }
};
</script>
