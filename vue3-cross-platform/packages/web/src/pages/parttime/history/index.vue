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
// 引入 Vue 的组合式 API，用于创建响应式数据和处理生命周期
import { computed, onMounted } from 'vue' // 从 vue 中导入 computed 和 onMounted
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
// 引入公共兼职 Store，统一管理兼职浏览记录状态和接口调用
import { useParttimeStore } from '@campus/common' // 从 @campus/common 导入 useParttimeStore

const router = useRouter()

// 通过公共 Store 统一获取和管理兼职浏览记录
const parttimeStore = useParttimeStore() // 调用 useParttimeStore 获取全局兼职 Store 实例

// 将 Store 中的浏览记录映射为当前页面需要展示的结构
const historyList = computed(() => {
  // 直接使用 Store 中的浏览记录数组（字段与页面展示基本一致）
  return parttimeStore.browseHistory.map((item) => ({
    id: item.id, // 浏览记录ID
    jobId: item.jobId, // 对应的兼职岗位ID
    jobTitle: item.jobTitle, // 岗位标题
    companyName: item.companyName, // 公司名称
    salary: item.salary, // 薪资信息（已为字符串，例如“20-30元/小时”）
    location: item.location, // 工作地点（校内/校外/线上等）
    viewTime: item.viewTime // 浏览时间（后端已格式化为可读字符串）
  }))
})

// 跳转到岗位详情
const goToDetail = (jobId: number) => {
  router.push(`/parttime/detail/${jobId}`)
}

// 移除单条记录
const removeItem = async (id: number) => {
  try {
    // 调用公共 Store 提供的删除浏览记录方法，自动同步更新全局状态
    await parttimeStore.removeBrowseHistoryItem(id) // 根据记录ID删除对应的浏览记录
  } catch (error) {
    console.error('删除浏览记录失败:', error) // 打印错误信息，便于调试
    alert('删除浏览记录失败，请稍后重试') // 提示用户操作失败
  }
}

// 清空记录
const clearHistory = async () => {
  // 如果当前没有任何记录，则不需要执行清空逻辑
  if (!historyList.value.length) {
    return // 直接返回
  }
  // 弹出确认对话框，防止误操作
  if (!confirm('确定要清空所有浏览记录吗？')) {
    return // 用户取消，则不执行后续逻辑
  }
  try {
    // 调用公共 Store 提供的“清空浏览记录”方法
    await parttimeStore.clearBrowseHistoryAll() // 调用后端接口并清空本地状态
  } catch (error) {
    console.error('清空浏览记录失败:', error) // 打印错误信息
    alert('清空浏览记录失败，请稍后重试') // 提示用户操作失败
  }
}

// 去浏览兼职
const goToParttime = () => {
  router.push('/parttime') // 跳转到兼职列表页面，方便用户继续浏览岗位
}

// 组件挂载时，从后端加载真实的兼职浏览记录数据
onMounted(() => {
  // 通过公共 Store 提供的加载方法，从后端获取第 1 页、最多 50 条浏览记录
  parttimeStore.loadBrowseHistory(1, 50) // 初始化时拉取浏览记录填充到页面
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
