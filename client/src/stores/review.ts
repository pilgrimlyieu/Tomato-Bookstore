import reviewService from "@/services/review-service";
import type {
  Review,
  ReviewCreateParams,
  ReviewUpdateParams,
} from "@/types/review";
import { performAsyncAction } from "@/utils/asyncHelper";
import { HttpStatusCode } from "axios";
import { defineStore } from "pinia";

/**
 * 书评状态管理
 */
export const useReviewStore = defineStore("review", {
  state: () => ({
    productReviews: [] as Review[], // 当前查看的商品书评列表
    userReviews: [] as Review[], // 当前用户的书评列表
    managedUserReviews: [] as Review[], // 管理员查看的用户书评列表（仅管理员）
    allReviews: [] as Review[], // 所有书评列表（仅管理员）
    currentReview: null as Review | null, // 当前操作的书评
    loading: false, // 加载状态
  }),

  getters: {
    /**
     * 当前用户是否已评价指定商品
     *
     * @param {number} productId 商品 ID
     * @returns {boolean} 是否已评价
     */
    hasUserReviewed:
      (state) =>
      (productId: number, userId: number): boolean => {
        return state.productReviews.some(
          (review) =>
            review.productId === productId && review.userId === userId,
        );
      },

    /**
     * 获取当前用户对指定商品的书评
     *
     * @param {number} productId 商品 ID
     * @param {number} userId 用户 ID
     * @returns {Review | undefined} 书评信息
     */
    getUserReviewForProduct:
      (state) =>
      (productId: number, userId: number): Review | undefined => {
        return state.productReviews.find(
          (review) =>
            review.productId === productId && review.userId === userId,
        );
      },
  },

  actions: {
    /**
     * 获取指定商品的所有书评
     *
     * @param {number} productId 商品 ID
     * @returns {Promise<void>}
     */
    async fetchProductReviews(productId: number): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => reviewService.getProductReviews(productId),
        (data) => {
          this.productReviews = data;
        },
        `获取商品书评失败（ID: ${productId}）：`,
        true,
      );
    },

    /**
     * 获取当前用户的所有书评
     *
     * @returns {Promise<void>}
     */
    async fetchUserReviews(): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => reviewService.getUserReviews(),
        (data) => {
          this.userReviews = data;
        },
        "获取用户书评列表失败：",
        true,
      );
    },

    /**
     * 获取指定用户的所有书评（管理员）
     *
     * @param {number} userId 用户 ID
     * @returns {Promise<void>}
     */
    async fetchUserReviewsByAdmin(userId: number): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => reviewService.getUserReviewsByAdmin(userId),
        (data) => {
          this.managedUserReviews = data;
        },
        `获取用户书评列表失败（用户ID: ${userId}）：`,
        true,
      );
    },

    /**
     * 获取所有书评（仅管理员）
     *
     * @returns {Promise<void>}
     */
    async fetchAllReviews(): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => reviewService.getAllReviews(),
        (data) => {
          this.allReviews = data;
        },
        "获取所有书评失败：",
        true,
      );
    },

    /**
     * 创建书评
     *
     * @param {number} productId 商品 ID
     * @param {ReviewCreateParams} params 书评参数
     * @returns {Promise<boolean>} 是否创建成功
     */
    async createReview(
      productId: number,
      params: ReviewCreateParams,
    ): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => reviewService.createReview(productId, params),
        (data) => {
          // 更新本地商品书评列表
          this.productReviews.unshift(data);
          // 更新用户书评列表
          this.userReviews.unshift(data);
        },
        `创建书评失败（商品ID: ${productId}）：`,
        true,
        [HttpStatusCode.Created],
        "书评发布成功",
      );
    },

    /**
     * 更新书评
     *
     * @param {number} reviewId 书评 ID
     * @param {ReviewUpdateParams} params 更新参数
     * @returns {Promise<boolean>} 是否更新成功
     */
    async updateReview(
      reviewId: number,
      params: ReviewUpdateParams,
    ): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => reviewService.updateReview(reviewId, params),
        (data) => {
          // 更新本地数据
          this.updateLocalReviewData(data);
        },
        `更新书评失败（ID: ${reviewId}）：`,
        true,
        [HttpStatusCode.Ok],
        "书评更新成功",
      );
    },

    /**
     * 管理员更新书评
     *
     * @param {number} reviewId 书评 ID
     * @param {ReviewUpdateParams} params 更新参数
     * @returns {Promise<boolean>} 是否更新成功
     */
    async updateReviewByAdmin(
      reviewId: number,
      params: ReviewUpdateParams,
    ): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => reviewService.updateReviewByAdmin(reviewId, params),
        (data) => {
          // 更新本地数据
          this.updateLocalReviewData(data);
        },
        `管理员更新书评失败（ID: ${reviewId}）：`,
        true,
        [HttpStatusCode.Ok],
        "书评已修改",
      );
    },

    /**
     * 删除书评
     *
     * @param {number} reviewId 书评 ID
     * @returns {Promise<boolean>} 是否删除成功
     */
    async deleteReview(reviewId: number): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => reviewService.deleteReview(reviewId),
        () => {
          // 从本地列表中移除
          this.removeLocalReview(reviewId);
        },
        `删除书评失败（ID: ${reviewId}）：`,
        true,
        [HttpStatusCode.Ok],
        "书评已删除",
      );
    },

    /**
     * 管理员删除书评
     *
     * @param {number} reviewId 书评 ID
     * @returns {Promise<boolean>} 是否删除成功
     */
    async deleteReviewByAdmin(reviewId: number): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => reviewService.deleteReviewByAdmin(reviewId),
        () => {
          // 从本地列表中移除
          this.removeLocalReview(reviewId);
        },
        `管理员删除书评失败（ID: ${reviewId}）：`,
        true,
        [HttpStatusCode.Ok],
        "书评已删除",
      );
    },

    /**
     * 更新本地书评数据
     *
     * @param {Review} updatedReview 更新后的书评
     */
    updateLocalReviewData(updatedReview: Review): void {
      // 定义一个辅助函数来更新列表中的评论
      const updateInList = (list: Review[]) => {
        const index = list.findIndex(
          (review) => review.id === updatedReview.id,
        );
        if (index !== -1) {
          list[index] = updatedReview;
        }
      };

      // 更新所有列表
      updateInList(this.productReviews);
      updateInList(this.userReviews);
      updateInList(this.managedUserReviews);
      updateInList(this.allReviews);

      // 更新当前书评
      if (this.currentReview && this.currentReview.id === updatedReview.id) {
        this.currentReview = updatedReview;
      }
    },

    /**
     * 从本地移除书评
     *
     * @param {number} reviewId 书评 ID
     */
    removeLocalReview(reviewId: number): void {
      // 从所有列表中移除指定 ID 的评论
      this.productReviews = this.productReviews.filter(
        (review) => review.id !== reviewId,
      );
      this.userReviews = this.userReviews.filter(
        (review) => review.id !== reviewId,
      );
      this.managedUserReviews = this.managedUserReviews.filter(
        (review) => review.id !== reviewId,
      );
      this.allReviews = this.allReviews.filter(
        (review) => review.id !== reviewId,
      );

      if (this.currentReview && this.currentReview.id === reviewId) {
        this.currentReview = null;
      }
    },

    /**
     * 设置当前操作的书评
     *
     * @param {Review | null} review 书评对象
     */
    setCurrentReview(review: Review | null): void {
      this.currentReview = review;
    },

    /**
     * 清除当前操作的书评
     */
    clearCurrentReview(): void {
      this.currentReview = null;
    },
  },
});
