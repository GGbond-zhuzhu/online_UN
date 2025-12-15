<template>
  <footer class="app-footer">
    <div class="footer-container">
      <!-- 关于平台 -->
      <div class="footer-section">
        <h4 class="footer-title">关于上大学Online</h4>
        <p class="footer-desc">
          "上大学Online"是一款以"高校官方注册封装用户群体"为核心的校园综合服务平台，
          针对当前高校校园服务存在的功能分散、用户边界模糊、信息流通低效、安全信任缺失四大痛点，
          提供全方位的解决方案。
        </p>
        <p class="footer-vision">
          我们的愿景是：实现"一校一集合、一人一身份、一站全服务、一策保安全"。
        </p>
      </div>

      <!-- 平台动态 -->
      <div class="footer-section">
        <h4 class="footer-title">平台动态</h4>
        <ul class="footer-news">
          <li v-for="(news, index) in newsList" :key="index" class="news-item">
            <span class="news-tag">【{{ news.tag }}】</span>
            <span class="news-text">{{ news.text }}</span>
          </li>
        </ul>
      </div>

      <!-- 联系我们 -->
      <div class="footer-section">
        <h4 class="footer-title">联系我们</h4>
        <ul class="footer-contact">
          <li>服务热线：400-123-4567</li>
          <li>技术支持：tech@campus.edu.cn</li>
          <li>商务合作：business@campus.edu.cn</li>
          <li>高校接入：university@campus.edu.cn</li>
        </ul>
      </div>

      <!-- 快速链接 -->
      <div class="footer-section">
        <h4 class="footer-title">快速链接</h4>
        <ul class="footer-links">
          <li><router-link to="/about">平台介绍</router-link></li>
          <li><router-link to="/university/apply">高校接入申请</router-link></li>
          <li><router-link to="/help">用户使用指南</router-link></li>
          <li><router-link to="/privacy">隐私政策</router-link></li>
          <li><router-link to="/terms">服务协议</router-link></li>
          <li><router-link to="/feedback">问题反馈</router-link></li>
        </ul>
      </div>
    </div>

    <!-- 版权信息 -->
    <div class="footer-copyright">
      <p>© 2024 上大学Online校园综合服务平台 版权所有 | 让校园生活更简单</p>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 平台动态数据
const newsList = ref([
  { tag: '新功能上线', text: '校园E卡通人脸支付功能正式推出！' },
  { tag: '高校合作', text: '恭喜北京大学、清华大学成为首批官方接入院校。' },
  { tag: '安全提示', text: '二手交易防诈骗指南，请各位用户仔细阅读。' },
  { tag: '商户招募', text: '校园商家入驻通道限时开放，享专属扶持计划。' },
  { tag: '用户调研', text: '关于提升平台体验的问卷调查，期待您的参与。' }
])
</script>

<style scoped>
.app-footer {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 3rem 0 1rem;
  margin-top: 4rem;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.footer-title {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  color: #fff;
}

.footer-desc {
  font-size: 0.9rem;
  line-height: 1.6;
  color: #bdc3c7;
  margin-bottom: 0.5rem;
}

.footer-vision {
  font-size: 0.9rem;
  line-height: 1.6;
  color: #e91e63;
  font-weight: 500;
  margin-top: 0.5rem;
}

.footer-news {
  list-style: none;
  padding: 0;
  margin: 0;
}

.news-item {
  margin-bottom: 0.75rem;
  font-size: 0.9rem;
  line-height: 1.6;
  color: #bdc3c7;
}

.news-tag {
  color: #e91e63;
  font-weight: 500;
}

.news-text {
  color: #bdc3c7;
}

.footer-contact {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-contact li {
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  color: #bdc3c7;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 0.5rem;
}

.footer-links a {
  color: #bdc3c7;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #e91e63;
}

.footer-copyright {
  max-width: 1200px;
  margin: 2rem auto 0;
  padding: 1.5rem 2rem 0;
  border-top: 1px solid #34495e;
  text-align: center;
}

.footer-copyright p {
  color: #95a5a6;
  font-size: 0.85rem;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer-container {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
}
</style>
