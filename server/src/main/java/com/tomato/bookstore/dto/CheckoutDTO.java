package com.tomato.bookstore.dto;

import com.tomato.bookstore.constant.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 结账请求 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDTO {
  private Long userId;

  @NotEmpty(message = "请选择要结算的商品")
  private List<Long> cartItemIds;

  @NotBlank(message = "收货地址不能为空")
  private String shippingAddress;

  @NotNull(message = "支付方式不能为空")
  private PaymentMethod paymentMethod;
}
