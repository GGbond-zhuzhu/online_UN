# ⚠️ 测试前必读 - 重要！

## 🎯 两个必须执行的步骤

### 步骤1：更新数据库密码（必须！）

**问题**：数据库中的密码是MD5格式，代码使用BCrypt验证，不匹配会导致登录500错误。

**解决**：执行以下SQL更新密码

```sql
USE campus_db;

UPDATE `user` SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pLKO' WHERE `username` IN ('zhangsan', 'lisi', 'wangwu', 'admin');
```

**如何执行**：
- 打开MySQL客户端（命令行、Workbench、Navicat等）
- 连接到MySQL
- 复制上面的3行SQL执行
- 看到 "4 row(s) affected" 表示成功

### 步骤2：等待服务启动完成

**检查方法**：
1. 查看控制台，应该看到：`Started BackendApplication in X.XXX seconds`
2. 访问：http://localhost:8080/doc.html
3. 如果页面正常显示，说明服务已启动

**启动时间**：通常需要30-60秒

## 🧪 测试登录

### 测试账号
- 用户名：`zhangsan`
- 密码：`123456`

### 测试方法

**方法1：使用API文档（推荐）**
1. 访问：http://localhost:8080/doc.html
2. 找到"用户管理" -> "用户登录"
3. 输入测试账号
4. 点击发送

**方法2：使用curl**
```bash
curl -X POST http://localhost:8080/api/user/login -H "Content-Type: application/json" -d "{\"username\":\"zhangsan\",\"password\":\"123456\"}"
```

## ✅ 成功标志

登录成功应该返回：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    ...
  }
}
```

## ❌ 如果返回500错误

**原因**：数据库密码未更新

**解决**：执行步骤1的SQL更新密码

## 📁 相关文档

- `UPDATE_PASSWORD_SIMPLE.sql` - 最简单的SQL（3行）
- `UPDATE_PASSWORD_STEP_BY_STEP.md` - 详细步骤说明
- `完整测试指南.md` - 完整测试流程
