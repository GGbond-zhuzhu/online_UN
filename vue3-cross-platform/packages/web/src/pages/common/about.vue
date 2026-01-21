<template>
  <div class="common-page about-page">
    <NavBar />

    <main class="about-main">
      <div class="container">
        <!-- 顶部轮播 + 标题 -->
        <section class="hero">
          <Carousel :slides="carouselSlides" :autoplay="true" :interval="6500" />

          <div class="hero-head">
            <p class="breadcrumb">首页 / 平台介绍</p>
            <h1 class="title">{{ aboutInfo?.platformName || '上大学Online' }}</h1>
            <p class="subtitle">{{ aboutInfo?.slogan || '让校园生活更简单' }}</p>
          </div>
        </section>

        <!-- 平台概览 -->
        <section class="section">
          <div class="section-header">
            <h2 class="section-title">平台概览</h2>
            <p class="section-subtitle">用更少的步骤，完成更多的校园事务。</p>
          </div>

          <div class="overview-grid">
            <article class="overview-card">
              <div class="overview-icon"><i class="fas fa-layer-group"></i></div>
              <div class="overview-title">一站式服务</div>
              <div class="overview-desc">校园卡、二手、兼职、行程等模块统一入口。</div>
            </article>
            <article class="overview-card">
              <div class="overview-icon"><i class="fas fa-user-check"></i></div>
              <div class="overview-title">身份可信</div>
              <div class="overview-desc">多重身份认证机制，保障平台与交易安全。</div>
            </article>
            <article class="overview-card">
              <div class="overview-icon"><i class="fas fa-shield-alt"></i></div>
              <div class="overview-title">安全合规</div>
              <div class="overview-desc">加密传输、权限分级与风控策略，持续守护数据。</div>
            </article>
            <article class="overview-card">
              <div class="overview-icon"><i class="fas fa-rocket"></i></div>
              <div class="overview-title">持续迭代</div>
              <div class="overview-desc">围绕真实校园场景不断完善体验与效率。</div>
            </article>
          </div>
        </section>

        <!-- 平台时间线 -->
        <section class="section">
          <div class="section-header">
            <h2 class="section-title">平台时间线</h2>
            <p class="section-subtitle">从立项到迭代，我们记录每一步关键进展。</p>
          </div>

          <div class="timeline">
            <div v-for="(item, idx) in timeline" :key="idx" class="timeline-item">
              <div class="timeline-dot" aria-hidden="true"></div>
              <div class="timeline-card">
                <div class="timeline-time">{{ item.time }}</div>
                <div class="timeline-title">{{ item.title }}</div>
                <div class="timeline-desc">{{ item.desc }}</div>
              </div>
            </div>
          </div>
        </section>

        <!-- 详细介绍 -->
        <section class="section">
          <div class="section-header">
            <h2 class="section-title">平台介绍</h2>
            <p class="section-subtitle">面向高校与大学生的综合校园服务平台。</p>
          </div>

          <div class="intro-grid">
            <article class="intro-card">
              <div class="intro-card-head">
                <i class="fas fa-bullseye"></i>
                <h3>我们的愿景</h3>
              </div>
              <p class="intro-text">{{ aboutInfo?.vision || '让每一所高校都拥有更高效、更安全、更贴近学生的数字化服务体验。' }}</p>
            </article>

            <article class="intro-card">
              <div class="intro-card-head">
                <i class="fas fa-info-circle"></i>
                <h3>平台简介</h3>
              </div>
              <p class="intro-text">{{ aboutInfo?.description || '整合校园多类服务，提供统一入口与可信身份体系，并持续迭代安全机制与用户体验。' }}</p>
            </article>
          </div>
        </section>

        <!-- 核心特性 -->
        <section class="section">
          <div class="section-header">
            <h2 class="section-title">核心特性</h2>
            <p class="section-subtitle">围绕“效率、安全、可信”打造的关键能力。</p>
          </div>

          <div class="features-grid">
            <article v-for="(feature, index) in featureList" :key="index" class="feature-card">
              <div class="feature-icon"><i :class="['fas', getFeatureIcon(feature)]"></i></div>
              <div class="feature-title">{{ feature }}</div>
            </article>
          </div>
        </section>

        <!-- 联系我们 -->
        <section class="section">
          <div class="section-header">
            <h2 class="section-title">联系我们</h2>
            <p class="section-subtitle">合作接入、技术支持与问题反馈，欢迎随时联系。</p>
          </div>

          <div class="contact-grid">
            <article class="contact-card">
              <div class="contact-head">
                <i class="fas fa-phone"></i>
                <div class="contact-title">服务热线</div>
              </div>
              <div class="contact-value">{{ contactInfo?.serviceHotline || '400-123-4567' }}</div>
              <div class="contact-tip">工作日 9:00-18:00</div>
            </article>
            <article class="contact-card">
              <div class="contact-head">
                <i class="fas fa-wrench"></i>
                <div class="contact-title">技术支持</div>
              </div>
              <div class="contact-value">{{ contactInfo?.techSupport || 'support@shangdaxue.com' }}</div>
              <div class="contact-tip">问题排查 / 接口对接</div>
            </article>
            <article class="contact-card">
              <div class="contact-head">
                <i class="fas fa-handshake"></i>
                <div class="contact-title">商务合作</div>
              </div>
              <div class="contact-value">{{ contactInfo?.businessCooperation || 'bd@shangdaxue.com' }}</div>
              <div class="contact-tip">生态共建 / 合作洽谈</div>
            </article>
            <article class="contact-card">
              <div class="contact-head">
                <i class="fas fa-university"></i>
                <div class="contact-title">高校接入</div>
              </div>
              <div class="contact-value">{{ contactInfo?.universityAccess || 'campus@shangdaxue.com' }}</div>
              <div class="contact-tip">{{ contactInfo?.workingHours || '工作日 9:00-18:00' }}</div>
            </article>
          </div>

          <div class="links-row">
            <a class="link-chip" href="/help">帮助中心</a>
            <a class="link-chip" href="/security">安全保障</a>
            <a class="link-chip" href="/privacy">隐私政策</a>
            <a class="link-chip" href="/terms">服务协议</a>
          </div>
        </section>

        <div v-if="isLoading" class="loading">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
      </div>
    </main>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import Carousel, { type CarouselSlide } from '@/components/common/Carousel.vue'
