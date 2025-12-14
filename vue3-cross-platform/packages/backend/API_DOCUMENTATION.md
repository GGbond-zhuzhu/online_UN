# 校园集成系统 - API接口文档

## 📋 接口总览

根据参考网页设计和业务需求，系统共包含 **9个Controller**，**80+个API接口**。

### 接口统计

| Controller | 接口数量 | 说明 |
|-----------|---------|------|
| UserController | 6个 | 用户注册、登录、信息管理 |
| AuthController | 7个 | 身份认证（学生/教师/游客/高校） |
| EcardController | 12个 | 校园卡管理、消费、人脸支付 |
| SecondhandController | 12个 | 二手交易平台 |
| ParttimeController | 12个 | 兼职管理 |
| ScheduleController | 18个 | 行程管理、团队协作、课程表导入 |
| CommonController | 15个 | 通用功能（帮助、公告、反馈等） |
| AdminController | 11个 | 管理员功能 |
| MerchantController | 8个 | 商户管理 |
| UniversityController | 10个 | 高校管理（高校角色专用） |

**总计：111个API接口**

---

## 📚 详细接口列表

### 1. 用户管理 (UserController)

**基础路径**: `/api/user`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/register` | 用户注册 |
| POST | `/login` | 用户登录 |
| GET | `/info` | 获取当前用户信息 |
| PUT | `/info` | 更新用户信息 |
| POST | `/change-password` | 修改密码 |
| POST | `/logout` | 退出登录 |

---

### 2. 身份认证 (AuthController)

**基础路径**: `/api/auth`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/student/apply` | 学生身份认证申请（学号+验证码） |
| POST | `/teacher/apply` | 教师身份认证申请 |
| POST | `/visitor/face-detect` | 游客刷脸活体检测 |
| POST | `/visitor/register` | 游客进校登记 |
| GET | `/apply/status` | 查询认证申请状态 |
| POST | `/university/apply` | 高校官方接入申请 |
| GET | `/apply/records` | 获取认证申请记录 |

---

### 3. 校园卡管理 (EcardController)

**基础路径**: `/api/ecard`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/info` | 获取校园卡信息 |
| POST | `/consume` | 校园卡消费 |
| GET | `/consume-records` | 查询消费记录（分页） |
| POST | `/visitor-card/apply` | 申请游客临时卡 |
| POST | `/report-loss` | 校园卡挂失 |
| POST | `/cancel-loss` | 校园卡解挂 |
| GET | `/check-location` | 校验定位是否在校内 |
| GET | `/today-statistics` | 获取今日消费统计 |
| POST | `/recharge` | 充值校园卡 |
| POST | `/face-pay` | 人脸支付（新功能） |
| GET | `/dynamic-code` | 生成动态学生码 |
| POST | `/access-control` | 门禁系统对接 |
| POST | `/library` | 图书馆系统对接 |

---

### 4. 二手交易平台 (SecondhandController)

**基础路径**: `/api/secondhand`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/publish` | 发布二手商品 |
| GET | `/list` | 商品列表（分页） |
| GET | `/detail/{id}` | 商品详情 |
| GET | `/my-goods` | 获取我的发布 |
| PUT | `/update/{id}` | 修改商品信息 |
| PUT | `/off-shelf/{id}` | 下架商品 |
| PUT | `/on-shelf/{id}` | 重新上架商品 |
| DELETE | `/delete/{id}` | 删除商品 |
| POST | `/favorite/{id}` | 收藏商品 |
| DELETE | `/favorite/{id}` | 取消收藏 |
| GET | `/favorites` | 获取收藏列表 |
| GET | `/categories` | 获取商品分类 |

---

### 5. 兼职管理 (ParttimeController)

**基础路径**: `/api/parttime`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/publish` | 发布兼职 |
| GET | `/list` | 兼职列表（分页） |
| GET | `/detail/{id}` | 兼职详情 |
| POST | `/apply` | 报名兼职 |
| DELETE | `/apply/{id}` | 取消报名 |
| GET | `/my-applications` | 获取我的报名记录 |
| GET | `/my-published` | 获取我发布的兼职 |
| GET | `/{id}/applications` | 获取兼职报名列表 |
| PUT | `/applications/{applicationId}/process` | 处理报名申请 |
| PUT | `/update/{id}` | 修改兼职信息 |
| PUT | `/update-status/{id}` | 更新兼职状态 |
| DELETE | `/delete/{id}` | 删除兼职 |

---

### 6. 行程管理 (ScheduleController)

**基础路径**: `/api/schedule`

#### 个人行程
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/personal/create` | 创建个人行程 |
| GET | `/personal/list` | 获取个人行程列表 |
| GET | `/personal/detail/{id}` | 获取个人行程详情 |
| PUT | `/personal/update/{id}` | 修改个人行程 |
| DELETE | `/personal/delete/{id}` | 删除个人行程 |
| PUT | `/personal/update-status/{id}` | 更新行程状态 |

