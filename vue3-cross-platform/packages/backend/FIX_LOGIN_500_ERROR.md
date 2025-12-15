# 修复登录500错误

## 问题分析

登录时返回500错误，可能的原因：

1. **密码加密方式不匹配** ✅ 已修复
   - 数据库中的密码是MD5加密
   - 代码使用BCrypt验证
   - **解决方案**：已更新init.sql使用BCrypt密码

2. **枚举类型转换问题**
   - UserRoleEnum可能无法正确从数据库字符串转换

3. **数据库连接问题**
   - MySQL服务未启动
   - 数据库配置错误

4. **其他运行时异常**
   - 需要查看后端日志

## 修复步骤

### 步骤1：更新数据库密码（如果数据库已存在）

如果数据库已经存在并且有旧数据，需要执行以下SQL更新密码：

```sql
USE campus_db;

-- 更新测试用户的密码为BCrypt加密（原始密码：123456）
UPDATE `user` SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

或者执行文件：`src/main/resources/update_password.sql`

### 步骤2：重新启动服务

1. 停止当前运行的服务
2. 重新启动服务：
   ```powershell
   cd packages\backend
   .\mvnw.cmd spring-boot:run
   ```

### 步骤3：测试登录

使用以下账号测试：
- 用户名：`zhangsan`
- 密码：`123456`

## 验证修复

### 方法1：使用Knife4j文档
1. 访问 http://localhost:8080/doc.html
2. 找到"用户管理" -> "用户登录"
3. 输入测试账号
4. 应该返回200状态码和Token

### 方法2：使用curl
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"zhangsan\",\"password\":\"123456\"}"
```

## 如果仍然失败

### 检查后端日志
查看控制台输出的错误信息，可能包括：
- 数据库连接错误
- SQL执行错误
- 枚举转换错误
- 其他运行时异常

### 检查数据库
1. 确认MySQL服务已启动
2. 确认数据库`campus_db`已创建
3. 确认用户表存在
4. 确认密码字段已更新为BCrypt格式

### 检查配置
确认`application.properties`中的数据库配置正确：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_db
spring.datasource.username=root
spring.datasource.password=your_password
```

## 测试账号

所有测试账号的密码都是：**123456**

| 用户名 | 角色 | 说明 |
|--------|------|------|
| zhangsan | STUDENT | 学生 |
| lisi | TEACHER | 教师 |
| wangwu | STUDENT | 学生 |
| admin | ADMIN | 管理员 |
