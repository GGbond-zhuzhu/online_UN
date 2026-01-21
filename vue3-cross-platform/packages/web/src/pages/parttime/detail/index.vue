<template>
  <div class="detail-page">
    <NavBar />

    <div class="page-container">
      <!-- 岗位基本信息卡片 -->
      <section class="job-header-card">
        <div class="job-title-section">
          <h1 class="job-title">{{ job.title }}</h1>
          <div class="company-info">
            <i class="fas fa-building"></i>
            <span class="company-name">{{ job.company }}</span>
            <span class="company-verified" v-if="job.verified">
              <i class="fas fa-check-circle"></i> 已认证
            </span>
          </div>
        </div>
        <div class="job-meta">
          <div class="meta-item">
            <i class="fas fa-map-marker-alt"></i>
            <span>{{ job.location }}</span>
          </div>
          <div class="meta-item">
            <i class="fas fa-clock"></i>
            <span>{{ job.time }}</span>
          </div>
          <div class="meta-item">
            <i class="fas fa-tag"></i>
            <span>{{ job.type }}</span>
          </div>
          <div class="meta-item">
            <i class="fas fa-calendar"></i>
            <span>发布时间：{{ job.publishTime }}</span>
          </div>
        </div>
        <div class="salary-section">
          <span class="salary-label">薪资待遇</span>
          <span class="salary-value">¥{{ job.salary }}</span>
        </div>
        <div class="job-tags">
          <span class="tag" v-for="tag in job.tags" :key="tag">{{ tag }}</span>
        </div>
      </section>

      <!-- 岗位详情 -->
      <section class="job-detail-card">
        <h2 class="card-title">岗位详情</h2>
        <div class="detail-content">
          <div class="detail-item">
            <label>岗位描述：</label>
            <p>{{ job.description }}</p>
          </div>
          <div class="detail-item">
            <label>工作内容：</label>
            <ul class="work-content-list">
              <li v-for="(item, idx) in job.workContent" :key="idx">{{ item }}</li>
            </ul>
          </div>
          <div class="detail-item">
            <label>任职要求：</label>
            <ul class="requirement-list">
              <li v-for="(req, idx) in job.requirements" :key="idx">{{ req }}</li>
            </ul>
          </div>
          <div class="detail-item">
            <label>工作时间：</label>
            <span>{{ job.workTime }}</span>
          </div>
          <div class="detail-item">
            <label>工作地点：</label>
            <span>{{ job.workLocation }}</span>
          </div>
          <div class="detail-item">
            <label>招聘人数：</label>
            <span>{{ job.recruitCount }}人</span>
          </div>
          <div class="detail-item">
            <label>截止日期：</label>
            <span>{{ job.deadline }}</span>
          </div>
        </div>
      </section>

      <!-- 公司/发布者信息卡片（参考app端） -->
      <section class="company-card" v-if="companyName">
        <div class="company-header">
          <div class="company-avatar-wrapper">
            <div class="company-avatar" :style="{ background: getAvatarColor(companyId || 0) }"></div>
          </div>
          <div class="company-main-info">
            <div class="company-name-large">{{ companyName }}</div>
            <div class="company-rating-row">
              <div class="rating-stars">
                <i 
                  v-for="(star, index) in 5" 
                  :key="index"
                  :class="index < Math.floor(companyRating) ? 'fas fa-star' : 'far fa-star'"
                  class="star-icon"
                ></i>
              </div>
              <span class="rating-score">{{ companyRating.toFixed(1) }}</span>
              <span class="rating-count">({{ companyRatingCount }}条评价)</span>
            </div>
            <div class="company-stats">
              <div class="stat-item">
                <span class="stat-value">{{ companyJobCount }}</span>
                <span class="stat-label">在招岗位</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-value">{{ companyGoodRate }}%</span>
                <span class="stat-label">好评率</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-value">{{ companyResponseTime }}</span>
                <span class="stat-label">平均响应</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 申请通过的评价区域（参考app端） -->
      <section class="reviews-section" v-if="reviews.length > 0">
        <div class="section-header-reviews">
          <div class="section-title-row">
            <h2 class="card-title">申请通过评价</h2>
            <span class="review-count-text">({{ reviews.length }})</span>
          </div>
          <div class="filter-tabs">
            <div 
              v-for="(tab, index) in reviewTabs" 
              :key="index"
              class="filter-tab"
              :class="{ active: currentReviewTab === tab.value }"
              @click="currentReviewTab = tab.value"
            >
              {{ tab.label }}
            </div>
          </div>
        </div>

        <!-- 评价列表 -->
        <div v-if="filteredReviews.length > 0" class="reviews-list">
          <div 
            v-for="(review, index) in filteredReviews" 
            :key="index"
            class="review-item"
          >
            <div class="review-header">
              <div class="reviewer-info">
                <div class="reviewer-avatar" :style="{ background: getAvatarColor(index) }"></div>
                <div class="reviewer-details">
                  <span class="reviewer-name">{{ review.applicantName }}</span>
                  <div class="review-rating">
                    <i 
                      v-for="(star, starIndex) in 5" 
                      :key="starIndex"
                      :class="starIndex < review.rating ? 'fas fa-star' : 'far fa-star'"
                      class="review-star"
                    ></i>
                  </div>
                </div>
              </div>
              <div class="review-meta">
                <span class="review-status" :class="review.status">{{ review.statusText }}</span>
                <span class="review-time">{{ review.time }}</span>
              </div>
            </div>
            <p class="review-content">{{ review.content }}</p>
            <div v-if="review.tags && review.tags.length > 0" class="review-tags">
              <span 
                v-for="(tag, tagIndex) in review.tags" 
                :key="tagIndex"
                class="review-tag"
              >
                {{ tag }}
              </span>
            </div>
          </div>
        </div>

        <!-- 无评价提示 -->
        <div v-else class="empty-reviews">
          <div class="empty-icon">
            <i class="far fa-comment"></i>
          </div>
          <span class="empty-text">暂无评价</span>
        </div>
      </section>

      <!-- 联系方式 -->
      <section class="contact-card">
        <h2 class="card-title">联系方式</h2>
        <div class="contact-info">
          <div class="contact-item">
            <i class="fas fa-user"></i>
            <span>联系人：{{ job.contactName }}</span>
          </div>
          <div class="contact-item">
            <i class="fas fa-phone"></i>
            <span>联系电话：{{ job.contactPhone }}</span>
          </div>
          <div class="contact-item">
            <i class="fas fa-envelope"></i>
            <span>邮箱：{{ job.contactEmail }}</span>
          </div>
          <div class="contact-item">
            <i class="fas fa-map-marker-alt"></i>
            <span>地址：{{ job.contactAddress }}</span>
          </div>
        </div>
      </section>

      <!-- 推荐岗位 -->
      <section class="recommendation-section">
        <h2 class="section-title">
          <i class="fas fa-briefcase"></i> 推荐岗位
        </h2>
        <div class="job-grid">
          <div
            v-for="item in recommendedJobs"
            :key="item.id"
            class="job-card"
            @click="goToDetail(item.id)"
          >
            <div class="job-header">
              <h3 class="job-title-small">{{ item.title }}</h3>
              <div class="company-name">{{ item.company }}</div>
              <div class="job-tags">
                <span class="tag" v-for="tag in item.tags" :key="tag">{{ tag }}</span>
              </div>
            </div>
            <div class="job-info">
              <div class="info-row">
                <div class="info-item">
                  <i class="fas fa-map-marker-alt"></i>
                  <span>{{ item.location }}</span>
                </div>
                <div class="info-item">
                  <i class="fas fa-clock"></i>
                  <span>{{ item.time }}</span>
                </div>
              </div>
              <div class="salary">¥{{ item.salary }}</div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-bar">
      <button class="action-btn favorite" @click="toggleFavorite">
        <i :class="isFavorite ? 'fas fa-heart' : 'far fa-heart'"></i>
        <span>{{ isFavorite ? '已收藏' : '收藏' }}</span>
      </button>
      <button class="action-btn share" @click="shareJob">
        <i class="fas fa-share-alt"></i>
        <span>分享</span>
      </button>
      <button class="btn primary apply-btn" @click="handleApply">
        <i class="fas fa-paper-plane"></i> 立即申请
      </button>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue' // 从 vue 导入组合式 API，用于管理岗位详情页的响应式数据和生命周期
