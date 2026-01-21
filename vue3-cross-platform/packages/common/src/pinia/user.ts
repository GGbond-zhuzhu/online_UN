/**
 * 用户相关 Pinia Store（Web / App 共用）
 * 说明：这里是「真正的」用户状态中心，负责：
 * 1. 对接后端 /api/user/login、/api/user/info 等接口；
 * 2. 统一维护 token、用户基础信息、当前角色；
 * 3. 提供 isStudent / isTeacher / isVisitor / currentRole 等便捷 getter；
 * 4. 为导航栏 / 首页 / 校园卡等页面提供一致的权限判断依据（canAccess）。
 */

// 引入 Pinia 的 defineStore，用于创建全局用户 Store
import { defineStore } from 'pinia' // 从 pinia 中导入 defineStore 函数，用来定义用户 Store

// 引入 Vue 的响应式工具，用于管理用户状态和派生状态
import { ref, computed } from 'vue' // 从 vue 中导入 ref 和 computed，用来创建响应式变量和计算属性

// 引入认证相关接口与类型定义（账号登录 / 注册 / 获取用户信息 / 退出登录）
// 注意：从 @campus/common 包导入，避免路径解析错误
import {
  login as loginApi, // loginApi：封装好的账号密码登录接口
  register as registerApi, // registerApi：封装好的注册接口
  getUserInfo as getUserInfoApi, // getUserInfoApi：获取当前登录用户信息的接口
  logout as logoutApi, // logoutApi：退出登录接口
  type LoginParams, // LoginParams：账号密码登录参数类型
  type RegisterParams, // RegisterParams：用户注册参数类型
  type UserInfo as ApiUserInfo // ApiUserInfo：后端返回的用户信息类型
} from '@campus/common/api/auth' // 从 @campus/common 包的 api/auth 模块中导入认证相关方法

// 引入认证工具方法，用于在本地存储 token 和用户信息
import {
  getToken, // getToken：从本地存储中读取 token 字符串
  setToken, // setToken：将新的 token 保存到本地（支持"记住我"）
  removeToken, // removeToken：从本地删除 token（当前未直接使用，保留备用）
  getUserInfo as getStoredUserInfo, // getStoredUserInfo：从本地存储中读取用户信息对象
  setUserInfo as setStoredUserInfo, // setStoredUserInfo：将用户信息写入本地存储
  removeUserInfo, // removeUserInfo：从本地删除用户信息（当前未直接使用，保留备用）
  clearAuth // clearAuth：同时清除 token 和用户信息
} from '@campus/common/utils/auth' // 从 @campus/common 包的 utils/auth 中导入跨端认证工具

// ==================== 类型定义 ====================

// 统一前端使用的用户角色编码（全部小写），兼容后端及旧版前端
export type UserRole =
  | 'student' // 学生
  | 'teacher' // 教师
  | 'university' // 高校管理员
  | 'merchant' // 商家（前端预留角色，后端可按需扩展）
  | 'admin' // 系统管理员
  | 'visitor' // 游客（新标识）
  | 'tourist' // 游客（兼容后端 / 旧代码中的 tourist）

// 统一对外暴露的用户信息结构（比后端 UserInfo 多了 role 归一化等字段）
export interface UserInfo {
  id: number | null // 用户 ID（后端 userId 或 id 统一映射到这里）
  username: string // 用户名（登录名）
  role: UserRole // 当前角色（使用小写英文 code）
  campusId?: number | null // 校区 ID（可选）
  campusName?: string // 校区名称（可选）
  avatar?: string // 头像 URL（可选）
  email?: string // 邮箱（可选）
  phone?: string // 手机号（可选）
}

