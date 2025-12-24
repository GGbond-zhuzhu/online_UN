<template>
  <div class="messages-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-comments"></i> 消息中心
        </h1>
        <p class="page-subtitle">查看所有消息和通知</p>
      </section>

      <!-- 消息分类 -->
      <section class="message-tabs">
        <button
          :class="['tab-btn', { active: activeTab === 'all' }]"
          @click="activeTab = 'all'"
        >
          全部
        </button>
        <button
          :class="['tab-btn', { active: activeTab === 'system' }]"
          @click="activeTab = 'system'"
        >
          系统通知
        </button>
        <button
          :class="['tab-btn', { active: activeTab === 'parttime' }]"
          @click="activeTab = 'parttime'"
        >
          兼职消息
        </button>
        <button
          :class="['tab-btn', { active: activeTab === 'secondhand' }]"
          @click="activeTab = 'secondhand'"
        >
          二手交易
        </button>
      </section>

      <!-- 消息列表 -->
      <section class="messages-section">
        <div v-if="filteredMessages.length === 0" class="empty-state">
          <i class="fas fa-comments"></i>
          <h3>暂无消息</h3>
          <p>您还没有收到任何消息</p>
        </div>
        <div v-else class="messages-list">
          <div
            v-for="message in filteredMessages"
            :key="message.id"
            :class="['message-item', { unread: !message.read }]"
            @click="viewMessage(message)"
          >
            <div class="message-icon">
              <i :class="message.icon"></i>
            </div>
            <div class="message-content">
              <div class="message-header">
                <h3 class="message-title">{{ message.title }}</h3>
                <span class="message-time">{{ message.time }}</span>
              </div>
              <p class="message-preview">{{ message.preview }}</p>
            </div>
            <div v-if="!message.read" class="unread-badge"></div>
          </div>
        </div>
      </section>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue' // 引入响应式变量、计算属性和生命周期方法（增加onUnmounted用于清理监听器）
import { useRouter } from 'vue-router' // 引入路由工具，用于页面跳转
import NavBar from '@/components/common/NavBar.vue' // 顶部导航栏组件
import AppFooter from '@/components/common/AppFooter.vue' // 底部页脚组件
import FloatingMenu from '@/components/common/FloatingMenu.vue' // 右下角浮动菜单组件
import { request, useUserStore } from '@campus/common' // 引入统一封装的请求工具和用户状态Store
import { connectMessageWebSocket, addMessageListener, removeMessageListener } from '@/utils/messageWebSocket' // 引入消息中心 WebSocket 工具，用于在有新通知时自动刷新列表

// 消息类型在前端使用的小写字符串，方便和标签值对应
type FrontMessageType = 'system' | 'parttime' | 'secondhand' // 三种业务类型

// 单条消息在前端展示用的数据结构
interface MessageItem {
  id: number // 消息ID
  type: FrontMessageType // 前端使用的小写消息类型
  title: string // 消息标题
  preview: string // 消息内容预览
  time: string // 已格式化的时间字符串
  read: boolean // 是否已读
  icon: string // 列表左侧图标类名
}

// 创建路由实例，用于点击消息后跳转不同页面
const router = useRouter() // 路由实例
// 获取用户Store，用于判断当前是否已登录以及获取用户ID
const userStore = useUserStore() // 用户状态管理实例

// 当前激活的标签（全部/系统/兼职/二手）
const activeTab = ref<'all' | FrontMessageType>('all') // 默认展示全部消息

// 消息列表数据，由后端接口加载
const messages = ref<MessageItem[]>([]) // 当前页面展示的消息数组

// 加载状态标记，后续可用于显示“加载中”状态
const loading = ref(false) // 是否正在加载消息

// 分页参数：当前页码和每页大小
const page = ref(1) // 当前页码
const size = ref(20) // 每页条数

// 工具函数：根据后端返回的类型映射为前端类型和对应图标
const mapTypeAndIcon = (
  backendType: string // 后端返回的消息类型字符串
): { type: FrontMessageType; icon: string } => {
  const upper = (backendType || '').toUpperCase() // 将类型统一转为大写
  if (upper === 'PARTTIME') {
    return { type: 'parttime', icon: 'fas fa-briefcase' } // 兼职消息使用公文包图标
  }
  if (upper === 'SECONDHAND') {
    return { type: 'secondhand', icon: 'fas fa-shopping-bag' } // 二手交易消息使用购物袋图标
  }
  // 默认归类为系统消息
  return { type: 'system', icon: 'fas fa-bell' } // 系统消息使用铃铛图标
}

// 工具函数：格式化时间字符串为更友好的展示形式
const formatTime = (raw: string): string => {
  if (!raw) {
    return '' // 如果时间为空则返回空字符串
  }
  const date = new Date(raw) // 将后端返回的时间字符串转换为Date对象
  return date.toLocaleString('zh-CN', { hour12: false }) // 使用本地时间格式显示（不使用12小时制）
}

// 将后端单条消息数据转换为前端MessageItem结构
const mapBackendMessage = (item: any): MessageItem => {
  const { type, icon } = mapTypeAndIcon(item.type || 'SYSTEM') // 将后端类型转换为前端类型和图标
  const content: string = item.content || '' // 完整消息内容
  // 截取部分内容作为预览，过长时加省略号
  const preview =
    content.length > 50 ? `${content.slice(0, 50)}...` : content // 预览最长50个字符
  return {
    id: item.id, // 消息ID
    type, // 前端小写类型
    title: item.title || '消息通知', // 消息标题（为空时使用默认标题）
    preview, // 消息预览内容
    time: formatTime(item.createTime), // 格式化后的时间字符串
    read: !!item.read, // 将后端的read字段转换为布尔值
    icon // 图标类名
  }
}

