package com.tomato.bookstore.model;

import jakarta.persistence.Entity;
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
 * 读书笔记评论实体类
 *
 * <p>该类对应数据库中的 note_comments 表，用于存储用户对读书笔记的评论信息。
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "note_comments")
public class NoteComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long noteId;

  private Long userId;

  private String content;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
