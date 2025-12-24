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
// 引入 Vue 的组合式 API，用于创建计算属性和生命周期钩子
import { computed, onMounted } from 'vue' // 从 vue 导入 computed 和 onMounted
// 引入路由，用于在点击记录时跳转到二手详情页
import { useRouter } from 'vue-router'
// 引入页面用到的通用导航栏、页脚和悬浮菜单组件
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
// 引入 common 包中封装好的二手 Store，用来统一管理浏览记录数据
import { useSecondhandStore } from '@campus/common'

// 定义浏览记录在当前页面中的展示结构
interface HistoryItem {
  id: number // 浏览记录 ID（用于删除单条记录）
  goodsId: number // 对应的商品 ID（用于跳转到详情页）
  title: string // 商品标题
  desc: string // 商品描述（此处示例中暂留空字符串）
  price: number // 商品价格
  tag: string // 记录标签文案（例如“浏览记录”）
  icon: string // 左侧图标的类名（使用 Font Awesome）
  viewTime: string // 浏览时间的格式化字符串
}

// 创建路由实例，用于在点击记录时跳转到二手详情页
const router = useRouter() // 调用 useRouter 获取路由对象

// 获取二手 Store 实例，后续所有浏览记录相关的数据都从这里读取
const secondhandStore = useSecondhandStore() // 调用 useSecondhandStore 获取全局的二手 Store

// 使用计算属性，将 Store 中的浏览记录转换为页面展示所需的结构
const historyList = computed<HistoryItem[]>(() => {
  // 从 Store 中取出原始浏览记录数组（包含 goodsTitle 等字段）
  const records = secondhandStore.browseHistory // 直接访问 Store 中的浏览记录响应式数据
  // 将原始记录映射为页面使用的 HistoryItem 结构
  return records.map((item) => ({
    id: item.id, // 使用记录自身的 ID
    goodsId: item.goodsId, // 商品 ID，用于后续跳转
    title: item.goodsTitle, // 展示商品标题
    desc: '', // 此处暂不展示描述信息，保留字段方便后续扩展
    price: item.price || 0, // 价格字段，后端可能为空，这里做兜底
    tag: '浏览记录', // 固定文案，标识该条目来自浏览记录
    icon: 'fas fa-box', // 使用统一的盒子图标作为占位
    viewTime: formatDateTime(item.viewTime) // 将原始时间格式化成易读字符串
  })) // 返回新的数组供模板使用
})

// 将 Store 中的 loading 状态透传给页面，方便后续根据需要展示加载中效果
const loading = computed(() => secondhandStore.loading) // 直接使用 Store 自己的 loading 状态

// 跳转到对应商品的详情页
const goToDetail = (goodsId: number) => {
  router.push(`/secondhand/detail/${goodsId}`) // 使用路由跳转到二手详情页面
}

// 移除单条浏览记录
const removeItem = async (id: number) => {
  try {
    // 调用 Store 中封装好的删除单条记录方法，自动同步更新全局状态
    await secondhandStore.removeBrowseHistoryItem(id) // 根据记录 ID 调用删除接口并更新本地列表
  } catch (error) {
    console.error('删除浏览记录失败:', error) // 控制台输出详细错误信息
    alert('删除失败，请稍后重试') // 给用户一个简单的错误提示
  }
}

// 清空所有浏览记录
const clearHistory = async () => {
  // 如果当前本地列表已经为空，则不进行任何操作
  if (!historyList.value.length) {
    return // 直接返回，避免无意义请求
  }
  // 弹窗确认，避免用户误操作清空全部记录
  if (!confirm('确定要清空所有浏览记录吗？')) {
    return // 用户点击取消，则不继续执行
  }
  try {
    // 调用 Store 中封装好的清空浏览记录方法
    await secondhandStore.clearBrowseHistoryAll() // 调用后端接口并清空本地状态
  } catch (error) {
    console.error('清空浏览记录失败:', error) // 控制台输出错误日志
    alert('清空浏览记录失败，请稍后重试') // 给用户反馈提示
  }
}

// 将时间字符串格式化为 yyyy-MM-dd HH:mm 形式，便于用户阅读
const formatDateTime = (dateStr: string): string => {
  if (!dateStr) return '' // 后端未返回时间时，直接返回空字符串
  const date = new Date(dateStr) // 将原始字符串转换为 Date 对象
  if (Number.isNaN(date.getTime())) {
    return dateStr // 如果解析失败，则原样返回，避免显示为无意义的时间
  }
  const y = date.getFullYear() // 年份
  const m = String(date.getMonth() + 1).padStart(2, '0') // 月份补零
  const d = String(date.getDate()).padStart(2, '0') // 日期补零
  const h = String(date.getHours()).padStart(2, '0') // 小时补零
  const mm = String(date.getMinutes()).padStart(2, '0') // 分钟补零
  return `${y}-${m}-${d} ${h}:${mm}` // 拼接为最终展示格式
}

// 组件挂载时，从后端加载浏览记录列表到 Store 中
onMounted(() => {
  // 调用 Store 中的加载方法，约定第一页最多拉取 50 条记录
  // 这里不做错误捕获，具体错误在 Store 内部已经有统一处理并打印日志
  void secondhandStore.loadBrowseHistory(1, 50) // 使用 void 忽略 Promise 返回值，仅触发请求
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
