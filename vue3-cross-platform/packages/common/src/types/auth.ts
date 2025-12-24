/**
 * 认证与用户相关类型统一出口
 * 说明：这里主要是对 api/auth 中已有类型的二次导出，
 *       方便业务代码通过 `@campus/common` 顶层直接引入。
 */

// 从 api/auth 中导入需要复用的类型定义，并在此处使用更明确的别名导出，避免与顶层 API 导出产生命名冲突
export type {
  LoginParams as AuthLoginParams, // 登录参数类型（用户名 + 密码），在类型出口中使用 Auth 前缀区分
  LoginResponse as AuthLoginResponse, // 登录响应类型（包含 token、用户信息等），带 Auth 前缀避免与其他模块混淆
  RegisterParams as AuthRegisterParams, // 注册参数类型，命名为 AuthRegisterParams 更直观
  UserInfo as AuthUserInfo, // 用户信息类型，在类型出口中命名为 AuthUserInfo，防止与其他 UserInfo 类型重名
  StudentAuthApplyParams as AuthStudentAuthApplyParams, // 学生身份认证申请参数类型，命名为 AuthStudentAuthApplyParams 便于区分
  AuthApplyStatus as AuthApplyStatusInfo, // 单条认证申请状态查询结果类型，命名为 AuthApplyStatusInfo 更清晰
  AuthApplyRecordItem as AuthApplyRecordItemInfo, // 认证申请记录单条数据类型，命名为 AuthApplyRecordItemInfo
  AuthApplyRecordList as AuthApplyRecordListInfo // 认证申请记录分页列表类型，命名为 AuthApplyRecordListInfo
} from '../api/auth' // 相对路径导入 auth 接口模块中的类型，再通过别名统一对外暴露
