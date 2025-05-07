package com.tomato.bookstore.dto;

import com.tomato.bookstore.constant.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 读书笔记反馈 DTO 类，用于创建或更新反馈 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteFeedbackDTO {
  private FeedbackType feedbackType;
}
