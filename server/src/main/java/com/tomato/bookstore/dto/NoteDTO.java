package com.tomato.bookstore.dto;

import com.tomato.bookstore.constant.FeedbackType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 读书笔记 DTO 类，用于返回笔记信息 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
  private Long id;
  private String title;
  private String content;
  private Long productId;
  private String productTitle;
  private Long userId;
  private String username;
  private String userAvatar;
  private int likeCount;
  private int dislikeCount;
  private int commentCount;
  private FeedbackType userFeedback; // null 表示用户没有反馈
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
