/**
 * 兼职相关 Pinia Store
 * 目标：统一管理兼职岗位列表、收藏、浏览记录等前端状态，
 *      方便 Web 与 App 使用同一套数据访问与更新方式。
 */

// 引入 Pinia 和 Vue 的基础工具
import { defineStore } from 'pinia' // 从 pinia 中导入 defineStore，用于创建 Store
import { ref } from 'vue' // 从 vue 中导入 ref，用于创建响应式状态

// 引入兼职相关 API 和类型定义
import {
  getParttimeList, // 获取兼职列表的接口
  getParttimeDetail, // 获取兼职详情的接口
  getMyParttime, // 获取我发布的兼职列表的接口
  getMyApplications, // 获取我的报名列表的接口
  publishParttime, // 发布兼职岗位的接口
  updateParttime, // 更新兼职岗位信息的接口
  deleteParttime, // 删除兼职岗位的接口
  applyParttime, // 提交兼职报名的接口
  cancelApply, // 取消兼职报名的接口
  getParttimeFavorites, // 获取兼职收藏列表的接口
  getParttimeBrowseHistory, // 获取兼职浏览记录的接口
  deleteParttimeBrowseHistory, // 删除单条兼职浏览记录的接口
  clearParttimeBrowseHistory, // 清空兼职浏览记录的接口
  favoriteParttime, // 收藏兼职岗位的接口
  cancelFavoriteParttime, // 取消收藏兼职岗位的接口
  recordParttimeBrowse, // 记录兼职浏览行为的接口
  type ParttimeJob, // 兼职岗位信息类型
  type ParttimeQueryParams, // 兼职查询参数类型
  type ParttimeFavoritesResponse, // 收藏列表响应类型
  type ParttimeBrowseHistoryResponse, // 浏览记录响应类型
  type ApplyParttimeParams, // 兼职报名参数类型
  type PublishParttimeParams // 发布兼职岗位时提交的参数类型
} from '../api/parttime' // 从 api/parttime 模块导入所有需要的函数和类型

