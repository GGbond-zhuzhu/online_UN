<template>
  <div class="login-page">
    <!-- 导航栏 -->
    <LoginNavBar />

    <!-- 登录核心容器 -->
    <div class="login-container">
      <div class="login-wrapper">
        <!-- 左侧粉色渐变区域 -->
        <div class="login-left">
          <div class="brand-section">
            <h1 class="brand-title">上大学Online</h1>
            <h2 class="brand-subtitle">校园综合服务平台</h2>
            <p class="welcome-text">欢迎登录</p>
            <p class="welcome-desc">打造数字化校园，连接校园生活每一刻</p>
          </div>

          <div class="features-section">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="fa fa-credit-card"></i>
              </div>
              <div class="feature-content">
                <h3>校园E卡通</h3>
                <p>一码通校园，便捷支付与身份认证</p>
              </div>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <i class="fa fa-exchange"></i>
              </div>
              <div class="feature-content">
                <h3>二手交易</h3>
                <p>同校精准匹配，闲置物品变废为宝</p>
              </div>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <i class="fa fa-briefcase"></i>
              </div>
              <div class="feature-content">
                <h3>兼职服务</h3>
                <p>海量岗位，保障薪资安全可靠</p>
              </div>
            </div>
            <div class="feature-card">
              <div class="feature-icon">
                <i class="fa fa-calendar"></i>
              </div>
              <div class="feature-content">
                <h3>行程管理</h3>
                <p>智能提醒同步，学习生活有条不紊</p>
              </div>
            </div>
          </div>

          <div class="action-section">
            <a href="/" class="btn-experience">立即体验</a>
          </div>
        </div>

        <!-- 右侧登录表单区域 -->
        <div class="login-right">
          <div class="login-tabs">
            <button 
              class="tab-btn" 
              :class="{ active: loginType === 'account' }" 
              @click="loginType = 'account'"
            >
              账号登录
            </button>
            <button 
              class="tab-btn" 
              :class="{ active: loginType === 'phone' }" 
              @click="loginType = 'phone'"
            >
              手机号登录
            </button>
            <button 
              class="tab-btn" 
              :class="{ active: loginType === 'qr' }" 
              @click="switchToQRLogin"
            >
              扫码登录
            </button>
          </div>

          <!-- 账号登录表单 -->
          <div v-show="loginType === 'account'" class="login-form">
            <div class="form-group">
              <label for="username">用户名/手机号/邮箱</label>
              <input 
                type="text" 
                id="username" 
                class="form-control" 
                v-model="accountForm.username"
                placeholder="请输入用户名、手机号或邮箱"
                :disabled="loading"
              />
            </div>
            <div class="form-group">
              <label for="password">密码</label>
              <input 
                type="password" 
                id="password" 
                class="form-control" 
                v-model="accountForm.password"
                placeholder="请输入密码"
                :disabled="loading"
              />
            </div>
            <div class="form-options">
              <label class="checkbox-label">
                <input 
                  type="checkbox" 
                  v-model="rememberMe" 
                  :disabled="loading"
                />
                <span>30天内免登录</span>
              </label>
              <a href="/forget-password" class="forget-link">忘记密码？</a>
            </div>
            <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
            <button 
              class="btn-login" 
              @click="handleAccountLogin"
              :disabled="loading"
            >
              {{ loading ? '登录中...' : '登录' }}
            </button>
            <p class="login-agreement">
              登录即表示您同意我们的
              <a href="/terms">服务条款</a>和<a href="/privacy">隐私政策</a>
            </p>
          </div>

          <!-- 手机号登录表单 -->
          <div v-show="loginType === 'phone'" class="login-form">
            <div class="form-group">
              <label for="phone">手机号</label>
              <input 
                type="tel" 
                id="phone" 
                class="form-control" 
                v-model="phoneForm.phone"
                placeholder="请输入手机号"
                :disabled="loading"
              />
            </div>
            <div class="form-group">
              <label for="verifyCode">验证码</label>
              <div class="verify-code-group">
                <input 
                  type="text" 
                  id="verifyCode" 
                  class="form-control" 
                  v-model="phoneForm.verifyCode"
                  placeholder="请输入验证码"
                  :disabled="loading"
                />
                <button 
                  class="btn-send-code" 
                  :disabled="loading || countdown > 0"
                  @click="sendVerifyCode"
                >
                  {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
                </button>
              </div>
              <div v-if="countdown > 0" class="timer">{{ countdown }}秒后重新发送</div>
            </div>
            <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
            <button 
              class="btn-login" 
              @click="handlePhoneLogin"
              :disabled="loading"
            >
              {{ loading ? '登录中...' : '登录' }}
            </button>
            <p class="login-agreement">
              登录即表示您同意我们的
              <a href="/terms">服务条款</a>和<a href="/privacy">隐私政策</a>
            </p>
          </div>

          <!-- 扫码登录 -->
          <div v-show="loginType === 'qr'" class="qr-login">
            <p class="qr-tip-top">请使用上大学Online APP扫描二维码登录</p>
            <div class="qr-code-container">
              <canvas ref="qrCodeCanvas" width="200" height="200" class="qr-code-canvas"></canvas>
              <div v-if="!qrCodeData" class="qr-loading">
                <i class="fa fa-qrcode"></i>
                <p>生成中...</p>
              </div>
            </div>
            <p class="qr-tip-bottom">打开APP，点击"扫一扫"</p>
            <button 
              class="btn-refresh-qr" 
              @click="refreshQRCode"
              :disabled="loading"
            >
              <i class="fa fa-refresh"></i> 刷新二维码
            </button>
          </div>

          <!-- 注册链接 -->
          <div class="register-link">
            还没有账号？<a href="/register">注册新账号</a>
          </div>

          <!-- 其他登录方式 -->
          <div class="other-login">
            <h3><span>其他登录方式</span></h3>
            <div class="other-login-buttons">
              <button class="other-btn" title="企业微信">
                <div class="social-icon wecom">企</div>
                <span>企业微信</span>
              </button>
              <button class="other-btn" title="微信">
                <div class="social-icon wechat">微</div>
                <span>微信</span>
              </button>
              <button class="other-btn" title="QQ">
                <div class="social-icon qq">Q</div>
                <span>QQ</span>
              </button>
              <button class="other-btn" title="邮箱">
                <div class="social-icon email">邮</div>
                <span>邮箱</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 登录成功提示弹窗 -->
    <div v-if="showSuccess" class="success-modal" @click.self="closeSuccess">
      <div class="success-content">
        <i class="fa fa-check-circle success-icon"></i>
        <h2>登录成功！</h2>
        <p>您已成功登录上大学Online校园综合服务平台</p>
        <div class="success-countdown">
          <span>{{ successCountdown }}秒后自动跳转到首页...</span>
        </div>
        <button class="btn-jump" @click="jumpToHome">立即跳转</button>
      </div>
    </div>

    <!-- 页脚 -->
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import QRCode from 'qrcode'

// 引入导航栏和页脚组件
import LoginNavBar from '@/components/common/LoginNavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

// 引入API和工具函数
import { login } from '@campus/common'
import { setToken, setUserInfo } from '@campus/common'

// 路由实例
const router = useRouter()

// 登录方式切换
const loginType = ref<'account' | 'phone' | 'qr'>('account')

// 表单数据
const accountForm = ref({
  username: '',
  password: ''
})

const phoneForm = ref({
  phone: '',
  verifyCode: ''
})

// 状态管理
const loading = ref(false)
const errorMessage = ref('')
const rememberMe = ref(false)
const countdown = ref(0)
const showSuccess = ref(false)
const successCountdown = ref(5)

// 二维码相关
const qrCodeCanvas = ref<HTMLCanvasElement | null>(null)
const qrCodeData = ref<string>('')

// 定时器管理
let successTimer: number | null = null
let codeTimer: number | null = null

// 生成二维码
const generateQRCode = async () => {
  if (!qrCodeCanvas.value) return
  
  try {
    // 生成唯一登录二维码数据
    const qrData = JSON.stringify({
      type: 'login',
      timestamp: Date.now(),
      token: Math.random().toString(36).substring(7)
    })
    
    qrCodeData.value = qrData
    
    // 渲染二维码到Canvas
    await QRCode.toCanvas(qrCodeCanvas.value, qrData, {
      width: 200,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      }
    })
  } catch (error) {
    console.error('二维码生成失败:', error)
    qrCodeData.value = ''
  }
}