// 将后端返回的角色字段统一转换为前端使用的 UserRole
const normalizeRole = (rawRole?: string | null): UserRole => {
  // 如果没有传角色，则直接视为游客
  if (!rawRole) {
    return 'visitor' // 未登录或未设置角色时统一视为游客
  }

  // 先处理已经是小写 code 的情况（后端 UserRoleEnum.code 或前端手动设置）
  const lower = rawRole.toLowerCase() // 将原始角色字符串转换为小写，便于匹配
  const lowerMap: Record<string, UserRole> = { // 小写角色映射表
    student: 'student', // 学生
    teacher: 'teacher', // 教师
    university: 'university', // 高校管理员
    admin: 'admin', // 管理员
    merchant: 'merchant', // 商家（仅前端使用）
    visitor: 'visitor', // 游客（visitor 标识）
    tourist: 'tourist' // 游客（tourist 标识）
  }
  if (lowerMap[lower]) {
    return lowerMap[lower] // 命中小写映射时直接返回对应角色
  }

  // 再兼容处理后端枚举名（STUDENT / TEACHER / ADMIN / UNIVERSITY / TOURIST）
  const upper = rawRole.toUpperCase() // 将原始角色转换为大写，匹配后端枚举名
  const upperMap: Record<string, UserRole> = { // 大写枚举名到前端 code 的映射表
    STUDENT: 'student', // 枚举 STUDENT
    TEACHER: 'teacher', // 枚举 TEACHER
    UNIVERSITY: 'university', // 枚举 UNIVERSITY
    ADMIN: 'admin', // 枚举 ADMIN
    TOURIST: 'tourist' // 枚举 TOURIST
  }
  if (upperMap[upper]) {
    return upperMap[upper] // 命中大写映射时返回对应小写角色
  }

  // 未知角色一律当成游客处理，避免因后端扩展导致前端崩溃
  return 'visitor' // 兜底返回游客角色
}

// ==================== 用户 Store 定义 ====================

