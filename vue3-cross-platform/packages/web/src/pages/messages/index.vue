<template>
  <div class="messages-page">
    <NavBar />

    <div class="messages-container">
      <!-- 左侧会话列表区域 -->
      <div class="conversations-panel">
        <!-- 顶部工具栏 -->
        <div class="panel-header">
          <div class="header-title">
            <h2>消息</h2>
          </div>
          <div class="header-actions">
            <button class="action-btn" @click="showCreateGroup = true" title="创建群聊">
              <i class="fas fa-plus"></i>
            </button>
            <button class="action-btn" @click="toggleSearch" title="搜索">
              <i class="fas fa-search"></i>
            </button>
            <button class="action-btn" @click="toggleFilter" title="筛选">
              <i class="fas fa-filter"></i>
            </button>
          </div>
        </div>

        <!-- 搜索栏（可展开） -->
        <div v-if="showSearchBar" class="search-bar">
          <div class="search-input-wrapper">
            <i class="fas fa-search search-icon"></i>
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索消息、联系人..."
              class="search-input"
              @input="handleSearchInput"
            />
            <button v-if="searchKeyword" class="search-clear" @click="clearSearch">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>

        <!-- 筛选标签 -->
        <div v-if="showFilterBar" class="filter-bar">
          <div class="filter-tabs">
            <div
              v-for="(tab, idx) in filterTabs"
              :key="idx"
              class="filter-tab"
              :class="{ active: activeFilterTab === idx }"
              @click="handleFilterTabChange(idx)"
            >
              {{ tab.name }}
            </div>
          </div>
        </div>

        <!-- 会话列表 -->
        <div class="conversations-list">
          <div v-if="loading" class="loading-state">
            <i class="fas fa-spinner fa-spin"></i>
            <span>加载中...</span>
          </div>
          <div v-else-if="filteredConversations.length === 0" class="empty-state">
            <i class="fas fa-comments"></i>
            <h3>{{ searchKeyword || activeFilterTab > 0 ? '暂无匹配的聊天记录' : '暂无聊天记录' }}</h3>
            <p>{{ searchKeyword || activeFilterTab > 0 ? '试试其他搜索条件或筛选条件' : '开始与其他人聊天吧' }}</p>
          </div>
          <div
            v-else
            v-for="conv in filteredConversations"
            :key="conv.id"
            class="conversation-item"
            :class="{ active: selectedConversationId === conv.id, unread: conv.unreadCount > 0 }"
            @click="selectConversation(conv)"
          >
            <!-- 左侧类型标识色条 -->
            <div
              v-if="conv.chatType"
              class="type-indicator"
              :style="{ background: getChatTypeConfig(conv.chatType).bgColor }"
            ></div>

            <!-- 头像区域 -->
            <div class="avatar-wrapper">
              <div class="avatar-circle" :style="{ background: getAvatarColor(conv.targetUserName) }">
                <span class="avatar-text">{{ getAvatarText(conv.targetUserName) }}</span>
                <!-- 群聊标识 -->
                <div v-if="conv.type === 'group'" class="group-badge">
                  <i class="fas fa-users"></i>
                </div>
              </div>
              <!-- 未读消息数量徽章 -->
              <div v-if="conv.unreadCount > 0" class="unread-badge">
                {{ conv.unreadCount > 99 ? '99+' : conv.unreadCount }}
              </div>
            </div>

            <!-- 中间内容区域 -->
            <div class="conversation-content">
              <!-- 第一行：用户名和时间 -->
              <div class="conversation-header-row">
                <div class="conversation-name-wrapper">
                  <span class="conversation-name">{{ conv.groupNickname || conv.targetUserName || '用户' }}</span>
                  <!-- 置顶图标 -->
                  <i v-if="conv.isPinned" class="fas fa-thumbtack pin-icon" title="置顶"></i>
                  <!-- 免打扰图标 -->
                  <i v-if="conv.isMuted" class="fas fa-bell-slash mute-icon" title="免打扰"></i>
                  <!-- 聊天类型标签 -->
                  <span v-if="conv.chatType" class="chat-type-badge" :style="{ background: getChatTypeConfig(conv.chatType).bgColor, color: getChatTypeConfig(conv.chatType).color }">
                    <i :class="getChatTypeConfig(conv.chatType).icon"></i>
                    {{ getChatTypeConfig(conv.chatType).name }}
                  </span>
                  <span v-if="conv.type === 'group' && conv.groupMemberCount" class="group-member-count">
                    ({{ conv.groupMemberCount }}人)
                  </span>
                </div>
                <span class="conversation-time">{{ formatTime(conv.lastMessageTime) }}</span>
              </div>
              <!-- 第二行：最后一条消息预览 -->
              <div class="conversation-preview-row">
                <span class="conversation-preview">{{ conv.lastMessage || '暂无消息' }}</span>
              </div>
            </div>

            <!-- 右侧箭头图标 -->
            <div class="arrow-wrapper">
              <i class="fas fa-chevron-right"></i>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧消息详情/聊天窗口区域 -->
      <div class="messages-panel">
        <div v-if="!selectedConversation" class="empty-messages">
          <div class="empty-content">
            <i class="fas fa-comments"></i>
            <h3>选择一个会话开始聊天</h3>
            <p>从左侧列表中选择一个会话查看消息</p>
          </div>
        </div>
        <div v-else class="chat-window">
          <!-- 聊天头部 -->
          <div class="chat-header">
            <div class="chat-user-info">
              <div class="chat-avatar" :style="{ background: getAvatarColor(selectedConversation.targetUserName) }">
                <span>{{ getAvatarText(selectedConversation.targetUserName) }}</span>
              </div>
              <div class="chat-user-details">
                <h3>{{ selectedConversation.groupNickname || selectedConversation.targetUserName || '用户' }}</h3>
                <span v-if="selectedConversation.type === 'group' && selectedConversation.groupMemberCount" class="chat-status">
                  {{ selectedConversation.groupMemberCount }}人
                </span>
                <span v-else class="chat-status">在线</span>
              </div>
            </div>
            <div class="chat-actions">
              <button class="chat-action-btn" @click="showConversationInfo = true" title="会话信息">
                <i class="fas fa-info-circle"></i>
              </button>
            </div>
          </div>

          <!-- 消息列表 -->
          <div class="messages-list" ref="messagesListRef">
            <div v-if="currentMessages.length === 0" class="empty-messages-inner">
              <i class="fas fa-comments"></i>
              <p>还没有消息，开始聊天吧~</p>
            </div>
            <template v-else>
              <div
                v-for="(message, index) in currentMessages"
                :key="message.id"
                :class="['message-bubble', { own: message.isOwn, 'group-message': selectedConversation?.type === 'group' }]"
              >
                <!-- 群聊时，所有消息都显示头像 -->
                <div class="message-avatar" v-if="selectedConversation?.type === 'group' || !message.isOwn">
                  <div class="avatar-circle" :style="{ background: getAvatarColor(message.senderName || (message.isOwn ? (userStore.username || '我') : selectedConversation.targetUserName)) }">
                    <span>{{ getAvatarText(message.senderName || (message.isOwn ? (userStore.username || '我') : selectedConversation.targetUserName)) }}</span>
                  </div>
                </div>
                <div class="message-content">
                  <!-- 群聊时显示发送者名称 -->
                  <div v-if="selectedConversation?.type === 'group' && !message.isOwn && message.senderName" class="message-sender-name">
                    {{ message.senderName }}
                  </div>
                  <div class="message-text" :class="{ recalled: message.recalled }">
                    {{ message.recalled ? (message.isOwn ? '你撤回了一条消息' : '对方撤回了一条消息') : message.content }}
                  </div>
                  <div class="message-time">{{ message.time }}</div>
                </div>
                <!-- 私聊时，自己的消息显示头像 -->
                <div class="message-avatar" v-if="selectedConversation?.type !== 'group' && message.isOwn">
                  <div class="avatar-circle" :style="{ background: getAvatarColor(userStore.username || '我') }">
                    <span>{{ getAvatarText(userStore.username || '我') }}</span>
                  </div>
                </div>
              </div>
            </template>
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
            </div>
            <div v-if="showEmojiPicker" class="emoji-panel">
              <button
                v-for="emoji in emojiList"
                :key="emoji"
                class="emoji-btn"
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
    </div>

    <!-- 会话信息弹窗 -->
    <div v-if="showConversationInfo && selectedConversation" class="modal-overlay" @click="showConversationInfo = false">
      <div class="modal-content conversation-info-modal" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedConversation.type === 'group' ? '群聊信息' : '会话信息' }}</h3>
          <button class="modal-close" @click="showConversationInfo = false">×</button>
        </div>
        <div class="modal-body">
          <!-- 群昵称设置（仅群聊） -->
          <div v-if="selectedConversation.type === 'group'" class="info-section">
            <div class="info-item">
              <label>群昵称</label>
              <div class="info-input-wrapper">
                <input
                  v-model="editingGroupNickname"
                  type="text"
                  placeholder="设置群昵称"
                  class="info-input"
                  @blur="saveGroupNickname"
                  @keyup.enter="saveGroupNickname"
                />
                <button class="btn-save" @click="saveGroupNickname">
                  <i class="fas fa-check"></i>
                </button>
              </div>
            </div>
          </div>

          <!-- 会话设置 -->
          <div class="info-section">
            <div class="info-item">
              <div class="info-label-wrapper">
                <label>置顶聊天</label>
                <span class="info-desc">将聊天固定在列表顶部</span>
              </div>
              <label class="switch">
                <input
                  type="checkbox"
                  :checked="selectedConversation.isPinned"
                  @change="togglePin"
                />
                <span class="slider"></span>
              </label>
            </div>
            <div class="info-item">
              <div class="info-label-wrapper">
                <label>免打扰</label>
                <span class="info-desc">接收消息时不提醒</span>
              </div>
              <label class="switch">
                <input
                  type="checkbox"
                  :checked="selectedConversation.isMuted"
                  @change="toggleMute"
                />
                <span class="slider"></span>
              </label>
            </div>
          </div>

          <!-- 其他操作 -->
          <div class="info-section">
            <div class="info-item action-item" @click="clearChatHistory">
              <i class="fas fa-trash-alt"></i>
              <span>清空聊天记录</span>
            </div>
            <div v-if="selectedConversation.type === 'group'" class="info-item action-item" @click="exitGroup">
              <i class="fas fa-sign-out-alt"></i>
              <span>退出群聊</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建群聊弹窗 -->
    <div v-if="showCreateGroup" class="modal-overlay" @click="showCreateGroup = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>创建群聊</h3>
          <button class="modal-close" @click="showCreateGroup = false">×</button>
        </div>
        <div class="modal-body">
          <div class="create-methods">
            <div class="method-item" @click="handleFaceToFace">
              <div class="method-icon face-to-face-icon">
                <i class="fas fa-mobile-alt"></i>
              </div>
              <div class="method-info">
                <div class="method-title">面对面建群</div>
                <div class="method-desc">输入相同数字码即可加入群聊</div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
            <div class="method-item" @click="handleSelectFriends">
              <div class="method-icon select-friends-icon">
                <i class="fas fa-user-friends"></i>
              </div>
              <div class="method-info">
                <div class="method-title">选择好友建群</div>
                <div class="method-desc">从好友列表中选择成员创建群聊</div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
            <div class="method-item" @click="handleInviteCodeGroup">
              <div class="method-icon invite-code-icon">
                <i class="fas fa-qrcode"></i>
              </div>
              <div class="method-info">
                <div class="method-title">邀请码建群</div>
                <div class="method-desc">生成邀请码，分享给其他人加入</div>
              </div>
              <i class="fas fa-chevron-right"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
