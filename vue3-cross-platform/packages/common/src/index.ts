/**
 * Common包统一导出入口
 * 提供API、工具类、类型定义、组件等统一导出
 */

// ==================== API接口 ====================
export * from './api'

// ==================== 工具类 ====================
export {
  getToken, // 从认证工具导出获取 token 的方法
  setToken, // 从认证工具导出设置 token 的方法
  removeToken, // 从认证工具导出移除 token 的方法
  clearAuth, // 从认证工具导出清理所有认证信息的方法
  setUserInfo // 导出设置用户信息的方法，供 Web 注册/登录页面使用
} from './utils/auth' // 仅导出认证与用户信息存储相关函数，避免与 API 层的 getUserInfo 命名冲突
export * from './utils/format' // 导出格式化与“猜你喜欢”推荐相关工具函数
export * from './utils/location' // 导出地理位置相关纯工具函数
export * from './utils/request' // 导出基于 Axios 封装的请求工具
export * from './utils/storage' // 导出跨平台本地存储工具

// ==================== Pinia状态管理 ====================
// 为了避免浏览器运行时出现 “does not provide an export named XXX” 的链路错误，
// 这里通过 ./pinia 目录的聚合导出统一暴露所有 Store（user / ecard / secondhand / parttime / scheduleTeam 等）。
// ./pinia/index.ts 内部已经分别从子模块导出了 useUserStore、useSecondhandStore 等，export * 不会重复导出类型。
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

