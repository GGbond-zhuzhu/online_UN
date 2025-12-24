/**
 * 定位与地理位置工具函数（纯函数，无副作用）
 * 说明：这里提供一些与经纬度相关的基础计算工具，
 *       可以在 Web / App / 后端渲染环境中安全调用。
 */

// 定义简单的坐标点类型，便于复用
export interface LatLngPoint {
  latitude: number // 纬度
  longitude: number // 经度
}

// 地球半径常量（单位：米），用于距离计算
const EARTH_RADIUS_METERS = 6378137 // WGS84 椭球平均半径，常用近似值

/**
 * 将角度转换为弧度
 * @param degree 角度值（例如 30 度）
 * @returns 对应的弧度值
 */
export function degreeToRadian(degree: number): number {
  return (degree * Math.PI) / 180 // 使用公式：弧度 = 角度 × π / 180
}

/**
 * 使用 Haversine 公式计算两点之间的地表距离（近似值）
 * @param from 起点坐标（纬度 + 经度）
 * @param to 终点坐标（纬度 + 经度）
 * @returns 两点之间的距离（单位：米）
 */
export function getDistanceMeters(from: LatLngPoint, to: LatLngPoint): number {
  // 将经纬度从角度制转换为弧度制
  const lat1 = degreeToRadian(from.latitude) // 起点纬度（弧度）
  const lng1 = degreeToRadian(from.longitude) // 起点经度（弧度）
  const lat2 = degreeToRadian(to.latitude) // 终点纬度（弧度）
  const lng2 = degreeToRadian(to.longitude) // 终点经度（弧度）

  // 计算两点纬度和经度差值（弧度）
  const dLat = lat2 - lat1 // 纬度差
  const dLng = lng2 - lng1 // 经度差

  // Haversine 公式的核心计算部分
  const haversine =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) + // sin²(Δφ/2)
    Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLng / 2) * Math.sin(dLng / 2) // cos φ1 · cos φ2 · sin²(Δλ/2)

  const c = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine)) // 计算大圆距离对应的中心角

  // 距离 = 地球半径 × 中心角
  return EARTH_RADIUS_METERS * c // 返回两点之间的近似地表距离（米）
}

/**
 * 判断某个坐标点是否在一个“矩形经纬度范围”内
 * @param point 要判断的坐标点
 * @param bounds 范围对象（包含最小/最大纬度和经度）
 * @returns true 表示在范围内，false 表示不在
 */
export function isPointInBounds(
  point: LatLngPoint, // 要判断的坐标点
  bounds: { minLat: number; maxLat: number; minLng: number; maxLng: number } // 范围定义
): boolean {
  return (
    point.latitude >= bounds.minLat && // 纬度大于等于最小纬度
    point.latitude <= bounds.maxLat && // 且小于等于最大纬度
    point.longitude >= bounds.minLng && // 经度大于等于最小经度
    point.longitude <= bounds.maxLng // 且小于等于最大经度
  ) // 满足上述条件则认为在矩形范围内
}

/**
 * 根据圆形范围（中心点 + 半径）判断某个坐标是否在“某校园范围”附近
 * @param point 当前坐标点
 * @param center 校园中心点坐标
 * @param radiusMeters 半径（单位：米）
 * @returns true 表示在半径范围内，false 表示不在
 */
export function isPointInCircleRange(point: LatLngPoint, center: LatLngPoint, radiusMeters: number): boolean {
  const distance = getDistanceMeters(point, center) // 计算当前位置与校园中心之间的距离
  return distance <= radiusMeters // 如果距离小于等于给定半径，则认为在范围内
}
