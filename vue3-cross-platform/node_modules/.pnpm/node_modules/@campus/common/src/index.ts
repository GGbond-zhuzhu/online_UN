/**
 * Common包统一导出入口
 * 提供API、工具类、类型定义、组件等统一导出
 */

// ==================== API接口 ====================
export * from './api'

// ==================== 工具类 ====================
export * from './utils/auth'
export * from './utils/format'
export * from './utils/location'
export * from './utils/request'
export * from './utils/storage'

// ==================== Pinia状态管理 ====================
export * from './pinia'

// ==================== Hooks ====================
export * from './hooks/useAuth'
export * from './hooks/useEcard'
export * from './hooks/useLocation'
export * from './hooks/useRequest'
export * from './hooks/useScheduleTeam'
export * from './hooks/useWeather'

// ==================== 类型定义 ====================
export * from './types'

// ==================== 配置 ====================
export * from './config'

// ==================== 组件 ====================
// 注意：Vue组件无法直接导出，需要在具体使用时按需导入
// export { default as ParttimeCard } from './components/business/ParttimeCard.vue'
// export { default as ScheduleItem } from './components/business/ScheduleItem.vue'
// export { default as SecondhandCard } from './components/business/SecondhandCard.vue'
// export { default as AppFooter } from './components/common/AppFooter.vue'
// export { default as AppLogos } from './components/common/AppLogos.vue'
// export { default as InputEntry } from './components/common/InputEntry.vue'
// export { default as NoPermission } from './components/permission/NoPermission.vue'
// export { default as RoleTip } from './components/permission/RoleTip.vue'

