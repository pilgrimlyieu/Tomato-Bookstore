import { Routes } from "@/constants/routes";
import type { ApiResponse } from "@/types/api";
import type {
  LoginParams,
  RegisterParams,
  UpdateUserParams,
  User,
} from "@/types/user";
import apiClient from "@/utils/apiClient";

/**
 * 用户服务
 */
export default {
  /**
   * 用户登录
   *
   * @param {LoginParams} params 登录参数
   * @returns {Promise<ApiResponse<string>>} 登录结果
   */
  login(params: LoginParams): Promise<ApiResponse<string>> {
    return apiClient.post(Routes.USER_LOGIN, params);
  },

  /**
   * 用户注册
   *
   * @param {RegisterParams} params 注册参数
   * @returns {Promise<ApiResponse<void>>} 注册结果
   */
  register(params: RegisterParams): Promise<ApiResponse<void>> {
    return apiClient.post(Routes.USER_REGISTER, params);
  },

  /**
   * 获取当前用户信息
   *
   * @returns {Promise<ApiResponse<User>>} 当前用户信息
   */
  getCurrentUser(): Promise<ApiResponse<User>> {
    return apiClient.get(Routes.USER_PROFILE);
  },

  /**
   * 更新用户信息
   *
   * @param {UpdateUserParams} params 更新参数
   * @returns {Promise<ApiResponse<User>>} 更新结果
   */
  updateUserProfile(params: UpdateUserParams): Promise<ApiResponse<User>> {
    return apiClient.put(Routes.USER_PROFILE, params);
  },
};
