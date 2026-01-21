<template>
  <div class="terms-page">
    <NavBar />

    <main class="terms-main">
      <header class="page-head">
        <div class="head-inner">
          <p class="breadcrumb">首页 / 服务协议</p>
          <h1 class="page-title">服务条款</h1>
          <p class="page-subtitle">请仔细阅读以下条款内容。滚动阅读时，左侧目录会自动高亮当前章节。</p>
        </div>
      </header>

      <section class="terms-wrap">
        <div class="terms-container">
          <!-- 左侧目录导航 -->
          <aside class="toc-sidebar" aria-label="服务条款目录">
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
          <div class="terms-content">
            <div class="meta-bar">
              <div class="meta-item">
                <span class="meta-label">版本</span>
                <span class="meta-value">{{ serviceAgreement?.version || 'v1.0' }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">更新时间</span>
                <span class="meta-value">{{ serviceAgreement?.updateTime ? formatDate(serviceAgreement.updateTime) : '2026年1月' }}</span>
              </div>
            </div>

            <h2 class="section-title">服务条款详情</h2>

            <div class="terms-article">
              <div class="terms-section" id="section1">
                <h2>1. 服务说明</h2>
                <p>
                  欢迎使用上大学Online！上大学Online（以下简称"本平台"）是由苏州天富数字科技有限公司（以下简称"我们"）开发和运营的在线教育服务平台。本平台通过网站、移动应用程序和电脑客户端为大学生提供课程管理、学习资源、校园社交等服务。
                </p>
                <p>本服务条款（以下简称"本条款"）是您与我们之间就使用本平台服务所订立的协议。请您在使用本平台服务前，仔细阅读并理解本条款的全部内容。</p>

                <div class="highlight-box">
                  <p>
                    <strong>重要提示：</strong>
                    当您完成注册程序或开始使用本平台服务时，即表示您已充分阅读、理解并接受本条款的所有内容。如果您不同意本条款的任何内容，请立即停止使用本平台服务。
                  </p>
                </div>
              </div>

              <div class="terms-section" id="section2">
                <h2>2. 账户注册</h2>
                <p>为使用本平台的服务，您需要注册一个上大学Online账户：</p>

                <h3>2.1 注册资格</h3>
                <ul>
                  <li>您必须是具有完全民事行为能力的自然人，或依法成立并有效存续的法人或其他组织。</li>
                  <li>如果您是未成年人，请在法定监护人的陪同下阅读和判断是否同意本条款，并在监护人的指导下使用本平台服务。</li>
                  <li>您保证在注册时提供的信息真实、准确、完整，并及时更新。</li>
                </ul>

                <h3>2.2 账户安全</h3>
                <ul>
                  <li>您应对您的账户和密码的安全负全部责任，并对通过您的账户进行的所有活动承担责任。</li>
                  <li>您同意在发现任何未经授权的账户使用行为时立即通知我们。</li>
                  <li>我们保留在发现异常使用情况时暂停或终止为您提供服务的权利。</li>
                </ul>

                <h3>2.3 账户注销</h3>
                <p>您可以通过平台提供的注销功能或联系客服申请注销账户。账户注销后，我们将根据相关法律法规处理您的个人信息。</p>
              </div>

              <div class="terms-section" id="section3">
                <h2>3. 用户行为规范</h2>
                <p>在使用本平台服务时，您同意遵守以下行为规范：</p>

                <h3>3.1 禁止行为</h3>
                <p>您不得利用本平台服务从事以下行为：</p>
                <ul>
                  <li>发布、传播违反国家法律法规、社会主义制度、中国共产党领导、国家主权和领土完整等内容；</li>
                  <li>发布、传播淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的内容；</li>
                  <li>发布、传播侮辱或者诽谤他人，侵害他人合法权益的内容；</li>
                  <li>发布、传播虚假、骚扰性、中伤性、辱骂性、恐吓性、伤害性、庸俗或淫秽的信息；</li>
                  <li>从事任何干扰或破坏本平台服务或与本平台服务相连的服务器和网络的活动；</li>
                  <li>未经我们明确授权，使用本平台的名称、商标、标识等；</li>
                  <li>其他违反法律法规、社会公德和公序良俗的行为。</li>
                </ul>

                <h3>3.2 内容责任</h3>
                <p>您应对您在本平台上发布、传播的所有内容承担全部责任。我们有权对您发布的内容进行审核，但不对内容的真实性、准确性、完整性承担任何责任。</p>

                <h3>3.3 违规处理</h3>
                <p>
                  如果我们发现或收到他人举报您违反本条款，我们有权进行独立判断并采取技术手段予以删除、屏蔽或断开链接。同时，我们有权视用户的行为性质，采取包括但不限于暂停、终止部分或全部服务、限制、冻结或终止账户使用、追究法律责任等措施。
                </p>
              </div>

              <div class="terms-section" id="section4">
                <h2>4. 知识产权</h2>
                <p>本平台服务中包含的全部知识产权，以及与本平台服务相关的所有信息内容，均受著作权法、商标法、专利法等法律法规保护。</p>

                <h3>4.1 平台知识产权</h3>
                <ul>
                  <li>我们拥有本平台的著作权、商标权、专利权等所有知识产权；</li>
                  <li>未经我们书面同意，您不得为任何商业或非商业目的自行或许可任何第三方实施、利用、转让上述知识产权；</li>
                  <li>本平台展示的课程内容、学习资料等，其知识产权归内容提供方或我们所有。</li>
                </ul>

                <h3>4.2 用户内容授权</h3>
                <p>您在本平台上传、发布或传输的内容，您授予我们及其关联公司全球范围内、免费、非独家、可再许可的许可，使我们能够使用、托管、存储、复制、修改、创建衍生作品、传播、出版、公开演示、公开展示和分发此类内容。</p>

                <h3>4.3 第三方内容</h3>
                <p>本平台可能包含指向第三方网站或资源的链接。我们不对这些网站或资源的可用性负责，也不认可这些网站或资源上提供的任何内容、产品、服务或其他材料。</p>
              </div>

              <div class="terms-section" id="section5">
                <h2>5. 服务变更与终止</h2>
                <p>我们始终在不断更改和改进我们提供的服务。我们可能会增加或删除功能，也可能暂停或彻底停止某项服务。</p>

                <h3>5.1 服务变更</h3>
                <p>我们可以自行决定随时变更服务内容，或中止、终止向您提供服务。服务变更、中止或终止前，我们将尽可能通过适当方式向您发出通知。</p>

                <h3>5.2 服务终止</h3>
                <p>在下列情况下，我们有权终止向您提供服务：</p>
                <ul>
                  <li>您违反相关法律法规或本条款的规定；</li>
                  <li>按照法律规定或有权机关的要求；</li>
                  <li>出于安全原因或其他必要的情形；</li>
                  <li>本平台停止运营。</li>
                </ul>

                <h3>5.3 终止后果</h3>
                <p>服务终止后，您使用本平台服务的权利立即终止，我们无法继续向您提供任何信息或为您提供任何服务。</p>
              </div>

              <div class="terms-section" id="section6">
                <h2>6. 免责声明</h2>
                <p>您理解并同意，本平台服务是按现状提供的，我们不提供任何形式的保证：</p>

                <ul>
                  <li><strong>服务可用性：</strong> 我们不保证服务不会中断，也不保证服务的及时性、安全性、准确性；</li>
                  <li><strong>内容准确性：</strong> 我们不保证通过本平台服务获得的任何信息内容的准确性、真实性、适用性；</li>
                  <li><strong>用户行为：</strong> 您应对使用本平台服务过程中接触到的内容自行加以判断，并承担因使用内容而引起的所有风险；</li>
                  <li><strong>不可抗力：</strong> 对于因不可抗力或我们不能控制的原因造成的服务中断或其他缺陷，我们不承担任何责任。</li>
                </ul>

                <div class="highlight-box">
                  <p><strong>重要提示：</strong> 您理解并同意，使用本平台服务涉及到互联网服务，可能会受到各个环节不稳定因素的影响，存在一定的风险。</p>
                </div>
              </div>

              <div class="terms-section" id="section7">
                <h2>7. 责任限制</h2>
                <p>在法律允许的最大范围内，我们不对任何间接的、惩罚性的、偶然的、特殊的、后果性的损失承担责任：</p>

                <ul>
                  <li>因使用或无法使用本平台服务而引起的任何损害赔偿；</li>
                  <li>因通过本平台服务购买或获取任何商品、服务、信息等而引起的损害赔偿；</li>
                  <li>未经授权的访问或更改您的传输或数据；</li>
                  <li>任何第三方的声明或行为与本平台服务相关。</li>
                </ul>

                <p>在任何情况下，我们的全部责任，无论是基于合同、侵权（包括过失）或其他原因，均不超过您使用本平台服务而向我们支付的费用总额。</p>
              </div>

              <div class="terms-section" id="section8">
                <h2>8. 法律适用</h2>
                <p>本条款的订立、执行和解释及争议的解决均应适用中华人民共和国法律。</p>

                <h3>8.1 争议解决</h3>
                <p>如双方就本条款内容或其执行发生任何争议，双方应尽量友好协商解决；协商不成时，任何一方均可向被告所在地有管辖权的人民法院提起诉讼。</p>

                <h3>8.2 条款可分性</h3>
                <p>如本条款中的任何条款无论因何种原因完全或部分无效或不具有执行力，本条款的其余条款仍应有效并且有约束力。</p>

                <h3>8.3 标题解释</h3>
                <p>本条款各条的标题仅为方便参考而设，不具法律效力，不能作为对本条款进行解释的依据。</p>
              </div>

              <div class="terms-section" id="section9">
                <h2>9. 联系我们</h2>
                <p>如果您对本服务条款有任何疑问、意见或建议，请通过以下方式联系我们：</p>

                <ul>
                  <li><strong>客服热线：</strong> 400-123-4567（工作日 9:00-18:00）</li>
                  <li><strong>电子邮件：</strong> terms@shangdaxue.com</li>
                  <li><strong>邮寄地址：</strong> 北京市海淀区中关村大街科技园区A座1001室 上大学Online服务条款办公室（邮编：100000）</li>
                </ul>

                <p>我们将尽快审核所涉问题，并在验证您的用户身份后的15个工作日内予以回复。</p>

                <div class="highlight-box">
                  <p>
                    <strong>再次提醒：</strong>
                    在您开始使用上大学Online服务之前，请仔细阅读本服务条款。如果您对本服务条款的任何条款表示异议，您可以选择不使用上大学Online服务；使用则意味着您对本服务条款的完全接受。
                  </p>
                </div>
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
import { getServiceAgreement, type ServiceAgreement } from '@campus/common'

// 服务协议
const serviceAgreement = ref<ServiceAgreement | null>(null)

const sections = [
  { id: 'section1', label: '1. 服务说明' },
  { id: 'section2', label: '2. 账户注册' },
  { id: 'section3', label: '3. 用户行为规范' },
  { id: 'section4', label: '4. 知识产权' },
  { id: 'section5', label: '5. 服务变更与终止' },
  { id: 'section6', label: '6. 免责声明' },
  { id: 'section7', label: '7. 责任限制' },
  { id: 'section8', label: '8. 法律适用' },
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
.terms-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #fdfbff 0%, #f7f0ff 25%, #fdf3f7 60%, #ffffff 100%);
  color: #111827;
}

.terms-main {
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

.terms-wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.terms-container {
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

.terms-content {
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

.terms-article {
  padding-bottom: 8px;
}

.terms-section {
  scroll-margin-top: 96px;
  padding: 14px 12px;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  background: rgba(255, 255, 255, 0.75);
  margin-bottom: 12px;
}

.terms-section h2 {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 850;
  color: #111827;
}

.terms-section h3 {
  margin: 14px 0 8px;
  font-size: 14px;
  font-weight: 800;
  color: #233145;
}

.terms-section p {
  margin: 0 0 10px;
  font-size: 13px;
  color: #4b5563;
  line-height: 1.85;
}

.terms-section ul {
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
  .terms-container {
    grid-template-columns: 1fr;
  }

  .toc-sidebar {
    position: static;
    top: auto;
  }
}

@media (max-width: 768px) {
  .terms-main {
    padding: 18px 0 46px;
  }

  .page-head,
  .terms-wrap {
    padding: 0 16px;
  }

  .page-title {
    font-size: 24px;
  }
}
</style>
