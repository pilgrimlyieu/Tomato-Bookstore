import type { ApiResponse } from "@/types/api";
import type { Cart, CartList, Checkout, QuantityUpdate } from "@/types/cart";
import type { Order } from "@/types/order";
import apiClient from "@/utils/apiClient";

/**
 * 购物车服务
 */
export default {
  /**
   * 获取用户购物车
   *
   * @returns {Promise<ApiResponse<CartList>>} 购物车信息
   */
  getUserCart(): Promise<ApiResponse<CartList>> {
    return apiClient.get("/cart");
  },

  /**
   * 添加商品到购物车
   *
   * @param {Cart} cart 购物车商品信息
   * @returns {Promise<ApiResponse<Cart>>} 添加的商品信息
   */
  addToCart(cart: Cart): Promise<ApiResponse<Cart>> {
    return apiClient.post("/cart", cart);
  },

  /**
   * 从购物车中删除商品
   *
   * @param {number} cartItemId 购物车商品 ID
   * @returns {Promise<ApiResponse<string>>} 操作结果
   */
  removeFromCart(cartItemId: number): Promise<ApiResponse<string>> {
    return apiClient.delete(`/cart/${cartItemId}`);
  },

  /**
   * 更新购物车商品数量
   *
   * @param {number} cartItemId 购物车商品 ID
   * @param {QuantityUpdate} update 更新信息
   * @returns {Promise<ApiResponse<string>>} 操作结果
   */
  updateQuantity(
    cartItemId: number,
    update: QuantityUpdate,
  ): Promise<ApiResponse<string>> {
    return apiClient.patch(`/cart/${cartItemId}`, update);
  },

  /**
   * 结算购物车
   *
   * @param {Checkout} checkout 结算信息
   * @returns {Promise<ApiResponse<Order>>} 创建的订单信息
   */
  checkout(checkout: Checkout): Promise<ApiResponse<Order>> {
    return apiClient.post("/cart/checkout", checkout);
  },

  /**
   * 清空购物车
   *
   * @returns {Promise<ApiResponse<string>>} 操作结果
   */
  clearCart(): Promise<ApiResponse<string>> {
    return apiClient.delete("/cart");
  },
};
