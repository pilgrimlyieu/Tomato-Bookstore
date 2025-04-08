import type { Cart } from "./cart";

/**
 * 支付方式枚举
 */
export enum PaymentMethod {
  ALIPAY = "ALIPAY",
}

/**
 * 订单状态枚举
 */
export enum OrderStatus {
  PENDING = "PENDING", // 待支付
  PAID = "PAID", // 已支付
  CANCELLED = "CANCELLED", // 已取消
  TIMEOUT = "TIMEOUT", // 已超时
}

/**
 * 订单信息
 */
export interface Order {
  orderId?: number;
  userId?: number;
  totalAmount: number;
  paymentMethod: PaymentMethod;
  status: OrderStatus;
  shippingAddress: string;
  tradeNo?: string;
  paymentTime?: string;
  createTime?: string;
  updateTime?: string;
  orderItems: Cart[];
}

/**
 * 支付信息
 */
export interface Payment {
  paymentForm: string;
  orderId: number;
  totalAmount: number;
  paymentMethod: PaymentMethod;
}