import { request, useUserStore } from '@campus/common'
import { connectMessageWebSocket, addMessageListener, removeMessageListener } from '@/utils/messageWebSocket'
import { connectChatWebSocket, disconnectChatWebSocket } from '@/utils/chatWebSocket'

const router = useRouter()
const userStore = useUserStore()

// 会话列表相关
interface ConversationItem {
  id: number
  targetUserId?: number
  targetUserName: string
  targetUserAvatar?: string
  lastMessage: string
  lastMessageTime: string | Date
  unreadCount: number
  type?: 'private' | 'group'
  groupId?: number
  groupMemberCount?: number
  chatType?: 'secondhand' | 'parttime' | 'friend' | 'team'
  isPinned?: boolean // 是否置顶
  isMuted?: boolean // 是否免打扰
  groupNickname?: string // 群昵称（仅群聊）
}

const conversations = ref<ConversationItem[]>([])
const selectedConversationId = ref<number | null>(null)
const selectedConversation = computed(() => {
  return conversations.value.find(c => c.id === selectedConversationId.value) || null
})
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const hasMore = ref(true)

// 搜索和筛选
const showSearchBar = ref(false)
const showFilterBar = ref(false)
const searchKeyword = ref('')
const activeFilterTab = ref(0)
const filterTabs = ref([
  { name: '全部', value: 'all' },
  { name: '未读', value: 'unread' },
  { name: '群聊', value: 'group' },
  { name: '二手交易', value: 'secondhand' },
  { name: '兼职', value: 'parttime' },
  { name: '好友', value: 'friend' },
  { name: '团队', value: 'team' }
])

