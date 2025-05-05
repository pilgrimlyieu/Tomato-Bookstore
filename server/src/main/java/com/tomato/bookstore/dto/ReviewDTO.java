package com.tomato.bookstore.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 书评 DTO 类，用于返回书评信息 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
  private Long id;
  private Long productId;
  private Long userId;
  private String username;
  private String userAvatar;
  private Integer rating;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
