import {
  BusinessErrorCode,
  type BusinessErrorCodeType,
} from "@/constants/businessErrorCode";
import {
  HttpStatusCode,
  type HttpStatusCodeType,
} from "@/constants/httpStatusCode";

/**
 * API 响应类型
 */
export interface ApiResponse<T = object | null> {
  /**
   * 响应码
   */
  code: HttpStatusCodeType | BusinessErrorCodeType;

  /**
   * 响应信息
   */
  message: string;

  /**
   * 响应数据
   */
  data: T;
}

/**
 * 错误码和错误信息映射
 */
export const ErrorMessages: Record<number, string> = {
  // 用户模块（10xxx）
  [BusinessErrorCode.USER_NOT_FOUND]: "用户不存在",
  [BusinessErrorCode.USERNAME_ALREADY_EXISTS]: "用户名已存在",
  [BusinessErrorCode.EMAIL_ALREADY_EXISTS]: "邮箱已存在",
  [BusinessErrorCode.PHONE_ALREADY_EXISTS]: "手机号已存在",
  [BusinessErrorCode.PASSWORD_INCORRECT]: "密码不正确",
  [BusinessErrorCode.USER_ACCOUNT_LOCKED]: "账户已被锁定",
  [BusinessErrorCode.USER_UNAUTHORIZED]: "用户未授权",
  // 商品模块（20xxx）
  [BusinessErrorCode.PRODUCT_NOT_FOUND]: "商品不存在",
  [BusinessErrorCode.PRODUCT_OUT_OF_STOCK]: "商品库存不足",
  [BusinessErrorCode.PRODUCT_OFFLINE]: "商品已下架",
  [BusinessErrorCode.PRODUCT_PRICE_CHANGED]: "商品价格已变更",

  // 交易模块（30xxx）
  [BusinessErrorCode.CART_EMPTY]: "购物车为空",
  [BusinessErrorCode.CART_ITEM_NOT_FOUND]: "购物车商品不存在",
  [BusinessErrorCode.CART_ITEM_MAX_LIMIT]: "已达购买数量上限",

  // 订单模块（40xxx）
  [BusinessErrorCode.ORDER_NOT_FOUND]: "订单不存在",
  [BusinessErrorCode.ORDER_ALREADY_PAID]: "订单已支付",
  [BusinessErrorCode.ORDER_ALREADY_CANCELLED]: "订单已取消",
  [BusinessErrorCode.ORDER_CANNOT_CANCEL]: "订单无法取消",
  [BusinessErrorCode.ORDER_STATUS_ERROR]: "订单状态错误",

  // 书评模块（50xxx）
  [BusinessErrorCode.REVIEW_NOT_FOUND]: "书评不存在",
  [BusinessErrorCode.REVIEW_ALREADY_EXISTS]: "已发表过书评",
  [BusinessErrorCode.REVIEW_CONTENT_INVALID]: "书评内容不符合规范",

  // 笔记模块（60xxx）
  [BusinessErrorCode.NOTE_NOT_FOUND]: "笔记不存在",
  [BusinessErrorCode.NOTE_CONTENT_INVALID]: "笔记内容不符合规范",

  // 系统通用（90xxx）
  [BusinessErrorCode.SYSTEM_ERROR]: "系统错误",
  [BusinessErrorCode.OPERATION_TOO_FREQUENT]: "操作过于频繁",
  [BusinessErrorCode.INVALID_PARAMETER]: "参数无效",
  [BusinessErrorCode.ACCESS_DENIED]: "访问被拒绝",

  // HTTP 状态码
  [HttpStatusCode.BAD_REQUEST]: "请求参数错误",
  [HttpStatusCode.UNAUTHORIZED]: "未授权访问",
  [HttpStatusCode.FORBIDDEN]: "禁止访问",
  [HttpStatusCode.NOT_FOUND]: "资源不存在",
  [HttpStatusCode.INTERNAL_SERVER_ERROR]: "服务器内部错误",
};
