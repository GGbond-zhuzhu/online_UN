<template>
  <div class="schedule-detail-page">
    <NavBar />
    
    <div class="page-container" v-if="schedule">
      <!-- 顶部背景卡片 -->
      <div class="header-card" :style="{ background: getCategoryBgColor(schedule.type) }">
        <div class="header-content">
          <div class="header-top">
            <button class="back-btn" @click="handleBack">
              <i class="fas fa-arrow-left"></i>
            </button>
            <div class="header-actions">
              <button class="action-icon-btn" @click="handleEdit">
                <i class="fas fa-edit"></i>
              </button>
              <button class="action-icon-btn" @click="handleShare">
                <i class="fas fa-share-alt"></i>
              </button>
            </div>
          </div>
          <div class="schedule-header-info">
            <h1 class="schedule-title-main">{{ schedule.title }}</h1>
            <div class="schedule-meta-row">
              <span class="meta-tag" :style="{ background: 'rgba(255, 255, 255, 0.3)' }">
                {{ getCategoryLabel(schedule.type) }}
              </span>
              <span class="meta-status" :style="{ color: getStatusColor(schedule.status) }">
                <i class="fas fa-circle status-dot"></i>
                {{ getStatusLabel(schedule.status) }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 详情内容区域 -->
      <div class="detail-content">
        <!-- 时间信息卡片 -->
        <div class="info-card">
          <div class="card-header">
            <i class="fas fa-clock card-icon"></i>
            <span class="card-title">时间信息</span>
          </div>
          <div class="time-info">
            <div class="time-item">
              <span class="time-label">开始时间</span>
              <span class="time-value">{{ formatDateTime(schedule.startTime) }}</span>
            </div>
            <div class="time-divider"></div>
            <div class="time-item">
              <span class="time-label">结束时间</span>
              <span class="time-value">{{ formatDateTime(schedule.endTime) }}</span>
            </div>
          </div>
          <div class="duration-info" v-if="schedule.endTime">
            <i class="fas fa-hourglass-half"></i>
            <span class="duration-text">预计时长：{{ calculateDuration(schedule.startTime, schedule.endTime) }}</span>
          </div>
        </div>

        <!-- 地点信息卡片 -->
        <div class="info-card" v-if="schedule.location">
          <div class="card-header">
            <i class="fas fa-map-marker-alt card-icon"></i>
            <span class="card-title">地点信息</span>
          </div>
          <div class="location-info">
            <span class="location-text">{{ schedule.location }}</span>
            <button class="location-action" @click="handleNavigate">
              <i class="fas fa-directions"></i>
              <span>导航</span>
            </button>
          </div>
        </div>

        <!-- 描述信息卡片 -->
        <div class="info-card" v-if="schedule.description">
          <div class="card-header">
            <i class="fas fa-align-left card-icon"></i>
            <span class="card-title">描述信息</span>
          </div>
          <div class="description-content">
            <p>{{ schedule.description }}</p>
          </div>
        </div>

        <!-- 提醒信息卡片 -->
        <div class="info-card" v-if="schedule.reminderType">
          <div class="card-header">
            <i class="fas fa-bell card-icon"></i>
            <span class="card-title">提醒设置</span>
          </div>
          <div class="reminder-info">
            <span class="reminder-type">{{ getReminderTypeLabel(schedule.reminderType) }}</span>
            <span class="reminder-time" v-if="schedule.customRemindMinutes">
              {{ schedule.customRemindMinutes }}分钟前提醒
            </span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <button class="action-btn primary" @click="handleEdit">
            <i class="fas fa-edit"></i>
            编辑行程
          </button>
          <button class="action-btn" @click="handleUpdateStatus" v-if="schedule.status !== 'COMPLETED'">
            <i class="fas fa-check"></i>
            标记为已完成
          </button>
          <button class="action-btn danger" @click="handleDelete">
            <i class="fas fa-trash"></i>
            删除行程
          </button>
        </div>
      </div>
    </div>

    <div class="loading" v-else>
      <i class="fas fa-spinner fa-spin"></i>
      <p>加载中...</p>
    </div>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import {
  getPersonalScheduleDetail,
  deletePersonalSchedule,
  updatePersonalScheduleStatus,
  type PersonalSchedule
} from '@campus/common'

const router = useRouter()
const route = useRoute()

const schedule = ref<PersonalSchedule | null>(null)
const loading = ref(false)

// 加载行程详情
const loadScheduleDetail = async () => {
  try {
    loading.value = true
    const id = parseInt(route.params.id as string)
    const data = await getPersonalScheduleDetail(id)
    schedule.value = data
  } catch (error: any) {
    console.error('加载行程详情失败:', error)
    alert(error?.message || '加载失败，请稍后重试')
    router.back()
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 计算时长
const calculateDuration = (startTime: string, endTime: string) => {
  const start = new Date(startTime)
  const end = new Date(endTime)
  const diff = end.getTime() - start.getTime()
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  }
  return `${minutes}分钟`
}

// 获取分类标签
const getCategoryLabel = (type: string) => {
  const typeMap: Record<string, string> = {
    'COURSE': '课程',
    'EXAM': '考试',
    'CLUB': '社团',
    'PARTTIME': '兼职',
    'OTHER': '事务'
  }
  return typeMap[type] || '其他'
}

// 获取分类背景色
const getCategoryBgColor = (type: string) => {
  const colorMap: Record<string, string> = {
    'COURSE': '#F8BBD0',
    'EXAM': '#FFB3BA',
    'CLUB': '#E1BEE7',
    'PARTTIME': '#FFF9C4',
    'OTHER': '#B3E5FC'
  }
  return colorMap[type] || '#E1BEE7'
}

// 获取状态标签
const getStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    'COMPLETED': '已完成',
    'IN_PROGRESS': '进行中',
    'PENDING': '即将开始',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    'COMPLETED': '#66BB6A',
    'IN_PROGRESS': '#42A5F5',
    'PENDING': '#FFA726',
    'CANCELLED': '#999'
  }
  return colorMap[status] || '#666'
}

