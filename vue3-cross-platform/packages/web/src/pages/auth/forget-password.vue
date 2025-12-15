<template>
  <div class="forget-password-page">
    <!-- 背景装饰元素 -->
    <div class="bg-decoration bg-circle-1"></div>
    <div class="bg-decoration bg-circle-2"></div>
    <div class="bg-decoration bg-wave-1"></div>
    
    <!-- 顶部导航栏 -->
    <LoginNavBar />

    <div class="forget-container">
      <div class="forget-card">
        <!-- 标题区域 -->
        <div class="forget-header">
          <div class="forget-logo">
            <i class="fas fa-key"></i>
          </div>
          <h1 class="forget-title">找回密码</h1>
          <p class="forget-subtitle">通过手机号或邮箱验证重置您的账户密码</p>
        </div>

        <!-- 步骤指示器 -->
        <div class="steps-indicator">
          <div class="step-item" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
            <div class="step-number">
              <span v-if="currentStep <= 1">1</span>
              <i v-else class="fas fa-check"></i>
            </div>
            <div class="step-label">验证身份</div>
          </div>
          <div class="step-line" :class="{ active: currentStep > 1 }"></div>
          <div class="step-item" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
            <div class="step-number">
              <span v-if="currentStep <= 2">2</span>
              <i v-else class="fas fa-check"></i>
            </div>
            <div class="step-label">重置密码</div>
          </div>
          <div class="step-line" :class="{ active: currentStep > 2 }"></div>
          <div class="step-item" :class="{ active: currentStep >= 3, completed: currentStep > 3 }">
            <div class="step-number">
              <span v-if="currentStep <= 3">3</span>
              <i v-else class="fas fa-check"></i>
            </div>
            <div class="step-label">完成</div>
          </div>
        </div>

        <!-- 步骤1：验证身份 -->
        <div v-show="currentStep === 1" class="step-content">
          <form @submit.prevent="handleVerifyIdentity">
            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-user"></i> 手机号/邮箱
              </label>
              <input
                type="text"
                class="form-control"
                v-model="verifyForm.account"
                placeholder="请输入手机号或邮箱"
                required
                :disabled="loading"
              />
            </div>
            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-shield-halved"></i> 验证码
              </label>
              <div class="verify-code-group">
                <input
                  type="text"
                  class="form-control"
                  v-model="verifyForm.verifyCode"
                  placeholder="请输入6位验证码"
                  required
                  :disabled="loading"
                />
                <button
                  type="button"
                  class="btn-send-code"
                  :disabled="loading || countdown > 0 || !verifyForm.account"
                  @click="sendVerifyCode"
                >
                  {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
                </button>
              </div>
            </div>
            <div v-if="errorMessage" class="error-message">
              <i class="fas fa-exclamation-circle"></i> {{ errorMessage }}
            </div>
            <div class="form-actions">
              <router-link to="/login" class="btn-back">
                <i class="fas fa-arrow-left"></i> 返回登录
              </router-link>
              <button type="submit" class="btn-primary" :disabled="loading">
                <span v-if="!loading">下一步</span>
                <span v-else><i class="fas fa-spinner fa-spin"></i> 验证中...</span>
              </button>
            </div>
          </form>
        </div>

        <!-- 步骤2：重置密码 -->
        <div v-show="currentStep === 2" class="step-content">
          <form @submit.prevent="handleResetPassword">
            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-lock"></i> 新密码
              </label>
              <input
                type="password"
                class="form-control"
                v-model="passwordForm.newPassword"
                placeholder="8-16位，包含字母+数字+特殊字符更佳"
                required
                :disabled="loading"
                @input="checkPasswordStrength"
              />
              <div class="password-strength" v-if="passwordForm.newPassword">
                <div class="strength-label">密码强度：</div>
                <div class="strength-bar">
                  <div
                    class="strength-fill"
                    :class="passwordStrength"
                    :style="{ width: strengthWidth }"
                  ></div>
                </div>
                <div class="strength-text" :class="passwordStrength">
                  {{ strengthText }}
                </div>
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-lock-open"></i> 确认新密码
              </label>
              <input
                type="password"
                class="form-control"
                v-model="passwordForm.confirmPassword"
                placeholder="请再次输入新密码"
                required
                :disabled="loading"
              />
              <div v-if="passwordForm.confirmPassword && !passwordMatch" class="error-text">
                <i class="fas fa-times-circle"></i> 两次输入的密码不一致
              </div>
            </div>
            <div v-if="errorMessage" class="error-message">
              <i class="fas fa-exclamation-circle"></i> {{ errorMessage }}
            </div>
            <div class="form-actions">
              <button type="button" class="btn-back" @click="currentStep = 1" :disabled="loading">
                <i class="fas fa-arrow-left"></i> 上一步
              </button>
              <button type="submit" class="btn-primary" :disabled="loading || !passwordMatch">
                <span v-if="!loading">重置密码</span>
                <span v-else><i class="fas fa-spinner fa-spin"></i> 重置中...</span>
              </button>
            </div>
          </form>
        </div>

        <!-- 步骤3：完成 -->
        <div v-show="currentStep === 3" class="step-content success-content">
          <div class="success-icon">
            <div class="success-circle">
              <i class="fas fa-check"></i>
            </div>
          </div>
          <h2 class="success-title">密码重置成功！</h2>
          <p class="success-message">您的账户密码已成功重置，安全系数UP↑</p>
          <p class="success-tip">请使用新密码登录您的账户</p>
          <div class="success-countdown">
            <span>{{ successCountdown }}秒后自动跳转到登录页面...</span>
          </div>
          <button class="btn-login" @click="jumpToLogin">
            <i class="fas fa-sign-in-alt"></i> 立即登录
          </button>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import LoginNavBar from '@/components/common/LoginNavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const router = useRouter()

// 当前步骤
const currentStep = ref(1)

// 状态
const loading = ref(false)
const errorMessage = ref('')
const countdown = ref(0) // 验证码倒计时
const successCountdown = ref(5) // 成功提示倒计时

// 验证身份表单
const verifyForm = ref({
  account: '',
  verifyCode: ''
})

// 重置密码表单
const passwordForm = ref({
  newPassword: '',
  confirmPassword: ''
})

// 密码强度
const passwordStrength = ref<'weak' | 'medium' | 'strong'>('weak')
const strengthWidth = ref('33%')
const strengthText = ref('弱')

// 计算属性
const passwordMatch = computed(() => {
  return passwordForm.value.newPassword === passwordForm.value.confirmPassword &&
    passwordForm.value.newPassword.length > 0
})

// 检查密码强度
const checkPasswordStrength = () => {
  const password = passwordForm.value.newPassword
  if (!password) {
    passwordStrength.value = 'weak'
    strengthWidth.value = '33%'
    strengthText.value = '弱'
    return
  }

  let strength = 0
  // 长度检查
  if (password.length >= 8) strength++
  if (password.length >= 12) strength++
  // 包含数字
  if (/\d/.test(password)) strength++
  // 包含小写字母
  if (/[a-z]/.test(password)) strength++
  // 包含大写字母
  if (/[A-Z]/.test(password)) strength++
  // 包含特殊字符
  if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) strength++

  if (strength <= 2) {
    passwordStrength.value = 'weak'
    strengthWidth.value = '33%'
    strengthText.value = '弱'
  } else if (strength <= 4) {
    passwordStrength.value = 'medium'
    strengthWidth.value = '66%'
    strengthText.value = '中'
  } else {
    passwordStrength.value = 'strong'
    strengthWidth.value = '100%'
    strengthText.value = '强'
  }
}

