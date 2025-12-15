<template>
  <div class="home">
    <h1>{{ contentStore.title }}</h1>
    <p>{{ contentStore.content }}</p>
    <BaseButton type="primary" @click="handleUpdate">更新标题</BaseButton>
    <BaseButton type="secondary" style="margin-left: 10px" @click="testRequest">测试请求</BaseButton>
  </div>
</template>

<script setup lang="ts">
import { BaseButton, useContentStore, http } from '@cross/common';

const contentStore = useContentStore();

const handleUpdate = () => {
  contentStore.updateTitle('Web端更新后的标题');
};

const testRequest = async () => {
  try {
    // 测试请求
    const res = await http.get('/test');
    console.log('请求结果：', res);
  } catch (err) {
    console.error('请求失败：', err);
  }
};
</script>

<style scoped>
.home {
  padding: 20px;
}

h1 {
  color: #409eff;
}

p {
  font-size: 16px;
  margin: 20px 0;
}
</style>
