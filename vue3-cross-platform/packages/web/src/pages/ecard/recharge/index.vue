<template>
  <div class="recharge-page">
    <NavBar />
    <div class="recharge-container">
      <div class="page-header">
        <h1 class="page-title">校园卡充值</h1>
        <p class="page-subtitle">为您的校园卡账户充值</p>
      </div>

      <!-- 余额卡片 -->
      <div class="balance-card">
        <div class="balance-header">
          <span class="balance-label">当前余额</span>
          <span class="balance-amount">¥{{ currentBalance.toFixed(2) }}</span>
        </div>
        <p class="balance-tip">充值后余额将实时更新</p>
      </div>

      <!-- 充值金额选择 -->
      <div class="amount-section">
        <h2 class="section-title">选择充值金额</h2>
        
        <!-- 快速金额按钮 -->
        <div class="quick-amounts">
          <button
            v-for="amount in quickAmounts"
            :key="amount"
            class="quick-btn"
            :class="{ active: selectedAmount === amount }"
            @click="selectAmount(amount)"
          >
            ¥{{ amount }}
          </button>
        </div>

        <!-- 自定义金额输入 -->
        <div class="custom-amount">
          <label class="input-label">自定义金额</label>
          <div class="input-wrapper">
            <span class="input-prefix">¥</span>
            <input
              v-model="customAmountInput"
              type="number"
              class="amount-input"
              placeholder="请输入充值金额"
              min="1"
              max="1000"
              @input="handleCustomInput"
            />
          </div>
          <p class="input-hint">单笔建议不超过 1000 元，金额需为正整数</p>
        </div>
      </div>

      <!-- 支付方式 -->
      <div class="payment-section">
        <h2 class="section-title">支付方式</h2>
        <div class="payment-methods">
          <div
            v-for="method in paymentMethods"
            :key="method.id"
            class="payment-method"
            :class="{ active: selectedPayment === method.id }"
            @click="selectPayment(method.id)"
          >
            <div class="method-icon">
              <i :class="method.icon"></i>
            </div>
            <div class="method-info">
              <h3 class="method-name">{{ method.name }}</h3>
              <p class="method-desc">{{ method.desc }}</p>
            </div>
            <div class="method-check">
              <i v-if="selectedPayment === method.id" class="fas fa-check-circle"></i>
            </div>
          </div>
        </div>
      </div>

      <!-- 确认充值按钮 -->
      <div class="action-section">
        <div class="recharge-summary">
          <div class="summary-row">
            <span>充值金额：</span>
            <span class="summary-amount">¥{{ rechargeAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>充值后余额：</span>
            <span class="summary-balance">¥{{ (currentBalance + rechargeAmount).toFixed(2) }}</span>
          </div>
        </div>
        <button
          class="recharge-btn"
          :disabled="!canRecharge"
          @click="handleRecharge"
        >
          确认充值
        </button>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const router = useRouter()

// 当前余额
const currentBalance = ref(0.00)

// 快速金额选项
const quickAmounts = [10, 20, 50, 100, 200, 500]

// 选中的金额
const selectedAmount = ref<number | null>(null)
const customAmountInput = ref('')

// 充值金额（计算属性）
const rechargeAmount = computed(() => {
  if (selectedAmount.value !== null) {
    return selectedAmount.value
  }
  const custom = parseFloat(customAmountInput.value)
  return isNaN(custom) || custom <= 0 ? 0 : custom
})

// 支付方式
const paymentMethods = [
  {
    id: 'alipay',
    name: '支付宝',
    desc: '推荐使用',
    icon: 'fab fa-alipay'
  },
  {
    id: 'wechat',
    name: '微信支付',
    desc: '安全便捷',
    icon: 'fab fa-weixin'
  },
  {
    id: 'bank',
    name: '银行卡',
    desc: '支持各大银行',
    icon: 'fas fa-credit-card'
  }
]

// 选中的支付方式
const selectedPayment = ref('alipay')

// 是否可以进行充值
const canRecharge = computed(() => {
  return rechargeAmount.value > 0 && rechargeAmount.value <= 1000 && selectedPayment.value
})

// 选择快速金额
const selectAmount = (amount: number) => {
  selectedAmount.value = amount
  customAmountInput.value = ''
}

// 处理自定义输入
const handleCustomInput = () => {
  selectedAmount.value = null
}

// 选择支付方式
const selectPayment = (id: string) => {
  selectedPayment.value = id
}

// 确认充值
const handleRecharge = () => {
  if (!canRecharge.value) {
    return
  }

  // TODO: 调用充值 API
  console.log('充值金额:', rechargeAmount.value)
  console.log('支付方式:', selectedPayment.value)
  
  // 模拟充值成功
  alert(`充值成功！金额：¥${rechargeAmount.value.toFixed(2)}`)
  
  // 跳转到充值记录页面
  router.push('/ecard/consume-record')
}
</script>

<style scoped>
.recharge-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff5f7 0%, #f0f9ff 100%);
  display: flex;
  flex-direction: column;
}

.recharge-container {
  flex: 1;
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
  width: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
}

/* 余额卡片 */
.balance-card {
  background: linear-gradient(135deg, #ffb6c1, #ffc0cb);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 32px;
  color: white;
  box-shadow: 0 8px 24px rgba(255, 182, 193, 0.3);
}

.balance-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.balance-label {
  font-size: 16px;
  opacity: 0.9;
}

.balance-amount {
  font-size: 36px;
  font-weight: 700;
}

.balance-tip {
  font-size: 14px;
  opacity: 0.8;
}

/* 金额选择区域 */
.amount-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.15);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.quick-amounts {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.quick-btn {
  padding: 16px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  background: white;
  font-size: 16px;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-btn:hover {
  border-color: #ffb6c1;
  color: #ffb6c1;
}

.quick-btn.active {
  background: linear-gradient(135deg, #ffb6c1, #ffc0cb);
  border-color: #ffb6c1;
  color: white;
}

/* 自定义金额 */
.custom-amount {
  margin-top: 24px;
}

.input-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.input-wrapper {
  display: flex;
  align-items: center;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  padding: 12px 16px;
  transition: border-color 0.2s;
}

.input-wrapper:focus-within {
  border-color: #ffb6c1;
}

.input-prefix {
  font-size: 18px;
  font-weight: 600;
  color: #999;
  margin-right: 8px;
}

.amount-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.input-hint {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

/* 支付方式 */
.payment-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.15);
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-method {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.payment-method:hover {
  border-color: #ffb6c1;
}

.payment-method.active {
  border-color: #ffb6c1;
  background: #fff5f7;
}

.method-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffe4e6, #fff0f5);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #ffb6c1;
  margin-right: 16px;
}

.method-info {
  flex: 1;
}

.method-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.method-desc {
  font-size: 14px;
  color: #999;
}

.method-check {
  font-size: 24px;
  color: #ffb6c1;
}

/* 操作区域 */
.action-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.15);
}

.recharge-summary {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  color: #666;
}

.summary-row:last-child {
  margin-bottom: 0;
}

.summary-amount {
  font-size: 20px;
  font-weight: 600;
  color: #ffb6c1;
}

.summary-balance {
  font-size: 20px;
  font-weight: 600;
  color: #4ecdc4;
}

.recharge-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #ffb6c1, #ffc0cb);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.recharge-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 182, 193, 0.4);
}

.recharge-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .recharge-container {
    padding: 20px 12px;
  }

  .page-title {
    font-size: 24px;
  }

  .quick-amounts {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>

