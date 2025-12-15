# common 子包（双端公共核心）
## 核心内容
- components/common：双端通用基础组件
- api：RESTful 接口封装（适配 uni.request/axios）
- pinia：双端共用状态（用户/校园卡/行程等）
- config：角色权限/校园配置（核心约定）
- hooks/utils：双端通用钩子/工具

## 维护规则
1. 通用代码统一维护在此，app/web 端通过软链接/依赖引用
2. 接口返回格式遵循 {code, msg, data} 规范，JWT 校验请求头 Authorization
