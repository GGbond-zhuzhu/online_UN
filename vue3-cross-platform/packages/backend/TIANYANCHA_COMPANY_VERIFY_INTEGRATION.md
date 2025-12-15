# 天眼查企业信息查询接口集成说明

## ✅ 已完成的工作

### 1. 配置API凭证
- ✅ 在 `application.properties` 中添加配置：
  ```properties
  tianyancha.api.token=1cb0c52c-5f6e-4d43-8cfd-fe5960f60bb4
  tianyancha.api.url=https://open.api.tianyancha.com/services/v3/open/baseInfoV3
  ```

### 2. 创建服务类
- ✅ `CompanyVerifyService.java` - 服务接口
- ✅ `CompanyVerifyServiceImpl.java` - 服务实现类（使用天眼查API）

### 3. 集成到商户服务
- ✅ 更新 `MerchantServiceImpl.verifyCompany()` 方法
- ✅ 调用企业信息查询服务进行资格校验

---

## 🔧 配置说明

### API凭证信息
根据您提供的截图，已配置以下信息：

- **接口ID**：1116
- **接口名称**：企业基本信息
- **Token**：1cb0c52c-5f6e-4d43-8cfd-fe5960f60bb4
- **价格**：0.15元/次
- **计费方式**：按次计费
- **开通状态**：已生效
- **开始日期**：2025-12-15
- **到期日期**：2028-12-15
- **每分次数限制**：1000次
- **最新版本**：3.0

### 配置文件位置
`src/main/resources/application.properties`

---

## 📋 接口功能说明

### 企业基本信息查询接口
- **功能**：根据统一社会信用代码查询企业基本信息
- **输入**：
  - `keyword` - 统一社会信用代码或企业名称
- **输出**：
  - `isValid` - 是否有效
  - `companyName` - 企业名称
  - `creditCode` - 统一社会信用代码
  - `legalPerson` - 法人姓名
  - `registeredCapital` - 注册资本
  - `establishDate` - 成立日期
  - `status` - 企业状态
  - `companyType` - 企业类型
  - `address` - 注册地址
  - `scope` - 经营范围

### API调用方式
- **请求方法**：GET
- **请求URL**：`https://open.api.tianyancha.com/services/v3/open/baseInfoV3?keyword={统一社会信用代码}`
- **请求头**：
  - `Authorization: {token}`
  - `Content-Type: application/json`

---

## 🔄 调用流程

### 商户资格校验完整流程：

```
1. 商户提交入驻申请（包含统一社会信用代码和公司名称）
   ↓
2. 调用【企业基本信息查询API】查询企业信息
   ↓
3. 验证统一社会信用代码是否匹配
   ↓
4. 验证公司名称是否匹配（允许部分匹配）
   ↓
5. 返回企业详细信息
   ↓
6. 保存到商户申请表
```

---

## 🚀 使用示例

### API调用示例

**接口**：`POST /api/merchant/verify-company`

**请求参数**：
```json
{
  "creditCode": "91110000123456789X",
  "companyName": "北京某某科技有限公司"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "公司资格校验完成",
  "data": {
    "isValid": true,
    "companyName": "北京某某科技有限公司",
    "creditCode": "91110000123456789X",
    "legalPerson": "张三",
    "registeredCapital": "1000万元",
    "establishDate": "2020-01-01",
    "status": "存续",
    "companyType": "有限责任公司",
    "address": "北京市朝阳区xxx",
    "scope": "技术开发、技术服务...",
    "verifyTime": "2024-12-15T23:01:40",
    "message": "公司资格校验通过"
  }
}
```

---

## ⚙️ 实现细节

### 1. 统一社会信用代码验证
- 严格匹配：查询返回的统一社会信用代码必须与输入完全一致
- 不匹配时抛出异常

### 2. 公司名称验证
- 部分匹配：允许查询返回的公司名称包含输入的公司名称
- 不完全匹配时记录警告日志，但不阻止验证

### 3. 错误处理
- API调用失败时抛出 `BusinessException`
- 包含详细的错误信息
- 记录完整的调用日志

### 4. 模拟数据支持
- 如果未配置API Token，返回模拟数据
- 便于开发和测试

---

## ⚠️ 注意事项

1. **API调用限制**：
   - 每分次数限制：1000次
   - 价格：0.15元/次
   - 建议添加调用频率限制，避免超限

2. **费用控制**：
   - 监控API调用次数
   - 设置合理的调用频率
   - 避免重复查询相同企业

3. **数据准确性**：
   - 统一社会信用代码必须准确
   - 公司名称建议使用全称
   - 验证结果仅供参考，最终审核由人工完成

4. **错误处理**：
   - API调用失败不影响商户申请流程
   - 记录详细日志便于排查
   - 提供友好的错误提示

---

## 🔍 测试建议

1. **测试有效企业**：
   - 使用真实的统一社会信用代码
   - 验证返回的企业信息是否正确
   - 验证公司名称匹配逻辑

2. **测试无效企业**：
   - 使用不存在的统一社会信用代码
   - 验证错误处理是否正确
   - 验证错误信息是否友好

3. **测试API失败**：
   - 临时修改错误的Token
   - 验证错误处理机制
   - 验证是否返回模拟数据

---

## 📞 相关文档

- 天眼查开放平台：https://open.tianyancha.com/
- API接口文档：https://open.tianyancha.com/services/v3/open/baseInfoV3
- 接口测试：可在天眼查控制台进行接口测试

---

## ✅ 验证步骤

1. **启动后端服务**
   ```bash
   cd packages/backend
   mvnw.cmd spring-boot:run
   ```

2. **测试接口**
   - 使用Postman或Knife4j测试 `/api/merchant/verify-company` 接口
   - 传入真实的统一社会信用代码和公司名称
   - 查看返回的企业信息

3. **查看日志**
   - 检查控制台日志
   - 确认API调用成功
   - 验证返回数据正确

---

## 🔄 后续优化建议

1. **添加缓存**：
   - 相同统一社会信用代码缓存查询结果
   - 减少API调用次数
   - 提高响应速度

2. **批量查询**：
   - 支持批量查询多个企业信息
   - 减少API调用次数
   - 提高效率

3. **异步调用**：
   - 使用异步方式调用API
   - 不阻塞商户申请流程
   - 提高用户体验

---

**创建时间**：2024年12月  
**文档维护**：AI Assistant
