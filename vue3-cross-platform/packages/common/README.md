# common 子包（双端公共核心）

## 1. 目录结构说明（对新手友好）

> 这个 common 包可以理解为一个「前端公共小仓库」，web 端和 app 端都来这里拿接口、工具和类型。

- **`src/api`**：所有与后端交互的接口封装
  - `secondhand/index.ts`：二手交易相关接口（`getGoodsList` / `getGoodsDetail` 等）
  - `parttime/index.ts`：兼职招聘相关接口（`getParttimeList` / `getParttimeDetail` 等）
  - `ecard/index.ts`：校园卡相关接口
  - `auth/index.ts`：登录认证相关接口
  - `schedule/index.ts`：行程/课程表相关接口
  - `common/index.ts`：一些通用接口

- **`src/utils`**：通用工具函数（与具体页面无强耦合）
  - `request.ts`：基于 Axios 的请求封装，统一处理 token、错误提示等
  - `storage.ts`：跨平台本地存储封装（兼容 Web localStorage 和 UniApp storage）
  - `auth.ts`：token 与用户信息的存取工具
  - `location.ts`：经纬度计算、范围判断等纯函数
  - `format.ts`：**格式化与“猜你喜欢”推荐相关工具函数**（价格格式化/时间格式化/随机推荐等）

- **`src/types`**：接口返回/业务实体的 TypeScript 类型
  - `auth.ts` / `ecard.ts` / `secondhand.ts` / `parttime.ts` / `schedule.ts`：按模块拆分
  - `index.ts`：统一出口，业务代码只需要从 `@campus/common` 引入

- **`src/pinia`**：跨端共享的 Pinia 状态（用户、二手、兼职、校园卡、行程等）
  - 用于在 web/app 中保持数据结构与接口调用一致

- **`src/hooks`**：常用逻辑封装为组合式函数（Composition API Hooks）
  - `useRequest`：二次封装请求逻辑，统一 loading / 错误处理
  - `useAuth` / `useEcard` / `useLocation` / `useScheduleTeam` / `useWeather` 等

- **`src/config`**：公共配置
  - `role.ts`：角色枚举与角色说明
  - `campus.ts`：校园列表、默认校园、定位半径等
  - `index.ts`：统一导出配置

- **`src/components`**：可复用的 Vue 组件（卡片、布局、无权限提示等），按业务与通用分类

- **`src/styles`**：公共样式、变量和重置样式（SCSS）

- **`src/index.ts`**：**整个 common 包的总出口**
  - 对外统一导出 `api` / `utils` / `types` / `config` / `pinia` / `hooks` 等
  - 业务侧只需要：`import { getGoodsList } from '@campus/common'`

---

## 2. 接口返回规范

- 所有 HTTP 接口尽量遵循统一格式：`{ code, msg, data }`
- `code === 200` 视为成功，`data` 为真实业务数据
- `code === 401` 表示未登录或登录已过期，前端会自动清理 token 并跳转到登录页
- 其他 code 统一在控制台打印错误，业务层可以按需增加 UI 提示

Axios 封装在 `src/utils/request.ts` 中，已经统一处理：

- 请求头自动携带 `Authorization: Bearer <token>`
- 针对 401 状态自动清除本地 token，并跳转登录页（兼容 web / UniApp）
- 在开发环境打印详细的请求和响应日志，方便排查问题

---

## 3. “猜你喜欢/推荐列表”使用说明（配合二手 & 兼职页面）

二手和兼职页面只需要关心两件事：

1. **从后端拿到一整页列表数据**（例如通过 `getGoodsList` 或 `getParttimeList`）
2. **在前端从这批数据里随机挑出一小部分作为推荐列表**

### 3.1 获取完整列表

- 二手：`getGoodsList(params)` → 返回包含 `list/records/total/page/size` 的对象
- 兼职：`getParttimeList(params)` → 同样返回统一结构

### 3.2 使用工具函数做随机推荐

在 `src/utils/format.ts` 中已经提供了两个与推荐强相关的工具：

- **`pickRandomItems(list, count)`**：从数组中随机抽取若干条，不会修改原数组
- **`getRandomRecommendList(list, defaultCount)`**：在上一个函数基础上的封装，适合直接用于“猜你喜欢”场景

> 伪代码示例（web/app 端页面里）：
>
> ```ts
> import { getGoodsList, getRandomRecommendList } from '@campus/common'
>
> const allGoods = await getGoodsList({ page: 1, pageSize: 100 })
> const recommendGoods = getRandomRecommendList(allGoods.list, 8) // 随机推荐 8 条
> ```

这样一来：

- 业务页面只负责展示 UI，不需要重复写随机逻辑
- 若以后要改变推荐策略（例如根据浏览记录、收藏记录加权），只需要在 `format.ts` / 其他工具文件里升级实现

---

## 4. 维护规则（约定）

1. 所有通用代码尽量写成**纯函数**或**无副作用的工具/Hook**，方便在 web/app/SSR 中复用；
2. app/web 端不要各自复制粘贴公共逻辑，而是统一从 `@campus/common` 引入；
3. 新增接口时：
   - 先在 `src/types` 中定义好类型
   - 然后在 `src/api/xxx/index.ts` 中封装请求
   - 最后确认 `src/api/index.ts` 与 `src/index.ts` 已正确导出；
4. 接口返回格式必须符合 `{ code, msg, data }` 规范，并在 `request.ts` 中统一做错误处理与 token 校验；
5. 若有与“猜你喜欢/推荐”逻辑相关的新需求，优先在 `utils` 中抽取为通用函数，再在具体页面中调用。