// 定义并导出用户 Store，供 Web / App 共用
export const useUserStore = defineStore('user', () => {
  // ---------- 基础状态（ref 存储） ----------

  const userId = ref<number | null>(null) // userId：当前登录用户的 ID（未登录为 null）
  const username = ref('') // username：当前登录用户名
  const campusId = ref<number | null>(null) // campusId：当前用户所属校区 ID（可选）
  const campusName = ref('') // campusName：当前用户所属校区名称（可选）
  const avatar = ref<string | null>(null) // avatar：用户头像 URL（可选）
  const email = ref<string | null>(null) // email：用户邮箱（可选）
  const phone = ref<string | null>(null) // phone：用户手机号（可选）
  const currentRole = ref<UserRole>('visitor') // currentRole：当前角色编码，默认视为游客

  // ---------- 派生状态（computed 计算属性） ----------

  const isLoggedIn = computed(() => { // isLoggedIn：是否已登录
    return !!getToken() && userId.value !== null // 既有 token 又有 userId 时视为已登录
  })

  // 演示登录（demo-token）：仅用于前端预览，不具备真实后端鉴权能力
  const isDemoLogin = computed(() => {
    return getToken() === 'demo-token'
  })

  // 是否具备调用“需要登录的后端接口”的条件：真实登录（非 demo-token）
  const isApiReady = computed(() => {
    return isLoggedIn.value && !isDemoLogin.value
  })

  const isStudent = computed(() => currentRole.value === 'student') // isStudent：是否学生角色
  const isTeacher = computed(() => currentRole.value === 'teacher') // isTeacher：是否教师角色
  const isUniversity = computed(() => currentRole.value === 'university') // isUniversity：是否高校管理员角色
  const isMerchant = computed(() => currentRole.value === 'merchant') // isMerchant：是否商家角色
  const isAdmin = computed(() => currentRole.value === 'admin') // isAdmin：是否系统管理员角色
  const isVisitor = computed(() => { // isVisitor：是否游客（兼容 visitor / tourist）
    return currentRole.value === 'visitor' || currentRole.value === 'tourist' // visitor 或 tourist 都视为游客
  })

  // 开发者模式：用于在本地开发/调试时绕过权限校验（例如演示环境）
  const isDeveloper = computed(() => {
    // 约定：在开发环境（import.meta.env.DEV 为 true）且本地存储中显式开启 devMode 时视为开发者
    if (typeof localStorage === 'undefined') {
      return false // 非浏览器环境下直接返回 false，避免异常
    }
    try {
      // 优先读取文档约定的 campus_system_developer_mode，其次兼容旧的 campus_dev_mode
      const flag =
        localStorage.getItem('campus_system_developer_mode') || // 从本地读取新版开发者模式标记
        localStorage.getItem('campus_dev_mode') // 兼容旧版的开发者模式标记
      return flag === 'true' // 存在且为字符串 'true' 时视为开启
    } catch {
      return false // 读取失败时安全兜底为 false
    }
  })

  const userInfo = computed<UserInfo>(() => { // userInfo：对外统一暴露的用户信息对象
    return {
      id: userId.value, // 使用 userId ref 中的值
      username: username.value, // 使用当前用户名
      role: currentRole.value, // 使用归一化后的当前角色
      campusId: campusId.value, // 校区 ID
      campusName: campusName.value, // 校区名称
      avatar: avatar.value || undefined, // 头像（无值时返回 undefined）
      email: email.value || undefined, // 邮箱（无值时返回 undefined）
      phone: phone.value || undefined // 手机号（无值时返回 undefined）
    }
  })

  // ---------- 内部工具函数：根据后端/本地用户对象填充状态 ----------

  const applyUserFromPayload = (payload: Partial<ApiUserInfo> & { userId?: number; role?: string }) => {
    // 从 payload 中解析出用户 ID（login 接口用 userId，info 接口用 id）
    const resolvedId = payload.userId ?? payload.id ?? null // 优先使用 userId，其次 id，没有则为 null
    userId.value = resolvedId // 将解析后的用户 ID 写入状态

    // 用户名：login 接口与 info 接口都统一为 username 字段
    username.value = payload.username || '' // 如果没有用户名则回退为空字符串

    // 校区 ID 与名称
    campusId.value = payload.campusId ?? null // 校区 ID 没有时视为 null
    campusName.value = payload.campusName || '' // 校区名称没有时视为空字符串

    // 头像 / 邮箱 / 手机号
    avatar.value = payload.avatar || null // 头像 URL
    email.value = payload.email || null // 邮箱
    phone.value = payload.phone || null // 手机号

    // 角色：通过 normalizeRole 将后端枚举 / code 统一为 UserRole
    currentRole.value = normalizeRole(payload.role || (payload as any).roleCode) // 优先 role 字段，兼容可能存在的 roleCode 字段
  }

  // 重置本地状态为“未登录 + 游客”
  const resetState = () => {
    userId.value = null // 清空用户 ID
    username.value = '' // 清空用户名
    campusId.value = null // 清空校区 ID
    campusName.value = '' // 清空校区名称
    avatar.value = null // 清空头像
    email.value = null // 清空邮箱
    phone.value = null // 清空手机号
    currentRole.value = 'visitor' // 恢复为游客角色
  }

  // ---------- 对外动作：初始化、本地恢复 ----------

  const initUserFromStorage = async () => { // initUserFromStorage：从本地缓存中恢复用户登录状态
    try {
      const token = getToken() // 读取本地 token
      const stored = getStoredUserInfo() as Partial<ApiUserInfo> | null // 读取本地缓存的用户信息

      if (token && stored && typeof stored === 'object') { // 同时存在 token 与用户信息时尝试恢复状态
        applyUserFromPayload(stored) // 使用本地缓存填充状态
      } else {
        resetState() // 如果本地信息不完整，则回退为未登录游客
      }
    } catch (error) {
      // 从本地存储恢复用户信息失败（已静默处理）
      clearAuth() // 彻底清除本地认证信息，避免脏数据影响后续登录
      resetState() // 恢复为未登录游客状态
    }
  }

  // ---------- 对外动作：账号密码登录 ----------

  const login = async (params: LoginParams, remember = false) => { // login：账号密码登录方法
    // 说明：loginApi 内部会请求后端 /api/user/login，返回 LoginResponse
    const res = await loginApi(params) // 调用后端登录接口，获取登录响应

    // 将后端返回的 token 写入本地存储，支持“记住我”选项
    setToken(res.token, remember) // 保存 token 到本地

    // 使用登录返回的基础信息先行更新状态（快速让导航栏等位置展示用户名/角色）
    applyUserFromPayload({
      userId: res.userId, // 登录响应中的用户 ID
      username: res.username, // 登录响应中的用户名
      role: typeof res.role === 'string' ? res.role : String(res.role) // 兼容枚举 / 字符串形式
    })

    // 将当前用户信息写入本地存储，便于后续刷新时恢复状态
    setStoredUserInfo(userInfo.value, remember) // 使用统一的 USER_INFO_KEY 进行持久化
  }

  // ---------- 对外动作：注册并自动登录 ----------

  const register = async (params: RegisterParams) => { // register：用户注册方法
    await registerApi(params) // 调用后端注册接口创建新用户
    // 注册成功后，直接复用账号密码登录逻辑自动登录
    await login({ username: params.username, password: params.password }, false) // 使用注册时填写的账号密码完成自动登录
  }

  // ---------- 对外动作：从后端重新拉取用户信息 ----------

  const fetchUserInfo = async () => { // fetchUserInfo：主动从后端获取当前登录用户信息
    const info = await getUserInfoApi() // 调用 /api/user/info 接口获取用户详情
    applyUserFromPayload(info) // 使用接口返回的数据填充状态
    setStoredUserInfo(userInfo.value, true) // 将最新用户信息写回本地，便于后续恢复
  }

  // ---------- 对外动作：退出登录 ----------

  const logout = async () => { // logout：退出登录方法
    try {
      await logoutApi() // 调用后端 /api/user/logout 接口（如后端未实现，可忽略错误）
    } catch (error) {
      // 调用后端退出登录接口失败（可忽略，已静默处理）
    } finally {
      clearAuth() // 清除本地 token 和用户信息
      resetState() // 将本地状态重置为游客未登录
    }
  }

  // ---------- Store 对外暴露的状态 / getter / 方法 ----------

  return {
    // 基础状态
    userId, // 当前登录用户 ID
    username, // 当前登录用户名
    campusId, // 当前用户所属校区 ID
    campusName, // 当前用户所属校区名称
    avatar, // 当前用户头像 URL
    email, // 当前用户邮箱
    phone, // 当前用户手机号
    currentRole, // 当前角色编码（用于权限控制）

    // 派生状态
    isLoggedIn, // 是否已登录
    isDemoLogin, // 是否为演示登录（demo-token）
    isApiReady, // 是否可安全调用需要登录的后端接口
    isStudent, // 是否学生角色
    isTeacher, // 是否教师角色
    isUniversity, // 是否高校管理员角色
    isMerchant, // 是否商家角色
    isAdmin, // 是否系统管理员角色
    isVisitor, // 是否游客身份
    isDeveloper, // 是否处于开发者模式（用于在本地开发时放宽权限校验）
    userInfo, // 归一化后的用户信息对象

    // 动作方法
    initUserFromStorage, // 从本地缓存初始化用户状态
    login, // 账号密码登录
    register, // 用户注册（含自动登录）
    fetchUserInfo, // 从后端重新拉取用户信息
    logout, // 退出登录

    // 辅助方法：判断当前用户是否拥有指定角色
    hasRole: (role: UserRole) => currentRole.value === role, // 简单比较当前角色与目标角色是否一致

    // 身份认证方法（临时实现，后续对接后端API）
    identityAuth: async (role: UserRole, info: Record<string, any>): Promise<boolean> => {
      try {
        // TODO: 对接后端身份认证API
        // 临时实现：直接更新角色
        currentRole.value = role
        if (info.name) {
          username.value = info.name
        }
        // 保存到本地存储
        setStoredUserInfo(userInfo.value, true)
        return true
      } catch (error) {
        console.error('身份认证失败:', error)
        return false
      }
    }
  }
}) // useUserStore 定义结束
