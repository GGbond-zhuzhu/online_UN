<template>
  <div class="secondhand-page">
    <!-- 顶部导航栏（对应HTML的header） -->
    <NavBar />

    <div class="container">
      <!-- 搜索栏区域 - 适配全局配色 -->
      <section class="search-bar-section">
        <div class="search-container">
          <div class="search-box">
            <input
              v-model="filters.search"
              type="text"
              placeholder="搜索二手商品..."
              class="search-input"
              @keyup.enter="handleFilter"
            />
            <i class="fas fa-search search-icon"></i>
          </div>
          <div class="search-actions">
            <button class="action-btn" @click="handleQuick('publish')">
              <i class="fas fa-plus"></i> 发布闲置
            </button>
            <button class="action-btn" @click="handleQuick('message')">
              <i class="fas fa-comment"></i> 消息
            </button>
            <button class="action-btn" @click="handleQuick('profile')">
              <i class="fas fa-user"></i> 个人中心
            </button>
          </div>
        </div>
      </section>

      <!-- 筛选栏（完全对齐HTML的filter-section） -->
      <section class="filter-section">
        <div class="filter-row">
          <div class="filter-group">
            <span class="filter-label">品类：</span>
            <select class="filter-select" v-model="filters.category">
              <option value="all">全部品类</option>
              <option value="books">教材书籍</option>
              <option value="digital">数码产品</option>
              <option value="clothing">服饰鞋包</option>
              <option value="daily">生活用品</option>
              <option value="sports">运动户外</option>
              <option value="others">其他</option>
            </select>
          </div>
          <div class="filter-group">
            <span class="filter-label">成色：</span>
            <select class="filter-select" v-model="filters.condition">
              <option value="all">全部成色</option>
              <option value="new">全新</option>
              <option value="90">9成新</option>
              <option value="80">8成新</option>
              <option value="70">7成新</option>
              <option value="60">6成及以下</option>
            </select>
          </div>
          <div class="filter-group">
            <span class="filter-label">价格：</span>
            <div class="price-range">
              <input type="number" class="filter-input" v-model="filters.priceMin" placeholder="最低价" min="0" />
              <span>-</span>
              <input type="number" class="filter-input" v-model="filters.priceMax" placeholder="最高价" min="0" />
            </div>
          </div>
          <div class="filter-group">
            <span class="filter-label">校区：</span>
            <select class="filter-select" v-model="filters.campus">
              <option value="all">全部校区</option>
              <option value="main">主校区</option>
              <option value="east">东校区</option>
              <option value="west">西校区</option>
            </select>
          </div>
          <div class="filter-actions">
            <button class="confirm-btn" @click="handleFilter">确定筛选</button>
            <button class="reset-btn" @click="handleReset">重置</button>
          </div>
        </div>
      </section>

      <!-- 主要内容区域（完全对齐HTML的main-content） -->
      <div class="main-content">
        <!-- 商品列表区域 -->
        <div class="product-list">
          <!-- 新增：搜索匹配盒子 - 位于猜你喜欢上方 -->
          <div v-if="hasSearchOrFilter" class="search-result-section">
            <h2 class="section-title">
              <i class="fas fa-search"></i> 搜索结果
            </h2>
            <!-- 搜索/筛选有结果 -->
            <div v-if="filteredProducts.length > 0" class="recommendation-grid">
              <div class="product-card" v-for="item in filteredProducts" :key="item.id" @click="goToDetail(item.id)">
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
            <!-- 搜索/筛选无结果 -->
            <div v-else class="no-results">
              <i class="fas fa-exclamation-circle"></i>
              <h2>没有找到符合条件的商品~</h2>
              <p>请尝试调整搜索关键词或筛选条件</p>
            </div>
          </div>

          <!-- 猜你喜欢盒子 - 一直存在 -->
          <div class="recommendation-section">
            <h2 class="section-title"><i class="fas fa-heart"></i> 猜你喜欢</h2>
            <div class="recommendation-grid">
              <div class="product-card" v-for="item in recommendedProducts" :key="item.id" @click="goToDetail(item.id)">
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
          </div>
        </div>

        <!-- 侧边栏（完全对齐HTML的sidebar） -->
        <div class="sidebar">
          <div class="sidebar-widget">
            <h3 class="widget-title">热门分类</h3>
            <ul class="category-list">
              <li class="category-item" v-for="item in hotCategories" :key="item.key" @click="filterByCategory(item.key)">{{ item.name }}</li>
            </ul>
          </div>

          <div class="sidebar-widget">
            <h3 class="widget-title">快捷操作</h3>
            <div class="quick-action">
              <a href="#" @click.prevent="handleQuick('publish')" class="action-button">
                <i class="fas fa-plus-circle"></i>
                <span>发布闲置</span>
              </a>
              <a href="#" @click.prevent="handleQuick('message')" class="action-button">
                <i class="fas fa-comments"></i>
                <span>我的消息</span>
              </a>
              <a href="#" @click.prevent="handleQuick('history')" class="action-button">
                <i class="fas fa-history"></i>
                <span>浏览记录</span>
              </a>
              <a href="#" @click.prevent="handleQuick('favorite')" class="action-button">
                <i class="fas fa-heart"></i>
                <span>我的收藏</span>
              </a>
            </div>
          </div>

          <div class="sidebar-widget">
            <h3 class="widget-title">交易提示</h3>
            <div class="safety-tips">
              <p v-for="(item, idx) in tips" :key="idx">{{ idx + 1 }}. {{ item }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 页脚（完全对齐HTML的footer） -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <div class="footer-logo">上大学Online</div>
            <p class="contact-info">我们致力于构建安全、便捷的校园二手交易生态，促进资源循环利用，丰富校园生活。</p>
          </div>
          <div class="footer-section">
            <h3>联系我们</h3>
            <div class="contact-info">
              <p>服务热线：400-123-4567</p>
              <p>二手交易客服：secondhand@campus.edu.cn</p>
              <p>问题反馈：feedback@campus.edu.cn</p>
            </div>
          </div>
          <div class="footer-section">
            <h3>快速链接</h3>
            <div class="footer-links">
              <router-link to="/">首页</router-link>
              <a href="#">发布指南</a>
              <a href="#">安全提示</a>
              <a href="#">纠纷处理</a>
            </div>
          </div>
        </div>
        <div class="copyright">
          © 2024 上大学Online校园综合服务平台 版权所有 | 让校园生活更简单
        </div>
      </div>
    </footer>

    <FloatingMenu />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()

// 筛选条件（对齐HTML的筛选参数）
const filters = reactive({
  category: 'all',
  condition: 'all',
  priceMin: '',
  priceMax: '',
  campus: 'all',
  search: ''
})

// 所有商品数据
const allProducts = ref([
  {
    id: 1,
    title: "高等数学教材",
    desc: "第七版上下册",
    priceValue: 25,
    tag: "9成新",
    icon: "fas fa-book",
    seller: "张同学",
    rating: 4.5,
    category: "books",
    condition: "90",
    campus: "main"
  },
  {
    id: 2,
    title: "联想笔记本电脑",
    desc: "i5处理器轻薄本",
    priceValue: 2200,
    tag: "8成新",
    icon: "fas fa-laptop",
    seller: "李同学",
    rating: 4.0,
    category: "digital",
    condition: "80",
    campus: "main"
  },
  {
    id: 3,
    title: "山地自行车",
    desc: "24速变速送锁",
    priceValue: 380,
    tag: "7成新",
    icon: "fas fa-bicycle",
    seller: "王同学",
    rating: 4.8,
    category: "sports",
    condition: "70",
    campus: "east"
  },
  {
    id: 4,
    title: "校庆纪念卫衣",
    desc: "L码全新未拆",
    priceValue: 89,
    tag: "全新",
    icon: "fas fa-tshirt",
    seller: "赵同学",
    rating: 4.2,
    category: "clothing",
    condition: "new",
    campus: "west"
  },
  {
    id: 5,
    title: "静音增氧泵",
    desc: "W/12W可调气量",
    priceValue: 89,
    tag: "全新转让",
    icon: "fas fa-fan",
    seller: "刘同学",
    rating: 4.7,
    category: "daily",
    condition: "new",
    campus: "main"
  },
  {
    id: 6,
    title: "苹果17手机壳",
    desc: "全包防摔",
    priceValue: 25,
    tag: "热卖",
    icon: "fas fa-mobile",
    seller: "陈同学",
    rating: 4.3,
    category: "digital",
    condition: "new",
    campus: "main"
  },
  {
    id: 7,
    title: "电动车挡风被",
    desc: "加厚冬季款",
    priceValue: 45,
    tag: "新品",
    icon: "fas fa-motorcycle",
    seller: "孙同学",
    rating: 4.9,
    category: "daily",
    condition: "new",
    campus: "east"
  },
  {
    id: 8,
    title: "儿童羽绒服",
    desc: "90%白鸭绒",
    priceValue: 120,
    tag: "包邮",
    icon: "fas fa-tshirt",
    seller: "周同学",
    rating: 4.1,
    category: "clothing",
    condition: "new",
    campus: "west"
  }
])

// 热门分类（对齐HTML的category-list）
const hotCategories = ref([
  { key: 'books', name: '教材书籍' },
  { key: 'digital', name: '数码产品' },
  { key: 'clothing', name: '服饰鞋包' },
  { key: 'daily', name: '生活用品' },
  { key: 'sports', name: '运动户外' },
  { key: 'appliance', name: '电器家具' },
  { key: 'beauty', name: '美妆个护' },
  { key: 'others', name: '其他' }
])

// 交易提示（对齐HTML的safety-tips）
const tips = ref([
  '选择平台认证的卖家',
  '线下交易注意安全',
  '仔细检查商品质量',
  '保留交易凭证',
  '遇到问题及时反馈'
])

// 判断是否有搜索或筛选操作
const hasSearchOrFilter = computed(() => {
  return filters.search.trim() !== '' ||
         filters.category !== 'all' ||
         filters.condition !== 'all' ||
         filters.priceMin.trim() !== '' ||
         filters.priceMax.trim() !== '' ||
         filters.campus !== 'all'
})

// 搜索/筛选结果
const filteredProducts = computed(() => {
  return allProducts.value.filter(product => {
    // 品类筛选
    if (filters.category !== 'all' && product.category !== filters.category) return false
    
    // 成色筛选
    if (filters.condition !== 'all' && product.condition !== filters.condition) return false
    
    // 价格筛选
    if (filters.priceMin && product.priceValue < Number(filters.priceMin)) return false
    if (filters.priceMax && product.priceValue > Number(filters.priceMax)) return false
    
    // 校区筛选
    if (filters.campus !== 'all' && product.campus !== filters.campus) return false
    
    // 搜索筛选
    if (filters.search.trim()) {
      const key = filters.search.trim().toLowerCase()
      return product.title.toLowerCase().includes(key) || product.desc.toLowerCase().includes(key)
    }
    
    return true
  })
})

// 猜你喜欢商品（固定显示所有商品，也可根据需求修改为随机推荐等逻辑）
const recommendedProducts = computed(() => {
  // 这里保持显示所有商品，可根据需求修改为：
  // 1. 随机推荐部分商品
  // 2. 排除搜索/筛选结果后推荐
  // 3. 根据用户行为推荐
  return allProducts.value
})

// 处理筛选（空函数，computed已自动处理）
const handleFilter = () => {}

// 重置筛选条件
const handleReset = () => {
  filters.category = 'all'
  filters.condition = 'all'
  filters.priceMin = ''
  filters.priceMax = ''
  filters.campus = 'all'
  filters.search = ''
}

// 按分类筛选
const filterByCategory = (categoryKey: string) => {
  filters.category = categoryKey
}

// 跳转到商品详情页
const goToDetail = (id: number) => {
  router.push(`/secondhand/detail/${id}`)
}

// 快捷操作处理
const handleQuick = (type: string) => {
  switch (type) {
    case 'publish':
      router.push('/secondhand/publish')
      break
    case 'message':
      router.push('/secondhand/messages')
      break
    case 'history':
      router.push('/secondhand/history')
      break
    case 'favorite':
      router.push('/secondhand/favorites')
      break
    case 'profile':
      router.push('/profile')
      break
  }
}
</script>

<!-- 完全复用HTML的样式，解决scoped变量问题 -->
<style scoped>
/* 基础布局 - 完全对齐HTML的全局样式 */
.secondhand-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
  color: #333;
  line-height: 1.6;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Arial', 'Microsoft YaHei', sans-serif;
}

