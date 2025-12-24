/**
 * 二手交易相关 Pinia Store
 * 目标：统一管理二手商品列表、详情、收藏、浏览记录等前端状态，
 *      方便 Web 与 App 端通过同一套接口访问和更新数据。
 */

// 引入 Pinia 和 Vue 的基础工具
import { defineStore } from 'pinia' // 从 pinia 中导入 defineStore，用于创建 Store
import { ref } from 'vue' // 从 vue 中导入 ref，用于创建响应式状态

// 引入二手交易相关 API 和类型定义
import {
  getGoodsList, // 获取商品列表的接口
  getGoodsDetail, // 获取商品详情的接口
  getMyGoods, // 获取当前用户发布的商品列表的接口
  getSecondhandFavorites, // 获取收藏列表的接口
  getSecondhandBrowseHistory, // 获取浏览记录列表的接口
  deleteSecondhandBrowseHistory, // 删除单条浏览记录的接口
  clearSecondhandBrowseHistory, // 清空浏览记录的接口
  collectGoods, // 收藏商品的接口
  uncollectGoods, // 取消收藏商品的接口
  recordSecondhandBrowse, // 记录浏览行为的接口
  type SecondhandGoods, // 商品信息类型
  type SecondhandQueryParams, // 商品查询参数类型
  type SecondhandFavoritesResponse, // 收藏列表响应类型
  type SecondhandBrowseHistoryResponse // 浏览记录响应类型
} from '../api/secondhand' // 从 api/secondhand 模块导入所有需要的函数和类型

