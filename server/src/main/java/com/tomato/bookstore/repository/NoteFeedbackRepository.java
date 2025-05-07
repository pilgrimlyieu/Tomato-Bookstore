package com.tomato.bookstore.repository;

import com.tomato.bookstore.constant.FeedbackType;
import com.tomato.bookstore.model.NoteFeedback;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 读书笔记反馈仓库接口 */
@Repository
public interface NoteFeedbackRepository extends JpaRepository<NoteFeedback, Long> {
  /**
   * 根据笔记 ID 和用户 ID 查找反馈
   *
   * @param noteId 笔记 ID
   * @param userId 用户 ID
   * @return 反馈（可选）
   */
  Optional<NoteFeedback> findByNoteIdAndUserId(Long noteId, Long userId);

  /**
   * 检查用户是否已经对某笔记进行过反馈
   *
   * @param noteId 笔记 ID
   * @param userId 用户 ID
   * @return 是否存在
   */
  boolean existsByNoteIdAndUserId(Long noteId, Long userId);

  /**
   * 统计某笔记的点赞数
   *
   * @param noteId 笔记 ID
   * @return 点赞数
   */
  long countByNoteIdAndFeedbackType(Long noteId, FeedbackType feedbackType);

  /**
   * 获取笔记的点赞和点踩数
   *
   * @param noteId 笔记 ID
   * @return 包含点赞数和点踩数的对象数组
   */
  @Query(
      "SELECT f.feedbackType, COUNT(f) FROM NoteFeedback f WHERE f.noteId = :noteId GROUP BY f.feedbackType")
  Object[][] countFeedbacksByNoteId(@Param("noteId") Long noteId);
}
