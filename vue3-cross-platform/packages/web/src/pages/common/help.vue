<template>
  <!-- 整个帮助中心页面最外层容器 -->
  <div class="help-page">
    <!-- 顶部全局导航栏，保持与其他页面一致 -->
    <NavBar />

    <!-- 主体内容区域 -->
    <main class="help-main">
      <!-- 顶部横幅区域，对应设计稿中“如何为您提供帮助？” -->
      <section class="help-hero">
        <div class="hero-inner">
          <!-- 面包屑提示当前位置 -->
          <p class="hero-breadcrumb">首页 / 使用帮助</p>
          <!-- 主标题 -->
          <h1 class="hero-title">如何为您提供帮助？</h1>
          <!-- 副标题说明 -->
          <p class="hero-subtitle">
            请输入您的问题关键词，我们将为您找到最相关的解决方案
          </p>
          <!-- 搜索输入框和按钮 -->
          <div class="hero-search">
            <input
              v-model="searchKeyword"
              type="text"
              class="hero-search-input"
              placeholder="搜索：例如 注册、密码、二手、兼职..."
              @keyup.enter="handleSearch"
            />
            <button class="hero-search-button" @click="handleSearch">
              搜索
            </button>
          </div>
          <!-- 搜索结果提示文案，仅在有搜索结果列表时显示 -->
          <p v-if="searchResults" class="hero-search-tip">
            为您找到
            <span class="hero-search-count">{{ searchResults.length }}</span>
            条相关帮助内容
          </p>
        </div>
      </section>

      <!-- “使用帮助”总览模块：三张卡片（详细指南 / 视频教程 / 在线客服） -->
      <section class="help-section help-overview">
        <div class="section-header">
          <h2 class="section-title">使用帮助</h2>
          <p class="section-subtitle">
            为您提供全面的使用指南和问题解答，让您轻松掌握平台功能
          </p>
        </div>
        <div class="card-grid">
          <article class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-book-open"></i>
            </div>
            <h3 class="feature-title">详细使用指南</h3>
            <p class="feature-desc">
              提供从入门到精通的完整教程，涵盖平台所有功能模块的使用方法。
            </p>
          </article>
          <article class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-play-circle"></i>
            </div>
            <h3 class="feature-title">视频教程</h3>
            <p class="feature-desc">
              直观的视频演示，帮助您快速掌握复杂功能的具体操作步骤。
            </p>
          </article>
          <article class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-headset"></i>
            </div>
            <h3 class="feature-title">在线客服支持</h3>
            <p class="feature-desc">
              7×24
              小时在线客服，随时为您解答使用过程中遇到的任何问题。
            </p>
          </article>
        </div>
      </section>

      <!-- 常见问题分类区域：按照设计稿列出多个问题类别 -->
      <section class="help-section help-category-section" v-if="!searchResults">
        <div class="section-header">
          <h2 class="section-title">常见问题分类</h2>
          <p class="section-subtitle">
            按照问题类型进行分类，帮助您更快找到对应的解决方案。
          </p>
        </div>
        <div class="category-grid">
          <div
            v-for="item in faqCategories"
            :key="item.key"
            class="category-item"
            @click="quickFillKeyword(item.example)"
          >
            <div class="category-icon">
              <i :class="item.icon"></i>
            </div>
            <div class="category-content">
              <h3 class="category-title">{{ item.title }}</h3>
              <p class="category-desc">{{ item.desc }}</p>
            </div>
            <span class="category-arrow">›</span>
          </div>
        </div>
      </section>

      <!-- 快速上手指南：四个步骤的横向流程 -->
      <section class="help-section quick-start-section" v-if="!searchResults">
        <div class="section-header">
          <h2 class="section-title">快速上手指南</h2>
          <p class="section-subtitle">
            按照下面四个步骤完成账号准备，即可顺利使用平台的全部能力。
          </p>
        </div>
        <div class="steps-grid">
          <div
            v-for="(step, index) in quickStartSteps"
            :key="step.title"
            class="step-card"
          >
            <div class="step-number">{{ index + 1 }}</div>
            <h3 class="step-title">{{ step.title }}</h3>
            <p class="step-desc">{{ step.desc }}</p>
          </div>
        </div>
      </section>

      <!-- 功能快速入门：对应四大模块的入口说明 -->
      <section class="help-section feature-entry-section" v-if="!searchResults">
        <div class="section-header">
          <h2 class="section-title">功能快速入门</h2>
          <p class="section-subtitle">
            围绕校园 E
            卡通、二手交易、兼职服务、行程管理四大模块，提供一站式指引。
          </p>
        </div>
        <div class="feature-entry-grid">
          <article
            v-for="feature in featureEntries"
            :key="feature.title"
            class="feature-entry-card"
          >
            <div class="feature-entry-icon">
              <i :class="feature.icon"></i>
            </div>
            <div class="feature-entry-body">
              <h3 class="feature-entry-title">{{ feature.title }}</h3>
              <p class="feature-entry-desc">{{ feature.desc }}</p>
              <button
                class="feature-entry-button"
                type="button"
                @click="goFeature(feature.route)"
              >
                立即前往
              </button>
            </div>
          </article>
        </div>
      </section>

      <!-- 搜索结果列表：当用户输入关键词并搜索后显示 -->
      <section v-if="searchResults" class="help-section search-result-section">
        <div class="section-header">
          <h2 class="section-title">搜索结果</h2>
          <p class="section-subtitle">
            以下是与“
            <span class="keyword">{{ searchKeyword }}</span>
            ”相关的帮助内容：
          </p>
        </div>
        <div class="results-list">
          <article
            v-for="article in searchResults"
            :key="article.id"
            class="result-card"
          >
            <h3 class="result-title">{{ article.title }}</h3>
            <p class="result-snippet">
              {{ article.content.substring(0, 120) }}...
            </p>
            <div class="result-meta">
              <span class="result-category">{{ article.category }}</span>
              <span class="result-views">浏览 {{ article.viewCount }} 次</span>
              <span class="result-time">更新 {{ article.updateTime }}</span>
            </div>
          </article>
          <p v-if="searchResults.length === 0" class="no-result-text">
            暂时没有找到相关内容，建议尝试更简短或更通用的关键词。
          </p>
        </div>
        <button class="back-overview-button" type="button" @click="clearSearch">
          返回帮助总览
        </button>
      </section>

      <!-- 热门问题：展示几条用户最关心的问题 -->
      <section class="help-section hot-question-section" v-if="!searchResults">
        <div class="section-header">
          <h2 class="section-title">热门问题</h2>
          <p class="section-subtitle">
            覆盖注册登录、密码找回、App 下载、课程表等常见场景。
          </p>
        </div>
        <div class="hot-list">
          <article
            v-for="item in hotQuestions"
            :key="item.question"
            class="hot-item"
          >
            <h3 class="hot-question">{{ item.question }}</h3>
            <p class="hot-answer">{{ item.answer }}</p>
          </article>
        </div>
      </section>

      <!-- 帮助文章列表：模拟“帮助文章”模块 -->
      <section class="help-section article-section" v-if="!searchResults">
        <div class="section-header">
          <h2 class="section-title">帮助文章</h2>
          <p class="section-subtitle">
            为不同使用场景准备的专题文章，方便你系统性地学习。
          </p>
        </div>
        <div class="article-list">
          <article
            v-for="article in helpArticles"
            :key="article.title"
            class="article-item"
          >
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-desc">{{ article.desc }}</p>
            <button
              class="article-link"
              type="button"
              @click="quickFillKeyword(article.keyword)"
            >
              阅读全文
            </button>
          </article>
        </div>
      </section>

      <!-- 常见问题解答：以问答形式再整理一遍重点问题 -->
      <section class="help-section faq-section" v-if="!searchResults">
        <div class="section-header">
          <h2 class="section-title">常见问题解答</h2>
          <p class="section-subtitle">
            汇总账号安全、充值到账、举报不当内容、行程提醒等高频问题。
          </p>
        </div>
        <div class="faq-list">
          <details
            v-for="faq in faqDetailList"
            :key="faq.question"
            class="faq-item"
          >
            <summary class="faq-question">
              {{ faq.question }}
            </summary>
            <p class="faq-answer">
              {{ faq.answer }}
            </p>
          </details>
        </div>
      </section>

      <!-- 联系我们：四种联系方式卡片 -->
      <section class="help-section contact-section" v-if="!searchResults">
        <div class="section-header">
          <h2 class="section-title">联系我们</h2>
          <p class="section-subtitle">
            如果帮助中心仍无法解决你的问题，可以通过以下方式与我们取得联系。
          </p>
        </div>
        <div class="contact-grid">
          <article class="contact-card">
            <h3 class="contact-title">客服热线</h3>
            <p class="contact-desc">7×24 小时人工服务</p>
            <p class="contact-value">400-123-4567</p>
          </article>
          <article class="contact-card">
            <h3 class="contact-title">电子邮件</h3>
            <p class="contact-desc">工作日内 24 小时回复</p>
            <p class="contact-value">help@campus.edu.cn</p>
          </article>
          <article class="contact-card">
            <h3 class="contact-title">在线客服</h3>
            <p class="contact-desc">即时沟通解决问题</p>
            <button
              class="contact-button"
              type="button"
              @click="goMessages"
            >
              立即咨询
            </button>
          </article>
          <article class="contact-card">
            <h3 class="contact-title">线下服务点</h3>
            <p class="contact-desc">校园内多个服务网点</p>
            <button
              class="contact-button"
              type="button"
              @click="goSchedule"
            >
              查看位置
            </button>
          </article>
        </div>
      </section>
    </main>

    <!-- 页脚，与整站保持一致 -->
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
// 引入 Vue 的组合式 API，用于处理响应式数据和生命周期
import { ref } from 'vue'
// 引入路由实例，用于在帮助中心内部进行页面跳转
import { useRouter } from 'vue-router'
// 引入全局导航栏组件
import NavBar from '@/components/common/NavBar.vue'
// 引入全局页脚组件
import AppFooter from '@/components/common/AppFooter.vue'
// 从公共包中引入搜索帮助文档的接口和帮助文档类型
import { searchHelp, type HelpArticle } from '@campus/common'

