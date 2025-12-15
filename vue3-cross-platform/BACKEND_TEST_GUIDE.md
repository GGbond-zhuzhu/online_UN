# 后端测试指南

## 📋 测试前准备

### 1. 环境要求
- ✅ Java 17+
- ✅ Maven 3.6+
- ✅ MySQL 5.7+ 或 8.0+
- ✅ 数据库已创建（campus_db）

### 2. 数据库配置
确保MySQL服务已启动，并配置好数据库连接：

```properties
# application.properties 或环境变量
DB_URL=jdbc:mysql://localhost:3306/campus_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
DB_USERNAME=root
DB_PASSWORD=your_password
```

### 3. 数据库初始化
项目启动时会自动执行 `init.sql` 初始化数据库表结构和测试数据。

## 🚀 启动后端服务

### 方式一：使用Maven命令（推荐）

```bash
# 进入后端目录
cd packages/backend

# 启动服务
mvn spring-boot:run

# 或者先编译再运行
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 方式二：使用IDE
1. 在IDE中打开 `BackendApplication.java`
2. 右键选择 "Run" 或 "Debug"

### 方式三：使用Maven Wrapper（Windows）
```bash
cd packages/backend
.\mvnw.cmd spring-boot:run
```

## ✅ 验证服务启动

### 1. 检查启动日志
服务启动成功后，应该看到：
```
Started BackendApplication in X.XXX seconds
```

### 2. 访问API文档
- **Knife4j文档**: http://localhost:8080/doc.html
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### 3. 健康检查
访问：http://localhost:8080/actuator/health（如果配置了Actuator）

## 🧪 API测试

### 测试工具推荐
1. **Knife4j文档**（推荐）- 内置测试功能
2. **Postman** - 专业API测试工具
3. **curl** - 命令行工具
4. **前端页面** - 直接在前端测试

### 测试流程

#### 1. 用户注册和登录

**注册用户**
```http
POST http://localhost:8080/api/user/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456",
  "confirmPassword": "123456",
  "nickname": "测试用户",
  "role": "STUDENT",
  "schoolId": 1
}
```

**用户登录**
```http
POST http://localhost:8080/api/user/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "123456"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "username": "testuser",
    "role": "STUDENT",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200
  }
}
```

**保存Token**：后续请求需要在Header中添加：
```
Authorization: Bearer {token}
```

#### 2. 校园卡功能测试

**获取校园卡信息**
```http
GET http://localhost:8080/api/ecard/info
Authorization: Bearer {token}
```

**校园卡消费**
```http
POST http://localhost:8080/api/ecard/consume
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 15.50,
  "merchantId": "MERCHANT_001",
  "merchantName": "第一食堂",
  "consumeType": "CANTEEN",
  "description": "午餐",
  "longitude": 116.397128,
  "latitude": 39.916527
}
```

**查询消费记录**
```http
GET http://localhost:8080/api/ecard/consume-records?page=1&size=10
Authorization: Bearer {token}
```

**获取今日统计**
```http
GET http://localhost:8080/api/ecard/today-statistics
Authorization: Bearer {token}
```

#### 3. 二手交易功能测试

**发布商品**
```http
POST http://localhost:8080/api/secondhand/publish
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "测试商品",
  "description": "这是一个测试商品",
  "price": 100.00,
  "originalPrice": 150.00,
  "category": "ELECTRONICS",
  "contactPhone": "13800138000",
  "location": "图书馆一楼"
}
```

**查询商品列表**
```http
GET http://localhost:8080/api/secondhand/list?page=1&size=10
Authorization: Bearer {token}
```

**获取商品详情**
```http
GET http://localhost:8080/api/secondhand/detail/1
Authorization: Bearer {token}
```

#### 4. 兼职功能测试

**发布兼职**
```http
POST http://localhost:8080/api/parttime/publish
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "校园图书馆助理",
  "description": "协助图书馆管理员整理书籍",
  "salaryPerHour": 20.00,
  "recruitCount": 5,
  "workStartTime": "2024-03-01T09:00:00",
  "workEndTime": "2024-06-30T18:00:00",
  "location": "图书馆三楼",
  "requirements": "有责任心",
  "contactName": "李老师",
  "contactPhone": "13800138000"
}
```

**查询兼职列表**
```http
GET http://localhost:8080/api/parttime/list?page=1&size=10
Authorization: Bearer {token}
```

## 🔍 测试检查清单

### 基础功能
- [ ] 服务能正常启动
- [ ] API文档能正常访问
- [ ] 用户注册功能
- [ ] 用户登录功能
- [ ] Token验证功能

### 校园卡功能
- [ ] 获取校园卡信息
- [ ] 校园卡消费
- [ ] 查询消费记录
- [ ] 定位校验
- [ ] 今日统计
- [ ] 动态码生成

### 二手交易功能
- [ ] 发布商品
- [ ] 查询商品列表
- [ ] 获取商品详情
- [ ] 收藏商品
- [ ] 我的商品管理

### 兼职功能
- [ ] 发布兼职
- [ ] 查询兼职列表
- [ ] 报名兼职
- [ ] 我的报名记录

### 权限验证
- [ ] 未登录访问需要权限的接口应返回401
- [ ] 同校用户可见联系方式
- [ ] 发布者权限验证

## 🐛 常见问题排查

### 1. 服务启动失败

**问题**：端口被占用
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID {PID} /F

# Linux/Mac
lsof -i :8080
kill -9 {PID}
```

**问题**：数据库连接失败
- 检查MySQL服务是否启动
- 检查数据库配置是否正确
- 检查数据库是否存在

### 2. 接口返回401

**原因**：Token无效或过期
- 重新登录获取新Token
- 检查请求头格式：`Authorization: Bearer {token}`

### 3. 接口返回500

**原因**：服务器内部错误
- 查看后端日志
- 检查数据库表结构是否正确
- 检查Service实现是否有bug

### 4. 数据库初始化失败

**解决**：
1. 手动执行 `init.sql` 创建表结构
2. 修改 `application.properties`：
   ```properties
   spring.sql.init.mode=never
   ```

## 📊 测试数据

项目初始化时会自动插入以下测试数据：

### 测试用户
- 用户名：`zhangsan`，密码：`123456`，角色：`STUDENT`
- 用户名：`lisi`，密码：`123456`，角色：`TEACHER`
- 用户名：`wangwu`，密码：`123456`，角色：`STUDENT`
- 用户名：`admin`，密码：`123456`，角色：`ADMIN`

### 测试高校
- 清华大学（ID: 1）
- 北京大学（ID: 2）

## 🎯 下一步

1. **完善其他Controller**：继续完善SecondhandController、ParttimeController等
2. **添加单元测试**：为核心业务逻辑编写单元测试
3. **性能测试**：使用JMeter等工具进行压力测试
4. **集成测试**：测试前后端联调

## 📝 测试报告模板

测试完成后，建议记录：
- 测试时间
- 测试环境
- 测试结果
- 发现的问题
- 修复情况
