/**
 * 校园卡相关 Pinia Store
 * 说明：这里直接复用 hooks 中已经实现好的 useEcard 逻辑，
 *       让页面既可以用「全局 Store」方式访问校园卡状态，
 *       也可以用「组合式函数」方式（hooks/useEcard）。
 */

// 引入 Pinia 的 defineStore，用于创建全局状态仓库
import { defineStore } from 'pinia' // 从 pinia 中导入 defineStore 方法

// 引入已经实现好的校园卡 hooks
import { useEcard } from '../hooks/useEcard' // 从 hooks 目录中导入 useEcard 组合式函数

// 定义并导出校园卡 Store，命名为 useEcardStore，供外部按需使用
export const useEcardStore = defineStore('ecard', () => {
  // 在 Store 的 setup 函数中调用 useEcard，以复用其内部封装的所有状态和方法
  const ecard = useEcard() // 调用组合式函数 useEcard，获取其中暴露的响应式状态和操作方法

  // 直接把 useEcard 返回的对象展开返回，相当于“把 useEcard 挂到 Pinia 上”
  return {
    ...ecard // 展开 useEcard 返回的所有字段（状态 + 方法），供组件通过 useEcardStore 使用
  } // 这里返回的内容会自动成为 Pinia Store 的 state/getters/actions
}) // useEcardStore 定义结束
