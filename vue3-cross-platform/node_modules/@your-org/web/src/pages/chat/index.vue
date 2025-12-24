<template>
  <div class="chat-page">
    <NavBar />

    <div class="page-container">
      <!-- 聊天窗口 -->
      <div class="chat-container">
        <!-- 聊天头部 -->
        <div class="chat-header">
          <div class="user-info">
            <div class="user-avatar">
              <i class="fas fa-user"></i>
            </div>
            <div class="user-details">
              <h3 class="user-name">{{ chatUser.name || '用户' }}</h3>
              <span class="user-status">在线</span>
            </div>
          </div>
          <button class="btn-back" @click="goBack">
            <i class="fas fa-arrow-left"></i> 返回
          </button>
        </div>

        <!-- 消息列表 -->
        <div class="messages-container" ref="messagesContainer">
          <!-- 遍历消息列表，增加index用于时间分割线和长按菜单控制 -->
          <template v-for="(message, index) in messages" :key="message.id">
            <!-- 时间分割线：当需要展示时间分组时，在消息上方显示 -->
            <div v-if="shouldShowTime(index)" class="time-divider">
              <span class="time-divider-text">{{ message.time }}</span>
            </div>
            <!-- 单条消息气泡区域，支持长按/右键弹出菜单 -->
            <div
              :class="['message-bubble', { own: message.isOwn }]"
              @contextmenu.prevent="openContextMenu($event, message)"  <!-- 右键菜单 -->
              @mousedown="handlePressStart($event, message)"  <!-- 鼠标按下开始计时（PC端长按） -->
              @mouseup="handlePressEnd"  <!-- 鼠标松开取消计时 -->
              @mouseleave="handlePressEnd"  <!-- 鼠标移出也取消计时 -->
              @touchstart="handlePressStart($event, message)"  <!-- 触摸开始计时（移动端长按） -->
              @touchend="handlePressEnd"  <!-- 触摸结束取消计时 -->
              @touchcancel="handlePressEnd"  <!-- 触摸被中断时取消计时 -->
            >
              <div class="message-avatar" v-if="!message.isOwn">
                <i class="fas fa-user"></i>
              </div>
              <div class="message-content">
                <!-- 如果该消息已经被撤回，则只展示一行灰色提示文字 -->
                <div
                  v-if="message.recalled"
                  class="message-text message-text--recalled"
                >
                  {{ message.isOwn ? '你撤回了一条消息' : '对方撤回了一条消息' }}
                </div>
                <!-- 未撤回的消息，根据是否为图片消息展示不同内容 -->
                <template v-else>
                  <!-- 如果有图片URL，则按图片消息样式展示 -->
                  <div v-if="message.imageUrl" class="message-image">
                    <img :src="message.imageUrl" alt="图片消息" />
                  </div>
                  <!-- 否则按普通文本消息展示 -->
                  <div v-else class="message-text">
                    {{ message.content }}
                  </div>
                </template>
                <!-- 每条消息右下角的发送时间 -->
                <div class="message-time">{{ message.time }}</div>
              </div>
              <div class="message-avatar" v-if="message.isOwn">
                <i class="fas fa-user"></i>
              </div>
            </div>
          </template>
          <!-- 长按弹出的操作菜单（复制/删除），使用绝对定位显示在气泡附近 -->
          <div
            v-if="contextMenu.visible"
            class="context-menu"
            :style="{ top: contextMenu.y + 'px', left: contextMenu.x + 'px' }"
          >
            <!-- 仅当当前被选中的消息属于自己且在可撤回时间窗口内时显示“撤回”操作 -->
            <button
              v-if="contextMenu.message && canRecall(contextMenu.message)"
              class="context-menu-item"
              type="button"
              @click="recallMessage"
            >
              撤回
            </button>
            <button class="context-menu-item" type="button" @click="copyMessage">
              复制
            </button>
            <button class="context-menu-item" type="button" @click="deleteMessage">
              删除（本地）
            </button>
          </div>
        </div>

        <!-- 输入框 -->
        <div class="chat-input">
          <div class="input-toolbar">
            <button class="btn-tool" @click="showEmojiPicker = !showEmojiPicker">
              <i class="far fa-smile"></i>
            </button>
            <button class="btn-tool" @click="triggerFileInput">
              <i class="fas fa-image"></i>
            </button>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleImageUpload"
            />
          </div>
          <!-- 表情选择面板：当showEmojiPicker为true时显示，点击表情插入到输入框 -->
          <div v-if="showEmojiPicker" class="emoji-panel">
            <button
              v-for="emoji in emojiList"
              :key="emoji"
              class="emoji-btn"
              type="button"
              @click="appendEmoji(emoji)"
            >
              {{ emoji }}
            </button>
          </div>
          <div class="input-area">
            <textarea
              v-model="inputMessage"
              class="message-input"
              placeholder="输入消息..."
              rows="1"
              @keydown.enter.exact.prevent="sendMessage"
              @input="adjustTextareaHeight"
            ></textarea>
            <button class="btn-send" @click="sendMessage" :disabled="!inputMessage.trim()">
              <i class="fas fa-paper-plane"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue' // 引入Vue的响应式和生命周期方法，增加onUnmounted用于组件卸载时清理WebSocket
