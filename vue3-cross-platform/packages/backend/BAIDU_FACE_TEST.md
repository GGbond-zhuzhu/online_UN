# 百度人脸识别功能测试指南

## 📋 配置信息

已配置的百度AI云人脸识别参数：
- **AppID**: 121355953
- **API Key**: cqqs9bZbcUedXfyE2vbdG9X2
- **Secret Key**: EHf2vf08Fz50mdASEXGCVMkveFeoaunG

## 🚀 启动后端服务

1. **进入后端目录**
   ```bash
   cd vue3-cross-platform/packages/backend
   ```

2. **启动服务**
   ```bash
   mvn spring-boot:run
   ```

3. **检查启动日志**
   启动成功后，应该看到类似以下日志：
   ```
   百度AI云人脸识别客户端初始化成功，AppID: 121355953
   ```

## 🧪 测试接口

### 1. 游客刷脸活体检测

**接口**: `POST /api/auth/visitor/face-detect`

**请求参数**:
- `faceImage`: 人脸照片（Base64编码）
- `name`: 姓名
- `phone`: 手机号
- `idCard`: 身份证号
- `reason`: 进校事由

**测试步骤**:
1. 访问 http://localhost:8080/doc.html
2. 找到"06-身份认证"分组
3. 找到"游客刷脸活体检测"接口
4. 准备一张人脸照片，转换为Base64编码
5. 填写参数并发送请求

**Base64编码转换**:
- 在线工具：https://base64.guru/converter/encode/image
- 或使用Python：
  ```python
  import base64
  with open("face.jpg", "rb") as f:
      base64_str = base64.b64encode(f.read()).decode()
  ```

### 2. 人脸检测（直接调用服务）

**接口**: `POST /api/auth/visitor/face-detect`

该接口内部会调用以下百度AI服务：
- **人脸检测**: `detectFace()` - 检测图片中是否有人脸
- **活体检测**: `faceLiveness()` - 检测是否为真人（非照片/视频）

## 📝 功能说明

### BaiduFaceService 提供的三个方法：

1. **detectFace(String imageBase64)**
   - 功能：人脸检测
   - 返回：检测到的人脸数量、人脸信息（年龄、性别、表情等）

2. **faceLiveness(String imageBase64)**
   - 功能：在线活体检测
   - 返回：活体分数（0-1），是否通过活体检测
   - 阈值：活体分数 > 0.8 才通过

3. **faceMatch(String image1Base64, String image2Base64)**
   - 功能：人脸比对
   - 返回：相似度分数（0-100），是否匹配
   - 阈值：相似度 > 80.0 才匹配

## 🔍 测试示例

### 使用curl测试

```bash
# 1. 先获取Token（登录）
curl -X POST "http://localhost:8080/api/user/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'

# 2. 使用Token测试人脸检测（需要先登录获取Token）
curl -X POST "http://localhost:8080/api/auth/visitor/face-detect" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "faceImage=BASE64_ENCODED_IMAGE&name=张三&phone=13800138000&idCard=110101199001011234&reason=参观"
```

### 使用Postman测试

1. 导入API文档：访问 http://localhost:8080/v3/api-docs
2. 在Postman中导入OpenAPI文档
3. 设置环境变量：`base_url=http://localhost:8080`
4. 先调用登录接口获取Token
5. 设置Authorization为Bearer Token
6. 调用人脸检测接口

## ⚠️ 注意事项

1. **图片格式**: 支持JPG、PNG格式，Base64编码
2. **图片大小**: 建议小于2MB
3. **人脸要求**: 
   - 人脸清晰可见
   - 正面或侧面（角度不超过45度）
   - 光线充足
   - 无遮挡（眼镜、口罩等可能影响识别）

4. **活体检测**: 
   - 必须使用真人照片
   - 照片/视频/屏幕截图无法通过活体检测
   - 活体分数阈值：0.8（可在代码中调整）

5. **错误处理**:
   - 如果检测失败，会返回具体的错误信息
   - 常见错误：
     - `未检测到人脸` - 图片中没有人脸或人脸不清晰
     - `活体检测未通过` - 活体分数低于阈值
     - `人脸检测失败` - API调用失败，检查网络和配置

## 🔧 调试技巧

1. **查看日志**: 启动时会输出初始化日志
2. **检查配置**: 确认application.properties中的配置正确
3. **测试连接**: 可以先测试简单的接口，确认服务正常启动
4. **Base64编码**: 确保图片Base64编码正确（不包含data:image前缀）

## 📚 相关文档

- [百度AI开放平台](https://ai.baidu.com/)
- [人脸识别API文档](https://ai.baidu.com/ai-doc/FACE/yk37c1u4t)
- [API接口文档](./API_DOCUMENTATION.md)