a {
  text-decoration: none;
  color: #444;
  transition: all 0.3s;
}

a:hover {
  color: #d81b60 !important;
}

li {
  list-style: none;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
}

/* 搜索栏样式 - 适配全局#d81b60主色 */
.search-bar-section {
  background: white;
  border-radius: 12px;
  padding: 15px 20px;
  margin: 20px 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.search-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.search-box {
  flex: 1;
  max-width: 600px;
  position: relative;
}

.search-input {
  width: 100%;
  padding: 12px 15px 12px 20px;
  border: 2px solid #d81b60 !important;
  border-radius: 25px;
  font-size: 16px;
  outline: none;
  box-shadow: 0 2px 5px rgba(216, 27, 96, 0.2);
  color: #333;
}

.search-input::placeholder {
  color: #666;
  opacity: 0.8;
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #d81b60 !important;
  font-size: 18px;
  cursor: pointer;
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.action-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #333;
  display: flex;
  align-items: center;
  padding: 8px 15px;
  border-radius: 20px;
  transition: all 0.3s;
}

.action-btn:hover {
  background: #f9f0ff;
  color: #d81b60 !important;
}

.action-btn i {
  margin-right: 5px;
}

/* 筛选栏 - 100%复用HTML样式 */
.filter-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin: 20px 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
  align-items: center;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.filter-select,
.filter-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
  font-size: 14px;
}

