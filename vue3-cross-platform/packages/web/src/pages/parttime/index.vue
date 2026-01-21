<template>
  <div class="parttime-page">
    <NavBar />

    <div class="page-container">
      <!-- 顶部横幅区域（参考app端） -->
      <section class="top-banner">
        <div class="top-bar">
          <div class="location-section">
            <i class="fas fa-map-marker-alt"></i>
            <span class="location-text">{{ currentLocation }}</span>
            <i class="fas fa-chevron-down"></i>
          </div>
          <div class="top-right-actions">
            <button class="top-action-btn" @click="handleQuick('message')">
              <i class="fas fa-comment"></i>
              <span class="msg-badge" v-if="unreadMessages > 0">{{ unreadMessages > 99 ? '99+' : unreadMessages }}</span>
            </button>
            <button class="top-action-btn" @click="handleQuick('notification')">
              <i class="fas fa-bell"></i>
              <span class="noti-badge" v-if="unreadNotifications > 0"></span>
            </button>
          </div>
        </div>
        
        <!-- 搜索框 -->
        <div class="search-container">
          <div class="search-box" @click="handleSearchClick">
            <i class="fas fa-search"></i>
            <input
              v-model="filters.search"
              type="text"
              placeholder="搜索兼职/公司/岗位"
              @keyup.enter="handleFilter"
              @click.stop
            />
          </div>
          <button class="filter-btn" @click="showQuickFilter = true">
            <i class="fas fa-sliders-h"></i>
          </button>
        </div>

        <!-- 快速筛选标签（参考app端） -->
        <div class="quick-filter-tags">
          <div class="tags-scroll-container">
            <div 
              v-for="tag in quickFilterTags" 
              :key="tag.value"
              class="filter-tag"
              :class="{ active: activeQuickTag === tag.value }"
              @click="toggleQuickTag(tag.value)"
            >
              <i :class="tag.icon"></i>
              <span>{{ tag.label }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 统一的筛选排序区域 -->
      <section class="filter-section">
        <!-- 排序和薪资快捷筛选 -->
        <div class="sort-salary-row">
          <div class="sort-section">
            <span class="sort-label">排序：</span>
            <div 
              v-for="(item, index) in sortOptions" 
              :key="item.value"
              class="sort-item"
              :class="{ active: sortType === item.value }"
              @click="handleSort(item.value)"
            >
              <i :class="item.icon"></i>
              <span>{{ item.label }}</span>
            </div>
          </div>
          <div class="salary-quick-section">
            <span class="salary-label">薪资：</span>
            <div class="salary-quick-btns">
              <div 
                v-for="(item, index) in salaryQuickOptions" 
                :key="index"
                class="salary-quick-btn"
                :class="{ active: salaryQuickIndex === index }"
                @click="handleSalaryQuick(index, item.min, item.max)"
              >
                {{ item.label }}
              </div>
            </div>
          </div>
        </div>
        
        <!-- 详细筛选条件 -->
        <div class="filter-row">
          <div class="filter-item">
            <label>类型：</label>
            <select v-model="filters.type">
              <option value="">全部类型</option>
              <option v-for="item in types" :key="item" :value="item">{{ item }}</option>
            </select>
          </div>
          <div class="filter-item">
            <label>地区：</label>
            <select v-model="filters.location">
              <option value="">全部地区</option>
              <option v-for="item in locations" :key="item" :value="item">{{ item }}</option>
            </select>
          </div>
          <div class="filter-item price-item">
            <label>薪资：</label>
            <input v-model="filters.salaryMin" type="number" placeholder="最低" min="0" />
            <span class="price-sep">-</span>
            <input v-model="filters.salaryMax" type="number" placeholder="最高" min="0" />
          </div>
          <div class="filter-item">
            <label>时间：</label>
            <select v-model="filters.time">
              <option value="">全部时间</option>
              <option v-for="item in timeOptions" :key="item" :value="item">{{ item }}</option>
            </select>
          </div>
          <div class="filter-actions">
            <button class="btn" @click="handleFilter">确定筛选</button>
            <button class="btn" @click="handleReset">重置</button>
          </div>
        </div>
      </section>

      <section class="content-section">
        <div class="main-column">
          <!-- 热门兼职推荐（轮播图，参考app端） -->
          <div class="hot-jobs-section" v-if="hotJobs.length > 0">
            <div class="section-header">
              <div class="section-title-with-icon">
                <i class="fas fa-fire"></i>
                <span>热门推荐</span>
              </div>
              <div class="section-subtitle">高薪靠谱 · 立即上岗</div>
            </div>
            <div class="hot-jobs-carousel">
              <div 
                v-for="(job, index) in hotJobs" 
                :key="job.id"
                class="hot-job-slide"
                :style="{ background: getSlideColor(index) }"
                @click="handleViewDetail(job.id)"
              >
                <div class="salary-badge">日结 {{ job.salary }}</div>
                <div class="slide-info">
                  <h3 class="job-title">{{ job.title }}</h3>
                  <div class="job-meta">
                    <div class="meta-item">
                      <i class="fas fa-map-marker-alt"></i>
                      <span>{{ job.location }}</span>
                    </div>
                    <div class="meta-item">
                      <i class="fas fa-calendar"></i>
                      <span>{{ job.workTime }}</span>
                    </div>
                  </div>
                  <div class="company-info">
                    <div class="company-avatar"></div>
                    <span class="company-name">{{ job.company }}</span>
                    <div class="verified-badge" v-if="job.verified">
                      <i class="fas fa-check-circle" style="font-size: 14px;"></i>
                      <span>已认证</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>


          <!-- 1. 搜索匹配盒子 - 有搜索/筛选时显示 -->
          <div v-if="hasSearchOrFilter" class="search-result-section">
            <h2 class="section-title"><i class="fas fa-search"></i> 搜索结果</h2>
            
            <!-- 搜索/筛选有结果 -->
            <div v-if="searchFilteredJobs.length > 0" class="job-grid">
              <div class="job-card" v-for="job in searchFilteredJobs" :key="job.id" @click="handleViewDetail(job.id)">
                <div class="job-header">
                  <h3 class="job-title">{{ job.title }}</h3>
                  <div class="company-name">{{ job.company }}</div>
                  <div class="job-tags">
                    <span class="tag" v-for="tag in job.tags" :key="tag">{{ tag }}</span>
                  </div>
                </div>
                <div class="job-info">
                  <div class="info-row">
                    <div class="info-item">
                      <i class="fas fa-map-marker-alt"></i>
                      <span>{{ job.location }}</span>
                    </div>
                    <div class="info-item">
                      <i class="fas fa-clock"></i>
                      <span>{{ job.time }}</span>
                    </div>
                  </div>
                  <div class="salary">¥{{ job.salary }}</div>
                  <p class="job-desc">{{ job.description }}</p>
                </div>
                <div class="job-footer">
                  <span class="publish-time">{{ job.publishTime }}</span>
                  <div class="job-actions" @click.stop>
                    <button class="btn-detail" @click.stop="handleViewDetail(job.id)">查看详情</button>
                    <button class="btn-apply" @click.stop="handleApply(job.id)">立即申请</button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 搜索/筛选无结果 -->
            <div v-else class="no-results">
              <i class="fas fa-exclamation-circle"></i>
              <h2>没有找到符合条件的兼职岗位~</h2>
              <p>请尝试调整搜索关键词或筛选条件</p>
            </div>
          </div>

          <!-- 高薪专区（参考app端） -->
          <div class="high-salary-section" v-if="highSalaryJobs.length > 0">
            <div class="section-header">
              <div class="section-title-with-icon">
                <i class="fas fa-star"></i>
                <span>高薪专区</span>
              </div>
              <div class="salary-range">日薪300元以上</div>
            </div>
            <div class="salary-grid">
              <div 
                v-for="job in highSalaryJobs" 
                :key="job.id"
                class="salary-job-card"
                @click="handleViewDetail(job.id)"
              >
                <div class="salary-badge-high">高薪</div>
                <div class="salary-content">
                  <h3 class="salary-job-title">{{ job.title }}</h3>
                  <div class="salary-info">
                    <span class="salary-highlight">{{ job.salary }}</span>
                    <span class="salary-desc">{{ job.salaryDesc }}</span>
                  </div>
                  <div class="salary-meta">
                    <div class="meta-item">
                      <i class="fas fa-map-marker-alt"></i>
                      <span>{{ job.location }}</span>
                    </div>
                    <div class="meta-item">
                      <i class="fas fa-calendar"></i>
                      <span>{{ job.workTime }}</span>
                    </div>
                  </div>
                  <div class="company-brief">
                    <div class="company-avatar-tiny"></div>
                    <span class="company-name-small">{{ job.company }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 2. 推荐兼职盒子（猜你喜欢）- 一直显示（增加随机推荐和换一批功能） -->
          <div class="recommendation-section">
            <h2 class="section-title">
              <i class="fas fa-briefcase"></i>
              推荐兼职
              <!-- 换一批按钮：点击后重新随机一组推荐兼职 -->
              <button class="refresh-btn" type="button" @click="refreshRecommendations">换一批</button>
            </h2>
            <div class="job-grid">
              <div class="job-card" v-for="job in recommendedJobs" :key="job.id" @click="handleViewDetail(job.id)">
                <div class="job-header">
                  <h3 class="job-title">{{ job.title }}</h3>
                  <div class="company-name">{{ job.company }}</div>
                  <div class="job-tags">
                    <span class="tag" v-for="tag in job.tags" :key="tag">{{ tag }}</span>
                  </div>
                </div>
                <div class="job-info">
                  <div class="info-row">
                    <div class="info-item">
                      <i class="fas fa-map-marker-alt"></i>
                      <span>{{ job.location }}</span>
                    </div>
                    <div class="info-item">
                      <i class="fas fa-clock"></i>
                      <span>{{ job.time }}</span>
                    </div>
                  </div>
                  <div class="salary">¥{{ job.salary }}</div>
                  <p class="job-desc">{{ job.description }}</p>
                </div>
                <div class="job-footer">
                  <span class="publish-time">{{ job.publishTime }}</span>
                  <div class="job-actions" @click.stop>
                    <button class="btn-detail" @click.stop="handleViewDetail(job.id)">查看详情</button>
                    <button class="btn-apply" @click.stop="handleApply(job.id)">立即申请</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <aside class="sidebar">
          <div class="sidebar-card category-section">
            <div class="section-header">
              <div class="section-title-with-icon">
                <i class="fas fa-th"></i>
                <span>兼职分类</span>
              </div>
              <div class="view-all" @click="goToAllCategories">全部</div>
            </div>
            <div class="category-grid">
              <div 
                v-for="category in jobCategories" 
                :key="category.value"
                class="category-item"
                @click="goToCategory(category.value)"
              >
                <div class="category-icon" :style="{ background: category.color }">
                  <i :class="category.icon"></i>
                </div>
                <span class="category-name">{{ category.label }}</span>
                <span class="job-count">{{ category.count }}个岗位</span>
              </div>
            </div>
          </div>

          <div class="sidebar-card">
            <h3>快捷操作</h3>
            <div class="quick-links">
              <a href="#" @click.prevent="handleQuick('publish')">发布兼职</a>
              <a href="#" @click.prevent="handleQuick('application')">申请记录</a>
              <a href="#" @click.prevent="handleQuick('history')">浏览记录</a>
              <a href="#" @click.prevent="handleQuick('favorite')">我的收藏</a>
            </div>
          </div>

          <div class="sidebar-card">
            <h3>安全提示</h3>
            <ol class="tips-list">
              <li v-for="(item, idx) in tips" :key="idx">{{ item }}</li>
            </ol>
          </div>

  
        </aside>
      </section>
    </div>
    <!-- 快速筛选弹窗（参考app端） -->
    <div v-if="showQuickFilter" class="filter-modal-overlay" @click="showQuickFilter = false">
      <div class="filter-modal" @click.stop>
        <div class="filter-modal-header">
          <span class="filter-modal-title">快速筛选</span>
          <span class="filter-modal-close" @click="showQuickFilter = false">×</span>
        </div>
        <div class="filter-modal-content">
          <div class="filter-group">
            <span class="filter-label">薪资范围</span>
            <div class="filter-options">
              <div 
                v-for="range in salaryRanges" 
                :key="range.value"
                class="filter-option"
                :class="{ active: filters.salaryRange === range.value }"
                @click="filters.salaryRange = range.value"
              >
                {{ range.label }}
              </div>
            </div>
          </div>
          
          <div class="filter-group">
            <span class="filter-label">工作时间</span>
            <div class="filter-options">
              <div 
                v-for="time in workTimes" 
                :key="time.value"
                class="filter-option"
                :class="{ active: filters.workTime === time.value }"
                @click="filters.workTime = time.value"
              >
                {{ time.label }}
              </div>
            </div>
          </div>
          
          <div class="filter-group">
            <span class="filter-label">结算方式</span>
            <div class="filter-options">
              <div 
                v-for="settle in settleTypes" 
                :key="settle.value"
                class="filter-option"
                :class="{ active: filters.settleType === settle.value }"
                @click="filters.settleType = settle.value"
              >
                {{ settle.label }}
              </div>
            </div>
          </div>
        </div>
        <div class="filter-modal-footer">
          <button class="filter-reset-btn" @click="handleFilterReset">重置</button>
          <button class="filter-confirm-btn" @click="handleFilterConfirm">确定</button>
        </div>
      </div>
    </div>

    <!-- 发布兼职：改为弹窗，不再跳转到独立发布页面 -->
    <AppModal :visible="showPublishModal" title="发布兼职" @close="closePublishModal">
      <ParttimePublish :embedded="true" @close="closePublishModal" @submitted="handlePublishSubmitted" />
    </AppModal>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
// 引入Vue的响应式和生命周期方法（包含 onBeforeUnmount，方便卸载时移除滚动监听）
import { reactive, ref, computed, onMounted, onBeforeUnmount, watch } from 'vue' // 从 vue 引入组合式 API
import { useRouter, useRoute } from 'vue-router' // 引入 Vue Router，用于处理路由跳转
import NavBar from '@/components/common/NavBar.vue' // 顶部导航栏组件
import AppFooter from '@/components/common/AppFooter.vue' // 页面底部统一的页脚组件
import FloatingMenu from '@/components/common/FloatingMenu.vue' // 右下角悬浮快捷菜单
import AppModal from '@/components/common/AppModal.vue'
import ParttimePublish from './publish/index.vue'
// 从公共包统一引入兼职 Store；随机推荐工具通过命名空间方式引入，避免浏览器对命名导出做严格校验导致运行时报错
import { useParttimeStore } from '@campus/common' // useParttimeStore 管理兼职列表
import * as formatUtils from '@campus/common/utils/format' // 包含 getRandomRecommendList 等推荐工具函数

// 初始化路由与兼职 Store 实例
const router = useRouter() // 获取路由实例
const route = useRoute()
const parttimeStore = useParttimeStore() // 获取兼职 Store，用于加载兼职列表并共享给全局其他页面

// 发布弹窗（替代原“发布兼职页面”）
const showPublishModal = ref(false)
const openPublishModal = () => {
  showPublishModal.value = true
}
const closePublishModal = () => {
  showPublishModal.value = false
  // 如果是通过 query 打开的弹窗，则关闭时同步清理 query（避免刷新后重复弹出）
  if (route.query.publish) {
    const nextQuery: Record<string, any> = { ...route.query }
    delete nextQuery.publish
    delete nextQuery.id
    router.replace({ path: route.path, query: nextQuery })
  }
}

const handlePublishSubmitted = () => {
  closePublishModal()
  // 重新拉取一次列表（可看到最新数据；失败也不影响关闭弹窗）
  loadJobs(true)
}

// 基础数据定义
const types = ['实习', '兼职', '家教', '促销', '调研', '其他']
const locations = ['校内', '学校周边', '市中心', '远程']
const timeOptions = ['工作日', '周末', '时间灵活']

// 当前定位信息
const currentLocation = ref('北京海淀区')

// 消息通知数量
const unreadMessages = ref(3)
const unreadNotifications = ref(1)

// 快速筛选标签配置（参考app端）
const quickFilterTags = ref([
  { label: '日结', value: 'daily', icon: 'fas fa-wallet' },
  { label: '附近', value: 'nearby', icon: 'fas fa-map-marker-alt' },
  { label: '高薪', value: 'high_salary', icon: 'fas fa-star' },
  { label: '长期', value: 'long_term', icon: 'fas fa-calendar' },
  { label: '短期', value: 'short_term', icon: 'fas fa-bolt' },
  { label: '包餐', value: 'with_meal', icon: 'fas fa-utensils' },
  { label: '可预支', value: 'advance', icon: 'fas fa-money-bill-wave' },
  { label: '学生优选', value: 'student', icon: 'fas fa-user-graduate' }
])

// 当前激活的快速标签
const activeQuickTag = ref('')

// 排序选项（新增）
const sortType = ref('default')
const sortOptions = ref([
  { label: '默认', value: 'default', icon: 'fas fa-list' },
  { label: '薪资', value: 'salary', icon: 'fas fa-dollar-sign' },
  { label: '最新', value: 'time', icon: 'fas fa-clock' },
  { label: '热度', value: 'hot', icon: 'fas fa-fire' }
])

// 薪资快捷筛选选项（新增）
const salaryQuickIndex = ref(-1)
const salaryQuickOptions = ref([
  { label: '50以下', min: 0, max: 50 },
  { label: '50-100', min: 50, max: 100 },
  { label: '100-200', min: 100, max: 200 },
  { label: '200-300', min: 200, max: 300 },
  { label: '300以上', min: 300, max: 999999 }
])

// 控制筛选弹窗显示
const showQuickFilter = ref(false)

// 热门兼职推荐（轮播图数据）
const hotJobs = ref<any[]>([])

// 兼职分类配置（参考app端）
const jobCategories = ref([
  { label: '家教助教', value: 'tutor', icon: 'fas fa-chalkboard-teacher', color: '#FFB6C1', count: 128 },
  { label: '促销导购', value: 'promotion', icon: 'fas fa-bullhorn', color: '#B0E0E6', count: 89 },
  { label: '服务员', value: 'waiter', icon: 'fas fa-concierge-bell', color: '#FFF8DC', count: 76 },
  { label: '派发传单', value: 'flyer', icon: 'fas fa-paper-plane', color: '#DDA0DD', count: 45 },
  { label: '客服', value: 'service', icon: 'fas fa-headset', color: '#98FB98', count: 67 },
  { label: '地推', value: 'ground', icon: 'fas fa-users', color: '#FFE4B5', count: 52 },
  { label: '展会协助', value: 'exhibition', icon: 'fas fa-images', color: '#B0E0E6', count: 34 },
  { label: '线上兼职', value: 'online', icon: 'fas fa-laptop', color: '#FFF8DC', count: 112 }
])

// 高薪兼职列表
const highSalaryJobs = ref<any[]>([])

// 筛选条件（TS类型约束）
interface Filters {
  search: string
  type: string
  location: string
  salaryMin: string
  salaryMax: string
  time: string
  salaryRange: string
  workTime: string
  settleType: string
}

const filters = reactive<Filters>({
  search: '',
  type: '',
  location: '',
  salaryMin: '',
  salaryMax: '',
  time: '',
  salaryRange: '',
  workTime: '',
  settleType: ''
})

// 筛选选项配置（用于弹窗）
const salaryRanges = ref([
  { label: '不限', value: '' },
  { label: '100元以下', value: '0-100' },
  { label: '100-200元', value: '100-200' },
  { label: '200-300元', value: '200-300' },
  { label: '300元以上', value: '300+' }
])

const workTimes = ref([
  { label: '不限', value: '' },
  { label: '周末', value: 'weekend' },
  { label: '长期', value: 'long' },
  { label: '短期', value: 'short' },
  { label: '晚上', value: 'night' }
])

const settleTypes = ref([
  { label: '不限', value: '' },
  { label: '日结', value: 'daily' },
  { label: '周结', value: 'weekly' },
  { label: '月结', value: 'monthly' },
  { label: '完工结', value: 'finish' }
])

// 兼职数据类型定义（约束每条兼职记录的字段）
interface Job {
  id: number
  title: string
  company: string
  tags: string[]
  location: string
  time: string
  salary: string
  salaryMin: number
  salaryMax: number
  description: string
  publishTime: string
}

// 原始兼职数据（从后端接口获取的完整兼职列表）
const jobs = ref<Job[]>([])
// 当前分页页码（用于“滑到底自动加载下一页”）
const currentPage = ref(1)
// 每页加载多少条兼职（可根据需求调整，数值越大一次性加载更多）
const pageSize = ref(20)
// 是否还有更多数据可以继续加载（为 false 时表示已经到底）
const hasMore = ref(true)

// 格式化时间
const formatTime = (timeStr: string): string => {
  if (!timeStr) return '未知时间'
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  return `${Math.floor(days / 30)}个月前`
}

// 加载兼职数据（支持分页与重置，底层通过 useParttimeStore 统一调用公共包接口）
// reset = true 表示重新加载（从第一页开始并清空旧数据）；false 表示在原有列表后追加下一页
const loadJobs = async (reset = false) => {
  try {
    // 如果 Store 中已经有请求在进行中，则直接返回，避免重复请求
    if (parttimeStore.loading) return

    // 如果需要重置（例如切换筛选条件），则重置分页状态和数据
    if (reset) {
      currentPage.value = 1 // 回到第一页
      hasMore.value = true // 重新标记为“还有更多”
      jobs.value = [] // 清空当前兼职列表
    }

    // 如果已经确定没有更多数据，并且不是重置请求，则不再重复请求
    if (!hasMore.value && !reset) {
      return
    }

    const params: any = {
      page: currentPage.value, // 当前页码
      pageSize: pageSize.value // 每页数量
    }
    
    // 添加筛选参数
    if (filters.search.trim()) {
      params.keyword = filters.search.trim()
    }
    if (filters.salaryMin) {
      params.minSalary = Number(filters.salaryMin)
    }
    if (filters.salaryMax) {
      params.maxSalary = Number(filters.salaryMax)
    }
    
    // 添加排序参数（新增）
    if (sortType.value === 'salary') {
      params.sortBy = 'salaryPerHour'
      params.sortOrder = 'DESC' // 薪资从高到低
    } else if (sortType.value === 'time') {
      params.sortBy = 'createTime'
      params.sortOrder = 'DESC' // 最新发布
    } else if (sortType.value === 'hot') {
      params.sortBy = 'createTime' // 热度暂时用发布时间
      params.sortOrder = 'DESC'
    } else {
      params.sortBy = 'createTime'
      params.sortOrder = 'DESC' // 默认按发布时间倒序
    }

    // 通过兼职 Store 统一发起请求（内部会调用 getParttimeList，并维护 loading / error 状态）
    await parttimeStore.loadJobList(params) // 加载当前页兼职列表

    // 从 Store 中读取当前页的兼职记录列表（此处做一次类型断言，兼容 TS 对 Pinia Ref 的推断）
    const records = parttimeStore.jobList as unknown as any[] // 保证后续可以安全使用 length / map 等数组方法

    // 如果本次没有返回任何记录：
    if (!records.length) {
      if (currentPage.value === 1) {
        // 第一页就没有数据，说明当前筛选条件下没有兼职，标记为没有更多
        hasMore.value = false
      } else {
        // 后续页没有数据，说明已经加载完所有兼职
        hasMore.value = false
        // 这里只做静默处理或简单提示，避免打扰用户
        console.info('兼职列表已全部加载完毕')
      }
      return
    }

    // 转换数据格式：将后端返回的 ParttimeJob 数据映射为当前页面使用的 Job 结构
    const mapped = records.map((item: any) => {
      const salaryPerHour = item.salaryPerHour || item.salary || 0
      const salaryText = salaryPerHour > 0 ? `¥${salaryPerHour}/小时` : '面议'
      
      // 格式化发布时间
      const publishTime = formatTime(item.createTime || item.publishTime)
      
      return {
        id: item.id,
        title: item.title,
        company: item.publisherName || '未知发布者',
        tags: [item.type || '兼职', item.location || '未知地点'],
        location: item.location || '未知地点',
        time: item.workTime || '时间灵活',
        salary: salaryText,
        salaryMin: salaryPerHour,
        salaryMax: salaryPerHour,
        description: item.description || '',
        publishTime: publishTime,
        verified: true, // 默认已认证
        workTime: item.workTime || '时间灵活'
      }
    })

    // 更新热门兼职（取前3个作为轮播图）
    if (mapped.length > 0) {
      hotJobs.value = mapped.slice(0, 3).map((item: any) => ({
        ...item,
        salary: item.salary,
        verified: true
      }))
    }

    // 更新高薪兼职（薪资>=300的）
    highSalaryJobs.value = mapped.filter((item: any) => item.salaryMin >= 300).slice(0, 4)

    // 如果当前是第一页，则用新数据覆盖；否则在原有列表后面追加，实现“加载更多”效果
    if (currentPage.value === 1) {
      jobs.value = mapped
    } else {
      jobs.value = [...jobs.value, ...mapped]
    }

    // 如果本次返回数量小于 pageSize，说明已经没有更多数据
    if (records.length < pageSize.value) {
      hasMore.value = false
    } else {
      // 当前页加载成功且记录数充足，页码自增，准备下一次“到底自动加载”
      currentPage.value += 1
    }

    // 每次拉取完新数据后，顺便刷新一次推荐兼职，保持推荐多样性
    refreshRecommendations()
  } catch (error: any) {
    console.error('加载兼职列表失败:', error) // 控制台打印错误日志，便于排查问题
    // 如果接口报错（例如后端未启动或返回500），为了不让页面“空白一片”，这里注入一组本地演示数据作为兜底
    if (!jobs.value.length) {
      // 只在当前没有任何数据时注入 mock，避免覆盖后端已经成功加载过的真实数据
      jobs.value = [
        {
          id: 1,
          title: '初中数学家教',
          company: '学而思教育',
          tags: ['家教', '数学', '周末'],
          location: '学校周边',
          time: '周末',
          salary: '80-120元/小时',
          salaryMin: 80,
          salaryMax: 120,
          description: '辅导初中生数学，要求有相关经验，耐心负责，每周2-3次，每次2小时。',
          publishTime: '2天前'
        },
        {
          id: 2,
          title: '咖啡店兼职店员',
          company: '星巴克咖啡',
          tags: ['餐饮', '服务', '培训'],
          location: '市中心',
          time: '工作日晚上/周末',
          salary: '25-35元/小时',
          salaryMin: 25,
          salaryMax: 35,
          description: '负责点单、制作咖啡、清洁等工作，提供专业培训，工作环境优越。',
          publishTime: '1天前'
        },
        {
          id: 3,
          title: '市场调研员',
          company: '益普索调研',
          tags: ['调研', '远程', '灵活'],
          location: '远程',
          time: '时间灵活',
          salary: '按项目计费',
          salaryMin: 0,
          salaryMax: 0,
          description: '进行线上市场调研，填写问卷，参与焦点小组讨论，工作时间自由安排。',
          publishTime: '3天前'
        },
        {
          id: 4,
          title: '新媒体运营实习生',
          company: '字节跳动',
          tags: ['实习', '运营', '转正机会'],
          location: '市中心',
          time: '工作日',
          salary: '150-200元/天',
          salaryMin: 150,
          salaryMax: 200,
          description: '负责社交媒体内容创作、数据分析，实习期3个月，表现优秀可转正。',
          publishTime: '5天前'
        },
        {
          id: 5,
          title: '校园推广专员',
          company: '阿里巴巴',
          tags: ['推广', '校园', '提成'],
          location: '校内',
          time: '时间灵活',
          salary: '底薪+提成',
          salaryMin: 0,
          salaryMax: 0,
          description: '在校园内推广公司产品，组织线下活动，积累人脉，提升沟通能力。',
          publishTime: '1周前'
        },
        {
          id: 6,
          title: '英语口语陪练',
          company: 'VIPKID',
          tags: ['英语', '远程', '口语'],
          location: '远程',
          time: '晚上/周末',
          salary: '60-100元/小时',
          salaryMin: 60,
          salaryMax: 100,
          description: '与学员进行英语口语练习，纠正发音，提高口语流利度，需要有耐心。',
          publishTime: '4天前'
        }
      ]
      hasMore.value = false // 本地演示数据视为一次性加载完毕，不再继续向后端请求
      // 使用本地演示数据刷新一次“推荐兼职”，避免推荐区也为空
      refreshRecommendations()
    }
  }
}

// 热门分类/安全提示
const hotCategories = ref(['家教辅导', '促销活动', '餐饮服务', '实习岗位', '校园代理', '问卷调查', '文案写作', '其他'])
const tips = ref([
  '选择平台认证的企业',
  '警惕高薪诱惑，谨防诈骗',
  '面试前核实企业信息',
  '签订正规兼职协议',
  '遇到问题及时反馈'
])

// 判断是否有搜索/筛选操作
const hasSearchOrFilter = computed(() => {
  return filters.search.trim() !== '' ||
         filters.type !== '' ||
         filters.location !== '' ||
         filters.salaryMin.trim() !== '' ||
         filters.salaryMax.trim() !== '' ||
         filters.time !== ''
})

// 搜索/筛选结果（用于搜索匹配盒子，展示“搜索结果”列表）
const searchFilteredJobs = computed(() => {
  return jobs.value.filter((job) => {
    // 1. 类型筛选
    if (filters.type && !job.tags.includes(filters.type)) return false
    // 2. 地区筛选
    if (filters.location && job.location !== filters.location) return false
    // 3. 时间筛选
    if (filters.time && !job.time.includes(filters.time)) return false
    // 4. 薪资筛选（处理固定薪资/非固定薪资）
    if (filters.salaryMin && job.salaryMin > 0 && job.salaryMin < Number(filters.salaryMin)) return false
    if (filters.salaryMax && job.salaryMax > 0 && job.salaryMax > Number(filters.salaryMax)) return false
    // 5. 关键词搜索（不区分大小写）
    if (filters.search) {
      const key = filters.search.trim().toLowerCase()
      return job.title.toLowerCase().includes(key) || 
             job.company.toLowerCase().includes(key) || 
             job.description.toLowerCase().includes(key)
    }
    return true
  })
})

// 每次“推荐兼职（猜你喜欢）”最多展示的岗位数量（可根据需求调整）
const RECOMMEND_COUNT = 8 // 一次最多展示8条推荐兼职

// 用于驱动随机推荐重新计算的“种子”（简单计数器，只要发生变化就会触发重新计算）
const recommendSeed = ref(0) // 每次点击“换一批”或下拉到底部时自增

// 推荐兼职（猜你喜欢，始终显示，从列表中随机抽取一部分）
const recommendedJobs = computed(() => {
  recommendSeed.value // 读取种子，让计算属性依赖它，从而在种子变化时重新计算

  const all = jobs.value // 读取全部兼职列表
  if (!all || all.length === 0) {
    // 如果暂无数据，则返回空数组，避免页面渲染报错
    return []
  }

  // 当用户有搜索/筛选时，优先从“未出现在搜索结果中的岗位”里做随机推荐，避免重复
  let pool = all // 初始候选池为所有岗位
  if (hasSearchOrFilter.value) {
    // 取出搜索/筛选结果的岗位ID集合
    const filteredIds = new Set(searchFilteredJobs.value.map(j => j.id))
    // 从全部岗位中过滤掉已经出现在搜索结果中的岗位
    pool = all.filter(job => !filteredIds.has(job.id))
    // 如果过滤后一个都不剩（例如数据量太小），则退回到全部岗位中随机
    if (pool.length === 0) {
      pool = all
    }
  }

  // 使用公共包中的工具函数，从候选池中随机抽取若干条作为“推荐兼职（猜你喜欢）”列表
  return formatUtils.getRandomRecommendList(pool, RECOMMEND_COUNT) // 统一随机逻辑，方便后续在多端共享与升级推荐策略
})

// 手动刷新推荐兼职（点击“换一批”按钮时调用）
const refreshRecommendations = () => {
  recommendSeed.value++ // 每次把种子自增1，触发计算属性重新随机
}

// 记录上一次触发滚动加载的时间，用于简单节流，避免触发过于频繁
let lastScrollRefreshTime = 0 // 记录上次触发时间戳（毫秒）

// 监听页面滚动事件：当用户向下滚动接近页面底部时，自动“加载下一页 + 换一批推荐兼职”
const handleScroll = () => {
  const now = Date.now() // 获取当前时间戳
  // 如果两次触发间隔小于1000毫秒（1秒），则直接返回，防止短时间内频繁刷新
  if (now - lastScrollRefreshTime < 1000) {
    return
  }

  // 获取当前滚动条位置
  const scrollTop =
    window.pageYOffset ||
    document.documentElement.scrollTop ||
    document.body.scrollTop ||
    0

  // 获取可视区域高度
  const windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight

  // 获取整个文档的总高度
  const docHeight = document.documentElement.scrollHeight || document.body.scrollHeight

  // 如果当前已滚动到底部附近（预留150像素阈值），则自动加载下一页或刷新推荐兼职
  if (docHeight - (scrollTop + windowHeight) < 150) {
    lastScrollRefreshTime = now // 记录本次触发时间

    if (hasMore.value) {
      // 如果还有更多数据，则继续向后端请求下一页兼职并追加到列表中
      loadJobs(false)
    } else {
      // 如果已经没有更多数据，则只刷新一次推荐兼职，避免无意义请求
      refreshRecommendations()
    }
  }
}

// 筛选触发（重新加载数据，从第一页开始）
const handleFilter = () => {
  loadJobs(true)
}

// 重置筛选条件
const handleReset = () => {
  filters.search = ''
  filters.type = ''
  filters.location = ''
  filters.salaryMin = ''
  filters.salaryMax = ''
  filters.time = ''
  filters.salaryRange = ''
  filters.workTime = ''
  filters.settleType = ''
  sortType.value = 'default'
  salaryQuickIndex.value = -1
  activeQuickTag.value = ''
  loadJobs(true)
}

// 筛选重置（弹窗内）
const handleFilterReset = () => {
  filters.salaryRange = ''
  filters.workTime = ''
  filters.settleType = ''
}

// 筛选确认（弹窗内）
const handleFilterConfirm = () => {
  showQuickFilter.value = false
  // 根据筛选条件重新加载数据
  if (filters.salaryRange) {
    const [min, max] = filters.salaryRange.split('-')
    filters.salaryMin = min || ''
    filters.salaryMax = max || ''
  }
  handleFilter()
}

// 页面加载时获取数据，并添加滚动监听实现“滑到底自动加载下一页 + 换一批推荐兼职”
onMounted(() => {
  loadJobs(true) // 初次加载兼职列表（从第一页开始）
  // 注册滚动事件监听器，当用户向下滚动接近底部时自动“加载下一页 + 换一批推荐兼职”
  window.addEventListener('scroll', handleScroll)
})

// 组件卸载前移除滚动监听，避免内存泄漏
onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 快捷操作（集成VueRouter跳转）
const handleQuick = (type: string) => {
  switch (type) {
    case 'publish':
      openPublishModal() // 弹窗发布
      break
    case 'message':
      router.push('/messages') // 消息页
      break
    case 'profile':
      router.push('/profile') // 个人中心
      break
    case 'application':
      router.push('/parttime/applications') // 申请记录
      break
    case 'history':
      router.push('/parttime/history') // 浏览记录
      break
    case 'favorite':
      router.push('/parttime/favorites') // 我的收藏
      break
    default:
      // 未知操作类型，不做处理
      break
  }
}

// 兼容旧路由：/parttime/publish -> /parttime?publish=1（由路由表 redirect 触发）
watch(
  () => route.query.publish,
  (val) => {
    const v = Array.isArray(val) ? val[0] : val
    if (v === '1') {
      openPublishModal()
    }
  },
  { immediate: true }
)

// 查看详情（路由跳转）
const handleViewDetail = (jobId: number) => {
  router.push(`/parttime/detail/${jobId}`)
}

// 申请岗位
const handleApply = (jobId: number) => {
  // 跳转到岗位详情页，用户可以在详情页申请
  router.push(`/parttime/detail/${jobId}`)
}

// 按分类筛选
const filterByCategory = (category: string) => {
  filters.type = category
  handleFilter()
}

// 切换快速标签（新增）
const toggleQuickTag = (tagValue: string) => {
  activeQuickTag.value = activeQuickTag.value === tagValue ? '' : tagValue
  // 根据标签筛选数据
  if (tagValue === 'high_salary') {
    filters.salaryMin = '300'
  } else if (tagValue === 'daily') {
    // 日结标签，可以设置结算方式筛选
  }
  handleFilter()
}

// 处理排序（新增）
const handleSort = (value: string) => {
  sortType.value = value
  loadJobs(true)
}

// 处理薪资快捷筛选（新增）
const handleSalaryQuick = (index: number, min: number, max: number) => {
  if (salaryQuickIndex.value === index) {
    salaryQuickIndex.value = -1
    filters.salaryMin = ''
    filters.salaryMax = ''
  } else {
    salaryQuickIndex.value = index
    filters.salaryMin = min.toString()
    filters.salaryMax = max === 999999 ? '' : max.toString()
  }
  handleFilter()
}

// 获取轮播卡片颜色（新增）
const getSlideColor = (index: number): string => {
  const colors = [
    '#FFE5F1', // 淡粉色
    '#E6F3FF', // 淡蓝色
    '#FFF4E6', // 淡黄色
    '#F0E6FF', // 淡紫色
    '#E6FFE6', // 淡绿色
    '#FFE6F0'  // 淡粉红色
  ]
  return colors[index % colors.length]
}

// 跳转到分类页面（新增）
const goToCategory = (category: string) => {
  filters.type = category
  handleFilter()
}

// 跳转到全部分类（新增）
const goToAllCategories = () => {
  // 可以跳转到分类列表页，这里先重置筛选
  filters.type = ''
  handleFilter()
}

// 处理搜索点击（新增）
const handleSearchClick = () => {
  // 可以跳转到搜索页面，这里先保持当前逻辑
}
</script>


<!-- 组件内样式（scoped） -->
<style scoped>
.parttime-page {
  min-height: 100vh;
  /* 直接用固定渐变背景，避免依赖上面删掉的全局变量 */
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

/* 顶部横幅区域（统一二手交易页面风格） */
.top-banner {
  background: white;
  border-radius: 12px;
  padding: 15px 20px;
  margin: 20px 0;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.12); /* 使用柔和的玫红色阴影，与二手交易页面一致 */
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.location-section {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  font-size: 14px;
  padding: 8px 16px;
  background: #f5f5f5;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
}

.location-section:hover {
  background: rgba(255, 107, 157, 0.1); /* 使用柔和的玫红色背景，与二手交易页面一致 */
  color: #FF6B9D; /* 使用柔和的玫红色 */
}

.location-text {
  font-size: 14px;
  color: inherit;
}

.location-section i {
  color: inherit;
  font-size: 14px;
}

.top-right-actions {
  display: flex;
  gap: 15px; /* 与二手交易页面一致 */
}

.top-action-btn {
  width: 40px;
  height: 40px;
  background: none; /* 与二手交易页面一致，去掉背景 */
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
  position: relative;
  cursor: pointer;
  transition: all 0.3s;
}

.top-action-btn:hover {
  background: rgba(255, 107, 157, 0.1); /* 使用柔和的玫红色背景，与二手交易页面一致 */
  color: #FF6B9D; /* 使用柔和的玫红色 */
  transform: scale(1.05);
}

.top-action-btn i {
  color: inherit;
  font-size: 18px;
}

.msg-badge, .noti-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #FF6B9D; /* 使用柔和的玫红色，与二手交易页面一致 */
  color: white;
  font-size: 12px;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.4); /* 使用柔和的玫红色阴影 */
  font-weight: 600; /* 加粗字体，提升可读性 */
}

.noti-badge {
  background: #FF6B9D; /* 使用柔和的玫红色 */
  width: 10px;
  height: 10px;
  min-width: 10px;
  border-radius: 5px;
  padding: 0;
  border: 2px solid white;
}

.search-container {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  background: #FFFFFF;
  border-radius: 25px;
  padding: 12px 15px 12px 20px; /* 与二手交易页面一致 */
  min-height: 50px;
  box-shadow: 0 2px 5px rgba(255, 107, 157, 0.15); /* 使用柔和的玫红色阴影，与二手交易页面一致 */
  border: 2px solid #FF6B9D; /* 使用柔和的玫红色边框，与二手交易页面一致 */
  gap: 12px;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
}

.search-box:hover {
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.25); /* 使用柔和的玫红色阴影 */
  border-color: #FF6B9D; /* 保持玫红色边框 */
}

