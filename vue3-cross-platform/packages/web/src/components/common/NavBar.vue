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
          <!-- 消息中心图标：点击跳转到消息列表，存在未读时显示右上角小红点 -->
          <div class="message-bell" @click="goToMessages">
            <i class="fas fa-bell"></i>
            <span v-if="hasUnreadMessages" class="message-badge"></span>
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
import { ref, computed, onMounted, onUnmounted } from 'vue' // 引入 Vue 的响应式工具和生命周期函数
import { useRouter } from 'vue-router' // 引入路由工具，用于在导航栏中进行页面跳转
import { useUserStore, request } from '@campus/common' // 引入用户状态 Store 和统一封装的请求工具
import { connectMessageWebSocket, disconnectMessageWebSocket } from '@/utils/messageWebSocket' // 引入消息中心 WebSocket 工具，用于实时更新未读消息小红点

const router = useRouter() // 创建路由实例，后续用于执行跳转操作
const userStore = useUserStore() // 获取用户状态管理实例，用于读取登录信息和用户角色

// 搜索关键词
const searchKeyword = ref('') // 绑定顶部搜索输入框中的内容
// 下拉菜单显示状态
const showDropdown = ref(false) // 控制用户头像右侧下拉菜单的展开与收起
// 当前时间
const currentTime = ref('--:--:--') // 显示在导航栏中的当前时间文本
// 定位状态
const locationStatus = ref<'loading' | 'success' | 'error'>('loading') // 表示定位的加载状态（加载中/成功/失败）
// 定位文本
const locationText = ref('定位中...') // 展示给用户看的当前位置描述
// 时间更新定时器
let timeInterval: number | null = null // 保存 setInterval 的ID，组件卸载时需要清除

// 消息中心未读消息数量（用于控制右上角的小红点显示）
const unreadCount = ref(0) // 存储当前用户在消息中心的未读消息总数

// 是否有未读消息的计算属性（大于0即认为存在未读消息）
const hasUnreadMessages = computed(() => unreadCount.value > 0) // 当未读数量大于0时返回true，用于控制小红点显示

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