// 获取路由实例，用于在“功能快速入门”和“联系我们”区域进行跳转
const router = useRouter()

// 搜索关键词（绑定到顶部搜索输入框）
const searchKeyword = ref('') // 默认空字符串
// 搜索结果列表，为空时展示为 null 表示未进行搜索
const searchResults = ref<HelpArticle[] | null>(null) // 初始为 null

// 常见问题分类静态配置，对应设计稿中的分类列表
const faqCategories = ref([
  {
    key: 'account',
    title: '账户问题',
    desc: '注册、登录、密码找回等常见账号相关问题。',
    icon: 'fas fa-user-circle',
    example: '账号'
  },
  {
    key: 'app',
    title: 'App 使用',
    desc: '移动端 App 的下载安装与功能使用说明。',
    icon: 'fas fa-mobile-alt',
    example: 'App'
  },
  {
    key: 'payment',
    title: '支付问题',
    desc: '充值、支付、退款相关的规则和处理方式。',
    icon: 'fas fa-credit-card',
    example: '支付'
  },
  {
    key: 'security',
    title: '安全保障',
    desc: '账号安全、隐私保护和风控相关说明。',
    icon: 'fas fa-shield-alt',
    example: '安全'
  },
  {
    key: 'trade',
    title: '交易问题',
    desc: '二手交易、兼职服务的流程与注意事项。',
    icon: 'fas fa-exchange-alt',
    example: '二手'
  },
  {
    key: 'course',
    title: '课程管理',
    desc: '课程表、考试、作业等学习相关功能说明。',
    icon: 'fas fa-book',
    example: '课程表'
  },
  {
    key: 'social',
    title: '社交功能',
    desc: '好友、消息、活动等社交能力的使用指引。',
    icon: 'fas fa-comments',
    example: '消息'
  },
  {
    key: 'other',
    title: '其他问题',
    desc: '暂时无法归类的其他使用问题。',
    icon: 'fas fa-question-circle',
    example: '其他'
  }
])

