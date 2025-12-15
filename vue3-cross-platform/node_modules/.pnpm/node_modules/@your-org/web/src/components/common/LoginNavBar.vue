<template>
  <nav class="login-navbar">
    <div class="navbar-container">
      <!-- Logo -->
      <div class="navbar-left">
        <router-link to="/" class="logo-link">
          <span class="logo-main">上大学Online</span>
        </router-link>
      </div>

      <!-- 导航链接 -->
      <div class="navbar-center">
        <nav class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/app-download" class="nav-link">手机App下载</router-link>
          <router-link to="/client-download" class="nav-link">电脑客户端</router-link>
          <router-link to="/security" class="nav-link">安全保障</router-link>
          <router-link to="/help" class="nav-link">使用帮助</router-link>
          <router-link to="/feedback" class="nav-link">问题反馈</router-link>
          <router-link to="/about" class="nav-link">关于我们</router-link>
        </nav>
      </div>

      <!-- 用户操作 -->
      <div class="navbar-right">
        <div class="user-menu" @mouseenter="showDropdown = true" @mouseleave="showDropdown = false">
          <div class="user-info">
            <i class="fas fa-user"></i>
            <span class="user-text">{{ userStore.isLoggedIn ? (userStore.username || '用户') : '登录' }}</span>
          </div>
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
  </nav>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@campus/common'

const router = useRouter()
const userStore = useUserStore()

const showDropdown = ref(false)

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
</script>

<style scoped>
.login-navbar {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 40px;
}

.navbar-left {
  flex-shrink: 0;
}

.logo-link {
  text-decoration: none;
  display: flex;
  align-items: center;
}

.logo-main {
  font-size: 24px;
  font-weight: bold;
  color: #d81b60;
}

.navbar-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-link {
  color: #333;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
  position: relative;
  padding: 5px 0;
}

.nav-link:hover {
  color: #d81b60;
}

.nav-link.router-link-active {
  color: #d81b60;
  font-weight: 500;
}

.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: #d81b60;
}

.navbar-right {
  flex-shrink: 0;
}

.user-menu {
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 15px;
  border-radius: 20px;
  cursor: pointer;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f5f5f5;
}

.user-info i {
  color: #d81b60;
  font-size: 16px;
}

.user-text {
  font-size: 14px;
  color: #333;
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  min-width: 160px;
  padding: 8px 0;
  margin-top: 8px;
  z-index: 1001;
}

.user-dropdown::before {
  content: '';
  position: absolute;
  top: -6px;
  right: 15px;
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-bottom: 6px solid white;
}

.user-dropdown a {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  color: #333;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
}

.user-dropdown a:hover {
  background: #f9f0ff;
  color: #d81b60;
  padding-left: 20px;
}

.user-dropdown a i {
  width: 18px;
  margin-right: 10px;
  text-align: center;
  color: #666;
}

.user-dropdown a:hover i {
  color: #d81b60;
}

.dropdown-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 8px 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .navbar-container {
    padding: 15px 20px;
  }

  .nav-links {
    gap: 20px;
  }

  .nav-link {
    font-size: 13px;
  }
}

@media (max-width: 768px) {
  .navbar-container {
    flex-wrap: wrap;
    padding: 12px 15px;
  }

  .navbar-center {
    order: 3;
    width: 100%;
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #f0f0f0;
  }

  .nav-links {
    flex-wrap: wrap;
    gap: 15px;
    justify-content: center;
  }

  .nav-link {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .nav-links {
    gap: 10px;
  }

  .nav-link {
    font-size: 11px;
  }

  .logo-main {
    font-size: 20px;
  }
}
</style>
