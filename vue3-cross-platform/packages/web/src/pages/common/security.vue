<template>
  <div class="security-page">
    <NavBar />

    <main class="security-main">
      <section class="hero">
        <div class="hero-inner">
          <div class="hero-title-row">
            <div>
              <p class="hero-breadcrumb">首页 / 安全保障</p>
              <h1 class="hero-title">安全保障</h1>
              <p class="hero-subtitle">Windows/Fluent 风格安全中心：可搜索、可跳转、可快速定位</p>
            </div>
            <div class="hero-badge">
              <i class="fas fa-shield-alt"></i>
              <span>Security Center</span>
            </div>
          </div>

          <div class="search-area">
            <div class="searchbar" :class="{ focused: isSearchFocused }">
              <i class="fas fa-search search-icon" aria-hidden="true"></i>
              <input
                v-model="searchKeyword"
                class="search-input"
                type="text"
                placeholder="搜索：密码、手机、邮箱、两步验证、加密、隐私…"
                @focus="handleSearchFocus"
                @blur="handleSearchBlur"
                @keydown.enter.prevent="jumpFirstResult"
              />
              <button v-if="searchKeyword" class="search-clear" type="button" @click="clearSearch" aria-label="清空搜索">
                <i class="fas fa-times"></i>
              </button>
            </div>

            <div v-if="showSuggestions" class="suggestions">
              <div class="suggestions-header">
                <span>搜索结果</span>
                <span class="suggestions-count">{{ filteredItems.length }}</span>
              </div>

              <button
                v-for="item in filteredItems"
                :key="item.id"
                class="suggestion-item"
                type="button"
                @mousedown.prevent="scrollToTarget(item.id, item.categoryId)"
              >
                <div class="suggestion-left">
                  <div class="suggestion-title">{{ item.title }}</div>
                  <div class="suggestion-desc">{{ item.description }}</div>
                </div>
                <div class="suggestion-right">
                  <span class="suggestion-tag">{{ categoryNameMap[item.categoryId] }}</span>
                  <i class="fas fa-chevron-right" aria-hidden="true"></i>
                </div>
              </button>

              <div v-if="searchKeyword && filteredItems.length === 0" class="suggestion-empty">
                未找到相关内容，试试更短的关键词（例如“加密”“两步验证”）。
              </div>
            </div>
          </div>
        </div>
      </section>

      <section v-if="isLoading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中，请稍候...</p>
      </section>

      <section v-else class="shell">
        <aside class="sidebar" aria-label="安全保障分类">
          <div class="sidebar-title">分类</div>
          <nav class="nav-list">
            <button
              v-for="cat in categories"
              :key="cat.id"
              class="nav-item"
              :class="{ active: activeCategoryId === cat.id }"
              type="button"
              @click="scrollToCategory(cat.id)"
            >
              <i :class="['fas', cat.icon]" aria-hidden="true"></i>
              <span class="nav-label">{{ cat.name }}</span>
              <i class="fas fa-chevron-right nav-arrow" aria-hidden="true"></i>
            </button>
          </nav>
        </aside>

        <div class="content">
          <!-- 账号安全 -->
          <section class="group" id="cat-account">
            <div class="group-header">
              <h2 class="group-title">账号安全</h2>
              <p class="group-desc">密码、验证与登录相关设置。</p>
            </div>

            <div class="group-card">
              <div id="setting-password" class="row" :class="{ highlight: highlightId === 'setting-password' }">
                <div class="row-left">
                  <div class="row-icon"><i class="fas fa-key"></i></div>
                  <div class="row-text">
                    <div class="row-title">登录密码</div>
                    <div class="row-desc">定期修改密码可提高账号安全性</div>
                  </div>
                </div>
                <div class="row-right">
                  <button class="action-btn" type="button" @click="showChangePassword = true">修改</button>
                </div>
              </div>

              <div id="setting-twofactor" class="row" :class="{ highlight: highlightId === 'setting-twofactor' }">
                <div class="row-left">
                  <div class="row-icon"><i class="fas fa-shield-alt"></i></div>
                  <div class="row-text">
                    <div class="row-title">两步验证</div>
                    <div class="row-desc">
                      {{ userSecurityInfo.twoFactorEnabled ? '已开启，账号更安全' : '未开启，建议开启以增强安全性' }}
                    </div>
                  </div>
                </div>
                <div class="row-right">
                  <label class="switch" title="两步验证">
                    <input type="checkbox" v-model="userSecurityInfo.twoFactorEnabled" @change="toggleTwoFactor">
                    <span class="slider"></span>
                  </label>
                </div>
              </div>
            </div>
          </section>

          <!-- 绑定与验证 -->
          <section class="group" id="cat-bindings">
            <div class="group-header">
              <h2 class="group-title">绑定与验证</h2>
              <p class="group-desc">手机号/邮箱用于验证码、通知与找回密码。</p>
            </div>

            <div class="group-card">
              <div id="setting-phone" class="row" :class="{ highlight: highlightId === 'setting-phone' }">
                <div class="row-left">
                  <div class="row-icon"><i class="fas fa-mobile-alt"></i></div>
                  <div class="row-text">
                    <div class="row-title">绑定手机</div>
                    <div class="row-desc">
                      {{ userSecurityInfo.phone ? `已绑定：${maskPhone(userSecurityInfo.phone)}` : '未绑定手机号' }}
                    </div>
                  </div>
                </div>
                <div class="row-right">
                  <button class="action-btn" type="button" @click="showBindPhone = true">
                    {{ userSecurityInfo.phone ? '更换' : '绑定' }}
                  </button>
                </div>
              </div>

              <div id="setting-email" class="row" :class="{ highlight: highlightId === 'setting-email' }">
                <div class="row-left">
                  <div class="row-icon"><i class="fas fa-envelope"></i></div>
                  <div class="row-text">
                    <div class="row-title">绑定邮箱</div>
                    <div class="row-desc">
                      {{ userSecurityInfo.email ? `已绑定：${maskEmail(userSecurityInfo.email)}` : '未绑定邮箱' }}
                    </div>
                  </div>
                </div>
                <div class="row-right">
                  <button class="action-btn" type="button" @click="showBindEmail = true">
                    {{ userSecurityInfo.email ? '更换' : '绑定' }}
                  </button>
                </div>
              </div>
            </div>
          </section>

          <!-- 平台安全保障 -->
          <section class="group" id="cat-platform">
            <div class="group-header">
              <h2 class="group-title">平台安全保障</h2>
              <p class="group-desc">我们如何保护你的数据与账户安全。</p>
            </div>

            <div v-if="loadError" class="load-error">
              <p class="load-error-text">{{ loadError }}</p>
            </div>

            <div v-if="securityInfo" class="grid">
              <div id="card-encryption" class="info-card" :class="{ highlight: highlightId === 'card-encryption' }">
                <div class="info-icon"><i class="fas fa-lock"></i></div>
                <div class="info-title">数据加密传输</div>
                <div class="info-desc">{{ securityInfo.encryption }}</div>
              </div>
              <div id="card-location" class="info-card" :class="{ highlight: highlightId === 'card-location' }">
                <div class="info-icon"><i class="fas fa-map-marker-alt"></i></div>
                <div class="info-title">定位权限分级</div>
                <div class="info-desc">{{ securityInfo.locationPermission }}</div>
              </div>
              <div id="card-transaction" class="info-card" :class="{ highlight: highlightId === 'card-transaction' }">
                <div class="info-icon"><i class="fas fa-credit-card"></i></div>
                <div class="info-title">交易记录加密</div>
                <div class="info-desc">{{ securityInfo.transactionEncryption }}</div>
              </div>
              <div id="card-protection" class="info-card" :class="{ highlight: highlightId === 'card-protection' }">
                <div class="info-icon"><i class="fas fa-shield-alt"></i></div>
                <div class="info-title">数据保护</div>
                <div class="info-desc">{{ securityInfo.dataProtection }}</div>
              </div>
            </div>

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
          </section>

          <!-- 安全机制说明 -->
          <section class="group" id="cat-mechanism">
            <div class="group-header">
              <h2 class="group-title">安全机制说明</h2>
              <p class="group-desc">我们采用的关键安全策略与实践。</p>
            </div>

            <div class="article">
              <div id="mechanism-encryption" class="article-item" :class="{ highlight: highlightId === 'mechanism-encryption' }">
                <h3>1. 数据加密</h3>
                <p>
                  平台全站默认启用 <strong>HTTPS 加密传输协议</strong>，在您浏览、登录、发布信息和进行交易时，数据都会被加密后再在网络中传输，防止中途被窃取或篡改。
                  对于密码、交易记录等敏感信息，我们采用 <strong>AES-256 等业界主流加密算法</strong> 进行存储，只有经过授权的系统才能解密访问。
                </p>
              </div>
              <div id="mechanism-permission" class="article-item" :class="{ highlight: highlightId === 'mechanism-permission' }">
                <h3>2. 权限管理</h3>
                <p>
                  平台采用 <strong>分级权限管理机制</strong>，不同身份拥有不同的操作权限。对于定位等敏感权限，平台坚持 <strong>按需申请、最小授权</strong> 的原则，不会在后台长期跟踪您的位置，也不会在无关功能中调用定位。
                </p>
              </div>
              <div id="mechanism-transaction" class="article-item" :class="{ highlight: highlightId === 'mechanism-transaction' }">
                <h3>3. 交易安全</h3>
                <p>
                  所有与资金或交易相关的记录，都会被 <strong>加密存储并进行访问控制</strong>。我们支持对异常交易进行风控校验，必要时可配合学校或相关部门进行核查，保障买卖双方的交易安全。
                </p>
              </div>
              <div id="mechanism-privacy" class="article-item" :class="{ highlight: highlightId === 'mechanism-privacy' }">
                <h3>4. 隐私保护</h3>
                <p>
                  平台严格遵守国家相关法律法规，坚持 <strong>"能不收就不收、能少收就少收"</strong> 的原则，只在提供服务所必需的范围内收集和使用您的个人信息。
                  除非获得您的明确授权或法律法规要求，我们不会向任何无关第三方泄露您的个人信息。
                </p>
              </div>
            </div>
          </section>

          <!-- 小贴士 -->
          <section class="group" id="cat-tips">
            <div class="group-header">
              <h2 class="group-title">安全使用小贴士</h2>
              <p class="group-desc">一些简单习惯，让账号更安全。</p>
            </div>

            <div id="tips-list" class="tips-card" :class="{ highlight: highlightId === 'tips-list' }">
              <ul class="tips-list">
                <li class="tips-item">请不要将账号和密码告知他人，尽量避免在公共电脑上勾选"记住密码"。</li>
                <li class="tips-item">发现异常登录或可疑交易时，请立即修改密码，并通过平台反馈渠道联系我们。</li>
                <li class="tips-item">发布二手或兼职信息时，请避免直接暴露身份证号、家庭住址等高敏感隐私信息。</li>
                <li class="tips-item">线下见面交易时，请尽量选择公共场所，并告知同学或室友，注意人身和财物安全。</li>
              </ul>
            </div>
          </section>
        </div>
      </section>
    </main>

    <AppFooter />

    <!-- 修改密码弹窗 -->
    <div v-if="showChangePassword" class="modal-overlay" @click="showChangePassword = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>修改密码</h3>
          <button class="modal-close" @click="showChangePassword = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>当前密码</label>
            <input type="password" v-model="passwordForm.oldPassword" placeholder="请输入当前密码">
          </div>
          <div class="form-group">
            <label>新密码</label>
            <input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码（至少8位）">
          </div>
          <div class="form-group">
            <label>确认新密码</label>
            <input type="password" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showChangePassword = false">取消</button>
          <button class="btn-confirm" @click="handleChangePassword">确认修改</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