// 检查是否可以访问某个功能（与首页保持同一套角色控制表）
const canAccess = (feature: string): boolean => { // canAccess：根据当前角色判断某个功能是否可访问
  const role = userStore.currentRole // 读取当前用户角色编码（student / teacher / visitor 等）
  const featureRoles: Record<string, string[]> = { // 为不同功能配置允许访问的角色列表
    ecard: ['student', 'teacher', 'university', 'visitor', 'tourist'], // 校园卡：学生 / 教师 / 高校管理员 / 游客
    secondhand: ['student', 'teacher', 'merchant', 'visitor', 'tourist'], // 二手交易：学生 / 教师 / 商家 / 游客
    parttime: ['student', 'merchant', 'admin', 'visitor', 'tourist'], // 兼职服务：学生 / 商家 / 管理员 / 游客
    schedule: ['student', 'teacher', 'visitor', 'tourist'], // 行程管理：学生 / 教师 / 游客
    admin: ['admin', 'university'] // 管理中心：仅管理员与高校管理员可见
  }
  const allowedRoles = featureRoles[feature] || [] // 根据功能名称取出允许访问的角色数组
  return allowedRoles.includes(role) // 当前角色出现在允许列表中时返回 true，否则返回 false
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

// 从后端获取当前用户的未读消息数量，用于初始化右上角小红点
const fetchUnreadCount = async () => {
  // 如果当前用户未登录，则直接将未读数量重置为0
  if (!userStore.isLoggedIn || !userStore.userId) { // 判断用户是否已登录并且拥有有效的用户ID
    unreadCount.value = 0 // 未登录时未读数量恒为0
    return // 不再继续请求后端接口
  }
  try {
    const res: any = await request.get('/api/messages/unread-count') // 调用后端GET /api/messages/unread-count 接口
    unreadCount.value = Number(res.unreadCount || 0) // 将返回的未读数量转换为数字并赋值（为空时按0处理）
  } catch (error) {
    console.error('获取未读消息数量失败:', error) // 如果请求失败，在控制台打印错误信息
  }
}

// 处理通过 WebSocket 收到的“未读消息变化”事件
const handleMessageWsEvent = async (payload: any) => {
  // 每当后端推送一条“未读消息变化”事件时，重新向后端请求未读数量，保证前端与数据库状态一致
  try {
    await fetchUnreadCount() // 调用封装好的函数刷新未读数量
  } catch (error) {
    console.error('处理消息中心 WebSocket 事件失败:', error) // 如果刷新失败，记录错误日志但不中断其它逻辑
  }
}

// 跳转到消息中心页面
const goToMessages = () => {
  router.push('/messages') // 使用路由跳转到消息中心列表页面
}

// 退出登录
const handleLogout = async () => {
  try {
    await userStore.logout() // 调用用户Store的登出方法，清理登录状态与本地缓存
    unreadCount.value = 0 // 登出后重置未读消息数量为0
    disconnectMessageWebSocket() // 断开消息中心 WebSocket 连接，释放资源
    router.push('/') // 跳转回首页
    showDropdown.value = false // 关闭用户下拉菜单
  } catch (error) {
    console.error('退出登录失败:', error) // 登出过程中发生异常时打印错误信息
  }
}

// 组件挂载
onMounted(() => {
  // 从本地存储中初始化用户信息，防止刷新页面后导航栏丢失登录状态
  // @ts-expect-error 兼容旧版Store中可能不存在该方法的情况
  if (typeof userStore.initUserFromStorage === 'function') { // 判断Store中是否存在该初始化方法
    // @ts-ignore
    userStore.initUserFromStorage() // 调用初始化方法，从本地缓存中恢复用户登录信息
  }

  updateTime() // 首次立即更新时间显示
  timeInterval = window.setInterval(updateTime, 1000) // 每隔1秒更新时间文本
  getLocation() // 初始化定位信息（当前为模拟实现）

  // 如果当前用户已登录，则初始化未读消息数量并建立消息中心 WebSocket 连接
  if (userStore.isLoggedIn && userStore.userId) { // 确保存在有效用户ID后再建立WebSocket连接
    fetchUnreadCount() // 先调用一次接口获取当前未读消息数量
    connectMessageWebSocket(userStore.userId, handleMessageWsEvent) // 建立WebSocket连接并订阅未读状态变化事件
  }
})

// 组件卸载
onUnmounted(() => {
  if (timeInterval) { // 如果存在定时器
    clearInterval(timeInterval) // 清除定时器，避免内存泄漏
  }
  disconnectMessageWebSocket() // 组件卸载时断开消息中心 WebSocket 连接
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

/* 消息中心小铃铛图标外层容器 */
.message-bell {
  position: relative; /* 为小红点绝对定位提供参照 */
  width: 32px; /* 设定一个合适的宽度 */
  height: 32px; /* 设定一个合适的高度 */
  border-radius: 50%; /* 圆形背景 */
  background: #f8f9fa; /* 使用浅灰背景与整体风格统一 */
  display: flex; /* 使用flex让图标居中 */
  align-items: center; /* 垂直居中图标 */
  justify-content: center; /* 水平居中图标 */
  cursor: pointer; /* 鼠标悬停时显示为可点击 */
  transition: background 0.3s, transform 0.2s; /* 增加背景色和轻微位移动画 */
}

.message-bell i {
  color: #666; /* 铃铛图标使用中性灰色 */
  font-size: 16px; /* 图标大小适中，避免过大抢眼 */
}

.message-bell:hover {
  background: #f1e4f7; /* 悬停时背景略带粉紫色，呼应主色调 */
  transform: translateY(-1px); /* 轻微上移增强交互感 */
}

/* 未读消息小红点样式 */
.message-badge {
  position: absolute; /* 绝对定位在铃铛右上角 */
  top: 4px; /* 调整垂直位置 */
  right: 4px; /* 调整水平位置 */
  width: 8px; /* 小圆点宽度 */
  height: 8px; /* 小圆点高度 */
  border-radius: 50%; /* 圆形小点 */
  background-color: #ff4d4f; /* 使用亮红色表示有未读消息 */
  box-shadow: 0 0 0 2px #fff; /* 外边加一圈白色描边，让小红点在浅背景上更清晰 */
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
