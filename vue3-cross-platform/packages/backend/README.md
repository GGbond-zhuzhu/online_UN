# 校园集成系统后端

## 项目简介

这是一个基于 Spring Boot 3.2.0 开发的校园集成系统后端项目，提供用户管理、二手交易、校园卡、兼职、行程管理等五大核心功能模块。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **Java版本**: JDK 17
- **数据库**: MySQL 8.0+
- **ORM框架**: MyBatis-Plus 3.5.15
- **API文档**: Knife4j 4.5.0 (基于 OpenAPI 3)
- **安全认证**: JWT (JSON Web Token)
- **密码加密**: BCrypt

## 项目结构

```
src/main/java/com/yourschool/campussystem/
├── BackendApplication.java          # 主启动类
├── common/                          # 通用类
│   ├── ApiResponse.java            # 统一响应格式
│   ├── ErrorCode.java              # 错误码定义
│   └── PageResult.java             # 分页结果
├── config/                         # 配置类
│   ├── Knife4jConfig.java          # API文档配置
│   ├── MyBatisPlusConfig.java      # MyBatis-Plus配置
│   └── SecurityConfig.java         # 安全配置
├── controller/                     # 控制器层（5个控制器）
├── dto/                           # 数据传输对象（13个DTO）
├── entity/                        # 实体类（4个实体）
├── enums/                         # 枚举类（13个枚举）
├── exception/                     # 异常处理
├── mapper/                        # 数据访问层（3个Mapper）
├── service/                       # 业务逻辑层
├── util/                          # 工具类
└── vo/                            # 视图对象（14个VO）
```

## 功能模块

### 1. 用户管理模块 (`/api/user/**`)
- 用户注册、登录
- 用户信息查询和更新
- 用户认证申请

### 2. 校园卡管理模块 (`/api/ecard/**`)
- 校园卡信息查询
- 消费记录查询
- 游客卡申请

### 3. 二手交易平台 (`/api/secondhand/**`)
- 商品发布、查询、更新
- 商品收藏、浏览统计
- 商品分类管理

### 4. 兼职管理 (`/api/parttime/**`)
- 兼职信息发布
- 兼职申请管理
- 兼职信息查询

### 5. 行程管理 (`/api/schedule/**`)
- 个人行程管理
- 团队行程管理
- 行程提醒功能

## 环境要求

- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+ 数据库
- 确保 MySQL 服务已启动

## 快速开始

### 1. 数据库配置

确保 MySQL 服务已启动，并创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS campus_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 修改数据库配置

编辑 `src/main/resources/application.properties` 文件，修改数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
```

### 3. 运行项目

#### 方式一：使用 Maven 命令

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

#### 方式二：使用 IDE

直接运行 `BackendApplication.java` 主类即可。

### 4. 访问 API 文档

项目启动成功后，访问以下地址查看 API 文档：

- **Knife4j 文档界面**: http://localhost:8080/doc.html
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## API 文档说明

项目使用 Knife4j 4.5.0 生成 API 文档，文档按模块分组：

- **00-全部接口**: 所有 API 接口
- **01-用户管理**: 用户相关接口
- **02-校园卡管理**: 校园卡相关接口
- **03-二手交易平台**: 二手商品相关接口
- **04-兼职管理**: 兼职相关接口
- **05-行程管理**: 行程相关接口

## 默认测试数据

项目启动时会自动执行 `init.sql` 初始化脚本，创建以下测试用户：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| zhangsan | 123456 | STUDENT | 学生用户 |
| lisi | 123456 | TEACHER | 教师用户 |
| wangwu | 123456 | STUDENT | 学生用户 |
| admin | 123456 | ADMIN | 管理员用户 |

## 端口配置

默认端口：**8080**

如需修改，请编辑 `application.properties` 中的 `server.port` 配置项。

## 常见问题

### 1. 数据库连接失败

- 检查 MySQL 服务是否启动
- 检查数据库用户名和密码是否正确
- 检查数据库 `campus_db` 是否已创建

### 2. 端口被占用

- 修改 `application.properties` 中的 `server.port` 为其他端口
- 或关闭占用 8080 端口的其他程序

### 3. API 文档无法访问

- 确保项目已成功启动
- 检查访问地址是否正确：http://localhost:8080/doc.html
- 检查浏览器控制台是否有错误信息

## 开发说明

### 代码规范

- 所有代码都包含详细的中文注释
- 使用统一的响应格式 `ApiResponse`
- 使用枚举类管理状态和类型
- 使用 DTO 进行数据传输，VO 进行数据展示

### 日志配置

MyBatis-Plus 的 SQL 日志已启用，可在控制台查看执行的 SQL 语句。

## 许可证

Apache 2.0

## 联系方式

如有问题，请联系开发团队：dev@yourschool.com

