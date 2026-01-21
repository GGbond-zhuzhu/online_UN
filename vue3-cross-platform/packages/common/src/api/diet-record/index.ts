/**
 * 饮食记录相关API
 */
import request from '../../utils/request'

// 饮食记录
export interface DietRecord {
  id: number
  userId: number
  dietDate: string
  mealType: string // BREAKFAST、LUNCH、DINNER、SNACK
  foodName: string
  foodDetail?: string
  location?: string
  source: string // CANTEEN、TAKEOUT、RESTAURANT、HOME、OTHER
  isAutoImport: boolean
  backgroundColor?: string
  calories?: number
  price?: number
  createTime: string
}

// 饮食记录DTO
export interface DietRecordDTO {
  mealType: string
  foodName: string
  foodDetail?: string
  location?: string
  source: string
  dietDate?: string
  backgroundColor?: string
  calories?: number
  price?: number
}

/**
 * 添加饮食记录
 */
export function addDietRecord(data: DietRecordDTO) {
  return request.post<DietRecord>('/api/diet-record/add', data)
}

/**
 * 删除饮食记录
 */
export function deleteDietRecord(id: number) {
  return request.delete(`/api/diet-record/${id}`)
}

/**
 * 更新饮食记录
 */
export function updateDietRecord(id: number, data: DietRecordDTO) {
  return request.put<DietRecord>(`/api/diet-record/${id}`, data)
}

/**
 * 查询饮食记录列表
 */
export function getDietRecords(params: {
  startDate?: string
  endDate?: string
  mealType?: string
  page?: number
  size?: number
}) {
  return request.get<DietRecord[]>('/api/diet-record/list', { params })
}

/**
 * 获取某天的饮食记录
 */
export function getDietRecordsByDate(date?: string) {
  return request.get<DietRecord[]>('/api/diet-record/by-date', {
    params: date ? { date } : {}
  })
}

/**
 * 根据ID获取单条饮食记录
 */
export function getDietRecordById(id: number) {
  return request.get<DietRecord>(`/api/diet-record/${id}`)
}

/**
 * 自动导入E卡通食堂消费
 */
export function autoImportDietFromEcard(params?: {
  startDate?: string
  endDate?: string
}) {
  return request.post('/api/diet-record/auto-import', null, { params })
}

/**
 * 获取饮食统计
 */
export function getDietStatistics(params?: {
  startDate?: string
  endDate?: string
}) {
  return request.get('/api/diet-record/statistics', { params })
}

