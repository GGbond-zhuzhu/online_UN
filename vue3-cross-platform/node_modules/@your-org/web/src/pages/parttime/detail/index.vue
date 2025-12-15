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

      <!-- 公司信息 -->
      <section class="company-card">
        <h2 class="card-title">公司信息</h2>
        <div class="company-details">
          <div class="company-header">
            <div class="company-logo">
              <i class="fas fa-building"></i>
            </div>
            <div class="company-info">
              <div class="company-name-large">{{ job.company }}</div>
              <div class="company-type">{{ job.companyType }}</div>
            </div>
          </div>
          <div class="company-desc">
            <p>{{ job.companyDescription }}</p>
          </div>
          <div class="company-stats">
            <div class="stat-item">
              <span class="stat-label">规模</span>
              <span class="stat-value">{{ job.companySize }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">行业</span>
              <span class="stat-value">{{ job.industry }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">成立时间</span>
              <span class="stat-value">{{ job.established }}</span>
            </div>
          </div>
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
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const route = useRoute()
const router = useRouter()

const isFavorite = ref(false)

// 岗位数据
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
})

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

// 方法
const goToDetail = (id: number) => {
  router.push(`/parttime/detail/${id}`)
}

const toggleFavorite = async () => {
  try {
    isFavorite.value = !isFavorite.value
    // TODO: 调用收藏API
    // await toggleFavoriteAPI(job.value.id)
  } catch (error) {
    console.error('收藏操作失败:', error)
    // 回滚状态
    isFavorite.value = !isFavorite.value
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

const handleApply = () => {
  // 跳转到申请页面或显示申请表单
  // router.push(`/parttime/apply/${job.value.id}`)
  // 暂时显示提示信息
  const confirmed = confirm(`确定要申请"${job.value.title}"这个岗位吗？`)
  if (confirmed) {
    // TODO: 调用申请API
    // await applyJobAPI(job.value.id)
    alert('申请已提交，请等待审核')
  }
}

onMounted(() => {
  // 从路由参数获取岗位ID
  const jobId = route.params.id
  if (jobId) {
    // 根据ID加载岗位数据
    // loadJob(jobId)
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
