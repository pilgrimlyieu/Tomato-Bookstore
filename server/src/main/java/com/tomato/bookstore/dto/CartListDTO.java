package com.tomato.bookstore.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 购物车列表 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartListDTO {
  private List<CartDTO> items;
  private Integer total;
  private BigDecimal totalAmount;
}
