/**
 * 用户状态管理 (Pinia Store)
 * 管理用户登录状态、用户信息、权限等
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getUserInfo as getUserInfoApi, updateUserInfo as updateUserInfoApi, changePassword as changePasswordApi, logout as logoutApi, type LoginParams, type RegisterParams, type UserInfo } from '../api/auth'
import { setToken, getToken, removeToken, setUserInfo as setStorageUserInfo, getUserInfo as getStorageUserInfo, removeUserInfo } from '../utils/auth'

export const useUserStore = defineStore('user', () => {
  // ==================== 状态 ====================
  // 用户信息
  const userInfo = ref<UserInfo | null>(null)
  // 登录状态
  const isLoggedIn = ref<boolean>(false)
  // 加载状态
  const loading = ref<boolean>(false)
  // 开发者模式标识（从存储读取，用于开发者测试所有功能）
  const isDeveloperMode = ref<boolean>(() => {
    // 检查存储中是否有开发者模式标识（兼容Web和uni-app环境）
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        // Web环境使用localStorage
        return localStorage.getItem('campus_system_developer_mode') === 'true'
      } else if (typeof uni !== 'undefined') {
        // uni-app环境使用uni.getStorageSync
        return uni.getStorageSync('campus_system_developer_mode') === 'true'
      }
    } catch (e) {
      console.error('读取开发者模式状态失败:', e)
    }
    return false
  })

  // ==================== 计算属性 ====================
  // 当前用户角色
  const currentRole = computed(() => {
    return userInfo.value?.role || 'visitor'
  })

  // 是否为学生
  const isStudent = computed(() => {
    return currentRole.value === 'student'
  })

  // 是否为教师
  const isTeacher = computed(() => {
    return currentRole.value === 'teacher'
  })

  // 是否为管理员
  const isAdmin = computed(() => {
    return currentRole.value === 'admin'
  })

  // 是否为高校角色
  const isUniversity = computed(() => {
    return currentRole.value === 'university'
  })

  // 是否为游客
  const isVisitor = computed(() => {
    return currentRole.value === 'visitor' || !isLoggedIn.value
  })

  // 是否已认证（非游客）
  const isAuthenticated = computed(() => {
    return isLoggedIn.value && !isVisitor.value
  })

  // 是否为开发者模式（开发者拥有所有权限）
  const isDeveloper = computed(() => {
    return isDeveloperMode.value
  })

  // 用户ID
  const userId = computed(() => {
    return userInfo.value?.id
  })

  // 用户名
  const username = computed(() => {
    return userInfo.value?.username || ''
  })

  // 学校ID
  const campusId = computed(() => {
    return userInfo.value?.campusId
  })

  // 学校名称
  const campusName = computed(() => {
    return userInfo.value?.campusName || ''
  })

  // ==================== Actions ====================
  
  /**
   * 用户登录
   * @param params 登录参数
   * @param remember 是否记住登录
   */
  const login = async (params: LoginParams, remember: boolean = false): Promise<void> => {
    try {
      loading.value = true
      const response = await loginApi(params)
      
      // 保存token
      setToken(response.token, remember)
      
      // 保存用户信息
      userInfo.value = response.userInfo
      setStorageUserInfo(response.userInfo, remember)
      
      // 更新登录状态
      isLoggedIn.value = true
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 用户注册
   * @param params 注册参数
   */
  const register = async (params: RegisterParams): Promise<void> => {
    try {
      loading.value = true
      await registerApi(params)
      // 注册成功后自动登录
      await login({ username: params.username, password: params.password })
    } catch (error) {
      console.error('注册失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取用户信息
   */
  const fetchUserInfo = async (): Promise<void> => {
    try {
      loading.value = true
      const info = await getUserInfoApi()
      userInfo.value = info
      setStorageUserInfo(info, true)
      isLoggedIn.value = true
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 如果获取失败，清除本地信息
      clearUserInfo()
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 更新用户信息
   * @param info 用户信息
   */
  const updateUserInfo = async (info: Partial<UserInfo>): Promise<void> => {
    try {
      loading.value = true
      const updatedInfo = await updateUserInfoApi(info)
      userInfo.value = updatedInfo
      setStorageUserInfo(updatedInfo, true)
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 修改密码
   * @param oldPassword 旧密码
   * @param newPassword 新密码
   */
  const changePassword = async (oldPassword: string, newPassword: string): Promise<void> => {
    try {
      loading.value = true
      await changePasswordApi(oldPassword, newPassword)
    } catch (error) {
      console.error('修改密码失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 退出登录
   */
  const logout = async (): Promise<void> => {
    try {
      loading.value = true
      await logoutApi()
    } catch (error) {
      console.error('退出登录失败:', error)
    } finally {
      // 无论API调用是否成功，都清除本地信息
      clearUserInfo()
      loading.value = false
    }
  }

  /**
   * 清除用户信息（不调用API）
   */
  const clearUserInfo = (): void => {
    userInfo.value = null
    isLoggedIn.value = false
    removeToken()
    removeUserInfo()
  }

  /**
   * 从本地存储初始化用户信息
   */
  const initUserFromStorage = (): void => {
    const token = getToken()
    const storedUserInfo = getStorageUserInfo()
    
    if (token && storedUserInfo) {
      userInfo.value = storedUserInfo
      isLoggedIn.value = true
    } else {
      clearUserInfo()
    }
  }

  /**
   * 检查是否有权限
   * @param permission 权限标识
   */
  const hasPermission = (permission: string): boolean => {
    // 开发者模式：拥有所有权限
    if (isDeveloperMode.value) {
      return true
    }
    
    if (!isLoggedIn.value) {
      return false
    }
    
    // 根据角色判断权限
    // 游客只能浏览，不能申请/发布
    if (isVisitor.value) {
      return permission === 'browse'
    }
    
    // 已认证用户可以申请和发布
    if (isAuthenticated.value) {
      return ['browse', 'apply', 'publish'].includes(permission)
    }
    
    return false
  }

  /**
   * 检查角色
   * @param roles 角色数组
   */
  const hasRole = (roles: string | string[]): boolean => {
    // 开发者模式：拥有所有角色权限
    if (isDeveloperMode.value) {
      return true
    }
    
    if (!isLoggedIn.value) {
      return false
    }
    
    const roleArray = Array.isArray(roles) ? roles : [roles]
    return roleArray.includes(currentRole.value)
  }

  /**
   * 启用开发者模式（用于开发者测试所有功能）
   */
  const enableDeveloperMode = (): void => {
    isDeveloperMode.value = true
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        // Web环境使用localStorage
        localStorage.setItem('campus_system_developer_mode', 'true')
      } else if (typeof uni !== 'undefined') {
        // uni-app环境使用uni.setStorageSync
        uni.setStorageSync('campus_system_developer_mode', 'true')
      }
      console.log('✅ 开发者模式已启用 - 您拥有所有权限')
    } catch (e) {
      console.error('启用开发者模式失败:', e)
    }
  }

  /**
   * 禁用开发者模式
   */
  const disableDeveloperMode = (): void => {
    isDeveloperMode.value = false
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        // Web环境使用localStorage
        localStorage.removeItem('campus_system_developer_mode')
      } else if (typeof uni !== 'undefined') {
        // uni-app环境使用uni.removeStorageSync
        uni.removeStorageSync('campus_system_developer_mode')
      }
      console.log('❌ 开发者模式已禁用')
    } catch (e) {
      console.error('禁用开发者模式失败:', e)
    }
  }

  /**
   * 切换开发者模式
   */
  const toggleDeveloperMode = (): void => {
    if (isDeveloperMode.value) {
      disableDeveloperMode()
    } else {
      enableDeveloperMode()
    }
  }

  return {
    // 状态
    userInfo,
    isLoggedIn,
    loading,
    isDeveloperMode,
    
    // 计算属性
    currentRole,
    isStudent,
    isTeacher,
    isAdmin,
    isUniversity,
    isVisitor,
    isAuthenticated,
    isDeveloper,
    userId,
    username,
    campusId,
    campusName,
    
    // Actions
    login,
    register,
    fetchUserInfo,
    updateUserInfo,
    changePassword,
    logout,
    clearUserInfo,
    initUserFromStorage,
    hasPermission,
    hasRole,
    enableDeveloperMode,
    disableDeveloperMode,
    toggleDeveloperMode
  }
})
