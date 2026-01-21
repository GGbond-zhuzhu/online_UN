<template>
  <div class="card-detail-page">
    <NavBar />
    
    <div class="page-container">
      <!-- 卡片信息卡片 -->
      <div class="card-info-card">
        <div class="card-header">
          <i class="fas fa-credit-card"></i>
          <div class="card-info">
            <div class="card-title">校园E卡通</div>
            <div class="card-number">NO. {{ formattedCardNo }}</div>
          </div>
        </div>
        <div class="card-status">
          <div class="status-label">卡片状态</div>
          <div class="status-value" :class="cardStatusClass">{{ cardStatusText }}</div>
        </div>
      </div>

      <!-- 卡片详情列表 -->
      <div class="detail-list">
        <div class="detail-item">
          <div class="detail-label">卡号</div>
          <div class="detail-value">{{ formattedCardNo }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">余额</div>
          <div class="detail-value amount">¥{{ formattedBalance }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">卡片类型</div>
          <div class="detail-value">{{ cardType }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">开卡时间</div>
          <div class="detail-value">{{ createTime }}</div>
        </div>
        <div class="detail-item">
          <div class="detail-label">有效期</div>
          <div class="detail-value">{{ expireTime }}</div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button class="action-btn" @click="handleReportLoss">挂失</button>
        <button class="action-btn" @click="handleUnlock">解挂</button>
        <button class="action-btn" @click="handleReplace">补卡</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import { useEcardStore } from '@campus/common'

const router = useRouter()
const ecardStore = useEcardStore()

// 卡片信息
const formattedCardNo = computed(() => {
  const cardNo = ecardStore.ecardInfo?.cardNo || '202400123456'
  return cardNo.replace(/(\d{4})(?=\d)/g, '$1 ')
})

const formattedBalance = computed(() => {
  return (ecardStore.ecardInfo?.balance ?? 328.50).toFixed(2)
})

const cardStatusText = computed(() => {
  const status = ecardStore.ecardInfo?.status || 'NORMAL'
  const statusMap: Record<string, string> = {
    'NORMAL': '正常',
    'LOST': '已挂失',
    'FROZEN': '已冻结',
    'EXPIRED': '已过期'
  }
  return statusMap[status] || '未知'
})

const cardStatusClass = computed(() => {
  const status = ecardStore.ecardInfo?.status || 'NORMAL'
  return {
    'status-normal': status === 'NORMAL',
    'status-lost': status === 'LOST',
    'status-frozen': status === 'FROZEN',
    'status-expired': status === 'EXPIRED'
  }
})

const cardType = computed(() => {
  return ecardStore.ecardInfo?.isVisitorCard ? '游客卡' : '学生卡'
})

const createTime = ref('2024-01-01')
const expireTime = ref('2027-12-31')

// 挂失
const handleReportLoss = () => {
  if (confirm('挂失后卡片将无法使用，确定要挂失吗？')) {
    // 调用API
    alert('挂失成功')
  }
}

// 解挂
const handleUnlock = () => {
  if (confirm('确定要解挂吗？')) {
    // 调用API
    alert('解挂成功')
  }
}

// 补卡
const handleReplace = () => {
  if (confirm('补卡需要支付工本费，确定要补卡吗？')) {
    // 调用API
    alert('补卡申请已提交')
  }
}
</script>

<style scoped lang="scss">
.card-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.card-info-card {
  background: linear-gradient(135deg, #FF1493 0%, #FF69B4 100%);
  border-radius: 16px;
  padding: 24px;
  color: white;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(255, 20, 147, 0.3);

  .card-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 20px;

    i {
      font-size: 48px;
      opacity: 0.3;
    }

    .card-info {
      .card-title {
        font-size: 24px;
        font-weight: bold;
        margin-bottom: 8px;
      }

      .card-number {
        font-size: 14px;
        opacity: 0.9;
      }
    }
  }

  .card-status {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 20px;
    border-top: 1px solid rgba(255, 255, 255, 0.3);

    .status-label {
      font-size: 14px;
      opacity: 0.9;
    }

    .status-value {
      padding: 6px 16px;
      border-radius: 20px;
      font-size: 14px;
      font-weight: 500;
      background: rgba(255, 255, 255, 0.2);

      &.status-normal {
        background: rgba(82, 196, 26, 0.3);
      }

      &.status-lost {
        background: rgba(255, 77, 79, 0.3);
      }

      &.status-frozen {
        background: rgba(250, 173, 20, 0.3);
      }

      &.status-expired {
        background: rgba(140, 140, 140, 0.3);
      }
    }
  }
}

.detail-list {
  background: white;
  border-radius: 16px;
  padding: 0;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;

  .detail-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .detail-label {
      font-size: 14px;
      color: #666;
    }

    .detail-value {
      font-size: 16px;
      color: #333;
      font-weight: 500;

      &.amount {
        color: #FF1493;
        font-size: 20px;
        font-weight: bold;
      }
    }
  }
}

.action-buttons {
  display: flex;
  gap: 12px;

  .action-btn {
    flex: 1;
    padding: 14px;
    border: 1px solid #ddd;
    border-radius: 12px;
    background: white;
    color: #333;
    font-size: 16px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      border-color: #FF1493;
      color: #FF1493;
    }

    &:active {
      transform: scale(0.98);
    }
  }
}
</style>

