/**
 * 统一请求封装
 * 支持Web端（axios）和UniApp端（uni.request）
 * 统一处理请求拦截、响应拦截、错误处理等
 */
import { getToken, removeToken } from './auth'
import axios, { type AxiosInstance } from 'axios' // 直接导入axios

// 定义统一响应格式
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

// 判断是否为UniApp环境
const isUniApp = typeof uni !== 'undefined' && typeof uni.request === 'function'

/**
 * 统一获取 API BaseURL
 * - Web端：默认使用相对路径（配合 Vite devServer proxy / 生产环境反向代理），避免 CORS 问题
 * - UniApp端：必须使用完整域名（真机/小程序不能用 localhost），可通过 VITE_API_BASE_URL 覆盖
 */
const getApiBaseURL = (): string => {
  const envBaseURL = (import.meta as any)?.env?.VITE_API_BASE_URL as string | undefined
  if (envBaseURL && String(envBaseURL).trim()) return String(envBaseURL).trim()
  // Web 端默认走同源（开发时由 Vite proxy 转发 /api 到后端）
  if (!isUniApp) return ''
  // UniApp 端必须是完整 URL；默认仅用于本机 H5 调试，真机/小程序请配置为局域网 IP
  return 'http://localhost:8080'
}

// UniApp请求适配器
const uniRequestAdapter = <T = any>(url: string, config: any = {}): Promise<T> => {
  return new Promise((resolve, reject) => {
    const baseURL = getApiBaseURL()
    let fullUrl = url.startsWith('http') ? url : `${baseURL}${url}`
    
    // 获取token
    const token = getToken()
    const headers: Record<string, string> = {
      'Content-Type': 'application/json;charset=UTF-8',
      ...config.headers
    }
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }
    
    // 处理请求参数
    let data = config.data
    if ((config.method === 'GET' || !config.method) && config.params) {
      // GET请求的参数需要拼接到URL
      // 手动拼接参数，避免使用URLSearchParams（微信小程序不支持）
      const paramsArray: string[] = []
      for (const key in config.params) {
        if (config.params.hasOwnProperty(key)) {
          const value = config.params[key]
          if (value !== null && value !== undefined) {
            paramsArray.push(`${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`)
          }
        }
      }
      if (paramsArray.length > 0) {
        const paramsString = paramsArray.join('&')
        const separator = fullUrl.includes('?') ? '&' : '?'
        fullUrl = `${fullUrl}${separator}${paramsString}`
      }
    }
    
    // 打印请求信息（开发环境）
    if (import.meta.env.DEV) {
      console.log('📤 请求发送:', {
        url: fullUrl,
        method: config.method || 'GET',
        params: config.params,
        data: data
      })
    }
    
    // 调用uni.request
    uni.request({
      url: fullUrl,
      method: (config.method || 'GET').toUpperCase() as any,
      data: data,
      header: headers,
      timeout: config.timeout || 30000,
      success: (res: any) => {
        // 打印响应信息（开发环境）
        if (import.meta.env.DEV) {
          console.log('📥 响应接收:', {
            url: fullUrl,
            code: res.data?.code,
            msg: res.data?.msg,
            data: res.data?.data
          })
        }
        
        const responseData = res.data as ApiResponse<T>
        
        // 根据业务状态码处理
        if (responseData.code === 200) {
          // 成功响应，直接返回data
          resolve(responseData.data as T)
        } else if (responseData.code === 401) {
          // ⚠️ 演示模式：已禁用401自动跳转登录，仅清除token并返回错误
          removeToken()
          console.warn('⚠️ 演示模式：收到401错误，但不会自动跳转登录页')
          reject(new Error(responseData.msg || '未授权，请重新登录'))
        } else {
          // 其他业务错误
          console.error('❌ 业务错误:', responseData.msg)
          reject(new Error(responseData.msg || '请求失败'))
        }
      },
      fail: (err: any) => {
        // HTTP错误处理
        let message = '请求失败'
        
        if (err.statusCode) {
          const status = err.statusCode
          switch (status) {
            case 400:
              message = '请求参数错误'
              break
            case 401:
              message = '未授权，请重新登录'
              removeToken()
              console.warn('⚠️ 演示模式：收到401错误，但不会自动跳转登录页')
              break
            case 403:
              message = '没有权限访问'
              break
            case 404:
              message = '请求的资源不存在'
              break
            case 500:
              message = '服务器内部错误'
              break
            case 502:
              message = '网关错误'
              break
            case 503:
              message = '服务不可用'
              break
            default:
              message = `请求失败 (${status})`
          }
        } else {
          // 处理连接失败的情况
          const errMsg = err.errMsg || err.message || ''
          if (errMsg.includes('fail') || errMsg.includes('CONNECTION_REFUSED') || errMsg.includes('localhost')) {
            // 微信小程序无法访问localhost，需要配置实际IP地址
            message = '无法连接到服务器，请检查：\n1. 后端服务是否已启动\n2. API地址是否正确（微信小程序不能使用localhost，需要使用实际IP地址）\n3. 网络连接是否正常'
            console.error('❌ 连接失败提示:', {
              url: fullUrl,
              tip: '微信小程序无法访问localhost，请在环境变量中配置实际的服务器IP地址',
              example: 'VITE_API_BASE_URL=http://192.168.1.100:8080'
            })
          } else {
            message = errMsg || '网络连接失败，请检查网络'
          }
        }
        
        console.error('❌ 请求错误:', {
          url: fullUrl,
          message: message,
          error: err
        })
        
        reject(new Error(message))
      }
    })
  })
}

