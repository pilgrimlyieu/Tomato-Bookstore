package com.tomato.bookstore.repository;

import com.tomato.bookstore.model.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 书评仓库接口 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
  /**
   * 查询指定商品的所有书评，按创建时间倒序排列
   *
   * @param productId 商品 ID
   * @return 书评列表
   */
  List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

  /**
   * 查询用户发布的所有书评
   *
   * @param userId 用户 ID
   * @return 书评列表
   */
  List<Review> findByUserId(Long userId);

  /**
   * 查询特定用户对特定商品的书评
   *
   * @param productId 商品 ID
   * @param userId 用户 ID
   * @return 书评（可能不存在）
   */
  Optional<Review> findByProductIdAndUserId(Long productId, Long userId);

  /**
   * 检查用户是否已经评论过指定商品
   *
   * @param productId 商品 ID
   * @param userId 用户 ID
   * @return 是否已评论
   */
  boolean existsByProductIdAndUserId(Long productId, Long userId);

  /**
   * 计算商品的平均评分
   *
   * @param productId 商品 ID
   * @return 平均评分（可能为空）
   */
  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId")
  Double calculateAverageRating(@Param("productId") Long productId);
}
