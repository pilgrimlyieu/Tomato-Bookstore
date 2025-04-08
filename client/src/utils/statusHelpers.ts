import { OrderStatus, PaymentMethod } from "@/types/order";

/**
 * 获取订单状态显示文本
 *
 * @param {OrderStatus} status 订单状态枚举
 * @returns {string} 订单状态的中文显示文本
 */
export const getOrderStatusText = (status: OrderStatus): string => {
  switch (status) {
    case OrderStatus.PENDING:
      return "待支付";
    case OrderStatus.PAID:
      return "已支付";
    case OrderStatus.CANCELLED:
      return "已取消";
    case OrderStatus.TIMEOUT:
      return "已超时";
    default:
      return "未知状态";
  }
};

/**
 * 获取订单状态对应的 Element Plus 标签类型
 *
 * @param {OrderStatus} status 订单状态枚举
 * @returns { "warning" | "success" | "info" | "danger" | "primary" } 对应的 Element Plus 标签类型
 */
export const getOrderStatusType = (
  status: OrderStatus,
): "warning" | "success" | "info" | "danger" | "primary" => {
  switch (status) {
    case OrderStatus.PENDING:
      return "warning";
    case OrderStatus.PAID:
      return "success";
    case OrderStatus.CANCELLED:
    case OrderStatus.TIMEOUT:
      return "info";
    default:
      return "info";
  }
};

/**
 * 获取支付方式显示文本
 *
 * @param {PaymentMethod} method 支付方式枚举
 * @returns {string} 支付方式的中文显示文本
 */
export const getPaymentMethodText = (method: PaymentMethod): string => {
  switch (method) {
    case PaymentMethod.ALIPAY:
      return "支付宝";
    default:
      return "未知支付方式";
  }
};
