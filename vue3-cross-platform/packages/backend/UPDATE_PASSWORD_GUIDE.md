# 更新数据库密码详细指南

## 📋 操作前准备

### 需要的信息
- MySQL数据库地址：`localhost:3306`
- 数据库名称：`campus_db`
- 数据库用户名：`root`（或你的MySQL用户名）
- 数据库密码：你的MySQL密码

### 需要更新的用户
- `zhangsan` - 学生
- `lisi` - 教师
- `wangwu` - 学生
- `admin` - 管理员

**所有用户的密码都更新为：123456（BCrypt加密）**

## 🎯 方法一：使用MySQL命令行（推荐）

### 步骤1：打开命令行

**Windows**：
1. 按 `Win + R`
2. 输入 `cmd` 或 `powershell`
3. 按回车

### 步骤2：连接到MySQL

```bash
mysql -u root -p
```

然后输入你的MySQL密码。

### 步骤3：执行SQL脚本

**方式A：直接复制执行**

```sql
USE campus_db;

-- 查看更新前的状态
SELECT username, LEFT(password, 20) as password_preview FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');

-- 更新密码
UPDATE `user` 
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');

-- 验证更新结果
SELECT username, LEFT(password, 20) as password_preview FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

**方式B：使用SQL文件**

```bash
# 在MySQL命令行中执行
source C:/Users/m1346/Desktop/web_app/vue3-cross-platform/packages/backend/src/main/resources/update_user_password.sql
```

## 🎯 方法二：使用MySQL Workbench（图形界面）

### 步骤1：打开MySQL Workbench

1. 启动MySQL Workbench
2. 连接到你的MySQL服务器

### 步骤2：打开SQL文件

1. 点击菜单：`File` -> `Open SQL Script`
2. 选择文件：`packages/backend/src/main/resources/update_user_password.sql`
3. 或者直接复制SQL内容到查询窗口

### 步骤3：执行SQL

1. 点击工具栏的"执行"按钮（⚡图标）
2. 或者按快捷键：`Ctrl + Shift + Enter`

### 步骤4：查看结果

在结果窗口查看：
- 第一个查询：显示更新前的密码
- 第二个查询：显示更新后的密码（应该以 `$2a$10$` 开头）

## 🎯 方法三：使用Navicat或其他数据库工具

### 步骤1：连接到数据库

1. 打开Navicat
2. 连接到MySQL服务器
3. 选择 `campus_db` 数据库

### 步骤2：执行SQL

1. 点击"查询" -> "新建查询"
2. 复制以下SQL：

```sql
USE campus_db;

UPDATE `user` 
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

3. 点击"运行"按钮

### 步骤3：验证

执行以下查询验证：

```sql
SELECT username, LEFT(password, 20) as password_preview FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

应该看到所有密码都以 `$2a$10$` 开头。

## ✅ 验证更新是否成功

### 验证SQL

```sql
SELECT 
    username,
    nickname,
    LEFT(password, 20) as password_preview,
    CASE 
        WHEN password LIKE '$2a$10$%' THEN '✅ 正确（BCrypt格式）'
        ELSE '❌ 错误（不是BCrypt格式）'
    END as status
FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

### 预期结果

应该看到4行数据，每行的 `status` 列都显示 `✅ 正确（BCrypt格式）`，`password_preview` 列显示 `$2a$10$N.zmdr9k7uOCQb...`

## 🔄 如果更新错了怎么办？

### 回滚方案

如果更新错了，可以手动恢复：

```sql
-- 恢复为MD5密码（原始密码：123456）
UPDATE `user` 
SET `password` = 'e10adc3949ba59abbe56e057f20f883e' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

**注意**：回滚后，登录功能仍然会失败（因为代码使用BCrypt验证），所以还是需要更新为BCrypt密码。

## 📝 完整的SQL脚本（复制这个即可）

```sql
-- ========================================
-- 更新用户密码为BCrypt加密
-- 原始密码：123456
-- ========================================

USE campus_db;

-- 更新密码
UPDATE `user` 
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' 
WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');

-- 验证更新结果
SELECT 
    username,
    nickname,
    LEFT(password, 20) as password_preview,
    CASE 
        WHEN password LIKE '$2a$10$%' THEN '✅ 正确'
        ELSE '❌ 错误'
    END as status
FROM `user` 
WHERE username IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

## 🧪 更新后测试

1. 确保后端服务正在运行
2. 访问：http://localhost:8080/doc.html
3. 测试登录接口：
   - 用户名：`zhangsan`
   - 密码：`123456`
4. 应该能成功登录并返回Token

## ❓ 常见问题

### Q1: 执行SQL时提示"Table doesn't exist"

**原因**：数据库或表不存在

**解决**：
1. 确认数据库名称是 `campus_db`
2. 确认表名是 `user`（不是 `users`）
3. 可以先执行：`SHOW DATABASES;` 查看所有数据库
4. 然后执行：`USE campus_db; SHOW TABLES;` 查看所有表

### Q2: 执行SQL时提示"Access denied"

**原因**：没有权限

**解决**：
1. 确认MySQL用户名和密码正确
2. 确认用户有UPDATE权限
3. 尝试使用root用户

### Q3: 更新后验证，status还是显示"错误"

**原因**：更新可能没有成功

**解决**：
1. 检查WHERE条件是否正确
2. 确认用户名拼写正确（区分大小写）
3. 重新执行UPDATE语句
4. 查看MySQL返回的"受影响的行数"，应该是4行

### Q4: 如何确认更新成功？

**方法**：
1. 执行验证SQL，查看status列
2. 测试登录接口，应该能成功登录
3. 查看后端日志，应该没有密码验证失败的错误

## 📞 需要帮助？

如果遇到问题：
1. 查看MySQL的错误信息
2. 检查后端日志
3. 确认数据库连接正常
