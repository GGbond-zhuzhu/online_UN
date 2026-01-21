/**
 * Pinia Store 统一导出
 */
// 说明：
// - 避免在顶层 re-export 时引入同名类型（如 UserInfo / UserRole）导致 tsc 冲突
// - 仅导出各 Store 的 useXxxStore 方法；类型请从 `@campus/common/types` 或具体模块中引入
export { useUserStore } from './user'
export { useEcardStore } from './ecard'
export { useSecondhandStore } from './secondhand'
export { useParttimeStore } from './parttime'
export { useScheduleTeamStore } from './scheduleTeam'
