<!-- src/views/common/Login.vue -->
<template>
  <div class="login-page">
    <div class="header-container">
      <div class="header-top">
        <div class="logo">
          <span class="logo-main">上大学</span>
          <span class="logo-sub">Online</span>
        </div>
        <div class="user-actions">
          <a href="#" @click.prevent="goToHome" class="back-home">
            <i class="fas fa-home"></i> 返回首页
          </a>
        </div>
      </div>
    </div>

    <div class="login-container">
      <div class="login-form">
        <h2 class="login-title">用户登录</h2>
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">用户名/学号/手机号</label>
            <input 
              type="text" 
              id="username" 
              v-model="loginForm.username" 
              placeholder="请输入用户名、学号或手机号"
              required
            >
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input 
              type="password" 
              id="password" 
              v-model="loginForm.password" 
              placeholder="请输入密码"
              required
            >
          </div>
          <div class="form-options">
            <label class="remember-me">
              <input type="checkbox" v-model="loginForm.rememberMe"> 记住我
            </label>
            <a href="#" class="forgot-password">忘记密码？</a>
          </div>
          <button type="submit" class="login-btn">登录</button>
        </form>
        <div class="login-divider">
          <span>或使用以下方式登录</span>
        </div>
        <div class="social-login">
          <button class="social-btn wechat">
            <i class="fab fa-weixin"></i> 微信登录
          </button>
          <button class="social-btn qq">
            <i class="fab fa-qq"></i> QQ登录
          </button>
        </div>
        <div class="register-link">
          还没有账号？<a href="#" @click.prevent="goToRegister">立即注册</a>
        </div>
      </div>
      <div class="login-info">
        <h3>欢迎回来！</h3>
        <p>登录后即可享受上大学Online提供的全方位校园服务：</p>
        <ul>
          <li><i class="fas fa-check"></i> 校园E卡通服务</li>
          <li><i class="fas fa-check"></i> 二手交易平台</li>
          <li><i class="fas fa-check"></i> 校园私厨订餐</li>
          <li><i class="fas fa-check"></i> 个人行程管理</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginPage',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        rememberMe: false
      }
    }
  },
  methods: {
    handleLogin() {
      // 这里应该调用登录API
      console.log('登录信息:', this.loginForm);
      // 模拟登录成功
      alert('登录成功！');
      this.goToHome();
    },
    goToHome() {
      this.$router.push('/');
    },
    goToRegister() {
      this.$router.push('/register');
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.header-container {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 40px;
  border-bottom: 1px solid #f0f0f0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-main {
  font-size: 28px;
  font-weight: bold;
  color: #d81b60;
}

.logo-sub {
  font-size: 16px;
  color: #d81b60;
  align-self: flex-end;
  margin-bottom: 3px;
}

.back-home {
  color: #666;
  text-decoration: none;
  padding: 8px 15px;
  border-radius: 20px;
  transition: all 0.3s;
}

.back-home:hover {
  background: #f5f5f5;
  color: #d81b60;
}

.login-container {
  display: flex;
  max-width: 1000px;
  margin: 50px auto;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  overflow: hidden;
}

.login-form {
  flex: 1;
  padding: 40px;
  border-right: 1px solid #f0f0f0;
}

.login-title {
  text-align: center;
  font-size: 28px;
  font-weight: bold;
  color: #d81b60;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #d81b60;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.forgot-password {
  color: #d81b60;
  text-decoration: none;
}

.forgot-password:hover {
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: #d81b60;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.login-btn:hover {
  background: #c2185b;
  transform: translateY(-2px);
}

.login-divider {
  text-align: center;
  margin: 30px 0;
  position: relative;
  color: #999;
}

.login-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #eee;
}

.login-divider span {
  background: white;
  padding: 0 15px;
  position: relative;
}

.social-login {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.social-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.social-btn.wechat:hover {
  border-color: #07c160;
  color: #07c160;
}

.social-btn.qq:hover {
  border-color: #12b7f5;
  color: #12b7f5;
}

.register-link {
  text-align: center;
  color: #666;
}

.register-link a {
  color: #d81b60;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

.login-info {
  flex: 1;
  padding: 40px;
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.login-info h3 {
  font-size: 24px;
  color: #d81b60;
  margin-bottom: 20px;
}

.login-info p {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.6;
}

.login-info ul {
  list-style: none;
}

.login-info li {
  padding: 8px 0;
  color: #555;
}

.login-info li i {
  color: #d81b60;
  margin-right: 10px;
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    margin: 20px;
  }
  
  .login-form {
    border-right: none;
    border-bottom: 1px solid #f0f0f0;
  }
}
</style>