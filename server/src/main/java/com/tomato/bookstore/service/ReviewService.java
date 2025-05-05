package com.tomato.bookstore.service;

import com.tomato.bookstore.dto.ReviewCreateDTO;
import com.tomato.bookstore.dto.ReviewDTO;
import java.util.List;

/** 书评服务接口 */
public interface ReviewService {
  /**
   * 获取指定商品的所有书评
   *
   * @param productId 商品 ID
   * @return 书评列表
   */
  List<ReviewDTO> getReviewsByProductId(Long productId);

  /**
   * 获取用户的所有书评
   *
   * @param userId 用户 ID
   * @return 书评列表
   */
  List<ReviewDTO> getReviewsByUserId(Long userId);

  /**
   * 创建书评
   *
   * @param productId 商品 ID
   * @param userId 用户 ID
   * @param reviewCreateDTO 书评创建 DTO
   * @return 创建的书评
   */
  ReviewDTO createReview(Long productId, Long userId, ReviewCreateDTO reviewCreateDTO);

  /**
   * 更新书评
   *
   * @param reviewId 书评 ID
   * @param userId 用户 ID
   * @param reviewCreateDTO 书评创建 DTO
   * @return 更新后的书评
   */
  ReviewDTO updateReview(Long reviewId, Long userId, ReviewCreateDTO reviewCreateDTO);

  /**
   * 管理员更新书评
   *
   * @param reviewId 书评 ID
   * @param reviewCreateDTO 书评创建 DTO
   * @return 更新后的书评
   */
  ReviewDTO updateReviewByAdmin(Long reviewId, ReviewCreateDTO reviewCreateDTO);

  /**
   * 删除书评
   *
   * @param reviewId 书评 ID
   * @param userId 用户 ID
   */
  void deleteReview(Long reviewId, Long userId);

  /**
   * 管理员删除书评
   *
   * @param reviewId 书评 ID
   */
  void deleteReviewByAdmin(Long reviewId);

  /**
   * 计算产品的平均评分
   *
   * @param productId 商品 ID
   * @return 平均评分
   */
  Double calculateProductRating(Long productId);
}
