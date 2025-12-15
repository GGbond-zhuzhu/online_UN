<template>
  <div class="register-page">
    <!-- 顶部导航栏 -->
    <div class="header">
      <router-link to="/" class="header-logo">上大学Online</router-link>
      <div class="header-links">
        <router-link to="/">首页</router-link>
        <router-link to="/app-download">手机App下载</router-link>
        <router-link to="/client-download">电脑客户端</router-link>
        <router-link to="/security">安全保障</router-link>
        <router-link to="/help">使用帮助</router-link>
        <router-link to="/feedback">问题反馈</router-link>
        <router-link to="/about">关于我们</router-link>

        <!-- 登录状态区域 -->
        <div class="login-status">
          <div class="not-logged-in">
            <router-link to="/login" class="login-link-nav">登录</router-link>
          </div>
        </div>
      </div>
    </div>

    <div class="register-container">
      <div class="register-wrapper">
        <div class="register-left">
          <div class="logo">上大学Online</div>
          <div class="platform-name">校园综合服务平台</div>
          <div class="welcome-text">立即注册</div>
          <div class="slogan">加入我们，开启智慧校园生活</div>

          <div class="features">
            <div class="feature-item">
              <div class="feature-icon"><i class="fas fa-credit-card"></i></div>
              <div class="feature-text">
                <div class="feature-title">校园E卡通</div>
                <div class="feature-desc">一码通校园，便捷支付与身份认证</div>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon"><i class="fas fa-exchange-alt"></i></div>
              <div class="feature-text">
                <div class="feature-title">二手交易</div>
                <div class="feature-desc">同校精准匹配，闲置物品变废为宝</div>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon"><i class="fas fa-briefcase"></i></div>
              <div class="feature-text">
                <div class="feature-title">兼职服务</div>
                <div class="feature-desc">海量岗位，保障薪资安全可靠</div>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon"><i class="fas fa-calendar-alt"></i></div>
              <div class="feature-text">
                <div class="feature-title">行程管理</div>
                <div class="feature-desc">智能提醒同步，学习生活有条不紊</div>
              </div>
            </div>
          </div>
          <button class="experience-btn" @click="goToHome">立即体验</button>
        </div>

        <div class="register-right">
          <div class="register-title">注册新账号</div>

          <!-- 注册表单 -->
          <form class="register-form" @submit.prevent="handleSubmit" v-show="!showSuccess">
            <div class="form-group">
              <label for="username">用户名</label>
              <input
                type="text"
                id="username"
                class="form-control"
                placeholder="请输入用户名（4-20位字符）"
                v-model="form.username"
                @input="validateUsername"
                :style="{ borderColor: usernameBorderColor }"
                required
                :disabled="loading"
              >
              <div class="validation-message" :class="usernameValidationClass" id="username-validation">
                <i :class="usernameValidationIcon" class="validation-icon"></i>
                <span>{{ usernameValidationText }}</span>
              </div>
            </div>

            <div class="form-group">
              <label for="phone">手机号</label>
              <input
                type="tel"
                id="phone"
                class="form-control"
                placeholder="请输入手机号"
                v-model="form.phone"
                @input="validatePhone"
                :style="{ borderColor: phoneBorderColor }"
                required
                :disabled="loading"
              >
              <div class="validation-message" :class="phoneValidationClass" id="phone-validation">
                <i :class="phoneValidationIcon" class="validation-icon"></i>
                <span>{{ phoneValidationText }}</span>
              </div>
            </div>

            <div class="form-group">
              <label for="email">邮箱（选填）</label>
              <input
                type="email"
                id="email"
                class="form-control"
                placeholder="请输入邮箱地址"
                v-model="form.email"
                @input="validateEmail"
                :style="{ borderColor: emailBorderColor }"
                :disabled="loading"
              >
              <div class="validation-message" :class="emailValidationClass" id="email-validation">
                <i :class="emailValidationIcon" class="validation-icon"></i>
                <span>{{ emailValidationText }}</span>
              </div>
            </div>

            <div class="form-group">
              <label for="password">密码</label>
              <div class="password-wrapper">
                <input
                  type="password"
                  id="password"
                  class="form-control"
                  placeholder="请输入密码（6-20位字符）"
                  v-model="form.password"
                  @input="validatePassword"
                  required
                  :disabled="loading"
                >
              </div>
              <div class="password-strength">
                <div class="strength-bar" :class="passwordStrengthClass"></div>
              </div>
              <!-- 密码规则提示 -->
              <div class="password-rules" id="password-rules">
                <div class="rule-item">
                  <i :class="ruleIcons.length" class="rule-icon"></i>
                  <span>密码长度6-20位</span>
                </div>
                <div class="rule-item">
                  <i :class="ruleIcons.uppercase" class="rule-icon"></i>
                  <span>包含大写字母</span>
                </div>
                <div class="rule-item">
                  <i :class="ruleIcons.lowercase" class="rule-icon"></i>
                  <span>包含小写字母</span>
                </div>
                <div class="rule-item">
                  <i :class="ruleIcons.number" class="rule-icon"></i>
                  <span>包含数字</span>
                </div>
                <div class="rule-item">
                  <i :class="ruleIcons.special" class="rule-icon"></i>
                  <span>包含特殊字符</span>
                </div>
              </div>
              <div class="validation-message" :class="passwordValidationClass" id="password-validation">
                <i :class="passwordValidationIcon" class="validation-icon"></i>
                <span>{{ passwordValidationText }}</span>
              </div>
            </div>

            <div class="form-group">
              <label for="confirm-password">确认密码</label>
              <div class="password-wrapper">
                <input
                  type="password"
                  id="confirm-password"
                  class="form-control"
                  placeholder="请再次输入密码"
                  v-model="form.confirmPassword"
                  @input="validateConfirmPassword"
                  :style="{ borderColor: confirmPasswordBorderColor }"
                  required
                  :disabled="loading"
                >
              </div>
              <div class="match-status" :class="passwordMatchClass" id="password-match">
                <i :class="passwordMatchIcon" class="match-icon"></i>
                <span>{{ passwordMatchText }}</span>
              </div>
            </div>

            <div class="form-group">
              <label for="verification-code">验证码</label>
              <div class="code-group">
                <input
                  type="text"
                  id="verification-code"
                  class="form-control code-input"
                  placeholder="请输入验证码"
                  v-model="form.verifyCode"
                  @input="updateRegisterButtonStatus"
                  required
                  :disabled="loading"
                >
                <button
                  type="button"
                  class="send-code-btn"
                  id="send-code-btn"
                  @click="sendVerifyCode"
                  :disabled="isCounting || !isPhoneValid || loading"
                >
                  {{ isCounting ? `${countdown}秒后重发` : '发送验证码' }}
                </button>
              </div>
              <div class="timer" id="code-timer" v-if="isCounting">{{ countdown }}秒后重新发送</div>
            </div>

            <div class="agreement-group">
              <input
                type="checkbox"
                id="agree"
                v-model="form.agreeTerms"
                @change="updateRegisterButtonStatus"
                :disabled="loading"
                required
              >
              <label for="agree">
                我已阅读并同意
                <router-link to="/terms" target="_blank">《服务条款》</router-link>
                和
                <router-link to="/privacy" target="_blank">《隐私政策》</router-link>
              </label>
            </div>

            <button
              type="submit"
              class="register-btn"
              id="register-btn"
              :disabled="!isFormValid || loading"
            >
              {{ loading ? '注册中...' : '注册账号' }}
            </button>
          </form>

          <!-- 注册成功消息 -->
          <div class="success-message" id="success-message" v-show="showSuccess">
            <h2>注册成功！</h2>
            <p>恭喜您已成功注册上大学Online账号</p>
            <p>我们将向您的手机发送验证短信，请及时验证</p>
            <p><span>{{ redirectCountdown }}</span>秒后自动跳转到首页...</p>
            <button class="register-btn" id="go-home" style="margin-top: 15px;" @click="goToHome">立即跳转</button>
          </div>

          <!-- 登录链接 -->
          <div class="login-link" v-show="!showSuccess">
            已有账号？<router-link to="/login">立即登录</router-link>
          </div>

          <!-- 社交注册区域 -->
          <div class="social-register" v-show="!showSuccess">
            <h3><span>其他注册方式</span></h3>
            <div class="social-buttons">
              <button class="social-btn" @click="goToWecomLogin">
                <div class="social-icon wecom">企</div>
                <span class="social-name">企业微信</span>
              </button>
              <button class="social-btn" @click="goToWechatLogin">
                <div class="social-icon wechat">微</div>
                <span class="social-name">微信</span>
              </button>
              <button class="social-btn" @click="goToQQLogin">
                <div class="social-icon qq">Q</div>
                <span class="social-name">QQ</span>
              </button>
              <button class="social-btn" @click="goToEmailLogin">
                <div class="social-icon email">邮</div>
                <span class="social-name">邮箱</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="footer">
      <div>北京软工杨啵啵有限公司 版权所有 京ICP备12046592号 经营许可证：京B2-20170388</div>
      <div class="footer-links">
        <router-link to="/privacy">隐私协议</router-link>
        <router-link to="/terms">服务条款</router-link>
        <router-link to="/about">关于我们</router-link>
        <router-link to="/about">关于我们</router-link>
        <router-link to="/help">帮助中心</router-link>
        <router-link to="/feedback">问题反馈</router-link>
      </div>
    </div>

    <div class="help-bubble" @click="showHelpInfo">
      <i class="fas fa-comment"></i>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'

