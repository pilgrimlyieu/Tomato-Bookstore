package com.tomato.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 读书笔记评论创建 DTO 类，用于创建评论 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteCommentCreateDTO {
  @NotBlank(message = "评论内容不能为空")
  @Size(min = 1, max = 500, message = "评论内容长度必须在 1-500 之间")
  private String content;
}
