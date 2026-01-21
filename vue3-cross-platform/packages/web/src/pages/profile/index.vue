<template>
  <div class="profile-page">
    <NavBar />

    <div class="page-container">
      <!-- 左侧导航菜单 -->
      <aside class="sidebar">
      <!-- 用户信息卡片 -->
        <div class="user-card">
          <div class="user-avatar-wrapper" @click="handleEditAvatar">
            <img v-if="userAvatar" :src="userAvatar" alt="用户头像" class="user-avatar-img" />
            <div v-else class="user-avatar">
              <i class="fas fa-user"></i>
            </div>
            <div class="avatar-badge">
              <i class="fas fa-camera"></i>
            </div>
          </div>
          <h2 class="user-name">{{ userInfo.name || userStore.username || '用户' }}</h2>
          <p class="user-role">{{ roleLabel }}</p>
          <p class="user-school" v-if="userInfo.school || userStore.userInfo?.campusName">
            {{ userInfo.school || userStore.userInfo?.campusName }}
          </p>
          <div class="user-stats">
            <div class="stat-item" @click="handleViewFavorites">
              <span class="stat-value">{{ favoriteCount }}</span>
              <span class="stat-label">收藏</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item" @click="handleViewHistory">
              <span class="stat-value">{{ historyCount }}</span>
              <span class="stat-label">历史</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item" @click="handleMyPublish">
              <span class="stat-value">{{ publishCount }}</span>
              <span class="stat-label">发布</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item" @click="handleMyWallet">
              <span class="stat-value">{{ points }}</span>
              <span class="stat-label">积分</span>
            </div>
          </div>
        </div>

        <!-- 导航菜单 -->
        <nav class="sidebar-nav">
          <div
            v-for="item in menuItems"
            :key="item.key"
            class="nav-item"
            :class="{ active: activeMenu === item.key }"
            @click="switchMenu(item.key)"
          >
            <i :class="item.icon"></i>
            <span>{{ item.label }}</span>
            </div>
        </nav>
      </aside>

      <!-- 右侧内容区域 -->
      <main class="content-area">
        <!-- 个人信息概览 -->
        <div v-if="activeMenu === 'overview'" class="content-section">
          <div class="section-header">
            <h2>个人信息概览</h2>
            <div class="header-actions">
              <button class="action-btn" @click="handleSettings">
                <i class="fas fa-cog"></i>
              </button>
              <button class="action-btn" @click="handleMessages">
                <i class="fas fa-bell"></i>
                <span v-if="messageCount > 0" class="badge">{{ messageCount > 99 ? '99+' : messageCount }}</span>
              </button>
              <button class="btn-edit" @click="editProfile">
                <i class="fas fa-edit"></i> 编辑资料
              </button>
            </div>
          </div>

          <!-- 快捷功能网格（参考app端） -->
          <div class="quick-actions-grid">
            <div class="quick-action-item" @click="handleMyOrders">
              <div class="quick-icon-wrapper orders">
                <i class="fas fa-shopping-bag"></i>
              </div>
              <span class="quick-label">我的订单</span>
              <span v-if="pendingOrders > 0" class="quick-badge">{{ pendingOrders > 9 ? '9+' : pendingOrders }}</span>
            </div>
            <div class="quick-action-item" @click="handleMyFavorites">
              <div class="quick-icon-wrapper favorites">
                <i class="fas fa-heart"></i>
              </div>
              <span class="quick-label">我的收藏</span>
            </div>
            <div class="quick-action-item" @click="handleMyPublish">
              <div class="quick-icon-wrapper publish">
                <i class="fas fa-file-alt"></i>
              </div>
              <span class="quick-label">我的发布</span>
            </div>
            <div class="quick-action-item" @click="handleMyWallet">
              <div class="quick-icon-wrapper wallet">
                <i class="fas fa-wallet"></i>
              </div>
              <span class="quick-label">我的钱包</span>
            </div>
          </div>

          <!-- 毕业提醒：引导即将毕业的学生去重新发起学生身份认证 -->
          <div class="alert-box" v-if="userInfo.role === 'student'">
            <div class="alert-content">
              <i class="fas fa-exclamation-circle"></i>
              <div class="alert-text">
                <strong>毕业提醒</strong>
                <p>您的学籍即将到期，学号将无法修改。如需升学或变更学籍信息，请及时申请重新认证。</p>
          </div>
            </div>
            <div class="alert-actions">
              <!-- 直接跳转到“学生身份认证申请”专用页面，便于一键发起申请 -->
              <button class="btn-primary" @click="goToPage('/auth/student-apply')">申请重新认证</button>
              <button class="btn-secondary" @click="dismissAlert">稍后提醒</button>
            </div>
          </div>

          <!-- 基本信息 -->
          <div class="info-card">
            <h3 class="card-title">
              <i class="fas fa-id-card"></i> 基本信息
            </h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">姓名</span>
                <span class="info-value">{{ userInfo.name || '未设置' }}</span>
            </div>
              <div class="info-item">
                <span class="info-label">学号/工号</span>
                <span class="info-value">{{ userInfo.studentId || '未设置' }}</span>
            </div>
              <div class="info-item">
                <span class="info-label">身份类型</span>
                <span class="info-value">{{ roleLabel }}</span>
          </div>
              <div class="info-item">
                <span class="info-label">所属高校</span>
                <span class="info-value">{{ userInfo.school || '未设置' }}</span>
              </div>
              <div class="info-item" v-if="userInfo.role === 'student'">
                <span class="info-label">院系专业</span>
                <span class="info-value">{{ userInfo.major || '未设置' }}</span>
              </div>
              <div class="info-item" v-if="userInfo.role === 'student'">
                <span class="info-label">入学年份</span>
                <span class="info-value">{{ userInfo.enrollmentYear || '未设置' }}</span>
              </div>
              <div class="info-item" v-if="userInfo.role === 'student'">
                <span class="info-label">预计毕业时间</span>
                <span class="info-value">{{ userInfo.graduationDate || '未设置' }}</span>
              </div>
              <div class="info-item" v-if="userInfo.role === 'student'">
                <span class="info-label">学籍状态</span>
                <span class="info-value">
                  <span class="status-badge status-active">在读</span>
                  <span class="status-badge status-normal">正常</span>
                </span>
              </div>
            </div>
          </div>

          <!-- 账户状态 -->
          <div class="info-card">
            <h3 class="card-title">
              <i class="fas fa-shield-alt"></i> 账户状态
            </h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">身份认证</span>
                <span class="info-value">
                  <span class="status-badge status-verified">已认证</span>
                  <span class="status-badge status-checked">已验证</span>
                </span>
              </div>
              <div class="info-item" v-if="userInfo.role === 'student' || userInfo.role === 'teacher'">
                <span class="info-label">E卡通状态</span>
                <span class="info-value">
                  <span class="status-badge status-active">正常使用</span>
                  <span class="status-badge status-checked">已激活</span>
                </span>
              </div>
              <div class="info-item">
                <span class="info-label">账户安全等级</span>
                <span class="info-value">
                  <span class="status-badge status-high">高</span>
                  <span class="status-badge status-safe">安全</span>
                </span>
              </div>
              <div class="info-item">
                <span class="info-label">信用积分</span>
                <span class="info-value points">{{ creditScore }}/100</span>
              </div>
            </div>
          </div>

          <!-- 快捷功能 -->
          <div class="info-card">
            <h3 class="card-title">
              <i class="fas fa-bolt"></i> 快捷功能
            </h3>
            <div class="quick-actions">
              <div class="quick-action-item" @click="goToPage('/ecard')" v-if="userInfo.role === 'student' || userInfo.role === 'teacher'">
                <i class="fas fa-qrcode"></i>
                <div class="action-content">
                  <h4>校园码</h4>
                  <p>出示二维码用于校园消费、门禁等</p>
                </div>
              </div>
              <div class="quick-action-item" @click="goToPage('/schedule')" v-if="userInfo.role === 'student' || userInfo.role === 'teacher'">
                <i class="fas fa-calendar-alt"></i>
                <div class="action-content">
                  <h4>课程表</h4>
                  <p>查看今日课程与上课地点</p>
                </div>
              </div>
              <div class="quick-action-item" @click="goToPage('/ecard')" v-if="userInfo.role === 'student' || userInfo.role === 'teacher'">
                <i class="fas fa-wallet"></i>
                <div class="action-content">
                  <h4>余额充值</h4>
                  <p>为校园卡、钱包等账户充值</p>
                </div>
              </div>
              <div class="quick-action-item" @click="goToPage('/ecard')" v-if="userInfo.role === 'student' || userInfo.role === 'teacher'">
                <i class="fas fa-history"></i>
                <div class="action-content">
                  <h4>消费记录</h4>
                  <p>查看最近的消费明细</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 个人资料 -->
        <div v-if="activeMenu === 'profile'" class="content-section">
          <div class="section-header">
            <h2>个人资料</h2>
            <div class="header-actions">
              <button class="btn-secondary" @click="cancelEdit">取消</button>
              <button class="btn-primary" @click="saveProfile">保存更改</button>
            </div>
          </div>
          <div class="form-card">
            <form @submit.prevent="saveProfile">
              <div class="form-group">
                <label>姓名</label>
                <input type="text" v-model="editForm.name" placeholder="请输入姓名" />
              </div>
              <div class="form-group">
                <label>学号/工号</label>
                <input type="text" v-model="editForm.studentId" placeholder="学号注册后不可直接修改，如需变更请申请重新认证" disabled />
              </div>
              <div class="form-group" v-if="userInfo.role === 'student'">
                <label>院系专业</label>
                <input type="text" v-model="editForm.major" placeholder="请输入院系专业" />
              </div>
              <div class="form-group" v-if="userInfo.role === 'student'">
                <label>入学年份</label>
                <select v-model="editForm.enrollmentYear">
                  <option value="">请选择</option>
                  <option v-for="year in years" :key="year" :value="year">{{ year }}年</option>
                </select>
              </div>
              <div class="form-group" v-if="userInfo.role === 'student'">
                <label>预计毕业时间</label>
                <input type="text" v-model="editForm.graduationDate" placeholder="根据入学年份和学制自动计算" disabled />
              </div>
              <div class="form-group">
                <label>联系电话</label>
                <input type="tel" v-model="editForm.phone" placeholder="请输入联系电话" />
              </div>
              <div class="form-group">
                <label>电子邮箱</label>
                <input type="email" v-model="editForm.email" placeholder="请输入电子邮箱" />
              </div>
            </form>
          </div>
        </div>

        <!-- 账户安全 -->
        <div v-if="activeMenu === 'security'" class="content-section">
          <div class="section-header">
            <h2>账户安全</h2>
          </div>
          <div class="info-card">
            <h3 class="card-title">安全设置</h3>
            <div class="security-list">
              <div class="security-item">
                <div class="security-info">
                  <i class="fas fa-lock"></i>
                  <div>
                    <h4>登录密码</h4>
                    <p>••••••••</p>
                  </div>
                </div>
                <button class="btn-link" @click="changePassword">修改密码</button>
              </div>
              <div class="security-item">
                <div class="security-info">
                  <i class="fas fa-mobile-alt"></i>
                  <div>
                    <h4>手机绑定</h4>
                    <p>{{ maskedPhone }}</p>
                  </div>
                </div>
                <div class="security-status">
                  <span class="status-badge status-verified">已验证</span>
                  <button class="btn-link" @click="changePhone">更换手机</button>
                </div>
              </div>
              <div class="security-item">
                <div class="security-info">
                  <i class="fas fa-envelope"></i>
                  <div>
                    <h4>邮箱绑定</h4>
                    <p>{{ userInfo.email || '未绑定' }}</p>
                  </div>
                </div>
                <div class="security-status">
                  <span class="status-badge status-verified" v-if="userInfo.email">已验证</span>
                  <button class="btn-link" @click="changeEmail">{{ userInfo.email ? '更换邮箱' : '绑定邮箱' }}</button>
                </div>
              </div>
              <div class="security-item">
                <div class="security-info">
                  <i class="fas fa-desktop"></i>
                  <div>
                    <h4>登录设备</h4>
                    <p>{{ deviceCount }}台设备</p>
                  </div>
                </div>
                <button class="btn-link" @click="manageDevices">管理设备</button>
              </div>
            </div>
          </div>
          <div class="info-card">
            <h3 class="card-title">安全提醒</h3>
            <div class="alert-box alert-info">
              <i class="fas fa-info-circle"></i>
              <div>
                <p><strong>账户安全等级：高</strong></p>
                <p>您的账户安全设置较为完善，建议定期更换密码以增强安全性。</p>
                <span class="alert-time">系统检测 今天</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 学号管理 -->
        <div v-if="activeMenu === 'studentId'" class="content-section">
          <div class="section-header">
            <h2>学号管理</h2>
            <button class="btn-primary" @click="showStudentIdModal = true">申请学号变更</button>
          </div>
          <div class="info-card">
            <h3 class="card-title">学号信息</h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">当前学号</span>
                <span class="info-value">{{ userInfo.studentId || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">学号状态</span>
                <span class="info-value">
                  <span class="status-badge status-active">有效</span>
                  <span class="status-badge status-checked">已验证</span>
                </span>
              </div>
              <div class="info-item">
                <span class="info-label">认证时间</span>
                <span class="info-value">{{ userInfo.authTime || '未认证' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">有效期至</span>
                <span class="info-value">{{ userInfo.expiryDate || '未设置' }}</span>
              </div>
            </div>
          </div>
          <div class="info-card">
            <h3 class="card-title">学号修改说明</h3>
            <div class="notice-box">
              <h4>学号修改政策</h4>
              <p>学号一经认证后通常不可修改，仅在特定情况下可申请变更：升学、转学、学籍信息更正等特殊情况。</p>
              <h4>重要提醒</h4>
              <p>大学期间学号关联所有学术记录和校园服务，请谨慎处理学号变更申请。</p>
              <h4>毕业前提醒</h4>
              <p>毕业前6个月将暂停学号变更申请，请提前规划。</p>
            </div>
          </div>
        </div>

        <!-- 我的订单 -->
        <div v-if="activeMenu === 'orders'" class="content-section">
          <div class="section-header">
            <h2>我的订单</h2>
            <div class="filter-tabs">
              <button class="tab-btn" :class="{ active: orderFilter === 'all' }" @click="orderFilter = 'all'">全部订单</button>
              <button class="tab-btn" :class="{ active: orderFilter === 'secondhand' }" @click="orderFilter = 'secondhand'">二手市场</button>
              <button class="tab-btn" :class="{ active: orderFilter === 'parttime' }" @click="orderFilter = 'parttime'">兼职收藏</button>
            </div>
          </div>
          <!-- 搜索栏 -->
          <div class="search-bar">
            <div class="search-box">
              <i class="fas fa-search search-icon"></i>
              <input
                type="text"
                v-model="orderSearchKeyword"
                placeholder="搜索订单标题、描述或订单号..."
                class="search-input"
              />
              <button v-if="orderSearchKeyword" class="search-clear" @click="orderSearchKeyword = ''">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
          <div class="order-list">
            <div v-for="order in filteredOrders" :key="order.id" class="order-item">
              <div class="order-type">{{ order.type }}</div>
              <div class="order-content">
                <h4>{{ order.title }}</h4>
                <p>{{ order.description }}</p>
                <div class="order-meta">
                  <span>订单号: {{ order.orderNo }}</span>
                  <span>{{ order.date }}</span>
                </div>
              </div>
              <button class="btn-link" @click="viewOrderDetail(order.id)">查看详情</button>
            </div>
            <div v-if="filteredOrders.length === 0" class="empty-state">
              <i class="fas fa-inbox"></i>
              <p>暂无订单</p>
            </div>
          </div>
        </div>

        <!-- 我的收藏 -->
        <div v-if="activeMenu === 'favorites'" class="content-section">
          <div class="section-header">
            <h2>我的收藏</h2>
          </div>
          <!-- 搜索栏 -->
          <div class="search-bar">
            <div class="search-box">
              <i class="fas fa-search search-icon"></i>
              <input
                type="text"
                v-model="favoriteSearchKeyword"
                placeholder="搜索收藏内容..."
                class="search-input"
              />
              <button v-if="favoriteSearchKeyword" class="search-clear" @click="favoriteSearchKeyword = ''">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
          <div class="favorite-list">
            <div v-for="item in filteredFavorites" :key="item.id" class="favorite-item">
              <div class="favorite-content">
                <h4>{{ item.title }}</h4>
                <p>{{ item.description }}</p>
                <div class="favorite-meta">
                  <span>{{ item.category }}</span>
                  <span>{{ item.date }}</span>
                  <span class="status-badge" :class="item.status === '已收藏' ? 'status-active' : ''">{{ item.status }}</span>
                </div>
              </div>
              <button class="btn-link" @click="viewFavoriteDetail(item.id)">查看详情</button>
            </div>
            <div v-if="filteredFavorites.length === 0" class="empty-state">
              <i class="fas fa-heart"></i>
              <p>您还没有任何收藏</p>
              <p class="empty-hint">在浏览商品或服务时，点击心形图标即可收藏</p>
              <button class="btn-primary" @click="goToPage('/secondhand')">去发现</button>
            </div>
            </div>
          </div>

        <!-- 浏览记录 -->
        <div v-if="activeMenu === 'history'" class="content-section">
          <div class="section-header">
            <h2>浏览记录</h2>
            <button class="btn-secondary" @click="clearHistory">清空记录</button>
          </div>
          <!-- 搜索栏 -->
          <div class="search-bar">
            <div class="search-box">
              <i class="fas fa-search search-icon"></i>
              <input
                type="text"
                v-model="historySearchKeyword"
                placeholder="搜索浏览记录..."
                class="search-input"
              />
              <button v-if="historySearchKeyword" class="search-clear" @click="historySearchKeyword = ''">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
          <div class="history-list">
            <div v-for="item in filteredHistory" :key="item.id" class="history-item">
              <div class="history-content">
                <h4>{{ item.title }}</h4>
                <p>{{ item.description }}</p>
                <div class="history-meta">
                  <span>{{ item.category }}</span>
                  <span>{{ item.time }}</span>
                </div>
              </div>
              <button class="btn-link" @click="viewHistoryDetail(item.id)">查看</button>
            </div>
            <div v-if="filteredHistory.length === 0" class="empty-state">
              <i class="fas fa-history"></i>
              <p>暂无浏览记录</p>
            </div>
            </div>
          </div>

        <!-- 通知提醒 -->
        <div v-if="activeMenu === 'messages'" class="content-section">
          <div class="section-header">
            <h2>通知提醒</h2>
            <div class="header-actions">
              <div class="filter-tabs">
                <button class="tab-btn" :class="{ active: messageFilter === 'all' }" @click="messageFilter = 'all'">全部消息</button>
                <button class="tab-btn" :class="{ active: messageFilter === 'secondhand' }" @click="messageFilter = 'secondhand'">二手交易</button>
                <button class="tab-btn" :class="{ active: messageFilter === 'parttime' }" @click="messageFilter = 'parttime'">兼职交流</button>
            </div>
              <button class="btn-secondary" @click="markAllRead">全部已读</button>
            </div>
          </div>
          <!-- 搜索栏 -->
          <div class="search-bar">
            <div class="search-box">
              <i class="fas fa-search search-icon"></i>
              <input
                type="text"
                v-model="messageSearchKeyword"
                placeholder="搜索通知标题或内容..."
                class="search-input"
              />
              <button v-if="messageSearchKeyword" class="search-clear" @click="messageSearchKeyword = ''">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
          <div class="message-list">
            <div v-for="msg in filteredMessages" :key="msg.id" class="message-item" :class="{ unread: !msg.read }">
              <div class="message-icon">
                <i :class="msg.icon"></i>
        </div>
              <div class="message-content">
                <h4>{{ msg.title }}</h4>
                <p>{{ msg.content }}</p>
                <div class="message-meta">
                  <span>{{ msg.category }}</span>
                  <span>{{ msg.time }}</span>
                </div>
              </div>
            </div>
            <div v-if="filteredMessages.length === 0" class="empty-state">
              <i class="fas fa-envelope"></i>
              <p>暂无消息</p>
            </div>
          </div>
        </div>

        <!-- 我的申请 -->
        <div v-if="activeMenu === 'applications'" class="content-section">
          <div class="section-header">
            <h2>我的申请</h2>
            <div class="filter-tabs">
              <button class="tab-btn" :class="{ active: appFilter === 'all' }" @click="appFilter = 'all'">全部申请</button>
              <button class="tab-btn" :class="{ active: appFilter === 'auth' }" @click="appFilter = 'auth'">身份认证</button>
              <button class="tab-btn" :class="{ active: appFilter === 'parttime' }" @click="appFilter = 'parttime'">兼职申请</button>
            </div>
          </div>
          <!-- 搜索栏 -->
          <div class="search-bar">
            <div class="search-box">
              <i class="fas fa-search search-icon"></i>
              <input
                type="text"
                v-model="appSearchKeyword"
                placeholder="搜索申请标题或描述..."
                class="search-input"
              />
              <button v-if="appSearchKeyword" class="search-clear" @click="appSearchKeyword = ''">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
          <div class="application-list">
            <div v-for="app in filteredApplications" :key="app.id" class="application-item">
              <div class="application-content">
                <h4>{{ app.title }}</h4>
                <p>{{ app.description }}</p>
                <div class="application-meta">
                  <span class="status-badge" :class="getStatusClass(app.status)">{{ app.status }}</span>
                  <span>{{ app.date }}</span>
                </div>
              </div>
            </div>
            <div v-if="filteredApplications.length === 0" class="empty-state">
              <i class="fas fa-file-alt"></i>
              <p>暂无申请记录</p>
            </div>
          </div>
        </div>

        <!-- 我的优惠券 -->
        <div v-if="activeMenu === 'coupons'" class="content-section">
          <div class="section-header">
            <h2>我的优惠券</h2>
            <button class="btn-primary" @click="goToPage('/coupons/receive')">领取优惠券</button>
          </div>
          <div class="coupon-list">
            <div v-for="coupon in coupons" :key="coupon.id" class="coupon-item" :class="{ used: coupon.used, expired: coupon.expired }">
              <div class="coupon-content">
                <div class="coupon-amount">
                  <span class="amount-symbol">¥</span>
                  <span class="amount-value">{{ coupon.amount }}</span>
                </div>
                <div class="coupon-info">
                  <h4>{{ coupon.title }}</h4>
                  <p>{{ coupon.description }}</p>
                  <div class="coupon-meta">
                    <span>有效期至：{{ coupon.expireDate }}</span>
                    <span class="coupon-status" :class="coupon.used ? 'status-used' : coupon.expired ? 'status-expired' : 'status-available'">
                      {{ coupon.used ? '已使用' : coupon.expired ? '已过期' : '可使用' }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
            <div v-if="coupons.length === 0" class="empty-state">
              <i class="fas fa-ticket-alt"></i>
              <p>暂无优惠券</p>
              <button class="btn-primary" @click="goToPage('/coupons/receive')">去领取</button>
            </div>
          </div>
        </div>

        <!-- 收货地址 -->
        <div v-if="activeMenu === 'address'" class="content-section">
          <div class="section-header">
            <h2>收货地址</h2>
            <button class="btn-primary" @click="showAddressModal = true">
              <i class="fas fa-plus"></i> 新增地址
            </button>
          </div>
          <div class="address-list">
            <div v-for="addr in addresses" :key="addr.id" class="address-item" :class="{ default: addr.isDefault }">
              <div class="address-content">
                <div class="address-header">
                  <span class="address-name">{{ addr.name }}</span>
                  <span class="address-phone">{{ addr.phone }}</span>
                  <span v-if="addr.isDefault" class="default-badge">默认</span>
                </div>
                <p class="address-detail">{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detail }}</p>
              </div>
              <div class="address-actions">
                <button class="btn-link" @click="editAddress(addr)">编辑</button>
                <button class="btn-link" @click="deleteAddress(addr.id)">删除</button>
                <button v-if="!addr.isDefault" class="btn-link" @click="setDefaultAddress(addr.id)">设为默认</button>
              </div>
            </div>
            <div v-if="addresses.length === 0" class="empty-state">
              <i class="fas fa-map-marker-alt"></i>
              <p>暂无收货地址</p>
              <button class="btn-primary" @click="showAddressModal = true">添加地址</button>
            </div>
          </div>
        </div>

        <!-- 我的等级 -->
        <div v-if="activeMenu === 'level'" class="content-section">
          <div class="section-header">
            <h2>我的等级</h2>
          </div>
          <div class="level-card">
            <div class="level-header">
              <div class="level-icon">
                <i class="fas fa-crown"></i>
              </div>
              <div class="level-info">
                <h3>VIP{{ userLevel }}</h3>
                <p>当前等级：{{ getLevelName(userLevel) }}</p>
              </div>
            </div>
            <div class="level-progress">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: levelProgress + '%' }"></div>
              </div>
              <div class="progress-text">
                <span>还需 {{ nextLevelPoints - currentPoints }} 积分升级到 VIP{{ userLevel + 1 }}</span>
              </div>
            </div>
            <div class="level-benefits">
              <h4>当前等级权益</h4>
              <ul class="benefits-list">
                <li v-for="benefit in currentLevelBenefits" :key="benefit">
                  <i class="fas fa-check-circle"></i>
                  <span>{{ benefit }}</span>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 我的发布 -->
        <div v-if="activeMenu === 'publish'" class="content-section">
          <div class="section-header">
            <h2>我的发布</h2>
          </div>
          <!-- 搜索栏 -->
          <div class="search-bar">
            <div class="search-box">
              <i class="fas fa-search search-icon"></i>
              <input
                type="text"
                v-model="publishSearchKeyword"
                placeholder="搜索发布内容..."
                class="search-input"
              />
              <button v-if="publishSearchKeyword" class="search-clear" @click="publishSearchKeyword = ''">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
          <div class="publish-list">
            <div v-for="item in filteredPublishes" :key="item.id" class="publish-item">
              <div class="publish-content">
                <h4>{{ item.title }}</h4>
                <p>{{ item.description }}</p>
                <div class="publish-meta">
                  <span>{{ item.category }}</span>
                  <span>{{ item.date }}</span>
                  <span class="status-badge" :class="item.status === '在售' || item.status === '招聘中' ? 'status-active' : 'status-inactive'">{{ item.status }}</span>
                </div>
              </div>
              <button class="btn-link" @click="viewPublishDetail(item.id)">查看详情</button>
            </div>
            <div v-if="filteredPublishes.length === 0" class="empty-state">
              <i class="fas fa-file-alt"></i>
              <p>暂无发布记录</p>
            </div>
          </div>
        </div>

        <!-- 我的评价 -->
        <div v-if="activeMenu === 'reviews'" class="content-section">
          <div class="section-header">
            <h2>我的评价</h2>
            <div class="filter-tabs">
              <button class="tab-btn" :class="{ active: reviewFilter === 'all' }" @click="reviewFilter = 'all'">全部</button>
              <button class="tab-btn" :class="{ active: reviewFilter === 'goods' }" @click="reviewFilter = 'goods'">商品评价</button>
              <button class="tab-btn" :class="{ active: reviewFilter === 'service' }" @click="reviewFilter = 'service'">服务评价</button>
            </div>
          </div>
          <!-- 搜索栏 -->
          <div class="search-bar">
            <div class="search-box">
              <i class="fas fa-search search-icon"></i>
              <input
                type="text"
                v-model="reviewSearchKeyword"
                placeholder="搜索评价内容..."
                class="search-input"
              />
              <button v-if="reviewSearchKeyword" class="search-clear" @click="reviewSearchKeyword = ''">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
          <div class="review-list">
            <div v-for="review in filteredReviews" :key="review.id" class="review-item-card">
              <div class="review-header">
                <div class="review-target">
                  <h4>{{ review.targetName }}</h4>
                  <div class="review-rating">
                    <i v-for="(star, index) in 5" :key="index" :class="index < review.rating ? 'fas fa-star' : 'far fa-star'" class="star"></i>
                  </div>
                </div>
                <span class="review-time">{{ review.time }}</span>
              </div>
              <p class="review-content">{{ review.content }}</p>
              <div class="review-images" v-if="review.images && review.images.length > 0">
                <img v-for="(img, index) in review.images" :key="index" :src="img" alt="评价图片" />
              </div>
            </div>
            <div v-if="filteredReviews.length === 0" class="empty-state">
              <i class="fas fa-star"></i>
              <p>暂无评价记录</p>
            </div>
          </div>
        </div>

        <!-- 帮助中心 -->
        <div v-if="activeMenu === 'help'" class="content-section">
          <div class="section-header">
            <h2>帮助中心</h2>
          </div>
          <div class="help-categories">
            <div v-for="category in helpCategories" :key="category.id" class="help-category">
              <h3 class="category-title">
                <i :class="category.icon"></i>
                {{ category.title }}
              </h3>
              <div class="help-articles">
                <div v-for="article in category.articles" :key="article.id" class="help-article" @click="viewArticle(article.id)">
                  <span>{{ article.title }}</span>
                  <i class="fas fa-chevron-right"></i>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 意见反馈 -->
        <div v-if="activeMenu === 'feedback'" class="content-section">
          <div class="section-header">
            <h2>意见反馈</h2>
          </div>
          <div class="form-card">
            <form @submit.prevent="submitFeedback">
              <div class="form-group">
                <label>反馈类型</label>
                <select v-model="feedbackForm.type" required>
                  <option value="">请选择反馈类型</option>
                  <option value="bug">Bug反馈</option>
                  <option value="suggestion">功能建议</option>
                  <option value="complaint">投诉建议</option>
                  <option value="other">其他</option>
                </select>
              </div>
              <div class="form-group">
                <label>反馈内容</label>
                <textarea v-model="feedbackForm.content" placeholder="请详细描述您的问题或建议..." rows="6" required></textarea>
              </div>
              <div class="form-group">
                <label>联系方式（选填）</label>
                <input type="text" v-model="feedbackForm.contact" placeholder="手机号或邮箱，方便我们联系您" />
              </div>
              <div class="form-group">
                <label>上传截图（选填）</label>
                <input type="file" @change="handleFeedbackImage" accept="image/*" multiple />
                <div class="image-preview" v-if="feedbackForm.images.length > 0">
                  <div v-for="(img, index) in feedbackForm.images" :key="index" class="preview-item">
                    <img :src="img" alt="反馈图片" />
                    <button type="button" class="remove-image" @click="removeFeedbackImage(index)">×</button>
                  </div>
                </div>
              </div>
              <div class="form-actions">
                <button type="submit" class="btn-primary">提交反馈</button>
              </div>
            </form>
          </div>
        </div>

        <!-- 关于我们 -->
        <div v-if="activeMenu === 'about'" class="content-section">
          <div class="section-header">
            <h2>关于我们</h2>
            <span class="version-text">v1.0.0</span>
          </div>
          <div class="about-content">
            <div class="about-logo">
              <i class="fas fa-graduation-cap"></i>
            </div>
            <h3>上大学Online</h3>
            <p class="about-desc">我们致力于构建安全、可靠的校园服务平台，为学生提供便捷的校园生活服务。</p>
            <div class="about-info">
              <div class="info-item">
                <span class="info-label">版本号</span>
                <span class="info-value">v1.0.0</span>
              </div>
              <div class="info-item">
                <span class="info-label">更新时间</span>
                <span class="info-value">2024-05-20</span>
              </div>
              <div class="info-item">
                <span class="info-label">服务热线</span>
                <span class="info-value">400-123-4567</span>
              </div>
              <div class="info-item">
                <span class="info-label">官方邮箱</span>
                <span class="info-value">support@campus.edu.cn</span>
              </div>
            </div>
            <div class="about-links">
              <a href="#" @click.prevent="viewPrivacyPolicy">隐私政策</a>
              <a href="#" @click.prevent="viewServiceAgreement">服务协议</a>
              <a href="#" @click.prevent="viewSecurityInfo">安全保障</a>
            </div>
          </div>
        </div>

        <!-- 系统设置 -->
        <div v-if="activeMenu === 'settings'" class="content-section">
          <div class="section-header">
            <h2>系统设置</h2>
            <button class="btn-primary" @click="saveSettings">保存设置</button>
          </div>
          <div class="info-card">
            <h3 class="card-title">通知设置</h3>
        <div class="settings-list">
              <div class="setting-item">
                <div class="setting-info">
                  <h4>课程提醒</h4>
                  <p>在上课前提醒您即将开始的课程</p>
            </div>
                <label class="switch">
                  <input type="checkbox" v-model="settings.courseReminder" />
                  <span class="slider"></span>
                </label>
            </div>
              <div class="setting-item">
                <div class="setting-info">
                  <h4>交易通知</h4>
                  <p>当有新的订单或交易状态变更时通知您</p>
          </div>
                <label class="switch">
                  <input type="checkbox" v-model="settings.transactionNotify" />
                  <span class="slider"></span>
                </label>
            </div>
              <div class="setting-item">
                <div class="setting-info">
                  <h4>活动推送</h4>
                  <p>向您推送校园活动、讲座等信息</p>
            </div>
                <label class="switch">
                  <input type="checkbox" v-model="settings.activityPush" />
                  <span class="slider"></span>
                </label>
          </div>
              <div class="setting-item">
                <div class="setting-info">
                  <h4>系统公告</h4>
                  <p>接收平台更新、维护等重要通知</p>
            </div>
                <label class="switch">
                  <input type="checkbox" v-model="settings.systemNotice" />
                  <span class="slider"></span>
                </label>
            </div>
          </div>
            </div>
          <div class="info-card">
            <h3 class="card-title">隐私设置</h3>
            <div class="settings-list">
              <div class="setting-item">
                <div class="setting-info">
                  <h4>谁可以看到我的个人信息</h4>
            </div>
                <select v-model="settings.privacyLevel" class="select-input">
                  <option value="self">仅自己</option>
                  <option value="school">同校用户</option>
                  <option value="all">所有用户</option>
                </select>
          </div>
              <div class="setting-item">
                <div class="setting-info">
                  <h4>允许通过手机号找到我</h4>
        </div>
                <select v-model="settings.phoneSearch" class="select-input">
                  <option value="school">同校用户</option>
                  <option value="all">所有用户</option>
                  <option value="none">不允许</option>
                </select>
              </div>
              <div class="setting-item">
                <div class="setting-info">
                  <h4>个性化推荐</h4>
                  <p>根据您的兴趣和行为推荐相关内容</p>
                </div>
                <label class="switch">
                  <input type="checkbox" v-model="settings.personalizedRecommend" />
                  <span class="slider"></span>
                </label>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- 学号变更申请模态框 -->
    <div v-if="showStudentIdModal" class="modal-overlay" @click="showStudentIdModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>学号变更申请</h3>
          <button class="modal-close" @click="showStudentIdModal = false">
            <i class="fas fa-times"></i>
        </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitStudentIdChange">
            <div class="form-group">
              <label>变更原因</label>
              <select v-model="studentIdForm.reason" required>
                <option value="">请选择变更原因</option>
                <option value="upgrade">升学（本科升硕士/博士）</option>
                <option value="transfer">转学至其他院校</option>
                <option value="correction">学籍信息更正</option>
                <option value="other">其他特殊情况</option>
              </select>
            </div>
            <div class="form-group">
              <label>新学号（如已知）</label>
              <input type="text" v-model="studentIdForm.newStudentId" placeholder="请输入新学号" />
            </div>
            <div class="form-group">
              <label>申请说明</label>
              <textarea v-model="studentIdForm.description" placeholder="请详细说明变更原因" rows="4"></textarea>
            </div>
            <div class="form-group">
              <label>证明材料</label>
              <input type="file" @change="handleFileUpload" accept="image/*,.pdf" />
              <p class="form-hint">请上传相关证明文件（如录取通知书、转学证明等）</p>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn-secondary" @click="showStudentIdModal = false">取消</button>
              <button type="submit" class="btn-primary">提交申请</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue' // 引入 ref、computed、onMounted 组合式 API，用于创建响应式数据和生命周期钩子
import { useRouter } from 'vue-router' // 引入 useRouter，用于在脚本中进行页面跳转
import { useUserStore, getAuthApplyRecords } from '@campus/common' // 引入用户 Store 和获取认证申请记录的接口方法
import type { AuthApplyRecordListInfo } from '@campus/common' // 引入认证申请记录列表的类型，方便对接口返回值做类型约束
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()
const userStore = useUserStore()

// 当前激活的菜单项
const activeMenu = ref('overview')

// 菜单项配置（参考app端）
const menuItems = [
  { key: 'overview', label: '个人信息概览', icon: 'fas fa-user-circle' },
  { key: 'profile', label: '个人资料', icon: 'fas fa-user-edit' },
  { key: 'security', label: '账户安全', icon: 'fas fa-shield-alt' },
  { key: 'studentId', label: '学号管理', icon: 'fas fa-id-card' },
  { key: 'orders', label: '我的订单', icon: 'fas fa-shopping-bag' },
  { key: 'favorites', label: '我的收藏', icon: 'fas fa-heart' },
  { key: 'publish', label: '我的发布', icon: 'fas fa-file-alt' },
  { key: 'history', label: '浏览记录', icon: 'fas fa-history' },
  { key: 'messages', label: '通知提醒', icon: 'fas fa-bell' },
  { key: 'applications', label: '我的申请', icon: 'fas fa-file-alt' },
  { key: 'coupons', label: '我的优惠券', icon: 'fas fa-ticket-alt' },
  { key: 'address', label: '收货地址', icon: 'fas fa-map-marker-alt' },
  { key: 'level', label: '我的等级', icon: 'fas fa-crown' },
  { key: 'reviews', label: '我的评价', icon: 'fas fa-star' },
  { key: 'help', label: '帮助中心', icon: 'fas fa-question-circle' },
  { key: 'feedback', label: '意见反馈', icon: 'fas fa-comment-dots' },
  { key: 'about', label: '关于我们', icon: 'fas fa-info-circle' },
  { key: 'settings', label: '系统设置', icon: 'fas fa-cog' }
]

// 用户信息
const userInfo = ref({
  name: '张三',
  role: 'student',
  school: '北京大学',
  studentId: '2023012345',
  major: '计算机科学与技术',
  enrollmentYear: '2023年',
  graduationDate: '2027年6月',
  phone: '138****5678',
  email: 'zhangsan@example.com',
  authTime: '2023-09-01',
  expiryDate: '2027-06-30'
})

// 用户头像
const userAvatar = ref<string>('')

// 编辑头像
const handleEditAvatar = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e: Event) => {
    const target = e.target as HTMLInputElement
    if (target.files && target.files[0]) {
      const file = target.files[0]
      const reader = new FileReader()
      reader.onload = (event) => {
        if (event.target?.result) {
          userAvatar.value = event.target.result as string
          // TODO: 上传头像到服务器
          alert('头像已更新')
        }
      }
      reader.readAsDataURL(file)
    }
  }
  input.click()
}

// 统计数据（参考app端）
const orderCount = ref(12)
const favoriteCount = ref(8)
const historyCount = ref(28)
const publishCount = ref(8)
const points = ref(1280)
const creditScore = ref(98)
const deviceCount = ref(3)
const messageCount = ref(5)
const pendingOrders = ref(3)
const couponsCount = ref(5)
const userLevel = ref(3)

// 角色标签
const roleLabel = computed(() => {
  if (userStore.isStudent) return '学生'
  if (userStore.isTeacher) return '教师'
  if (userStore.isMerchant) return '商户'
  if (userStore.isAdmin) return '管理员'
  return '用户'
})

// 掩码手机号
const maskedPhone = computed(() => {
  return userInfo.value.phone || '未绑定'
})

// 切换菜单
const switchMenu = (key: string) => {
  activeMenu.value = key
}

// 编辑表单
const editForm = ref({
  name: '',
  studentId: '',
  major: '',
  enrollmentYear: '',
  graduationDate: '',
  phone: '',
  email: ''
})

// 年份选项
const years = ref(['2020', '2021', '2022', '2023', '2024'])

// 保存个人资料
const saveProfile = async () => {
  try {
    const { updateUserInfo } = await import('@campus/common/api/auth')
    await updateUserInfo({
      nickname: editForm.value.name,
      phone: editForm.value.phone,
      email: editForm.value.email
    })
    // 更新本地用户信息
    Object.assign(userInfo.value, editForm.value)
    alert('保存成功！')
    // 切换回概览页面
    activeMenu.value = 'overview'
  } catch (error) {
    console.error('保存用户信息失败:', error)
    alert('保存失败，请稍后重试')
  }
}

// 取消编辑
const cancelEdit = () => {
  // 重置表单
  editForm.value = {
    name: userInfo.value.name,
    studentId: userInfo.value.studentId,
    major: userInfo.value.major,
    enrollmentYear: userInfo.value.enrollmentYear,
    graduationDate: userInfo.value.graduationDate,
    phone: userInfo.value.phone,
    email: userInfo.value.email
  }
}

// 编辑资料
const editProfile = () => {
  activeMenu.value = 'profile'
  cancelEdit() // 初始化表单
}

// 修改密码
const changePassword = () => {
  router.push('/forget-password')
}

// 更换手机
const changePhone = () => {
  alert('更换手机功能开发中...')
}

// 更换邮箱
const changeEmail = () => {
  alert('更换邮箱功能开发中...')
}

// 管理设备
const manageDevices = () => {
  alert('管理设备功能开发中...')
}

// 学号变更申请
const showStudentIdModal = ref(false)
const studentIdForm = ref({
  reason: '',
  newStudentId: '',
  description: '',
  file: null as File | null
})

// 文件上传
const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    studentIdForm.value.file = target.files[0]
  }
}

