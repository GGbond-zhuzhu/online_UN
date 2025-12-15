<template>
  <div class="history-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-history"></i> 浏览记录
        </h1>
        <div class="header-actions">
          <button class="btn-clear" @click="clearHistory">
            <i class="fas fa-trash"></i> 清空记录
          </button>
        </div>
      </section>

      <!-- 浏览记录列表 -->
      <section class="history-section">
        <div v-if="historyList.length === 0" class="empty-state">
          <i class="fas fa-history"></i>
          <h2>暂无浏览记录</h2>
          <p>您还没有浏览过任何商品</p>
          <router-link to="/secondhand" class="btn-primary">
            <i class="fas fa-shopping-bag"></i> 去逛逛
          </router-link>
        </div>

        <div v-else class="history-list">
          <div
            v-for="item in historyList"
            :key="item.id"
            class="history-item"
            @click="goToDetail(item.id)"
          >
            <div class="item-image">
              <i :class="item.icon"></i>
              <div class="item-tag">{{ item.tag }}</div>
            </div>
            <div class="item-info">
              <div class="item-title">{{ item.title }}</div>
              <div class="item-desc">{{ item.desc }}</div>
              <div class="item-meta">
                <span class="item-price">¥{{ item.price }}</span>
                <span class="item-time">{{ item.viewTime }}</span>
              </div>
            </div>
            <button class="btn-remove" @click.stop="removeItem(item.id)">
              <i class="fas fa-times"></i>
            </button>
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

// 浏览记录数据
const historyList = ref([
  {
    id: 1,
    title: '高等数学教材',
    desc: '第七版上下册',
    price: 25,
    tag: '9成新',
    icon: 'fas fa-book',
    viewTime: '2小时前'
  },
  {
    id: 2,
    title: '联想笔记本电脑',
    desc: 'i5处理器轻薄本',
    price: 2200,
    tag: '8成新',
    icon: 'fas fa-laptop',
    viewTime: '1天前'
  },
  {
    id: 3,
    title: '山地自行车',
    desc: '24速变速送锁',
    price: 380,
    tag: '7成新',
    icon: 'fas fa-bicycle',
    viewTime: '2天前'
  },
  {
    id: 4,
    title: '校庆纪念卫衣',
    desc: 'L码全新未拆',
    price: 89,
    tag: '全新',
    icon: 'fas fa-tshirt',
    viewTime: '3天前'
  }
])

// 跳转到商品详情
const goToDetail = (id: number) => {
  router.push(`/secondhand/detail/${id}`)
}

// 移除单条记录
const removeItem = (id: number) => {
  const index = historyList.value.findIndex(item => item.id === id)
  if (index > -1) {
    historyList.value.splice(index, 1)
  }
}

// 清空所有记录
const clearHistory = () => {
  if (confirm('确定要清空所有浏览记录吗？')) {
    historyList.value = []
  }
}

onMounted(() => {
  // 从本地存储或API加载浏览记录
  // loadHistory()
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

.history-page {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.header-actions {
  display: flex;
  gap: 10px;
}

.btn-clear {
  padding: 10px 20px;
  border-radius: 12px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--muted);
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-clear:hover {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

/* 浏览记录列表 */
.history-section {
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
  margin-bottom: 20px;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: var(--primary);
  color: white;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background: var(--primary-dark);
  transform: translateY(-2px);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.history-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  display: flex;
  gap: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.history-item:hover {
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.15);
  transform: translateY(-2px);
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
}

.item-image i {
  font-size: 40px;
  color: var(--primary);
}

.item-tag {
  position: absolute;
  top: 6px;
  right: 6px;
  padding: 4px 8px;
  background: var(--primary);
  color: white;
  border-radius: 4px;
  font-size: 11px;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 6px;
}

.item-desc {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 10px;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  font-size: 20px;
  font-weight: bold;
  color: var(--primary);
}

.item-time {
  font-size: 12px;
  color: var(--muted);
}

.btn-remove {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.05);
  color: var(--muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.btn-remove:hover {
  background: #ff4d4f;
  color: white;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .page-title {
    font-size: 24px;
  }

  .history-item {
    padding: 15px;
  }

  .item-image {
    width: 80px;
    height: 80px;
  }

  .item-image i {
    font-size: 32px;
  }
}
</style>
