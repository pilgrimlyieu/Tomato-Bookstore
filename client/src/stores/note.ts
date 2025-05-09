import noteService from "@/services/note-service";
import type {
  FeedbackType,
  Note,
  NoteComment,
  NoteCommentCreateParams,
  NoteCreateParams,
  NoteFeedbackParams,
  NoteUpdateParams,
} from "@/types/note";
import { performAsyncAction } from "@/utils/asyncHelper";
import { HttpStatusCode } from "axios";
import { defineStore } from "pinia";

/**
 * 读书笔记状态管理
 */
export const useNoteStore = defineStore("note", {
  state: () => ({
    productNotes: [] as Note[], // 当前查看的商品读书笔记列表
    userNotes: [] as Note[], // 当前用户的读书笔记列表
    managedUserNotes: [] as Note[], // 管理员查看的用户读书笔记列表（仅管理员）
    allNotes: [] as Note[], // 所有读书笔记列表（仅管理员）
    currentNote: null as Note | null, // 当前操作的读书笔记
    noteComments: [] as NoteComment[], // 当前读书笔记的评论列表
    loading: false, // 加载状态
    commentLoading: false, // 评论加载状态
  }),

  getters: {
    /**
     * 当前用户是否已为指定商品创建读书笔记
     *
     * @param {number} productId 商品 ID
     * @param {number} userId 用户 ID
     * @returns {boolean} 是否已创建
     */
    hasUserCreatedNote:
      (state) =>
      (productId: number, userId: number): boolean => {
        return state.productNotes.some(
          (note) => note.productId === productId && note.userId === userId,
        );
      },

    /**
     * 获取当前用户对指定商品的读书笔记
     *
     * @param {number} productId 商品 ID
     * @param {number} userId 用户 ID
     * @returns {Note | undefined} 读书笔记信息
     */
    getUserNoteForProduct:
      (state) =>
      (productId: number, userId: number): Note | undefined => {
        return state.productNotes.find(
          (note) => note.productId === productId && note.userId === userId,
        );
      },
  },

  actions: {
    /**
     * 获取指定商品的所有读书笔记
     *
     * @param {number} productId 商品 ID
     * @returns {Promise<void>}
     */
    async fetchProductNotes(productId: number): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => noteService.getProductNotes(productId),
        (data) => {
          this.productNotes = data;
        },
        `获取商品读书笔记失败（ID: ${productId}）：`,
        true,
      );
    },

    /**
     * 获取当前用户的所有读书笔记
     *
     * @returns {Promise<void>}
     */
    async fetchUserNotes(): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => noteService.getUserNotes(),
        (data) => {
          this.userNotes = data;
        },
        "获取用户读书笔记列表失败：",
        true,
      );
    },

    /**
     * 获取指定用户的所有读书笔记（管理员）
     *
     * @param {number} userId 用户 ID
     * @returns {Promise<void>}
     */
    async fetchUserNotesByAdmin(userId: number): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => noteService.getUserNotesByAdmin(userId),
        (data) => {
          this.managedUserNotes = data;
        },
        `获取用户读书笔记列表失败（用户ID: ${userId}）：`,
        true,
      );
    },

    /**
     * 获取所有读书笔记（仅管理员）
     *
     * @returns {Promise<void>}
     */
    async fetchAllNotes(): Promise<void> {
      await performAsyncAction(
        this,
        "loading",
        () => noteService.getAllNotes(),
        (data) => {
          this.allNotes = data;
        },
        "获取所有读书笔记失败：",
        true,
      );
    },

    /**
     * 获取读书笔记详情
     *
     * @param {number} noteId 笔记 ID
     * @returns {Promise<Note | null>} 读书笔记详情
     */
    async fetchNoteById(noteId: number): Promise<Note | null> {
      let fetchedNote: Note | null = null;

      await performAsyncAction(
        this,
        "loading",
        () => noteService.getNoteById(noteId),
        (data) => {
          this.currentNote = data;
          fetchedNote = data;
        },
        `获取读书笔记详情失败（ID: ${noteId}）：`,
        true,
      );

      return fetchedNote;
    },

    /**
     * 创建读书笔记
     *
     * @param {number} productId 商品 ID
     * @param {NoteCreateParams} params 读书笔记参数
     * @returns {Promise<boolean>} 是否创建成功
     */
    async createNote(
      productId: number,
      params: NoteCreateParams,
    ): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => noteService.createNote(productId, params),
        (data) => {
          // 更新本地商品读书笔记列表
          this.productNotes.unshift(data);
          // 更新用户读书笔记列表
          this.userNotes.unshift(data);
          // 设置当前读书笔记
          this.currentNote = data;
        },
        `创建读书笔记失败（商品ID: ${productId}）：`,
        true,
        [HttpStatusCode.Created],
        "读书笔记发布成功",
      );
    },

    /**
     * 更新读书笔记
     *
     * @param {number} noteId 笔记 ID
     * @param {NoteUpdateParams} params 更新参数
     * @returns {Promise<boolean>} 是否更新成功
     */
    async updateNote(
      noteId: number,
      params: NoteUpdateParams,
    ): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => noteService.updateNote(noteId, params),
        (data) => {
          // 更新本地数据
          this.updateLocalNoteData(data);
        },
        `更新读书笔记失败（ID: ${noteId}）：`,
        true,
        [HttpStatusCode.Ok],
        "读书笔记更新成功",
      );
    },

    /**
     * 管理员更新读书笔记
     *
     * @param {number} noteId 笔记 ID
     * @param {NoteUpdateParams} params 更新参数
     * @returns {Promise<boolean>} 是否更新成功
     */
    async updateNoteByAdmin(
      noteId: number,
      params: NoteUpdateParams,
    ): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => noteService.updateNoteByAdmin(noteId, params),
        (data) => {
          // 更新本地数据
          this.updateLocalNoteData(data);
        },
        `管理员更新读书笔记失败（ID: ${noteId}）：`,
        true,
        [HttpStatusCode.Ok],
        "读书笔记已修改",
      );
    },

    /**
     * 删除读书笔记
     *
     * @param {number} noteId 笔记 ID
     * @returns {Promise<boolean>} 是否删除成功
     */
    async deleteNote(noteId: number): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => noteService.deleteNote(noteId),
        () => {
          // 从本地列表中移除
          this.removeLocalNote(noteId);
        },
        `删除读书笔记失败（ID: ${noteId}）：`,
        true,
        [HttpStatusCode.Ok],
        "读书笔记已删除",
      );
    },

    /**
     * 管理员删除读书笔记
     *
     * @param {number} noteId 笔记 ID
     * @returns {Promise<boolean>} 是否删除成功
     */
    async deleteNoteByAdmin(noteId: number): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => noteService.deleteNoteByAdmin(noteId),
        () => {
          // 从本地列表中移除
          this.removeLocalNote(noteId);
        },
        `管理员删除读书笔记失败（ID: ${noteId}）：`,
        true,
        [HttpStatusCode.Ok],
        "读书笔记已删除",
      );
    },

    /**
     * 为读书笔记添加反馈（点赞/点踩）
     *
     * @param {number} noteId 笔记 ID
     * @param {FeedbackType} feedbackType 反馈类型
     * @returns {Promise<boolean>} 是否操作成功
     */
    async addFeedback(
      noteId: number,
      feedbackType: FeedbackType,
    ): Promise<boolean> {
      const params: NoteFeedbackParams = { feedbackType };

      return await performAsyncAction(
        this,
        "loading",
        () => noteService.addFeedback(noteId, params),
        (data) => {
          // 更新本地数据
          this.updateLocalNoteData(data);
        },
        `添加反馈失败（ID: ${noteId}）：`,
        true,
      );
    },

    /**
     * 获取读书笔记的评论
     *
     * @param {number} noteId 笔记 ID
     * @returns {Promise<void>}
     */
    async fetchNoteComments(noteId: number): Promise<void> {
      await performAsyncAction(
        this,
        "commentLoading",
        () => noteService.getNoteComments(noteId),
        (data) => {
          this.noteComments = data;
        },
        `获取读书笔记评论失败（ID: ${noteId}）：`,
        true,
      );
    },

    /**
     * 添加评论
     *
     * @param {number} noteId 笔记 ID
     * @param {NoteCommentCreateParams} params 评论参数
     * @returns {Promise<boolean>} 是否添加成功
     */
    async addComment(
      noteId: number,
      params: NoteCommentCreateParams,
    ): Promise<boolean> {
      return await performAsyncAction(
        this,
        "commentLoading",
        () => noteService.addComment(noteId, params),
        (data) => {
          // 将新评论添加到列表开头
          this.noteComments.unshift(data);

          // 如果当前正在查看的是这条笔记，更新评论数
          if (this.currentNote && this.currentNote.id === noteId) {
            this.currentNote.commentCount += 1;
          }

          // 在其他列表中也更新评论数
          this.updateCommentCountInLists(noteId, 1);
        },
        `添加评论失败（笔记ID: ${noteId}）：`,
        true,
        [HttpStatusCode.Created],
        "评论发布成功",
      );
    },

    /**
     * 删除评论
     *
     * @param {number} noteId 笔记 ID
     * @param {number} commentId 评论 ID
     * @returns {Promise<boolean>} 是否删除成功
     */
    async deleteComment(noteId: number, commentId: number): Promise<boolean> {
      return await performAsyncAction(
        this,
        "commentLoading",
        () => noteService.deleteComment(noteId, commentId),
        () => {
          // 从列表中移除评论
          this.noteComments = this.noteComments.filter(
            (comment) => comment.id !== commentId,
          );

          // 如果当前正在查看的是这条笔记，更新评论数
          if (this.currentNote && this.currentNote.id === noteId) {
            this.currentNote.commentCount = Math.max(
              0,
              this.currentNote.commentCount - 1,
            );
          }

          // 在其他列表中也更新评论数
          this.updateCommentCountInLists(noteId, -1);
        },
        `删除评论失败（ID: ${commentId}）：`,
        true,
        [HttpStatusCode.Ok],
        "评论已删除",
      );
    },

    /**
     * 管理员删除评论
     *
     * @param {number} noteId 笔记 ID
     * @param {number} commentId 评论 ID
     * @returns {Promise<boolean>} 是否删除成功
     */
    async deleteCommentByAdmin(
      noteId: number,
      commentId: number,
    ): Promise<boolean> {
      return await performAsyncAction(
        this,
        "commentLoading",
        () => noteService.deleteCommentByAdmin(noteId, commentId),
        () => {
          // 从列表中移除评论
          this.noteComments = this.noteComments.filter(
            (comment) => comment.id !== commentId,
          );

          // 如果当前正在查看的是这条笔记，更新评论数
          if (this.currentNote && this.currentNote.id === noteId) {
            this.currentNote.commentCount = Math.max(
              0,
              this.currentNote.commentCount - 1,
            );
          }

          // 在其他列表中也更新评论数
          this.updateCommentCountInLists(noteId, -1);
        },
        `管理员删除评论失败（ID: ${commentId}）：`,
        true,
        [HttpStatusCode.Ok],
        "评论已删除",
      );
    },

    /**
     * 更新本地读书笔记数据
     *
     * @param {Note} updatedNote 更新后的读书笔记
     */
    updateLocalNoteData(updatedNote: Note): void {
      // 定义一个辅助函数来更新列表中的笔记
      const updateInList = (list: Note[]) => {
        const index = list.findIndex((note) => note.id === updatedNote.id);
        if (index !== -1) {
          list[index] = updatedNote;
        }
      };

      // 更新所有列表
      updateInList(this.productNotes);
      updateInList(this.userNotes);
      updateInList(this.managedUserNotes);
      updateInList(this.allNotes);

      // 更新当前读书笔记
      if (this.currentNote && this.currentNote.id === updatedNote.id) {
        this.currentNote = updatedNote;
      }
    },

    /**
     * 从本地移除读书笔记
     *
     * @param {number} noteId 笔记 ID
     */
    removeLocalNote(noteId: number): void {
      // 从所有列表中移除指定 ID 的笔记
      this.productNotes = this.productNotes.filter(
        (note) => note.id !== noteId,
      );
      this.userNotes = this.userNotes.filter((note) => note.id !== noteId);
      this.managedUserNotes = this.managedUserNotes.filter(
        (note) => note.id !== noteId,
      );
      this.allNotes = this.allNotes.filter((note) => note.id !== noteId);

      if (this.currentNote && this.currentNote.id === noteId) {
        this.currentNote = null;
      }
    },

    /**
     * 更新所有列表中指定笔记的评论数
     *
     * @param {number} noteId 笔记 ID
     * @param {number} delta 变化量，可以是正数或负数
     */
    updateCommentCountInLists(noteId: number, delta: number): void {
      const updateCommentCount = (list: Note[]) => {
        const note = list.find((n) => n.id === noteId);
        if (note) {
          note.commentCount = Math.max(0, note.commentCount + delta);
        }
      };

      updateCommentCount(this.productNotes);
      updateCommentCount(this.userNotes);
      updateCommentCount(this.managedUserNotes);
      updateCommentCount(this.allNotes);
    },

    /**
     * 设置当前操作的读书笔记
     *
     * @param {Note | null} note 读书笔记对象
     */
    setCurrentNote(note: Note | null): void {
      this.currentNote = note;
    },

    /**
     * 清除当前操作的读书笔记
     */
    clearCurrentNote(): void {
      this.currentNote = null;
    },

    /**
     * 清除评论列表
     */
    clearComments(): void {
      this.noteComments = [];
    },
  },
});
