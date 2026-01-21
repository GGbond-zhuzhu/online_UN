/**
 * 行程管理相关API
 * 包括个人行程、团队行程、同步、日历等
 */
import request from '../../utils/request'

// 个人行程
export interface PersonalSchedule {
  id: number
  title: string
  description?: string
  startTime: string
  endTime: string
  location?: string
  type: string
  status: string
  reminderType?: string
  reminderTime?: string
  createTime: string
  updateTime: string
}

// 团队行程
export interface TeamSchedule {
  id: number
  title: string
  description?: string
  startTime: string
  endTime: string
  location?: string
  teamId: number
  teamName: string
  creatorId: number
  creatorName: string
  attendees: TeamScheduleAttendee[]
  createTime: string
  updateTime: string
}

// 团队成员
export interface TeamScheduleAttendee {
  userId: number
  userName: string
  status: string
}

// 团队信息
export interface Team {
  id: number
  name: string
  description?: string
  creatorId: number
  creatorName: string
  inviteCode?: string
  chatGroupId?: number
  maxAdmins?: number
  userRole?: string
  members: TeamMember[]
  createTime: string
}

// 团队成员信息
export interface TeamMember {
  userId: number
  userName: string
  role: string
  joinTime: string
}

// 创建行程参数
export interface CreateScheduleParams {
  title: string
  description?: string
  startTime: string
  endTime: string
  location?: string
  type: string
  reminderType?: string
  reminderTime?: string
  isRouteSeries?: boolean // 是否为行程系列
  routeSeriesData?: RouteSeriesData // 行程系列数据
}

// 行程系列数据
export interface RouteSeriesData {
  theme: string // 行程主题
  locations: Array<{
    name: string
    address: string
    longitude: number
    latitude: number
  }> // 地点列表
  routeResult?: {
    totalDistance: number // 总距离（公里）
    totalDuration: number // 总时长（分钟）
    steps: Array<{
      instruction: string
      distance?: number
    }> // 路线步骤
  } // 路线规划结果（可选）
}

// 创建团队参数
export interface CreateTeamParams {
  name: string
  description?: string
}

// 创建团队行程参数
export interface CreateTeamScheduleParams {
  teamId: number
  title: string
  description?: string
  startTime: string
  endTime: string
  location?: string
  attendeeIds?: number[]
  needConfirm?: boolean
}

// 查询参数
export interface ScheduleQueryParams {
  page?: number
  pageSize?: number
  startDate?: string
  endDate?: string
  type?: string
  status?: string
}

/**
 * 获取个人行程列表
 * @param params 查询参数
 */
export function getPersonalSchedules(params: ScheduleQueryParams = {}): Promise<{
  records: PersonalSchedule[]
  total: number
  current: number
  size: number
}> {
  return request.get('/api/schedule/personal/list', { params })
}

/**
 * 获取个人行程详情
 * @param id 行程ID
 */
export function getPersonalScheduleDetail(id: number): Promise<PersonalSchedule> {
  return request.get<PersonalSchedule>(`/api/schedule/personal/detail/${id}`)
}

/**
 * 获取个人行程详情（兼容旧函数名）
 * @deprecated 请使用 getPersonalScheduleDetail
 */
export function getPersonalScheduleById(id: number): Promise<PersonalSchedule> {
  return getPersonalScheduleDetail(id)
}

/**
 * 创建个人行程
 * @param params 创建参数
 */
export function createPersonalSchedule(params: CreateScheduleParams): Promise<PersonalSchedule> {
  return request.post<PersonalSchedule>('/api/schedule/personal/create', params)
}

/**
 * 更新个人行程
 * @param id 行程ID
 * @param params 更新参数
 */
export function updatePersonalSchedule(id: number, params: Partial<CreateScheduleParams>): Promise<PersonalSchedule>
export function updatePersonalSchedule(params: { id: number } & Partial<CreateScheduleParams>): Promise<PersonalSchedule>
export function updatePersonalSchedule(arg1: any, arg2?: any): Promise<PersonalSchedule> {
  // 兼容两种调用方式：
  // 1) updatePersonalSchedule(id, params)
  // 2) updatePersonalSchedule({ id, ...params })
  if (typeof arg1 === 'number') {
    return request.put<PersonalSchedule>(`/api/schedule/personal/update/${arg1}`, arg2 || {})
  }
  if (arg1 && typeof arg1 === 'object' && typeof arg1.id === 'number') {
    const { id, ...rest } = arg1
    return request.put<PersonalSchedule>(`/api/schedule/personal/update/${id}`, rest)
  }
  return Promise.reject(new Error('updatePersonalSchedule 参数错误'))
}

/**
 * 删除个人行程
 * @param id 行程ID
 */
export function deletePersonalSchedule(id: number): Promise<void> {
  return request.delete(`/api/schedule/personal/delete/${id}`)
}

/**
 * 更新行程状态
 * @param id 行程ID
 * @param status 状态
 */
