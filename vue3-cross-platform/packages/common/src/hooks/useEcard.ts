// 校园卡相关通用钩子（双端可用）—— 封装校园卡信息和消费记录等常用操作
// 目标：页面直接调用钩子即可完成“查询校园卡信息 / 消费记录 / 挂失、解挂、充值”等操作

// 引入 Vue 的响应式工具，用于管理校园卡相关的状态
import { ref } from 'vue' // 从 vue 中导入 ref，用来创建响应式的状态变量

// 引入校园卡相关 API 和类型定义
import {
  getEcardInfo, // 获取当前用户校园卡信息的接口
  getConsumeRecords, // 获取校园卡消费记录列表的接口
  applyVisitorCard, // 申请游客校园卡的接口
  reportLoss, // 校园卡挂失接口
  cancelLoss, // 校园卡解挂接口
  recharge, // 校园卡充值接口
  checkLocation, // 校园卡相关的定位校验接口（判断是否在校园范围内）
  getTodayStatistics, // 获取今日消费统计数据的接口
  generateDynamicCode, // 生成动态学生码（二维码）的接口
  consume, // 校园卡消费（扣款）接口
  type EcardInfo, // 校园卡信息类型
  type ConsumeRecord, // 单条消费记录类型
  type ConsumeRecordsResponse, // 消费记录分页响应类型
  type VisitorCardApplyParams, // 游客卡申请参数类型
  type LocationCheckResponse, // 定位校验接口返回的数据类型
  type TodayStatisticsResponse, // 今日消费统计接口返回的数据类型
  type DynamicCodeResponse // 动态学生码接口返回的数据类型
} from '../api/ecard' // 从 api/ecard 模块中导入所有相关接口和类型

// 引入通用请求钩子，用于统一管理 loading / data / error 状态（可选使用）
import { useRequest } from './useRequest' // 从同目录下的 useRequest.ts 导入通用请求钩子

// 定义消费记录查询参数类型，便于在钩子内统一管理
export interface EcardRecordsQuery {
  startDate?: string // 查询开始日期（格式：yyyy-MM-dd，可选）
  endDate?: string // 查询结束日期（格式：yyyy-MM-dd，可选）
  consumeType?: string // 消费类型（如：餐饮、超市等，可选）
  page?: number // 当前页码（从 1 开始，可选）
  size?: number // 每页数量（可选）
}

