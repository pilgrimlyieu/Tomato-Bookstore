package com.tomato.bookstore.repository;

import com.tomato.bookstore.model.Note;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 读书笔记仓库接口 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
  /**
   * 根据用户 ID 查找所有笔记
   *
   * @param userId 用户 ID
   * @return 笔记列表
   */
  List<Note> findByUserIdOrderByCreatedAtDesc(Long userId);

  /**
   * 根据商品 ID 查找所有笔记
   *
   * @param productId 商品 ID
   * @return 笔记列表
   */
  List<Note> findByProductIdOrderByCreatedAtDesc(Long productId);

  /**
   * 根据商品 ID 和用户 ID 查找笔记
   *
   * @param productId 商品 ID
   * @param userId 用户 ID
   * @return 笔记（可选）
   */
  Optional<Note> findByProductIdAndUserId(Long productId, Long userId);

  /**
   * 检查用户是否已经为某商品创建过笔记
   *
   * @param productId 商品 ID
   * @param userId 用户 ID
   * @return 是否存在
   */
  boolean existsByProductIdAndUserId(Long productId, Long userId);
}