import { useRouter, useRoute } from 'vue-router' // 引入路由工具，方便页面跳转和读取参数
import NavBar from '@/components/common/NavBar.vue' // 引入顶部导航栏组件
import AppFooter from '@/components/common/AppFooter.vue' // 引入底部页脚组件
import FloatingMenu from '@/components/common/FloatingMenu.vue' // 引入右下角浮动菜单组件
import { request, useUserStore } from '@campus/common' // 引入公共请求工具和用户信息Store
import { connectChatWebSocket, disconnectChatWebSocket } from '@/utils/chatWebSocket' // 引入封装好的聊天 WebSocket 工具，用于建立和断开实时连接

// 创建路由实例，用于返回上一页和页面跳转
const router = useRouter() // 当前路由实例
const route = useRoute() // 当前路由信息（包含query参数等）

// 获取用户Store，用于拿到当前登录用户的ID
const userStore = useUserStore() // 获取用户状态管理实例
userStore.initUserFromStorage() // 从本地存储中初始化用户信息，防止刷新后丢失

// 聊天窗口DOM容器ref，用于滚动到底部
const messagesContainer = ref<HTMLElement | null>(null) // 聊天消息列表外层容器
// 隐藏的文件选择框ref，用于选择图片
const fileInput = ref<HTMLInputElement | null>(null) // 图片上传的文件输入框
// 文本输入框绑定的内容
const inputMessage = ref('') // 待发送的文本消息内容
// 表情选择器显示开关（暂时只控制图标高亮，不真正弹出表情面板）
const showEmojiPicker = ref(false) // 是否展示表情选择器

// 当前会话ID（后端创建的会话主键）
const conversationId = ref<number | null>(null) // 当前聊天会话ID
// 对方用户ID（比如卖家ID或聊天对象ID）
const targetUserId = ref<number | null>(null) // 聊天对象用户ID

// 聊天用户信息（聊天窗口顶部展示）
const chatUser = reactive({
  id: '', // 对方用户ID（字符串形式，方便直接拼接到请求参数）
  name: '' // 对方用户昵称或名称
})

// 定义前端展示用的单条消息类型
interface ChatMessageItem {
  id: number // 消息ID
  content: string // 消息文本内容
  time: string // 已格式化的时间字符串（用于展示和时间分割线）
  timestamp: number // 时间戳（毫秒），用于判断时间分组
  imageUrl?: string // 图片消息的图片URL（如果是图片消息则有值）
  recalled?: boolean // 是否已被撤回（前端本地状态标记）
  isOwn: boolean // 是否是当前用户发送的消息
}

// 消息列表（从后端接口加载并转换）
const messages = ref<ChatMessageItem[]>([]) // 聊天消息数组

// 常用表情列表（简单选择几个常用emoji，点击后可插入到输入框）
const emojiList = ref<string[]>([
  '😀',
  '😁',
  '😂',
  '🤣',
  '😊',
  '😍',
  '😘',
  '😎',
  '😭',
  '👍',
  '🙏',
  '🎉',
  '❤️',
  '💪'
]) // 定义一个基础的表情数组，后续可以根据需要继续扩展

// 工具函数：将原始时间转换为时间戳（毫秒）
const getTimestamp = (raw: string | Date): number => {
  const date = raw instanceof Date ? raw : new Date(raw) // 将字符串转换为Date对象
  return date.getTime() // 返回时间戳（毫秒）
}

// 工具函数：格式化时间字符串为“时:分”用于展示
const formatTime = (raw: string | Date): string => {
  const date = raw instanceof Date ? raw : new Date(raw) // 创建时间对象
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) // 返回“HH:MM”格式
}