// 聊天消息相关
interface ChatMessageItem {
  id: number
  content: string
  time: string
  timestamp: number
  isOwn: boolean
  status?: 'sending' | 'sent' | 'read'
  recalled?: boolean
  senderName?: string
  senderId?: number
}

const currentMessages = ref<ChatMessageItem[]>([])
const inputMessage = ref('')
const showEmojiPicker = ref(false)
const messagesListRef = ref<HTMLElement | null>(null)
const showConversationInfo = ref(false)
const editingGroupNickname = ref('')

// 创建群聊相关
const showCreateGroup = ref(false)

// 表情列表
const emojiList = ref(['😀', '😃', '😄', '😁', '😆', '😅', '😂', '🤣', '😊', '😇', '🙂', '🙃', '😉', '😌', '😍', '🥰', '😘', '😗', '😙', '😚', '😋', '😛', '😝', '😜', '🤪', '🤨', '🧐', '🤓', '😎', '🤩', '🥳', '😏', '😒', '😞', '😔', '😟', '😕', '🙁', '☹️', '😣', '😖', '😫', '😩', '🥺', '😢', '😭', '😤', '😠', '😡', '🤬', '🤯', '😳', '🥵', '🥶', '😱', '😨', '😰', '😥', '😓', '🤗', '🤔', '🤭', '🤫', '🤥', '😶', '😐', '😑', '😬', '🙄', '😯', '😦', '😧', '😮', '😲', '🥱', '😴', '🤤', '😪', '😵', '🤐', '🥴', '🤢', '🤮', '🤧', '😷', '🤒', '🤕'])