// 定义并导出兼职 Store
export const useParttimeStore = defineStore('parttime', () => {
  // ========== 基础状态 ==========

  const loading = ref(false) // 是否有兼职相关请求正在进行中的标记
  const errorMessage = ref('') // 最近一次兼职相关操作的错误提示文本

  // ========== 兼职列表相关状态 ==========

  const jobList = ref<ParttimeJob[]>([]) // 当前兼职岗位列表
  const jobTotal = ref(0) // 兼职列表总条数
  const jobPage = ref(1) // 当前兼职列表页码
  const jobPageSize = ref(10) // 当前兼职列表每页数量

  // ========== 兼职详情相关状态 ==========

  const currentJob = ref<ParttimeJob | null>(null) // 当前正在查看的兼职详情

  // ========== 收藏列表相关状态 ==========

  const favoriteList = ref<ParttimeFavoritesResponse['list']>([]) // 兼职收藏列表
  const favoriteTotal = ref(0) // 收藏总条数
  const favoritePage = ref(1) // 收藏当前页
  const favoritePageSize = ref(20) // 收藏每页数量

  // ========== 浏览记录相关状态 ==========

  const browseHistory = ref<ParttimeBrowseHistoryResponse['records']>([]) // 浏览记录列表
  const browseTotal = ref(0) // 浏览记录总条数
  const browsePage = ref(1) // 浏览记录当前页
  const browsePageSize = ref(20) // 浏览记录每页数量

  // ========== 我的发布 / 我的报名相关状态 ==========

  const myParttimeList = ref<ParttimeJob[]>([]) // 我发布的兼职岗位列表
  const myApplicationList = ref<any[]>([]) // 我报名的兼职记录列表（具体类型可根据后端 VO 补充）

  // ========== 演示模式辅助：未登录时提供可演示数据 ==========
  const isAuthError = (error: any): boolean => {
    const msg = String(error?.message || '')
    return (
      msg.includes('未授权') ||
      msg.includes('未登录') ||
      msg.includes('请重新登录') ||
      msg.includes('token') ||
      msg.includes('401')
    )
  }

  const getDemoApplications = () => {
    const now = new Date()
    const fmt = (d: Date) => d.toLocaleString('zh-CN', { hour12: false })
    return [
      {
        id: 10001,
        jobId: 20001,
        jobTitle: '图书馆助理（演示）',
        companyName: '校内勤工助学中心',
        status: 'pending',
        applyTime: fmt(new Date(now.getTime() - 2 * 60 * 60 * 1000)),
        reviewTime: '',
        reviewComment: ''
      },
      {
        id: 10002,
        jobId: 20002,
        jobTitle: '食堂收银（演示）',
        companyName: '第一食堂',
        status: 'approved',
        applyTime: fmt(new Date(now.getTime() - 26 * 60 * 60 * 1000)),
        reviewTime: fmt(new Date(now.getTime() - 20 * 60 * 60 * 1000)),
        reviewComment: '请携带学生证到岗培训'
      },
      {
        id: 10003,
        jobId: 20003,
        jobTitle: '活动执行（演示）',
        companyName: '校园活动组委会',
        status: 'rejected',
        applyTime: fmt(new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000)),
        reviewTime: fmt(new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000)),
        reviewComment: '本次岗位已满员'
      }
    ]
  }

  // ========== 列表与详情加载方法 ==========

  // 加载兼职列表
  const loadJobList = async (params: ParttimeQueryParams = {}) => {
    loading.value = true // 标记开始加载
    errorMessage.value = '' // 清空历史错误
    try {
      // 调用获取兼职列表接口
      const res = await getParttimeList({
        ...params, // 合并调用方传入的查询条件
        page: params.page ?? jobPage.value, // 使用调用方传入的页码或当前页码
        pageSize: params.pageSize ?? jobPageSize.value // 使用调用方传入的每页数量或当前每页数量
      })
      // 将接口返回的数据写入本地状态
      jobList.value = res.records || res.list || [] // 岗位列表
      jobTotal.value = res.total || 0 // 总条数
      jobPage.value = res.page || res.current || params.page || 1 // 当前页码
      jobPageSize.value = res.size || params.pageSize || 10 // 每页数量
    } catch (error: any) {
      console.error('加载兼职列表失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '加载兼职列表失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误给调用方
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 加载兼职详情
  const loadJobDetail = async (id: number) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空历史错误
    try {
      const detail = await getParttimeDetail(id) // 调用获取详情接口
      currentJob.value = detail // 将详情写入本地状态
      return detail // 返回详情数据给调用方
    } catch (error: any) {
      console.error('加载兼职详情失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '加载兼职详情失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 加载我发布的兼职列表
  const loadMyParttime = async (status?: string, page = 1, size = 10) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误
    try {
      const res = await getMyParttime(status, page, size) // 调用“我发布的兼职”接口
      myParttimeList.value = res.list || [] // 保存列表
      return res // 返回完整响应给调用方
    } catch (error: any) {
      console.error('加载我发布的兼职列表失败:', error) // 打印错误
      errorMessage.value = error?.message || '加载我发布的兼职失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 加载我的兼职报名记录
  const loadMyApplications = async (status?: string, page = 1, size = 10) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误
    try {
      const res = await getMyApplications(status, page, size) // 调用“我的报名”接口
      myApplicationList.value = res.list || [] // 保存报名记录列表
      return res // 返回完整响应给调用方
    } catch (error: any) {
      console.error('加载我的兼职报名记录失败:', error) // 打印错误日志
      // 演示模式：未登录时用模拟数据填充，方便演示“取消申请”等交互
      if (isAuthError(error)) {
        myApplicationList.value = getDemoApplications()
        return { list: myApplicationList.value, total: myApplicationList.value.length, page, size }
      }
      errorMessage.value = error?.message || '加载我的报名记录失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // ========== 发布 / 更新 / 删除岗位相关方法 ==========

  // 发布新的兼职岗位
  const publishJob = async (params: PublishParttimeParams) => {
    loading.value = true // 标记为加载中，避免用户重复点击“发布”按钮
    errorMessage.value = '' // 清空上一次的错误提示
    try {
      const created = await publishParttime(params) // 调用后端发布接口，创建新的兼职岗位
      // 可选：将新发布的岗位追加到“我发布的兼职列表”中，便于前端立即看到效果
      myParttimeList.value = [created, ...myParttimeList.value] // 将新岗位插入到列表开头
      return created // 将创建成功的岗位对象返回给调用方
    } catch (error: any) {
      console.error('发布兼职岗位失败:', error) // 控制台打印错误日志，便于排查问题
      errorMessage.value = error?.message || '发布兼职失败，请稍后重试' // 将错误提示写入状态，供页面展示
      throw error // 将错误抛给调用方，方便页面按需弹出提示
    } finally {
      loading.value = false // 无论成功还是失败，最终都恢复加载状态
    }
  }

  // 更新已有的兼职岗位信息
  const updateJob = async (id: number, params: Partial<PublishParttimeParams>) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空历史错误
    try {
      const updated = await updateParttime(id, params) // 调用后端更新接口
      // 在“我发布的兼职列表”中同步更新对应岗位的信息
      myParttimeList.value = myParttimeList.value.map((job) =>
        job.id === id ? { ...job, ...updated } : job
      ) // 使用新的岗位数据替换原有记录
      return updated // 将更新后的岗位对象返回给调用方
    } catch (error: any) {
      console.error('更新兼职岗位失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '更新兼职失败，请稍后重试' // 写入错误提示
      throw error // 将错误抛出，交由调用方处理
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 删除指定的兼职岗位
  const deleteJob = async (id: number) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误提示
    try {
      await deleteParttime(id) // 调用后端删除接口，根据岗位 ID 删除记录
      // 在“我发布的兼职列表”中移除对应的岗位
      myParttimeList.value = myParttimeList.value.filter((job) => job.id !== id) // 过滤掉被删除的岗位
    } catch (error: any) {
      console.error('删除兼职岗位失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '删除兼职失败，请稍后重试' // 写入错误提示
      throw error // 将错误抛出，供调用方感知
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 提交兼职报名
  const applyForJob = async (params: ApplyParttimeParams) => {
    loading.value = true // 标记为加载中，避免用户重复点击“立即申请”
    errorMessage.value = '' // 清空上一次的错误提示
    try {
      await applyParttime(params) // 调用后端报名接口，将 jobId（以及可选的简历、留言）提交给服务器
      // 是否立即刷新“我的申请”列表，交给调用方控制（例如在“我的申请”页面手动调用 loadMyApplications）
    } catch (error: any) {
      console.error('提交兼职报名失败:', error) // 控制台打印错误日志，便于排查问题
      errorMessage.value = error?.message || '提交报名失败，请稍后重试' // 将错误提示写入状态，供页面展示
      throw error // 将错误抛给调用方，方便页面按需做额外提示（例如弹窗）
    } finally {
      loading.value = false // 无论成功还是失败，最终都恢复加载状态
    }
  }

  // 取消兼职报名
  const cancelApplication = async (applicationId: number) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误提示
    try {
      await cancelApply(applicationId) // 调用后端“取消报名”接口
      // 本地“我的申请”列表中同步删除对应的记录（依赖每条记录包含 id 字段）
      myApplicationList.value = myApplicationList.value.filter((item: any) => item.id !== applicationId) // 过滤掉被取消的申请
    } catch (error: any) {
      console.error('取消兼职报名失败:', error) // 打印错误日志
      // 演示模式：未登录也允许“本地取消”，让演示流程可走通
      if (isAuthError(error)) {
        myApplicationList.value = myApplicationList.value.filter((item: any) => item.id !== applicationId)
        return
      }
      errorMessage.value = error?.message || '取消报名失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误交由调用方处理
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // ========== 收藏相关方法 ==========

  // 加载兼职收藏列表
  const loadFavorites = async (page = 1, size = 20) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误
    try {
      const res: ParttimeFavoritesResponse = await getParttimeFavorites(page, size) // 调用收藏列表接口
      favoriteList.value = res.records || res.list || [] // 保存收藏列表
      favoriteTotal.value = res.total || 0 // 保存总条数
      favoritePage.value = res.page || page // 保存当前页
      favoritePageSize.value = res.size || size // 保存每页数量
      return res // 返回完整响应
    } catch (error: any) {
      console.error('加载兼职收藏列表失败:', error) // 打印错误
      errorMessage.value = error?.message || '加载兼职收藏列表失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 收藏兼职岗位
  const addFavorite = async (jobId: number) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误
    try {
      await favoriteParttime(jobId) // 调用收藏接口
      // 是否立即刷新收藏列表交由调用方决定，这里只负责调用接口
    } catch (error: any) {
      console.error('收藏兼职岗位失败:', error) // 打印错误
      errorMessage.value = error?.message || '收藏兼职失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 取消收藏兼职岗位
  const removeFavorite = async (jobId: number) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误提示
    try {
      await cancelFavoriteParttime(jobId) // 调用后端取消收藏接口
      // 本地同步更新收藏列表：过滤掉已取消收藏的岗位
      favoriteList.value = favoriteList.value.filter((item) => item.jobId !== jobId) // 删除对应收藏记录
      // 同步更新收藏总条数，确保不出现负数
      favoriteTotal.value = Math.max(0, favoriteTotal.value - 1) // 收藏总数减一
    } catch (error: any) {
      console.error('取消收藏兼职岗位失败:', error) // 在控制台打印错误信息
      errorMessage.value = error?.message || '取消收藏失败，请稍后重试' // 将错误提示写入状态
      throw error // 将错误抛出，交由调用方决定是否额外提示
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 清空兼职收藏列表（逐个调用取消收藏接口）
  const clearFavoritesAll = async () => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空历史错误
    try {
      // 使用 Promise.all 并行取消当前列表中所有收藏记录
      await Promise.all(
        favoriteList.value.map((item) =>
          // 对每一个已收藏岗位调用取消收藏方法
          removeFavorite(item.jobId)
        )
      )
      // 本地收藏列表清空
      favoriteList.value = [] // 将收藏列表重置为空数组
      favoriteTotal.value = 0 // 将收藏总数重置为 0
    } catch (error: any) {
      console.error('清空兼职收藏失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '清空收藏失败，请稍后重试' // 写入错误提示信息
      throw error // 将错误抛给调用方，以便页面做额外处理
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // ========== 浏览记录相关方法 ==========

  // 加载兼职浏览记录列表
  const loadBrowseHistory = async (page = 1, size = 20) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误
    try {
      const res: ParttimeBrowseHistoryResponse = await getParttimeBrowseHistory(page, size) // 调用浏览记录接口
      browseHistory.value = res.records || [] // 保存浏览记录列表
      browseTotal.value = res.total || 0 // 保存总条数
      browsePage.value = res.page || page // 保存当前页
      browsePageSize.value = res.size || size // 保存每页数量
      return res // 返回完整响应
    } catch (error: any) {
      console.error('加载兼职浏览记录失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '加载兼职浏览记录失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 删除单条兼职浏览记录
  const removeBrowseHistoryItem = async (id: number) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误
    try {
      await deleteParttimeBrowseHistory(id) // 调用删除接口
      // 本地状态中同步删除该条记录
      browseHistory.value = browseHistory.value.filter((item) => item.id !== id) // 过滤掉被删除的记录
      browseTotal.value = Math.max(0, browseTotal.value - 1) // 总数减一，不小于 0
    } catch (error: any) {
      console.error('删除兼职浏览记录失败:', error) // 打印错误
      errorMessage.value = error?.message || '删除浏览记录失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 清空兼职浏览记录
  const clearBrowseHistoryAll = async () => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误
    try {
      await clearParttimeBrowseHistory() // 调用清空接口
      browseHistory.value = [] // 清空本地浏览记录列表
      browseTotal.value = 0 // 总数归零
    } catch (error: any) {
      console.error('清空兼职浏览记录失败:', error) // 打印错误
      errorMessage.value = error?.message || '清空浏览记录失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 记录兼职浏览行为（通常在进入兼职详情页时调用）
  const addBrowseRecord = async (jobId: number) => {
    // 记录浏览行为一般不需要展示 loading 状态，因此这里不修改 loading
    try {
      await recordParttimeBrowse(jobId) // 调用记录浏览接口
    } catch (error: any) {
      console.error('记录兼职浏览行为失败:', error) // 打印错误日志
      // 此处一般不需要提示用户，因此不写入 errorMessage
    }
  }

  // ========== 对外统一返回 ==========

  return {
    // 基础状态
    loading, // 是否有请求进行中的标记
    errorMessage, // 最近一次操作的错误提示

    // 兼职列表相关
    jobList, // 兼职列表
    jobTotal, // 兼职总条数
    jobPage, // 当前兼职页码
    jobPageSize, // 每页兼职数量
    loadJobList, // 加载兼职列表的方法

    // 兼职详情相关
    currentJob, // 当前兼职详情
    loadJobDetail, // 加载兼职详情的方法

    // 我的发布 / 报名相关
    myParttimeList, // 我发布的兼职列表
    myApplicationList, // 我的兼职报名列表
    loadMyParttime, // 加载“我发布的兼职”的方法
    loadMyApplications, // 加载“我的报名记录”的方法
    publishJob, // 发布新的兼职岗位的方法
    updateJob, // 更新已有兼职岗位信息的方法
    deleteJob, // 删除兼职岗位的方法
    applyForJob, // 提交兼职报名的方法
    cancelApplication, // 取消兼职报名的方法

    // 收藏相关
    favoriteList, // 收藏列表
    favoriteTotal, // 收藏总条数
    favoritePage, // 收藏当前页
    favoritePageSize, // 收藏每页数量
    loadFavorites, // 加载收藏列表的方法
    addFavorite, // 收藏兼职的方法
    removeFavorite, // 取消收藏兼职的方法
    clearFavoritesAll, // 清空收藏列表的方法

    // 浏览记录相关
    browseHistory, // 浏览记录列表
    browseTotal, // 浏览记录总条数
    browsePage, // 浏览记录当前页
    browsePageSize, // 浏览记录每页数量
    loadBrowseHistory, // 加载浏览记录的方法
    removeBrowseHistoryItem, // 删除单条浏览记录的方法
    clearBrowseHistoryAll, // 清空浏览记录的方法
    addBrowseRecord // 记录兼职浏览行为的方法
  } // Store 返回对象结束
}) // useParttimeStore 定义结束
