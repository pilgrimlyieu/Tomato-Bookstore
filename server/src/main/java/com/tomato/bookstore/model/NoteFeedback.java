package com.tomato.bookstore.model;

import com.tomato.bookstore.constant.FeedbackType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 读书笔记反馈实体类
 *
 * <p>该类对应数据库中的 note_feedbacks 表，用于存储用户对读书笔记的反馈信息（点赞/点踩）。
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "note_feedbacks")
public class NoteFeedback {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long noteId;

  private Long userId;

  @Enumerated(EnumType.STRING)
  private FeedbackType feedbackType;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