// 聊天类型配置
interface ChatTypeConfig {
  name: string
  color: string
  bgColor: string
  icon: string
  borderColor: string
}

const getChatTypeConfig = (chatType?: string): ChatTypeConfig => {
  const configs: Record<string, ChatTypeConfig> = {
    secondhand: {
      name: '二手',
      color: '#fff',
      bgColor: '#FF6B9D',
      icon: 'fas fa-shopping-bag',
      borderColor: '#FF6B9D'
    },
    parttime: {
      name: '兼职',
      color: '#fff',
      bgColor: '#4CAF50',
      icon: 'fas fa-briefcase',
      borderColor: '#4CAF50'
    },
    friend: {
      name: '好友',
      color: '#fff',
      bgColor: '#2196F3',
      icon: 'fas fa-user-friends',
      borderColor: '#2196F3'
    },
    team: {
      name: '团队',
      color: '#fff',
      bgColor: '#FF9800',
      icon: 'fas fa-users',
      borderColor: '#FF9800'
    }
  }
  return configs[chatType || 'friend'] || configs.friend
}

// 获取头像颜色
const getAvatarColor = (name: string): string => {
  const colors = ['#FF6B9D', '#4CAF50', '#2196F3', '#FF9800', '#9C27B0', '#00BCD4', '#FF5722', '#795548']
  const index = name.charCodeAt(0) % colors.length
  return colors[index]
}

// 获取头像文字
const getAvatarText = (name: string): string => {
  if (!name) return '?'
  return name.length > 2 ? name.substring(name.length - 2) : name
}

// 格式化时间
const formatTime = (time: string | Date): string => {
  if (!time) return ''
  const date = typeof time === 'string' ? new Date(time) : time
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

// 切换搜索栏
const toggleSearch = () => {
  showSearchBar.value = !showSearchBar.value
  if (!showSearchBar.value) {
    searchKeyword.value = ''
  }
}

// 切换筛选栏
const toggleFilter = () => {
  showFilterBar.value = !showFilterBar.value
}

// 处理搜索输入
const handleSearchInput = () => {
  // 搜索逻辑已在computed中处理
}

// 清空搜索
const clearSearch = () => {
  searchKeyword.value = ''
}

// 处理筛选标签切换
const handleFilterTabChange = (idx: number) => {
  activeFilterTab.value = idx
}

// 过滤后的会话列表（置顶优先排序）
const filteredConversations = computed(() => {
  let result = conversations.value

  // 搜索过滤
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    result = result.filter(conv =>
      (conv.groupNickname || conv.targetUserName).toLowerCase().includes(keyword) ||
      conv.lastMessage.toLowerCase().includes(keyword)
    )
  }

  // 筛选过滤
  const activeFilter = filterTabs.value[activeFilterTab.value]
  if (activeFilter && activeFilter.value !== 'all') {
    if (activeFilter.value === 'unread') {
      result = result.filter(conv => conv.unreadCount > 0)
    } else if (activeFilter.value === 'group') {
      result = result.filter(conv => conv.type === 'group')
    } else {
      result = result.filter(conv => conv.chatType === activeFilter.value)
    }
  }

  // 置顶排序：置顶的会话排在前面
  result.sort((a, b) => {
    if (a.isPinned && !b.isPinned) return -1
    if (!a.isPinned && b.isPinned) return 1
    // 如果都是置顶或都不是置顶，按时间排序
    const timeA = typeof a.lastMessageTime === 'string' ? new Date(a.lastMessageTime).getTime() : a.lastMessageTime.getTime()
    const timeB = typeof b.lastMessageTime === 'string' ? new Date(b.lastMessageTime).getTime() : b.lastMessageTime.getTime()
    return timeB - timeA
  })

  return result
})

// 加载会话列表
const loadConversations = async (reset = false) => {
  if (loading.value) return
  loading.value = true

  try {
    if (reset) {
      currentPage.value = 1
      conversations.value = []
    }

    const res: any = await request.get('/api/chat/conversations', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })

    const records: ConversationItem[] = Array.isArray(res.records) ? res.records : []
    const total = res.total || 0

    if (reset) {
      conversations.value = records
    } else {
      conversations.value.push(...records)
    }

    hasMore.value = conversations.value.length < total
    currentPage.value += 1

    // 如果没有数据，使用模拟数据
    if (reset && conversations.value.length === 0) {
      conversations.value = getMockConversations()
    }
  } catch (error) {
    console.error('加载会话列表失败:', error)
    if (reset || conversations.value.length === 0) {
      conversations.value = getMockConversations()
    }
  } finally {
    loading.value = false
  }
}

