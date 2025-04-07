package com.tomato.bookstore.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 业务错误代码枚举 规则：前两位表示模块，后三位表示具体错误 */
@Getter
@RequiredArgsConstructor
public enum BusinessErrorCode {
  // 用户模块（10xxx）
  USER_NOT_FOUND(10001, "用户不存在"),
  USERNAME_ALREADY_EXISTS(10002, "用户名已存在"),
  EMAIL_ALREADY_EXISTS(10003, "邮箱已存在"),
  PHONE_ALREADY_EXISTS(10004, "手机号已存在"),
  PASSWORD_INCORRECT(10005, "密码不正确"),
  USER_ACCOUNT_LOCKED(10006, "账户已被锁定"),
  USER_UNAUTHORIZED(10007, "用户未授权"),

  // 商品模块（20xxx）
  PRODUCT_NOT_FOUND(20001, "商品不存在"),
  PRODUCT_OUT_OF_STOCK(20002, "商品库存不足"),
  PRODUCT_OFFLINE(20003, "商品已下架"),
  PRODUCT_PRICE_CHANGED(20004, "商品价格已变更"),
  PRODUCT_TITLE_ALREADY_EXISTS(20005, "商品名称已存在"),
  PRODUCT_STOCK_NOT_FOUND(20006, "商品库存记录不存在"),
  PRODUCT_INVALID_PARAMETER(20007, "商品参数无效"),
  PRODUCT_STOCK_INSUFFICIENT(20008, "商品库存不足"),

  // 购物车模块（30xxx）
  CART_EMPTY(30001, "购物车为空"),
  CART_ITEM_NOT_FOUND(30002, "购物车商品不存在"),
  CART_ITEM_MAX_LIMIT(30003, "已达购买数量上限"),
  CART_ITEM_ADD_FAILED(30004, "添加商品到购物车失败"),
  CART_ITEM_UPDATE_FAILED(30005, "更新购物车商品失败"),
  CART_ITEM_DELETE_FAILED(30006, "删除购物车商品失败"),
  CART_ITEM_INVALID_QUANTITY(30007, "商品数量无效"),
  CART_ITEM_EXCEED_STOCK(30008, "商品数量超过库存"),

  // 订单模块（40xxx）
  ORDER_NOT_FOUND(40001, "订单不存在"),
  ORDER_ALREADY_PAID(40002, "订单已支付"),
  ORDER_ALREADY_CANCELLED(40003, "订单已取消"),
  ORDER_CANNOT_CANCEL(40004, "订单无法取消"),
  ORDER_STATUS_ERROR(40005, "订单状态错误"),
  ORDER_CREATE_FAILED(40006, "创建订单失败"),
  ORDER_PAY_FAILED(40007, "支付订单失败"),
  ORDER_VERIFY_FAILED(40008, "订单验证失败"),
  ORDER_EXPIRED(40009, "订单已过期"),
  ORDER_EMPTY_CART_ITEMS(40010, "未选择任何商品"),
  ORDER_INVALID_PAYMENT_METHOD(40011, "无效的支付方式"),
  ORDER_INVALID_ADDRESS(40012, "无效的收货地址"),
  ORDER_NOTIFY_VERIFY_FAILED(40013, "支付通知验证失败"),
  ORDER_PAYMENT_AMOUNT_ERROR(40014, "支付金额错误"),

  // 书评模块（50xxx）
  REVIEW_NOT_FOUND(50001, "书评不存在"),
  REVIEW_ALREADY_EXISTS(50002, "已发表过书评"),
  REVIEW_CONTENT_INVALID(50003, "书评内容不符合规范"),

  // 笔记模块（60xxx）
  NOTE_NOT_FOUND(60001, "笔记不存在"),
  NOTE_CONTENT_INVALID(60002, "笔记内容不符合规范"),

  // 系统通用（90xxx）
  SYSTEM_ERROR(90001, "系统错误"),
  OPERATION_TOO_FREQUENT(90002, "操作过于频繁"),
  INVALID_PARAMETER(90003, "参数无效"),
  ACCESS_DENIED(90004, "访问被拒绝");

  private final int code;
  private final String message;
}
