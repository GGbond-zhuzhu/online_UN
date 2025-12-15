# API文档访问指南

## 📖 文档访问地址

启动后端服务后，可以通过以下地址访问API文档：

### 主要访问地址
- **Knife4j文档（推荐）**: http://localhost:8080/doc.html
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 📚 文档分组说明

API文档按功能模块进行了分组，方便查找和使用：

| 分组名称 | 路径前缀 | 说明 |
|---------|---------|------|
| 00-全部接口 | `/api/**` | 所有API接口 |
| 01-用户管理 | `/api/user/**` | 用户注册、登录、信息管理 |
| 02-校园卡管理 | `/api/ecard/**` | 校园卡管理、消费、人脸支付 |
| 03-二手交易平台 | `/api/secondhand/**` | 商品发布、浏览、收藏、浏览记录 |
| 04-兼职管理 | `/api/parttime/**` | 兼职发布、报名、收藏、浏览记录 |
| 05-行程管理 | `/api/schedule/**` | 个人/团队行程、课程表导入 |
| 06-身份认证 | `/api/auth/**` | 学生/教师/游客/高校认证、邮箱登录 |
| 07-通用功能 | `/api/common/**` | 帮助中心、公告、反馈、文件上传 |
| 08-消息中心 | `/api/messages/**` | 系统消息、消息列表、标记已读 |
| 09-聊天功能 | `/api/chat/**` | 聊天会话、消息发送 |
| 10-管理员功能 | `/api/admin/**` | 用户管理、内容审核、系统配置 |
| 11-商户管理 | `/api/merchant/**` | 商户入驻、保证金管理 |
| 12-高校管理 | `/api/university/**` | 高校信息、用户管理、数据统计 |

## 🔍 文档功能

### 1. 接口浏览
- 按模块分组查看所有接口
- 查看接口的详细说明、请求参数、响应格式
- 查看请求/响应示例

### 2. 在线测试
- 直接在文档页面测试接口
- 支持设置请求头（如JWT Token）
- 查看实时响应结果

### 3. 接口搜索
- 使用搜索功能快速查找接口
- 支持按接口名称、路径、描述搜索

### 4. 导出文档
- 支持导出OpenAPI JSON格式
- 可以导入到Postman等工具中使用

## 🔐 认证说明

大部分接口需要JWT认证，测试时请：

1. 先调用登录接口获取Token
2. 在文档页面右上角点击"Authorize"按钮
3. 输入Token（格式：`Bearer {token}`）
4. 然后就可以测试需要认证的接口了

## 📝 新增接口说明

### 浏览记录管理
- `POST /api/secondhand/browse/{id}` - 记录商品浏览
- `GET /api/secondhand/browse-history` - 获取浏览记录列表
- `DELETE /api/secondhand/browse-history/{id}` - 删除浏览记录
- `DELETE /api/secondhand/browse-history/clear` - 清空浏览记录
- `POST /api/parttime/browse/{id}` - 记录岗位浏览
- `GET /api/parttime/browse-history` - 获取浏览记录列表
- `DELETE /api/parttime/browse-history/{id}` - 删除浏览记录
- `DELETE /api/parttime/browse-history/clear` - 清空浏览记录

### 兼职收藏管理
- `POST /api/parttime/favorite/{id}` - 收藏兼职
- `DELETE /api/parttime/favorite/{id}` - 取消收藏
- `GET /api/parttime/favorites` - 获取收藏列表
- `DELETE /api/parttime/favorites/clear` - 清空收藏

### 消息和聊天
- `GET /api/messages/list` - 获取消息列表
- `PUT /api/messages/{id}/read` - 标记消息已读
- `PUT /api/messages/batch-read` - 批量标记已读
- `GET /api/chat/conversations` - 获取聊天会话列表
- `GET /api/chat/messages` - 获取聊天消息列表
- `POST /api/chat/send` - 发送消息

### 行程管理增强
- `POST /api/schedule/team/join-by-code` - 通过邀请码加入团队
- `GET /api/schedule/team/invitations` - 获取团队邀请列表
- `PUT /api/schedule/reminder/{id}/status` - 更新提醒状态
- `DELETE /api/schedule/reminder/{id}` - 删除提醒
- `POST /api/schedule/import/manual` - 手动录入导入课程表
- `POST /api/schedule/import/from-link` - 链接导入课程表

## 🚀 快速开始

1. **启动后端服务**
   ```bash
   cd vue3-cross-platform/packages/backend
   mvn spring-boot:run
   ```

2. **访问文档**
   打开浏览器访问：http://localhost:8080/doc.html

3. **测试接口**
   - 选择"01-用户管理"分组
   - 找到"用户登录"接口
   - 点击"调试"按钮
   - 输入用户名和密码
   - 点击"发送请求"
   - 复制返回的Token

4. **设置认证**
   - 点击页面右上角"Authorize"按钮
   - 输入：`Bearer {刚才复制的Token}`
   - 点击"Authorize"

5. **测试其他接口**
   - 现在可以测试需要认证的接口了
   - 选择任意接口，点击"调试"即可测试

## 📌 注意事项

1. **数据库配置**：确保MySQL服务已启动，数据库已创建
2. **端口占用**：确保8080端口未被占用
3. **跨域问题**：前端调用时可能需要配置CORS
4. **Token过期**：Token默认24小时过期，过期后需要重新登录

## 🔗 相关文档

- [API接口文档](./API_DOCUMENTATION.md) - 完整的接口说明文档
- [缺失API清单](./MISSING_APIS.md) - 已实现的缺失接口清单
- [README](./README.md) - 项目说明文档