// 切换到扫码登录
const switchToQRLogin = async () => {
  loginType.value = 'qr'
  await nextTick()
  if (!qrCodeData.value) {
    await generateQRCode()
  }
}

// 刷新二维码
const refreshQRCode = async () => {
  qrCodeData.value = ''
  await generateQRCode()
}

// 发送验证码
const sendVerifyCode = () => {
  if (!phoneForm.value.phone) {
    errorMessage.value = '请输入手机号'
    return
  }

  // 清空之前的错误提示
  errorMessage.value = ''
  
  // 启动倒计时
  countdown.value = 60
  codeTimer = window.setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(codeTimer!)
    }
  }, 1000)

  // 模拟发送验证码请求（实际项目替换为真实接口）
  console.log('发送验证码到:', phoneForm.value.phone)
}

// 账号登录处理
const handleAccountLogin = async () => {
  // 表单验证
  if (!accountForm.value.username || !accountForm.value.password) {
    errorMessage.value = '请输入用户名和密码'
    return
  }

  errorMessage.value = ''
  loading.value = true

  try {
    // 调用真实登录API
    const result = await login({
      username: accountForm.value.username,
      password: accountForm.value.password
    })
    
    // 保存token和用户信息（适配后端返回格式）
    setToken(result.token)
    // 适配后端LoginVO格式：userId, username, role, token, expiresIn
    // 如果result有userInfo字段，使用它；否则从result中提取
    const userInfo = result.userInfo || {
      id: result.userId || (result as any).id,
      username: result.username,
      role: typeof result.role === 'string' ? result.role : (result.role as any)?.name || 'TOURIST'
    }
    setUserInfo(userInfo)
    
    // 显示成功弹窗
    showSuccess.value = true
    startSuccessCountdown()
  } catch (error: any) {
    // 处理错误信息
    const errorMsg = error?.message || '登录失败，请检查账号密码'
    errorMessage.value = errorMsg
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 手机号登录处理
const handlePhoneLogin = async () => {
  // 表单验证
  if (!phoneForm.value.phone || !phoneForm.value.verifyCode) {
    errorMessage.value = '请输入手机号和验证码'
    return
  }

  errorMessage.value = ''
  loading.value = true

  try {
    // 模拟登录请求（实际项目替换为真实接口）
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 登录成功处理
    localStorage.setItem('isLoggedIn', 'true')
    localStorage.setItem('userName', phoneForm.value.phone)
    
    // 显示成功弹窗
    showSuccess.value = true
    startSuccessCountdown()
  } catch (error) {
    errorMessage.value = '登录失败，请检查手机号和验证码'
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 登录成功倒计时
const startSuccessCountdown = () => {
  successCountdown.value = 5
  successTimer = window.setInterval(() => {
    successCountdown.value--
    if (successCountdown.value <= 0) {
      jumpToHome()
    }
  }, 1000)
}

// 跳转到首页
const jumpToHome = () => {
  if (successTimer) clearInterval(successTimer)
  showSuccess.value = false
  router.push('/')
}

// 关闭成功弹窗
const closeSuccess = () => {
  if (successTimer) clearInterval(successTimer)
  showSuccess.value = false
}

// 初始化
onMounted(() => {
  // 如果默认是扫码登录，初始化二维码
  if (loginType.value === 'qr') {
    generateQRCode()
  }
})

// 清理定时器
onUnmounted(() => {
  if (successTimer) clearInterval(successTimer)
  if (codeTimer) clearInterval(codeTimer)
})
</script>

<!-- 全局样式 + 局部样式结合，解决展示问题 -->
<style>
/* 全局引入Font Awesome图标 */
@import url('https://cdn.bootcdn.net/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css');

/* 全局粉色调变量 */
:root {
  --primary-pink: #e91e63;
  --secondary-pink: #ec407a;
  --light-pink: #f8bbd9;
  --light-pink-2: #fce4ec;
  --pink-gradient: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

/* 全局重置样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}

html, body {
  height: 100%;
  overflow-x: hidden;
}

/* 页面基础布局 */
.login-page {
  min-height: 100vh;
  background: var(--pink-gradient);
  display: flex;
  flex-direction: column;
  color: #333;
}
</style>

<style scoped>
/* 登录容器核心样式 */
.login-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  min-height: calc(100vh - 180px); /* 预留导航栏和页脚高度 */
}

.login-wrapper {
  width: 100%;
  max-width: 1200px;
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  display: flex;
  min-height: 600px;
}

/* 左侧粉色渐变区域 */
.login-left {
  flex: 1.2;
  background: linear-gradient(135deg, var(--secondary-pink) 0%, var(--primary-pink) 100%);
  color: white;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: "";
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  transform: rotate(30deg);
  z-index: 1;
}

/* 左侧品牌文字 */
.brand-section {
  margin-bottom: 40px;
  position: relative;
  z-index: 2;
}

.brand-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 15px;
}

.brand-subtitle {
  font-size: 20px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.welcome-text {
  font-size: 32px;
  margin-bottom: 10px;
}

.welcome-desc {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
}

/* 左侧功能卡片 */
.features-section {
  margin: 30px 0;
  position: relative;
  z-index: 2;
}

.feature-card {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px 15px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  transition: all 0.3s;
}

.feature-card:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(5px);
}

.feature-icon {
  width: 40px;
  height: 40px;
  background-color: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 16px;
  flex-shrink: 0;
}

.feature-content h3 {
  font-weight: bold;
  margin-bottom: 4px;
  font-size: 16px;
}

.feature-content p {
  font-size: 13px;
  opacity: 0.9;
}

/* 立即体验按钮 */
.action-section {
  position: relative;
  z-index: 2;
}

.btn-experience {
  background: white;
  color: var(--primary-pink);
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 20px;
  text-decoration: none;
  display: inline-block;
}

.btn-experience:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
  background: #fff5f8;
}

/* 右侧登录区域 */
.login-right {
  flex: 1;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
}

/* 登录选项卡 */
.login-tabs {
  display: flex;
  margin-bottom: 25px;
  border-bottom: 1px solid #eee;
}

.tab-btn {
  padding: 12px 25px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
  border-bottom: 3px solid transparent;
  color: #666;
  background: none;
  border: none;
}

.tab-btn:hover {
  color: var(--primary-pink);
}

.tab-btn.active {
  color: var(--primary-pink);
  border-bottom: 3px solid var(--primary-pink);
  font-weight: bold;
}

/* 表单样式 */
.login-form {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #555;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 14px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
  box-sizing: border-box;
}

.form-control:focus {
  border-color: var(--primary-pink);
  box-shadow: 0 0 0 3px rgba(233, 30, 99, 0.1);
  outline: none;
}

.form-control:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

/* 验证码区域 */
.verify-code-group {
  display: flex;
  gap: 10px;
}

.verify-code-group .form-control {
  flex: 1;
}

.btn-send-code {
  background-color: var(--light-pink-2);
  color: var(--primary-pink);
  border: none;
  border-radius: 8px;
  padding: 0 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  font-weight: 500;
}

.btn-send-code:hover:not(:disabled) {
  background-color: var(--light-pink);
}

.btn-send-code:disabled {
  background-color: #f0f0f0;
  color: #999;
  cursor: not-allowed;
}

.timer {
  font-size: 12px;
  color: var(--primary-pink);
  margin-top: 5px;
  text-align: left;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
  font-size: 14px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #555;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: var(--primary-pink);
}

.forget-link {
  color: var(--primary-pink);
  text-decoration: none;
  font-size: 14px;
}

.forget-link:hover {
  text-decoration: underline;
}

/* 错误提示 */
.error-message {
  color: #f5222d;
  font-size: 13px;
  margin-bottom: 15px;
  padding: 8px;
  background: #fff1f0;
  border-radius: 6px;
  text-align: center;
  border-left: 3px solid #f5222d;
}

/* 登录按钮 */
.btn-login {
  width: 100%;
  padding: 14px;
  background-color: var(--primary-pink);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 600;
  margin-bottom: 15px;
}

.btn-login:hover:not(:disabled) {
  background-color: #d81b60;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(233, 30, 99, 0.3);
}

.btn-login:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 登录协议 */
.login-agreement {
  text-align: center;
  margin: 15px 0;
  font-size: 12px;
  color: #888;
}

.login-agreement a {
  color: var(--primary-pink);
  text-decoration: none;
}

.login-agreement a:hover {
  text-decoration: underline;
}

/* 扫码登录 */
.qr-login {
  text-align: center;
  padding: 30px 0;
}

.qr-tip-top {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
}

.qr-code-container {
  width: 200px;
  height: 200px;
  margin: 0 auto 20px;
  background: linear-gradient(45deg, #f0f0f0, #e0e0e0);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.qr-code-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.qr-loading {
  text-align: center;
  color: #888;
}

.qr-loading i {
  font-size: 50px;
  margin-bottom: 10px;
  color: var(--primary-pink);
}

.qr-tip-bottom {
  font-size: 13px;
  color: #777;
  margin-bottom: 20px;
}

.btn-refresh-qr {
  padding: 10px 20px;
  background: var(--light-pink-2);
  border: 1px solid var(--light-pink);
  border-radius: 8px;
  font-size: 14px;
  color: var(--primary-pink);
  cursor: pointer;
  transition: all 0.3s;
}

.btn-refresh-qr:hover:not(:disabled) {
  background: var(--primary-pink);
  color: white;
  border-color: var(--primary-pink);
}

.btn-refresh-qr:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 注册链接 */
.register-link {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  font-size: 14px;
}

.register-link a {
  color: var(--primary-pink);
  text-decoration: none;
  font-weight: bold;
}

.register-link a:hover {
  text-decoration: underline;
}

/* 其他登录方式 */
.other-login {
  margin-top: 30px;
  padding-top: 25px;
  border-top: 1px solid #eee;
}

.other-login h3 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 16px;
  color: #777;
  position: relative;
}

.other-login h3:before {
  content: "";
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background-color: #eee;
  z-index: 1;
}

.other-login h3 span {
  background-color: white;
  padding: 0 15px;
  position: relative;
  z-index: 2;
}

.other-login-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.other-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 70px;
  padding: 10px;
  border: none;
  background-color: transparent;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 8px;
}

.other-btn:hover {
  background-color: #f9f9f9;
  transform: translateY(-2px);
}

/* 社交图标 */
.social-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 5px;
  font-size: 18px;
  color: white;
}

