package com.tomato.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 商品规格数据传输对象 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecificationDTO {
  private Long id;

  @NotBlank(message = "规格名称不能为空")
  private String item;

  @NotBlank(message = "规格值不能为空")
  private String value;

  private Long productId;
}
