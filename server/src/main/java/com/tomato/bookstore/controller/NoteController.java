package com.tomato.bookstore.controller;

import com.tomato.bookstore.constant.RoleConstants;
import com.tomato.bookstore.dto.ApiResponse;
import com.tomato.bookstore.dto.NoteCommentCreateDTO;
import com.tomato.bookstore.dto.NoteCommentDTO;
import com.tomato.bookstore.dto.NoteCreateDTO;
import com.tomato.bookstore.dto.NoteDTO;
import com.tomato.bookstore.dto.NoteFeedbackDTO;
import com.tomato.bookstore.security.UserPrincipal;
import com.tomato.bookstore.service.NoteService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 读书笔记控制器
 *
 * <p>该类包含读书笔记相关的接口，包括获取读书笔记列表、创建笔记、更新笔记、删除笔记、评论管理等操作。
 */
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
@Slf4j
@Validated
public class NoteController {
  private final NoteService noteService;

  /**
   * 获取指定商品的所有读书笔记
   *
   * @param productId 商品 ID
   * @return 读书笔记列表
   */
  @GetMapping("/product/{productId}")
  public ApiResponse<List<NoteDTO>> getProductNotes(@PathVariable Long productId) {
    log.info("获取商品 {} 的所有读书笔记", productId);
    List<NoteDTO> notes = noteService.getNotesByProductId(productId);
    return ApiResponse.success(notes);
  }

  /**
   * 获取当前用户的所有读书笔记
   *
   * @param userPrincipal 当前用户
   * @return 读书笔记列表
   */
  @GetMapping("/user")
  @PreAuthorize("RoleConstants.HAS_ANY_ROLE")
  public ApiResponse<List<NoteDTO>> getUserNotes(
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户 {} 查看自己的读书笔记列表", userPrincipal.getUsername());
    List<NoteDTO> notes = noteService.getNotesByUserId(userPrincipal.getUserId());
    return ApiResponse.success(notes);
  }

  /**
   * 获取指定用户的所有读书笔记（仅管理员）
   *
   * @param userId 用户 ID
   * @return 读书笔记列表
   */
  @GetMapping("/user/{userId}")
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<List<NoteDTO>> getUserNotesByAdmin(@PathVariable Long userId) {
    log.info("管理员查看用户 {} 的读书笔记列表", userId);
    List<NoteDTO> notes = noteService.getNotesByUserId(userId);
    return ApiResponse.success(notes);
  }

  /**
   * 获取所有读书笔记（仅管理员）
   *
   * @return 读书笔记列表
   */
  @GetMapping("/all")
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<List<NoteDTO>> getAllNotes() {
    log.info("管理员查看所有读书笔记");
    List<NoteDTO> notes = noteService.getAllNotes();
    return ApiResponse.success(notes);
  }

  /**
   * 根据 ID 获取读书笔记详情
   *
   * @param noteId 笔记 ID
   * @param userPrincipal 当前登录用户（可选）
   * @return 读书笔记详情
   */
  @GetMapping("/{noteId}")
  public ApiResponse<NoteDTO> getNoteById(
      @PathVariable Long noteId, @AuthenticationPrincipal(expression = "userId") Long userId) {
    log.info("查看读书笔记 {}", noteId);
    NoteDTO note = noteService.getNoteById(noteId, userId);
    return ApiResponse.success(note);
  }

  /**
   * 创建读书笔记
   *
   * @param productId 商品 ID
   * @param noteCreateDTO 读书笔记创建 DTO
   * @param userPrincipal 当前用户
   * @return 创建的读书笔记
   */
  @PostMapping("/product/{productId}")
  @PreAuthorize("RoleConstants.HAS_ANY_ROLE")
  public ApiResponse<NoteDTO> createNote(
      @PathVariable Long productId,
      @RequestBody @Valid NoteCreateDTO noteCreateDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户 {} 为商品 {} 创建读书笔记", userPrincipal.getUsername(), productId);
    NoteDTO createdNote =
        noteService.createNote(productId, userPrincipal.getUserId(), noteCreateDTO);
    return ApiResponse.created(createdNote);
  }

  /**
   * 更新读书笔记
   *
   * @param noteId 笔记 ID
   * @param noteCreateDTO 读书笔记创建 DTO
   * @param userPrincipal 当前用户
   * @return 更新后的读书笔记
   */
  @PutMapping("/{noteId}")
  @PreAuthorize("RoleConstants.HAS_ANY_ROLE")
  public ApiResponse<NoteDTO> updateNote(
      @PathVariable Long noteId,
      @RequestBody @Valid NoteCreateDTO noteCreateDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户 {} 更新读书笔记 {}", userPrincipal.getUsername(), noteId);
    NoteDTO updatedNote = noteService.updateNote(noteId, userPrincipal.getUserId(), noteCreateDTO);
    return ApiResponse.success(updatedNote);
  }

