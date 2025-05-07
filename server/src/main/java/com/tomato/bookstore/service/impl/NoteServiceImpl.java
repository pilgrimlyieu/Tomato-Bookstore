package com.tomato.bookstore.service.impl;

import com.tomato.bookstore.constant.BusinessErrorCode;
import com.tomato.bookstore.constant.FeedbackType;
import com.tomato.bookstore.dto.NoteCommentCreateDTO;
import com.tomato.bookstore.dto.NoteCommentDTO;
import com.tomato.bookstore.dto.NoteCreateDTO;
import com.tomato.bookstore.dto.NoteDTO;
import com.tomato.bookstore.exception.BusinessException;
import com.tomato.bookstore.exception.ResourceNotFoundException;
import com.tomato.bookstore.model.Note;
import com.tomato.bookstore.model.NoteComment;
import com.tomato.bookstore.model.NoteFeedback;
import com.tomato.bookstore.model.Product;
import com.tomato.bookstore.model.User;
import com.tomato.bookstore.repository.NoteCommentRepository;
import com.tomato.bookstore.repository.NoteFeedbackRepository;
import com.tomato.bookstore.repository.NoteRepository;
import com.tomato.bookstore.repository.ProductRepository;
import com.tomato.bookstore.repository.UserRepository;
import com.tomato.bookstore.service.NoteService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 读书笔记服务实现类 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {

  private final NoteRepository noteRepository;
  private final NoteFeedbackRepository noteFeedbackRepository;
  private final NoteCommentRepository noteCommentRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  @Override
  public List<NoteDTO> getNotesByProductId(Long productId) {
    // 确认商品存在
    getProductById(productId);

    List<Note> notes = noteRepository.findByProductIdOrderByCreatedAtDesc(productId);
    return notes.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  @Override
  public List<NoteDTO> getNotesByUserId(Long userId) {
    // 确认用户存在
    getUserById(userId);

    List<Note> notes = noteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    return notes.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  @Override
  public List<NoteDTO> getAllNotes() {
    List<Note> notes = noteRepository.findAll();
    return notes.stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  @Override
  public NoteDTO getNoteById(Long noteId, Long currentUserId) {
    Note note = getNoteEntityById(noteId);

    FeedbackType userFeedback = null; // 默认为 null，表示没有反馈
    if (currentUserId != null) {
      Optional<NoteFeedback> feedback =
          noteFeedbackRepository.findByNoteIdAndUserId(noteId, currentUserId);
      if (feedback.isPresent()) {
        userFeedback = feedback.get().getFeedbackType();
      }
    }

    return convertToDTO(note, userFeedback);
  }

  @Override
  @Transactional
  public NoteDTO createNote(Long productId, Long userId, NoteCreateDTO noteCreateDTO) {
    // 检查商品是否存在
    Product product = getProductById(productId);

    // 检查用户是否存在
    User user = getUserById(userId);

    // 检查用户是否已经为该商品创建过笔记
    if (noteRepository.existsByProductIdAndUserId(productId, userId)) {
      throw new BusinessException(BusinessErrorCode.NOTE_ALREADY_EXISTS);
    }

    Note note =
        Note.builder()
            .title(noteCreateDTO.getTitle())
            .content(noteCreateDTO.getContent())
            .productId(productId)
            .userId(userId)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Note savedNote = noteRepository.save(note);
    log.info("用户 {} 为商品 {} 创建了读书笔记", userId, productId);

    return convertToDTO(savedNote);
  }

  @Override
  @Transactional
  public NoteDTO updateNote(Long noteId, Long userId, NoteCreateDTO noteCreateDTO) {
    Note note = getNoteEntityById(noteId);

    // 检查权限
    if (!note.getUserId().equals(userId)) {
      throw new BusinessException(BusinessErrorCode.ACCESS_DENIED);
    }

    note.setTitle(noteCreateDTO.getTitle());
    note.setContent(noteCreateDTO.getContent());
    note.setUpdatedAt(LocalDateTime.now());

    Note updatedNote = noteRepository.save(note);
    log.info("用户 {} 更新了笔记 {}", userId, noteId);

    return convertToDTO(updatedNote);
  }

  @Override
  @Transactional
  public NoteDTO updateNoteByAdmin(Long noteId, NoteCreateDTO noteCreateDTO) {
    Note note = getNoteEntityById(noteId);

    note.setTitle(noteCreateDTO.getTitle());
    note.setContent(noteCreateDTO.getContent());
    note.setUpdatedAt(LocalDateTime.now());

    Note updatedNote = noteRepository.save(note);
    log.info("管理员更新了笔记 {}", noteId);

    return convertToDTO(updatedNote);
  }

  @Override
  @Transactional
  public void deleteNote(Long noteId, Long userId) {
    Note note = getNoteEntityById(noteId);

    // 检查权限
    if (!note.getUserId().equals(userId)) {
      throw new BusinessException(BusinessErrorCode.ACCESS_DENIED);
    }

    // 删除笔记相关的所有评论
    noteCommentRepository.deleteByNoteId(noteId);

    // 删除笔记
    noteRepository.delete(note);
    log.info("用户 {} 删除了笔记 {}", userId, noteId);
  }

  @Override
  @Transactional
  public void deleteNoteByAdmin(Long noteId) {
    Note note = getNoteEntityById(noteId);

    // 删除笔记相关的所有评论
    noteCommentRepository.deleteByNoteId(noteId);

    // 删除笔记
    noteRepository.delete(note);
    log.info("管理员删除了笔记 {}", noteId);
  }

  @Override
  @Transactional
  public NoteDTO addOrUpdateFeedback(Long noteId, Long userId, FeedbackType feedbackType) {
    // 确认笔记存在
    Note note = getNoteEntityById(noteId);

    // 确认用户存在
    getUserById(userId);

    // 检查用户是否已经反馈过该笔记
    Optional<NoteFeedback> existingFeedback =
        noteFeedbackRepository.findByNoteIdAndUserId(noteId, userId);

    if (existingFeedback.isPresent()) {
      NoteFeedback feedback = existingFeedback.get();
      // 如果反馈类型相同，则视为取消反馈
      if (feedback.getFeedbackType() == feedbackType) {
        noteFeedbackRepository.delete(feedback);
        log.info("用户 {} 取消了对笔记 {} 的反馈", userId, noteId);
      } else {
        // 更新为新的反馈类型
        feedback.setFeedbackType(feedbackType);
        feedback.setUpdatedAt(LocalDateTime.now());
        noteFeedbackRepository.save(feedback);
        log.info(
            "用户 {} 将对笔记 {} 的反馈从 {} 更改为 {}",
            userId,
            noteId,
            existingFeedback.get().getFeedbackType(),
            feedbackType);
      }
    } else {
      // 创建新的反馈
      NoteFeedback feedback =
          NoteFeedback.builder()
              .noteId(noteId)
              .userId(userId)
              .feedbackType(feedbackType)
              .createdAt(LocalDateTime.now())
              .updatedAt(LocalDateTime.now())
              .build();
      noteFeedbackRepository.save(feedback);
      log.info("用户 {} 对笔记 {} 添加了 {} 反馈", userId, noteId, feedbackType);
    }

    return getNoteById(noteId, userId);
  }

  @Override
  public List<NoteCommentDTO> getNoteComments(Long noteId) {
    // 确认笔记存在
    getNoteEntityById(noteId);

    List<NoteComment> comments = noteCommentRepository.findByNoteIdOrderByCreatedAtDesc(noteId);

    // 获取所有评论的用户 ID
    List<Long> userIds =
        comments.stream().map(NoteComment::getUserId).distinct().collect(Collectors.toList());

    // 一次性获取所有用户信息
    List<User> users = userRepository.findAllById(userIds);

    // 构建用户 ID 到用户信息的映射
    Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

    return comments.stream()
        .map(comment -> convertToCommentDTO(comment, userMap))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public NoteCommentDTO addComment(
      Long noteId, Long userId, NoteCommentCreateDTO commentCreateDTO) {
    // 确认笔记存在
    getNoteEntityById(noteId);

    // 确认用户存在
    User user = getUserById(userId);

    NoteComment comment =
        NoteComment.builder()
            .noteId(noteId)
            .userId(userId)
            .content(commentCreateDTO.getContent())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    NoteComment savedComment = noteCommentRepository.save(comment);
    log.info("用户 {} 为笔记 {} 添加了评论", userId, noteId);

    return NoteCommentDTO.builder()
        .id(savedComment.getId())
        .noteId(savedComment.getNoteId())
        .userId(savedComment.getUserId())
        .username(user.getUsername())
        .userAvatar(user.getAvatar())
        .content(savedComment.getContent())
        .createdAt(savedComment.getCreatedAt())
        .updatedAt(savedComment.getUpdatedAt())
        .build();
  }

  @Override
  @Transactional
  public void deleteComment(Long commentId, Long userId) {
    NoteComment comment = getNoteCommentById(commentId);

    // 检查权限
    if (!comment.getUserId().equals(userId)) {
      throw new BusinessException(BusinessErrorCode.ACCESS_DENIED);
    }

    noteCommentRepository.delete(comment);
    log.info("用户 {} 删除了评论 {}", userId, commentId);
  }

  @Override
  @Transactional
  public void deleteCommentByAdmin(Long commentId) {
    NoteComment comment = getNoteCommentById(commentId);

    noteCommentRepository.delete(comment);
    log.info("管理员删除了评论 {}", commentId);
  }

  /**
   * 将 Note 实体转换为 NoteDTO
   *
   * @param note 笔记实体
   * @return 笔记 DTO
   */
  private NoteDTO convertToDTO(Note note) {
    return convertToDTO(note, null);
  }

  /**
   * 将 Note 实体转换为 NoteDTO，包括用户反馈状态
   *
   * @param note 笔记实体
   * @param userFeedback 用户反馈状态，null 表示无反馈
   * @return 笔记 DTO
   */
  private NoteDTO convertToDTO(Note note, FeedbackType userFeedback) {
    User user = getUserById(note.getUserId());
    Product product = getProductById(note.getProductId());

    // 获取点赞和点踩数
    long likeCount =
        noteFeedbackRepository.countByNoteIdAndFeedbackType(note.getId(), FeedbackType.LIKE);
    long dislikeCount =
        noteFeedbackRepository.countByNoteIdAndFeedbackType(note.getId(), FeedbackType.DISLIKE);

    // 获取评论数
    long commentCount = noteCommentRepository.countByNoteId(note.getId());

    return NoteDTO.builder()
        .id(note.getId())
        .title(note.getTitle())
        .content(note.getContent())
        .productId(note.getProductId())
        .productTitle(product.getTitle())
        .userId(note.getUserId())
        .username(user.getUsername())
        .userAvatar(user.getAvatar())
        .likeCount((int) likeCount)
        .dislikeCount((int) dislikeCount)
        .commentCount((int) commentCount)
        .userFeedback(userFeedback)
        .createdAt(note.getCreatedAt())
        .updatedAt(note.getUpdatedAt())
        .build();
  }

  /**
   * 将 NoteComment 实体转换为 NoteCommentDTO
   *
   * @param comment 评论实体
   * @param userMap 用户映射
   * @return 评论 DTO
   */
  private NoteCommentDTO convertToCommentDTO(NoteComment comment, Map<Long, User> userMap) {
    User user = userMap.get(comment.getUserId());

    if (user == null) {
      log.error("严重的数据一致性问题：用户 {} 不存在但关联了评论 {}", comment.getUserId(), comment.getId());
      return NoteCommentDTO.builder()
          .id(comment.getId())
          .noteId(comment.getNoteId())
          .userId(comment.getUserId())
          .username("未知用户")
          .userAvatar(null)
          .content(comment.getContent())
          .createdAt(comment.getCreatedAt())
          .updatedAt(comment.getUpdatedAt())
          .build();
    }

    return NoteCommentDTO.builder()
        .id(comment.getId())
        .noteId(comment.getNoteId())
        .userId(comment.getUserId())
        .username(user.getUsername())
        .userAvatar(user.getAvatar())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .build();
  }

  /**
   * 获取商品信息
   *
   * @param productId 商品 ID
   * @return 商品信息
   */
  private Product getProductById(Long productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(() -> ResourceNotFoundException.create("商品", "id", productId));
  }

  /**
   * 获取用户信息
   *
   * @param userId 用户 ID
   * @return 用户信息
   */
  private User getUserById(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> ResourceNotFoundException.create("用户", "id", userId));
  }

  /**
   * 获取笔记实体
   *
   * @param noteId 笔记 ID
   * @return 笔记实体
   */
  private Note getNoteEntityById(Long noteId) {
    return noteRepository
        .findById(noteId)
        .orElseThrow(() -> ResourceNotFoundException.create("读书笔记", "id", noteId));
  }

  /**
   * 获取评论实体
   *
   * @param commentId 评论 ID
   * @return 评论实体
   */
  private NoteComment getNoteCommentById(Long commentId) {
    return noteCommentRepository
        .findById(commentId)
        .orElseThrow(() -> ResourceNotFoundException.create("笔记评论", "id", commentId));
  }
}
