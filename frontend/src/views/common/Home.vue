<!-- src/views/common/Home.vue -->
<template>
  <div class="home-page">
    <!-- 顶部导航栏 -->
    <div class="header-container">
      <!-- 第一行：品牌标志和用户功能 -->
      <div class="header-top">
        <div class="logo">
          <span class="logo-main">上大学</span>
          <span class="logo-sub">Online</span>
          <!-- 新增：当前登录高校显示 -->
          <span class="current-school" id="currentSchool">北京大学</span>
        </div>
        <div class="user-actions">
          <div class="search-bar">
            <i class="fas fa-search"></i>
            <input type="text" placeholder="搜索校园服务...">
          </div>
          <div class="user-login" @mouseenter="showDropdown = true" @mouseleave="showDropdown = false">
            <div class="user-icon">
              <i class="fas fa-user"></i>
            </div>
            <span id="userNameDisplay">{{ user.name || '登录/注册' }}</span>
            <!-- 新增：用户角色标签 -->
            <span class="user-role-tag" :style="{display: user.role !== 'tourist' ? 'inline' : 'none'}">
              {{ roleLabels[user.role] }}
            </span>
            <div class="user-dropdown" v-show="showDropdown">
              <a href="#" @click.prevent="goToLogin"><i class="fas fa-sign-in-alt"></i> 登录</a>
              <a href="#" @click.prevent="goToRegister"><i class="fas fa-user-plus"></i> 注册</a>
            </div>
          </div>
        </div>
      </div>

      <!-- 第二行：主导航菜单 -->
      <div class="header-bottom">
        <nav class="main-nav">
          <a href="#" @click.prevent="goToHome" data-role="all">首页</a>
          <a href="#" @click.prevent="goToEcard" data-role="student,teacher,university">校园E卡通</a>
          <a href="#" @click.prevent="goToMarket" data-role="student,teacher,merchant">二手交易</a>
          <a href="#" @click.prevent="goToChef" data-role="student,teacher,merchant">校园私厨</a>
          <a href="#" @click.prevent="goToSchedule" data-role="student,teacher">行程管理</a>
          <a href="#" @click.prevent="goToDownload" data-role="all">客户端下载</a>
          <a href="#" @click.prevent="goToHelp" data-role="all">帮助中心</a>
          <a href="#" @click.prevent="goToAdmin" data-role="admin,university">管理中心</a>
        </nav>
      </div>
    </div>

    <div class="main-container">
      <!-- 轮播图区域 -->
      <section class="carousel-section">
        <div class="carousel-slide active"
          :style="{backgroundImage: 'url(https://source.unsplash.com/random/1200x400/?university,campus)'}">
          <div class="slide-content">
            <h2 class="slide-title">打造智慧校园新生态</h2>
            <p class="slide-text">连接校园生活每一刻，提供安全、便捷、个性化的一站式服务</p>
            <a href="#" class="btn" style="margin-top: 20px;" @click.prevent="goToLogin">立即体验</a>
          </div>
        </div>
        <div class="carousel-slide"
          :style="{backgroundImage: 'url(https://source.unsplash.com/random/1200x400/?student,study)'}">
          <div class="slide-content">
            <h2 class="slide-title">一校一集合，一人一身份</h2>
            <p class="slide-text">基于高校官方认证，构建精准用户群体，实现个性化服务推送</p>
          </div>
        </div>
        <div class="carousel-slide"
          :style="{backgroundImage: 'url(https://source.unsplash.com/random/1200x400/?community,network)'}">
          <div class="slide-content">
            <h2 class="slide-title">一站全服务，一策保安全</h2>
            <p class="slide-text">整合校园各类服务，建立健全安全机制，让校园生活更安心</p>
          </div>
        </div>
        <button class="carousel-arrow prev" @click="prevSlide"><i class="fas fa-chevron-left"></i></button>
        <button class="carousel-arrow next" @click="nextSlide"><i class="fas fa-chevron-right"></i></button>
        <div class="carousel-indicators">
          <div class="indicator" :class="{active: currentSlide === 0}" @click="goToSlide(0)"></div>
          <div class="indicator" :class="{active: currentSlide === 1}" @click="goToSlide(1)"></div>
          <div class="indicator" :class="{active: currentSlide === 2}" @click="goToSlide(2)"></div>
        </div>
      </section>

      <!-- 服务网格 -->
      <section class="services-grid">
        <div class="service-card e-card" data-role="student,teacher,university">
          <div class="service-icon">
            <i class="fas fa-id-card-alt"></i>
          </div>
          <h3 class="service-title">校园E卡通</h3>
          <p class="service-desc">动态学生码消费，对接校内门禁、图书馆等系统，实现无卡化校园生活。</p>
        </div>
        <div class="service-card market" data-role="student,teacher,merchant">
          <div class="service-icon">
            <i class="fas fa-shopping-bag"></i>
          </div>
          <h3 class="service-title">高校专属二手交易</h3>
          <p class="service-desc">同校精准匹配，担保交易，物品成色标注，打造安全可信的校内交易市场。</p>
        </div>
        <div class="service-card chef" data-role="student,teacher,merchant">
          <div class="service-icon">
            <i class="fas fa-utensils"></i>
          </div>
          <h3 class="service-title">规范化校园私厨</h3>
          <p class="service-desc">严格资质审核，健康证智能校验，动态监管，保障师生饮食安全与多样化选择。</p>
        </div>
        <div class="service-card schedule" data-role="student,teacher">
          <div class="service-icon">
            <i class="fas fa-calendar-alt"></i>
          </div>
          <h3 class="service-title">校园行程管理</h3>
          <p class="service-desc">课程表自动导入，多设备同步，智能提醒，整合校园活动与个人事务。</p>
        </div>
      </section>

      <!-- 用户类型卡片 -->
      <section class="user-types">
        <div class="user-card student" :class="{'active-user-card': user.role === 'student'}" data-role="student" @click="switchRole('student')">
          <div class="user-icon-large">
            <i class="fas fa-user-graduate"></i>
          </div>
          <h3 class="user-title">学生</h3>
          <p class="user-desc">学号+教务系统验证码双因素认证，畅享校园全功能服务。</p>
        </div>
        <div class="user-card teacher" :class="{'active-user-card': user.role === 'teacher'}" data-role="teacher" @click="switchRole('teacher')">
          <div class="user-icon-large">
            <i class="fas fa-user-tie"></i>
          </div>
          <h3 class="user-title">教师</h3>
          <p class="user-desc">教学管理与校园生活一体化，提升工作与生活效率。</p>
        </div>
        <div class="user-card tourist" :class="{'active-user-card': user.role === 'tourist'}" data-role="tourist" @click="switchRole('tourist')">
          <div class="user-icon-large">
            <i class="fas fa-user-clock"></i>
          </div>
          <h3 class="user-title">游客</h3>
          <p class="user-desc">刷脸活体检测+进校登记，获取基础校园服务与导览信息。</p>
        </div>
        <div class="user-card university" :class="{'active-user-card': user.role === 'university'}" data-role="university" @click="switchRole('university')">
          <div class="user-icon-large">
            <i class="fas fa-university"></i>
          </div>
          <h3 class="user-title">高校</h3>
          <p class="user-desc">官方接入与管理，定制化功能配置，实现数字化校园治理。</p>
        </div>
        <div class="user-card merchant" :class="{'active-user-card': user.role === 'merchant'}" data-role="merchant" @click="switchRole('merchant')">
          <div class="user-icon-large">
            <i class="fas fa-store"></i>
          </div>
          <h3 class="user-title">兼职商家</h3>
          <p class="user-desc">资质核验与保证金保障，安全合规地开展校园商业活动。</p>
        </div>
        <div class="user-card admin" :class="{'active-user-card': user.role === 'admin'}" data-role="admin" @click="switchRole('admin')">
          <div class="user-icon-large">
            <i class="fas fa-user-shield"></i>
          </div>
          <h3 class="user-title">管理员</h3>
          <p class="user-desc">系统全局配置与用户管理，保障平台稳定、安全运行。</p>
        </div>
      </section>

      <!-- 特色优势区域 -->
      <section class="features-section">
        <h2 class="section-title">平台核心优势</h2>
        <div class="features-grid">
          <div class="feature-item security">
            <i class="fas fa-shield-alt"></i>
            <div class="feature-content">
              <h3>信息安全保障</h3>
              <p>采用加密传输技术存储个人信息，定位权限分级调用，交易记录AES加密存储，确保用户信息安全。</p>
            </div>
          </div>
          <div class="feature-item official">
            <i class="fas fa-university"></i>
            <div class="feature-content">
              <h3>高校官方接入</h3>
              <p>高校通过后台系统注册，上传校园核心信息，实现"一校一集合、一人一身份"的精准服务。</p>
            </div>
          </div>
          <div class="feature-item efficient">
            <i class="fas fa-bolt"></i>
            <div class="feature-content">
              <h3>高效认证流程</h3>
              <p>学生双因素认证，游客刷脸活体检测，平衡认证速度与准确性，3个工作日内功能解锁审核。</p>
            </div>
          </div>
          <div class="feature-item sync">
            <i class="fas fa-sync-alt"></i>
            <div class="feature-content">
              <h3>多设备同步</h3>
              <p>云同步技术实现手机、电脑端实时更新，确保用户在不同设备上查看最新行程和消息。</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 平台介绍 -->
      <section class="platform-intro">
        <div class="intro-content">
          <h2 class="section-title">关于上大学Online</h2>
          <p>"上大学Online"是一款以"高校官方注册封装用户群体"为核心的校园综合服务平台，针对当前高校校园服务存在的功能分散、用户边界模糊、信息流通低效、安全信任缺失四大痛点，提供全方位的解决方案。</p>
          <p class="vision-highlight">我们的愿景是：实现"一校一集合、一人一身份、一站全服务、一策保安全"。</p>
          <p>平台采用"基础功能+高校定制功能"的模块化设计，整合多源融合定位、支付接口对接、精准信息推送、交易流程保障等先进技术，为用户提供安全、便捷、精准、个性化的校园服务体验。</p>
        </div>
      </section>

      <!-- 动态区域 -->
      <section class="news-section">
        <h2 class="section-title">平台动态</h2>
        <div class="news-container">
          <div class="news-image">
            <i class="fas fa-bullhorn"></i>
          </div>
          <div class="news-content">
            <h3 class="news-title">最新公告</h3>
            <div class="news-list">
              <div class="news-item">
                <a href="#">【新功能上线】校园E卡通人脸支付功能正式推出！</a>
              </div>
              <div class="news-item">
                <a href="#">【高校合作】恭喜北京大学、清华大学成为首批官方接入院校。</a>
              </div>
              <div class="news-item">
                <a href="#">【安全提示】二手交易防诈骗指南，请各位用户仔细阅读。</a>
              </div>
              <div class="news-item">
                <a href="#">【商户招募】校园私厨商家入驻通道限时开放，享专属扶持计划。</a>
              </div>
              <div class="news-item">
                <a href="#">【用户调研】关于提升平台体验的问卷调查，期待您的参与。</a>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <div class="footer-logo">上大学Online</div>
          <p class="contact-info">我们致力于构建"高校官方封装+身份精准认证+技术模块联动+安全机制兜底"的校园服务生态，为高校师生提供安全、便捷、精准、个性化的校园服务体验。</p>
        </div>
        <div class="footer-section">
          <h3>联系我们</h3>
          <div class="footer-links">
            <p class="contact-info">服务热线：400-123-4567</p>
            <p class="contact-info">技术支持：tech@campus.edu.cn</p>
            <p class="contact-info">商务合作：business@campus.edu.cn</p>
            <p class="contact-info">高校接入：university@campus.edu.cn</p>
          </div>
        </div>
        <div class="footer-section">
          <h3>快速链接</h3>
          <div class="footer-links">
            <a href="#">平台介绍</a>
            <a href="#">高校接入申请</a>
            <a href="#">用户使用指南</a>
            <a href="#">隐私政策</a>
            <a href="#">服务协议</a>
            <a href="#">问题反馈</a>
          </div>
        </div>
      </div>
      <div class="copyright">
        © 2024 上大学Online校园综合服务平台 版权所有 | 让校园生活更简单
      </div>
    </footer>

    <!-- 悬浮菜单 -->
    <div class="floating-menu">
      <div class="floating-content" :class="{active: showFloatingMenu}" id="floatingContent">
        <a href="#" @click.prevent="goToHome"><i class="fas fa-home"></i> 首页</a>
        <a href="#" @click.prevent="goToProfile"><i class="fas fa-user"></i> 个人中心</a>
        <a href="#" @click.prevent="goToSettings"><i class="fas fa-cog"></i> 设置</a>
        <a href="#" @click.prevent="goToHelp"><i class="fas fa-question-circle"></i> 帮助</a>
        <a href="#" @click.prevent="logout"><i class="fas fa-sign-out-alt"></i> 退出登录</a>
      </div>
      <div class="floating-btn" id="floatingBtn" @click="toggleFloatingMenu">
        <i class="fas fa-bars"></i>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage',
  data() {
    return {
      currentSlide: 0,
      slideInterval: null,
      showDropdown: false,
      showFloatingMenu: false,
      user: {
        name: '',
        role: 'tourist'
      },
      roleLabels: {
        student: '学生',
        teacher: '教师',
        tourist: '游客',
        university: '高校管理员',
        merchant: '商家',
        admin: '系统管理员'
      },
      slides: [
        {
          title: '打造智慧校园新生态',
          text: '连接校园生活每一刻，提供安全、便捷、个性化的一站式服务',
          bgImage: 'https://source.unsplash.com/random/1200x400/?university,campus'
        },
        {
          title: '一校一集合，一人一身份',
          text: '基于高校官方认证，构建精准用户群体，实现个性化服务推送',
          bgImage: 'https://source.unsplash.com/random/1200x400/?student,study'
        },
        {
          title: '一站全服务，一策保安全',
          text: '整合校园各类服务，建立健全安全机制，让校园生活更安心',
          bgImage: 'https://source.unsplash.com/random/1200x400/?community,network'
        }
      ]
    }
  },
  mounted() {
    this.initCarousel();
    this.loadUserData();
  },
  beforeUnmount() {
    if (this.slideInterval) {
      clearInterval(this.slideInterval);
    }
  },
  methods: {
    initCarousel() {
      this.slideInterval = setInterval(this.nextSlide, 5000);
    },
    nextSlide() {
      this.currentSlide = (this.currentSlide + 1) % this.slides.length;
    },
    prevSlide() {
      this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length;
    },
    goToSlide(index) {
      this.currentSlide = index;
    },
    toggleFloatingMenu() {
      this.showFloatingMenu = !this.showFloatingMenu;
    },
    loadUserData() {
      // 模拟从本地存储加载用户数据
      const savedUser = localStorage.getItem('user');
      if (savedUser) {
        this.user = JSON.parse(savedUser);
      }
    },
    switchRole(role) {
      const roleNames = {
        student: '学生',
        teacher: '教师',
        tourist: '游客',
        university: '高校管理员',
        merchant: '商家',
        admin: '系统管理员'
      };
      
      this.user.role = role;
      this.user.name = roleNames[role] + ' (演示)';
      
      // 保存到本地存储
      localStorage.setItem('user', JSON.stringify(this.user));
      
      alert(`已切换至【${roleNames[role]}】角色视图！`);
    },
    logout() {
      this.user = { name: '', role: 'tourist' };
      localStorage.removeItem('user');
      this.showFloatingMenu = false;
    },
    goToLogin() {
      this.$router.push('/login');
    },
    goToRegister() {
      this.$router.push('/register');
    },
    goToHome() {
      this.$router.push('/');
    },
    goToEcard() {
      alert('即将跳转到校园E卡通页面');
    },
    goToMarket() {
      alert('即将跳转到二手交易页面');
    },
    goToChef() {
      alert('即将跳转到校园私厨页面');
    },
    goToSchedule() {
      alert('即将跳转到行程管理页面');
    },
    goToDownload() {
      alert('即将跳转到客户端下载页面');
    },
    goToHelp() {
      alert('即将跳转到帮助中心页面');
    },
    goToAdmin() {
      alert('即将跳转到管理中心页面');
    },
    goToProfile() {
      alert('即将跳转到个人中心页面');
    },
    goToSettings() {
      alert('即将跳转到设置页面');
    }
  }
}
</script>

