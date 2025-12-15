# 百度AI云人脸识别接口集成说明

## ✅ 已完成的工作

### 1. 添加依赖
- ✅ 在 `pom.xml` 中添加百度AI云Java SDK依赖（版本4.16.8）

### 2. 配置API凭证
- ✅ 在 `application.properties` 中添加配置：
  ```properties
  baidu.ai.app-id=121355953
  baidu.ai.api-key=cqqs9bZbcUedXfyE2vbdG9X2
  baidu.ai.secret-key=EHf2vf08Fz50mdASEXGCVMkveFeoaunG
  ```

### 3. 创建服务类
- ✅ `BaiduFaceService.java` - 服务接口
- ✅ `BaiduFaceServiceImpl.java` - 服务实现类

### 4. 实现接口调用
- ✅ `detectFace()` - 人脸检测接口
- ✅ `faceLiveness()` - 在线活体检测接口
- ✅ `faceMatch()` - 人脸比对接口

### 5. 集成到认证服务
- ✅ 更新 `AuthServiceImpl.visitorFaceDetect()` 方法
- ✅ 调用百度AI云接口进行活体检测

---

## 🔧 配置说明

### API凭证信息
根据您提供的截图，已配置以下信息：

- **应用名称**：上大学online
- **AppID**：121355953
- **API Key**：cqqs9bZbcUedXfyE2vbdG9X2
- **Secret Key**：EHf2vf08Fz50mdASEXGCVMkveFeoaunG

### 配置文件位置
`src/main/resources/application.properties`

---

## 📋 接口功能说明

### 1. 人脸检测（detectFace）
- **功能**：检测图片中是否存在人脸，获取人脸位置、大小等信息
- **输入**：人脸图片Base64编码
- **输出**：人脸数量、人脸列表（包含年龄、性别、表情等信息）

### 2. 在线活体检测（faceLiveness）
- **功能**：检测用户是否为真实活体（防止照片、视频攻击）
- **输入**：人脸图片Base64编码
- **输出**：活体检测分数（0-1）、是否为活体（true/false）
- **阈值**：活体分数 > 0.8 判定为活体（可在代码中调整）

### 3. 人脸比对（faceMatch）
- **功能**：比对两张人脸照片是否为同一人
- **输入**：两张人脸图片Base64编码
- **输出**：相似度分数（0-100）、是否匹配（true/false）
- **阈值**：相似度分数 > 80.0 判定为匹配（可在代码中调整）

---

## 🔄 调用流程

### 游客活体检测完整流程：

```
1. 用户上传人脸照片（Base64编码）
   ↓
2. 调用【人脸检测】检测是否有人脸
   ↓ (如果检测到人脸)
3. 调用【在线活体检测】检测是否为活体
   ↓ (如果是活体)
4. （可选）调用【人脸比对】与身份证照片比对
   ↓
5. 返回检测结果
```

---

## 🚀 使用示例

### API调用示例

**接口**：`POST /api/auth/visitor/face-detect`

**请求参数**：
```json
{
  "faceImage": "data:image/jpeg;base64,/9j/4AAQSkZJRg...",  // Base64编码的人脸图片
  "name": "张三",
  "phone": "13800138000",
  "idCard": "110101199001011234",
  "reason": "参观校园"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "活体检测通过",
  "data": {
    "detectResult": "SUCCESS",
    "livenessScore": 0.95,
    "isAlive": true,
    "faceNum": 1,
    "message": "活体检测通过，可以申请游客临时卡",
    "timestamp": "2024-12-15T23:01:40"
  }
}
```

---

## ⚙️ 参数调整

### 活体检测阈值
在 `BaiduFaceServiceImpl.java` 中：
```java
boolean isAlive = livenessScore > 0.8; // 可调整此阈值
```

### 人脸比对阈值
在 `BaiduFaceServiceImpl.java` 中：
```java
boolean isMatch = score > 80.0; // 可调整此阈值
```

---

## ⚠️ 注意事项

1. **Base64编码格式**：
   - 图片需要转换为Base64编码
   - 如果前端传入的是 `data:image/jpeg;base64,xxx` 格式，需要去掉前缀

2. **图片大小限制**：
   - 百度AI云对图片大小有限制（通常不超过4MB）
   - 建议在上传前压缩图片

3. **错误处理**：
   - 已实现完善的错误处理机制
   - 会抛出 `BusinessException` 异常，包含详细的错误信息

4. **日志记录**：
   - 所有接口调用都会记录日志
   - 便于排查问题和监控调用情况

---

## 🔍 测试建议

1. **测试人脸检测**：
   - 上传包含人脸的图片
   - 上传不包含人脸的图片（应该返回错误）

2. **测试活体检测**：
   - 上传真人照片（应该通过）
   - 上传打印的照片（应该不通过）

3. **测试人脸比对**：
   - 上传同一人的两张照片（应该匹配）
   - 上传不同人的照片（应该不匹配）

---

## 📞 相关文档

- 百度AI云人脸识别官方文档：https://ai.baidu.com/ai-doc/FACE/yk37c1u4t
- Java SDK文档：https://ai.baidu.com/ai-doc/FACE/yk37c1u4t#java-sdk
- API接口文档：https://ai.baidu.com/ai-doc/FACE/yk37c1u4t#api

---

## ✅ 验证步骤

1. **启动后端服务**
   ```bash
   cd packages/backend
   mvnw.cmd spring-boot:run
   ```

2. **检查配置**
   - 确认 `application.properties` 中的API凭证正确
   - 确认百度AI云控制台中已开通相关接口权限

3. **测试接口**
   - 使用Postman或Knife4j测试 `/api/auth/visitor/face-detect` 接口
   - 查看日志确认接口调用成功

---

**创建时间**：2024年12月  
**文档维护**：AI Assistant