import { useRoute, useRouter } from 'vue-router' // 从 vue-router 导入路由钩子，用于获取路由参数和页面跳转
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
import {
  useParttimeStore, // 从 @campus/common 导入兼职相关的 Pinia Store，用于统一管理兼职数据和操作
  type ParttimeJob // 导入兼职岗位的类型定义，便于后续做字段映射时参考
} from '@campus/common'

const route = useRoute() // 获取当前路由对象，用于读取路径参数（例如岗位ID）
const router = useRouter() // 获取路由实例，用于在页面中做导航跳转
const parttimeStore = useParttimeStore() // 获取兼职 Store，用于统一管理当前岗位详情、收藏和浏览记录等操作

const isFavorite = ref(false) // 是否已收藏当前岗位的本地标记，用于控制按钮状态与文案
const currentJobId = ref<number | null>(null) // 当前正在查看的岗位ID，后续收藏/浏览记录都依赖该ID

// 公司/发布者信息（参考app端）
const companyName = ref('') // 公司名称
const companyId = ref<number | null>(null) // 公司ID
const companyRating = ref(4.7) // 公司评分（1-5分）
const companyRatingCount = ref(89) // 评价总数
const companyJobCount = ref(12) // 在招岗位数
const companyGoodRate = ref(96) // 好评率
const companyResponseTime = ref('1小时') // 平均响应时间

