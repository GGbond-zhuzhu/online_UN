<template>
  <div class="schedule-page">
    <NavBar />

    <div class="page-container">
      <!-- 今日行程概览 -->
      <section class="overview-section">
        <div class="overview-header">
          <h2>今日行程概览</h2>
          <div class="date-selector">
            <button class="date-btn" @click="changeDate(-1)">
              <i class="fas fa-chevron-left"></i>
            </button>
            <div class="current-date" @click="showDatePicker = true">
              <i class="fas fa-calendar"></i>
              <span>{{ currentDateText }}</span>
              <i class="fas fa-chevron-down"></i>
            </div>
            <button class="date-btn" @click="changeDate(1)">
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
        <div class="stats-cards">
          <div class="stat-card">
            <i class="fas fa-list"></i>
            <div class="stat-value">{{ todaySchedules.length }}</div>
            <div class="stat-label">今日行程</div>
          </div>
          <div class="stat-card completed">
            <i class="fas fa-check-circle"></i>
            <div class="stat-value">{{ completedCount }}</div>
            <div class="stat-label">已完成</div>
          </div>
          <div class="stat-card ongoing">
            <i class="fas fa-clock"></i>
            <div class="stat-value">{{ ongoingCount }}</div>
            <div class="stat-label">进行中</div>
          </div>
          <div class="stat-card upcoming">
            <i class="fas fa-hourglass-start"></i>
            <div class="stat-value">{{ upcomingCount }}</div>
            <div class="stat-label">即将开始</div>
          </div>
        </div>
      </section>

      <!-- 分类筛选和操作 -->
      <section class="filter-actions-section">
        <div class="category-filters">
          <button
            v-for="category in categories"
            :key="category.value"
            :class="['category-btn', { active: filters.category === category.value }]"
            @click="filters.category = category.value"
          >
            {{ category.label }}
          </button>
        </div>
        <div class="action-buttons">
          <button class="action-btn" @click="showImportModal = true">
            <i class="fas fa-file-import"></i> 导入行程
          </button>
          <button class="action-btn" @click="showTeamModal = true">
            <i class="fas fa-users"></i> 团队管理
          </button>
          <div class="search-box">
            <input
              v-model="filters.search"
              type="text"
              placeholder="搜索行程关键词..."
              @keyup.enter="handleSearch"
              @input="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">
              <i class="fas fa-search"></i> 搜索
            </button>
          </div>
        </div>
        <!-- 搜索结果盒子 -->
        <div v-if="filters.search && searchResults.length > 0" class="search-results-box">
          <div class="search-results-header">
            <h3>搜索结果 ({{ searchResults.length }})</h3>
            <button class="clear-search-btn" @click="clearSearch">
              <i class="fas fa-times"></i> 清除
            </button>
          </div>
          <div class="search-results-list">
            <div
              v-for="result in searchResults"
              :key="result.id"
              :class="['search-result-item', `category-${result.category}`]"
              @click="selectSchedule(result)"
            >
              <div class="result-time">
                <div class="time">{{ result.time }}</div>
                <div class="date">{{ result.date }}</div>
              </div>
              <div class="result-content">
                <h4 class="result-title">{{ result.title }}</h4>
                <p class="result-desc">{{ result.description }}</p>
                <div class="result-meta">
                  <span class="category-tag">{{ getCategoryLabel(result.category) }}</span>
                  <span class="location" v-if="result.location">
                    <i class="fas fa-map-marker-alt"></i> {{ result.location }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="filters.search && searchResults.length === 0" class="search-results-box empty">
          <div class="empty-search">
            <i class="fas fa-search"></i>
            <p>未找到相关行程</p>
            <button class="clear-search-btn" @click="clearSearch">清除搜索</button>
          </div>
        </div>
      </section>

      <!-- 本周行程 -->
      <section class="week-schedule-section">
        <div class="section-header">
          <h2>本周行程</h2>
          <div class="section-actions">
            <button class="export-btn" @click="exportPDF">
              <i class="fas fa-file-pdf"></i> 导出PDF
            </button>
            <button class="export-btn" @click="exportWord">
              <i class="fas fa-file-word"></i> 导出Word
            </button>
            <a href="#" class="month-view-link" @click.prevent="viewMonth">
              查看月视图 <i class="fas fa-arrow-right"></i>
            </a>
          </div>
        </div>
        <div class="week-grid">
          <div
            v-for="day in weekDays"
            :key="day.date"
            :class="['week-day', { today: day.isToday, active: day.isSelected }]"
            @click="selectDay(day.date)"
          >
            <div class="day-name">{{ day.name }}</div>
            <div class="day-number">{{ day.number }}</div>
            <div class="day-count">{{ day.count }}个行程</div>
          </div>
        </div>
      </section>

      <!-- 独立提醒 -->
      <section class="reminder-section">
        <div class="section-header">
          <h2>独立提醒</h2>
          <button class="add-btn" @click="showReminderModal = true">
            <i class="fas fa-plus"></i> 添加提醒
          </button>
        </div>
        <div class="reminder-list">
          <div v-for="reminder in reminders" :key="reminder.id" class="reminder-item">
            <div class="reminder-content">
              <div class="reminder-title">{{ reminder.title }}</div>
              <div class="reminder-time">{{ reminder.timeText }}</div>
            </div>
            <div class="reminder-actions">
              <button class="icon-btn" @click="editReminder(reminder.id)" title="编辑">
                <i class="fas fa-edit"></i>
              </button>
              <button class="icon-btn" @click="toggleReminder(reminder.id)" :title="reminder.enabled ? '禁用' : '启用'">
                <i :class="reminder.enabled ? 'fas fa-bell' : 'far fa-bell-slash'"></i>
              </button>
              <button class="icon-btn delete" @click="deleteReminder(reminder.id)" title="删除">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- 今日行程 -->
      <section class="today-schedule-section">
        <div class="section-header">
          <h2>今日行程</h2>
          <button class="add-btn" @click="showAddModal = true">
            <i class="fas fa-plus"></i> 添加行程
          </button>
        </div>
        <div class="schedule-list">
          <div
            v-for="schedule in filteredTodaySchedules"
            :key="schedule.id"
            :class="['schedule-item', `category-${schedule.category}`]"
          >
            <div class="schedule-time">
              <div class="time">{{ schedule.time }}</div>
              <div class="period">{{ schedule.period }}</div>
            </div>
            <div class="schedule-content">
              <div class="schedule-header">
                <h3 class="schedule-title">{{ schedule.title }}</h3>
                <span class="category-tag">{{ getCategoryLabel(schedule.category) }}</span>
              </div>
              <div class="schedule-location">
                <i class="fas fa-map-marker-alt"></i>
                <span>{{ schedule.location }}</span>
              </div>
              <div class="schedule-desc">{{ schedule.description }}</div>
              <div class="schedule-footer">
                <span :class="['status-badge', schedule.status]">{{ getStatusLabel(schedule.status) }}</span>
                <span v-if="schedule.sharedWith && schedule.sharedWith.length > 0" class="shared-info">
                  <i class="fas fa-users"></i> 已共享给: {{ schedule.sharedWith.join(', ') }}
                </span>
                <div class="schedule-actions">
                  <button class="action-link" @click="shareSchedule(schedule.id)">
                    <i class="fas fa-share-alt"></i> 共享
                  </button>
                  <button class="action-link" @click="editSchedule(schedule.id)">
                    <i class="fas fa-edit"></i> 编辑
                  </button>
                  <button class="action-link delete" @click="deleteSchedule(schedule.id)">
                    <i class="fas fa-trash"></i> 删除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 添加行程模态框 -->
    <div v-if="showAddModal" class="modal-overlay" @click="showAddModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>添加新行程</h3>
          <button class="close-btn" @click="showAddModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>行程标题</label>
            <input v-model="newSchedule.title" type="text" placeholder="请输入行程标题" />
          </div>
          <div class="form-group">
            <label>分类</label>
            <select v-model="newSchedule.category">
              <option value="">请选择分类</option>
              <option v-for="cat in categories" :key="cat.value" :value="cat.value">{{ cat.label }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>时间</label>
            <div class="datetime-inputs">
              <input v-model="newSchedule.date" type="date" />
              <input v-model="newSchedule.time" type="time" />
            </div>
          </div>
          <div class="form-group">
            <label>地点</label>
            <input v-model="newSchedule.location" type="text" placeholder="请输入地点" />
          </div>
          <div class="form-group">
            <label>描述</label>
            <textarea v-model="newSchedule.description" placeholder="请输入描述"></textarea>
          </div>
          <div class="form-group">
            <label class="checkbox-label">
              <input v-model="newSchedule.hasReminder" type="checkbox" />
              <span>提醒设置</span>
            </label>
            <div v-if="newSchedule.hasReminder" class="reminder-settings">
              <div class="reminder-row">
                <label>提醒频率</label>
                <div class="reminder-input-group">
                  <input v-model.number="newSchedule.reminderValue" type="number" min="1" />
                  <select v-model="newSchedule.reminderUnit">
                    <option value="minute">分钟前</option>
                    <option value="hour">小时前</option>
                    <option value="day">天前</option>
                  </select>
                </div>
              </div>
              <div class="reminder-row">
                <label>提醒方式</label>
                <div class="reminder-checkboxes">
                  <label><input v-model="newSchedule.reminderMethods" type="checkbox" value="app" /> App弹窗</label>
                  <label><input v-model="newSchedule.reminderMethods" type="checkbox" value="notification" /> 系统通知</label>
                  <label><input v-model="newSchedule.reminderMethods" type="checkbox" value="alarm" /> 闹钟铃声</label>
                  <label><input v-model="newSchedule.reminderMethods" type="checkbox" value="sms" /> 短信提醒</label>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn cancel" @click="showAddModal = false">取消</button>
          <button class="btn primary" @click="addSchedule">添加行程</button>
        </div>
      </div>
    </div>

    <!-- 导入行程模态框 -->
    <div v-if="showImportModal" class="modal-overlay" @click="showImportModal = false">
      <div class="modal-content large" @click.stop>
        <div class="modal-header">
          <h3>导入行程</h3>
          <button class="close-btn" @click="showImportModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="import-options">
            <button class="import-option-btn" @click="goToImport('file')">
              <i class="fas fa-file-excel"></i>
              <span>从文件导入</span>
              <p>支持Excel和CSV格式</p>
            </button>
            <button class="import-option-btn" @click="goToImport('manual')">
              <i class="fas fa-keyboard"></i>
              <span>手动录入</span>
              <p>逐条添加课程信息</p>
            </button>
            <button class="import-option-btn" @click="goToImport('link')">
              <i class="fas fa-link"></i>
              <span>链接导入</span>
              <p>从教务系统链接导入</p>
            </button>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn cancel" @click="showImportModal = false">取消</button>
        </div>
      </div>
    </div>

    <!-- 团队管理模态框 -->
    <div v-if="showTeamModal" class="modal-overlay" @click="showTeamModal = false">
      <div class="modal-content large" @click.stop>
        <div class="modal-header">
          <h3>团队管理</h3>
          <button class="close-btn" @click="showTeamModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <button class="create-team-btn" @click="showCreateTeamModal = true">
            <i class="fas fa-plus"></i> 创建新团队
          </button>
          <div class="team-section">
            <h4>我的团队</h4>
            <div class="team-list">
              <div v-for="team in myTeams" :key="team.id" class="team-item" @click="viewTeam(team.id)">
                <div class="team-avatar">{{ team.name.charAt(0) }}</div>
                <div class="team-info">
                  <div class="team-name">{{ team.name }}</div>
                  <div class="team-members">{{ team.memberCount }} 名成员</div>
                </div>
                <i class="fas fa-chevron-right"></i>
              </div>
            </div>
          </div>
          <div class="team-section">
            <h4>团队邀请</h4>
            <div class="invitation-list">
              <div v-for="invitation in teamInvitations" :key="invitation.id" class="invitation-item">
                <div class="invitation-icon">
                  <i class="fas fa-user-plus"></i>
                </div>
                <div class="invitation-info">
                  <div class="invitation-team">{{ invitation.teamName }}</div>
                  <div class="invitation-inviter">{{ invitation.inviter }} 邀请您加入</div>
                </div>
                <div class="invitation-actions">
                  <button class="btn accept" @click="acceptInvitation(invitation.id)">接受</button>
                  <button class="btn reject" @click="rejectInvitation(invitation.id)">拒绝</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 共享行程模态框 -->
    <div v-if="showShareModal" class="modal-overlay" @click="showShareModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>共享行程</h3>
          <button class="close-btn" @click="showShareModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>添加共享对象</label>
            <input v-model="shareInput" type="text" placeholder="输入用户名或邮箱" @keyup.enter="addShareUser" />
            <button class="add-share-btn" @click="addShareUser">
              <i class="fas fa-plus"></i> 添加
            </button>
          </div>
          <div v-if="shareUsers.length > 0" class="share-users">
            <div v-for="(user, idx) in shareUsers" :key="idx" class="share-user-tag">
              {{ user }}
              <button @click="removeShareUser(idx)">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn cancel" @click="showShareModal = false">取消</button>
          <button class="btn primary" @click="confirmShare">确认共享</button>
        </div>
      </div>
    </div>

    <!-- 添加独立提醒模态框 -->
    <div v-if="showReminderModal" class="modal-overlay" @click="showReminderModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>添加独立提醒</h3>
          <button class="close-btn" @click="showReminderModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>提醒标题 <span class="required">*</span></label>
            <input v-model="newReminder.title" type="text" placeholder="请输入提醒标题" />
          </div>
          <div class="form-group">
            <label>提醒时间 <span class="required">*</span></label>
            <div class="datetime-inputs">
              <input v-model="newReminder.date" type="date" />
              <input v-model="newReminder.time" type="time" />
            </div>
          </div>
          <div class="form-group">
            <label>提醒内容</label>
            <textarea v-model="newReminder.content" placeholder="请输入提醒内容（选填）" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label class="checkbox-label">
              <input v-model="newReminder.enabled" type="checkbox" checked />
              <span>立即启用</span>
            </label>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn cancel" @click="showReminderModal = false">取消</button>
          <button class="btn primary" @click="addReminder">添加提醒</button>
        </div>
      </div>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
import { createTeam as createTeamAPI } from '@campus/common'

const router = useRouter()

// 当前日期
const currentDate = ref(new Date())
const showDatePicker = ref(false)

// 筛选条件
const filters = reactive({
  category: 'all',
  search: ''
})

// 分类选项
const categories = [
  { label: '全部', value: 'all' },
  { label: '课程', value: 'course' },
  { label: '考试', value: 'exam' },
  { label: '社团', value: 'club' },
  { label: '兼职', value: 'parttime' },
  { label: '个人事务', value: 'personal' }
]

// 行程数据
const schedules = ref([
  {
    id: 1,
    title: '高等数学',
    category: 'course',
    time: '08:00',
    period: '上午',
    date: '2024-12-15',
    location: '3号教学楼201室',
    description: '第二章：多元函数微分学',
    status: 'completed',
    sharedWith: ['李四', '王五']
  },
  {
    id: 2,
    title: '大学英语期末考试',
    category: 'exam',
    time: '10:00',
    period: '上午',
    date: '2024-12-15',
    location: '外语学院101室',
    description: 'Unit 3: Cultural Differences',
    status: 'completed',
    sharedWith: []
  },
  {
    id: 3,
    title: '数据结构与算法',
    category: 'course',
    time: '14:00',
    period: '下午',
    date: '2024-12-15',
    location: '计算机学院305室',
    description: '树与二叉树的应用',
    status: 'ongoing',
    sharedWith: ['赵六']
  },
  {
    id: 4,
    title: '编程俱乐部技术分享',
    category: 'club',
    time: '16:30',
    period: '下午',
    date: '2024-12-15',
    location: '学生活动中心B201',
    description: '每周技术分享会',
    status: 'upcoming',
    sharedWith: ['钱七', '孙八']
  },
  {
    id: 5,
    title: '图书馆自习',
    category: 'personal',
    time: '19:00',
    period: '晚上',
    date: '2024-12-15',
    location: '图书馆3楼自习区',
    description: '准备期末考试',
    status: 'upcoming',
    sharedWith: []
  },
  {
    id: 6,
    title: '家教兼职',
    category: 'parttime',
    time: '20:00',
    period: '晚上',
    date: '2024-12-15',
    location: '线上会议',
    description: '高中数学辅导',
    status: 'upcoming',
    sharedWith: []
  }
])

// 提醒数据
const reminders = ref([
  {
    id: 1,
    title: '提交课程作业',
    timeText: '今天 18:00',
    enabled: true
  },
  {
    id: 2,
    title: '准备小组讨论',
    timeText: '明天 14:00',
    enabled: true
  },
  {
    id: 3,
    title: '还图书馆书籍',
    timeText: '1月18日 16:00',
    enabled: false
  }
])

// 团队数据
const myTeams = ref([
  { id: 1, name: '项目开发组', memberCount: 5 },
  { id: 2, name: '学习小组', memberCount: 3 },
  { id: 3, name: '社团活动', memberCount: 8 }
])

const teamInvitations = ref([
  { id: 1, teamName: '竞赛团队', inviter: '李四' },
  { id: 2, teamName: '研究小组', inviter: '王五' }
])

// 模态框状态
const showAddModal = ref(false)
const showImportModal = ref(false)
const showTeamModal = ref(false)
const showShareModal = ref(false)
const showCreateTeamModal = ref(false)
const showReminderModal = ref(false)

// 创建团队表单
const newTeamForm = reactive({
  name: '',
  description: ''
})

// 新提醒表单
const newReminder = reactive({
  title: '',
  date: '',
  time: '',
  content: '',
  enabled: true
})

// 新行程表单
const newSchedule = reactive({
  title: '',
  category: '',
  date: '',
  time: '',
  location: '',
  description: '',
  hasReminder: false,
  reminderValue: 15,
  reminderUnit: 'minute',
  reminderMethods: []
})

// 共享相关
const shareInput = ref('')
const shareUsers = ref<string[]>([])
const currentShareScheduleId = ref<number | null>(null)

// 计算属性
const currentDateText = computed(() => {
  const date = currentDate.value
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekday = weekdays[date.getDay()]
  return `${year}年${month}月${day}日${weekday}`
})

const todaySchedules = computed(() => {
  const today = formatDate(currentDate.value)
  return schedules.value.filter(s => s.date === today)
})

const completedCount = computed(() => {
  return todaySchedules.value.filter(s => s.status === 'completed').length
})

const ongoingCount = computed(() => {
  return todaySchedules.value.filter(s => s.status === 'ongoing').length
})

const upcomingCount = computed(() => {
  return todaySchedules.value.filter(s => s.status === 'upcoming').length
})

const filteredTodaySchedules = computed(() => {
  let result = todaySchedules.value

  if (filters.category !== 'all') {
    result = result.filter(s => s.category === filters.category)
  }

  if (filters.search) {
    const key = filters.search.trim().toLowerCase()
    result = result.filter(s =>
      s.title.toLowerCase().includes(key) ||
      s.description.toLowerCase().includes(key) ||
      s.location.toLowerCase().includes(key)
    )
  }

  return result.sort((a, b) => {
    const timeA = a.time.split(':').map(Number)
    const timeB = b.time.split(':').map(Number)
    return timeA[0] * 60 + timeA[1] - (timeB[0] * 60 + timeB[1])
  })
})

const weekDays = computed(() => {
  const today = new Date()
  const startOfWeek = new Date(currentDate.value)
  startOfWeek.setDate(startOfWeek.getDate() - startOfWeek.getDay())

  const days = []
  const dayNames = ['日', '一', '二', '三', '四', '五', '六']

  for (let i = 0; i < 7; i++) {
    const date = new Date(startOfWeek)
    date.setDate(startOfWeek.getDate() + i)
    const dateStr = formatDate(date)
    const count = schedules.value.filter(s => s.date === dateStr).length
    const isToday = formatDate(today) === dateStr
    const isSelected = formatDate(currentDate.value) === dateStr

    days.push({
      name: dayNames[i],
      number: date.getDate(),
      date: dateStr,
      count,
      isToday,
      isSelected
    })
  }

  return days
})

// 方法
const formatDate = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const changeDate = (days: number) => {
  const newDate = new Date(currentDate.value)
  newDate.setDate(newDate.getDate() + days)
  currentDate.value = newDate
}

const selectDay = (dateStr: string) => {
  currentDate.value = new Date(dateStr)
}

const getCategoryLabel = (category: string) => {
  const cat = categories.find(c => c.value === category)
  return cat ? cat.label : category
}

const getStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    completed: '已完成',
    ongoing: '进行中',
    upcoming: '即将开始'
  }
  return statusMap[status] || status
}

