<template>
  <nav class="navbar">
    <div class="header-container">
      <!-- 第一行：品牌标志和用户功能 -->
      <div class="header-top">
        <div class="logo">
          <router-link to="/" class="logo-link">
            <span class="logo-main">上大学</span>
            <span class="logo-sub">Online</span>
        </router-link>
          <!-- 当前登录高校显示 -->
          <span class="current-school" v-if="userStore.campusName && showSchool">
            {{ userStore.campusName }}
          </span>
      </div>

        <!-- 时间和定位信息显示区域 -->
        <div class="header-info">
          <div class="time-display">
            <i class="fas fa-clock"></i>
            <span>{{ currentTime }}</span>
          </div>
          <div class="location-display">
            <i class="fas fa-map-marker-alt"></i>
            <span>{{ locationText }}</span>
            <div class="status-indicator" v-if="locationStatus === 'success'"></div>
      </div>
      </div>

        <div class="user-actions">
          <div class="search-bar">
            <i class="fas fa-search"></i>
            <input
              type="text"
              placeholder="搜索校园服务..."
              v-model="searchKeyword"
              @keyup.enter="handleSearch"
            />
          </div>
          <div class="user-login" @mouseenter="showDropdown = true" @mouseleave="showDropdown = false">
            <div class="user-icon">
              <i class="fas fa-user"></i>
            </div>
            <span class="user-name-display">{{ displayName }}</span>
            <!-- 用户角色标签 -->
            <span class="user-role-tag" v-if="userStore.isLoggedIn">{{ roleLabel }}</span>
            <div class="user-dropdown" v-show="showDropdown">
        <template v-if="userStore.isLoggedIn">
                <router-link to="/profile" @click="closeDropdown">
                  <i class="fas fa-user-circle"></i> 个人中心
                </router-link>
                <router-link to="/profile" @click="closeDropdown">
                  <i class="fas fa-cog"></i> 设置
                </router-link>
                <a href="#" @click.prevent="goToHome">
                  <i class="fas fa-home"></i> 回到首页
                </a>
                <div class="dropdown-divider"></div>
                <router-link to="/schedule" v-if="userStore.isStudent" @click="closeDropdown">
                  <i class="fas fa-book"></i> 我的课程
                </router-link>
                <router-link to="/ecard" v-if="userStore.isStudent || userStore.isTeacher" @click="closeDropdown">
                  <i class="fas fa-id-card-alt"></i> 校园E卡通
                </router-link>
                <a href="#" @click.prevent="handleLogout">
                  <i class="fas fa-sign-out-alt"></i> 退出登录
                </a>
        </template>
        <template v-else>
                <router-link to="/login" @click="closeDropdown">
                  <i class="fas fa-sign-in-alt"></i> 登录
                </router-link>
                <router-link to="/register" @click="closeDropdown">
                  <i class="fas fa-user-plus"></i> 注册
                </router-link>
                <a href="#" @click.prevent="goToHome">
                  <i class="fas fa-home"></i> 回到首页
                </a>
        </template>
            </div>
          </div>
        </div>
      </div>

      <!-- 第二行：主导航菜单 -->
      <div class="header-bottom">
        <nav class="main-nav">
          <router-link to="/" class="nav-link" data-role="all">首页</router-link>
          <router-link
            to="/ecard"
            class="nav-link"
            :class="{ hidden: !canAccess('ecard') }"
            data-role="student,teacher,university"
          >
            校园E卡通
          </router-link>
          <router-link
            to="/secondhand"
            class="nav-link"
            :class="{ hidden: !canAccess('secondhand') }"
            data-role="student,teacher,merchant"
          >
            二手交易
          </router-link>
          <router-link
            to="/parttime"
            class="nav-link"
            :class="{ hidden: !canAccess('parttime') }"
            data-role="student,merchant,admin"
          >
            兼职服务
          </router-link>
          <router-link
            to="/schedule"
            class="nav-link"
            :class="{ hidden: !canAccess('schedule') }"
            data-role="student,teacher"
          >
            行程管理
          </router-link>
          <router-link to="/security" class="nav-link" data-role="all">安全保障</router-link>
          <router-link to="/help" class="nav-link" data-role="all">帮助中心</router-link>
          <router-link
            to="/admin"
            class="nav-link"
            :class="{ hidden: !canAccess('admin') }"
            data-role="admin,university"
          >
            管理中心
          </router-link>
        </nav>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@campus/common'

const router = useRouter()
const userStore = useUserStore()

// 搜索关键词
const searchKeyword = ref('')
// 下拉菜单显示状态
const showDropdown = ref(false)
// 当前时间
const currentTime = ref('--:--:--')
// 定位状态
const locationStatus = ref<'loading' | 'success' | 'error'>('loading')
// 定位文本
const locationText = ref('定位中...')
// 时间更新定时器
let timeInterval: number | null = null

// 显示名称
const displayName = computed(() => {
  if (!userStore.isLoggedIn) {
    return '登录/注册'
  }
  return userStore.username || '用户'
})

// 角色标签
const roleLabel = computed(() => {
  if (userStore.isStudent) return '学生'
  if (userStore.isTeacher) return '教师'
  if (userStore.isAdmin) return '系统管理员'
  if (userStore.isUniversity) return '高校管理员'
  if (userStore.isVisitor) return '游客'
  return '用户'
})