// 快速上手的四个步骤配置
const quickStartSteps = ref([
  {
    title: '注册登录',
    desc: '使用学号或手机号完成实名认证，创建个人账户。'
  },
  {
    title: '功能探索',
    desc: '浏览校园 E 卡通、二手、兼职、行程管理等功能入口。'
  },
  {
    title: '个性化设置',
    desc: '在个人中心中调整通知偏好和常用功能入口。'
  },
  {
    title: '开始使用',
    desc: '完成设置后，即可畅享一站式校园综合服务。'
  }
])

// 四大功能模块的快速入口配置（注意：这里是一个标准的数组字面量，属性之间使用英文逗号分隔，末尾不加多余分号）
const featureEntries = ref([
  {
    title: '校园 E 卡通',
    desc: '学习如何充值、消费以及查看交易记录与门禁记录。',
    icon: 'fas fa-id-card-alt',
    route: '/ecard',
  },
  {
    title: '二手交易',
    desc: '掌握发布商品、沟通交易和完成评价的完整流程。',
    icon: 'fas fa-shopping-bag',
    route: '/secondhand',
  },
  {
    title: '兼职服务',
    desc: '了解如何寻找、申请和管理适合你的兼职岗位。',
    icon: 'fas fa-briefcase',
    route: '/parttime',
  },
  {
    title: '行程管理',
    desc: '学会创建个人行程、导入课程表并设置提醒。',
    icon: 'fas fa-calendar-alt',
    route: '/schedule',
  },
])

