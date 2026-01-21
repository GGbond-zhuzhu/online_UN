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


      <!-- 快速筛选栏（完全对齐HTML的filter-section） -->
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

      <!-- 排序和价格快捷筛选栏（新增，参考app端） -->
      <section class="sort-price-bar">
        <div class="sort-section">
          <span class="sort-label">排序：</span>
          <div 
            v-for="(item, index) in sortOptions" 
            :key="item.value"
            class="sort-item"
            :class="{ active: sortType === item.value }"
            @click="handleSort(item.value)"
          >
            <i :class="item.icon"></i>
            <span>{{ item.label }}</span>
          </div>
        </div>
        <div class="price-quick-section">
          <span class="price-label">价格：</span>
          <div class="price-quick-btns">
            <div 
              v-for="(item, index) in priceQuickOptions" 
              :key="index"
              class="price-quick-btn"
              :class="{ active: priceQuickIndex === index }"
              @click="handlePriceQuick(index, item.min, item.max)"
            >
              {{ item.label }}
            </div>
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
              <div class="product-card" v-for="(item, index) in filteredProducts" :key="item.id" @click="goToDetail(item.id)">
                <div class="product-image">
                  <img v-if="item.images && item.images.length > 0" :src="item.images[0]" :alt="item.title" />
                  <i v-else :class="item.icon"></i>
                  <div class="product-tag">{{ item.tag }}</div>
                  <!-- 新增：热门标签 -->
                  <div v-if="index < 3" class="hot-badge">
                    <i class="fas fa-fire"></i>
                    <span>热门</span>
                  </div>
                  <!-- 新增：新上架标签 -->
                  <div v-if="index % 5 === 0 && index > 0" class="new-badge">
                    <i class="fas fa-sparkles"></i>
                    <span>新上架</span>
                  </div>
                  <!-- 新增：收藏按钮 -->
                  <div class="wishlist-btn" @click.stop="toggleFavorite(item.id)">
                    <i :class="item.isFavorite ? 'fas fa-heart' : 'far fa-heart'" :style="{ color: item.isFavorite ? '#ff6b9d' : '#999' }"></i>
                  </div>
                </div>
                <div class="product-info">
                  <div class="product-title">{{ item.title }}</div>
                  <div class="product-desc">{{ item.desc }}</div>
                  <div class="product-price">¥{{ item.priceValue.toFixed(2) }}</div>
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

          <!-- 猜你喜欢盒子 - 一直存在（增加随机推荐和换一批功能） -->
          <div class="recommendation-section">
            <h2 class="section-title">
              <i class="fas fa-heart"></i>
              猜你喜欢
              <!-- 换一批按钮：点击后重新随机一组推荐商品 -->
              <button class="refresh-btn" type="button" @click="refreshRecommendations">换一批</button>
            </h2>
            <div class="recommendation-grid">
              <div class="product-card" v-for="item in recommendedProducts" :key="item.id" @click="goToDetail(item.id)">
                <div class="product-image">
                  <img v-if="item.images && item.images.length > 0" :src="item.images[0]" :alt="item.title" />
                  <i v-else :class="item.icon"></i>
                  <div class="product-tag">{{ item.tag }}</div>
                  <!-- 新增：收藏按钮 -->
                  <div class="wishlist-btn" @click.stop="toggleFavorite(item.id)">
                    <i :class="item.isFavorite ? 'fas fa-heart' : 'far fa-heart'" :style="{ color: item.isFavorite ? '#ff6b9d' : '#999' }"></i>
                  </div>
                </div>
                <div class="product-info">
                  <div class="product-title">{{ item.title }}</div>
                  <div class="product-desc">{{ item.desc }}</div>
                  <div class="product-price">¥{{ item.priceValue.toFixed(2) }}</div>
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

        <!-- 侧边栏（现代化设计，整合特色标签） -->
        <aside class="sidebar">
          <!-- 特色标签区域（移到侧边栏顶部） -->
          <div class="sidebar-widget featured-tags-widget">
            <h3 class="widget-title">
              <i class="fas fa-tags"></i>
              特色分类
            </h3>
            <div class="tags-grid">
              <div 
                v-for="(tag, index) in featuredTags" 
                :key="index"
                class="featured-tag-card"
                :style="{ background: tag.color }"
                @click="handleTagClick(tag.value)"
              >
                <div class="tag-icon">
                  <i :class="tag.icon"></i>
                </div>
                <span class="tag-label">{{ tag.label }}</span>
              </div>
            </div>
          </div>

          <!-- 热门分类 -->
          <div class="sidebar-widget">
            <h3 class="widget-title">
              <i class="fas fa-fire"></i>
              热门分类
            </h3>
            <ul class="category-list">
              <li 
                class="category-item" 
                v-for="item in hotCategories" 
                :key="item.key" 
                :class="{ active: filters.category === item.key }"
                @click="filterByCategory(item.key)"
              >
                <i class="fas fa-chevron-right"></i>
                {{ item.name }}
              </li>
            </ul>
          </div>

          <!-- 快捷操作 -->
          <div class="sidebar-widget">
            <h3 class="widget-title">
              <i class="fas fa-bolt"></i>
              快捷操作
            </h3>
            <div class="quick-action">
              <a href="#" @click.prevent="handleQuick('publish')" class="action-button primary">
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

          <!-- 交易提示 -->
          <div class="sidebar-widget tips-widget">
            <h3 class="widget-title">
              <i class="fas fa-shield-alt"></i>
              交易提示
            </h3>
            <div class="safety-tips">
              <div class="tip-item" v-for="(item, idx) in tips" :key="idx">
                <i class="fas fa-check-circle"></i>
                <span>{{ item }}</span>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <!-- 发布闲置：改为弹窗，不再跳转到独立发布页面 -->
    <AppModal :visible="showPublishModal" title="发布闲置" @close="closePublishModal">
      <SecondhandPublish :embedded="true" @close="closePublishModal" />
    </AppModal>

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
// 引入Vue的响应式和生命周期方法
import { reactive, ref, computed, onMounted, onBeforeUnmount, watch } from 'vue' // 增加 onBeforeUnmount 用于移除滚动监听
import { useRouter, useRoute } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
import AppModal from '@/components/common/AppModal.vue'
import SecondhandPublish from './publish/index.vue'
import { getGoodsList } from '@campus/common'
// 通过命名空间方式引入格式化工具，避免浏览器对命名导出做严格校验导致运行时 SyntaxError
import * as formatUtils from '@campus/common/utils/format' // 其中包含 getRandomRecommendList 等工具函数

