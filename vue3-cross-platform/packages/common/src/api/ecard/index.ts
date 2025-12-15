/**
 * 校园卡相关API
 * 包括余额查询、消费记录、游客卡申请等
 */
import request from '../../utils/request'

// 校园卡信息（匹配后端EcardVO）
export interface EcardInfo {
  cardNo: string
  userId: number | null
  userName: string
  balance: number
  status: string
  isVisitorCard: boolean
  visitorExpireTime?: string
  todayConsumeCount: number
  todayConsumeAmount: number
  lastConsumeTime?: string
  createTime?: string
}

// 消费记录（匹配后端ConsumeRecordVO）
export interface ConsumeRecord {
  id: number
  cardNo: string
  amount: number
  balanceAfter: number
  merchantId: string
  merchantName: string
  consumeType: string
  description: string
  longitude?: number
  latitude?: number
  isInCampus: boolean
  consumeTime: string
  payMethod?: string
}

// 消费记录分页响应
export interface ConsumeRecordsResponse {
  records: ConsumeRecord[]
  page: number
  size: number
  total: number
  totalAmount: number
}

// 游客卡申请参数（匹配后端VisitorCardApplyDTO）
export interface VisitorCardApplyParams {
  name: string
  idCard: string
  phone: string
  purpose?: string
  expectedLeaveDate?: string
  longitude?: number
  latitude?: number
}

// 定位检查响应
export interface LocationCheckResponse {
  longitude: number
  latitude: number
  isInCampus: boolean
  distanceToCampus: number
  campusName: string
  message: string
}

// 今日统计响应
export interface TodayStatisticsResponse {
  consumeCount: number
  consumeAmount: number
  averageConsume: number
  mostFrequentType: string
  mostFrequentCount: number
}

// 动态码响应
export interface DynamicCodeResponse {
  code: string
  qrCodeUrl: string
  expireTime: number
  message: string
}

/**
 * 获取当前用户的校园卡信息
 */
export function getEcardInfo(): Promise<EcardInfo> {
  return request.get<EcardInfo>('/api/ecard/info')
}

/**
 * 获取消费记录
 * @param startDate 开始时间（yyyy-MM-dd）
 * @param endDate 结束时间（yyyy-MM-dd）
 * @param consumeType 消费类型
 * @param page 页码
 * @param size 每页数量
 */
export function getConsumeRecords(
  startDate?: string,
  endDate?: string,
  consumeType?: string,
  page: number = 1,
  size: number = 10
): Promise<ConsumeRecordsResponse> {
  return request.get<ConsumeRecordsResponse>('/api/ecard/consume-records', {
    params: { startDate, endDate, consumeType, page, size }
  })
}

/**
 * 申请游客卡
 * @param params 申请参数
 */
export function applyVisitorCard(params: VisitorCardApplyParams): Promise<EcardInfo> {
  return request.post<EcardInfo>('/api/ecard/visitor-card/apply', params)
}

/**
 * 挂失校园卡
 */
export function reportLoss(): Promise<string> {
  return request.post<string>('/api/ecard/report-loss')
}

/**
 * 解挂校园卡
 */
export function cancelLoss(): Promise<string> {
  return request.post<string>('/api/ecard/cancel-loss')
}

/**
 * 充值校园卡
 * @param amount 充值金额
 */
export function recharge(amount: number): Promise<EcardInfo> {
  return request.post<EcardInfo>('/api/ecard/recharge', null, {
    params: { amount: amount.toString() }
  })
}

/**
 * 校验定位是否在校内
 * @param longitude 经度
 * @param latitude 纬度
 */
export function checkLocation(longitude: number, latitude: number): Promise<LocationCheckResponse> {
  return request.get<LocationCheckResponse>('/api/ecard/check-location', {
    params: { longitude, latitude }
  })
}

/**
 * 获取今日消费统计
 */
export function getTodayStatistics(): Promise<TodayStatisticsResponse> {
  return request.get<TodayStatisticsResponse>('/api/ecard/today-statistics')
}

/**
 * 生成动态学生码
 */
export function generateDynamicCode(): Promise<DynamicCodeResponse> {
  return request.get<DynamicCodeResponse>('/api/ecard/dynamic-code')
}

/**
 * 校园卡消费
 * @param params 消费参数
 */
export function consume(params: {
  amount: number
  merchantId: string
  merchantName: string
  consumeType: string
  description?: string
  longitude?: number
  latitude?: number
}): Promise<ConsumeRecord> {
  return request.post<ConsumeRecord>('/api/ecard/consume', params)
}