// 热门问题列表，对应设计稿中的“热门问题”部分
const hotQuestions = ref([
  {
    question: '如何注册上大学 Online 账号？',
    answer:
      '你可以使用手机号或邮箱进行注册，注册后按照页面提示完成验证码校验和身份认证即可开始使用。'
  },
  {
    question: '忘记密码怎么办？',
    answer:
      '在登录页面点击“忘记密码”，按照提示输入注册时使用的手机号或邮箱，系统会发送验证码，验证成功后即可重置密码。'
  },
  {
    question: '如何下载手机 App？',
    answer:
      '你可以在“手机 App 下载”页面查看二维码，也可以在各大应用商店搜索“上大学 Online”进行下载安装。'
  },
  {
    question: '如何查看课程表？',
    answer:
      '登录后进入“行程管理”或课程相关页面，可以导入教务系统课程表或手动添加课程安排。'
  }
])

// 常见问题解答列表，使用 details/summary 进行展开折叠
const faqDetailList = ref([
  {
    question: '如何重置登录密码？',
    answer:
      '在登录页面点击“忘记密码”，输入注册时使用的手机号或邮箱，完成验证码校验后即可设置新密码。建议设置包含字母与数字的强密码。'
  },
  {
    question: 'E 卡通充值后未到账怎么办？',
    answer:
      '请先在支付页面确认支付是否成功。如支付成功但余额未更新，请等待 5-10 分钟。如果仍未到账，请保留支付凭证并联系在线客服处理。'
  },
  {
    question: '如何举报不当内容或用户？',
    answer:
      '在对应的二手商品、兼职岗位或用户主页点击“举报”按钮，选择举报原因并提交。平台审核通过后会对相关内容或账号进行处理。'
  },
  {
    question: '行程提醒没有收到怎么办？',
    answer:
      '请检查手机系统通知权限是否为本应用开启，同时确认 App 在后台保持运行。你也可以在“行程管理”中检查该行程是否开启了提醒开关。'
  }
])

// 帮助文章列表，模拟“帮助文章”模块
const helpArticles = ref([
  {
    title: '新手指南：快速上手上大学 Online',
    desc: '从注册登录到完成基础设置，带你一步步熟悉整个平台的核心能力。',
    keyword: '新手指南'
  },
  {
    title: '校园 E 卡通使用全攻略',
    desc: '详细介绍校园 E 卡通的充值、消费、挂失等常见操作。',
    keyword: 'E卡通'
  },
  {
    title: '二手交易规则与安全提示',
    desc: '了解二手交易的基本规则和注意事项，保障交易安全顺利。', 
    keyword: '二手交易'
  },
  {
    title: '兼职服务权益保障说明',
    desc: '从岗位审核到报名流程，帮助你安全地寻找和申请兼职工作。',
    keyword: '兼职'
  }
])

// 点击分类或文章时，快速填充搜索关键词并触发搜索
const quickFillKeyword = (keyword: string) => {
  // 将用户点击的关键字填充到搜索框
  searchKeyword.value = keyword
  // 立即执行一次搜索
  handleSearch()
}

// 执行搜索帮助文档的函数
const handleSearch = async () => {
  // 如果输入为空，则清空搜索结果，恢复总览界面
  if (!searchKeyword.value.trim()) {
    searchResults.value = null
    return
  }

  try {
    // 调用后端搜索接口，根据关键字获取帮助文档列表
    const res = await searchHelp(searchKeyword.value.trim(), 1, 10)
    // 将返回的列表赋值到本地状态中，用于渲染搜索结果
    searchResults.value = res.list || []
  } catch (error) {
    // 如果请求失败，在控制台打印错误并展示为空列表
    console.error('搜索帮助文档失败:', error)
    searchResults.value = []
  }
}

