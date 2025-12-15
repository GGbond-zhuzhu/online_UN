# 后端快速测试指南

## 🚀 快速启动

### Windows PowerShell
```powershell
# 进入后端目录
cd packages\backend

# 启动服务
mvnw.cmd spring-boot:run
```

### Windows CMD
```cmd
cd packages\backend
mvnw.cmd spring-boot:run
```

### Linux/Mac
```bash
cd packages/backend
./mvnw spring-boot:run
```

## ✅ 验证启动成功

1. **查看控制台输出**，应该看到：
   ```
   Started BackendApplication in X.XXX seconds
   ```

2. **访问API文档**：
   - http://localhost:8080/doc.html （Knife4j文档，推荐）
   - http://localhost:8080/swagger-ui.html （Swagger UI）

3. **测试健康检查**（如果配置了）：
   - http://localhost:8080/actuator/health

## 🧪 快速测试API

### 1. 使用Knife4j文档测试（最简单）

1. 访问 http://localhost:8080/doc.html
2. 找到"用户管理" -> "用户登录"
3. 使用测试账号登录：
   - 用户名：`zhangsan`
   - 密码：`123456`
4. 复制返回的Token
5. 点击右上角"Authorize"按钮，输入：`Bearer {token}`
6. 现在可以测试其他需要登录的接口了

### 2. 使用curl测试

**登录获取Token**
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"zhangsan\",\"password\":\"123456\"}"
```

**使用Token访问接口**
```bash
curl -X GET http://localhost:8080/api/ecard/info \
  -H "Authorization: Bearer {your_token}"
```

## 📋 测试检查清单

### 基础功能
- [ ] 服务启动成功
- [ ] API文档可访问
- [ ] 用户登录成功
- [ ] Token验证正常

### 核心功能
- [ ] 获取校园卡信息
- [ ] 查询消费记录
- [ ] 发布二手商品
- [ ] 查询商品列表
- [ ] 发布兼职
- [ ] 查询兼职列表

## 🐛 常见问题

### 端口被占用
```powershell
# 查找占用8080端口的进程
netstat -ano | findstr :8080

# 结束进程（替换PID为实际进程ID）
taskkill /PID {PID} /F
```

### 数据库连接失败
1. 确保MySQL服务已启动
2. 检查 `application.properties` 中的数据库配置
3. 确保数据库 `campus_db` 已创建

### 编译错误
```powershell
# 清理并重新编译
mvnw.cmd clean compile
```

## 📝 测试数据

项目启动时会自动创建以下测试数据：

**测试用户**（密码都是：123456）
- `zhangsan` - 学生
- `lisi` - 教师
- `wangwu` - 学生
- `admin` - 管理员

**测试高校**
- 清华大学（ID: 1）
- 北京大学（ID: 2）

## 🎯 下一步

测试通过后，可以：
1. 继续完善其他Controller
2. 添加更多测试用例
3. 进行前后端联调测试
