/**
 * 行程管理相关类型统一出口
 * 说明：这里主要是对 api/schedule 中已有类型的二次导出，
 *       方便业务代码通过 `@campus/common` 顶层直接引入。
 */

// 从 api/schedule 模块中导入需要对外暴露的类型
export type {
  PersonalSchedule, // 个人行程类型
  TeamSchedule, // 团队行程类型
  TeamScheduleAttendee, // 团队行程参与人类型
  Team, // 团队信息类型
  TeamMember, // 团队成员信息类型
  CreateScheduleParams, // 创建个人行程参数类型
  CreateTeamParams, // 创建团队参数类型
  CreateTeamScheduleParams, // 创建团队行程参数类型
  ScheduleQueryParams // 行程查询参数类型
} from '../api/schedule' // 相对路径导入 schedule 接口模块中的类型
