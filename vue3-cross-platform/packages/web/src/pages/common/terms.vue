<template>
  <div class="common-page terms-page">
    <!-- 全局导航栏 -->
    <NavBar />

    <!-- 页面内容 -->
    <div class="page-content">
      <div class="content-container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1 class="page-title">服务协议</h1>
          <p class="page-subtitle">使用本平台服务前，请仔细阅读并同意本服务协议</p>
        </div>

        <!-- 服务协议内容 -->
        <div class="terms-content" v-if="serviceAgreement">
          <!-- 版本信息 -->
          <div class="version-info">
            <p>版本：{{ serviceAgreement.version }}</p>
            <p>更新时间：{{ formatDate(serviceAgreement.updateTime) }}</p>
          </div>

          <!-- 协议内容 -->
          <div class="agreement-content">
            <div class="content-text" v-html="formatContent(serviceAgreement.content)"></div>
          </div>

          <!-- 详细条款 -->
          <div class="agreement-sections">
            <div class="agreement-section">
              <h3>1. 协议的接受</h3>
              <p>欢迎使用"上大学Online"平台服务。在使用本平台服务前，您应当仔细阅读并充分理解本服务协议的全部内容。当您点击"同意"或开始使用本平台服务时，即表示您已充分理解并同意接受本协议的全部内容。</p>
            </div>

            <div class="agreement-section">
              <h3>2. 服务说明</h3>
              <p>本平台提供以下服务：</p>
              <ul>
                <li>校园E卡通服务：提供学生码消费、门禁、图书馆等功能</li>
                <li>二手交易服务：提供同校二手商品交易平台</li>
                <li>兼职服务：提供规范化校园兼职信息发布和申请</li>
                <li>行程管理服务：提供课程表管理和团队行程功能</li>
                <li>其他相关服务</li>
              </ul>
            </div>

            <div class="agreement-section">
              <h3>3. 用户账户</h3>
              <p>使用本平台服务需要注册账户。您应当：</p>
              <ul>
                <li>提供真实、准确、完整的注册信息</li>
                <li>妥善保管账户密码，对账户下的所有行为负责</li>
                <li>及时更新注册信息，保持信息的准确性</li>
                <li>不得将账户转让、出售或授权他人使用</li>
              </ul>
            </div>

            <div class="agreement-section">
              <h3>4. 用户行为规范</h3>
              <p>您在使用本平台服务时，应当遵守以下规范：</p>
              <ul>
                <li>遵守国家法律法规和平台规则</li>
                <li>不得发布虚假、违法、侵权信息</li>
                <li>不得进行欺诈、诈骗等违法行为</li>
                <li>不得干扰平台正常运营</li>
                <li>尊重其他用户的合法权益</li>
              </ul>
            </div>

            <div class="agreement-section">
              <h3>5. 知识产权</h3>
              <p>本平台的所有内容，包括但不限于文字、图片、音频、视频、软件、程序、版面设计等，均受知识产权法保护。未经授权，您不得复制、传播、展示、镜像、上传、下载本平台的任何内容。</p>
            </div>

            <div class="agreement-section">
              <h3>6. 免责声明</h3>
              <p>在以下情况下，本平台不承担责任：</p>
              <ul>
                <li>因不可抗力导致的服务中断或数据丢失</li>
                <li>因用户违反本协议导致的一切后果</li>
                <li>因第三方原因导致的服务问题</li>
                <li>用户因使用本平台服务而产生的直接或间接损失</li>
              </ul>
            </div>

            <div class="agreement-section">
              <h3>7. 服务变更与终止</h3>
              <p>本平台有权根据业务发展需要，变更、中断或终止部分或全部服务。如因服务变更、中断或终止给您造成损失的，本平台不承担责任。</p>
            </div>

            <div class="agreement-section">
              <h3>8. 协议修改</h3>
              <p>本平台有权根据法律法规变化和业务发展需要，随时修改本协议。修改后的协议将在平台上公布，自公布之日起生效。如您不同意修改后的协议，应当停止使用本平台服务。</p>
            </div>

            <div class="agreement-section">
              <h3>9. 争议解决</h3>
              <p>因本协议引起的或与本协议有关的任何争议，双方应当友好协商解决。协商不成的，任何一方均可向本平台所在地的人民法院提起诉讼。</p>
            </div>

            <div class="agreement-section">
              <h3>10. 联系我们</h3>
              <p>如果您对本服务协议有任何疑问，请通过以下方式联系我们：</p>
              <ul>
                <li>邮箱：legal@campus.edu.cn</li>
                <li>服务热线：400-123-4567</li>
              </ul>
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
import { getServiceAgreement, type ServiceAgreement } from '@campus/common'

// 服务协议
const serviceAgreement = ref<ServiceAgreement | null>(null)

// 格式化日期
const formatDate = (dateString: string): string => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 格式化内容（将换行符转换为HTML）
const formatContent = (content: string): string => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}

// 加载服务协议
const loadServiceAgreement = async () => {
  try {
    serviceAgreement.value = await getServiceAgreement()
  } catch (error) {
    console.error('加载服务协议失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadServiceAgreement()
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
  max-width: 1000px;
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

/* 版本信息 */
.version-info {
  text-align: right;
  margin-bottom: 2rem;
  padding: 1rem;
  background: #fff;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #999;
}

.version-info p {
  margin: 0.3rem 0;
}

/* 协议内容 */
.terms-content {
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.agreement-content {
  margin-bottom: 3rem;
  padding-bottom: 2rem;
  border-bottom: 2px solid #f0f0f0;
}

.content-text {
  font-size: 1rem;
  color: #666;
  line-height: 1.8;
}

/* 详细条款 */
.agreement-sections {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.agreement-section {
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #e91e63;
}

.agreement-section h3 {
  font-size: 1.3rem;
  font-weight: bold;
  color: #e91e63;
  margin-bottom: 1rem;
}

.agreement-section p {
  font-size: 1rem;
  color: #666;
  line-height: 1.8;
  margin-bottom: 1rem;
}

.agreement-section ul {
  margin-left: 2rem;
  margin-bottom: 0;
}

.agreement-section li {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.8;
  margin-bottom: 0.5rem;
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

  .terms-content {
    padding: 1.5rem;
  }

  .agreement-section {
    padding: 1rem;
  }

  .agreement-section h3 {
    font-size: 1.1rem;
  }
}
</style>
