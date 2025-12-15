<template>
  <div class="detail-page">
    <NavBar />

    <div class="page-container">
      <!-- 商品图片轮播 -->
      <section class="product-images">
        <div class="main-image">
          <img :src="currentImage" :alt="product.title" />
          <div class="image-indicator">
            <span>{{ currentImageIndex + 1 }} / {{ product.images.length }}</span>
          </div>
        </div>
        <div class="thumbnail-list">
          <div
            v-for="(img, idx) in product.images"
            :key="idx"
            :class="['thumbnail', { active: idx === currentImageIndex }]"
            @click="currentImageIndex = idx"
          >
            <img :src="img" :alt="`${product.title} ${idx + 1}`" />
          </div>
        </div>
      </section>

      <!-- 商品基本信息 -->
      <section class="product-info-card">
        <div class="product-header">
          <h1 class="product-title">{{ product.title }}</h1>
          <div class="product-tags">
            <span class="tag condition">{{ product.condition }}</span>
            <span class="tag category">{{ getCategoryLabel(product.category) }}</span>
          </div>
        </div>
        <div class="product-price">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ product.price }}</span>
        </div>
        <div class="product-meta">
          <div class="meta-item">
            <i class="fas fa-map-marker-alt"></i>
            <span>{{ getCampusLabel(product.campus) }}</span>
          </div>
          <div class="meta-item">
            <i class="fas fa-clock"></i>
            <span>发布时间：{{ product.publishTime }}</span>
          </div>
          <div class="meta-item">
            <i class="fas fa-eye"></i>
            <span>浏览 {{ product.views }} 次</span>
          </div>
        </div>
      </section>

      <!-- 卖家信息 -->
      <section class="seller-card">
        <div class="seller-header">
          <div class="seller-avatar">
            <i class="fas fa-user"></i>
          </div>
          <div class="seller-info">
            <div class="seller-name">{{ product.seller }}</div>
            <div class="seller-rating">
              <i
                v-for="n in 5"
                :key="n"
                :class="n <= Math.floor(product.sellerRating) ? 'fas fa-star' : (n === Math.floor(product.sellerRating) + 1 && product.sellerRating % 1 !== 0 ? 'fas fa-star-half-alt' : 'far fa-star')"
              ></i>
              <span>{{ product.sellerRating }}</span>
            </div>
            <div class="seller-stats">
              <span>已售 {{ product.soldCount }} 件</span>
              <span>·</span>
              <span>好评率 {{ product.goodRate }}%</span>
            </div>
          </div>
        </div>
        <div class="seller-actions">
          <button class="btn contact-btn" @click="contactSeller">
            <i class="fas fa-comment"></i> 联系卖家
          </button>
          <button class="btn view-profile-btn" @click="viewSellerProfile">
            <i class="fas fa-user-circle"></i> 查看主页
          </button>
        </div>
      </section>

      <!-- 商品详情 -->
      <section class="product-detail-card">
        <h2 class="card-title">商品详情</h2>
        <div class="detail-content">
          <div class="detail-item">
            <label>商品描述：</label>
            <p>{{ product.description }}</p>
          </div>
          <div class="detail-item">
            <label>成色：</label>
            <span>{{ product.condition }}</span>
          </div>
          <div class="detail-item">
            <label>购买时间：</label>
            <span>{{ product.purchaseTime }}</span>
          </div>
          <div class="detail-item">
            <label>使用情况：</label>
            <span>{{ product.usage }}</span>
          </div>
          <div class="detail-item">
            <label>交易方式：</label>
            <span>{{ product.tradeMethod }}</span>
          </div>
        </div>
      </section>

      <!-- 推荐商品 -->
      <section class="recommendation-section">
        <h2 class="section-title">
          <i class="fas fa-heart"></i> 猜你喜欢
        </h2>
        <div class="recommendation-grid">
          <div
            v-for="item in recommendedProducts"
            :key="item.id"
            class="product-card"
            @click="goToDetail(item.id)"
          >
            <div class="product-image">
              <i :class="item.icon"></i>
              <div class="product-tag">{{ item.tag }}</div>
            </div>
            <div class="product-info">
              <div class="product-title">{{ item.title }}</div>
              <div class="product-desc">{{ item.desc }}</div>
              <div class="product-price">¥{{ item.priceValue }}</div>
              <div class="seller-info">
                <div class="seller-avatar"></div>
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

    <!-- 底部操作栏 -->
    <div class="bottom-bar">
      <button class="action-btn favorite" @click="toggleFavorite">
        <i :class="isFavorite ? 'fas fa-heart' : 'far fa-heart'"></i>
        <span>{{ isFavorite ? '已收藏' : '收藏' }}</span>
      </button>
      <button class="action-btn share" @click="shareProduct">
        <i class="fas fa-share-alt"></i>
        <span>分享</span>
      </button>
      <button class="btn primary buy-btn" @click="contactSeller">
        <i class="fas fa-comment"></i> 立即咨询
      </button>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const route = useRoute()
