// 认证相关通用钩子（双端可用）—— 封装登录 / 注册 / 邮箱验证码登录等逻辑
// 目标：页面只关心“调用钩子”，不需要自己处理 token、本地存储等细节

// 引入 Vue 的响应式工具，用于管理加载状态和错误提示
import { ref } from 'vue' // 从 vue 中导入 ref，用来创建响应式数据（例如 loading、errorMessage 等）

// 引入用户 Pinia Store，用于统一管理用户登录状态和信息
import { useUserStore } from '../pinia/user' // 从 common 包内部的 pinia 目录中导入用户 Store

// 引入认证相关的 API 方法和类型定义
import {
  sendEmailCode, // 发送邮箱验证码的接口方法
  emailLogin, // 邮箱验证码登录的接口方法
  type LoginParams, // 登录表单参数类型（用户名+密码）
  type RegisterParams, // 注册表单参数类型
  type LoginResponse // 登录接口返回的数据类型（包含 token、用户信息等）
} from '../api/auth' // 从 api/auth 模块中导入认证相关接口

// 引入认证工具，用于直接读写 token 等（主要给邮箱登录流程使用）
import { setToken, setUserInfo as setStorageUserInfo } from '../utils/auth' // 从 utils/auth 中导入 token 和用户信息的存储工具

// 定义邮箱验证码发送接口的响应类型（与后端文档保持一致）
interface EmailCodeResponse {
  codeId: string // 验证码ID，用于后续登录时和验证码一起提交
  code?: string // 开发或测试环境中，后端可能会直接返回验证码内容，方便调试
  expireTime: number // 验证码过期时间的时间戳（毫秒）
}