.search-box input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  color: #333;
  background: transparent;
}

.search-box input::placeholder {
  color: #666;
  opacity: 0.8;
}

.search-box i {
  color: #FF6B9D; /* 使用柔和的玫红色，与二手交易页面一致 */
  font-size: 18px;
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
}

.filter-btn {
  width: 50px;
  height: 50px;
  background: #FFFFFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FF6B9D; /* 使用柔和的玫红色，与二手交易页面一致 */
  box-shadow: 0 2px 5px rgba(255, 107, 157, 0.15); /* 使用柔和的玫红色阴影 */
  border: 2px solid #FF6B9D; /* 使用柔和的玫红色边框，与二手交易页面一致 */
  transition: all 0.3s;
  cursor: pointer;
}

.filter-btn:hover {
  transform: scale(0.95);
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.25); /* 使用柔和的玫红色阴影 */
  border-color: #FF6B9D; /* 保持玫红色边框 */
  background: rgba(255, 107, 157, 0.1); /* 悬停时添加柔和的玫红色背景 */
}

.quick-filter-tags {
  margin-top: 10px;
}

.tags-scroll-container {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 5px;
}

.tags-scroll-container::-webkit-scrollbar {
  height: 4px;
}

.tags-scroll-container::-webkit-scrollbar-thumb {
  background: #FF6B9D; /* 使用柔和的玫红色 */
  border-radius: 3px;
}

