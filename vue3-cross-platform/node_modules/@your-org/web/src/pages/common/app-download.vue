<template>
  <div class="app-download-page">
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
          <!-- 未登录时显示 -->
          <div class="not-logged-in" v-if="!isLoggedIn">
            <router-link to="/login" class="login-link">登录</router-link>
          </div>
          <!-- 登录后显示 -->
          <div class="logged-in" v-else>
            <div class="user-menu">
              <span class="user-name">{{ userName }}</span>
              <div class="user-dropdown">
                <router-link to="/profile">个人中心</router-link>
                <router-link to="/profile">账户设置</router-link>
                <a href="#" @click.prevent="handleLogout" id="logoutBtn">退出登录</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 下载英雄区域 -->
      <div class="download-hero">
        <div class="hero-content">
          <h1>上大学Online<br>校园综合服务平台</h1>
          <p>全方位校园生活助手，随时随地畅享校园服务</p>

          <div class="download-buttons">
            <a href="#" class="download-btn ios" @click.prevent="showDownloadTip('ios')">
              <div class="btn-icon">
                <i class="fa-brands fa-apple"></i>
              </div>
              <div class="btn-text">
                <span>下载于</span>
                <strong>App Store</strong>
              </div>
            </a>
            <a href="#" class="download-btn android" @click.prevent="showDownloadTip('android')">
              <div class="btn-icon">
                <i class="fa-brands fa-android"></i>
              </div>
              <div class="btn-text">
                <span>安卓版下载</span>
                <strong>Android</strong>
              </div>
            </a>
          </div>
        </div>

        <div class="hero-visual">
          <div class="phone-mockup">
            <div class="phone-screen">
              <div class="app-icon">
                <i class="fa-solid fa-graduation-cap"></i>
              </div>
              <h3>上大学Online</h3>
              <p>校园综合服务平台</p>
            </div>
          </div>
          <!-- 二维码区域（登录界面同款） -->
          <div class="qr-code">
            <img 
              src="https://api.qrserver.com/v1/create-qr-code/?size=130x130&data=https://www.shangdaxueonline.com/download" 
              alt="APP下载二维码" 
              class="qr-img"
            >
            <div class="qr-tip">扫码下载APP</div>
          </div>
        </div>
      </div>

      <!-- 功能特色区域 -->
      <div class="features-section">
        <div class="section-title">
          <h2>全方位校园服务</h2>
          <p>上大学Online App为您提供完整的校园生活解决方案</p>
        </div>

        <div class="features-grid">
          <!-- 改用 v-for 收集 ref，解决数组收集失败 -->
          <div 
            v-for="(item, index) in featureList" 
            :key="index"
            class="feature-card"
            :ref="setElementRef"
          >
            <div class="feature-icon">
              <i :class="item.icon"></i>
            </div>
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </div>

      <!-- 合并模块：侧边栏+轮播图 -->
      <div class="combined-section">
        <!-- 侧边栏：手机平台支持 -->
        <div class="sidebar">
          <h2 class="sidebar-title">手机平台支持</h2>
          <div class="platform-list">
            <div 
              v-for="(platform, index) in platformList" 
              :key="index"
              class="platform-item"
              :ref="setElementRef"
            >
              <div class="platform-icon" :class="platform.type">
                <i :class="platform.icon"></i>
              </div>
              <div class="platform-info">
                <h3>{{ platform.title }}</h3>
                <p>{{ platform.desc }}</p>
              </div>
              <a href="#" class="platform-download" @click.prevent="showDownloadTip(platform.type)">下载</a>
            </div>
          </div>
        </div>

        <!-- 轮播图：校园生活展示 -->
        <div class="carousel-container" ref="carouselRef">
          <div class="carousel-slides" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
            <div class="carousel-slide" v-for="(slide, index) in carouselList" :key="index">
              <img :src="slide.img" :alt="slide.alt" class="carousel-image">
            </div>
          </div>

          <button class="carousel-arrow prev" @click="prevSlide">
            <i class="fa-solid fa-chevron-left"></i>
          </button>
          <button class="carousel-arrow next" @click="nextSlide">
            <i class="fa-solid fa-chevron-right"></i>
          </button>

          <div class="carousel-dots">
            <div 
              class="carousel-dot" 
              :class="{ active: currentSlide === index }" 
              v-for="(dot, index) in carouselList" 
              :key="index"
              @click="goToSlide(index)"
            ></div>
          </div>
        </div>
      </div>

      <!-- 步骤引导区域 -->
      <div class="steps-section">
        <div class="section-title">
          <h2>快速开始使用</h2>
          <p>简单三步，立即体验上大学Online</p>
        </div>

        <div class="steps-container">
          <div 
            v-for="(step, index) in stepList" 
            :key="index"
            class="step"
            :ref="setElementRef"
          >
            <div class="step-number">{{ index + 1 }}</div>
            <h3>{{ step.title }}</h3>
            <p>{{ step.desc }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 多功能悬浮球 -->
    <div class="floating-ball" :class="{ active: isBallMenuOpen }" @click="toggleBallMenu">
      <div class="ball-main">
        <i class="fa-solid fa-plus"></i>
      </div>
      <div class="ball-menu" v-show="isBallMenuOpen">
        <router-link to="/" class="menu-item" title="首页" @click="closeBallMenu">
          <i class="fa-solid fa-home"></i>
        </router-link>
        <router-link to="/profile" class="menu-item" title="个人中心" @click="closeBallMenu">
          <i class="fa-solid fa-user"></i>
        </router-link>
        <router-link to="/feedback" class="menu-item" title="问题反馈" @click="closeBallMenu">
          <i class="fa-solid fa-comment"></i>
        </router-link>
        <a href="#" class="menu-item" title="返回顶部" @click.prevent="backToTop">
          <i class="fa-solid fa-arrow-up"></i>
        </a>
        <router-link to="/help" class="menu-item" title="帮助中心" @click="closeBallMenu">
          <i class="fa-solid fa-question"></i>
        </router-link>
      </div>
    </div>

    <!-- 页脚 -->
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// ========== 核心数据定义（解决硬编码导致的渲染问题） ==========
// 功能列表
const featureList = ref([
  { icon: 'fa-solid fa-id-card', title: '校园E卡通', desc: '一码通校园，便捷支付与身份认证，食堂、图书馆、门禁一码搞定' },
  { icon: 'fa-solid fa-right-left', title: '二手交易', desc: '同校精准匹配，闲置物品变废为宝，安全可靠的校园交易平台' },
  { icon: 'fa-solid fa-briefcase', title: '兼职服务', desc: '海量岗位，保障薪资安全可靠，校园周边兼职机会一手掌握' },
  { icon: 'fa-solid fa-calendar-alt', title: '课程管理', desc: '智能排课，学习生活有条不紊，考试提醒、作业提交一站式管理' },
  { icon: 'fa-solid fa-users', title: '校园社交', desc: '同校交友，活动参与，丰富你的大学社交圈，认识更多志同道合的朋友' },
  { icon: 'fa-solid fa-newspaper', title: '校园资讯', desc: '最新校园动态，活动通知，学术讲座，不错过任何重要信息' }
])

// 平台列表
const platformList = ref([
  { type: 'ios', icon: 'fa-brands fa-apple', title: 'iOS版', desc: '适用于iPhone、iPad等iOS设备' },
  { type: 'android', icon: 'fa-brands fa-android', title: '安卓版', desc: '适用于Android 5.0及以上系统' }
])

// 轮播图列表
const carouselList = ref([
  { img: 'https://via.placeholder.com/800x400?text=校园E卡通+便捷支付', alt: '校园E卡通' },
  { img: 'https://via.placeholder.com/800x400?text=二手交易+闲置变现', alt: '二手交易' },
  { img: 'https://via.placeholder.com/800x400?text=兼职服务+安全可靠', alt: '兼职服务' },
  { img: 'https://via.placeholder.com/800x400?text=课程管理+智能提醒', alt: '课程管理' }
])

// 步骤列表
const stepList = ref([
  { title: '下载安装', desc: '点击上方按钮或扫描二维码下载App' },
  { title: '注册账号', desc: '使用学号或手机号快速注册账号' },
  { title: '开始使用', desc: '登录后即可享受全方位的校园服务' }
])

// ========== 登录状态管理 ==========
// 安全地访问localStorage（避免SSR错误）
const getStorageItem = (key: string, defaultValue: string = '') => {
  if (typeof window === 'undefined') return defaultValue
  try {
    return localStorage.getItem(key) || defaultValue
  } catch {
    return defaultValue
  }
}

const isLoggedIn = ref<boolean>(!!getStorageItem('isLoggedIn'))
const userName = ref<string>(getStorageItem('userName', '用户'))

// 监听登录状态变化
watch([isLoggedIn, userName], () => {
  if (typeof window !== 'undefined') {
    try {
      localStorage.setItem('isLoggedIn', isLoggedIn.value ? 'true' : 'false')
      localStorage.setItem('userName', userName.value)
    } catch {
      // localStorage不可用时忽略
    }
  }
}, { immediate: true })

// 登出功能
const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    isLoggedIn.value = false
    userName.value = '用户'
  }
}