// 搜索结果
const searchResults = computed(() => {
  if (!filters.search) return []
  
  const keyword = filters.search.trim().toLowerCase()
  return schedules.value.filter(s =>
    s.title.toLowerCase().includes(keyword) ||
    s.description.toLowerCase().includes(keyword) ||
    s.location.toLowerCase().includes(keyword) ||
    getCategoryLabel(s.category).toLowerCase().includes(keyword)
  )
})

const handleSearch = () => {
  // 搜索逻辑已在 computed 中实现
  // 可以在这里添加额外的搜索处理逻辑
}

const clearSearch = () => {
  filters.search = ''
}

const selectSchedule = (schedule: any) => {
  // 选中搜索结果中的行程，跳转到对应日期
  currentDate.value = new Date(schedule.date)
  filters.search = '' // 清除搜索，显示该日期的行程
}

const addSchedule = () => {
  if (!newSchedule.title || !newSchedule.category || !newSchedule.date || !newSchedule.time) {
    alert('请填写必填项')
    return
  }

  const [hours, minutes] = newSchedule.time.split(':')
  const period = getPeriod(parseInt(hours))

  const newScheduleItem = {
    id: schedules.value.length + 1,
    title: newSchedule.title,
    category: newSchedule.category,
    time: newSchedule.time,
    period,
    date: newSchedule.date,
    location: newSchedule.location || '未设置',
    description: newSchedule.description || '',
    status: 'upcoming',
    sharedWith: []
  }

  schedules.value.push(newScheduleItem)

  // 如果设置了提醒，创建独立提醒
  if (newSchedule.hasReminder) {
    const reminderDate = new Date(newSchedule.date)
    const [hour, minute] = newSchedule.time.split(':')
    reminderDate.setHours(parseInt(hour), parseInt(minute), 0, 0)
    
    // 计算提醒时间
    let reminderTime = new Date(reminderDate)
    if (newSchedule.reminderUnit === 'minute') {
      reminderTime.setMinutes(reminderTime.getMinutes() - newSchedule.reminderValue)
    } else if (newSchedule.reminderUnit === 'hour') {
      reminderTime.setHours(reminderTime.getHours() - newSchedule.reminderValue)
    } else if (newSchedule.reminderUnit === 'day') {
      reminderTime.setDate(reminderTime.getDate() - newSchedule.reminderValue)
    }

    reminders.value.push({
      id: reminders.value.length + 1,
      title: newSchedule.title,
      timeText: formatReminderTime(reminderTime),
      enabled: true
    } as any)
  }

  // 重置表单
  Object.assign(newSchedule, {
    title: '',
    category: '',
    date: formatDate(new Date()),
    time: '',
    location: '',
    description: '',
    hasReminder: false,
    reminderValue: 15,
    reminderUnit: 'minute',
    reminderMethods: []
  })

  showAddModal.value = false
  
  // 跳转到新添加的行程日期
  currentDate.value = new Date(newScheduleItem.date)
  
  // 显示成功提示
  alert(`行程"${newScheduleItem.title}"已添加成功！`)
}

