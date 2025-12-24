/**
 * 行程团队相关 Pinia Store
 * 说明：这里直接复用 hooks 中已经实现好的 useScheduleTeam 逻辑，
 *       让页面可以通过全局 Store 访问“团队列表 + 团队行程”等状态。
 */

// 引入 Pinia 的 defineStore，用于创建全局状态仓库
import { defineStore } from 'pinia' // 从 pinia 中导入 defineStore 方法

// 引入已经实现好的行程团队 hooks
import { useScheduleTeam } from '../hooks/useScheduleTeam' // 从 hooks 目录中导入 useScheduleTeam 组合式函数

// 定义并导出行程团队 Store，命名为 useScheduleTeamStore，供外部页面按需使用
export const useScheduleTeamStore = defineStore('scheduleTeam', () => {
  // 在 Store 的 setup 函数中调用 useScheduleTeam，以复用其内部封装的所有状态和方法
  const scheduleTeam = useScheduleTeam() // 调用组合式函数 useScheduleTeam，获取其中暴露的响应式状态和操作方法

  // 直接把 useScheduleTeam 返回的对象展开返回，让这些字段成为 Pinia Store 的一部分
  return {
    ...scheduleTeam // 展开 useScheduleTeam 返回的所有字段（状态 + 方法），供组件通过 useScheduleTeamStore 使用
  } // 这里返回的内容会自动成为 Pinia Store 的 state/getters/actions
}) // useScheduleTeamStore 定义结束
