<template>
  <div class="client-download-page">
    <LoginNavBar />

    <div class="page-container">
      <!-- Hero区域 -->
      <section class="hero-section">
        <div class="hero-content">
          <div class="hero-icon">
            <i class="fas fa-desktop"></i>
          </div>
          <h1 class="hero-title">电脑客户端下载</h1>
          <p class="hero-subtitle">大屏体验，更高效便捷的校园服务</p>
        </div>
      </section>

      <!-- 下载区域 -->
      <section class="download-section">
        <div class="download-cards">
          <!-- Windows下载 -->
          <div class="download-card windows-card">
            <div class="card-header">
              <div class="platform-icon">
                <i class="fab fa-windows"></i>
              </div>
              <h3 class="platform-name">Windows版本</h3>
              <span class="platform-version">v2.0.1</span>
            </div>
            <div class="card-content">
              <p class="card-desc">支持 Windows 7/8/10/11</p>
              <div class="file-info">
                <div class="info-item">
                  <i class="fas fa-file-archive"></i>
                  <span>安装包大小：45.2 MB</span>
                </div>
                <div class="info-item">
                  <i class="fas fa-calendar"></i>
                  <span>更新时间：2024-01-15</span>
                </div>
              </div>
              <button class="btn-download" @click="downloadWindows">
                <i class="fab fa-windows"></i>
                立即下载
              </button>
            </div>
          </div>

          <!-- macOS下载 -->
          <div class="download-card macos-card">
            <div class="card-header">
              <div class="platform-icon">
                <i class="fab fa-apple"></i>
              </div>
              <h3 class="platform-name">macOS版本</h3>
              <span class="platform-version">v2.0.1</span>
            </div>
            <div class="card-content">
              <p class="card-desc">支持 macOS 10.13 及以上版本</p>
              <div class="file-info">
                <div class="info-item">
                  <i class="fas fa-file-archive"></i>
                  <span>安装包大小：52.8 MB</span>
                </div>
                <div class="info-item">
                  <i class="fas fa-calendar"></i>
                  <span>更新时间：2024-01-15</span>
                </div>
              </div>
              <button class="btn-download" @click="downloadMacOS">
                <i class="fab fa-apple"></i>
                立即下载
              </button>
            </div>
          </div>

          <!-- Linux下载 -->
          <div class="download-card linux-card">
            <div class="card-header">
              <div class="platform-icon">
                <i class="fab fa-linux"></i>
              </div>
              <h3 class="platform-name">Linux版本</h3>
              <span class="platform-version">v2.0.1</span>
            </div>
            <div class="card-content">
              <p class="card-desc">支持 Ubuntu 18.04+ / CentOS 7+</p>
              <div class="file-info">
                <div class="info-item">
                  <i class="fas fa-file-archive"></i>
                  <span>安装包大小：48.5 MB</span>
                </div>
                <div class="info-item">
                  <i class="fas fa-calendar"></i>
                  <span>更新时间：2024-01-15</span>
                </div>
              </div>
              <button class="btn-download" @click="downloadLinux">
                <i class="fab fa-linux"></i>
                立即下载
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- 功能特色 -->
      <section class="features-section">
        <h2 class="section-title">客户端功能特色</h2>
        <div class="features-grid">
          <div class="feature-item">
            <div class="feature-icon">
              <i class="fas fa-tachometer-alt"></i>
            </div>
            <h3 class="feature-title">高性能</h3>
            <p class="feature-desc">原生应用，运行流畅，响应迅速</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <i class="fas fa-shield-alt"></i>
            </div>
            <h3 class="feature-title">安全可靠</h3>
            <p class="feature-desc">本地数据加密，保护您的隐私安全</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <i class="fas fa-sync-alt"></i>
            </div>
            <h3 class="feature-title">数据同步</h3>
            <p class="feature-desc">与手机App数据实时同步，无缝切换</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <i class="fas fa-keyboard"></i>
            </div>
            <h3 class="feature-title">快捷键支持</h3>
            <p class="feature-desc">丰富的快捷键，提升操作效率</p>
          </div>
        </div>
      </section>

      <!-- 使用说明 -->
      <section class="instructions-section">
        <h2 class="section-title">安装使用说明</h2>
        <div class="instructions-tabs">
          <button
            v-for="(tab, index) in tabs"
            :key="index"
            :class="['tab-btn', { active: activeTab === index }]"
            @click="activeTab = index"
          >
            {{ tab.name }}
          </button>
        </div>
        <div class="instructions-content">
          <div v-show="activeTab === 0" class="instruction-content">
            <h3>Windows安装步骤</h3>
            <ol>
              <li>下载Windows安装包（.exe文件）</li>
              <li>双击运行安装程序</li>
              <li>按照提示完成安装（建议选择默认安装路径）</li>
              <li>安装完成后，在桌面或开始菜单找到应用图标</li>
              <li>双击启动应用，使用账号登录即可使用</li>
            </ol>
          </div>
          <div v-show="activeTab === 1" class="instruction-content">
            <h3>macOS安装步骤</h3>
            <ol>
              <li>下载macOS安装包（.dmg文件）</li>
              <li>双击打开.dmg文件</li>
              <li>将应用拖拽到"应用程序"文件夹</li>
              <li>首次运行可能需要在"系统偏好设置 > 安全性与隐私"中允许运行</li>
              <li>启动应用，使用账号登录即可使用</li>
            </ol>
          </div>
          <div v-show="activeTab === 2" class="instruction-content">
            <h3>Linux安装步骤</h3>
            <ol>
              <li>下载对应Linux发行版的安装包</li>
              <li>Ubuntu/Debian系统：使用 <code>sudo dpkg -i package.deb</code> 安装</li>
              <li>CentOS/RHEL系统：使用 <code>sudo rpm -i package.rpm</code> 安装</li>
              <li>或使用包管理器：<code>sudo apt install ./package.deb</code></li>
              <li>安装完成后，在应用菜单中找到并启动应用</li>
            </ol>
          </div>
        </div>
      </section>

      <!-- 常见问题 -->
      <section class="faq-section">
        <h2 class="section-title">常见问题</h2>
        <div class="faq-list">
          <div class="faq-item" v-for="(faq, index) in faqs" :key="index">
            <div class="faq-question" @click="toggleFaq(index)">
              <span>{{ faq.question }}</span>
              <i :class="['fas', faq.open ? 'fa-chevron-up' : 'fa-chevron-down']"></i>
            </div>
            <div class="faq-answer" v-show="faq.open">
              <p>{{ faq.answer }}</p>
            </div>
          </div>
        </div>
      </section>
    </div>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import LoginNavBar from '@/components/common/LoginNavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

