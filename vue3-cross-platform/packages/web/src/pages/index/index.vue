<template>
  <div class="home-page">
    <!-- 全局导航栏 -->
    <NavBar />

    <div class="main-container">
      <!-- 轮播图区域 -->
      <Carousel :slides="carouselSlides" />

      <!-- 服务网格 -->
      <section class="services-grid">
        <div
          class="service-card e-card"
          :class="{ hidden: !canAccess('ecard') }"
          @click="goToModule('/ecard')"
        >
          <div class="service-icon">
            <i class="fas fa-id-card-alt"></i>
      </div>
          <h3 class="service-title">校园E卡通</h3>
          <p class="service-desc">
            动态学生码消费，对接校内门禁、图书馆等系统，实现无卡化校园生活。
        </p>
      </div>
        <div
          class="service-card market"
          :class="{ hidden: !canAccess('secondhand') }"
          @click="goToModule('/secondhand')"
        >
          <div class="service-icon">
            <i class="fas fa-shopping-bag"></i>
          </div>
          <h3 class="service-title">高校专属二手交易</h3>
          <p class="service-desc">
              同校精准匹配，担保交易，物品成色标注，打造安全可信的校内交易市场。
            </p>
        </div>
        <div
          class="service-card chef"
          :class="{ hidden: !canAccess('parttime') }"
          @click="goToModule('/parttime')"
        >
          <div class="service-icon">
            <i class="fas fa-utensils"></i>
          </div>
          <h3 class="service-title">规范化校园兼职</h3>
          <p class="service-desc">
              严格资质审核，公司资格智能校验，动态监管，保障学生兼职选择。
            </p>
          </div>
        <div
          class="service-card schedule"
          :class="{ hidden: !canAccess('schedule') }"
          @click="goToModule('/schedule')"
        >
          <div class="service-icon">
            <i class="fas fa-calendar-alt"></i>
          </div>
          <h3 class="service-title">校园行程管理</h3>
          <p class="service-desc">
            课程表自动导入，多设备同步，智能提醒，整合校园活动与个人事务。
          </p>
      </div>
    </section>

      <!-- 用户类型卡片 -->
      <section class="user-types">
        <h2 class="section-title" style="width: 100%; text-align: center; margin-bottom: 30px;">
          选择您的身份查看对应功能
        </h2>
        <div
          class="user-card student"
          :class="{ active: userStore.currentRole === 'student' }"
          @click="switchRole('student')"
        >
          <div class="user-icon-large">
            <i class="fas fa-user-graduate"></i>
          </div>
          <h3 class="user-title">学生</h3>
          <p class="user-desc">学号+教务系统验证码双因素认证，畅享校园全功能服务。</p>
        </div>
        <div
          class="user-card teacher"
          :class="{ active: userStore.currentRole === 'teacher' }"
          @click="switchRole('teacher')"
        >
          <div class="user-icon-large">
            <i class="fas fa-user-tie"></i>
          </div>
          <h3 class="user-title">教师</h3>
          <p class="user-desc">教学管理与校园生活一体化，提升工作与生活效率。</p>
        </div>
        <div
          class="user-card tourist"
          :class="{ active: userStore.currentRole === 'visitor' || userStore.currentRole === 'tourist' }"
          @click="switchRole('visitor')"
        >
          <div class="user-icon-large">
            <i class="fas fa-user-clock"></i>
          </div>
          <h3 class="user-title">游客</h3>
          <p class="user-desc">刷脸活体检测+进校登记，获取基础校园服务与导览信息。</p>
        </div>
        <div
          class="user-card university"
          :class="{ active: userStore.currentRole === 'university' }"
          @click="switchRole('university')"
        >
          <div class="user-icon-large">
            <i class="fas fa-university"></i>
          </div>
          <h3 class="user-title">高校</h3>
          <p class="user-desc">官方接入与管理，定制化功能配置，实现数字化校园治理。</p>
        </div>
        <div
          class="user-card merchant"
          :class="{ active: userStore.currentRole === 'merchant' }"
          @click="switchRole('merchant')"
        >
          <div class="user-icon-large">
            <i class="fas fa-store"></i>
          </div>
          <h3 class="user-title">兼职商家</h3>
          <p class="user-desc">资质核验与保证金保障，安全合规地开展校园商业活动。</p>
        </div>
        <div
          class="user-card admin"
          :class="{ active: userStore.currentRole === 'admin' }"
          @click="switchRole('admin')"
          v-if="userStore.isAdmin"
        >
          <div class="user-icon-large">
            <i class="fas fa-user-shield"></i>
          </div>
          <h3 class="user-title">管理员</h3>
          <p class="user-desc">系统全局配置与用户管理，保障平台稳定、安全运行。</p>
      </div>
    </section>

      <!-- 特色优势区域 -->
      <section class="features-section">
        <h2 class="section-title">平台核心优势</h2>
        <div class="features-grid">
          <div class="feature-item security">
            <i class="fas fa-shield-alt"></i>
            <div class="feature-content">
              <h3>信息安全保障</h3>
              <p>
              采用加密传输技术存储个人信息，定位权限分级调用，交易记录AES加密存储，确保用户信息安全。
            </p>
            </div>
          </div>
          <div class="feature-item official">
            <i class="fas fa-university"></i>
            <div class="feature-content">
              <h3>高校官方接入</h3>
              <p>
              高校通过后台系统注册，上传校园核心信息，实现"一校一集合、一人一身份"的精准服务。
            </p>
            </div>
          </div>
          <div class="feature-item efficient">
            <i class="fas fa-bolt"></i>
            <div class="feature-content">
              <h3>高效认证流程</h3>
              <p>
              学生双因素认证，游客刷脸活体检测，平衡认证速度与准确性，3个工作日内功能解锁审核。
            </p>
            </div>
          </div>
          <div class="feature-item sync">
            <i class="fas fa-sync-alt"></i>
            <div class="feature-content">
              <h3>多设备同步</h3>
              <p>
              云同步技术实现手机、电脑端实时更新，确保用户在不同设备上查看最新行程和消息。
            </p>
          </div>
        </div>
      </div>
    </section>

      <!-- 平台介绍 -->
      <section class="platform-intro">
        <div class="intro-content">
        <h2 class="section-title">关于上大学Online</h2>
          <p>
            "上大学Online"是一款以"高校官方注册封装用户群体"为核心的校园综合服务平台，针对当前高校校园服务存在的功能分散、用户边界模糊、信息流通低效、安全信任缺失四大痛点，提供全方位的解决方案。
          </p>
          <p class="vision-highlight">
            我们的愿景是：实现"一校一集合、一人一身份、一站全服务、一策保安全"。
          </p>
          <p>
            平台采用"基础功能+高校定制功能"的模块化设计，整合多源融合定位、支付接口对接、精准信息推送、交易流程保障等先进技术，为用户提供安全、便捷、精准、个性化的校园服务体验。
          </p>
        </div>
      </section>

      <!-- 动态区域 -->
      <section class="news-section">
        <h2 class="section-title">平台动态</h2>
        <div class="news-container">
          <div class="news-image">
            <i class="fas fa-bullhorn"></i>
          </div>
          <div class="news-content">
            <h3 class="news-title">最新公告</h3>
            <div class="news-list">
              <div class="news-item" v-for="(news, index) in newsList" :key="index">
                <router-link :to="news.link">{{ news.title }}</router-link>
              </div>
            </div>
        </div>
      </div>
    </section>
    </div>

    <!-- 页脚 -->
    <AppFooter />

    <!-- 悬浮菜单 -->
    <FloatingMenu />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import Carousel, { type CarouselSlide } from '@/components/common/Carousel.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
