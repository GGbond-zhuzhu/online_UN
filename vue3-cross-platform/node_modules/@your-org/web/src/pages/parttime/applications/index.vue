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
// 引入 Vue 的组合式 API，用于管理响应式数据、计算属性和生命周期
import { ref, computed, onMounted } from 'vue' // 从 vue 导入 ref、computed 和 onMounted
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
// 引入公共兼职 Store，统一管理“我的兼职申请”记录
import { useParttimeStore } from '@campus/common'

const router = useRouter() // 获取路由实例，用于页面跳转

// 当前选中的申请状态筛选（all：全部；pending：待审核；approved：已通过；rejected：已拒绝）
const filterStatus = ref<'all' | 'pending' | 'approved' | 'rejected'>('all') // 默认展示全部申请记录

// 通过公共 Store 统一管理“我的兼职申请”记录
const parttimeStore = useParttimeStore() // 调用 useParttimeStore 获取 Store 实例

// 将 Store 中的原始申请记录映射为页面需要展示的结构
const applications = computed(() =>
  parttimeStore.myApplicationList.map((item: any) => ({
    id: item.id as number, // 申请记录ID
    jobId: item.jobId as number, // 对应的兼职岗位ID
    jobTitle: item.jobTitle as string, // 岗位标题
    companyName: item.companyName as string, // 公司名称
    status: (item.status as 'pending' | 'approved' | 'rejected') || 'pending', // 申请状态
    applyTime: (item.applyTime as string) || '', // 申请时间
    reviewTime: (item.reviewTime as string) || '', // 审核时间（可能为空）
    reviewComment: (item.reviewComment as string) || '' // 审核意见（可能为空）
  }))
)

// 筛选后的申请记录
const filteredApplications = computed(() => {
  if (filterStatus.value === 'all') {
    return applications.value // 当选择“全部”时，直接返回完整申请列表
  }
  // 根据当前选中的状态进行过滤，只展示对应状态的申请记录
  return applications.value.filter((app) => app.status === filterStatus.value)
})

// 获取状态标签（将后端返回的英文状态映射为中文文案）
const getStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待审核', // 等待企业或管理员处理
    approved: '已通过', // 申请已审核通过
    rejected: '已拒绝' // 申请已被拒绝
  }
  return statusMap[status] || status // 未知状态则直接返回原值
}

// 跳转到岗位详情
const goToDetail = (jobId: number) => {
  router.push(`/parttime/detail/${jobId}`) // 跳转到对应的兼职详情页
}

// 取消申请（调用公共 Store 的统一方法，同时更新“我的申请”列表）
const cancelApplication = async (id: number) => {
  // 弹出确认对话框，避免用户误操作取消已提交的申请
  if (!confirm('确定要取消这个申请吗？')) {
    return // 用户取消操作则直接返回
  }
  try {
    // 通过兼职 Store 调用统一的“取消报名”方法（内部会请求后端并从 myApplicationList 中移除该记录）
    await parttimeStore.cancelApplication(id) // 传入申请记录 ID
  } catch (error) {
    console.error('取消兼职申请失败:', error) // 控制台打印错误日志，便于排查问题
    alert('取消申请失败，请稍后重试') // 给用户一个通用失败提示
  }
}

// 去浏览兼职
const goToParttime = () => {
  router.push('/parttime') // 跳转到兼职列表页面，方便用户继续浏览岗位并发起申请
}

// 组件挂载时，从后端加载“我的兼职申请”记录
onMounted(() => {
  // 通过公共 Store 的加载方法获取我的申请列表（默认不按状态过滤，加载第 1 页、最多 50 条）
  parttimeStore
    .loadMyApplications(undefined, 1, 50)
    .catch((error) => {
      console.error('加载申请记录失败:', error) // 打印错误信息，便于调试
    })
})
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
