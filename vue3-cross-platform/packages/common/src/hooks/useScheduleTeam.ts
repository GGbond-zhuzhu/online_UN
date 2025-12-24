// 行程团队相关通用钩子（双端可用）—— 封装团队列表和团队行程等操作
// 目标：页面只要调用钩子，就能方便地获取“我的团队”和“团队行程”数据，并执行创建 / 同步等操作

// 引入 Vue 的响应式工具和计算属性
import { ref, computed } from 'vue' // 从 vue 中导入 ref（响应式变量）和 computed（计算属性）

// 引入行程管理相关 API 和类型定义
import {
  getTeams, // 获取当前用户所属团队列表的接口
  createTeam, // 创建新团队的接口
  getTeamDetail, // 获取单个团队详情的接口
  regenerateTeamInviteCode, // 重新生成团队邀请码的接口
  inviteTeamMembers, // 邀请成员加入团队的接口
  getTeamSchedules, // 获取团队行程列表的接口
  createTeamSchedule, // 创建团队行程的接口
  syncTeamScheduleToPersonal, // 将团队行程同步到个人行程的接口
  type Team, // 团队信息类型
  type TeamSchedule, // 团队行程类型
  type CreateTeamParams, // 创建团队参数类型
  type CreateTeamScheduleParams, // 创建团队行程参数类型
  type ScheduleQueryParams // 行程查询参数类型
} from '../api/schedule' // 从 api/schedule 模块中导入所有相关接口和类型

// 引入通用请求钩子，用于统一管理 loading / data / error 状态
import { useRequest } from './useRequest' // 从同目录下导入 useRequest 通用请求钩子

