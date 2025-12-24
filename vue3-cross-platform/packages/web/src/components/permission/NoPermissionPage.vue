<template>
  <!--
    Web 端整页无权限页面组件
    说明：通常用于 403 场景，内置通用导航栏和页脚，并在中间位置展示
    common 包中的 NoPermission 提示组件。
  -->
  <div class="no-permission-page">
    <!-- 顶部全局导航栏 -->
    <NavBar />

    <!-- 中间主内容容器 -->
    <main class="content-wrapper">
      <section class="card">
        <!-- 直接使用 common 包中的 NoPermission 组件 -->
        <NoPermission />
      </section>
    </main>

    <!-- 底部全局页脚 -->
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
// 引入 Web 端导航栏与页脚组件
import NavBar from '@/components/common/NavBar.vue' // 导航栏组件，与首页保持一致
import AppFooter from '@/components/common/AppFooter.vue' // 页脚组件

// 引入 common 包中的无权限提示组件
import NoPermission from '@campus/common/components/permission/NoPermission.vue' // 通用无权限提示
</script>

<style scoped>
/* 整个页面容器样式 */
.no-permission-page {
  min-height: 100vh; /* 页面最小高度占满视口 */
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%); /* 使用与首页相同的渐变背景 */
  display: flex; /* 使用 flex 让主内容和页脚在垂直方向排列 */
  flex-direction: column; /* 垂直排列子元素 */
}

/* 主内容区域外层容器样式 */
.content-wrapper {
  flex: 1; /* 占据除页脚外的所有剩余高度 */
  max-width: 1200px; /* 与其他页面保持一致的内容宽度 */
  margin: 0 auto; /* 水平居中主内容 */
  padding: 40px 20px; /* 上下左右内边距 */
  box-sizing: border-box; /* 使用 border-box 计算宽度 */
  display: flex; /* 使用 flex 让卡片在容器中央显示 */
  align-items: center; /* 垂直方向居中 */
  justify-content: center; /* 水平方向居中 */
}

/* 中间卡片样式 */
.card {
  width: 100%; /* 宽度默认撑满容器 */
  max-width: 520px; /* 限制最大宽度，让卡片聚焦在中间 */
  background-color: #ffffff; /* 白色背景作为主体卡片颜色 */
  border-radius: 16px; /* 使用较大的圆角 */
  padding: 30px 24px; /* 卡片内边距 */
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12); /* 添加较强阴影突出卡片 */
}
</style>
