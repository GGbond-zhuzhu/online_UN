<template>
  <div class="schedule-calendar">
    <!-- 日历主体容器 -->
    <div class="calendar-container">
      <!-- 左侧：迷你日历和分类筛选 -->
      <div class="calendar-sidebar">
        <!-- 迷你日历 -->
        <div class="mini-calendar">
          <div class="mini-calendar-header">
            <button class="nav-btn" @click="prevMonthMini">
              <i class="fas fa-chevron-left"></i>
            </button>
            <span class="month-year">{{ miniCalendarYear }}年{{ miniCalendarMonth }}月</span>
            <button class="nav-btn" @click="nextMonthMini">
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          <div class="mini-calendar-grid">
            <!-- 星期标题 -->
            <div class="mini-weekdays">
              <div v-for="day in weekDays" :key="day" class="mini-weekday">{{ day }}</div>
            </div>
            <!-- 日期网格 -->
            <div class="mini-days">
              <div
                v-for="(day, index) in miniCalendarDays"
                :key="index"
                :class="[
                  'mini-day',
                  {
                    'other-month': day.otherMonth,
                    'today': day.isToday,
                    'selected': day.isSelected
                  }
                ]"
                @click="selectDateFromMini(day.date)"
              >
                {{ day.day }}
              </div>
            </div>
          </div>
        </div>

        <!-- 我的日历分类 -->
        <div class="calendar-categories">
          <div class="categories-header" @click="toggleCategories">
            <span>我的日历</span>
            <i :class="['fas', categoriesExpanded ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
          </div>
          <div v-if="categoriesExpanded" class="categories-list">
            <label
              v-for="category in calendarCategories"
              :key="category.id"
              class="category-item"
            >
              <input
                type="checkbox"
                v-model="category.checked"
                @change="handleCategoryChange"
              />
              <span :style="{ color: category.color }">{{ category.name }}</span>
            </label>
          </div>
        </div>
      </div>

      <!-- 右侧：主日历视图 -->
      <div class="main-calendar">
        <!-- 主日历头部 -->
        <div class="main-calendar-header">
          <button class="today-btn" @click="goToToday">
            <i class="fas fa-calendar-day"></i>
            今天
          </button>
          <div class="month-navigation">
            <button class="nav-btn" @click="prevMonth">
              <i class="fas fa-chevron-left"></i>
            </button>
            <span class="month-year">{{ currentYear }}年{{ currentMonth }}月</span>
            <button class="nav-btn" @click="nextMonth">
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>

        <!-- 主日历网格 -->
        <div class="main-calendar-grid">
          <!-- 星期标题 -->
          <div class="main-weekdays">
            <div v-for="day in weekDays" :key="day" class="main-weekday">{{ day }}</div>
          </div>
          <!-- 日期网格 -->
          <div class="main-days">
            <div
              v-for="(day, index) in mainCalendarDays"
              :key="index"
              :class="[
                'main-day',
                {
                  'other-month': day.otherMonth,
                  'today': day.isToday,
                  'selected': day.isSelected
                }
              ]"
              @click="selectDate(day.date)"
            >
              <div class="day-number">{{ day.day }}</div>
              <!-- 显示该日期的事件 -->
              <div v-if="day.events && day.events.length > 0" class="day-events">
                <div
                  v-for="(event, eventIndex) in day.events.slice(0, 3)"
                  :key="eventIndex"
                  class="day-event"
                  :style="{ borderColor: event.color || '#9c27b0' }"
                  @click.stop="handleEventClick(event)"
                >
                  {{ event.title }}
                </div>
                <div v-if="day.events.length > 3" class="day-event-more">
                  +{{ day.events.length - 3 }}个
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 选中日期的事件列表 -->
    <div v-if="selectedDateEvents.length > 0" class="selected-date-events">
      <div class="events-header">
        <h3>{{ selectedDateText }}</h3>
        <span class="events-count">共{{ selectedDateEvents.length }}个事件</span>
      </div>
      <div class="events-list">
        <div
          v-for="event in selectedDateEvents"
          :key="event.id"
          class="event-item"
          :style="{ borderLeftColor: event.color || '#9c27b0' }"
          @click="handleEventClick(event)"
        >
          <div class="event-time">{{ formatEventTime(event) }}</div>
          <div class="event-content">
            <div class="event-title">{{ event.title }}</div>
            <div v-if="event.description" class="event-description">{{ event.description }}</div>
            <div v-if="event.location" class="event-location">
              <i class="fas fa-map-marker-alt"></i>
              {{ event.location }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import type { CalendarEvent } from './ScheduleCalendar.types'

// Props定义
interface Props {
  events?: CalendarEvent[] // 事件列表
  selectedDate?: string // 初始选中的日期 YYYY-MM-DD
}

const props = withDefaults(defineProps<Props>(), {
  events: () => [],
  selectedDate: ''
})

// Emits定义
const emit = defineEmits<{
  'date-selected': [date: string]
  'event-click': [event: CalendarEvent]
}>()

// 星期标题
const weekDays = ['一', '二', '三', '四', '五', '六', '日']

// 格式化日期为 YYYY-MM-DD（需要先定义函数）
function formatDate(date: Date): string {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 当前日期和时间
const now = new Date()
const currentYear = ref(now.getFullYear())
const currentMonth = ref(now.getMonth() + 1)
const selectedDate = ref<string>(props.selectedDate || formatDate(now))

// 迷你日历的月份
const miniCalendarYear = ref(currentYear.value)
const miniCalendarMonth = ref(currentMonth.value)

// 分类展开状态
const categoriesExpanded = ref(true)

// 日历分类
const calendarCategories = ref([
  { id: 'files', name: '我的文件', checked: true, color: '#9c27b0' },
  { id: 'todos', name: '我的待办', checked: true, color: '#4caf50' }
])

// 获取某月的第一天是星期几（1-7，一-日）
function getFirstDayOfMonth(year: number, month: number): number {
  const date = new Date(year, month - 1, 1)
  const day = date.getDay()
  // 将星期日从0转换为7
  return day === 0 ? 7 : day
}

// 获取某月的天数
function getDaysInMonth(year: number, month: number): number {
  return new Date(year, month, 0).getDate()
}

// 判断是否为今天
function isToday(year: number, month: number, day: number): boolean {
  const today = new Date()
  return (
    year === today.getFullYear() &&
    month === today.getMonth() + 1 &&
    day === today.getDate()
  )
}

// 判断是否为选中的日期
function isSelectedDate(year: number, month: number, day: number): boolean {
  if (!selectedDate.value) return false
  const [yearStr, monthStr, dayStr] = selectedDate.value.split('-')
  return (
    year === parseInt(yearStr) &&
    month === parseInt(monthStr) &&
    day === parseInt(dayStr)
  )
}

// 获取某日期的事件
function getEventsForDate(dateStr: string): CalendarEvent[] {
  if (!props.events || props.events.length === 0) return []
  
  // 检查分类筛选
  const activeCategories = calendarCategories.value
    .filter(cat => cat.checked)
    .map(cat => cat.id)
  
  return props.events.filter(event => {
    // 检查日期匹配
    const eventDate = event.date || event.startTime.split(' ')[0]
    if (eventDate !== dateStr) return false
    
    // 检查分类筛选（如果有分类字段）
    if (event.category && !activeCategories.includes(event.category)) {
      return false
    }
    
    return true
  })
}

// 计算迷你日历的日期数组
const miniCalendarDays = computed(() => {
  const days: Array<{
    day: number
    date: string
    otherMonth: boolean
    isToday: boolean
    isSelected: boolean
  }> = []

  const year = miniCalendarYear.value
  const month = miniCalendarMonth.value
  const firstDay = getFirstDayOfMonth(year, month)
  const daysInMonth = getDaysInMonth(year, month)
  
  // 上个月的日期
  const prevMonth = month === 1 ? 12 : month - 1
  const prevYear = month === 1 ? year - 1 : year
  const daysInPrevMonth = getDaysInMonth(prevYear, prevMonth)
  
  // 填充上个月的日期（显示在日历开头）
  for (let i = firstDay - 1; i > 0; i--) {
    const day = daysInPrevMonth - i + 1
    const date = formatDate(new Date(prevYear, prevMonth - 1, day))
    days.push({
      day,
      date,
      otherMonth: true,
      isToday: isToday(prevYear, prevMonth, day),
      isSelected: isSelectedDate(prevYear, prevMonth, day)
    })
  }
  
  // 当前月的日期
  for (let day = 1; day <= daysInMonth; day++) {
    const date = formatDate(new Date(year, month - 1, day))
    days.push({
      day,
      date,
      otherMonth: false,
      isToday: isToday(year, month, day),
      isSelected: isSelectedDate(year, month, day)
    })
  }
  
  // 下个月的日期（填充到42个格子，6行×7列）
  const totalCells = 42
  const remainingCells = totalCells - days.length
  const nextMonth = month === 12 ? 1 : month + 1
  const nextYear = month === 12 ? year + 1 : year
  
  for (let day = 1; day <= remainingCells; day++) {
    const date = formatDate(new Date(nextYear, nextMonth - 1, day))
    days.push({
      day,
      date,
      otherMonth: true,
      isToday: isToday(nextYear, nextMonth, day),
      isSelected: isSelectedDate(nextYear, nextMonth, day)
    })
  }
  
  return days
})

// 计算主日历的日期数组
const mainCalendarDays = computed(() => {
  const days: Array<{
    day: number
    date: string
    otherMonth: boolean
    isToday: boolean
    isSelected: boolean
    events: CalendarEvent[]
  }> = []

  const year = currentYear.value
  const month = currentMonth.value
  const firstDay = getFirstDayOfMonth(year, month)
  const daysInMonth = getDaysInMonth(year, month)
  
  // 上个月的日期
  const prevMonth = month === 1 ? 12 : month - 1
  const prevYear = month === 1 ? year - 1 : year
  const daysInPrevMonth = getDaysInMonth(prevYear, prevMonth)
  
  // 填充上个月的日期
  for (let i = firstDay - 1; i > 0; i--) {
    const day = daysInPrevMonth - i + 1
    const date = formatDate(new Date(prevYear, prevMonth - 1, day))
    days.push({
      day,
      date,
      otherMonth: true,
      isToday: isToday(prevYear, prevMonth, day),
      isSelected: isSelectedDate(prevYear, prevMonth, day),
      events: getEventsForDate(date)
    })
  }
  
  // 当前月的日期
  for (let day = 1; day <= daysInMonth; day++) {
    const date = formatDate(new Date(year, month - 1, day))
    days.push({
      day,
      date,
      otherMonth: false,
      isToday: isToday(year, month, day),
      isSelected: isSelectedDate(year, month, day),
      events: getEventsForDate(date)
    })
  }
  
  // 下个月的日期
  const totalCells = 42
  const remainingCells = totalCells - days.length
  const nextMonth = month === 12 ? 1 : month + 1
  const nextYear = month === 12 ? year + 1 : year
  
  for (let day = 1; day <= remainingCells; day++) {
    const date = formatDate(new Date(nextYear, nextMonth - 1, day))
    days.push({
      day,
      date,
      otherMonth: true,
      isToday: isToday(nextYear, nextMonth, day),
      isSelected: isSelectedDate(nextYear, nextMonth, day),
      events: getEventsForDate(date)
    })
  }
  
  return days
})

// 选中日期的事件列表
const selectedDateEvents = computed(() => {
  return getEventsForDate(selectedDate.value)
})

// 选中日期的文本
const selectedDateText = computed(() => {
  if (!selectedDate.value) return ''
  const date = new Date(selectedDate.value + 'T00:00:00')
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekday = weekdays[date.getDay()]
  return `${year}年${month}月${day}日 ${weekday}`
})

// 格式化事件时间
function formatEventTime(event: CalendarEvent): string {
  if (!event.startTime) return ''
  
  // 如果startTime包含时间部分，提取时间
  const timePart = event.startTime.includes(' ') 
    ? event.startTime.split(' ')[1] 
    : ''
  
  if (timePart) {
    const [hour, minute] = timePart.split(':')
    return `${hour}:${minute}`
  }
  
  // 如果没有时间部分，尝试从endTime获取
  if (event.endTime) {
    const timePart = event.endTime.includes(' ') 
      ? event.endTime.split(' ')[1] 
      : ''
    if (timePart) {
      const [hour, minute] = timePart.split(':')
      return `${hour}:${minute}`
    }
  }
  
  return '全天'
}

// 选择日期（从迷你日历）
function selectDateFromMini(dateStr: string) {
  selectedDate.value = dateStr
  // 同步主日历到该日期所在的月份
  const date = new Date(dateStr + 'T00:00:00')
  currentYear.value = date.getFullYear()
  currentMonth.value = date.getMonth() + 1
  emit('date-selected', dateStr)
}

// 选择日期（从主日历）
function selectDate(dateStr: string) {
  selectedDate.value = dateStr
  // 同步迷你日历到该日期所在的月份
  const date = new Date(dateStr + 'T00:00:00')
  miniCalendarYear.value = date.getFullYear()
  miniCalendarMonth.value = date.getMonth() + 1
  emit('date-selected', dateStr)
}

// 跳转到今天
function goToToday() {
  const today = new Date()
  selectedDate.value = formatDate(today)
  currentYear.value = today.getFullYear()
  currentMonth.value = today.getMonth() + 1
  miniCalendarYear.value = today.getFullYear()
  miniCalendarMonth.value = today.getMonth() + 1
  emit('date-selected', selectedDate.value)
}

// 上一月（主日历）
function prevMonth() {
  if (currentMonth.value === 1) {
    currentMonth.value = 12
    currentYear.value--
  } else {
    currentMonth.value--
  }
}

// 下一月（主日历）
function nextMonth() {
  if (currentMonth.value === 12) {
    currentMonth.value = 1
    currentYear.value++
  } else {
    currentMonth.value++
  }
}

// 上一月（迷你日历）
function prevMonthMini() {
  if (miniCalendarMonth.value === 1) {
    miniCalendarMonth.value = 12
    miniCalendarYear.value--
  } else {
    miniCalendarMonth.value--
  }
}

// 下一月（迷你日历）
function nextMonthMini() {
  if (miniCalendarMonth.value === 12) {
    miniCalendarMonth.value = 1
    miniCalendarYear.value++
  } else {
    miniCalendarMonth.value++
  }
}

// 切换分类展开
function toggleCategories() {
  categoriesExpanded.value = !categoriesExpanded.value
}

// 处理分类变更
function handleCategoryChange() {
  // 分类变更时，事件列表会自动更新（通过computed）
}

// 处理事件点击
function handleEventClick(event: CalendarEvent) {
  emit('event-click', event)
}

// 监听props.selectedDate变化
watch(() => props.selectedDate, (newDate) => {
  if (newDate && newDate !== selectedDate.value) {
    selectedDate.value = newDate
    const date = new Date(newDate + 'T00:00:00')
    currentYear.value = date.getFullYear()
    currentMonth.value = date.getMonth() + 1
    miniCalendarYear.value = date.getFullYear()
    miniCalendarMonth.value = date.getMonth() + 1
  }
})

// 监听props.events变化
watch(() => props.events, () => {
  // 事件列表变化时，日历会自动更新（通过computed）
}, { deep: true, immediate: false })

// 组件挂载时初始化
onMounted(() => {
  if (props.selectedDate) {
    const date = new Date(props.selectedDate + 'T00:00:00')
    currentYear.value = date.getFullYear()
    currentMonth.value = date.getMonth() + 1
    miniCalendarYear.value = date.getFullYear()
    miniCalendarMonth.value = date.getMonth() + 1
  }
})
</script>

<style scoped>
/* 日历容器 */
.schedule-calendar {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.calendar-container {
  display: flex;
  gap: 20px;
}

/* 左侧边栏 */
.calendar-sidebar {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 迷你日历 */
.mini-calendar {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
}

.mini-calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.mini-calendar-header .month-year {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.nav-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.nav-btn:hover {
  background: #e0e0e0;
  color: #333;
}

.mini-calendar-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mini-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.mini-weekday {
  text-align: center;
  font-size: 12px;
  color: #666;
  font-weight: 500;
  padding: 4px;
}

.mini-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.mini-day {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #333;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
  position: relative;
}

.mini-day:hover {
  background: #e0e0e0;
}

.mini-day.other-month {
  color: #ccc;
}

.mini-day.today {
  color: #d81b60;
  font-weight: 600;
}

.mini-day.selected {
  background: #2196f3;
  color: white;
  font-weight: 600;
}

.mini-day.selected.today {
  background: #2196f3;
  color: white;
}

/* 日历分类 */
.calendar-categories {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
}

.categories-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.categories-header i {
  font-size: 12px;
  color: #666;
}

.categories-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
}

.category-item input[type="checkbox"] {
  cursor: pointer;
}

/* 主日历 */
.main-calendar {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.main-calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.today-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #f9f0ff;
  border: 1px solid #d81b60;
  border-radius: 6px;
  color: #d81b60;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.today-btn:hover {
  background: #d81b60;
  color: white;
}

.month-navigation {
  display: flex;
  align-items: center;
  gap: 15px;
}

.month-navigation .month-year {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  min-width: 120px;
  text-align: center;
}

/* 主日历网格 */
.main-calendar-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.main-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.main-weekday {
  text-align: center;
  font-size: 14px;
  color: #666;
  font-weight: 600;
  padding: 10px;
  background: #f9f9f9;
  border-radius: 6px;
}

.main-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.main-day {
  min-height: 120px;
  padding: 8px;
  border: 1px solid #eee;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.main-day:hover {
  border-color: #d81b60;
  background: #f9f0ff;
}

.main-day.other-month {
  background: #fafafa;
  opacity: 0.5;
}

.main-day.today {
  border-color: #d81b60;
  background: #fff3e0;
}

.main-day.selected {
  border-color: #2196f3;
  background: #e3f2fd;
  box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.2);
}

.main-day.selected.today {
  border-color: #2196f3;
  background: #e3f2fd;
}

.day-number {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.main-day.today .day-number {
  color: #d81b60;
  font-weight: 600;
}

.main-day.selected .day-number {
  color: #2196f3;
  font-weight: 600;
}

.main-day.other-month .day-number {
  color: #ccc;
}

/* 日期事件 */
.day-events {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  overflow: hidden;
}

.day-event {
  font-size: 11px;
  padding: 4px 6px;
  border-left: 3px solid #9c27b0;
  background: rgba(156, 39, 176, 0.1);
  border-radius: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  transition: all 0.2s;
}

.day-event:hover {
  background: rgba(156, 39, 176, 0.2);
}

.day-event-more {
  font-size: 10px;
  color: #666;
  padding: 2px 6px;
}

/* 选中日期的事件列表 */
.selected-date-events {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 2px solid #eee;
}

.events-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.events-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.events-count {
  font-size: 14px;
  color: #666;
}

.events-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.event-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background: #f9f9f9;
  border-left: 4px solid #9c27b0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.event-item:hover {
  background: #f0f0f0;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.event-time {
  min-width: 60px;
  font-size: 14px;
  font-weight: 600;
  color: #666;
  text-align: center;
  padding-top: 2px;
}

.event-content {
  flex: 1;
}

.event-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.event-description {
  font-size: 13px;
  color: #666;
  margin-bottom: 6px;
  line-height: 1.5;
}

.event-location {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 6px;
}

.event-location i {
  font-size: 10px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .calendar-container {
    flex-direction: column;
  }

  .calendar-sidebar {
    width: 100%;
  }

  .main-day {
    min-height: 100px;
  }
}

@media (max-width: 768px) {
  .schedule-calendar {
    padding: 15px;
  }

  .main-day {
    min-height: 80px;
    padding: 6px;
  }

  .day-event {
    font-size: 10px;
    padding: 3px 4px;
  }

  .today-btn {
    padding: 6px 12px;
    font-size: 12px;
  }

  .month-navigation .month-year {
    font-size: 16px;
    min-width: 100px;
  }
}
</style>