// 行程团队钩子：统一管理“团队列表 + 团队行程”的前端状态和操作
export function useScheduleTeam() {
  // ========== 基础状态 ==========

  const loading = ref(false) // 全局加载状态（部分操作共用）
  const errorMessage = ref('') // 最近一次操作的错误提示文案

  // 团队列表及当前选中团队
  const teams = ref<Team[]>([]) // 当前用户所属的所有团队列表
  const currentTeamId = ref<number | null>(null) // 当前选中的团队ID（用于展示团队行程）

  // 当前选中的团队详情（基于 teams 和 currentTeamId 动态计算）
  const currentTeam = computed<Team | null>(() => {
    // 如果尚未选择团队或团队列表为空，则返回 null
    if (!currentTeamId.value || teams.value.length === 0) {
      return null // 表示当前还没有有效的团队信息
    }
    // 在团队列表中查找与当前选中 ID 匹配的团队
    return teams.value.find((team) => team.id === currentTeamId.value) || null // 找不到则返回 null
  })

  // 团队行程列表及分页信息
  const teamSchedules = ref<TeamSchedule[]>([]) // 当前选中团队的行程列表
  const schedulePage = ref(1) // 当前行程列表页码
  const scheduleSize = ref(10) // 当前每页行程数量
  const scheduleTotal = ref(0) // 行程总数

  // ========== 使用 useRequest 封装“查询型”接口 ==========

  // 1）封装获取团队列表的请求
  const {
    loading: loadingTeams, // 获取团队列表时的局部 loading 状态
    error: teamsError, // 获取团队列表时的错误对象
    run: fetchTeams // 发起获取团队列表请求的方法
  } = useRequest<Team[], void>(
    () => getTeams(), // 实际调用获取团队列表的接口
    {
      onSuccess: (data) => {
        teams.value = data // 将团队列表写入本地状态
        // 如果当前尚未选中团队，而返回列表不为空，则默认选中第一个团队
        if (!currentTeamId.value && data.length > 0) {
          currentTeamId.value = data[0].id // 将第一个团队的 ID 设为当前选中团队
        }
      },
      onError: (error) => {
        errorMessage.value = error.message || '获取团队列表失败' // 写入错误提示
      }
    }
  )

  // 2）封装获取团队行程列表的请求
  const {
    loading: loadingTeamSchedules, // 获取团队行程时的局部 loading 状态
    error: teamSchedulesError, // 获取团队行程时的错误对象
    run: fetchTeamSchedules // 发起获取团队行程列表请求的方法
  } = useRequest<
    {
      records: TeamSchedule[] // 行程记录列表
      total: number // 总条数
      current: number // 当前页码
      size: number // 每页数量
    },
    { teamId?: number; query?: ScheduleQueryParams } // 查询参数类型：包含 teamId 和额外查询条件
  >(
    (params) => {
      // 若未显式传入 teamId，则优先使用当前选中团队 ID
      const teamId = params?.teamId ?? currentTeamId.value // 从参数或当前选中团队中确定最终 teamId
      if (!teamId) {
        // 如果仍然没有 teamId，则返回一个被拒绝的 Promise，提示调用方必须先选择团队
        return Promise.reject(new Error('请先选择一个团队')) // 提示缺少团队 ID
      }
      // 组装查询参数（页码与页大小默认使用当前状态）
      const query: ScheduleQueryParams = {
        page: params?.query?.page ?? schedulePage.value, // 当前或传入的页码
        pageSize: params?.query?.pageSize ?? scheduleSize.value, // 当前或传入的每页数量
        startDate: params?.query?.startDate, // 可选的开始日期
        endDate: params?.query?.endDate, // 可选的结束日期
        type: params?.query?.type, // 可选的行程类型
        status: params?.query?.status // 可选的行程状态
      }
      // 调用获取团队行程列表的接口
      return getTeamSchedules(teamId, query) // 返回 Promise 以供 useRequest 管理
    },
    {
      onSuccess: (data) => {
        // 将返回的行程数据写入本地状态
        teamSchedules.value = data.records // 保存当前页的团队行程列表
        schedulePage.value = data.current // 保存当前页码
        scheduleSize.value = data.size // 保存当前每页数量
        scheduleTotal.value = data.total // 保存总行程数
      },
      onError: (error) => {
        errorMessage.value = error.message || '获取团队行程失败' // 写入错误提示
      }
    }
  )

  // 3）获取指定团队详情（按需调用）
  const {
    loading: loadingTeamDetail, // 获取团队详情时的局部 loading
    error: teamDetailError, // 获取团队详情时的错误对象
    run: fetchTeamDetail // 发起获取团队详情请求的方法
  } = useRequest<Team, number>(
    (teamId) => {
      // 如果没有传入 teamId，则尝试使用当前选中团队 ID
      const finalId = teamId ?? currentTeamId.value // 决定最终使用的团队 ID
      if (!finalId) {
        return Promise.reject(new Error('请先选择一个团队')) // 没有有效 teamId 时直接报错
      }
      return getTeamDetail(finalId) // 调用实际的获取团队详情接口
    },
    {
      onError: (error) => {
        errorMessage.value = error.message || '获取团队详情失败' // 写入错误提示
      }
    }
  )

  // ========== 操作型方法：创建团队 / 生成邀请码 / 邀请成员 / 创建行程 / 同步行程 ==========

  // 选择当前团队（只修改前端状态，不调用接口）
  const selectTeam = (teamId: number) => {
    currentTeamId.value = teamId // 将传入的团队 ID 设为当前选中团队
  }

  // 创建新团队
  const createNewTeam = async (params: CreateTeamParams) => {
    loading.value = true // 标记全局加载中
    errorMessage.value = '' // 清空历史错误
    try {
      const team = await createTeam(params) // 调用后端创建团队接口
      // 将新团队追加到团队列表中
      teams.value = [...teams.value, team] // 使用展开运算符创建新数组，触发响应式更新
      // 如果之前尚未选择团队，则自动将新建团队设为当前团队
      if (!currentTeamId.value) {
        currentTeamId.value = team.id // 选择新创建的团队
      }
      return team // 返回新创建的团队信息
    } catch (error: any) {
      console.error('创建团队失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '创建团队失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误，便于页面弹出对话框
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 重新生成团队邀请码
  const regenerateInviteCode = async (teamId?: number) => {
    loading.value = true // 标记全局加载中
    errorMessage.value = '' // 清空错误
    try {
      const finalId = teamId ?? currentTeamId.value // 确定团队 ID
      if (!finalId) {
        throw new Error('请先选择一个团队') // 如果没有 teamId，则直接抛错
      }
      const res = await regenerateTeamInviteCode(finalId) // 调用后端接口重新生成邀请码
      // 同步更新本地团队列表中的邀请码字段
      teams.value = teams.value.map((team) =>
        team.id === finalId
          ? {
              ...team, // 保留原有字段
              inviteCode: res.inviteCode // 更新邀请码为最新的值
            }
          : team // 其他团队保持不变
      )
      return res.inviteCode // 返回新的邀请码，方便页面展示或复制
    } catch (error: any) {
      console.error('重新生成团队邀请码失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '重新生成邀请码失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误给调用方
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 邀请成员加入团队
  const inviteMembers = async (userIds: number[], teamId?: number) => {
    loading.value = true // 标记为加载中
    errorMessage.value = '' // 清空错误
    try {
      const finalId = teamId ?? currentTeamId.value // 决定最终使用的团队 ID
      if (!finalId) {
        throw new Error('请先选择一个团队') // 如果没有有效团队 ID，直接抛错
      }
      await inviteTeamMembers(finalId, userIds) // 调用后端接口邀请成员
    } catch (error: any) {
      console.error('邀请团队成员失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '邀请成员失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误给调用方
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 创建团队行程
  const createNewTeamSchedule = async (params: Omit<CreateTeamScheduleParams, 'teamId'> & { teamId?: number }) => {
    loading.value = true // 标记加载中
    errorMessage.value = '' // 清空错误
    try {
      const finalTeamId = params.teamId ?? currentTeamId.value // 决定最终团队 ID
      if (!finalTeamId) {
        throw new Error('请先选择一个团队') // 没有有效团队 ID 时提示错误
      }
      // 将 teamId 填充进真正的请求参数中
      const payload: CreateTeamScheduleParams = {
        teamId: finalTeamId, // 使用最终确定的 teamId
        title: params.title, // 行程标题
        description: params.description, // 行程描述
        startTime: params.startTime, // 开始时间
        endTime: params.endTime, // 结束时间
        location: params.location // 行程地点
      }
      const schedule = await createTeamSchedule(payload) // 调用创建团队行程接口
      // 创建成功后，可以选择直接插入到当前行程列表的头部
      if (finalTeamId === currentTeamId.value) {
        teamSchedules.value = [schedule, ...teamSchedules.value] // 将新行程插入到列表前面
        scheduleTotal.value += 1 // 总数加一
      }
      return schedule // 返回新创建的行程对象
    } catch (error: any) {
      console.error('创建团队行程失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '创建团队行程失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误给调用方
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // 将团队行程同步到个人行程
  const syncTeamSchedule = async (scheduleId: number) => {
    loading.value = true // 标记加载中
    errorMessage.value = '' // 清空错误
    try {
      await syncTeamScheduleToPersonal(scheduleId) // 调用后端接口同步行程到个人日程
    } catch (error: any) {
      console.error('同步团队行程到个人行程失败:', error) // 打印错误日志
      errorMessage.value = error?.message || '同步行程失败，请稍后重试' // 写入错误提示
      throw error // 抛出错误给调用方
    } finally {
      loading.value = false // 恢复加载状态
    }
  }

  // ========== 对外统一返回 ==========

  return {
    // 基础状态
    loading, // 全局加载中状态
    errorMessage, // 最近一次操作的错误提示文案

    // 团队列表及当前团队
    teams, // 当前用户所属团队列表
    currentTeamId, // 当前选中团队的 ID
    currentTeam, // 当前选中团队的详细信息（计算属性）
    fetchTeams, // 获取团队列表的方法
    loadingTeams, // 获取团队列表时的局部 loading 状态
    teamsError, // 获取团队列表时的错误对象
    selectTeam, // 修改当前选中团队的方法

    // 团队行程相关
    teamSchedules, // 当前团队行程列表
    schedulePage, // 团队行程当前页码
    scheduleSize, // 团队行程每页数量
    scheduleTotal, // 团队行程总数
    fetchTeamSchedules, // 获取团队行程列表的方法
    loadingTeamSchedules, // 获取团队行程时的局部 loading 状态
    teamSchedulesError, // 获取团队行程时的错误对象

    // 团队详情（按需获取）
    fetchTeamDetail, // 获取指定团队详情的方法
    loadingTeamDetail, // 获取团队详情时的局部 loading 状态
    teamDetailError, // 获取团队详情时的错误对象

    // 操作方法
    createNewTeam, // 创建团队的方法
    regenerateInviteCode, // 重新生成团队邀请码的方法
    inviteMembers, // 邀请成员加入团队的方法
    createNewTeamSchedule, // 创建团队行程的方法
    syncTeamSchedule // 将团队行程同步到个人行程的方法
  }
} // useScheduleTeam 钩子定义结束
