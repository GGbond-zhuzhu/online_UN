# API集成完成说明

## ✅ 已完成的工作

### 1. 请求封装 (`@your-org/common/src/utils/request.ts`)

- ✅ 创建了统一的axios实例
- ✅ 配置了请求拦截器（自动添加JWT token）
- ✅ 配置了响应拦截器（统一处理响应格式和错误）
- ✅ 支持开发环境请求日志
- ✅ 自动处理401未授权（清除token并跳转登录）

### 2. 认证工具 (`@your-org/common/src/utils/auth.ts`)

- ✅ Token存储和获取（支持sessionStorage和localStorage）
- ✅ 用户信息存储和获取
- ✅ 清除认证信息

### 3. API模块

已创建完整的API调用文件：

#### 认证模块 (`api/auth/index.ts`)
- ✅ `login()` - 用户登录
- ✅ `register()` - 用户注册
- ✅ `getUserInfo()` - 获取用户信息
- ✅ `updateUserInfo()` - 更新用户信息
- ✅ `changePassword()` - 修改密码
- ✅ `logout()` - 退出登录

#### 校园卡模块 (`api/ecard/index.ts`)
- ✅ `getEcardInfo()` - 获取校园卡信息
- ✅ `getConsumeRecords()` - 获取消费记录
- ✅ `applyVisitorCard()` - 申请游客卡
- ✅ `reportLoss()` - 挂失校园卡
- ✅ `unblockCard()` - 解挂校园卡
- ✅ `recharge()` - 充值校园卡

#### 二手交易模块 (`api/secondhand/index.ts`)
- ✅ `getGoodsList()` - 获取商品列表
- ✅ `getGoodsDetail()` - 获取商品详情
- ✅ `publishGoods()` - 发布商品
- ✅ `updateGoods()` - 更新商品
- ✅ `deleteGoods()` - 删除商品
- ✅ `collectGoods()` - 收藏商品
- ✅ `uncollectGoods()` - 取消收藏
- ✅ `getMyGoods()` - 获取我的发布
- ✅ `getMyCollections()` - 获取我的收藏

#### 兼职模块 (`api/parttime/index.ts`)
- ✅ `getParttimeList()` - 获取兼职列表
- ✅ `getParttimeDetail()` - 获取兼职详情
- ✅ `publishParttime()` - 发布兼职
- ✅ `updateParttime()` - 更新兼职
- ✅ `deleteParttime()` - 删除兼职
- ✅ `applyParttime()` - 报名兼职
- ✅ `cancelApply()` - 取消报名
- ✅ `getMyParttime()` - 获取我的发布
- ✅ `getMyApplications()` - 获取我的报名

#### 行程管理模块 (`api/schedule/index.ts`)
- ✅ `getPersonalSchedules()` - 获取个人行程列表
- ✅ `getPersonalScheduleDetail()` - 获取个人行程详情
- ✅ `createPersonalSchedule()` - 创建个人行程
- ✅ `updatePersonalSchedule()` - 更新个人行程
- ✅ `deletePersonalSchedule()` - 删除个人行程
- ✅ `getTeams()` - 获取团队列表
- ✅ `createTeam()` - 创建团队
- ✅ `getTeamDetail()` - 获取团队详情
- ✅ `inviteTeamMembers()` - 邀请团队成员
- ✅ `getTeamSchedules()` - 获取团队行程列表
- ✅ `createTeamSchedule()` - 创建团队行程
- ✅ `syncTeamScheduleToPersonal()` - 同步团队行程到个人
- ✅ `getCalendarData()` - 获取日历视图数据

### 4. Vite配置 (`vite.config.ts`)

- ✅ 配置了API代理（`/api` → `http://localhost:8080/api`）
- ✅ 配置了Knife4j文档代理
- ✅ 配置了环境变量默认值

### 5. 主入口 (`src/main.ts`)

- ✅ 导入请求工具初始化axios实例
- ✅ 添加了启动日志（显示API地址和文档地址）

### 6. 环境变量类型定义 (`src/env.d.ts`)

- ✅ 定义了环境变量类型
- ✅ 支持 `VITE_API_BASE_URL`、`VITE_API_DOCS_URL`、`VITE_APP_TITLE`

## 📝 使用示例

### 在组件中使用API

```vue
<script setup lang="ts">
import { ref } from 'vue'
import { login, getUserInfo } from '@your-org/common/api'
import { setToken } from '@your-org/common/utils/auth'

// 登录示例
const handleLogin = async () => {
  try {
    const result = await login({
      username: 'admin',
      password: '123456'
    })
    
    // 保存token
    setToken(result.token, true) // true表示记住登录
    
    // 保存用户信息
    setUserInfo(result.userInfo, true)
    
    console.log('登录成功:', result)
  } catch (error) {
    console.error('登录失败:', error)
  }
}

// 获取用户信息示例
const userInfo = ref(null)
const loadUserInfo = async () => {
  try {
    userInfo.value = await getUserInfo()
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}
</script>
```

### 在页面中使用API

```typescript
// pages/index/index.vue
import { onMounted, ref } from 'vue'
import { getGoodsList } from '@your-org/common/api'

const goodsList = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const result = await getGoodsList({
      page: 1,
      pageSize: 10,
      keyword: '手机'
    })
    goodsList.value = result.records
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
})
```

## 🔗 后端API文档

访问 Knife4j 文档查看所有接口详情：

- 开发环境: http://localhost:8080/doc.html
- 接口路径: `/api/**`

## 📚 相关文档

- [快速开始](./QUICK_START.md)
- [部署指南](./DEPLOYMENT.md)
- [环境变量配置](./ENV_SETUP.md)
- [项目README](./README.md)

## 🎯 下一步

1. ✅ API集成已完成
2. 📝 在页面组件中调用API
3. 🎨 完善UI界面
4. 🧪 测试API连接
5. 🚀 部署到生产环境