// 申请通过的评价数据（参考app端）
const reviews = ref([
  {
    id: 1,
    applicantName: '张**',
    rating: 5,
    content: '工作环境很好，公司很正规，按时发工资，推荐！',
    time: '2024-01-10',
    status: 'approved',
    statusText: '已通过',
    tags: ['按时发薪', '工作环境好', '推荐']
  },
  {
    id: 2,
    applicantName: '李**',
    rating: 5,
    content: '工作内容简单，上手快，老板人很好，很照顾学生。',
    time: '2024-01-05',
    status: 'approved',
    statusText: '已通过',
    tags: ['上手快', '老板好']
  },
  {
    id: 3,
    applicantName: '王**',
    rating: 4,
    content: '整体不错，就是工作时间有点长，但薪资还可以。',
    time: '2023-12-28',
    status: 'approved',
    statusText: '已通过',
    tags: ['薪资合理']
  }
])

// 评价筛选标签
const reviewTabs = ref([
  { label: '全部', value: 'all' },
  { label: '好评', value: 'good' },
  { label: '中评', value: 'medium' },
  { label: '差评', value: 'bad' }
])

const currentReviewTab = ref('all')

// 过滤后的评价列表
const filteredReviews = computed(() => {
  if (currentReviewTab.value === 'all') {
    return reviews.value
  } else if (currentReviewTab.value === 'good') {
    return reviews.value.filter(r => r.rating >= 4)
  } else if (currentReviewTab.value === 'medium') {
    return reviews.value.filter(r => r.rating === 3)
  } else {
    return reviews.value.filter(r => r.rating <= 2)
  }
})

// 获取头像颜色（参考app端）
const getAvatarColor = (id: number): string => {
  const colors = [
    '#FFE5E5', '#E6F3FF', '#FFF4E6', '#F0E6FF', '#E6FFE6',
    '#FFE6F0', '#E6F7FF', '#FFF9E5', '#F0E6FF', '#E6FFE6'
  ]
  return colors[id % colors.length]
}

