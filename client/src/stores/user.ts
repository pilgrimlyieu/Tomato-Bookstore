import userService from "@/services/user-service";
import {
  type LoginParams,
  type RegisterParams,
  type UpdateUserParams,
  type User,
  UserRole,
} from "@/types/user";
import { HttpStatusCode } from "axios";
import { ElMessage } from "element-plus";
import { defineStore } from "pinia";

export const useUserStore = defineStore("user", {
  state: () => ({
    token: localStorage.getItem("token") || "",
    user: null as User | null,
    loading: false,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === UserRole.ADMIN,
    username: (state) => state.user?.username || "",
  },

  actions: {
    /**
     * 设置 token 并保存到本地存储
     *
     * @param {string} token
     * @returns {void}
     */
    setToken(token: string): void {
      this.token = token;
      localStorage.setItem("token", token);
    },

    /**
     * 用户登录
     *
     * @param {LoginParams} params 登录参数
     * @returns {Promise<boolean>} 是否登录成功
     */
    async login(params: LoginParams): Promise<boolean> {
      try {
        this.loading = true;
        const response = await userService.login(params);

        if (response.code === HttpStatusCode.Ok) {
          this.setToken(response.data);
          await this.fetchUserInfo();
          ElMessage.success("登录成功");
          return true;
        }
        return false;
      } catch (error) {
        console.error("登录失败：", error);
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 用户注册
     *
     * @param {RegisterParams} params 注册参数
     * @returns {Promise<boolean>} 是否注册成功
     */
    async register(params: RegisterParams): Promise<boolean> {
      try {
        this.loading = true;
        const response = await userService.register(params);
        if (response.code === HttpStatusCode.Created) {
          ElMessage.success("注册成功，请登录");
          return true;
        }
        return false;
      } catch (error) {
        console.error("注册失败：", error);
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 获取用户信息
     *
     * @returns {Promise<void>}
     */
    async fetchUserInfo(): Promise<void> {
      if (!this.token) return;

      try {
        this.loading = true;
        const response = await userService.getCurrentUser();

        if (response.code === HttpStatusCode.Ok) {
          this.user = response.data;
        }
      } catch (error: any) {
        console.error("获取用户信息失败：", error);
        if (error.response?.status === HttpStatusCode.Unauthorized) {
          // 如果获取用户信息失败，可能 token 无效，清除登录状态
          this.logout();
        }
      } finally {
        this.loading = false;
      }
    },

    /**
     * 更新用户信息
     *
     * @param {UpdateUserParams} params 更新参数
     * @returns {Promise<boolean>} 是否更新成功
     */
    async updateUserProfile(params: UpdateUserParams): Promise<boolean> {
      try {
        this.loading = true;
        const response = await userService.updateUserProfile(params);

        if (response.code === HttpStatusCode.Ok) {
          this.user = response.data;
          ElMessage.success("个人信息更新成功");
          return true;
        }
        return false;
      } catch (error) {
        console.error("更新用户信息失败：", error);
        return false;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 退出登录
     *
     * @returns {void}
     */
    logout(): void {
      this.token = "";
      this.user = null;
      localStorage.removeItem("token");
      ElMessage.success("已退出登录");
    },
  },
});
