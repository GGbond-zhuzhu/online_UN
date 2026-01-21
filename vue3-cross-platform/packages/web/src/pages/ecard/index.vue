<template>
  <div class="ecard-page">
    <!-- 全局导航栏 -->
    <NavBar />

    <div class="main-container">
      <!-- 校园身份认证区域（在校内但未认证时显示） -->
      <div class="auth-section" v-if="showAuthForm && isInCampus && !isAuthenticated">
        <div class="card auth-card">
        <div class="auth-header">
          <h2 class="auth-title">校园身份认证</h2>
          <p class="auth-subtitle">请选择您的身份类型并填写相关信息</p>
        </div>
        <form @submit.prevent="handleAuthSubmit">
          <div class="form-group">
            <label class="form-label">身份类型</label>
            <select class="form-select" v-model="authForm.userRole" required>
              <option value="">请选择身份</option>
              <option value="student">在校学生</option>
              <option value="teacher">教职工</option>
              <option value="visitor">访客/临时用户</option>
            </select>
          </div>

          <!-- 学生字段 -->
          <div class="form-group" v-if="authForm.userRole === 'student'">
            <div class="form-row">
              <div class="form-group">
                <label class="form-label">学号</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="authForm.studentId"
                  placeholder="请输入学号"
                  required
                />
              </div>
              <div class="form-group">
                <label class="form-label">姓名</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="authForm.studentName"
                  placeholder="请输入姓名"
                  required
                />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">所在学院</label>
              <input
                type="text"
                class="form-control"
                v-model="authForm.studentCollege"
                placeholder="请输入学院"
                required
              />
            </div>
          </div>

          <!-- 教师字段 -->
          <div class="form-group" v-if="authForm.userRole === 'teacher'">
            <div class="form-row">
              <div class="form-group">
                <label class="form-label">工号</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="authForm.teacherId"
                  placeholder="请输入工号"
                  required
                />
              </div>
              <div class="form-group">
                <label class="form-label">姓名</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="authForm.teacherName"
                  placeholder="请输入姓名"
                  required
                />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">所在部门</label>
              <input
                type="text"
                class="form-control"
                v-model="authForm.teacherDepartment"
                placeholder="请输入部门"
                required
              />
            </div>
          </div>

          <!-- 访客字段 -->
          <div class="form-group" v-if="authForm.userRole === 'visitor'">
            <div class="form-group">
              <label class="form-label">手机号码</label>
              <input
                type="tel"
                class="form-control"
                v-model="authForm.visitorPhone"
                placeholder="请输入手机号码"
                required
              />
            </div>
            <div class="form-group">
              <label class="form-label">身份证号</label>
              <input
                type="text"
                class="form-control"
                v-model="authForm.visitorIdCard"
                placeholder="请输入身份证号"
                required
              />
            </div>
            <div class="form-group">
              <label class="form-label">来访事由</label>
              <textarea
                class="form-control"
                v-model="authForm.visitorReason"
                placeholder="请简要说明来访事由"
                rows="2"
                required
              ></textarea>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">所在校区</label>
            <select class="form-select" v-model="authForm.campusLocation" required>
              <option value="">请选择校区</option>
              <option value="main">主校区</option>
              <option value="east">东校区</option>
              <option value="west">西校区</option>
              <option value="south">南校区</option>
              <option value="north">北校区</option>
            </select>
          </div>

          <button type="submit" class="btn btn-primary btn-block" :disabled="authLoading">
            <i class="fas fa-check-circle"></i>
            {{ authLoading ? '认证中...' : '确认认证' }}
          </button>
        </form>
      </div>
    </div>

      <!-- 定位检测中提示 -->
      <div class="loading-section" v-if="isLocationChecking">
        <div class="card">
          <div style="text-align: center; padding: 40px;">
            <i class="fas fa-map-marker-alt" style="font-size: 48px; color: #d81b60; margin-bottom: 20px;"></i>
            <h3 style="color: #333; margin-bottom: 10px;">正在检测定位...</h3>
            <p style="color: #666;">请允许浏览器获取您的位置信息</p>
          </div>
        </div>
      </div>

      <!-- 正常用户界面 -->
      <div class="main-content-wrapper" v-if="showMainInterface && !isLocationChecking">
        <!-- 主要内容区域 -->
        <div class="main-content">
          <!-- 顶部状态栏（参考app端） -->
          <div class="top-status-bar">
            <div class="location-info" @click="handleLocationSelect">
              <i class="fas fa-map-marker-alt location-icon"></i>
              <span class="location-text">{{ campusLocation }}</span>
              <i class="fas fa-chevron-down arrow-icon"></i>
            </div>
            <div class="weather-info">
              <i :class="weatherIcon" class="weather-icon"></i>
              <span class="weather-text">{{ weatherDesc }}</span>
            </div>
          </div>

          <!-- 账户余额卡片（增强版） -->
          <div class="card balance-card">
            <div class="balance-header">
              <div class="balance-title-section">
                <i class="fas fa-credit-card card-icon"></i>
                <div class="balance-titles">
                  <div class="balance-label">校园E卡通</div>
                  <div class="card-number">NO. {{ formattedCardNo }}</div>
                </div>
              </div>
              <div class="balance-actions-header">
                <button class="action-btn" @click="goToCardDetail" title="卡片详情">
                  <i class="fas fa-info-circle"></i>
                </button>
                <button class="action-btn" @click="goToSettings" title="设置">
                  <i class="fas fa-cog"></i>
                </button>
              </div>
            </div>
            <div class="balance-content">
              <div class="balance-amount">¥ {{ formattedBalance }}</div>
              <div class="balance-desc">可用余额</div>
              <div class="balance-sub-info">
                <div class="sub-item">
                  <i class="fas fa-history"></i>
                  <span class="sub-text">上次充值：{{ lastRechargeTime }}</span>
                </div>
                <div class="sub-item">
                  <i class="fas fa-sync-alt"></i>
                  <span class="sub-text">今日消费：{{ todayConsume }}</span>
                </div>
              </div>
            </div>
            <div class="balance-footer">
              <button class="btn btn-primary recharge-btn" @click="handleRecharge">
                <i class="fas fa-plus-circle"></i>
                <span>立即充值</span>
              </button>
              <button class="btn btn-outline transfer-btn" @click="handleTransfer">
                <i class="fas fa-exchange-alt"></i>
                <span>转账</span>
              </button>
            </div>
          </div>

        <!-- 消费目标卡片（新增，参考app端） -->
        <div class="card goal-card">
          <div class="goal-header">
            <div class="goal-title-section">
              <i class="fas fa-bullseye goal-icon"></i>
              <span class="goal-title">本月消费目标</span>
            </div>
            <button class="goal-edit" @click="goToGoalSettings" title="设置目标">
              <i class="fas fa-edit"></i>
            </button>
          </div>
          <div class="goal-content">
            <div class="goal-progress">
              <div class="goal-progress-bar">
                <div class="goal-progress-fill" :style="{ width: goalProgress + '%' }"></div>
              </div>
              <div class="goal-info">
                <span class="goal-current">已消费 ¥{{ consumeGoal.current.toFixed(2) }}</span>
                <span class="goal-target">目标 ¥{{ consumeGoal.monthly.toFixed(2) }}</span>
              </div>
              <div class="goal-percent">{{ goalProgress.toFixed(0) }}%</div>
            </div>
          </div>
        </div>

        <!-- 本月消费统计（增强版，参考app端） -->
        <div class="card stats-card">
          <div class="stat-header">
            <div class="stat-title-section">
              <i class="fas fa-chart-pie stat-icon"></i>
              <span class="stat-title">本月消费统计</span>
            </div>
            <div class="stat-period">
              <i class="fas fa-calendar-alt"></i>
              <span>{{ currentMonth }}月</span>
            </div>
          </div>
          <div class="stat-content">
            <div class="stat-grid">
              <div class="stat-item" v-for="item in statItems" :key="item.id">
                <div class="stat-icon-circle" :style="{ background: item.color }">
                  <i :class="item.icon"></i>
                </div>
                <div class="stat-data">
                  <div class="stat-value">{{ item.value }}</div>
                  <div class="stat-label">{{ item.label }}</div>
                </div>
              </div>
            </div>
            <!-- 消费图表（参考app端） -->
            <div class="stat-chart">
              <div class="chart-bars">
                <div 
                  v-for="(bar, index) in consumeBars" 
                  :key="index"
                  class="chart-bar"
                  :style="{ height: bar.height, background: bar.color }"
                  :title="bar.label"
                >
                  <span class="bar-label">{{ bar.label }}</span>
                </div>
              </div>
              <div class="chart-legend">
                <div 
                  v-for="(legend, index) in chartLegends" 
                  :key="index"
                  class="legend-item"
                >
                  <div class="legend-color" :style="{ background: legend.color }"></div>
                  <span class="legend-text">{{ legend.text }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="stat-footer">
            <div class="stat-tips">
              <i class="fas fa-lightbulb"></i>
              <span>较上月{{ compareText }}</span>
            </div>
            <a href="#" @click.prevent="goToStatDetail" class="view-detail">
              <span>详细分析</span>
              <i class="fas fa-chevron-right"></i>
            </a>
          </div>
        </div>

        <!-- 我的付款码 -->
        <div class="card qrcode-card">
          <div class="card-header">
            <h2 class="card-title">我的付款码</h2>
            <div class="qrcode-status">
              <div class="status-indicator"></div>
              <span>动态更新中</span>
            </div>
          </div>
          <div class="qrcode-section">
            <div class="qrcode-display">
              <canvas 
                ref="qrcodeCanvas" 
                class="qrcode-canvas"
                width="200"
                height="200"
              ></canvas>
              <div v-if="!qrcodeData" class="qrcode-loading">
                <i class="fas fa-qrcode"></i>
                <p>生成中...</p>
              </div>
            </div>
            <div class="qrcode-info">
              <h3>动态付款码</h3>
              <p>
                此二维码每分钟自动更新，保障账户安全。支持校园消费、门禁通行、图书借阅等场景。
              </p>
              <div class="qrcode-status">
                <div class="status-indicator"></div>
                <span>GPS+WiFi定位已开启</span>
              </div>
              <p class="qrcode-meta">有效期：<span class="countdown">{{ countdown }}秒</span></p>
              <p class="qrcode-meta">当前位置：<span class="location">{{ currentLocation }}</span></p>
              <div class="qrcode-actions">
                <button class="btn btn-primary" @click="refreshQrCode">
                  <i class="fas fa-sync-alt"></i> 刷新二维码
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 最近交易（增强版，添加筛选功能） -->
        <div class="card transaction-card">
          <div class="transactions-header">
            <div class="transactions-title-section">
              <i class="fas fa-history transactions-icon"></i>
              <span class="transactions-title">最近交易</span>
            </div>
            <button class="transactions-filter" @click="showFilter = true">
              <i class="fas fa-filter"></i>
              <span>筛选</span>
            </button>
          </div>
          <!-- 已选筛选条件标签 -->
          <div class="filter-tags" v-if="hasActiveFilters">
            <div 
              v-for="(tag, index) in activeFilterTags" 
              :key="index"
              class="filter-tag"
              @click="removeFilterTag(tag.key)"
            >
              <span>{{ tag.label }}</span>
              <i class="fas fa-times"></i>
            </div>
          </div>
          <div class="transaction-list">
            <div
              class="transaction-item"
              v-for="record in filteredTransactions"
              :key="record.id"
              @click="goToTransactionDetail(record.id)"
            >
              <div class="transaction-info">
                <div
                  class="transaction-icon"
                  :style="{ background: getTransactionColor(record.consumeType) }"
                >
                  <i :class="getTransactionIcon(record.consumeType)"></i>
                </div>
                <div class="transaction-details">
                  <div class="transaction-title">{{ record.merchantName }}</div>
                  <div class="transaction-meta">
                    {{ formatDateTime(record.consumeTime) }} · {{ record.description }}
                  </div>
                </div>
              </div>
              <div class="transaction-right">
                <div
                  class="transaction-amount"
                  :class="record.amount > 0 ? 'amount-positive' : 'amount-negative'"
                >
                  {{ record.amount > 0 ? '+' : '' }}¥{{ Math.abs(record.amount).toFixed(2) }}
                </div>
                <i class="fas fa-chevron-right arrow-right"></i>
              </div>
            </div>
            <div v-if="filteredTransactions.length === 0" class="empty-state">
              <i class="fas fa-inbox"></i>
              <p>暂无交易记录</p>
            </div>
          </div>
          <div class="transactions-footer" @click="showAllRecords = true">
            <span>查看全部交易记录</span>
            <i class="fas fa-arrow-right"></i>
          </div>
        </div>
        </div>

        <!-- 侧边栏（功能菜单） -->
        <aside class="sidebar">
          <!-- 快捷功能 -->
          <div class="sidebar-widget">
            <h3 class="widget-title">
              <i class="fas fa-th"></i>
              快捷功能
            </h3>
            <div class="function-list">
              <div 
                v-for="func in quickFunctions" 
                :key="func.id"
                class="function-item"
                @click="handleFunctionClick(func.id)"
              >
                <div class="function-icon" :style="{ background: func.color }">
                  <i :class="func.icon"></i>
                </div>
                <span class="function-name">{{ func.name }}</span>
                <i class="fas fa-chevron-right function-arrow"></i>
              </div>
            </div>
          </div>

          <!-- 快捷操作 -->
          <div class="sidebar-widget">
            <h3 class="widget-title">
              <i class="fas fa-bolt"></i>
              快捷操作
            </h3>
            <div class="quick-action">
              <a href="#" @click.prevent="handleRecharge" class="action-button primary">
                <i class="fas fa-plus-circle"></i>
                <span>立即充值</span>
              </a>
              <a href="#" @click.prevent="handleTransfer" class="action-button">
                <i class="fas fa-exchange-alt"></i>
                <span>转账</span>
              </a>
              <a href="#" @click.prevent="showAllRecords = true" class="action-button">
                <i class="fas fa-receipt"></i>
                <span>消费记录</span>
              </a>
              <a href="#" @click.prevent="goToAccountBook" class="action-button">
                <i class="fas fa-book-open"></i>
                <span>记账本</span>
              </a>
            </div>
          </div>

          <!-- 余额提醒 -->
          <div class="sidebar-widget reminder-widget" v-if="balanceReminder.enabled">
            <h3 class="widget-title">
              <i class="fas fa-bell"></i>
              余额提醒
            </h3>
            <div class="reminder-content-sidebar">
              <p class="reminder-text-sidebar">
                当余额低于 <span class="reminder-amount">¥{{ balanceReminder.threshold.toFixed(2) }}</span> 时将提醒您
              </p>
              <button class="reminder-toggle-sidebar" @click="toggleReminder" title="关闭提醒">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>

          <!-- 使用提示 -->
          <div class="sidebar-widget tips-widget">
            <h3 class="widget-title">
              <i class="fas fa-info-circle"></i>
              使用提示
            </h3>
            <div class="safety-tips">
              <div class="tip-item" v-for="(tip, idx) in tips" :key="idx">
                <i class="fas fa-check-circle"></i>
                <span>{{ tip }}</span>
              </div>
            </div>
          </div>
        </aside>
      </div>

      <!-- 游客界面（不在校内时显示） -->
      <div class="guest-interface" v-if="showGuestInterface && !isLocationChecking">
        <!-- 临时校园卡申请 -->
        <div class="card temp-card-form-card">
          <div class="card-header">
            <h2 class="card-title">临时校园卡申请</h2>
          </div>
          <form @submit.prevent="handleTempCardSubmit" class="temp-card-form">
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">姓名</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="tempCardForm.name"
                    placeholder="请输入真实姓名"
                    required
                  />
                </div>
                <div class="form-group">
                  <label class="form-label">手机号码</label>
                  <input
                    type="tel"
                    class="form-control"
                    v-model="tempCardForm.phone"
                    placeholder="请输入手机号码"
                    required
                  />
                </div>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">身份证号</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="tempCardForm.idCard"
                    placeholder="请输入身份证号"
                    required
                  />
                </div>
                <div class="form-group">
                  <label class="form-label">来访事由</label>
                  <select class="form-select" v-model="tempCardForm.purpose" required>
                    <option value="">请选择来访事由</option>
                    <option value="visit">参观访问</option>
                    <option value="business">商务合作</option>
                    <option value="exam">考试</option>
                    <option value="other">其他</option>
                  </select>
                </div>
              </div>
              <div class="form-group">
                <label class="form-label">预计停留时间</label>
              <select class="form-select" v-model="tempCardForm.duration" required>
                <option value="">请选择停留时间</option>
                <option value="1">1天</option>
                <option value="3">3天</option>
                <option value="7">7天</option>
                <option value="30">30天</option>
              </select>
              </div>
              <div class="form-group">
                <label class="form-label">初始充值金额</label>
                <input
                  type="number"
                  class="form-control"
                  v-model.number="tempCardForm.initialAmount"
                  placeholder="请输入充值金额"
                  min="10"
                  max="1000"
                  value="100"
                  required
                />
                <div class="form-text">最低充值10元，最高1000元</div>
              </div>
              <button type="submit" class="btn btn-primary btn-block" :disabled="tempCardLoading">
                <i class="fas fa-credit-card"></i>
                {{ tempCardLoading ? '申请中...' : '申请临时校园卡' }}
              </button>
            </form>
        </div>

        <!-- 临时卡信息（申请成功后显示） -->
        <div class="card balance-card" v-if="tempCardInfo">
          <div class="balance-content">
            <div class="balance-label">临时卡余额</div>
            <div class="balance-amount">¥ {{ tempCardInfo.balance?.toFixed(2) || '0.00' }}</div>
            <div class="balance-meta" v-if="tempCardInfo.visitorExpireTime">
              有效期至：{{ formatDate(tempCardInfo.visitorExpireTime) }}
            </div>
            <div class="balance-meta" v-else>--</div>
          </div>
          <div class="balance-actions">
            <button class="btn btn-primary" @click="handleRecharge">
              <i class="fas fa-plus"></i> 立即充值
            </button>
            <button class="btn btn-outline" @click="showAllRecords = true">
              <i class="fas fa-exchange-alt"></i> 交易记录
            </button>
          </div>
        </div>

        <!-- 临时卡付款码 -->
        <div class="card qrcode-card" v-if="tempCardInfo">
          <div class="card-header">
            <h2 class="card-title">临时卡付款码</h2>
            <div class="qrcode-status">
              <div class="status-indicator"></div>
              <span>动态更新中</span>
            </div>
          </div>
          <div class="qrcode-section">
            <div class="qrcode-display">
              <canvas 
                ref="tempQrcodeCanvas" 
                class="qrcode-canvas"
                width="200"
                height="200"
              ></canvas>
              <div v-if="!tempQrcodeData" class="qrcode-loading">
                <i class="fas fa-qrcode"></i>
                <p>生成中...</p>
              </div>
            </div>
            <div class="qrcode-info">
              <h3>临时卡动态付款码</h3>
              <p>此二维码每分钟自动更新，仅限在授权区域内使用。</p>
              <div class="qrcode-status">
                <div class="status-indicator"></div>
                <span>临时卡状态：正常</span>
              </div>
              <p class="qrcode-meta">有效期：<span class="countdown">{{ countdown }}秒</span></p>
              <p class="qrcode-meta">剩余金额：<span class="balance">¥ {{ tempCardInfo.balance?.toFixed(2) || '0.00' }}</span></p>
              <div class="qrcode-actions">
                <button class="btn btn-primary" @click="refreshQrCode">
                  <i class="fas fa-sync-alt"></i> 刷新二维码
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 临时卡交易记录 -->
        <div class="card transaction-card" v-if="tempCardInfo">
          <div class="card-header">
            <h2 class="card-title">临时卡交易记录</h2>
      </div>
          <div class="transaction-list">
            <div
              class="transaction-item"
              v-for="record in tempCardRecords"
              :key="record.id"
            >
              <div class="transaction-info">
                <div
                  class="transaction-icon"
                  :style="{ background: getTransactionColor(record.consumeType) }"
                >
                  <i :class="getTransactionIcon(record.consumeType)"></i>
    </div>
                <div class="transaction-details">
                  <div class="transaction-title">{{ record.merchantName }}</div>
                  <div class="transaction-meta">
                    {{ formatDateTime(record.consumeTime) }} · {{ record.description }}
      </div>
      </div>
    </div>
              <div
                class="transaction-amount"
                :class="record.amount > 0 ? 'amount-positive' : 'amount-negative'"
              >
                {{ record.amount > 0 ? '+' : '' }}¥{{ Math.abs(record.amount).toFixed(2) }}
      </div>
            </div>
            <div v-if="tempCardRecords.length === 0" class="empty-state">
              <i class="fas fa-inbox"></i>
              <p>暂无交易记录</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选弹窗（参考app端） -->
    <div v-if="showFilter" class="filter-modal-overlay" @click="showFilter = false">
      <div class="filter-modal" @click.stop>
        <div class="filter-header">
          <span class="filter-title">交易筛选</span>
          <i class="fas fa-times filter-close" @click="showFilter = false"></i>
        </div>
        
        <div class="filter-content">
          <!-- 时间筛选 -->
          <div class="filter-section">
            <div class="section-title">
              <i class="fas fa-calendar-alt section-icon"></i>
              <span>时间范围</span>
            </div>
            <div class="filter-options">
              <div 
                v-for="item in timeFilterOptions" 
                :key="item.value"
                class="filter-option"
                :class="{ active: transactionFilter.timeRange === item.value }"
                @click="transactionFilter.timeRange = item.value"
              >
                <span>{{ item.label }}</span>
              </div>
            </div>
            <!-- 自定义时间 -->
            <div v-if="transactionFilter.timeRange === 'custom'" class="custom-time-section">
              <div class="date-time-row">
                <span class="date-label">开始时间</span>
                <input type="date" class="date-picker-value" v-model="transactionFilter.customStartDate" />
                <input type="time" class="time-picker-value" v-model="transactionFilter.customStartTime" />
              </div>
              <div class="date-time-row">
                <span class="date-label">结束时间</span>
                <input type="date" class="date-picker-value" v-model="transactionFilter.customEndDate" />
                <input type="time" class="time-picker-value" v-model="transactionFilter.customEndTime" />
              </div>
            </div>
          </div>

          <!-- 交易类型筛选 -->
          <div class="filter-section">
            <div class="section-title">
              <i class="fas fa-exchange-alt section-icon"></i>
              <span>交易类型</span>
            </div>
            <div class="filter-options">
              <div 
                v-for="item in transactionTypeOptions" 
                :key="item.value"
                class="filter-option"
                :class="{ active: transactionFilter.transactionType === item.value }"
                @click="transactionFilter.transactionType = item.value"
              >
                <i :class="item.icon"></i>
                <span>{{ item.label }}</span>
              </div>
            </div>
          </div>

          <!-- 消费类型筛选 -->
          <div class="filter-section">
            <div class="section-title">
              <i class="fas fa-tags section-icon"></i>
              <span>消费类型</span>
            </div>
            <div class="filter-options">
              <div 
                v-for="item in consumeTypeOptions" 
                :key="item.value"
                class="filter-option"
                :class="{ active: transactionFilter.consumeType === item.value }"
                @click="transactionFilter.consumeType = item.value"
              >
                <i :class="item.icon"></i>
                <span>{{ item.label }}</span>
              </div>
            </div>
          </div>

          <!-- 金额范围筛选 -->
          <div class="filter-section">
            <div class="section-title">
              <i class="fas fa-dollar-sign section-icon"></i>
              <span>金额范围</span>
            </div>
            <div class="amount-range-section">
              <div class="amount-input-row">
                <span class="amount-label">最小金额</span>
                <input 
                  class="amount-input" 
                  v-model="transactionFilter.minAmount"
                  type="number"
                  placeholder="0"
                />
              </div>
              <div class="amount-separator">至</div>
              <div class="amount-input-row">
                <span class="amount-label">最大金额</span>
                <input 
                  class="amount-input" 
                  v-model="transactionFilter.maxAmount"
                  type="number"
                  placeholder="不限"
                />
              </div>
            </div>
            <!-- 快捷金额 -->
            <div class="quick-amount-tags">
              <div 
                v-for="amount in quickAmountTags" 
                :key="amount"
                class="amount-tag"
                :class="{ active: isQuickAmountActive(amount) }"
                @click="setQuickAmount(amount)"
              >
                <span>¥{{ amount }}</span>
              </div>
            </div>
          </div>

          <!-- 地点筛选 -->
          <div class="filter-section">
            <div class="section-title">
              <i class="fas fa-map-marker-alt section-icon"></i>
              <span>地点</span>
            </div>
            <input 
              class="filter-input" 
              v-model="transactionFilter.location"
              placeholder="输入地点名称（如：第一食堂、校园超市）"
            />
          </div>

          <!-- 排序方式 -->
          <div class="filter-section">
            <div class="section-title">
              <i class="fas fa-sort section-icon"></i>
              <span>排序方式</span>
            </div>
            <div class="filter-options">
              <div 
                v-for="item in sortOptions" 
                :key="item.value"
                class="filter-option"
                :class="{ active: transactionFilter.sortBy === item.value }"
                @click="transactionFilter.sortBy = item.value"
              >
                <i :class="item.icon"></i>
                <span>{{ item.label }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="filter-footer">
          <button class="filter-btn reset-btn" @click="resetTransactionFilter">重置</button>
          <button class="filter-btn confirm-btn" @click="applyTransactionFilter">确定</button>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <AppFooter />

    <!-- 悬浮菜单 -->
    <FloatingMenu />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import QRCode from 'qrcode'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
import { useUserStore } from '@campus/common'
import {
  getEcardInfo,
  getConsumeRecords,
  applyVisitorCard,
  getTodayStatistics,
  generateDynamicCode,
  checkLocation,
  recharge,
  type EcardInfo,
  type ConsumeRecord,
  type VisitorCardApplyParams,
  type TodayStatisticsResponse
} from '@campus/common'

const router = useRouter()
const userStore = useUserStore()

// 页面状态
const showMainInterface = ref(false)
const showGuestInterface = ref(false)
const showAuthForm = ref(false) // 显示认证表单
const showAllRecords = ref(false)
const showFilter = ref(false) // 显示筛选弹窗
const authLoading = ref(false)
const tempCardLoading = ref(false)
const isLocationChecking = ref(true) // 定位检测中
const isInCampus = ref(false) // 是否在校内

// 校区位置和天气（参考app端）
const campusLocation = ref('东校区')
const weatherIcon = ref('fas fa-sun')
const weatherDesc = ref('晴 22°C')

// 数据
const ecardInfo = ref<EcardInfo>({
  cardNo: '',
  userId: null,
  userName: '',
  balance: 0,
  status: '',
  isVisitorCard: false,
  todayConsumeCount: 0,
  todayConsumeAmount: 0
})
const recentRecords = ref<ConsumeRecord[]>([])
const tempCardRecords = ref<ConsumeRecord[]>([])
const todayStats = ref<TodayStatisticsResponse>({
  consumeCount: 0,
  consumeAmount: 0,
  averageConsume: 0,
  mostFrequentType: '',
  mostFrequentCount: 0
})

// 当前月份
const currentMonth = new Date().getMonth() + 1

// 快捷功能列表（参考app端，移到侧边栏）
const quickFunctions = ref([
  { id: 'scan', name: '扫码支付', icon: 'fas fa-qrcode', color: '#FFB5D8' },
  { id: 'location', name: '智能定位', icon: 'fas fa-map-marker-alt', color: '#A8E6CF' },
  { id: 'security', name: '安全中心', icon: 'fas fa-shield-alt', color: '#FFD3B6' },
  { id: 'records', name: '消费记录', icon: 'fas fa-receipt', color: '#C7CEEA' },
  { id: 'canteen', name: '食堂消费', icon: 'fas fa-utensils', color: '#FFAAA5' },
  { id: 'library', name: '图书馆', icon: 'fas fa-book', color: '#B5EAD7' },
  { id: 'account-book', name: '记账本', icon: 'fas fa-book-open', color: '#E8B4CB' },
  { id: 'diet-record', name: '饮食表', icon: 'fas fa-apple-alt', color: '#D4A5F5' },
  { id: 'bus', name: '校车支付', icon: 'fas fa-bus', color: '#FFE5B4' },
  { id: 'more', name: '更多服务', icon: 'fas fa-ellipsis-h', color: '#B8E6E6' }
])

// 使用提示
const tips = ref([
  '二维码每分钟自动更新',
  '支持校园消费、门禁通行',
  '保留交易凭证便于查询',
  '遇到问题及时反馈'
])

// 本月统计（参考app端）
const statItems = ref([
  { id: 1, label: '本月消费', value: '¥245.80', icon: 'fas fa-money-bill-wave', color: '#FFB5D8' },
  { id: 2, label: '交易次数', value: '28笔', icon: 'fas fa-exchange-alt', color: '#A8E6CF' },
  { id: 3, label: '食堂占比', value: '65%', icon: 'fas fa-chart-pie', color: '#FFD3B6' },
  { id: 4, label: '日均消费', value: '¥8.78', icon: 'fas fa-calendar-day', color: '#C7CEEA' }
])

// 消费图表数据（参考app端）
const consumeBars = ref([
  { height: '60%', color: '#FFB5D8', label: '食堂' },
  { height: '30%', color: '#A8E6CF', label: '超市' },
  { height: '45%', color: '#FFD3B6', label: '图书馆' },
  { height: '25%', color: '#C7CEEA', label: '校车' },
  { height: '20%', color: '#FFE5B4', label: '其他' }
])

const chartLegends = ref([
  { color: '#FFB5D8', text: '食堂消费' },
  { color: '#A8E6CF', text: '超市购物' },
  { color: '#FFD3B6', text: '图书借阅' },
  { color: '#C7CEEA', text: '交通出行' },
  { color: '#FFE5B4', text: '其他消费' }
])

const compareText = ref('下降5%')

// 消费目标（参考app端）
interface ConsumeGoal {
  monthly: number
  current: number
  progress: number
}

const consumeGoal = ref<ConsumeGoal>({
  monthly: 500.00,
  current: 245.80,
  progress: 0
})

// 余额提醒（参考app端）
const balanceReminder = ref({
  enabled: true,
  threshold: 50.00,
  message: '余额不足提醒'
})

// 交易筛选条件（参考app端）
const transactionFilter = ref({
  timeRange: 'all', // all, today, 7days, month, custom
  customStartDate: '',
  customStartTime: '00:00',
  customEndDate: '',
  customEndTime: '23:59',
  transactionType: 'all', // all, income, expense
  consumeType: 'all', // all, DINING, SUPERMARKET, LIBRARY, UTILITY, OTHER
  minAmount: '',
  maxAmount: '',
  location: '',
  sortBy: 'time' // time, amount
})

// 时间筛选选项
const timeFilterOptions = [
  { label: '全部', value: 'all' },
  { label: '今天', value: 'today' },
  { label: '近7天', value: '7days' },
  { label: '本月', value: 'month' },
  { label: '自定义', value: 'custom' }
]

// 交易类型选项
const transactionTypeOptions = [
  { label: '全部', value: 'all', icon: 'fas fa-list' },
  { label: '收入', value: 'income', icon: 'fas fa-arrow-down' },
  { label: '支出', value: 'expense', icon: 'fas fa-arrow-up' }
]

// 消费类型选项
const consumeTypeOptions = [
  { label: '全部', value: 'all', icon: 'fas fa-list' },
  { label: '食堂', value: 'DINING', icon: 'fas fa-utensils' },
  { label: '超市', value: 'SUPERMARKET', icon: 'fas fa-shopping-cart' },
  { label: '图书馆', value: 'LIBRARY', icon: 'fas fa-book' },
  { label: '水电', value: 'UTILITY', icon: 'fas fa-bolt' },
  { label: '其他', value: 'OTHER', icon: 'fas fa-ellipsis-h' }
]

// 排序选项
const sortOptions = [
  { label: '时间', value: 'time', icon: 'fas fa-clock' },
  { label: '金额', value: 'amount', icon: 'fas fa-dollar-sign' }
]

// 快捷金额标签
const quickAmountTags = [10, 50, 100, 200, 500, 1000]

const tempCardInfo = ref<EcardInfo | null>(null)
const currentLocation = ref('定位中...')
const countdown = ref(59)

// 二维码相关
const qrcodeCanvas = ref<HTMLCanvasElement | null>(null)
const tempQrcodeCanvas = ref<HTMLCanvasElement | null>(null)
const qrcodeData = ref<string>('')
const tempQrcodeData = ref<string>('')
const dynamicCode = ref<string>('')

// 认证表单
const authForm = ref({
  userRole: '',
  studentId: '',
  studentName: '',
  studentCollege: '',
  teacherId: '',
  teacherName: '',
  teacherDepartment: '',
  visitorPhone: '',
  visitorIdCard: '',
  visitorReason: '',
  campusLocation: ''
})

// 临时卡表单
const tempCardForm = ref({
  name: '',
  phone: '',
  idCard: '',
  purpose: '',
  duration: '',
  initialAmount: 100
})

// 计算属性
const isAuthenticated = computed(() => {
  return userStore.isLoggedIn && !userStore.isVisitor
})

// 格式化卡号（参考app端）
const formattedCardNo = computed(() => {
  const cardNo = ecardInfo.value?.cardNo || '202400123456'
  return cardNo.replace(/(\d{4})(?=\d)/g, '$1 ')
})

// 格式化余额（参考app端）
const formattedBalance = computed(() => {
  return (ecardInfo.value?.balance ?? 328.50).toFixed(2)
})

// 上次充值时间（参考app端）
const lastRechargeTime = computed(() => {
  const time = (ecardInfo.value as any)?.lastRechargeTime
  return time || '2024-01-10'
})

// 今日消费（参考app端）
const todayConsume = computed(() => {
  return `¥${(todayStats.value?.consumeAmount ?? 12.50).toFixed(2)}`
})

// 消费进度（参考app端）
const goalProgress = computed<number>(() => {
  return Math.min((consumeGoal.value.current / consumeGoal.value.monthly) * 100, 100)
})

// 筛选后的交易记录（参考app端）
const filteredTransactions = computed(() => {
  let result = [...recentRecords.value]
  
  // 交易类型筛选
  if (transactionFilter.value.transactionType === 'income') {
    result = result.filter(trans => trans.amount > 0)
  } else if (transactionFilter.value.transactionType === 'expense') {
    result = result.filter(trans => trans.amount < 0)
  }
  
  // 消费类型筛选
  if (transactionFilter.value.consumeType !== 'all') {
    result = result.filter(trans => trans.consumeType === transactionFilter.value.consumeType)
  }
  
  // 金额范围筛选
  if (transactionFilter.value.minAmount) {
    const min = parseFloat(transactionFilter.value.minAmount)
    result = result.filter(trans => Math.abs(trans.amount) >= min)
  }
  if (transactionFilter.value.maxAmount) {
    const max = parseFloat(transactionFilter.value.maxAmount)
    result = result.filter(trans => Math.abs(trans.amount) <= max)
  }
  
  // 地点筛选
  if (transactionFilter.value.location) {
    result = result.filter(trans => 
      trans.merchantName.includes(transactionFilter.value.location) ||
      trans.description.includes(transactionFilter.value.location)
    )
  }
  
  // 排序
  if (transactionFilter.value.sortBy === 'amount') {
    result.sort((a, b) => Math.abs(b.amount) - Math.abs(a.amount))
  } else {
    result.sort((a, b) => new Date(b.consumeTime).getTime() - new Date(a.consumeTime).getTime())
  }
  
  return result
})

// 已选筛选条件标签（参考app端）
const hasActiveFilters = computed(() => {
  return transactionFilter.value.timeRange !== 'all' ||
         transactionFilter.value.transactionType !== 'all' ||
         transactionFilter.value.consumeType !== 'all' ||
         transactionFilter.value.minAmount ||
         transactionFilter.value.maxAmount ||
         transactionFilter.value.location
})

const activeFilterTags = computed(() => {
  const tags: Array<{ key: string; label: string }> = []
  
  if (transactionFilter.value.timeRange !== 'all') {
    const timeLabel = timeFilterOptions.find(opt => opt.value === transactionFilter.value.timeRange)?.label || ''
    tags.push({ key: 'timeRange', label: `时间：${timeLabel}` })
  }
  
  if (transactionFilter.value.transactionType !== 'all') {
    const typeLabel = transactionTypeOptions.find(opt => opt.value === transactionFilter.value.transactionType)?.label || ''
    tags.push({ key: 'transactionType', label: `类型：${typeLabel}` })
  }
  
  if (transactionFilter.value.consumeType !== 'all') {
    const consumeLabel = consumeTypeOptions.find(opt => opt.value === transactionFilter.value.consumeType)?.label || ''
    tags.push({ key: 'consumeType', label: `消费：${consumeLabel}` })
  }
  
  if (transactionFilter.value.minAmount) {
    tags.push({ key: 'minAmount', label: `最低：¥${transactionFilter.value.minAmount}` })
  }
  
  if (transactionFilter.value.maxAmount) {
    tags.push({ key: 'maxAmount', label: `最高：¥${transactionFilter.value.maxAmount}` })
  }
  
  if (transactionFilter.value.location) {
    tags.push({ key: 'location', label: `地点：${transactionFilter.value.location}` })
  }
  
  return tags
})

// 方法
const formatDate = (dateStr?: string) => {
  if (!dateStr) return '--'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const formatDateTime = (dateStr: string) => {
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

const getTransactionIcon = (type: string) => {
  const iconMap: Record<string, string> = {
    CANTEEN: 'fas fa-utensils',
    SUPERMARKET: 'fas fa-shopping-cart',
    LIBRARY: 'fas fa-book',
    ACCESS: 'fas fa-door-open',
    RECHARGE: 'fas fa-plus-circle'
  }
  return iconMap[type] || 'fas fa-money-bill'
}

const getTransactionColor = (type: string) => {
  const colorMap: Record<string, string> = {
    CANTEEN: '#4caf50',
    SUPERMARKET: '#2196f3',
    LIBRARY: '#ff9800',
    ACCESS: '#9c27b0',
    RECHARGE: '#4caf50'
  }
  return colorMap[type] || '#666'
}

// 加载数据 - 临时移除权限检查，直接显示主界面
const loadEcardInfo = async () => {
  try {
    // 直接显示主界面，不进行权限检查
    showMainInterface.value = true
    showGuestInterface.value = false
    showAuthForm.value = false
    
    // 尝试加载真实数据，如果失败则使用模拟数据
    try {
      if (userStore.isLoggedIn && !userStore.isVisitor) {
        const info = await getEcardInfo()
        ecardInfo.value = info
      } else {
        // 使用模拟数据
        ecardInfo.value = {
          cardNo: '2024001234',
          userId: null,
          userName: '演示用户',
          balance: 128.50,
          status: '正常',
          isVisitorCard: false,
          todayConsumeCount: 3,
          todayConsumeAmount: 25.80
        }
      }
      // 加载成功后生成二维码
      await nextTick()
      await generateQRCode(false)
    } catch (error: any) {
      // 使用模拟数据
      ecardInfo.value = {
        cardNo: '2024001234',
        userId: null,
        userName: '演示用户',
        balance: 128.50,
        status: '正常',
        isVisitorCard: false,
        todayConsumeCount: 3,
        todayConsumeAmount: 25.80
      }
      await nextTick()
      await generateQRCode(false)
    }
  } catch (error: any) {
    console.error('加载校园卡信息失败:', error)
    // 即使出错也显示主界面
    showMainInterface.value = true
    showGuestInterface.value = false
    showAuthForm.value = false
    // 使用模拟数据
    ecardInfo.value = {
      cardNo: '2024001234',
      userId: null,
      userName: '演示用户',
      balance: 128.50,
      status: '正常',
      isVisitorCard: false,
      todayConsumeCount: 3,
      todayConsumeAmount: 25.80
    }
  }
}

const loadRecentRecords = async () => {
  try {
    const response = await getConsumeRecords(undefined, undefined, undefined, 1, 5)
    recentRecords.value = response.records || []
  } catch (error) {
    console.error('加载交易记录失败:', error)
  }
}

const loadTodayStatistics = async () => {
  try {
    const stats = await getTodayStatistics()
    todayStats.value = stats
  } catch (error) {
    console.error('加载今日统计失败:', error)
  }
}

// 定位检测 - 返回是否在校内
const checkUserLocation = async (): Promise<boolean> => {
  return new Promise((resolve) => {
  try {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const { longitude, latitude } = position.coords
          try {
            const result = await checkLocation(longitude, latitude)
              isInCampus.value = result.isInCampus
              currentLocation.value = result.campusName || (result.isInCampus ? '校内区域' : '校外区域')
              
              // 根据定位结果和认证状态决定显示哪个界面
              if (result.isInCampus) {
                // 在校内：检查是否已认证
                if (isAuthenticated.value) {
                  // 已认证：显示主界面，加载数据
                  showMainInterface.value = true
                  showGuestInterface.value = false
                  showAuthForm.value = false
                  // 加载校园卡数据
                  try {
                    const info = await getEcardInfo()
                    ecardInfo.value = info
                    await Promise.all([loadRecentRecords(), loadTodayStatistics()])
                    await nextTick()
                    setTimeout(async () => {
                      await generateQRCode(false)
                    }, 500)
                  } catch (error) {
                    console.error('加载校园卡信息失败:', error)
                  }
                } else {
                  // 未认证：显示认证表单
                  showAuthForm.value = true
                  showMainInterface.value = false
                  showGuestInterface.value = false
                }
              } else {
                // 不在校内：显示临时卡申请界面
                showGuestInterface.value = true
                showMainInterface.value = false
                showAuthForm.value = false
              }
              
              resolve(result.isInCampus)
          } catch (error) {
            console.error('定位校验失败:', error)
              // 定位校验失败，默认显示临时卡申请界面
            currentLocation.value = '定位失败'
              isInCampus.value = false
              showGuestInterface.value = true
              showMainInterface.value = false
              showAuthForm.value = false
              resolve(false)
          }
        },
        (error) => {
          console.error('获取位置失败:', error)
            // 获取位置失败，默认显示临时卡申请界面
          currentLocation.value = '定位失败'
            isInCampus.value = false
            showGuestInterface.value = true
            showMainInterface.value = false
            showAuthForm.value = false
            resolve(false)
          },
          {
            timeout: 10000, // 10秒超时
            enableHighAccuracy: false
        }
      )
    } else {
        // 不支持定位，默认显示临时卡申请界面
        console.warn('浏览器不支持定位功能')
      currentLocation.value = '不支持定位'
        isInCampus.value = false
        showGuestInterface.value = true
        showMainInterface.value = false
        showAuthForm.value = false
        resolve(false)
    }
  } catch (error) {
    console.error('定位检测失败:', error)
      currentLocation.value = '定位失败'
      isInCampus.value = false
      showGuestInterface.value = true
      showMainInterface.value = false
      showAuthForm.value = false
      resolve(false)
    }
  })
}

// 认证提交
const handleAuthSubmit = async () => {
  authLoading.value = true
  try {
    // 这里应该调用后端认证API
    // 暂时模拟成功
    await new Promise((resolve) => setTimeout(resolve, 1000))
    
    // 认证成功后，如果在校内，显示主界面并加载数据
    if (isInCampus.value) {
      showAuthForm.value = false
      showMainInterface.value = true
      showGuestInterface.value = false
    
      // 加载校园卡数据
      try {
        const info = await getEcardInfo()
        ecardInfo.value = info
        await Promise.all([loadRecentRecords(), loadTodayStatistics()])
        await nextTick()
        setTimeout(async () => {
          await generateQRCode(false)
        }, 500)
      } catch (error) {
        console.error('加载校园卡信息失败:', error)
      }
    } else {
      // 不在校内，显示临时卡申请界面
      showGuestInterface.value = true
      showAuthForm.value = false
      showMainInterface.value = false
    }
  } catch (error: any) {
    console.error('认证失败:', error)
    alert(error.message || '认证失败，请重试')
  } finally {
    authLoading.value = false
  }
}

// 临时卡申请
const handleTempCardSubmit = async () => {
  tempCardLoading.value = true
  try {
    const params: VisitorCardApplyParams = {
      name: tempCardForm.value.name,
      phone: tempCardForm.value.phone,
      idCard: tempCardForm.value.idCard,
      purpose: tempCardForm.value.purpose
    }
    
    const result = await applyVisitorCard(params)
    tempCardInfo.value = result
    // 申请成功后生成二维码
    await nextTick()
    setTimeout(async () => {
      await generateQRCode(true)
    }, 300)
  } catch (error: any) {
    console.error('申请失败:', error)
    // 即使申请失败，也生成一个测试二维码
    await nextTick()
    setTimeout(async () => {
      await generateQRCode(true)
    }, 300)
  } finally {
    tempCardLoading.value = false
  }
}

// 充值
const handleRecharge = async () => {
  const amountStr = prompt('请输入充值金额（最低10元）:')
  if (amountStr) {
    const amount = parseFloat(amountStr)
    if (isNaN(amount) || amount < 10) {
      alert('充值金额不能少于10元')
      return
    }
    try {
      const result = await recharge(amount)
      ecardInfo.value = result
      if (tempCardInfo.value) {
        tempCardInfo.value = result
      }
      alert(`充值成功！充值金额：¥${amount.toFixed(2)}`)
    } catch (error: any) {
      alert(error.message || '充值失败，请重试')
    }
  }
}

// 生成二维码到Canvas
const generateQRCodeToCanvas = async (canvas: HTMLCanvasElement | null, data: string) => {
  if (!canvas || !data) return
  
  try {
    await QRCode.toCanvas(canvas, data, {
      width: 200,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      },
      errorCorrectionLevel: 'M'
    })
  } catch (error) {
    console.error('生成二维码失败:', error)
  }
}