// 获取模拟会话数据
const getMockConversations = (): ConversationItem[] => {
  return [
    {
      id: 1,
      targetUserId: 1001,
      targetUserName: '张三',
      lastMessage: '你好，请问这个商品还在吗？',
      lastMessageTime: new Date(Date.now() - 5 * 60000),
      unreadCount: 2,
      type: 'private',
      chatType: 'secondhand',
      isPinned: true,
      isMuted: false
    },
    {
      id: 2,
      targetUserId: 1002,
      targetUserName: '李四',
      lastMessage: '好的，我明天可以面试',
      lastMessageTime: new Date(Date.now() - 30 * 60000),
      unreadCount: 0,
      type: 'private',
      chatType: 'parttime',
      isPinned: false,
      isMuted: false
    },
    {
      id: 3,
      targetUserId: 1003,
      targetUserName: '项目讨论组',
      lastMessage: '王五：今天的任务完成了',
      lastMessageTime: new Date(Date.now() - 2 * 3600000),
      unreadCount: 5,
      type: 'group',
      groupId: 2001,
      groupMemberCount: 12,
      chatType: 'team',
      isPinned: false,
      isMuted: true,
      groupNickname: '项目讨论组'
    }
  ]
}

// 选择会话
const selectConversation = async (conv: ConversationItem) => {
  // 立即清空当前消息列表，避免显示旧消息
  currentMessages.value = []
  // 清空输入框
  inputMessage.value = ''
  // 关闭表情选择器
  showEmojiPicker.value = false
  
  // 设置选中的会话ID
  selectedConversationId.value = conv.id
  
  // 初始化群昵称编辑
  editingGroupNickname.value = conv.groupNickname || ''
  
  // 加载新会话的消息
  await loadMessages(conv.id)
  
  // 标记为已读
  if (conv.unreadCount > 0) {
    await markConversationAsRead(conv.id)
    conv.unreadCount = 0
  }
}

// 加载消息
const loadMessages = async (conversationId: number) => {
  // 先清空消息列表，确保切换时立即清空旧消息
  currentMessages.value = []
  
  try {
    const res: any = await request.get('/api/chat/messages', {
      params: {
        conversationId,
        page: 1,
        size: 50
      }
    })

    const records: any[] = Array.isArray(res.records) ? res.records : []
    const currentUserId = userStore.userId || 0

    const mapped = records
      .slice()
      .reverse()
      .map((item: any) => {
        const isOwn = item.senderId === currentUserId
        return {
          id: item.id,
          content: item.content || '',
          time: formatTime(item.createTime || new Date()),
          timestamp: new Date(item.createTime || new Date()).getTime(),
          isOwn,
          status: 'read' as const,
          senderName: item.senderName,
          senderId: item.senderId,
          recalled: item.recalled || false
        } as ChatMessageItem
      })

    currentMessages.value = mapped

    nextTick(() => {
      scrollToBottom()
    })
  } catch (error) {
    console.error('加载消息失败:', error)
    // 使用模拟数据，根据会话ID返回不同的模拟数据
    currentMessages.value = getMockMessages(conversationId)
    
    nextTick(() => {
      scrollToBottom()
    })
  }
}

// 获取模拟消息数据（根据会话ID返回不同的消息）
const getMockMessages = (conversationId: number): ChatMessageItem[] => {
  const now = Date.now()
  
  // 根据会话ID返回不同的模拟消息
  if (conversationId === 1) {
    // 张三的会话（二手交易）
    return [
      {
        id: 1,
        content: '你好，请问这个商品还在吗？',
        time: formatTime(new Date(now - 3600000)),
        timestamp: now - 3600000,
        isOwn: false,
        senderName: '张三'
      },
      {
        id: 2,
        content: '还在的，您需要吗？',
        time: formatTime(new Date(now - 3580000)),
        timestamp: now - 3580000,
        isOwn: true
      }
    ]
  } else if (conversationId === 2) {
    // 李四的会话（兼职）
    return [
      {
        id: 3,
        content: '你好，请问这个兼职岗位还在招聘吗？',
        time: formatTime(new Date(now - 1800000)),
        timestamp: now - 1800000,
        isOwn: false,
        senderName: '李四'
      },
      {
        id: 4,
        content: '还在招聘的，您有兴趣吗？',
        time: formatTime(new Date(now - 1750000)),
        timestamp: now - 1750000,
        isOwn: true
      },
      {
        id: 5,
        content: '好的，我明天可以面试',
        time: formatTime(new Date(now - 1700000)),
        timestamp: now - 1700000,
        isOwn: false,
        senderName: '李四'
      }
    ]
  } else if (conversationId === 3) {
    // 项目讨论组的会话（团队）
    return [
      {
        id: 6,
        content: '大家好，今天的任务分配如下',
        time: formatTime(new Date(now - 7200000)),
        timestamp: now - 7200000,
        isOwn: false,
        senderName: '王五'
      },
      {
        id: 7,
        content: '收到',
        time: formatTime(new Date(now - 7100000)),
        timestamp: now - 7100000,
        isOwn: true
      },
      {
        id: 8,
        content: '今天的任务完成了',
        time: formatTime(new Date(now - 7000000)),
        timestamp: now - 7000000,
        isOwn: false,
        senderName: '王五'
      },
      {
        id: 9,
        content: '收到，辛苦了',
        time: formatTime(new Date(now - 6900000)),
        timestamp: now - 6900000,
        isOwn: true
      }
    ]
  }
  
  // 默认返回空数组
  return []
}