// 从后端加载聊天消息列表
const loadMessages = async () => {
  // 如果没有会话ID且也没有目标用户ID，无法加载消息
  if (!conversationId.value && !targetUserId.value) {
    console.warn('缺少会话ID或目标用户ID，无法加载聊天记录') // 输出警告日志
    return // 直接结束函数
  }

  try {
    // 通过request.get调用后端 /api/chat/messages 接口
    const res: any = await request.get('/api/chat/messages', {
      // 所有查询参数通过params传递给后端
      params: {
        conversationId: conversationId.value || undefined, // 如果已有会话ID则传递
        targetUserId: targetUserId.value || undefined, // 如果没有会话ID则使用目标用户ID让后端自动创建会话
        page: 1, // 当前页码固定为1（简单起见一次性获取最近的若干条）
        size: 50 // 每页条数设置为50，够用且不会太大
      }
    })

    // 后端返回的数据结构中包含records和conversationId等字段
    const records: any[] = Array.isArray(res.records) ? res.records : [] // 取出消息记录数组
    if (res.conversationId) {
      conversationId.value = Number(res.conversationId) // 如果后端返回了会话ID则更新本地会话ID
    }

    // 将后端消息转换为前端展示需要的结构
    const mapped = records
      .slice() // 先复制一份数组，避免直接修改原数组
      .reverse() // 后端按时间倒序返回，这里反转为从旧到新展示
      .map((item) => {
        // 计算当前消息是否为自己发送
        const isOwn = item.senderId === userStore.userId // 比较发送者ID和当前用户ID
        const isImage = item.type === 'IMAGE' && item.imageUrl // 判断是否为图片消息且存在图片URL
        return {
          id: item.id, // 消息ID
          content: isImage ? '' : item.content || '', // 如果是图片消息则文本内容可为空，否则兜底为空字符串
          time: formatTime(item.createTime), // 使用工具函数格式化时间（仅显示小时和分钟）
          timestamp: getTimestamp(item.createTime), // 记录原始时间戳用于时间分割线判断
          imageUrl: isImage ? item.imageUrl : undefined, // 如果是图片消息则保存图片URL
          recalled: false, // 默认消息未被撤回
          isOwn // 是否是自己发送的消息
        } as ChatMessageItem // 强制断言为ChatMessageItem类型
      })

    messages.value = mapped // 更新本地消息列表

    // 尝试从消息记录中推断聊天对象信息（找出非当前用户发送的那一方）
    const other = records.find((item) => item.senderId !== userStore.userId) // 查找第一条对方发送的消息
    if (other) {
      chatUser.id = String(other.senderId) // 设置对方用户ID
      chatUser.name = other.senderName || '用户' // 设置对方昵称或默认名称
      targetUserId.value = other.senderId // 同步目标用户ID
    }

    // 消息加载完成后自动滚动到底部
    await scrollToBottom() // 调用滚动函数
  } catch (error) {
    console.error('加载聊天消息失败:', error) // 打印错误日志
  }
}

// 调用后端接口，将当前会话所有消息标记为已读
const markConversationAsRead = async () => {
  // 如果当前还没有会话ID，则无需标记
  if (!conversationId.value) {
    return // 直接返回
  }
  try {
    // 调用PUT /api/chat/conversations/{conversationId}/read 接口
    await request.put(`/api/chat/conversations/${conversationId.value}/read`)
  } catch (error) {
    console.error('标记会话消息为已读失败:', error) // 打印错误日志
  }
}

// 发送文本消息到后端并更新本地列表
const sendMessage = async () => {
  // 去除两端空格后如果内容为空，则不发送
  const content = inputMessage.value.trim() // 取出并裁剪输入内容
  if (!content) {
    return // 输入内容为空时直接返回
  }

  // 如果当前还没有确定聊天对象ID，不能发送消息
  if (!chatUser.id && !targetUserId.value) {
    alert('当前没有选定聊天对象，无法发送消息') // 弹出提示
    return // 结束函数
  }

  // 构造本地临时消息（先乐观更新到界面）
  const now = new Date() // 获取当前时间对象
  const tempMessage: ChatMessageItem = {
    id: Date.now(), // 使用当前时间戳作为临时ID
    content, // 文本内容
    time: formatTime(now), // 使用当前时间作为发送时间
    timestamp: now.getTime(), // 保存当前时间戳
    imageUrl: undefined, // 文本消息不包含图片URL
    recalled: false, // 新发送的消息默认未撤回
    isOwn: true // 标记为自己发送的消息
  }

  messages.value.push(tempMessage) // 将临时消息添加到本地列表
  inputMessage.value = '' // 清空输入框内容
  await scrollToBottom() // 滚动到底部，保证新消息可见

  try {
    // 实际调用后端发送消息接口，使用POST /api/chat/send
    const res: any = await request.post(
      '/api/chat/send', // 接口路径
      null, // 不传JSON请求体，避免与@RequestParam冲突
      {
        // 所有业务参数通过params拼到URL上
        params: {
          targetUserId: Number(chatUser.id || targetUserId.value), // 目标用户ID（优先使用chatUser.id）
          content, // 消息文本内容
          type: 'TEXT' // 消息类型固定为文本消息
        }
      }
    )

    // 如果之前没有会话ID，而后端返回了会话ID，则同步更新本地会话ID
    if (!conversationId.value && res && res.conversationId) {
      conversationId.value = Number(res.conversationId) // 更新当前会话ID
    }
  } catch (error) {
    console.error('发送消息失败:', error) // 打印错误日志
    alert('消息发送失败，请稍后重试') // 提示用户发送失败
  }
}

