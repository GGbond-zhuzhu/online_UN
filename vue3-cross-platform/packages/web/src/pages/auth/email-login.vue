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
// 引入 Vue 的组合式 API，用于管理响应式数据和生命周期
import { ref, reactive, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import LoginNavBar from '@/components/common/LoginNavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

// 引入公共认证钩子，统一处理邮箱验证码发送和登录逻辑
import { useAuth } from '@campus/common'

const router = useRouter() // 获取路由实例，用于登录成功后的页面跳转

// 使用公共认证钩子，获取统一的邮箱验证码登录能力和状态
const {
  loading, // 当前是否有认证相关操作在进行（发送验证码 / 登录）
  errorMessage, // 最近一次认证操作的错误信息（可选，用于展示）
  sendLoginEmailCode, // 发送登录邮箱验证码的方法（内部会自动处理倒计时和错误）
  loginByEmailCode, // 使用邮箱验证码登录的方法（内部会自动保存 token 和用户信息）
  emailCountdown // 邮箱验证码倒计时秒数（>0 时应禁用发送按钮）
} = useAuth() // 调用认证钩子，复用公共逻辑

const countdown = emailCountdown // 将钩子中的倒计时引用给本地变量，方便模板直接使用

// 表单数据：记录用户输入的邮箱地址和验证码
const emailForm = reactive({
  email: '', // 用户输入的邮箱地址
  code: '' // 用户输入的验证码
})

// 发送邮箱验证码
const sendCode = async () => {
  // 基本校验：必须先输入邮箱
  if (!emailForm.email) {
    alert('请先输入邮箱地址') // 提示用户先填写邮箱
    return // 终止发送流程
  }

  // 校验邮箱格式是否正确
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/ // 邮箱格式验证的正则表达式
  if (!emailRegex.test(emailForm.email)) {
    alert('请输入正确的邮箱地址') // 格式不正确时给出提示
    return // 终止发送流程
  }

  try {
    // 调用公共认证钩子中的发送验证码方法（内部会自动记录 codeId 和开启倒计时）
    const result = await sendLoginEmailCode(emailForm.email, 60) // 设置倒计时 60 秒

    // 如果在开发/测试环境下后端返回了验证码内容，可以直接提示出来，方便调试
    if (result && result.code) {
      alert(`验证码：${result.code}（测试环境专用，请勿泄露）`) // 测试环境便捷提示
    } else {
      alert('验证码已发送到您的邮箱，请注意查收') // 正常环境提示用户查收邮件
    }
  } catch (error: any) {
    console.error('发送验证码失败:', error) // 控制台输出错误信息
    const msg = error?.message || '发送验证码失败，请稍后重试' // 生成友好的错误提示
    alert(msg) // 使用弹窗提示用户
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
  
  try {
    // 使用公共认证钩子提供的邮箱验证码登录方法（内部会保存 token 和用户信息）
    await loginByEmailCode(
      emailForm.email, // 用户输入的邮箱地址
      emailForm.code // 用户输入的验证码（codeId 由钩子内部管理）
    )

    alert('登录成功') // 登录成功后给出提示
    router.push('/') // 跳转到首页
  } catch (error: any) {
    console.error('登录失败:', error) // 控制台输出错误信息
    const msg = error?.message || '登录失败，请检查验证码是否正确' // 生成友好的错误提示
    alert(msg) // 弹窗提示用户
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
  // 目前验证码倒计时由认证钩子内部管理，这里无需手动清理定时器，保留函数以便后续扩展
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
