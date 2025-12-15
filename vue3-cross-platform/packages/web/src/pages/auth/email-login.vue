<template>
  <div class="email-login-page">
    <LoginNavBar />

    <div class="page-container">
      <div class="login-card">
        <div class="login-header">
          <h1 class="login-title">
            <i class="fas fa-envelope"></i> 邮箱登录
          </h1>
          <p class="login-subtitle">使用邮箱账号快速登录</p>
        </div>

        <form @submit.prevent="handleEmailLogin" class="login-form">
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-envelope"></i> 邮箱地址
            </label>
            <input
              v-model="emailForm.email"
              type="email"
              class="form-input"
              placeholder="请输入邮箱地址"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-key"></i> 验证码
            </label>
            <div class="code-input-group">
              <input
                v-model="emailForm.code"
                type="text"
                class="form-input"
                placeholder="请输入验证码"
                maxlength="6"
                required
              />
              <button
                type="button"
                class="btn-send-code"
                :disabled="countdown > 0"
                @click="sendCode"
              >
                {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
              </button>
            </div>
          </div>

          <button type="submit" class="btn-login" :disabled="loading">
            <span v-if="!loading">登录</span>
            <span v-else><i class="fas fa-spinner fa-spin"></i> 登录中...</span>
          </button>
        </form>

        <div class="divider">
          <span>或</span>
        </div>

        <div class="alternative-login">
          <button class="btn-alternative" @click="goToAccountLogin">
            <i class="fas fa-user"></i> 使用账号密码登录
          </button>
          <div class="other-login">
            <span>其他登录方式：</span>
            <button class="btn-social" @click="goToWecomLogin">
              <i class="fab fa-microsoft"></i>
            </button>
            <button class="btn-social" @click="goToWechatLogin">
              <i class="fab fa-weixin"></i>
            </button>
            <button class="btn-social" @click="goToQQLogin">
              <i class="fab fa-qq"></i>
            </button>
          </div>
        </div>

        <div class="login-footer">
          <router-link to="/register">还没有账号？立即注册</router-link>
        </div>
      </div>
    </div>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import LoginNavBar from '@/components/common/LoginNavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

// 引入API和工具函数
import { sendEmailCode, emailLogin } from '@campus/common'
import { setToken, setUserInfo } from '@campus/common'

const router = useRouter()

const loading = ref(false)
const countdown = ref(0)
let codeTimer: number | null = null

const emailForm = reactive({
  email: '',
  code: '',
  codeId: '' // 保存验证码ID
})

const sendCode = async () => {
  if (!emailForm.email) {
    alert('请先输入邮箱地址')
    return
  }

  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(emailForm.email)) {
    alert('请输入正确的邮箱地址')
    return
  }

  try {
    // 调用发送验证码API
    const result = await sendEmailCode(emailForm.email)
    
    // 保存验证码ID用于后续登录
    emailForm.codeId = result.codeId
    
    // 如果返回了验证码（模拟模式），显示给用户
    if (result.code) {
      alert(`验证码：${result.code}（模拟模式，请直接使用）`)
    } else {
      alert('验证码已发送到您的邮箱，请查收')
    }
    
    // 开始倒计时（使用返回的过期时间或默认60秒）
    countdown.value = Math.floor(result.expireTime / 60) || 60
    if (codeTimer) clearInterval(codeTimer)
    codeTimer = window.setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        if (codeTimer) clearInterval(codeTimer)
        codeTimer = null
      }
    }, 1000)
  } catch (error: any) {
    console.error('发送验证码失败:', error)
    const errorMsg = error?.message || '发送验证码失败，请稍后重试'
    alert(errorMsg)
  }
}

const handleEmailLogin = async () => {
  // 表单验证
  if (!emailForm.email) {
    alert('请输入邮箱地址')
    return
  }
  
  if (!emailForm.code) {
    alert('请输入验证码')
    return
  }
  
  if (!emailForm.codeId) {
    alert('请先获取验证码')
    return
  }

  try {
    loading.value = true
    
    // 调用邮箱登录API
    const result = await emailLogin(
      emailForm.email,
      emailForm.code,
      emailForm.codeId
    )
    
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
    
    alert('登录成功')
    router.push('/')
  } catch (error: any) {
    console.error('登录失败:', error)
    const errorMsg = error?.message || '登录失败，请检查验证码是否正确'
    alert(errorMsg)
  } finally {
    loading.value = false
  }
}

const goToAccountLogin = () => {
  router.push('/login')
}

const goToWecomLogin = () => {
  router.push('/wecom-login')
}

const goToWechatLogin = () => {
  router.push('/wechat-login')
}

const goToQQLogin = () => {
  router.push('/qq-login')
}

// 清理定时器
onUnmounted(() => {
  if (codeTimer) {
    clearInterval(codeTimer)
    codeTimer = null
  }
})
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.email-login-page {
  min-height: 100vh;
  background: var(--bg);
  padding: 40px 20px;
}

.page-container {
  max-width: 500px;
  margin: 0 auto;
}

.login-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.login-title i {
  color: var(--primary);
}

.login-subtitle {
  font-size: 14px;
  color: #666;
}

.login-form {
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.form-label i {
  color: var(--primary);
}

.form-input {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(216, 27, 96, 0.1);
}

.code-input-group {
  display: flex;
  gap: 10px;
}

.code-input-group .form-input {
  flex: 1;
}

.btn-send-code {
  padding: 12px 20px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
  transition: all 0.3s;
}

.btn-send-code:hover:not(:disabled) {
  background: #f9f0ff;
  border-color: var(--primary);
}

.btn-send-code:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-login {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
}

.btn-login:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.divider {
  position: relative;
  text-align: center;
  margin: 30px 0;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #eee;
}

.divider span {
  position: relative;
  background: white;
  padding: 0 15px;
  color: #999;
  font-size: 12px;
}

.alternative-login {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.btn-alternative {
  width: 100%;
  padding: 12px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-alternative:hover {
  background: #f9f0ff;
  border-color: var(--primary);
}

.other-login {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 12px;
  color: #666;
}

.btn-social {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid #eee;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  font-size: 20px;
}

.btn-social:hover {
  border-color: var(--primary);
  transform: scale(1.1);
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.login-footer a {
  color: var(--primary);
  text-decoration: none;
  font-size: 14px;
}

.login-footer a:hover {
  text-decoration: underline;
}
</style>
