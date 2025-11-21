<template>
  <div class="app-layout">
    <header class="app-header">
      <nav class="navigation">
        <router-link to="/">首页</router-link>
        <router-link to="/login">登录</router-link>
      </nav>
    </header>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
// 组合式 API
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.app-header {
  background: #409EFF;
  padding: 1rem 2rem;
}
.navigation a {
  color: white;
  text-decoration: none;
  margin-right: 1rem;
}
.main-content {
  flex: 1;
  padding: 2rem;
}
</style>
