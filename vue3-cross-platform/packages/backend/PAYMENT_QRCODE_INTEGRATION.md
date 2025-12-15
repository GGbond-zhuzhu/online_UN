# 支付二维码功能集成说明

## ✅ 已完成的工作

### 1. 添加依赖
- ✅ 在 `pom.xml` 中添加ZXing二维码生成库（版本3.5.2）

### 2. 创建支付服务
- ✅ `PaymentService.java` - 支付服务接口
- ✅ `PaymentServiceImpl.java` - 支付服务实现类（使用模拟二维码）

### 3. 更新商户服务
- ✅ 更新 `MerchantService` 接口，添加二维码刷新和状态查询方法
- ✅ 更新 `MerchantServiceImpl.payDeposit()` 方法，返回支付二维码
- ✅ 实现 `refreshPaymentQrCode()` 方法
- ✅ 实现 `queryPaymentStatus()` 方法

### 4. 更新Controller接口
- ✅ 更新 `MerchantController.payDeposit()` 接口
- ✅ 添加 `refreshPaymentQrCode()` 接口
- ✅ 添加 `queryPaymentStatus()` 接口

---

## 🔧 功能说明

### 1. 创建支付订单并生成二维码
- **接口**：`POST /api/merchant/deposit/pay`
- **功能**：创建支付订单，生成支付二维码（Base64编码）
- **返回**：二维码图片URL、订单号、过期时间等

### 2. 刷新支付二维码
- **接口**：`POST /api/merchant/deposit/pay/{orderId}/refresh`
- **功能**：刷新支付订单的二维码（如果二维码过期或需要重新生成）
- **返回**：新的二维码图片URL

### 3. 查询支付状态
- **接口**：`GET /api/merchant/deposit/pay/{orderId}/status`
- **功能**：查询支付订单的支付状态
- **返回**：订单状态、是否过期等信息

---

## 📋 二维码说明

### 二维码内容格式
当前使用**模拟的无效二维码**，二维码内容为：
- **支付宝**：`http://{server}/mock/alipay/pay?orderId={orderId}&amount={amount}&timestamp={timestamp}`
- **微信支付**：`http://{server}/mock/wechat/pay?orderId={orderId}&amount={amount}&timestamp={timestamp}`

**注意**：这些URL是无效的，仅用于测试和演示。实际对接支付接口时，需要替换为真实的支付链接。

### 二维码格式
- **图片格式**：PNG
- **编码方式**：Base64
- **返回格式**：`data:image/png;base64,{base64编码的图片数据}`
- **尺寸**：300x300像素

---

## 🔄 支付流程

### 完整支付流程：

```
1. 商户提交保证金支付请求
   ↓
2. 系统创建支付订单（状态：PENDING）
   ↓
3. 生成支付二维码（Base64编码）
   ↓
4. 返回二维码给前端显示
   ↓
5. 用户扫描二维码（当前为无效二维码）
   ↓
6. （可选）刷新二维码（如果过期）
   ↓
7. （可选）查询支付状态
   ↓
8. 等待支付回调（实际对接时需要）
   ↓
9. 更新订单状态为PAID
```

---

## 🚀 使用示例

### 1. 创建支付订单

**接口**：`POST /api/merchant/deposit/pay`

**请求参数**：
```
amount: 1000.00
payMethod: ALIPAY 或 WECHAT
```

**响应示例**：
```json
{
  "code": 200,
  "message": "支付订单创建成功，请扫描二维码完成支付",
  "data": {
    "orderId": "DEPOSIT_1702656000000_abc12345",
    "amount": 1000.00,
    "payMethod": "ALIPAY",
    "status": "PENDING",
    "qrCodeUrl": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
    "qrCodeContent": "http://localhost:8080/mock/alipay/pay?orderId=...",
    "expireTime": 1702657800000,
    "message": "请使用支付宝扫描二维码完成支付",
    "createTime": "2024-12-15T23:01:40"
  }
}
```

### 2. 刷新支付二维码

**接口**：`POST /api/merchant/deposit/pay/{orderId}/refresh`

**响应示例**：
```json
{
  "code": 200,
  "message": "二维码刷新成功",
  "data": {
    "orderId": "DEPOSIT_1702656000000_abc12345",
    "qrCodeUrl": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...",
    "qrCodeContent": "http://localhost:8080/mock/alipay/pay?orderId=...",
    "expireTime": 1702657800000,
    "message": "二维码已刷新，请重新扫描"
  }
}
```

### 3. 查询支付状态

**接口**：`GET /api/merchant/deposit/pay/{orderId}/status`

**响应示例**：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "orderId": "DEPOSIT_1702656000000_abc12345",
    "status": "PENDING",
    "amount": 1000.00,
    "payMethod": "ALIPAY",
    "createTime": "2024-12-15T23:01:40",
    "payTime": null,
    "isExpired": false,
    "expireTime": 1702657800000
  }
}
```

---

## ⚙️ 实现细节

### 1. 订单号生成
- 格式：`DEPOSIT_{timestamp}_{uuid前8位}`
- 示例：`DEPOSIT_1702656000000_abc12345`
- 确保唯一性

### 2. 二维码过期时间
- 默认过期时间：30分钟
- 过期后无法刷新，需要重新创建订单

### 3. 订单状态管理
- **PENDING**：等待支付
- **PAID**：已支付
- **FAILED**：支付失败
- **REFUNDED**：已退款

### 4. 订单缓存
- 使用内存缓存（ConcurrentHashMap）存储订单信息
- 实际生产环境建议使用Redis

---

## ⚠️ 注意事项

1. **模拟二维码**：
   - 当前二维码内容是无效的，仅用于测试
   - 实际对接时需要替换为真实的支付链接

2. **订单过期**：
   - 二维码30分钟后过期
   - 过期后需要重新创建订单

3. **状态管理**：
   - 订单创建时状态为PENDING
   - 实际对接支付接口后，需要通过回调更新状态

4. **并发安全**：
   - 使用ConcurrentHashMap保证线程安全
   - 生产环境建议使用Redis

---

## 🔄 后续对接真实支付接口

当需要对接真实的支付宝/微信支付接口时：

1. **修改二维码内容**：
   - 支付宝：调用支付宝统一下单接口，获取支付链接
   - 微信支付：调用微信统一下单接口，获取支付二维码

2. **实现支付回调**：
   - 支付宝回调：`POST /api/payment/alipay/notify`
   - 微信回调：`POST /api/payment/wechat/notify`
   - 更新订单状态为PAID

3. **更新PaymentServiceImpl**：
   - 替换 `generateMockQrCodeContent()` 方法
   - 调用真实的支付API
   - 处理支付回调

---

## 📞 相关文档

- ZXing二维码生成库：https://github.com/zxing/zxing
- 支付宝开放平台：https://open.alipay.com/
- 微信支付商户平台：https://pay.weixin.qq.com/

---

## ✅ 验证步骤

1. **启动后端服务**
   ```bash
   cd packages/backend
   mvnw.cmd spring-boot:run
   ```

2. **测试创建支付订单**
   - 使用Postman或Knife4j测试 `/api/merchant/deposit/pay` 接口
   - 传入金额和支付方式
   - 查看返回的二维码URL

3. **测试刷新二维码**
   - 使用返回的orderId调用刷新接口
   - 验证返回新的二维码

4. **测试查询状态**
   - 使用orderId查询支付状态
   - 验证返回的状态信息

---

**创建时间**：2024年12月  
**文档维护**：AI Assistant