// ========== 下载提示 ==========
const showDownloadTip = (platform?: string) => {
  try {
    if (platform === 'ios') {
      // iOS App Store链接（实际使用时替换为真实链接）
      const iosUrl = 'https://apps.apple.com/app/id123456789'
      window.open(iosUrl, '_blank')
    } else if (platform === 'android') {
      // Google Play链接（实际使用时替换为真实链接）
      const androidUrl = 'https://play.google.com/store/apps/details?id=com.campus.app'
      window.open(androidUrl, '_blank')
    } else {
      // 默认提示
      alert('请选择对应的下载平台')
    }
  } catch (error) {
    console.error('下载失败:', error)
    alert('下载失败，请稍后重试')
  }
}

// ========== 轮播图管理 ==========
const currentSlide = ref<number>(0)
const carouselRef = ref<HTMLElement | null>(null)
let slideInterval: NodeJS.Timeout | null = null

// 自动轮播（增加DOM校验）
const startAutoSlide = () => {
  if (!carouselRef.value) return
  if (slideInterval) clearInterval(slideInterval)
  slideInterval = setInterval(() => {
    nextSlide()
  }, 5000)
}

// 下一张
const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % carouselList.value.length
}

// 上一张
const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + carouselList.value.length) % carouselList.value.length
  startAutoSlide() // 重置定时器
}