// 提交学号变更申请
const submitStudentIdChange = () => {
  // TODO: 调用API提交申请
  alert('申请已提交，请等待审核')
  showStudentIdModal.value = false
}

// 订单相关
const orderFilter = ref('all')
const orderSearchKeyword = ref('')
const orders = ref([
  { id: 1, type: '二手市场', title: '二手教材《数据结构与算法》', description: '九成新，有少量笔记，价格实惠', orderNo: '20240520001', date: '2024-05-20' },
  { id: 2, type: '兼职收藏', title: '校园图书馆助理', description: '工作时间灵活，适合学生兼职', orderNo: '', date: '2024-05-18' },
  { id: 3, type: '二手市场', title: '二手笔记本电脑', description: '配置良好，运行流畅，适合学习使用', orderNo: '20240515002', date: '2024-05-15' }
])

const filteredOrders = computed(() => {
  let result = orders.value
  
  // 类型筛选
  if (orderFilter.value !== 'all') {
    result = result.filter(order => {
      if (orderFilter.value === 'secondhand') return order.type === '二手市场'
      if (orderFilter.value === 'parttime') return order.type === '兼职收藏'
      return false
    })
  }
  
  // 搜索筛选
  if (orderSearchKeyword.value.trim()) {
    const keyword = orderSearchKeyword.value.trim().toLowerCase()
    result = result.filter(order => 
      order.title.toLowerCase().includes(keyword) ||
      order.description.toLowerCase().includes(keyword) ||
      order.orderNo.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 收藏列表
const favoriteSearchKeyword = ref('')
const favorites = ref([
  { id: 1, title: '校园图书馆助理', description: '工作时间灵活，适合学生兼职', category: '兼职收藏', date: '2024-05-18', status: '已收藏' },
  { id: 2, title: '校园活动策划助理', description: '协助组织校园文化活动，积累经验', category: '兼职收藏', date: '2024-05-10', status: '已收藏' }
])

const filteredFavorites = computed(() => {
  if (!favoriteSearchKeyword.value.trim()) return favorites.value
  const keyword = favoriteSearchKeyword.value.trim().toLowerCase()
  return favorites.value.filter(item =>
    item.title.toLowerCase().includes(keyword) ||
    item.description.toLowerCase().includes(keyword) ||
    item.category.toLowerCase().includes(keyword)
  )
})

// 浏览记录
const historySearchKeyword = ref('')
const history = ref([
  { id: 1, title: '二手教材《Java编程思想》', description: '经典编程教材，九成新，无笔记', category: '二手市场', time: '今天 10:30' },
  { id: 2, title: '校园咖啡厅兼职', description: '工作时间灵活，提供培训，适合学生', category: '兼职招聘', time: '昨天 15:20' },
  { id: 3, title: '二手MacBook Pro', description: '2019款，16GB内存，512GB SSD', category: '二手市场', time: '昨天 09:15' }
])

const filteredHistory = computed(() => {
  if (!historySearchKeyword.value.trim()) return history.value
  const keyword = historySearchKeyword.value.trim().toLowerCase()
  return history.value.filter(item =>
    item.title.toLowerCase().includes(keyword) ||
    item.description.toLowerCase().includes(keyword) ||
    item.category.toLowerCase().includes(keyword)
  )
})

// 消息列表
const messageFilter = ref('all')
const messageSearchKeyword = ref('')
const messages = ref([
  { id: 1, title: '交易提醒：订单已发货', content: '您购买的《数据结构与算法》教材已发货，预计明天送达。', category: '二手交易', time: '2小时前', read: false, icon: 'fas fa-shopping-bag' },
  { id: 2, title: '兼职申请状态更新', content: '您申请的校园图书馆助理职位已通过初审，请等待面试通知。', category: '兼职交流', time: '1天前', read: false, icon: 'fas fa-briefcase' },
  { id: 3, title: '新消息：关于商品咨询', content: '有用户对您发布的二手笔记本电脑感兴趣，并发送了咨询消息。', category: '二手交易', time: '2天前', read: true, icon: 'fas fa-comments' }
])

const filteredMessages = computed(() => {
  let result = messages.value
  
  // 类型筛选
  if (messageFilter.value !== 'all') {
    result = result.filter(msg => {
      if (messageFilter.value === 'secondhand') return msg.category === '二手交易'
      if (messageFilter.value === 'parttime') return msg.category === '兼职交流'
      return false
    })
  }
  
  // 搜索筛选
  if (messageSearchKeyword.value.trim()) {
    const keyword = messageSearchKeyword.value.trim().toLowerCase()
    result = result.filter(msg =>
      msg.title.toLowerCase().includes(keyword) ||
      msg.content.toLowerCase().includes(keyword) ||
      msg.category.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 申请列表当前筛选标签（all：全部；auth：身份认证；parttime：兼职申请）
const appFilter = ref<'all' | 'auth' | 'parttime'>('all') // appFilter：控制“我的申请”中展示哪一类记录

// “我的申请”列表项在前端展示层使用的数据结构定义
interface ApplicationViewItem { // ApplicationViewItem：前端用于渲染“我的申请”每一行的数据结构
  id: number // id：申请记录的唯一标识
  title: string // title：申请标题（例如“学生身份认证”）
  description: string // description：申请说明或审核备注（用于列表中的简介文字）
  category: string // category：申请类别（例如“身份认证”、“兼职申请”）
  status: string // status：中文状态（例如“已通过”“审核中”“已拒绝”）
  date: string // date：申请时间（格式化后的日期字符串）
}

// 实际用于渲染“我的申请”列表的数据，初始为空，后续由接口填充
const applications = ref<ApplicationViewItem[]>([]) // applications：承载“我的申请”列表的响应式数组

const appSearchKeyword = ref('')
const filteredApplications = computed(() => {
  let result = applications.value
  
  // 类型筛选
  if (appFilter.value !== 'all') {
    result = result.filter(app => {
      if (appFilter.value === 'auth') return app.category === '身份认证'
      if (appFilter.value === 'parttime') return app.category === '兼职申请'
      return false
    })
  }
  
  // 搜索筛选
  if (appSearchKeyword.value.trim()) {
    const keyword = appSearchKeyword.value.trim().toLowerCase()
    result = result.filter(app =>
      app.title.toLowerCase().includes(keyword) ||
      app.description.toLowerCase().includes(keyword) ||
      app.category.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 将后端返回的英文状态码映射为中文友好状态文案
const mapStatusToText = (status: AuthApplyRecordListInfo['list'][number]['status']): string => {
  if (status === 'APPROVED') return '已通过' // 审核通过
  if (status === 'PENDING') return '审核中' // 正在审核
  if (status === 'REJECTED') return '已拒绝' // 审核未通过
  return '未知状态' // 兜底文案，防止出现未识别的状态
}

// 简单的日期格式化工具：从完整时间字符串中截取“年月日”部分
const formatDate = (time: any): string => {
  const str = String(time || '') // 将时间转换为字符串，避免空值报错
  return str.length >= 10 ? str.slice(0, 10) : str // 只取前 10 位（形如 2024-05-10）
}

// 从后端加载当前登录用户的身份认证申请记录，并转换为前端展示格式
const loadAuthApplications = async () => {
  try {
    // 调用公共 SDK 中封装好的接口，加载第一页最多 20 条认证申请记录
    const result: AuthApplyRecordListInfo = await getAuthApplyRecords(1, 20)
    // 将后端返回的数据映射为前端展示所需结构
    applications.value = (result.list || []).map(app => ({
      id: app.id, // 使用申请记录的主键 ID
      title: app.applyRole === 'STUDENT' ? '学生身份认证' : '身份认证', // 根据申请角色生成标题
      description: app.auditRemark || '申请验证学生身份，享受校园专属服务', // 优先展示审核备注，否则使用默认说明
      category: '身份认证', // 当前仅接入身份认证类记录
      status: mapStatusToText(app.status), // 将英文状态码转换为中文状态
      date: formatDate(app.createTime) // 使用创建时间作为申请时间展示
    }))
  } catch (error) {
    // 接口调用失败时仅在控制台输出错误，不打断页面正常渲染
    console.error('加载认证申请记录失败：', error)
  }
}

// 获取状态样式类
const getStatusClass = (status: string) => {
  if (status === '已通过') return 'status-success'
  if (status === '审核中') return 'status-warning'
  if (status === '已拒绝') return 'status-error'
  return ''
}

// 系统设置
const settings = ref({
  courseReminder: true,
  transactionNotify: true,
  activityPush: false,
  systemNotice: true,
  privacyLevel: 'school',
  phoneSearch: 'school',
  personalizedRecommend: true
})

// 保存设置
const saveSettings = () => {
  // TODO: 调用API保存设置
  alert('设置已保存！')
}

// 跳转页面
const goToPage = (path: string) => {
  router.push(path)
}

// 查看订单详情
const viewOrderDetail = (id: number) => {
  // TODO: 跳转到订单详情页
  console.log('查看订单:', id)
}

// 查看收藏详情
const viewFavoriteDetail = (id: number) => {
  // TODO: 跳转到收藏详情页
  console.log('查看收藏:', id)
}

// 查看浏览记录详情
const viewHistoryDetail = (id: number) => {
  // TODO: 跳转到详情页
  console.log('查看记录:', id)
}

// 查看发布详情
const viewPublishDetail = (id: number) => {
  // TODO: 跳转到发布详情页
  console.log('查看发布:', id)
}

// 清空浏览记录
const clearHistory = () => {
  if (confirm('确定要清空所有浏览记录吗？')) {
    history.value = []
    alert('已清空浏览记录')
  }
}

// 标记全部已读
const markAllRead = () => {
  messages.value.forEach(msg => {
    msg.read = true
  })
  alert('已标记全部已读')
}

// 关闭提醒
const dismissAlert = () => {
  // TODO: 保存用户选择
  console.log('稍后提醒')
}

// 快捷功能处理（参考app端）
const handleMyOrders = () => {
  activeMenu.value = 'orders'
}

const handleMyFavorites = () => {
  activeMenu.value = 'favorites'
}

const handleMyPublish = () => {
  activeMenu.value = 'publish'
}

// 我的发布相关
const publishSearchKeyword = ref('')
const publishes = ref([
  { id: 1, title: '二手教材《数据结构与算法》', description: '九成新，有少量笔记，价格实惠', category: '二手市场', date: '2024-05-20', status: '在售' },
  { id: 2, title: '校园图书馆助理招聘', description: '工作时间灵活，适合学生兼职', category: '兼职招聘', date: '2024-05-18', status: '招聘中' },
  { id: 3, title: '二手笔记本电脑', description: '配置良好，运行流畅，适合学习使用', category: '二手市场', date: '2024-05-15', status: '已售' }
])

const filteredPublishes = computed(() => {
  if (!publishSearchKeyword.value.trim()) return publishes.value
  const keyword = publishSearchKeyword.value.trim().toLowerCase()
  return publishes.value.filter(item =>
    item.title.toLowerCase().includes(keyword) ||
    item.description.toLowerCase().includes(keyword) ||
    item.category.toLowerCase().includes(keyword)
  )
})

const handleMyWallet = () => {
  // TODO: 跳转到钱包页面
  alert('钱包功能开发中...')
}

const handleViewFavorites = () => {
  activeMenu.value = 'favorites'
}

const handleViewHistory = () => {
  activeMenu.value = 'history'
}

const handleSettings = () => {
  activeMenu.value = 'settings'
}

const handleMessages = () => {
  activeMenu.value = 'messages'
}

// 优惠券数据
const coupons = ref([
  { id: 1, title: '新用户专享', amount: 10, description: '满50元可用', expireDate: '2024-12-31', used: false, expired: false },
  { id: 2, title: '满减优惠', amount: 20, description: '满100元可用', expireDate: '2024-11-30', used: false, expired: false },
  { id: 3, title: '限时优惠', amount: 5, description: '满30元可用', expireDate: '2024-10-15', used: true, expired: false }
])

// 收货地址数据
const addresses = ref([
  { id: 1, name: '张三', phone: '138****5678', province: '北京市', city: '北京市', district: '海淀区', detail: '中关村大街1号', isDefault: true },
  { id: 2, name: '张三', phone: '138****5678', province: '北京市', city: '北京市', district: '朝阳区', detail: '建国路88号', isDefault: false }
])

const showAddressModal = ref(false)

const editAddress = (addr: any) => {
  // TODO: 编辑地址
  console.log('编辑地址:', addr)
}

const deleteAddress = (id: number) => {
  if (confirm('确定要删除这个地址吗？')) {
    addresses.value = addresses.value.filter(addr => addr.id !== id)
  }
}

const setDefaultAddress = (id: number) => {
  addresses.value.forEach(addr => {
    addr.isDefault = addr.id === id
  })
}

// 等级相关
const currentPoints = ref(1280)
const nextLevelPoints = ref(2000)
const levelProgress = computed(() => {
  return Math.min((currentPoints.value / nextLevelPoints.value) * 100, 100)
})

const getLevelName = (level: number) => {
  const names = ['普通用户', 'VIP1', 'VIP2', 'VIP3', 'VIP4', 'VIP5']
  return names[level] || 'VIP' + level
}

const currentLevelBenefits = computed(() => {
  const benefits = [
    '享受平台专属服务',
    '优先客服支持',
    '专属活动邀请',
    '积分翻倍奖励'
  ]
  return benefits
})

// 评价相关
const reviewFilter = ref('all')
const reviewSearchKeyword = ref('')
const reviews = ref([
  { id: 1, targetName: '二手教材《数据结构与算法》', rating: 5, content: '书很新，价格实惠，卖家很nice！', time: '2024-05-20', type: 'goods', images: [] },
  { id: 2, targetName: '校园图书馆助理', rating: 4, content: '工作环境不错，同事都很友好。', time: '2024-05-15', type: 'service', images: [] }
])

const filteredReviews = computed(() => {
  let result = reviews.value
  
  // 类型筛选
  if (reviewFilter.value !== 'all') {
    result = result.filter(r => r.type === reviewFilter.value)
  }
  
  // 搜索筛选
  if (reviewSearchKeyword.value.trim()) {
    const keyword = reviewSearchKeyword.value.trim().toLowerCase()
    result = result.filter(r =>
      r.targetName.toLowerCase().includes(keyword) ||
      r.content.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 帮助中心数据
const helpCategories = ref([
  {
    id: 1,
    title: '账户相关',
    icon: 'fas fa-user-circle',
    articles: [
      { id: 1, title: '如何修改个人信息？' },
      { id: 2, title: '如何绑定手机号？' },
      { id: 3, title: '忘记密码怎么办？' }
    ]
  },
  {
    id: 2,
    title: '交易相关',
    icon: 'fas fa-shopping-bag',
    articles: [
      { id: 4, title: '如何发布商品？' },
      { id: 5, title: '如何申请兼职？' },
      { id: 6, title: '交易安全如何保障？' }
    ]
  },
  {
    id: 3,
    title: '其他问题',
    icon: 'fas fa-question-circle',
    articles: [
      { id: 7, title: '如何联系客服？' },
      { id: 8, title: '平台使用规则' }
    ]
  }
])

const viewArticle = (id: number) => {
  // TODO: 查看帮助文章详情
  console.log('查看文章:', id)
}

// 意见反馈
const feedbackForm = ref({
  type: '',
  content: '',
  contact: '',
  images: [] as string[]
})

const handleFeedbackImage = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    Array.from(target.files).forEach(file => {
      const reader = new FileReader()
      reader.onload = (e) => {
        if (e.target?.result) {
          feedbackForm.value.images.push(e.target.result as string)
        }
      }
      reader.readAsDataURL(file)
    })
  }
}

const removeFeedbackImage = (index: number) => {
  feedbackForm.value.images.splice(index, 1)
}

const submitFeedback = () => {
  // TODO: 提交反馈
  alert('反馈已提交，感谢您的建议！')
  feedbackForm.value = { type: '', content: '', contact: '', images: [] }
}

const viewPrivacyPolicy = () => {
  // TODO: 查看隐私政策
  alert('隐私政策页面开发中...')
}

const viewServiceAgreement = () => {
  // TODO: 查看服务协议
  alert('服务协议页面开发中...')
}

const viewSecurityInfo = () => {
  // TODO: 查看安全保障
  alert('安全保障页面开发中...')
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const { getUserInfo } = await import('@campus/common/api/auth')
    const info = await getUserInfo()
    if (info) {
      userInfo.value.name = (info as any).nickname || (info as any).username || userInfo.value.name;
      userInfo.value.phone = info.phone || userInfo.value.phone
      userInfo.value.email = info.email || userInfo.value.email
      userAvatar.value = info.avatar || ''
      // 更新编辑表单
      editForm.value.name = userInfo.value.name
      editForm.value.phone = userInfo.value.phone
      editForm.value.email = userInfo.value.email
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

// 加载用户统计数据
const loadUserStats = async () => {
  try {
    const { getUserStats } = await import('@campus/common/api/auth')
    const stats = await getUserStats()
    if (stats) {
      favoriteCount.value = stats.favoritesCount || favoriteCount.value
      historyCount.value = stats.historyCount || historyCount.value
      publishCount.value = stats.publishCount || publishCount.value
      points.value = stats.pointsCount || points.value
    }
  } catch (error) {
    console.error('加载用户统计数据失败:', error)
  }
}

// 组件挂载时初始化
onMounted(() => {
  // 初始化编辑表单
  cancelEdit()
  // 加载用户信息和统计数据
  loadUserInfo()
  loadUserStats()
  // 加载"我的申请"中来自后端的身份认证申请记录
  loadAuthApplications()
})
</script>

<style scoped>
/* 主容器样式 */
.profile-page {
  min-height: 100vh;
  /* 使用浅灰色背景，更符合主流App设计 */
  background: #f5f5f5;
  padding-bottom: 40px;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px 15px 40px;
  display: flex;
  gap: 20px;
}

/* 左侧边栏样式 */
.sidebar {
  width: 280px;
  flex-shrink: 0;
  position: sticky;
  top: 20px;
  align-self: flex-start;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
}

.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-thumb {
  background: #FF6B9D;
  border-radius: 3px;
}

.sidebar::-webkit-scrollbar-track {
  background: transparent;
}

.user-card {
  /* 使用单原色：淡淡的马卡龙粉色，与背景有对比度 */
  background: #FFB5D8;
  border-radius: 16px;
  padding: 30px 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  position: relative;
  overflow: hidden;
  
  /* 背景装饰圆圈 */
  &::before {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 50%;
    top: -200px;
    right: -100px;
  }
  
  &::after {
    content: '';
    position: absolute;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.12);
    border-radius: 50%;
    bottom: -150px;
    left: -50px;
  }
}

.user-avatar-wrapper {
  position: relative;
  margin: 0 auto 15px;
  width: 80px;
  height: 80px;
  cursor: pointer;
  z-index: 1;
}

.user-avatar,
.user-avatar-img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 36px;
  box-shadow: 0 4px 15px rgba(255, 255, 255, 0.3);
  border: 4px solid rgba(255, 255, 255, 0.5);
  object-fit: cover;
}

.avatar-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  cursor: pointer;
}

.avatar-badge i {
  font-size: 14px;
  color: #FF6B9D;
}

.user-name {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8px;
  position: relative;
  z-index: 1;
}

.user-role {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 5px;
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 12px;
  border-radius: 8px;
  display: inline-block;
  position: relative;
  z-index: 1;
}

.user-school {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
}

.stat-item {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-item:hover {
  transform: translateY(-2px);
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.85);
}

/* 导航菜单样式 */
.sidebar-nav {
  background: white;
  border-radius: 16px;
  padding: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
  font-size: 14px;
}

.nav-item:hover {
  background: #f9f0ff;
  color: #d81b60;
}

.nav-item.active {
  background: #FFB5D8;
  color: #fff;
  box-shadow: 0 2px 8px rgba(255, 181, 216, 0.3);
}

.nav-item i {
  width: 20px;
  text-align: center;
}

/* 右侧内容区域样式 */
.content-area {
  flex: 1;
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.section-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.action-btn {
  width: 40px;
  height: 40px;
  background: rgba(255, 181, 216, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FF6B9D;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  border: none;
}

.action-btn:hover {
  background: rgba(255, 181, 216, 0.3);
  transform: scale(1.05);
}

.action-btn .badge {
  position: absolute;
  top: -5px;
  right: -5px;
  min-width: 20px;
  height: 20px;
  background: #ff4757;
  color: white;
  font-size: 12px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
  border: 2px solid white;
}

/* 快捷功能网格样式（参考app端） */
.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
  background: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  padding: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-action-item:hover {
  transform: translateY(-3px);
}

.quick-icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.quick-icon-wrapper i {
  font-size: 28px;
  color: rgba(0, 0, 0, 0.7);
}

.quick-icon-wrapper.orders {
  background: #FFD3B6;
}

.quick-icon-wrapper.favorites {
  background: #FFB5D8;
}

.quick-icon-wrapper.publish {
  background: #C7CEEA;
}

.quick-icon-wrapper.wallet {
  background: #A8E6CF;
}

.quick-label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.quick-badge {
  position: absolute;
  top: 0;
  right: 0;
  min-width: 24px;
  height: 24px;
  background: #ff4757;
  color: white;
  font-size: 12px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
  border: 2px solid white;
}

/* 优惠券样式 */
.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.coupon-item {
  background: linear-gradient(135deg, #FFE5E5 0%, #FFD1D1 100%);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  border: 2px solid #FFB5D8;
  transition: all 0.3s;
}

.coupon-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 12px rgba(255, 181, 216, 0.3);
}

.coupon-item.used,
.coupon-item.expired {
  opacity: 0.6;
  background: #f5f5f5;
  border-color: #ddd;
}

.coupon-content {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
}

.coupon-amount {
  display: flex;
  align-items: baseline;
  color: #FF6B9D;
}

.amount-symbol {
  font-size: 20px;
  font-weight: bold;
}

.amount-value {
  font-size: 36px;
  font-weight: bold;
}

.coupon-info {
  flex: 1;
}

.coupon-info h4 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.coupon-info p {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.coupon-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.coupon-status {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.status-available {
  background: #e6f7ff;
  color: #0288d1;
}

.status-used {
  background: #f5f5f5;
  color: #999;
}

.status-expired {
  background: #fff1f0;
  color: #ff4d4f;
}

/* 收货地址样式 */
.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 2px solid #eee;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #FFB5D8;
  box-shadow: 0 2px 8px rgba(255, 181, 216, 0.2);
}

.address-item.default {
  border-color: #FFB5D8;
  background: #fff8fc;
}

.address-content {
  margin-bottom: 15px;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.address-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.address-phone {
  font-size: 14px;
  color: #666;
}

.default-badge {
  padding: 4px 10px;
  background: #FFB5D8;
  color: white;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.address-detail {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.address-actions {
  display: flex;
  gap: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

/* 等级卡片样式 */
.level-card {
  background: linear-gradient(135deg, #FFE5E5 0%, #FFD1D1 100%);
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(255, 181, 216, 0.2);
}

.level-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 25px;
}

.level-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #D4A5F5 0%, #C7CEEA 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 36px;
  box-shadow: 0 4px 15px rgba(212, 165, 245, 0.3);
}

.level-info h3 {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.level-info p {
  font-size: 14px;
  color: #666;
}

.level-progress {
  margin-bottom: 25px;
}

.progress-bar {
  width: 100%;
  height: 12px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #FFB5D8 0%, #FF6B9D 100%);
  border-radius: 6px;
  transition: width 0.3s;
}

.progress-text {
  font-size: 13px;
  color: #666;
  text-align: center;
}

.level-benefits {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.level-benefits h4 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
}

.benefits-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.benefits-list li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  font-size: 14px;
  color: #666;
}

.benefits-list i {
  color: #52c41a;
}

/* 评价列表样式 */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.review-item-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #eee;
  transition: all 0.3s;
}

.review-item-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.review-target h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.review-rating {
  display: flex;
  gap: 4px;
}

.review-rating .star {
  font-size: 14px;
  color: #FFA500;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.review-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
}

.review-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.review-images img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

/* 帮助中心样式 */
.help-categories {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.help-category {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.category-title i {
  color: #FF6B9D;
}

.help-articles {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.help-article {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.help-article:hover {
  background: #fff0f5;
  transform: translateX(5px);
}

.help-article span {
  font-size: 14px;
  color: #333;
}

.help-article i {
  color: #999;
  font-size: 12px;
}

/* 意见反馈样式 */
.image-preview {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 10px;
}

.preview-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.remove-image {
  position: absolute;
  top: -5px;
  right: -5px;
  width: 24px;
  height: 24px;
  background: #ff4757;
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 关于我们样式 */
.about-content {
  text-align: center;
  padding: 40px 20px;
}

.about-logo {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #FFB5D8 0%, #FF6B9D 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
  font-size: 48px;
  box-shadow: 0 4px 20px rgba(255, 181, 216, 0.3);
}

.about-content h3 {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
}

.about-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 30px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.about-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 30px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.about-links {
  display: flex;
  justify-content: center;
  gap: 30px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}

.about-links a {
  color: #FF6B9D;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
}

.about-links a:hover {
  color: #FFB5D8;
  text-decoration: underline;
}

.version-text {
  font-size: 14px;
  color: #999;
}

/* 按钮样式 */
.btn-primary {
  padding: 10px 20px;
  background: #FFB5D8;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 8px rgba(255, 181, 216, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 181, 216, 0.4);
  background: #FF6B9D;
}

.btn-secondary {
  padding: 10px 20px;
  background: white;
  color: #666;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background: #f5f5f5;
  border-color: #FFB5D8;
  color: #FF6B9D;
}

.btn-edit {
  padding: 8px 16px;
  background: white;
  color: #FF6B9D;
  border: 1px solid #FFB5D8;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn-edit:hover {
  background: #FFB5D8;
  color: white;
}

.btn-link {
  background: none;
  border: none;
  color: #FF6B9D;
  cursor: pointer;
  font-size: 14px;
  padding: 0;
  text-decoration: underline;
}

.btn-link:hover {
  color: #FFB5D8;
}

/* 信息卡片样式 */
.info-card {
  margin-bottom: 25px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title i {
  color: #FF6B9D;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-label {
  font-size: 13px;
  color: #666;
}

.info-value {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

/* 状态标签样式 */
.status-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  margin-right: 6px;
}

.status-active {
  background: #e6f7ff;
  color: #0288d1;
}

.status-normal {
  background: #f6ffed;
  color: #52c41a;
}

.status-verified {
  background: #f6ffed;
  color: #52c41a;
}

.status-checked {
  background: #e6f7ff;
  color: #0288d1;
}

.status-high {
  background: #fff7e6;
  color: #fa8c16;
}

.status-safe {
  background: #f6ffed;
  color: #52c41a;
}

.status-success {
  background: #f6ffed;
  color: #52c41a;
}

.status-warning {
  background: #fff7e6;
  color: #fa8c16;
}

.status-error {
  background: #fff1f0;
  color: #ff4d4f;
}

.points {
  color: #FF6B9D;
  font-weight: bold;
}

/* 提醒框样式 */
.alert-box {
  background: #fff7e6;
  border: 1px solid #ffe58f;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 25px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.alert-content {
  display: flex;
  gap: 12px;
  flex: 1;
}

.alert-content i {
  color: #fa8c16;
  font-size: 20px;
  margin-top: 2px;
}

.alert-text strong {
  display: block;
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
}

.alert-text p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.alert-actions {
  display: flex;
  gap: 10px;
}

.alert-info {
  background: #e6f7ff;
  border-color: #91d5ff;
}

.alert-info i {
  color: #0288d1;
}

.alert-time {
  display: block;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

/* 快捷功能样式 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.quick-action-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #eee;
}

.quick-action-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #FFB5D8;
}

.quick-action-item i {
  font-size: 24px;
  color: #FF6B9D;
  width: 40px;
  text-align: center;
}

.action-content h4 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.action-content p {
  font-size: 12px;
  color: #666;
  margin: 0;
}

/* 表单样式 */
.form-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #FFB5D8;
  box-shadow: 0 0 0 3px rgba(255, 181, 216, 0.1);
}

.form-group input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-hint {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

/* 安全设置样式 */
.security-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 8px;
  border: 1px solid #eee;
}

.security-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.security-info i {
  font-size: 20px;
  color: #FF6B9D;
  width: 30px;
  text-align: center;
}

.security-info h4 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.security-info p {
  font-size: 13px;
  color: #666;
  margin: 0;
}

.security-status {
  display: flex;
  align-items: center;
  gap: 15px;
}

/* 通知设置样式 */
.settings-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: white;
  border-radius: 8px;
  border: 1px solid #eee;
}

.setting-info h4 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.setting-info p {
  font-size: 13px;
  color: #666;
  margin: 0;
}

/* 开关样式 */
.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 26px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: 0.3s;
  border-radius: 26px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.3s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #FFB5D8;
}

input:checked + .slider:before {
  transform: translateX(24px);
}

.select-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  min-width: 150px;
}

/* 列表样式 */
.order-list,
.favorite-list,
.history-list,
.message-list,
.application-list,
.publish-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 搜索栏样式 */
.search-bar {
  margin-bottom: 20px;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
  max-width: 600px;
}

.search-icon {
  position: absolute;
  left: 15px;
  color: #999;
  font-size: 16px;
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: 12px 15px 12px 45px;
  border: 2px solid #e0e0e0;
  border-radius: 25px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s;
  background: #f8f9fa;
}

.search-input:focus {
  border-color: #FF6B9D;
  background: white;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.15);
}

.search-input::placeholder {
  color: #999;
}

.search-clear {
  position: absolute;
  right: 10px;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #e0e0e0;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
}

.search-clear:hover {
  background: #FF6B9D;
  color: white;
  transform: scale(1.1);
}

/* 发布列表项样式 */
.publish-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s;
}

.publish-item:hover {
  border-color: #FF6B9D;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.1);
  transform: translateY(-2px);
}

.publish-content {
  flex: 1;
}

.publish-content h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.publish-content p {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  line-height: 1.6;
}

.publish-meta {
  display: flex;
  gap: 15px;
  align-items: center;
  font-size: 13px;
  color: #999;
}

.publish-meta .status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-active {
  background: rgba(76, 175, 80, 0.1);
  color: #4caf50;
}

.status-inactive {
  background: rgba(158, 158, 158, 0.1);
  color: #9e9e9e;
}

.order-item,
.favorite-item,
.history-item,
.message-item,
.application-item,
.publish-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #eee;
  transition: all 0.3s;
}

.order-item:hover,
.favorite-item:hover,
.history-item:hover,
.message-item:hover,
.application-item:hover,
.publish-item:hover {
  transform: translateX(5px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-type {
  padding: 4px 10px;
  background: #FFB5D8;
  color: white;
  border-radius: 12px;
  font-size: 12px;
  white-space: nowrap;
}

.order-content,
.favorite-content,
.history-content,
.message-content,
.application-content {
  flex: 1;
}

.order-content h4,
.favorite-content h4,
.history-content h4,
.message-content h4,
.application-content h4 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.order-content p,
.favorite-content p,
.history-content p,
.message-content p,
.application-content p {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.order-meta,
.favorite-meta,
.history-meta,
.message-meta,
.application-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #999;
}

.message-item {
  position: relative;
}

.message-item.unread::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 60%;
  background: #FFB5D8;
  border-radius: 0 2px 2px 0;
}

.message-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #FFF0F5;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FF6B9D;
  font-size: 18px;
}

/* 筛选标签样式 */
.filter-tabs {
  display: flex;
  gap: 10px;
}

.tab-btn {
  padding: 8px 16px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s;
}

.tab-btn:hover {
  border-color: #FFB5D8;
  color: #FF6B9D;
}

.tab-btn.active {
  background: #FFB5D8;
  color: white;
  border-color: #FFB5D8;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state i {
  font-size: 48px;
  color: #ddd;
  margin-bottom: 15px;
}

.empty-state p {
  font-size: 14px;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 12px;
  color: #bbb;
  margin-bottom: 20px;
}

/* 说明框样式 */
.notice-box {
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.notice-box h4 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  margin-top: 15px;
}

.notice-box h4:first-child {
  margin-top: 0;
}

.notice-box p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.modal-close:hover {
  background: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 25px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .page-container {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .sidebar-nav {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .nav-item {
    flex: 1;
    min-width: 120px;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
  }

  .filter-tabs {
    flex-wrap: wrap;
  }

  .alert-box {
    flex-direction: column;
  }

  .alert-actions {
    width: 100%;
    flex-direction: column;
  }

  .order-item,
  .favorite-item,
  .history-item,
  .message-item,
  .application-item {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
