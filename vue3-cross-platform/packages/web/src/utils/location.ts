/**
 * Web 端地理位置与校园范围工具
 * 说明：这里负责从浏览器获取当前经纬度，并与 common 包中的纯函数组合使用，
 *       方便在 E 卡通、行程等页面判断“是否在校园范围内”。
 */

import { // 从 common 包中复用经纬度类型与基础计算函数
  type LatLngPoint, // LatLngPoint：包含 latitude/longitude 的简单坐标类型
  getDistanceMeters, // getDistanceMeters：计算两点之间的地表距离（米）
  isPointInCircleRange // isPointInCircleRange：判断某点是否在圆形范围内
} from '@campus/common/utils/location' // 引入公共的地理计算工具，避免重复造轮子

// 从 common 配置中引入“校园中心点”和“默认半径”等配置（如果后续有调整，只需改配置）
import { campusConfig } from '@campus/common/config/campus' // campusConfig：包含各校区的经纬度与半径配置

// 将浏览器坐标结构转换为通用 LatLngPoint 类型
function toLatLngPoint(coords: GeolocationCoordinates): LatLngPoint { // 工具函数：适配浏览器返回的坐标结构
  return { // 返回一个符合 LatLngPoint 接口的对象
    latitude: coords.latitude, // 使用浏览器提供的纬度
    longitude: coords.longitude // 使用浏览器提供的经度
  }
}

// 获取当前浏览器经纬度（Promise 封装 navigator.geolocation）
export function getBrowserLocation(options?: PositionOptions): Promise<LatLngPoint> { // 对外暴露：获取当前经纬度
  return new Promise((resolve, reject) => { // 使用 Promise 包装异步回调
    if (typeof navigator === 'undefined' || !navigator.geolocation) { // 如果当前环境不支持定位
      reject(new Error('当前环境不支持地理定位')) // 返回错误提示
      return // 结束函数执行
    }

    navigator.geolocation.getCurrentPosition( // 调用浏览器原生定位接口
      (position) => { // 成功回调
        const point = toLatLngPoint(position.coords) // 将返回的坐标转换为 LatLngPoint
        resolve(point) // 通过 Promise 返回坐标
      },
      (error) => { // 失败回调
        reject(error) // 将错误对象传给调用方处理
      },
      options // 透传调用方配置（超时、精度等）
    )
  })
}

// 根据 common 中配置的“当前默认校园”，判断是否在校园范围内
export function isInCurrentCampus(point: LatLngPoint): boolean { // 对外暴露：判断当前点是否在默认校园范围
  const campus = campusConfig.defaultCampus // 从配置中读取默认校园（包含中心点与半径）
  if (!campus || !campus.center || !campus.radiusMeters) { // 如果配置不完整
    return false // 无法判断时返回 false，交由上层决定如何处理
  }
  return isPointInCircleRange(point, campus.center, campus.radiusMeters) // 复用 common 中的圆形范围判断函数
}

// 计算当前坐标与某个校园之间的距离（单位：米）
export function getDistanceToCampus(point: LatLngPoint, campusKey: string): number | null { // 对外暴露：计算与指定校园的距离
  const campus = campusConfig.campuses[campusKey] // 根据 key 在配置中查找校园
  if (!campus || !campus.center) { // 如果没有找到对应校区或缺少中心点
    return null // 返回 null 表示无法计算
  }
  return getDistanceMeters(point, campus.center) // 使用公共函数返回两点之间的距离
}

// 一个综合工具：获取当前浏览器定位并判断是否在“默认校园”内
export async function detectCurrentCampusStatus(options?: PositionOptions): Promise<{
  success: boolean // success：是否成功获取到定位信息
  inCampus: boolean // inCampus：是否在校园范围内
  point: LatLngPoint | null // point：当前经纬度（如果失败则为 null）
  message: string // message：给前端展示用的中文提示
}> {
  try { // 使用 try/catch 捕获整个流程中的异常
    const point = await getBrowserLocation(options) // 先获取浏览器当前经纬度
    const inCampus = isInCurrentCampus(point) // 调用上面的函数判断是否在默认校园内

    if (inCampus) { // 如果在校园范围内
      return { // 返回成功状态和友好的提示文案
        success: true, // 表示定位获取成功
        inCampus: true, // 表示在校园范围内
        point, // 返回当前经纬度
        message: '已检测到您在校园范围内' // 提示用户当前在校内
      }
    }

    // 定位成功但不在校园范围
    return {
      success: true, // 表示定位成功
      inCampus: false // 表示不在校园范围
      , point, // 返回当前经纬度
      message: '当前定位显示不在校园范围内' // 友好的中文提示
    }
  } catch (error) { // 如果在定位过程中发生错误
    console.error('定位检测失败:', error) // 在控制台打印错误，方便开发调试
    return { // 返回失败状态和说明
      success: false, // 表示定位获取失败
      inCampus: false // 无法判断时默认为不在校园
      , point: null, // 当前坐标未知
      message: '无法获取当前位置，请检查浏览器定位权限' // 给用户的错误提示
    }
  }
}
