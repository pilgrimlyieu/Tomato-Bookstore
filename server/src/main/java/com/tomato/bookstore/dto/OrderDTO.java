package com.tomato.bookstore.dto;

import com.tomato.bookstore.constant.OrderStatus;
import com.tomato.bookstore.constant.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 订单 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
  private Long orderId;

  private Long userId;

  private BigDecimal totalAmount;

  private PaymentMethod paymentMethod;

  private OrderStatus status;

  private String shippingAddress;

  private String tradeNo;

  private LocalDateTime paymentTime;

  private LocalDateTime createTime;

  private LocalDateTime updateTime;

  private List<CartDTO> orderItems;
}
