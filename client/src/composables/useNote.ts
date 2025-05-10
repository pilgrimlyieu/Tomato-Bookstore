import { Routes } from "@/constants/routes";
import { useNoteStore } from "@/stores/note";
import { useProductStore } from "@/stores/product";
import type {
  FeedbackType,
  Note,
  NoteCommentCreateParams,
  NoteCreateParams,
  NoteUpdateParams,
} from "@/types/note";
import { buildRoute } from "@/utils/routeHelper";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAuth } from "./useAuth";

/**
 * 读书笔记相关的组合式函数
 */
export function useNote() {
  const noteStore = useNoteStore();
  const productStore = useProductStore();
  const router = useRouter();
  const { checkLogin, currentUser } = useAuth();

  /**
   * 创建读书笔记
   *
   * @param {number} productId 商品 ID
   * @param {NoteCreateParams} params 笔记参数
   * @returns {Promise<boolean>} 是否创建成功
   */
  const createNote = async (
    productId: number,
    params: NoteCreateParams,
  ): Promise<boolean> => {
    // 先检查用户是否已登录
    if (!(await checkLogin())) {
      return false;
    }

    try {
      const success = await noteStore.createNote(productId, params);

      if (success && noteStore.currentNote) {
        // 创建成功后跳转到笔记详情页
        router.push(
          buildRoute(Routes.NOTE_DETAIL, { noteId: noteStore.currentNote.id }),
        );
      }

      return success;
    } catch (error) {
      console.error("创建读书笔记失败：", error);
      ElMessage.error("创建读书笔记失败，请稍后再试");
      return false;
    }
  };

  /**
   * 更新读书笔记
   *
   * @param {number} noteId 笔记 ID
   * @param {NoteUpdateParams} params 笔记参数
   * @returns {Promise<boolean>} 是否更新成功
   */
  const updateNote = async (
    noteId: number,
    params: NoteUpdateParams,
  ): Promise<boolean> => {
    // 先检查用户是否已登录
    if (!(await checkLogin())) {
      return false;
    }

    try {
      const success = await noteStore.updateNote(noteId, params);

      if (success) {
        // 更新成功后跳转到笔记详情页
        router.push(buildRoute(Routes.NOTE_DETAIL, { noteId }));
      }

      return success;
    } catch (error) {
      console.error("更新读书笔记失败：", error);
      ElMessage.error("更新读书笔记失败，请稍后再试");
      return false;
    }
  };

  /**
   * 删除读书笔记
   *
   * @param {number} noteId 笔记 ID
   * @param {number} productId 商品 ID，用于删除后跳转
   * @returns {Promise<boolean>} 是否删除成功
   */
  const deleteNote = async (
    noteId: number,
    productId?: number,
  ): Promise<boolean> => {
    // 先检查用户是否已登录
    if (!(await checkLogin())) {
      return false;
    }

    try {
      // 弹出确认框
      await ElMessageBox.confirm(
        "确定要删除这篇读书笔记吗？此操作不可恢复。",
        "删除确认",
        {
          confirmButtonText: "确定删除",
          cancelButtonText: "取消",
          type: "warning",
        },
      );

      const success = await noteStore.deleteNote(noteId);

      if (success) {
        // 删除成功后，根据情况跳转
        if (productId) {
          router.push(buildRoute(Routes.PRODUCT_DETAIL, { id: productId }));
        } else {
          router.push(Routes.USER_NOTES);
        }
      }

      return success;
    } catch (error) {
      if (error === "cancel" || error === "close") {
        return false;
      }
      console.error("删除读书笔记失败：", error);
      ElMessage.error("删除读书笔记失败，请稍后再试");
      return false;
    }
  };

  /**
   * 添加反馈（点赞/点踩）
   *
   * @param {number} noteId 笔记 ID
   * @param {FeedbackType} feedbackType 反馈类型
   * @returns {Promise<boolean>} 是否操作成功
   */
  const addFeedback = async (
    noteId: number,
    feedbackType: FeedbackType,
  ): Promise<boolean> => {
    // 先检查用户是否已登录
    if (!(await checkLogin())) {
      return false;
    }

    try {
      return await noteStore.addFeedback(noteId, feedbackType);
    } catch (error) {
      console.error("添加反馈失败：", error);
      ElMessage.error("操作失败，请稍后再试");
      return false;
    }
  };

  /**
   * 添加评论
   *
   * @param {number} noteId 笔记 ID
   * @param {NoteCommentCreateParams} params 评论参数
   * @param {() => void} [resetForm] 重置表单的回调函数
   * @returns {Promise<boolean>} 是否添加成功
   */
  const addComment = async (
    noteId: number,
    params: NoteCommentCreateParams,
    resetForm?: () => void,
  ): Promise<boolean> => {
    // 先检查用户是否已登录
    if (!(await checkLogin())) {
      ElMessage.error("请先登录再进行操作");
      router.push(
        buildRoute(Routes.USER_LOGIN, {
          redirect: router.currentRoute.value.fullPath,
        }),
      );
      return false;
    }

    try {
      const success = await noteStore.addComment(noteId, params);
      if (success && resetForm) {
        resetForm();
      }

      return success;
    } catch (error) {
      console.error("添加评论失败：", error);
      ElMessage.error("添加评论失败，请稍后再试");
      return false;
    }
  };

  /**
   * 删除评论
   *
   * @param {number} noteId 笔记 ID
   * @param {number} commentId 评论 ID
   * @returns {Promise<boolean>} 是否删除成功
   */
  const deleteComment = async (
    noteId: number,
    commentId: number,
  ): Promise<boolean> => {
    // 先检查用户是否已登录
    if (!(await checkLogin())) {
      return false;
    }

    try {
      // 弹出确认框
      await ElMessageBox.confirm("确定要删除这条评论吗？", "删除确认", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
      });

      return await noteStore.deleteComment(noteId, commentId);
    } catch (error) {
      if (error === "cancel" || error === "close") {
        return false;
      }
      console.error("删除评论失败：", error);
      ElMessage.error("删除评论失败，请稍后再试");
      return false;
    }
  };

  /**
   * 笔记列表与对应的商品名称
   */
  const userNotesWithProductNames = computed(() => {
    return noteStore.userNotes.map((note) => ({
      ...note,
      productName:
        productStore.products.find((p) => p.id === note.productId)?.title ||
        note.productTitle ||
        "未知书名",
    }));
  });

  return {
    // 状态
    loading: computed(() => noteStore.loading),
    commentLoading: computed(() => noteStore.commentLoading),
    productNotes: computed(() => noteStore.productNotes),
    userNotes: computed(() => noteStore.userNotes),
    currentNote: computed(() => noteStore.currentNote),
    noteComments: computed(() => noteStore.noteComments),
    userNotesWithProductNames,

    // 方法
    fetchProductNotes: noteStore.fetchProductNotes,
    fetchUserNotes: noteStore.fetchUserNotes,
    fetchNoteById: noteStore.fetchNoteById,
    fetchNoteComments: noteStore.fetchNoteComments,
    createNote,
    updateNote,
    deleteNote,
    addFeedback,
    addComment,
    deleteComment,

    // 工具函数
    setCurrentNote: noteStore.setCurrentNote,
    clearCurrentNote: noteStore.clearCurrentNote,
    clearComments: noteStore.clearComments,
  };
}