.filter-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f5f5f5;
  color: #666;
  border-radius: 20px;
  font-size: 13px;
  border: 1px solid #e0e0e0;
  transition: all 0.3s;
  cursor: pointer;
  white-space: nowrap;
}

.filter-tag:hover {
  background: rgba(255, 107, 157, 0.1); /* 使用柔和的玫红色背景，与二手交易页面一致 */
  color: #FF6B9D; /* 使用柔和的玫红色 */
  border-color: #FF6B9D; /* 使用柔和的玫红色边框 */
}

.filter-tag.active {
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%); /* 使用柔和的玫红色渐变，与二手交易页面一致 */
  border-color: #FF6B9D; /* 使用柔和的玫红色边框 */
  transform: translateY(-2px);
  color: #FFFFFF; /* 白色文字，确保对比度 */
  font-weight: 600; /* 加粗字体，提升可读性 */
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.3); /* 使用柔和的玫红色阴影 */
}

.filter-tag i {
  color: inherit;
}

/* 筛选区域内的排序和薪资快捷筛选行 */
.filter-section .sort-salary-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.sort-section, .salary-quick-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sort-label, .salary-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.sort-item {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  background: #f5f5f5;
  border-radius: 15px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.sort-item:hover {
  background: #e0e0e0;
}

.sort-item.active {
  background: linear-gradient(135deg, #FFE5E5 0%, #FFD1D1 100%);
  color: #FF6B9D;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255, 182, 193, 0.3);
}

.salary-quick-btns {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.salary-quick-btn {
  padding: 6px 12px;
  background: #f5f5f5;
  border-radius: 15px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.salary-quick-btn:hover {
  background: #e0e0e0;
}

.salary-quick-btn.active {
  background: linear-gradient(135deg, #FFE5E5 0%, #FFD1D1 100%);
  color: #FF6B9D;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255, 182, 193, 0.3);
}

/* 热门兼职推荐区域 */
.hot-jobs-section {
  margin-bottom: 30px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.section-title-with-icon {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.section-title-with-icon i {
  color: #E91E63; /* 使用柔和的玫红色 */
}

.section-subtitle {
  font-size: 13px;
  color: #999;
}

.view-all, .salary-range {
  font-size: 13px;
  color: #E91E63; /* 使用柔和的玫红色 */
  font-weight: 500;
  cursor: pointer;
}

.hot-jobs-carousel {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
}

.hot-job-slide {
  border-radius: 15px;
  padding: 20px;
  position: relative;
  overflow: hidden;
  color: #333;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: 2px solid rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.hot-job-slide:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.15);
}

.salary-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 6px 15px;
  background: linear-gradient(135deg, #FFF9E5 0%, #FFF4D1 100%);
  color: #FFA500;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 182, 193, 0.3);
  box-shadow: 0 2px 8px rgba(255, 182, 193, 0.3);
}

.slide-info {
  margin-top: 30px;
}

.hot-job-slide .job-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 15px;
}

.hot-job-slide .job-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
}

.hot-job-slide .meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

/* 公司信息区域 - 增加间距，避免元素拥挤 */
.company-info {
  display: flex;
  align-items: center;
  gap: 12px; /* 增大元素间距，从10px增加到12px，让布局更舒展 */
  padding-top: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  flex-wrap: wrap; /* 允许换行，避免在小屏幕上拥挤 */
}

.company-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFE5E5 0%, #FFD1D1 100%);
  box-shadow: 0 2px 6px rgba(255, 182, 193, 0.3);
}

.company-name {
  flex: 1;
  font-size: 13px;
  color: #666;
}

/* 企业认证标识 - 使用柔和玫红色，增大尺寸和间距，避免拥挤 */
.verified-badge {
  display: flex;
  align-items: center;
  gap: 6px; /* 增大图标与文字间距，从4px增加到6px */
  padding: 6px 14px; /* 增大内边距，从4px 10px增加到6px 14px，让标识更舒展 */
  background: linear-gradient(135deg, rgba(255, 182, 193, 0.25) 0%, rgba(255, 192, 203, 0.2) 100%); /* 使用柔和的玫红色渐变背景，马卡龙风格 */
  border-radius: 16px; /* 增大圆角，从12px增加到16px，更圆润 */
  font-size: 12px; /* 增大字体，从11px增加到12px，提升可读性 */
  color: #E91E63; /* 使用柔和的玫红色文字，比#FF1493更柔和，不刺眼 */
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 182, 193, 0.4); /* 使用更柔和的玫红色边框，从1px增加到2px */
  font-weight: 500; /* 加粗字体，提升视觉重要性 */
  box-shadow: 0 2px 6px rgba(255, 182, 193, 0.2); /* 添加柔和的阴影，增强层次感 */
  margin-left: 8px; /* 增加左侧外边距，与公司名称保持适当距离 */
}

/* 兼职分类区域 */
.category-section {
  margin-bottom: 30px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 侧边栏中的分类网格（2列布局） */
.sidebar .category-section .category-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

/* 主内容区域的分类网格（4列布局） */
.main-column .category-section .category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.category-item {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item:hover {
  transform: translateY(-3px);
}

.category-icon {
  width: 60px;
  height: 60px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 10px;
  color: white;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.category-icon i {
  font-size: 24px;
}

.category-name {
  font-size: 13px;
  color: #333;
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.job-count {
  font-size: 11px;
  color: #999;
}

/* 高薪专区 */
.high-salary-section {
  margin-bottom: 30px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.salary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
}

.salary-job-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  padding: 15px;
  box-shadow: 0 4px 20px rgba(255, 182, 193, 0.15);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 182, 193, 0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.salary-job-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 25px rgba(255, 182, 193, 0.25);
}

.salary-badge-high {
  position: absolute;
  top: 0;
  right: 0;
  padding: 4px 15px;
  background: linear-gradient(135deg, #FFF9E5 0%, #FFF4D1 100%);
  color: #FFA500;
  font-size: 11px;
  font-weight: bold;
  border-radius: 0 0 0 15px;
  box-shadow: 0 2px 6px rgba(255, 182, 193, 0.3);
  border: 1px solid rgba(255, 182, 193, 0.2);
}

.salary-content {
  margin-top: 8px;
}

.salary-job-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
  height: 42px;
}

.salary-info {
  margin-bottom: 10px;
}

.salary-highlight {
  font-size: 16px;
  font-weight: bold;
  color: #E91E63; /* 使用柔和的玫红色 */
  display: block;
  margin-bottom: 4px;
}

.salary-desc {
  font-size: 11px;
  color: #999;
  display: block;
}

.salary-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 10px;
}

.salary-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: #999;
}

.company-brief {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.company-avatar-tiny {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, #E5FFE5 0%, #D1FFD1 100%);
  box-shadow: 0 2px 4px rgba(182, 224, 255, 0.3);
}

.company-name-small {
  font-size: 11px;
  color: #666;
  flex: 1;
}

/* 筛选弹窗 */
.filter-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  display: flex;
  align-items: flex-end;
}

.filter-modal {
  width: 100%;
  max-height: 80vh;
  background-color: #fff;
  border-radius: 20px 20px 0 0;
  padding: 20px;
  box-sizing: border-box;
}

.filter-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.filter-modal-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.filter-modal-close {
  font-size: 32px;
  color: #999;
  line-height: 1;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.filter-modal-content {
  max-height: calc(80vh - 150px);
  overflow-y: auto;
}

.filter-group {
  margin-bottom: 30px;
}

.filter-label {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 15px;
  display: block;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-option {
  padding: 10px 20px;
  background-color: #f5f5f5;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  min-height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-option:hover {
  background-color: #e0e0e0;
}

.filter-option.active {
  background: linear-gradient(135deg, #FFE5E5 0%, #FFD1D1 100%);
  color: #FF6B9D;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(255, 182, 193, 0.3);
}

.filter-modal-footer {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.filter-reset-btn,
.filter-confirm-btn {
  flex: 1;
  padding: 15px;
  border-radius: 12px;
  font-size: 16px;
  border: none;
  min-height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-reset-btn {
  background-color: #f5f5f5;
  color: #666;
}

.filter-confirm-btn {
  background: linear-gradient(135deg, #FFE5E5 0%, #FFD1D1 50%, #FFB6C1 100%);
  color: #FF6B9D;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(255, 182, 193, 0.3);
}

.filter-confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 182, 193, 0.4);
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 15px 40px;
}

/* 顶部搜索栏 */
.top-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  padding: 14px 18px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.search-box {
  position: relative;
  flex: 1;
}

.search-box input {
  width: 100%;
  padding: 12px 14px;
  border: 2px solid var(--primary);
  border-radius: 22px;
  font-size: 14px;
  box-shadow: 0 2px 6px rgba(216, 27, 96, 0.2);
  outline: none;
}

.search-box i {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--primary);
  cursor: pointer;
}

.top-actions {
  display: flex; /* 使用 Flex 布局，让按钮横向排列 */
  align-items: center; /* 垂直方向居中对齐三个操作按钮 */
  gap: 15px; /* 按钮之间保留 15 像素的间距，与二手页面保持一致 */
}

.action-btn {
  background: none; /* 去掉背景色，保持与二手页一致的简洁风格 */
  border: none; /* 去掉边框线条，让按钮看起来更扁平 */
  font-size: 16px; /* 统一按钮文字大小 */
  cursor: pointer; /* 鼠标移上去显示为手型，提示可点击 */
  color: #333; /* 默认文字颜色为深灰色，与二手页一致 */
  display: flex; /* 使用 Flex 布局，让图标与文字在水平方向排布 */
  align-items: center; /* 垂直方向居中对齐图标和文字 */
  padding: 8px 15px; /* 内边距与二手页一致，保证触控区域足够大 */
  border-radius: 20px; /* 使用圆角矩形按钮形状，统一视觉风格 */
  transition: all 0.3s; /* 添加过渡效果，使 hover 状态更顺滑 */
}

.action-btn:hover {
  /* 悬停时仅做轻微前移和阴影，不改变文字颜色，避免颜色跳变 */
  background: none;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.action-btn i {
  margin-right: 5px; /* 图标与文字之间预留 5 像素间距，避免贴得太紧 */
}

/* 筛选区域 */
.filter-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

/* 筛选区域内的排序和薪资快捷筛选行 */
.filter-section .sort-salary-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  color: var(--text);
  min-width: 52px;
}

.filter-item select,
.filter-item input {
  padding: 10px 12px;
  /* 使用统一的浅灰色边框，让下拉框/输入框边界更清晰 */
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  min-width: 150px;
  transition: border-color 0.3s;
  outline: none;
}

.filter-item input[type='number'] {
  min-width: 80px;
}

.filter-item select:focus,
.filter-item input:focus {
  border-color: var(--primary);
}

.price-item {
  gap: 6px;
}

.price-sep {
  color: var(--muted);
}

.filter-actions {
  display: flex;
  gap: 12px;
  margin-left: auto;
}

.btn {
  padding: 10px 24px;
  border-radius: 12px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.btn:hover {
  /* 悬停时只加阴影和轻微上移，不改变文字颜色和背景色 */
  background: white;
  border-color: var(--primary);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
  transform: translateY(-2px);
}

.btn:active {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(216, 27, 96, 0.2);
}

.btn.primary {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.btn.primary:hover {
  background: var(--primary-dark);
  border-color: var(--primary-dark);
}

/* 主内容区域 */
.content-section {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.main-column {
  flex: 1;
  min-width: 0;
}

/* 搜索结果区域 */
.search-result-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

/* 推荐兼职区域：增加非常浅的粉色背景，方便与周围区块区分 */
.recommendation-section {
  /* 使用极浅的粉色渐变，与页面整体紫色背景保持和谐，又不刺眼 */
  background: linear-gradient(135deg, #fff8fc 0%, #ffeefe 100%);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

/* 区域标题 */
.section-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: var(--text);
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title i {
  color: var(--primary);
}

/* 换一批按钮样式：放在标题右侧，视觉轻量但可点击 */
.refresh-btn {
  margin-left: auto; /* 将按钮推到最右侧 */
  padding: 4px 10px;
  font-size: 12px;
  border-radius: 12px;
  border: 1px solid var(--primary);
  background: #fff;
  color: var(--primary);
  cursor: pointer;
  transition: all 0.2s;
}

.refresh-btn:hover {
  background: var(--primary);
  color: #fff;
}

/* 无结果提示（和二手界面样式统一） */
.no-results {
  text-align: center;
  padding: 60px 20px;
  background: #fafafa;
  border-radius: 12px;
  margin: 10px 0;
}

.no-results i {
  font-size: 60px;
  color: var(--primary);
  margin-bottom: 20px;
}

.no-results h2 {
  font-size: 24px;
  margin-bottom: 10px;
  color: var(--text);
}

.no-results p {
  color: var(--muted);
  margin-bottom: 20px;
}

/* 兼职卡片网格 */
.job-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

/* 兼职卡片 */
.job-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(255, 182, 193, 0.08); /* 使用柔和的玫红色阴影 */
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 182, 193, 0.15); /* 使用柔和的玫红色边框 */
  cursor: pointer;
}

.job-card:hover {
  box-shadow: 0 4px 20px rgba(255, 182, 193, 0.15); /* 使用柔和的玫红色阴影 */
  transform: translateY(-2px);
  border-color: rgba(255, 182, 193, 0.25); /* 悬停时边框颜色加深 */
}

.job-header {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.job-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 8px;
}

.company-name {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 10px;
}

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  background: linear-gradient(135deg, rgba(255, 182, 193, 0.15) 0%, rgba(255, 192, 203, 0.1) 100%); /* 使用柔和的玫红色渐变背景 */
  color: #E91E63; /* 使用柔和的玫红色文字 */
  border-radius: 4px;
  font-size: 12px;
  border: 1px solid rgba(255, 182, 193, 0.2); /* 添加柔和的边框 */
}

.job-info {
  margin-bottom: 15px;
}

.info-row {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--muted);
}

.info-item i {
  color: #E91E63; /* 使用柔和的玫红色 */
  font-size: 12px;
}

.salary {
  font-size: 16px;
  font-weight: bold;
  color: #E91E63; /* 使用柔和的玫红色 */
  margin-bottom: 10px;
}

.job-desc {
  font-size: 14px;
  color: var(--muted);
  line-height: 1.6;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.job-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.publish-time {
  font-size: 12px;
  color: var(--muted);
}

.job-actions {
  display: flex;
  gap: 10px;
}

.btn-detail {
  padding: 6px 16px;
  border-radius: 6px;
  border: 1px solid rgba(255, 182, 193, 0.4); /* 使用柔和的玫红色边框 */
  background: white;
  color: #E91E63; /* 使用柔和的玫红色文字 */
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.btn-detail:hover {
  /* 悬停时仅通过阴影和前移强调点击感受，文字与边框颜色保持不变 */
  background: white;
  box-shadow: 0 2px 8px rgba(255, 182, 193, 0.25); /* 使用柔和的玫红色阴影 */
  transform: translateY(-1px);
  border-color: rgba(255, 182, 193, 0.5); /* 悬停时边框颜色加深 */
}

.btn-apply {
  padding: 6px 16px;
  border-radius: 6px;
  border: 1px solid rgba(255, 182, 193, 0.4); /* 使用柔和的玫红色边框 */
  background: white;
  color: #E91E63; /* 使用柔和的玫红色文字 */
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.btn-apply:hover {
  /* 悬停时保持按钮配色不变，只增加轻微阴影与前移效果 */
  background: white;
  box-shadow: 0 2px 8px rgba(255, 182, 193, 0.25); /* 使用柔和的玫红色阴影 */
  transform: translateY(-1px);
  border-color: rgba(255, 182, 193, 0.5); /* 悬停时边框颜色加深 */
}

/* 侧边栏 */
.sidebar {
  width: 300px;
  flex-shrink: 0;
}

.sidebar-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.sidebar-card h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary);
  color: var(--primary);
}

/* 侧边栏中的分类区域标题样式 */
.sidebar-card.category-section .section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary);
}

.sidebar-card.category-section .section-title-with-icon {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  color: var(--primary);
}

.sidebar-card.category-section .section-title-with-icon i {
  color: #E91E63; /* 使用柔和的玫红色 */
}

.sidebar-card.category-section .view-all {
  font-size: 13px;
  color: #FF91A4;
  font-weight: 500;
  cursor: pointer;
}

.sidebar-card.category-section .category-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 8px;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.sidebar-card.category-section .category-icon i {
  font-size: 20px;
}

.sidebar-card.category-section .category-name {
  font-size: 12px;
  color: #333;
  display: block;
  margin-bottom: 4px;
  font-weight: 500;
}

.sidebar-card.category-section .job-count {
  font-size: 10px;
  color: #999;
}

.sidebar-card ul {
  list-style: none;
  padding: 0;
}

.sidebar-card ul li {
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: color 0.3s;
  color: var(--text);
}

.sidebar-card ul li:hover {
  color: var(--primary);
}

.sidebar-card ul li:last-child {
  border-bottom: none;
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.quick-links a {
  padding: 10px 12px;
  background: #f9f0ff;
  border-radius: 8px;
  color: var(--text);
  transition: all 0.3s;
  display: flex;
  align-items: center;
}

.quick-links a:hover {
  /* 悬停时不改变文字颜色，只通过阴影和位移体现可点击 */
  background: #f9f0ff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.tips-list {
  padding-left: 20px;
  margin: 0;
}

.tips-list li {
  padding: 8px 0;
  color: var(--muted);
  line-height: 1.6;
}



/* 响应式适配 */
@media (max-width: 768px) {
  .content-section {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .top-bar {
    flex-direction: column;
    gap: 12px;
  }

  .filter-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-actions {
    margin-left: 0;
    width: 100%;
    justify-content: center;
    margin-top: 10px;
  }

  .job-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .page-container {
    padding: 10px 10px 20px;
  }

  .filter-section {
    padding: 16px;
  }

  .filter-item select,
  .filter-item input {
    width: 100%;
  }

  .btn {
    padding: 8px 16px;
    font-size: 13px;
  }

  .action-btn {
    padding: 8px 16px;
    font-size: 13px;
  }
}

</style>