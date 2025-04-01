package com.tomato.bookstore.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 商品 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
  private Long id;

  @NotBlank(message = "商品名称不能为空")
  private String title;

  @NotNull(message = "商品价格不能为空")
  @Min(value = 0, message = "商品价格不能低于 0 元")
  @Digits(integer = 10, fraction = 2, message = "价格最多支持两位小数")
  private BigDecimal price;

  @NotNull(message = "商品评分不能为空")
  @Min(value = 0, message = "商品评分最低为 0 分")
  @Max(value = 10, message = "商品评分最高为 10 分")
  private Integer rate;

  private String description;
  private String cover;
  private String detail;

  private List<SpecificationDTO> specifications;
}
