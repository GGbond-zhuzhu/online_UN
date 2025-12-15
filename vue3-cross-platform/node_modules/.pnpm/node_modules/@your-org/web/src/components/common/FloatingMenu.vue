<template>
  <div class="floating-menu">
    <div class="floating-content" :class="{ active: isOpen }">
      <a href="#" @click.prevent="scrollToTop" v-if="showScrollToTop">
        <i class="fas fa-arrow-up"></i> 回到页面顶部
      </a>
      <a href="#" @click.prevent="goBack" v-if="canGoBack">
        <i class="fas fa-arrow-left"></i> 返回上一页
      </a>
      <router-link to="/" @click="closeMenu">
        <i class="fas fa-home"></i> 首页
      </router-link>
      <router-link to="/profile" v-if="userStore.isLoggedIn" @click="closeMenu">
        <i class="fas fa-user"></i> 个人中心
      </router-link>
      <router-link to="/profile" v-if="userStore.isLoggedIn" @click="closeMenu">
        <i class="fas fa-cog"></i> 设置
      </router-link>
      <router-link to="/help" @click="closeMenu">
        <i class="fas fa-question-circle"></i> 帮助
      </router-link>
      <a href="#" @click.prevent="handleLogout" v-if="userStore.isLoggedIn">
        <i class="fas fa-sign-out-alt"></i> 退出登录
      </a>
    </div>
    <div class="floating-btn" @click="toggleMenu">
      <i class="fas fa-bars"></i>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@campus/common'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isOpen = ref(false)
const showScrollToTop = ref(false)

// 判断是否可以返回上一页（不在首页时可以返回）
const canGoBack = computed(() => {
  return route.path !== '/' && window.history.length > 1
})

// 监听滚动，显示/隐藏回到顶部按钮
const handleScroll = () => {
  showScrollToTop.value = window.scrollY > 300
}

// 回到页面顶部
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
  isOpen.value = false
}

// 关闭菜单
const closeMenu = () => {
  isOpen.value = false
}

// 返回上一页
const goBack = () => {
  router.back()
  isOpen.value = false
}

// 切换菜单
const toggleMenu = () => {
  isOpen.value = !isOpen.value
}

// 退出登录
const handleLogout = async () => {
  try {
    await userStore.logout()
    router.push('/')
    isOpen.value = false
  } catch (error) {
    console.error('退出登录失败:', error)
  }
}

// 点击外部关闭菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (!target.closest('.floating-menu')) {
    isOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  // 监听滚动事件
  window.addEventListener('scroll', handleScroll)
  // 初始化检查
  handleScroll()
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
  // 移除滚动监听
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.floating-menu {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 1000;
}

.floating-btn {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.3);
  transition: all 0.3s;
  font-size: 24px;
}

.floating-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(216, 27, 96, 0.4);
}

.floating-content {
  position: absolute;
  bottom: 70px;
  right: 0;
  background: white;
  border-radius: 10px;
  padding: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
  min-width: 200px;
  display: none;
}

.floating-content.active {
  display: block;
}

.floating-content a {
  display: block;
  padding: 10px 15px;
  color: #333;
  text-decoration: none;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.floating-content a:hover {
  background: #f9f0ff;
  color: #d81b60;
  padding-left: 20px;
}

.floating-content a:last-child {
  border-bottom: none;
}

.floating-content a i {
  margin-right: 8px;
  width: 16px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .floating-menu {
    bottom: 20px;
    right: 20px;
  }

  .floating-btn {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
}
</style>
