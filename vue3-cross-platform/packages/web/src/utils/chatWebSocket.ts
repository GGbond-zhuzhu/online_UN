import SockJS from 'sockjs-client' // 引入 SockJS 客户端，用于创建兼容性的 WebSocket 连接
import { Client, IMessage } from '@stomp/stompjs' // 引入 STOMP 客户端类型，用于在 WebSocket 之上收发消息

// 全局保存一个 STOMP 客户端实例，避免重复创建连接
let stompClient: Client | null = null // 当前 STOMP 客户端对象（未连接时为 null）

// 记录已经建立连接的用户ID，避免同一用户重复连接
let connectedUserId: number | null = null // 当前通过 WebSocket 连接的用户 ID

/**
 * 连接聊天 WebSocket，并订阅当前用户的聊天消息
 * @param userId 当前登录用户的 ID，用于订阅专属的聊天通道
 * @param onMessage 收到新消息时的回调函数，页面传入用于更新界面
 */
export function connectChatWebSocket( // 导出一个函数，用于在页面中发起 WebSocket 连接
  userId: number, // 当前登录用户的数字 ID
  onMessage: (msg: any) => void // 收到服务端推送的新消息时调用的回调函数
) {
  // 如果已经为同一个用户建立了连接，则直接返回，避免重复连接
  if (stompClient && stompClient.connected && connectedUserId === userId) { // 判断是否已有连接且用户一致
    return // 已经连接，无需再次创建
  }

  // 如果存在旧连接但用户ID不同或已断开，则先安全断开旧连接
  if (stompClient) { // 判断是否存在旧的 STOMP 客户端实例
    stompClient.deactivate() // 调用 deactivate 方法关闭旧的 WebSocket 连接
    stompClient = null // 将客户端实例重置为 null
  }

  connectedUserId = userId // 更新当前连接的用户 ID

  // 创建一个新的 STOMP 客户端实例
  stompClient = new Client({ // 使用配置对象初始化 STOMP 客户端
    // 指定如何创建底层 WebSocket 连接，这里通过 SockJS 连接到后端 /ws 端点
    webSocketFactory: () => new SockJS('http://localhost:8080/ws') as any, // 返回一个 SockJS 实例，连接后端 WebSocket 服务
    debug: () => {}, // 关闭 STOMP 自带的调试日志，避免在控制台刷屏
    reconnectDelay: 5000 // 如果连接意外断开，5 秒后自动重连
  })

  // 配置连接成功后的回调逻辑
  stompClient.onConnect = () => { // 当 STOMP 客户端与服务端成功建立连接时会触发该回调
    // 订阅后端为当前用户推送聊天消息的通道，格式为 /topic/chat/{userId}
    const destination = `/topic/chat/${userId}` // 根据当前用户 ID 拼接订阅目的地路径
    stompClient!.subscribe(destination, (frame: IMessage) => { // 调用 subscribe 订阅该目的地，并处理收到的每一条消息
      try { // 使用 try-catch 保证解析失败不会导致整个回调崩溃
        const body = frame.body ? JSON.parse(frame.body) : null // 将消息体字符串解析为 JSON 对象
        if (body) { // 如果解析结果存在
          onMessage(body) // 调用页面传入的回调，将消息交给页面处理和展示
        }
      } catch (e) { // 捕获 JSON 解析过程中可能出现的异常
        console.error('解析 WebSocket 消息失败:', e) // 在控制台输出错误信息，方便排查问题
      }
    })
  }

  // 启动 STOMP 客户端，真正发起与后端的连接
  stompClient.activate() // 调用 activate 方法后，客户端会尝试建立 WebSocket 连接并触发 onConnect
}

/**
 * 断开聊天 WebSocket 连接
 */
export function disconnectChatWebSocket() { // 导出一个函数，供页面在销毁时主动断开连接
  if (stompClient) { // 如果当前存在 STOMP 客户端实例
    stompClient.deactivate() // 调用 deactivate 方法优雅关闭连接
    stompClient = null // 重置客户端实例为 null，方便下次重新连接
  }
  connectedUserId = null // 同时清空已连接的用户 ID 记录
}
