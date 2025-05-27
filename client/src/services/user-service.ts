import { USER_MODULE } from "@/constants/apiPrefix";
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
    return apiClient.post(`${USER_MODULE}/login`, params);
  },

  /**
   * 用户注册
   *
   * @param {RegisterParams} params 注册参数
   * @returns {Promise<ApiResponse<void>>} 注册结果
   */
  register(params: RegisterParams): Promise<ApiResponse<void>> {
    return apiClient.post(`${USER_MODULE}/register`, params);
  },

  /**
   * 获取当前用户信息
   *
   * @returns {Promise<ApiResponse<User>>} 当前用户信息
   */
  getCurrentUser(): Promise<ApiResponse<User>> {
    return apiClient.get(`${USER_MODULE}/profile`);
  },

  /**
   * 更新用户信息
   *
   * @param {UpdateUserParams} params 更新参数
   * @returns {Promise<ApiResponse<User>>} 更新结果
   */
  updateUserProfile(params: UpdateUserParams): Promise<ApiResponse<User>> {
    return apiClient.put(`${USER_MODULE}/profile`, params);
  },

  /**
   * 修改用户密码
   *
   * @param {string} newPassword 新密码
   * @returns {Promise<ApiResponse<User>>} 修改结果
   */
  changePassword(newPassword: string): Promise<ApiResponse<User>> {
    return apiClient.put(`${USER_MODULE}/profile`, { password: newPassword });
  },
};