const router = useRouter()

// 当前图片索引
const currentImageIndex = ref(0)
const isFavorite = ref(false)

// 商品数据
const product = ref({
  id: 1,
  title: '高等数学教材',
  desc: '第七版上下册',
  price: 25,
  condition: '9成新',
  category: 'books',
  campus: 'main',
  publishTime: '2天前',
  views: 128,
  seller: '张同学',
  sellerRating: 4.5,
  soldCount: 12,
  goodRate: 95,
  description: '高等数学第七版上下册，9成新，无笔记，无破损。包含教材和习题册，适合大一学生使用。',
  purchaseTime: '2023年9月',
  usage: '使用一学期，保存完好',
  tradeMethod: '面交/快递',
  images: [
    'https://via.placeholder.com/600x400?text=商品图片1',
    'https://via.placeholder.com/600x400?text=商品图片2',
    'https://via.placeholder.com/600x400?text=商品图片3'
  ]
})

// 推荐商品
const recommendedProducts = ref([
  {
    id: 2,
    title: '线性代数教材',
    desc: '第五版',
    priceValue: 20,
    tag: '8成新',
    icon: 'fas fa-book',
    seller: '李同学',
    rating: 4.0
  },
  {
    id: 3,
    title: '概率论与数理统计',
    desc: '第三版',
    priceValue: 22,
    tag: '9成新',
    icon: 'fas fa-book',
    seller: '王同学',
    rating: 4.8
  },
  {
    id: 4,
    title: '大学物理教材',
    desc: '上下册',
    priceValue: 30,
    tag: '全新',
    icon: 'fas fa-book',
    seller: '赵同学',
    rating: 4.2
  }
])

// 计算属性
const currentImage = computed(() => {
  return product.value.images[currentImageIndex.value] || product.value.images[0]
})

// 方法
const getCategoryLabel = (category: string) => {
  const categoryMap: Record<string, string> = {
    books: '教材书籍',
    digital: '数码产品',
    clothing: '服饰鞋包',
    daily: '生活用品',
    sports: '运动户外',
    others: '其他'
  }
  return categoryMap[category] || category
}

const getCampusLabel = (campus: string) => {
  const campusMap: Record<string, string> = {
    main: '主校区',
    east: '东校区',
    west: '西校区'
  }
  return campusMap[campus] || campus
}

const contactSeller = () => {
  // 跳转到聊天页面
  // router.push(`/chat?sellerId=${product.value.sellerId}`)
  // 暂时显示提示
  if (navigator.share) {
    navigator.share({
      title: product.value.title,
      text: `我想咨询一下${product.value.title}`,
      url: window.location.href
    }).catch(() => {
      // 分享失败，可以跳转到聊天页面或显示联系方式
    })
  }
}

const viewSellerProfile = () => {
  // 跳转到卖家主页
  // router.push(`/profile?userId=${product.value.sellerId}`)
}

const toggleFavorite = async () => {
  try {
    isFavorite.value = !isFavorite.value
    // TODO: 调用收藏API
    // await toggleFavoriteAPI(product.value.id)
  } catch (error) {
    console.error('收藏操作失败:', error)
    // 回滚状态
    isFavorite.value = !isFavorite.value
  }
}

const shareProduct = async () => {
  try {
    if (navigator.share) {
      await navigator.share({
        title: product.value.title,
        text: product.value.description,
        url: window.location.href
      })
    } else {
      // 降级方案：复制链接到剪贴板
      await navigator.clipboard.writeText(window.location.href)
      alert('链接已复制到剪贴板')
    }
  } catch (error) {
    // 用户取消分享或出错
    if ((error as Error).name !== 'AbortError') {
      console.error('分享失败:', error)
    }
  }
}