import { getAboutInfo, getContactInfo, type AboutInfo, type ContactInfo } from '@campus/common'

// 平台介绍信息
const aboutInfo = ref<AboutInfo | null>(null)
// 联系方式
const contactInfo = ref<ContactInfo | null>(null)
const isLoading = ref(true)

// 获取特性图标
const getFeatureIcon = (feature: string): string => {
  if (feature.includes('一校一集合')) return 'fa-university'
  if (feature.includes('一人一身份')) return 'fa-user'
  if (feature.includes('一站全服务')) return 'fa-tools'
  if (feature.includes('一策保安全')) return 'fa-lock'
  if (feature.includes('基础功能')) return 'fa-cog'
  if (feature.includes('定位')) return 'fa-map-marker-alt'
  return 'fa-star'
}

const carouselSlides = ref<CarouselSlide[]>([
  {
    title: '上大学Online',
    text: '连接校园生活每一刻，提供安全、便捷、个性化的一站式服务。',
    image: 'https://source.unsplash.com/random/1200x400/?campus,university,building',
    buttonText: '了解功能',
    buttonLink: '/help'
  },
  {
    title: '一校一集合，一人一身份',
    text: '基于高校官方接入与身份体系，构建可信校园服务网络。',
    image: 'https://source.unsplash.com/random/1200x400/?student,study,library'
  },
  {
    title: '一站全服务，一策保安全',
    text: '整合多类校园能力，配套加密传输与权限分级机制。',
    image: 'https://source.unsplash.com/random/1200x400/?security,technology,network'
  }
])

const timeline = ref([
  { time: '2024.03', title: '项目立项', desc: '面向高校场景，启动一站式校园服务平台建设。' },
  { time: '2024.06', title: '核心模块上线', desc: '校园卡、二手、兼职、行程等基础能力完成第一轮落地。' },
  { time: '2024.09', title: '安全体系升级', desc: '完善加密传输、权限分级与风控策略，提升整体安全性。' },
  { time: '2024.12', title: '体验迭代', desc: '围绕高频场景优化交互与性能，持续提升使用效率。' }
])

const featureList = computed(() => {
  const list = aboutInfo.value?.features?.length ? aboutInfo.value.features : [
    '一校一集合',
    '一人一身份',
    '一站全服务',
    '一策保安全'
  ]
  return list
})

// 加载平台介绍信息
const loadAboutInfo = async () => {
  try {
    aboutInfo.value = await getAboutInfo()
  } catch (error) {
    console.error('加载平台介绍失败:', error)
  }
}

// 加载联系方式
const loadContactInfo = async () => {
  try {
    contactInfo.value = await getContactInfo()
  } catch (error) {
    console.error('加载联系方式失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  Promise.allSettled([loadAboutInfo(), loadContactInfo()]).finally(() => {
    isLoading.value = false
  })
})
</script>

<style scoped>
.common-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #fdfbff 0%, #f7f0ff 25%, #fdf3f7 60%, #ffffff 100%);
}

