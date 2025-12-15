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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()

const activeTab = ref<'all' | 'system' | 'parttime' | 'secondhand'>('all')

// 消息列表
const messages = ref([
  {
    id: 1,
    type: 'system',
    title: '系统通知',
    preview: '您的身份认证已通过审核',
    time: '2小时前',
    read: false,
    icon: 'fas fa-bell'
  },
  {
    id: 2,
    type: 'parttime',
    title: '兼职申请',
    preview: '您的"校园推广专员"申请已通过',
    time: '1天前',
    read: false,
    icon: 'fas fa-briefcase'
  },
  {
    id: 3,
    type: 'secondhand',
    title: '商品咨询',
    preview: '有人咨询您的商品"二手笔记本电脑"',
    time: '2天前',
    read: true,
    icon: 'fas fa-shopping-bag'
  },
  {
    id: 4,
    type: 'system',
    title: '系统通知',
    preview: '欢迎使用上大学Online平台',
    time: '3天前',
    read: true,
    icon: 'fas fa-bell'
  }
])

// 筛选后的消息
const filteredMessages = computed(() => {
  if (activeTab.value === 'all') {
    return messages.value
  }
  return messages.value.filter(msg => msg.type === activeTab.value)
})

// 查看消息
const viewMessage = (message: any) => {
  // 标记为已读
  message.read = true
  
  // 根据消息类型跳转
  switch (message.type) {
    case 'parttime':
      router.push('/parttime/applications')
      break
    case 'secondhand':
      router.push('/secondhand/messages')
      break
    default:
      // 系统消息，显示详情
      alert(message.preview)
      break
  }
}
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
