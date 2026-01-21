/**
 * 记账本相关API
 */
import request from '../../utils/request'

// 记账记录
export interface AccountBookRecord {
  id: number
  userId: number
  amount: number
  category: string // 餐饮、交通、购物、娱乐、学习、其他
  description?: string
  consumeDate: string
  isAutoImport: boolean
  location?: string
  payMethod: string // CARD、CASH、ALIPAY、WECHAT、OTHER
  createTime: string
}

// 记账记录DTO
export interface AccountBookDTO {
  amount: number
  category: string
  description?: string
  consumeDate?: string
  location?: string
  payMethod?: string
}

// 周报
export interface WeeklyReport {
  weekRange: string
  totalAmount: number
  totalCount: number
  avgDailyAmount: number
  categoryStats: CategoryStat[]
  locationStats: LocationStat[]
  dailyStats: DailyStat[]
}

// 月报
export interface MonthlyReport {
  monthRange: string
  totalAmount: number
  totalCount: number
  avgDailyAmount: number
  categoryStats: CategoryStat[]
  locationStats: LocationStat[]
  dailyStats: DailyStat[]
  comparison: {
    lastMonthAmount: number
    lastMonthCount: number
    amountChange: number
    amountChangePercent: number
  }
}

// 年报
export interface YearlyReport {
  yearRange: string
  totalAmount: number
  totalCount: number
  avgMonthlyAmount: number
  categoryStats: CategoryStat[]
  locationStats: LocationStat[]
  monthlyStats: MonthlyStat[]
  comparison: {
    lastYearAmount: number
    lastYearCount: number
    amountChange: number
    amountChangePercent: number
  }
}

export interface MonthlyStat {
  month: string
  amount: number
  count: number
}

export interface CategoryStat {
  category: string
  amount: number
  count: number
  percentage: number
}

export interface LocationStat {
  location: string
  amount: number
  count: number
  percentage: number
}

export interface DailyStat {
  date: string
  amount: number
  count: number
}

/**
 * 添加记账记录
 */
export function addAccountBookRecord(data: AccountBookDTO) {
  return request.post<AccountBookRecord>('/api/account-book/add', data)
}

/**
 * 删除记账记录
 */
export function deleteAccountBookRecord(id: number) {
  return request.delete(`/api/account-book/${id}`)
}

/**
 * 更新记账记录
 */
export function updateAccountBookRecord(id: number, data: AccountBookDTO) {
  return request.put<AccountBookRecord>(`/api/account-book/${id}`, data)
}

/**
 * 查询记账记录列表
 */
export function getAccountBookRecords(params: {
  startDate?: string
  endDate?: string
  category?: string
  page?: number
  size?: number
}) {
  return request.get<AccountBookRecord[]>('/api/account-book/list', { params })
}

/**
 * 获取周报
 */
export function getWeeklyReport(weekStartDate?: string) {
  return request.get<WeeklyReport>('/api/account-book/weekly-report', {
    params: weekStartDate ? { weekStartDate } : {}
  })
}

/**
 * 获取月报
 */
export function getMonthlyReport(monthDate?: string) {
  return request.get<MonthlyReport>('/api/account-book/monthly-report', {
    params: monthDate ? { monthDate } : {}
  })
}

/**
 * 获取年报
 */
export function getYearlyReport(yearDate?: string) {
  return request.get<YearlyReport>('/api/account-book/yearly-report', {
    params: yearDate ? { yearDate } : {}
  })
}

/**
 * 自动导入E卡通消费
 */
export function autoImportFromEcard(params?: {
  startDate?: string
  endDate?: string
}) {
  return request.post('/api/account-book/auto-import', null, { params })
}

/**
 * 获取统计信息
 */
export function getAccountBookStatistics(params?: {
  startDate?: string
  endDate?: string
}) {
  return request.get('/api/account-book/statistics', { params })
}

