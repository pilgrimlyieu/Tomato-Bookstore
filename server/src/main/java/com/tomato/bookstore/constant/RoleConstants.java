package com.tomato.bookstore.constant;

/** 角色常量类 */
public class RoleConstants {
  public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  public static final String HAS_ROLE_ADMIN = "hasRole('ADMIN')";
  public static final String HAS_ANY_ROLE = "hasAnyRole('ADMIN', 'CUSTOMER')";
}
