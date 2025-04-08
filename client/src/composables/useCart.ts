import { Routes } from "@/constants/routes";
import { useCartStore } from "@/stores/cart";
import type { Cart } from "@/types/cart";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { useAuth } from "./useAuth";

/**
 * 购物车相关的组合式函数
 */
export function useCart() {
  const cartStore = useCartStore();
  const router = useRouter();
  const { checkLogin } = useAuth();

  /**
   * 添加商品到购物车
   *
   * @param {number} productId 商品 ID
   * @param {number} quantity 数量
   * @param {string} [productName] 商品名称（用于显示成功消息）
   * @returns {Promise<boolean>} 是否添加成功
   */
  const addToCart = async (
    productId: number,
    quantity: number,
    productName?: string,
  ): Promise<boolean> => {
    // 先检查用户是否已登录
    if (!(await checkLogin())) {
      return false;
    }

    try {
      // 准备添加到购物车的数据
      const cart: Cart = {
        productId,
        quantity,
      };

      // 调用添加到购物车的方法
      const success = await cartStore.addToCart(cart);

      if (success) {
        ElMessage.success(
          `已将${productName ? `《${productName}》` : "商品"}加入购物车`,
        );
      }

      return success;
    } catch (error) {
      console.error("添加到购物车失败：", error);
      ElMessage.error("添加到购物车失败，请稍后再试");
      return false;
    }
  };

  /**
   * 立即购买商品（加入购物车后直接跳转到购物车页面）
   *
   * @param {number} productId 商品 ID
   * @param {number} quantity 数量
   * @param {string} [productName] 商品名称（用于显示成功消息）
   * @returns {Promise<boolean>} 是否操作成功
   */
  const buyNow = async (
    productId: number,
    quantity: number,
    productName?: string,
  ): Promise<boolean> => {
    const success = await addToCart(productId, quantity, productName);

    if (success) {
      // 添加成功后直接跳转到购物车页面
      router.push(Routes.CART);
    }

    return success;
  };

  return {
    addToCart,
    buyNow,
  };
}
