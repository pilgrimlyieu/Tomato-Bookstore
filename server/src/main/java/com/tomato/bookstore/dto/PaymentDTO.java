package com.tomato.bookstore.dto;

import com.tomato.bookstore.constant.PaymentMethod;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 支付 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
  private String paymentForm;
  private Long orderId;
  private BigDecimal totalAmount;
  private PaymentMethod paymentMethod;
}