// 岗位数据（先给出一份默认示例数据，便于在接口未返回时页面也能正常展示）
const job = ref({
  id: 1,
  title: '初中数学家教',
  company: '学而思教育',
  companyType: '教育培训',
  companySize: '1000-5000人',
  industry: '教育/培训',
  established: '2003年',
    companyDescription: '学而思教育是一家专注于中小学课外辅导的教育机构，致力于为学生提供优质的教育服务。',
    verified: true,
    publisherId: 1,
  type: '家教',
  location: '学校周边',
  time: '周末',
  salary: '80-120元/小时',
  publishTime: '2天前',
  tags: ['家教', '数学', '周末'],
  description: '辅导初中生数学，要求有相关经验，耐心负责，每周2-3次，每次2小时。',
  workContent: [
    '负责初中数学课程辅导',
    '制定个性化学习计划',
    '定期与家长沟通学习进度',
    '完成教学记录和反馈'
  ],
  requirements: [
    '数学相关专业优先',
    '有教学经验者优先',
    '耐心负责，沟通能力强',
    '周末时间充裕'
  ],
  workTime: '每周2-3次，每次2小时，具体时间可协商',
  workLocation: '学校周边或学生家中',
  recruitCount: 3,
  deadline: '2024年12月31日',
  contactName: '张老师',
  contactPhone: '138****8888',
  contactEmail: 'hr@xueersi.com',
  contactAddress: '学校周边教育大厦3楼'
}) // 默认的岗位详情数据对象，用于在接口加载前提供占位内容

// 推荐岗位
const recommendedJobs = ref([
  {
    id: 2,
    title: '英语口语陪练',
    company: 'VIPKID',
    location: '远程',
    time: '晚上/周末',
    salary: '60-100元/小时',
    tags: ['英语', '远程', '口语']
  },
  {
    id: 3,
    title: '咖啡店兼职店员',
    company: '星巴克咖啡',
    location: '市中心',
    time: '工作日晚上/周末',
    salary: '25-35元/小时',
    tags: ['餐饮', '服务', '培训']
  },
  {
    id: 4,
    title: '新媒体运营实习生',
    company: '字节跳动',
    location: '市中心',
    time: '工作日',
    salary: '150-200元/天',
    tags: ['实习', '运营', '转正机会']
  }
])

// 将后端返回的 ParttimeJob 结构映射为当前页面使用的 job 结构
const mapJobDetailToViewModel = (detail: ParttimeJob) => {
  const location = detail.location || detail.campusName || '工作地点待定' // 优先使用接口中的工作地点字段，没有则显示占位文案
  const salaryValue = detail.salary || 0 // 读取薪资数值（后端为 number 类型）
  const salaryType = detail.salaryType || '小时' // 读取薪资类型（例如“小时/天/月”），默认按小时计算
  const salaryText = salaryValue > 0 ? `${salaryValue}-${salaryValue}${salaryType === '元' ? '' : `元/${salaryType}`}` : '薪资面议' // 简单拼接出薪资展示文案

  const tags: string[] = [] // 初始化标签数组，用于构建“类型 + 地点”等小标签
  if (detail.location) tags.push(detail.location) // 有工作地点时加入一个标签
  if (detail.workTime) tags.push(detail.workTime) // 有工作时间描述时加入一个标签

  job.value = {
    ...job.value, // 先保留原有示例数据中的结构，防止缺字段导致模板报错
    id: detail.id, // 使用接口返回的岗位ID
    title: detail.title || job.value.title, // 岗位标题
    company: detail.publisherName || job.value.company, // 公司/发布方名称，缺省时沿用默认
    type: detail.type || job.value.type, // 岗位类型（例如 实习/兼职/家教）
    location, // 工作地点
    time: detail.workTime || job.value.time, // 顶部显示的时间信息（例如 工作日/周末）
    salary: salaryText, // 顶部薪资展示文案
    publishTime: job.value.publishTime, // 发布时间目前沿用默认占位，后续可接 formatRelativeTime
    tags: tags.length ? tags : job.value.tags, // 如果自动生成了标签，则覆盖默认标签
    description: detail.description || job.value.description, // 岗位描述
    workTime: detail.workTime || job.value.workTime, // 详情中的“工作时间”字段
    workLocation: location, // 详情中的“工作地点”字段
    // 其余公司信息、要求等字段目前保留默认示例内容，后端补充相应字段后可以在这里继续映射
    recruitCount: job.value.recruitCount,
    deadline: job.value.deadline,
    contactName: job.value.contactName,
    contactPhone: job.value.contactPhone,
    contactEmail: job.value.contactEmail,
    contactAddress: job.value.contactAddress,
    companyType: job.value.companyType,
    companySize: job.value.companySize,
    industry: job.value.industry,
    established: job.value.established,
    companyDescription: job.value.companyDescription,
    verified: job.value.verified
  } // 将接口数据与默认结构合并，生成最终用于页面展示的岗位详情对象
}

