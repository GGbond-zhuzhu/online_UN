/**
 * 用户认证相关API                                          // 整个文件：封装所有与用户认证相关的前端接口调用
 * 包括登录、注册、用户信息等接口                          // 功能包含：登录、注册、用户信息、人脸认证等
 */
import request from '../../utils/request'                 // 引入统一封装的 request 实例，用于发起 HTTP 请求

// 登录请求参数                                             // LoginParams：登录接口需要的参数结构
export interface LoginParams {                             // 定义登录参数接口类型
  username: string                                         // username：用户名
  password: string                                         // password：密码
}

// 登录响应数据（适配后端LoginVO）                           // LoginResponse：登录接口返回的数据结构（与后端 LoginVO 对齐）
export interface LoginResponse {                           // 定义登录响应数据接口
  token: string                                            // token：后端签发的 JWT 字符串
  userId: number                                           // userId：当前登录用户的ID
  username: string                                         // username：当前登录用户的用户名
  role: string                                             // role：当前登录用户的角色编码
  expiresIn?: number                                       // expiresIn：Token 过期时间（秒），可选
  // 兼容旧格式                                             // 下面的 userInfo 字段用于兼容旧版返回结构
  userInfo?: {                                             // userInfo：旧版中嵌套的用户信息对象
    id: number                                             // id：用户ID
    username: string                                       // username：用户名
    role: string                                           // role：角色编码
    campusId?: number                                      // campusId：所属学校ID（可选）
    campusName?: string                                    // campusName：所属学校名称（可选）
  }
}

// 注册请求参数                                             // RegisterParams：注册接口需要的参数结构
export interface RegisterParams {                          // 定义注册参数接口类型
  username: string                                         // username：注册时填写的用户名
  password: string                                         // password：注册时填写的密码
  email?: string                                           // email：邮箱（可选）
  phone?: string                                           // phone：手机号（可选）
}

// 用户信息                                                 // UserInfo：当前登录用户的详细信息结构
export interface UserInfo {                                // 定义用户信息接口类型
  id: number                                               // id：用户ID
  username: string                                         // username：用户名
  email?: string                                           // email：邮箱（可选）
  phone?: string                                           // phone：手机号（可选）
  role: string                                             // role：角色编码
  campusId?: number                                        // campusId：所属学校ID（可选）
  campusName?: string                                      // campusName：所属学校名称（可选）
  avatar?: string                                          // avatar：头像地址（可选）
}

// 游客刷脸活体检测请求参数                                  // VisitorFaceDetectParams：游客刷脸活体检测接口的入参类型
export interface VisitorFaceDetectParams {                 // 定义游客刷脸活体检测请求参数类型
  faceImage: string                                        // faceImage：人脸照片的 Base64 字符串（可带 data:image 前缀）
  name: string                                             // name：游客姓名
  phone: string                                            // phone：游客手机号
  idCard: string                                           // idCard：游客身份证号
  reason: string                                           // reason：进校事由说明
}

// 游客刷脸活体检测响应数据                                  // VisitorFaceDetectResponse：游客刷脸活体检测接口返回的数据结构
export interface VisitorFaceDetectResponse {               // 定义游客刷脸活体检测响应数据类型
  detectResult: string                                     // detectResult：检测流程结果标记（例如 SUCCESS）
  livenessScore: number                                    // livenessScore：活体检测分数（0~1）
  isAlive: boolean                                         // isAlive：是否通过活体检测
  faceNum: number                                          // faceNum：检测到的人脸数量
  message: string                                          // message：后端返回的友好提示文案
  timestamp: string                                        // timestamp：检测完成的时间戳字符串
}

/**
 * 用户登录                                                 // login：调用后端登录接口，获取 Token 和用户信息
 * @param params 登录参数                                  // params：登录所需的用户名和密码
 */
export function login(params: LoginParams): Promise<LoginResponse> { // 定义 login 函数，返回 Promise<LoginResponse>
  return request.post<LoginResponse>('/api/user/login', params)     // 使用 POST 请求 /api/user/login，并将参数放入请求体
}

/**
 * 获取当前用户信息
 */
export function getUserInfo(): Promise<UserInfo> {
  return request.get<UserInfo>('/api/user/info')
}

/**
 * 更新用户信息
 */
export interface UpdateUserInfoParams {
  nickname?: string
  avatarUrl?: string
  phone?: string
  email?: string
}

export function updateUserInfo(params: UpdateUserInfoParams): Promise<UserInfo> {
  return request.put<UserInfo>('/api/user/info', params)
}

/**
 * 获取用户统计数据
 */
export interface UserStats {
  favoritesCount: number
  historyCount: number
  publishCount: number
  pointsCount: number
}

export function getUserStats(): Promise<UserStats> {
  return request.get<UserStats>('/api/user/stats')
}