// 清空搜索结果，返回到帮助总览页面
const clearSearch = () => {
  // 清空关键字和结果
  searchKeyword.value = ''
  searchResults.value = null
}

// 跳转到对应功能模块的页面
const goFeature = (routePath: string) => {
  // 通过路由跳转到传入的功能路径
  router.push(routePath)
}

// 跳转到消息中心页面，用于“在线客服/立即咨询”
const goMessages = () => {
  router.push('/messages')
}

// 跳转到行程管理页面，用于模拟“线下服务点 / 查看位置”
const goSchedule = () => {
  router.push('/schedule')
}
</script>

<style scoped>
/* 页面整体背景和布局 */
.help-page {
  min-height: 100vh; /* 页面最小高度占满整个视口高度 */
  display: flex; /* 使用弹性布局使页脚贴底 */
  flex-direction: column; /* 垂直排列导航、内容和页脚 */
  background: linear-gradient(
    135deg,
    #fdfbff 0%,
    #f7f0ff 25%,
    #fdf3f7 60%,
    #ffffff 100%
  ); /* 柔和的渐变背景，贴近设计稿风格 */
}

/* 主体内容容器 */
.help-main {
  flex: 1; /* 占据剩余高度，让页脚自动贴底 */
  padding: 40px 0 60px; /* 上下留出一定的空白 */
}

/* 顶部横幅区域样式 */
.help-hero {
  max-width: 1200px; /* 限制内容最大宽度 */
  margin: 0 auto 40px; /* 居中并与下方模块拉开距离 */
  padding: 32px 32px 40px; /* 内边距 */
  background: linear-gradient(
    135deg,
    rgba(255, 64, 129, 0.06),
    rgba(63, 81, 181, 0.08)
  ); /* 渐变背景色块 */
  border-radius: 20px; /* 圆角矩形 */
  box-shadow: 0 18px 45px rgba(15, 35, 52, 0.08); /* 柔和投影 */
}

/* 顶部横幅内部容器 */
.hero-inner {
  max-width: 720px; /* 文案内容宽度控制 */
}

/* 面包屑文字样式 */
.hero-breadcrumb {
  font-size: 13px;
  color: #9e9eaa;
  margin-bottom: 8px;
}

/* 顶部大标题样式 */
.hero-title {
  font-size: 32px;
  line-height: 1.4;
  font-weight: 700;
  color: #233145;
  margin-bottom: 10px;
}

/* 顶部副标题说明 */
.hero-subtitle {
  font-size: 15px;
  color: #5f6473;
  margin-bottom: 22px;
}

/* 顶部搜索容器 */
.hero-search {
  display: flex;
  align-items: center;
  gap: 12px;
  max-width: 540px;
}

/* 顶部搜索输入框样式 */
.hero-search-input {
  flex: 1;
  height: 44px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.5);
  background-color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  color: #333648;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease,
    background-color 0.2s ease;
}

/* 顶部搜索输入聚焦态 */
.hero-search-input:focus {
  border-color: #ff4b8b;
  box-shadow: 0 0 0 1px rgba(255, 75, 139, 0.2);
  background-color: #ffffff;
}

/* 顶部搜索按钮样式 */
.hero-search-button {
  height: 44px;
  padding: 0 22px;
  border-radius: 999px;
  border: none;
  background: linear-gradient(135deg, #ff4b8b, #ff7ab2);
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 10px 24px rgba(255, 75, 139, 0.35);
  transition: transform 0.15s ease, box-shadow 0.15s ease,
    opacity 0.15s ease;
}

/* 顶部搜索按钮悬停态 */
.hero-search-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 30px rgba(255, 75, 139, 0.45);
}

/* 顶部搜索按钮按下态 */
.hero-search-button:active {
  transform: translateY(0);
  box-shadow: 0 8px 16px rgba(255, 75, 139, 0.3);
  opacity: 0.92;
}

/* 搜索结果计数提示文案 */
.hero-search-tip {
  margin-top: 12px;
  font-size: 13px;
  color: #6b7280;
}

