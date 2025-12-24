/**
 * 兼职相关类型统一出口
 * 说明：这里主要是对 api/parttime 中已有类型的二次导出，
 *       方便业务代码通过 `@campus/common` 顶层直接引入。
 */

// 从 api/parttime 模块中导入需要对外暴露的类型
export type {
  ParttimeJob, // 兼职岗位信息类型
  ParttimeQueryParams, // 兼职岗位查询参数类型
  ParttimeFavoritesResponse, // 兼职收藏列表响应类型
  ParttimeBrowseHistoryItem, // 兼职浏览记录条目类型
  ParttimeBrowseHistoryResponse // 兼职浏览记录分页响应类型
} from '../api/parttime' // 相对路径导入 parttime 接口模块中的类型