const getPeriod = (hour: number) => {
  if (hour < 12) return '上午'
  if (hour < 18) return '下午'
  return '晚上'
}

const editSchedule = (id: number) => {
  const schedule = schedules.value.find(s => s.id === id)
  if (schedule) {
    Object.assign(newSchedule, {
      title: schedule.title,
      category: schedule.category,
      date: schedule.date,
      time: schedule.time,
      location: schedule.location,
      description: schedule.description
    })
    showAddModal.value = true
    // 这里可以添加编辑逻辑
  }
}

const deleteSchedule = (id: number) => {
  if (confirm('确定要删除这个行程吗？')) {
    const index = schedules.value.findIndex(s => s.id === id)
    if (index > -1) {
      schedules.value.splice(index, 1)
    }
  }
}

const shareSchedule = (id: number) => {
  currentShareScheduleId.value = id
  const schedule = schedules.value.find(s => s.id === id)
  if (schedule) {
    shareUsers.value = [...schedule.sharedWith]
  }
  showShareModal.value = true
}

const addShareUser = () => {
  if (shareInput.value.trim() && !shareUsers.value.includes(shareInput.value.trim())) {
    shareUsers.value.push(shareInput.value.trim())
    shareInput.value = ''
  }
}

const removeShareUser = (index: number) => {
  shareUsers.value.splice(index, 1)
}