#### 团队管理
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/team/create` | 创建团队 |
| GET | `/team/my-teams` | 获取我的团队列表 |
| GET | `/team/detail/{id}` | 获取团队详情 |
| POST | `/team/{teamId}/invite` | 邀请成员加入团队 |
| POST | `/team/invite/{inviteId}/process` | 处理团队邀请 |
| DELETE | `/team/{teamId}/member/{userId}` | 移除团队成员 |

#### 团队行程
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/team/create-schedule` | 创建团队行程 |
| GET | `/team/{teamId}/schedules` | 获取团队行程列表 |

#### 行程同步
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/sync/personal-to-team` | 同步个人行程到团队 |
| POST | `/sync/team-to-personal` | 同步团队行程到个人 |

#### 课程表导入（新功能）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/import/schedule-excel` | 导入课程表（Excel） |
| GET | `/export/schedule-excel` | 导出课程表（Excel） |
| GET | `/import/template` | 获取课程表模板 |

#### 提醒与日历
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/reminders/today` | 获取今日提醒 |
| PUT | `/reminders/mark-reminded/{id}` | 标记为已提醒 |
| GET | `/calendar/{year}/{month}` | 获取日历视图数据 |

---

### 7. 通用功能 (CommonController)

**基础路径**: `/api/common`

#### 帮助中心
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/help/categories` | 获取帮助文档分类 |
| GET | `/help/article/{id}` | 获取帮助文档详情 |
| GET | `/help/search` | 搜索帮助文档 |

#### 公告管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/announcements` | 获取公告列表 |
| GET | `/announcements/{id}` | 获取公告详情 |

#### 文件上传
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/upload/image` | 上传图片 |
| POST | `/upload/file` | 上传文件 |

#### 安全保障（新功能）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/security/info` | 获取安全保障说明 |
| GET | `/privacy-policy` | 获取隐私政策 |
| GET | `/service-agreement` | 获取服务协议 |

#### 用户反馈（新功能）
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/feedback` | 提交用户反馈 |
| GET | `/feedback/my-feedbacks` | 获取我的反馈列表 |

#### 联系我们（新功能）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/contact` | 获取联系方式 |
| POST | `/contact/submit` | 提交联系表单 |

#### 用户调研（新功能）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/surveys` | 获取调研问卷列表 |
| GET | `/surveys/{surveyId}` | 获取问卷详情 |
| POST | `/surveys/{surveyId}/submit` | 提交问卷答案 |

#### 平台介绍（新功能）
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/about` | 获取平台介绍 |

---

### 8. 管理员功能 (AdminController)

**基础路径**: `/api/admin`

#### 用户管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/users` | 获取用户列表 |
| PUT | `/users/{userId}/status` | 禁用/启用用户 |
| POST | `/users/{userId}/reset-password` | 重置用户密码 |

#### 认证审核
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/auth-applies` | 获取认证申请列表 |
| PUT | `/auth-applies/{applyId}/review` | 审核认证申请 |

#### 内容审核
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/content/review-list` | 获取待审核内容列表 |
| PUT | `/content/{contentId}/review` | 审核内容 |

#### 高校管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/universities` | 获取高校列表 |
| PUT | `/universities/{applyId}/review` | 审核高校接入申请 |

#### 系统配置
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/config` | 获取系统配置 |
| PUT | `/config` | 更新系统配置 |

#### 数据统计
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/statistics` | 获取平台数据统计 |

---

### 9. 商户管理 (MerchantController)

**基础路径**: `/api/merchant`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/apply` | 商户入驻申请 |
| POST | `/verify-company` | 公司资格智能校验 |
| GET | `/info` | 获取商户信息 |
| POST | `/deposit/pay` | 缴纳保证金 |
| GET | `/deposit/records` | 查询保证金记录 |
| GET | `/jobs` | 获取商户发布的兼职列表 |
| GET | `/statistics` | 获取商户统计数据 |

---

### 10. 高校管理 (UniversityController) - 新增

**基础路径**: `/api/university`

#### 高校信息管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/info` | 获取高校信息 |
| PUT | `/info` | 更新高校信息 |

#### 用户管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/users` | 获取本校用户列表 |
| PUT | `/users/auth-applies/{applyId}/review` | 审核本校用户认证申请 |
| POST | `/users/import` | 批量导入用户（Excel） |
| GET | `/users/export` | 导出用户列表（Excel） |

#### 功能配置
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/config` | 获取功能配置 |
| PUT | `/config` | 更新功能配置 |

#### 数据统计
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/statistics` | 获取高校数据统计 |
| GET | `/statistics/activity` | 获取用户活跃度统计 |
| GET | `/content/statistics` | 获取本校内容统计 |

#### 内容管理
| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | `/content/{contentId}/review` | 审核本校内容 |