  /**
   * 管理员更新读书笔记
   *
   * @param noteId 笔记 ID
   * @param noteCreateDTO 读书笔记创建 DTO
   * @param userPrincipal 当前用户
   * @return 更新后的读书笔记
   */
  @PutMapping("/admin/{noteId}")
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<NoteDTO> updateNoteByAdmin(
      @PathVariable Long noteId,
      @RequestBody @Valid NoteCreateDTO noteCreateDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员 {} 更新读书笔记 {}", userPrincipal.getUsername(), noteId);
    NoteDTO updatedNote = noteService.updateNoteByAdmin(noteId, noteCreateDTO);
    return ApiResponse.success(updatedNote);
  }

  /**
   * 删除读书笔记
   *
   * @param noteId 笔记 ID
   * @param userPrincipal 当前用户
   * @return 删除结果
   */
  @DeleteMapping("/{noteId}")
  @PreAuthorize("RoleConstants.HAS_ANY_ROLE")
  public ApiResponse<String> deleteNote(
      @PathVariable Long noteId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户 {} 删除读书笔记 {}", userPrincipal.getUsername(), noteId);
    noteService.deleteNote(noteId, userPrincipal.getUserId());
    return ApiResponse.success("删除成功");
  }

  /**
   * 管理员删除读书笔记
   *
   * @param noteId 笔记 ID
   * @param userPrincipal 当前用户
   * @return 删除结果
   */
  @DeleteMapping("/admin/{noteId}")
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<String> deleteNoteByAdmin(
      @PathVariable Long noteId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员 {} 删除读书笔记 {}", userPrincipal.getUsername(), noteId);
    noteService.deleteNoteByAdmin(noteId);
    return ApiResponse.success("删除成功");
  }

  /**
   * 为读书笔记添加反馈（点赞/点踩）
   *
   * @param noteId 笔记 ID
   * @param feedbackDTO 反馈 DTO
   * @param userPrincipal 当前用户
   * @return 更新后的读书笔记
   */
  @PostMapping("/{noteId}/feedback")
  @PreAuthorize("RoleConstants.HAS_ANY_ROLE")
  public ApiResponse<NoteDTO> addFeedback(
      @PathVariable Long noteId,
      @RequestBody @Valid NoteFeedbackDTO feedbackDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info(
        "用户 {} 为读书笔记 {} 添加反馈：{}",
        userPrincipal.getUsername(),
        noteId,
        feedbackDTO.getFeedbackType());
    NoteDTO updatedNote =
        noteService.addOrUpdateFeedback(
            noteId, userPrincipal.getUserId(), feedbackDTO.getFeedbackType());
    return ApiResponse.success(updatedNote);
  }

  /**
   * 获取读书笔记的所有评论
   *
   * @param noteId 笔记 ID
   * @return 评论列表
   */
  @GetMapping("/{noteId}/comments")
  public ApiResponse<List<NoteCommentDTO>> getNoteComments(@PathVariable Long noteId) {
    log.info("获取读书笔记 {} 的所有评论", noteId);
    List<NoteCommentDTO> comments = noteService.getNoteComments(noteId);
    return ApiResponse.success(comments);
  }

  /**
   * 为读书笔记添加评论
   *
   * @param noteId 笔记 ID
   * @param commentCreateDTO 评论创建 DTO
   * @param userPrincipal 当前用户
   * @return 创建的评论
   */
  @PostMapping("/{noteId}/comments")
  @PreAuthorize("RoleConstants.HAS_ANY_ROLE")
  public ApiResponse<NoteCommentDTO> addComment(
      @PathVariable Long noteId,
      @RequestBody @Valid NoteCommentCreateDTO commentCreateDTO,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户 {} 为读书笔记 {} 添加评论", userPrincipal.getUsername(), noteId);
    NoteCommentDTO createdComment =
        noteService.addComment(noteId, userPrincipal.getUserId(), commentCreateDTO);
    return ApiResponse.created(createdComment);
  }

  /**
   * 删除读书笔记评论
   *
   * @param noteId 笔记 ID
   * @param commentId 评论 ID
   * @param userPrincipal 当前用户
   * @return 删除结果
   */
  @DeleteMapping("/{noteId}/comments/{commentId}")
  @PreAuthorize("RoleConstants.HAS_ANY_ROLE")
  public ApiResponse<String> deleteComment(
      @PathVariable Long noteId,
      @PathVariable Long commentId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("用户 {} 删除评论 {}", userPrincipal.getUsername(), commentId);
    noteService.deleteComment(commentId, userPrincipal.getUserId());
    return ApiResponse.success("删除成功");
  }

  /**
   * 管理员删除读书笔记评论
   *
   * @param noteId 笔记 ID
   * @param commentId 评论 ID
   * @param userPrincipal 当前用户
   * @return 删除结果
   */
  @DeleteMapping("/admin/{noteId}/comments/{commentId}")
  @PreAuthorize(RoleConstants.HAS_ROLE_ADMIN)
  public ApiResponse<String> deleteCommentByAdmin(
      @PathVariable Long noteId,
      @PathVariable Long commentId,
      @AuthenticationPrincipal UserPrincipal userPrincipal) {
    log.info("管理员 {} 删除评论 {}", userPrincipal.getUsername(), commentId);
    noteService.deleteCommentByAdmin(commentId);
    return ApiResponse.success("删除成功");
  }
}
