package com.tomato.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 购物车商品 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
  private Long cartItemId;

  @NotNull(message = "商品 ID 不能为空")
  private Long productId;

  private String title;
  private BigDecimal price;
  private String description;
  private String cover;
  private String detail;

  @NotNull(message = "商品数量不能为空")
  @Min(value = 1, message = "商品数量必须大于 0")
  private Integer quantity;
}
