package com.tomato.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 广告 DTO 类 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDTO {
  private Long id;

  @NotBlank(message = "广告标题不能为空")
  @Size(max = 50, message = "广告标题不能超过 50 个字符")
  private String title;

  @NotBlank(message = "广告内容不能为空")
  @Size(max = 500, message = "广告内容不能超过 500 个字符")
  private String content;

  @NotBlank(message = "广告图片 URL 不能为空")
  @Size(max = 500, message = "广告图片 URL 不能超过 500 个字符")
  private String imageUrl;

  @NotNull(message = "商品 ID 不能为空")
  private Long productId;
}
