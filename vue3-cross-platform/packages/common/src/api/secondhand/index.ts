/**
 * 二手交易相关API
 * 包括商品发布、查询、收藏、管理等
 */
import request from '../../utils/request'

// 商品信息（与后端 SecondhandGoodsVO 基本对应）
export interface SecondhandGoods {
  id: number
  title: string
  description: string
  price: number
  originalPrice?: number
  // 后端字段：imageUrls，前端统一用 images 访问
  imageUrls?: string[]
  images?: string[]
  category: string
  status: string
  publisherId?: number
  publisherName?: string
  schoolId?: number
  schoolName?: string
  favoriteCount?: number
  viewCount?: number
  publishTime?: string
  updateTime?: string
}

// 商品查询参数
export interface SecondhandQueryParams {
  page?: number
  pageSize?: number
  keyword?: string
  category?: string
  campusId?: number
  minPrice?: number
  maxPrice?: number
  status?: string
}

// 发布商品参数（与后端 SecondhandPublishDTO 对应）
export interface PublishGoodsParams {
  title: string
  description: string
  price: number
  originalPrice?: number
  category: string // 后端使用枚举：ELECTRONICS, BOOKS, CLOTHING, DAILY, SPORTS, STUDY, OTHER
  status?: string // 商品状态：ON_SALE, OFF_SHELF, SOLD
  imageUrls?: string[]
  contactPhone?: string
  contactWechat?: string
  location?: string
}

// 收藏列表响应
export interface SecondhandFavoritesResponse {
  list: SecondhandGoods[]
  records?: SecondhandGoods[]
  total: number
  page: number
  size: number
}

// 浏览记录条目
export interface SecondhandBrowseHistoryItem {
  id: number        // 浏览记录ID
  goodsId: number   // 商品ID
  goodsTitle: string
  goodsImage?: string | null
  price: number
  viewTime: string
}

// 浏览记录响应
export interface SecondhandBrowseHistoryResponse {
  records: SecondhandBrowseHistoryItem[]
  total: number
  page: number
  size: number
}

/**
 * 获取商品列表
 * @param params 查询参数
 */
export function getGoodsList(params: SecondhandQueryParams = {}): Promise<{
  list: SecondhandGoods[]  // 后端返回list字段
  records?: SecondhandGoods[]  // 兼容字段
  total: number
  page: number  // 后端返回page字段
  current?: number  // 兼容字段
  size: number
}> {
  return request.get('/api/secondhand/list', { params }).then((res: any) => {
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
 * 获取商品详情
 * @param id 商品ID
 */
export function getGoodsDetail(id: number): Promise<SecondhandGoods> {
  return request.get<SecondhandGoods>(`/api/secondhand/detail/${id}`)
}

/**
 * 发布商品
 * @param params 发布参数
 */
export function publishGoods(params: PublishGoodsParams): Promise<SecondhandGoods> {
  return request.post<SecondhandGoods>('/api/secondhand/publish', params)
}

/**
 * 更新商品信息
 * @param id 商品ID
 * @param params 更新参数
 */
export function updateGoods(id: number, params: Partial<PublishGoodsParams>): Promise<SecondhandGoods> {
  return request.put<SecondhandGoods>(`/api/secondhand/update/${id}`, params)
}

/**
 * 删除商品
 * @param id 商品ID
 */
export function deleteGoods(id: number): Promise<void> {
  return request.delete(`/api/secondhand/delete/${id}`)
}

/**
 * 收藏商品
 * @param id 商品ID
 */
export function collectGoods(id: number): Promise<void> {
  return request.post(`/api/secondhand/favorite/${id}`)
}

/**
 * 取消收藏
 * @param id 商品ID
 */
export function uncollectGoods(id: number): Promise<void> {
  return request.delete(`/api/secondhand/favorite/${id}`)
}

/**
 * 获取我的发布（当前用户发布的二手商品）
 */
export function getMyGoods(): Promise<SecondhandGoods[]> {
  return request.get<SecondhandGoods[]>('/api/secondhand/my-goods')
}

/**
 * 获取收藏列表（分页）
 * @param page 页码
 * @param size 每页数量
 */
export function getSecondhandFavorites(
  page: number = 1,
  size: number = 20
): Promise<SecondhandFavoritesResponse> {
  return request.get('/api/secondhand/favorites', {
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
 * 获取浏览记录列表（分页）
 * @param page 页码
 * @param size 每页数量
 */
export function getSecondhandBrowseHistory(
  page: number = 1,
  size: number = 20
): Promise<SecondhandBrowseHistoryResponse> {
  return request.get<SecondhandBrowseHistoryResponse>('/api/secondhand/browse-history', {
    params: { page, size }
  })
}

/**
 * 删除单条浏览记录
 * @param historyId 浏览记录ID
 */
export function deleteSecondhandBrowseHistory(historyId: number): Promise<void> {
  return request.delete(`/api/secondhand/browse-history/${historyId}`)
}

/**
 * 清空浏览记录
 */
export function clearSecondhandBrowseHistory(): Promise<void> {
  return request.delete('/api/secondhand/browse-history/clear')
}

/**
 * 记录商品浏览行为
 * @param goodsId 商品ID
 */
export function recordSecondhandBrowse(goodsId: number): Promise<void> {
  return request.post(`/api/secondhand/browse/${goodsId}`)
}