// 方法
const goToDetail = (id: number) => {
  router.push(`/parttime/detail/${id}`)
}

const toggleFavorite = async () => {
  try {
    const jobId = currentJobId.value || job.value.id // 优先使用路由解析出来的岗位ID，其次使用当前 job 对象中的 ID
    if (!jobId) {
      // 如果两处都拿不到有效的岗位ID，则无法调用收藏接口
      alert('当前岗位信息异常，暂时无法收藏') // 提示用户当前无法执行收藏操作
      return // 直接结束函数执行
    }

    if (!isFavorite.value) {
      // 当前未收藏 -> 通过兼职 Store 调用统一的收藏方法
      await parttimeStore.addFavorite(jobId) // 调用 Store 中封装的收藏兼职岗位方法（内部会调用公共接口）
      isFavorite.value = true // 本地状态标记为“已收藏”，更新按钮样式和文案
      alert('已收藏该兼职岗位') // 给用户成功提示
    } else {
      // 当前已收藏 -> 通过兼职 Store 调用统一的取消收藏方法
      await parttimeStore.removeFavorite(jobId) // 调用 Store 中的取消收藏方法（内部会同步更新收藏列表）
      isFavorite.value = false // 本地状态标记为“未收藏”
      alert('已取消收藏') // 给用户取消成功提示
    }
  } catch (error) {
    console.error('收藏操作失败:', error) // 控制台打印错误日志，便于排查问题
    alert('收藏操作失败，请稍后重试') // 给用户一个通用失败提示
  }
}

const shareJob = async () => {
  try {
    if (navigator.share) {
      await navigator.share({
        title: job.value.title,
        text: job.value.description,
        url: window.location.href
      })
    } else {
      // 降级方案：复制链接到剪贴板
      await navigator.clipboard.writeText(window.location.href)
      alert('链接已复制到剪贴板')
    }
  } catch (error) {
    // 用户取消分享或出错
    if ((error as Error).name !== 'AbortError') {
      console.error('分享失败:', error)
    }
  }
}

const handleApply = async () => {
  // 在提交申请前先做一次确认，避免用户误操作
  const confirmed = confirm(`确定要申请"${job.value.title}"这个岗位吗？`)
  if (!confirmed) {
    return // 用户取消操作则直接返回
  }

  // 从当前页面状态中获取岗位 ID（优先使用路由参数解析出的 ID，其次使用 job 对象中的 ID）
  const jobId = currentJobId.value || job.value.id
  if (!jobId) {
    alert('当前岗位信息异常，暂时无法提交申请') // 如果拿不到有效 ID，则提示用户稍后再试
    return
  }

  try {
    // 通过兼职 Store 调用统一的“提交报名”方法（内部会调用公共包中的 applyParttime 接口）
    await parttimeStore.applyForJob({ jobId }) // 目前只提交岗位 ID，后续可在此扩展简历、留言等字段
    alert('申请已提交，请等待审核') // 给用户一个清晰的成功提示
  } catch (error) {
    console.error('提交兼职申请失败:', error) // 控制台打印错误日志，方便排查问题
    alert('提交申请失败，请稍后重试') // 给用户一个友好的失败提示
  }
}