const confirmShare = () => {
  if (currentShareScheduleId.value) {
    const schedule = schedules.value.find(s => s.id === currentShareScheduleId.value)
    if (schedule) {
      schedule.sharedWith = [...shareUsers.value]
    }
  }
  showShareModal.value = false
  shareUsers.value = []
  shareInput.value = ''
  currentShareScheduleId.value = null
}

const exportPDF = () => {
  try {
    // TODO: 实现PDF导出功能
    // 可以使用jsPDF等库实现
    alert('PDF导出功能开发中...')
  } catch (error) {
    console.error('PDF导出失败:', error)
    alert('导出失败，请稍后重试')
  }
}

const exportWord = () => {
  try {
    // TODO: 实现Word导出功能
    // 可以使用docx等库实现
    alert('Word导出功能开发中...')
  } catch (error) {
    console.error('Word导出失败:', error)
    alert('导出失败，请稍后重试')
  }
}

const viewMonth = () => {
  // TODO: 实现月视图功能
  // router.push('/schedule/month')
  alert('月视图功能开发中...')
}

const editReminder = (id: number) => {
  // TODO: 实现编辑提醒功能
  // 可以打开编辑模态框或跳转到编辑页面
  const reminder = reminders.value.find(r => r.id === id)
  if (reminder) {
    // 打开编辑模态框
    alert(`编辑提醒：${reminder.title}`)
  }
}

