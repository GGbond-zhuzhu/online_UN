# 后端服务启动指南

## 🚀 快速启动

### Windows系统（PowerShell/CMD）

```bash
cd vue3-cross-platform/packages/backend
mvn spring-boot:run
```

或者使用启动脚本：
```bash
start.bat
```

### Linux/Mac系统（Bash）

```bash
cd vue3-cross-platform/packages/backend
./start.sh
```

或者直接使用Maven：
```bash
mvn spring-boot:run
```

### Git Bash（Windows）

```bash
cd vue3-cross-platform/packages/backend
mvn spring-boot:run
```

## 📋 启动前检查

1. **Java环境**：确保已安装JDK 17或更高版本
   ```bash
   java -version
   ```

2. **Maven环境**：确保已安装Maven 3.6+
   ```bash
   mvn -version
   ```

3. **MySQL服务**：确保MySQL服务已启动
   - Windows: 检查服务管理器
   - Linux: `sudo systemctl status mysql`
   - Mac: `brew services list | grep mysql`

4. **数据库配置**：检查 `application.properties` 中的数据库连接配置

## ✅ 启动成功标志

启动成功后，您应该看到：

1. **控制台输出**：
   ```
   百度AI云人脸识别客户端初始化成功，AppID: 121355953
   Started BackendApplication in X.XXX seconds
   ```

2. **访问测试**：
   - API文档：http://localhost:8080/doc.html
   - Swagger UI：http://localhost:8080/swagger-ui.html
   - 健康检查：http://localhost:8080/actuator/health（如果配置了）

## 🔧 常见问题

### 1. 端口被占用
如果8080端口被占用，可以修改 `application.properties`：
```properties
server.port=8081
```

### 2. 数据库连接失败
- 检查MySQL服务是否启动
- 检查数据库用户名密码是否正确
- 检查数据库 `campus_db` 是否已创建

### 3. 百度AI初始化失败
- 检查 `application.properties` 中的配置是否正确
- 检查网络连接是否正常
- 查看日志中的具体错误信息

## 📝 启动日志说明

启动过程中会输出以下关键信息：
- ✅ `百度AI云人脸识别客户端初始化成功` - 百度AI配置正确
- ✅ `Started BackendApplication` - 应用启动成功
- ⚠️ `MySQL连接失败` - 需要检查数据库配置
- ⚠️ `端口被占用` - 需要修改端口或关闭占用端口的程序

## 🎯 下一步

启动成功后：
1. 访问 http://localhost:8080/doc.html 查看API文档
2. 测试登录接口获取Token
3. 测试百度人脸识别功能
4. 查看各个模块的接口说明

## 📚 相关文档

- [API文档访问指南](./API_DOC_ACCESS.md)
- [百度人脸识别测试指南](./BAIDU_FACE_TEST.md)
- [API接口文档](./API_DOCUMENTATION.md)

