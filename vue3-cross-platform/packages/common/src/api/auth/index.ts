/**
 * 用户认证相关API
 * 包括登录、注册、用户信息等接口
 */
import request from '../../utils/request'

// 登录请求参数
export interface LoginParams {
  username: string
  password: string
}

// 登录响应数据（适配后端LoginVO）
export interface LoginResponse {
  token: string
  userId: number
  username: string
  role: string
  expiresIn?: number
  // 兼容旧格式
  userInfo?: {
    id: number
    username: string
    role: string
    campusId?: number
    campusName?: string
  }
}

// 注册请求参数
export interface RegisterParams {
  username: string
  password: string
  email?: string
  phone?: string
}

// 用户信息
export interface UserInfo {
  id: number
  username: string
  email?: string
  phone?: string
  role: string
  campusId?: number
  campusName?: string
  avatar?: string
}

/**
 * 用户登录
 * @param params 登录参数
 */
export function login(params: LoginParams): Promise<LoginResponse> {
  return request.post<LoginResponse>('/api/user/login', params)
}

/**
 * 用户注册
 * @param params 注册参数
 */
export function register(params: RegisterParams): Promise<{ id: number; username: string }> {
  return request.post('/api/user/register', params)
}

/**
 * 获取当前用户信息
 */
export function getUserInfo(): Promise<UserInfo> {
  return request.get<UserInfo>('/api/user/info')
}

/**
 * 更新用户信息
 * @param userInfo 用户信息
 */
export function updateUserInfo(userInfo: Partial<UserInfo>): Promise<UserInfo> {
  return request.put<UserInfo>('/api/user/info', userInfo)
}

/**
 * 修改密码
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
export function changePassword(oldPassword: string, newPassword: string): Promise<void> {
  return request.post('/api/user/change-password', { oldPassword, newPassword })
}

/**
 * 退出登录
 */
export function logout(): Promise<void> {
  return request.post('/api/user/logout')
}

/**
 * 发送邮箱验证码
 * @param email 邮箱地址
 */
export function sendEmailCode(email: string): Promise<{ codeId: string; code?: string; expireTime: number }> {
  return request.post('/api/auth/email/send-code', { email })
}

/**
 * 邮箱登录
 * @param email 邮箱地址
 * @param code 验证码
 * @param codeId 验证码ID
 */
export function emailLogin(email: string, code: string, codeId: string): Promise<LoginResponse> {
  return request.post<LoginResponse>('/api/auth/email/login', { email, code, codeId })
}