// 引入站点统一的导航栏组件
import NavBar from '@/components/common/NavBar.vue'
// 引入站点统一的页脚组件
import AppFooter from '@/components/common/AppFooter.vue'
// 引入公共 SDK 中的安全保障接口定义
import { getSecurityInfo, type SecurityInfo } from '@campus/common'

// 安全保障信息（优先展示后端接口返回的数据）
const securityInfo = ref<SecurityInfo | null>(null)

// 是否处于加载中状态，用于控制"加载中"动画的显示与隐藏
const isLoading = ref<boolean>(true)

// 加载错误的友好提示文案（例如网络异常、接口异常等）
const loadError = ref<string | null>(null)

// 用户安全信息（模拟数据，实际应从后端获取）
const userSecurityInfo = ref({
  phone: '138****8888', // 示例：已绑定手机
  email: 'user@example.com', // 示例：已绑定邮箱
  twoFactorEnabled: false // 两步验证状态
})

// 弹窗显示状态
const showChangePassword = ref(false)
const showBindPhone = ref(false)
const showBindEmail = ref(false)
const showDeviceList = ref(false)
const showLoginHistory = ref(false)
const showPrivacySettings = ref(false)

// 修改密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

type CategoryId = 'account' | 'bindings' | 'platform' | 'mechanism' | 'tips'

