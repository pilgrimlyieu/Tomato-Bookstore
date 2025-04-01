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

  // 系统通用异常
  public static final String UNAUTHORIZED = "未授权访问";
  public static final String ACCESS_DENIED = "无权限执行此操作";
}
