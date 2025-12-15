<template>
  <div class="favorites-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-heart"></i> 我的收藏
        </h1>
        <p class="page-subtitle">查看您收藏的兼职岗位</p>
      </section>

      <!-- 操作栏 -->
      <section class="actions-bar">
        <button class="btn-clear" @click="clearFavorites">
          <i class="fas fa-trash"></i> 清空收藏
        </button>
      </section>

      <!-- 收藏列表 -->
      <section class="favorites-section">
        <div v-if="favoritesList.length === 0" class="empty-state">
          <i class="fas fa-heart"></i>
          <h3>暂无收藏</h3>
          <p>您还没有收藏任何兼职岗位</p>
          <button class="btn-browse" @click="goToParttime">
            <i class="fas fa-search"></i> 去浏览兼职
          </button>
        </div>
        <div v-else class="favorites-grid">
          <div
            v-for="item in favoritesList"
            :key="item.id"
            class="favorite-card"
          >
            <div class="card-header" @click="goToDetail(item.jobId)">
              <h3 class="job-title">{{ item.jobTitle }}</h3>
              <button
                class="btn-favorite active"
                @click.stop="toggleFavorite(item.id)"
              >
                <i class="fas fa-heart"></i>
              </button>
            </div>
            <div class="card-content" @click="goToDetail(item.jobId)">
              <p class="company-name">{{ item.companyName }}</p>
              <div class="job-meta">
                <span class="salary">{{ item.salary }}</span>
                <span class="location">
                  <i class="fas fa-map-marker-alt"></i> {{ item.location }}
                </span>
              </div>
              <p class="job-desc">{{ item.description }}</p>
            </div>
            <div class="card-actions">
              <button class="btn-apply" @click="goToDetail(item.jobId)">
                <i class="fas fa-paper-plane"></i> 立即申请
              </button>
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

// 收藏列表
const favoritesList = ref([
  {
    id: 1,
    jobId: 1,
    jobTitle: '校园推广专员',
    companyName: '某教育公司',
    salary: '150-200元/天',
    location: '校内',
    description: '负责校园推广活动，协助品牌宣传'
  },
  {
    id: 2,
    jobId: 2,
    jobTitle: '数据录入员',
    companyName: '某科技公司',
    salary: '20-30元/小时',
    location: '线上',
    description: '负责数据录入和整理工作'
  },
  {
    id: 3,
    jobId: 3,
    jobTitle: '客服助理',
    companyName: '某服务公司',
    salary: '3000-4000元/月',
    location: '校外',
    description: '负责客户咨询和服务工作'
  }
])

// 跳转到岗位详情
const goToDetail = (jobId: number) => {
  router.push(`/parttime/detail/${jobId}`)
}

// 切换收藏状态
const toggleFavorite = async (id: number) => {
  const index = favoritesList.value.findIndex(item => item.id === id)
  if (index > -1) {
    favoritesList.value.splice(index, 1)
    // TODO: 调用取消收藏API
    // await toggleFavoriteAPI(id)
  }
}

// 清空收藏
const clearFavorites = () => {
  if (confirm('确定要清空所有收藏吗？')) {
    favoritesList.value = []
    // TODO: 调用清空收藏API
    // await clearFavoritesAPI()
  }
}

// 去浏览兼职
const goToParttime = () => {
  router.push('/parttime')
}

onMounted(() => {
  // 加载收藏列表
  // loadFavorites()
})
</script>

<style scoped>
:root {
  --primary: #d81b60;
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

.actions-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.btn-clear {
  padding: 10px 20px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  background: white;
  color: #ff4d4f;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-clear:hover {
  background: #fff1f0;
  border-color: #ff4d4f;
}

.favorites-section {
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
  margin-bottom: 20px;
}

.btn-browse {
  padding: 12px 24px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-browse:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.favorite-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  transition: all 0.3s;
}

.favorite-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  cursor: pointer;
}

.job-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  flex: 1;
  transition: color 0.3s;
}

.job-title:hover {
  color: var(--primary);
}

.btn-favorite {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: #f5f5f5;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-favorite.active {
  background: #fff1f0;
  color: var(--primary);
}

.btn-favorite:hover {
  transform: scale(1.1);
}

.card-content {
  margin-bottom: 15px;
  cursor: pointer;
}

.company-name {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.job-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
  font-size: 14px;
}

.salary {
  color: var(--primary);
  font-weight: 600;
}

.location {
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.job-desc {
  font-size: 13px;
  color: #999;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-actions {
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.btn-apply {
  width: 100%;
  padding: 10px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.btn-apply:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
}

@media (max-width: 768px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }
}
</style>
