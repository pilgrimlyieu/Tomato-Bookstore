package com.tomato.bookstore.repository;

import com.tomato.bookstore.constant.FeedbackType;
import com.tomato.bookstore.model.NoteFeedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 读书笔记反馈仓库接口 */
@Repository
public interface NoteFeedbackRepository extends JpaRepository<NoteFeedback, Long> {
  /** 反馈数量统计投影接口 */
  interface NoteFeedbackCountProjection {
    Long getNoteId();

    Long getCount();
  }

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
   * 批量统计多个笔记的指定反馈类型数量
   *
   * @param noteIds 笔记 ID 列表
   * @param feedbackType 反馈类型
   * @return 笔记 ID 和对应的反馈数量的投影对象列表
   */
  @Query(
      "SELECT f.noteId AS noteId, COUNT(f) AS count FROM NoteFeedback f WHERE f.noteId IN :noteIds AND f.feedbackType = :feedbackType GROUP BY f.noteId")
  List<NoteFeedbackCountProjection> countByNoteIdsAndFeedbackType(
      @Param("noteIds") List<Long> noteIds, @Param("feedbackType") FeedbackType feedbackType);
}