.social-icon.wecom {
  background-color: #1aad19;
}

.social-icon.wechat {
  background-color: #07c160;
}

.social-icon.qq {
  background-color: #12b7f5;
}

.social-icon.email {
  background-color: #ff6d02;
}

.other-btn span {
  font-size: 12px;
  color: #777;
}

/* 登录成功弹窗 */
.success-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.success-content {
  background: white;
  border-radius: 16px;
  padding: 45px 35px;
  text-align: center;
  max-width: 400px;
  width: 90%;
  animation: slideUp 0.3s ease;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.success-icon {
  font-size: 60px;
  color: #4caf50;
  margin-bottom: 18px;
}

.success-content h2 {
  font-size: 22px;
  color: #333;
  margin-bottom: 12px;
}

.success-content p {
  font-size: 13px;
  color: #666;
  margin-bottom: 18px;
  line-height: 1.6;
}

.success-countdown {
  font-size: 12px;
  color: var(--primary-pink);
  margin-bottom: 22px;
  font-weight: bold;
}

.btn-jump {
  padding: 10px 28px;
  background-color: var(--primary-pink);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-jump:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(233, 30, 99, 0.3);
  background-color: #d81b60;
}

/* 响应式适配 */
@media (max-width: 968px) {
  .login-wrapper {
    flex-direction: column;
    max-width: 500px;
  }

  .login-left {
    padding: 30px 20px;
  }

  .login-right {
    padding: 30px 20px;
  }

  .other-login-buttons {
    gap: 10px;
  }

  .other-btn {
    width: 60px;
  }
}

@media (max-width: 480px) {
  .brand-title {
    font-size: 28px;
  }

  .feature-card {
    padding: 15px;
  }

  .qr-code-container {
    width: 160px;
    height: 160px;
  }

  .other-login-buttons {
    flex-wrap: wrap;
    gap: 10px;
  }
}
</style>