// 跳转到指定幻灯片
const goToSlide = (index: number) => {
  currentSlide.value = index
  startAutoSlide() // 重置定时器
}

// ========== 悬浮球管理 ==========
const isBallMenuOpen = ref<boolean>(false)

// 切换悬浮球菜单（阻止冒泡）
const toggleBallMenu = (e: MouseEvent) => {
  e.stopPropagation()
  isBallMenuOpen.value = !isBallMenuOpen.value
}

// 关闭悬浮球菜单
const closeBallMenu = () => {
  isBallMenuOpen.value = false
}

// 返回顶部
const backToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
  closeBallMenu()
}

// ========== 滚动动画（修复ref数组收集） ==========
const animatedElements = ref<HTMLElement[]>([])
let observer: IntersectionObserver | null = null

// ref回调辅助函数
const setElementRef = (el: any) => {
  if (el && el instanceof HTMLElement) {
    animatedElements.value.push(el)
  }
}

// 初始化滚动动画
const initScrollAnimation = () => {
  if (animatedElements.value.length === 0) return
  
  const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
  }

  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting && entry.target instanceof HTMLElement) {
        entry.target.style.opacity = '1'
        entry.target.style.transform = 'translateY(0)'
      }
    })
  }, observerOptions)

  // 初始化元素样式并观察
  animatedElements.value.forEach(el => {
    if (el instanceof HTMLElement) {
      el.style.opacity = '0'
      el.style.transform = 'translateY(20px)'
      el.style.transition = 'opacity 0.5s ease, transform 0.5s ease'
      observer!.observe(el)
    }
  })
}