.filter-input {
  width: 100px;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 5px;
}

.filter-actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.confirm-btn {
  background: #d81b60 !important;
  color: white !important;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.confirm-btn:hover {
  background: #c2185b !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(216, 27, 96, 0.3);
}

.reset-btn {
  background: #f5f5f5 !important;
  color: #666 !important;
  border: 1px solid #ddd;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.reset-btn:hover {
  background: #e0e0e0 !important;
}

/* 主要内容区域 - 新增搜索结果区域样式 */
.main-content {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
}

.product-list {
  flex: 1;
}

/* 搜索结果区域 */
.search-result-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

/* 猜你喜欢区域 */
.recommendation-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
  display: flex;
  align-items: center;
}

.section-title i {
  color: #d81b60 !important;
  margin-right: 8px;
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
  transition: transform 0.3s;
  cursor: pointer;
  border: 1px solid #f0f0f0;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(216, 27, 96, 0.2);
}

.product-image {
  height: 150px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.product-image i {
  font-size: 50px;
  color: #d81b60 !important;
}

.product-info {
  padding: 10px;
}

.product-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: #d81b60 !important;
}

.product-tag {
  position: absolute;
  top: 5px;
  right: 5px;
  background: #d81b60 !important;
  color: white !important;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.seller-info {
  display: flex;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
  color: #888;
}

.seller-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #ddd;
  margin-right: 5px;
}

