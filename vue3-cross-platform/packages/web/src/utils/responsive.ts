/**
 * Web 端响应式与设备尺寸工具
 * 说明：这里封装了一些与窗口宽度、断点判断相关的工具函数，
 *       方便在页面中判断“是否是手机端”、“是否是平板”等场景。
 */

// 定义一个断点枚举类型，方便代码中使用固定字符串
export type Breakpoint = 'xs' | 'sm' | 'md' | 'lg' | 'xl' // Breakpoint：表示不同尺寸区间

// 与 common/styles/variables.scss 中保持一致的断点数值
const BREAKPOINT_XS = 480 // 与 $breakpoint-xs 对应的小屏手机阈值（px）
const BREAKPOINT_SM = 576 // 与 $breakpoint-sm 对应的大屏手机阈值（px）
const BREAKPOINT_MD = 768 // 与 $breakpoint-md 对应的平板阈值（px）
const BREAKPOINT_LG = 992 // 与 $breakpoint-lg 对应的桌面阈值（px）
const BREAKPOINT_XL = 1200 // 与 $breakpoint-xl 对应的大屏桌面阈值（px）

// 安全地获取当前窗口宽度（SSR 或非浏览器环境下返回一个默认值）
export function getWindowWidth(): number { // 对外暴露：获取当前窗口宽度
  if (typeof window === 'undefined') { // 如果当前不是浏览器环境
    return BREAKPOINT_XL // 返回一个较大的默认宽度，表示“桌面端”
  }
  return window.innerWidth // 在浏览器中直接返回视口宽度
}

// 根据给定宽度（或当前窗口宽度）计算当前断点
export function getBreakpoint(width: number = getWindowWidth()): Breakpoint { // 对外暴露：根据宽度计算断点
  if (width < BREAKPOINT_XS) return 'xs' // 小于 480px 视为极小屏（如窄手机）
  if (width < BREAKPOINT_SM) return 'sm' // 小于 576px 视为小屏手机
  if (width < BREAKPOINT_MD) return 'md' // 小于 768px 视为平板或横屏手机
  if (width < BREAKPOINT_LG) return 'lg' // 小于 992px 视为小桌面
  return 'xl' // 其余视为大屏桌面
}

// 判断当前是否为“移动端”（手机 + 小平板）
export function isMobile(width: number = getWindowWidth()): boolean { // 对外暴露：判断当前是否处于移动端宽度
  const bp = getBreakpoint(width) // 获取当前断点
  return bp === 'xs' || bp === 'sm' || bp === 'md' // xs/sm/md 视为移动或平板端
}

// 判断当前是否为“桌面端”（大屏）
export function isDesktop(width: number = getWindowWidth()): boolean { // 对外暴露：判断当前是否为桌面端
  const bp = getBreakpoint(width) // 获取当前断点
  return bp === 'lg' || bp === 'xl' // lg/xl 视为桌面端
}

// 监听窗口尺寸变化，并在变化时触发回调；返回一个取消监听的函数
export function onWindowResize(callback: (width: number, height: number) => void): () => void { // 提供统一的 resize 监听封装
  if (typeof window === 'undefined') { // 如果不在浏览器环境中
    return () => {} // 返回一个空函数，调用后什么也不做
  }

  const handler = () => { // 内部事件处理函数
    callback(window.innerWidth, window.innerHeight) // 将当前宽高传给外部回调
  }

  window.addEventListener('resize', handler) // 监听 resize 事件

  // 返回一个取消监听的函数，方便在组件卸载时调用
  return () => {
    window.removeEventListener('resize', handler) // 移除事件监听，避免内存泄漏
  }
}

// 使用 matchMedia 监听媒体查询是否匹配（例如最小宽度、大于某个断点等）
export function watchMediaQuery(query: string, onChange: (matches: boolean) => void): () => void { // 对外暴露：监听媒体查询结果变化
  if (typeof window === 'undefined' || !window.matchMedia) { // 如果不支持 matchMedia
    onChange(true) // 默认认为匹配，交由上层决定如何处理
    return () => {} // 返回一个空的取消函数
  }

  const mediaQueryList = window.matchMedia(query) // 创建媒体查询对象

  const listener = (event: MediaQueryListEvent) => { // 定义监听函数
    onChange(event.matches) // 当媒体查询结果改变时，将是否匹配的布尔值传给回调
  }

  // 立即触发一次，告知当前是否匹配
  onChange(mediaQueryList.matches) // 初始化时调用一次回调

  mediaQueryList.addEventListener('change', listener) // 监听媒体查询变化

  // 返回取消监听的函数
  return () => {
    mediaQueryList.removeEventListener('change', listener) // 移除媒体查询监听
  }
}
