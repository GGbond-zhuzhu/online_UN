<template>
  <div class="messages-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-comments"></i> 我的消息
        </h1>
      </section>

      <!-- 消息列表 -->
      <section class="messages-section">
        <div v-if="messages.length === 0" class="empty-state">
          <i class="fas fa-inbox"></i>
          <h2>暂无消息</h2>
          <p>您还没有收到任何消息</p>
        </div>

        <div v-else class="messages-list">
          <div
            v-for="message in messages"
            :key="message.id"
            class="message-item"
            :class="{ unread: !message.read }"
            @click="viewMessage(message)"
          >
            <div class="message-avatar">
              <i class="fas fa-user"></i>
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="sender-name">{{ message.sender }}</span>
                <span class="message-time">{{ message.time }}</span>
              </div>
              <div class="message-title">{{ message.title }}</div>
              <div class="message-preview">{{ message.preview }}</div>
              <div class="message-tags" v-if="message.relatedProduct">
                <span class="tag">相关商品：{{ message.relatedProduct }}</span>
              </div>
            </div>
            <div class="message-status">
              <i v-if="!message.read" class="fas fa-circle unread-dot"></i>
            </div>
          </div>
        </div>
      </section>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue' // 引入 ref/onMounted，用于管理列表数据和生命周期
import { useRouter } from 'vue-router' // 引入路由，方便在点击会话时跳转到聊天页
import NavBar from '@/components/common/NavBar.vue' // 顶部导航栏组件
import AppFooter from '@/components/common/AppFooter.vue' // 底部页脚组件
import FloatingMenu from '@/components/common/FloatingMenu.vue' // 右下角浮动菜单组件
import { request } from '@campus/common' // 引入公共请求工具，方便调用后端聊天接口

const router = useRouter() // 创建路由实例，后面通过它跳转到 /chat 页面

// 定义前端展示用的“二手会话消息”类型
interface SecondhandConversationItem {
  id: number // 会话ID（后端 chat_conversation 的主键）
  sender: string // 对方昵称（例如“张同学”）
  title: string // 列表标题，这里统一为“与xxx的聊天”
  preview: string // 最近一条消息的内容预览
  time: string // 最近一条消息时间的格式化结果
  read: boolean // 是否全部已读（未读数为0则视为已读）
  relatedProduct?: string // 相关商品标题（当前后端未提供，这里预留字段）
  targetUserId: number // 聊天对象用户ID，跳转聊天页面时需要传给前端
}

// 消息数据列表，实际展示的是“聊天会话列表”，而不是简单静态内容
const messages = ref<SecondhandConversationItem[]>([]) // 初始为空数组，进入页面时从后端加载

// 工具函数：将后端返回的时间字符串格式化为“月-日 时:分”形式，便于在列表中展示
const formatTime = (raw: string | Date | null): string => {
  if (!raw) {
    return '' // 如果时间为空则返回空字符串
  }
  const date = raw instanceof Date ? raw : new Date(raw) // 将字符串转换为 Date 对象
  // 使用本地化时间显示“月-日 时:分”，例如“04-12 10:30”
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const time = date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  return `${month}-${day} ${time}`
}

// 从后端加载当前用户的聊天会话列表（只展示与二手交易相关的会话）
const loadConversations = async () => {
  try {
    // 调用后端 GET /api/chat/conversations 接口，获取当前用户参与的所有会话列表
    const list: any[] = await request.get('/api/chat/conversations')

    // 将后端数据转换为前端用于展示的结构
    messages.value = (list || []).map((item: any) => {
      // 后端返回字段：conversationId、targetUserId、targetUserName、lastMessage、lastMessageTime、unreadCount
      return {
        id: item.conversationId, // 以会话ID作为当前列表项的主键
        sender: item.targetUserName || '用户', // 使用对方昵称作为发送者名称
        title: `与 ${item.targetUserName || '用户'} 的聊天`, // 列表标题统一为“与xx的聊天”
        preview: item.lastMessage || '暂时没有聊天内容', // 如果没有最后一条消息则给一个占位提示
        time: formatTime(item.lastMessageTime || null), // 将最后消息时间格式化为列表展示文本
        read: !item.unreadCount || item.unreadCount === 0, // 未读数为0则视为已读
        relatedProduct: undefined, // 当前后端未返回具体商品信息，这里先留空以便后续扩展
        targetUserId: item.targetUserId // 保存对方用户ID，后续跳转聊天时需要传入
      } as SecondhandConversationItem
    })
  } catch (error) {
    console.error('加载聊天会话列表失败:', error) // 控制台输出错误信息，方便调试
  }
}

// 查看消息详情：这里实际逻辑是“进入与该用户的聊天窗口”
const viewMessage = (message: SecondhandConversationItem) => {
  // 点击后先将本地状态标记为已读，界面上去掉“未读高亮”效果
  message.read = true

  // 使用已知的会话ID和对方用户ID跳转到聊天页面
  router.push({
    name: 'chat', // 使用路由名称，指向 /chat 对应的页面
    query: {
      conversationId: message.id, // 将当前会话ID传递给聊天页面
      targetUserId: message.targetUserId // 同时传入聊天对象用户ID，便于前端/后端校验
    }
  })
}

// 组件挂载完成后自动加载一次会话列表
onMounted(() => {
  loadConversations() // 进入“我的消息”页面时，从后端拉取当前用户的所有聊天会话
})
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --primary-dark: #c2185b;
  --text: #333;
  --muted: #666;
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
  padding: 20px 15px 40px;
}

/* 页面标题 */
.page-header {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--text);
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title i {
  color: var(--primary);
}

/* 消息列表 */
.messages-section {
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
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

.empty-state h2 {
  font-size: 20px;
  color: var(--text);
  margin-bottom: 10px;
}

.empty-state p {
  color: var(--muted);
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
  display: flex;
  gap: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.message-item:hover {
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.15);
  transform: translateY(-2px);
}

.message-item.unread {
  background: #fff5f9;
  border-color: var(--primary);
}

.message-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: var(--primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.sender-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
}

.message-time {
  font-size: 12px;
  color: var(--muted);
}

.message-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 6px;
}

.message-preview {
  font-size: 14px;
  color: var(--muted);
  line-height: 1.6;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.message-tags {
  margin-top: 8px;
}

.tag {
  display: inline-block;
  padding: 4px 10px;
  background: #f9f0ff;
  color: var(--primary);
  border-radius: 4px;
  font-size: 12px;
}

.message-status {
  display: flex;
  align-items: flex-start;
  flex-shrink: 0;
}

.unread-dot {
  color: var(--primary);
  font-size: 8px;
  margin-top: 6px;
}

@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }

  .page-title {
    font-size: 24px;
  }

  .message-item {
    padding: 15px;
  }

  .message-avatar {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
}
</style>
