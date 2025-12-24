<template>
  <div class="common-page security-page">
    <!-- 全局导航栏：保持与站点其他页面一致的头部导航 -->
    <NavBar />

    <!-- 页面主体内容：包含标题、安全卡片、详细说明、使用小贴士等 -->
    <div class="page-content">
      <div class="content-container">
        <!-- 页面标题区域：告诉用户这是安全相关的重要页面 -->
        <div class="page-header">
          <h1 class="page-title">安全保障</h1>
          <p class="page-subtitle">我们通过多重技术与管理手段，全方位保护您的个人信息和数据安全</p>
        </div>

        <!-- 加载状态：接口请求过程中展示统一的加载动画 -->
        <div v-if="isLoading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载中，请稍候...</p>
        </div>

        <!-- 安全保障内容：有数据（接口或本地兜底）时展示主体内容 -->
        <div class="security-content" v-else-if="securityInfo">
          <!-- 如果接口请求出现问题，这里给出友好的文字提示，但仍然展示页面主体 -->
          <div v-if="loadError" class="load-error">
            <p class="load-error-text">{{ loadError }}</p>
          </div>

          <!-- 安全保障四大维度卡片：快速让用户理解平台做了哪些安全工作 -->
          <div class="security-section">
            <!-- 卡片一：数据加密传输 -->
            <div class="security-card">
              <div class="card-icon"><i class="fas fa-lock"></i></div>
              <h3 class="card-title">数据加密传输</h3>
              <p class="card-desc">{{ securityInfo.encryption }}</p>
            </div>

            <!-- 卡片二：定位权限分级 -->
            <div class="security-card">
              <div class="card-icon"><i class="fas fa-map-marker-alt"></i></div>
              <h3 class="card-title">定位权限分级</h3>
              <p class="card-desc">{{ securityInfo.locationPermission }}</p>
            </div>

            <!-- 卡片三：交易记录加密 -->
            <div class="security-card">
              <div class="card-icon"><i class="fas fa-credit-card"></i></div>
              <h3 class="card-title">交易记录加密</h3>
              <p class="card-desc">{{ securityInfo.transactionEncryption }}</p>
            </div>

            <!-- 卡片四：数据保护 -->
            <div class="security-card">
              <div class="card-icon"><i class="fas fa-shield-alt"></i></div>
              <h3 class="card-title">数据保护</h3>
              <p class="card-desc">{{ securityInfo.dataProtection }}</p>
            </div>
          </div>

          <!-- 详细说明区域：用更通俗的语言解释平台的安全机制 -->
          <div class="security-details">
            <h2 class="details-title">安全机制说明</h2>
            <div class="details-content">
              <div class="detail-item">
                <h4>1. 数据加密</h4>
                <p>
                  平台全站默认启用
                  <strong>HTTPS 加密传输协议</strong>
                  ，在您浏览、登录、发布信息和进行交易时，数据都会被加密后再在网络中传输，防止中途被窃取或篡改。
                  对于密码、交易记录等敏感信息，我们采用
                  <strong>AES-256 等业界主流加密算法</strong>
                  进行存储，只有经过授权的系统才能解密访问。
                </p>
              </div>
              <div class="detail-item">
                <h4>2. 权限管理</h4>
                <p>
                  平台采用
                  <strong>分级权限管理机制</strong>
                  ，不同身份（普通学生、管理员等）拥有不同的操作权限。对于定位等敏感权限，平台坚持
                  <strong>按需申请、最小授权</strong>
                  的原则，不会在后台长期跟踪您的位置，也不会在无关功能中调用定位。
                </p>
              </div>
              <div class="detail-item">
                <h4>3. 交易安全</h4>
                <p>
                  所有与资金或交易相关的记录，都会被
                  <strong>加密存储并进行访问控制</strong>
                  。我们支持对异常交易进行风控校验，必要时可配合学校或相关部门进行核查，保障买卖双方的交易安全。
                </p>
              </div>
              <div class="detail-item">
                <h4>4. 隐私保护</h4>
                <p>
                  平台严格遵守国家相关法律法规，坚持
                  <strong>“能不收就不收、能少收就少收”</strong>
                  的原则，只在提供服务所必需的范围内收集和使用您的个人信息。除非获得您的明确授权或法律法规要求，我们不会向任何无关第三方泄露您的个人信息。
                </p>
              </div>
            </div>
          </div>

          <!-- 安全使用小贴士：从用户角度给出简单易懂的安全建议 -->
          <div class="security-tips">
            <h2 class="tips-title">安全使用小贴士</h2>
            <ul class="tips-list">
              <li class="tips-item">请不要将账号和密码告知他人，尽量避免在公共电脑上勾选“记住密码”。</li>
              <li class="tips-item">发现异常登录或可疑交易时，请立即修改密码，并通过平台反馈渠道联系我们。</li>
              <li class="tips-item">发布二手或兼职信息时，请避免直接暴露身份证号、家庭住址等高敏感隐私信息。</li>
              <li class="tips-item">线下见面交易时，请尽量选择公共场所，并告知同学或室友，注意人身和财物安全。</li>
            </ul>
          </div>
        </div>

        <!-- 兜底错误状态：接口失败且没有任何数据时展示，避免页面一直卡在“加载中” -->
        <div v-else class="error-state">
          <h2 class="error-title">暂时无法获取安全保障信息</h2>
          <p class="error-desc">
            可能是网络波动或服务器维护中。您可以稍后点击下方按钮重新加载，以下为本平台的基础安全说明摘要：
          </p>
          <ul class="error-list">
            <li>平台默认启用 HTTPS 加密传输，防止信息在网络传输过程中被窃取。</li>
            <li>重要数据采用加密存储，并通过权限控制限制访问范围。</li>
            <li>我们不会将您的个人信息出售或泄露给无关第三方。</li>
          </ul>
          <button type="button" class="reload-button" @click="loadSecurityInfo">
            重新尝试加载安全说明
          </button>
        </div>
      </div>
    </div>

    <!-- 页脚：与其他页面保持一致的全局页脚 -->
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
// 引入 Vue 的响应式工具和生命周期钩子
import { ref, onMounted } from 'vue'
// 引入站点统一的导航栏组件
import NavBar from '@/components/common/NavBar.vue'
// 引入站点统一的页脚组件
import AppFooter from '@/components/common/AppFooter.vue'
// 引入公共 SDK 中的安全保障接口定义
import { getSecurityInfo, type SecurityInfo } from '@campus/common'

