/**
 * 地图服务相关API
 * 包括POI搜索、路径规划、逆地理编码等
 */
import request from '../../utils/request'

// POI搜索结果
export interface POIResult {
  name: string
  address: string
  longitude: number
  latitude: number
}

// 路径规划结果
export interface RoutePlanResult {
  totalDistance: number // 总距离（公里）
  totalDuration: number // 总时长（分钟）
  steps: RouteStep[] // 路线步骤
}

// 路线步骤
export interface RouteStep {
  instruction: string // 步骤说明
  distance?: number // 步骤距离（公里）
}

// POI搜索参数
export interface MapSearchParams {
  keyword: string // 搜索关键词
  city?: string // 城市名称（可选）
  pageSize?: number // 返回结果数量
  page?: number // 页码
}

// 路径规划参数
export interface RoutePlanParams {
  points: Array<{
    longitude: number
    latitude: number
  }> // 地点坐标列表（至少2个）
  strategy?: number // 路径规划策略（0:速度优先, 1:费用优先, 2:距离最短, 3:不走高速）
}

/**
 * POI搜索（地点搜索）
 * @param params 搜索参数
 */
export function searchPOI(params: MapSearchParams): Promise<POIResult[]> {
  return request.post<POIResult[]>('/api/map/search', params)
}

/**
 * 路径规划
 * @param params 路径规划参数
 */
export function planRoute(params: RoutePlanParams): Promise<RoutePlanResult> {
  return request.post<RoutePlanResult>('/api/map/route/plan', params)
}

/**
 * 逆地理编码（根据经纬度获取地址）
 * @param longitude 经度
 * @param latitude 纬度
 */
export function reverseGeocode(longitude: number, latitude: number): Promise<{
  formattedAddress: string
  province?: string
  city?: string
  district?: string
  street?: string
}> {
  return request.get('/api/map/reverse-geocode', {
    params: { longitude, latitude }
  })
}