// 生成二维码数据并渲染
const generateQRCode = async (isTempCard: boolean = false) => {
  const canvas = isTempCard ? tempQrcodeCanvas.value : qrcodeCanvas.value
  
  if (!canvas) {
    await nextTick()
    const retryCanvas = isTempCard ? tempQrcodeCanvas.value : qrcodeCanvas.value
    if (!retryCanvas) return
  }
  
  try {
    const cardInfo = isTempCard ? tempCardInfo.value : ecardInfo.value
    let qrData = ''
    
    if (cardInfo && cardInfo.cardNo) {
      try {
        const codeInfo = await generateDynamicCode()
        dynamicCode.value = codeInfo.code
        
        qrData = JSON.stringify({
          cardNo: cardInfo.cardNo,
          code: codeInfo.code,
          timestamp: Date.now(),
          expireTime: codeInfo.expireTime,
          type: isTempCard ? 'visitor' : 'normal',
          userId: cardInfo.userId
        })
      } catch (apiError) {
        qrData = JSON.stringify({
          cardNo: cardInfo.cardNo,
          timestamp: Date.now(),
          type: isTempCard ? 'visitor' : 'normal',
          userId: cardInfo.userId
        })
      }
    } else {
      qrData = JSON.stringify({
        test: true,
        message: '请先登录或申请临时卡',
        timestamp: Date.now(),
        type: isTempCard ? 'visitor' : 'normal'
      })
    }
    
    if (isTempCard) {
      tempQrcodeData.value = qrData
    } else {
      qrcodeData.value = qrData
    }
    
    await nextTick()
    
    const finalCanvas = isTempCard ? tempQrcodeCanvas.value : qrcodeCanvas.value
    if (finalCanvas) {
      await generateQRCodeToCanvas(finalCanvas, qrData)
    }
  } catch (error: any) {
    console.error('生成二维码失败:', error)
  }
}

