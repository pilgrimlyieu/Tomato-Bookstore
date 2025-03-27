import { axios } from '../utils/request'

const USER_MODULE = '/user' // 实际接口前缀请替换为你的后端路径

export type LoginInfo = {
  phone: string,
  password: string
}

export type RegisterInfo = {
  role: string,
  name: string,
  phone: string,
  password: string,
  address: string,
  storeId?: number,
}

export type UpdateInfo = {
  name?: string,
  password?: string,
  address?: string,
}

// 登录
export const userLogin = (loginInfo: LoginInfo) => {
  return axios.post(`${USER_MODULE}/login`, null, { params: loginInfo })
}

// 注册
export const userRegister = (registerInfo: RegisterInfo) => {
  return axios.post(`${USER_MODULE}/register`, registerInfo)
}

// 获取用户信息
export const userInfo = () => {
  return axios.get(`${USER_MODULE}`)
}

// 更新用户信息（昵称/密码/地址）
export const userInfoUpdate = (updateInfo: UpdateInfo) => {
  return axios.post(`${USER_MODULE}`, updateInfo)
}