// 引入API和工具函数
import { register } from '@campus/common'
import { setUserInfo } from '@campus/common'

const router = useRouter()

// 表单数据
interface RegisterForm {
  username: string
  phone: string
  email: string
  password: string
  confirmPassword: string
  verifyCode: string
  agreeTerms: boolean
}

const form = ref<RegisterForm>({
  username: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  verifyCode: '',
  agreeTerms: false
})

// 状态管理
const loading = ref(false)
const showSuccess = ref(false)
const isCounting = ref(false)
const countdown = ref(60)
const redirectCountdown = ref(5)
let codeTimer: NodeJS.Timeout | null = null
let redirectTimer: NodeJS.Timeout | null = null

// 验证状态 - 用户名
const usernameValidationText = ref('用户名应为4-20位字符，可包含字母、数字、下划线')
const usernameValidationClass = ref('validation-message validation-info')
const usernameValidationIcon = ref('fas fa-info-circle')
const usernameBorderColor = ref('#ddd')
const isUsernameValid = ref(false)

// 验证状态 - 手机号
const phoneValidationText = ref('请输入正确的手机号码格式')
const phoneValidationClass = ref('validation-message validation-info')
const phoneValidationIcon = ref('fas fa-info-circle')
const phoneBorderColor = ref('#ddd')
const isPhoneValid = ref(false)