type SearchItem = {
  id: string
  title: string
  description: string
  keywords: string[]
  categoryId: CategoryId
}

const categories: Array<{ id: CategoryId; name: string; icon: string }> = [
  { id: 'account', name: '账号安全', icon: 'fa-user-shield' },
  { id: 'bindings', name: '绑定与验证', icon: 'fa-link' },
  { id: 'platform', name: '平台保障', icon: 'fa-shield-alt' },
  { id: 'mechanism', name: '安全机制', icon: 'fa-list-check' },
  { id: 'tips', name: '小贴士', icon: 'fa-lightbulb' }
]

const categoryNameMap = categories.reduce<Record<string, string>>((acc, cur) => {
  acc[cur.id] = cur.name
  return acc
}, {})

const activeCategoryId = ref<CategoryId>('account')
const highlightId = ref<string | null>(null)

const searchKeyword = ref('')
const isSearchFocused = ref(false)
const showSuggestions = computed(() => isSearchFocused.value && searchKeyword.value.trim().length > 0)

const searchableItems = computed<SearchItem[]>(() => [
  {
    id: 'setting-password',
    title: '登录密码',
    description: '修改密码，提高账号安全性',
    keywords: ['密码', '登录密码', '修改密码', '强密码'],
    categoryId: 'account'
  },
  {
    id: 'setting-twofactor',
    title: '两步验证',
    description: '开启后登录需要额外验证码',
    keywords: ['两步验证', '2FA', '验证码'],
    categoryId: 'account'
  },
  {
    id: 'setting-phone',
    title: '绑定手机',
    description: '用于验证码登录、找回密码',
    keywords: ['手机', '手机号', '绑定', '验证码'],
    categoryId: 'bindings'
  },
  {
    id: 'setting-email',
    title: '绑定邮箱',
    description: '用于安全通知、找回密码',
    keywords: ['邮箱', 'email', '绑定'],
    categoryId: 'bindings'
  },
  {
    id: 'card-encryption',
    title: '数据加密传输',
    description: 'HTTPS + 敏感数据加密存储',
    keywords: ['加密', 'HTTPS', 'AES', '数据'],
    categoryId: 'platform'
  },
  {
    id: 'card-location',
    title: '定位权限分级',
    description: '按需申请、最小授权',
    keywords: ['定位', '权限', '授权'],
    categoryId: 'platform'
  },
  {
    id: 'card-transaction',
    title: '交易记录加密',
    description: '交易数据加密与风控审计',
    keywords: ['交易', '记录', '加密', '风控'],
    categoryId: 'platform'
  },
  {
    id: 'card-protection',
    title: '数据保护',
    description: '访问控制 + 安全审计',
    keywords: ['数据保护', '审计', '权限控制'],
    categoryId: 'platform'
  },
  {
    id: 'mechanism-encryption',
    title: '安全机制：数据加密',
    description: '传输加密与敏感信息加密存储',
    keywords: ['安全机制', '数据加密', 'HTTPS', 'AES'],
    categoryId: 'mechanism'
  },
  {
    id: 'mechanism-permission',
    title: '安全机制：权限管理',
    description: '分级权限、最小授权原则',
    keywords: ['权限', '最小授权', '分级'],
    categoryId: 'mechanism'
  },
  {
    id: 'mechanism-transaction',
    title: '安全机制：交易安全',
    description: '异常交易风控与记录保护',
    keywords: ['交易安全', '风控', '审计'],
    categoryId: 'mechanism'
  },
  {
    id: 'mechanism-privacy',
    title: '安全机制：隐私保护',
    description: '最小必要原则与合规',
    keywords: ['隐私', '合规', '最小必要'],
    categoryId: 'mechanism'
  },
  {
    id: 'tips-list',
    title: '安全使用小贴士',
    description: '账号与交易的日常安全建议',
    keywords: ['小贴士', '建议', '安全'],
    categoryId: 'tips'
  }
])

