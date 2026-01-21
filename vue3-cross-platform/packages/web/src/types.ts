/**
 * Web 端通用类型定义
 * 说明：历史代码使用 `../types` / `../../types` 引入，这里提供统一入口，避免模块缺失导致构建失败。
 */

export enum ScheduleCategory {
  ALL = 'ALL',
  COURSE = 'COURSE',
  EXAM = 'EXAM',
  CLUB = 'CLUB',
  PART_TIME = 'PART_TIME',
  PERSONAL = 'PERSONAL'
}

export enum ScheduleStatus {
  UPCOMING = 'UPCOMING',
  ONGOING = 'ONGOING',
  COMPLETED = 'COMPLETED'
}

export interface Schedule {
  id: string
  title: string
  category: ScheduleCategory
  startTime: string
  endTime?: string
  dateKey: string
  location?: string
  description?: string
  status: ScheduleStatus
  sharedWith: string[]
  moodIcon?: string
  isAllDay?: boolean
  color?: string
}

export interface DailyMood {
  dateKey: string
  moodIcon: string
}

