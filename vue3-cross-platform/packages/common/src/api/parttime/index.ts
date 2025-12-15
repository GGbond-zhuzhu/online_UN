/**
 * 兼职相关API
 * 包括兼职发布、报名、审核、管理等
 */
import request from '../../utils/request'

// 兼职信息
export interface ParttimeJob {
  id: number
  title: string
  description: string
  salary: number
  salaryType: string
  location: string
  workTime: string
  requirements?: string
  contact: string
  status: string
  publisherId: number
  publisherName: string
  campusId: number
  campusName: string
  createTime: string
  updateTime: string
}

// 兼职查询参数
export interface ParttimeQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  campusId?: number
  minSalary?: number
  maxSalary?: number
  status?: string
}

// 发布兼职参数
export interface PublishParttimeParams {
  title: string
  description: string
  salary: number
  salaryType: string
  location: string
  workTime: string
  requirements?: string
  contact: string
}

// 报名参数
export interface ApplyParttimeParams {
  jobId: number
  resume?: string
  message?: string
}

/**
 * 获取兼职列表
 * @param params 查询参数
 */
export function getParttimeList(params: ParttimeQueryParams = {}): Promise<{
  records: ParttimeJob[]
  total: number
  current: number
  size: number
}> {
  return request.get('/api/parttime/list', { params })
}

/**
 * 获取兼职详情
 * @param id 兼职ID
 */
export function getParttimeDetail(id: number): Promise<ParttimeJob> {
  return request.get<ParttimeJob>(`/api/parttime/${id}`)
}

/**
 * 发布兼职
 * @param params 发布参数
 */
export function publishParttime(params: PublishParttimeParams): Promise<ParttimeJob> {
  return request.post<ParttimeJob>('/api/parttime/publish', params)
}

/**
 * 更新兼职信息
 * @param id 兼职ID
 * @param params 更新参数
 */
export function updateParttime(id: number, params: Partial<PublishParttimeParams>): Promise<ParttimeJob> {
  return request.put<ParttimeJob>(`/api/parttime/${id}`, params)
}

/**
 * 删除兼职
 * @param id 兼职ID
 */
export function deleteParttime(id: number): Promise<void> {
  return request.delete(`/api/parttime/${id}`)
}

/**
 * 报名兼职
 * @param params 报名参数
 */
export function applyParttime(params: ApplyParttimeParams): Promise<void> {
  return request.post('/api/parttime/apply', params)
}

/**
 * 取消报名
 * @param jobId 兼职ID
 */
export function cancelApply(jobId: number): Promise<void> {
  return request.delete(`/api/parttime/apply/${jobId}`)
}

/**
 * 获取我的发布
 */
export function getMyParttime(): Promise<ParttimeJob[]> {
  return request.get<ParttimeJob[]>('/api/parttime/my-jobs')
}

/**
 * 获取我的报名
 */
export function getMyApplications(): Promise<ParttimeJob[]> {
  return request.get<ParttimeJob[]>('/api/parttime/my-applications')
}

