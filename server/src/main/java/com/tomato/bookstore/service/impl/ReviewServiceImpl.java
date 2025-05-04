package com.tomato.bookstore.service.impl;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.constant.ExceptionMessages;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Override
  public List<ReviewDTO> getReviewsByProductId(Long productId) {
    // 确认商品存在
    productRepository.findById(productId)
        .orElseThrow(() -> ResourceNotFoundException.create("商品", "id", productId));

    List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);

    // 转换为 DTO
    return reviews.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<ReviewDTO> getReviewsByUserId(Long userId) {
    // 确认用户存在
    userRepository.findById(userId)
        .orElseThrow(() -> ResourceNotFoundException.create("用户", "id", userId));

    List<Review> reviews = reviewRepository.findByUserId(userId);

    // 转换为 DTO
    return reviews.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ReviewDTO createReview(Long productId, Long userId, ReviewCreateDTO reviewCreateDTO) {
    // 检查商品是否存在
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> ResourceNotFoundException.create("商品", "id", productId));

    // 检查用户是否存在
    User user = userRepository.findById(userId)
        .orElseThrow(() -> ResourceNotFoundException.create("用户", "id", userId));

    // 检查用户是否已经评论过该商品
    if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
      throw new BusinessException(BusinessErrorCode.REVIEW_ALREADY_EXISTS);
    }

    // 创建书评
    Review review = Review.builder()
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
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> ResourceNotFoundException.create("书评", "id", reviewId));

    if (!review.getUserId().equals(userId)) {
      throw new BusinessException(BusinessErrorCode.ACCESS_DENIED);
    }

    // 更新书评
    review.setRating(reviewCreateDTO.getRating());
    review.setContent(reviewCreateDTO.getContent());
    review.setUpdatedAt(LocalDateTime.now());

    Review updatedReview = reviewRepository.save(review);
    log.info("用户 {} 更新了书评 {}", userId, reviewId);

    // 更新商品评分
    Product product = productRepository.findById(review.getProductId())
        .orElseThrow(() -> ResourceNotFoundException.create("商品", "id", review.getProductId()));
    updateProductRating(product);

    return convertToDTO(updatedReview);
  }

  @Override
  @Transactional
  public ReviewDTO updateReviewByAdmin(Long reviewId, ReviewCreateDTO reviewCreateDTO) {
    // 检查书评是否存在
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> ResourceNotFoundException.create("书评", "id", reviewId));

    // 更新书评
    review.setRating(reviewCreateDTO.getRating());
    review.setContent(reviewCreateDTO.getContent());
    review.setUpdatedAt(LocalDateTime.now());

    Review updatedReview = reviewRepository.save(review);
    log.info("管理员更新了书评 {}", reviewId);

    // 更新商品评分
    Product product = productRepository.findById(review.getProductId())
        .orElseThrow(() -> ResourceNotFoundException.create("商品", "id", review.getProductId()));
    updateProductRating(product);

    return convertToDTO(updatedReview);
  }

  @Override
  @Transactional
  public void deleteReview(Long reviewId, Long userId) {
    // 检查书评是否存在且属于该用户
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> ResourceNotFoundException.create("书评", "id", reviewId));

    if (!review.getUserId().equals(userId)) {
      throw new BusinessException(BusinessErrorCode.ACCESS_DENIED);
    }

    // 删除书评
    reviewRepository.delete(review);
    log.info("用户 {} 删除了书评 {}", userId, reviewId);

    // 更新商品评分
    Product product = productRepository.findById(review.getProductId())
        .orElseThrow(() -> ResourceNotFoundException.create("商品", "id", review.getProductId()));
    updateProductRating(product);
  }

  @Override
  @Transactional
  public void deleteReviewByAdmin(Long reviewId) {
    // 检查书评是否存在
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> ResourceNotFoundException.create("书评", "id", reviewId));

    // 删除书评
    reviewRepository.delete(review);
    log.info("管理员删除了书评 {}", reviewId);

    // 更新商品评分
    Product product = productRepository.findById(review.getProductId())
        .orElseThrow(() -> ResourceNotFoundException.create("商品", "id", review.getProductId()));
    updateProductRating(product);
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
    User user = userRepository.findById(review.getUserId())
        .orElse(new User()); // 如果用户不存在，使用空对象避免空指针

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
   * 更新商品评分
   *
   * 使用简单的加权平均算法，基准评分权重为 1，用户评分权重为 2
   *
   * @param product 商品
   */
  private void updateProductRating(Product product) {
    Double averageRating = reviewRepository.calculateAverageRating(product.getId());

    if (averageRating != null) {
      // 加权平均：基准评分权重为 1，用户评分平均值权重为 2
      // 最终评分 = (基准评分 + 用户评分平均值 * 2) / 3
      int baseRating = 8; // 基准评分
      int finalRating = (int) Math.round((baseRating + averageRating * 2) / 3);
      product.setRate(finalRating);
      productRepository.save(product);
      log.info("更新商品 {} 评分为 {}", product.getId(), finalRating);
    }
  }
}
