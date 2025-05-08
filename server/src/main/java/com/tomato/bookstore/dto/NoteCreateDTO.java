package com.tomato.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 读书笔记创建 DTO 类，用于创建或更新笔记 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteCreateDTO {
  @NotBlank(message = "笔记标题不能为空")
  @Size(min = 2, max = 100, message = "笔记标题长度必须在 2-100 之间")
  private String title;

  @NotBlank(message = "笔记内容不能为空")
  @Size(min = 10, message = "笔记内容不能少于 10 个字符")
  private String content;
}