// 安全保障信息（优先展示后端接口返回的数据）
const securityInfo = ref<SecurityInfo | null>(null)

// 是否处于加载中状态，用于控制“加载中”动画的显示与隐藏
const isLoading = ref<boolean>(true)

// 加载错误的友好提示文案（例如网络异常、接口异常等）
const loadError = ref<string | null>(null)

// 本地兜底的安全说明，当接口不可用时仍然可以展示给用户看的基础内容
const fallbackSecurityInfo: SecurityInfo = {
  encryption: '平台全站启用 HTTPS 加密传输，敏感数据采用 AES-256 等主流算法加密存储。',
  locationPermission: '仅在提供必要服务时按需申请定位权限，不会在后台长期跟踪您的位置信息。',
  transactionEncryption: '交易记录采用加密存储并严格控制访问权限，仅用于账单查询和风险审计。',
  dataProtection: '遵循最小必要原则存储数据，定期进行安全审计和漏洞排查，防止信息泄露。'
}

// 加载安全保障信息：优先请求后端接口，失败时使用本地兜底数据
const loadSecurityInfo = async () => {
  // 进入加载状态，显示“加载中”动画
  isLoading.value = true
  // 每次重新加载前先清空上一次的错误提示
  loadError.value = null

  try {
    // 调用公共 SDK 提供的接口，请求后端安全保障说明
    const result = await getSecurityInfo()

    // 正常情况下 result 会是一个包含四个字符串字段的对象
    if (result && typeof result === 'object') {
      securityInfo.value = result
    } else {
      // 如果返回格式异常（极少出现），使用本地兜底数据，避免页面没有内容
      console.warn('安全保障接口返回格式异常，已启用本地兜底数据')
      securityInfo.value = fallbackSecurityInfo
      loadError.value = '暂时无法从服务器获取最新安全说明，当前展示的是本地预置的安全说明内容。'
    }
  } catch (error) {
    // 捕获网络错误或后端报错，打印日志方便调试
    console.error('加载安全保障信息失败:', error)
    // 使用本地兜底数据，保证用户能看到完整的安全说明
    securityInfo.value = fallbackSecurityInfo
    // 给用户一个温和的错误提示，而不是一直停留在“加载中”
    loadError.value = '网络或服务器异常，已为您展示本地预置的安全说明，稍后可尝试重新加载。'
  } finally {
    // 无论成功还是失败，都要结束加载状态，隐藏“加载中”动画
    isLoading.value = false
  }
}