// 是否显示学校名称
const showSchool = computed(() => {
  return userStore.isStudent || userStore.isTeacher || userStore.isUniversity
})

// 检查是否可以访问某个功能
const canAccess = (feature: string): boolean => {
  const role = userStore.currentRole
  const featureRoles: Record<string, string[]> = {
    ecard: ['student', 'teacher', 'university', 'visitor'],
    secondhand: ['student', 'teacher', 'merchant', 'visitor'],
    parttime: ['student', 'merchant', 'admin', 'visitor'],
    schedule: ['student', 'teacher', 'visitor'],
    admin: ['admin', 'university']
  }
  const allowedRoles = featureRoles[feature] || []
  return allowedRoles.includes(role) || allowedRoles.includes('all')
}

// 更新时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN')
}

// 获取定位信息
const getLocation = async () => {
  try {
    // 这里可以调用定位API
    // 暂时模拟
    setTimeout(() => {
      locationStatus.value = 'success'
      locationText.value = userStore.campusName || '中央民族大学'
    }, 1000)
  } catch (error) {
    locationStatus.value = 'error'
    locationText.value = '定位失败'
  }
}

// 搜索处理
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    // TODO: 实现搜索功能
    console.log('搜索:', searchKeyword.value)
  }
}

// 关闭下拉菜单
const closeDropdown = () => {
  showDropdown.value = false
}

// 回到首页
const goToHome = () => {
  router.push('/')
  showDropdown.value = false
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 退出登录
const handleLogout = async () => {
  try {
    await userStore.logout()
    router.push('/')
    showDropdown.value = false
  } catch (error) {
    console.error('退出登录失败:', error)
  }
}

// 组件挂载
onMounted(() => {
  updateTime()
  timeInterval = window.setInterval(updateTime, 1000)
  getLocation()
})

// 组件卸载
onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
.navbar {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  width: 100%;
}

/* 第一行：品牌标志和用户功能 */
.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 40px;
  border-bottom: 1px solid #f0f0f0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.logo-main {
  font-size: 28px;
  font-weight: bold;
  color: #d81b60;
}

.logo-sub {
  font-size: 16px;
  color: #d81b60;
  align-self: flex-end;
  margin-bottom: 3px;
}

.current-school {
  font-size: 14px;
  color: #666;
  margin-left: 20px;
  padding: 5px 10px;
  background-color: #f8f9fa;
  border-radius: 15px;
}

/* 时间和定位信息显示区域 */
.header-info {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 13px;
  color: #666;
}

.time-display {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  background: #f8f9fa;
  border-radius: 15px;
}

.location-display {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 5px 12px;
  background: #f0f7ff;
  border-radius: 15px;
  border-left: 3px solid #4a90e2;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4caf50;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.search-bar {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 20px;
  padding: 8px 15px;
}

.search-bar input {
  border: none;
  background: transparent;
  padding: 5px;
  outline: none;
  width: 180px;
}

.search-bar i {
  color: #666;
  margin-right: 5px;
}

.user-login {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #444;
  cursor: pointer;
  padding: 8px 15px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-login:hover {
  background: #f5f5f5;
}

.user-icon {
  width: 24px;
  height: 24px;
  background: #444;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
}

.user-name-display {
  font-size: 14px;
}

.user-role-tag {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  background-color: #e6f7ff;
  color: #0288d1;
  margin-left: 5px;
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
  padding: 10px 0;
  min-width: 160px;
  z-index: 1001;
}

.user-dropdown a {
  display: block;
  padding: 10px 20px;
  color: #333;
  text-decoration: none;
  transition: all 0.3s;
}

.user-dropdown a:hover {
  background: #f9f0ff;
  color: #d81b60;
  padding-left: 25px;
}

.user-dropdown a i {
  margin-right: 8px;
  width: 16px;
  text-align: center;
}

.dropdown-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 8px 0;
}

/* 第二行：主导航菜单 */
.header-bottom {
  padding: 12px 40px;
}

.main-nav {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.nav-link {
  text-decoration: none;
  color: #666;
  font-size: 15px;
  padding: 8px 0;
  position: relative;
  transition: color 0.3s;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #d81b60;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background: #d81b60;
  transition: width 0.3s;
}

.nav-link:hover::after,
.nav-link.router-link-active::after {
  width: 100%;
}

.nav-link.hidden {
  display: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-top {
    flex-direction: column;
    gap: 15px;
    padding: 15px 20px;
  }

  .header-info {
    order: 2;
    width: 100%;
    justify-content: center;
  }

  .current-school {
    margin-left: 0;
    margin-top: 10px;
}

  .user-actions {
    flex-direction: column;
    width: 100%;
  }

  .search-bar {
    width: 100%;
}

  .search-bar input {
    width: 100%;
}

  .header-bottom {
    padding: 12px 20px;
}

  .main-nav {
    flex-wrap: wrap;
    gap: 15px;
}
}

@media (max-width: 480px) {
  .main-nav {
    gap: 10px;
  }
  
  .nav-link {
    font-size: 14px;
  }

  .logo-main {
    font-size: 24px;
  }

  .logo-sub {
    font-size: 14px;
  }

  .header-info {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