const filteredItems = computed(() => {
  const q = searchKeyword.value.trim().toLowerCase()
  if (!q) return []
  return searchableItems.value.filter((s) => {
    const hay = [s.title, s.description, ...s.keywords].join(' ').toLowerCase()
    return hay.includes(q)
  })
})

const clearSearch = () => {
  searchKeyword.value = ''
}

const handleSearchFocus = () => {
  isSearchFocused.value = true
}

const handleSearchBlur = () => {
  window.setTimeout(() => {
    isSearchFocused.value = false
  }, 120)
}

const flashHighlight = (id: string) => {
  highlightId.value = id
  window.setTimeout(() => {
    if (highlightId.value === id) highlightId.value = null
  }, 1400)
}

const scrollToEl = (el: HTMLElement) => {
  el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

const scrollToTarget = (targetId: string, categoryId?: CategoryId) => {
  if (categoryId) activeCategoryId.value = categoryId
  const el = document.getElementById(targetId)
  if (!el) return
  scrollToEl(el)
  flashHighlight(targetId)
}

const scrollToCategory = (categoryId: CategoryId) => {
  activeCategoryId.value = categoryId
  const el = document.getElementById(`cat-${categoryId}`)
  if (el) scrollToEl(el)
}

const jumpFirstResult = () => {
  const first = filteredItems.value[0]
  if (!first) return
  scrollToTarget(first.id, first.categoryId)
}

let catObserver: IntersectionObserver | null = null

// 本地兜底的安全说明，当接口不可用时仍然可以展示给用户看的基础内容
const fallbackSecurityInfo: SecurityInfo = {
  encryption: '平台全站启用 HTTPS 加密传输，敏感数据采用 AES-256 等主流算法加密存储。',
  locationPermission: '仅在提供必要服务时按需申请定位权限，不会在后台长期跟踪您的位置信息。',
  transactionEncryption: '交易记录采用加密存储并严格控制访问权限，仅用于账单查询和风险审计。',
  dataProtection: '遵循最小必要原则存储数据，定期进行安全审计和漏洞排查，防止信息泄露。'
}

// 加载安全保障信息：优先请求后端接口，失败时使用本地兜底数据
const loadSecurityInfo = async () => {
  // 进入加载状态，显示"加载中"动画
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
    // 给用户一个温和的错误提示，而不是一直停留在"加载中"
    loadError.value = '网络或服务器异常，已为您展示本地预置的安全说明，稍后可尝试重新加载。'
  } finally {
    // 无论成功还是失败，都要结束加载状态，隐藏"加载中"动画
    isLoading.value = false
  }
}