// 验证状态 - 邮箱
const emailValidationText = ref('请输入正确的邮箱格式（选填）')
const emailValidationClass = ref('validation-message validation-info')
const emailValidationIcon = ref('fas fa-info-circle')
const emailBorderColor = ref('#ddd')
const isEmailValid = ref(true) // 选填，默认有效

// 密码规则验证
interface PasswordRules {
  length: boolean
  uppercase: boolean
  lowercase: boolean
  number: boolean
  special: boolean
}

const passwordRules = ref<PasswordRules>({
  length: false,
  uppercase: false,
  lowercase: false,
  number: false,
  special: false
})

const ruleIcons = computed(() => ({
  length: passwordRules.value.length 
    ? 'fas fa-check rule-icon rule-valid' 
    : passwordRules.value.length === false && form.value.password 
      ? 'fas fa-times rule-icon rule-invalid' 
      : 'fas fa-circle rule-icon rule-pending',
  uppercase: passwordRules.value.uppercase 
    ? 'fas fa-check rule-icon rule-valid' 
    : passwordRules.value.uppercase === false && form.value.password 
      ? 'fas fa-times rule-icon rule-invalid' 
      : 'fas fa-circle rule-icon rule-pending',
  lowercase: passwordRules.value.lowercase 
    ? 'fas fa-check rule-icon rule-valid' 
    : passwordRules.value.lowercase === false && form.value.password 
      ? 'fas fa-times rule-icon rule-invalid' 
      : 'fas fa-circle rule-icon rule-pending',
  number: passwordRules.value.number 
    ? 'fas fa-check rule-icon rule-valid' 
    : passwordRules.value.number === false && form.value.password 
      ? 'fas fa-times rule-icon rule-invalid' 
      : 'fas fa-circle rule-icon rule-pending',
  special: passwordRules.value.special 
    ? 'fas fa-check rule-icon rule-valid' 
    : passwordRules.value.special === false && form.value.password 
      ? 'fas fa-times rule-icon rule-invalid' 
      : 'fas fa-circle rule-icon rule-pending'
}))

// 密码强度
const passwordStrengthClass = computed(() => {
  const strength = Object.values(passwordRules.value).filter(Boolean).length
  if (strength <= 2) return 'strength-bar strength-weak'
  if (strength <= 4) return 'strength-bar strength-medium'
  return 'strength-bar strength-strong'
})

