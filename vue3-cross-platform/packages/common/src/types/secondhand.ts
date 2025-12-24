/**
 * 二手交易相关类型统一出口
 * 说明：这里主要是对 api/secondhand 中已有类型的二次导出，
 *       方便业务代码通过 `@campus/common` 顶层直接引入。
 */

// 从 api/secondhand 模块中导入需要对外暴露的类型
export type {
  SecondhandGoods, // 二手商品信息类型
  SecondhandQueryParams, // 二手商品查询参数类型
  SecondhandFavoritesResponse, // 收藏列表响应类型
  SecondhandBrowseHistoryItem, // 浏览记录条目类型
  SecondhandBrowseHistoryResponse // 浏览记录分页响应类型
} from '../api/secondhand' // 相对路径导入 secondhand 接口模块中的类型
