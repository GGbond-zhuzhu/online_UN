<template>
  <div class="common-page security-page">
    <!-- 全局导航栏 -->
    <NavBar />

    <!-- 页面内容 -->
    <div class="page-content">
      <div class="content-container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1 class="page-title">安全保障</h1>
          <p class="page-subtitle">我们致力于保护您的个人信息和数据安全</p>
        </div>

        <!-- 安全保障内容 -->
        <div class="security-content" v-if="securityInfo">
          <div class="security-section">
            <div class="security-card">
              <div class="card-icon"><i class="fas fa-lock"></i></div>
              <h3 class="card-title">数据加密传输</h3>
              <p class="card-desc">{{ securityInfo.encryption }}</p>
            </div>

            <div class="security-card">
              <div class="card-icon"><i class="fas fa-map-marker-alt"></i></div>
              <h3 class="card-title">定位权限分级</h3>
              <p class="card-desc">{{ securityInfo.locationPermission }}</p>
            </div>

            <div class="security-card">
              <div class="card-icon"><i class="fas fa-credit-card"></i></div>
              <h3 class="card-title">交易记录加密</h3>
              <p class="card-desc">{{ securityInfo.transactionEncryption }}</p>
            </div>

            <div class="security-card">
              <div class="card-icon"><i class="fas fa-shield-alt"></i></div>
              <h3 class="card-title">数据保护</h3>
              <p class="card-desc">{{ securityInfo.dataProtection }}</p>
            </div>
          </div>

          <!-- 详细说明 -->
          <div class="security-details">
            <h2 class="details-title">安全机制说明</h2>
            <div class="details-content">
              <div class="detail-item">
                <h4>1. 数据加密</h4>
                <p>平台采用HTTPS加密传输协议，确保所有数据在传输过程中的安全性。敏感信息采用AES-256加密算法进行存储，只有授权用户才能访问。</p>
              </div>
              <div class="detail-item">
                <h4>2. 权限管理</h4>
                <p>采用分级权限管理机制，不同身份的用户拥有不同的权限。定位权限按需申请，不会过度收集用户位置信息。</p>
              </div>
              <div class="detail-item">
                <h4>3. 交易安全</h4>
                <p>所有交易记录采用AES加密存储，确保交易数据的安全性和完整性。支持担保交易，保障买卖双方权益。</p>
              </div>
              <div class="detail-item">
                <h4>4. 隐私保护</h4>
                <p>严格遵守相关法律法规，保护用户隐私信息。不会向第三方泄露用户个人信息，除非获得用户明确授权或法律要求。</p>
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
import { getSecurityInfo, type SecurityInfo } from '@campus/common'

// 安全保障信息
const securityInfo = ref<SecurityInfo | null>(null)

// 加载安全保障信息
const loadSecurityInfo = async () => {
  try {
    securityInfo.value = await getSecurityInfo()
  } catch (error) {
    console.error('加载安全保障信息失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadSecurityInfo()
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
  font-size: 2.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 1rem;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #666;
}

/* 安全保障卡片区域 */
.security-content {
  margin-top: 2rem;
}

.security-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 4rem;
}

.security-card {
  background: #fff;
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.security-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.card-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #e91e63;
}

.card-icon i {
  display: inline-block;
}

.card-title {
  font-size: 1.3rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 1rem;
}

.card-desc {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.6;
}

/* 详细说明区域 */
.security-details {
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.details-title {
  font-size: 1.8rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 2rem;
  text-align: center;
}

.details-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.detail-item {
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #e91e63;
}

.detail-item h4 {
  font-size: 1.2rem;
  font-weight: bold;
  color: #e91e63;
  margin-bottom: 0.8rem;
}

.detail-item p {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.8;
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
    font-size: 1rem;
  }

  .security-section {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .security-details {
    padding: 1.5rem;
  }

  .details-title {
    font-size: 1.5rem;
  }
}
</style>
