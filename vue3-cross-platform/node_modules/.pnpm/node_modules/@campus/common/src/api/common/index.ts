/**
 * 通用功能API接口
 * 包括帮助中心、公告管理、安全保障、用户反馈等
 */
import request from '../../utils/request'

// ==================== 类型定义 ====================

/** 帮助文档分类 */
export interface HelpCategory {
  name: string
  description: string
  articleCount: number
}

/** 帮助文档 */
export interface HelpArticle {
  id: number
  title: string
  content: string
  category: string
  viewCount: number
  updateTime: string
}

/** 公告 */
export interface Announcement {
  id: number
  title: string
  content: string
  type: 'NEWS' | 'UPDATE' | 'NOTICE'
  isTop: boolean
  publishTime: string
  viewCount: number
}

/** 安全保障信息 */
export interface SecurityInfo {
  encryption: string
  locationPermission: string
  transactionEncryption: string
  dataProtection: string
}

/** 隐私政策 */
export interface PrivacyPolicy {
  title: string
  content: string
  version: string
  updateTime: string
}

/** 服务协议 */
export interface ServiceAgreement {
  title: string
  content: string
  version: string
  updateTime: string
}

/** 平台介绍 */
export interface AboutInfo {
  platformName: string
  slogan: string
  vision: string
  description: string
  features: string[]
}

/** 联系方式 */
export interface ContactInfo {
  serviceHotline: string
  techSupport: string
  businessCooperation: string
  universityAccess: string
  workingHours: string
}

/** 反馈类型 */
export type FeedbackType = 'BUG' | 'SUGGESTION' | 'COMPLAINT' | 'OTHER'

/** 反馈提交数据 */
export interface FeedbackSubmit {
  feedbackType: FeedbackType
  title: string
  content: string
  contact?: string
  screenshots?: string
}

/** 联系表单提交数据 */
export interface ContactFormSubmit {
  contactType: 'TECH' | 'BUSINESS' | 'UNIVERSITY' | 'OTHER'
  name: string
  email: string
  phone: string
  subject: string
  content: string
}

// ==================== 帮助中心API ====================

/**
 * 获取帮助文档分类列表
 */
export const getHelpCategories = (): Promise<{
  categories: HelpCategory[]
  total: number
}> => {
  return request.get('/api/common/help/categories')
}

/**
 * 获取帮助文档详情
 * @param id 文档ID
 */
export const getHelpArticle = (id: number): Promise<HelpArticle> => {
  return request.get(`/api/common/help/article/${id}`)
}

/**
 * 搜索帮助文档
 * @param keyword 搜索关键词
 * @param page 页码
 * @param size 每页大小
 */
export const searchHelp = (
  keyword: string,
  page: number = 1,
  size: number = 10
): Promise<{
  list: HelpArticle[]
  total: number
  keyword: string
}> => {
  return request.get('/api/common/help/search', {
    params: { keyword, page, size }
  })
}

// ==================== 公告管理API ====================

/**
 * 获取公告列表
 * @param type 公告类型（ALL/NEWS/UPDATE/NOTICE）
 * @param page 页码
 * @param size 每页大小
 */
export const getAnnouncements = (
  type: string = 'ALL',
  page: number = 1,
  size: number = 10
): Promise<{
  list: Announcement[]
  total: number
  page: number
  size: number
}> => {
  return request.get('/api/common/announcements', {
    params: { type, page, size }
  })
}

/**
 * 获取公告详情
 * @param id 公告ID
 */
export const getAnnouncementDetail = (id: number): Promise<Announcement> => {
  return request.get(`/api/common/announcements/${id}`)
}

// ==================== 安全保障API ====================

/**
 * 获取安全保障说明
 */
export const getSecurityInfo = (): Promise<SecurityInfo> => {
  return request.get('/api/common/security/info')
}

/**
 * 获取隐私政策
 */
export const getPrivacyPolicy = (): Promise<PrivacyPolicy> => {
  return request.get('/api/common/privacy-policy')
}

/**
 * 获取服务协议
 */
export const getServiceAgreement = (): Promise<ServiceAgreement> => {
  return request.get('/api/common/service-agreement')
}

// ==================== 平台介绍API ====================

/**
 * 获取平台介绍信息
 */
export const getAboutInfo = (): Promise<AboutInfo> => {
  return request.get('/api/common/about')
}

// ==================== 用户反馈API ====================

/**
 * 提交用户反馈
 * @param data 反馈数据
 */
export const submitFeedback = (data: FeedbackSubmit): Promise<{
  feedbackId: number
  status: string
  message: string
  submitTime: string
}> => {
  return request.post('/api/common/feedback', null, {
    params: data
  })
}

/**
 * 获取我的反馈列表
 * @param page 页码
 * @param size 每页大小
 */
export const getMyFeedbacks = (
  page: number = 1,
  size: number = 10
): Promise<{
  list: any[]
  total: number
  page: number
  size: number
}> => {
  return request.get('/api/common/feedback/my-feedbacks', {
    params: { page, size }
  })
}

// ==================== 联系我们API ====================

/**
 * 获取联系方式
 */
export const getContactInfo = (): Promise<ContactInfo> => {
  return request.get('/api/common/contact')
}

/**
 * 提交联系表单
 * @param data 联系表单数据
 */
export const submitContactForm = (data: ContactFormSubmit): Promise<{
  contactId: number
  message: string
  submitTime: string
}> => {
  return request.post('/api/common/contact/submit', null, {
    params: data
  })
}