// 密码验证状态
const passwordValidationText = ref('密码必须包含大小写字母、数字和特殊字符')
const passwordValidationClass = ref('validation-message validation-info')
const passwordValidationIcon = ref('fas fa-info-circle')
const isPasswordValid = ref(false)

// 确认密码验证
const passwordMatchText = ref('请再次输入密码以确认')
const passwordMatchClass = ref('match-status validation-info')
const passwordMatchIcon = ref('fas fa-info-circle')
const confirmPasswordBorderColor = ref('#ddd')
const isPasswordMatch = ref(false)

// 表单整体有效性
const isFormValid = computed(() => {
  return (
    isUsernameValid.value &&
    isPhoneValid.value &&
    isPasswordValid.value &&
    isPasswordMatch.value &&
    form.value.verifyCode.trim() !== '' &&
    form.value.agreeTerms
  )
})

// 验证用户名
const validateUsername = () => {
  const username = form.value.username.trim()
  
  if (!username) {
    usernameValidationText.value = '用户名应为4-20位字符，可包含字母、数字、下划线'
    usernameValidationClass.value = 'validation-message validation-info'
    usernameValidationIcon.value = 'fas fa-info-circle'
    usernameBorderColor.value = '#ddd'
    isUsernameValid.value = false
  } else if (username.length >= 4 && username.length <= 20 && /^[a-zA-Z0-9_]+$/.test(username)) {
    usernameValidationText.value = '用户名格式正确'
    usernameValidationClass.value = 'validation-message validation-success'
    usernameValidationIcon.value = 'fas fa-check-circle'
    usernameBorderColor.value = '#2ed573'
    isUsernameValid.value = true
  } else {
    usernameValidationText.value = '用户名格式不正确'
    usernameValidationClass.value = 'validation-message validation-error'
    usernameValidationIcon.value = 'fas fa-times-circle'
    usernameBorderColor.value = '#ff4757'
    isUsernameValid.value = false
  }
  
  updateRegisterButtonStatus()
}

// 验证手机号
const validatePhone = () => {
  const phone = form.value.phone.trim()
  
  if (!phone) {
    phoneValidationText.value = '请输入正确的手机号码格式'
    phoneValidationClass.value = 'validation-message validation-info'
    phoneValidationIcon.value = 'fas fa-info-circle'
    phoneBorderColor.value = '#ddd'
    isPhoneValid.value = false
  } else if (/^1[3-9]\d{9}$/.test(phone)) {
    phoneValidationText.value = '手机号格式正确'
    phoneValidationClass.value = 'validation-message validation-success'
    phoneValidationIcon.value = 'fas fa-check-circle'
    phoneBorderColor.value = '#2ed573'
    isPhoneValid.value = true
  } else {
    phoneValidationText.value = '手机号格式不正确'
    phoneValidationClass.value = 'validation-message validation-error'
    phoneValidationIcon.value = 'fas fa-times-circle'
    phoneBorderColor.value = '#ff4757'
    isPhoneValid.value = false
  }
  
  updateRegisterButtonStatus()
}

// 验证邮箱
const validateEmail = () => {
  const email = form.value.email.trim()
  
  if (!email) {
    emailValidationText.value = '请输入正确的邮箱格式（选填）'
    emailValidationClass.value = 'validation-message validation-info'
    emailValidationIcon.value = 'fas fa-info-circle'
    emailBorderColor.value = '#ddd'
    isEmailValid.value = true
  } else if (/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
    emailValidationText.value = '邮箱格式正确'
    emailValidationClass.value = 'validation-message validation-success'
    emailValidationIcon.value = 'fas fa-check-circle'
    emailBorderColor.value = '#2ed573'
    isEmailValid.value = true
  } else {
    emailValidationText.value = '邮箱格式不正确'
    emailValidationClass.value = 'validation-message validation-error'
    emailValidationIcon.value = 'fas fa-times-circle'
    emailBorderColor.value = '#ff4757'
    isEmailValid.value = false
  }
  
  updateRegisterButtonStatus()
}

