<template>
  <div class="privacy-page">
    <NavBar />

    <main class="privacy-main">
      <header class="page-head">
        <div class="head-inner">
          <p class="breadcrumb">首页 / 隐私政策</p>
          <h1 class="page-title">隐私协议</h1>
          <p class="page-subtitle">我们重视您的隐私。滚动阅读时，左侧目录会自动高亮当前章节。</p>
        </div>
      </header>

      <section class="privacy-wrap">
        <div class="privacy-container">
          <!-- 左侧目录导航 -->
          <aside class="toc-sidebar" aria-label="隐私协议目录">
            <h2 class="toc-title">目录</h2>
            <ul class="toc-list">
              <li v-for="s in sections" :key="s.id" class="toc-item">
                <a
                  class="toc-link"
                  :class="{ active: activeSectionId === s.id }"
                  :href="`#${s.id}`"
                  @click.prevent="scrollTo(s.id)"
                >
                  {{ s.label }}
                </a>
              </li>
            </ul>
          </aside>

          <!-- 右侧内容区域 -->
          <div class="privacy-content">
            <div class="meta-bar">
              <div class="meta-item">
                <span class="meta-label">版本</span>
                <span class="meta-value">{{ privacyPolicy?.version || 'v1.0' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">更新时间</span>
                <span class="meta-value">{{ privacyPolicy?.updateTime ? formatDate(privacyPolicy.updateTime) : '2026年1月' }}</span>
              </div>
            </div>

            <h2 class="section-title">隐私协议详情</h2>

            <div class="privacy-article">
              <div class="privacy-section" id="section1">
                <h2>1. 引言</h2>
                <p>
                  欢迎使用上大学Online！我们深知个人信息对您的重要性，并庄严承诺保护您的隐私。本隐私协议（以下简称"本协议"）阐述了上大学Online（以下简称"我们"）如何收集、使用、存储、共享和保护您的个人信息，以及您如何行使相关权利。
                </p>
                <p>请在使用我们的服务前，仔细阅读并理解本协议。当您开始使用上大学Online的服务时，即表示您已充分理解并同意本协议的全部内容。</p>

                <div class="highlight-box">
                  <p>
                    <strong>重要提示：</strong>
                    本协议适用于上大学Online网站、移动应用程序及电脑客户端提供的所有产品和服务。如您不同意本协议的任何内容，应立即停止使用我们的服务。
                  </p>
                </div>
              </div>

              <div class="privacy-section" id="section2">
                <h2>2. 信息收集</h2>
                <p>为了向您提供优质的服务，我们会在合法、正当、必要的原则下收集您的个人信息：</p>

                <h3>2.1 您直接提供的信息</h3>
                <ul>
                  <li><strong>账户信息：</strong> 当您注册上大学Online账户时，我们会收集您的手机号码、电子邮箱地址、用户名和密码。</li>
                  <li><strong>个人资料：</strong> 您在使用服务时可能提供的真实姓名、学校信息、专业、年级、头像等。</li>
                  <li><strong>内容信息：</strong> 您在使用服务过程中创建、上传或分享的内容，如课程表、笔记、作业、交易信息等。</li>
                </ul>

                <h3>2.2 我们自动收集的信息</h3>
                <ul>
                  <li><strong>设备信息：</strong> 包括设备型号、操作系统版本、唯一设备标识符、移动网络信息等。</li>
                  <li><strong>日志信息：</strong> 当您使用我们的服务时，我们会自动收集某些信息并存储在日志文件中，包括IP地址、浏览器类型、访问日期和时间、点击流数据等。</li>
                  <li><strong>位置信息：</strong> 当您使用基于位置的服务时，我们可能会收集和处理有关您实际位置的信息。</li>
                </ul>

                <h3>2.3 从第三方获取的信息</h3>
                <p>我们可能会从关联公司、商业合作伙伴及其他独立第三方来源获取关于您的信息，并依据法律法规的要求及与第三方的约定，在您授权同意范围内使用您的个人信息。</p>
              </div>

              <div class="privacy-section" id="section3">
                <h2>3. 信息使用</h2>
                <p>我们承诺仅在本协议所述目的范围内使用您的个人信息：</p>

                <ul>
                  <li><strong>提供服务：</strong> 使用您提供的信息创建和维护您的账户，提供个性化服务。</li>
                  <li><strong>改善服务：</strong> 分析使用趋势和用户活动，以改善用户体验和优化服务功能。</li>
                  <li><strong>安全保障：</strong> 验证账户身份、检测和防范欺诈行为，保护账户安全。</li>
                  <li><strong>沟通联系：</strong> 向您发送服务通知、技术更新、安全警报和支持消息。</li>
                  <li><strong>个性化体验：</strong> 根据您的偏好和兴趣，提供定制化的内容推荐和服务。</li>
                  <li><strong>合规性：</strong> 遵守法律法规要求，履行我们的法定义务。</li>
                </ul>

                <div class="highlight-box">
                  <p><strong>承诺：</strong> 我们不会将您的个人信息用于本协议未说明的其他商业目的，除非事先获得您的明确同意。</p>
                </div>
              </div>

              <div class="privacy-section" id="section4">
                <h2>4. 信息共享</h2>
                <p>我们高度重视您的个人信息安全，仅在以下情况下共享您的信息：</p>

                <ul>
                  <li><strong>获得您的明确同意：</strong> 在获得您的明确同意后，我们会与其他方共享您的个人信息。</li>
                  <li><strong>法定要求：</strong> 根据法律法规规定、诉讼、仲裁、司法机关或政府机关的要求，我们可能会披露您的个人信息。</li>
                  <li><strong>关联公司：</strong> 我们可能会与我们的关联公司共享您的个人信息，但只会用于本协议声明的目的。</li>
                  <li><strong>服务提供商：</strong> 我们可能会与为我们提供服务的第三方共享信息，但这些服务提供商必须遵守我们的数据保护要求。</li>
                  <li><strong>业务转让：</strong> 如果发生合并、收购或资产出售等交易，您的个人信息可能会作为交易的一部分被转移。</li>
                </ul>

                <p>我们会对信息实行严格的访问控制，确保只有授权人员才能访问您的个人信息，并与所有接触您个人信息的人员签署保密协议。</p>
              </div>

              <div class="privacy-section" id="section5">
                <h2>5. 信息安全</h2>
                <p>我们采用各种安全技术和程序，以防信息的丢失、不当使用、未经授权阅览或披露：</p>

                <ul>
                  <li><strong>数据加密：</strong> 我们对敏感信息进行加密存储和传输，确保数据在传输过程中的安全。</li>
                  <li><strong>访问控制：</strong> 我们对能够访问您个人信息的工作人员采取最小权限原则，并对其进行身份认证和权限审查。</li>
                  <li><strong>安全审计：</strong> 我们定期审查信息收集、存储和处理实践，包括物理安全措施，以防系统遭受未经授权的访问。</li>
                  <li><strong>应急响应：</strong> 我们建立了个人信息安全事件应急响应流程，按照法律法规要求及时向您告知安全事件的基本情况和可能的影响。</li>
                </ul>

                <p>尽管有这些安全措施，但请注意在互联网上不存在"完善的安全措施"，我们会尽力保护您的个人信息安全。</p>
              </div>

              <div class="privacy-section" id="section6">
                <h2>6. 用户权利</h2>
                <p>您对自己的个人信息享有以下权利：</p>

                <ul>
                  <li><strong>访问权：</strong> 您有权访问我们持有的关于您的个人信息。</li>
                  <li><strong>更正权：</strong> 当您发现我们处理的关于您的个人信息有错误时，您有权要求我们做出更正。</li>
                  <li>
                    <strong>删除权：</strong> 在以下情况下，您有权要求我们删除您的个人信息：
                    <ul>
                      <li>处理目的已实现、无法实现或者为实现处理目的不再必要</li>
                      <li>我们停止提供产品或者服务，或者保存期限已届满</li>
                      <li>您撤回了同意</li>
                      <li>我们违反法律、行政法规或者违反约定处理个人信息</li>
                    </ul>
                  </li>
                  <li><strong>撤回同意权：</strong> 您有权通过注销账户、删除信息等方式撤回您之前的授权。</li>
                  <li><strong>注销权：</strong> 您有权注销您的上大学Online账户，我们将在您注销账户后删除您的个人信息或进行匿名化处理。</li>
                </ul>

                <p>如您希望行使上述权利，可以通过"个人中心-隐私设置"或联系客服（support@shangdaxue.com）进行操作。</p>
              </div>

              <div class="privacy-section" id="section7">
                <h2>7. 未成年人保护</h2>
                <p>我们非常重视未成年人的个人信息保护：</p>

                <ul>
                  <li>上大学Online主要面向大学生群体提供服务，如果您是未成年人，请在父母或监护人的指导下使用我们的服务。</li>
                  <li>我们不会在知情的情况下收集未成年人的个人信息，除非法律允许或经父母或监护人同意。</li>
                  <li>如果我们发现自己在未事先获得可证实的父母同意的情况下收集了未成年人的个人信息，则会设法尽快删除相关数据。</li>
                </ul>

                <p>如果您是未成年人的父母或监护人，当您对您所监护的未成年人的个人信息有相关疑问时，请通过本协议列明的联系方式与我们联系。</p>
              </div>

              <div class="privacy-section" id="section8">
                <h2>8. 协议更新</h2>
                <p>我们可能会适时对本协议进行修订：</p>

                <ul>
                  <li>当我们的服务范围、信息处理目的或方式发生变化时，我们可能会更新本协议。</li>
                  <li>当法律法规或监管要求发生变化时，我们可能需要更新本协议以符合新的要求。</li>
                  <li>当我们的所有权结构、组织架构等方面发生重大变化时，我们可能会更新本协议。</li>
                </ul>

                <p>若本协议发生变更，我们会通过网站公告、推送通知、电子邮件等方式通知您。若您继续使用我们的服务，即表示您同意接受修订后的本协议。</p>

                <p>我们会在本页面上发布对本协议所做的任何变更，并更新本协议顶部的"最后更新日期"。</p>
              </div>

              <div class="privacy-section" id="section9">
                <h2>9. 联系我们</h2>
                <p>如果您对本隐私协议有任何疑问、意见或建议，或者希望行使您的个人信息相关权利，请通过以下方式联系我们：</p>

                <ul>
                  <li><strong>客服热线：</strong> 400-123-4567（工作日 9:00-18:00）</li>
                  <li><strong>电子邮件：</strong> privacy@shangdaxue.com</li>
                  <li><strong>邮寄地址：</strong> 北京市海淀区中关村大街科技园区A座1001室 上大学Online隐私保护办公室（邮编：100000）</li>
                </ul>

                <p>一般情况下，我们将在15个工作日内回复您的请求。如您对我们的回复不满意，或认为我们的个人信息处理行为损害了您的合法权益，您还可以向网信、公安、工商等监管部门进行投诉或举报，或向有管辖权的法院提起诉讼。</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import { getPrivacyPolicy, type PrivacyPolicy } from '@campus/common'

// 隐私政策
const privacyPolicy = ref<PrivacyPolicy | null>(null)

const sections = [
  { id: 'section1', label: '1. 引言' },
  { id: 'section2', label: '2. 信息收集' },
  { id: 'section3', label: '3. 信息使用' },
  { id: 'section4', label: '4. 信息共享' },
  { id: 'section5', label: '5. 信息安全' },
  { id: 'section6', label: '6. 用户权利' },
  { id: 'section7', label: '7. 未成年人保护' },
  { id: 'section8', label: '8. 协议更新' },
  { id: 'section9', label: '9. 联系我们' }
]

const activeSectionId = ref<string>('section1')
let observer: IntersectionObserver | null = null

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

const scrollTo = (id: string) => {
  const el = document.getElementById(id)
  if (!el) return
  activeSectionId.value = id
  el.scrollIntoView({ behavior: 'smooth', block: 'start' })
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

  const targets = sections
    .map((s) => document.getElementById(s.id))
    .filter(Boolean) as HTMLElement[]

  if (targets.length === 0) return

  observer = new IntersectionObserver(
    (entries) => {
      const visible = entries
        .filter((e) => e.isIntersecting)
        .sort((a, b) => (a.boundingClientRect.top ?? 0) - (b.boundingClientRect.top ?? 0))
      const first = visible[0]
      const id = (first?.target as HTMLElement | undefined)?.id
      if (id) activeSectionId.value = id
    },
    {
      root: null,
      threshold: [0.12, 0.25, 0.5],
      rootMargin: '-15% 0px -70% 0px'
    }
  )

  for (const el of targets) observer.observe(el)
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
    observer = null
  }
})
</script>