// ========== 生命周期钩子（修复时机问题） ==========
onMounted(async () => {
  // 等待DOM完全渲染
  await nextTick()
  
  // 初始化轮播图
  startAutoSlide()
  
  // 初始化滚动动画
  initScrollAnimation()
  
  // 点击外部关闭悬浮球菜单
  document.addEventListener('click', () => {
    isBallMenuOpen.value = false
  })
})

onUnmounted(() => {
  // 清理定时器
  if (slideInterval) clearInterval(slideInterval)
  
  // 清理观察者
  if (observer) observer.disconnect()
  
  // 移除事件监听
  document.removeEventListener('click', () => {
    isBallMenuOpen.value = false
  })
})
</script>

<!-- 引入Font Awesome（双CDN保障加载） -->
<style>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');
@import url('https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css');

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
  overflow-x: hidden; /* 解决横向滚动 */
}

/* 解决图标样式穿透 */
:root {
  --primary-pink: #e91e63;
  --secondary-pink: #ec407a;
  --light-pink: #f8bbd9;
  --light-pink-2: #fce4ec;
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
  width: 100%;
  position: relative;
  z-index: 999;
}

.header-logo {
  font-size: 24px;
  font-weight: bold;
  color: var(--primary-pink);
  text-decoration: none;
}

.header-links {
  display: flex;
  gap: 20px;
  align-items: center;
  flex-wrap: wrap; /* 修复导航栏溢出 */
}

.header-links a {
  text-decoration: none;
  color: #666;
  font-size: 14px;
  transition: color 0.3s;
}

.header-links a:hover {
  color: var(--primary-pink);
}

/* 登录状态样式 */
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

.login-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.login-link:hover {
  color: var(--primary-pink);
}

/* 登录后用户菜单 */
.logged-in {
  position: relative;
}

.user-menu {
  position: relative;
  cursor: pointer;
}

.user-name {
  color: #666;
  font-size: 14px;
  padding: 5px 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-name:hover {
  background-color: var(--light-pink-2);
  color: var(--primary-pink);
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  min-width: 120px;
  display: none;
  z-index: 1000;
}

.user-menu:hover .user-dropdown {
  display: block;
}

.user-dropdown a {
  display: block;
  padding: 10px 15px;
  color: #666;
  text-decoration: none;
  font-size: 13px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.user-dropdown a:hover {
  background-color: var(--light-pink-2);
  color: var(--primary-pink);
}

.user-dropdown a:last-child {
  border-bottom: none;
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%; /* 修复宽度不足 */
}

.download-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 60px;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%; /* 修复宽度 */
  flex-wrap: wrap; /* 移动端换行 */
}

.hero-content {
  flex: 1 1 500px; /* 弹性宽度 */
  padding-right: 40px;
  margin-bottom: 20px; /* 移动端间距 */
}

.hero-content h1 {
  font-size: 42px;
  color: var(--primary-pink);
  margin-bottom: 20px;
  line-height: 1.2;
}

.hero-content p {
  font-size: 20px;
  color: #666;
  margin-bottom: 30px;
  line-height: 1.5;
}

.download-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.download-btn {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background: #333;
  border-radius: 10px;
  text-decoration: none;
  color: white;
  transition: all 0.3s;
  min-width: 180px;
}

.download-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.download-btn.ios {
  background: #000;
  border: 2px solid #000;
}

.download-btn.android {
  background: #3DDC84;
  border: 2px solid #3DDC84;
}

.btn-icon {
  font-size: 30px;
  margin-right: 15px;
}

.btn-text {
  text-align: left;
}

.btn-text span {
  display: block;
  font-size: 12px;
}

.btn-text strong {
  font-size: 16px;
}

.hero-visual {
  flex: 0 0 400px;
  position: relative;
  margin: 0 auto; /* 居中 */
}

.phone-mockup {
  position: relative;
  width: 300px;
  height: 600px;
  background: white;
  border-radius: 40px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  padding: 20px;
  margin: 0 auto;
}

.phone-screen {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--secondary-pink) 0%, var(--primary-pink) 100%);
  border-radius: 25px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  padding: 30px;
  text-align: center;
}