const router = useRouter()
const route = useRoute()

// 发布弹窗（替代原“发布闲置页面”）
const showPublishModal = ref(false)
const openPublishModal = () => {
  showPublishModal.value = true
}
const closePublishModal = () => {
  showPublishModal.value = false
  if (route.query.publish) {
    const nextQuery: Record<string, any> = { ...route.query }
    delete nextQuery.publish
    delete nextQuery.id
    router.replace({ path: route.path, query: nextQuery })
  }
}

const loading = ref(false)
// 当前分页页码（用于“滑到底自动加载下一页”）
const currentPage = ref(1)
// 每页加载多少条商品（可根据需求调整，数值越大一次性加载更多）
const pageSize = ref(20)
// 是否还有更多数据可以继续加载（为 false 时表示已经到底）
const hasMore = ref(true)

// 筛选条件（对齐HTML的筛选参数）
const filters = reactive({
  category: 'all',
  condition: 'all',
  priceMin: '',
  priceMax: '',
  campus: 'all',
  search: ''
})

// 特色标签配置（参考app端）
const featuredTags = ref([
  { label: '教材特惠', value: 'books_sale', icon: 'fas fa-book', color: '#FFB6C1' },
  { label: '数码精选', value: 'digital_best', icon: 'fas fa-laptop', color: '#B0E0E6' },
  { label: '服饰清仓', value: 'clothing_clear', icon: 'fas fa-tshirt', color: '#FFF8DC' },
  { label: '生活好物', value: 'daily_goods', icon: 'fas fa-home', color: '#98FB98' },
  { label: '运动装备', value: 'sports_gear', icon: 'fas fa-running', color: '#DDA0DD' },
  { label: '急出专区', value: 'urgent_sale', icon: 'fas fa-bolt', color: '#FFE4B5' }
])