// 处理通过 WebSocket 收到的新聊天消息
const handleWebSocketMessage = (payload: any) => { // 定义一个回调函数，当后端通过WebSocket推送新消息时由工具调用
  // 确保收到的数据中包含必要的字段（至少要有会话ID），否则直接忽略
  if (!payload || !payload.conversationId) { // 判断基础字段是否存在
    return // 数据不完整时不做处理，避免报错
  }

  // 如果当前还没有会话ID，但收到推送中带有会话ID，则同步更新本地会话ID
  if (!conversationId.value) { // 判断本地是否尚未记录会话ID
    conversationId.value = Number(payload.conversationId) // 将后端推送的会话ID转换为数字后保存
  }

  // 只在当前页面正在查看对应会话时才追加消息（避免在查看其他页面时误插入）
  if (Number(payload.conversationId) !== conversationId.value) { // 如果推送的会话ID与当前会话不一致
    return // 直接返回，不在当前消息列表中展示
  }

  // 计算该条消息是否为当前用户自己发送（通常是对方发来的，但这里做一次防御性判断）
  const isOwn = payload.senderId === userStore.userId // 通过对比发送者ID和当前登录用户ID判断是否为自己发送

  // 如果后端未来支持通过WebSocket推送“撤回消息”事件，这里可以根据payload.action进行区分
  if (payload.action === 'RECALL' && payload.messageId) {
    // 查找本地消息列表中对应的消息，并标记为已撤回
    const target = messages.value.find((msg) => msg.id === payload.messageId) // 根据消息ID查找本地消息
    if (target) {
      target.recalled = true // 标记该消息为已撤回
      target.content = '' // 文本内容清空，实际展示由模板中的提示语控制
    }
    return // 处理完撤回逻辑后直接返回，不再按新消息追加
  }

  // 构造前端展示用的“新聊天消息”对象
  const isImage = payload.type === 'IMAGE' && payload.imageUrl // 判断是否为图片消息
  const timestamp = getTimestamp(payload.createTime || new Date()) // 计算时间戳
  const newMessage: ChatMessageItem = { // 创建一个符合 ChatMessageItem 类型的新消息对象
    id: payload.messageId || Date.now(), // 使用后端推送的消息ID，如果没有则使用时间戳兜底
    content: isImage ? '' : payload.content || '', // 如果是图片消息则文本内容可以为空
    time: formatTime(payload.createTime || new Date()), // 使用推送中的创建时间或当前时间，并格式化为“时:分”
    timestamp, // 保存原始时间戳，便于时间分割线逻辑使用
    imageUrl: isImage ? payload.imageUrl : undefined, // 如果为图片消息则保存图片URL
    recalled: false, // 新推送的消息默认未撤回
    isOwn // 是否为当前用户发送
  }

  messages.value.push(newMessage) // 将新消息追加到消息列表末尾
  scrollToBottom() // 滚动到底部，保证新消息立即可见

  // 当前会话收到新消息后，立即调用后端接口将消息标记为已读，防止未读数持续累积
  markConversationAsRead().catch((error) => { // 调用标记已读函数并捕获可能的异常
    console.error('通过 WebSocket 收到消息后标记已读失败:', error) // 在控制台打印错误信息，方便排查
  })
}

// 触发文件选择，用于准备发送图片（这里只实现选择，不真正上传）
const triggerFileInput = () => {
  fileInput.value?.click() // 调用原生click方法弹出文件选择框
}

// 处理图片选择事件（可以在这里集成图片上传后再调用发送图片消息接口）
const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement // 将事件目标断言为HTMLInputElement
  const file = target.files?.[0] // 获取用户选择的第一张图片
  if (!file) {
    return // 如果没有选择文件则直接返回
  }

  // 只处理图片类型的文件
  if (file.type.startsWith('image/')) {
    // 这里只是示例：读取为Base64，实际项目中建议上传到服务器得到URL后再发送IMAGE类型消息
    const reader = new FileReader() // 创建文件读取器
    reader.onload = () => {
      // 这里可以拿到图片的Base64数据：reader.result
      // TODO: 可在此处调用专门的图片上传接口，拿到图片URL后使用type=IMAGE调用发送消息接口
      alert('图片选择成功，后续可接入图片上传与发送逻辑') // 先给出简单提示
    }
    reader.readAsDataURL(file) // 将文件读取为Base64字符串
  }
}

// 调整文本输入框高度以适配多行输入
const adjustTextareaHeight = (event: Event) => {
  const textarea = event.target as HTMLTextAreaElement // 将事件目标断言为文本域元素
  textarea.style.height = 'auto' // 先重置高度，避免高度无限增大
  textarea.style.height = `${Math.min(textarea.scrollHeight, 120)}px` // 根据内容自动调整高度，上限为120px
}

// 滚动聊天窗口到底部，保证最新消息显示在视野中
const scrollToBottom = async () => {
  await nextTick() // 等待DOM更新完成
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight // 将滚动条位置设置为底部
  }
}

// 点击“返回”按钮回到上一页
const goBack = () => {
  router.back() // 使用路由返回上一页面
}

