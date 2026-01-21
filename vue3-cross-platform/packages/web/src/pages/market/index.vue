<template>
  <div class="market-page">
    <NavBar />
    <div class="market-container">
      <div class="page-header">
        <h1 class="page-title">校园市场</h1>
        <p class="page-subtitle">商家入驻、优惠活动</p>
      </div>
      <div class="market-content">
        <p class="coming-soon">功能开发中，敬请期待...</p>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
</script>

<style scoped>
.market-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff8e1 0%, #fff3c4 100%);
  display: flex;
  flex-direction: column;
}

.market-container {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
}

.market-content {
  background: white;
  border-radius: 20px;
  padding: 80px 60px;
  text-align: center;
  box-shadow: 0 8px 30px rgba(148, 163, 184, 0.15);
  border: 1px solid rgba(216, 27, 96, 0.1);
  position: relative;
  overflow: hidden;
}

.market-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #fff3c4 0%, #fff8e1 100%);
}

.coming-soon {
  font-size: 20px;
  color: #666;
  font-weight: 500;
  position: relative;
  z-index: 1;
}

.coming-soon::before {
  content: '🚧';
  margin-right: 10px;
  font-size: 24px;
}
</style>

