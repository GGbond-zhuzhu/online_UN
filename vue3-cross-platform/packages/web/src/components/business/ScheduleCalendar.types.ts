/**
 * 日历组件类型定义
 */

// 事件接口定义
export interface CalendarEvent {
  id: number
  title: string
  description?: string
  location?: string
  startTime: string
  endTime?: string
  color?: string
  category?: string
  date: string // YYYY-MM-DD格式
}
