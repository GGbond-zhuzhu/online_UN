<template>
  <div class="transfer-page">
    <NavBar />
    
    <div class="page-container">
      <!-- 顶部余额卡片 -->
      <div class="balance-card">
        <div class="balance-header">
          <div class="balance-left">
            <div class="balance-label">当前余额</div>
            <div class="balance-value">¥{{ currentBalance.toFixed(2) }}</div>
          </div>
          <div class="balance-icon-wrapper">
            <i class="fas fa-wallet"></i>
          </div>
        </div>
        <div class="limit-notice">
          <div class="limit-item">
            <i class="fas fa-shield-alt"></i>
            <span>单笔限额 ¥{{ singleLimit.toFixed(2) }}</span>
          </div>
          <div class="limit-divider"></div>
          <div class="limit-item">
            <i class="fas fa-calendar-day"></i>
            <span>单日限额 ¥{{ dailyLimit.toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- 转账表单卡片 -->
      <div class="transfer-card">
        <div class="card-header">
          <div class="header-left">
            <div class="header-icon-wrapper">
              <i class="fas fa-exchange-alt"></i>
            </div>
            <div class="header-titles">
              <div class="card-title">校园卡转账</div>
              <div class="card-subtitle">安全便捷，实时到账</div>
            </div>
          </div>
          <button class="help-btn" @click="showHelp">
            <i class="fas fa-question-circle"></i>
          </button>
        </div>

        <!-- 收款方式选择 -->
        <div class="payment-method-section">
          <div class="section-label">收款方式</div>
          <div class="method-tabs">
            <div
              v-for="method in paymentMethods"
              :key="method.value"
              class="method-tab"
              :class="{ active: selectedMethod === method.value }"
              @click="selectedMethod = method.value"
            >
              <div class="method-icon-wrapper">
                <i :class="method.icon"></i>
              </div>
              <span>{{ method.label }}</span>
            </div>
          </div>
        </div>

        <!-- 收款信息输入 -->
        <div class="form-section">
          <div class="form-item">
            <div class="form-label">
              {{ selectedMethod === 'cardNo' ? '收款卡号' : '收款人手机号' }}
            </div>
            <div class="input-wrapper">
              <div class="input-left-icon">
                <i class="fas fa-user"></i>
              </div>
              <input
                class="form-input"
                v-model="transferForm.receiver"
                :placeholder="selectedMethod === 'cardNo' ? '请输入收款卡号' : '请输入收款人手机号'"
                type="text"
                maxlength="20"
              />
            </div>
            <div class="receiver-info-card" v-if="receiverInfo">
              <div class="receiver-avatar">
                <img v-if="receiverInfo.avatar" :src="receiverInfo.avatar" alt="" />
                <i v-else class="fas fa-user-circle"></i>
              </div>
              <div class="receiver-details">
                <div class="receiver-name">{{ receiverInfo.name }}</div>
                <div class="receiver-id">
                  {{ selectedMethod === 'cardNo' ? '卡号' : '手机号' }}：{{ receiverInfo.cardNo }}
                </div>
              </div>
              <div class="receiver-status">
                <i class="fas fa-check-circle"></i>
                <span>已验证</span>
              </div>
            </div>
          </div>

          <div class="form-item">
            <div class="form-label">转账金额</div>
            <div class="amount-input-wrapper">
              <div class="amount-prefix">¥</div>
              <input
                class="amount-input"
                v-model="transferForm.amount"
                type="number"
                placeholder="0.00"
                @input="handleAmountInput"
              />
            </div>
            <div class="quick-amounts">
              <button
                v-for="amount in quickAmounts"
                :key="amount"
                class="quick-amount-btn"
                @click="transferForm.amount = amount.toString()"
              >
                ¥{{ amount }}
              </button>
            </div>
          </div>

          <div class="form-item">
            <div class="form-label">备注（选填）</div>
            <textarea
              class="form-textarea"
              v-model="transferForm.remark"
              placeholder="请输入转账备注"
              maxlength="50"
            ></textarea>
          </div>
        </div>

        <!-- 确认转账按钮 -->
        <div class="submit-section">
          <button
            class="submit-btn"
            :disabled="!canSubmit"
            @click="handleSubmit"
          >
            确认转账
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'

const router = useRouter()

// 余额信息
const currentBalance = ref(328.50)
const singleLimit = ref(5000)
const dailyLimit = ref(20000)

// 收款方式
const paymentMethods = [
  { value: 'cardNo', label: '卡号', icon: 'fas fa-credit-card' },
  { value: 'phone', label: '手机号', icon: 'fas fa-mobile-alt' }
]
const selectedMethod = ref('cardNo')

// 转账表单
const transferForm = ref({
  receiver: '',
  amount: '',
  remark: ''
})

const receiverInfo = ref<any>(null)
const quickAmounts = [50, 100, 200, 500]

// 验证表单
const canSubmit = computed(() => {
  return transferForm.value.receiver && 
         transferForm.value.amount && 
         parseFloat(transferForm.value.amount) > 0 &&
         parseFloat(transferForm.value.amount) <= singleLimit.value
})

// 处理金额输入
const handleAmountInput = () => {
  const amount = Number(transferForm.value.amount)
  if (Number.isFinite(amount) && amount > singleLimit.value) {
    transferForm.value.amount = String(singleLimit.value)
    alert(`单笔限额为¥${singleLimit.value}`)
  }
}

// 帮助说明
const showHelp = () => {
  alert('请输入收款人信息与转账金额，确认后将发起校园卡转账（演示页面）。')
}

// 提交转账（演示）
const handleSubmit = async () => {
  if (!canSubmit.value) return

  const amount = Number(transferForm.value.amount)
  if (!Number.isFinite(amount) || amount <= 0) {
    alert('请输入有效的转账金额')
    return
  }

  if (amount > currentBalance.value) {
    alert('余额不足')
    return
  }

  // TODO: 接入真实接口（common/api/ecard 或后端接口）
  alert(`已发起转账：¥${amount.toFixed(2)}`)
  router.back()
}
</script>

<style scoped lang="scss">
.transfer-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;

      .header-icon-wrapper {
        width: 48px;
        height: 48px;
        background: linear-gradient(135deg, #FF1493 0%, #FF69B4 100%);
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 20px;
      }

      .header-titles {
        .card-title {
          font-size: 20px;
          font-weight: bold;
          color: #333;
        }

        .card-subtitle {
          font-size: 14px;
          color: #999;
          margin-top: 4px;
        }
      }
    }

    .help-btn {
      background: #f5f5f5;
      border: none;
      width: 36px;
      height: 36px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #666;
      cursor: pointer;

      &:hover {
        background: #eee;
      }
    }
  }

  .payment-method-section {
    margin-bottom: 24px;

    .section-label {
      font-size: 14px;
      color: #666;
      margin-bottom: 12px;
    }

    .method-tabs {
      display: flex;
      gap: 12px;

      .method-tab {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
        padding: 16px;
        border: 2px solid #eee;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s;

        &.active {
          border-color: #FF1493;
          background: rgba(255, 20, 147, 0.05);
        }

        .method-icon-wrapper {
          width: 40px;
          height: 40px;
          background: #f5f5f5;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #666;
        }

        &.active .method-icon-wrapper {
          background: rgba(255, 20, 147, 0.1);
          color: #FF1493;
        }
      }
    }
  }

  .form-section {
    .form-item {
      margin-bottom: 24px;

      .form-label {
        font-size: 14px;
        color: #333;
        margin-bottom: 8px;
        font-weight: 500;
      }

      .input-wrapper {
        display: flex;
        align-items: center;
        background: #f5f5f5;
        border-radius: 12px;
        padding: 0 16px;

        .input-left-icon {
          color: #999;
          margin-right: 12px;
        }

        .form-input {
          flex: 1;
          border: none;
          background: transparent;
          padding: 16px 0;
          font-size: 16px;
          outline: none;
        }
      }

      .receiver-info-card {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px;
        background: #f9f9f9;
        border-radius: 12px;
        margin-top: 12px;

        .receiver-avatar {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background: #ddd;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #999;

          img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            object-fit: cover;
          }
        }

        .receiver-details {
          flex: 1;

          .receiver-name {
            font-size: 16px;
            font-weight: 500;
            color: #333;
          }

          .receiver-id {
            font-size: 12px;
            color: #999;
            margin-top: 4px;
          }
        }

        .receiver-status {
          display: flex;
          align-items: center;
          gap: 4px;
          color: #52c41a;
          font-size: 12px;
        }
      }

      .amount-input-wrapper {
        display: flex;
        align-items: center;
        background: #f5f5f5;
        border-radius: 12px;
        padding: 0 16px;

        .amount-prefix {
          font-size: 24px;
          font-weight: bold;
          color: #333;
          margin-right: 8px;
        }

        .amount-input {
          flex: 1;
          border: none;
          background: transparent;
          padding: 16px 0;
          font-size: 24px;
          font-weight: bold;
          outline: none;
        }
      }

      .quick-amounts {
        display: flex;
        gap: 12px;
        margin-top: 12px;

        .quick-amount-btn {
          flex: 1;
          padding: 8px;
          border: 1px solid #ddd;
          border-radius: 8px;
          background: white;
          color: #666;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            border-color: #FF1493;
            color: #FF1493;
          }
        }
      }

      .form-textarea {
        width: 100%;
        min-height: 80px;
        padding: 12px;
        border: 1px solid #eee;
        border-radius: 12px;
        font-size: 14px;
        resize: vertical;
        outline: none;

        &:focus {
          border-color: #FF1493;
        }
      }
    }
  }

  .submit-section {
    margin-top: 32px;

    .submit-btn {
      width: 100%;
      padding: 16px;
      background: linear-gradient(135deg, #FF1493 0%, #FF69B4 100%);
      color: white;
      border: none;
      border-radius: 12px;
      font-size: 16px;
      font-weight: bold;
      cursor: pointer;
      transition: all 0.3s;

      &:hover:not(:disabled) {
        opacity: 0.9;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(255, 20, 147, 0.3);
      }

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
  }
}
</style>

