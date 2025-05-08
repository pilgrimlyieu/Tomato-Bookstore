package com.tomato.bookstore.controller;

import com.tomato.bookstore.constant.RoleConstants;
import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.dto.ReviewCreateDTO;
import com.tomato.bookstore.dto.ReviewDTO;
import com.tomato.bookstore.security.UserPrincipal;
import com.tomato.bookstore.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 书评控制器
 *
 * <p>该类包含书评相关的接口，包括获取书评列表、创建书评、更新书评、删除书评等操作。
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ReviewController {
  private final ReviewService reviewService;

  /**
   * 获取指定商品的所有书评
   *
   * @param productId 商品 ID
   * @return 书评列表
   */
  @GetMapping("/product/{productId}")
  public ApiResponse<List<ReviewDTO>> getProductReviews(@PathVariable Long productId) {
    log.info("获取商品 {} 的所有书评", productId);
    List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
    return ApiResponse.success(reviews);
  }

  /**
   * 获取当前用户的所有书评
   *
   * @param userPrincipal 当前用户
   * @return 书评列表
   */
  @GetMapping("/user")
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<List<ReviewDTO>> getUserReviews(
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户 {} 查看自己的书评列表", userPrincipal.getUsername());
    List<ReviewDTO> reviews = reviewService.getReviewsByUserId(userPrincipal.getUserId());
    return ApiResponse.success(reviews);
  }

  /**
   * 获取指定用户的所有书评（仅管理员）
   *
   * @param userId 用户 ID
   * @return 书评列表
   */
  @GetMapping("/user/{userId}")
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<List<ReviewDTO>> getUserReviewsByAdmin(@PathVariable Long userId) {
    log.info("管理员查看用户 {} 的书评列表", userId);
    List<ReviewDTO> reviews = reviewService.getReviewsByUserId(userId);
    return ApiResponse.success(reviews);
  }

  /**
   * 获取所有书评（仅管理员）
   *
   * @return 书评列表
   */
  @GetMapping("/all")
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<List<ReviewDTO>> getAllReviews() {
    log.info("管理员查看所有书评");
    List<ReviewDTO> reviews = reviewService.getAllReviews();
    return ApiResponse.success(reviews);
  }

  /**
   * 创建书评
   *
   * @param productId 商品 ID
   * @param reviewCreateDTO 书评创建 DTO
   * @param userPrincipal 当前用户
   * @return 创建的书评
   */
  @PostMapping("/product/{productId}")
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<ReviewDTO> createReview(
      @PathVariable Long productId,
      @RequestBody @Valid ReviewCreateDTO reviewCreateDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户 {} 为商品 {} 创建书评", userPrincipal.getUsername(), productId);
    ReviewDTO createdReview =
        reviewService.createReview(productId, userPrincipal.getUserId(), reviewCreateDTO);
    return ApiResponse.created(createdReview);
  }

  /**
   * 更新书评
   *
   * @param reviewId 书评 ID
   * @param reviewCreateDTO 书评创建 DTO
   * @param userPrincipal 当前用户
   * @return 更新后的书评
   */
  @PutMapping("/{reviewId}")
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<ReviewDTO> updateReview(
      @PathVariable Long reviewId,
      @RequestBody @Valid ReviewCreateDTO reviewCreateDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户 {} 更新书评 {}", userPrincipal.getUsername(), reviewId);
    ReviewDTO updatedReview =
        reviewService.updateReview(reviewId, userPrincipal.getUserId(), reviewCreateDTO);
    return ApiResponse.success(updatedReview);
  }

  /**
   * 管理员更新书评
   *
   * @param reviewId 书评 ID
   * @param reviewCreateDTO 书评创建 DTO
   * @param userPrincipal 当前用户
   * @return 更新后的书评
   */
  @PutMapping("/admin/{reviewId}")
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<ReviewDTO> updateReviewByAdmin(
      @PathVariable Long reviewId,
      @RequestBody @Valid ReviewCreateDTO reviewCreateDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员 {} 更新书评 {}", userPrincipal.getUsername(), reviewId);
    ReviewDTO updatedReview = reviewService.updateReviewByAdmin(reviewId, reviewCreateDTO);
    return ApiResponse.success(updatedReview);
  }

  /**
   * 删除书评
   *
   * @param reviewId 书评 ID
   * @param userPrincipal 当前用户
   * @return 删除结果
   */
  @DeleteMapping("/{reviewId}")
  @PreAuthorize(RoleConstants.HAS_ANY_ROLE)
  public ApiResponse<String> deleteReview(
      @PathVariable Long reviewId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户 {} 删除书评 {}", userPrincipal.getUsername(), reviewId);
    reviewService.deleteReview(reviewId, userPrincipal.getUserId());
    return ApiResponse.success("删除成功");
  }

  /**
   * 管理员删除书评
   *
   * @param reviewId 书评 ID
   * @param userPrincipal 当前用户
   * @return 删除结果
   */
  @DeleteMapping("/admin/{reviewId}")
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<String> deleteReviewByAdmin(
      @PathVariable Long reviewId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员 {} 删除书评 {}", userPrincipal.getUsername(), reviewId);
    reviewService.deleteReviewByAdmin(reviewId);
    return ApiResponse.success("删除成功");
  }
}
