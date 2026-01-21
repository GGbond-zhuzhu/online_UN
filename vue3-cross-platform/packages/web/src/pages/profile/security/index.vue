<template>
  <div class="page">
    <NavBar />

    <div class="container">
      <div class="card">
        <div class="title">安全设置</div>
        <div class="desc">该页面用于账号安全相关配置（Web端占位页）。</div>

        <div class="actions">
          <button class="btn primary" @click="goBack">
            <i class="fas fa-arrow-left"></i>
            返回
          </button>
          <router-link class="btn" to="/profile">
            <i class="fas fa-user-circle"></i>
            个人中心
          </router-link>
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
const goBack = () => router.back()
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #fff0f5 0%, #f0f8ff 60%, #f5f0ff 100%);
}
.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 16px;
}
.card {
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 107, 157, 0.18);
  border-radius: 14px;
  box-shadow: 0 10px 24px rgba(255, 107, 157, 0.08);
  padding: 16px;
}
.title {
  font-size: 20px;
  font-weight: 800;
  color: #333;
}
.desc {
  margin-top: 6px;
  color: #666;
  font-size: 13px;
}
.actions {
  margin-top: 12px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.btn {
  height: 36px;
  padding: 0 14px;
  border-radius: 10px;
  border: 1px solid rgba(255, 107, 157, 0.25);
  background: rgba(255, 255, 255, 0.9);
  color: #ff6b9d;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  font-weight: 700;
}
.btn.primary {
  border: none;
  background: linear-gradient(135deg, #ff8fb3 0%, #ff6b9d 100%);
  color: #fff;
}
</style>