const toggleReminder = (id: number) => {
  const reminder = reminders.value.find(r => r.id === id)
  if (reminder) {
    reminder.enabled = !reminder.enabled
    // TODO: 调用API更新提醒状态
    // await updateReminderAPI(id, { enabled: reminder.enabled })
  }
}

const deleteReminder = (id: number) => {
  if (confirm('确定要删除这个提醒吗？')) {
    try {
      const index = reminders.value.findIndex(r => r.id === id)
      if (index > -1) {
        reminders.value.splice(index, 1)
        // TODO: 调用API删除提醒
        // await deleteReminderAPI(id)
        alert('提醒已删除')
      }
    } catch (error) {
      console.error('删除提醒失败:', error)
      alert('删除失败，请稍后重试')
    }
  }
}

// 格式化提醒时间
const formatReminderTime = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 跳转到导入页面
const goToImport = (method: 'file' | 'manual' | 'link') => {
  showImportModal.value = false
  router.push({
    path: '/schedule/import',
    query: { method }
  })
}

// 创建团队
const createTeam = async () => {
  if (!newTeamForm.name.trim()) {
    alert('请输入团队名称')
    return
  }

  try {
    const team = await createTeamAPI({
      name: newTeamForm.name,
      description: newTeamForm.description || undefined
    })
    
    myTeams.value.push({
      id: team.id,
      name: team.name,
      memberCount: team.members?.length || 1
    })
    
    newTeamForm.name = ''
    newTeamForm.description = ''
    showCreateTeamModal.value = false
    showTeamModal.value = true // 保持在团队管理模态框
    alert(`团队创建成功！邀请码：${team.inviteCode || '已生成'}`)
  } catch (error: any) {
    console.error('创建团队失败:', error)
    alert(error?.message || '创建失败，请稍后重试')
  }
}

