<template>
  <div class="history-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-history"></i> 浏览记录
        </h1>
        <p class="page-subtitle">查看您浏览过的兼职岗位</p>
      </section>

      <!-- 操作栏 -->
      <section class="actions-bar">
        <button class="btn-clear" @click="clearHistory">
          <i class="fas fa-trash"></i> 清空记录
        </button>
      </section>

      <!-- 浏览记录列表 -->
      <section class="history-section">
        <div v-if="historyList.length === 0" class="empty-state">
          <i class="fas fa-history"></i>
          <h3>暂无浏览记录</h3>
          <p>您还没有浏览过任何兼职岗位</p>
          <button class="btn-browse" @click="goToParttime">
            <i class="fas fa-search"></i> 去浏览兼职
          </button>
        </div>
        <div v-else class="history-list">
          <div
            v-for="item in historyList"
            :key="item.id"
            class="history-card"
            @click="goToDetail(item.jobId)"
          >
            <div class="job-info">
              <h3 class="job-title">{{ item.jobTitle }}</h3>
              <p class="company-name">{{ item.companyName }}</p>
              <div class="job-meta">
                <span class="salary">{{ item.salary }}</span>
                <span class="location">
                  <i class="fas fa-map-marker-alt"></i> {{ item.location }}
                </span>
              </div>
            </div>
            <div class="history-meta">
              <span class="view-time">{{ item.viewTime }}</span>
              <button class="btn-remove" @click.stop="removeItem(item.id)">
                <i class="fas fa-times"></i>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()

// 浏览记录
const historyList = ref([
  {
    id: 1,
    jobId: 1,
    jobTitle: '校园推广专员',
    companyName: '某教育公司',
    salary: '150-200元/天',
    location: '校内',
    viewTime: '2小时前'
  },
  {
    id: 2,
    jobId: 2,
    jobTitle: '数据录入员',
    companyName: '某科技公司',
    salary: '20-30元/小时',
    location: '线上',
    viewTime: '1天前'
  },
  {
    id: 3,
    jobId: 3,
    jobTitle: '客服助理',
    companyName: '某服务公司',
    salary: '3000-4000元/月',
    location: '校外',
    viewTime: '3天前'
  }
])

// 跳转到岗位详情
const goToDetail = (jobId: number) => {
  router.push(`/parttime/detail/${jobId}`)
}

// 移除单条记录
const removeItem = (id: number) => {
  const index = historyList.value.findIndex(item => item.id === id)
  if (index > -1) {
    historyList.value.splice(index, 1)
    // TODO: 调用API删除记录
    // await removeHistoryAPI(id)
  }
}

// 清空记录
const clearHistory = () => {
  if (confirm('确定要清空所有浏览记录吗？')) {
    historyList.value = []
    // TODO: 调用API清空记录
    // await clearHistoryAPI()
  }
}

// 去浏览兼职
const goToParttime = () => {
  router.push('/parttime')
}

onMounted(() => {
  // 加载浏览记录
  // loadHistory()
})
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.history-page {
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

.actions-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.btn-clear {
  padding: 10px 20px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  background: white;
  color: #ff4d4f;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-clear:hover {
  background: #fff1f0;
  border-color: #ff4d4f;
}

.history-section {
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

.history-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.history-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.history-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.15);
}

.job-info {
  flex: 1;
}

.job-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.company-name {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.job-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
}

.salary {
  color: var(--primary);
  font-weight: 600;
}

.location {
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.history-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
}

.view-time {
  font-size: 12px;
  color: #999;
}

.btn-remove {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: #f5f5f5;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-remove:hover {
  background: #ff4d4f;
  color: white;
}

@media (max-width: 768px) {
  .history-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .history-meta {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
