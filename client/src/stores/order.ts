import orderService from "@/services/order-service";
import type { Order, Payment } from "@/types/order";
import { performAsync, performAsyncAction } from "@/utils/asyncHelper";
import { HttpStatusCode } from "axios";
import { defineStore } from "pinia";

/**
 * 订单状态管理
 */
export const useOrderStore = defineStore("order", {
  state: () => ({
    currentOrder: null as Order | null, // 当前订单
    payment: null as Payment | null, // 支付信息
    orderList: [] as Order[], // 订单列表
    loading: false, // 加载状态
  }),

  actions: {
    /**
     * 获取订单详情
     *
     * @param {number} orderId 订单 ID
     * @returns {Promise<boolean>} 是否成功
     */
    async fetchOrderById(orderId: number): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => orderService.getOrder(orderId),
        (data) => {
          this.currentOrder = data;
        },
        `获取订单详情失败（ID: ${orderId}）：`,
        true,
      );
    },

    /**
     * 支付订单
     *
     * @param {number} orderId 订单 ID
     * @returns {Promise<Payment | null>} 支付信息
     */
    async payOrder(orderId: number): Promise<Payment | null> {
      return await performAsync<Payment, Payment>(
        () => orderService.payOrder(orderId),
        (data) => {
          this.payment = data;
          return data;
        },
        `支付订单失败（ID: ${orderId}）：`,
        true,
        (loading) => (this.loading = loading),
        (loading) => (this.loading = loading),
      );
    },

    /**
     * 取消订单
     *
     * @param {number} orderId 订单 ID
     * @returns {Promise<boolean>} 是否成功
     */
    async cancelOrder(orderId: number): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => orderService.cancelOrder(orderId),
        async () => {
          // 如果是当前订单，则刷新订单状态
          if (this.currentOrder && this.currentOrder.orderId === orderId) {
            await this.fetchOrderById(orderId);
          }
        },
        `取消订单失败（ID: ${orderId}）`,
        true,
        [HttpStatusCode.Ok],
        "订单已取消",
      );
    },

    /**
     * 获取用户的订单列表
     *
     * @returns {Promise<boolean>} 是否成功
     */
    async fetchOrderList(): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => orderService.getOrderList(),
        (data) => {
          this.orderList = data;
        },
        "获取订单列表失败：",
        true,
      );
    },

    /**
     * 清除当前订单数据
     */
    clearCurrentOrder(): void {
      this.currentOrder = null;
      this.payment = null;
    },
  },
});