// Web端axios适配器（仅在Web端使用）
let axiosInstance: AxiosInstance | null = null
const createAxiosInstance = (): AxiosInstance => {
  if (axiosInstance) return axiosInstance
  
  // 动态导入axios（仅在Web端）
  if (!isUniApp) {
    try {
      // 使用已导入的axios
      const baseURL = getApiBaseURL()
      
      axiosInstance = axios.create({
        baseURL: baseURL,
        timeout: 30000,
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        }
      })

      // 请求拦截器
      axiosInstance.interceptors.request.use(
        (config: any) => {
          const token = getToken()
          if (token && config.headers) {
            config.headers['Authorization'] = `Bearer ${token}`
          }
          
          if (import.meta.env.DEV) {
            console.log('📤 请求发送:', {
              url: config.url,
              method: config.method,
              params: config.params,
              data: config.data
            })
          }
          
          return config
        },
        (error: any) => {
          console.error('❌ 请求拦截器错误:', error)
          return Promise.reject(error)
        }
      )

      // 响应拦截器
      axiosInstance.interceptors.response.use(
        (response: any) => {
          const res = response.data
          
          if (import.meta.env.DEV) {
            console.log('📥 响应接收:', {
              url: response.config.url,
              code: res.code,
              msg: res.msg,
              data: res.data
            })
          }
          
          if (res.code === 200) {
            return res.data
          } else if (res.code === 401) {
            removeToken()
            console.warn('⚠️ 演示模式：收到401错误，但不会自动跳转登录页')
            return Promise.reject(new Error(res.msg || '未授权，请重新登录'))
          } else {
            console.error('❌ 业务错误:', res.msg)
            return Promise.reject(new Error(res.msg || '请求失败'))
          }
        },
        (error: any) => {
          let message = '请求失败'
          
          if (error.response) {
            const status = error.response.status
            switch (status) {
              case 400:
                message = '请求参数错误'
                break
              case 401:
                message = '未授权，请重新登录'
                removeToken()
                console.warn('⚠️ 演示模式：收到401错误，但不会自动跳转登录页')
                break
              case 403:
                message = '没有权限访问'
                break
              case 404:
                message = '请求的资源不存在'
                break
              case 500:
                message = '服务器内部错误'
                break
              case 502:
                message = '网关错误'
                break
              case 503:
                message = '服务不可用'
                break
              default:
                message = `请求失败 (${status})`
            }
          } else if (error.request) {
            message = '网络连接失败，请检查网络'
          } else {
            message = error.message || '请求失败'
          }
          
          console.error('❌ 请求错误:', {
            url: error.config?.url,
            message: message,
            error: error
          })
          
          return Promise.reject(new Error(message))
        }
      )
    } catch (error) {
      console.error('❌ 初始化axios失败:', error)
      throw error
    }
  }

  if (!axiosInstance) {
    throw new Error('axios 实例初始化失败')
  }

  return axiosInstance
}

// 统一请求接口
const request = {
  get<T = any>(url: string, config?: any): Promise<T> {
    if (isUniApp) {
      return uniRequestAdapter<T>(url, { ...config, method: 'GET' })
    } else {
      const instance = createAxiosInstance()
      // 注意：响应拦截器已将 AxiosResponse 解包为 res.data
      return instance.get(url, config) as unknown as Promise<T>
    }
  },
  
  post<T = any>(url: string, data?: any, config?: any): Promise<T> {
    if (isUniApp) {
      return uniRequestAdapter<T>(url, { ...config, method: 'POST', data })
    } else {
      const instance = createAxiosInstance()
      return instance.post(url, data, config) as unknown as Promise<T>
    }
  },
  
  put<T = any>(url: string, data?: any, config?: any): Promise<T> {
    if (isUniApp) {
      return uniRequestAdapter<T>(url, { ...config, method: 'PUT', data })
    } else {
      const instance = createAxiosInstance()
      return instance.put(url, data, config) as unknown as Promise<T>
    }
  },
  
  delete<T = any>(url: string, config?: any): Promise<T> {
    if (isUniApp) {
      return uniRequestAdapter<T>(url, { ...config, method: 'DELETE' })
    } else {
      const instance = createAxiosInstance()
      return instance.delete(url, config) as unknown as Promise<T>
    }
  },
  
  patch<T = any>(url: string, data?: any, config?: any): Promise<T> {
    if (isUniApp) {
      return uniRequestAdapter<T>(url, { ...config, method: 'PATCH', data })
    } else {
      const instance = createAxiosInstance()
      return instance.patch(url, data, config) as unknown as Promise<T>
    }
  }
}

// 导出请求对象
export { request }

// 导出默认请求对象（兼容旧代码）
export default request