// 刷新二维码
const refreshQrCode = async () => {
  try {
    countdown.value = 60
    if (showMainInterface.value) {
      await generateQRCode(false)
    }
    if (tempCardInfo.value) {
      await generateQRCode(true)
    }
  } catch (error) {
    console.error('刷新二维码失败:', error)
  }
}

// 功能点击处理
const handleFunctionClick = (funcId: string) => {
  switch (funcId) {
    case 'scan':
      handleScanPay()
      break
    case 'location':
      handleSmartLocation()
      break
    case 'security':
      handleSecurity()
      break
    case 'records':
      showAllRecords.value = true
      break
    case 'canteen':
      goToCanteenPayment()
      break
    case 'library':
      goToLibraryPayment()
      break
    case 'account-book':
      goToAccountBook()
      break
    case 'diet-record':
      goToDietRecord()
      break
    case 'bus':
      goToBusPayment()
      break
    case 'more':
      goToMoreServices()
      break
  }
}

// 其他功能
const handleScanPay = () => {
  // 跳转到付款码区域
  document.querySelector('.qrcode-card')?.scrollIntoView({ behavior: 'smooth' })
}

const handleSmartLocation = () => {
  checkUserLocation()
}

const handleSecurity = () => {
  router.push('/security')
}

// 处理位置选择（参考app端）
const handleLocationSelect = () => {
  const campuses = ['东校区', '西校区', '南校区', '北校区']
  const selected = prompt('请选择校区：\n1. 东校区\n2. 西校区\n3. 南校区\n4. 北校区', '1')
  if (selected) {
    const index = parseInt(selected) - 1
    if (index >= 0 && index < campuses.length) {
      campusLocation.value = campuses[index]
    }
  }
}

