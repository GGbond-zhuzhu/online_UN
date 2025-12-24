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
// 引入 Vue 的组合式 API
import { computed, onMounted } from 'vue' // 从 vue 导入 computed 和 onMounted
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
// 引入 common 包中封装好的二手 Store
import { useSecondhandStore } from '@campus/common' // 从 @campus/common 导入二手相关 Pinia Store

const router = useRouter()

interface FavoriteItem {
  id: number // 商品 ID（用于跳转和取消收藏）
  title: string // 商品标题
  desc: string // 商品描述
  price: number // 商品价格
  tag: string // 标签文案（例如“在售”“已下架”）
  icon: string // 左侧类目图标（Font Awesome 类名）
  seller: string // 卖家名称
  rating: number // 卖家评分（此处使用固定值占位）
}

// 获取二手 Store 实例，用于统一管理收藏列表数据
const secondhandStore = useSecondhandStore() // 调用 useSecondhandStore 获取 Store 实例

// 将 Store 中的收藏商品映射为当前页面所需的展示结构
const favoritesList = computed<FavoriteItem[]>(() => {
  // 从 Store 中取得原始收藏列表（SecondhandGoods[]）
  const list = secondhandStore.favoriteList // 直接访问 Store 中的收藏数组
  // 将原始数据映射为页面展示模型
  return list.map((item) => ({
    id: item.id, // 使用商品 ID 作为主键
    title: item.title, // 商品标题
    desc: item.description || '', // 描述字段，后端可能为空，这里做兜底
    price: item.price || 0, // 价格字段，缺省时使用 0 兜底
    tag: item.status === 'ON_SALE' ? '在售' : '已下架', // 根据状态展示不同标签
    icon: getCategoryIcon(item.category), // 根据分类选择对应的 Font Awesome 图标
    seller: item.publisherName || '匿名用户', // 卖家名称，缺省时显示“匿名用户”
    rating: 4.5 // 此处先使用固定评分占位，后续可与后端真实评分字段打通
  })) // 返回新的数组供模板使用
})

// 透传 Store 的 loading 状态，方便后续在页面上做“加载中”提示
const loading = computed(() => secondhandStore.loading) // 使用 Store 中的 loading 状态

// 分类图标映射
const getCategoryIcon = (category: string): string => {
  const map: Record<string, string> = {
    BOOKS: 'fas fa-book',
    ELECTRONICS: 'fas fa-laptop',
    CLOTHING: 'fas fa-tshirt',
    DAILY: 'fas fa-home',
    SPORTS: 'fas fa-bicycle',
    OTHER: 'fas fa-box'
  }
  return map[category] || 'fas fa-box'
}

// 跳转到商品详情
const goToDetail = (id: number) => {
  router.push(`/secondhand/detail/${id}`)
}

// 取消收藏（单个）
const toggleFavorite = async (id: number) => {
  try {
    // 调用 Store 中的取消收藏方法，自动同步更新全局收藏状态
    await secondhandStore.removeFavorite(id) // 根据商品 ID 调用取消收藏接口
    // 本地映射列表会自动因为 Store 的变化而更新，无需手动过滤
  } catch (error) {
    console.error('取消收藏失败:', error)
    alert('取消收藏失败，请稍后重试')
  }
}

// 清空所有收藏（逐个取消）
const clearFavorites = async () => {
  // 如果当前收藏列表为空，则不进行任何操作
  if (!favoritesList.value.length) {
    return
  }
  // 弹窗确认，避免误操作清空所有收藏
  if (!confirm('确定要清空所有收藏吗？')) {
    return
  }
  try {
    // 调用 Store 中封装好的“清空收藏列表”方法
    await secondhandStore.clearFavoritesAll() // 内部会逐个调用取消收藏接口并清空本地状态
  } catch (error) {
    console.error('清空收藏失败:', error)
    alert('清空收藏失败，请稍后重试')
  }
}

// 从后端加载收藏列表
const loadFavorites = async () => {
  try {
    // 通过 Store 统一加载收藏列表（这里约定第一页最多 50 条）
    await secondhandStore.loadFavorites(1, 50) // 调用 Store 中封装好的加载方法
  } catch (error) {
    console.error('加载收藏列表失败:', error)
  }
}

onMounted(() => {
  loadFavorites()
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
