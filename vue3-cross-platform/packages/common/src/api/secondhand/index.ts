/**
 * 二手交易相关API
 * 包括商品发布、查询、收藏、管理等
 */
import request from '../../utils/request'

// 商品信息
export interface SecondhandGoods {
  id: number
  title: string
  description: string
  price: number
  images: string[]
  category: string
  status: string
  publisherId: number
  publisherName: string
  campusId: number
  campusName: string
  createTime: string
  updateTime: string
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

// 发布商品参数
export interface PublishGoodsParams {
  title: string
  description: string
  price: number
  images: string[]
  category: string
}

/**
 * 获取商品列表
 * @param params 查询参数
 */
export function getGoodsList(params: SecondhandQueryParams = {}): Promise<{
  records: SecondhandGoods[]
  total: number
  current: number
  size: number
}> {
  return request.get('/api/secondhand/list', { params })
}

/**
 * 获取商品详情
 * @param id 商品ID
 */
export function getGoodsDetail(id: number): Promise<SecondhandGoods> {
  return request.get<SecondhandGoods>(`/api/secondhand/${id}`)
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
  return request.put<SecondhandGoods>(`/api/secondhand/${id}`, params)
}

/**
 * 删除商品
 * @param id 商品ID
 */
export function deleteGoods(id: number): Promise<void> {
  return request.delete(`/api/secondhand/${id}`)
}

/**
 * 收藏商品
 * @param id 商品ID
 */
export function collectGoods(id: number): Promise<void> {
  return request.post(`/api/secondhand/${id}/collect`)
}

/**
 * 取消收藏
 * @param id 商品ID
 */
export function uncollectGoods(id: number): Promise<void> {
  return request.delete(`/api/secondhand/${id}/collect`)
}

/**
 * 获取我的发布
 */
export function getMyGoods(): Promise<SecondhandGoods[]> {
  return request.get<SecondhandGoods[]>('/api/secondhand/my-goods')
}

/**
 * 获取我的收藏
 */
export function getMyCollections(): Promise<SecondhandGoods[]> {
  return request.get<SecondhandGoods[]>('/api/secondhand/my-collections')
}