// 定义并导出二手交易 Store
export const useSecondhandStore = defineStore('secondhand', () => {
  // ========== 基础状态 ==========

  const loading = ref(false) // 是否有二手相关请求正在进行中的标记
  const errorMessage = ref('') // 最近一次二手相关操作的错误提示文本

  // ========== 商品列表相关状态 ==========

  const goodsList = ref<SecondhandGoods[]>([]) // 当前商品列表数据
  const goodsTotal = ref(0) // 商品列表总条数
  const goodsPage = ref(1) // 当前商品列表页码
  const goodsPageSize = ref(10) // 当前商品列表每页数量

  // ========== 商品详情相关状态 ==========

  const currentGoods = ref<SecondhandGoods | null>(null) // 当前正在查看的商品详情

  // ========== 收藏列表相关状态 ==========

  const favoriteList = ref<SecondhandGoods[]>([]) // 当前用户的二手收藏列表
  const favoriteTotal = ref(0) // 收藏总条数
  const favoritePage = ref(1) // 收藏列表当前页码
  const favoritePageSize = ref(20) // 收藏列表每页数量

  // ========== 浏览记录相关状态 ==========

  const browseHistory = ref<SecondhandBrowseHistoryResponse['records']>([]) // 浏览记录列表
  const browseTotal = ref(0) // 浏览记录总条数
  const browsePage = ref(1) // 浏览记录当前页码
  const browsePageSize = ref(20) // 浏览记录每页数量

  // ========== 我的发布相关状态 ==========

  const myGoodsList = ref<SecondhandGoods[]>([]) // 当前用户发布的二手商品列表

  // ========== 列表与详情加载方法 ==========

  // 加载商品列表
  const loadGoodsList = async (params: SecondhandQueryParams = {}) => {
    loading.value = true // 标记开始加载
    errorMessage.value = '' // 清空历史错误
    try {
      // 调用获取商品列表的接口
      const res = await getGoodsList({
        ...params, // 合并调用方传入的查询条件
        page: params.page ?? goodsPage.value, // 使用调用方传入的页码或当前页码
        pageSize: params.pageSize ?? goodsPageSize.value // 使用调用方传入的每页数量或当前每页数量
      })
      // 将接口返回的数据写入本地状态
      goodsList.value = res.records || res.list || [] // 优先使用 records，其次使用 list 字段
      goodsTotal.value = res.total || 0 // 总条数
      goodsPage.value = res.page || res.current || params.page || 1 // 当前页码
      goodsPageSize.value = res.size || params.pageSize || 10 // 当前页大小
    } catch (error: any) {
      console.error('加载二手商品列表失败:', error) // 控制台打印详细错误
      errorMessage.value = error?.message || '加载二手商品列表失败，请稍后重试' // 写入错误提示
      throw error // 将错误抛给调用方，方便页面做额外处理
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 加载商品详情
  const loadGoodsDetail = async (id: number) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空历史错误
    try {
      const detail = await getGoodsDetail(id) // 调用获取详情接口
      currentGoods.value = detail // 将商品详情写入本地状态
      return detail // 返回详情数据给调用方
    } catch (error: any) {
      console.error('加载商品详情失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '加载商品详情失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 加载当前用户的发布列表
  const loadMyGoods = async () => {
    loading.value = true // 标记加载中
    errorMessage.value = '' // 清空错误
    try {
      const list = await getMyGoods() // 调用获取“我的发布”接口
      myGoodsList.value = list || [] // 将结果写入本地状态
      return list // 返回数据给调用方
    } catch (error: any) {
      console.error('加载我的二手发布失败:', error) // 打印错误
      errorMessage.value = error?.message || '加载我的发布失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // ========== 收藏相关方法 ==========

  // 加载收藏列表
  const loadFavorites = async (page = 1, size = 20) => {
    loading.value = true // 标记加载中
    errorMessage.value = '' // 清空错误
    try {
      const res: SecondhandFavoritesResponse = await getSecondhandFavorites(page, size) // 调用收藏列表接口
      favoriteList.value = res.records || res.list || [] // 保存收藏列表
      favoriteTotal.value = res.total // 保存总条数
      favoritePage.value = res.page // 保存当前页
      favoritePageSize.value = res.size // 保存每页数量
      return res // 返回完整响应给调用方
    } catch (error: any) {
      console.error('加载二手收藏列表失败:', error) // 打印错误
      errorMessage.value = error?.message || '加载收藏列表失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 收藏商品
  const addFavorite = async (goodsId: number) => {
    loading.value = true // 标记加载中
    errorMessage.value = '' // 清空错误
    try {
      await collectGoods(goodsId) // 调用收藏接口
      // 收藏成功后，可根据需要选择是否立即刷新收藏列表，这里交给调用方自行决定
    } catch (error: any) {
      console.error('收藏二手商品失败:', error) // 打印错误
      errorMessage.value = error?.message || '收藏失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 取消收藏商品
  const removeFavorite = async (goodsId: number) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空之前的错误信息
    try {
      await uncollectGoods(goodsId) // 调用后端取消收藏接口
      // 本地同步更新收藏列表：过滤掉已取消收藏的商品
      favoriteList.value = favoriteList.value.filter((item) => item.id !== goodsId) // 删除对应的收藏记录
      // 同步更新收藏总数，确保不出现负数
      favoriteTotal.value = Math.max(0, favoriteTotal.value - 1) // 总数减一（不小于 0）
    } catch (error: any) {
      console.error('取消收藏二手商品失败:', error) // 打印错误日志，便于问题排查
      errorMessage.value = error?.message || '取消收藏失败，请稍后重试' // 将错误提示写入状态，供页面展示
      throw error // 将错误抛给调用方，方便页面进行额外处理（如弹窗）
    } finally {
      loading.value = false // 无论成功还是失败，最后都恢复为非加载状态
    }
  }

  // ========== 浏览记录相关方法 ==========

  // 加载浏览记录列表
  const loadBrowseHistory = async (page = 1, size = 20) => {
    loading.value = true // 标记加载中
    errorMessage.value = '' // 清空错误
    try {
      const res: SecondhandBrowseHistoryResponse = await getSecondhandBrowseHistory(page, size) // 调用接口
      browseHistory.value = res.records || [] // 保存浏览记录列表
      browseTotal.value = res.total // 保存总条数
      browsePage.value = res.page // 保存当前页
      browsePageSize.value = res.size // 保存每页数量
      return res // 返回完整响应
    } catch (error: any) {
      console.error('加载二手浏览记录失败:', error) // 打印错误
      errorMessage.value = error?.message || '加载浏览记录失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 删除单条浏览记录
  const removeBrowseHistoryItem = async (historyId: number) => {
    loading.value = true // 标记加载中
    errorMessage.value = '' // 清空错误
    try {
      await deleteSecondhandBrowseHistory(historyId) // 调用删除接口
      // 本地状态中同步移除对应记录
      browseHistory.value = browseHistory.value.filter((item) => item.id !== historyId) // 过滤掉被删除的记录
      browseTotal.value = Math.max(0, browseTotal.value - 1) // 总条数减一（不小于 0）
    } catch (error: any) {
      console.error('删除二手浏览记录失败:', error) // 打印错误
      errorMessage.value = error?.message || '删除浏览记录失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 清空浏览记录
  const clearBrowseHistoryAll = async () => {
    loading.value = true // 标记加载中
    errorMessage.value = '' // 清空错误
    try {
      await clearSecondhandBrowseHistory() // 调用清空接口
      browseHistory.value = [] // 本地列表清空
      browseTotal.value = 0 // 总条数归零
    } catch (error: any) {
      console.error('清空二手浏览记录失败:', error) // 打印错误
      errorMessage.value = error?.message || '清空浏览记录失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 清空收藏列表（逐个调用取消收藏接口）
  const clearFavoritesAll = async () => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误信息
    try {
      // 使用 Promise.all 并行取消当前列表中所有收藏记录
      await Promise.all(
        favoriteList.value.map((item) =>
          // 对每一个已收藏商品调用取消收藏接口
          removeFavorite(item.id)
        )
      )
      // 本地收藏列表清空
      favoriteList.value = [] // 将收藏列表重置为空数组
      favoriteTotal.value = 0 // 将收藏总数重置为 0
    } catch (error: any) {
      console.error('清空二手收藏失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '清空收藏失败，请稍后重试' // 写入错误提示信息
      throw error // 将错误抛给调用方，以便页面做额外处理
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 记录浏览行为（通常在进入详情页时调用）
  const addBrowseRecord = async (goodsId: number) => {
    // 记录浏览行为一般不需要展示 loading 状态，这里不修改 loading，只做错误兜底
    try {
      await recordSecondhandBrowse(goodsId) // 调用记录浏览接口
    } catch (error: any) {
      console.error('记录二手商品浏览行为失败:', error) // 打印错误日志
      // 此处一般不需要给用户提示，因此不写入 errorMessage
    }
  }

  // ========== 对外统一返回 ==========

  return {
    // 基础状态
    loading, // 是否有请求进行中的标记
    errorMessage, // 最近一次操作的错误提示

    // 商品列表相关
    goodsList, // 当前商品列表
    goodsTotal, // 商品总条数
    goodsPage, // 当前商品页码
    goodsPageSize, // 当前商品每页数量
    loadGoodsList, // 加载商品列表的方法

    // 商品详情相关
    currentGoods, // 当前商品详情
    loadGoodsDetail, // 加载商品详情的方法

    // 我的发布相关
    myGoodsList, // 当前用户发布的商品列表
    loadMyGoods, // 加载“我的发布”的方法

    // 收藏相关
    favoriteList, // 收藏列表
    favoriteTotal, // 收藏总数
    favoritePage, // 收藏当前页
    favoritePageSize, // 收藏每页数量
    loadFavorites, // 加载收藏列表的方法
    addFavorite, // 收藏商品的方法
    removeFavorite, // 取消收藏商品的方法

    // 浏览记录相关
    browseHistory, // 浏览记录列表
    browseTotal, // 浏览记录总条数
    browsePage, // 浏览记录当前页
    browsePageSize, // 浏览记录每页数量
    loadBrowseHistory, // 加载浏览记录列表的方法
    removeBrowseHistoryItem, // 删除单条浏览记录的方法
    clearBrowseHistoryAll, // 清空浏览记录的方法
    clearFavoritesAll, // 清空收藏列表的方法
    addBrowseRecord // 记录浏览行为的方法
  } // Store 返回对象结束
}) // useSecondhandStore 定义结束
