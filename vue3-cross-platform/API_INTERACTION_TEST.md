# 前后端交互测试指南

## 测试时间
2024-01-20

## 测试目的
验证前后端能否正常交互，包括API调用、数据格式、权限验证等。

---

## 1. 环境准备

### 1.1 启动后端服务
```bash
cd packages/backend
mvn spring-boot:run
# 或使用IDE运行 BackendApplication.java
```

**验证：**
- 访问 http://localhost:8080/doc.html 应该能看到API文档
- 后端日志显示服务启动成功

### 1.2 启动前端服务
```bash
cd packages/web
npm run dev
# 或
pnpm dev
```

**验证：**
- 访问 http://localhost:5173 应该能看到前端页面
- 浏览器控制台没有严重错误

---

## 2. 基础连接测试

### 2.1 检查代理配置

**测试步骤：**
1. 打开浏览器开发者工具（F12）
2. 切换到 Network 标签
3. 访问前端页面
4. 查看API请求

**预期结果：**
- 请求URL应该是 `/api/xxx`（相对路径）
- 实际请求应该发送到 `http://localhost:8080/api/xxx`
- 请求状态码应该是 200 或 401（未登录）

### 2.2 检查CORS配置

**测试步骤：**
1. 在浏览器控制台执行：
```javascript
fetch('http://localhost:8080/api/secondhand/list?page=1&pageSize=10')
  .then(res => res.json())
  .then(data => console.log('✅ CORS正常:', data))
  .catch(err => console.error('❌ CORS错误:', err))
```

**预期结果：**
- 不应该出现CORS错误
- 应该能正常返回数据或401错误

---

## 3. API接口测试

### 3.1 测试商品列表接口

**前端调用：**
```typescript
// packages/web/src/pages/secondhand/index.vue
const result = await getGoodsList({ page: 1, pageSize: 10 })
```

**后端接口：**
```
GET /api/secondhand/list?page=1&pageSize=10
```

**测试步骤：**
1. 打开二手交易页面
2. 查看浏览器控制台
3. 检查Network请求

**预期结果：**
- ✅ 请求成功（200状态码）
- ✅ 返回数据格式正确
- ✅ 前端能正确解析数据
- ✅ 商品列表正常显示

**验证点：**
- [ ] 请求路径正确：`/api/secondhand/list`
- [ ] 响应格式：`{ code: 200, msg: "查询成功", data: { list: [...], total: X, page: 1, size: 10 } }`
- [ ] 前端正确提取 `data.list` 或 `data.records`
- [ ] 商品列表正常渲染

### 3.2 测试兼职列表接口

**前端调用：**
```typescript
// packages/web/src/pages/parttime/index.vue
const result = await getParttimeList({ page: 1, pageSize: 10 })
```

**后端接口：**
```
GET /api/parttime/list?page=1&pageSize=10
```

**测试步骤：**
1. 打开兼职页面
2. 查看浏览器控制台
3. 检查Network请求

**预期结果：**
- ✅ 请求成功（200状态码）
- ✅ 返回数据格式正确
- ✅ 前端能正确解析数据
- ✅ 兼职列表正常显示

### 3.3 测试商品详情接口

**前端调用：**
```typescript
// packages/common/src/api/secondhand/index.ts
const detail = await getGoodsDetail(1)
```

**后端接口：**
```
GET /api/secondhand/detail/1
```

**测试步骤：**
1. 在商品列表中点击某个商品
2. 查看浏览器控制台
3. 检查Network请求

**预期结果：**
- ✅ 请求路径正确：`/api/secondhand/detail/1`
- ✅ 返回商品详情数据
- ✅ 详情页正常显示

### 3.4 测试登录接口

**前端调用：**
```typescript
// packages/common/src/api/auth/index.ts
const result = await login({ username: 'zhangsan', password: '123456' })
```

**后端接口：**
```
POST /api/user/login
Body: { "username": "zhangsan", "password": "123456" }
```

**测试步骤：**
1. 打开登录页面
2. 输入用户名和密码（zhangsan / 123456）
3. 点击登录
4. 查看浏览器控制台和Network

**预期结果：**
- ✅ 请求成功（200状态码）
- ✅ 返回token和用户信息
- ✅ token保存到localStorage
- ✅ 后续请求自动携带token

---

## 4. 数据格式验证

### 4.1 商品列表响应格式

**后端返回：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "华为MateBook 14 2022款",
        "price": 4500.00,
        "category": "ELECTRONICS",
        ...
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

**前端处理：**
```typescript
// request.ts 响应拦截器提取 data 字段
// API函数适配：list -> records, page -> current
{
  records: [...],  // 从 list 转换而来
  total: 10,
  current: 1,      // 从 page 转换而来
  size: 10
}
```

**验证：**
- [ ] 后端返回格式正确
- [ ] 前端正确提取data字段
- [ ] 字段名正确转换（list -> records）

### 4.2 兼职列表响应格式

**验证方式：** 同商品列表

---

## 5. 权限验证测试

### 5.1 游客访问列表接口

**测试步骤：**
1. 清除localStorage中的token（或使用无痕模式）
2. 访问二手交易页面
3. 检查商品列表是否正常加载

**预期结果：**
- ✅ 列表接口可以正常访问（不需要登录）
- ✅ 返回商品列表数据
- ✅ 前端正常显示

### 5.2 登录后访问

**测试步骤：**
1. 登录账号
2. 访问二手交易页面
3. 检查是否显示更多信息（如同校商品）

