import orderService from "@/services/order-service";
import type { Order, Payment } from "@/types/order";
import { HttpStatusCode } from "axios";
import { ElMessage } from "element-plus";
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
      try {
        this.loading = true;
        const response = await orderService.getOrder(orderId);

        if (response.code === HttpStatusCode.Ok) {
          this.currentOrder = response.data;
          return true;
        }
        return false;
      } catch (error) {
        console.error(`获取订单详情失败（ID: ${orderId}）：`, error);
        ElMessage.error("获取订单详情失败");
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 支付订单
     *
     * @param {number} orderId 订单 ID
     * @returns {Promise<Payment | null>} 支付信息
     */
    async payOrder(orderId: number): Promise<Payment | null> {
      try {
        this.loading = true;
        const response = await orderService.payOrder(orderId);

        if (response.code === HttpStatusCode.Ok) {
          this.payment = response.data;
          return this.payment;
        }
        return null;
      } catch (error) {
        console.error(`支付订单失败（ID: ${orderId}）：`, error);
        ElMessage.error("订单支付失败");
        return null;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 取消订单
     *
     * @param {number} orderId 订单 ID
     * @returns {Promise<boolean>} 是否成功
     */
    async cancelOrder(orderId: number): Promise<boolean> {
      try {
        this.loading = true;
        const response = await orderService.cancelOrder(orderId);

        if (response.code === HttpStatusCode.Ok) {
          ElMessage.success("订单已取消");

          // 如果是当前订单，则刷新订单状态
          if (this.currentOrder && this.currentOrder.orderId === orderId) {
            await this.fetchOrderById(orderId);
          }
          return true;
        }
        return false;
      } catch (error) {
        console.error(`取消订单失败（ID: ${orderId}）`, error);
        ElMessage.error("订单取消失败");
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 获取用户的订单列表
     *
     * @returns {Promise<boolean>} 是否成功
     */
    async fetchOrderList(): Promise<boolean> {
      try {
        this.loading = true;
        const response = await orderService.getOrderList();

        if (response.code === HttpStatusCode.Ok) {
          this.orderList = response.data;
          return true;
        }
        return false;
      } catch (error) {
        console.error(`获取订单列表失败：`, error);
        ElMessage.error("获取订单列表失败");
        return false;
      } finally {
        this.loading = false;
      }
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
