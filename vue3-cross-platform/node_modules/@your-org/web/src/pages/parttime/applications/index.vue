<template>
  <div class="applications-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-file-alt"></i> 申请记录
        </h1>
        <p class="page-subtitle">查看我的兼职申请记录</p>
      </section>

      <!-- 筛选栏 -->
      <section class="filter-section">
        <div class="filter-tabs">
          <button
            :class="['filter-tab', { active: filterStatus === 'all' }]"
            @click="filterStatus = 'all'"
          >
            全部
          </button>
          <button
            :class="['filter-tab', { active: filterStatus === 'pending' }]"
            @click="filterStatus = 'pending'"
          >
            待审核
          </button>
          <button
            :class="['filter-tab', { active: filterStatus === 'approved' }]"
            @click="filterStatus = 'approved'"
          >
            已通过
          </button>
          <button
            :class="['filter-tab', { active: filterStatus === 'rejected' }]"
            @click="filterStatus = 'rejected'"
          >
            已拒绝
          </button>
        </div>
      </section>

      <!-- 申请列表 -->
      <section class="applications-section">
        <div v-if="filteredApplications.length === 0" class="empty-state">
          <i class="fas fa-file-alt"></i>
          <h3>暂无申请记录</h3>
          <p>您还没有申请过任何兼职岗位</p>
          <button class="btn-browse" @click="goToParttime">
            <i class="fas fa-search"></i> 去浏览兼职
          </button>
        </div>
        <div v-else class="applications-list">
          <div
            v-for="application in filteredApplications"
            :key="application.id"
            class="application-card"
          >
            <div class="application-header">
              <div class="job-info">
                <h3 class="job-title" @click="goToDetail(application.jobId)">
                  {{ application.jobTitle }}
                </h3>
                <p class="company-name">{{ application.companyName }}</p>
              </div>
              <div :class="['status-badge', application.status]">
                {{ getStatusLabel(application.status) }}
              </div>
            </div>
            <div class="application-content">
              <div class="info-row">
                <span class="info-label">申请时间：</span>
                <span class="info-value">{{ application.applyTime }}</span>
              </div>
              <div class="info-row" v-if="application.reviewTime">
                <span class="info-label">审核时间：</span>
                <span class="info-value">{{ application.reviewTime }}</span>
              </div>
              <div class="info-row" v-if="application.reviewComment">
                <span class="info-label">审核意见：</span>
                <span class="info-value">{{ application.reviewComment }}</span>
              </div>
            </div>
            <div class="application-actions">
              <button class="btn-view" @click="goToDetail(application.jobId)">
                <i class="fas fa-eye"></i> 查看岗位
              </button>
              <button
                v-if="application.status === 'pending'"
                class="btn-cancel"
                @click="cancelApplication(application.id)"
              >
                <i class="fas fa-times"></i> 取消申请
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()

const filterStatus = ref<'all' | 'pending' | 'approved' | 'rejected'>('all')

// 申请记录
const applications = ref([
  {
    id: 1,
    jobId: 1,
    jobTitle: '校园推广专员',
    companyName: '某教育公司',
    status: 'pending',
    applyTime: '2024-01-15 10:30',
    reviewTime: '',
    reviewComment: ''
  },
  {
    id: 2,
    jobId: 2,
    jobTitle: '数据录入员',
    companyName: '某科技公司',
    status: 'approved',
    applyTime: '2024-01-10 14:20',
    reviewTime: '2024-01-11 09:15',
    reviewComment: '审核通过，请尽快联系HR'
  },
  {
    id: 3,
    jobId: 3,
    jobTitle: '客服助理',
    companyName: '某服务公司',
    status: 'rejected',
    applyTime: '2024-01-08 16:45',
    reviewTime: '2024-01-09 11:30',
    reviewComment: '不符合岗位要求'
  }
])

// 筛选后的申请记录
const filteredApplications = computed(() => {
  if (filterStatus.value === 'all') {
    return applications.value
  }
  return applications.value.filter(app => app.status === filterStatus.value)
})

// 获取状态标签
const getStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return statusMap[status] || status
}

// 跳转到岗位详情
const goToDetail = (jobId: number) => {
  router.push(`/parttime/detail/${jobId}`)
}

// 取消申请
const cancelApplication = (id: number) => {
  if (confirm('确定要取消这个申请吗？')) {
    const index = applications.value.findIndex(app => app.id === id)
    if (index > -1) {
      applications.value.splice(index, 1)
      // TODO: 调用取消申请API
      // await cancelApplicationAPI(id)
    }
  }
}

// 去浏览兼职
const goToParttime = () => {
  router.push('/parttime')
}
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.applications-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-title i {
  color: var(--primary);
}

.page-subtitle {
  font-size: 16px;
  color: #666;
}

.filter-section {
  margin-bottom: 30px;
}

.filter-tabs {
  display: flex;
  gap: 10px;
  background: white;
  padding: 10px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.filter-tab {
  flex: 1;
  padding: 10px 20px;
  border: none;
  background: transparent;
  color: #666;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}

.filter-tab:hover {
  background: #f9f0ff;
}

.filter-tab.active {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
}

.applications-section {
  margin-bottom: 30px;
}

.empty-state {
  background: white;
  border-radius: 12px;
  padding: 60px 20px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.empty-state i {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 10px;
}

.empty-state p {
  color: #666;
  margin-bottom: 20px;
}

.btn-browse {
  padding: 12px 24px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-browse:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
}

.applications-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.application-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.application-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.job-info {
  flex: 1;
}

.job-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  cursor: pointer;
  transition: color 0.3s;
}

.job-title:hover {
  color: var(--primary);
}

.company-name {
  font-size: 14px;
  color: #666;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.pending {
  background: #fff7e6;
  color: #fa8c16;
}

.status-badge.approved {
  background: #f6ffed;
  color: #52c41a;
}

.status-badge.rejected {
  background: #fff1f0;
  color: #ff4d4f;
}

.application-content {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-label {
  color: #666;
  min-width: 80px;
}

.info-value {
  color: #333;
  flex: 1;
}

.application-actions {
  display: flex;
  gap: 10px;
}

.btn-view,
.btn-cancel {
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-view {
  background: #f9f0ff;
  color: var(--primary);
  border: 2px solid rgba(240, 240, 240, 0.9);
}

.btn-view:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.btn-cancel {
  background: white;
  color: #ff4d4f;
  border: 2px solid rgba(255, 77, 79, 0.3);
}

.btn-cancel:hover {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

@media (max-width: 768px) {
  .filter-tabs {
    flex-wrap: wrap;
  }

  .application-header {
    flex-direction: column;
    gap: 10px;
  }

  .application-actions {
    flex-direction: column;
  }

  .btn-view,
  .btn-cancel {
    width: 100%;
    justify-content: center;
  }
}
</style>
