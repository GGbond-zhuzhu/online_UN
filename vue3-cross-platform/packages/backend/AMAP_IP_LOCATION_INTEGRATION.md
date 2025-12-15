# 高德地图IP定位接口集成说明

## ✅ 已完成的工作

### 1. 配置API凭证
- ✅ 在 `application.properties` 中添加配置：
  ```properties
  amap.ip-location.key=7694bf250eca17cf53e34154e50b9250
  amap.ip-location.secret=136ee5895726ff68c4c056c6f9a6e86e
  ```

### 2. 创建服务类
- ✅ `IpLocationService.java` - 服务接口
- ✅ `IpLocationServiceImpl.java` - 服务实现类（使用高德地图API）

### 3. 集成到登录日志
- ✅ 更新 `UserServiceImpl.recordLoginLog()` 方法
- ✅ 调用IP定位服务获取地理位置信息

---

## 🔧 配置说明

### API凭证信息
根据您提供的截图，已配置以下信息：

- **应用名称**：上大学online
- **Key名称**：point_key
- **API Key**：7694bf250eca17cf53e34154e50b9250
- **Secret Key**：136ee5895726ff68c4c056c6f9a6e86e
- **绑定服务**：Web端

### 配置文件位置
`src/main/resources/application.properties`

---

## 📋 接口功能说明

### IP定位接口
- **功能**：根据IP地址获取地理位置信息
- **输入**：IP地址（字符串）
- **输出**：地理位置信息（省份、城市）
- **API地址**：https://restapi.amap.com/v3/ip

### 返回数据格式
```json
{
  "status": "1",
  "info": "OK",
  "infocode": "10000",
  "province": "北京市",
  "city": "北京市",
  "adcode": "110000",
  "rectangle": "116.011934,39.661271;116.782984,40.216496"
}
```

---

## 🔄 调用流程

### 用户登录时IP定位流程：

```
1. 用户登录
   ↓
2. 获取客户端IP地址
   ↓
3. 判断是否为本地/内网IP
   ↓ (如果是公网IP)
4. 调用【高德地图IP定位API】获取地理位置
   ↓
5. 解析返回结果（省份、城市）
   ↓
6. 保存到登录日志表
```

---

## ⚙️ 实现细节

### 1. IP地址过滤
- **本地IP**：127.0.0.1、localhost
- **内网IP**：192.168.x.x、10.x.x.x、172.16-31.x.x
- 这些IP不会调用API，直接返回null

### 2. 签名计算
如果配置了Secret Key，会计算API签名：
- 签名算法：MD5(请求参数（按key排序）+ SecretKey)
- 参数排序：ip、key

### 3. 错误处理
- IP定位失败不影响登录流程
- 失败时记录警告日志，但不抛出异常
- 登录日志的location字段为null

---

## 🚀 使用示例

### 自动调用
IP定位功能会在用户登录时自动调用，无需手动触发。

### 登录日志记录示例

**登录日志表（user_login_log）**：
```sql
INSERT INTO user_login_log (user_id, login_ip, login_device, login_location, login_time) 
VALUES (1, '123.456.789.0', 'Mozilla/5.0...', '北京市 北京市', '2024-12-15 23:01:40');
```

---

## ⚠️ 注意事项

1. **API调用限制**：
   - 高德地图IP定位API有免费额度（每日30万次）
   - 超出后按量付费，约0.001元/次
   - 建议监控API调用次数

2. **性能优化**：
   - IP定位失败不影响登录流程
   - 使用异步调用（可选，当前为同步）
   - 可以添加缓存机制（相同IP不重复查询）

3. **隐私保护**：
   - IP地址和地理位置信息仅用于日志记录
   - 不对外暴露敏感信息
   - 符合隐私保护要求

4. **错误处理**：
   - API调用失败时，location字段为null
   - 不影响用户登录功能
   - 记录详细日志便于排查

---

## 🔍 测试建议

1. **测试公网IP定位**：
   - 使用真实公网IP登录
   - 查看登录日志中的location字段
   - 验证地理位置信息是否正确

2. **测试内网IP**：
   - 使用内网IP登录（如192.168.x.x）
   - 验证不会调用API
   - location字段应该为null

3. **测试API失败**：
   - 临时修改错误的API Key
   - 验证登录仍然成功
   - 验证location字段为null

---

## 📞 相关文档

- 高德地图开放平台：https://lbs.amap.com/
- IP定位API文档：https://lbs.amap.com/api/webservice/guide/api/ipconfig
- API签名算法：https://lbs.amap.com/api/webservice/guide/api/authentication

---

## ✅ 验证步骤

1. **启动后端服务**
   ```bash
   cd packages/backend
   mvnw.cmd spring-boot:run
   ```

2. **测试登录**
   - 使用真实IP地址登录系统
   - 查看数据库 `user_login_log` 表
   - 验证 `login_location` 字段是否有值

3. **查看日志**
   - 检查控制台日志
   - 确认IP定位API调用成功
   - 验证地理位置信息正确

---

## 🔄 后续优化建议

1. **添加缓存**：
   - 相同IP地址缓存地理位置信息
   - 减少API调用次数
   - 提高响应速度

2. **异步调用**：
   - 使用异步方式调用IP定位API
   - 不阻塞登录流程
   - 提高用户体验

3. **批量处理**：
   - 批量查询多个IP的地理位置
   - 减少API调用次数
   - 提高效率

---

**创建时间**：2024年12月  
**文档维护**：AI Assistant
