<template>
  <div class="settings-page">
    <NavBar />
    <div class="settings-container">
      <div class="page-header">
        <h1 class="page-title">设置</h1>
        <p class="page-subtitle">管理您的账户和偏好设置</p>
      </div>

      <div class="settings-sections">
        <!-- 账户设置 -->
        <div class="settings-section">
          <h2 class="section-title">账户设置</h2>
          <div class="settings-list">
            <div class="setting-item" @click="goToProfile">
              <div class="setting-icon">
                <i class="fas fa-user"></i>
              </div>
              <div class="setting-content">
                <h3 class="setting-title">个人资料</h3>
                <p class="setting-desc">修改头像、昵称等信息</p>
              </div>
              <i class="fas fa-chevron-right setting-arrow"></i>
            </div>
            <div class="setting-item" @click="goToSecurity">
              <div class="setting-icon">
                <i class="fas fa-shield-alt"></i>
              </div>
              <div class="setting-content">
                <h3 class="setting-title">安全设置</h3>
                <p class="setting-desc">密码、手机号、邮箱管理</p>
              </div>
              <i class="fas fa-chevron-right setting-arrow"></i>
            </div>
            <div class="setting-item" @click="goToAccount">
              <div class="setting-icon">
                <i class="fas fa-wallet"></i>
              </div>
              <div class="setting-content">
                <h3 class="setting-title">账户管理</h3>
                <p class="setting-desc">绑定支付方式、查看余额</p>
              </div>
              <i class="fas fa-chevron-right setting-arrow"></i>
            </div>
          </div>
        </div>

        <!-- 通知设置 -->
        <div class="settings-section">
          <h2 class="section-title">通知设置</h2>
          <div class="settings-list">
            <div class="setting-item">
              <div class="setting-icon">
                <i class="fas fa-bell"></i>
              </div>
              <div class="setting-content">
                <h3 class="setting-title">消息通知</h3>
                <p class="setting-desc">接收系统消息和提醒</p>
              </div>
              <label class="switch">
                <input type="checkbox" v-model="settings.messageNotify" />
                <span class="slider"></span>
              </label>
            </div>
            <div class="setting-item">
              <div class="setting-icon">
                <i class="fas fa-envelope"></i>
              </div>
              <div class="setting-content">
                <h3 class="setting-title">邮件通知</h3>
                <p class="setting-desc">接收邮件提醒</p>
              </div>
              <label class="switch">
                <input type="checkbox" v-model="settings.emailNotify" />
                <span class="slider"></span>
              </label>
            </div>
          </div>
        </div>

        <!-- 隐私设置 -->
        <div class="settings-section">
          <h2 class="section-title">隐私设置</h2>
          <div class="settings-list">
            <div class="setting-item" @click="goToPrivacy">
              <div class="setting-icon">
                <i class="fas fa-lock"></i>
              </div>
              <div class="setting-content">
                <h3 class="setting-title">隐私政策</h3>
                <p class="setting-desc">查看隐私保护政策</p>
              </div>
              <i class="fas fa-chevron-right setting-arrow"></i>
            </div>
            <div class="setting-item">
              <div class="setting-icon">
                <i class="fas fa-eye"></i>
              </div>
              <div class="setting-content">
                <h3 class="setting-title">公开个人信息</h3>
                <p class="setting-desc">允许他人查看我的资料</p>
              </div>
              <label class="switch">
                <input type="checkbox" v-model="settings.publicProfile" />
                <span class="slider"></span>
              </label>
            </div>
          </div>
        </div>

        <!-- 其他设置 -->
        <div class="settings-section">
          <h2 class="section-title">其他</h2>
          <div class="settings-list">
            <div class="setting-item" @click="goToHelp">
              <div class="setting-icon">
                <i class="fas fa-question-circle"></i>
              </div>
              <div class="setting-content">
                <h3 class="setting-title">帮助中心</h3>
                <p class="setting-desc">常见问题和使用指南</p>
              </div>
              <i class="fas fa-chevron-right setting-arrow"></i>
            </div>
            <div class="setting-item" @click="goToAbout">
              <div class="setting-icon">
                <i class="fas fa-info-circle"></i>
              </div>
              <div class="setting-content">
                <h3 class="setting-title">关于我们</h3>
                <p class="setting-desc">平台介绍和联系方式</p>
              </div>
              <i class="fas fa-chevron-right setting-arrow"></i>
            </div>
            <div class="setting-item" @click="goToFeedback">
              <div class="setting-icon">
                <i class="fas fa-comment-dots"></i>
              </div>
              <div class="setting-content">
                <h3 class="setting-title">意见反馈</h3>
                <p class="setting-desc">提交问题或建议</p>
              </div>
              <i class="fas fa-chevron-right setting-arrow"></i>
            </div>
          </div>
        </div>

        <!-- 退出登录 -->
        <div class="logout-section">
          <button class="logout-btn" @click="handleLogout">退出登录</button>
        </div>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@campus/common'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const router = useRouter()
