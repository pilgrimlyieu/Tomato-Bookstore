/**
 * 用户角色枚举
 */
export enum UserRole {
  CUSTOMER = "CUSTOMER",
  ADMIN = "ADMIN",
}

/**
 * 用户信息接口
 */
export interface User {
  id: number;
  username: string;
  email: string;
  phone: string;
  avatar?: string;
  address?: string;
  role: UserRole;
}

/**
 * 登录参数接口
 */
export interface LoginParams {
  username: string;
  password: string;
}

/**
 * 注册参数接口
 */
export interface RegisterParams {
  username: string;
  password: string;
  email: string;
  phone: string;
}

/**
 * 更新用户信息参数接口
 */
export interface UpdateUserParams {
  email?: string;
  phone?: string;
  avatar?: string;
  address?: string;
}
