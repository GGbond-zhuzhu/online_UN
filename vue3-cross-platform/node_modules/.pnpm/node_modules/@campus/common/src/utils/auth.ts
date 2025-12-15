/**
 * 认证相关工具函数
 * 处理token的存储和获取
 * 兼容 Web 和 UniApp 环境
 */

import { getStorage, setStorage, removeStorage, getStorageJSON, setStorageJSON } from './storage'

// Token存储的key
const TOKEN_KEY = 'campus_system_token'
const USER_INFO_KEY = 'campus_system_user_info'

/**
 * 获取token
 */
export function getToken(): string | null {
  // 使用跨平台存储工具
  return getStorage(TOKEN_KEY)
}

/**
 * 设置token
 * @param token JWT token
 * @param remember 是否记住登录（UniApp 中此参数保留以保持接口兼容性）
 */
export function setToken(token: string, remember: boolean = false): void {
  // UniApp 和 Web 都使用持久化存储
  // remember 参数在 UniApp 中意义不大，但保留以保持接口兼容性
  setStorage(TOKEN_KEY, token)
}

/**
 * 移除token
 */
export function removeToken(): void {
  removeStorage(TOKEN_KEY)
}

/**
 * 获取用户信息
 */
export function getUserInfo(): any | null {
  return getStorageJSON(USER_INFO_KEY)
}

/**
 * 设置用户信息
 * @param userInfo 用户信息对象
 * @param remember 是否记住（UniApp 中此参数保留以保持接口兼容性）
 */
export function setUserInfo(userInfo: any, remember: boolean = false): void {
  // UniApp 和 Web 都使用持久化存储
  // remember 参数在 UniApp 中意义不大，但保留以保持接口兼容性
  setStorageJSON(USER_INFO_KEY, userInfo)
}

/**
 * 移除用户信息
 */
export function removeUserInfo(): void {
  removeStorage(USER_INFO_KEY)
}

/**
 * 清除所有认证信息
 */
export function clearAuth(): void {
  removeToken()
  removeUserInfo()
}

