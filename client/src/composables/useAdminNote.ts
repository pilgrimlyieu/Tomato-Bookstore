import { useNoteStore } from "@/stores/note";
import type { NoteCreateParams } from "@/types/note";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed } from "vue";
import { useAuth } from "./useAuth";

/**
 * 管理员管理读书笔记相关的组合式函数
 */
export function useAdminNote() {
  const noteStore = useNoteStore();
  const { isAdmin } = useAuth();

  /**
   * 管理员更新读书笔记
   *
   * @param {number} noteId 笔记 ID
   * @param {NoteCreateParams} params 笔记参数
   * @returns {Promise<boolean>} 是否更新成功
   */
  const updateNoteByAdmin = async (
    noteId: number,
    params: NoteCreateParams,
  ): Promise<boolean> => {
    // 检查是否为管理员
    if (!isAdmin.value) {
      ElMessage.error("您没有权限执行此操作");
      return false;
    }

    try {
      return await noteStore.updateNoteByAdmin(noteId, params);
    } catch (error) {
      console.error("管理员更新读书笔记失败：", error);
      ElMessage.error("更新读书笔记失败，请稍后再试");
      return false;
    }
  };

  /**
   * 管理员删除读书笔记
   *
   * @param {number} noteId 笔记 ID
   * @returns {Promise<boolean>} 是否删除成功
   */
  const deleteNoteByAdmin = async (noteId: number): Promise<boolean> => {
    // 检查是否为管理员
    if (!isAdmin.value) {
      ElMessage.error("您没有权限执行此操作");
      return false;
    }

    try {
      // 弹出确认框
      await ElMessageBox.confirm(
        "确定要删除这篇读书笔记吗？此操作不可恢复。",
        "管理员删除确认",
        {
          confirmButtonText: "确定删除",
          cancelButtonText: "取消",
          type: "warning",
        },
      );

      return await noteStore.deleteNoteByAdmin(noteId);
    } catch (error) {
      if (error === "cancel") {
        return false;
      }
      console.error("管理员删除读书笔记失败：", error);
      ElMessage.error("删除读书笔记失败，请稍后再试");
      return false;
    }
  };

  /**
   * 管理员删除评论
   *
   * @param {number} noteId 笔记 ID
   * @param {number} commentId 评论 ID
   * @returns {Promise<boolean>} 是否删除成功
   */
  const deleteCommentByAdmin = async (
    noteId: number,
    commentId: number,
  ): Promise<boolean> => {
    // 检查是否为管理员
    if (!isAdmin.value) {
      ElMessage.error("您没有权限执行此操作");
      return false;
    }

    try {
      // 弹出确认框
      await ElMessageBox.confirm("确定要删除这条评论吗？", "管理员删除确认", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
      });

      return await noteStore.deleteCommentByAdmin(noteId, commentId);
    } catch (error) {
      if (error === "cancel") {
        return false;
      }
      console.error("管理员删除评论失败：", error);
      ElMessage.error("删除评论失败，请稍后再试");
      return false;
    }
  };

  /**
   * 所有笔记数据
   */
  const allNotes = computed(() => noteStore.allNotes);

  /**
   * 管理员查看的用户笔记列表
   */
  const managedUserNotes = computed(() => noteStore.managedUserNotes);

  /**
   * 加载状态
   */
  const loading = computed(() => noteStore.loading);

  return {
    // 状态
    loading,
    allNotes,
    managedUserNotes,

    // 方法
    fetchAllNotes: noteStore.fetchAllNotes,
    fetchUserNotesByAdmin: noteStore.fetchUserNotesByAdmin,
    updateNoteByAdmin,
    deleteNoteByAdmin,
    deleteCommentByAdmin,
  };
}
