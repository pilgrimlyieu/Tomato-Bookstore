package com.tomato.bookstore.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 支付方式枚举类 */
@Getter
@AllArgsConstructor
public enum PaymentMethod {
  ALIPAY("支付宝");

  private final String description;
}
