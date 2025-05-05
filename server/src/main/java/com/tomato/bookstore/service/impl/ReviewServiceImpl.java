package com.tomato.bookstore.service.impl;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.dto.ReviewCreateDTO;
import com.tomato.bookstore.dto.ReviewDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.Review;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.repository.ReviewRepository;
import com.tomato.bookstore.repository.UserRepository;
import com.tomato.bookstore.service.ReviewService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 书评服务实现类 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  private static final int BASE_RATING = 8; // 基准评分
  private static final int BASE_RATING_WEIGHT = 1;
  private static final int USER_RATING_WEIGHT = 2;
  private static final int TOTAL_WEIGHT = BASE_RATING_WEIGHT + USER_RATING_WEIGHT;

  @Override
  public List<ReviewDTO> getReviewsByProductId(Long productId) {
    // 确认商品存在
    getProductById(productId);

    List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);

    // 获取所有评论的 userId 列表
    List<Long> userIds = reviews.stream().map(Review::getUserId).distinct().collect(Collectors.toList());

    // 一次性获取所有用户信息
    List<User> users = userRepository.findAllById(userIds);

    // 构建 userId 到 User 的映射
    Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

    // 转换为 DTO
    return reviews.stream().map(review -> convertToDTO(review, userMap)).collect(Collectors.toList());
  }


  @Override
  public List<ReviewDTO> getReviewsByUserId(Long userId) {
    // 确认用户存在
    getUserById(userId);

    List<Review> reviews = reviewRepository.findByUserId(userId);

    // 转换为 DTO
    return reviews.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ReviewDTO createReview(Long productId, Long userId, ReviewCreateDTO reviewCreateDTO) {
    // 检查商品是否存在
    Product product = getProductById(productId);

    // 检查用户是否存在
    getUserById(userId);

    // 检查用户是否已经评论过该商品
    if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
      throw new BusinessException(BusinessErrorCode.REVIEW_ALREADY_EXISTS);
    }

    // 创建书评
    Review review =
        Review.builder()
            .productId(productId)
            .userId(userId)
            .rating(reviewCreateDTO.getRating())
            .content(reviewCreateDTO.getContent())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Review savedReview = reviewRepository.save(review);
    log.info("用户 {} 为商品 {} 创建了书评", userId, productId);

    // 更新商品评分
    updateProductRating(product);

    return convertToDTO(savedReview);
  }

  @Override
  @Transactional
  public ReviewDTO updateReview(Long reviewId, Long userId, ReviewCreateDTO reviewCreateDTO) {
    // 检查书评是否存在且属于该用户
    Review review = getReviewById(reviewId);

    if (!review.getUserId().equals(userId)) {
      throw new BusinessException(BusinessErrorCode.ACCESS_DENIED);
    }

    // 更新书评
    log.info("用户 {} 更新了书评 {}", userId, reviewId);
    return updateReviewCommon(review, reviewCreateDTO);
  }

  @Override
  @Transactional
  public ReviewDTO updateReviewByAdmin(Long reviewId, ReviewCreateDTO reviewCreateDTO) {
    // 检查书评是否存在
    Review review = getReviewById(reviewId);

    // 更新书评
    log.info("管理员更新了书评 {}", reviewId);
    return updateReviewCommon(review, reviewCreateDTO);
  }

  @Override
  @Transactional
  public void deleteReview(Long reviewId, Long userId) {
    // 检查书评是否存在且属于该用户
    Review review = getReviewById(reviewId);

    if (!review.getUserId().equals(userId)) {
      throw new BusinessException(BusinessErrorCode.ACCESS_DENIED);
    }

    // 删除书评
    deleteReviewCommon(review);
    log.info("用户 {} 删除了书评 {}", userId, reviewId);
  }

  @Override
  @Transactional
  public void deleteReviewByAdmin(Long reviewId) {
    // 检查书评是否存在
    Review review = getReviewById(reviewId);

    // 删除书评
    deleteReviewCommon(review);
    log.info("管理员删除了书评 {}", reviewId);
  }

  @Override
  public Double calculateProductRating(Long productId) {
    return reviewRepository.calculateAverageRating(productId);
  }

  /**
   * 将 Review 实体转换为 ReviewDTO
   *
   * @param review 书评实体
   * @return 书评 DTO
   */
  private ReviewDTO convertToDTO(Review review) {
    // 获取用户信息
    User user = getUserById(review.getUserId());

    return ReviewDTO.builder()
        .id(review.getId())
        .productId(review.getProductId())
        .userId(review.getUserId())
        .username(user.getUsername())
        .userAvatar(user.getAvatar())
        .rating(review.getRating())
        .content(review.getContent())
        .createdAt(review.getCreatedAt())
        .updatedAt(review.getUpdatedAt())
        .build();
  }

  /**
   * 将 Review 实体转换为 ReviewDTO
   *
   * @param review  书评实体
   * @param userMap 用户信息 Map
   * @return 书评 DTO
   */
  private ReviewDTO convertToDTO(Review review, Map<Long, User> userMap) {
    // 从 map 中获取用户信息
    User user = userMap.get(review.getUserId());

    if (user == null) {
      log.error("严重的数据一致性问题：用户 {} 不存在但关联了书评 {}", review.getUserId(), review.getId());
      return ReviewDTO.builder()
          .id(review.getId())
          .productId(review.getProductId())
          .userId(review.getUserId())
          .username("未知用户")
          .userAvatar(null)
          .rating(review.getRating())
          .content(review.getContent())
          .createdAt(review.getCreatedAt())
          .updatedAt(review.getUpdatedAt())
          .build();
    }

    return ReviewDTO.builder()
        .id(review.getId())
        .productId(review.getProductId())
        .userId(review.getUserId())
        .username(user.getUsername())
        .userAvatar(user.getAvatar())
        .rating(review.getRating())
        .content(review.getContent())
        .createdAt(review.getCreatedAt())
        .updatedAt(review.getUpdatedAt())
        .build();
  }

  /**
   * 获取商品信息
   *
   * @param productId 商品 ID
   * @return 商品信息
   */
  private Product getProductById(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> ResourceNotFoundException.create("商品", "id", productId));
  }

  /**
   * 获取用户信息
   *
   * @param userId 用户 ID
   * @return 用户信息
   */
  private User getUserById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> ResourceNotFoundException.create("用户", "id", userId));
  }

  /**
   * 获取书评信息
   *
   * @param reviewId 书评 ID
   * @return 书评信息
   */
  private Review getReviewById(Long reviewId) {
    return reviewRepository.findById(reviewId)
        .orElseThrow(() -> ResourceNotFoundException.create("书评", "id", reviewId));
  }

  /**
   * 更新书评公共逻辑
   *
   * @param review          书评
   * @param reviewCreateDTO 书评创建 DTO
   * @return 更新后的书评 DTO
   */
  private ReviewDTO updateReviewCommon(Review review, ReviewCreateDTO reviewCreateDTO) {
    review.setRating(reviewCreateDTO.getRating());
    review.setContent(reviewCreateDTO.getContent());
    review.setUpdatedAt(LocalDateTime.now());
    Review updatedReview = reviewRepository.save(review);

    // 更新商品评分
    Product product = getProductById(review.getProductId());
    updateProductRating(product);

    return convertToDTO(updatedReview);
  }

  /**
   * 删除书评公共逻辑
   *
   * @param review 书评
   */
  private void deleteReviewCommon(Review review) {
    reviewRepository.delete(review);

    // 更新商品评分
    Product product = getProductById(review.getProductId());
    updateProductRating(product);
  }

  /**
   * 更新商品评分
   *
   * <p>使用简单的加权平均算法，基准评分权重为 1，用户评分权重为 2
   *
   * @param product 商品
   */
  private void updateProductRating(Product product) {
    Double averageRating = reviewRepository.calculateAverageRating(product.getId());

    if (averageRating != null) {
      int finalRating = (int) Math
          .round((BASE_RATING * BASE_RATING_WEIGHT + averageRating * USER_RATING_WEIGHT) / TOTAL_WEIGHT);
      product.setRate(finalRating);
    } else {
      product.setRate(BASE_RATING);
    }
    productRepository.save(product);
    log.info("更新商品 {} 评分为 {}", product.getId(), product.getRate());
  }
}
