package com.tomato.bookstore.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 读书笔记评论 DTO 类，用于返回评论信息 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteCommentDTO {
  private Long id;
  private Long noteId;
  private Long userId;
  private String username;
  private String userAvatar;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
