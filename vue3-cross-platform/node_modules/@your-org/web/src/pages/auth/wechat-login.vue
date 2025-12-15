<template>
  <div class="wechat-login-page">
    <LoginNavBar />

    <div class="page-container">
      <div class="login-card">
        <div class="login-header">
          <h1 class="login-title">
            <i class="fab fa-weixin"></i> 微信登录
          </h1>
          <p class="login-subtitle">使用微信账号快速登录</p>
        </div>

        <div class="login-content">
          <div class="qr-section">
            <div class="qr-code">
              <i class="fas fa-qrcode"></i>
              <p>请使用微信扫描二维码</p>
            </div>
            <p class="qr-tip">打开微信，扫描上方二维码即可登录</p>
          </div>

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
              <button class="btn-social" @click="goToQQLogin">
                <i class="fab fa-qq"></i>
              </button>
            </div>
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
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LoginNavBar from '@/components/common/LoginNavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const router = useRouter()

const goToAccountLogin = () => {
  router.push('/login')
}

const goToWecomLogin = () => {
  router.push('/wecom-login')
}

const goToQQLogin = () => {
  router.push('/qq-login')
}

onMounted(() => {
  // TODO: 生成微信登录二维码
  // generateWechatQRCode()
})
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.wechat-login-page {
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
  color: #07c160;
}

.login-subtitle {
  font-size: 14px;
  color: #666;
}

.qr-section {
  text-align: center;
  margin-bottom: 30px;
}

.qr-code {
  width: 200px;
  height: 200px;
  margin: 0 auto 20px;
  border: 2px dashed #ddd;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: #fafafa;
}

.qr-code i {
  font-size: 64px;
  color: #07c160;
}

.qr-code p {
  font-size: 14px;
  color: #666;
}

.qr-tip {
  font-size: 12px;
  color: #999;
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
