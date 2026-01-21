<template>
  <div class="consume-record-page">
    <NavBar />
    <div class="record-container">
      <div class="page-header">
        <h1 class="page-title">消费记录</h1>
        <p class="page-subtitle">查看您的校园卡消费明细</p>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon income">
            <i class="fas fa-plus"></i>
          </div>
          <div class="stat-info">
            <p class="stat-label">本月收入</p>
            <p class="stat-value">¥{{ monthlyIncome.toFixed(2) }}</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon expense">
            <i class="fas fa-minus"></i>
          </div>
          <div class="stat-info">
            <p class="stat-label">本月支出</p>
            <p class="stat-value">¥{{ monthlyExpense.toFixed(2) }}</p>
          </div>
        </div>
      </div>

      <!-- 筛选和搜索 -->
      <div class="filter-section">
        <div class="filter-tabs">
          <button
            v-for="tab in filterTabs"
            :key="tab.key"
            class="filter-tab"
            :class="{ active: currentFilter === tab.key }"
            @click="switchFilter(tab.key)"
          >
            {{ tab.label }}
          </button>
        </div>
        <div class="search-box">
          <i class="fas fa-search"></i>
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索交易记录..."
            class="search-input"
          />
        </div>
      </div>

      <!-- 记录列表 -->
      <div class="records-list">
        <div v-if="filteredRecords.length > 0">
          <div
            v-for="record in filteredRecords"
            :key="record.id"
            class="record-item"
            @click="viewDetail(record.id)"
          >
            <div class="record-icon" :class="record.type">
              <i :class="getRecordIcon(record.type)"></i>
            </div>
            <div class="record-info">
              <h4 class="record-title">{{ record.title }}</h4>
              <p class="record-desc">{{ record.description }}</p>
              <p class="record-time">{{ record.time }}</p>
            </div>
            <div class="record-amount" :class="record.type">
              <span v-if="record.type === 'income'">+</span>
              <span v-else>-</span>
              ¥{{ Math.abs(record.amount).toFixed(2) }}
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <i class="fas fa-receipt empty-icon"></i>
          <p class="empty-text">暂无交易记录</p>
        </div>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const router = useRouter()

// 本月统计
const monthlyIncome = ref(500.00)
const monthlyExpense = ref(320.50)

// 筛选标签
const filterTabs = [
  { key: 'all', label: '全部' },
  { key: 'income', label: '收入' },
  { key: 'expense', label: '支出' }
]

const currentFilter = ref('all')
const searchKeyword = ref('')

// 交易记录（模拟数据）
const records = ref([
  {
    id: 1,
    type: 'income',
    title: '账户充值',
    description: '支付宝充值',
    amount: 100.00,
    time: '2024-12-01 10:30'
  },
  {
    id: 2,
    type: 'expense',
    title: '食堂消费',
    description: '第一食堂',
    amount: -15.50,
    time: '2024-12-01 12:00'
  },
  {
    id: 3,
    type: 'expense',
    title: '图书馆借书',
    description: '图书借阅费用',
    amount: -5.00,
    time: '2024-11-30 14:20'
  },
  {
    id: 4,
    type: 'income',
    title: '账户充值',
    description: '微信充值',
    amount: 200.00,
    time: '2024-11-29 09:15'
  }
])

// 筛选后的记录
const filteredRecords = computed(() => {
  let result = records.value

  // 按类型筛选
  if (currentFilter.value !== 'all') {
    result = result.filter(r => r.type === currentFilter.value)
  }

  // 搜索筛选
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(r =>
      r.title.toLowerCase().includes(keyword) ||
      r.description.toLowerCase().includes(keyword)
    )
  }

  return result
})

// 切换筛选
const switchFilter = (key: string) => {
  currentFilter.value = key
}

// 获取记录图标
const getRecordIcon = (type: string) => {
  return type === 'income' ? 'fas fa-plus-circle' : 'fas fa-minus-circle'
}

// 查看详情
const viewDetail = (id: number) => {
  router.push(`/ecard/transaction-detail/${id}`)
}
</script>

<style scoped>
.consume-record-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff5f7 0%, #f0f9ff 100%);
  display: flex;
  flex-direction: column;
}

.record-container {
  flex: 1;
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
  width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.income {
  background: linear-gradient(135deg, #4ecdc4, #44a08d);
}

.stat-icon.expense {
  background: linear-gradient(135deg, #ffb6c1, #ffc0cb);
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

/* 筛选区域 */
.filter-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.15);
}

.filter-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.filter-tab {
  flex: 1;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  background: white;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-tab:hover {
  border-color: #ffb6c1;
  color: #ffb6c1;
}

.filter-tab.active {
  background: linear-gradient(135deg, #ffb6c1, #ffc0cb);
  color: white;
  border-color: #ffb6c1;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  background: #f9f9f9;
}

.search-box i {
  color: #999;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  color: #333;
}

/* 记录列表 */
.records-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 8px rgba(148, 163, 184, 0.1);
}

.record-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(148, 163, 184, 0.2);
}

.record-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.record-icon.income {
  background: linear-gradient(135deg, #4ecdc4, #44a08d);
}

.record-icon.expense {
  background: linear-gradient(135deg, #ffb6c1, #ffc0cb);
}

.record-info {
  flex: 1;
}

.record-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.record-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 4px;
}

.record-time {
  font-size: 12px;
  color: #ccc;
}

.record-amount {
  font-size: 18px;
  font-weight: 700;
}

.record-amount.income {
  color: #4ecdc4;
}

.record-amount.expense {
  color: #ffb6c1;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.15);
}

.empty-icon {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #999;
}

@media (max-width: 768px) {
  .record-container {
    padding: 20px 12px;
  }

  .page-title {
    font-size: 24px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>