import { useUserStore } from '@campus/common'

const router = useRouter()
const userStore = useUserStore()

// 轮播图数据
const carouselSlides = ref<CarouselSlide[]>([
  {
    title: '打造智慧校园新生态',
    text: '连接校园生活每一刻，提供安全、便捷、个性化的一站式服务',
    image: 'https://source.unsplash.com/random/1200x400/?university,campus',
    buttonText: '立即体验',
    buttonLink: '/login'
  },
  {
    title: '一校一集合，一人一身份',
    text: '基于高校官方认证，构建精准用户群体，实现个性化服务推送',
    image: 'https://source.unsplash.com/random/1200x400/?student,study'
  },
  {
    title: '一站全服务，一策保安全',
    text: '整合校园各类服务，建立健全安全机制，让校园生活更安心',
    image: 'https://source.unsplash.com/random/1200x400/?community,network'
  }
])

// 平台动态列表
const newsList = ref([
  { title: '【新功能上线】校园E卡通人脸支付功能正式推出！', link: '/about' },
  { title: '【高校合作】恭喜北京大学、清华大学成为首批官方接入院校。', link: '/about' },
  { title: '【安全提示】二手交易防诈骗指南，请各位用户仔细阅读。', link: '/security' },
  { title: '【商户招募】校园商家入驻通道限时开放，享专属扶持计划。', link: '/feedback' },
  { title: '【用户调研】关于提升平台体验的问卷调查，期待您的参与。', link: '/feedback' }
])