/* 搜索结果数量高亮 */
.hero-search-count {
  color: #ff4b8b;
  font-weight: 600;
}

/* 通用版块外层容器 */
.help-section {
  max-width: 1200px;
  margin: 0 auto 40px;
  padding: 0 24px;
}

/* 每个板块头部标题与说明文案容器 */
.section-header {
  margin-bottom: 20px;
}

/* 板块主标题 */
.section-title {
  font-size: 22px;
  font-weight: 700;
  color: #222a3a;
  margin-bottom: 6px;
}

/* 板块副标题 */
.section-subtitle {
  font-size: 14px;
  color: #6b7280;
}

/* 三个“使用帮助”卡片容器 */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 18px;
}

/* 单个“使用帮助”功能卡片样式 */
.feature-card {
  background-color: #ffffff;
  border-radius: 16px;
  padding: 20px 18px;
  box-shadow: 0 10px 30px rgba(148, 163, 184, 0.25);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* “使用帮助”功能卡片图标外层 */
.feature-icon {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: linear-gradient(135deg, #ffebf3, #ffe4ff);
  color: #ff4b8b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

/* “使用帮助”功能卡片标题 */
.feature-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

/* “使用帮助”功能卡片描述 */
.feature-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.7;
}

/* 分类列表所在板块额外间距 */
.help-category-section {
  margin-top: 8px;
}

/* 常见问题分类列表网格布局 */
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 14px;
}

/* 单条分类卡片外层 */
.category-item {
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 14px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  border: 1px solid rgba(209, 213, 219, 0.7);
  transition: border-color 0.15s ease, box-shadow 0.15s ease,
    transform 0.1s ease, background-color 0.15s ease;
}

/* 分类卡片悬停态 */
.category-item:hover {
  border-color: rgba(255, 75, 139, 0.6);
  box-shadow: 0 10px 24px rgba(148, 163, 184, 0.35);
  background-color: #ffffff;
  transform: translateY(-2px);
}

/* 分类图标圆形背景容器 */
.category-icon {
  width: 32px;
  height: 32px;
  border-radius: 999px;
  background: rgba(248, 250, 252, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ff4b8b;
  font-size: 16px;
}

/* 分类文案容器 */
.category-content {
  flex: 1;
}

/* 分类标题文字 */
.category-title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 2px;
}

/* 分类描述文字 */
.category-desc {
  font-size: 12px;
  color: #6b7280;
}

/* 分类卡片右侧箭头 */
.category-arrow {
  font-size: 18px;
  color: #d1d5db;
}

/* 快速上手步骤网格布局 */
.steps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

/* 单个步骤卡片样式 */
.step-card {
  position: relative;
  background-color: #ffffff;
  border-radius: 14px;
  padding: 18px 18px 16px;
  box-shadow: 0 8px 22px rgba(148, 163, 184, 0.3);
}

/* 步骤编号圆形标记 */
.step-number {
  width: 30px;
  height: 30px;
  border-radius: 999px;
  background: linear-gradient(135deg, #ff7ab2, #ff4b8b);
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

/* 步骤标题文字 */
.step-title {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

/* 步骤描述文字 */
.step-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.7;
}

/* 功能快速入门模块网格布局 */
.feature-entry-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 18px;
}

/* 单个功能快速入门卡片外层 */
.feature-entry-card {
  display: flex;
  gap: 12px;
  padding: 16px 16px 18px;
  background-color: #ffffff;
  border-radius: 14px;
  box-shadow: 0 8px 24px rgba(148, 163, 184, 0.3);
}

/* 功能快速入门左侧图标容器 */
.feature-entry-icon {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: rgba(248, 250, 252, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4f46e5;
  font-size: 18px;
}

/* 功能快速入门右侧文本容器 */
.feature-entry-body {
  flex: 1;
}

/* 功能快速入门标题 */
.feature-entry-title {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

/* 功能快速入门描述 */
.feature-entry-desc {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
}

/* 功能快速入门按钮样式 */
.feature-entry-button {
  border-radius: 999px;
  border: 1px solid rgba(209, 213, 219, 0.8);
  background-color: #ffffff;
  padding: 4px 14px;
  font-size: 12px;
  color: #4b5563;
  cursor: pointer;
  transition: background-color 0.15s ease, border-color 0.15s ease,
    color 0.15s ease;
}

/* 功能快速入门按钮悬停态 */
.feature-entry-button:hover {
  border-color: #ff4b8b;
  color: #ff4b8b;
  background-color: #fff7fb;
}

/* 搜索结果版块整体样式 */
.search-result-section {
  margin-top: 8px;
}

/* 搜索结果列表容器 */
.results-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

/* 单条搜索结果卡片样式 */
.result-card {
  background-color: #ffffff;
  border-radius: 14px;
  padding: 16px 18px;
  box-shadow: 0 8px 22px rgba(148, 163, 184, 0.3);
}

/* 搜索结果标题样式 */
.result-title {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 6px;
}

/* 搜索结果内容摘要样式 */
.result-snippet {
  font-size: 13px;
  color: #4b5563;
  margin-bottom: 8px;
  line-height: 1.7;
}

/* 搜索结果底部元信息容器 */
.result-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 12px;
  color: #9ca3af;
}

/* 搜索结果分类文字高亮 */
.result-category {
  color: #ff4b8b;
}

/* 搜索结果无数据提示文字 */
.no-result-text {
  font-size: 13px;
  color: #9ca3af;
  text-align: center;
  margin-top: 10px;
}

/* 返回帮助总览按钮样式 */
.back-overview-button {
  margin-top: 18px;
  border-radius: 999px;
  border: 1px solid rgba(209, 213, 219, 0.9);
  background-color: #ffffff;
  padding: 6px 18px;
  font-size: 13px;
  color: #4b5563;
  cursor: pointer;
}

/* 热门问题列表容器 */
.hot-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 16px;
}

/* 单条热门问题卡片样式 */
.hot-item {
  background-color: #ffffff;
  border-radius: 14px;
  padding: 16px 16px 14px;
  box-shadow: 0 8px 22px rgba(148, 163, 184, 0.3);
}

/* 热门问题标题 */
.hot-question {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

/* 热门问题答案 */
.hot-answer {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.7;
}

/* 帮助文章列表容器 */
.article-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 16px;
}

/* 单条帮助文章卡片样式 */
.article-item {
  background-color: #ffffff;
  border-radius: 14px;
  padding: 16px 16px 14px;
  box-shadow: 0 8px 22px rgba(148, 163, 184, 0.3);
}

/* 帮助文章标题 */
.article-title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

/* 帮助文章简介 */
.article-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.7;
  margin-bottom: 8px;
}

/* “阅读全文”按钮样式 */
.article-link {
  border-radius: 999px;
  border: none;
  background: transparent;
  padding: 0;
  font-size: 12px;
  color: #ff4b8b;
  cursor: pointer;
}

/* 常见问题解答列表容器 */
.faq-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 单条 FAQ 外层 details 元素样式 */
.faq-item {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 10px 14px;
  box-shadow: 0 8px 22px rgba(148, 163, 184, 0.3);
}

/* FAQ 问题标题样式 */
.faq-question {
  list-style: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

/* FAQ 答案文字样式 */
.faq-answer {
  margin-top: 6px;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.7;
}

/* 联系我们模块网格布局 */
.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

/* 单个联系方式卡片 */
.contact-card {
  background-color: #ffffff;
  border-radius: 14px;
  padding: 16px 16px 14px;
  box-shadow: 0 10px 26px rgba(148, 163, 184, 0.35);
}

/* 联系方式标题 */
.contact-title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 4px;
}

/* 联系方式描述 */
.contact-desc {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
}

/* 联系方式具体数值样式 */
.contact-value {
  font-size: 15px;
  font-weight: 600;
  color: #ff4b8b;
}

/* 联系方式按钮样式 */
.contact-button {
  border-radius: 999px;
  border: 1px solid rgba(209, 213, 219, 0.9);
  background-color: #ffffff;
  padding: 4px 14px;
  font-size: 12px;
  color: #4b5563;
  cursor: pointer;
}

/* 小屏幕适配样式 */
@media (max-width: 768px) {
  .help-main {
    padding: 24px 0 40px;
  }

  .help-hero {
    margin: 0 12px 28px;
    padding: 22px 18px 24px;
  }

  .hero-title {
    font-size: 24px;
  }

  .hero-search {
    flex-direction: column;
    align-items: stretch;
  }

  .hero-search-button {
    width: 100%;
    justify-content: center;
  }

  .help-section {
    padding: 0 16px;
  }
}
</style>