/**
 * 用户注册                                                 // register：调用后端注册接口，创建新用户
 * @param params 注册参数                                  // params：注册所需的用户名、密码等信息
 */
export function register(params: RegisterParams): Promise<{ id: number; username: string }> { // 定义 register 函数
  return request.post('/api/user/register', params)                                            // 使用 POST 请求 /api/user/register
}

/**
 * 修改密码                                                 // changePassword：修改当前用户登录密码
 * @param oldPassword 旧密码                               // oldPassword：原密码
 * @param newPassword 新密码                               // newPassword：新密码
 */
export function changePassword(oldPassword: string, newPassword: string): Promise<void> { // 定义 changePassword 函数
  return request.post('/api/user/change-password', { oldPassword, newPassword })          // 使用 POST 请求 /api/user/change-password
}

/**
 * 退出登录                                                 // logout：退出当前登录状态
 */
export function logout(): Promise<void> {                 // 定义 logout 函数
  return request.post('/api/user/logout')                // 使用 POST 请求 /api/user/logout
}

/**
 * 发送邮箱验证码                                           // sendEmailCode：向指定邮箱发送登录验证码
 * @param email 邮箱地址                                   // email：目标邮箱地址
 */
export function sendEmailCode(email: string): Promise<{ codeId: string; code?: string; expireTime: number }> { // 定义 sendEmailCode 函数
  return request.post('/api/auth/email/send-code', { email })                                                   // 使用 POST 请求 /api/auth/email/send-code
}

/**
 * 邮箱登录                                                 // emailLogin：使用邮箱验证码完成登录
 * @param email 邮箱地址                                   // email：邮箱地址
 * @param code 验证码                                      // code：收到的邮箱验证码
 * @param codeId 验证码ID                                  // codeId：发送验证码接口返回的验证码ID
 */
export function emailLogin(email: string, code: string, codeId: string): Promise<LoginResponse> { // 定义 emailLogin 函数
  return request.post<LoginResponse>('/api/auth/email/login', { email, code, codeId })           // 使用 POST 请求 /api/auth/email/login
}

/**
 * 游客刷脸活体检测                                         // visitorFaceDetect：调用后端游客刷脸活体检测接口
 * @param params 检测参数（包含人脸照片和基础信息）         // params：包含人脸 Base64、姓名、手机号、身份证号和进校事由
 */
export function visitorFaceDetect(params: VisitorFaceDetectParams): Promise<VisitorFaceDetectResponse> { // 定义 visitorFaceDetect 函数
  // 这里使用 POST + queryString 的方式调用后端接口，对应后端的 @RequestParam 入参            // 说明：使用 POST 请求并通过 params 选项拼接查询参数
  return request.post<VisitorFaceDetectResponse>('/api/auth/visitor/face-detect', null, {        // 调用 /api/auth/visitor/face-detect 接口
    params                                                                                       // params：通过 axios 的 params 选项拼接到URL查询参数中
  })
}

// ==================== 学生身份认证相关类型与接口 ====================

// 学生身份认证申请参数                                      // StudentAuthApplyParams：学生身份认证时需要提交的字段集合
export interface StudentAuthApplyParams {                  // 定义学生身份认证请求参数接口类型
  studentId: string                                        // studentId：学号
  verificationCode: string                                 // verificationCode：教务系统或学信网验证码
  name: string                                             // name：学生姓名
  idCard: string                                           // idCard：身份证号
  schoolId: number                                         // schoolId：学校ID（由前端从下拉列表或用户信息中选择）
  faceImage?: string                                       // faceImage：人脸照片Base64字符串（可选，用于动态人脸身份认证）
}

// 学生身份认证人脸识别请求参数                              // StudentFaceDetectParams：学生身份认证人脸识别接口的入参类型
export interface StudentFaceDetectParams {                 // 定义学生身份认证人脸识别请求参数类型
  faceImage: string                                       // faceImage：人脸照片的Base64字符串（可带data:image前缀）
  studentId: string                                       // studentId：学号
  name: string                                            // name：学生姓名
  idCard: string                                          // idCard：身份证号
  schoolId: number                                        // schoolId：学校ID
}

// 学生身份认证人脸识别响应数据                              // StudentFaceDetectResponse：学生身份认证人脸识别接口返回的数据结构
export interface StudentFaceDetectResponse {               // 定义学生身份认证人脸识别响应数据类型
  detectResult: string                                    // detectResult：检测流程结果标记（例如SUCCESS）
  livenessScore: number                                   // livenessScore：活体检测分数（0~1）
  isAlive: boolean                                        // isAlive：是否通过活体检测
  faceNum: number                                         // faceNum：检测到的人脸数量
  similarityScore?: number                                 // similarityScore：与身份证照片相似度分数（0~1，可选）
  message: string                                         // message：后端返回的友好提示文案
  timestamp: string                                       // timestamp：检测完成的时间戳字符串
}