.app-icon {
  width: 80px;
  height: 80px;
  background: white;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  font-size: 40px;
  color: var(--primary-pink);
}

.phone-screen h3 {
  font-size: 24px;
  margin-bottom: 10px;
}

.phone-screen p {
  font-size: 14px;
  opacity: 0.9;
}

/* 二维码样式（确保100%呈现） */
.qr-code {
  position: absolute;
  bottom: 20px;
  right: 0;
  width: 150px;
  height: 150px;
  background: white;
  border-radius: 10px;
  padding: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.qr-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 5px;
  margin-bottom: 5px;
}

.qr-tip {
  position: absolute;
  bottom: 5px;
  font-size: 12px;
  color: #666;
  text-align: center;
  width: 100%;
  padding: 0 5px;
}

.features-section {
  margin-bottom: 60px;
  width: 100%;
}

.section-title {
  text-align: center;
  margin-bottom: 40px;
}

.section-title h2 {
  font-size: 32px;
  color: var(--primary-pink);
  margin-bottom: 10px;
}

.section-title p {
  font-size: 18px;
  color: #666;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); /* 自适应列数 */
  gap: 30px;
  width: 100%;
}

.feature-card {
  background: white;
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  text-align: center;
  transition: transform 0.3s;
  width: 100%;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  width: 60px;
  height: 60px;
  background: var(--light-pink-2);
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 24px;
  color: var(--primary-pink);
}

.feature-card h3 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #333;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

/* 合并模块：侧边栏+轮播图 */
.combined-section {
  display: flex;
  gap: 30px;
  margin-bottom: 60px;
  width: 100%;
  flex-wrap: wrap; /* 移动端换行 */
}

.sidebar {
  flex: 0 0 300px;
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  flex: 1 1 300px; /* 弹性宽度 */
}

.sidebar-title {
  font-size: 24px;
  color: var(--primary-pink);
  margin-bottom: 20px;
  text-align: center;
}

.platform-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.platform-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 10px;
  transition: all 0.3s;
  width: 100%;
}

.platform-item:hover {
  background: #f0f0f0;
  transform: translateX(5px);
}

.platform-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 20px;
  color: white;
}

.platform-icon.ios {
  background: #000;
}

.platform-icon.android {
  background: #3DDC84;
}

.platform-info {
  flex: 1;
}

.platform-info h3 {
  font-size: 16px;
  margin-bottom: 5px;
  color: #333;
}

.platform-info p {
  font-size: 12px;
  color: #666;
}

.platform-download {
  display: inline-block;
  padding: 5px 10px;
  background: var(--primary-pink);
  color: white;
  border-radius: 5px;
  text-decoration: none;
  font-size: 12px;
  transition: background 0.3s;
}

.platform-download:hover {
  background: #d81b60;
}

.carousel-container {
  flex: 1 1 600px; /* 弹性宽度 */
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  width: 100%;
}

.carousel-slides {
  display: flex;
  transition: transform 0.5s ease-in-out;
  width: 100%;
}

.carousel-slide {
  min-width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.carousel-image {
  width: 100%;
  max-width: 800px;
  height: 400px;
  object-fit: cover;
  border-radius: 10px;
}

.carousel-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.8);
  border: none;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 20px;
  color: var(--primary-pink);
  transition: all 0.3s;
  z-index: 10;
}

.carousel-arrow:hover {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.carousel-arrow.prev {
  left: 20px;
}

.carousel-arrow.next {
  right: 20px;
}

.carousel-dots {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  gap: 10px;
}

.carousel-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #ddd;
  cursor: pointer;
  transition: background 0.3s;
}

.carousel-dot.active {
  background: var(--primary-pink);
}

