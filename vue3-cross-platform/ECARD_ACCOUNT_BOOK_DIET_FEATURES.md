# E卡通记账本和饮食表功能说明

## 功能概述

本次更新为E卡通模块添加了两个核心功能：
1. **记账本功能** - 自动导入E卡通消费，手动添加日常消费，生成周报分析
2. **饮食表功能** - 自动导入食堂消费，手动记录饮食，支持自定义背景颜色

## 已完成的工作

### 1. 后端开发

#### 数据库表结构
- ✅ `account_book` - 记账本表
- ✅ `diet_record` - 饮食记录表
- SQL脚本位置：`packages/backend/src/main/resources/account_book_diet_record.sql`

#### 后端接口
- ✅ AccountBookController - 记账本相关接口
  - POST `/api/account-book/add` - 添加记账记录
  - DELETE `/api/account-book/{id}` - 删除记录
  - PUT `/api/account-book/{id}` - 更新记录
  - GET `/api/account-book/list` - 查询记录列表
  - GET `/api/account-book/weekly-report` - 获取周报
  - POST `/api/account-book/auto-import` - 自动导入E卡通消费
  - GET `/api/account-book/statistics` - 获取统计信息

- ✅ DietRecordController - 饮食记录相关接口
  - POST `/api/diet-record/add` - 添加饮食记录
  - DELETE `/api/diet-record/{id}` - 删除记录
  - PUT `/api/diet-record/{id}` - 更新记录
  - GET `/api/diet-record/list` - 查询记录列表
  - GET `/api/diet-record/by-date` - 获取某天的记录
  - POST `/api/diet-record/auto-import` - 自动导入食堂消费
  - GET `/api/diet-record/statistics` - 获取统计信息

#### 自动导入功能
- ✅ E卡通消费时自动导入到记账本
- ✅ E卡通食堂消费时自动导入到饮食表
- ✅ 自动判断餐次（早餐/午餐/晚餐/加餐）
- ✅ 自动设置分类和背景颜色

### 2. 前端开发

#### 页面路由配置
已在 `pages.json` 中添加以下页面：
- `/pages/ecard/account-book/index` - 记账本主页
- `/pages/ecard/account-book/add/index` - 添加记账记录
- `/pages/ecard/account-book/report/index` - 周报分析
- `/pages/ecard/diet-record/index` - 饮食表主页
- `/pages/ecard/diet-record/add/index` - 添加饮食记录
- `/pages/ecard/diet-record/detail/index` - 饮食记录详情

#### 记账本功能页面
- ✅ **记账本主页** (`account-book/index.vue`)
  - 本月消费统计展示
  - 快捷操作：添加记录、导入E卡通、周报分析
  - 消费分类统计（餐饮、交通、购物、娱乐、学习、其他）
  - 消费记录列表，支持分类筛选
  - 自动导入标记显示

- ✅ **添加记账页面** (`account-book/add/index.vue`)
  - 金额输入（支持快捷金额）
  - 分类选择（6大分类，带图标和颜色）
  - 消费描述、地点、支付方式
  - 日期选择器

- ✅ **周报分析页面** (`account-book/report/index.vue`)
  - 周选择器（可切换不同周）
  - 周报概览（总消费、交易次数、日均消费）
  - 消费分类占比（柱状图）
  - 消费地点TOP5
  - 每日消费趋势（柱状图）

#### 饮食表功能页面
- ✅ **饮食表主页** (`diet-record/index.vue`)
  - 日期选择器（可切换日期）
  - 快捷操作：添加饮食、导入食堂消费
  - 按餐次分类展示（早餐、午餐、晚餐、加餐）
  - 每个餐次使用不同背景颜色
  - 显示食物名称、详情、地点、价格

- ✅ **添加饮食页面** (`diet-record/add/index.vue`)
  - 餐次选择（早餐/午餐/晚餐/加餐）
  - 食物名称和详情输入
  - 来源选择（食堂/外卖/餐厅/家里/其他）
  - 用餐地点、价格、卡路里
  - 背景颜色选择器（15种颜色可选）
  - 日期选择器

- ✅ **饮食详情页面** (`diet-record/detail/index.vue`)
  - 显示完整的饮食信息
  - 使用记录的自定义背景颜色
  - 编辑和删除功能

### 3. API接口封装

- ✅ `packages/common/src/api/account-book/index.ts` - 记账本API
- ✅ `packages/common/src/api/diet-record/index.ts` - 饮食记录API
- ✅ 已在 `packages/common/src/api/index.ts` 中导出

### 4. E卡通页面集成

- ✅ 在快捷功能网格中添加"记账本"和"饮食表"入口
- ✅ 修复所有页面跳转路径（添加 `/index` 后缀）
- ✅ 实现页面间的导航逻辑

## 功能特点

### 记账本功能
1. **自动导入**：E卡通消费时自动同步到记账本
2. **手动添加**：支持手动添加各种日常消费
3. **分类管理**：6大消费分类（餐饮、交通、购物、娱乐、学习、其他）
4. **周报分析**：
   - 总消费金额和交易次数
   - 分类占比分析
   - 消费地点TOP5
   - 每日消费趋势图

### 饮食表功能
1. **自动导入**：食堂消费时自动导入，并根据时间判断餐次
2. **手动记录**：支持记录外卖、餐厅、家里等各种来源的饮食
3. **餐次分类**：早餐、午餐、晚餐、加餐
4. **个性化**：
   - 15种背景颜色可选
   - 支持记录卡路里和价格
   - 详细的食物描述

## 设计参考

参考了以下主流App的设计：
- **记账本**：参考了"随手记"、"薄荷记账"等App的设计
- **饮食表**：参考了"薄荷健康"、"Keep"等App的饮食记录功能
- **周报分析**：参考了"支付宝账单"、"微信支付"的统计报表设计

## 使用说明

### 记账本使用流程
1. 进入E卡通页面，点击"记账本"快捷入口
2. 查看本月消费统计和分类占比
3. 点击"添加记录"手动添加消费
4. 点击"导入E卡通"同步E卡通消费记录
5. 点击"周报分析"查看详细的消费分析报告

### 饮食表使用流程
1. 进入E卡通页面，点击"饮食表"快捷入口
2. 选择日期查看当天的饮食记录
3. 食堂消费会自动导入（根据时间判断餐次）
4. 点击"添加饮食"手动记录外卖、餐厅等饮食
5. 可以为每条记录选择背景颜色
6. 点击记录查看详情，可编辑或删除

## 数据库初始化

执行以下SQL脚本创建表：
```sql
-- 位置：packages/backend/src/main/resources/account_book_diet_record.sql
```

## 注意事项

1. **自动导入**：E卡通消费时会自动导入，无需手动操作
2. **去重机制**：通过 `ecard_record_id` 字段避免重复导入
3. **餐次判断**：
   - 6:00-10:00 → 早餐
   - 11:00-14:00 → 午餐
   - 17:00-21:00 → 晚餐
   - 其他时间 → 加餐
4. **错误处理**：自动导入失败不影响E卡通消费主流程

## 后续优化建议

1. 添加月报、年报功能
2. 添加预算设置和超支提醒
3. 添加饮食营养分析
4. 添加消费趋势预测
5. 添加数据导出功能（Excel、PDF）