const userStore = useUserStore()

// 设置项
const settings = ref({
  messageNotify: true,
  emailNotify: false,
  publicProfile: true
})

// 跳转到个人资料
const goToProfile = () => {
  router.push('/profile')
}

// 跳转到安全设置
const goToSecurity = () => {
  router.push('/security')
}

// 跳转到账户管理
const goToAccount = () => {
  router.push('/account')
}

// 跳转到隐私政策
const goToPrivacy = () => {
  router.push('/privacy')
}

// 跳转到帮助中心
const goToHelp = () => {
  router.push('/help')
}

// 跳转到关于我们
const goToAbout = () => {
  router.push('/about')
}

// 跳转到意见反馈
const goToFeedback = () => {
  router.push('/feedback')
}

// 退出登录
const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff5f7 0%, #f0f9ff 100%);
  display: flex;
  flex-direction: column;
}

.settings-container {
  flex: 1;
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
  width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
}

.settings-sections {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.settings-section {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 8px 30px rgba(148, 163, 184, 0.15);
  border: 1px solid rgba(216, 27, 96, 0.1);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.settings-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ffb6c1 0%, #d81b60 100%);
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.settings-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.setting-item {
  display: flex;
  align-items: center;
  padding: 18px 0;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, #d81b60 0%, #ffb6c1 100%);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
  border-radius: 0 2px 2px 0;
}

.setting-item:hover {
  background: linear-gradient(90deg, rgba(255, 182, 193, 0.05) 0%, transparent 100%);
  border-radius: 12px;
  padding-left: 12px;
  padding-right: 12px;
  transform: translateX(4px);
}

.setting-item:hover::before {
  transform: scaleX(1);
}

.setting-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffe4e6 0%, #fff0f5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #d81b60;
  margin-right: 18px;
  font-size: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(216, 27, 96, 0.1);
}

.setting-item:hover .setting-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.2);
}

.setting-content {
  flex: 1;
}

.setting-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.setting-desc {
  font-size: 14px;
  color: #999;
}

.setting-arrow {
  color: #ccc;
  font-size: 14px;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.3s;
  border-radius: 26px;
}

.slider:before {
  position: absolute;
  content: '';
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background: linear-gradient(135deg, #ffb6c1, #ffc0cb);
}

input:checked + .slider:before {
  transform: translateX(24px);
}

/* 退出登录 */
.logout-section {
  margin-top: 32px;
}

.logout-btn {
  width: 100%;
  padding: 18px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8787 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
  position: relative;
  overflow: hidden;
}

.logout-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.logout-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.5);
}

.logout-btn:hover::before {
  width: 300px;
  height: 300px;
}

.logout-btn:active {
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .settings-container {
    padding: 20px 12px;
  }

  .page-title {
    font-size: 24px;
  }
}
</style>

