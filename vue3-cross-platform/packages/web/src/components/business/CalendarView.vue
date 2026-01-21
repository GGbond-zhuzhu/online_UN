<template>
  <div class="calendar-view">
    <!-- 视图切换和导航 -->
    <div class="calendar-header">
      <div class="header-left">
        <button class="nav-btn" @click="prevView">
          <i class="fas fa-chevron-left"></i>
        </button>
        <h2 class="current-view">{{ currentViewText }}</h2>
        <button class="nav-btn" @click="nextView">
          <i class="fas fa-chevron-right"></i>
        </button>
        <button class="today-btn" @click="goToToday">
          今天
        </button>
      </div>
      <div class="header-right">
        <div class="view-switcher">
          <button
            v-for="view in viewTypes"
            :key="view.value"
            class="view-btn"
            :class="{ active: currentViewType === view.value }"
            @click="switchView(view.value)"
          >
            <i :class="view.icon"></i>
            <span>{{ view.label }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 日视图 -->
    <div v-if="currentViewType === 'day'" class="day-view">
      <div class="day-header">
        <h3>{{ formatDateCN(dayjs(currentDate)) }}</h3>
        <div class="day-mood" v-if="currentDayMood">
          <i :class="currentDayMood" style="font-size: 24px; color: #FF6B9D;"></i>
          <span>今日心情</span>
        </div>
      </div>
      <div class="time-slots">
        <div
          v-for="(slot, index) in dayTimeSlots"
          :key="index"
          class="time-slot"
        >
          <div class="slot-time">{{ slot }}</div>
          <div class="slot-events">
            <template v-if="getEventsForTimeSlot(slot).length > 0">
              <div
                v-for="event in getEventsForTimeSlot(slot)"
                :key="event.id"
                class="event-item"
                :style="{ backgroundColor: event.color, borderLeftColor: event.color }"
                @click="handleEventClick(event)"
              >
                <div class="event-time">
                  {{ event.startTime }} - {{ event.endTime }}
                </div>
                <div class="event-content">
                  <div class="event-title">{{ event.title }}</div>
                  <div v-if="event.location" class="event-location">
                    <i class="fas fa-map-marker-alt"></i>
                    {{ event.location }}
                  </div>
                </div>
              </div>
            </template>
            <div v-else class="empty-slot">
              <i class="fas fa-calendar-times"></i>
              <span>暂无行程</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 周视图 -->
    <div v-else-if="currentViewType === 'week'" class="week-view">
      <div class="week-days">
        <div
          v-for="(day, index) in weekDays"
          :key="index"
          class="week-day"
          :class="{ 'today': isToday(day.dateKey) }"
        >
          <div class="day-date">
            <span class="day-number">{{ day.date }}</span>
            <span class="day-weekday">{{ day.weekday }}</span>
          </div>
          <div class="day-mood" v-if="day.moodIcon">
            <i :class="day.moodIcon" style="font-size: 18px; color: #FF6B9D;"></i>
          </div>
          <div class="day-events">
            <div
              v-for="event in getEventsForDate(day.dateKey)"
              :key="event.id"
              class="event-item"
              :style="{ backgroundColor: event.color + '80', borderLeftColor: event.color }"
              @click="handleEventClick(event)"
            >
              <div class="event-title">{{ event.title }}</div>
              <div class="event-time">{{ event.startTime }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 月视图 -->
    <div v-else-if="currentViewType === 'month'" class="month-view">
      <div class="month-grid">
        <!-- 星期标题 -->
        <div class="month-weekdays">
          <div v-for="weekday in monthWeekdays" :key="weekday" class="month-weekday">
            {{ weekday }}
          </div>
        </div>
        <!-- 日期网格 -->
        <div class="month-days">
          <div
            v-for="(day, index) in monthDays"
            :key="index"
            class="month-day"
            :class="{
              'today': day.isToday,
              'other-month': day.isOtherMonth,
              'selected': day.isSelected
            }"
            @click="selectDate(day.dateKey)"
          >
            <div class="day-number">{{ day.date }}</div>
            <div class="day-mood" v-if="day.moodIcon">
              <i :class="day.moodIcon" style="font-size: 12px; color: #FF6B9D;"></i>
            </div>
            <div class="day-events">
              <div
                v-for="(event, eventIndex) in day.events"
                :key="eventIndex"
                class="event-dot"
                :style="{ backgroundColor: event.color }"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { Schedule, DailyMood } from '../../types'
import dayjs from '../../utils/date'
import { formatDateCN, getDateKey, getWeekStart, generateTimeSlots } from '../../utils/date'
import { moodStorage } from '../../utils/storage'

interface Props {
  events?: Schedule[]
  moods?: DailyMood[]
  selectedDate?: string
}

const props = withDefaults(defineProps<Props>(), {
  events: () => [] as Schedule[],
  moods: () => [] as DailyMood[],
  selectedDate: getDateKey()
})

const emit = defineEmits<{
  'select-date': [dateKey: string]
  'event-click': [event: Schedule]
  'change-view': [viewType: string]
}>()

// 视图类型
const viewTypes = [
  { value: 'day', label: '日', icon: 'fas fa-calendar-day' },
  { value: 'week', label: '周', icon: 'fas fa-calendar-week' },
  { value: 'month', label: '月', icon: 'fas fa-calendar-alt' }
]

// 当前视图类型
const currentViewType = ref('month')

// 当前查看的日期
const currentDate = ref(props.selectedDate)

// 当前视图文本
const currentViewText = computed(() => {
  const date = dayjs(currentDate.value)
  switch (currentViewType.value) {
    case 'day':
      return formatDateCN(date)
    case 'week':
      const weekStart = getWeekStart(date)
      const weekEnd = weekStart.add(6, 'day')
      return `${weekStart.format('YYYY年MM月DD日')} - ${weekEnd.format('YYYY年MM月DD日')}`
    case 'month':
      return `${date.year()}年${date.month() + 1}月`
    default:
      return ''
  }
})

// 星期标题
const monthWeekdays = ['日', '一', '二', '三', '四', '五', '六']

// 日视图时间槽
const dayTimeSlots = generateTimeSlots()

// 当前日期的心情
const currentDayMood = computed(() => {
  const mood = props.moods.find(m => m.dateKey === currentDate.value)
  return mood?.moodIcon
})

// 获取某一日期的事件（添加缓存机制提高性能）
const eventsByDate = computed(() => {
  return props.events.reduce<Record<string, Schedule[]>>((acc, event) => {
    if (!acc[event.dateKey]) {
      acc[event.dateKey] = []
    }
    acc[event.dateKey].push(event)
    return acc
  }, {})
})

// 获取某一日期的事件
const getEventsForDate = (dateKey: string) => {
  return eventsByDate.value[dateKey] || []
}

// 获取日视图某一时间槽的事件（优化性能）
const getEventsForTimeSlot = (timeSlot: string) => {
  const dayEvents = getEventsForDate(currentDate.value)
  if (!dayEvents.length) return []
  
  const slotHour = parseInt(timeSlot.split(':')[0])
  return dayEvents.filter(event => {
    const eventHour = parseInt(event.startTime.split(':')[0])
    return eventHour === slotHour
  })
}

// 定义月视图和周视图的日期数据结构
interface CalendarDay {
  date: number;
  dateKey: string;
  isToday: boolean;
  isSelected: boolean;
  isOtherMonth: boolean;
  events: Schedule[];
  moodIcon?: string;
}

interface WeekDay extends CalendarDay {
  weekday: string;
}

// 月视图日期数据
const monthDays = computed(() => {
  const date = dayjs(currentDate.value)
  const year = date.year()
  const month = date.month() + 1
  const firstDay = dayjs(new Date(year, month - 1, 1))
  const firstDayOfWeek = firstDay.day() // 0-6，0是周日
  const daysInMonth = dayjs(new Date(year, month, 0)).date()
  const days: CalendarDay[] = []

  // 添加上个月的日期
  for (let i = firstDayOfWeek - 1; i >= 0; i--) {
    const prevDate = firstDay.subtract(i + 1, 'day')
    const dateKey = getDateKey(prevDate)
    days.push({
      date: prevDate.date(),
      dateKey: dateKey,
      isToday: false,
      isSelected: false,
      isOtherMonth: true,
      events: getEventsForDate(dateKey),
      moodIcon: props.moods.find(m => m.dateKey === dateKey)?.moodIcon
    })
  }

  // 添加当前月的日期
  for (let i = 1; i <= daysInMonth; i++) {
    const currentDay = dayjs(new Date(year, month - 1, i))
    const dateKey = getDateKey(currentDay)
    days.push({
      date: i,
      dateKey: dateKey,
      isToday: dateKey === getDateKey(dayjs()),
      isSelected: dateKey === currentDate.value,
      isOtherMonth: false,
      events: getEventsForDate(dateKey),
      moodIcon: props.moods.find(m => m.dateKey === dateKey)?.moodIcon
    })
  }

  // 添加下个月的日期
  const remainingDays = 42 - days.length // 6行7列共42个单元格
  for (let i = 1; i <= remainingDays; i++) {
    const nextDate = firstDay.add(daysInMonth + i - 1, 'day')
    const dateKey = getDateKey(nextDate)
    days.push({
      date: nextDate.date(),
      dateKey: dateKey,
      isToday: false,
      isSelected: false,
      isOtherMonth: true,
      events: getEventsForDate(dateKey),
      moodIcon: props.moods.find(m => m.dateKey === dateKey)?.moodIcon
    })
  }

  return days
})

// 周视图日期数据
const weekDays = computed(() => {
  const date = dayjs(currentDate.value)
  const weekStart = getWeekStart(date)
  const days: WeekDay[] = []

  for (let i = 0; i < 7; i++) {
    const currentDay = weekStart.add(i, 'day')
    const dateKey = getDateKey(currentDay)
    days.push({
      date: currentDay.date(),
      dateKey: dateKey,
      weekday: monthWeekdays[currentDay.day()],
      isToday: dateKey === getDateKey(dayjs()),
      moodIcon: props.moods.find(m => m.dateKey === dateKey)?.moodIcon
    })
  }

  return days
})

// 切换视图
const switchView = (viewType: string) => {
  currentViewType.value = viewType
  emit('change-view', viewType)
}

// 上一视图
const prevView = () => {
  const date = dayjs(currentDate.value)
  switch (currentViewType.value) {
    case 'day':
      currentDate.value = getDateKey(date.subtract(1, 'day'))
      break
    case 'week':
      currentDate.value = getDateKey(date.subtract(7, 'day'))
      break
    case 'month':
      currentDate.value = getDateKey(date.subtract(1, 'month'))
      break
  }
}

// 下一视图
const nextView = () => {
  const date = dayjs(currentDate.value)
  switch (currentViewType.value) {
    case 'day':
      currentDate.value = getDateKey(date.add(1, 'day'))
      break
    case 'week':
      currentDate.value = getDateKey(date.add(7, 'day'))
      break
    case 'month':
      currentDate.value = getDateKey(date.add(1, 'month'))
      break
  }
}

// 前往今天
const goToToday = () => {
  currentDate.value = getDateKey(dayjs())
}

// 选择日期
const selectDate = (dateKey: string) => {
  currentDate.value = dateKey
  emit('select-date', dateKey)
}

// 处理事件点击
const handleEventClick = (event: Schedule) => {
  emit('event-click', event)
}

// 监听props.selectedDate变化
watch(() => props.selectedDate, (newDate) => {
  if (newDate && newDate !== currentDate.value) {
    currentDate.value = newDate
  }
})
</script>

<style scoped lang="scss">
.calendar-view {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 日历头部 */
.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%);
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-view {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.nav-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
  }
}