// 标记会话为已读
const markConversationAsRead = async (conversationId: number) => {
  try {
    await request.put(`/api/chat/conversations/${conversationId}/read`)
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || !selectedConversation.value) return

  const message: ChatMessageItem = {
    id: Date.now(),
    content: inputMessage.value.trim(),
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    timestamp: Date.now(),
    isOwn: true,
    status: 'sending'
  }

  currentMessages.value.push(message)
  inputMessage.value = ''
  adjustTextareaHeight()

  nextTick(() => {
    scrollToBottom()
  })

  try {
    const res: any = await request.post('/api/chat/messages', {
      conversationId: selectedConversation.value.id,
      content: message.content,
      targetUserId: selectedConversation.value.targetUserId,
      groupId: selectedConversation.value.groupId
    })

    message.id = res.id || message.id
    message.status = 'sent'

    // 更新会话列表的最后一条消息
    if (selectedConversation.value) {
      selectedConversation.value.lastMessage = message.content
      selectedConversation.value.lastMessageTime = new Date()
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    message.status = 'sent'
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesListRef.value) {
    messagesListRef.value.scrollTop = messagesListRef.value.scrollHeight
  }
}

// 调整输入框高度
const adjustTextareaHeight = () => {
  const textarea = document.querySelector('.message-input') as HTMLTextAreaElement
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = `${Math.min(textarea.scrollHeight, 120)}px`
  }
}

// 添加表情
const appendEmoji = (emoji: string) => {
  inputMessage.value += emoji
  showEmojiPicker.value = false
}

// 触发文件输入
const triggerFileInput = () => {
  // TODO: 实现文件上传功能
  console.log('文件上传功能待实现')
}

// 创建群聊相关方法
const handleFaceToFace = () => {
  showCreateGroup.value = false
  // TODO: 实现面对面建群
  console.log('面对面建群功能待实现')
}

const handleSelectFriends = () => {
  showCreateGroup.value = false
  // TODO: 实现选择好友建群
  console.log('选择好友建群功能待实现')
}

const handleInviteCodeGroup = () => {
  showCreateGroup.value = false
  // TODO: 实现邀请码建群
  console.log('邀请码建群功能待实现')
}

// 保存群昵称
const saveGroupNickname = async () => {
  if (!selectedConversation.value) return
  
  const newNickname = editingGroupNickname.value.trim()
  if (newNickname === selectedConversation.value.groupNickname) return
  
  try {
    await request.put(`/api/chat/conversations/${selectedConversation.value.id}/nickname`, {
      nickname: newNickname
    })
    selectedConversation.value.groupNickname = newNickname || undefined
  } catch (error) {
    console.error('保存群昵称失败:', error)
    // 恢复原值
    editingGroupNickname.value = selectedConversation.value.groupNickname || ''
  }
}

// 切换置顶
const togglePin = async () => {
  if (!selectedConversation.value) return
  
  const newPinStatus = !selectedConversation.value.isPinned
  
  try {
    await request.put(`/api/chat/conversations/${selectedConversation.value.id}/pin`, {
      isPinned: newPinStatus
    })
    selectedConversation.value.isPinned = newPinStatus
  } catch (error) {
    console.error('切换置顶失败:', error)
    // 恢复原状态
    selectedConversation.value.isPinned = !newPinStatus
  }
}

// 切换免打扰
const toggleMute = async () => {
  if (!selectedConversation.value) return
  
  const newMuteStatus = !selectedConversation.value.isMuted
  
  try {
    await request.put(`/api/chat/conversations/${selectedConversation.value.id}/mute`, {
      isMuted: newMuteStatus
    })
    selectedConversation.value.isMuted = newMuteStatus
  } catch (error) {
    console.error('切换免打扰失败:', error)
    // 恢复原状态
    selectedConversation.value.isMuted = !newMuteStatus
  }
}

// 清空聊天记录
const clearChatHistory = async () => {
  if (!selectedConversation.value) return
  
  if (!confirm('确定要清空聊天记录吗？此操作不可恢复。')) {
    return
  }
  
  try {
    await request.delete(`/api/chat/conversations/${selectedConversation.value.id}/messages`)
    currentMessages.value = []
    selectedConversation.value.lastMessage = ''
    selectedConversation.value.lastMessageTime = new Date()
    showConversationInfo.value = false
  } catch (error) {
    console.error('清空聊天记录失败:', error)
    alert('清空聊天记录失败，请稍后重试')
  }
}