export function updatePersonalScheduleStatus(id: number, status: string): Promise<PersonalSchedule> {
  return request.put<PersonalSchedule>(`/api/schedule/personal/update-status/${id}`, null, {
    params: { status }
  })
}

/**
 * 获取我的团队列表
 */
export function getMyTeams(page?: number, size?: number): Promise<{
  records: Team[]
  total: number
  current: number
  size: number
}> {
  return request.get('/api/schedule/team/my-teams', {
    params: { page, size }
  })
}

/**
 * 获取团队列表（兼容旧接口）
 */
export function getTeams(): Promise<Team[]> {
  return getMyTeams(1, 100).then(res => res.records)
}

/**
 * 创建团队
 * @param params 创建参数
 */
export function createTeam(params: CreateTeamParams): Promise<Team> {
  return request.post<Team>('/api/schedule/team/create', params)
}

/**
 * 设置团队管理员（仅创建者；最多4人）
 */
export function setTeamAdmins(teamId: number, adminUserIds: number[]): Promise<Team> {
  return request.put<Team>(`/api/schedule/team/${teamId}/admins`, { adminUserIds })
}

/**
 * 获取团队详情
 * @param id 团队ID
 */
export function getTeamDetail(id: number): Promise<Team> {
  return request.get<Team>(`/api/schedule/team/detail/${id}`)
}

/**
 * 重新生成团队邀请码
 * @param teamId 团队ID
 */
export function regenerateTeamInviteCode(teamId: number): Promise<{ inviteCode: string }> {
  return request.post<{ inviteCode: string }>(`/api/schedule/team/${teamId}/regenerate-invite-code`)
}

/**
 * 邀请成员加入团队
 * @param teamId 团队ID
 * @param userIds 用户ID列表
 */
export function inviteTeamMembers(teamId: number, userIds: number[]): Promise<void> {
  return request.post(`/api/schedule/team/${teamId}/invite`, { userIds })
}

/**
 * 获取团队行程列表
 * @param teamId 团队ID
 * @param params 查询参数
 */
export function getTeamSchedules(teamId: number, params: ScheduleQueryParams = {}): Promise<{
  records: TeamSchedule[]
  total: number
  current: number
  size: number
}> {
  return request.get(`/api/schedule/team/${teamId}/schedules`, { params })
}

/**
 * 创建团队行程
 * @param params 创建参数
 */
export function createTeamSchedule(params: CreateTeamScheduleParams): Promise<TeamSchedule> {
  return request.post<TeamSchedule>('/api/schedule/team/create-schedule', params)
}

/**
 * 批量同步团队行程到指定成员
 */
export function syncTeamScheduleToMembers(teamId: number, scheduleId: number, userIds: number[]): Promise<{
  teamId: number
  teamScheduleId: number
  syncedCount: number
  skippedCount: number
}> {
  return request.post(`/api/schedule/team/${teamId}/sync/team-schedule/${scheduleId}`, { userIds })
}

/**
 * 批量同步课程表到指定成员
 */
export function syncCoursesToMembers(teamId: number, sourceUserId: number, targetUserIds: number[], overwrite = false): Promise<{
  teamId: number
  sourceUserId: number
  targetCount: number
  sourceCourseCount: number
  overwrite: boolean
  deletedCount: number
  insertedCount: number
  skippedCount: number
}> {
  return request.post(`/api/schedule/team/${teamId}/sync/courses`, { sourceUserId, targetUserIds, overwrite })
}

/**
 * 同步团队行程到个人
 * @param scheduleId 团队行程ID
 */
export function syncTeamScheduleToPersonal(scheduleId: number): Promise<void> {
  return request.post(`/api/schedule/team/sync/${scheduleId}`)
}

/**
 * 获取日历视图数据
 * @param year 年份
 * @param month 月份
 */
export function getCalendarData(year: number, month: number): Promise<{
  personal: PersonalSchedule[]
  team: TeamSchedule[]
}> {
  return request.get(`/api/schedule/calendar/${year}/${month}`)
}

/**
 * 获取今日提醒
 */
export function getTodayReminders(): Promise<{
  reminders: any[]
  schedules: PersonalSchedule[]
}> {
  return request.get('/api/schedule/reminders/today')
}

/**
 * 获取团队邀请列表
 */
export function getTeamInvitations(): Promise<{
  invitations: any[]
}> {
  return request.get('/api/schedule/team/invitations')
}

/**
 * 处理团队邀请
 * @param inviteId 邀请ID
 * @param action 操作（accept/reject）
 */
export function processTeamInvitation(inviteId: number, action: 'accept' | 'reject'): Promise<void> {
  return request.post(`/api/schedule/team/invitation/${inviteId}/process`, null, {
    params: { action }
  })
}

/**
 * 通过邀请码加入团队
 * @param inviteCode 邀请码
 */
export function joinTeamByCode(inviteCode: string): Promise<Team> {
  return request.post<Team>('/api/schedule/team/join-by-code', null, {
    params: { inviteCode }
  })
}

