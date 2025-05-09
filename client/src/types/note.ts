/**
 * 反馈类型枚举
 */
export enum FeedbackType {
  LIKE = "LIKE", // 点赞
  DISLIKE = "DISLIKE", // 点踩
}

/**
 * 读书笔记接口
 */
export interface Note {
  id: number;
  title: string;
  content: string;
  productId: number;
  productTitle: string;
  userId: number;
  username: string;
  userAvatar?: string;
  likeCount: number;
  dislikeCount: number;
  commentCount: number;
  userFeedback?: FeedbackType; // null 表示用户没有反馈
  createdAt: string;
  updatedAt: string;
}

/**
 * 读书笔记评论接口
 */
export interface NoteComment {
  id: number;
  noteId: number;
  userId: number;
  username: string;
  userAvatar?: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * 读书笔记创建参数接口
 */
export interface NoteCreateParams {
  title: string;
  content: string;
}

/**
 * 更新读书笔记的参数
 */
export type NoteUpdateParams = NoteCreateParams;

/**
 * 读书笔记反馈参数接口
 */
export interface NoteFeedbackParams {
  feedbackType: FeedbackType;
}

/**
 * 读书笔记评论创建参数接口
 */
export interface NoteCommentCreateParams {
  content: string;
}