#### 通知推送
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/notifications/send` | 发送通知 |
| GET | `/notifications/history` | 获取通知发送记录 |

---

## 🆕 本次新增接口

### 1. 课程表导入功能（ScheduleController）
- ✅ `POST /api/schedule/import/schedule-excel` - Excel导入课程表
- ✅ `GET /api/schedule/export/schedule-excel` - 导出课程表
- ✅ `GET /api/schedule/import/template` - 获取导入模板

### 2. 用户管理增强（UserController）
- ✅ `PUT /api/user/info` - 更新用户信息
- ✅ `POST /api/user/change-password` - 修改密码
- ✅ `POST /api/user/logout` - 退出登录

### 3. 通用功能扩展（CommonController）
- ✅ `GET /api/common/security/info` - 安全保障说明
- ✅ `GET /api/common/privacy-policy` - 隐私政策
- ✅ `GET /api/common/service-agreement` - 服务协议
- ✅ `POST /api/common/feedback` - 提交反馈
- ✅ `GET /api/common/feedback/my-feedbacks` - 我的反馈
- ✅ `GET /api/common/contact` - 联系方式
- ✅ `POST /api/common/contact/submit` - 提交联系表单
- ✅ `GET /api/common/surveys` - 调研问卷列表
- ✅ `GET /api/common/surveys/{surveyId}` - 问卷详情
- ✅ `POST /api/common/surveys/{surveyId}/submit` - 提交问卷
- ✅ `GET /api/common/about` - 平台介绍

### 4. 高校管理功能（UniversityController）- 全新Controller
- ✅ `GET /api/university/info` - 获取高校信息
- ✅ `PUT /api/university/info` - 更新高校信息
- ✅ `GET /api/university/users` - 获取本校用户列表
- ✅ `PUT /api/university/users/auth-applies/{applyId}/review` - 审核用户认证
- ✅ `POST /api/university/users/import` - 批量导入用户
- ✅ `GET /api/university/users/export` - 导出用户列表
- ✅ `GET /api/university/config` - 获取功能配置
- ✅ `PUT /api/university/config` - 更新功能配置
- ✅ `GET /api/university/statistics` - 高校数据统计
- ✅ `GET /api/university/statistics/activity` - 用户活跃度统计
- ✅ `GET /api/university/content/statistics` - 内容统计
- ✅ `PUT /api/university/content/{contentId}/review` - 审核内容
- ✅ `POST /api/university/notifications/send` - 发送通知
- ✅ `GET /api/university/notifications/history` - 通知历史

---

## 📝 接口设计说明

### 统一响应格式

所有接口统一使用 `ApiResponse<T>` 格式：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... }
}
```

### 统一错误码

- `200`: 成功
- `400`: 参数错误
- `401`: 未授权
- `403`: 无权限
- `404`: 资源不存在
- `500`: 服务器错误

### 权限说明

- **游客**: 仅浏览，不能发布/申请
- **学生/教师**: 可发布二手、报名兼职、创建行程
- **高校**: 可管理本校用户、配置功能、发送通知
- **商户**: 可发布兼职、管理报名
- **管理员**: 全局管理权限

---

## 🔗 API文档访问

启动后端服务后，访问：

- **Knife4j文档**: http://localhost:8080/doc.html
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

---

## 📌 注意事项

1. **JWT认证**: 除登录、注册外，所有接口需要在请求头携带 `Authorization: Bearer {token}`
2. **角色权限**: 不同角色可访问的接口不同，详见各Controller说明
3. **分页参数**: 列表接口统一使用 `page` 和 `size` 参数
4. **文件上传**: 使用 `MultipartFile` 类型，支持图片和Excel文件
5. **定位校验**: 校园卡相关接口需要验证用户是否在校内

---

## 🎯 参考网页功能对照

根据参考网页 [上大学Online](https://ggbond-zhuzhu.github.io/online_UN/)，已实现的功能：

✅ **一校一集合，一人一身份** - AuthController实现
✅ **一站全服务** - 4大子系统完整实现
✅ **安全保障** - CommonController新增安全保障接口
✅ **课程表自动导入** - ScheduleController新增Excel导入
✅ **多设备同步** - ScheduleController同步功能
✅ **高校官方接入** - AuthController + UniversityController
✅ **用户反馈** - CommonController新增反馈功能
✅ **帮助中心** - CommonController已实现
✅ **平台介绍** - CommonController新增about接口

---

## 📅 更新日志

**2024-12-13**
- ✅ 新增课程表导入功能（Excel）
- ✅ 新增用户管理增强功能（修改密码、更新信息）
- ✅ 新增安全保障、隐私政策、服务协议接口
- ✅ 新增用户反馈、联系我们、用户调研功能
- ✅ 新增高校管理Controller（UniversityController）
- ✅ 完善ConsumeRecordVO，添加payMethod字段

