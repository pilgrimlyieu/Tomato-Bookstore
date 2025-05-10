import { NOTE_MODULE } from "@/constants/apiPrefix";
import type { ApiResponse } from "@/types/api";
import type {
  Note,
  NoteComment,
  NoteCommentCreateParams,
  NoteCreateParams,
  NoteFeedbackParams,
  NoteUpdateParams,
} from "@/types/note";
import apiClient from "@/utils/apiClient";

/**
 * 读书笔记服务
 */
const noteService = {
  /**
   * 获取指定商品的所有读书笔记
   *
   * @param {number} productId 商品 ID
   * @returns {Promise<ApiResponse<Note[]>>} 笔记列表
   */
  getProductNotes(productId: number): Promise<ApiResponse<Note[]>> {
    return apiClient.get(`${NOTE_MODULE}/product/${productId}`);
  },

  /**
   * 获取当前用户的所有读书笔记
   *
   * @returns {Promise<ApiResponse<Note[]>>} 笔记列表
   */
  getUserNotes(): Promise<ApiResponse<Note[]>> {
    return apiClient.get(`${NOTE_MODULE}/user`);
  },

  /**
   * 获取指定用户的所有读书笔记（仅管理员）
   *
   * @param {number} userId 用户 ID
   * @returns {Promise<ApiResponse<Note[]>>} 笔记列表
   */
  getUserNotesByAdmin(userId: number): Promise<ApiResponse<Note[]>> {
    return apiClient.get(`${NOTE_MODULE}/user/${userId}`);
  },

  /**
   * 获取所有读书笔记（仅管理员）
   *
   * @returns {Promise<ApiResponse<Note[]>>} 笔记列表
   */
  getAllNotes(): Promise<ApiResponse<Note[]>> {
    return apiClient.get(`${NOTE_MODULE}/all`);
  },

  /**
   * 获取读书笔记详情
   *
   * @param {number} noteId 笔记 ID
   * @returns {Promise<ApiResponse<Note>>} 笔记详情
   */
  getNoteById(noteId: number): Promise<ApiResponse<Note>> {
    return apiClient.get(`${NOTE_MODULE}/${noteId}`);
  },

  /**
   * 创建读书笔记
   *
   * @param {number} productId 商品 ID
   * @param {NoteCreateParams} params 笔记参数
   * @returns {Promise<ApiResponse<Note>>} 创建的笔记
   */
  createNote(
    productId: number,
    params: NoteCreateParams,
  ): Promise<ApiResponse<Note>> {
    return apiClient.post(`${NOTE_MODULE}/product/${productId}`, params);
  },

  /**
   * 更新读书笔记
   *
   * @param {number} noteId 笔记 ID
   * @param {NoteUpdateParams} params 更新参数
   * @returns {Promise<ApiResponse<Note>>} 更新后的笔记
   */
  updateNote(
    noteId: number,
    params: NoteUpdateParams,
  ): Promise<ApiResponse<Note>> {
    return apiClient.put(`${NOTE_MODULE}/${noteId}`, params);
  },

  /**
   * 管理员更新读书笔记
   *
   * @param {number} noteId 笔记 ID
   * @param {NoteUpdateParams} params 更新参数
   * @returns {Promise<ApiResponse<Note>>} 更新后的笔记
   */
  updateNoteByAdmin(
    noteId: number,
    params: NoteUpdateParams,
  ): Promise<ApiResponse<Note>> {
    return apiClient.put(`${NOTE_MODULE}/admin/${noteId}`, params);
  },

  /**
   * 删除读书笔记
   *
   * @param {number} noteId 笔记 ID
   * @returns {Promise<ApiResponse<string>>} 删除结果
   */
  deleteNote(noteId: number): Promise<ApiResponse<string>> {
    return apiClient.delete(`${NOTE_MODULE}/${noteId}`);
  },

  /**
   * 管理员删除读书笔记
   *
   * @param {number} noteId 笔记 ID
   * @returns {Promise<ApiResponse<string>>} 删除结果
   */
  deleteNoteByAdmin(noteId: number): Promise<ApiResponse<string>> {
    return apiClient.delete(`${NOTE_MODULE}/admin/${noteId}`);
  },

  /**
   * 为读书笔记添加反馈（点赞/点踩）
   *
   * @param {number} noteId 笔记 ID
   * @param {NoteFeedbackParams} params 反馈参数
   * @returns {Promise<ApiResponse<Note>>} 更新后的笔记
   */
  addFeedback(
    noteId: number,
    params: NoteFeedbackParams,
  ): Promise<ApiResponse<Note>> {
    return apiClient.post(`${NOTE_MODULE}/${noteId}/feedback`, params);
  },

  /**
   * 获取读书笔记的所有评论
   *
   * @param {number} noteId 笔记 ID
   * @returns {Promise<ApiResponse<NoteComment[]>>} 评论列表
   */
  getNoteComments(noteId: number): Promise<ApiResponse<NoteComment[]>> {
    return apiClient.get(`${NOTE_MODULE}/${noteId}/comments`);
  },

  /**
   * 为读书笔记添加评论
   *
   * @param {number} noteId 笔记 ID
   * @param {NoteCommentCreateParams} params 评论参数
   * @returns {Promise<ApiResponse<NoteComment>>} 创建的评论
   */
  addComment(
    noteId: number,
    params: NoteCommentCreateParams,
  ): Promise<ApiResponse<NoteComment>> {
    return apiClient.post(`${NOTE_MODULE}/${noteId}/comments`, params);
  },

  /**
   * 删除读书笔记评论
   *
   * @param {number} noteId 笔记 ID
   * @param {number} commentId 评论 ID
   * @returns {Promise<ApiResponse<string>>} 删除结果
   */
  deleteComment(
    noteId: number,
    commentId: number,
  ): Promise<ApiResponse<string>> {
    return apiClient.delete(`${NOTE_MODULE}/${noteId}/comments/${commentId}`);
  },

  /**
   * 管理员删除读书笔记评论
   *
   * @param {number} noteId 笔记 ID
   * @param {number} commentId 评论 ID
   * @returns {Promise<ApiResponse<string>>} 删除结果
   */
  deleteCommentByAdmin(
    noteId: number,
    commentId: number,
  ): Promise<ApiResponse<string>> {
    return apiClient.delete(
      `${NOTE_MODULE}/admin/${noteId}/comments/${commentId}`,
    );
  },
};

export default noteService;
