/**
 * 跨平台存储工具
 * 兼容 Web 和 UniApp 环境
 */

// 判断运行环境
const isUniApp = typeof uni !== 'undefined'

/**
 * 存储接口
 */
interface StorageInterface {
  getItem(key: string): string | null
  setItem(key: string, value: string): void
  removeItem(key: string): void
  clear(): void
}

/**
 * Web 环境存储实现（使用 localStorage）
 */
class WebStorage implements StorageInterface {
  getItem(key: string): string | null {
    try {
      return localStorage.getItem(key)
    } catch (e) {
      console.error('localStorage.getItem 失败:', e)
      return null
    }
  }

  setItem(key: string, value: string): void {
    try {
      localStorage.setItem(key, value)
    } catch (e) {
      console.error('localStorage.setItem 失败:', e)
    }
  }

  removeItem(key: string): void {
    try {
      localStorage.removeItem(key)
    } catch (e) {
      console.error('localStorage.removeItem 失败:', e)
    }
  }

  clear(): void {
    try {
      localStorage.clear()
    } catch (e) {
      console.error('localStorage.clear 失败:', e)
    }
  }
}

/**
 * UniApp 环境存储实现（使用 uni.setStorageSync）
 */
class UniAppStorage implements StorageInterface {
  getItem(key: string): string | null {
    try {
      return uni.getStorageSync(key) || null
    } catch (e) {
      console.error('uni.getStorageSync 失败:', e)
      return null
    }
  }

  setItem(key: string, value: string): void {
    try {
      uni.setStorageSync(key, value)
    } catch (e) {
      console.error('uni.setStorageSync 失败:', e)
    }
  }

  removeItem(key: string): void {
    try {
      uni.removeStorageSync(key)
    } catch (e) {
      console.error('uni.removeStorageSync 失败:', e)
    }
  }

  clear(): void {
    try {
      uni.clearStorageSync()
    } catch (e) {
      console.error('uni.clearStorageSync 失败:', e)
    }
  }
}

// 根据环境选择存储实现
const storage: StorageInterface = isUniApp ? new UniAppStorage() : new WebStorage()

/**
 * 获取存储值
 * @param key 存储键
 * @returns 存储值，不存在返回 null
 */
export function getStorage(key: string): string | null {
  return storage.getItem(key)
}

/**
 * 设置存储值
 * @param key 存储键
 * @param value 存储值
 */
export function setStorage(key: string, value: string): void {
  storage.setItem(key, value)
}

/**
 * 移除存储值
 * @param key 存储键
 */
export function removeStorage(key: string): void {
  storage.removeItem(key)
}

/**
 * 清空所有存储
 */
export function clearStorage(): void {
  storage.clear()
}

/**
 * 获取 JSON 对象
 * @param key 存储键
 * @returns 解析后的对象，失败返回 null
 */
export function getStorageJSON<T = any>(key: string): T | null {
  const value = getStorage(key)
  if (!value) return null
  try {
    return JSON.parse(value) as T
  } catch (e) {
    console.error('解析存储值失败:', e)
    return null
  }
}

/**
 * 设置 JSON 对象
 * @param key 存储键
 * @param value 要存储的对象
 */
export function setStorageJSON(key: string, value: any): void {
  try {
    setStorage(key, JSON.stringify(value))
  } catch (e) {
    console.error('序列化存储值失败:', e)
  }
}

// 导出默认存储实例
export default storage
