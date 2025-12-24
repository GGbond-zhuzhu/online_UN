<template>
  <div class="add-friend-page">
    <!-- 全局导航栏 -->
    <NavBar />

    <div class="main-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">添加朋友</h1>
        <p class="page-desc">通过多种方式添加校园好友</p>
      </div>

      <!-- 搜索框：通过用户名/学号搜索 -->
      <div class="search-section">
        <div class="search-box">
          <i class="fas fa-search search-icon"></i>
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="搜索用户名、学号或手机号"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
      </div>

      <!-- 功能入口卡片 -->
      <div class="function-cards">
        <!-- 扫一扫添加 -->
        <div class="function-card" @click="handleScanQR">
          <div class="card-icon scan-icon">
            <i class="fas fa-qrcode"></i>
          </div>
          <h3 class="card-title">扫一扫</h3>
          <p class="card-desc">扫描二维码添加好友</p>
        </div>

        <!-- 手机联系人 -->
        <div class="function-card" @click="handlePhoneContacts">
          <div class="card-icon phone-icon">
            <i class="fas fa-address-book"></i>
          </div>
          <h3 class="card-title">手机联系人</h3>
          <p class="card-desc">从手机通讯录添加</p>
        </div>

        <!-- 同校推荐 -->
        <div class="function-card" @click="handleSchoolRecommend">
          <div class="card-icon school-icon">
            <i class="fas fa-university"></i>
          </div>
          <h3 class="card-title">同校推荐</h3>
          <p class="card-desc">查看同校用户推荐</p>
        </div>

        <!-- 我的二维码 -->
        <div class="function-card" @click="handleMyQRCode">
          <div class="card-icon qrcode-icon">
            <i class="fas fa-qr-code"></i>
          </div>
          <h3 class="card-title">我的二维码</h3>
          <p class="card-desc">分享我的二维码名片</p>
        </div>
      </div>

      <!-- 搜索结果列表 -->
      <div v-if="searchResults.length > 0" class="search-results">
        <h2 class="results-title">搜索结果</h2>
        <div class="results-list">
          <div
            v-for="user in searchResults"
            :key="user.id"
            class="result-item"
            @click="handleViewProfile(user)"
          >
            <div class="user-avatar">
              <img v-if="user.avatar" :src="user.avatar" :alt="user.username" />
              <i v-else class="fas fa-user-circle"></i>
            </div>
            <div class="user-info">
              <h3 class="user-name">{{ user.username }}</h3>
              <p class="user-meta">
                <span v-if="user.studentId">学号：{{ user.studentId }}</span>
                <span v-if="user.schoolName" class="school-name">{{ user.schoolName }}</span>
              </p>
            </div>
            <button class="add-btn" @click.stop="handleAddFriend(user)">
              <i class="fas fa-user-plus"></i>
              添加
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态提示 -->
      <div v-if="hasSearched && searchResults.length === 0" class="empty-state">
        <i class="fas fa-search empty-icon"></i>
        <p class="empty-text">未找到相关用户</p>
        <p class="empty-desc">请尝试其他搜索关键词</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import { useUserStore, request } from '@campus/common'

const router = useRouter()
const userStore = useUserStore()

// 搜索关键词
const searchKeyword = ref('')
// 搜索结果列表
const searchResults = ref<any[]>([])
// 是否已搜索
const hasSearched = ref(false)

// 搜索用户
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    return
  }

  try {
    // 调用后端API搜索用户
    const res: any = await request.get('/api/user/search', {
      params: {
        keyword: searchKeyword.value
      }
    })
    searchResults.value = res.data || []
    hasSearched.value = true
  } catch (error) {
    console.error('搜索用户失败:', error)
    searchResults.value = []
    hasSearched.value = true
    // 可以显示错误提示
  }
}

// 扫一扫添加
const handleScanQR = () => {
  // 跳转到扫码页面或打开扫码功能
  router.push('/ecard?action=scan')
}

// 手机联系人
const handlePhoneContacts = () => {
  // 打开手机联系人选择
  // 注意：这需要浏览器权限，实际实现可能需要调用相关API
  alert('手机联系人功能需要浏览器权限支持')
}

// 同校推荐
const handleSchoolRecommend = () => {
  // 跳转到同校推荐页面
  router.push('/add-friend/recommend')
}

// 我的二维码
const handleMyQRCode = () => {
  // 显示我的二维码弹窗
  router.push('/add-friend/my-qrcode')
}

// 查看用户资料
const handleViewProfile = (user: any) => {
  // 跳转到用户资料页面
  router.push(`/profile/${user.id}`)
}

// 添加好友
const handleAddFriend = async (user: any) => {
  try {
    // 调用后端API添加好友
    await request.post('/api/user/friend/add', {
      friendId: user.id
    })
    alert('好友请求已发送')
  } catch (error: any) {
    console.error('添加好友失败:', error)
    alert(error.message || '添加好友失败，请稍后重试')
  }
}
</script>

<style scoped>
.add-friend-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 页面标题区域 */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  color: #d81b60;
  margin-bottom: 10px;
}

.page-desc {
  font-size: 16px;
  color: #666;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 40px;
}

.search-box {
  display: flex;
  align-items: center;
  background: white;
  border-radius: 50px;
  padding: 12px 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  max-width: 600px;
  margin: 0 auto;
}

.search-icon {
  color: #999;
  margin-right: 12px;
  font-size: 18px;
}

.search-box input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  color: #333;
}

.search-btn {
  background: #d81b60;
  color: white;
  border: none;
  border-radius: 25px;
  padding: 8px 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.search-btn:hover {
  background: #c2185b;
  transform: translateY(-1px);
}

/* 功能卡片区域 */
.function-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.function-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;
}

.function-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(216, 27, 96, 0.15);
}

.card-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 36px;
  color: white;
}

.scan-icon {
  background: linear-gradient(135deg, #42a5f5 0%, #1e88e5 100%);
}

.phone-icon {
  background: linear-gradient(135deg, #66bb6a 0%, #43a047 100%);
}

.school-icon {
  background: linear-gradient(135deg, #ab47bc 0%, #8e24aa 100%);
}

.qrcode-icon {
  background: linear-gradient(135deg, #ffa726 0%, #fb8c00 100%);
}

.card-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.card-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

/* 搜索结果区域 */
.search-results {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.results-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.result-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  transition: all 0.3s;
  cursor: pointer;
}

.result-item:hover {
  background: #f8f9fa;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 15px;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-avatar i {
  font-size: 40px;
  color: #999;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.user-meta {
  font-size: 14px;
  color: #666;
}

.school-name {
  margin-left: 10px;
  color: #d81b60;
}

.add-btn {
  background: #d81b60;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 20px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.3s;
}

.add-btn:hover {
  background: #c2185b;
  transform: translateY(-1px);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  font-size: 64px;
  color: #ccc;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 20px;
  color: #666;
  margin-bottom: 10px;
}

.empty-desc {
  font-size: 14px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .function-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .page-title {
    font-size: 28px;
  }

  .search-box {
    flex-direction: column;
    gap: 10px;
  }

  .search-btn {
    width: 100%;
  }
}
</style>

