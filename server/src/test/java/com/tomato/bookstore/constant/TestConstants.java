package com.tomato.bookstore.constant;

import java.math.BigDecimal;

/**
 * 测试常量类
 *
 * <p>用于测试中的常量值
 */
public class TestConstants {
  // 用户模块常量
  public static final String TEST_USERNAME = "testuser";
  public static final String TEST_PASSWORD = "password123";
  public static final String TEST_EMAIL = "test@example.com";
  public static final String TEST_PHONE = "13800138000";

  // 商品模块常量
  public static final String TEST_PRODUCT_TITLE = "测试商品";
  public static final String TEST_PRODUCT_AUTHOR = "测试作者";
  public static final String TEST_PRODUCT_PUBLISHER = "测试出版社";
  public static final String TEST_PRODUCT_ISBN = "9787123456789";
  public static final String TEST_PRODUCT_LANGUAGE = "中文";
  public static final BigDecimal TEST_PRODUCT_PRICE = new BigDecimal("49.90");
  public static final float TEST_PRODUCT_DISCOUNT = 0.9f;
  public static final String TEST_PRODUCT_COVER = "cover-url.jpg";
  public static final String TEST_PRODUCT_DESCRIPTION = "这是一个测试商品描述";
  public static final Integer TEST_PRODUCT_RATE = 8;
  public static final String TEST_PRODUCT_DETAIL = "<p>详细描述</p>";

  // 商品规格常量
  public static final String TEST_SPEC_ITEM = "出版年份";
  public static final String TEST_SPEC_VALUE = "2023";

  // 库存常量
  public static final int TEST_STOCK_AMOUNT = 100;
  public static final int TEST_STOCK_FROZEN = 0;

  // 管理员账号
  public static final String TEST_ADMIN_USERNAME = "admin";
  public static final String TEST_ADMIN_PASSWORD = "password";

  // 错误信息
  public static final String PRODUCT_NOT_FOUND_MESSAGE = "商品不存在";
  public static final String PRODUCT_STOCK_NOT_FOUND_MESSAGE = "商品库存记录不存在";

  // 购物车模块常量
  public static final String TEST_CART_USERNAME = "cartuser";

  // 订单模块常量
  public static final String TEST_ORDER_USERNAME = "orderuser";
  public static final String TEST_SHIPPING_ADDRESS = "测试地址，测试街道 123 号";
  public static final Integer TEST_ORDER_QUANTITY = 3;
  public static final String TEST_ALIPAY_TRADE_NO = "2023112822001123456789";

  // 广告模块常量
  public static final String TEST_ADVERTISEMENT_TITLE = "测试广告";
  public static final String TEST_ADVERTISEMENT_CONTENT = "这是一条测试广告内容";
  public static final String TEST_ADVERTISEMENT_IMAGE_URL = "https://example.com/test-ad-image.jpg";
  public static final String TEST_ADVERTISEMENT_PRODUCT_NAME = "广告测试商品";

  // 测试用户名
  public static final String TEST_CUSTOMER_USERNAME = "customer";
}
