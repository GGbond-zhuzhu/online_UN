/**
 * 类型定义总出口
 * 说明：将各业务模块的类型统一从一个入口导出，
 *       以便业务代码可以通过 `@campus/common` 顶层一次性引入。
 */

// 认证与用户相关类型
export * from './auth' // 导出认证相关类型（登录参数、用户信息等）

// 校园卡相关类型
export * from './ecard' // 导出校园卡相关类型（卡信息、消费记录等）

// 二手交易相关类型
export * from './secondhand' // 导出二手交易相关类型

// 兼职相关类型
export * from './parttime' // 导出兼职相关类型

// 行程管理相关类型
export * from './schedule' // 导出行程管理相关类型
