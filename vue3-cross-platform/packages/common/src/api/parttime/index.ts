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

// 收藏记录条目（兼职）
export interface ParttimeFavoriteItem {
  id: number        // 收藏记录ID
  jobId: number     // 兼职ID
  jobTitle: string
  companyName: string
  salary: string
  location: string
  description?: string
}

// 收藏列表响应
export interface ParttimeFavoritesResponse {
  list: ParttimeFavoriteItem[]
  records?: ParttimeFavoriteItem[]
  total: number
  page: number
  size: number
}

// 浏览记录条目（兼职）
export interface ParttimeBrowseHistoryItem {
  id: number        // 浏览记录ID
  jobId: number     // 兼职ID
  jobTitle: string
  companyName: string
  salary: string
  location: string
  viewTime: string
}

// 浏览记录响应
export interface ParttimeBrowseHistoryResponse {
  records: ParttimeBrowseHistoryItem[]
  total: number
  page: number
  size: number
}

/**
 * 获取兼职列表
 * @param params 查询参数
 */
export function getParttimeList(params: ParttimeQueryParams = {}): Promise<{
  list: ParttimeJob[]  // 后端返回list字段
  records?: ParttimeJob[]  // 兼容字段
  total: number
  page: number  // 后端返回page字段
  current?: number  // 兼容字段
  size: number
}> {
  return request.get('/api/parttime/list', { params }).then((res: any) => {
    // 适配后端返回格式：list -> records, page -> current
    return {
      records: res.list || res.records || [],
      list: res.list || [],
      total: res.total || 0,
      current: res.page || res.current || 1,
      page: res.page || 1,
      size: res.size || 10
    }
  })
}

/**
 * 获取兼职详情
 * @param id 兼职ID
 */
export function getParttimeDetail(id: number): Promise<ParttimeJob> {
  return request.get<ParttimeJob>(`/api/parttime/detail/${id}`)
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
  return request.put<ParttimeJob>(`/api/parttime/update/${id}`, params)
}

/**
 * 删除兼职
 * @param id 兼职ID
 */
export function deleteParttime(id: number): Promise<void> {
  return request.delete(`/api/parttime/delete/${id}`)
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
 * @param applicationId 报名记录ID
 */
export function cancelApply(applicationId: number): Promise<void> {
  return request.delete(`/api/parttime/apply/${applicationId}`)
}

/**
 * 获取我发布的兼职（分页）
 */
export function getMyParttime(
  status?: string,
  page: number = 1,
  size: number = 10
): Promise<{
  list: ParttimeJob[]
  total: number
}> {
  return request.get('/api/parttime/my-published', {
    params: { status, page, size }
  })
}

/**
 * 获取我的报名记录（分页）
 */
export function getMyApplications(
  status?: string,
  page: number = 1,
  size: number = 10
): Promise<{
  list: any[]
  total: number
}> {
  return request.get('/api/parttime/my-applications', {
    params: { status, page, size }
  })
}

/**
 * 收藏兼职岗位
 * @param id 兼职ID
 */
export function favoriteParttime(id: number): Promise<void> {
  return request.post(`/api/parttime/favorite/${id}`)
}

/**
 * 取消收藏兼职岗位
 * @param id 兼职ID
 */
export function cancelFavoriteParttime(id: number): Promise<void> {
  return request.delete(`/api/parttime/favorite/${id}`)
}

/**
 * 获取兼职收藏列表（分页）
 */
export function getParttimeFavorites(
  page: number = 1,
  size: number = 20
): Promise<ParttimeFavoritesResponse> {
  return request.get('/api/parttime/favorites', {
    params: { page, size }
  }).then((res: any) => {
    return {
      list: res.list || res.records || [],
      records: res.list || res.records || [],
      total: res.total || 0,
      page: res.page || page,
      size: res.size || size
    }
  })
}

/**
 * 清空兼职收藏
 */
export function clearParttimeFavorites(): Promise<void> {
  return request.delete('/api/parttime/favorites/clear')
}

/**
 * 记录兼职浏览行为
 * @param id 兼职ID
 */
export function recordParttimeBrowse(id: number): Promise<void> {
  return request.post(`/api/parttime/browse/${id}`)
}

/**
 * 获取兼职浏览记录
 */
export function getParttimeBrowseHistory(
  page: number = 1,
  size: number = 20
): Promise<ParttimeBrowseHistoryResponse> {
  return request.get<ParttimeBrowseHistoryResponse>('/api/parttime/browse-history', {
    params: { page, size }
  })
}

/**
 * 删除单条兼职浏览记录
 */
export function deleteParttimeBrowseHistory(id: number): Promise<void> {
  return request.delete(`/api/parttime/browse-history/${id}`)
}

/**
 * 清空兼职浏览记录
 */
export function clearParttimeBrowseHistory(): Promise<void> {
  return request.delete('/api/parttime/browse-history/clear')
}

