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
}
