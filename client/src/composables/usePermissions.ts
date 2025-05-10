import type { Note } from "@/types/note";
import { computed } from "vue";
import { useAuth } from "./useAuth";

/**
 * 权限相关的组合式函数
 */
export function usePermissions() {
  const { currentUser, isAdmin } = useAuth();

  /**
   * 检查用户是否有权限编辑指定的笔记
   *
   * @param note 笔记对象
   * @returns 是否有权限编辑
   */
  const canEditNote = (note: Note) => {
    return currentUser.value && currentUser.value.id === note.userId;
  };

  /**
   * 检查用户是否有权限删除指定的笔记
   *
   * @param note 笔记对象
   * @returns 是否有权限删除
   */
  const canDeleteNote = (note: Note) => {
    return (
      (currentUser.value && currentUser.value.id === note.userId) ||
      isAdmin.value
    );
  };

  return {
    canEditNote,
    canDeleteNote,
    isAdmin,
  };
}