// 添加独立提醒
const addReminder = () => {
  if (!newReminder.title.trim() || !newReminder.date || !newReminder.time) {
    alert('请填写提醒标题和时间')
    return
  }

  try {
    const reminderDate = new Date(`${newReminder.date} ${newReminder.time}`)
    
    reminders.value.push({
      id: reminders.value.length + 1,
      title: newReminder.title,
      timeText: formatReminderTime(reminderDate),
      enabled: newReminder.enabled
    } as any)
    
    // 重置表单
    newReminder.title = ''
    newReminder.date = ''
    newReminder.time = ''
    newReminder.content = ''
    newReminder.enabled = true
    
    showReminderModal.value = false
    alert('提醒添加成功！')
  } catch (error) {
    console.error('添加提醒失败:', error)
    alert('添加失败，请稍后重试')
  }
}

const viewTeam = (id: number) => {
  // 跳转到团队详情页
  router.push(`/schedule/team/${id}`)
}

const acceptInvitation = (id: number) => {
  const invitation = teamInvitations.value.find(inv => inv.id === id)
  if (invitation) {
    myTeams.value.push({
      id: myTeams.value.length + 1,
      name: invitation.teamName,
      memberCount: 1
    })
    const index = teamInvitations.value.findIndex(inv => inv.id === id)
    if (index > -1) {
      teamInvitations.value.splice(index, 1)
    }
  }
}

const rejectInvitation = (id: number) => {
  const index = teamInvitations.value.findIndex(inv => inv.id === id)
  if (index > -1) {
    teamInvitations.value.splice(index, 1)
  }
}