const goToDetail = (id: number) => {
  router.push(`/secondhand/detail/${id}`)
}

onMounted(() => {
  // 从路由参数获取商品ID
  const productId = route.params.id
  if (productId) {
    // 根据ID加载商品数据
    // loadProduct(productId)
  }
})
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --primary-dark: #c2185b;
  --text: #333;
  --muted: #666;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
  --card-shadow: 0 4px 18px rgba(216, 27, 96, 0.12);
  --border: #f1d9e9;
}

.detail-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 80px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 15px 40px;
}

/* 商品图片 */
.product-images {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.main-image {
  position: relative;
  width: 100%;
  height: 500px;
  margin-bottom: 15px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.image-indicator {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  overflow-x: auto;
}

.thumbnail {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 6px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s;
}

.thumbnail:hover {
  border-color: var(--primary);
}

.thumbnail.active {
  border-color: var(--primary);
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息卡片 */
.product-info-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.product-header {
  margin-bottom: 15px;
}

.product-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 12px;
}

.product-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.tag.condition {
  background: #fff3e0;
  color: #f57c00;
}

.tag.category {
  background: #f9f0ff;
  color: var(--primary);
}

.product-price {
  display: flex;
  align-items: baseline;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.price-symbol {
  font-size: 20px;
  color: var(--primary);
  font-weight: bold;
}

.price-value {
  font-size: 32px;
  color: var(--primary);
  font-weight: bold;
}

.product-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--muted);
}

.meta-item i {
  color: var(--primary);
  font-size: 12px;
}

/* 卖家信息卡片 */
.seller-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.seller-header {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.seller-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: var(--primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.seller-info {
  flex: 1;
}

.seller-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 6px;
}

.seller-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 6px;
}

.seller-rating i {
  color: #ffc107;
  font-size: 14px;
}

.seller-rating span {
  font-size: 14px;
  color: var(--muted);
  margin-left: 4px;
}

.seller-stats {
  font-size: 12px;
  color: var(--muted);
  display: flex;
  gap: 8px;
}

.seller-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 10px 20px;
  border-radius: 12px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.contact-btn {
  flex: 1;
}

.view-profile-btn {
  flex: 1;
}

/* 商品详情卡片 */
.product-detail-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary);
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detail-item {
  display: flex;
  gap: 10px;
}

.detail-item label {
  font-weight: 600;
  color: var(--text);
  min-width: 100px;
}

.detail-item p,
.detail-item span {
  color: var(--muted);
  line-height: 1.6;
}

.detail-item p {
  flex: 1;
}

/* 推荐商品 */
.recommendation-section {
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: var(--text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title i {
  color: var(--primary);
}

.recommendation-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 15px;
}

.product-card {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.15);
  transform: translateY(-2px);
}

.product-card .product-image {
  width: 100%;
  height: 150px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.product-card .product-image i {
  font-size: 48px;
  color: var(--primary);
}

.product-card .product-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 8px;
  background: var(--primary);
  color: white;
  border-radius: 4px;
  font-size: 11px;
}

.product-card .product-info {
  padding: 12px;
}

.product-card .product-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-card .product-desc {
  font-size: 12px;
  color: var(--muted);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-card .product-price {
  font-size: 16px;
  font-weight: bold;
  color: var(--primary);
  margin-bottom: 8px;
}

.product-card .seller-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--muted);
}

.product-card .seller-avatar {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #ddd;
}

.product-card .seller-rating {
  display: flex;
  align-items: center;
  gap: 2px;
  margin-left: auto;
}

.product-card .seller-rating i {
  color: #ffc107;
  font-size: 10px;
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 12px 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 10px;
  z-index: 100;
}

.action-btn {
  padding: 10px 16px;
  border-radius: 12px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-btn:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.action-btn.favorite.active {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.buy-btn {
  flex: 1;
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.buy-btn:hover {
  background: var(--primary-dark);
  border-color: var(--primary-dark);
}

@media (max-width: 768px) {
  .main-image {
    height: 300px;
  }

  .product-title {
    font-size: 20px;
  }

  .price-value {
    font-size: 28px;
  }

  .recommendation-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .seller-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .main-image {
    height: 250px;
  }

  .thumbnail {
    width: 60px;
    height: 60px;
  }

  .recommendation-grid {
    grid-template-columns: 1fr;
  }
}
</style>