/* 步骤引导区域 */
.steps-section {
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  margin-bottom: 60px;
  width: 100%;
}

.steps-container {
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  gap: 30px;
  width: 100%;
  flex-wrap: wrap; /* 移动端换行 */
}

.step {
  text-align: center;
  flex: 1 1 250px; /* 弹性宽度 */
  max-width: 300px;
}

.step-number {
  width: 60px;
  height: 60px;
  background: var(--primary-pink);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 24px;
  font-weight: bold;
}

.step h3 {
  font-size: 20px;
  margin-bottom: 15px;
  color: #333;
}

.step p {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

/* 悬浮球样式 */
.floating-ball {
  position: fixed;
  right: 30px;
  bottom: 30px;
  z-index: 1000;
}

.ball-main {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, var(--primary-pink) 0%, var(--secondary-pink) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(233, 30, 99, 0.3);
  transition: all 0.3s ease;
  font-size: 20px;
}

.ball-main:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 25px rgba(233, 30, 99, 0.4);
}

.ball-menu {
  position: absolute;
  bottom: 70px;
  right: 0;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.menu-item {
  width: 45px;
  height: 45px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-pink);
  text-decoration: none;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.menu-item:hover {
  transform: scale(1.1);
  background: var(--light-pink-2);
}

.floating-ball.active .ball-main {
  transform: rotate(45deg);
}

/* 菜单项动画延迟 */
.menu-item:nth-child(1) {
  transition: all 0.3s ease 0.1s;
}
.menu-item:nth-child(2) {
  transition: all 0.3s ease 0.2s;
}
.menu-item:nth-child(3) {
  transition: all 0.3s ease 0.3s;
}
.menu-item:nth-child(4) {
  transition: all 0.3s ease 0.4s;
}
.menu-item:nth-child(5) {
  transition: all 0.3s ease 0.5s;
}

.footer {
  background-color: #f8f9fa;
  padding: 30px 0;
  text-align: center;
  color: #666;
  font-size: 13px;
  width: 100%;
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
  color: #ec407a;
}

/* 响应式设计（彻底修复移动端适配） */
@media (max-width: 968px) {
  .download-hero {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }

  .hero-content {
    padding-right: 0;
    margin-bottom: 40px;
  }

  .hero-content h1 {
    font-size: 36px;
  }

  .hero-content p {
    font-size: 18px;
  }

  .download-buttons {
    justify-content: center;
  }

  .hero-visual {
    width: 100%;
  }

  /* 二维码适配移动端 */
  .qr-code {
    position: relative;
    margin: 20px auto 0;
    width: 180px;
    height: 180px;
    right: unset;
    bottom: unset;
  }

  .combined-section {
    flex-direction: column;
  }

  .sidebar {
    margin-bottom: 20px;
  }

  .steps-container {
    flex-direction: column;
    align-items: center;
  }

  .header {
    padding: 15px 20px;
    flex-direction: column;
    gap: 10px;
  }

  .header-links {
    justify-content: center;
  }

  .login-status {
    margin-left: 0;
    padding-left: 0;
    border-left: none;
    margin-top: 10px;
  }
}

@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 32px;
  }

  .download-btn {
    min-width: 160px;
    padding: 12px 15px;
  }

  .phone-mockup {
    width: 250px;
    height: 500px;
  }

  .carousel-image {
    height: 250px;
  }

  .carousel-arrow {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }

  /* 移动端二维码优化 */
  .qr-code {
    width: 150px;
    height: 150px;
  }

  .steps-section {
    padding: 20px;
  }

  .step {
    margin-bottom: 20px;
  }
}

@media (max-width: 480px) {
  .header-links {
    gap: 10px;
  }

  .header-links a {
    font-size: 13px;
  }

  .download-btn {
    min-width: 100%;
    justify-content: center;
  }

  .main-content {
    padding: 20px 10px;
  }

  .carousel-container {
    padding: 15px;
  }

  .carousel-image {
    height: 200px;
  }
}
</style>