// 组件挂载完成后自动加载一次安全保障信息
onMounted(() => {
  loadSecurityInfo()
})
</script>

<style scoped>
/* 页面整体容器：垂直布局，保证页脚在底部 */
.common-page {
  min-height: 100vh; /* 页面最小高度占满整个可视区域 */
  display: flex; /* 启用弹性布局 */
  flex-direction: column; /* 垂直方向排列：导航栏 / 内容 / 页脚 */
}

/* 页面主体区域：用于承载安全保障的主要内容 */
.page-content {
  flex: 1; /* 占据除导航栏和页脚外的所有空间 */
  padding: 2rem 0; /* 上下留出一定内边距，让内容不贴边 */
  background: #f8f9fa; /* 与其他说明类页面统一的浅灰背景色 */
}

/* 内容容器：控制内容最大宽度并居中显示 */
.content-container {
  max-width: 1200px; /* PC 端最大宽度 */
  margin: 0 auto; /* 水平居中 */
  padding: 0 2rem; /* 左右留白，避免贴边 */
}

/* 页面标题区域：居中显示标题和副标题 */
.page-header {
  text-align: center; /* 文本居中 */
  margin-bottom: 3rem; /* 标题和后续内容之间留出距离 */
  padding-top: 2rem; /* 顶部稍微留白，显得更舒展 */
}

/* 主标题样式：字号较大，突出主题 */
.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 1rem;
}

/* 副标题样式：说明页面的核心承诺 */
.page-subtitle {
  font-size: 1.1rem;
  color: #666;
}

/* 安全内容容器：包裹卡片、详细说明和小贴士 */
.security-content {
  margin-top: 2rem;
}

/* 四大安全维度卡片区域：使用网格布局，适配不同屏幕宽度 */
.security-section {
  display: grid; /* 使用网格布局方便自适应多列 */
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); /* 每列最小 280px，最多均分一行 */
  gap: 2rem; /* 卡片之间的间距 */
  margin-bottom: 4rem; /* 与下方详细说明区拉开距离 */
}

/* 单个安全卡片：白底卡片 + 阴影效果 */
.security-card {
  background: #fff; /* 白色背景 */
  border-radius: 12px; /* 圆角卡片 */
  padding: 2rem; /* 内边距让内容更加舒适 */
  text-align: center; /* 内容居中 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); /* 轻微阴影提升层次感 */
  transition: transform 0.3s, box-shadow 0.3s; /* 悬停时的动效 */
}

/* 卡片悬停效果：轻微上移和加深阴影 */
.security-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

/* 卡片图标：使用统一主题色的 Font Awesome 图标 */
.card-icon {
  font-size: 3rem; /* 图标稍大一些，视觉上更有安全感 */
  margin-bottom: 1rem;
  color: #e91e63; /* 与站点主色保持一致的粉色调 */
}

/* 确保图标本身作为一个独立内联块存在 */
.card-icon i {
  display: inline-block;
}

/* 卡片标题：安全维度名称 */
.card-title {
  font-size: 1.3rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 1rem;
}

/* 卡片描述文字：对该安全维度进行简要说明 */
.card-desc {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.6;
}

/* 详细说明整体区域：用于放更长篇的文字说明 */
.security-details {
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 3rem; /* 与下方小贴士区留出一定间距 */
}

/* 详细说明标题：位于卡片下方的章节标题 */
.details-title {
  font-size: 1.8rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 2rem;
  text-align: center;
}

/* 详细说明内容：竖向排列多条说明项 */
.details-content {
  display: flex;
  flex-direction: column;
  gap: 2rem; /* 每一条说明之间的间距 */
}

