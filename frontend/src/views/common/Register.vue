<!-- src/views/common/Register.vue -->
<template>
  <div class="register-page">
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

    <div class="register-container">
      <div class="register-form">
        <h2 class="register-title">用户注册</h2>
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="userType">注册类型</label>
            <select id="userType" v-model="registerForm.userType" required>
              <option value="">请选择注册类型</option>
              <option value="student">学生</option>
              <option value="teacher">教师</option>
              <option value="merchant">商家</option>
            </select>
          </div>
          
          <div class="form-group" v-if="registerForm.userType === 'student'">
            <label for="studentId">学号</label>
            <input 
              type="text" 
              id="studentId" 
              v-model="registerForm.studentId" 
              placeholder="请输入学号"
              required
            >
          </div>
          
          <div class="form-group" v-if="registerForm.userType === 'student'">
            <label for="school">所在学校</label>
            <select id="school" v-model="registerForm.school" required>
              <option value="">请选择学校</option>
              <option value="pku">北京大学</option>
              <option value="tsinghua">清华大学</option>
              <option value="fudan">复旦大学</option>
              <option value="shanghaijiao">上海交通大学</option>
            </select>
          </div>
          
          <div class="form-group" v-if="registerForm.userType === 'teacher'">
            <label for="teacherId">工号</label>
            <input 
              type="text" 
              id="teacherId" 
              v-model="registerForm.teacherId" 
              placeholder="请输入工号"
              required
            >
          </div>
          
          <div class="form-group">
            <label for="username">用户名</label>
            <input 
              type="text" 
              id="username" 
              v-model="registerForm.username" 
              placeholder="请输入用户名（2-20位字符）"
              required
            >
          </div>
          
          <div class="form-group">
            <label for="phone">手机号</label>
            <input 
              type="tel" 
              id="phone" 
              v-model="registerForm.phone" 
              placeholder="请输入手机号"
              required
            >
          </div>
          
          <div class="form-group">
            <label for="password">密码</label>
            <input 
              type="password" 
              id="password" 
              v-model="registerForm.password" 
              placeholder="请输入密码（6-20位字符）"
              required
            >
          </div>
          
          <div class="form-group">
            <label for="confirmPassword">确认密码</label>
            <input 
              type="password" 
              id="confirmPassword" 
              v-model="registerForm.confirmPassword" 
              placeholder="请再次输入密码"
              required
            >
          </div>
          
          <div class="form-group">
            <label for="verificationCode">验证码</label>
            <div class="verification-code">
              <input 
                type="text" 
                id="verificationCode" 
                v-model="registerForm.verificationCode" 
                placeholder="请输入验证码"
                required
              >
              <button type="button" class="send-code-btn" @click="sendVerificationCode">
                {{ countdown > 0 ? `${countdown}秒后重发` : '发送验证码' }}
              </button>
            </div>
          </div>
          
          <div class="form-options">
            <label class="agree-terms">
              <input type="checkbox" v-model="registerForm.agreeTerms" required> 
              我已阅读并同意
              <a href="#">《服务协议》</a>和
              <a href="#">《隐私政策》</a>
            </label>
          </div>
          
          <button type="submit" class="register-btn">注册</button>
        </form>
        
        <div class="login-link">
          已有账号？<a href="#" @click.prevent="goToLogin">立即登录</a>
        </div>
      </div>
      
      <div class="register-info">
        <h3>加入上大学Online</h3>
        <p>注册后即可享受以下专属服务：</p>
        <ul>
          <li><i class="fas fa-check"></i> 校园E卡通，无卡化校园生活</li>
          <li><i class="fas fa-check"></i> 安全可靠的二手交易平台</li>
          <li><i class="fas fa-check"></i> 丰富多样的校园私厨选择</li>
          <li><i class="fas fa-check"></i> 智能行程管理与提醒</li>
          <li><i class="fas fa-check"></i> 个性化服务推荐</li>
        </ul>
        
        <div class="features-highlight">
          <div class="feature-item">
            <i class="fas fa-shield-alt"></i>
            <span>信息安全保障</span>
          </div>
          <div class="feature-item">
            <i class="fas fa-bolt"></i>
            <span>高效认证流程</span>
          </div>
          <div class="feature-item">
            <i class="fas fa-sync-alt"></i>
            <span>多设备同步</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RegisterPage',
  data() {
    return {
      registerForm: {
        userType: '',
        studentId: '',
        teacherId: '',
        school: '',
        username: '',
        phone: '',
        password: '',
        confirmPassword: '',
        verificationCode: '',
        agreeTerms: false
      },
      countdown: 0
    }
  },
  methods: {
    handleRegister() {
      // 这里应该调用注册API
      console.log('注册信息:', this.registerForm);
      // 模拟注册成功
      alert('注册成功！');
      this.goToLogin();
    },
    sendVerificationCode() {
      if (this.countdown > 0) return;
      
      // 模拟发送验证码
      this.countdown = 60;
      const timer = setInterval(() => {
        this.countdown--;
        if (this.countdown <= 0) {
          clearInterval(timer);
        }
      }, 1000);
      
      alert('验证码已发送到您的手机');
    },
    goToHome() {
      this.$router.push('/');
    },
    goToLogin() {
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.register-page {
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

.register-container {
  display: flex;
  max-width: 1000px;
  margin: 30px auto;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  overflow: hidden;
}

.register-form {
  flex: 1;
  padding: 40px;
  border-right: 1px solid #f0f0f0;
}

.register-title {
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

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #d81b60;
}

.verification-code {
  display: flex;
  gap: 10px;
}

.verification-code input {
  flex: 1;
}

.send-code-btn {
  padding: 12px 15px;
  background: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s;
}

.send-code-btn:hover:not(:disabled) {
  background: #e0e0e0;
}

.send-code-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-options {
  margin-bottom: 20px;
}

.agree-terms {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.agree-terms a {
  color: #d81b60;
  text-decoration: none;
}

.agree-terms a:hover {
  text-decoration: underline;
}

.register-btn {
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
  margin-bottom: 20px;
}

.register-btn:hover {
  background: #c2185b;
  transform: translateY(-2px);
}

.login-link {
  text-align: center;
  color: #666;
}

.login-link a {
  color: #d81b60;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}

.register-info {
  flex: 1;
  padding: 40px;
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.register-info h3 {
  font-size: 24px;
  color: #d81b60;
  margin-bottom: 20px;
}

.register-info p {
  color: #666;
  margin-bottom: 20px;
  line-height: 1.6;
}

.register-info ul {
  list-style: none;
  margin-bottom: 30px;
}

.register-info li {
  padding: 8px 0;
  color: #555;
}

.register-info li i {
  color: #d81b60;
  margin-right: 10px;
}

.features-highlight {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 15px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
}

.feature-item i {
  color: #d81b60;
  font-size: 18px;
}

@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
    margin: 20px;
  }
  
  .register-form {
    border-right: none;
    border-bottom: 1px solid #f0f0f0;
  }
}
</style>