import { Routes } from "@/constants/routes";
import { useUserStore } from "@/stores/user";
import { ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";

/**
 * 认证相关的组合式函数
 */
export function useAuth() {
  const userStore = useUserStore();
  const router = useRouter();

  /**
   * 检查用户是否已登录，如果未登录则提示用户登录
   *
   * @param {string} redirectPath 登录成功后重定向的路径，默认为当前路径
   * @returns {Promise<boolean>} 用户是否已登录
   */
  const checkLogin = async (redirectPath?: string): Promise<boolean> => {
    // 如果用户已登录，直接返回 true
    if (userStore.isLoggedIn) {
      return true;
    }

    // 如果未指定重定向路径，则使用当前路径
    const redirect = redirectPath || router.currentRoute.value.fullPath;

    try {
      // 显示登录提示
      await ElMessageBox.confirm("您需要登录后才能继续操作", "提示", {
        confirmButtonText: "去登录",
        cancelButtonText: "取消",
        type: "info",
      });

      // 导航到登录页面，并带上重定向参数
      router.push({
        path: Routes.USER_LOGIN,
        query: { redirect },
      });
      return false;
    } catch {
      // 用户取消了登录操作
      return false;
    }
  };

  return {
    checkLogin,
  };
}