// 标签页
const tabs = [
  { name: 'Windows' },
  { name: 'macOS' },
  { name: 'Linux' }
]
const activeTab = ref(0)

// 常见问题数据
const faqs = ref([
  {
    question: '客户端支持哪些操作系统？',
    answer: '目前支持Windows 7/8/10/11、macOS 10.13+以及主流Linux发行版（Ubuntu 18.04+、CentOS 7+等）。',
    open: false
  },
  {
    question: '安装时提示权限不足怎么办？',
    answer: 'Windows系统请右键选择"以管理员身份运行"；macOS系统需要在系统偏好设置中允许运行；Linux系统需要使用sudo权限安装。',
    open: false
  },
  {
    question: '客户端和网页版有什么区别？',
    answer: '客户端是原生应用，性能更好，支持离线功能，数据同步更快。网页版无需安装，但功能相对有限。',
    open: false
  },
  {
    question: '如何卸载客户端？',
    answer: 'Windows系统：在控制面板的程序和功能中卸载；macOS系统：直接将应用拖到废纸篓；Linux系统：使用对应的包管理器卸载。',
    open: false
  }
])

// 切换FAQ展开/收起
const toggleFaq = (index: number) => {
  faqs.value[index].open = !faqs.value[index].open
}

// 下载Windows版本
const downloadWindows = () => {
  try {
    // Windows下载链接（实际使用时替换为真实链接）
    const windowsUrl = 'https://download.example.com/windows/app.exe'
    window.open(windowsUrl, '_blank')
  } catch (error) {
    console.error('Windows下载失败:', error)
  }
}

