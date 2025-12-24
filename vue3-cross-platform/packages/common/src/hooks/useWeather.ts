// 天气相关通用钩子（主要面向 H5 / Web 环境）—— 统一实时天气获取逻辑
// 说明：在 uni-app H5 环境中，通过高德天气接口 + JSONP 获取当前城市的实时天气信息

// 引入 Vue 的响应式工具
import { ref } from 'vue' // 从 vue 中导入 ref，用于创建响应式数据

// 定义天气信息的简单结构体类型
export interface SimpleWeatherInfo {
  city: string // 城市名称，例如“北京”
  weather: string // 天气情况，例如“晴”、“多云”
  temperature: string // 当前温度（带单位字符串），例如“13℃”
  text: string // 已拼接好的完整展示文案，例如“北京 晴 13℃”
}

// 从环境变量中读取高德天气的 Key（需要在 .env 中配置 VITE_AMAP_KEY），没有则使用占位符
const AMAP_KEY = import.meta.env.VITE_AMAP_KEY || 'your-amap-key' // 高德天气服务的接口 Key，占位符需要在真实项目中替换

// useWeather 钩子：统一封装“获取定位并查询实时天气”的逻辑
export function useWeather() {
  // ========== 基础状态 ==========

  const loading = ref(false) // 标记当前是否正在获取天气信息
  const errorMessage = ref('') // 最近一次获取天气过程中的错误提示

  // 默认天气信息（在无法获取真实天气时，用于兜底展示）
  const weatherInfo = ref<SimpleWeatherInfo>({
    city: '北京', // 默认城市名
    weather: '晴', // 默认天气情况
    temperature: '13℃', // 默认温度
    text: '北京 晴 13℃' // 已拼接好的完整展示文案
  }) // 使用 ref 创建响应式天气信息对象

  // ========== 内部工具函数：更新 weatherInfo ==========

  const setWeatherInfo = (city: string, weather: string, temperature: string) => {
    const text = `${city} ${weather} ${temperature}` // 拼接展示文案
    weatherInfo.value = {
      city, // 更新城市名称
      weather, // 更新天气情况
      temperature, // 更新温度
      text // 更新完整展示文案
    } // 将新的天气信息写入响应式变量
  }

  // ========== 获取实时天气的主流程 ==========

  const getRealTimeWeather = () => {
    // 如果当前环境既没有 window/document，也没有 uni 对象，则说明不是 H5/浏览器环境，直接使用默认天气
    if (typeof window === 'undefined' || typeof document === 'undefined' || typeof uni === 'undefined') {
      errorMessage.value = '当前环境不支持实时天气，将显示默认天气' // 写入错误提示
      return // 直接返回，不再尝试请求
    }

    // 如果没有正确配置高德 Key，则不发起请求，避免无效网络调用
    if (!AMAP_KEY || AMAP_KEY === 'your-amap-key') {
      errorMessage.value = '未配置高德天气 Key，将显示默认天气' // 提示需要配置环境变量
      return // 直接返回
    }

    loading.value = true // 标记开始获取天气
    errorMessage.value = '' // 清空历史错误提示

    // 第一步：先通过 uni.getLocation 获取当前位置经纬度（使用 any 以兼容不同端返回结构）
    uni.getLocation({
      type: 'gcj02', // 使用国测局坐标系（适配微信小程序 / 高德）
      success: (locationRes: any) => {
        // 从定位结果中解构出经纬度
        const { longitude, latitude } = locationRes // 当前用户的经度和纬度

        // 第二步：动态拼接 JSONP 回调函数名称，避免全局命名冲突
        const callbackName = `onCampusWeatherCallback_${Date.now()}_${Math.floor(Math.random() * 1000)}` // 生成一个较为唯一的回调函数名

        // 在 window 对象上挂载回调函数，供高德天气接口在 JSONP 返回时调用
        ;(window as any)[callbackName] = (res: any) => {
          try {
            // 按高德天气返回格式解析接口数据
            if (res && (res.status === '1' || res.status === 1) && res.lives && res.lives.length > 0) {
              const live = res.lives[0] // 取第一条实时天气数据
              const city = live.city || '当前位置' // 城市名称，缺失时用“当前位置”兜底
              const weather = live.weather || '晴' // 天气情况，缺失时默认“晴”
              const temperature = (live.temperature || '13') + '℃' // 温度字段，后面统一补上“℃”
              setWeatherInfo(city, weather, temperature) // 使用工具函数更新天气信息
            } else {
              // 如果接口状态不正常，则保留默认天气，并写入错误信息
              errorMessage.value = '获取实时天气失败，将显示默认天气' // 写入错误提示
            }
          } finally {
            // 无论成功失败，都要清理全局回调，避免内存泄漏
            try {
              delete (window as any)[callbackName] // 从 window 上移除回调函数
            } catch {
              // 删除失败时可以忽略，不影响主流程
            }
            loading.value = false // 恢复加载状态
          }
        }

        // 第三步：通过 script 标签 + JSONP 的方式调用高德天气接口
        const script = document.createElement('script') // 创建一个 script 标签
        // 这里使用高德天气 JSONP 接口（以官方文档为准），callback 参数使用前面定义的 callbackName
        script.src = `https://webapi.amap.com/weather/v1/weatherInfo?key=${encodeURIComponent(
          AMAP_KEY
        )}&location=${longitude},${latitude}&output=jsonp&callback=${callbackName}` // 动态拼接请求地址

        // 当 script 加载失败时的兜底处理
        script.onerror = () => {
          loading.value = false // 结束加载状态
          errorMessage.value = '天气服务请求失败，将显示默认天气' // 写入错误提示
          try {
            delete (window as any)[callbackName] // 清理全局回调函数
          } catch {
            // 删除失败忽略
          }
        }

        // 将 script 标签插入到页面中以发起请求
        document.head.appendChild(script) // 把 script 插入到 head 中以触发请求

        // 在请求发出后适当时机移除 script 标签，避免 DOM 累积（这里使用 setTimeout 做简单清理）
        setTimeout(() => {
          try {
            document.head.removeChild(script) // 从 head 中移除 script 标签
          } catch {
            // 如果已经被移除，则忽略异常
          }
        }, 10000) // 10 秒后尝试移除脚本标签
      },
      fail: () => {
        // 定位失败时，给出提示并保留默认天气
        loading.value = false // 结束加载状态
        errorMessage.value = '未获取到定位权限，已显示默认天气' // 写入错误提示
        // 此处不再尝试通过 IP 等方式获取城市，保持逻辑简单清晰
      }
    })
  }

  // ========== 对外统一返回 ==========

  return {
    loading, // 当前是否正在获取天气信息
    errorMessage, // 最近一次获取天气过程中的错误提示
    weatherInfo, // 当前天气信息（包含 city / weather / temperature / text）
    getRealTimeWeather // 主动触发获取实时天气的方法
  }
} // useWeather 钩子定义结束