// 排序选项（新增）
const sortType = ref('default')
const sortOptions = ref([
  { label: '默认', value: 'default', icon: 'fas fa-list' },
  { label: '价格', value: 'price', icon: 'fas fa-dollar-sign' },
  { label: '最新', value: 'time', icon: 'fas fa-clock' },
  { label: '热度', value: 'hot', icon: 'fas fa-fire' }
])

// 价格快捷筛选选项（新增）
const priceQuickIndex = ref(-1)
const priceQuickOptions = ref([
  { label: '50以下', min: 0, max: 50 },
  { label: '50-100', min: 50, max: 100 },
  { label: '100-200', min: 100, max: 200 },
  { label: '200-500', min: 200, max: 500 },
  { label: '500以上', min: 500, max: 999999 }
])

// 所有商品数据（列表，用于后续筛选与随机推荐，初始为空，真正的数据全部来自后端接口）
const allProducts = ref<any[]>([])

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

// 搜索/筛选结果（根据筛选条件从全部商品中筛选出匹配的结果）
// 注意：排序现在由后端处理，这里只做前端筛选
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

// 每次“猜你喜欢”最多展示的商品数量（可根据需求调整）
const RECOMMEND_COUNT = 8 // 一次最多展示8件商品

// 用于驱动随机推荐重新计算的“种子”（简单计数器，只要发生变化就会触发重新计算）
const recommendSeed = ref(0) // 每次点击“换一批”或下拉到底部时自增

// 猜你喜欢商品（从所有商品中随机抽取一部分，用于动态推荐）
const recommendedProducts = computed(() => {
  recommendSeed.value // 读取种子，让计算属性依赖它，从而在种子变化时重新计算

  const all = allProducts.value // 读取所有商品列表
  if (!all || all.length === 0) {
    // 如果暂无数据，则返回空数组，避免页面渲染报错
    return []
  }

  // 当用户有搜索/筛选时，优先从“未出现在搜索结果中的商品”里做随机推荐，避免重复
  let pool = all // 初始候选池为所有商品
  if (hasSearchOrFilter.value) {
    // 取出搜索/筛选结果的商品ID集合
    const filteredIds = new Set(filteredProducts.value.map(item => item.id))
    // 从全部商品中过滤掉已经出现在搜索结果中的商品
    pool = all.filter(item => !filteredIds.has(item.id))
    // 如果过滤后一个都不剩（例如数据量太小），则退回到全部商品中随机
    if (pool.length === 0) {
      pool = all
    }
  }

  // 使用公共包中的工具函数，从候选池中随机抽取若干条作为“猜你喜欢”列表
  // 使用工具命名空间中的 getRandomRecommendList 生成推荐列表
  return formatUtils.getRandomRecommendList(pool, RECOMMEND_COUNT) // 统一封装随机算法，便于后续在多端复用与维护
})

// 手动刷新猜你喜欢（点击“换一批”按钮时调用）
const refreshRecommendations = () => {
  recommendSeed.value++ // 每次把种子自增1，触发计算属性重新随机
}

// 记录上一次触发滚动加载的时间，用于简单节流，避免触发过于频繁
let lastScrollRefreshTime = 0 // 记录上次触发时间戳（毫秒）

// 监听页面滚动事件：当用户向下滚动接近页面底部时，自动“加载下一页 + 换一批猜你喜欢”
const handleScroll = () => {
  const now = Date.now() // 获取当前时间戳
  // 如果两次触发间隔小于1000毫秒（1秒），则直接返回，防止短时间内频繁刷新
  if (now - lastScrollRefreshTime < 1000) {
    return
  }

  // 获取当前滚动条位置
  const scrollTop =
    window.pageYOffset ||
    document.documentElement.scrollTop ||
    document.body.scrollTop ||
    0

  // 获取可视区域高度
  const windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight

  // 获取整个文档的总高度
  const docHeight = document.documentElement.scrollHeight || document.body.scrollHeight

  // 如果当前已滚动到底部附近（预留150像素阈值），则自动加载下一页或刷新猜你喜欢
  if (docHeight - (scrollTop + windowHeight) < 150) {
    lastScrollRefreshTime = now // 记录本次触发时间

    if (hasMore.value) {
      // 如果还有更多数据，则继续向后端请求下一页商品并追加到列表中
      loadGoods(false)
    } else {
      // 如果已经没有更多数据，则只刷新一次猜你喜欢，避免无意义请求
      refreshRecommendations()
    }
  }
}

