import { REVIEW_MODULE } from "@/constants/apiPrefix";
import type { ApiResponse } from "@/types/api";
import type {
  Review,
  ReviewCreateParams,
  ReviewUpdateParams,
} from "@/types/review";
import apiClient from "@/utils/apiClient";

/**
 * 书评服务
 */
export default {
  /**
   * 获取指定商品的所有书评
   *
   * @param {number} productId 商品 ID
   * @returns {Promise<ApiResponse<Review[]>>} 书评列表
   */
  getProductReviews(productId: number): Promise<ApiResponse<Review[]>> {
    return apiClient.get(`${REVIEW_MODULE}/product/${productId}`);
  },

  /**
   * 获取当前用户的所有书评
   *
   * @returns {Promise<ApiResponse<Review[]>>} 书评列表
   */
  getUserReviews(): Promise<ApiResponse<Review[]>> {
    return apiClient.get(`${REVIEW_MODULE}/user`);
  },

  /**
   * 获取指定用户的所有书评（仅管理员）
   *
   * @param {number} userId 用户 ID
   * @returns {Promise<ApiResponse<Review[]>>} 书评列表
   */
  getUserReviewsByAdmin(userId: number): Promise<ApiResponse<Review[]>> {
    return apiClient.get(`${REVIEW_MODULE}/user/${userId}`);
  },

  /**
   * 获取所有书评（仅管理员）
   *
   * @returns {Promise<ApiResponse<Review[]>>} 书评列表
   */
  getAllReviews(): Promise<ApiResponse<Review[]>> {
    return apiClient.get(`${REVIEW_MODULE}/all`);
  },

  /**
   * 创建书评
   *
   * @param {number} productId 商品 ID
   * @param {ReviewCreateParams} params 书评参数
   * @returns {Promise<ApiResponse<Review>>} 创建的书评
   */
  createReview(
    productId: number,
    params: ReviewCreateParams,
  ): Promise<ApiResponse<Review>> {
    return apiClient.post(`${REVIEW_MODULE}/product/${productId}`, params);
  },

  /**
   * 更新书评
   *
   * @param {number} reviewId 书评 ID
   * @param {ReviewUpdateParams} params 更新参数
   * @returns {Promise<ApiResponse<Review>>} 更新后的书评
   */
  updateReview(
    reviewId: number,
    params: ReviewUpdateParams,
  ): Promise<ApiResponse<Review>> {
    return apiClient.put(`${REVIEW_MODULE}/${reviewId}`, params);
  },

  /**
   * 管理员更新书评
   *
   * @param {number} reviewId 书评 ID
   * @param {ReviewUpdateParams} params 更新参数
   * @returns {Promise<ApiResponse<Review>>} 更新后的书评
   */
  updateReviewByAdmin(
    reviewId: number,
    params: ReviewUpdateParams,
  ): Promise<ApiResponse<Review>> {
    return apiClient.put(`${REVIEW_MODULE}/admin/${reviewId}`, params);
  },

  /**
   * 删除书评
   *
   * @param {number} reviewId 书评 ID
   * @returns {Promise<ApiResponse<string>>} 删除结果
   */
  deleteReview(reviewId: number): Promise<ApiResponse<string>> {
    return apiClient.delete(`${REVIEW_MODULE}/${reviewId}`);
  },

  /**
   * 管理员删除书评
   *
   * @param {number} reviewId 书评 ID
   * @returns {Promise<ApiResponse<string>>} 删除结果
   */
  deleteReviewByAdmin(reviewId: number): Promise<ApiResponse<string>> {
    return apiClient.delete(`${REVIEW_MODULE}/admin/${reviewId}`);
  },
};
