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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()

// 消息数据
const messages = ref([
  {
    id: 1,
    sender: '张同学',
    title: '关于高等数学教材的咨询',
    preview: '您好，我想了解一下这本教材的详细情况...',
    time: '2小时前',
    read: false,
    relatedProduct: '高等数学教材'
  },
  {
    id: 2,
    sender: '李同学',
    title: '商品已售出',
    preview: '您发布的联想笔记本电脑已被购买，请及时确认...',
    time: '1天前',
    read: false,
    relatedProduct: '联想笔记本电脑'
  },
  {
    id: 3,
    sender: '系统消息',
    title: '交易提醒',
    preview: '您的商品"山地自行车"有新的询价...',
    time: '2天前',
    read: true,
    relatedProduct: '山地自行车'
  },
  {
    id: 4,
    sender: '王同学',
    title: '商品咨询',
    preview: '请问这个商品还在吗？可以面交吗？',
    time: '3天前',
    read: true,
    relatedProduct: '校庆纪念卫衣'
  }
])

// 查看消息详情
const viewMessage = (message: any) => {
  // 标记为已读
  message.read = true
  // 跳转到消息详情或商品详情
  if (message.relatedProduct) {
    // router.push(`/secondhand/detail/${message.productId}`)
    // 暂时跳转到商品列表页
    router.push('/secondhand')
  }
}

onMounted(() => {
  // 加载消息列表
  // loadMessages()
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