// 下载macOS版本
const downloadMacOS = () => {
  try {
    // macOS下载链接（实际使用时替换为真实链接）
    const macosUrl = 'https://download.example.com/macos/app.dmg'
    window.open(macosUrl, '_blank')
  } catch (error) {
    console.error('macOS下载失败:', error)
  }
}

// 下载Linux版本
const downloadLinux = () => {
  try {
    // Linux下载链接（实际使用时替换为真实链接）
    const linuxUrl = 'https://download.example.com/linux/app.deb'
    window.open(linuxUrl, '_blank')
  } catch (error) {
    console.error('Linux下载失败:', error)
  }
}
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --primary-dark: #c2185b;
  --text: #333;
  --muted: #666;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.client-download-page {
  min-height: 100vh;
  background: var(--bg);
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* Hero区域 */
.hero-section {
  text-align: center;
  padding: 60px 20px;
  margin-bottom: 40px;
}

.hero-icon {
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 30px;
  box-shadow: 0 10px 30px rgba(216, 27, 96, 0.2);
}

.hero-icon i {
  font-size: 60px;
  color: white;
}

.hero-title {
  font-size: 42px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 15px;
}

.hero-subtitle {
  font-size: 18px;
  color: var(--muted);
}

/* 下载区域 */
.download-section {
  margin-bottom: 60px;
}

.download-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.download-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid #eee;
}

.download-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(216, 27, 96, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.platform-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.windows-card .platform-icon {
  background: #0078d4;
  color: white;
}

.macos-card .platform-icon {
  background: #000;
  color: white;
}

.linux-card .platform-icon {
  background: #fcc624;
  color: #333;
}

.platform-name {
  flex: 1;
  font-size: 24px;
  font-weight: bold;
  color: var(--text);
}

.platform-version {
  font-size: 14px;
  color: var(--muted);
  background: #f5f5f5;
  padding: 4px 12px;
  border-radius: 12px;
}

.card-content {
  text-align: center;
}

.card-desc {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 20px;
}

.file-info {
  margin: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  color: var(--muted);
}

.info-item i {
  color: var(--primary);
}

.btn-download {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
}

.btn-download:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(216, 27, 96, 0.3);
}

/* 功能特色 */
.features-section {
  margin-bottom: 60px;
}

.section-title {
  font-size: 32px;
  font-weight: bold;
  text-align: center;
  color: var(--text);
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-item {
  background: white;
  border-radius: 16px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 1px solid #eee;
}

.feature-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(216, 27, 96, 0.15);
}

.feature-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.feature-icon i {
  font-size: 32px;
  color: white;
}

.feature-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 10px;
}

.feature-desc {
  font-size: 14px;
  color: var(--muted);
  line-height: 1.6;
}

/* 使用说明 */
.instructions-section {
  margin-bottom: 60px;
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  border: 1px solid #eee;
}

.instructions-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
}

.tab-btn {
  padding: 12px 24px;
  background: transparent;
  border: none;
  border-bottom: 3px solid transparent;
  font-size: 16px;
  font-weight: 600;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  color: var(--primary);
}

.tab-btn.active {
  color: var(--primary);
  border-bottom-color: var(--primary);
}

.instructions-content {
  padding-top: 20px;
}

.instruction-content h3 {
  font-size: 24px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 20px;
}

.instruction-content ol {
  padding-left: 20px;
}

.instruction-content li {
  font-size: 16px;
  color: var(--muted);
  line-height: 2;
  margin-bottom: 10px;
}

.instruction-content code {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  color: var(--primary);
}

/* 常见问题 */
.faq-section {
  margin-bottom: 40px;
}

.faq-list {
  max-width: 800px;
  margin: 0 auto;
}

.faq-item {
  background: white;
  border-radius: 12px;
  margin-bottom: 15px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  border: 1px solid #eee;
}

.faq-question {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.faq-question:hover {
  background: #f9f0ff;
}

.faq-question span {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
}

.faq-question i {
  color: var(--primary);
  transition: transform 0.3s ease;
}

.faq-answer {
  padding: 0 20px 20px;
  color: var(--muted);
  line-height: 1.6;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }

  .download-cards {
    grid-template-columns: 1fr;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .instructions-section {
    padding: 30px 20px;
  }

  .instructions-tabs {
    flex-wrap: wrap;
  }
}
</style>
