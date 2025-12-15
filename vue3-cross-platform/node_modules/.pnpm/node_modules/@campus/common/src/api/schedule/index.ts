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
  return request.get<PersonalSchedule>(`/api/schedule/personal/${id}`)
}

/**
 * 创建个人行程
 * @param params 创建参数
 */
export function createPersonalSchedule(params: CreateScheduleParams): Promise<PersonalSchedule> {
  return request.post<PersonalSchedule>('/api/schedule/personal', params)
}

/**
 * 更新个人行程
 * @param id 行程ID
 * @param params 更新参数
 */
export function updatePersonalSchedule(id: number, params: Partial<CreateScheduleParams>): Promise<PersonalSchedule> {
  return request.put<PersonalSchedule>(`/api/schedule/personal/${id}`, params)
}

/**
 * 删除个人行程
 * @param id 行程ID
 */
export function deletePersonalSchedule(id: number): Promise<void> {
  return request.delete(`/api/schedule/personal/${id}`)
}

/**
 * 获取团队列表
 */
export function getTeams(): Promise<Team[]> {
  return request.get<Team[]>('/api/schedule/team/list')
}

/**
 * 创建团队
 * @param params 创建参数
 */
export function createTeam(params: CreateTeamParams): Promise<Team> {
  return request.post<Team>('/api/schedule/team/create', params)
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
  return request.get('/api/schedule/calendar', {
    params: { year, month }
  })
}