<style scoped>
.privacy-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #fdfbff 0%, #f7f0ff 25%, #fdf3f7 60%, #ffffff 100%);
  color: #111827;
}

.privacy-main {
  flex: 1;
  padding: 26px 0 60px;
}

.page-head {
  max-width: 1200px;
  margin: 0 auto 18px;
  padding: 0 24px;
}

.head-inner {
  border-radius: 20px;
  padding: 20px 22px;
  background: linear-gradient(135deg, rgba(255, 64, 129, 0.06), rgba(63, 81, 181, 0.07));
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.35);
}

.breadcrumb {
  margin: 0 0 6px;
  font-size: 13px;
  color: #9e9eaa;
}

.page-title {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 800;
  color: #233145;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #5f6473;
}

.privacy-wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.privacy-container {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 18px;
  align-items: start;
}

.toc-sidebar {
  position: sticky;
  top: 84px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 14px 12px;
}

.toc-title {
  margin: 0 0 10px;
  font-size: 12px;
  color: #6b7280;
  font-weight: 800;
  letter-spacing: 0.3px;
}

.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.toc-link {
  display: block;
  padding: 10px 10px;
  border-radius: 12px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 650;
  color: #1f2937;
  border: 1px solid transparent;
  transition: background 0.12s ease, border-color 0.12s ease;
}

