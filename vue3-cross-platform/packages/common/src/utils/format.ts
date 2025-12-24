/**
 * 通用格式化与推荐工具函数集合
 * 说明：这里放与具体业务无强耦合的纯函数，
 *       例如价格格式化、时间格式化、数组随机推荐等，
 *       方便 web / app 端统一复用。
 */

/**
 * 将数字格式化为价格字符串（保留两位小数）
 * @param value 原始数字或可以转为数字的值
 * @param prefix 价格前缀，默认使用人民币符号 "￥"
 * @returns 例如：1234.5 -> "￥1,234.50"
 */
export function formatPrice( // 导出一个名为 formatPrice 的函数，用于格式化价格
  value: number | string | null | undefined, // 函数入参 value，可以是数字、字符串或空
  prefix: string = '￥' // 价格字符串前缀，默认是人民币符号
): string { // 函数返回一个字符串
  if (value === null || value === undefined || value === '') { // 如果传入值为空或未定义
    return '-' // 返回占位符 "-"，避免出现 "NaN"
  }

  const num = Number(value) // 将传入的值强制转换为数字
  if (Number.isNaN(num)) { // 如果转换后的结果不是一个有效数字
    return String(value) // 直接返回原始值的字符串形式，方便排查问题
  }

  const fixed = num.toFixed(2) // 将数字格式化为保留两位小数的字符串

  // 使用正则表达式为整数部分添加千分位逗号，例如 1234 -> 1,234
  const parts = fixed.split('.') // 将小数部分与整数部分拆开
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',') // 给整数部分添加千分位分隔符

  return `${prefix}${parts.join('.')}` // 拼接前缀与格式化后的整数+小数部分并返回
}

/**
 * 将日期时间格式化为 "YYYY-MM-DD HH:mm" 形式
 * @param input 可以被 new Date 解析的时间（字符串/时间戳/Date 实例）
 * @returns 格式化后的时间字符串，如果解析失败返回 "-"
 */
export function formatDateTime( // 导出一个名为 formatDateTime 的函数，用于格式化日期时间
  input: string | number | Date | null | undefined // 函数入参，可以是字符串、数字或 Date 对象
): string { // 返回格式化后的时间字符串
  if (!input) { // 如果没有传入任何值
    return '-' // 返回占位符 "-"
  }

  const date = input instanceof Date ? input : new Date(input) // 如果已经是 Date 实例则直接使用，否则通过构造函数转换
  if (Number.isNaN(date.getTime())) { // 判断日期是否有效
    return '-' // 无效日期返回占位符
  }

  const year = date.getFullYear() // 获取年份
  const month = String(date.getMonth() + 1).padStart(2, '0') // 获取月份并补零
  const day = String(date.getDate()).padStart(2, '0') // 获取日并补零
  const hours = String(date.getHours()).padStart(2, '0') // 获取小时并补零
  const minutes = String(date.getMinutes()).padStart(2, '0') // 获取分钟并补零

  return `${year}-${month}-${day} ${hours}:${minutes}` // 拼接成 "YYYY-MM-DD HH:mm" 格式并返回
}

/**
 * 将时间转为相对时间描述（如：刚刚 / 5分钟前 / 2天前）
 * @param input 源时间
 * @param now 当前时间（可选，默认使用系统当前时间）
 */
export function formatRelativeTime( // 导出一个名为 formatRelativeTime 的函数
  input: string | number | Date, // 需要转换的源时间
  now: Date = new Date() // 当前时间，默认使用系统时间，可以在单元测试中传入固定值
): string { // 返回相对时间描述
  const target = input instanceof Date ? input : new Date(input) // 将输入统一转为 Date 对象
  const diffMs = now.getTime() - target.getTime() // 计算两个时间的毫秒差值

  if (diffMs < 0) { // 如果源时间在未来
    return '刚刚' // 统一显示为刚刚，避免出现 "-1分钟"
  }

  const diffSeconds = Math.floor(diffMs / 1000) // 将毫秒差值换算为秒
  if (diffSeconds < 60) { // 小于 60 秒
    return '刚刚' // 显示为刚刚
  }

  const diffMinutes = Math.floor(diffSeconds / 60) // 将秒换算为分钟
  if (diffMinutes < 60) { // 小于 60 分钟
    return `${diffMinutes}分钟前` // 显示为 X 分钟前
  }

  const diffHours = Math.floor(diffMinutes / 60) // 将分钟换算为小时
  if (diffHours < 24) { // 小于 24 小时
    return `${diffHours}小时前` // 显示为 X 小时前
  }

  const diffDays = Math.floor(diffHours / 24) // 将小时换算为天
  if (diffDays < 30) { // 小于 30 天
    return `${diffDays}天前` // 显示为 X 天前
  }

  const diffMonths = Math.floor(diffDays / 30) // 粗略换算为月
  if (diffMonths < 12) { // 小于 12 个月
    return `${diffMonths}个月前` // 显示为 X 个月前
  }

  const diffYears = Math.floor(diffMonths / 12) // 粗略换算为年
  return `${diffYears}年前` // 显示为 X 年前
}

/**
 * 对数组进行原地洗牌（Fisher-Yates 算法）
 * @param list 要被打乱顺序的数组
 * @returns 返回同一个数组引用（已被打乱）
 */
export function shuffleArray<T>( // 导出一个泛型函数 shuffleArray，用于打乱数组顺序
  list: T[] // 需要打乱的数组
): T[] { // 返回打乱后的数组
  for (let i = list.length - 1; i > 0; i--) { // 从数组末尾开始向前遍历
    const j = Math.floor(Math.random() * (i + 1)) // 在 [0, i] 区间内随机取一个索引
    const temp = list[i] // 暂存当前位置的元素
    list[i] = list[j] // 将随机位置的元素放到当前位置
    list[j] = temp // 将原来的当前位置元素放到随机位置
  }
  return list // 返回已经打乱顺序的原数组
}

/**
 * 从数组中随机抽取若干个不重复元素（不会修改原数组）
 * @param list 原始数组
 * @param count 需要抽取的数量，默认 6 条
 * @returns 新数组，包含随机挑选出的元素
 */
export function pickRandomItems<T>( // 导出一个名为 pickRandomItems 的泛型函数
  list: T[], // 原始数组
  count: number = 6 // 需要抽取的数量，默认 6 条
): T[] { // 返回一个新的数组
  if (!Array.isArray(list) || list.length === 0) { // 如果传入的不是数组或数组为空
    return [] // 直接返回空数组
  }

  const safeCount = Math.max(0, Math.min(count, list.length)) // 将需要抽取的数量限制在 [0, 数组长度] 之间

  const copy = list.slice() // 复制一份新数组，避免修改原数组
  shuffleArray(copy) // 对复制出来的数组进行洗牌
  return copy.slice(0, safeCount) // 从打乱后的数组中截取前 safeCount 个元素并返回
}

/**
 * 获取用于“猜你喜欢/推荐列表”的随机数据
 * 封装了常见的默认数量与安全保护，推荐页面直接复用该函数。
 * @param list 原始完整数据列表
 * @param defaultCount 默认推荐数量，二手/兼职场景可以传 6 或 8
 */
export function getRandomRecommendList<T>( // 导出一个名为 getRandomRecommendList 的泛型函数
  list: T[], // 原始数据列表
  defaultCount: number = 6 // 默认抽取的数量
): T[] { // 返回推荐列表
  return pickRandomItems(list, defaultCount) // 直接复用 pickRandomItems 实现核心逻辑
}