// 验证密码
const validatePassword = () => {
  const password = form.value.password
  
  // 更新密码规则
  passwordRules.value = {
    length: password.length >= 6 && password.length <= 20,
    uppercase: /[A-Z]/.test(password),
    lowercase: /[a-z]/.test(password),
    number: /\d/.test(password),
    special: /[^A-Za-z0-9]/.test(password)
  }
  
  // 验证密码有效性
  if (!password) {
    passwordValidationText.value = '密码必须包含大小写字母、数字和特殊字符'
    passwordValidationClass.value = 'validation-message validation-info'
    passwordValidationIcon.value = 'fas fa-info-circle'
    isPasswordValid.value = false
  } else if (Object.values(passwordRules.value).every(rule => rule)) {
    passwordValidationText.value = '密码符合要求'
    passwordValidationClass.value = 'validation-message validation-success'
    passwordValidationIcon.value = 'fas fa-check-circle'
    isPasswordValid.value = true
  } else {
    passwordValidationText.value = '密码不符合要求'
    passwordValidationClass.value = 'validation-message validation-error'
    passwordValidationIcon.value = 'fas fa-times-circle'
    isPasswordValid.value = false
  }
  
  // 同步验证确认密码
  validateConfirmPassword()
  updateRegisterButtonStatus()
}

// 验证确认密码
const validateConfirmPassword = () => {
  const password = form.value.password
  const confirmPassword = form.value.confirmPassword
  
  if (!confirmPassword) {
    passwordMatchText.value = '请再次输入密码以确认'
    passwordMatchClass.value = 'match-status validation-info'
    passwordMatchIcon.value = 'fas fa-info-circle'
    confirmPasswordBorderColor.value = '#ddd'
    isPasswordMatch.value = false
  } else if (password === confirmPassword) {
    passwordMatchText.value = '密码匹配'
    passwordMatchClass.value = 'match-status match-success'
    passwordMatchIcon.value = 'fas fa-check-circle'
    confirmPasswordBorderColor.value = '#2ed573'
    isPasswordMatch.value = true
  } else {
    passwordMatchText.value = '密码不匹配'
    passwordMatchClass.value = 'match-status match-error'
    passwordMatchIcon.value = 'fas fa-times-circle'
    confirmPasswordBorderColor.value = '#ff4757'
    isPasswordMatch.value = false
  }
  
  updateRegisterButtonStatus()
}

// 更新注册按钮状态（兼容原有逻辑）
const updateRegisterButtonStatus = () => {
  // 空函数，主要靠computed的isFormValid控制
}

// 发送验证码
const sendVerifyCode = () => {
  if (isCounting.value || !isPhoneValid.value) return
  
  // 模拟发送验证码
  alert('验证码已发送到您的手机，请注意查收')
  
  // 开始倒计时
  isCounting.value = true
  countdown.value = 60
  
  codeTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(codeTimer!)
      isCounting.value = false
    }
  }, 1000)
}