// 导航方法（参考app端）
const goToCardDetail = () => {
  alert('卡片详情功能开发中...')
}

const goToSettings = () => {
  alert('设置功能开发中...')
}

const handleTransfer = () => {
  alert('转账功能开发中...')
}

const goToCanteenPayment = () => {
  alert('食堂消费功能开发中...')
}

const goToLibraryPayment = () => {
  alert('图书馆功能开发中...')
}

const goToAccountBook = () => {
  router.push('/ecard/account-book')
}

const goToDietRecord = () => {
  router.push('/ecard/diet-record')
}

const goToBusPayment = () => {
  alert('校车支付功能开发中...')
}

const goToMoreServices = () => {
  alert('更多服务功能开发中...')
}

const goToStatDetail = () => {
  alert('详细分析功能开发中...')
}

const goToTransactionDetail = (id: number) => {
  alert(`交易详情功能开发中... ID: ${id}`)
}

// 消费目标设置（参考app端）
const goToGoalSettings = () => {
  const amountStr = prompt('请输入月度消费目标金额：', consumeGoal.value.monthly.toString())
  if (amountStr) {
    const amount = parseFloat(amountStr)
    if (!isNaN(amount) && amount > 0) {
      consumeGoal.value.monthly = amount
      alert('目标设置成功')
    }
  }
}

