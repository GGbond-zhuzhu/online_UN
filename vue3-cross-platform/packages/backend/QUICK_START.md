# 后端服务快速启动指南

## 🚀 一键启动（Git Bash）

在Git Bash终端中执行：

```bash
cd vue3-cross-platform/packages/backend
mvn spring-boot:run
```

## ✅ 启动成功标志

启动成功后，您会看到：

```
百度AI云人脸识别客户端初始化成功，AppID: 121355953
Started BackendApplication in X.XXX seconds
```

## 🌐 访问服务

- **API文档**: http://localhost:8080/doc.html
- **Swagger UI**: http://localhost:8080/swagger-ui.html

## 📝 百度人脸识别配置

已配置的百度AI参数：
- AppID: 121355953
- API Key: cqqs9bZbcUedXfyE2vbdG9X2
- Secret Key: EHf2vf08Fz50mdASEXGCVMkveFeoaunG

启动时会自动初始化百度AI客户端。

## 🧪 测试人脸识别

1. 访问 http://localhost:8080/doc.html
2. 找到"06-身份认证" → "游客刷脸活体检测"
3. 准备人脸照片Base64编码
4. 填写参数测试

## ⚠️ 注意事项

- 确保MySQL服务已启动
- 确保数据库 `campus_db` 已创建
- 首次启动需要下载依赖，请耐心等待

## 🛑 停止服务

在启动服务的终端中按 `Ctrl + C`