// 手机号脱敏显示
const maskPhone = (phone: string) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 邮箱脱敏显示
const maskEmail = (email: string) => {
  if (!email) return ''
  const [name, domain] = email.split('@')
  if (name.length <= 2) return email
  const maskedName = name.substring(0, 2) + '***' + name.substring(name.length - 1)
  return `${maskedName}@${domain}`
}

// 切换两步验证
const toggleTwoFactor = () => {
  // TODO: 调用后端接口更新两步验证状态
  console.log('两步验证状态:', userSecurityInfo.value.twoFactorEnabled)
  // 这里可以添加实际的 API 调用
}

// 处理修改密码
const handleChangePassword = () => {
  // 验证表单
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    alert('请填写完整信息')
    return
  }
  
  if (passwordForm.value.newPassword.length < 8) {
    alert('新密码长度至少为8位')
    return
  }
  
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    alert('两次输入的密码不一致')
    return
  }
  
  // TODO: 调用后端接口修改密码
  console.log('修改密码:', passwordForm.value)
  alert('密码修改成功！')
  
  // 重置表单并关闭弹窗
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  showChangePassword.value = false
}

// 组件挂载完成后自动加载一次安全保障信息
onMounted(() => {
  loadSecurityInfo()

  // 目录高亮：观察每个分类 section
  const ids: CategoryId[] = ['account', 'bindings', 'platform', 'mechanism', 'tips']
  const targets = ids
    .map((id) => document.getElementById(`cat-${id}`))
    .filter(Boolean) as HTMLElement[]

  if (targets.length > 0) {
    catObserver = new IntersectionObserver(
      (entries) => {
        const visible = entries
          .filter((e) => e.isIntersecting)
          .sort((a, b) => (a.boundingClientRect.top ?? 0) - (b.boundingClientRect.top ?? 0))
        const first = visible[0]
        const id = (first?.target as HTMLElement | undefined)?.id?.replace(/^cat-/, '') as CategoryId | undefined
        if (id) activeCategoryId.value = id
      },
      {
        root: null,
        threshold: [0.1, 0.25, 0.5],
        rootMargin: '-20% 0px -65% 0px'
      }
    )

    for (const el of targets) catObserver.observe(el)
  }
})

