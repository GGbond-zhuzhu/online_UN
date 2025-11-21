<!-- src/views/common/AboutUs.vue -->
<template>
  <div class="about-page">
    <!-- 页面标题 -->
    <h1 class="page-title">关于上大学Online</h1>

    <!-- 平台介绍 -->
    <section class="intro">
      <p>
        “上大学Online”是一款以"高校官方注册封装用户群体"为核心的校园综合服务平台，针对高校校园服务存在的功能分散、用户边界模糊、信息流通低效、安全信任缺失四大痛点，提供全方位的解决方案。
      </p>
      <p>
        我们的愿景是：实现“一校一集合、一人一身份、一站全服务、一策保安全”。
      </p>
      <p>
        平台采用“基础功能+高校定制功能”的模块化设计，整合多源融合定位、支付接口对接、消息信息推送、交易流程保障等先进技术，为用户提供安全、便捷、精准、个性化的校园服务体验。
      </p>
    </section>

    <!-- 平台动态 -->
    <section class="platform-dynamic">
      <h2 class="section-title">平台动态</h2>
      <div class="notices">
        <div 
          class="notice-item" 
          v-for="(notice, index) in notices" 
          :key="index"
        >
          <span class="notice-tag">{{ notice.tag }}</span>
          <a :href="notice.link" target="_blank">{{ notice.title }}</a>
        </div>
      </div>
    </section>

    <!-- 联系我们 -->
    <section class="contact-us">
      <h2 class="section-title">联系我们</h2>
      <p>服务热线：400-123-4567</p>
      <p>技术支持：<a href="mailto:tech@campus.edu.cn">tech@campus.edu.cn</a></p>
      <p>商务合作：<a href="mailto:business@campus.edu.cn">business@campus.edu.cn</a></p>
      <p>高校接入：<a href="mailto:university@campus.edu.cn">university@campus.edu.cn</a></p>
    </section>

    <!-- 快速链接 -->
    <section class="quick-links">
      <h2 class="section-title">快速链接</h2>
      <ul>
        <li v-for="(link, index) in quickLinks" :key="index">
          <a :href="link.url" target="_blank">{{ link.text }}</a>
        </li>
      </ul>
    </section>

    <!-- 页脚版权 -->
    <footer class="footer">
      <p>© 2024 上大学Online校园综合服务平台 版权所有 | 让校园生活更简单</p>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 平台动态数据（可从后端接口替换为动态数据）
const notices = ref([
  { tag: '[新功能上线]', title: '校园E卡通人脸支付功能正式推出！', link: '#' },
  { tag: '[高校合作]', title: '恭喜北京大学、清华大学成为首批官方接入院校。', link: '#' },
  { tag: '[安全提示]', title: '二手交易防诈骗指南，请各位用户仔细阅读。', link: '#' },
  { tag: '[商户招募]', title: '校园私厨商家入驻通道限时开放，享专属扶持计划。', link: '#' },
  { tag: '[用户调研]', title: '关于提升平台体验的问卷调查，期待您的参与。', link: '#' },
])

// 快速链接数据（可从后端接口替换为动态数据）
const quickLinks = ref([
  { text: '平台介绍', url: '#' },
  { text: '高校接入', url: '#' },
  { text: '申请用户', url: '#' },
  { text: '使用指南', url: '#' },
  { text: '隐私政策', url: '#' },
  { text: '服务协议', url: '#' },
  { text: '问题反馈', url: '#' },
])
</script>

<style scoped>
.about-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.page-title {
  text-align: center;
  font-size: 2rem;
  margin-bottom: 20px;
}

.intro p {
  line-height: 1.6;
  margin-bottom: 10px;
}

.section-title {
  font-size: 1.5rem;
  border-left: 4px solid #409eff; /* 匹配 Element Plus 主题色，可自定义 */
  padding-left: 8px;
  margin: 30px 0 15px;
}

.notices .notice-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.notice-tag {
  background-color: #f56c6c;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.8rem;
  margin-right: 8px;
}

.contact-us p {
  line-height: 1.8;
  margin-bottom: 8px;
}

.contact-us a {
  color: #409eff;
  text-decoration: none;
}

.contact-us a:hover {
  text-decoration: underline;
}

.quick-links ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.quick-links li {
  margin-bottom: 8px;
}

.quick-links a {
  color: #606266;
  text-decoration: none;
}

.quick-links a:hover {
  color: #409eff;
}

.footer {
  text-align: center;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  color: #909399;
  font-size: 0.9rem;
}
</style>