onMounted(() => {
  // 初始化当前日期
  const today = formatDate(new Date())
  newSchedule.date = today
  newReminder.date = today
  
  // 如果有导入方法的查询参数，跳转到导入页面
  const route = router.currentRoute.value
  if (route.query.method) {
    showImportModal.value = false
    // 导入页面会根据method参数自动切换标签
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

.schedule-page {
  min-height: 100vh;
  background: var(--bg);
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 15px 40px;
}

/* 今日行程概览 */
.overview-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.overview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.overview-header h2 {
  font-size: 20px;
  font-weight: bold;
  color: var(--text);
}

.date-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-btn {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.date-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.current-date {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: var(--text);
}

.current-date i:first-child {
  color: var(--primary);
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.stat-card {
  background: #f9f0ff;
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.stat-card.completed {
  background: #e8f5e9;
}

.stat-card.ongoing {
  background: #fff3e0;
}

.stat-card.upcoming {
  background: #e3f2fd;
}

.stat-card i {
  font-size: 24px;
  color: var(--primary);
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: var(--muted);
}

/* 筛选和操作区域 */
.filter-actions-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.category-filters {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.category-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  color: var(--text);
}

.category-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.category-btn.active {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.action-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.action-btn {
  padding: 8px 16px;
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.action-btn:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
  transform: translateY(-2px);
}

.search-box {
  display: flex;
  gap: 8px;
  flex: 1;
  min-width: 200px;
}

.search-box input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
}

.search-btn {
  padding: 8px 16px;
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

.search-btn:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

/* 搜索结果盒子 */
.search-results-box {
  margin-top: 15px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.search-results-box.empty {
  padding: 40px 20px;
  text-align: center;
}

.search-results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.search-results-header h3 {
  font-size: 16px;
  font-weight: bold;
  color: var(--text);
  margin: 0;
}

.clear-search-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  color: var(--muted);
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.clear-search-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.search-results-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.search-result-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafafa;
}

.search-result-item:hover {
  border-color: var(--primary);
  background: white;
  box-shadow: 0 2px 8px rgba(216, 27, 96, 0.1);
  transform: translateY(-2px);
}

.result-time {
  min-width: 80px;
  text-align: center;
  padding: 10px;
  background: var(--primary);
  color: white;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.result-time .time {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 4px;
}

.result-time .date {
  font-size: 12px;
  opacity: 0.9;
}

.result-content {
  flex: 1;
}

.result-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 8px;
}

.result-desc {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 8px;
  line-height: 1.5;
}

.result-meta {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.result-meta .category-tag {
  padding: 4px 10px;
  background: var(--primary);
  color: white;
  border-radius: 4px;
  font-size: 12px;
}

.result-meta .location {
  font-size: 12px;
  color: var(--muted);
  display: flex;
  align-items: center;
  gap: 4px;
}

.empty-search {
  padding: 40px 20px;
}

.empty-search i {
  font-size: 48px;
  color: #ddd;
  margin-bottom: 15px;
}

.empty-search p {
  color: var(--muted);
  margin-bottom: 15px;
}

/* 本周行程 */
.week-schedule-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: bold;
  color: var(--text);
}

.section-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.export-btn {
  padding: 8px 16px;
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

.export-btn:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.month-view-link {
  color: var(--primary);
  text-decoration: none;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.week-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10px;
}

.week-day {
  background: #f9f0ff;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.week-day:hover {
  border-color: var(--primary);
  transform: translateY(-2px);
}

.week-day.today {
  background: var(--primary);
  color: white;
}

.week-day.active {
  border-color: var(--primary);
  background: #fff3e0;
}

.day-name {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 5px;
}

.week-day.today .day-name {
  color: rgba(255, 255, 255, 0.9);
}

.day-number {
  font-size: 20px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 5px;
}

.week-day.today .day-number {
  color: white;
}

.day-count {
  font-size: 12px;
  color: var(--muted);
}

.week-day.today .day-count {
  color: rgba(255, 255, 255, 0.9);
}

/* 独立提醒 */
.reminder-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.add-btn {
  padding: 8px 16px;
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

.add-btn:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.reminder-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reminder-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f9f0ff;
  border-radius: 8px;
}

.reminder-content {
  flex: 1;
}

.reminder-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}

.reminder-time {
  font-size: 12px;
  color: var(--muted);
}

.reminder-actions {
  display: flex;
  gap: 8px;
}

.icon-btn {
  padding: 6px 10px;
  border: none;
  background: transparent;
  color: var(--muted);
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.icon-btn:hover {
  background: var(--primary);
  color: white;
}

.icon-btn.delete:hover {
  background: #f44336;
  color: white;
}

/* 今日行程 */
.today-schedule-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.schedule-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: #f9f0ff;
  border-radius: 10px;
  border-left: 4px solid var(--primary);
  transition: all 0.3s;
}

.schedule-item:hover {
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.15);
  transform: translateX(4px);
}

.schedule-item.category-course {
  border-left-color: #4caf50;
}

.schedule-item.category-exam {
  border-left-color: #f44336;
}

.schedule-item.category-club {
  border-left-color: #ff9800;
}

.schedule-item.category-parttime {
  border-left-color: #2196f3;
}

.schedule-item.category-personal {
  border-left-color: #9c27b0;
}

.schedule-time {
  min-width: 80px;
  text-align: center;
}

.time {
  font-size: 18px;
  font-weight: bold;
  color: var(--text);
}

.period {
  font-size: 12px;
  color: var(--muted);
  margin-top: 4px;
}

.schedule-content {
  flex: 1;
}

.schedule-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.schedule-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--text);
  margin: 0;
}

.category-tag {
  padding: 4px 10px;
  background: var(--primary);
  color: white;
  border-radius: 4px;
  font-size: 12px;
}

.schedule-location {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 8px;
}

.schedule-location i {
  color: var(--primary);
  font-size: 12px;
}

.schedule-desc {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 12px;
}

.schedule-footer {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.completed {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-badge.ongoing {
  background: #fff3e0;
  color: #f57c00;
}

.status-badge.upcoming {
  background: #e3f2fd;
  color: #1976d2;
}

.shared-info {
  font-size: 12px;
  color: var(--muted);
  display: flex;
  align-items: center;
  gap: 4px;
}

.schedule-actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.action-link {
  padding: 4px 8px;
  border: none;
  background: transparent;
  color: var(--primary);
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.action-link:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

.action-link.delete {
  color: #f44336;
}

.action-link.delete:hover {
  color: #d32f2f;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 0;
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.modal-content.large {
  max-width: 800px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: bold;
  color: var(--text);
  margin: 0;
}

.close-btn {
  padding: 8px;
  border: none;
  background: transparent;
  color: var(--muted);
  cursor: pointer;
  font-size: 18px;
  transition: all 0.3s;
}

.close-btn:hover {
  color: var(--primary);
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #eee;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 8px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--primary);
}

.form-group textarea {
  min-height: 80px;
  resize: vertical;
}

.datetime-inputs {
  display: flex;
  gap: 10px;
}

.datetime-inputs input {
  flex: 1;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.reminder-settings {
  margin-top: 10px;
  padding: 15px;
  background: #f9f0ff;
  border-radius: 6px;
}

.reminder-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.reminder-row:last-child {
  margin-bottom: 0;
}

.reminder-input-group {
  display: flex;
  gap: 8px;
}

.reminder-input-group input {
  width: 80px;
}

.reminder-checkboxes {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.reminder-checkboxes label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: normal;
  margin: 0;
  cursor: pointer;
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
}

.btn:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.btn.cancel {
  border-color: #ddd;
  color: var(--muted);
}

.btn.cancel:hover {
  background: #f5f5f5;
  border-color: #ddd;
  color: var(--muted);
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

.btn.accept {
  background: #4caf50;
  color: white;
  border-color: #4caf50;
}

.btn.reject {
  background: #f44336;
  color: white;
  border-color: #f44336;
}

/* 导入选项 */
.import-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.import-option-btn {
  padding: 30px 20px;
  border: 2px dashed #ddd;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.import-option-btn:hover {
  border-color: var(--primary);
  background: #f9f0ff;
}

.import-option-btn i {
  font-size: 32px;
  color: var(--primary);
}

.import-option-btn span {
  font-size: 14px;
  color: var(--text);
  font-weight: 600;
}

.import-option-btn p {
  font-size: 12px;
  color: var(--muted);
  margin: 0;
}

/* 团队管理 */
.create-team-btn {
  width: 100%;
  padding: 12px;
  border: 2px dashed #ddd;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--primary);
  font-weight: 600;
  transition: all 0.3s;
}

.create-team-btn:hover {
  border-color: var(--primary);
  background: #f9f0ff;
}

.team-section {
  margin-bottom: 30px;
}

.team-section h4 {
  font-size: 16px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 15px;
}

.team-list,
.invitation-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.team-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f9f0ff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.team-item:hover {
  background: #f0e6ff;
  transform: translateX(4px);
}

.empty-team {
  padding: 40px 20px;
  text-align: center;
  background: #f9f0ff;
  border-radius: 8px;
  border: 2px dashed #ddd;
}

.empty-team i {
  font-size: 48px;
  color: #ddd;
  margin-bottom: 15px;
}

.empty-team p {
  color: var(--muted);
  margin: 0;
}

.team-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
}

.team-info {
  flex: 1;
}

.team-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}

.team-members {
  font-size: 12px;
  color: var(--muted);
}

.team-item i {
  color: var(--muted);
}

.invitation-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #fff3e0;
  border-radius: 8px;
}

.invitation-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #ff9800;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.invitation-info {
  flex: 1;
}

.invitation-team {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}

.invitation-inviter {
  font-size: 12px;
  color: var(--muted);
}

.invitation-actions {
  display: flex;
  gap: 8px;
}

/* 共享用户 */
.add-share-btn {
  margin-top: 8px;
  padding: 6px 12px;
  border: 1px solid var(--primary);
  background: var(--primary);
  color: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.add-share-btn:hover {
  background: var(--primary-dark);
}

.share-users {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.share-user-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #f9f0ff;
  border-radius: 6px;
  font-size: 13px;
  color: var(--text);
}

.share-user-tag button {
  border: none;
  background: transparent;
  color: var(--muted);
  cursor: pointer;
  padding: 0;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.share-user-tag button:hover {
  color: var(--primary);
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .week-grid {
    grid-template-columns: repeat(7, 1fr);
    gap: 5px;
  }

  .week-day {
    padding: 10px 5px;
  }

  .day-number {
    font-size: 16px;
  }

  .day-count {
    font-size: 11px;
  }

  .schedule-item {
    flex-direction: column;
  }

  .schedule-time {
    text-align: left;
    display: flex;
    gap: 10px;
  }

  .action-buttons {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    width: 100%;
  }

  .import-options {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }

  .category-filters {
    flex-direction: column;
  }

  .category-btn {
    width: 100%;
  }
}
</style>
