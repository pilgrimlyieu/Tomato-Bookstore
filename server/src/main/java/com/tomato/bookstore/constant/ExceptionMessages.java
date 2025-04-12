package com.tomato.bookstore.constant;

/**
 * 异常信息常量类
 *
 * <p>统一管理系统中使用的异常信息文本，便于维护和国际化
 */
public class ExceptionMessages {
  // 商品模块异常
  public static final String PRODUCT_NOT_FOUND = "商品不存在，ID：%s";
  public static final String PRODUCT_TITLE_EXISTS = "商品名称已存在：%s";
  public static final String PRODUCT_TITLE_USED_BY_OTHER = "商品名称已被其他商品使用：%s";
  public static final String PRODUCT_STOCK_NOT_FOUND = "商品库存记录不存在，商品 ID：%s";

  // 用户模块异常
  public static final String USER_NOT_FOUND = "用户不存在，ID：%s";
  public static final String USER_NOT_FOUND_BY_USERNAME = "用户不存在：%s";

  // 购物车模块异常
  public static final String CART_ITEM_NOT_FOUND = "购物车商品不存在，ID：%s";
  public static final String CART_ITEM_EXCEED_STOCK = "商品「%s」库存不足，当前库存：%d，请求数量：%d";
  public static final String CART_ITEM_INVALID_QUANTITY = "商品数量必须大于 0";

  // 订单模块异常
  public static final String ORDER_NOT_FOUND = "订单不存在，ID：%s";
  public static final String ORDER_ALREADY_PAID = "订单已支付，ID：%s";
  public static final String ORDER_STATUS_ERROR = "订单状态错误，当前状态：%s，期望状态：%s";
  public static final String ORDER_EMPTY_CART_ITEMS = "未选择任何商品";
  public static final String ORDER_INVALID_PAYMENT_METHOD = "不支持的支付方式：%s";
  public static final String ORDER_INVALID_ADDRESS = "收货地址不能为空";

  // 系统通用异常
  public static final String UNAUTHORIZED = "未授权访问";
  public static final String ACCESS_DENIED = "无权限执行此操作";

  // 广告模块异常
  public static final String ADVERTISEMENT_NOT_FOUND = "广告不存在，ID：%s";
  public static final String ADVERTISEMENT_TITLE_DUPLICATE = "广告标题已存在：%s";
}
