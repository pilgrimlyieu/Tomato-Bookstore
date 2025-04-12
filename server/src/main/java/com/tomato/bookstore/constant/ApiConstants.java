package com.tomato.bookstore.constant;

/** API 常量类 */
public class ApiConstants {
  // 基础路径
  public static final String HOME = "/";

  // 用户相关路径
  public static final String USER = "/user";
  public static final String USER_REGISTER = "/register";
  public static final String USER_LOGIN = "/login";
  public static final String USER_PROFILE = "/profile";

  // 完整用户路径
  public static final String USER_REGISTER_PATH = USER + USER_REGISTER;
  public static final String USER_LOGIN_PATH = USER + USER_LOGIN;
  public static final String USER_PROFILE_PATH = USER + USER_PROFILE;

  // 商品相关路径
  public static final String PRODUCTS = "/products";
  public static final String PRODUCT_DETAIL = "/{id}";
  public static final String STOCKPILE = "/stockpile";
  public static final String STOCKPILE_DETAIL = "/stockpile/{productId}";

  // 完整商品路径
  public static final String PRODUCT_BASE_PATH = PRODUCTS;
  public static final String PRODUCT_DETAIL_PATH = PRODUCTS + PRODUCT_DETAIL;
  public static final String PRODUCT_STOCKPILE_BASE_PATH = PRODUCTS + STOCKPILE;
  public static final String PRODUCT_STOCKPILE_DETAIL_PATH = PRODUCTS + STOCKPILE_DETAIL;

  // 购物车相关路径
  public static final String CART = "/cart";
  public static final String CART_ITEM = "/{cartItemId}";
  public static final String CART_CHECKOUT = "/checkout";

  // 完整购物车路径
  public static final String CART_BASE_PATH = CART;
  public static final String CART_ITEM_PATH = CART + CART_ITEM;
  public static final String CART_CHECKOUT_PATH = CART + CART_CHECKOUT;

  // 订单相关路径
  public static final String ORDERS = "/orders";
  public static final String ORDER_DETAIL = "/{orderId}";
  public static final String ORDER_PAY = "/{orderId}/pay";
  public static final String ORDER_NOTIFY = "/notify";
  public static final String ORDER_RETURN = "/return";

  // 完整订单路径
  public static final String ORDER_BASE_PATH = ORDERS;
  public static final String ORDER_DETAIL_PATH = ORDERS + ORDER_DETAIL;
  public static final String ORDER_PAY_PATH = ORDERS + ORDER_PAY;
  public static final String ORDER_NOTIFY_PATH = ORDERS + ORDER_NOTIFY;
  public static final String ORDER_RETURN_PATH = ORDERS + ORDER_RETURN;

  // 广告相关路径
  public static final String ADVERTISEMENTS = "/advertisements";
  public static final String ADVERTISEMENT_DETAIL = "/{id}";

  // 完整广告路径
  public static final String ADVERTISEMENT_BASE_PATH = ADVERTISEMENTS;
  public static final String ADVERTISEMENT_DETAIL_PATH = ADVERTISEMENTS + ADVERTISEMENT_DETAIL;
}
