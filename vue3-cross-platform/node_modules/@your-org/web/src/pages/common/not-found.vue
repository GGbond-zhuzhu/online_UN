<template>
  <div class="not-found-page">
    <NavBar />

    <div class="page-container">
      <div class="not-found-content">
        <div class="error-code">404</div>
        <h1 class="error-title">页面未找到</h1>
        <p class="error-desc">抱歉，您访问的页面不存在或已被删除</p>
        <div class="error-actions">
          <button class="btn-home" @click="goHome">
            <i class="fas fa-home"></i> 返回首页
          </button>
          <button class="btn-back" @click="goBack">
            <i class="fas fa-arrow-left"></i> 返回上一页
          </button>
        </div>
        <div class="helpful-links">
          <h3>您可能想要访问：</h3>
          <div class="links-grid">
            <router-link to="/" class="link-item">
              <i class="fas fa-home"></i>
              <span>首页</span>
            </router-link>
            <router-link to="/secondhand" class="link-item">
              <i class="fas fa-shopping-bag"></i>
              <span>二手交易</span>
            </router-link>
            <router-link to="/parttime" class="link-item">
              <i class="fas fa-briefcase"></i>
              <span>兼职服务</span>
            </router-link>
            <router-link to="/schedule" class="link-item">
              <i class="fas fa-calendar-alt"></i>
              <span>行程管理</span>
            </router-link>
            <router-link to="/help" class="link-item">
              <i class="fas fa-question-circle"></i>
              <span>帮助中心</span>
            </router-link>
            <router-link to="/feedback" class="link-item">
              <i class="fas fa-comment-dots"></i>
              <span>问题反馈</span>
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.not-found-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 60px 20px;
}

.not-found-content {
  text-align: center;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 20px;
  line-height: 1;
}

.error-title {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
}

.error-desc {
  font-size: 16px;
  color: #666;
  margin-bottom: 40px;
}

.error-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-bottom: 60px;
}

.btn-home,
.btn-back {
  padding: 12px 24px;
  border-radius: 8px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-home {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
}

.btn-home:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
}

.btn-back {
  background: white;
  color: var(--primary);
  border: 2px solid rgba(240, 240, 240, 0.9);
}

.btn-back:hover {
  background: #f9f0ff;
  border-color: var(--primary);
}

.helpful-links {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.helpful-links h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
}

.link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border-radius: 12px;
  background: #f9f0ff;
  color: var(--primary);
  text-decoration: none;
  transition: all 0.3s;
}

.link-item:hover {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  transform: translateY(-2px);
}

.link-item i {
  font-size: 24px;
}

.link-item span {
  font-size: 14px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .error-code {
    font-size: 80px;
  }

  .error-title {
    font-size: 24px;
  }

  .error-actions {
    flex-direction: column;
  }

  .btn-home,
  .btn-back {
    width: 100%;
    justify-content: center;
  }

  .links-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
