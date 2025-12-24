/**
 * 校园相关配置
 * 说明：用于维护平台支持的校园列表、默认校园信息等，
 *       真实项目中可以从后端接口动态获取，这里提供一份前端兜底配置。
 */

// 单个校园基础信息配置项的数据结构
export interface CampusConfigItem {
  id: number // 校园唯一编号（可与后端数据库 ID 对应）
  name: string // 校园全称，例如“上大学 Online 示范大学”
  shortName: string // 简称，例如“示范大学”
  city?: string // 所在城市（可选）
  province?: string // 所在省份（可选）
}

// 默认的校园配置列表（仅作为占位与示例，真实环境建议从后端加载）
export const DEFAULT_CAMPUS_LIST: CampusConfigItem[] = [
  {
    id: 1, // 校园 ID
    name: '上大学 Online 示范大学', // 校园全称
    shortName: '示范大学', // 简称
    city: '示范市', // 城市名称
    province: '示范省' // 省份名称
  }
] // 默认校园列表结束

// 默认校园（用于在未选择学校时做兜底展示）
export const DEFAULT_CAMPUS: CampusConfigItem = DEFAULT_CAMPUS_LIST[0] // 默认取列表中的第一所

// 校园地理围栏配置项的数据结构（用于判断是否在校园范围内）
export interface CampusGeoConfigItem {
  center: { latitude: number; longitude: number } // 校园中心点经纬度
  radiusMeters: number // 认为“在校内”的半径（单位：米）
}

// 校园地理围栏配置映射表（key 可以是 'default' 或具体校区编码）
export const CAMPUS_GEO_CONFIG: Record<string, CampusGeoConfigItem> = {
  default: {
    center: { latitude: 39.9042, longitude: 116.4074 }, // 示例：北京天安门附近坐标，真实项目中请替换为学校实际坐标
    radiusMeters: 2000 // 示例半径：2 公里内视为在校园范围内
  }
} // 这里只提供一份默认配置，后续可按实际校区扩展更多 key

// 综合导出的校园配置对象，供 web 端等工具函数直接使用
export const campusConfig = {
  defaultCampus: {
    ...DEFAULT_CAMPUS, // 继承基础校园信息（名称、城市等）
    center: CAMPUS_GEO_CONFIG.default.center, // 默认校园的中心点坐标
    radiusMeters: CAMPUS_GEO_CONFIG.default.radiusMeters // 默认校园的半径设置
  },
  campuses: CAMPUS_GEO_CONFIG // 所有校园的地理围栏配置映射
} // campusConfig 统一对外暴露，便于在不同模块中按需使用