// 监听消息列表变化，自动滚动到底部
watch(
  messages, // 监听的目标是messages这个ref
  () => {
    scrollToBottom() // 每当消息数组变动时滚动到底部
  },
  { deep: true } // 深度监听，确保数组内部元素变化也会触发
)

// 根据当前消息索引判断是否需要显示时间分割线
const shouldShowTime = (index: number): boolean => {
  // 第一条消息一定显示时间
  if (index === 0) {
    return true // 返回true表示需要展示时间分割线
  }
  const current = messages.value[index] // 当前消息
  const previous = messages.value[index - 1] // 前一条消息
  if (!current || !previous) {
    return false // 如果数据异常则不显示
  }
  const diff = current.timestamp - previous.timestamp // 计算时间差（毫秒）
  // 当两条消息间隔超过5分钟时，显示时间分割线
  return diff > 5 * 60 * 1000 // 超过5分钟则显示时间
}

// 长按菜单相关状态：记录是否显示、位置以及当前选中的消息
const contextMenu = reactive({
  visible: false, // 是否显示菜单
  x: 0, // 菜单在页面中的X坐标
  y: 0, // 菜单在页面中的Y坐标
  message: null as ChatMessageItem | null // 当前被操作的消息
})

// 长按计时器ID，用于识别长按行为
const pressTimer = ref<number | null>(null) // 保存setTimeout返回的定时器ID

// 打开右键/长按菜单的通用函数
const openContextMenu = (event: MouseEvent | TouchEvent, message: ChatMessageItem) => {
  let clientX = 0 // 菜单位置X坐标
  let clientY = 0 // 菜单位置Y坐标

  // 根据是鼠标事件还是触摸事件分别取坐标
  if (event instanceof MouseEvent) {
    clientX = event.clientX // 鼠标事件直接读取clientX
    clientY = event.clientY // 鼠标事件直接读取clientY
  } else if (event.touches && event.touches[0]) {
    clientX = event.touches[0].clientX // 触摸事件读取第一个触点的X坐标
    clientY = event.touches[0].clientY // 触摸事件读取第一个触点的Y坐标
  }

  contextMenu.visible = true // 显示菜单
  contextMenu.x = clientX // 设置菜单X位置
  contextMenu.y = clientY // 设置菜单Y位置
  contextMenu.message = message // 记录当前操作的消息
}

// 处理按下开始事件（用于识别长按）
const handlePressStart = (event: MouseEvent | TouchEvent, message: ChatMessageItem) => {
  // 每次开始前先清理上一次的计时器
  handlePressEnd() // 确保没有残留的定时任务
  // 设置一个定时器，当按住超过600ms时认为是长按
  const timerId = window.setTimeout(() => {
    openContextMenu(event, message) // 调用打开菜单函数
  }, 600) // 600毫秒长按触发
  pressTimer.value = timerId // 保存定时器ID，方便后续清理
}

// 处理按下结束事件（鼠标抬起、触摸结束等）
const handlePressEnd = () => {
  if (pressTimer.value !== null) {
    window.clearTimeout(pressTimer.value) // 清除未触发的长按定时器
    pressTimer.value = null // 重置为null
  }
}

// 复制当前选中消息的内容到剪贴板
const copyMessage = async () => {
  if (!contextMenu.message) {
    return // 如果没有选中消息则直接返回
  }
  try {
    // 优先使用现代浏览器的剪贴板API
    await navigator.clipboard.writeText(contextMenu.message.content) // 将消息内容写入剪贴板
    alert('已复制到剪贴板') // 提示复制成功
  } catch (error) {
    console.error('复制失败:', error) // 输出错误信息
    alert('复制失败，请手动选择文本复制') // 提示用户手动复制
  } finally {
    contextMenu.visible = false // 无论成功与否，关闭菜单
  }
}

// 从当前列表中删除选中的消息（仅本地删除，不影响后端）
const deleteMessage = () => {
  if (!contextMenu.message) {
    return // 如果没有选中消息则直接返回
  }
  // 通过过滤当前消息数组，实现本地删除效果
  messages.value = messages.value.filter((msg) => msg.id !== contextMenu.message?.id) // 过滤掉被删除的消息
  contextMenu.visible = false // 删除后关闭菜单
}

// 判断某条消息当前是否满足“可以撤回”的条件（前端本地判断）
const canRecall = (message: ChatMessageItem): boolean => {
  if (!message.isOwn) {
    return false // 仅允许撤回自己发送的消息
  }
  if (message.recalled) {
    return false // 已经撤回过的消息不能再次撤回
  }
  const now = Date.now() // 当前时间戳
  const diff = now - message.timestamp // 计算当前时间与消息发送时间的差值
  // 仅允许在发送后2分钟内撤回（120000毫秒），模拟微信/闲鱼的撤回时间限制
  return diff <= 2 * 60 * 1000 // 在2分钟内返回true，表示可撤回
}

