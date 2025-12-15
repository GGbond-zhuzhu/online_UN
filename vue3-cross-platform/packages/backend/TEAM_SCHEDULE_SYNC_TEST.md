# 团队行程同步功能测试文档

## 功能说明

当团队创建者创建团队行程后，系统会自动将该行程同步到所有团队成员的个人行程表中。

## 数据库表结构

### 1. team_schedule（团队行程表）
- `id`: 团队行程ID（主键）
- `team_id`: 团队ID
- `title`: 行程标题
- `description`: 行程描述
- `start_time`: 开始时间
- `end_time`: 结束时间
- `location`: 地点
- `creator_id`: 创建者ID
- `status`: 状态（PENDING/ONGOING/COMPLETED）
- `create_time`: 创建时间

### 2. personal_schedule（个人行程表）
- `id`: 个人行程ID（主键）
- `title`: 行程标题
- `description`: 行程描述
- `start_time`: 开始时间
- `end_time`: 结束时间
- `type`: 行程类型（TEAM表示团队事务）
- `status`: 状态
- `location`: 地点
- `creator_id`: 创建者ID（团队成员的用户ID）
- `synced_team_schedule_id`: 同步的团队行程ID（关联字段）
- `create_time`: 创建时间

### 3. team_member（团队成员表）
- `team_id`: 团队ID
- `user_id`: 用户ID
- `role`: 角色（CREATOR/ADMIN/MEMBER）

## 后端实现逻辑

### API接口
- **路径**: `POST /api/schedule/team/create-schedule`
- **权限**: 需要登录，且必须是团队成员
- **请求体**:
```json
{
  "teamId": 1,
  "title": "团队会议",
  "description": "讨论项目进度",
  "startTime": "2024-01-20T14:00:00",
  "endTime": "2024-01-20T16:00:00",
  "location": "会议室A101"
}
```

### 处理流程

1. **验证用户权限**
   - 检查用户是否是团队成员
   - 检查团队是否存在

2. **创建团队行程**
   - 插入 `team_schedule` 表
   - 设置状态为 `PENDING`

3. **添加参会人员**
   - 如果没有指定 `attendeeIds`，默认添加所有团队成员
   - 插入 `team_schedule_attendee` 表

4. **同步到个人行程**（核心功能）
   - 查询所有团队成员
   - 为每个成员创建个人行程记录
   - 设置 `type` 为 `TEAM`（团队事务）
   - 设置 `synced_team_schedule_id` 关联团队行程ID
   - 设置 `creator_id` 为成员的用户ID

5. **事务保证**
   - 使用 `@Transactional` 注解确保数据一致性
   - 如果同步失败，会记录日志但继续处理其他成员

## 前端实现

### 页面位置
`packages/web/src/pages/schedule/team/detail.vue`

### 功能点

1. **创建行程按钮**
   - 仅创建者可见（通过 `isCreator` 计算属性判断）
   - 开发者模式用户也可见

2. **创建行程表单**
   - 标题（必填）
   - 描述（选填）
   - 开始日期和时间（必填）
   - 结束日期和时间（必填）
   - 地点（选填）

3. **时间格式处理**
   - 前端格式化为 ISO 8601 格式：`YYYY-MM-DDTHH:mm:ss`
   - 后端接收并转换为 `LocalDateTime`

4. **错误处理**
   - 表单验证
   - API错误提示
   - 成功提示

## 测试步骤

### 1. 准备测试数据

```sql
-- 确保有测试用户和团队
SELECT * FROM user WHERE username IN ('zhangsan', 'lisi', 'wangwu');

-- 创建测试团队（如果不存在）
INSERT INTO team (name, description, creator_id, school_id, invite_code) 
VALUES ('测试团队', '用于测试的团队', 1, 1, 'TEST001')
ON DUPLICATE KEY UPDATE name=name;

-- 添加团队成员
INSERT INTO team_member (team_id, user_id, role) 
VALUES (1, 1, 'CREATOR'), (1, 2, 'MEMBER'), (1, 3, 'MEMBER')
ON DUPLICATE KEY UPDATE role=role;
```

### 2. 测试创建团队行程

1. 登录为团队创建者（zhangsan）
2. 进入团队详情页
3. 点击"创建行程"按钮
4. 填写行程信息：
   - 标题：测试会议
   - 开始时间：2024-01-20 14:00
   - 结束时间：2024-01-20 16:00
   - 地点：会议室A101
5. 点击"创建行程"

### 3. 验证数据保存

#### 检查团队行程表
```sql
SELECT * FROM team_schedule 
WHERE team_id = 1 
ORDER BY create_time DESC 
LIMIT 1;
```

应该看到：
- `team_id` = 1
- `title` = "测试会议"
- `creator_id` = 1（创建者ID）

#### 检查个人行程表
```sql
SELECT ps.*, u.username 
FROM personal_schedule ps
JOIN user u ON ps.creator_id = u.id
WHERE ps.synced_team_schedule_id = (
    SELECT id FROM team_schedule WHERE team_id = 1 ORDER BY create_time DESC LIMIT 1
)
ORDER BY ps.creator_id;
```

应该看到：
- 每个团队成员都有一条个人行程记录
- `type` = 'TEAM'
- `synced_team_schedule_id` = 团队行程ID
- `creator_id` = 各自的用户ID
- `title`、`start_time`、`end_time`、`location` 与团队行程一致

#### 检查参会人员表
```sql
SELECT tsa.*, u.username 
FROM team_schedule_attendee tsa
JOIN user u ON tsa.user_id = u.id
WHERE tsa.schedule_id = (
    SELECT id FROM team_schedule WHERE team_id = 1 ORDER BY create_time DESC LIMIT 1
);
```

应该看到：
- 所有团队成员都在参会人员列表中
- `status` = 'PENDING'

### 4. 验证前端显示

1. 刷新团队详情页，应该看到新创建的行程
2. 切换到个人行程页面，应该看到同步的个人行程
3. 检查行程类型是否为"团队事务"

## 日志检查

### 后端日志
查看后端控制台，应该看到：
```
已为用户 1 同步团队行程 X 到个人行程表
已为用户 2 同步团队行程 X 到个人行程表
已为用户 3 同步团队行程 X 到个人行程表
团队行程 X 已创建，并同步到 3 个成员的个人行程表
```

### 前端日志
查看浏览器控制台，应该看到：
```
📤 请求发送: { url: '/api/schedule/team/create-schedule', ... }
📥 响应接收: { code: 200, msg: '团队行程创建成功', ... }
创建成功，返回结果: { id: X, title: '测试会议', ... }
```

## 常见问题排查

### 1. 数据未保存到数据库
- 检查后端事务是否提交
- 检查数据库连接是否正常
- 查看后端日志是否有错误

### 2. 个人行程未同步
- 检查团队成员是否正确
- 检查 `synced_team_schedule_id` 字段是否正确设置
- 查看后端日志中的同步记录

### 3. 前端显示错误
- 检查API响应格式
- 检查时间格式转换
- 查看浏览器控制台错误信息

### 4. 权限问题
- 确认用户是团队成员
- 确认用户有创建权限
- 检查JWT token是否有效

## 性能考虑

- 如果团队成员很多（>100），同步操作可能较慢
- 建议添加异步处理或批量插入优化
- 可以考虑添加进度提示

## 后续优化建议

1. 添加同步进度提示
2. 支持选择性同步（指定成员）
3. 添加同步失败重试机制
4. 添加同步历史记录
5. 支持取消同步（删除个人行程）
