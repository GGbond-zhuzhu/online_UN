<template>
  <div class="parttime-page">
    <NavBar />

    <div class="page-container">
      <!-- 顶部搜索与快捷操作 -->
      <section class="top-bar">
        <div class="search-box">
          <input
            v-model="filters.search"
            type="text"
            placeholder="搜索兼职岗位..."
            @keyup.enter="handleFilter"
          />
          <i class="fas fa-search"></i>
        </div>
        <div class="top-actions">
          <button class="action-btn" @click="handleQuick('publish')"><i class="fas fa-plus"></i> 发布兼职</button>
          <button class="action-btn" @click="handleQuick('message')"><i class="fas fa-comment"></i> 消息</button>
          <button class="action-btn" @click="handleQuick('profile')"><i class="fas fa-user"></i> 个人中心</button>
        </div>
      </section>

      <!-- 顶部筛选区域 -->
      <section class="filter-section">
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
            <button class="btn primary" @click="handleFilter">确定筛选</button>
            <button class="btn" @click="handleReset">重置</button>
          </div>
        </div>
      </section>

      <section class="content-section">
        <div class="main-column">
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

          <!-- 2. 推荐兼职盒子（猜你喜欢）- 一直显示 -->
          <div class="recommendation-section">
            <h2 class="section-title"><i class="fas fa-briefcase"></i> 推荐兼职</h2>
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
          <div class="sidebar-card">
            <h3>热门分类</h3>
            <ul>
              <li v-for="item in hotCategories" :key="item" @click="filterByCategory(item)">{{ item }}</li>
            </ul>
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

          <div class="sidebar-card contact-card">
            <h3>上大学Online</h3>
            <p>我们致力于构建安全、可靠的校园兼职服务平台，帮助学生积累工作经验，丰富校园生活。</p>
            <div class="contact-info">
              <p>服务热线：400-123-4567</p>
              <p>兼职服务客服：parttime@campus.edu.cn</p>
              <p>问题反馈：feedback@campus.edu.cn</p>
            </div>
            <div class="quick-links">
              <router-link to="/">首页</router-link>
              <a href="#">发布指南</a>
              <a href="#">安全提示</a>
              <a href="#">纠纷处理</a>
            </div>
          </div>
        </aside>
      </section>
    </div>
    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router' // 引入VueRouter
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

// 初始化路由实例
const router = useRouter()

// 基础数据定义
const types = ['实习', '兼职', '家教', '促销', '调研', '其他']
const locations = ['校内', '学校周边', '市中心', '远程']
const timeOptions = ['工作日', '周末', '时间灵活']

// 筛选条件（TS类型约束）
interface Filters {
  search: string
  type: string
  location: string
  salaryMin: string
  salaryMax: string
  time: string
}

const filters = reactive<Filters>({
  search: '',
  type: '',
  location: '',
  salaryMin: '',
  salaryMax: '',
  time: ''
})

// 兼职数据类型定义
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

// 原始兼职数据
const jobs = ref<Job[]>([
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
])

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

// 搜索/筛选结果（用于搜索匹配盒子）
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

// 推荐兼职（猜你喜欢，始终显示，排除已筛选结果避免重复）
const recommendedJobs = computed(() => {
  if (hasSearchOrFilter.value) {
    const filteredIds = searchFilteredJobs.value.map(j => j.id)
    return jobs.value.filter(job => !filteredIds.includes(job.id))
  }
  return jobs.value
})

// 筛选触发（空函数，computed已自动处理）
const handleFilter = () => {}

// 重置筛选条件
const handleReset = () => {
  filters.search = ''
  filters.type = ''
  filters.location = ''
  filters.salaryMin = ''
  filters.salaryMax = ''
  filters.time = ''
}

// 快捷操作（集成VueRouter跳转）
const handleQuick = (type: string) => {
  switch (type) {
    case 'publish':
      router.push('/parttime/publish') // 兼职发布页
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
}
</script>

<!-- 全局样式（解决配色变量可见性） -->
<style>
/* 全局引入Font Awesome图标 */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css');

/* 全局配色变量（确保100%可见） */
:root {
  --primary: #d81b60 !important;
  --primary-dark: #c2185b !important;
  --text: #333 !important;
  --muted: #666 !important;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%) !important;
  --card-shadow: 0 4px 18px rgba(216, 27, 96, 0.12) !important;
  --border: #f1d9e9 !important;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Arial', 'Microsoft YaHei', sans-serif;
}

a {
  text-decoration: none;
  color: inherit;
  transition: all 0.3s;
}
</style>

<!-- 组件内样式（scoped） -->
<style scoped>
.parttime-page {
  min-height: 100vh;
  background: var(--bg);
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
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 10px 20px;
  border-radius: 12px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.action-btn:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
  transform: translateY(-2px);
}

.action-btn:active {
  background: var(--primary-dark);
  border-color: var(--primary-dark);
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(216, 27, 96, 0.25);
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
  border: 2px solid var(--border);
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
  background: var(--primary);
  color: white;
  border-color: var(--primary);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
  transform: translateY(-2px);
}

.btn:active {
  background: var(--primary-dark);
  border-color: var(--primary-dark);
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(216, 27, 96, 0.25);
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

/* 推荐兼职区域 */
.recommendation-section {
  background: white;
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
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #eee;
  cursor: pointer;
}

.job-card:hover {
  box-shadow: 0 4px 20px rgba(216, 27, 96, 0.15);
  transform: translateY(-2px);
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
  background: #f9f0ff;
  color: var(--primary);
  border-radius: 4px;
  font-size: 12px;
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
  color: var(--primary);
  font-size: 12px;
}

.salary {
  font-size: 16px;
  font-weight: bold;
  color: var(--primary);
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
  border: 1px solid var(--primary);
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.btn-detail:hover {
  background: var(--primary);
  color: white;
}

.btn-apply {
  padding: 6px 16px;
  border-radius: 6px;
  border: none;
  background: var(--primary);
  color: white;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.btn-apply:hover {
  background: var(--primary-dark);
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
  background: var(--primary);
  color: white;
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

.contact-card {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  color: white;
}

.contact-card h3 {
  color: white;
  border-bottom-color: rgba(255, 255, 255, 0.3);
}

.contact-card p {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.8;
  margin-bottom: 15px;
}

.contact-info {
  margin: 15px 0;
}

.contact-info p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin-bottom: 8px;
}

.contact-card .quick-links a {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.contact-card .quick-links a:hover {
  background: white;
  color: var(--primary);
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