/* 单条详细说明：左侧彩色边框强调重点 */
.detail-item {
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #e91e63; /* 左侧主色边线，增强视觉引导 */
}

/* 详细说明小标题：序号 + 维度名称 */
.detail-item h4 {
  font-size: 1.2rem;
  font-weight: bold;
  color: #e91e63;
  margin-bottom: 0.8rem;
}

/* 详细说明正文：行距稍大，阅读体验更好 */
.detail-item p {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.8;
}

/* 接口错误提示区域：在安全内容顶部显示一条温和的提示 */
.load-error {
  margin-bottom: 1.5rem;
  padding: 1rem 1.25rem;
  border-radius: 8px;
  background: #fff3f6; /* 浅粉色背景，提示但不刺眼 */
  border: 1px solid #f8bbd0; /* 边框颜色略深，形成层次 */
}

/* 错误提示文字样式：稍小号字体的说明文字 */
.load-error-text {
  font-size: 0.9rem;
  color: #c2185b;
}

/* 安全使用小贴士整体区域：卡片下方的一块温馨提示区域 */
.security-tips {
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 小贴士标题样式 */
.tips-title {
  font-size: 1.6rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 1.5rem;
  text-align: center;
}

/* 小贴士列表：竖向排列多条建议 */
.tips-list {
  list-style: none; /* 取消默认项目符号 */
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.75rem; /* 每条建议之间的间距 */
}

/* 单条小贴士文字 */
.tips-item {
  position: relative;
  padding-left: 1.5rem; /* 为自定义圆点留出空间 */
  font-size: 0.95rem;
  color: #555;
  line-height: 1.8;
}

/* 小贴士前面的自定义圆点 */
.tips-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0.8rem;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #e91e63;
}

/* 加载状态容器：居中显示加载动画和提示文字 */
.loading-state {
  text-align: center;
  padding: 4rem 0;
}

/* 加载动画：简单的圆形旋转效果 */
.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #e91e63;
  border-radius: 50%;
  animation: spin 1s linear infinite; /* 持续旋转 */
  margin: 0 auto 1rem;
}

/* 兜底错误区域：接口失败且拿不到任何数据时展示的说明块 */
.error-state {
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: left;
}

/* 兜底错误标题：提醒用户当前是异常情况 */
.error-title {
  font-size: 1.6rem;
  font-weight: bold;
  color: #d32f2f;
  margin-bottom: 1rem;
}

/* 兜底错误描述：解释可能的原因 */
.error-desc {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.8;
  margin-bottom: 1rem;
}

/* 兜底说明列表：简要列出几条基础安全承诺 */
.error-list {
  margin: 0 0 1.5rem 1.25rem;
  padding: 0;
  color: #555;
  font-size: 0.95rem;
  line-height: 1.8;
}

/* 重新加载按钮：让用户可以手动重试接口请求 */
.reload-button {
  display: inline-block;
  padding: 0.6rem 1.4rem;
  border-radius: 999px; /* 胶囊形按钮 */
  border: none;
  background: linear-gradient(135deg, #e91e63 0%, #c2185b 100%);
  color: #fff;
  font-size: 0.95rem;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
}

/* 按钮悬停和按下的状态反馈 */
.reload-button:hover {
  box-shadow: 0 3px 10px rgba(233, 30, 99, 0.35);
  transform: translateY(-1px);
}

.reload-button:active {
  transform: translateY(0);
  box-shadow: 0 1px 5px rgba(233, 30, 99, 0.25);
}

/* 简单的旋转动画定义，供加载动画使用 */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 响应式设计：在中小屏设备上优化布局和字号 */
@media (max-width: 768px) {
  .content-container {
    padding: 0 1rem; /* 移动端左右留白稍微缩小一些 */
  }

  .page-title {
    font-size: 2rem;
  }

  .page-subtitle {
    font-size: 1rem;
  }

  .security-section {
    grid-template-columns: 1fr; /* 小屏下改为单列展示卡片 */
    gap: 1.5rem;
  }

  .security-details {
    padding: 1.5rem;
  }

  .details-title {
    font-size: 1.5rem;
  }

  .security-tips {
    padding: 1.5rem;
  }
}
</style>
