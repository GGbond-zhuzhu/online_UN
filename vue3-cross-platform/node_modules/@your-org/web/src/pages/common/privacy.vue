<template>
  <div class="common-page privacy-page">
    <!-- 全局导航栏 -->
    <NavBar />

    <!-- 页面内容 -->
    <div class="page-content">
      <div class="content-container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1 class="page-title">隐私政策</h1>
          <p class="page-subtitle">我们重视您的隐私，致力于保护您的个人信息安全</p>
        </div>

        <!-- 隐私政策内容 -->
        <div class="privacy-content" v-if="privacyPolicy">
          <!-- 版本信息 -->
          <div class="version-info">
            <p>版本：{{ privacyPolicy.version }}</p>
            <p>更新时间：{{ formatDate(privacyPolicy.updateTime) }}</p>
          </div>

          <!-- 政策内容 -->
          <div class="policy-content">
            <div class="content-text" v-html="formatContent(privacyPolicy.content)"></div>
          </div>

          <!-- 详细条款 -->
          <div class="policy-sections">
            <div class="policy-section">
              <h3>1. 信息收集</h3>
              <p>我们收集的信息包括但不限于：</p>
              <ul>
                <li>注册时提供的个人信息（手机号、邮箱等）</li>
                <li>身份认证时提供的身份信息（学号、身份证号等）</li>
                <li>使用服务时产生的数据（位置信息、交易记录等）</li>
                <li>设备信息（设备型号、操作系统等）</li>
              </ul>
            </div>

            <div class="policy-section">
              <h3>2. 信息使用</h3>
              <p>我们使用收集的信息用于：</p>
              <ul>
                <li>提供、维护和改进我们的服务</li>
                <li>处理您的交易和请求</li>
                <li>发送服务通知和重要信息</li>
                <li>进行数据分析和研究</li>
                <li>保障平台安全和防止欺诈</li>
              </ul>
            </div>

            <div class="policy-section">
              <h3>3. 信息共享</h3>
              <p>我们不会向第三方出售、交易或转让您的个人信息，除非：</p>
              <ul>
                <li>获得您的明确同意</li>
                <li>法律法规要求或司法机关要求</li>
                <li>为提供服务需要（如支付服务提供商）</li>
                <li>保护我们的权利和财产</li>
              </ul>
            </div>

            <div class="policy-section">
              <h3>4. 信息安全</h3>
              <p>我们采取以下措施保护您的信息安全：</p>
              <ul>
                <li>采用HTTPS加密传输协议</li>
                <li>敏感信息采用AES-256加密存储</li>
                <li>实施访问控制和权限管理</li>
                <li>定期进行安全审计和漏洞检测</li>
              </ul>
            </div>

            <div class="policy-section">
              <h3>5. 您的权利</h3>
              <p>您对自己的个人信息享有以下权利：</p>
              <ul>
                <li>访问和查看您的个人信息</li>
                <li>更正或更新您的个人信息</li>
                <li>删除您的个人信息</li>
                <li>撤回您的同意</li>
                <li>投诉和举报</li>
              </ul>
            </div>

            <div class="policy-section">
              <h3>6. Cookie使用</h3>
              <p>我们使用Cookie和类似技术来：</p>
              <ul>
                <li>记住您的登录状态</li>
                <li>保存您的偏好设置</li>
                <li>分析网站流量和使用情况</li>
                <li>提供个性化内容</li>
              </ul>
            </div>

            <div class="policy-section">
              <h3>7. 联系我们</h3>
              <p>如果您对本隐私政策有任何疑问或建议，请通过以下方式联系我们：</p>
              <ul>
                <li>邮箱：privacy@campus.edu.cn</li>
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
import { getPrivacyPolicy, type PrivacyPolicy } from '@campus/common'

// 隐私政策
const privacyPolicy = ref<PrivacyPolicy | null>(null)

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

// 加载隐私政策
const loadPrivacyPolicy = async () => {
  try {
    privacyPolicy.value = await getPrivacyPolicy()
  } catch (error) {
    console.error('加载隐私政策失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadPrivacyPolicy()
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

/* 政策内容 */
.privacy-content {
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.policy-content {
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
.policy-sections {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.policy-section {
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #e91e63;
}

.policy-section h3 {
  font-size: 1.3rem;
  font-weight: bold;
  color: #e91e63;
  margin-bottom: 1rem;
}

.policy-section p {
  font-size: 1rem;
  color: #666;
  line-height: 1.8;
  margin-bottom: 1rem;
}

.policy-section ul {
  margin-left: 2rem;
  margin-bottom: 0;
}

.policy-section li {
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

  .privacy-content {
    padding: 1.5rem;
  }

  .policy-section {
    padding: 1rem;
  }

  .policy-section h3 {
    font-size: 1.1rem;
  }
}
</style>
