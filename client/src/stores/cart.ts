import cartService from "@/services/cart-service";
import type { Cart, Checkout, QuantityUpdate } from "@/types/cart";
import type { Order } from "@/types/order";
import { performAsync, performAsyncAction } from "@/utils/asyncHelper";
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
      return await performAsyncAction(
        this,
        "loading",
        () => cartService.getUserCart(),
        (cart) => {
          this.items = cart.items;
          this.totalAmount = cart.totalAmount;
          this.totalItems = cart.total;
        },
        "获取购物车失败：",
        false,
      );
    },

    /**
     * 添加商品到购物车
     *
     * @param {Cart} cart 购物车商品信息
     * @returns {Promise<boolean>} 是否成功
     */
    async addToCart(cart: Cart): Promise<boolean> {
      const result = await performAsyncAction(
        this,
        "loading",
        () => cartService.addToCart(cart),
        () => {},
        "添加商品到购物车失败：",
        false,
      );

      if (result) {
        await this.fetchUserCart(); // 重新获取购物车，确保数据一致性
        return true;
      }
      return false;
    },

    /**
     * 从购物车删除商品
     *
     * @param {number} cartItemId 购物车商品 ID
     * @returns {Promise<boolean>} 是否成功
     */
    async removeFromCart(cartItemId: number): Promise<boolean> {
      const result = await performAsyncAction(
        this,
        "loading",
        () => cartService.removeFromCart(cartItemId),
        () => {
          // 从本地移除商品
          this.items = this.items.filter(
            (item) => item.cartItemId !== cartItemId,
          );
        },
        "从购物车删除商品失败：",
        false,
        [HttpStatusCode.Ok],
        "商品已从购物车移除",
      );

      if (result) {
        await this.fetchUserCart(); // 重新获取购物车，确保数据一致性
        return true;
      }
      return false;
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
      const update: QuantityUpdate = { quantity };
      const result = await performAsyncAction(
        this,
        "loading",
        () => cartService.updateQuantity(cartItemId, update),
        () => {},
        "更新购物车商品数量失败：",
        false,
      );

      if (result) {
        await this.fetchUserCart(); // 重新获取购物车，确保数据一致性
        return true;
      }
      return false;
    },

    /**
     * 结算购物车，创建订单
     *
     * @param {Checkout} checkout 结算信息
     * @returns {Promise<Order | null>} 创建的订单
     */
    async checkout(checkout: Checkout): Promise<Order | null> {
      return await performAsync<Order, Order | null>(
        () => cartService.checkout(checkout),
        (data) => {
          ElMessage.success("订单创建成功，即将跳转到支付页面");
          return data;
        },
        "结算购物车失败：",
        true,
        (loading) => (this.loading = loading),
        (loading) => (this.loading = loading),
      );
    },

    /**
     * 清空购物车
     *
     * @returns {Promise<boolean>} 是否成功
     */
    async clearCart(): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => cartService.clearCart(),
        () => {
          // 清空本地购物车数据
          this.items = [];
          this.totalAmount = 0;
          this.totalItems = 0;
        },
        "清空购物车失败：",
        true,
        [HttpStatusCode.Ok],
        "购物车已清空",
      );
    },
  },
});