// 发送验证码
const sendVerifyCode = async () => {
  if (!verifyForm.value.account) {
    errorMessage.value = '请输入手机号或邮箱'
    return
  }

  // 验证手机号或邮箱格式
  const phoneRegex = /^1[3-9]\d{9}$/
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  
  if (!phoneRegex.test(verifyForm.value.account) && !emailRegex.test(verifyForm.value.account)) {
    errorMessage.value = '请输入正确的手机号或邮箱'
    return
  }

  try {
    errorMessage.value = ''
    // 模拟发送验证码API
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
    
    await new Promise(resolve => setTimeout(resolve, 1000))
  } catch (error: any) {
    errorMessage.value = error.message || '发送验证码失败'
  }
}

// 验证身份
const handleVerifyIdentity = async () => {
  errorMessage.value = ''
  
  if (!verifyForm.value.account || !verifyForm.value.verifyCode) {
    errorMessage.value = '请填写完整信息'
    return
  }

  try {
    loading.value = true
    // 模拟验证身份API
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 验证成功，进入下一步
    currentStep.value = 2
  } catch (error: any) {
    errorMessage.value = error.message || '验证失败，请检查验证码'
  } finally {
    loading.value = false
  }
}

// 重置密码
const handleResetPassword = async () => {
  errorMessage.value = ''
  
  if (!passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    errorMessage.value = '请填写完整信息'
    return
  }

  if (!passwordMatch.value) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  if (passwordForm.value.newPassword.length < 8) {
    errorMessage.value = '密码长度不能少于8位'
    return
  }

  try {
    loading.value = true
    // 模拟重置密码API
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 重置成功，进入完成步骤
    currentStep.value = 3
    startSuccessCountdown()
  } catch (error: any) {
    errorMessage.value = error.message || '重置密码失败，请重试'
  } finally {
    loading.value = false
  }
}

