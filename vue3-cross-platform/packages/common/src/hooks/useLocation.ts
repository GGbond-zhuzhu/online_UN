// 定位相关通用钩子（优先适配 UniApp 环境）—— 提供当前位置经纬度和“是否在校内”的简单判断

// 引入 Vue 的响应式工具
import { ref } from 'vue' // 从 vue 中导入 ref，用于创建响应式的数据对象

// 定义一个简单的坐标类型，方便在项目中复用
export interface SimpleLocation {
  latitude: number // 当前纬度
  longitude: number // 当前经度
}

// useLocation 钩子：统一封装“获取定位 + 校园范围判断”的逻辑
export function useLocation() {
  // 当前定位信息（默认经纬度都为 0，表示尚未获取到真实位置）
  const location = ref<SimpleLocation>({
    latitude: 0, // 默认纬度为 0
    longitude: 0 // 默认经度为 0
  }) // 使用 ref 将 location 包装为响应式对象，页面绑定后会自动更新

  // 定位失败或不可用时的提示信息
  const locationError = ref('') // 当定位失败或当前环境不支持定位时，会写入对应的错误提示

  // 获取当前位置（优先使用 UniApp 提供的 uni.getLocation 接口）
  const getLocation = () => {
    // 如果当前环境中不存在 uni 对象，说明不在 UniApp 运行环境（例如纯 Web），此时不主动报错，只提示不支持定位
    if (typeof uni === 'undefined') {
      locationError.value = '当前环境暂不支持定位功能' // 写入错误提示，提醒用户当前环境无法调用定位
      return // 直接返回，不再尝试调用定位接口
    }

    // 调用 uni.getLocation 获取经纬度信息（此处使用 any 类型以兼容不同端返回结构）
    uni.getLocation({
      type: 'gcj02', // 使用国标坐标系（微信小程序 / 高德地图通用）
      success: (res: any) => {
        // 在成功回调中，将返回的经纬度写入响应式 location 对象
        location.value = {
          latitude: res.latitude, // 从返回结果中读取纬度
          longitude: res.longitude // 从返回结果中读取经度
        }
        locationError.value = '' // 成功获取定位后，清空错误提示
      },
      fail: (err: any) => {
        // 当定位失败（例如未授权或系统关闭定位服务）时，记录错误提示
        locationError.value = '定位失败，请在系统中开启定位权限' // 写入用户可理解的错误提示
        console.error('定位失败：', err) // 在控制台打印具体错误信息，便于开发调试
      }
    })
  }

  // 校验当前位置是否在“校园范围”内（前端简化版本，后续可结合后端接口进行精确判断）
  const checkIsInCampus = (schoolRange?: { minLat: number; maxLat: number; minLng: number; maxLng: number }) => {
    // 如果没有传入校园范围配置，则直接返回 false，表示“不可判断”
    if (!schoolRange) return false // 没有可用的校园范围时，直接认为“不在校园内”

    // 从当前定位信息中取出经纬度
    const { latitude, longitude } = location.value // 解构出当前位置的纬度和经度

    // 通过简单的矩形边界判断当前位置是否处于校园范围内
    return (
      latitude >= schoolRange.minLat && // 纬度是否大于等于校园最小纬度
      latitude <= schoolRange.maxLat && // 纬度是否小于等于校园最大纬度
      longitude >= schoolRange.minLng && // 经度是否大于等于校园最小经度
      longitude <= schoolRange.maxLng // 经度是否小于等于校园最大经度
    ) // 若四个条件都满足，则认为当前位置在校园范围内
  }

  // 将响应式数据和方法统一返回给调用方
  return {
    location, // 当前定位信息（经纬度）
    locationError, // 当前定位错误提示信息
    getLocation, // 主动获取定位的方法
    checkIsInCampus // 校验当前位置是否在校园范围内的方法
  }
}

// 同时导出默认导出，方便旧代码通过 `import useLocation from '@campus/common'` 的方式使用
export default useLocation // 将 useLocation 作为默认导出导出，兼顾命名导出和默认导出两种用法
