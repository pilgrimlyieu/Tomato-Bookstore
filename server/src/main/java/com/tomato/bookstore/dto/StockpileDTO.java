package com.tomato.bookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 商品库存数据传输对象 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockpileDTO {
  private Long id;

  @NotNull(message = "可售库存量不能为空")
  @Min(value = 0, message = "可售库存量不能小于 0")
  private Integer amount;

  @NotNull(message = "冻结库存量不能为空")
  @Min(value = 0, message = "冻结库存量不能小于 0")
  private Integer frozen;

  private Long productId;
}