onUnmounted(() => {
  if (catObserver) {
    catObserver.disconnect()
    catObserver = null
  }
})
</script>

<style scoped>
/* 页面整体 */
.security-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #fdfbff 0%, #f7f0ff 25%, #fdf3f7 60%, #ffffff 100%);
  color: #111827;
}

.security-main {
  flex: 1;
  padding: 24px 0 52px;
}

.hero {
  max-width: 1200px;
  margin: 0 auto 18px;
  padding: 0 20px;
}

.hero-inner {
  border-radius: 20px;
  padding: 22px 22px 18px;
  background: linear-gradient(135deg, rgba(255, 64, 129, 0.06), rgba(63, 81, 181, 0.07));
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.35);
}

.hero-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 14px;
}

.hero-breadcrumb {
  font-size: 13px;
  color: #8a8fa1;
  margin: 0 0 6px;
}

.hero-title {
  margin: 0 0 6px;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 0.2px;
  color: #1f2937;
}

.hero-subtitle {
  margin: 0;
  font-size: 14px;
  color: #5f6473;
}

.hero-badge {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(148, 163, 184, 0.35);
  color: #ff4b8b;
  backdrop-filter: blur(10px);
  font-weight: 800;
}

.search-area {
  position: relative;
  max-width: 720px;
}

.searchbar {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 44px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.5);
  background: rgba(255, 255, 255, 0.9);
  transition: border-color 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.searchbar.focused {
  border-color: rgba(255, 75, 139, 0.65);
  box-shadow: 0 0 0 3px rgba(255, 75, 139, 0.12);
  background: #fff;
}

.search-icon {
  color: #9ca3af;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  color: #111827;
}

.search-clear {
  border: none;
  background: transparent;
  cursor: pointer;
  width: 30px;
  height: 30px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  transition: background 0.15s ease, color 0.15s ease;
}

.search-clear:hover {
  background: rgba(148, 163, 184, 0.18);
  color: #6b7280;
}

.suggestions {
  position: absolute;
  top: calc(44px + 10px);
  left: 0;
  width: 100%;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.38);
  box-shadow: 0 18px 46px rgba(15, 23, 42, 0.16);
  backdrop-filter: blur(10px);
  overflow: hidden;
  z-index: 20;
}

.suggestions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  font-size: 12px;
  color: #6b7280;
  background: rgba(248, 250, 252, 0.7);
}

.suggestions-count {
  font-weight: 800;
  color: #ff4b8b;
}

.suggestion-item {
  width: 100%;
  display: flex;
  gap: 12px;
  justify-content: space-between;
  align-items: center;
  text-align: left;
  padding: 12px 12px;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: background 0.12s ease;
}

.suggestion-item:hover {
  background: rgba(255, 75, 139, 0.06);
}

.suggestion-left {
  min-width: 0;
}

.suggestion-title {
  font-size: 13px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 3px;
}

.suggestion-desc {
  font-size: 12px;
  color: #6b7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 520px;
}