onMounted(() => {
  // 从路由参数中读取岗位ID（路径形如 /parttime/detail/:id）
  const rawId = route.params.id // 读取原始的路由参数（可能是字符串）
  const id = Number(rawId) // 将路由参数转换为数字，便于与接口对接
  if (!Number.isNaN(id) && id > 0) {
    // 仅当转换后的ID是一个有效正整数时才继续处理
    currentJobId.value = id // 记录当前正在查看的岗位ID，后续收藏/浏览记录都会使用
    // 使用兼职 Store 中的“加载岗位详情”方法，从公共包统一获取数据
    parttimeStore
      .loadJobDetail(id)
      .then((detail) => {
        mapJobDetailToViewModel(detail) // 将接口返回的详情数据映射到页面使用的 job 结构
        
        // 初始化公司信息（参考app端）
        const anyDetail = detail as any
        companyName.value = detail.publisherName || job.value.company
        companyId.value = detail.publisherId || anyDetail.publisherId || null
        
        // 如果后端将"是否已收藏"一并返回（例如 detail.isFavorited），可以在这里初始化收藏状态
        if (typeof anyDetail.isFavorited === 'boolean') {
          isFavorite.value = anyDetail.isFavorited // 使用后端返回的收藏状态初始化本地状态
        }
      })
      .catch((error) => {
        console.error('加载兼职详情失败:', error) // 如果接口调用失败，打印错误日志方便排查
      })
      .finally(() => {
        // 无论详情加载成功与否，都尝试记录一次浏览行为（失败不会打断用户操作）
        parttimeStore.addBrowseRecord(id).catch((error) => {
          console.error('记录兼职浏览行为失败:', error) // 记录浏览失败一般无需提示给用户，只做日志打印即可
        })
      })
  }
})
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --primary-dark: #c2185b;
  --text: #333;
  --muted: #666;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
  --card-shadow: 0 4px 18px rgba(216, 27, 96, 0.12);
  --border: #f1d9e9;
}

.detail-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 80px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 15px 40px;
}

/* 岗位头部卡片 */
.job-header-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.job-title-section {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.job-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 12px;
}

.company-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: var(--muted);
}

.company-info i {
  color: var(--primary);
}

.company-name {
  font-weight: 600;
  color: var(--text);
}

.company-verified {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #52c41a;
  font-size: 14px;
}

.company-verified i {
  color: #52c41a;
}

.job-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--muted);
}

.meta-item i {
  color: var(--primary);
  font-size: 12px;
}

.salary-section {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 15px;
  padding: 15px;
  background: #fff5f9;
  border-radius: 8px;
}

.salary-label {
  font-size: 14px;
  color: var(--muted);
}

.salary-value {
  font-size: 32px;
  font-weight: bold;
  color: var(--primary);
}

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 6px 12px;
  background: #f9f0ff;
  color: var(--primary);
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

/* 岗位详情卡片 */
.job-detail-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.card-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary);
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-item {
  display: flex;
  gap: 10px;
}

.detail-item label {
  font-weight: 600;
  color: var(--text);
  min-width: 100px;
}

.detail-item p,
.detail-item span {
  color: var(--muted);
  line-height: 1.8;
  flex: 1;
}

.work-content-list,
.requirement-list {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
}

.work-content-list li,
.requirement-list li {
  padding: 8px 0;
  padding-left: 20px;
  position: relative;
  color: var(--muted);
  line-height: 1.8;
}

.work-content-list li::before,
.requirement-list li::before {
  content: '•';
  color: var(--primary);
  font-weight: bold;
  position: absolute;
  left: 0;
}