// 撤回当前通过菜单选中的消息（仅前端本地效果，不影响后端数据）
const recallMessage = () => {
  if (!contextMenu.message) {
    return // 如果没有选中消息则直接返回
  }
  const target = contextMenu.message // 取出被选中的消息对象
  if (!canRecall(target)) {
    contextMenu.visible = false // 如果已经不满足撤回条件，则直接关闭菜单
    return // 结束函数，不做任何修改
  }
  target.recalled = true // 将该消息标记为已撤回
  target.content = '' // 将原始文本内容清空（界面展示统一由提示语代替）
  contextMenu.visible = false // 操作完成后关闭菜单
}

// 组件挂载完成后初始化会话信息、加载消息并建立 WebSocket 连接
onMounted(() => {
  // 从路由参数中读取可能传入的conversationId和targetUserId（兼容sellerId）
  const query = route.query // 获取当前路由的query参数
  const conversationIdParam = query.conversationId as string | undefined // 会话ID字符串
  const targetUserParam =
    (query.targetUserId as string | undefined) || (query.sellerId as string | undefined) // 目标用户ID（兼容sellerId）

  // 如果路由中带有会话ID，则转换为数字后保存
  if (conversationIdParam) {
    conversationId.value = Number(conversationIdParam) // 将字符串转换为数字并保存
  }

  // 如果路由中带有目标用户ID，则转换为数字并同步到chatUser和targetUserId
  if (targetUserParam) {
    const idNum = Number(targetUserParam) // 将目标用户ID字符串转换为数字
    targetUserId.value = idNum // 保存为目标用户ID
    chatUser.id = String(idNum) // 更新聊天对象ID（字符串形式）
    chatUser.name = '用户' // 暂时使用通用名称，后续可根据业务接口补充真实昵称
  }

  // 在组件挂载后，先加载一次历史聊天记录并标记为已读
  loadMessages() // 加载聊天记录
    .then(() => {
      return markConversationAsRead() // 加载完成后标记为已读
    })
    .catch((error) => {
      console.error('初始化聊天页面失败:', error) // 打印初始化错误
    })

  // 如果当前已经登录（存在用户ID），则建立 WebSocket 连接，订阅实时聊天消息
  if (userStore.userId) { // 判断用户状态中是否存在有效的用户ID
    connectChatWebSocket(userStore.userId, handleWebSocketMessage) // 调用工具函数发起连接，并指定收到新消息时的回调
  }
})

// 组件卸载前断开 WebSocket 连接，避免页面切换后仍然占用连接资源
onUnmounted(() => {
  disconnectChatWebSocket() // 调用工具方法断开聊天 WebSocket 连接
})
</script>

<style scoped>
:root {
  --primary: #d81b60; /* 主色调整为项目统一的偏粉色主题色 */
  --bg: #f5e9f0; /* 页面背景使用带一点粉调的浅色，保持柔和校园氛围 */
}

/* 整个聊天页面外层容器，控制整体背景和高度 */
.chat-page {
  min-height: 100vh; /* 页面高度至少占满一屏 */
  background: var(--bg); /* 使用上方定义的浅灰背景，接近微信效果 */
  padding-bottom: 0; /* 去掉多余底部留白，让聊天区域更紧凑 */
}

/* 聊天页面中间内容区域，限制最大宽度居中显示 */
.page-container {
  max-width: 900px; /* 最大宽度略微收窄，类似PC版微信的对话宽度 */
  margin: 0 auto; /* 水平居中 */
  padding: 0 16px; /* 左右适当留白 */
}

/* 聊天主容器，包含头部、消息列表和输入区域 */
.chat-container {
  background: transparent; /* 去掉白色卡片感，让聊天区域与背景更融合 */
  border-radius: 0; /* 去掉圆角，整体更接近微信的直角风格 */
  box-shadow: none; /* 去掉阴影效果 */
  border: none; /* 去掉边框线 */
  display: flex; /* 使用flex垂直排列头部、消息和输入栏 */
  flex-direction: column; /* 垂直方向布局 */
  height: calc(100vh - 80px); /* 高度占满一屏，预留顶部导航高度 */
  min-height: 500px; /* 设置一个较小的最小高度，适配小屏幕 */
}