<style scoped>
/* 这里放置原始HTML中的所有CSS样式 */
/* 由于代码长度限制，这里只展示关键部分，完整样式请参考原始HTML */

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Arial', 'Microsoft YaHei', sans-serif;
}

body {
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
  color: #333;
  line-height: 1.6;
  min-height: 100vh;
}

/* 其他样式... */

/* 响应式设计 */
@media (max-width: 768px) {
  .header-top {
    flex-direction: column;
    gap: 15px;
    padding: 15px 20px;
  }

  .current-school {
    margin-left: 0;
    margin-top: 10px;
  }

  .user-actions {
    flex-direction: column;
    width: 100%;
  }

  .search-bar {
    width: 100%;
  }

  .search-bar input {
    width: 100%;
  }

  .header-bottom {
    padding: 12px 20px;
  }

  .main-nav {
    flex-wrap: wrap;
    gap: 15px;
  }

  .carousel-section {
    height: 300px;
  }

  .slide-title {
    font-size: 24px;
  }

  .slide-text {
    font-size: 16px;
  }

  .services-grid {
    flex-direction: column;
    align-items: center;
  }

  .service-card {
    width: 100%;
  }

  .user-types {
    flex-direction: column;
  }

  .news-container {
    flex-direction: column;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .features-section,
  .platform-intro,
  .news-section {
    padding: 30px 20px;
  }
}

@media (max-width: 480px) {
  .main-nav {
    gap: 10px;
  }

  .main-nav a {
    font-size: 14px;
  }

  .logo-main {
    font-size: 24px;
  }

  .logo-sub {
    font-size: 14px;
  }
}
</style>