# 修复登录500错误 - 详细说明

## 问题描述

使用测试账号（zhangsan/123456）登录时，返回500服务器内部错误。

## 可能的原因

1. **枚举类型转换问题** ✅ 已修复
   - 数据库中的role字段是字符串（如"STUDENT"）
   - MyBatis-Plus需要将字符串转换为UserRoleEnum枚举
   - **解决方案**：已添加`UserRoleEnumTypeHandler`自定义类型处理器

2. **数据库连接问题**
   - MySQL服务未启动
   - 数据库配置错误

3. **密码验证问题**
   - 数据库中的密码格式不正确
   - BCrypt密码验证失败

4. **其他运行时异常**
   - 需要查看后端控制台日志

## 已实施的修复

### 1. 添加枚举类型处理器

创建了 `UserRoleEnumTypeHandler.java`，用于正确处理数据库字符串到枚举的转换。

### 2. 更新User实体类

在User实体类的role字段上添加了`typeHandler`注解：
```java
@TableField(value = "role", typeHandler = UserRoleEnumTypeHandler.class)
private UserRoleEnum role;
```

### 3. 改进错误处理

在登录方法中添加了更详细的错误信息输出，方便调试。

## 修复步骤

### 步骤1：重新编译并启动后端

```powershell
cd vue3-cross-platform\packages\backend
mvn clean compile
mvn spring-boot:run
```

### 步骤2：检查数据库中的用户数据

确保数据库中的用户数据正确：

```sql
USE campus_db;

-- 查看用户数据
SELECT id, username, role, password FROM user WHERE username = 'zhangsan';

-- 确认role字段的值（应该是'STUDENT'、'TEACHER'、'ADMIN'等）
-- 确认password字段是BCrypt加密的（以$2a$开头）
```

### 步骤3：测试登录

使用以下方式测试登录：

**方式1：使用API文档**
1. 访问 http://localhost:8080/doc.html
2. 找到"01-用户管理" → "用户登录"
3. 输入：
   ```json
   {
     "username": "zhangsan",
     "password": "123456"
   }
   ```
4. 点击"发送请求"

**方式2：使用curl**
```powershell
$body = @{username='zhangsan';password='123456'} | ConvertTo-Json
Invoke-WebRequest -Uri http://localhost:8080/api/user/login -Method POST -Body $body -ContentType 'application/json' -UseBasicParsing
```

**方式3：在前端页面测试**
1. 访问 http://localhost:5173/login
2. 输入用户名：zhangsan
3. 输入密码：123456
4. 点击登录

### 步骤4：查看后端日志

如果仍然失败，查看后端控制台输出的详细错误信息：
- 查找包含"登录过程发生异常"的日志
- 查看完整的异常堆栈信息
- 根据错误信息进一步排查

## 如果仍然失败

### 检查1：数据库连接

```powershell
# 检查MySQL服务是否运行
Get-Service | Where-Object {$_.Name -like "*mysql*"}

# 测试数据库连接
mysql -u root -p -e "USE campus_db; SELECT COUNT(*) FROM user;"
```

### 检查2：数据库中的密码格式

确保数据库中的密码是BCrypt加密格式（以`$2a$`开头）：

```sql
SELECT username, password FROM user WHERE username = 'zhangsan';
```

如果密码不是BCrypt格式，需要更新：

```sql
-- 更新密码为BCrypt加密（原始密码：123456）
UPDATE user SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE username = 'zhangsan';
```

### 检查3：重新初始化数据库

如果数据库数据有问题，可以重新初始化：

```sql
-- 删除数据库（谨慎操作！）
DROP DATABASE IF EXISTS campus_db;

-- 重新创建数据库
CREATE DATABASE campus_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后重启后端服务，会自动执行`init.sql`初始化脚本。

## 验证修复

登录成功后，应该返回：

```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "zhangsan",
    "role": "STUDENT",
    "expiresIn": 7200
  }
}
```

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| zhangsan | 123456 | STUDENT |
| lisi | 123456 | TEACHER |
| wangwu | 123456 | STUDENT |
| admin | 123456 | ADMIN |

## 相关文件

- `UserRoleEnumTypeHandler.java` - 枚举类型处理器
- `User.java` - 用户实体类（已更新）
- `UserServiceImpl.java` - 用户服务实现（已改进错误处理）
- `init.sql` - 数据库初始化脚本