.toc-link:hover {
  background: rgba(255, 75, 139, 0.06);
}

.toc-link.active {
  background: rgba(255, 75, 139, 0.09);
  border-color: rgba(255, 75, 139, 0.22);
  color: #ff4b8b;
}

.privacy-content {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 18px 18px 10px;
}

.meta-bar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
  margin-bottom: 10px;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(248, 250, 252, 0.8);
  border: 1px solid rgba(148, 163, 184, 0.22);
  font-size: 12px;
  color: #6b7280;
}

.meta-label {
  font-weight: 800;
}

.meta-value {
  color: #111827;
  font-weight: 650;
}

.section-title {
  margin: 6px 0 14px;
  font-size: 18px;
  font-weight: 800;
  color: #111827;
}

.privacy-section {
  scroll-margin-top: 96px;
  padding: 14px 12px;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  background: rgba(255, 255, 255, 0.75);
  margin-bottom: 12px;
}

.privacy-section h2 {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 850;
  color: #111827;
}

.privacy-section h3 {
  margin: 14px 0 8px;
  font-size: 14px;
  font-weight: 800;
  color: #233145;
}

.privacy-section p {
  margin: 0 0 10px;
  font-size: 13px;
  color: #4b5563;
  line-height: 1.85;
}

.privacy-section ul {
  margin: 0 0 10px 18px;
  padding: 0;
  color: #4b5563;
  font-size: 13px;
  line-height: 1.85;
}

.highlight-box {
  border-radius: 14px;
  border: 1px solid rgba(255, 75, 139, 0.22);
  background: rgba(255, 75, 139, 0.06);
  padding: 12px 12px;
}

.highlight-box p {
  margin: 0;
}

@media (max-width: 900px) {
  .privacy-container {
    grid-template-columns: 1fr;
  }

  .toc-sidebar {
    position: static;
    top: auto;
  }
}

@media (max-width: 768px) {
  .privacy-main {
    padding: 18px 0 46px;
  }

  .page-head,
  .privacy-wrap {
    padding: 0 16px;
  }

  .page-title {
    font-size: 24px;
  }
}
</style>