/* 公司信息卡片 */
.company-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.company-header {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.company-avatar-wrapper {
  flex-shrink: 0;
}

.company-avatar {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.company-main-info {
  flex: 1;
}

.company-rating-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 10px 0;
}

.rating-stars {
  display: flex;
  gap: 2px;
}

.star-icon {
  font-size: 14px;
  color: #FFA500;
}

.rating-score {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.rating-count {
  font-size: 13px;
  color: #999;
}

.company-stats {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-top: 10px;
}

.company-stats .stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.company-stats .stat-value {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.company-stats .stat-label {
  font-size: 12px;
  color: #999;
}

.stat-divider {
  width: 1px;
  height: 30px;
  background: #eee;
}

/* 评价区域样式 */
.reviews-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.section-header-reviews {
  margin-bottom: 20px;
}

.section-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.review-count-text {
  font-size: 14px;
  color: #999;
}

.filter-tabs {
  display: flex;
  gap: 10px;
}

.filter-tab {
  padding: 6px 16px;
  background: #f5f5f5;
  border-radius: 15px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tab:hover {
  background: #e0e0e0;
}

.filter-tab.active {
  background: linear-gradient(135deg, #FFE5E5 0%, #FFD1D1 100%);
  color: #FF6B9D;
  font-weight: bold;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  background: #fafafa;
  border-radius: 10px;
  border: 1px solid #eee;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.reviewer-info {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}

.reviewer-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.reviewer-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.review-rating {
  display: flex;
  gap: 2px;
}

.review-star {
  font-size: 12px;
  color: #FFA500;
}

.review-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.review-status {
  padding: 4px 10px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
}

.review-status.approved {
  background: #e6f7ff;
  color: #1890ff;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.review-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
}

.review-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.review-tag {
  padding: 4px 10px;
  background: #f0f0f0;
  border-radius: 12px;
  font-size: 11px;
  color: #666;
}

.empty-reviews {
  text-align: center;
  padding: 40px 20px;
}

.empty-icon {
  font-size: 48px;
  color: #ccc;
  margin-bottom: 10px;
}

.empty-text {
  font-size: 14px;
  color: #999;
}

.company-logo {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  background: var(--primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.company-info {
  flex: 1;
}

.company-name-large {
  font-size: 18px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 6px;
}

.company-type {
  font-size: 14px;
  color: var(--muted);
}

.company-desc {
  margin-bottom: 20px;
}

.company-desc p {
  color: var(--muted);
  line-height: 1.8;
}

.company-stats {
  display: flex;
  gap: 30px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 12px;
  color: var(--muted);
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}

/* 联系方式卡片 */
.contact-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--muted);
}

.contact-item i {
  color: var(--primary);
  width: 20px;
}

/* 推荐岗位 */
.recommendation-section {
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: var(--text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title i {
  color: var(--primary);
}

.job-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.job-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #eee;
}

.job-card:hover {
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.15);
  transform: translateY(-2px);
}

.job-card .job-header {
  margin-bottom: 15px;
}

.job-card .job-title-small {
  font-size: 16px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 8px;
}

.job-card .company-name {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 8px;
}

.job-card .job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.job-card .tag {
  padding: 4px 8px;
  background: #f9f0ff;
  color: var(--primary);
  border-radius: 4px;
  font-size: 11px;
}

.job-card .job-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.job-card .info-row {
  display: flex;
  gap: 15px;
}

.job-card .info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--muted);
}

.job-card .info-item i {
  color: var(--primary);
  font-size: 10px;
}

.job-card .salary {
  font-size: 18px;
  font-weight: bold;
  color: var(--primary);
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 12px 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 10px;
  z-index: 100;
}

.action-btn {
  padding: 10px 16px;
  border-radius: 12px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-btn:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.action-btn.favorite.active {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.apply-btn {
  flex: 1;
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.apply-btn:hover {
  background: var(--primary-dark);
  border-color: var(--primary-dark);
}

.btn.primary {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

@media (max-width: 768px) {
  .job-title {
    font-size: 24px;
  }

  .salary-value {
    font-size: 28px;
  }

  .job-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .company-stats {
    flex-direction: column;
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .job-grid {
    grid-template-columns: 1fr;
  }

  .job-meta {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