// 计算属性：当前实际要展示的消息列表
const filteredMessages = computed(() => {
  // 由于请求接口时已经传了type参数，这里可以直接返回全部messages
  return messages.value // 直接返回消息数组
})

// 从后端加载消息列表，根据当前标签和分页参数
const loadMessages = async () => {
  try {
    loading.value = true // 设置加载状态为true

    // 将前端标签值转换为后端需要的type参数（大写）
    const backendType =
      activeTab.value === 'all'
        ? 'ALL' // 全部消息使用ALL
        : activeTab.value.toUpperCase() // 其他类型转为大写SYSTEM/PARTTIME/SECONDHAND

    // 调用GET /api/messages/list 接口获取消息列表
    const res: any = await request.get('/api/messages/list', {
      params: {
        type: backendType, // 消息类型
        page: page.value, // 当前页码
        size: size.value // 每页条数
      }
    })

    const records: any[] = Array.isArray(res.records) ? res.records : [] // 从响应中取出records数组
    messages.value = records.map(mapBackendMessage) // 将后端数据映射为前端MessageItem数组
  } catch (error) {
    console.error('加载消息列表失败:', error) // 打印错误日志
  } finally {
    loading.value = false // 无论成功或失败都重置加载状态
  }
}

// 查看消息时的处理逻辑：标记为已读并跳转到对应页面或弹出详情
const viewMessage = async (message: MessageItem) => {
  try {
    // 如果消息当前为未读状态，则先调用后端接口标记为已读
    if (!message.read) {
      await request.put(`/api/messages/${message.id}/read`) // 调用PUT /api/messages/{id}/read 接口
      message.read = true // 本地同步更新为已读
    }
  } catch (error) {
    console.error('标记消息为已读失败:', error) // 打印错误日志
  }

  // 根据不同类型的消息跳转到对应的业务页面
  switch (message.type) {
    case 'parttime':
      router.push('/parttime/applications') // 兼职消息跳转到“我的兼职申请”页面
      break
    case 'secondhand':
      router.push('/secondhand/messages') // 二手交易消息跳转到二手交易消息页面
      break
    default:
      // 系统消息暂时使用浏览器弹窗展示内容
      alert(message.preview || message.title) // 弹出消息内容或标题
      break
  }
}

// 保存当前注册到 WebSocket 的事件处理函数引用，便于组件卸载时移除监听器
let messageWsHandler: ((event: any) => void) | null = null // 初始为null，实际注册时赋值

// 组件挂载完成后，默认加载“全部”类型的消息列表，并接入消息中心 WebSocket 事件
onMounted(() => {
  loadMessages() // 首次进入页面时加载消息列表

  // 如果当前用户已登录，则将消息中心页面也接入 WebSocket 事件流
  if (userStore.isLoggedIn && userStore.userId) { // 判断是否存在有效用户ID
    // 确保底层WebSocket连接已建立（如果导航栏已经建立，这里会直接返回，不会重复建立）
    connectMessageWebSocket(userStore.userId) // 只保证连接存在，不强制传入回调

    // 定义本页面专用的事件处理函数：每次有“未读状态变化”时重新加载一次列表
    messageWsHandler = () => {
      // 保持当前标签（全部/系统/兼职/二手）不变，只重新根据当前标签调用接口
      loadMessages() // 重新从后端拉取当前类型的消息列表
    }

    // 将该事件处理函数注册到全局消息中心监听器中
    addMessageListener(messageWsHandler) // 注册本页面的消息中心事件回调
  }
})

// 组件卸载时，移除本页面注册的WebSocket事件监听器，避免内存泄漏
onUnmounted(() => {
  if (messageWsHandler) { // 如果之前注册过事件处理函数
    removeMessageListener(messageWsHandler) // 从监听器数组中移除该函数
    messageWsHandler = null // 将本地引用重置为null
  }
})

// 监听标签切换，当用户点击不同标签时重新加载对应类型的消息
watch(
  activeTab, // 监听当前激活的标签值
  () => {
    page.value = 1 // 切换标签时重置页码为1
    loadMessages() // 重新调用接口加载消息列表
  }
)
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.messages-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-title i {
  color: var(--primary);
}

.page-subtitle {
  font-size: 16px;
  color: #666;
}

.message-tabs {
  display: flex;
  gap: 10px;
  background: white;
  padding: 10px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  margin-bottom: 30px;
}

.tab-btn {
  flex: 1;
  padding: 10px 20px;
  border: none;
  background: transparent;
  color: #666;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
}

.tab-btn:hover {
  background: #f9f0ff;
}

.tab-btn.active {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
}

.messages-section {
  margin-bottom: 30px;
}

.empty-state {
  background: white;
  border-radius: 12px;
  padding: 60px 20px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.empty-state i {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 10px;
}

.empty-state p {
  color: #666;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  gap: 15px;
  position: relative;
}

.message-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.15);
}

.message-item.unread {
  background: #fff5f9;
  border-color: var(--primary);
}

.message-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #f9f0ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary);
  font-size: 20px;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.message-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.message-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.message-preview {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.unread-badge {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--primary);
  flex-shrink: 0;
  margin-top: 5px;
}

@media (max-width: 768px) {
  .message-tabs {
    flex-wrap: wrap;
  }

  .message-header {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
