import cartService from "@/services/cart-service";
import type { Cart, CartList, Checkout, QuantityUpdate } from "@/types/cart";
import type { Order } from "@/types/order";
import { HttpStatusCode } from "axios";
import { ElMessage } from "element-plus";
import { defineStore } from "pinia";

/**
 * 购物车状态管理
 */
export const useCartStore = defineStore("cart", {
  state: () => ({
    items: [] as Cart[], // 购物车商品列表
    totalAmount: 0, // 购物车总金额
    totalItems: 0, // 购物车商品总数
    loading: false, // 加载状态
  }),

  getters: {
    // 购物车是否为空
    isEmpty: (state) => state.items.length === 0,

    // 选中的购物车商品 ID 列表
    selectedCartItemIds: (state) => state.items.map((item) => item.cartItemId),
  },

  actions: {
    /**
     * 获取用户购物车
     *
     * @returns {Promise<boolean>} 是否成功
     */
    async fetchUserCart(): Promise<boolean> {
      try {
        this.loading = true;
        const response = await cartService.getUserCart();

        if (response.code === HttpStatusCode.Ok) {
          const cartList = response.data as CartList;
          this.items = cartList.items;
          this.totalAmount = cartList.totalAmount;
          this.totalItems = cartList.total;
          return true;
        }
        return false;
      } catch (error) {
        console.error("获取购物车失败：", error);
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 添加商品到购物车
     *
     * @param {Cart} cart 购物车商品信息
     * @returns {Promise<boolean>} 是否成功
     */
    async addToCart(cart: Cart): Promise<boolean> {
      try {
        this.loading = true;
        const response = await cartService.addToCart(cart);

        if (response.code === HttpStatusCode.Ok) {
          await this.fetchUserCart(); // 重新获取购物车，确保数据一致性
          return true;
        }
        return false;
      } catch (error) {
        console.error("添加商品到购物车失败：", error);
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 从购物车删除商品
     *
     * @param {number} cartItemId 购物车商品 ID
     * @returns {Promise<boolean>} 是否成功
     */
    async removeFromCart(cartItemId: number): Promise<boolean> {
      try {
        this.loading = true;
        const response = await cartService.removeFromCart(cartItemId);

        if (response.code === HttpStatusCode.Ok) {
          // 从本地移除商品
          this.items = this.items.filter(
            (item) => item.cartItemId !== cartItemId,
          );
          await this.fetchUserCart(); // 重新获取购物车，确保数据一致性
          ElMessage.success("商品已从购物车移除");
          return true;
        }
        return false;
      } catch (error) {
        console.error("从购物车删除商品失败：", error);
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 更新购物车商品数量
     *
     * @param {number} cartItemId 购物车商品 ID
     * @param {number} quantity 新的商品数量
     * @returns {Promise<boolean>} 是否成功
     */
    async updateQuantity(
      cartItemId: number,
      quantity: number,
    ): Promise<boolean> {
      try {
        this.loading = true;
        const update: QuantityUpdate = { quantity };
        const response = await cartService.updateQuantity(cartItemId, update);

        if (response.code === HttpStatusCode.Ok) {
          await this.fetchUserCart(); // 重新获取购物车，确保数据一致性
          return true;
        }
        return false;
      } catch (error) {
        console.error("更新购物车商品数量失败：", error);
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 结算购物车，创建订单
     *
     * @param {Checkout} checkout 结算信息
     * @returns {Promise<Order | null>} 创建的订单
     */
    async checkout(checkout: Checkout): Promise<Order | null> {
      try {
        this.loading = true;
        const response = await cartService.checkout(checkout);

        if (response.code === HttpStatusCode.Ok && response.data) {
          ElMessage.success("订单创建成功，即将跳转到支付页面");
          return response.data as Order;
        }
        return null;
      } catch (error) {
        console.error("结算购物车失败：", error);
        return null;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 清空购物车
     *
     * @returns {Promise<boolean>} 是否成功
     */
    async clearCart(): Promise<boolean> {
      try {
        this.loading = true;
        const response = await cartService.clearCart();

        if (response.code === HttpStatusCode.Ok) {
          // 清空本地购物车数据
          this.items = [];
          this.totalAmount = 0;
          this.totalItems = 0;
          ElMessage.success("购物车已清空");
          return true;
        }
        return false;
      } catch (error) {
        console.error("清空购物车失败：", error);
        return false;
      } finally {
        this.loading = false;
      }
    },
  },
});
