// 为 sockjs-client 提供最简模块声明，避免 TypeScript 在缺少 @types/sockjs-client 的情况下报错
declare module 'sockjs-client' {
  const SockJS: any // SockJS 构造函数类型在此简化为 any，具体结构由运行时库决定
  export default SockJS // 默认导出 SockJS 构造函数
}