// useAuth 钩子：对外暴露统一的认证操作
export function useAuth() {
  // 获取用户 Store，用于调用内部的登录 / 注册等方法
  const userStore = useUserStore() // 通过 Pinia 获取用户 Store 实例

  // ========== 通用状态 ==========

  const loading = ref(false) // 当前是否在执行任意认证相关操作（登录 / 注册 / 发送验证码 / 邮箱登录等）
  const errorMessage = ref('') // 最近一次操作的错误提示文案（适合直接绑定到表单错误提示位置）

  // ========== 邮箱验证码发送相关状态 ==========

  const emailSending = ref(false) // 是否正在发送邮箱验证码（避免用户连续点击）
  const emailCountdown = ref(0) // 验证码倒计时秒数（例如 60 秒内不允许重复发送）
  const lastEmail = ref('') // 上一次成功发送验证码的邮箱地址，方便表单回显
  const lastEmailCodeId = ref('') // 最近一次发送验证码返回的 codeId，用于后续邮箱登录提交

  let countdownTimer: ReturnType<typeof setInterval> | null = null // 内部倒计时定时器句柄，用于清理 setInterval

  // 内部工具：开始验证码倒计时
  const startCountdown = (seconds: number) => {
    // 先清理可能存在的旧定时器，避免多个倒计时叠加
    if (countdownTimer) {
      clearInterval(countdownTimer) // 清除旧的定时器
      countdownTimer = null // 复位计时器引用
    }
    emailCountdown.value = seconds // 将倒计时起始秒数写入响应式变量
    // 创建新的定时器，每秒减一，直到减到 0
    countdownTimer = setInterval(() => {
      if (emailCountdown.value <= 1) {
        emailCountdown.value = 0 // 最后一次时将其置为 0
        if (countdownTimer) {
          clearInterval(countdownTimer) // 清理定时器
          countdownTimer = null // 复位引用
        }
      } else {
        emailCountdown.value -= 1 // 每秒将倒计时减一
      }
    }, 1000) // 每 1000 毫秒（1 秒）执行一次
  }

  // ========== 登录相关方法 ==========

  // 账号密码登录（会自动调用 userStore.login，完成 token 和用户信息的存储）
  const login = async (params: LoginParams, remember = false) => {
    loading.value = true // 操作开始时标记为加载中
    errorMessage.value = '' // 清空上一次的错误信息
    try {
      // 直接调用用户 Store 中已经封装好的 login 方法
      await userStore.login(params, remember) // 将表单参数与“记住我”选项传给 Store 处理
    } catch (error: any) {
      // 捕获异常，并转换为用户可读的提示文案
      console.error('账号密码登录失败:', error) // 在控制台输出详细错误，方便开发调试
      errorMessage.value = error?.message || '登录失败，请稍后重试' // 将错误信息展示在表单附近
      throw error // 继续向外抛出错误，方便页面进行额外处理（如弹窗）
    } finally {
      loading.value = false // 无论成功或失败，最后都要恢复为“非加载中”状态
    }
  }

  // 用户注册（注册成功后，Store 中的实现会自动帮用户登录）
  const register = async (params: RegisterParams) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空上一次的错误
    try {
      // 调用用户 Store 中已经封装好的 register 方法
      await userStore.register(params) // 注册完成后，内部会自动调用 login 完成登录
    } catch (error: any) {
      console.error('用户注册失败:', error) // 在控制台输出详细错误信息
      errorMessage.value = error?.message || '注册失败，请稍后重试' // 将错误信息写入响应式变量
      throw error // 把错误抛给调用方，以便页面进行特别处理
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 主动拉取当前登录用户信息（通常在应用初始化或登录成功后调用）
  const fetchUserInfo = async () => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误信息
    try {
      await userStore.fetchUserInfo() // 通过 Store 调用后端接口获取用户信息，并写入全局状态
    } catch (error: any) {
      console.error('获取用户信息失败:', error) // 在控制台打印错误
      errorMessage.value = error?.message || '获取用户信息失败，请重新登录' // 显示友好的错误提示
      throw error // 向上抛出，方便跳转登录页等逻辑
    } finally {
      loading.value = false // 恢复为非加载状态
    }
  }

  // 退出登录（调用后端接口 + 本地清理）
  const logout = async () => {
    loading.value = true // 退出登录过程同样需要展示加载中状态
    errorMessage.value = '' // 清空历史错误
    try {
      await userStore.logout() // 调用 Store 中封装好的退出登录逻辑（包含清理本地 token）
    } catch (error: any) {
      console.error('退出登录失败:', error) // 打印错误信息
      errorMessage.value = error?.message || '退出登录失败，请稍后重试' // 给出友好的提示
      // 退出登录失败通常不需要向上抛出错误，以免打断用户操作，这里选择吞掉异常
    } finally {
      loading.value = false // 结束加载状态
    }
  }

  // ========== 邮箱验证码相关方法 ==========

  // 发送登录邮箱验证码
  const sendLoginEmailCode = async (email: string, countdownSeconds = 60): Promise<EmailCodeResponse | void> => {
    // 如果已经在倒计时中，则不允许重复发送
    if (emailCountdown.value > 0) {
      return // 直接返回，不做任何操作
    }
    emailSending.value = true // 标记当前正在发送验证码
    errorMessage.value = '' // 清空历史错误信息
    try {
      // 调用后端接口发送邮箱验证码
      const res = (await sendEmailCode(email)) as EmailCodeResponse // 发送邮箱验证码的接口返回数据
      lastEmail.value = email // 记录本次发送的邮箱地址
      lastEmailCodeId.value = res.codeId // 记录本次验证码对应的 codeId，后续登录时要一起传给后端
      startCountdown(countdownSeconds) // 启动倒计时，防止用户频繁点击发送
      // 如果后端在开发环境中返回了验证码本身，可以在控制台打印出来方便测试
      if (res.code) {
        console.log('开发环境邮箱验证码（仅供测试使用）:', res.code) // 控制台打印验证码内容，方便开发调试
      }
      return res // 将完整的验证码响应对象返回给调用方，便于在页面中做更丰富的提示
    } catch (error: any) {
      console.error('发送邮箱验证码失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '验证码发送失败，请稍后重试' // 将错误信息显示给用户
      throw error // 抛出错误，页面可以按需弹窗提示
    } finally {
      emailSending.value = false // 重置发送状态
    }
  }

  // 使用邮箱验证码登录（成功后会写入 token，并通过 Store 拉取用户信息）
  const loginByEmailCode = async (email: string, code: string, codeId?: string, remember = false) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空历史错误
    try {
      // 如果调用方没有显式传入 codeId，则优先使用最近一次发送验证码时记录的 codeId
      const finalCodeId = codeId || lastEmailCodeId.value // 从参数或内部状态中确定最终使用的 codeId
      if (!finalCodeId) {
        throw new Error('验证码已过期或未发送，请重新获取验证码') // 如果 codeId 不存在，则给出明确提示
      }

      // 调用邮箱登录接口，获取 token 和用户基础信息
      const res = await emailLogin(email, code, finalCodeId) // 通过邮箱 + 验证码 + codeId 调用后端登录接口

      // 将 token 写入本地存储，记住登录与否交给工具函数处理
      setToken(res.token, remember) // 把后端返回的 token 保存到本地存储

      // 如果后端返回了用户信息，则先行写入本地存储，便于后续初始化
      if (res.userInfo) {
        setStorageUserInfo(res.userInfo, remember) // 将用户信息写入本地存储
      }

      // 再通过 Store 主动拉取一次用户完整信息，保证与后端保持同步
      await userStore.fetchUserInfo() // 这一步会自动更新 isLoggedIn、userInfo 等全局状态
    } catch (error: any) {
      console.error('邮箱验证码登录失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '邮箱登录失败，请稍后重试' // 显示友好的错误内容
      throw error // 将错误抛给调用方，方便页面按需弹出提示框
    } finally {
      loading.value = false // 不论成功失败，最后都重置 loading 状态
    }
  }

  // ========== 对外统一返回 ==========

  return {
    // 状态
    loading, // 当前是否有认证相关操作正在进行中的标记
    errorMessage, // 最近一次认证操作的错误提示文本
    emailSending, // 邮箱验证码是否正在发送中的标记
    emailCountdown, // 邮箱验证码重新发送前的倒计时秒数
    lastEmail, // 最近一次发送验证码时填写的邮箱地址
    lastEmailCodeId, // 最近一次发送验证码接口返回的 codeId

    // 操作方法
    login, // 账号密码登录方法
    register, // 用户注册方法（注册成功会自动登录）
    fetchUserInfo, // 主动拉取当前登录用户信息的方法
    logout, // 退出登录方法
    sendLoginEmailCode, // 发送登录邮箱验证码的方法
    loginByEmailCode // 使用邮箱验证码进行登录的方法
  }
} // useAuth 钩子定义结束

// 同时导出一个默认的 useAuth，方便某些地方以 `import useAuth from '@campus/common'` 的方式按默认导入
// 这样既兼容具名导入（import { useAuth } ...），也兼容默认导入，避免运行时报“没有 default 导出”的错误。
export default useAuth
