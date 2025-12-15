<template>
  <div class="common-page help-page">
    <!-- 全局导航栏 -->
    <NavBar />

    <!-- 页面内容 -->
    <div class="page-content">
      <div class="content-container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1 class="page-title">帮助中心</h1>
          <p class="page-subtitle">常见问题解答，快速找到您需要的帮助</p>
        </div>

        <!-- 搜索框 -->
        <div class="search-section">
          <div class="search-box">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索帮助文档..."
              class="search-input"
              @keyup.enter="handleSearch"
            />
            <button class="search-button" @click="handleSearch">搜索</button>
          </div>
        </div>

        <!-- 帮助分类 -->
        <div class="help-categories" v-if="categories.length > 0 && !searchResults">
          <h2 class="section-title">帮助分类</h2>
          <div class="categories-grid">
            <div
              v-for="(category, index) in categories"
              :key="index"
              class="category-card"
              @click="selectCategory(category.name)"
            >
              <div class="category-icon"><i :class="['fas', getCategoryIcon(category.name)]"></i></div>
              <h3 class="category-name">{{ category.name }}</h3>
              <p class="category-desc">{{ category.description }}</p>
              <div class="category-count">{{ category.articleCount }} 篇文章</div>
            </div>
          </div>
        </div>

        <!-- 搜索结果 -->
        <div class="search-results" v-if="searchResults">
          <h2 class="section-title">搜索结果</h2>
          <div class="results-list">
            <div
              v-for="(article, index) in searchResults"
              :key="index"
              class="result-item"
              @click="viewArticle(article)"
            >
              <h4 class="result-title">{{ article.title }}</h4>
              <p class="result-content">{{ article.content.substring(0, 100) }}...</p>
              <div class="result-meta">
                <span class="result-category">{{ article.category }}</span>
                <span class="result-views">浏览 {{ article.viewCount }} 次</span>
              </div>
            </div>
            <div v-if="searchResults.length === 0" class="no-results">
              未找到相关帮助文档
            </div>
          </div>
        </div>

        <!-- 常见问题 -->
        <div class="faq-section" v-if="!searchResults">
          <h2 class="section-title">常见问题</h2>
          <div class="faq-list">
            <div
              v-for="(faq, index) in faqList"
              :key="index"
              class="faq-item"
              :class="{ active: activeFaq === index }"
              @click="toggleFaq(index)"
            >
              <div class="faq-question">
                <span>{{ faq.question }}</span>
                <span class="faq-icon">{{ activeFaq === index ? '−' : '+' }}</span>
              </div>
              <div class="faq-answer" v-if="activeFaq === index">
                <p>{{ faq.answer }}</p>
              </div>
            </div>
          </div>
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
import { getHelpCategories, searchHelp, type HelpCategory, type HelpArticle } from '@campus/common'

// 帮助分类列表
const categories = ref<HelpCategory[]>([])
// 搜索关键词
const searchKeyword = ref('')
// 搜索结果
const searchResults = ref<HelpArticle[] | null>(null)
// 当前展开的FAQ
const activeFaq = ref<number | null>(null)

// 常见问题列表
const faqList = ref([
  {
    question: '如何注册账号？',
    answer: '访问注册页面，填写手机号、密码等信息，完成手机验证码验证即可注册成功。'
  },
  {
    question: '如何申请学生身份认证？',
    answer: '在个人中心选择身份认证，选择学生身份，填写学号和教务系统验证码，提交后等待审核（3个工作日内）。'
  },
  {
    question: '校园E卡通如何使用？',
    answer: '完成身份认证后，在校园E卡通页面可以查看学生码，用于消费、门禁、图书馆等场景。'
  },
  {
    question: '如何发布二手商品？',
    answer: '在二手交易页面点击"发布商品"，填写商品信息、上传图片，设置价格和交易方式即可发布。'
  },
  {
    question: '如何申请兼职？',
    answer: '在兼职服务页面浏览兼职信息，点击感兴趣的兼职查看详情，点击"立即报名"即可申请。'
  },
  {
    question: '如何导入课程表？',
    answer: '在行程管理页面选择"导入课程表"，可以手动添加或从教务系统导入课程信息。'
  }
])