// 退出群聊
const exitGroup = async () => {
  if (!selectedConversation.value || selectedConversation.value.type !== 'group') return
  
  if (!confirm('确定要退出群聊吗？')) {
    return
  }
  
  try {
    await request.post(`/api/chat/groups/${selectedConversation.value.groupId}/exit`)
    // 从会话列表中移除
    const index = conversations.value.findIndex(c => c.id === selectedConversation.value!.id)
    if (index > -1) {
      conversations.value.splice(index, 1)
    }
    selectedConversationId.value = null
    currentMessages.value = []
    showConversationInfo.value = false
  } catch (error) {
    console.error('退出群聊失败:', error)
    alert('退出群聊失败，请稍后重试')
  }
}

// WebSocket消息处理
let messageWsHandler: ((event: any) => void) | null = null

onMounted(() => {
  loadConversations(true)

  if (userStore.isLoggedIn && userStore.userId) {
    connectMessageWebSocket(userStore.userId)
    messageWsHandler = () => {
      loadConversations(true)
    }
    addMessageListener(messageWsHandler)
  }
})

onUnmounted(() => {
  if (messageWsHandler) {
    removeMessageListener(messageWsHandler)
    messageWsHandler = null
  }
  disconnectChatWebSocket()
})

// 监听会话选择变化（当通过其他方式改变selectedConversationId时也会触发）
watch(selectedConversationId, (newId, oldId) => {
  // 如果会话ID发生变化，且不是初始状态，则加载消息
  if (newId && newId !== oldId) {
    // 清空当前消息和输入框
    currentMessages.value = []
    inputMessage.value = ''
    showEmojiPicker.value = false
    // 初始化群昵称编辑
    const conv = conversations.value.find(c => c.id === newId)
    if (conv) {
      editingGroupNickname.value = conv.groupNickname || ''
    }
    // 加载新会话的消息
    loadMessages(newId)
  } else if (!newId) {
    // 如果没有选中会话，清空消息列表
    currentMessages.value = []
    inputMessage.value = ''
    editingGroupNickname.value = ''
  }
})
</script>

<style scoped>
.messages-page {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.messages-container {
  flex: 1;
  display: flex;
  max-width: 1400px;
  margin: 20px auto;
  width: 100%;
  height: calc(100vh - 200px);
  max-height: 800px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 左侧会话列表 */
.conversations-panel {
  width: 360px;
  height: 100%;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  background: #fafafa;
  overflow: hidden;
}

.panel-header {
  padding: 16px 20px;
  background: white;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: #f5f5f5;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #e0e0e0;
  color: #333;
}

.search-bar {
  padding: 12px 16px;
  background: white;
  border-bottom: 1px solid #e0e0e0;
}

.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 12px;
  color: #999;
  font-size: 14px;
}

.search-input {
  width: 100%;
  padding: 8px 36px 8px 36px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.search-input:focus {
  border-color: #FF6B9D;
}

.search-clear {
  position: absolute;
  right: 8px;
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.filter-bar {
  padding: 12px 16px;
  background: white;
  border-bottom: 1px solid #e0e0e0;
}

.filter-tabs {
  display: flex;
  gap: 8px;
  overflow-x: auto;
}

.filter-tab {
  padding: 6px 16px;
  border-radius: 16px;
  font-size: 13px;
  color: #666;
  background: #f5f5f5;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.filter-tab:hover {
  background: #e0e0e0;
}

.filter-tab.active {
  background: #FF6B9D;
  color: white;
}

.conversations-list {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  min-height: 0;
}

.conversations-list::-webkit-scrollbar {
  width: 6px;
}

.conversations-list::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

.conversations-list::-webkit-scrollbar-thumb:hover {
  background: #999;
}

.loading-state,
.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #999;
}

.loading-state i,
.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #ddd;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #666;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 3px solid transparent;
  position: relative;
}

.conversation-item:hover {
  background: #f0f0f0;
}

.conversation-item.active {
  background: #e8f4f8;
  border-left-color: #FF6B9D;
}

.conversation-item.unread {
  background: #fff5f9;
}

.type-indicator {
  width: 3px;
  height: 100%;
  position: absolute;
  left: 0;
  top: 0;
}

.avatar-wrapper {
  position: relative;
  margin-right: 12px;
  flex-shrink: 0;
}

.avatar-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 16px;
  position: relative;
}

.group-badge {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 18px;
  height: 18px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: #2196F3;
}

.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: #ff4444;
  color: white;
  border-radius: 9px;
  font-size: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  border: 2px solid white;
}

.conversation-content {
  flex: 1;
  min-width: 0;
}

.conversation-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.conversation-name-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  min-width: 0;
}

.conversation-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pin-icon {
  font-size: 11px;
  color: #FF6B9D;
  margin-left: 4px;
}

.mute-icon {
  font-size: 11px;
  color: #999;
  margin-left: 4px;
}

.chat-type-badge {
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 10px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 2px;
  white-space: nowrap;
}

.chat-type-badge i {
  font-size: 8px;
}

.group-member-count {
  font-size: 12px;
  color: #999;
}

.conversation-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  margin-left: 8px;
}

.conversation-preview-row {
  display: flex;
  align-items: center;
}

