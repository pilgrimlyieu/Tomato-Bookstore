package com.tomato.bookstore.service;

import com.tomato.bookstore.constant.FeedbackType;
import com.tomato.bookstore.dto.NoteCommentCreateDTO;
import com.tomato.bookstore.dto.NoteCommentDTO;
import com.tomato.bookstore.dto.NoteCreateDTO;
import com.tomato.bookstore.dto.NoteDTO;
import java.util.List;

/** 读书笔记服务接口 */
public interface NoteService {
  /**
   * 根据商品 ID 获取所有笔记
   *
   * @param productId 商品 ID
   * @return 笔记列表
   */
  List<NoteDTO> getNotesByProductId(Long productId);

  /**
   * 根据用户 ID 获取所有笔记
   *
   * @param userId 用户 ID
   * @return 笔记列表
   */
  List<NoteDTO> getNotesByUserId(Long userId);

  /**
   * 获取所有笔记（管理员使用）
   *
   * @return 笔记列表
   */
  List<NoteDTO> getAllNotes();

  /**
   * 根据笔记 ID 获取笔记详情
   *
   * @param noteId 笔记 ID
   * @param currentUserId 当前用户 ID（可选，提供后可获取用户反馈状态）
   * @return 笔记详情
   */
  NoteDTO getNoteById(Long noteId, Long currentUserId);

  /**
   * 创建笔记
   *
   * @param productId 商品 ID
   * @param userId 用户 ID
   * @param noteCreateDTO 笔记创建 DTO
   * @return 创建的笔记
   */
  NoteDTO createNote(Long productId, Long userId, NoteCreateDTO noteCreateDTO);

  /**
   * 更新笔记
   *
   * @param noteId 笔记 ID
   * @param userId 用户 ID
   * @param noteCreateDTO 笔记创建 DTO
   * @return 更新后的笔记
   */
  NoteDTO updateNote(Long noteId, Long userId, NoteCreateDTO noteCreateDTO);

  /**
   * 管理员更新笔记
   *
   * @param noteId 笔记 ID
   * @param noteCreateDTO 笔记创建 DTO
   * @return 更新后的笔记
   */
  NoteDTO updateNoteByAdmin(Long noteId, NoteCreateDTO noteCreateDTO);

  /**
   * 删除笔记
   *
   * @param noteId 笔记 ID
   * @param userId 用户 ID
   */
  void deleteNote(Long noteId, Long userId);

  /**
   * 管理员删除笔记
   *
   * @param noteId 笔记 ID
   */
  void deleteNoteByAdmin(Long noteId);

  /**
   * 为笔记添加或更新反馈
   *
   * @param noteId 笔记 ID
   * @param userId 用户 ID
   * @param feedbackType 反馈类型
   * @return 更新后的笔记
   */
  NoteDTO addOrUpdateFeedback(Long noteId, Long userId, FeedbackType feedbackType);

  /**
   * 获取笔记的所有评论
   *
   * @param noteId 笔记 ID
   * @return 评论列表
   */
  List<NoteCommentDTO> getNoteComments(Long noteId);

  /**
   * 添加笔记评论
   *
   * @param noteId 笔记 ID
   * @param userId 用户 ID
   * @param commentCreateDTO 评论创建 DTO
   * @return 创建的评论
   */
  NoteCommentDTO addComment(Long noteId, Long userId, NoteCommentCreateDTO commentCreateDTO);

  /**
   * 删除笔记评论
   *
   * @param commentId 评论 ID
   * @param userId 用户 ID
   */
  void deleteComment(Long commentId, Long userId);

  /**
   * 管理员删除笔记评论
   *
   * @param commentId 评论 ID
   */
  void deleteCommentByAdmin(Long commentId);
}