/* 顶部聊天标题栏，类似微信的聊天顶部信息 */
.chat-header {
  display: flex; /* 横向排列头像和返回按钮 */
  justify-content: space-between; /* 两端对齐 */
  align-items: center; /* 垂直居中 */
  padding: 12px 0; /* 上下适当内边距 */
  border-bottom: 1px solid #d6d6d6; /* 底部使用浅灰实线分隔 */
  background-color: #f7f7f7; /* 顶部背景为浅灰色，接近微信样式 */
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.user-status {
  font-size: 12px;
  color: #52c41a;
}

.btn-back {
  padding: 8px 16px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-back:hover {
  background: #f9f0ff;
  border-color: var(--primary);
}

/* 消息列表容器，占据中间可滚动区域 */
.messages-container {
  flex: 1; /* 占满除头部和输入栏以外的剩余高度 */
  overflow-y: auto; /* 竖向滚动查看历史消息 */
  padding: 16px 0; /* 上下内边距 */
  display: flex; /* 使用flex垂直排列每一条消息 */
  flex-direction: column; /* 垂直方向排列消息气泡 */
  gap: 10px; /* 消息之间的间距 */
}

/* 单条消息外层容器，包含头像和气泡 */
.message-bubble {
  display: flex; /* 横向排列头像和文本气泡 */
  gap: 8px; /* 头像和气泡之间的间距 */
  align-items: flex-end; /* 底部对齐，气泡底部对齐 */
}

/* 自己发送的消息在右侧显示 */
.message-bubble.own {
  flex-direction: row-reverse; /* 反转排列方向，使头像和气泡靠右 */
}

/* 聊天头像区域，显示对方或自己的默认头像图标 */
.message-avatar {
  width: 32px; /* 头像宽度 */
  height: 32px; /* 头像高度 */
  border-radius: 4px; /* 轻微圆角，类似微信方形头像效果 */
  background: #dedede; /* 浅灰背景色 */
  display: flex; /* 使用flex使图标居中 */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  color: #666; /* 图标颜色 */
  font-size: 14px; /* 图标大小 */
  flex-shrink: 0; /* 防止头像被压缩 */
}

/* 消息文本区域，包含文本和时间 */
.message-content {
  max-width: 70%; /* 限制单条消息的最大宽度，避免过宽 */
  display: flex; /* 使用flex垂直排列文本和时间 */
  flex-direction: column; /* 垂直方向布局 */
  gap: 4px; /* 文本与时间之间间距 */
}

/* 自己发送的消息文本内容右对齐 */
.message-bubble.own .message-content {
  align-items: flex-end; /* 右对齐文本和时间 */
}

/* 聊天气泡本体的样式 */
.message-text {
  padding: 8px 10px; /* 气泡内边距 */
  border-radius: 4px; /* 圆角大小接近微信的矩形气泡 */
  font-size: 14px; /* 文本字号 */
  line-height: 1.6; /* 行高让文本更易读 */
  word-wrap: break-word; /* 长单词或长链接自动换行 */
  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.15); /* 轻微阴影增加立体感 */
}

/* 对方消息气泡样式（左侧，白色背景） */
.message-bubble:not(.own) .message-text {
  background: #ffffff; /* 左侧消息使用白色气泡 */
  color: #000; /* 文字使用黑色增强对比度 */
}

/* 自己消息气泡样式（右侧，粉色背景，符合项目主题色） */
.message-bubble.own .message-text {
  background: linear-gradient(135deg, #ff80ab 0%, #d81b60 100%); /* 使用从浅粉到主题粉的渐变色 */
  color: #ffffff; /* 文字使用白色，与粉色背景形成清晰对比 */
}

.message-time {
  font-size: 11px;
  color: #999;
  padding: 0 4px;
}

/* 被撤回消息的气泡样式，弱化背景和阴影，仅保留文字提示 */
.message-text--recalled {
  background: transparent !important; /* 撤回提示不需要彩色或白色背景，设为透明 */
  box-shadow: none !important; /* 去掉阴影，让其看起来更像系统提示 */
  color: #999; /* 使用中性灰色文字 */
  font-size: 12px; /* 字号略小，弱化存在感 */
}

/* 时间分割线容器，位于消息列表中间，居中显示时间 */
.time-divider {
  display: flex; /* 使用flex使时间分割线居中 */
  justify-content: center; /* 水平居中显示 */
  margin: 8px 0; /* 上下留出一定间距区分不同时间段 */
}

/* 时间分割线中文字样式 */
.time-divider-text {
  font-size: 12px; /* 稍小的字号 */
  color: #888; /* 中性灰色文字 */
  background-color: rgba(255, 255, 255, 0.8); /* 半透明白色背景 */
  padding: 2px 8px; /* 内边距让文字不显得拥挤 */
  border-radius: 12px; /* 胶囊型圆角 */
}

/* 图片消息外层容器，控制图片最大宽度和圆角 */
.message-image {
  max-width: 220px; /* 限制图片显示区域的最大宽度，防止过大撑破布局 */
  border-radius: 6px; /* 图片外层容器使用轻微圆角 */
  overflow: hidden; /* 超出圆角区域的图片裁剪掉 */
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2); /* 给图片添加轻微阴影增加层次感 */
}

/* 图片消息中的实际图片样式 */
.message-image img {
  display: block; /* 将图片设为块级元素，去掉底部缝隙 */
  width: 100%; /* 图片宽度自适应容器宽度 */
  height: auto; /* 图片高度按比例缩放 */
  cursor: pointer; /* 鼠标悬停时显示为可点击手型，暗示可预览放大 */
}

/* 长按/右键弹出的操作菜单外层容器 */
.context-menu {
  position: fixed; /* 固定定位，基于视口位置 */
  z-index: 1000; /* 设置较高层级，防止被其他元素遮挡 */
  background-color: #ffffff; /* 菜单背景使用白色 */
  border-radius: 8px; /* 适度圆角，符合现代UI风格 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); /* 较明显阴影突出菜单 */
  padding: 4px 0; /* 上下适当留白 */
  min-width: 120px; /* 设置最小宽度 */
}

/* 菜单中每一项按钮样式 */
.context-menu-item {
  width: 100%; /* 按钮宽度占满菜单容器 */
  padding: 6px 12px; /* 内边距让点击区域更大 */
  border: none; /* 去掉默认边框 */
  background: transparent; /* 背景默认透明 */
  text-align: left; /* 文本靠左对齐 */
  font-size: 14px; /* 字号 */
  color: #333; /* 默认文字颜色 */
  cursor: pointer; /* 鼠标变为可点击形状 */
}

/* 悬停时的菜单项高亮效果 */
.context-menu-item:hover {
  background-color: #f5f5f5; /* 使用浅灰色背景表示悬停 */
}

/* 底部输入区域外层容器，包含工具栏和输入框 */
.chat-input {
  border-top: 1px solid #d6d6d6; /* 顶部使用浅灰分隔线 */
  padding: 8px 0; /* 上下内边距 */
  background-color: #f7f7f7; /* 底部背景为浅灰色，类似微信输入栏 */
}

/* 输入栏上方的小工具栏，包含表情和图片按钮 */
.input-toolbar {
  display: flex; /* 横向排列按钮 */
  gap: 8px; /* 按钮之间间距 */
  margin-bottom: 6px; /* 底部留出空间给输入框 */
}

/* 输入栏工具按钮样式，例如表情和图片按钮 */
.btn-tool {
  width: 28px; /* 按钮宽度 */
  height: 28px; /* 按钮高度 */
  border-radius: 4px; /* 轻微圆角 */
  border: none; /* 去掉边框线 */
  background: #f0f0f0; /* 浅灰背景 */
  color: #666; /* 图标颜色 */
  cursor: pointer; /* 鼠标样式为可点击 */
  display: flex; /* 使用flex使图标居中 */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  transition: all 0.2s; /* 悬停时平滑过渡效果 */
}

/* 工具按钮悬停时的高亮效果 */
.btn-tool:hover {
  background: #e0e0e0; /* 悬停时背景稍微变深 */
  color: var(--primary); /* 图标使用主色高亮 */
}

/* 输入区域主容器，包含文本输入框和发送按钮 */
.input-area {
  display: flex; /* 横向排列输入框和发送按钮 */
  gap: 8px; /* 中间留出空隙 */
  align-items: flex-end; /* 底部对齐 */
}

/* 文本输入框样式 */
.message-input {
  flex: 1; /* 占据除发送按钮外的所有可用宽度 */
  padding: 8px 10px; /* 内边距 */
  border: 1px solid #d6d6d6; /* 浅灰细边框 */
  border-radius: 4px; /* 轻微圆角，类似微信输入框 */
  font-size: 14px; /* 文字大小 */
  resize: none; /* 禁止用户拖动改变高度 */
  max-height: 120px; /* 限制最大高度 */
  transition: border-color 0.2s, box-shadow 0.2s; /* 焦点状态的过渡动画 */
}

/* 文本输入框聚焦时的高亮效果 */
.message-input:focus {
  outline: none; /* 去掉默认的外轮廓线 */
  border-color: var(--primary); /* 使用主色高亮边框 */
  box-shadow: 0 0 0 1px rgba(216, 27, 96, 0.25); /* 使用主题粉色的淡外阴影 */
}

/* 发送按钮样式（参考闲鱼等风格，使用主题色实心矩形按钮） */
.btn-send {
  width: 40px; /* 按钮宽度 */
  height: 32px; /* 按钮高度 */
  border-radius: 4px; /* 圆角矩形按钮 */
  border: none; /* 去掉边框线 */
  background: var(--primary); /* 使用主色作为背景色 */
  color: white; /* 文字或图标使用白色 */
  cursor: pointer; /* 鼠标变为可点击形状 */
  display: flex; /* 使用flex使图标居中 */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  transition: opacity 0.2s, transform 0.1s; /* 悬停和点击的过渡动画 */
  flex-shrink: 0; /* 防止按钮被压缩 */
}

/* 发送按钮悬停时的效果（仅在未禁用时生效） */
.btn-send:hover:not(:disabled) {
  opacity: 0.9; /* 稍微降低透明度，形成悬停效果 */
  transform: translateY(-1px); /* 轻微上移增加动感 */
}

/* 禁用状态下的发送按钮样式 */
.btn-send:disabled {
  opacity: 0.5; /* 降低透明度表示不可用 */
  cursor: not-allowed; /* 鼠标样式改为禁止 */
}

@media (max-width: 768px) {
  .chat-container {
    height: calc(100vh - 150px);
  }

  .message-content {
    max-width: 85%;
  }
}
</style>