.about-main {
  flex: 1;
  padding: 30px 0 60px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero {
  margin-bottom: 26px;
}

.hero-head {
  margin-top: 14px;
  padding: 0 2px;
}

.breadcrumb {
  margin: 0 0 6px;
  font-size: 13px;
  color: #9e9eaa;
}

.title {
  margin: 0 0 6px;
  font-size: 32px;
  font-weight: 800;
  color: #233145;
}

.subtitle {
  margin: 0;
  font-size: 15px;
  color: #5f6473;
}

.section {
  margin-top: 26px;
}

.section-header {
  margin-bottom: 16px;
}

.section-title {
  margin: 0 0 6px;
  font-size: 22px;
  font-weight: 800;
  color: #222a3a;
}

.section-subtitle {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 14px;
}

.overview-card {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(209, 213, 219, 0.7);
  border-radius: 16px;
  padding: 16px 16px 14px;
  box-shadow: 0 10px 24px rgba(148, 163, 184, 0.22);
  transition: transform 0.12s ease, box-shadow 0.12s ease, border-color 0.12s ease;
}

.overview-card:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 75, 139, 0.45);
  box-shadow: 0 14px 30px rgba(148, 163, 184, 0.30);
}

.overview-icon {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: linear-gradient(135deg, #ffebf3, #ffe4ff);
  color: #ff4b8b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  margin-bottom: 10px;
}

.overview-title {
  font-size: 15px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 4px;
}

.overview-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.7;
}

.timeline {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-left: 18px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 7px;
  top: 6px;
  bottom: 6px;
  width: 2px;
  background: rgba(255, 75, 139, 0.25);
}

.timeline-item {
  position: relative;
  display: flex;
  gap: 12px;
}

.timeline-dot {
  position: absolute;
  left: -2px;
  top: 14px;
  width: 14px;
  height: 14px;
  border-radius: 999px;
  background: linear-gradient(135deg, #ff4b8b, #ff7ab2);
  box-shadow: 0 8px 18px rgba(255, 75, 139, 0.28);
}

.timeline-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(209, 213, 219, 0.7);
  border-radius: 16px;
  padding: 14px 14px 12px;
  box-shadow: 0 10px 24px rgba(148, 163, 184, 0.20);
}

.timeline-time {
  font-size: 12px;
  color: #ff4b8b;
  font-weight: 800;
  margin-bottom: 4px;
}

.timeline-title {
  font-size: 15px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 4px;
}

.timeline-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.7;
}

.intro-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 14px;
}

.intro-card {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(209, 213, 219, 0.7);
  border-radius: 16px;
  padding: 16px 16px 14px;
  box-shadow: 0 10px 24px rgba(148, 163, 184, 0.22);
}

.intro-card-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  color: #111827;
}

.intro-card-head i {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: rgba(255, 75, 139, 0.10);
  border: 1px solid rgba(255, 75, 139, 0.18);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #ff4b8b;
}

.intro-card-head h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 800;
}

.intro-text {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.8;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 14px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(209, 213, 219, 0.7);
  border-radius: 16px;
  padding: 14px 14px 12px;
  box-shadow: 0 10px 24px rgba(148, 163, 184, 0.20);
  display: flex;
  gap: 12px;
  align-items: center;
}

.feature-icon {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: rgba(248, 250, 252, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ff4b8b;
  border: 1px solid rgba(148, 163, 184, 0.22);
}

.feature-title {
  font-size: 14px;
  font-weight: 800;
  color: #111827;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 14px;
}

.contact-card {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(209, 213, 219, 0.7);
  border-radius: 16px;
  padding: 16px 16px 14px;
  box-shadow: 0 10px 24px rgba(148, 163, 184, 0.22);
}

.contact-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  color: #111827;
}

.contact-head i {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffebf3, #ffe4ff);
  color: #ff4b8b;
  display: flex;
  align-items: center;
  justify-content: center;
}

.contact-title {
  font-size: 15px;
  font-weight: 800;
}

.contact-value {
  font-size: 14px;
  font-weight: 800;
  color: #ff4b8b;
  margin-bottom: 6px;
}

.contact-tip {
  font-size: 12px;
  color: #6b7280;
}

.links-row {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.link-chip {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  border: 1px solid rgba(209, 213, 219, 0.8);
  background: rgba(255, 255, 255, 0.85);
  color: #374151;
  text-decoration: none;
  font-size: 13px;
  font-weight: 650;
  transition: background 0.12s ease, border-color 0.12s ease, transform 0.06s ease;
}

.link-chip:hover {
  border-color: rgba(255, 75, 139, 0.45);
  background: rgba(255, 247, 251, 0.9);
}

.link-chip:active {
  transform: scale(0.98);
}

.loading {
  text-align: center;
  padding: 28px 0 0;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #ff4b8b;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .about-main {
    padding: 20px 0 46px;
  }

  .container {
    padding: 0 16px;
  }

  .title {
    font-size: 24px;
  }
}
</style>