// 加载商品数据（支持分页与重置）
// reset = true 表示重新加载（从第一页开始并清空旧数据）；false 表示在原有列表后追加下一页
const loadGoods = async (reset = false) => {
  try {
    // 如果已经在加载中，则直接返回，避免重复请求
    if (loading.value) return

    // 如果需要重置（例如切换筛选条件），则重置分页状态和数据
    if (reset) {
      currentPage.value = 1 // 回到第一页
      hasMore.value = true // 重新标记为“还有更多”
      allProducts.value = [] // 清空当前商品列表
    }

    // 如果已经确定没有更多数据，并且不是重置请求，则不再重复请求
    if (!hasMore.value && !reset) {
      return
    }

    loading.value = true // 标记加载中

    const params: any = {
      page: currentPage.value, // 当前页码
      pageSize: pageSize.value // 每页数量
    }
    
    // 添加筛选参数
    if (filters.search.trim()) {
      params.keyword = filters.search.trim()
    }
    if (filters.category !== 'all') {
      // 映射前端分类到后端分类
      const categoryMap: Record<string, string> = {
        'books': 'BOOKS',
        'digital': 'ELECTRONICS',
        'clothing': 'CLOTHING',
        'daily': 'DAILY',
        'sports': 'SPORTS',
        'others': 'OTHER'
      }
      params.category = categoryMap[filters.category] || filters.category.toUpperCase()
    }
    if (filters.priceMin) {
      params.minPrice = Number(filters.priceMin)
    }
    if (filters.priceMax) {
      params.maxPrice = Number(filters.priceMax)
    }
    
    // 添加排序参数（新增）
    if (sortType.value === 'price') {
      params.sortBy = 'price'
      params.sortOrder = 'ASC' // 价格从低到高
    } else if (sortType.value === 'time') {
      params.sortBy = 'publishTime'
      params.sortOrder = 'DESC' // 最新发布
    } else if (sortType.value === 'hot') {
      params.sortBy = 'hot'
      params.sortOrder = 'DESC' // 热度从高到低
    } else {
      params.sortBy = 'publishTime'
      params.sortOrder = 'DESC' // 默认按发布时间倒序
    }
    
    const result = await getGoodsList(params) // 调用公共包中的接口获取分页结果
    
    // 兼容 records 和 list 两种字段，防止为 undefined
    const records = result.records || result.list || []

    // 如果本次没有返回任何记录：
    if (!records.length) {
      if (currentPage.value === 1) {
        // 第一页就没有数据，说明当前筛选条件下没有商品，标记为没有更多
        hasMore.value = false
      } else {
        // 后续页没有数据，说明已经加载完所有商品
        hasMore.value = false
        // 这里只做静默处理或简单提示，避免打扰用户
        console.info('二手商品已全部加载完毕')
      }
      return
    }

    // 转换数据格式
    const mapped = records.map((item: any) => {
      // 映射后端分类到前端分类
      const categoryMap: Record<string, string> = {
        'BOOKS': 'books',
        'ELECTRONICS': 'digital',
        'CLOTHING': 'clothing',
        'DAILY': 'daily',
        'SPORTS': 'sports',
        'OTHER': 'others'
      }
      
      return {
        id: item.id,
        title: item.title,
        desc: item.description || '',
        priceValue: item.price,
        tag: item.status === 'ON_SALE' ? '在售' : '已售',
        icon: getCategoryIcon(categoryMap[item.category] || 'others'),
        seller: item.publisherName || '未知',
        rating: 4.5, // 默认评分
        category: categoryMap[item.category] || 'others',
        condition: '90', // 默认成色
        campus: 'main', // 默认校区
        // 后端字段是 imageUrls，这里做兼容
        images: item.imageUrls || item.images || [],
        isFavorite: false // 收藏状态（新增字段）
      }
    })

    // 如果当前是第一页，则用新数据覆盖；否则在原有列表后面追加，实现“加载更多”效果
    if (currentPage.value === 1) {
      allProducts.value = mapped
    } else {
      allProducts.value = [...allProducts.value, ...mapped]
    }

    // 如果本次返回数量小于 pageSize，说明已经没有更多数据
    if (records.length < pageSize.value) {
      hasMore.value = false
    } else {
      // 当前页加载成功且记录数充足，页码自增，准备下一次“到底自动加载”
      currentPage.value += 1
    }

    // 每次拉取完新数据后，顺便刷新一次猜你喜欢，保持推荐多样性
    refreshRecommendations()
  } catch (error: any) {
    console.error('加载商品列表失败:', error)
    // 如果接口报错（例如后端未启动或返回500），为了不让页面“空白一片”，这里注入一组本地演示数据作为兜底
    if (!allProducts.value.length) {
      // 只在当前没有任何数据时注入mock，避免覆盖后端已经成功加载过的真实数据
      allProducts.value = [
        {
          id: -1, // 负数ID表示演示数据，避免与真实数据冲突
          title: '【演示】九成新高等数学教材', // 商品标题（演示用）
          desc: '大一上学期使用，一直包书皮，无划线和笔记，适合复习和转让给学弟学妹。', // 商品描述
          priceValue: 25, // 价格数值
          tag: '在售', // 状态标签
          icon: getCategoryIcon('books'), // 分类图标（使用教材图标）
          seller: '演示卖家', // 卖家昵称
          rating: 4.8, // 默认评分
          category: 'books', // 分类编码（前端内部使用）
          condition: '90', // 成色（演示为9成新）
          campus: 'main', // 校区（主校区）
          images: [] // 图片列表（演示数据暂不提供真实图片）
        },
        {
          id: -2,
          title: '【演示】台式学习台灯',
          desc: '护眼台灯，亮度可调，宿舍使用不到一年，无明显划痕。',
          priceValue: 39,
          tag: '在售',
          icon: getCategoryIcon('daily'),
          seller: '演示卖家',
          rating: 4.6,
          category: 'daily',
          condition: '80',
          campus: 'main',
          images: []
        }
      ]
      // 使用本地演示数据刷新一次“猜你喜欢”，避免推荐区也为空
      refreshRecommendations()
    }
  } finally {
    loading.value = false
  }
}

