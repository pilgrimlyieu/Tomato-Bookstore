package com.tomato.bookstore.repository;

import com.tomato.bookstore.model.NoteComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 读书笔记评论仓库接口 */
@Repository
public interface NoteCommentRepository extends JpaRepository<NoteComment, Long> {
  /**
   * 根据笔记 ID 查找所有评论，按创建时间倒序排序
   *
   * @param noteId 笔记 ID
   * @return 评论列表
   */
  List<NoteComment> findByNoteIdOrderByCreatedAtDesc(Long noteId);

  /**
   * 删除指定笔记的所有评论
   *
   * @param noteId 笔记 ID
   */
  void deleteByNoteId(Long noteId);

  /**
   * 统计笔记的评论数
   *
   * @param noteId 笔记 ID
   * @return 评论数
   */
  long countByNoteId(Long noteId);
}
