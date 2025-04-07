package com.tomato.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 购物车数量更新 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuantityUpdateDTO {
  @NotNull(message = "商品数量不能为空")
  @Min(value = 1, message = "商品数量必须大于 0")
  private Integer quantity;
}