// 切换余额提醒（参考app端）
const toggleReminder = () => {
  balanceReminder.value.enabled = !balanceReminder.value.enabled
  alert(balanceReminder.value.enabled ? '余额提醒已开启' : '余额提醒已关闭')
}

// 筛选相关方法（参考app端）
const setQuickAmount = (amount: number) => {
  if (transactionFilter.value.maxAmount === amount.toString()) {
    transactionFilter.value.maxAmount = ''
  } else {
    transactionFilter.value.maxAmount = amount.toString()
  }
}

const isQuickAmountActive = (amount: number) => {
  return transactionFilter.value.maxAmount === amount.toString()
}

const resetTransactionFilter = () => {
  transactionFilter.value = {
    timeRange: 'all',
    customStartDate: '',
    customStartTime: '00:00',
    customEndDate: '',
    customEndTime: '23:59',
    transactionType: 'all',
    consumeType: 'all',
    minAmount: '',
    maxAmount: '',
    location: '',
    sortBy: 'time'
  }
  alert('筛选已重置')
}

const applyTransactionFilter = () => {
  // 验证自定义时间
  if (transactionFilter.value.timeRange === 'custom') {
    if (!transactionFilter.value.customStartDate || !transactionFilter.value.customEndDate) {
      alert('请选择完整的时间范围')
      return
    }
  }
  
  showFilter.value = false
  alert('筛选已应用')
}

