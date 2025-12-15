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
          <div
            v-for="message in messages"
            :key="message.id"
            :class="['message-bubble', { own: message.isOwn }]"
          >
            <div class="message-avatar" v-if="!message.isOwn">
              <i class="fas fa-user"></i>
            </div>
            <div class="message-content">
              <div class="message-text">{{ message.content }}</div>
              <div class="message-time">{{ message.time }}</div>
            </div>
            <div class="message-avatar" v-if="message.isOwn">
              <i class="fas fa-user"></i>
            </div>
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
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()
const route = useRoute()

const messagesContainer = ref<HTMLElement | null>(null)
const fileInput = ref<HTMLInputElement | null>(null)
const inputMessage = ref('')
const showEmojiPicker = ref(false)

// 聊天用户信息
const chatUser = reactive({
  id: '',
  name: ''
})

// 消息列表
const messages = ref([
  {
    id: 1,
    content: '你好，请问这个商品还在吗？',
    time: '10:30',
    isOwn: false
  },
  {
    id: 2,
    content: '还在的，您需要的话可以联系我',
    time: '10:32',
    isOwn: true
  },
  {
    id: 3,
    content: '好的，价格可以商量吗？',
    time: '10:33',
    isOwn: false
  }
])

// 发送消息
const sendMessage = () => {
  if (!inputMessage.value.trim()) return

  const newMessage = {
    id: Date.now(),
    content: inputMessage.value.trim(),
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
    isOwn: true
  }

  messages.value.push(newMessage)
  inputMessage.value = ''
  
  // 滚动到底部
  scrollToBottom()
  
  // TODO: 发送消息到服务器
  // await sendMessageAPI(newMessage)
}

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理图片上传
const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  if (file.type.startsWith('image/')) {
    // TODO: 上传图片并发送
    const reader = new FileReader()
    reader.onload = (e) => {
      const imageUrl = e.target?.result as string
      // 发送图片消息
      // sendImageMessage(imageUrl)
    }
    reader.readAsDataURL(file)
  }
}

// 调整文本框高度
const adjustTextareaHeight = (event: Event) => {
  const textarea = event.target as HTMLTextAreaElement
  textarea.style.height = 'auto'
  textarea.style.height = `${Math.min(textarea.scrollHeight, 120)}px`
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 监听消息变化，自动滚动
watch(messages, () => {
  scrollToBottom()
}, { deep: true })

onMounted(() => {
  // 从路由参数获取聊天用户信息
  const sellerId = route.query.sellerId as string
  if (sellerId) {
    // TODO: 加载用户信息
    // loadChatUser(sellerId)
    chatUser.id = sellerId
    chatUser.name = '卖家'
  }
  
  scrollToBottom()
})
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.chat-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.chat-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200px);
  min-height: 600px;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
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

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-bubble {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.message-bubble.own {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message-bubble.own .message-content {
  align-items: flex-end;
}

.message-text {
  padding: 10px 15px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
}

.message-bubble:not(.own) .message-text {
  background: #f5f5f5;
  color: #333;
}

.message-bubble.own .message-text {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
}

.message-time {
  font-size: 11px;
  color: #999;
  padding: 0 4px;
}

.chat-input {
  border-top: 1px solid #f0f0f0;
  padding: 15px;
}

.input-toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.btn-tool {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: #f5f5f5;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-tool:hover {
  background: #f9f0ff;
  color: var(--primary);
}

.input-area {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  padding: 10px 15px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 20px;
  font-size: 14px;
  resize: none;
  max-height: 120px;
  transition: all 0.3s;
}

.message-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(216, 27, 96, 0.1);
}

.btn-send {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  flex-shrink: 0;
}

.btn-send:hover:not(:disabled) {
  transform: scale(1.1);
}

.btn-send:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
