<template>
  <div class="admin-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-cog"></i> 管理中心
        </h1>
        <p class="page-subtitle">平台管理和数据统计</p>
      </section>

      <!-- 统计卡片 -->
      <section class="stats-section">
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon user">
              <i class="fas fa-users"></i>
            </div>
            <div class="stat-content">
              <h3 class="stat-value">{{ stats.totalUsers }}</h3>
              <p class="stat-label">总用户数</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon product">
              <i class="fas fa-shopping-bag"></i>
            </div>
            <div class="stat-content">
              <h3 class="stat-value">{{ stats.totalProducts }}</h3>
              <p class="stat-label">商品总数</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon job">
              <i class="fas fa-briefcase"></i>
            </div>
            <div class="stat-content">
              <h3 class="stat-value">{{ stats.totalJobs }}</h3>
              <p class="stat-label">兼职岗位</p>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon order">
              <i class="fas fa-chart-line"></i>
            </div>
            <div class="stat-content">
              <h3 class="stat-value">{{ stats.totalOrders }}</h3>
              <p class="stat-label">交易订单</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 功能模块 -->
      <section class="modules-section">
        <h2 class="section-title">功能模块</h2>
        <div class="modules-grid">
          <div class="module-card" @click="goToModule('users')">
            <div class="module-icon">
              <i class="fas fa-user-cog"></i>
            </div>
            <h3 class="module-title">用户管理</h3>
            <p class="module-desc">管理用户账号和权限</p>
          </div>
          <div class="module-card" @click="goToModule('products')">
            <div class="module-icon">
              <i class="fas fa-box"></i>
            </div>
            <h3 class="module-title">商品管理</h3>
            <p class="module-desc">审核和管理二手商品</p>
          </div>
          <div class="module-card" @click="goToModule('jobs')">
            <div class="module-icon">
              <i class="fas fa-briefcase"></i>
            </div>
            <h3 class="module-title">兼职管理</h3>
            <p class="module-desc">审核和管理兼职岗位</p>
          </div>
          <div class="module-card" @click="goToModule('orders')">
            <div class="module-icon">
              <i class="fas fa-receipt"></i>
            </div>
            <h3 class="module-title">订单管理</h3>
            <p class="module-desc">查看和处理交易订单</p>
          </div>
          <div class="module-card" @click="goToModule('reports')">
            <div class="module-icon">
              <i class="fas fa-flag"></i>
            </div>
            <h3 class="module-title">举报处理</h3>
            <p class="module-desc">处理用户举报和投诉</p>
          </div>
          <div class="module-card" @click="goToModule('settings')">
            <div class="module-icon">
              <i class="fas fa-cog"></i>
            </div>
            <h3 class="module-title">系统设置</h3>
            <p class="module-desc">平台配置和参数设置</p>
          </div>
        </div>
      </section>

      <!-- 最近活动 -->
      <section class="activity-section">
        <h2 class="section-title">最近活动</h2>
        <div class="activity-list">
          <div
            v-for="activity in recentActivities"
            :key="activity.id"
            class="activity-item"
          >
            <div class="activity-icon">
              <i :class="activity.icon"></i>
            </div>
            <div class="activity-content">
              <h4 class="activity-title">{{ activity.title }}</h4>
              <p class="activity-desc">{{ activity.description }}</p>
              <span class="activity-time">{{ activity.time }}</span>
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

// 统计数据
const stats = ref({
  totalUsers: 1234,
  totalProducts: 5678,
  totalJobs: 890,
  totalOrders: 3456
})

// 最近活动
const recentActivities = ref([
  {
    id: 1,
    icon: 'fas fa-user-plus',
    title: '新用户注册',
    description: '张同学注册了账号',
    time: '5分钟前'
  },
  {
    id: 2,
    icon: 'fas fa-shopping-bag',
    title: '商品发布',
    description: '李同学发布了新商品',
    time: '10分钟前'
  },
  {
    id: 3,
    icon: 'fas fa-flag',
    title: '举报处理',
    description: '处理了1条用户举报',
    time: '1小时前'
  },
  {
    id: 4,
    icon: 'fas fa-check-circle',
    title: '认证审核',
    description: '通过了5条身份认证',
    time: '2小时前'
  }
])

// 跳转到功能模块
const goToModule = (module: string) => {
  // TODO: 实现各个模块的跳转
  alert(`${module} 管理功能开发中...`)
  // router.push(`/admin/${module}`)
}

onMounted(() => {
  // 加载统计数据
  // loadStats()
  // loadRecentActivities()
})
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --primary-dark: #c2185b;
  --text: #333;
  --muted: #666;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.admin-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  color: var(--text);
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
  color: var(--muted);
}

/* 统计卡片 */
.stats-section {
  margin-bottom: 40px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.15);
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
}

.stat-icon.user {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.product {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.job {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.order {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: var(--muted);
}

/* 功能模块 */
.modules-section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 20px;
}

.modules-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.module-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.module-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(216, 27, 96, 0.15);
}

.module-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
  font-size: 36px;
}

.module-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 8px;
}

.module-desc {
  font-size: 14px;
  color: var(--muted);
}

/* 最近活动 */
.activity-section {
  margin-bottom: 40px;
}

.activity-list {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.activity-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  background: #f9f0ff;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f9f0ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
  font-size: 18px;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}

.activity-desc {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: var(--muted);
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .modules-grid {
    grid-template-columns: 1fr;
  }
}
</style>