const removeFilterTag = (key: string) => {
  switch (key) {
    case 'timeRange':
      transactionFilter.value.timeRange = 'all'
      break
    case 'transactionType':
      transactionFilter.value.transactionType = 'all'
      break
    case 'consumeType':
      transactionFilter.value.consumeType = 'all'
      break
    case 'minAmount':
      transactionFilter.value.minAmount = ''
      break
    case 'maxAmount':
      transactionFilter.value.maxAmount = ''
      break
    case 'location':
      transactionFilter.value.location = ''
      break
  }
}

// 倒计时
let countdownInterval: number | null = null
const startCountdown = () => {
  if (countdownInterval) {
    clearInterval(countdownInterval)
  }
  countdownInterval = window.setInterval(async () => {
    countdown.value--
    if (countdown.value < 0) {
      countdown.value = 59
      // 倒计时结束时自动刷新二维码
      if (showMainInterface.value) {
        await generateQRCode(false)
      }
      if (tempCardInfo.value) {
        await generateQRCode(true)
      }
    }
  }, 1000)
}

// 生命周期
onMounted(async () => {
  userStore.initUserFromStorage()
  
  // 临时移除权限检查：直接显示主界面，不进行定位检测
  isLocationChecking.value = false
  isInCampus.value = true // 设置为在校内，以便显示完整功能
  currentLocation.value = '校内区域'
  
  // 直接加载并显示主界面
  await loadEcardInfo()
  
  // 加载最近记录和今日统计（使用try-catch避免错误影响界面显示）
  try {
    await Promise.all([loadRecentRecords(), loadTodayStatistics()])
  } catch (error) {
    console.log('加载统计数据失败，使用默认数据')
  }
  
  startCountdown()
  
  // 确保二维码已生成
  setTimeout(async () => {
    if (!qrcodeData.value && qrcodeCanvas.value) {
      await generateQRCode(false)
    }
  }, 1000)
})

