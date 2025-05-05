import userService from "@/services/user-service";
import {
  type LoginParams,
  type RegisterParams,
  type UpdateUserParams,
  type User,
  UserRole,
} from "@/types/user";
import { performAsyncAction } from "@/utils/asyncHelper";
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
      return await performAsyncAction(
        this,
        "loading",
        () => userService.login(params),
        async (token) => {
          this.setToken(token);
          // 登录成功后获取用户信息
          await this.fetchUserInfo();
        },
        "登录失败：",
        true,
        [HttpStatusCode.Ok],
        "登录成功",
      );
    },

    /**
     * 用户注册
     *
     * @param {RegisterParams} params 注册参数
     * @returns {Promise<boolean>} 是否注册成功
     */
    async register(params: RegisterParams): Promise<boolean> {
      return await performAsyncAction(
        this,
        "loading",
        () => userService.register(params),
        () => {
          // 注册成功不需要特别处理
        },
        "注册失败：",
        true,
        [HttpStatusCode.Created],
        "注册成功，请登录",
      );
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
      return await performAsyncAction(
        this,
        "loading",
        () => userService.updateUserProfile(params),
        (data) => {
          this.user = data;
        },
        "更新用户信息失败：",
        true,
        [HttpStatusCode.Ok],
        "个人信息更新成功",
      );
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
