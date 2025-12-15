<template>
  <div class="common-page about-page">
    <!-- 全局导航栏 -->
    <NavBar />

    <!-- 页面内容 -->
    <div class="page-content">
      <div class="content-container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1 class="page-title">{{ aboutInfo?.platformName || '上大学Online' }}</h1>
          <p class="page-subtitle">{{ aboutInfo?.slogan || '让校园生活更简单' }}</p>
        </div>

        <!-- 平台介绍内容 -->
        <div class="about-content" v-if="aboutInfo">
          <!-- 愿景展示 -->
          <div class="vision-section">
            <div class="vision-card">
              <h2 class="vision-title">我们的愿景</h2>
              <p class="vision-text">{{ aboutInfo.vision }}</p>
            </div>
          </div>

          <!-- 平台描述 -->
          <div class="description-section">
            <h2 class="section-title">平台简介</h2>
            <p class="description-text">{{ aboutInfo.description }}</p>
          </div>

          <!-- 核心特性 -->
          <div class="features-section">
            <h2 class="section-title">核心特性</h2>
            <div class="features-grid">
              <div
                v-for="(feature, index) in aboutInfo.features"
                :key="index"
                class="feature-card"
              >
                <div class="feature-icon"><i :class="['fas', getFeatureIcon(feature)]"></i></div>
                <h3 class="feature-title">{{ feature }}</h3>
              </div>
            </div>
          </div>

          <!-- 平台优势 -->
          <div class="advantages-section">
            <h2 class="section-title">平台优势</h2>
            <div class="advantages-list">
              <div class="advantage-item">
                <div class="advantage-icon"><i class="fas fa-university"></i></div>
                <div class="advantage-content">
                  <h4>一校一集合</h4>
                  <p>基于高校官方认证，构建精准用户群体，实现个性化服务推送</p>
                </div>
              </div>
              <div class="advantage-item">
                <div class="advantage-icon"><i class="fas fa-user"></i></div>
                <div class="advantage-content">
                  <h4>一人一身份</h4>
                  <p>多重身份认证机制，确保用户身份真实可靠，保障平台安全</p>
                </div>
              </div>
              <div class="advantage-item">
                <div class="advantage-icon"><i class="fas fa-tools"></i></div>
                <div class="advantage-content">
                  <h4>一站全服务</h4>
                  <p>整合校园各类服务，提供一站式校园生活解决方案</p>
                </div>
              </div>
              <div class="advantage-item">
                <div class="advantage-icon"><i class="fas fa-lock"></i></div>
                <div class="advantage-content">
                  <h4>一策保安全</h4>
                  <p>建立健全安全机制，采用加密传输和权限分级，保护用户信息安全</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 联系我们 -->
          <div class="contact-section">
            <h2 class="section-title">联系我们</h2>
            <div class="contact-info" v-if="contactInfo">
              <div class="contact-item">
                <span class="contact-label">服务热线：</span>
                <span class="contact-value">{{ contactInfo.serviceHotline }}</span>
              </div>
              <div class="contact-item">
                <span class="contact-label">技术支持：</span>
                <span class="contact-value">{{ contactInfo.techSupport }}</span>
              </div>
              <div class="contact-item">
                <span class="contact-label">商务合作：</span>
                <span class="contact-value">{{ contactInfo.businessCooperation }}</span>
              </div>
              <div class="contact-item">
                <span class="contact-label">高校接入：</span>
                <span class="contact-value">{{ contactInfo.universityAccess }}</span>
              </div>
              <div class="contact-item">
                <span class="contact-label">工作时间：</span>
                <span class="contact-value">{{ contactInfo.workingHours }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-else class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import { getAboutInfo, getContactInfo, type AboutInfo, type ContactInfo } from '@campus/common'

// 平台介绍信息
const aboutInfo = ref<AboutInfo | null>(null)
// 联系方式
const contactInfo = ref<ContactInfo | null>(null)

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
  loadAboutInfo()
  loadContactInfo()
})
</script>

<style scoped>
.common-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.page-content {
  flex: 1;
  padding: 2rem 0;
  background: #f8f9fa;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* 页面标题区域 */
.page-header {
  text-align: center;
  margin-bottom: 3rem;
  padding-top: 2rem;
}

.page-title {
  font-size: 3rem;
  font-weight: bold;
  color: #e91e63;
  margin-bottom: 1rem;
}

.page-subtitle {
  font-size: 1.3rem;
  color: #666;
}

/* 愿景展示区域 */
.vision-section {
  margin-bottom: 4rem;
}

.vision-card {
  background: linear-gradient(135deg, #e91e63 0%, #c2185b 100%);
  border-radius: 16px;
  padding: 3rem;
  text-align: center;
  color: #fff;
  box-shadow: 0 4px 20px rgba(233, 30, 99, 0.3);
}

.vision-title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
}

.vision-text {
  font-size: 1.3rem;
  line-height: 1.8;
  opacity: 0.95;
}

/* 平台描述区域 */
.description-section {
  margin-bottom: 4rem;
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 1.8rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 1.5rem;
  text-align: center;
}

.description-text {
  font-size: 1.1rem;
  color: #666;
  line-height: 1.8;
  text-align: center;
}

/* 核心特性区域 */
.features-section {
  margin-bottom: 4rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: #fff;
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #e91e63;
}

.feature-icon i {
  display: inline-block;
}

.feature-title {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
}

/* 平台优势区域 */
.advantages-section {
  margin-bottom: 4rem;
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.advantages-list {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.advantage-item {
  display: flex;
  gap: 2rem;
  align-items: flex-start;
}

.advantage-icon {
  font-size: 3rem;
  flex-shrink: 0;
  color: #e91e63;
}

.advantage-icon i {
  display: inline-block;
}

.advantage-content {
  flex: 1;
}

.advantage-content h4 {
  font-size: 1.3rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.8rem;
}

.advantage-content p {
  font-size: 1rem;
  color: #666;
  line-height: 1.8;
}

/* 联系我们区域 */
.contact-section {
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.contact-label {
  font-weight: bold;
  color: #333;
  min-width: 120px;
}

.contact-value {
  color: #666;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 4rem 0;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #e91e63;
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
  .content-container {
    padding: 0 1rem;
  }

  .page-title {
    font-size: 2rem;
  }

  .page-subtitle {
    font-size: 1.1rem;
  }

  .vision-card {
    padding: 2rem;
  }

  .vision-title {
    font-size: 1.5rem;
  }

  .vision-text {
    font-size: 1.1rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .advantage-item {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }

  .contact-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .contact-label {
    min-width: auto;
    margin-bottom: 0.5rem;
  }
}
</style>