// 检查是否可以访问某个功能（导航栏 / 首页统一使用同一套规则）
const canAccess = (feature: string): boolean => { // canAccess：根据当前角色判断首页卡片是否可点击
  const role = userStore.currentRole // 读取当前登录用户的角色编码
  const featureRoles: Record<string, string[]> = { // 功能到“允许访问角色列表”的映射表
    ecard: ['student', 'teacher', 'university', 'visitor', 'tourist'], // 校园卡：学生 / 教师 / 高校管理员 / 游客
    secondhand: ['student', 'teacher', 'merchant', 'visitor', 'tourist'], // 二手交易：学生 / 教师 / 商家 / 游客
    parttime: ['student', 'merchant', 'admin', 'visitor', 'tourist'], // 兼职：学生 / 商家 / 管理员 / 游客
    schedule: ['student', 'teacher', 'visitor', 'tourist'], // 行程管理：学生 / 教师 / 游客
    admin: ['admin', 'university'] // 管理中心：仅管理员与高校管理员可用
  }
  const allowedRoles = featureRoles[feature] || [] // 取出当前功能对应的允许角色数组（没有则为空数组）
  return allowedRoles.includes(role) // 当前角色在允许列表中则返回 true，否则返回 false
}

// 跳转到模块
const goToModule = (path: string) => {
  router.push(path)
}

// 切换角色（演示功能）
const switchRole = (role: string) => {
  // 这里只是演示，实际应该通过认证流程
  if (!userStore.isLoggedIn) {
    router.push('/login')
  } else {
    // 如果已登录，可以提示用户进行身份认证
    router.push('/auth')
  }
}

// 组件挂载
onMounted(() => {
  // 初始化用户信息
  userStore.initUserFromStorage()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 服务网格 */
.services-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin: 40px 0;
  justify-content: center;
}

.service-card {
  background: white;
  border-radius: 16px;
  padding: 30px 25px;
  width: calc(25% - 20px);
  min-width: 250px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(216, 27, 96, 0.1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.service-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #d81b60 0%, #ffb6c1 100%);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s ease;
}

.service-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 35px rgba(216, 27, 96, 0.2);
  border-color: rgba(216, 27, 96, 0.3);
}

.service-card:hover::before {
  transform: scaleX(1);
}

.service-card.hidden {
  display: none;
}

.service-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #f9d0da 0%, #f5b8c9 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  font-size: 28px;
  color: #d81b60;
}