// 获取提醒类型标签
const getReminderTypeLabel = (type: string) => {
  const typeMap: Record<string, string> = {
    'NONE': '无提醒',
    'FIVE_MINUTES': '5分钟前',
    'FIFTEEN_MINUTES': '15分钟前',
    'THIRTY_MINUTES': '30分钟前',
    'ONE_HOUR': '1小时前',
    'ONE_DAY': '1天前',
    'CUSTOM': '自定义'
  }
  return typeMap[type] || '无提醒'
}

// 返回
const handleBack = () => {
  router.back()
}

// 编辑
const handleEdit = () => {
  if (schedule.value) {
    router.push({
      path: '/schedule',
      query: { edit: schedule.value.id }
    })
  }
}

// 分享
const handleShare = () => {
  if (schedule.value) {
    // TODO: 实现分享功能
    alert('分享功能开发中...')
  }
}

// 导航
const handleNavigate = () => {
  if (schedule.value?.location) {
    // 打开地图导航
    const url = `https://map.baidu.com/search/${encodeURIComponent(schedule.value.location)}`
    window.open(url, '_blank')
  }
}

// 更新状态
const handleUpdateStatus = async () => {
  if (!schedule.value) return
  
  if (confirm('确定要将此行程标记为已完成吗？')) {
    try {
      await updatePersonalScheduleStatus(schedule.value.id, 'COMPLETED')
      await loadScheduleDetail()
      alert('状态已更新')
    } catch (error: any) {
      console.error('更新状态失败:', error)
      alert(error?.message || '更新失败，请稍后重试')
    }
  }
}

// 删除
const handleDelete = async () => {
  if (!schedule.value) return
  
  if (confirm('确定要删除这个行程吗？删除后无法恢复。')) {
    try {
      await deletePersonalSchedule(schedule.value.id)
      alert('行程已删除')
      router.back()
    } catch (error: any) {
      console.error('删除失败:', error)
      alert(error?.message || '删除失败，请稍后重试')
    }
  }
}

onMounted(() => {
  loadScheduleDetail()
})
</script>

<style scoped>
.schedule-detail-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #FFF0F5 0%, #F0F8FF 50%, #F5F0FF 100%);
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

/* 顶部背景卡片 */
.header-card {
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.header-content {
  color: #333;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.back-btn,
.action-icon-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.3);
  color: #333;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.back-btn:hover,
.action-icon-btn:hover {
  background: rgba(255, 255, 255, 0.5);
  transform: scale(1.1);
}

.header-actions {
  display: flex;
  gap: 10px;
}

.schedule-header-info {
  text-align: center;
}

.schedule-title-main {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin: 0 0 15px 0;
}

.schedule-meta-row {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.meta-tag {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.meta-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
}

.status-dot {
  font-size: 8px;
}

/* 详情内容 */
.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.card-icon {
  font-size: 20px;
  color: #9C27B0;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

/* 时间信息 */
.time-info {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 15px;
}

.time-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.time-label {
  font-size: 14px;
  color: #666;
}

.time-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.time-divider {
  width: 1px;
  height: 40px;
  background: #eee;
}

.duration-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #f9f0ff;
  border-radius: 8px;
  color: #9C27B0;
  font-size: 14px;
}

/* 地点信息 */
.location-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.location-text {
  flex: 1;
  font-size: 16px;
  color: #333;
}

.location-action {
  padding: 8px 16px;
  border: 1px solid #9C27B0;
  background: white;
  color: #9C27B0;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.location-action:hover {
  background: #9C27B0;
  color: white;
}

/* 描述信息 */
.description-content {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
}

/* 提醒信息 */
.reminder-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reminder-type {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.reminder-time {
  font-size: 14px;
  color: #666;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 20px;
}

.action-btn {
  padding: 14px 24px;
  border-radius: 12px;
  border: 2px solid #9C27B0;
  background: white;
  color: #9C27B0;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.action-btn:hover {
  background: #9C27B0;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(156, 39, 176, 0.3);
}

.action-btn.primary {
  background: #9C27B0;
  color: white;
}

.action-btn.primary:hover {
  background: #7B1FA2;
}

.action-btn.danger {
  border-color: #f44336;
  color: #f44336;
}

.action-btn.danger:hover {
  background: #f44336;
  color: white;
}

/* 加载状态 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #666;
}

.loading i {
  font-size: 48px;
  margin-bottom: 20px;
  color: #9C27B0;
}

@media (max-width: 768px) {
  .page-container {
    padding: 15px;
  }

  .schedule-title-main {
    font-size: 24px;
  }

  .time-info {
    flex-direction: column;
    gap: 15px;
  }

  .time-divider {
    width: 100%;
    height: 1px;
  }
}
</style>