// 成功提示倒计时
let successTimer: number | null = null
const startSuccessCountdown = () => {
  successCountdown.value = 5
  if (successTimer) {
    clearInterval(successTimer)
  }
  successTimer = window.setInterval(() => {
    successCountdown.value--
    if (successCountdown.value <= 0) {
      if (successTimer) {
        clearInterval(successTimer)
      }
      jumpToLogin()
    }
  }, 1000)
}

// 跳转到登录页
const jumpToLogin = () => {
  if (successTimer) {
    clearInterval(successTimer)
  }
  router.push('/login')
}

// 生命周期
onMounted(() => {
  // 初始化
})

onUnmounted(() => {
  if (successTimer) {
    clearInterval(successTimer)
  }
})
</script>

<style scoped>
/* 全局引入Font Awesome（若项目未全局引入） */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

.forget-password-page {
  min-height: 100vh;
  background: linear-gradient(120deg, #fdf2f8 0%, #fef7fb 30%, #fcf1f7 100%);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

/* 背景装饰元素 */
.bg-decoration {
  position: absolute;
  z-index: 0;
  opacity: 0.15;
  pointer-events: none;
}

.bg-circle-1 {
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: radial-gradient(circle, #d81b60 0%, transparent 70%);
  top: -100px;
  right: -100px;
}

.bg-circle-2 {
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: radial-gradient(circle, #9c27b0 0%, transparent 70%);
  bottom: -100px;
  left: -100px;
}

.bg-wave-1 {
  width: 100%;
  height: 150px;
  bottom: 0;
  left: 0;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1440 320'%3E%3Cpath fill='%23d81b60' fill-opacity='0.2' d='M0,192L48,176C96,160,192,128,288,128C384,128,480,160,576,181.3C672,203,768,213,864,197.3C960,181,1056,139,1152,128C1248,117,1344,139,1392,149.3L1440,160L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z'%3E%3C/path%3E%3C/svg%3E");
  background-size: cover;
}

.forget-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  position: relative;
  z-index: 1;
}

.forget-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 60px 50px;
  box-shadow: 0 15px 50px rgba(216, 27, 96, 0.12);
  max-width: 520px;
  width: 100%;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
}

/* 标题区域美化 */
.forget-header {
  text-align: center;
  margin-bottom: 50px;
}

.forget-logo {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  box-shadow: 0 8px 20px rgba(216, 27, 96, 0.2);
}

.forget-logo i {
  font-size: 36px;
  color: white;
}

.forget-title {
  font-size: 32px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
}

.forget-subtitle {
  font-size: 15px;
  color: #718096;
  line-height: 1.6;
  max-width: 400px;
  margin: 0 auto;
}

/* 步骤指示器美化 */
.steps-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 50px;
  padding: 0 20px;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  position: relative;
}

.step-number {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #f8f9fa;
  color: #cbd5e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid #e2e8f0;
}

.step-item.active .step-number {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  border-color: #d81b60;
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.2);
  transform: scale(1.05);
}

.step-item.completed .step-number {
  background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
  color: white;
  border-color: #4caf50;
}

.step-label {
  font-size: 14px;
  color: #a0aec0;
  transition: all 0.3s;
  font-weight: 500;
}

.step-item.active .step-label {
  color: #d81b60;
  font-weight: 600;
}

.step-item.completed .step-label {
  color: #4caf50;
}

.step-line {
  flex: 1;
  height: 3px;
  background: #e2e8f0;
  margin: 0 20px;
  border-radius: 3px;
  transition: all 0.4s;
}