.service-card.e-card .service-icon {
  background: linear-gradient(135deg, #e1f5fe 0%, #b3e5fc 100%);
  color: #0288d1;
}

.service-card.market .service-icon {
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
  color: #388e3c;
}

.service-card.chef .service-icon {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  color: #f57c00;
}

.service-card.schedule .service-icon {
  background: linear-gradient(135deg, #f3e5f5 0%, #e1bee7 100%);
  color: #7b1fa2;
}

.service-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.service-desc {
  color: #666;
  line-height: 1.5;
  font-size: 14px;
}

/* 用户类型卡片 */
.user-types {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin: 50px 0;
  flex-wrap: wrap;
}

.user-card {
  background: white;
  border-radius: 16px;
  padding: 35px 30px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  flex: 1;
  min-width: 150px;
  cursor: pointer;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.user-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(216, 27, 96, 0.05) 0%, rgba(255, 182, 193, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.4s ease;
}

.user-card:hover::after,
.user-card.active::after {
  opacity: 1;
}

.user-card:hover,
.user-card.active {
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 10px 30px rgba(216, 27, 96, 0.25);
  border-color: #d81b60;
}

.user-icon-large {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 32px;
  color: white;
}

.user-card.student .user-icon-large {
  background: linear-gradient(135deg, #42a5f5 0%, #1e88e5 100%);
}

.user-card.teacher .user-icon-large {
  background: linear-gradient(135deg, #66bb6a 0%, #43a047 100%);
}

.user-card.tourist .user-icon-large {
  background: linear-gradient(135deg, #ffa726 0%, #fb8c00 100%);
}

.user-card.university .user-icon-large {
  background: linear-gradient(135deg, #ec407a 0%, #d81b60 100%);
}

.user-card.merchant .user-icon-large {
  background: linear-gradient(135deg, #ab47bc 0%, #8e24aa 100%);
}

.user-card.admin .user-icon-large {
  background: linear-gradient(135deg, #ef5350 0%, #e53935 100%);
}

.user-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.user-desc {
  color: #666;
  line-height: 1.4;
  font-size: 13px;
}

/* 特色优势区域 */
.features-section {
  background: white;
  border-radius: 20px;
  padding: 60px 50px;
  margin: 50px 0;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(216, 27, 96, 0.1);
  position: relative;
  overflow: hidden;
}

.features-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #d81b60 0%, #ffb6c1 100%);
}

.section-title {
  text-align: center;
  font-size: 32px;
  font-weight: bold;
  color: #d81b60;
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  padding: 25px;
  background: linear-gradient(135deg, #f8f9fa 0%, #f5f5f5 100%);
  border-radius: 12px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
  position: relative;
  overflow: hidden;
}

.feature-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, #d81b60 0%, #ffb6c1 100%);
  transform: scaleY(0);
  transform-origin: top;
  transition: transform 0.4s ease;
}

.feature-item:hover {
  background: linear-gradient(135deg, #fff5f9 0%, #ffeef5 100%);
  transform: translateX(8px);
  border-color: rgba(216, 27, 96, 0.2);
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.1);
}

.feature-item:hover::before {
  transform: scaleY(1);
}

.feature-item i {
  width: 50px;
  height: 50px;
  background: #d81b60;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.feature-item.security i {
  background-color: #ef5350;
}

.feature-item.official i {
  background-color: #42a5f5;
}

.feature-item.efficient i {
  background-color: #66bb6a;
}

.feature-item.sync i {
  background-color: #ab47bc;
}

.feature-content h3 {
  color: #333;
  margin-bottom: 8px;
  font-size: 18px;
}

.feature-content p {
  color: #666;
  line-height: 1.6;
}

/* 平台介绍 */
.platform-intro {
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
  border-radius: 16px;
  padding: 50px;
  margin: 50px 0;
}

.intro-content {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.intro-content p {
  font-size: 16px;
  line-height: 1.8;
  color: #555;
  margin-bottom: 20px;
}

.vision-highlight {
  font-weight: bold;
  color: #d81b60;
  font-size: 18px;
  letter-spacing: 1px;
}

/* 动态区域 */
.news-section {
  background: white;
  border-radius: 16px;
  padding: 40px;
  margin: 50px 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.news-container {
  display: flex;
  gap: 30px;
  align-items: stretch;
}

.news-image {
  flex: 1;
  background: linear-gradient(135deg, #f9d0da 0%, #f5b8c9 100%);
  border-radius: 12px;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64px;
  color: #d81b60;
}

.news-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.news-title {
  font-size: 24px;
  font-weight: bold;
  color: #d81b60;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.news-item {
  padding: 15px 20px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s;
  border-left: 4px solid #d81b60;
}

.news-item:hover {
  background: #f0f0f0;
  transform: translateX(5px);
}

.news-item a {
  color: #333;
  font-weight: 500;
  text-decoration: none;
  display: block;
}

.news-item a:hover {
  color: #d81b60;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .services-grid {
    flex-direction: column;
    align-items: center;
  }

  .service-card {
    width: 100%;
  }

  .user-types {
    flex-direction: column;
  }

  .news-container {
    flex-direction: column;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .features-section,
  .platform-intro,
  .news-section {
    padding: 30px 20px;
  }
}
</style>
