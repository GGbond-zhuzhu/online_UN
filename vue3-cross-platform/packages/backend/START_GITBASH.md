# Git Bash 启动后端服务指南

## 🚀 启动命令

在Git Bash中，使用以下命令启动后端服务：

```bash
cd vue3-cross-platform/packages/backend
mvn spring-boot:run
```

## 📋 完整启动流程

### 1. 打开Git Bash终端

### 2. 进入项目目录
```bash
cd ~/Desktop/web_app/vue3-cross-platform/packages/backend
```

### 3. 检查环境
```bash
# 检查Java版本（需要JDK 17+）
java -version

# 检查Maven版本
mvn -version
```

### 4. 启动服务
```bash
mvn spring-boot:run
```

## ✅ 启动成功标志

启动成功后，您会看到类似以下输出：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

百度AI云人脸识别客户端初始化成功，AppID: 121355953
Started BackendApplication in X.XXX seconds
```

## 🌐 访问服务

启动成功后，在浏览器中访问：

- **API文档（Knife4j）**: http://localhost:8080/doc.html
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔍 检查服务状态

在另一个Git Bash窗口中，可以使用以下命令检查服务是否运行：

```bash
# 检查8080端口是否被占用
netstat -ano | grep 8080

# 或者使用curl测试（如果安装了curl）
curl http://localhost:8080/doc.html
```

## ⚠️ 常见问题

### 1. 端口被占用
如果8080端口被占用，可以：
- 关闭占用端口的程序
- 或修改 `application.properties` 中的端口：
  ```properties
  server.port=8081
  ```

### 2. Maven下载依赖慢
可以配置国内镜像，编辑 `~/.m2/settings.xml`：
```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

### 3. 数据库连接失败
确保：
- MySQL服务已启动
- 数据库 `campus_db` 已创建
- `application.properties` 中的数据库配置正确

## 🎯 测试百度人脸识别

服务启动后，可以测试百度人脸识别功能：

1. 访问 http://localhost:8080/doc.html
2. 找到"06-身份认证"分组
3. 找到"游客刷脸活体检测"接口
4. 准备一张人脸照片，转换为Base64编码
5. 填写参数并测试

详细测试步骤请参考：[BAIDU_FACE_TEST.md](./BAIDU_FACE_TEST.md)

## 📝 停止服务

在启动服务的终端中，按 `Ctrl + C` 停止服务。

## 💡 提示

- 首次启动可能需要下载依赖，请耐心等待
- 启动过程中会看到很多日志输出，这是正常的
- 如果看到"Started BackendApplication"，说明启动成功
- 如果启动失败，查看错误日志中的具体信息

