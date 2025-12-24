// 引入 Vue 的响应式工具，用于管理请求过程中的状态（加载中、数据、错误等）
import { ref, type Ref } from 'vue' // 从 vue 中导入 ref 和 Ref 类型，用来创建并标注响应式数据

// 使用请求钩子的配置项接口，约束可传入的配置
export interface UseRequestOptions<P = any, T = any> {
  immediate?: boolean // 是否在钩子创建后立刻发起一次请求
  defaultParams?: P // 默认的请求参数（通常是一个对象）
  onSuccess?: (data: T) => void // 请求成功时的回调函数
  onError?: (error: Error) => void // 请求失败时的回调函数
}

// 使用请求钩子返回的结果类型接口，方便调用方通过类型提示了解可用字段
export interface UseRequestResult<T = any, P = any> {
  loading: Ref<boolean> // 标记当前是否有请求在进行中的响应式变量
  data: Ref<T | null> // 保存请求结果数据的响应式变量
  error: Ref<Error | null> // 保存错误信息的响应式变量
  run: (params?: P) => Promise<T | null> // 手动发起请求的方法，支持传入参数
  refresh: () => Promise<T | null> // 使用上一次参数重新发起请求的方法
  reset: () => void // 重置 data 和 error 状态的方法
}

// 通用请求钩子：封装了请求过程中的 loading / data / error 等常用状态
// T 表示返回数据的类型，P 表示请求参数的类型
export function useRequest<T = any, P = any>(
  service: (params?: P) => Promise<T>, // 具体的请求函数，由调用方传入，例如 getEcardInfo 等
  options: UseRequestOptions<P, T> = {} // 可选配置，控制是否立即请求、默认参数以及回调
): UseRequestResult<T, P> {
  const loading = ref<boolean>(false) // 是否正在加载，默认为 false
  const data = ref<T | null>(null) // 请求返回的数据，初始为 null
  const error = ref<Error | null>(null) // 请求错误对象，初始为 null

  const lastParams = ref<P | undefined>(options.defaultParams) // 记录最近一次请求使用的参数，便于后续 refresh

  // 真正执行请求的函数，支持传入参数，并返回 Promise 以便外部 await
  const run = async (params?: P): Promise<T | null> => {
    loading.value = true // 请求开始时，设置为加载中
    error.value = null // 每次新请求前，先清空上一次的错误

    // 更新最近一次使用的参数，如果本次调用传入了参数，则覆盖 lastParams
    if (typeof params !== 'undefined') {
      lastParams.value = params // 把本次参数记录下来，后续 refresh 会用到
    }

    try {
      // 调用传入的 service 函数发起实际请求
      const result = await service(params ?? lastParams.value) // 如果本次没有传参，则使用已记录的参数
      data.value = result // 将请求成功的数据保存到响应式变量中

      // 如果调用方传入了成功回调，则在这里触发
      if (options.onSuccess) {
        options.onSuccess(result) // 把 result 作为参数传给调用方
      }

      return result // 将结果返回给调用方，方便链式处理
    } catch (e: any) {
      // 将错误统一转换为 Error 类型，便于外部处理
      const err = e instanceof Error ? e : new Error(e?.message || '请求失败') // 构造一个标准 Error 对象
      error.value = err // 记录错误信息，页面可以用来展示提示

      // 如果调用方传入了错误回调，则在这里触发
      if (options.onError) {
        options.onError(err) // 把错误对象传给调用方
      }

      return null // 发生错误时，返回 null 作为结果
    } finally {
      loading.value = false // 无论成功还是失败，最后都要把 loading 设为 false
    }
  }

  // 使用最近一次参数重新发起请求的简便方法
  const refresh = async (): Promise<T | null> => {
    // 如果既没有默认参数也没有最近一次参数，则直接调用 service()
    if (typeof lastParams.value === 'undefined' && typeof options.defaultParams === 'undefined') {
      return run(undefined) // 没有参数的请求，直接发起
    }
    // 优先使用 lastParams，其次使用 defaultParams
    const params = (lastParams.value ?? options.defaultParams) as P // 这里做一次类型断言，方便统一处理
    return run(params) // 使用选出的参数重新请求
  }

  // 重置 data 和 error，通常用于页面离开或重新进入时清理旧数据
  const reset = (): void => {
    data.value = null // 清空之前的请求数据
    error.value = null // 清空之前的错误信息
  }

  // 如果配置了 immediate，则在钩子创建完成后自动发起一次请求
  if (options.immediate) {
    // 对于需要参数的请求，如果没有提供 defaultParams，则默认传 undefined
    // 这里不等待结果，只是“触发”一次，调用方如需拿到结果可以自己再调用 run
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    run(options.defaultParams) // 触发一次初始请求
  }

  // 将状态和方法统一返回给调用方，并通过类型断言兼容 Vue3.5 中 Ref 的复杂类型定义
  return {
    loading, // 是否加载中的响应式变量
    data, // 保存请求结果数据的响应式变量
    error, // 保存错误对象的响应式变量
    run, // 手动发起请求的方法
    refresh, // 使用最近一次参数重新发起请求的方法
    reset // 重置 data 和 error 的方法
  } as UseRequestResult<T, P> // 使用类型断言将返回值视为 UseRequestResult，避免泛型与 Ref 内部类型推导不一致导致的编译错误
} // useRequest 钩子定义结束
