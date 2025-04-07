package com.tomato.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 支付通知 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotifyDTO {
  private Long orderId;
  private String status;
  private String tradeNo;
  private LocalDateTime paymentTime;
  private BigDecimal totalAmount;
}
