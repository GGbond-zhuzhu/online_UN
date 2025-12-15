# E卡通页面定位监控功能实现

## ✅ 功能说明

已实现定位监控功能，根据用户定位位置和认证状态自动决定显示哪个界面。

---

## 🎯 功能逻辑

### 1. 定位检测流程

页面加载时，按以下流程执行：

```
1. 页面加载
   ↓
2. 检测浏览器定位权限
   ↓
3. 获取用户当前位置（经纬度）
   ↓
4. 调用后端API检查是否在校内
   ↓
5. 根据定位结果和认证状态决定显示界面
```

### 2. 界面显示规则

| 定位状态 | 认证状态 | 显示界面 |
|---------|---------|---------|
| ✅ 在校内 | ✅ 已认证 | 主界面（余额卡片、功能卡片、付款码等） |
| ✅ 在校内 | ❌ 未认证 | 认证表单（身份认证） |
| ❌ 不在校内 | ✅/❌ 任意 | 临时卡申请界面 |

### 3. 定位检测状态

- **定位检测中**：显示加载提示
- **定位成功**：根据结果显示对应界面
- **定位失败**：默认显示临时卡申请界面

---

## 🔧 技术实现

### 1. 状态管理

```typescript
// 页面状态
const showMainInterface = ref(false)      // 主界面
const showGuestInterface = ref(false)     // 临时卡申请界面
const showAuthForm = ref(false)           // 认证表单
const isLocationChecking = ref(true)      // 定位检测中
const isInCampus = ref(false)             // 是否在校内
```

### 2. 定位检测函数

```typescript
const checkUserLocation = async (): Promise<boolean> => {
  // 1. 获取浏览器定位
  // 2. 调用后端API检查是否在校内
  // 3. 根据结果设置界面状态
  // 4. 返回是否在校内
}
```

### 3. 界面切换逻辑

#### 在校内 + 已认证
```typescript
if (result.isInCampus && isAuthenticated.value) {
  showMainInterface.value = true
  // 加载校园卡数据
  await loadEcardInfo()
}
```

#### 在校内 + 未认证
```typescript
if (result.isInCampus && !isAuthenticated.value) {
  showAuthForm.value = true
  // 显示认证表单
}
```

#### 不在校内
```typescript
if (!result.isInCampus) {
  showGuestInterface.value = true
  // 显示临时卡申请界面
}
```

---

## 📱 用户体验

### 1. 定位检测中

- 显示加载提示："正在检测定位..."
- 提示用户允许浏览器获取位置信息

### 2. 定位成功

- 自动切换到对应界面
- 无需用户手动操作

### 3. 定位失败

- 默认显示临时卡申请界面
- 用户仍可申请临时卡使用

---

## 🔄 认证流程

### 1. 在校内未认证用户

1. 页面加载 → 检测定位 → 在校内
2. 显示认证表单
3. 用户填写信息并提交
4. 认证成功 → 显示主界面

### 2. 不在校内用户

1. 页面加载 → 检测定位 → 不在校内
2. 直接显示临时卡申请界面
3. 用户申请临时卡后可使用

---

## 🛠️ API调用

### 定位检查API

```typescript
// 调用后端API检查定位
const result = await checkLocation(longitude, latitude)

// 返回结果
{
  isInCampus: boolean,        // 是否在校内
  campusName: string,         // 校区名称
  distanceToCampus: number,   // 距离校区距离
  message: string            // 提示信息
}
```

---

## 📋 测试场景

### 场景1：在校内已认证用户
- ✅ 应该显示主界面
- ✅ 显示余额卡片
- ✅ 显示付款码
- ✅ 显示交易记录

### 场景2：在校内未认证用户
- ✅ 应该显示认证表单
- ✅ 填写信息后可认证
- ✅ 认证成功后显示主界面

### 场景3：不在校内用户
- ✅ 应该显示临时卡申请界面
- ✅ 可以申请临时卡
- ✅ 申请成功后显示临时卡功能

### 场景4：定位失败
- ✅ 应该显示临时卡申请界面
- ✅ 用户仍可使用临时卡功能

---

## 🐛 错误处理

### 1. 浏览器不支持定位
- 显示临时卡申请界面
- 提示："不支持定位"

### 2. 用户拒绝定位权限
- 显示临时卡申请界面
- 提示："定位失败"

### 3. 定位API调用失败
- 显示临时卡申请界面
- 提示："定位失败"

### 4. 定位超时（10秒）
- 显示临时卡申请界面
- 提示："定位失败"

---

## ✨ 优化建议

1. **定位缓存**：缓存定位结果，避免频繁请求
2. **定位精度**：根据场景调整定位精度要求
3. **离线支持**：支持离线模式下的界面显示
4. **定位提示**：更友好的定位权限请求提示

---

## 📝 相关文件

- `packages/web/src/pages/ecard/index.vue` - E卡通页面
- `packages/common/src/api/ecard/index.ts` - E卡通API
- `ECARD_PAGE_REDESIGN.md` - E卡通页面设计文档

---

**完成时间**: 2024年12月  
**功能状态**: ✅ 已完成