// useEcard 钩子：统一管理与校园卡相关的前端状态和操作
export function useEcard() {
  // ========== 基础状态 ==========

  const loading = ref(false) // 全局加载状态（大部分校园卡操作共用）
  const errorMessage = ref('') // 最近一次操作的错误提示文案

  // 校园卡基础信息
  const ecardInfo = ref<EcardInfo | null>(null) // 当前用户校园卡信息，初始为 null，待接口返回后填充

  // 消费记录列表及分页信息
  const records = ref<ConsumeRecord[]>([]) // 当前已加载的消费记录列表
  const recordsPage = ref(1) // 当前消费记录的页码
  const recordsSize = ref(10) // 当前每页数量
  const recordsTotal = ref(0) // 消费记录总条数
  const recordsTotalAmount = ref(0) // 当前查询条件下的总消费金额

  // 今日消费统计信息
  const todayStatistics = ref<TodayStatisticsResponse | null>(null) // 今日消费统计信息

  // 动态学生码信息
  const dynamicCode = ref<DynamicCodeResponse | null>(null) // 最近一次生成的动态学生码信息

  // 定位校验结果（是否在校园范围内）
  const locationCheckResult = ref<LocationCheckResponse | null>(null) // 最近一次定位校验的结果

  // ========== 使用通用 useRequest 封装“查询型”接口 ==========

  // 1）封装获取校园卡信息的请求
  const {
    loading: loadingEcardInfo, // 获取校园卡信息的局部加载状态
    error: ecardInfoError, // 获取校园卡信息时的错误对象
    run: fetchEcardInfo // 发起获取校园卡信息请求的方法
  } = useRequest<EcardInfo, void>(
    () => getEcardInfo(), // 实际调用 getEcardInfo 接口的方法
    {
      // 请求成功时的回调：更新 ecardInfo 响应式变量
      onSuccess: (data) => {
        ecardInfo.value = data // 将接口返回的校园卡信息保存到本地状态中
      },
      // 请求失败时的回调：记录错误信息
      onError: (error) => {
        errorMessage.value = error.message || '获取校园卡信息失败' // 将错误信息写入统一错误提示变量
      }
    }
  )

  // 2）封装获取消费记录列表的请求
  const {
    loading: loadingRecords, // 获取消费记录的局部加载状态
    error: recordsError, // 获取消费记录时的错误对象
    run: fetchConsumeRecords // 发起获取消费记录请求的方法
  } = useRequest<ConsumeRecordsResponse, EcardRecordsQuery>(
    (query) => {
      // 从 query 中读取查询参数，如果不存在则使用默认值
      const params: EcardRecordsQuery = {
        startDate: query?.startDate, // 查询开始日期
        endDate: query?.endDate, // 查询结束日期
        consumeType: query?.consumeType, // 消费类型
        page: query?.page ?? recordsPage.value, // 优先使用传入页码，否则使用当前页码
        size: query?.size ?? recordsSize.value // 优先使用传入页大小，否则使用当前每页数量
      }
      // 调用具体的 API 接口获取消费记录列表
      return getConsumeRecords(params.startDate, params.endDate, params.consumeType, params.page, params.size) // 返回 Promise 以供 useRequest 管理
    },
    {
      onSuccess: (data) => {
        // 成功获取数据后，将返回值写入本地状态
        records.value = data.records // 保存当前页消费记录列表
        recordsPage.value = data.page // 保存当前页码
        recordsSize.value = data.size // 保存当前每页数量
        recordsTotal.value = data.total // 保存总记录数
        recordsTotalAmount.value = data.totalAmount // 保存查询条件下的总消费金额
      },
      onError: (error) => {
        errorMessage.value = error.message || '获取消费记录失败' // 将错误信息写入统一变量
      }
    }
  )

  // 3）封装获取今日消费统计的请求
  const {
    loading: loadingTodayStatistics, // 获取今日统计时的局部 loading
    error: todayStatisticsError, // 获取今日统计时的错误对象
    run: fetchTodayStatistics // 发起获取今日统计请求的方法
  } = useRequest<TodayStatisticsResponse, void>(
    () => getTodayStatistics(), // 实际调用今日统计接口的方法
    {
      onSuccess: (data) => {
        todayStatistics.value = data // 将统计结果写入本地状态，页面可直接展示
      },
      onError: (error) => {
        errorMessage.value = error.message || '获取今日消费统计失败' // 写入错误提示
      }
    }
  )

  // 4）封装生成动态学生码的请求
  const {
    loading: loadingDynamicCode, // 生成动态码时的局部 loading 状态
    error: dynamicCodeError, // 生成动态码过程中的错误对象
    run: fetchDynamicCode // 发起生成动态码请求的方法
  } = useRequest<DynamicCodeResponse, void>(
    () => generateDynamicCode(), // 实际调用生成动态码的接口
    {
      onSuccess: (data) => {
        dynamicCode.value = data // 将生成的动态码信息保存到本地状态
      },
      onError: (error) => {
        errorMessage.value = error.message || '生成动态学生码失败' // 写入错误提示
      }
    }
  )

  // 5）封装定位校验（判断是否在校园内）的请求
  const {
    loading: loadingLocationCheck, // 定位校验时的局部 loading
    error: locationCheckError, // 定位校验过程中的错误对象
    run: runLocationCheck // 发起定位校验请求的方法
  } = useRequest<LocationCheckResponse, { longitude: number; latitude: number }>(
    (params) => {
      // 如果调用方没有传入参数，则直接抛出错误（提醒必须提供经纬度）
      if (!params) {
        return Promise.reject(new Error('请先获取定位信息')) // 使用 Promise.reject 创建一个被拒绝的 Promise
      }
      // 调用 checkLocation 接口进行经纬度校验
      return checkLocation(params.longitude, params.latitude) // 返回 Promise 以供 useRequest 管理
    },
    {
      onSuccess: (data) => {
        locationCheckResult.value = data // 将定位校验结果保存到本地状态
      },
      onError: (error) => {
        errorMessage.value = error.message || '定位校验失败' // 写入错误提示信息
      }
    }
  )

  // ========== “操作型”接口：挂失 / 解挂 / 充值 / 游客卡申请 / 消费 ==========

  // 申请游客校园卡
  const applyVisitorEcard = async (params: VisitorCardApplyParams) => {
    loading.value = true // 标记为全局加载中
    errorMessage.value = '' // 清空历史错误信息
    try {
      const result = await applyVisitorCard(params) // 调用申请游客校园卡接口
      ecardInfo.value = result // 申请成功后，返回新的卡信息，覆盖当前 ecardInfo
      return result // 将结果返回给调用方，方便页面进行后续处理
    } catch (error: any) {
      console.error('申请游客校园卡失败:', error) // 控制台打印详细错误
      errorMessage.value = error?.message || '申请游客校园卡失败，请稍后重试' // 显示友好的错误提示
      throw error // 把错误抛给调用方，方便弹出对话框等
    } finally {
      loading.value = false // 最后无论成功失败，都要恢复加载状态
    }
  }

  // 校园卡挂失
  const doReportLoss = async () => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误
    try {
      await reportLoss() // 调用后端挂失接口
      // 挂失成功后，可以同步更新本地 ecardInfo 中的状态字段
      if (ecardInfo.value) {
        ecardInfo.value = {
          ...ecardInfo.value, // 保留原有字段
          status: 'LOST' // 将状态标记为挂失（与后端约定的状态值保持一致）
        }
      }
    } catch (error: any) {
      console.error('校园卡挂失失败:', error) // 打印错误
      errorMessage.value = error?.message || '校园卡挂失失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 校园卡解挂
  const doCancelLoss = async () => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误
    try {
      await cancelLoss() // 调用校园卡解挂接口
      // 解挂成功后，同步更新本地状态为正常
      if (ecardInfo.value) {
        ecardInfo.value = {
          ...ecardInfo.value, // 保留原有字段
          status: 'NORMAL' // 将状态改为正常（与后端状态约定保持一致）
        }
      }
    } catch (error: any) {
      console.error('校园卡解挂失败:', error) // 打印错误
      errorMessage.value = error?.message || '校园卡解挂失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 校园卡充值
  const doRecharge = async (amount: number) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误信息
    try {
      const result = await recharge(amount) // 调用充值接口
      ecardInfo.value = result // 用接口返回的最新卡信息覆盖本地状态
      return result // 返回最新的校园卡信息给调用方
    } catch (error: any) {
      console.error('校园卡充值失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '充值失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 校园卡消费（扣款）
  const doConsume = async (params: {
    amount: number // 消费金额
    merchantId: string // 商户ID
    merchantName: string // 商户名称
    consumeType: string // 消费类型
    description?: string // 备注说明
    longitude?: number // 消费时的经度
    latitude?: number // 消费时的纬度
  }) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误信息
    try {
      const record = await consume(params) // 调用消费接口，完成扣款并生成消费记录
      // 消费成功后，可以选择刷新校园卡信息和消费记录
      await fetchEcardInfo() // 刷新校园卡余额等信息
      // 也可以刷新当前查询条件下的消费记录（这里简单调用一次，不带条件）
      await fetchConsumeRecords({
        page: recordsPage.value, // 使用当前页码
        size: recordsSize.value // 使用当前每页数量
      })
      return record // 将本次消费记录返回给调用方
    } catch (error: any) {
      console.error('校园卡消费失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '消费失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误给调用方
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // ========== 对外统一返回 ==========

  return {
    // 全局基础状态
    loading, // 全局加载中状态（大部分校园卡操作会共用）
    errorMessage, // 最近一次校园卡相关操作的错误提示

    // 校园卡信息
    ecardInfo, // 当前用户校园卡信息
    fetchEcardInfo, // 获取校园卡信息的方法
    loadingEcardInfo, // 获取校园卡信息的局部 loading 状态
    ecardInfoError, // 获取校园卡信息的错误对象

    // 消费记录相关
    records, // 当前消费记录列表
    recordsPage, // 当前页码
    recordsSize, // 当前每页数量
    recordsTotal, // 总记录数
    recordsTotalAmount, // 当前查询条件下的总消费金额
    fetchConsumeRecords, // 获取消费记录列表的方法
    loadingRecords, // 获取消费记录时的局部 loading
    recordsError, // 获取消费记录时的错误对象

    // 今日消费统计
    todayStatistics, // 今日消费统计数据
    fetchTodayStatistics, // 获取今日消费统计数据的方法
    loadingTodayStatistics, // 获取今日统计时的局部 loading
    todayStatisticsError, // 获取今日统计时的错误对象

    // 动态学生码
    dynamicCode, // 最近一次生成的动态学生码信息
    fetchDynamicCode, // 生成动态学生码的方法
    loadingDynamicCode, // 生成动态码时的局部 loading
    dynamicCodeError, // 生成动态码过程中的错误对象

    // 定位校验（是否在校园内）
    locationCheckResult, // 最近一次定位校验的结果
    runLocationCheck, // 发起定位校验请求的方法
    loadingLocationCheck, // 定位校验时的局部 loading
    locationCheckError, // 定位校验过程中的错误对象

    // 操作型方法
    applyVisitorEcard, // 申请游客校园卡的方法
    doReportLoss, // 校园卡挂失方法
    doCancelLoss, // 校园卡解挂方法
    doRecharge, // 校园卡充值方法
    doConsume // 校园卡消费（扣款）方法
  }
} // useEcard 钩子定义结束