.step-line.active {
  background: linear-gradient(90deg, #d81b60 0%, #c2185b 100%);
}

/* 表单区域美化 */
.step-content {
  animation: fadeIn 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-group {
  margin-bottom: 25px;
}

.form-label {
  display: block;
  margin-bottom: 10px;
  font-size: 15px;
  color: #2d3748;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-label i {
  color: #d81b60;
  font-size: 16px;
}

.form-control {
  width: 100%;
  padding: 16px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-sizing: border-box;
  background: #f8f9fa;
}

.form-control:focus {
  outline: none;
  border-color: #d81b60;
  background: white;
  box-shadow: 0 0 0 4px rgba(216, 27, 96, 0.08);
  transform: translateY(-2px);
}

.form-control:disabled {
  background: #faf0f5;
  cursor: not-allowed;
  opacity: 0.8;
}

/* 验证码输入组 */
.verify-code-group {
  display: flex;
  gap: 12px;
}

.verify-code-group .form-control {
  flex: 1;
}

.btn-send-code {
  padding: 16px 24px;
  background: #f8f9fa;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 15px;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
  font-weight: 500;
}

.btn-send-code:hover:not(:disabled) {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  border-color: #d81b60;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.2);
}

.btn-send-code:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 密码强度美化 */
.password-strength {
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.strength-label {
  font-size: 14px;
  color: #718096;
  white-space: nowrap;
  font-weight: 500;
}

.strength-bar {
  flex: 1;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  border-radius: 3px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.strength-fill.weak {
  background: #f44336;
}

.strength-fill.medium {
  background: #ff9800;
}

.strength-fill.strong {
  background: #4caf50;
}

.strength-text {
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  width: 40px;
  text-align: center;
}

.strength-text.weak {
  color: #f44336;
}

.strength-text.medium {
  color: #ff9800;
}

.strength-text.strong {
  color: #4caf50;
}

/* 错误提示美化 */
.error-text {
  font-size: 13px;
  color: #f44336;
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.error-message {
  color: #f44336;
  font-size: 14px;
  margin-bottom: 20px;
  padding: 12px 16px;
  background: #ffebee;
  border-radius: 12px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-left: 4px solid #f44336;
}

/* 按钮组美化 */
.form-actions {
  display: flex;
  justify-content: space-between;
  gap: 15px;
  margin-top: 40px;
}

.btn-back {
  padding: 16px 30px;
  background: #f8f9fa;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  color: #4a5568;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 500;
}

.btn-back:hover:not(:disabled) {
  background: #faf0f5;
  border-color: #f06292;
  color: #d81b60;
  transform: translateY(-2px);
}

.btn-back:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn-primary {
  flex: 1;
  padding: 16px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(216, 27, 96, 0.3);
  background: linear-gradient(135deg, #c2185b 0%, #ad1457 100%);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 成功页面美化 */
.success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  margin-bottom: 30px;
}

.success-circle {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  box-shadow: 0 10px 30px rgba(76, 175, 80, 0.2);
  animation: scaleIn 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes scaleIn {
  from {
    transform: scale(0);
  }
  to {
    transform: scale(1);
  }
}

.success-circle i {
  font-size: 60px;
  color: white;
}

.success-title {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 20px;
}

.success-message {
  font-size: 17px;
  color: #4a5568;
  margin-bottom: 15px;
  line-height: 1.6;
}

.success-tip {
  font-size: 15px;
  color: #718096;
  margin-bottom: 30px;
}

.success-countdown {
  font-size: 14px;
  color: #a0aec0;
  margin-bottom: 30px;
}

.btn-login {
  padding: 16px 50px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  border: none;
  border-radius: 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-login:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(216, 27, 96, 0.3);
  background: linear-gradient(135deg, #c2185b 0%, #ad1457 100%);
}

/* 响应式适配优化 */
@media (max-width: 768px) {
  .forget-card {
    padding: 50px 35px;
    border-radius: 20px;
  }

  .forget-title {
    font-size: 28px;
  }

  .steps-indicator {
    padding: 0 10px;
  }

  .step-line {
    margin: 0 15px;
  }

  .step-label {
    font-size: 13px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn-back {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .forget-card {
    padding: 40px 25px;
    border-radius: 16px;
  }

  .forget-logo {
    width: 70px;
    height: 70px;
  }

  .forget-logo i {
    font-size: 30px;
  }

  .steps-indicator {
    flex-wrap: wrap;
    gap: 20px 10px;
  }

  .step-item {
    flex: 1;
    min-width: 90px;
  }

  .step-line {
    display: none;
  }

  .verify-code-group {
    flex-direction: column;
  }

  .btn-send-code {
    width: 100%;
  }
}
</style>