// 获取分类图标
const getCategoryIcon = (category: string): string => {
  const iconMap: Record<string, string> = {
    'books': 'fas fa-book',
    'digital': 'fas fa-laptop',
    'clothing': 'fas fa-tshirt',
    'daily': 'fas fa-home',
    'sports': 'fas fa-bicycle',
    'others': 'fas fa-box'
  }
  return iconMap[category] || 'fas fa-box'
}

// 处理筛选（重新加载数据，从第一页开始）
const handleFilter = () => {
  loadGoods(true)
}

// 重置筛选条件
const handleReset = () => {
  filters.category = 'all'
  filters.condition = 'all'
  filters.priceMin = ''
  filters.priceMax = ''
  filters.campus = 'all'
  filters.search = ''
  sortType.value = 'default'
  priceQuickIndex.value = -1
  loadGoods(true)
}

// 按分类筛选
const filterByCategory = (categoryKey: string) => {
  filters.category = categoryKey
  handleFilter()
}

// 特色标签点击处理（新增）
const handleTagClick = (tagValue: string) => {
  const tagMap: Record<string, string> = {
    'books_sale': 'books',
    'digital_best': 'digital',
    'clothing_clear': 'clothing',
    'daily_goods': 'daily',
    'sports_gear': 'sports',
    'urgent_sale': 'all'
  }
  const category = tagMap[tagValue] || 'all'
  filters.category = category
  handleFilter()
}