**预期结果：**
- ✅ 列表正常加载
- ✅ 可能显示更多数据（同校筛选）

### 5.3 需要登录的接口

**测试接口：**
- `POST /api/secondhand/publish` - 发布商品
- `GET /api/secondhand/my-goods` - 我的商品
- `POST /api/parttime/publish` - 发布兼职

**测试步骤：**
1. 未登录状态下尝试访问
2. 检查是否返回401错误
3. 登录后再次访问
4. 检查是否正常

**预期结果：**
- ✅ 未登录：返回401或权限错误
- ✅ 已登录：正常访问

---

## 6. 错误处理测试

### 6.1 网络错误

**测试步骤：**
1. 停止后端服务
2. 前端尝试调用API
3. 检查错误处理

**预期结果：**
- ✅ 前端显示友好的错误提示
- ✅ 控制台输出错误信息
- ✅ 页面不崩溃

### 6.2 业务错误

**测试步骤：**
1. 使用错误的参数调用API
2. 检查错误处理

**预期结果：**
- ✅ 后端返回错误信息
- ✅ 前端正确显示错误提示

---

## 7. 常见问题排查

### 7.1 请求失败（404）

**可能原因：**
- API路径不匹配
- 后端服务未启动
- 代理配置错误

**排查步骤：**
1. 检查浏览器Network标签，查看实际请求URL
2. 检查后端Controller路径
3. 检查Vite代理配置

### 7.2 请求失败（CORS错误）

**可能原因：**
- CORS配置不正确
- 请求头不匹配

**排查步骤：**
1. 检查后端CORS配置
2. 检查请求头设置
3. 确认使用代理而不是直接请求

### 7.3 数据格式错误

**可能原因：**
- 响应格式不匹配
- 字段名不一致

**排查步骤：**
1. 查看后端实际返回格式
2. 检查前端API函数适配逻辑
3. 检查响应拦截器处理

### 7.4 权限错误（401）

**可能原因：**
- Token未设置
- Token过期
- 接口需要登录但未登录

**排查步骤：**
1. 检查localStorage中是否有token
2. 检查请求头是否携带token
3. 检查拦截器排除路径

---

## 8. 测试检查清单

### 8.1 基础配置
- [x] 前端请求配置正确
- [x] Vite代理配置正确
- [x] 后端CORS配置正确
- [x] 后端拦截器配置正确

### 8.2 API路径
- [x] 商品列表：`GET /api/secondhand/list` ✅
- [x] 商品详情：`GET /api/secondhand/detail/{id}` ✅
- [x] 兼职列表：`GET /api/parttime/list` ✅
- [x] 兼职详情：`GET /api/parttime/detail/{id}` ✅
- [x] 更新商品：`PUT /api/secondhand/update/{id}` ✅
- [x] 删除商品：`DELETE /api/secondhand/delete/{id}` ✅
- [x] 更新兼职：`PUT /api/parttime/update/{id}` ✅
- [x] 删除兼职：`DELETE /api/parttime/delete/{id}` ✅

### 8.3 数据格式
- [x] 响应格式匹配
- [x] 分页格式适配
- [x] 字段名转换正确

### 8.4 功能测试
- [ ] 商品列表正常加载
- [ ] 兼职列表正常加载
- [ ] 筛选功能正常
- [ ] 搜索功能正常
- [ ] 登录功能正常
- [ ] Token自动添加
- [ ] 错误处理正常

---

## 9. 快速测试命令

### 9.1 使用curl测试

```bash
# 测试商品列表（不需要登录）
curl http://localhost:8080/api/secondhand/list?page=1&pageSize=10

# 测试兼职列表（不需要登录）
curl http://localhost:8080/api/parttime/list?page=1&pageSize=10

# 测试登录
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456"}'

# 测试商品详情（不需要登录）
curl http://localhost:8080/api/secondhand/detail/1
```

### 9.2 使用浏览器控制台测试

```javascript
// 测试商品列表
fetch('/api/secondhand/list?page=1&pageSize=10')
  .then(res => res.json())
  .then(data => console.log('商品列表:', data))
  .catch(err => console.error('错误:', err))

// 测试登录
fetch('/api/user/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'zhangsan', password: '123456' })
})
  .then(res => res.json())
  .then(data => {
    console.log('登录成功:', data)
    if (data.data && data.data.token) {
      localStorage.setItem('token', data.data.token)
      console.log('Token已保存')
    }
  })
  .catch(err => console.error('登录失败:', err))
```

---

## 10. 总结

### 10.1 已修复的问题
- ✅ API路径不匹配（详情、更新、删除接口）
- ✅ 分页响应格式不匹配
- ✅ 数据格式适配

### 10.2 配置状态
- ✅ 前端请求配置：正确
- ✅ Vite代理配置：正确
- ✅ 后端CORS配置：正确
- ✅ 后端拦截器配置：正确
- ✅ API路径匹配：已修复
- ✅ 数据格式匹配：已适配

### 10.3 测试建议
1. **先测试基础连接**：确保前后端服务都正常运行
2. **测试列表接口**：验证数据加载和格式转换
3. **测试登录功能**：验证token保存和自动添加
4. **测试详情接口**：验证路径修复是否生效
5. **测试错误处理**：验证各种错误情况的处理

### 10.4 下一步
1. 实际运行测试，验证所有功能
2. 根据测试结果进一步优化
3. 完善错误处理和用户提示