.suggestion-right {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: #9ca3af;
}

.suggestion-tag {
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 999px;
  background: rgba(255, 75, 139, 0.08);
  color: #ff4b8b;
  border: 1px solid rgba(255, 75, 139, 0.22);
  white-space: nowrap;
}

.suggestion-empty {
  padding: 12px;
  font-size: 13px;
  color: #6b7280;
}

/* 布局 */
.shell {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 18px;
  align-items: start;
}

.sidebar {
  position: sticky;
  top: 84px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 14px 10px;
}

.sidebar-title {
  font-size: 12px;
  color: #6b7280;
  padding: 6px 10px 10px;
  font-weight: 800;
  letter-spacing: 0.3px;
}

.nav-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 10px;
  border-radius: 12px;
  border: 1px solid transparent;
  background: transparent;
  cursor: pointer;
  transition: background 0.12s ease, border-color 0.12s ease;
  color: #1f2937;
}

.nav-item i {
  color: #6b7280;
}

.nav-label {
  flex: 1;
  text-align: left;
  font-size: 14px;
  font-weight: 700;
}

.nav-arrow {
  color: #cbd5e1;
}

.nav-item:hover {
  background: rgba(255, 75, 139, 0.06);
}

.nav-item.active {
  background: rgba(255, 75, 139, 0.09);
  border-color: rgba(255, 75, 139, 0.22);
}

.nav-item.active i {
  color: #ff4b8b;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.group {
  scroll-margin-top: 96px;
}

.group-header {
  margin-bottom: 10px;
  padding: 0 2px;
}

.group-title {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 850;
  color: #111827;
}

.group-desc {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
}

.group-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  overflow: hidden;
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 14px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.22);
  scroll-margin-top: 110px;
  transition: background 0.2s ease;
}

.row:last-child {
  border-bottom: none;
}

.row.highlight,
.info-card.highlight,
.article-item.highlight,
.tips-card.highlight {
  background: rgba(255, 75, 139, 0.10);
}

.row-left {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  min-width: 0;
}

.row-icon {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 75, 139, 0.10);
  color: #ff4b8b;
  border: 1px solid rgba(255, 75, 139, 0.18);
  flex-shrink: 0;
}

.row-text {
  min-width: 0;
}

.row-title {
  font-size: 14px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 4px;
}

.row-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
}

.row-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.action-btn {
  padding: 8px 14px;
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.55);
  background: rgba(255, 255, 255, 0.85);
  color: #111827;
  font-size: 13px;
  font-weight: 650;
  cursor: pointer;
  transition: border-color 0.15s ease, background 0.15s ease, transform 0.06s ease;
}

.action-btn:hover {
  border-color: rgba(255, 75, 139, 0.55);
  background: rgba(255, 247, 251, 0.9);
}

.action-btn:active {
  transform: scale(0.98);
}

/* 信息卡片 */
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 14px;
}

.info-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 14px 14px 12px;
  transition: transform 0.12s ease, border-color 0.12s ease;
}

.info-card:hover {
  transform: translateY(-2px);
  border-color: rgba(255, 75, 139, 0.35);
}

.info-icon {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: linear-gradient(135deg, #ffebf3, #ffe4ff);
  color: #ff4b8b;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.info-title {
  font-size: 15px;
  font-weight: 850;
  color: #111827;
  margin-bottom: 6px;
}

.info-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.7;
}

