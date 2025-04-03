import { HttpStatusCode } from "axios";

/**
 * 业务错误代码常量
 * 规则：前两位表示模块，后三位表示具体错误
 */
export const BusinessErrorCode = {
  // 用户模块（10xxx）
  USER_NOT_FOUND: 10001,
  USERNAME_ALREADY_EXISTS: 10002,
  EMAIL_ALREADY_EXISTS: 10003,
  PHONE_ALREADY_EXISTS: 10004,
  PASSWORD_INCORRECT: 10005,
  USER_ACCOUNT_LOCKED: 10006,
  USER_UNAUTHORIZED: 10007,

  // 商品模块（20xxx）
  PRODUCT_NOT_FOUND: 20001,
  PRODUCT_OUT_OF_STOCK: 20002,
  PRODUCT_OFFLINE: 20003,
  PRODUCT_PRICE_CHANGED: 20004,

  // 交易模块（30xxx）
  CART_EMPTY: 30001,
  CART_ITEM_NOT_FOUND: 30002,
  CART_ITEM_MAX_LIMIT: 30003,

  // 订单模块（40xxx）
  ORDER_NOT_FOUND: 40001,
  ORDER_ALREADY_PAID: 40002,
  ORDER_ALREADY_CANCELLED: 40003,
  ORDER_CANNOT_CANCEL: 40004,
  ORDER_STATUS_ERROR: 40005,

  // 书评模块（50xxx）
  REVIEW_NOT_FOUND: 50001,
  REVIEW_ALREADY_EXISTS: 50002,
  REVIEW_CONTENT_INVALID: 50003,

  // 笔记模块（60xxx）
  NOTE_NOT_FOUND: 60001,
  NOTE_CONTENT_INVALID: 60002,

  // 系统通用（90xxx）
  SYSTEM_ERROR: 90001,
  OPERATION_TOO_FREQUENT: 90002,
  INVALID_PARAMETER: 90003,
  ACCESS_DENIED: 90004,
} as const;

/**
 * HTTP 状态码对应的错误消息
 */
export const HttpErrorMessages: Record<number, string> = {
  [HttpStatusCode.BadRequest]: "请求参数错误",
  [HttpStatusCode.Unauthorized]: "未授权访问",
  [HttpStatusCode.Forbidden]: "禁止访问",
  [HttpStatusCode.NotFound]: "资源不存在",
  [HttpStatusCode.InternalServerError]: "服务器内部错误",
};

/**
 * 业务错误描述映射
 */
export const BusinessErrorMessages: Record<number, string> = {
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
};

/**
 * 统一的错误消息映射
 */
export const ErrorMessages: Record<number, string> = {
  ...BusinessErrorMessages,
  ...HttpErrorMessages,
};