onUnmounted(() => {
  if (countdownInterval) {
    clearInterval(countdownInterval)
  }
})
</script>

<style scoped>
.ecard-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
  color: #333;
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 主内容区域布局（参考二手交易界面） */
.main-content-wrapper {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
}

.main-content {
  flex: 1;
}

/* 卡片基础样式 */
.card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.card-title {
  font-size: 20px;
  font-weight: bold;
  color: #d81b60;
}

.view-detail,
.view-all {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.view-detail:hover,
.view-all:hover {
  color: #d81b60;
}

.view-detail i,
.view-all i {
  margin-left: 5px;
}

/* 加载状态 */
.loading-section {
  margin: 30px 0;
}

.loading-section .card {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 身份认证区域 */
.auth-section {
  margin-bottom: 30px;
}

.auth-card {
  max-width: 600px;
  margin: 0 auto;
}

.auth-header {
  text-align: center;
  margin-bottom: 25px;
}

.auth-title {
  font-size: 24px;
  font-weight: bold;
  color: #d81b60;
  margin-bottom: 10px;
}

.auth-subtitle {
  color: #666;
  font-size: 14px;
}

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.form-control {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
  font-family: inherit;
}

.form-control:focus {
  outline: none;
  border-color: #d81b60;
}

.form-select {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  transition: border-color 0.3s;
}

.form-select:focus {
  outline: none;
  border-color: #d81b60;
}

.form-text {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* 按钮样式 */
.btn {
  padding: 12px 24px;
  border-radius: 25px;
  border: none;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
}

.btn-primary {
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%) !important;
  color: white !important;
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #E91E63 0%, #FF6B9D 100%) !important;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 107, 157, 0.3);
}

.btn-primary:active:not(:disabled) {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.2);
}

.btn-outline {
  background: transparent;
  border: 2px solid #FF6B9D;
  color: #FF6B9D;
}

.btn-outline:hover:not(:disabled) {
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%);
  color: white;
}

.btn-outline:active:not(:disabled) {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.2);
}

.btn-block {
  width: 100%;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 余额卡片 */
.balance-card {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  padding: 30px;
  position: relative;
  overflow: hidden;
}

.balance-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.balance-content {
  margin-bottom: 20px;
}

.balance-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.balance-amount {
  font-size: 42px;
  font-weight: bold;
  margin: 10px 0;
}

.balance-meta {
  font-size: 13px;
  opacity: 0.8;
  margin-top: 8px;
}

.balance-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

/* 侧边栏样式（参考二手交易界面） */
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

.sidebar-widget {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s;
}

.sidebar-widget:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.widget-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f1f5f9;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
}

.widget-title i {
  color: #FF6B9D;
  font-size: 18px;
}

/* 功能列表样式 */
.function-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.function-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  color: #64748b;
  font-size: 14px;
}

.function-item:hover {
  background: rgba(255, 107, 157, 0.1);
  color: #FF6B9D;
  transform: translateX(4px);
}

.function-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
}

.function-name {
  flex: 1;
  font-weight: 500;
}

.function-arrow {
  font-size: 12px;
  color: #94a3b8;
  transition: all 0.2s;
}

.function-item:hover .function-arrow {
  color: #FF6B9D;
}

/* 快捷操作按钮 */
.quick-action {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-button {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  color: #475569;
  font-size: 14px;
  font-weight: 500;
  border: 2px solid transparent;
  text-decoration: none;
}

.action-button.primary {
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.25);
  font-weight: 600;
}

.action-button.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.35);
}

.action-button:not(.primary):hover {
  background: rgba(255, 107, 157, 0.1);
  border-color: #FFB3D1;
  color: #FF6B9D;
  transform: translateX(4px);
}

.action-button i {
  margin-right: 10px;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

/* 余额提醒侧边栏样式 */
.reminder-widget {
  background: linear-gradient(135deg, #fff0f5 0%, #ffe5f1 100%);
  border-left: 4px solid #FF6B9D;
}

.reminder-content-sidebar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.reminder-text-sidebar {
  font-size: 13px;
  color: #666;
  flex: 1;
}

.reminder-amount {
  color: #FF6B9D;
  font-weight: 600;
}

.reminder-toggle-sidebar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 107, 157, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FF6B9D;
  font-size: 14px;
  cursor: pointer;
  border: none;
  transition: all 0.3s;
}

.reminder-toggle-sidebar:hover {
  background: rgba(255, 107, 157, 0.2);
  transform: scale(1.1);
}

/* 使用提示样式 */
.tips-widget {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-left: 4px solid #f59e0b;
}

.tips-widget .widget-title {
  color: #92400e;
  border-bottom-color: rgba(245, 158, 11, 0.2);
}

.tips-widget .widget-title i {
  color: #f59e0b;
}

.safety-tips {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 8px 0;
  color: #78350f;
  font-size: 13px;
  line-height: 1.5;
}

.tip-item i {
  color: #f59e0b;
  margin-top: 2px;
  flex-shrink: 0;
  font-size: 14px;
}

/* 消费统计 */
.stats-card {
  margin: 30px 0;
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
  transition: all 0.3s;
}

.stat-item:hover {
  background: #f0f0f0;
  transform: translateY(-3px);
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #d81b60;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 13px;
  color: #666;
}

/* 二维码区域 */
.qrcode-card {
  margin: 30px 0;
}

.qrcode-section {
  display: flex;
  gap: 30px;
  align-items: center;
  margin-top: 20px;
}

.qrcode-display {
  width: 200px;
  height: 200px;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  flex-shrink: 0;
}

.qrcode-canvas {
  width: 100%;
  height: 100%;
  display: block;
}

.qrcode-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #d81b60;
  z-index: 0;
}

.qrcode-loading i {
  font-size: 48px;
  display: block;
  margin-bottom: 10px;
}

.qrcode-loading p {
  font-size: 13px;
  color: #666;
  margin: 0;
}

.qrcode-info {
  flex: 1;
}

.qrcode-info h3 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.qrcode-info p {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 15px;
}

.qrcode-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 12px 0;
  font-size: 13px;
  color: #666;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #4caf50;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.qrcode-meta {
  font-size: 13px;
  color: #666;
  margin: 8px 0;
}

.qrcode-meta .countdown {
  color: #d81b60;
  font-weight: bold;
}

.qrcode-meta .location {
  color: #333;
  font-weight: 500;
}

.qrcode-meta .balance {
  color: #d81b60;
  font-weight: bold;
}

.qrcode-actions {
  margin-top: 15px;
}

/* 交易记录 */
.transaction-card {
  margin: 30px 0;
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.3s;
}

.transaction-item:hover {
  background: #f8f9fa;
  padding-left: 10px;
  padding-right: 10px;
  margin-left: -10px;
  margin-right: -10px;
  border-radius: 8px;
}

.transaction-item:last-child {
  border-bottom: none;
}

.transaction-info {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.transaction-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  flex-shrink: 0;
}

.transaction-details {
  flex: 1;
}

.transaction-title {
  font-weight: bold;
  margin-bottom: 5px;
  font-size: 15px;
  color: #333;
}

.transaction-meta {
  font-size: 12px;
  color: #999;
}

.transaction-amount {
  font-weight: bold;
  text-align: right;
  font-size: 16px;
  flex-shrink: 0;
}

.amount-positive {
  color: #4caf50;
}

.amount-negative {
  color: #d81b60;
}

.empty-state {
  text-align: center;
  padding: 50px 20px;
  color: #999;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 15px;
  opacity: 0.5;
}

.empty-state p {
  font-size: 14px;
  margin: 0;
}

/* 临时卡表单 */
.temp-card-form-card {
  margin: 30px 0;
  }

.temp-card-form {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-container {
    padding: 15px;
  }

  .card {
    padding: 20px;
  }

  .qrcode-section {
    flex-direction: column;
    text-align: center;
  }

  .balance-actions {
    flex-direction: column;
  }

  .main-content-wrapper {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    position: static;
    max-height: none;
  }

  .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .stats-container {
    grid-template-columns: 1fr;
  }

  .balance-amount {
    font-size: 32px;
  }

  .qrcode-display {
    width: 160px;
    height: 160px;
  }
}