// 单条认证申请记录                                          // AuthApplyRecordItem：用于“我的申请列表”中的单条记录
export interface AuthApplyRecordItem {                     // 定义认证申请记录条目接口类型
  id: number                                               // id：申请ID
  realName: string                                         // realName：申请人姓名
  applyRole: string                                        // applyRole：申请的身份角色（如 STUDENT/TEACHER）
  status: 'PENDING' | 'APPROVED' | 'REJECTED'              // status：审核状态
  createTime: string                                       // createTime：申请时间
  auditTime?: string                                       // auditTime：审核时间（可选）
  auditRemark?: string                                     // auditRemark：审核备注（可选）
}

// 认证申请记录列表响应                                      // AuthApplyRecordList：分页返回的认证申请列表数据
export interface AuthApplyRecordList {                     // 定义认证申请记录列表接口类型
  list: AuthApplyRecordItem[]                              // list：当前页的申请记录数组
  total: number                                            // total：总记录数
  page: number                                             // page：当前页码
  size: number                                             // size：每页大小
}

// 单条认证申请状态查询结果                                  // AuthApplyStatus：根据 applyId 查询到的申请状态详情
export interface AuthApplyStatus {                         // 定义认证申请状态接口类型
  applyId: number                                          // applyId：申请ID
  status: 'PENDING' | 'APPROVED' | 'REJECTED'              // status：审核状态
  role: string                                             // role：申请角色（如 STUDENT/TEACHER）
  applyTime: string                                        // applyTime：提交时间
  processTime?: string                                     // processTime：处理时间（可选）
  processNote?: string                                     // processNote：处理备注（可选）
  message: string                                          // message：后端返回的友好提示文案
}

/**
 * 学生身份认证申请                                         // applyStudentAuth：提交学生身份认证申请表单
 * @param params 学生认证参数                              // params：包含学号、验证码、姓名、身份证号和学校ID
 */
export function applyStudentAuth(params: StudentAuthApplyParams): Promise<{ // 定义 applyStudentAuth 函数
  applyId: number                                                           // applyId：后端生成的认证申请ID
  status: string                                                            // status：当前申请状态（通常为 PENDING）
  message: string                                                           // message：后端返回的提示文案
  applyTime: string                                                         // applyTime：申请提交时间
}> {
  // 这里使用 POST + queryString 的方式调用后端接口，对应 @RequestParam 入参                     // 与后端 AuthController.applyStudentAuth 保持一致
  return request.post('/api/auth/student/apply', null, {                    // 调用 /api/auth/student/apply 接口
    params                                                                   // params：通过 queryString 方式传递所有字段
  })
}

/**
 * 查询单条认证申请状态                                     // getAuthApplyStatus：根据申请ID查询审核进度
 * @param applyId 申请ID                                   // applyId：需要查询的认证申请ID
 */
export function getAuthApplyStatus(applyId: number): Promise<AuthApplyStatus> { // 定义 getAuthApplyStatus 函数
  return request.get<AuthApplyStatus>('/api/auth/apply/status', {              // 调用 /api/auth/apply/status 接口
    params: { applyId }                                                        // 使用 params 方式传递 applyId
  })
}

/**
 * 获取当前用户的认证申请记录列表                           // getAuthApplyRecords：分页获取"我的认证申请"列表
 * @param page 页码                                        // page：当前页码，默认 1
 * @param size 每页数量                                    // size：每页加载的记录条数，默认 10
 */
export function getAuthApplyRecords(page = 1, size = 10): Promise<AuthApplyRecordList> { // 定义 getAuthApplyRecords 函数
  return request.get<AuthApplyRecordList>('/api/auth/apply/records', {                  // 调用 /api/auth/apply/records 接口
    params: { page, size }                                                              // 使用 params 方式传递分页参数
  })
}

/**
 * 学生身份认证动态人脸识别                                   // studentFaceDetect：调用后端学生身份认证人脸识别接口
 * @param params 检测参数（包含人脸照片和学籍信息）         // params：包含人脸Base64、学号、姓名、身份证号和学校ID
 */
export function studentFaceDetect(params: StudentFaceDetectParams): Promise<StudentFaceDetectResponse> { // 定义 studentFaceDetect 函数
  // 这里使用POST + queryString的方式调用后端接口，对应后端的@RequestParam入参            // 说明：使用POST请求并通过params选项拼接查询参数
  return request.post<StudentFaceDetectResponse>('/api/auth/student/face-detect', null, { // 调用 /api/auth/student/face-detect 接口
    params                                                                               // params：通过axios的params选项拼接到URL查询参数中
  })
}