// 处理排序（新增）
const handleSort = (value: string) => {
  sortType.value = value
  // 排序由后端处理，需要重新加载数据
  loadGoods(true)
}

// 处理价格快捷筛选（新增）
const handlePriceQuick = (index: number, min: number, max: number) => {
  if (priceQuickIndex.value === index) {
    // 如果点击的是已选中的，则取消筛选
    priceQuickIndex.value = -1
    filters.priceMin = ''
    filters.priceMax = ''
  } else {
    // 设置新的价格区间
    priceQuickIndex.value = index
    filters.priceMin = min.toString()
    filters.priceMax = max === 999999 ? '' : max.toString()
  }
  // 触发筛选
  handleFilter()
}

// 跳转到商品详情页
const goToDetail = (id: number) => {
  router.push(`/secondhand/detail/${id}`)
}

// 页面加载时获取数据，并添加滚动监听实现“滑到底自动加载下一页 + 换一批猜你喜欢”
onMounted(() => {
  loadGoods(true) // 初次加载商品列表（从第一页开始）
  // 注册滚动事件监听器，当用户向下滚动接近底部时自动“加载下一页 + 换一批猜你喜欢”
  window.addEventListener('scroll', handleScroll)
})

// 组件卸载前移除滚动监听，避免内存泄漏
onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 快捷操作处理
const handleQuick = (type: string) => {
  switch (type) {
    case 'publish':
      openPublishModal()
      break
    case 'message':
      router.push('/messages')
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

// 兼容旧路由：/secondhand/publish -> /secondhand?publish=1（由路由表 redirect 触发）
watch(
  () => route.query.publish,
  (val) => {
    const v = Array.isArray(val) ? val[0] : val
    if (v === '1') {
      openPublishModal()
    }
  },
  { immediate: true }
)

// 切换收藏状态（新增功能）
const toggleFavorite = async (id: number) => {
  const item = allProducts.value.find(p => p.id === id)
  if (item) {
    try {
      // 这里应该调用API保存收藏状态，暂时先本地更新
      item.isFavorite = !item.isFavorite
      // TODO: 调用收藏API
      // if (item.isFavorite) {
      //   await collectGoods(id)
      // } else {
      //   await uncollectGoods(id)
      // }
    } catch (error) {
      console.error('收藏操作失败:', error)
      item.isFavorite = !item.isFavorite // 回滚状态
    }
  }
}
</script>

<!-- 完全复用HTML的样式，解决scoped变量问题 -->
<style scoped>
/* 基础布局 - 使用柔和的马卡龙渐变背景 */
.secondhand-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #FFF0F5 0%, #F0F8FF 100%); /* 柔和的粉色到淡蓝色渐变，参考闲鱼等平台 */
  color: #333333; /* 深色文字，确保清晰可读 */
  line-height: 1.6;
  /* 在页面根容器上设置默认字体，避免使用 * 选择器覆盖 Font Awesome 图标的字体 */
  font-family: 'Arial', 'Microsoft YaHei', 'PingFang SC', sans-serif; /* 添加中文字体 */
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
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

/* 搜索栏样式 - 使用柔和的玫红色主色 */
.search-bar-section {
  background: white;
  border-radius: 12px;
  padding: 15px 20px;
  margin: 20px 0;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.12); /* 使用柔和的玫红色阴影 */
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
  border: 2px solid #FF6B9D !important; /* 使用柔和的玫红色边框 */
  border-radius: 25px;
  font-size: 16px;
  outline: none;
  box-shadow: 0 2px 5px rgba(255, 107, 157, 0.15); /* 使用柔和的玫红色阴影 */
  color: #333333; /* 深色文字，确保清晰可读 */
  font-weight: 400; /* 常规字重 */
}

.search-input::placeholder {
  color: #666666; /* 中等灰色，确保对比度 */
  opacity: 0.8;
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #FF6B9D !important; /* 使用柔和的玫红色 */
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
  background: rgba(255, 107, 157, 0.1); /* 使用柔和的玫红色背景 */
  color: #FF6B9D !important; /* 使用柔和的玫红色 */
}

.action-btn i {
  margin-right: 5px;
}

/* 特色标签区域（移到侧边栏中） */
.featured-tags-widget {
  margin-bottom: 24px;
}

.tags-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.featured-tag-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 16px 12px;
  border-radius: 12px;
  color: white;
  font-size: 13px;
  gap: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  cursor: pointer;
  text-align: center;
  min-height: 90px;
}

