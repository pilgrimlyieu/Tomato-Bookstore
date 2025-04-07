package com.tomato.bookstore.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 订单状态枚举类 */
@Getter
@AllArgsConstructor
public enum OrderStatus {
  PENDING("待支付"),

  PAID("已支付"),

  CANCELLED("已取消"),

  TIMEOUT("已超时");

  private final String description;
}