.today-btn {
  background: white;
  color: #FF6B9D;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 10px rgba(255, 255, 255, 0.3);
  }
}

.header-right {
  display: flex;
  align-items: center;
}

.view-switcher {
  display: flex;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 25px;
  padding: 5px;
}

.view-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  background: none;
  border: none;
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }

  &.active {
    background: white;
    color: #FF6B9D;
  }
}

/* 日视图 */
.day-view {
  padding: 20px;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.day-header h3 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.day-mood {
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 107, 157, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
}

.day-mood span {
  color: #FF6B9D;
  font-weight: 500;
}

.time-slots {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.time-slot {
  display: flex;
  border-bottom: 1px solid #eee;
  min-height: 60px;
}

.slot-time {
  width: 80px;
  padding: 10px;
  text-align: right;
  color: #666;
  font-weight: 500;
  flex-shrink: 0;
}

.slot-events {
  flex: 1;
  padding: 5px;
  min-height: 60px;
}

/* 周视图 */
.week-view {
  padding: 20px;
}

.week-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 15px;
}

.week-day {
  background: #f9f9f9;
  border-radius: 12px;
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.week-day.today {
  background: #fff3e0;
  border: 2px solid #ff9800;
}

.day-date {
  text-align: center;
}

.day-number {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.day-weekday {
  font-size: 12px;
  color: #666;
}

.day-events {
  display: flex;
  flex-direction: column;
  gap: 5px;
  flex: 1;
  overflow: hidden;
}

/* 月视图 */
.month-view {
  padding: 20px;
}

.month-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.month-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10px;
}

.month-weekday {
  text-align: center;
  font-weight: 600;
  color: #666;
  padding: 10px;
}

.month-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10px;
}

.month-day {
  aspect-ratio: 1;
  background: #f9f9f9;
  border-radius: 8px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 5px;
  cursor: pointer;
  transition: all 0.3s;
  min-height: 100px;

  &:hover {
    background: rgba(255, 107, 157, 0.1);
    border-color: #FF6B9D;
  }

  &.today {
    background: #E3F2FD;
    border: 2px solid #2196f3;
  }

  &.other-month {
    opacity: 0.5;
  }

  &.selected {
    background: rgba(255, 107, 157, 0.2);
    border: 2px solid #FF6B9D;
  }
}

/* 事件样式 */
.event-item {
  background: #FF6B9D;
  border-left: 4px solid #E91E63;
  border-radius: 8px;
  padding: 10px;
  margin: 5px 0;
  cursor: pointer;
  transition: all 0.3s;
  color: white;
  font-size: 14px;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  }
}

.event-time {
  font-weight: 600;
  margin-bottom: 5px;
  font-size: 12px;
}

.event-title {
  font-weight: 600;
  margin-bottom: 5px;
}

.event-location {
  font-size: 12px;
  opacity: 0.9;
  display: flex;
  align-items: center;
  gap: 5px;
}

.event-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin: 2px 1px;
  display: inline-block;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .calendar-header {
    flex-direction: column;
    gap: 15px;
    padding: 15px;
  }

  .week-days {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .calendar-view {
    border-radius: 0;
  }

  .view-switcher {
    flex-wrap: wrap;
  }

  .view-btn span {
    display: none;
  }

  .week-days {
    grid-template-columns: repeat(2, 1fr);
  }

  .month-days {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 480px) {
  .calendar-header {
    padding: 10px;
  }

  .current-view {
    font-size: 16px;
  }

  .day-view, .week-view, .month-view {
    padding: 10px;
  }

  .week-days {
    grid-template-columns: 1fr;
  }

  .month-days {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
