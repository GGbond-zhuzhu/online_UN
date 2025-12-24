import SockJS from 'sockjs-client' // 引入 SockJS 客户端，用于创建兼容 WebSocket 的连接
import { Client, IMessage } from '@stomp/stompjs' // 引入 STOMP 客户端类型，用于在 WebSocket 之上进行消息收发

// 全局保存一个 STOMP 客户端实例，确保整个 Web 端只维护一条“消息中心” WebSocket 连接
let stompClient: Client | null = null // 当前 STOMP 客户端实例（未连接时为 null）

// 记录当前已建立连接的用户 ID，避免同一用户重复建立连接
let connectedUserId: number | null = null // 当前通过 WebSocket 订阅消息中心的用户 ID

// 维护一个监听器数组，允许导航栏和消息中心页面等多个地方同时订阅同一条 WebSocket 通道
const listeners: Array<(event: any) => void> = [] // 保存所有注册的回调函数，每次收到事件时逐个调用

/**
 * 连接“消息中心” WebSocket，并订阅当前用户的未读消息事件
 * @param userId 当前登录用户的数字 ID
 * @param onEvent 每次接收到后端推送的事件时调用的回调函数（可选）
 */
export function connectMessageWebSocket( // 导出函数，供导航栏或消息中心页面调用建立连接
  userId: number, // 当前登录用户的 ID（后端会按此区分不同用户的推送通道）
  onEvent?: (event: any) => void // 收到服务端事件时的回调，外部用来刷新未读数或重新加载列表（可不传，仅保证连接存在）
) {
  // 如果调用方传入了回调函数，则将其加入监听器列表（避免重复添加同一个引用）
  if (onEvent && !listeners.includes(onEvent)) { // 判断回调是否存在且尚未被注册
    listeners.push(onEvent) // 将该回调加入全局监听器数组
  }

  // 如果已经为同一用户建立了连接，则直接返回，避免重复创建 WebSocket 连接
  if (stompClient && stompClient.connected && connectedUserId === userId) { // 判断当前是否已有同一用户的有效连接
    return // 已有连接时不再重复创建
  }

  // 如果存在旧连接（比如用户切换账号或刷新），先安全断开旧连接
  if (stompClient) { // 判断是否存在历史 STOMP 客户端实例
    stompClient.deactivate() // 调用 deactivate 方法优雅关闭旧连接
    stompClient = null // 将实例重置为 null，方便后续重新创建
  }

  connectedUserId = userId // 更新当前已连接的用户 ID

  // 创建一个新的 STOMP 客户端实例，底层使用 SockJS 连接后端 /ws 端点
  stompClient = new Client({ // 使用配置对象初始化 STOMP 客户端
    webSocketFactory: () => new SockJS('http://localhost:8080/ws') as any, // 通过 SockJS 创建到后端 /ws 的连接
    debug: () => {}, // 关闭调试日志输出，避免在浏览器控制台刷屏
    reconnectDelay: 5000 // 如果连接意外中断，5 秒后自动尝试重连
  })

  // 配置连接成功后的回调逻辑
  stompClient.onConnect = () => { // 当 STOMP 与后端成功建立连接时会触发该回调
    // 订阅后端为当前用户推送“消息中心事件”的专属通道：/topic/message/{userId}
    const destination = `/topic/message/${userId}` // 根据用户 ID 构造订阅目的地
    stompClient!.subscribe(destination, (frame: IMessage) => { // 调用 subscribe 方法订阅该目的地
      try { // 使用 try-catch 保证 JSON 解析异常不会影响整体连接
        const body = frame.body ? JSON.parse(frame.body) : null // 将消息体从字符串解析为对象
        if (body) { // 如果解析得到有效对象
          // 每当收到一条事件时，依次调用所有已注册的监听器回调
          listeners.forEach((fn) => { // 遍历监听器数组
            try {
              fn(body) // 将事件对象传递给每一个回调，由具体页面决定如何处理
            } catch (err) {
              console.error('处理消息中心事件回调时出错:', err) // 单个回调抛错不影响其它回调执行
            }
          })
        }
      } catch (e) { // 捕获 JSON 解析失败等异常
        console.error('解析消息中心 WebSocket 事件失败:', e) // 在控制台输出错误信息，方便排查
      }
    })
  }

  // 启动 STOMP 客户端，真正发起与后端 WebSocket 服务的连接
  stompClient.activate() // 调用 activate 后，客户端会连接 /ws 并自动触发 onConnect 回调
}

/**
 * 断开“消息中心” WebSocket 连接
 */
export function disconnectMessageWebSocket() { // 导出断开连接函数，供组件卸载或登出时调用
  if (stompClient) { // 如果当前存在 STOMP 客户端实例
    stompClient.deactivate() // 优雅关闭连接，释放资源
    stompClient = null // 将实例重置为 null
  }
  connectedUserId = null // 清空已连接的用户 ID 记录
  listeners.length = 0 // 清空所有已注册的监听器，避免内存泄漏
}

/**
 * 仅注册一个“消息中心事件”监听器，而不主动创建新连接
 * 通常由需要响应事件的页面调用（例如消息列表页），连接本身由导航栏负责创建
 * @param listener 收到事件时要执行的回调函数
 */
export function addMessageListener(listener: (event: any) => void) { // 导出添加监听器函数
  if (!listeners.includes(listener)) { // 避免重复添加相同的函数引用
    listeners.push(listener) // 将回调加入监听器数组
  }
}

/**
 * 取消注册一个“消息中心事件”监听器
 * @param listener 之前通过addMessageListener注册过的回调函数
 */
export function removeMessageListener(listener: (event: any) => void) { // 导出移除监听器函数
  const index = listeners.indexOf(listener) // 查找该回调在数组中的索引
  if (index !== -1) { // 如果存在
    listeners.splice(index, 1) // 将该监听器从数组中删除
  }
}
