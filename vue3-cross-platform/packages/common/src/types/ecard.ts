/**
 * 校园卡相关类型统一出口
 * 说明：这里主要是对 api/ecard 中已有类型的二次导出，
 *       方便业务代码通过 `@campus/common` 顶层直接引入。
 */

// 从 api/ecard 模块中导入所有需要对外暴露的类型
export type {
  EcardInfo, // 校园卡基础信息类型
  ConsumeRecord, // 单条消费记录类型
  ConsumeRecordsResponse, // 消费记录分页响应类型
  VisitorCardApplyParams, // 游客卡申请参数类型
  LocationCheckResponse, // 定位校验接口响应类型
  TodayStatisticsResponse, // 今日消费统计响应类型
  DynamicCodeResponse // 动态学生码接口响应类型
} from '../api/ecard' // 相对路径导入 ecard 接口模块中的类型
