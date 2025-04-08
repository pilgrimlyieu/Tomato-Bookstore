import type { PaymentMethod } from "./order";

/**
 * 购物车商品项
 */
export interface Cart {
  cartItemId?: number;
  productId: number;
  title?: string;
  price?: number;
  description?: string;
  cover?: string;
  detail?: string;
  quantity: number;
}

/**
 * 购物车列表
 */
export interface CartList {
  items: Cart[];
  total: number;
  totalAmount: number;
}

/**
 * 购物车数量更新
 */
export interface QuantityUpdate {
  quantity: number;
}

/**
 * 结算请求
 */
export interface Checkout {
  userId?: number;
  cartItemIds: number[];
  shippingAddress: string;
  paymentMethod: PaymentMethod;
}
