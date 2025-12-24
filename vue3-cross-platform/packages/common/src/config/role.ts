/**
 * 角色与权限相关配置
 * 说明：统一定义平台中用到的角色枚举、显示文案等信息，
 *       方便在 Web / App 中保持角色含义与展示的一致性。
 */

// 定义系统中可能出现的用户角色
export type UserRole =
  | 'student' // 学生
  | 'teacher' // 教师
  | 'university' // 高校管理员
  | 'merchant' // 商家
  | 'admin' // 系统管理员
  | 'visitor' // 未登录游客

// 单个角色配置项的数据结构
export interface RoleConfigItem {
  value: UserRole // 角色标识（用于接口和权限判断）
  label: string // 展示给用户看的角色名称
  description: string // 对该角色能力/定位的简短说明
}

// 平台支持的角色列表（用于角色选择下拉框、前端展示等）
export const ROLE_CONFIG_LIST: RoleConfigItem[] = [
  {
    value: 'student', // 学生角色标识
    label: '学生', // 中文名称
    description: '在校大学生，可使用二手、兼职、行程、E卡通等全部功能' // 简要说明
  },
  {
    value: 'teacher', // 教师角色标识
    label: '教师', // 中文名称
    description: '在校教师，可查看教学相关信息，使用行程与校园服务' // 简要说明
  },
  {
    value: 'university', // 高校管理员角色标识
    label: '高校管理员', // 中文名称
    description: '高校官方账号，可管理学校配置与统计数据' // 简要说明
  },
  {
    value: 'merchant', // 商家角色标识
    label: '商家', // 中文名称
    description: '入驻校园周边商家，可发布兼职岗位、管理交易' // 简要说明
  },
  {
    value: 'admin', // 系统管理员角色标识
    label: '系统管理员', // 中文名称
    description: '平台超管账号，拥有全局配置与运维权限' // 简要说明
  },
  {
    value: 'visitor', // 游客角色标识
    label: '游客', // 中文名称
    description: '未登录或未认证用户，只能浏览公开内容' // 简要说明
  }
] // 角色配置列表结束

// 角色对应的“是否可发布内容”的简单权限映射（前端快速判断用）
export const ROLE_CAN_PUBLISH: Record<UserRole, boolean> = {
  student: true, // 学生允许发布二手、求购等
  teacher: true, // 教师允许发布部分内容
  university: true, // 高校管理员可以发布公告等
  merchant: true, // 商家可以发布兼职、活动等
  admin: true, // 管理员拥有所有发布权限
  visitor: false // 游客不允许发布内容
} // 角色发布权限映射结束
