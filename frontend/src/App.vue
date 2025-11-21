<template>
  <div id="app">
    <nav class="navbar">
      <h1>🏫 大学在线服务平台</h1>
      <router-link to="/users">用户管理</router-link>
    </nav>
    <main>
      <router-view />
    </main>
  </div>
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Arial', sans-serif;
  line-height: 1.6;
  color: #333;
}

.navbar {
  background: #343a40;
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.navbar h1 {
  font-size: 1.5rem;
  margin: 0;
}

.navbar a {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background 0.3s;
}

.navbar a:hover {
  background: rgba(255,255,255,0.1);
}

main {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}
</style>