/* 文章段落 */
.article {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.article-item {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 14px 14px 12px;
}

.article-item h3 {
  margin: 0 0 8px;
  font-size: 14px;
  font-weight: 850;
  color: #111827;
}

.article-item p {
  margin: 0;
  font-size: 13px;
  color: #4b5563;
  line-height: 1.85;
}

.tips-card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 14px 14px 12px;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tips-item {
  position: relative;
  padding-left: 18px;
  font-size: 13px;
  color: #4b5563;
  line-height: 1.85;
}

.tips-item::before {
  content: '';
  position: absolute;
  left: 4px;
  top: 10px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff4b8b, #ff7ab2);
}

/* 接口错误提示区域：在安全内容顶部显示一条温和的提示 */
.load-error {
  margin-bottom: 1.5rem;
  padding: 1rem 1.25rem;
  border-radius: 12px;
  background: linear-gradient(135deg, #fff5f9 0%, #ffeef5 100%); /* 淡粉色渐变背景 */
  border: 1px solid #ffb3d9; /* 淡粉色边框 */
}

/* 错误提示文字样式：稍小号字体的说明文字 */
.load-error-text {
  font-size: 0.9rem;
  color: #ff66b3; /* 稍深的粉色 */
}

/* 安全使用小贴士整体区域：卡片下方的一块温馨提示区域 */
.security-tips {
  background: linear-gradient(135deg, #fff 0%, #faf5ff 100%); /* 淡紫色渐变背景 */
  border-radius: 16px;
  padding: 2.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #e6d4ff; /* 淡薰衣草色边框 */
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
  padding: 0.5rem 0.5rem 0.5rem 1.5rem;
  border-radius: 8px;
  transition: background 0.3s ease;
}

.tips-item:hover {
  background: rgba(230, 212, 255, 0.3); /* 淡薰衣草色背景 */
}

/* 小贴士前面的自定义圆点 - 使用马卡龙色系 */
.tips-item::before {
  content: '';
  position: absolute;
  left: 0.5rem;
  top: 0.9rem;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #d4b3ff 0%, #c299ff 100%); /* 淡紫色渐变 */
}

/* 加载状态容器：居中显示加载动画和提示文字 */
.loading-state {
  text-align: center;
  padding: 2rem 0;
}

/* 加载动画：简单的圆形旋转效果 - 使用马卡龙色系 */
.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f0f0f0;
  border-top: 4px solid #ff4b8b;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

/* 兜底错误区域：接口失败且拿不到任何数据时展示的说明块 */
.error-state {
  background: linear-gradient(135deg, #fff 0%, #fff5f9 100%);
  border-radius: 16px;
  padding: 2.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  text-align: left;
  border: 1px solid #ffb3d9;
}

/* 兜底错误标题：提醒用户当前是异常情况 */
.error-title {
  font-size: 1.6rem;
  font-weight: bold;
  color: #ff66b3; /* 粉色标题 */
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

/* 重新加载按钮：让用户可以手动重试接口请求 - 使用马卡龙色系 */
.reload-button {
  display: inline-block;
  padding: 0.6rem 1.4rem;
  border-radius: 999px; /* 胶囊形按钮 */
  border: none;
  background: linear-gradient(135deg, #ffb3d9 0%, #ff99cc 100%);
  color: #fff;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

/* 按钮悬停和按下的状态反馈 */
.reload-button:hover {
  box-shadow: 0 4px 12px rgba(255, 179, 217, 0.4);
  transform: translateY(-1px);
}

.reload-button:active {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(255, 153, 204, 0.3);
}

/* 弹窗遮罩层 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px); /* 毛玻璃效果 */
}

/* 弹窗内容 */
.modal-content {
  background: #fff;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 弹窗头部 */
.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  font-size: 1.3rem;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #999;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: #f5f5f5;
  color: #333;
}

/* 弹窗主体 */
.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  font-size: 0.9rem;
  font-weight: 500;
  color: #333;
  margin-bottom: 0.5rem;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #ffb3d9;
  box-shadow: 0 0 0 3px rgba(255, 179, 217, 0.1);
}

/* 弹窗底部 */
.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.btn-cancel,
.btn-confirm {
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-confirm {
  background: linear-gradient(135deg, #ffb3d9 0%, #ff99cc 100%);
  color: #fff;
}

.btn-confirm:hover {
  box-shadow: 0 4px 12px rgba(255, 179, 217, 0.4);
  transform: translateY(-1px);
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
  .security-main {
    padding: 16px 0 40px;
  }

  .shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
    top: auto;
  }

  .hero-title-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-title {
    font-size: 24px;
  }

  .suggestion-desc {
    max-width: 220px;
  }

  /* 弹窗在移动端的适配 */
  .modal-content {
    width: 95%;
    margin: 1rem;
  }

  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 1.25rem;
  }

  .modal-footer {
    flex-direction: column;
  }

  .btn-cancel,
  .btn-confirm {
    width: 100%;
  }
}
</style>
