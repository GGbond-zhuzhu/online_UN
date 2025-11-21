<template>
    <div class="user-management">
      <h2>🏫 大学在线平台 - 用户管理</h2>
      
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button @click="testBackend" class="btn">测试后端连接</button>
        <button @click="loadUsers" class="btn">获取用户列表</button>
        <button @click="addTestUser" class="btn">添加测试用户</button>
      </div>
  
      <!-- 状态显示 -->
      <div v-if="message" :class="['message', messageType]">
        {{ message }}
      </div>
  
      <!-- 用户列表 -->
      <div v-if="users.length > 0" class="user-list">
        <h3>用户列表 (共{{ users.length }}个用户)</h3>
        <div v-for="user in users" :key="user.id" class="user-card">
          <p><strong>ID:</strong> {{ user.id }}</p>
          <p><strong>用户名:</strong> {{ user.username }}</p>
          <p><strong>姓名:</strong> {{ user.realName }}</p>
          <p><strong>学号:</strong> {{ user.studentId }}</p>
          <p><strong>专业:</strong> {{ user.major }}</p>
          <p><strong>创建时间:</strong> {{ formatDate(user.createTime) }}</p>
        </div>
      </div>
  
      <!-- 添加用户表单 -->
      <div class="add-user-form">
        <h3>添加新用户</h3>
        <form @submit.prevent="submitUser">
          <input v-model="newUser.username" placeholder="用户名" required>
          <input v-model="newUser.password" type="password" placeholder="密码" required>
          <input v-model="newUser.realName" placeholder="真实姓名">
          <input v-model="newUser.studentId" placeholder="学号">
          <input v-model="newUser.major" placeholder="专业">
          <input v-model="newUser.email" type="email" placeholder="邮箱">
          <button type="submit" class="btn">添加用户</button>
        </form>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  
  const message = ref('')
  const messageType = ref('info')
  const users = ref([])
  
  const newUser = ref({
    username: '',
    password: '',
    realName: '',
    studentId: '',
    major: '',
    email: ''
  })
  
  // 测试后端连接
  const testBackend = async () => {
    try {
      const response = await axios.get('/api/users/test')
      message.value = '✅ ' + response.data
      messageType.value = 'success'
    } catch (error) {
      message.value = '❌ 连接失败: ' + error.message
      messageType.value = 'error'
    }
  }
  
  // 加载用户列表
  const loadUsers = async () => {
    try {
      const response = await axios.get('/api/users/all')
      users.value = response.data
      message.value = `✅ 成功加载 ${users.value.length} 个用户`
      messageType.value = 'success'
    } catch (error) {
      message.value = '❌ 加载用户失败: ' + error.message
      messageType.value = 'error'
    }
  }
  
  // 添加测试用户
  const addTestUser = async () => {
    try {
      const testUser = {
        username: 'student_' + Date.now(),
        password: '123456',
        realName: '测试学生',
        studentId: '2024' + Math.floor(Math.random() * 10000),
        major: '计算机科学',
        email: `test${Date.now()}@university.com`
      }
      
      const response = await axios.post('/api/users/create', testUser)
      message.value = '✅ 测试用户添加成功！'
      messageType.value = 'success'
      loadUsers() // 重新加载列表
    } catch (error) {
      message.value = '❌ 添加用户失败: ' + error.message
      messageType.value = 'error'
    }
  }
  
  // 提交新用户
  const submitUser = async () => {
    try {
      const response = await axios.post('/api/users/create', newUser.value)
      message.value = '✅ 用户添加成功！'
      messageType.value = 'success'
      
      // 清空表单
      newUser.value = {
        username: '', password: '', realName: '', 
        studentId: '', major: '', email: ''
      }
      
      loadUsers() // 刷新列表
    } catch (error) {
      message.value = '❌ 添加失败: ' + error.message
      messageType.value = 'error'
    }
  }
  
  // 格式化日期
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString('zh-CN')
  }
  
  // 组件挂载时自动加载用户
  onMounted(() => {
    loadUsers()
  })
  </script>
  
  <style scoped>
  .user-management {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .action-buttons {
    margin: 20px 0;
  }
  
  .btn {
    background: #007bff;
    color: white;
    border: none;
    padding: 10px 15px;
    margin: 0 10px 10px 0;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .btn:hover {
    background: #0056b3;
  }
  
  .message {
    padding: 10px;
    border-radius: 4px;
    margin: 10px 0;
  }
  
  .message.success { background: #d4edda; color: #155724; }
  .message.error { background: #f8d7da; color: #721c24; }
  .message.info { background: #d1ecf1; color: #0c5460; }
  
  .user-list {
    margin: 20px 0;
  }
  
  .user-card {
    border: 1px solid #ddd;
    padding: 15px;
    margin: 10px 0;
    border-radius: 4px;
    background: #f8f9fa;
  }
  
  .add-user-form {
    margin-top: 30px;
    padding: 20px;
    border: 1px solid #eee;
    border-radius: 4px;
  }
  
  .add-user-form input {
    display: block;
    width: 100%;
    margin: 5px 0 15px;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  </style>