/* 顶部状态栏样式（参考app端） */
.top-status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: rgba(248, 187, 208, 0.1);
  border-radius: 12px;
}

.location-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.location-info:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateY(-2px);
}

.location-icon {
  color: #d81b60;
  font-size: 14px;
}

.location-text {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.arrow-icon {
  font-size: 12px;
  color: #999;
  margin-left: 4px;
}

.weather-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20px;
}

.weather-icon {
  color: #ff9800;
  font-size: 14px;
}

.weather-text {
  font-size: 14px;
  color: #666;
}

/* 余额卡片增强样式（参考app端） */
.balance-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.balance-title-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.card-icon {
  font-size: 32px;
  color: #fff;
  background: rgba(255, 255, 255, 0.2);
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.balance-titles {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.card-number {
  font-size: 12px;
  opacity: 0.9;
  font-family: 'Courier New', monospace;
}

.balance-actions-header {
  display: flex;
  gap: 10px;
}

.action-btn {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.balance-sub-info {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 15px;
}

.sub-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  opacity: 0.9;
}

.sub-item i {
  font-size: 12px;
}

.balance-footer {
  display: flex;
  gap: 12px;
  margin-top: 20px;
}

.recharge-btn,
.transfer-btn {
  flex: 1;
}


/* 消费目标卡片样式（参考app端） */
.goal-card {
  margin: 20px 0;
  border-top: 3px solid #FFB5D8;
}

.goal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.goal-title-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.goal-icon {
  font-size: 20px;
  color: #d81b60;
  background: rgba(255, 181, 216, 0.2);
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.goal-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.goal-edit {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 181, 216, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #d81b60;
  font-size: 16px;
  cursor: pointer;
  border: none;
  transition: all 0.3s;
}

.goal-edit:hover {
  background: rgba(255, 181, 216, 0.3);
  transform: scale(1.1);
}

.goal-progress {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.goal-progress-bar {
  width: 100%;
  height: 12px;
  background: rgba(255, 181, 216, 0.2);
  border-radius: 6px;
  overflow: hidden;
}

.goal-progress-fill {
  height: 100%;
  background: #FFB5D8;
  border-radius: 6px;
  transition: width 0.5s ease;
}

.goal-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.goal-current {
  color: #d81b60;
  font-weight: 600;
}

.goal-target {
  color: #999;
}

.goal-percent {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #d81b60;
  margin-top: 8px;
}

/* 统计卡片增强样式（参考app端） */
.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.stat-title-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  font-size: 20px;
  color: #d81b60;
  background: rgba(255, 181, 216, 0.2);
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.stat-period {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  background: #f8f9fa;
  padding: 6px 12px;
  border-radius: 16px;
}

.stat-content {
  margin-bottom: 20px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.stat-item:hover {
  background: #f0f0f0;
  transform: translateX(4px);
}

.stat-icon-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon-circle i {
  color: white;
  font-size: 20px;
}

.stat-data {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #666;
}

.stat-chart {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin-top: 20px;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 150px;
  margin-bottom: 15px;
  padding: 0 15px;
}

.chart-bar {
  flex: 1;
  max-width: 60px;
  border-radius: 8px 8px 0 0;
  position: relative;
  transition: all 0.3s;
  display: flex;
  justify-content: center;
  margin: 0 4px;
}

.chart-bar:hover {
  opacity: 0.8;
  transform: scaleY(1.1);
}

.bar-label {
  position: absolute;
  bottom: -25px;
  font-size: 11px;
  color: #666;
  white-space: nowrap;
  transform: rotate(-45deg);
  transform-origin: left top;
}

.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #666;
}

.legend-color {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.stat-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.stat-tips {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #d81b60;
  background: rgba(255, 181, 216, 0.2);
  padding: 6px 12px;
  border-radius: 16px;
}


/* 交易记录增强样式（参考app端） */
.transactions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.transactions-title-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.transactions-icon {
  font-size: 20px;
  color: #9C27B0;
  background: #E1BEE7;
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.transactions-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.transactions-filter {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  background: #f8f9fa;
  padding: 6px 12px;
  border-radius: 16px;
  cursor: pointer;
  border: none;
  transition: all 0.3s;
}

.transactions-filter:hover {
  background: #f0f0f0;
  transform: scale(0.95);
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 0 0 15px 0;
  margin-bottom: 15px;
}

.filter-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(255, 181, 216, 0.2);
  border-radius: 16px;
  border: 1px solid rgba(255, 181, 216, 0.4);
  font-size: 12px;
  color: #d81b60;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tag:hover {
  background: rgba(255, 181, 216, 0.3);
}

.filter-tag i {
  font-size: 10px;
}

.transaction-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.arrow-right {
  color: #999;
  font-size: 14px;
}

.transactions-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  padding: 15px;
  background: #F3E5F5;
  border-radius: 12px;
  font-size: 14px;
  color: #CE93D8;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 15px;
}

.transactions-footer:hover {
  background: #E1BEE7;
  transform: translateX(4px);
}

/* 筛选弹窗样式（参考app端） */
.filter-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  display: flex;
  align-items: flex-end;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.filter-modal {
  width: 100%;
  max-height: 85vh;
  background-color: rgba(255, 255, 255, 0.98);
  border-radius: 20px 20px 0 0;
  padding: 0;
  animation: slideUp 0.3s ease;
  display: flex;
  flex-direction: column;
  box-shadow: 0 -4px 20px rgba(255, 181, 216, 0.2);
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.filter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 181, 216, 0.2);
}

.filter-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.filter-close {
  font-size: 24px;
  color: #999;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-close:hover {
  color: #d81b60;
  transform: scale(0.9);
}

.filter-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  max-height: calc(85vh - 140px);
}

.filter-section {
  margin-bottom: 30px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.section-icon {
  font-size: 16px;
  color: #d81b60;
  background: rgba(255, 181, 216, 0.2);
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-option {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  background: rgba(255, 181, 216, 0.1);
  border-radius: 16px;
  border: 2px solid rgba(255, 181, 216, 0.3);
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-option:hover {
  background: rgba(255, 181, 216, 0.2);
}

.filter-option.active {
  background: #FFB5D8;
  border-color: #FFB5D8;
  color: #fff;
}

.filter-option i {
  font-size: 12px;
}

.custom-time-section {
  margin-top: 15px;
  padding: 15px;
  background: rgba(255, 181, 216, 0.05);
  border-radius: 12px;
}

.date-time-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.date-time-row:last-child {
  margin-bottom: 0;
}

.date-label {
  font-size: 14px;
  color: #666;
  min-width: 80px;
}

.date-picker-value,
.time-picker-value {
  flex: 1;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  border: 2px solid rgba(255, 181, 216, 0.3);
  font-size: 14px;
  color: #333;
}

.amount-range-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.amount-input-row {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.amount-label {
  font-size: 12px;
  color: #999;
}

.amount-input {
  width: 100%;
  height: 40px;
  padding: 0 12px;
  background: rgba(255, 181, 216, 0.1);
  border-radius: 8px;
  border: 2px solid rgba(255, 181, 216, 0.3);
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
}

.amount-separator {
  font-size: 14px;
  color: #999;
  margin-top: 24px;
  flex-shrink: 0;
}

.quick-amount-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.amount-tag {
  padding: 8px 16px;
  background: rgba(255, 181, 216, 0.1);
  border-radius: 16px;
  border: 2px solid rgba(255, 181, 216, 0.3);
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.amount-tag:hover {
  background: rgba(255, 181, 216, 0.2);
}

.amount-tag.active {
  background: #FFB5D8;
  border-color: #FFB5D8;
  color: #fff;
}

.filter-input {
  width: 100%;
  height: 40px;
  padding: 0 15px;
  background: rgba(255, 181, 216, 0.1);
  border-radius: 12px;
  border: 2px solid rgba(255, 181, 216, 0.3);
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
}

.filter-footer {
  display: flex;
  gap: 15px;
  padding: 20px;
  border-top: 1px solid rgba(255, 181, 216, 0.2);
  background: rgba(255, 255, 255, 0.98);
}

.filter-btn {
  flex: 1;
  height: 44px;
  line-height: 44px;
  border-radius: 22px;
  border: none;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.reset-btn {
  background: rgba(255, 181, 216, 0.2);
  color: #d81b60;
  border: 2px solid rgba(255, 181, 216, 0.4);
}

.reset-btn:hover {
  background: rgba(255, 181, 216, 0.3);
  transform: scale(0.98);
}

.confirm-btn {
  background: #FFB5D8;
  color: #fff;
  box-shadow: 0 4px 15px rgba(255, 181, 216, 0.4);
}

.confirm-btn:hover {
  background: #FF9BC8;
  transform: scale(0.98);
}
</style>