// 表单提交
const handleSubmit = async () => {
  // 最终验证
  if (!isUsernameValid.value) {
    alert('用户名必须是4-20位字符，只能包含字母、数字、下划线')
    return
  }
  
  if (!isPhoneValid.value) {
    alert('请输入正确的手机号码')
    return
  }
  
  if (form.value.email && !isEmailValid.value) {
    alert('请输入正确的邮箱地址')
    return
  }
  
  if (!isPasswordValid.value) {
    alert('密码不符合要求，请确保密码包含大小写字母、数字和特殊字符，且长度为6-20位')
    return
  }
  
  if (!isPasswordMatch.value) {
    alert('两次输入的密码不一致')
    return
  }
  
  if (!form.value.verifyCode.trim()) {
    alert('请输入验证码')
    return
  }
  
  if (!form.value.agreeTerms) {
    alert('请同意服务条款和隐私政策')
    return
  }
  
  try {
    loading.value = true
    
    // 调用真实注册API
    const result = await register({
      username: form.value.username,
      password: form.value.password,
      phone: form.value.phone,
      email: form.value.email || undefined
    })
    
    // 注册成功后，可以选择自动登录（如果需要）
    // 注意：后端注册接口可能不返回token，需要用户手动登录
    // 这里只保存用户ID和用户名
    if (result) {
      setUserInfo({
        id: result.id,
        username: result.username
      })
    }
    
    // 显示成功提示
    showSuccess.value = true
    
    // 开始跳转倒计时
    redirectCountdown.value = 5
    redirectTimer = setInterval(() => {
      redirectCountdown.value--
      if (redirectCountdown.value <= 0) {
        goToHome()
      }
    }, 1000)
    
  } catch (error: any) {
    const errorMsg = error?.message || '注册失败，请稍后重试'
    alert(errorMsg)
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

// 页面跳转函数
const goToHome = () => {
  if (redirectTimer) clearInterval(redirectTimer)
  router.push('/')
}

const goToWecomLogin = () => {
  router.push('/wecom-login')
}

const goToWechatLogin = () => {
  router.push('/wechat-login')
}

const goToQQLogin = () => {
  // QQ登录暂未实现
  alert('QQ登录功能暂未开放')
}

const goToEmailLogin = () => {
  router.push('/email-login')
}

// 帮助气泡
const showHelpInfo = () => {
  alert('需要帮助吗？请联系客服：400-123-4567\n工作时间：周一至周日 9:00-21:00')
}

// 监听手机号变化，重置验证码倒计时
watch([() => form.value.phone], () => {
  if (codeTimer) {
    clearInterval(codeTimer)
    isCounting.value = false
  }
})

// 清理定时器
onUnmounted(() => {
  if (codeTimer) clearInterval(codeTimer)
  if (redirectTimer) clearInterval(redirectTimer)
})

// 初始化验证
onMounted(() => {
  validateUsername()
  validatePhone()
  validateEmail()
  validatePassword()
  validateConfirmPassword()
})
</script>

<!-- 引入Font Awesome -->
<style>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Arial', 'Microsoft YaHei', sans-serif;
}

body {
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  color: #333;
}
</style>

<style scoped>
/* 顶部导航栏 */
.header {
  background: white;
  padding: 15px 40px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-logo {
  font-size: 24px;
  font-weight: bold;
  color: #d81b60;
  text-decoration: none;
}

.header-links {
  display: flex;
  gap: 20px;
  align-items: center;
}

.header-links a {
  text-decoration: none;
  color: #666;
  font-size: 14px;
  transition: color 0.3s;
}

.header-links a:hover {
  color: #d81b60;
}

.login-status {
  margin-left: 20px;
  padding-left: 20px;
  border-left: 1px solid #eee;
}

.not-logged-in {
  display: flex;
  align-items: center;
  gap: 10px;
}

.login-link-nav {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.login-link-nav:hover {
  color: #d81b60;
}

.divider {
  color: #ddd;
}

.register-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.register-wrapper {
  width: 100%;
  max-width: 1200px;
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  display: flex;
  min-height: 600px;
}

.register-left {
  flex: 1.2;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.register-left::before {
  content: "";
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  transform: rotate(30deg);
}

.logo {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 15px;
  position: relative;
  z-index: 2;
}

.platform-name {
  font-size: 20px;
  opacity: 0.9;
  margin-bottom: 10px;
  position: relative;
  z-index: 2;
}

.welcome-text {
  font-size: 32px;
  margin-bottom: 10px;
  position: relative;
  z-index: 2;
}

.slogan {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
  position: relative;
  z-index: 2;
}

.features {
  margin: 30px 0;
  position: relative;
  z-index: 2;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 12px 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  transition: all 0.3s;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
}

.feature-icon {
  width: 40px;
  height: 40px;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 16px;
}

.feature-text {
  flex: 1;
}

.feature-title {
  font-weight: bold;
  margin-bottom: 4px;
}

.feature-desc {
  font-size: 13px;
  opacity: 0.9;
}

.experience-btn {
  background: white;
  color: #d81b60;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 20px;
  position: relative;
  z-index: 2;
}

.experience-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.register-right {
  flex: 1;
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.register-title {
  font-size: 28px;
  font-weight: bold;
  color: #d81b60;
  margin-bottom: 30px;
  text-align: center;
}

.register-form {
  width: 100%;
}

.form-group {
  margin-bottom: 20px;
  position: relative;
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
}

.form-control:focus {
  border-color: #d81b60;
  box-shadow: 0 0 0 3px rgba(216, 27, 96, 0.1);
  outline: none;
}

/* 密码输入框容器 */
.password-wrapper {
  position: relative;
}

.code-group {
  display: flex;
  gap: 10px;
}

.code-input {
  flex: 1;
}

.send-code-btn {
  background-color: #f9d0da;
  color: #d81b60;
  border: none;
  border-radius: 8px;
  padding: 0 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  font-weight: 500;
}

.send-code-btn:hover {
  background-color: #f5b8c9;
}

.send-code-btn:disabled {
  background-color: #f0f0f0;
  color: #999;
  cursor: not-allowed;
}

.agreement-group {
  display: flex;
  align-items: flex-start;
  margin: 20px 0;
  font-size: 14px;
}

.agreement-group input {
  margin-right: 10px;
  margin-top: 3px;
}

.agreement-group a {
  color: #d81b60;
  text-decoration: none;
}

.agreement-group a:hover {
  text-decoration: underline;
}

.register-btn {
  width: 100%;
  padding: 14px;
  background-color: #d81b60;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 600;
  margin-bottom: 20px;
}

.register-btn:hover {
  background-color: #c2185b;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(216, 27, 96, 0.3);
}

.register-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  font-size: 14px;
}

.login-link a {
  color: #d81b60;
  text-decoration: none;
  font-weight: bold;
}

.login-link a:hover {
  text-decoration: underline;
}

.social-register {
  margin-top: 30px;
  padding-top: 25px;
  border-top: 1px solid #eee;
}

.social-register h3 {
  text-align: center;
  margin-bottom: 20px;
  font-size: 16px;
  color: #777;
  position: relative;
}

.social-register h3:before {
  content: "";
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background-color: #eee;
  z-index: 1;
}

.social-register h3 span {
  background-color: white;
  padding: 0 15px;
  position: relative;
  z-index: 2;
}

.social-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.social-btn {
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

.social-btn:hover {
  background-color: #f9f9f9;
  transform: translateY(-2px);
}

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

.social-name {
  font-size: 12px;
  color: #555;
}

.wecom {
  background-color: #1aad19;
}

.wechat {
  background-color: #07c160;
}

.qq {
  background-color: #12b7f5;
}

.email {
  background-color: #ff6d02;
}

.timer {
  font-size: 12px;
  color: #d81b60;
  margin-top: 5px;
  text-align: center;
}

.success-message {
  text-align: center;
  padding: 30px;
  background-color: #f9f9f9;
  border-radius: 12px;
  margin-top: 20px;
}

.success-message h2 {
  color: #4caf50;
  margin-bottom: 15px;
}

.success-message p {
  margin-bottom: 20px;
  color: #555;
}

.redirect-countdown {
  font-weight: bold;
  color: #d81b60;
}

.help-bubble {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.3);
  z-index: 1000;
  transition: all 0.3s;
}

.help-bubble:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(216, 27, 96, 0.4);
}

.help-bubble i {
  font-size: 24px;
}

.footer {
  background-color: #f8f9fa;
  padding: 30px 0;
  text-align: center;
  color: #666;
  font-size: 13px;
}

.footer-links {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  margin: 15px 0;
}

.footer-links a {
  color: #666;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #d81b60;
}

.password-strength {
  margin-top: 5px;
  height: 4px;
  background-color: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
}

.strength-bar {
  height: 100%;
  width: 0%;
  transition: all 0.3s;
}

.strength-weak {
  background-color: #ff4757;
  width: 33%;
}

.strength-medium {
  background-color: #ffa502;
  width: 66%;
}

.strength-strong {
  background-color: #2ed573;
  width: 100%;
}

/* 密码验证提示样式 */
.validation-message {
  font-size: 12px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.validation-success {
  color: #2ed573;
}

.validation-error {
  color: #ff4757;
}

.validation-info {
  color: #666;
}

.validation-icon {
  margin-right: 5px;
  font-size: 14px;
}

.password-rules {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 10px 15px;
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}

.rule-item {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.rule-item:last-child {
  margin-bottom: 0;
}

.rule-icon {
  margin-right: 8px;
  font-size: 14px;
  width: 16px;
  text-align: center;
}

.rule-valid {
  color: #2ed573;
}

.rule-invalid {
  color: #ff4757;
}

.rule-pending {
  color: #999;
}

.match-status {
  font-size: 12px;
  margin-top: 4px;
  display: flex;
  align-items: center;
}

.match-success {
  color: #2ed573;
}

.match-error {
  color: #ff4757;
}

.match-icon {
  margin-right: 5px;
  font-size: 14px;
}

/* 响应式样式 */
@media (max-width: 968px) {
  .register-wrapper {
    flex-direction: column;
    max-width: 500px;
  }

  .register-left {
    padding: 30px 20px;
  }

  .register-right {
    padding: 30px 20px;
  }

  .social-buttons {
    gap: 10px;
  }

  .social-btn {
    width: 60px;
  }

  .header {
    padding: 15px 20px;
    flex-direction: column;
    gap: 10px;
  }

  .header-links {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>