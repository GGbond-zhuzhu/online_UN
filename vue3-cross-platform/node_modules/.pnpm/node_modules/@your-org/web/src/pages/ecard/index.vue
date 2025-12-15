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
      <div class="main-content" v-if="showMainInterface && !isLocationChecking">
        <!-- 账户余额卡片 -->
        <div class="card balance-card">
          <div class="balance-content">
            <div class="balance-label">账户余额</div>
            <div class="balance-amount">¥ {{ ecardInfo.balance?.toFixed(2) || '0.00' }}</div>
            <div class="balance-meta" v-if="ecardInfo.lastConsumeTime">
              上次充值：{{ formatDate(ecardInfo.lastConsumeTime) }}
            </div>
            <div class="balance-meta" v-else>暂无充值记录</div>
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

        <!-- 功能网格 -->
        <div class="features-grid">
          <div class="card feature-card" @click="handleScanPay">
            <div class="feature-icon">
              <i class="fas fa-qrcode"></i>
            </div>
            <h3 class="feature-title">扫码支付</h3>
            <p class="feature-desc">校园消费一键支付，安全便捷</p>
          </div>
          <div class="card feature-card" @click="handleSmartLocation">
            <div class="feature-icon">
              <i class="fas fa-map-marker-alt"></i>
            </div>
            <h3 class="feature-title">智能定位</h3>
            <p class="feature-desc">多源融合定位，室内外无缝切换</p>
          </div>
          <div class="card feature-card" @click="handleSecurity">
            <div class="feature-icon">
              <i class="fas fa-shield-alt"></i>
            </div>
            <h3 class="feature-title">安全加密</h3>
            <p class="feature-desc">AES加密存储，保障交易安全</p>
          </div>
          <div class="card feature-card" @click="showAllRecords = true">
            <div class="feature-icon">
              <i class="fas fa-history"></i>
            </div>
            <h3 class="feature-title">消费记录</h3>
            <p class="feature-desc">详细记录每一笔消费，随时查看</p>
          </div>
        </div>

        <!-- 本月消费统计 -->
        <div class="card stats-card">
          <div class="card-header">
            <h2 class="card-title">本月消费统计</h2>
            <a href="#" @click.prevent="showAllRecords = true" class="view-detail">
              查看详情 <i class="fas fa-arrow-right"></i>
            </a>
          </div>
          <div class="stats-container">
            <div class="stat-item">
              <div class="stat-value">¥ {{ monthStats.totalAmount?.toFixed(2) || '0.00' }}</div>
              <div class="stat-label">本月消费</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ monthStats.totalCount || 0 }} 笔</div>
              <div class="stat-label">交易次数</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ monthStats.consumeRatio || '0' }}%</div>
              <div class="stat-label">消费占比</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">¥ {{ monthStats.dailyAverage?.toFixed(2) || '0.00' }}</div>
              <div class="stat-label">日均消费</div>
            </div>
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

        <!-- 最近交易 -->
        <div class="card transaction-card">
          <div class="card-header">
            <h2 class="card-title">最近交易</h2>
            <a href="#" @click.prevent="showAllRecords = true" class="view-all">
              查看全部 <i class="fas fa-arrow-right"></i>
            </a>
          </div>
          <div class="transaction-list">
            <div
              class="transaction-item"
              v-for="record in recentRecords"
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
            <div v-if="recentRecords.length === 0" class="empty-state">
              <i class="fas fa-inbox"></i>
              <p>暂无交易记录</p>
            </div>
          </div>
        </div>
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
const authLoading = ref(false)
const tempCardLoading = ref(false)
const isLocationChecking = ref(true) // 定位检测中
const isInCampus = ref(false) // 是否在校内

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

// 本月统计（模拟数据，实际应从API获取）
const monthStats = ref({
  totalAmount: 245.80,
  totalCount: 28,
  consumeRatio: '食堂 65%', // 主要消费类型和占比
  dailyAverage: 8.78
})

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

// 加载数据
const loadEcardInfo = async () => {
  try {
    if (userStore.isLoggedIn && !userStore.isVisitor) {
      const info = await getEcardInfo()
      ecardInfo.value = info
      showMainInterface.value = true
      showGuestInterface.value = false
      showAuthForm.value = false
      // 加载成功后生成二维码
      await nextTick()
      await generateQRCode(false)
    } else {
      // 未认证用户，根据定位结果决定显示哪个界面
      if (isInCampus.value) {
        showAuthForm.value = true
      showMainInterface.value = false
        showGuestInterface.value = false
      } else {
      showGuestInterface.value = true
        showMainInterface.value = false
        showAuthForm.value = false
      }
    }
  } catch (error: any) {
    console.error('加载校园卡信息失败:', error)
    // 如果是未认证用户，根据定位结果决定显示哪个界面
    if (error.message?.includes('401') || error.message?.includes('未授权')) {
      if (isInCampus.value) {
        showAuthForm.value = true
      showMainInterface.value = false
        showGuestInterface.value = false
    } else {
        showGuestInterface.value = true
        showMainInterface.value = false
        showAuthForm.value = false
      }
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
  
  // 先检测定位，根据定位结果决定显示哪个界面
  isLocationChecking.value = true
  await checkUserLocation()
  isLocationChecking.value = false
  
  startCountdown()
  
  // 如果在校内且已认证，确保二维码已生成
  if (isInCampus.value && showMainInterface.value) {
  setTimeout(async () => {
    if (!qrcodeData.value && qrcodeCanvas.value) {
      await generateQRCode(false)
    }
  }, 1000)
  }
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
  background: #d81b60;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #c2185b;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(216, 27, 96, 0.3);
}

.btn-outline {
  background: transparent;
  border: 2px solid #d81b60;
  color: #d81b60;
}

.btn-outline:hover:not(:disabled) {
  background: #d81b60;
  color: white;
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

/* 功能网格 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin: 30px 0;
}

.feature-card {
  text-align: center;
  padding: 25px;
  cursor: pointer;
}

.feature-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #f9d0da 0%, #f5b8c9 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  font-size: 28px;
  color: #d81b60;
  transition: transform 0.3s;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1);
}

.feature-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.feature-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
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

  .features-grid {
    grid-template-columns: 1fr;
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
</style>