// 获取分类图标
const getCategoryIcon = (name: string): string => {
  const iconMap: Record<string, string> = {
    '注册登录': 'fa-user-lock',
    '校园E卡通': 'fa-id-card',
    '二手交易': 'fa-shopping-cart',
    '兼职服务': 'fa-briefcase',
    '行程管理': 'fa-calendar-alt',
    '身份认证': 'fa-check-circle'
  }
  return iconMap[name] || 'fa-book'
}

// 加载帮助分类
const loadCategories = async () => {
  try {
    const response = await getHelpCategories()
    categories.value = response.categories
  } catch (error) {
    console.error('加载帮助分类失败:', error)
  }
}

// 搜索帮助文档
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = null
    return
  }

  try {
    const response = await searchHelp(searchKeyword.value)
    searchResults.value = response.list
  } catch (error) {
    console.error('搜索失败:', error)
    searchResults.value = []
  }
}

// 选择分类
const selectCategory = (categoryName: string) => {
  searchKeyword.value = categoryName
  handleSearch()
}

// 查看文章详情
const viewArticle = (article: HelpArticle) => {
  // TODO: 跳转到文章详情页
  console.log('查看文章:', article)
}

// 切换FAQ展开/收起
const toggleFaq = (index: number) => {
  activeFaq.value = activeFaq.value === index ? null : index
}

// 组件挂载时加载数据
onMounted(() => {
  loadCategories()
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
  margin-bottom: 2rem;
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

/* 搜索区域 */
.search-section {
  margin-bottom: 3rem;
}

.search-box {
  display: flex;
  max-width: 600px;
  margin: 0 auto;
  gap: 1rem;
}

.search-input {
  flex: 1;
  padding: 0.8rem 1.2rem;
  border: 2px solid #ddd;
  border-radius: 25px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.3s;
}

.search-input:focus {
  border-color: #e91e63;
}

.search-button {
  padding: 0.8rem 2rem;
  background: #e91e63;
  color: #fff;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.3s;
}

.search-button:hover {
  background: #c2185b;
}

/* 分类区域 */
.help-categories {
  margin-bottom: 3rem;
}

.section-title {
  font-size: 1.8rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 2rem;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.category-card {
  background: #fff;
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.category-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #e91e63;
}

.category-icon i {
  display: inline-block;
}

.category-name {
  font-size: 1.3rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.8rem;
}

.category-desc {
  font-size: 0.95rem;
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.6;
}

.category-count {
  font-size: 0.85rem;
  color: #e91e63;
  font-weight: bold;
}

/* 搜索结果 */
.search-results {
  margin-bottom: 3rem;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.result-item {
  background: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.result-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.result-title {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.8rem;
}

.result-content {
  font-size: 0.95rem;
  color: #666;
  margin-bottom: 1rem;
  line-height: 1.6;
}

.result-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #999;
}

.result-category {
  color: #e91e63;
  font-weight: bold;
}

.no-results {
  text-align: center;
  padding: 3rem;
  color: #999;
  font-size: 1.1rem;
}

/* 常见问题 */
.faq-section {
  margin-bottom: 3rem;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.faq-item {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.faq-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.faq-question {
  padding: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  font-size: 1.1rem;
  font-weight: bold;
  color: #333;
}

.faq-icon {
  font-size: 1.5rem;
  color: #e91e63;
  font-weight: bold;
}

.faq-answer {
  padding: 0 1.5rem 1.5rem;
  font-size: 0.95rem;
  color: #666;
  line-height: 1.8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-container {
    padding: 0 1rem;
  }

  .page-title {
    font-size: 2rem;
  }

  .search-box {
    flex-direction: column;
  }

  .categories-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
}
</style>