.featured-tag-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.tag-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.tag-label {
  font-weight: 600;
  font-size: 12px;
}

/* 排序和价格快捷筛选栏（新增） */
.sort-price-bar {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin: 20px 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.sort-section {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.sort-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.sort-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: #F5F5F5;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  transition: all 0.3s;
  cursor: pointer;
}

.sort-item:hover {
  background: #e0e0e0;
}

.sort-item.active {
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%); /* 使用柔和的玫红色渐变 */
  color: #FFFFFF; /* 白色文字，确保对比度 */
  font-weight: 600; /* 加粗字体，提升可读性 */
}

.sort-item i {
  font-size: 12px;
}

.price-quick-section {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.price-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.price-quick-btns {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  flex: 1;
}

.price-quick-btn {
  padding: 8px 16px;
  background: #F5F5F5;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  transition: all 0.3s;
  white-space: nowrap;
  cursor: pointer;
}

.price-quick-btn:hover {
  background: #e0e0e0;
}

.price-quick-btn.active {
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%); /* 使用柔和的玫红色渐变 */
  color: #FFFFFF; /* 白色文字，确保对比度 */
  font-weight: 600; /* 加粗字体，提升可读性 */
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
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%) !important; /* 使用柔和的玫红色渐变 */
  color: #FFFFFF !important; /* 白色文字，确保对比度 */
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600; /* 加粗字体，提升可读性 */
  cursor: pointer;
  transition: all 0.3s;
}

.confirm-btn:hover {
  background: linear-gradient(135deg, #E91E63 0%, #FF6B9D 100%) !important; /* 悬停时稍深一点 */
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(255, 107, 157, 0.3); /* 使用柔和的玫红色阴影 */
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
  justify-content: flex-start;
}

.section-title i {
  color: #FF6B9D !important; /* 使用柔和的玫红色 */
  margin-right: 8px;
}

/* 换一批按钮样式：放在标题右侧，视觉轻量但可点击 */
.refresh-btn {
  margin-left: auto; /* 将按钮推到最右侧 */
  padding: 4px 10px;
  font-size: 12px;
  border-radius: 12px;
  border: 1px solid #FF6B9D; /* 使用柔和的玫红色边框 */
  background: #fff;
  color: #FF6B9D; /* 使用柔和的玫红色 */
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500; /* 中等字重 */
}

.refresh-btn:hover {
  background: #FF6B9D; /* 使用柔和的玫红色背景 */
  color: #fff;
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
  box-shadow: 0 5px 15px rgba(255, 107, 157, 0.2); /* 使用柔和的玫红色阴影 */
}

.product-image {
  height: 150px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-image i {
  font-size: 50px;
  color: #FF6B9D !important; /* 使用柔和的玫红色 */
}

/* 新增：热门标签样式 */
.hot-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  background: #FFE4B5;
  color: #FF8C00;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  z-index: 2;
  box-shadow: 0 2px 6px rgba(255, 140, 0, 0.3);
}

.hot-badge i {
  font-size: 10px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

/* 新增：新上架标签样式 */
.new-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  background: #DDA0DD;
  color: #8B008B;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  z-index: 2;
  box-shadow: 0 2px 6px rgba(221, 160, 221, 0.4);
}

.new-badge i {
  font-size: 10px;
  animation: sparkle 1.5s infinite;
}

@keyframes sparkle {
  0%, 100% { transform: scale(1) rotate(0deg); }
  50% { transform: scale(1.2) rotate(180deg); }
}

/* 新增：收藏按钮样式 */
.wishlist-btn {
  position: absolute;
  bottom: 8px;
  right: 8px;
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FF6B9D; /* 使用柔和的玫红色 */
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.25); /* 使用柔和的玫红色阴影 */
  transition: all 0.3s;
  z-index: 3;
  cursor: pointer;
}

