import type { ApiResponse } from "@/types/api";
import type { Order, Payment } from "@/types/order";
import apiClient from "@/utils/apiClient";

/**
 * 订单服务
 */
export default {
  /**
   * 获取用户订单列表
   *
   * @returns {Promise<ApiResponse<Order[]>>} 订单列表
   */
  getOrderList(): Promise<ApiResponse<Order[]>> {
    return apiClient.get("/orders");
  },

  /**
   * 获取订单详情
   *
   * @param {number} orderId 订单 ID
   * @returns {Promise<ApiResponse<Order>>} 订单详情
   */
  getOrder(orderId: number): Promise<ApiResponse<Order>> {
    return apiClient.get(`/orders/${orderId}`);
  },

  /**
   * 支付订单
   *
   * @param {number} orderId 订单 ID
   * @returns {Promise<ApiResponse<Payment>>} 支付信息
   */
  payOrder(orderId: number): Promise<ApiResponse<Payment>> {
    return apiClient.post(`/orders/${orderId}/pay`);
  },

  /**
   * 取消订单
   *
   * @param {number} orderId 订单 ID
   * @returns {Promise<ApiResponse<string>>} 操作结果
   */
  cancelOrder(orderId: number): Promise<ApiResponse<string>> {
    return apiClient.delete(`/orders/${orderId}`);
  },
};
