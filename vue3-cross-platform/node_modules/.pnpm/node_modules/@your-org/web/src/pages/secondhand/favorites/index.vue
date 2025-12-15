<template>
  <div class="favorites-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-heart"></i> 我的收藏
        </h1>
        <div class="header-actions">
          <button class="btn-clear" @click="clearFavorites">
            <i class="fas fa-trash"></i> 清空收藏
          </button>
        </div>
      </section>

      <!-- 收藏列表 -->
      <section class="favorites-section">
        <div v-if="favoritesList.length === 0" class="empty-state">
          <i class="fas fa-heart"></i>
          <h2>暂无收藏</h2>
          <p>您还没有收藏任何商品</p>
          <router-link to="/secondhand" class="btn-primary">
            <i class="fas fa-shopping-bag"></i> 去逛逛
          </router-link>
        </div>

        <div v-else class="favorites-grid">
          <div
            v-for="item in favoritesList"
            :key="item.id"
            class="favorite-card"
            @click="goToDetail(item.id)"
          >
            <div class="card-image">
              <i :class="item.icon"></i>
              <div class="card-tag">{{ item.tag }}</div>
              <button class="btn-favorite active" @click.stop="toggleFavorite(item.id)">
                <i class="fas fa-heart"></i>
              </button>
            </div>
            <div class="card-info">
              <div class="card-title">{{ item.title }}</div>
              <div class="card-desc">{{ item.desc }}</div>
              <div class="card-price">¥{{ item.price }}</div>
              <div class="card-seller">
                <span>{{ item.seller }}</span>
                <div class="seller-rating">
                  <i
                    v-for="n in 5"
                    :key="n"
                    :class="n <= Math.floor(item.rating) ? 'fas fa-star' : (n === Math.floor(item.rating) + 1 && item.rating % 1 !== 0 ? 'fas fa-star-half-alt' : 'far fa-star')"
                  ></i>
                  <span>{{ item.rating }}</span>
                </div>
              </div>
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

// 收藏列表数据
const favoritesList = ref([
  {
    id: 1,
    title: '高等数学教材',
    desc: '第七版上下册',
    price: 25,
    tag: '9成新',
    icon: 'fas fa-book',
    seller: '张同学',
    rating: 4.5
  },
  {
    id: 2,
    title: '联想笔记本电脑',
    desc: 'i5处理器轻薄本',
    price: 2200,
    tag: '8成新',
    icon: 'fas fa-laptop',
    seller: '李同学',
    rating: 4.0
  },
  {
    id: 3,
    title: '山地自行车',
    desc: '24速变速送锁',
    price: 380,
    tag: '7成新',
    icon: 'fas fa-bicycle',
    seller: '王同学',
    rating: 4.8
  },
  {
    id: 4,
    title: '校庆纪念卫衣',
    desc: 'L码全新未拆',
    price: 89,
    tag: '全新',
    icon: 'fas fa-tshirt',
    seller: '赵同学',
    rating: 4.2
  }
])

// 跳转到商品详情
const goToDetail = (id: number) => {
  router.push(`/secondhand/detail/${id}`)
}

// 取消收藏
const toggleFavorite = (id: number) => {
  const index = favoritesList.value.findIndex(item => item.id === id)
  if (index > -1) {
    favoritesList.value.splice(index, 1)
  }
}

// 清空所有收藏
const clearFavorites = () => {
  if (confirm('确定要清空所有收藏吗？')) {
    favoritesList.value = []
  }
}

onMounted(() => {
  // 从本地存储或API加载收藏列表
  // loadFavorites()
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

.favorites-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 1200px;
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

/* 收藏列表 */
.favorites-section {
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

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.favorite-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #eee;
}

.favorite-card:hover {
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.15);
  transform: translateY(-2px);
}

.card-image {
  width: 100%;
  height: 180px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.card-image i {
  font-size: 60px;
  color: var(--primary);
}

.card-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 10px;
  background: var(--primary);
  color: white;
  border-radius: 4px;
  font-size: 12px;
}

.btn-favorite {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  color: #ddd;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.btn-favorite.active {
  color: var(--primary);
}

.btn-favorite:hover {
  background: var(--primary);
  color: white;
}

.card-info {
  padding: 15px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-desc {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-price {
  font-size: 20px;
  font-weight: bold;
  color: var(--primary);
  margin-bottom: 10px;
}

.card-seller {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--muted);
}

.seller-rating {
  display: flex;
  align-items: center;
  gap: 2px;
}

.seller-rating i {
  color: #ffc107;
  font-size: 10px;
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

  .favorites-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }
}
</style>