.seller-rating {
  margin-left: auto;
  display: flex;
  align-items: center;
}

.seller-rating i {
  color: #ffc107 !important;
  font-size: 10px;
  margin-right: 2px;
}

/* 无结果提示 - 调整样式 */
.no-results {
  text-align: center;
  padding: 60px 20px;
  background: #fafafa;
  border-radius: 10px;
  margin: 10px 0;
}

.no-results i {
  font-size: 60px;
  color: #d81b60 !important;
  margin-bottom: 20px;
}

.no-results h2 {
  font-size: 24px;
  margin-bottom: 10px;
  color: #333;
}

.no-results p {
  color: #666;
  margin-bottom: 20px;
}

/* 侧边栏 - 100%复用HTML样式 */
.sidebar {
  width: 300px;
}

.sidebar-widget {
  background: white;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.widget-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  color: #d81b60 !important;
}

.category-list {
  list-style: none;
}

.category-item {
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: color 0.3s;
}

.category-item:hover {
  color: #d81b60 !important;
}

.category-item:last-child {
  border-bottom: none;
}

.quick-action {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-button {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background: #f5f5f5 !important;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #444 !important;
}

.action-button:hover {
  background: #f9f0ff !important;
  color: #d81b60 !important;
}

.action-button i {
  margin-right: 10px;
  font-size: 18px;
}

.safety-tips p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 8px;
}

/* 页脚 - 100%复用HTML样式 */
.footer {
  background: #2c3e50 !important;
  color: white !important;
  padding: 50px 0 20px;
  margin-top: 60px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-bottom: 30px;
}

.footer-section h3 {
  color: white !important;
  margin-bottom: 20px;
  font-size: 18px;
}

.footer-logo {
  font-size: 24px;
  font-weight: bold;
  color: #d81b60 !important;
  margin-bottom: 15px;
}

.footer-links a {
  color: #bdc3c7 !important;
  display: block;
  margin-bottom: 10px;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #d81b60 !important;
}

.contact-info {
  color: #bdc3c7 !important;
  line-height: 1.8;
}

.copyright {
  text-align: center;
  padding-top: 30px;
  margin-top: 30px;
  border-top: 1px solid #34495e;
  color: #bdc3c7 !important;
  font-size: 14px;
}

/* 响应式设计 - 适配搜索栏和结果区域 */
@media (max-width: 768px) {
  .search-container {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-box {
    width: 100%;
    max-width: none;
  }
  
  .search-actions {
    width: 100%;
    justify-content: center;
  }

  .main-content {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .recommendation-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-actions {
    margin-left: 0;
    width: 100%;
    justify-content: center;
    margin-top: 10px;
  }
}

@media (max-width: 480px) {
  .search-actions {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .action-btn {
    padding: 6px 10px;
    font-size: 14px;
  }

  .nav-links {
    flex-wrap: wrap;
    gap: 15px;
  }

  .recommendation-grid {
    grid-template-columns: 1fr;
  }

  .filter-input {
    width: 80px;
  }
}
</style>

<!-- 全局引入Font Awesome图标（确保配色中的图标正常显示） -->
<style>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css');
</style>