.conversation-preview {
  font-size: 13px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.arrow-wrapper {
  margin-left: 8px;
  color: #ccc;
  font-size: 12px;
}

/* 右侧聊天窗口 */
.messages-panel {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  overflow: hidden;
  min-width: 0;
}

.empty-messages {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-content {
  text-align: center;
  color: #999;
}

.empty-content i {
  font-size: 64px;
  margin-bottom: 16px;
  color: #ddd;
}

.empty-content h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #666;
}

.empty-content p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  overflow: hidden;
}

.chat-header {
  padding: 16px 20px;
  background: white;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 14px;
}

.chat-user-details h3 {
  margin: 0 0 2px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chat-status {
  font-size: 12px;
  color: #999;
}

.chat-actions {
  display: flex;
  gap: 8px;
}

.chat-action-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  border-radius: 8px;
  transition: all 0.2s;
}

.chat-action-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.messages-list {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 20px;
  background: #f5f5f5;
  min-height: 0;
}

.messages-list::-webkit-scrollbar {
  width: 6px;
}

.messages-list::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

.messages-list::-webkit-scrollbar-thumb:hover {
  background: #999;
}

.empty-messages-inner {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-messages-inner i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #ddd;
}

.message-bubble {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 16px;
}

.message-bubble.own {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-avatar .avatar-circle {
  width: 36px;
  height: 36px;
  font-size: 14px;
}

.message-content {
  max-width: 60%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message-bubble.own .message-content {
  align-items: flex-end;
}

.message-bubble.group-message .message-content {
  align-items: flex-start;
}

.message-sender-name {
  font-size: 12px;
  color: #999;
  margin-bottom: 2px;
  padding: 0 4px;
}

.message-text {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
  background: white;
  color: #333;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message-bubble.own .message-text {
  background: #FF6B9D;
  color: white;
}

.message-text.recalled {
  color: #999;
  font-style: italic;
}

.message-time {
  font-size: 11px;
  color: #999;
  padding: 0 4px;
}

.chat-input {
  padding: 16px;
  background: white;
  border-top: 1px solid #e0e0e0;
}

.input-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.btn-tool {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  border-radius: 6px;
  transition: all 0.2s;
}

.btn-tool:hover {
  background: #f5f5f5;
  color: #333;
}

.emoji-panel {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.emoji-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 20px;
  border-radius: 4px;
  transition: all 0.2s;
}

.emoji-btn:hover {
  background: #e0e0e0;
}

.input-area {
  display: flex;
  align-items: flex-end;
  gap: 8px;
}

.message-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  font-size: 14px;
  resize: none;
  outline: none;
  max-height: 120px;
  font-family: inherit;
  line-height: 1.5;
}

.message-input:focus {
  border-color: #FF6B9D;
}

.btn-send {
  width: 40px;
  height: 40px;
  border: none;
  background: #FF6B9D;
  color: white;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.btn-send:hover:not(:disabled) {
  background: #E91E63;
  transform: scale(1.05);
}

.btn-send:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 创建群聊弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 24px;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
}

.create-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.method-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.method-item:hover {
  border-color: #FF6B9D;
  background: #fff5f9;
}

.method-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.face-to-face-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.select-friends-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.invite-code-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.method-info {
  flex: 1;
}

.method-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.method-desc {
  font-size: 13px;
  color: #999;
}

.method-item i.fa-chevron-right {
  color: #ccc;
  font-size: 14px;
}

/* 会话信息弹窗样式 */
.conversation-info-modal {
  max-width: 500px;
  max-height: 80vh;
}

.info-section {
  margin-bottom: 24px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item label {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.info-label-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-desc {
  font-size: 12px;
  color: #999;
  font-weight: normal;
}

.info-input-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  max-width: 300px;
}

.info-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}

.info-input:focus {
  border-color: #FF6B9D;
}

.btn-save {
  width: 32px;
  height: 32px;
  border: none;
  background: #FF6B9D;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-save:hover {
  background: #E91E63;
}

/* 开关切换样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 44px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.3s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

.switch input:checked + .slider {
  background-color: #FF6B9D;
}

.switch input:checked + .slider:before {
  transform: translateX(20px);
}

.switch input:focus + .slider {
  box-shadow: 0 0 1px #FF6B9D;
}

/* 操作项样式 */
.action-item {
  cursor: pointer;
  padding: 12px 0;
  transition: all 0.2s;
}

.action-item:hover {
  background: #f5f5f5;
  padding-left: 8px;
}

.action-item i {
  margin-right: 12px;
  color: #666;
  width: 20px;
  text-align: center;
}

.action-item:first-child i {
  color: #ff4444;
}

.action-item:last-child i {
  color: #ff4444;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .messages-container {
    flex-direction: column;
    height: auto;
    min-height: calc(100vh - 200px);
  }

  .conversations-panel {
    width: 100%;
    height: 400px;
    border-right: none;
    border-bottom: 1px solid #e0e0e0;
  }

  .messages-panel {
    height: 500px;
  }

  .conversation-info-modal {
    max-width: 90%;
  }
}
</style>