.wishlist-btn:hover {
  background: #FF6B9D; /* 使用柔和的玫红色背景 */
  color: white;
  transform: scale(1.1);
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
  color: #E91E63 !important; /* 使用深一点的玫红色，确保价格清晰可见 */
  letter-spacing: 0.3px; /* 增加字间距，提升可读性 */
}

.product-tag {
  position: absolute;
  top: 5px;
  right: 5px;
  background: #FF6B9D !important; /* 使用柔和的玫红色 */
  color: white !important;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600; /* 加粗字体，提升可读性 */
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
  color: #FF6B9D !important; /* 使用柔和的玫红色 */
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

/* 侧边栏 - 现代化设计 */
.sidebar {
  width: 280px;
  flex-shrink: 0;
  position: sticky;
  top: 20px;
  align-self: flex-start;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
}

.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-thumb {
  background: #FF6B9D; /* 使用柔和的玫红色 */
  border-radius: 3px;
}

.sidebar-widget {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s;
}

.sidebar-widget:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.widget-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f1f5f9;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
}

.widget-title i {
  color: #FF6B9D; /* 使用柔和的玫红色 */
  font-size: 18px;
}

/* 分类列表 */
.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 4px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: #64748b;
  font-size: 14px;
  position: relative;
}

.category-item i {
  font-size: 10px;
  margin-right: 8px;
  color: #94a3b8;
  transition: all 0.2s;
}

.category-item:hover {
  background: rgba(255, 107, 157, 0.1); /* 使用柔和的玫红色背景 */
  color: #FF6B9D; /* 使用柔和的玫红色 */
  transform: translateX(4px);
}

.category-item:hover i {
  color: #FF6B9D; /* 使用柔和的玫红色 */
}

.category-item.active {
  background: linear-gradient(135deg, #FFE5F1 0%, #FFB3D1 100%); /* 使用柔和的玫红色渐变 */
  color: #E91E63; /* 使用深一点的玫红色，确保对比度 */
  font-weight: 600;
}

.category-item.active i {
  color: #E91E63; /* 使用深一点的玫红色 */
}

.category-item:last-child {
  margin-bottom: 0;
}

/* 快捷操作 */
.quick-action {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-button {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  color: #475569;
  font-size: 14px;
  font-weight: 500;
  border: 2px solid transparent;
}

.action-button.primary {
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%); /* 使用柔和的玫红色渐变 */
  color: white;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.25); /* 使用柔和的玫红色阴影 */
  font-weight: 600; /* 加粗字体，提升可读性 */
}

.action-button.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.35); /* 使用柔和的玫红色阴影 */
}

.action-button:not(.primary):hover {
  background: rgba(255, 107, 157, 0.1); /* 使用柔和的玫红色背景 */
  border-color: #FFB3D1; /* 使用柔和的玫红色边框 */
  color: #FF6B9D; /* 使用柔和的玫红色 */
  transform: translateX(4px);
}

.action-button i {
  margin-right: 10px;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

/* 交易提示 */
.tips-widget {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-left: 4px solid #f59e0b;
}

.tips-widget .widget-title {
  color: #92400e;
  border-bottom-color: rgba(245, 158, 11, 0.2);
}

.tips-widget .widget-title i {
  color: #f59e0b;
}

.safety-tips {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 8px 0;
  color: #78350f;
  font-size: 13px;
  line-height: 1.5;
}

.tip-item i {
  color: #f59e0b;
  margin-top: 2px;
  flex-shrink: 0;
  font-size: 14px;
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
  color: #FF6B9D !important; /* 使用柔和的玫红色 */
  margin-bottom: 15px;
}

.footer-links a {
  color: #bdc3c7 !important;
  display: block;
  margin-bottom: 10px;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #FF6B9D !important; /* 使用柔和的玫红色 */
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
    position: static;
    max-height: none;
  }

  .tags-grid {
    grid-template-columns: repeat(3, 1fr);
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
</style>