/**
 * 书评评分等级
 */
export enum RatingLevel {
  BAD = 0,
  POOR = 2,
  FAIR = 4,
  GOOD = 6,
  EXCELLENT = 8,
  PERFECT = 10,
}

/**
 * 书评接口
 */
export interface Review {
  id: number;
  productId: number;
  userId: number;
  username: string;
  userAvatar?: string;
  rating: number;
  content?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * 书评创建参数接口
 */
export interface ReviewCreateParams {
  rating: number;
  content?: string;
}

/**
 * 更新书评的参数
 */
export type ReviewUpdateParams = ReviewCreateParams;
