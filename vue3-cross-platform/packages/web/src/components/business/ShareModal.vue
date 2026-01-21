<template>
  <div class="share-modal">
    <div class="modal-overlay" @click="handleClose"></div>
    <div class="modal-content">
      <div class="modal-header">
        <h3>分享行程</h3>
        <button class="close-btn" @click="handleClose">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="modal-body">
        <div v-if="event" class="share-content">
          <div class="event-preview">
            <div class="event-info" :style="{ borderLeftColor: event.color }">
              <h4>{{ event.title }}</h4>
              <div class="event-meta">
                <span class="meta-item">
                  <i class="fas fa-calendar-day"></i>
                  {{ event.dateKey }}
                </span>
                <span class="meta-item">
                  <i class="fas fa-clock"></i>
                  {{ event.startTime }} - {{ event.endTime }}
                </span>
                <span class="meta-item" v-if="event.location">
                  <i class="fas fa-map-marker-alt"></i>
                  {{ event.location }}
                </span>
              </div>
            </div>
          </div>

          <div class="share-options">
            <div class="share-option">
              <div class="option-icon">
                <i class="fas fa-link"></i>
              </div>
              <div class="option-content">
                <h5>复制链接</h5>
                <div class="link-input-container">
                  <input
                    ref="linkInput"
                    type="text"
                    class="link-input"
                    :value="shareLink"
                    readonly
                  />
                  <button class="copy-btn" @click="copyLink">
                    <i class="fas fa-copy"></i>
                    {{ copied ? '已复制' : '复制' }}
                  </button>
                </div>
              </div>
            </div>

            <div class="share-option">
              <div class="option-icon">
                <i class="fas fa-envelope"></i>
              </div>
              <div class="option-content">
                <h5>邮件分享</h5>
                <button class="btn btn-secondary" @click="shareByEmail">
                  <i class="fas fa-envelope-open-text"></i>
                  发送邮件
                </button>
              </div>
            </div>

            <div class="share-option">
              <div class="option-icon">
                <i class="fas fa-clipboard-list"></i>
              </div>
              <div class="option-content">
                <h5>复制行程信息</h5>
                <button class="btn btn-secondary" @click="copyEventInfo">
                  <i class="fas fa-clipboard-check"></i>
                  复制信息
                </button>
              </div>
            </div>
          </div>

          <div class="share-tips">
            <h5>分享说明：</h5>
            <ul>
              <li>生成的链接可以分享给他人查看行程</li>
              <li>分享的行程不包含个人隐私信息</li>
              <li>链接有效期为30天</li>
            </ul>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-cancel" @click="handleClose">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, defineProps, defineEmits } from 'vue'
import { Schedule } from '../../types'

interface Props {
  event?: Schedule
  visible?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  event: () => ({} as Schedule),
  visible: false
})

const emit = defineEmits<{
  'close': []
}>()

// 分享链接
const shareLink = computed(() => {
  if (props.event?.id) {
    return `${window.location.origin}/schedule/share/${props.event.id}`
  }
  return ''
})

// 复制状态
const copied = ref(false)
const linkInput = ref<HTMLInputElement | null>(null)

// 复制链接
const copyLink = () => {
  if (linkInput.value) {
    linkInput.value.select()
    document.execCommand('copy')
    copied.value = true
    setTimeout(() => {
      copied.value = false
    }, 2000)
  }
}

// 通过邮件分享
const shareByEmail = () => {
  if (!props.event) return
  
  const subject = `分享行程：${props.event.title}`
  const body = `我想分享一个行程给你：\n\n标题：${props.event.title}\n日期：${props.event.dateKey}\n时间：${props.event.startTime} - ${props.event.endTime}\n${props.event.location ? `地点：${props.event.location}\n` : ''}${props.event.description ? `描述：${props.event.description}\n` : ''}\n点击查看详情：${shareLink.value}`
  
  window.location.href = `mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`
}

// 复制行程信息
const copyEventInfo = () => {
  if (!props.event) return
  
  const eventInfo = `行程信息\n标题：${props.event.title}\n日期：${props.event.dateKey}\n时间：${props.event.startTime} - ${props.event.endTime}\n${props.event.location ? `地点：${props.event.location}\n` : ''}${props.event.description ? `描述：${props.event.description}\n` : ''}\n查看链接：${shareLink.value}`
  
  navigator.clipboard.writeText(eventInfo).then(() => {
    alert('行程信息已复制到剪贴板！')
  }).catch(err => {
    console.error('复制失败：', err)
    alert('复制失败，请手动复制')
  })
}

// 关闭弹窗
const handleClose = () => {
  emit('close')
}
</script>

<style scoped lang="scss">
.share-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #666;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: all 0.3s;

  &:hover {
    background: #f5f5f5;
    color: #333;
  }
}

.modal-body {
  padding: 20px;
}

/* 事件预览 */
.event-preview {
  margin-bottom: 30px;
}

.event-info {
  padding: 20px;
  background: #f9f9f9;
  border-radius: 12px;
  border-left: 4px solid #FF6B9D;
}

.event-info h4 {
  margin: 0 0 15px;
  font-size: 18px;
  color: #333;
}

.event-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #666;
}

.meta-item i {
  color: #FF6B9D;
}

/* 分享选项 */
.share-options {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 30px;
}

.share-option {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 12px;
  align-items: center;
}

.option-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.option-content {
  flex: 1;
}

.option-content h5 {
  margin: 0 0 10px;
  font-size: 16px;
  color: #333;
}

/* 链接输入 */
.link-input-container {
  display: flex;
  gap: 10px;
}

.link-input {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
}

.copy-btn {
  background: #4caf50;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: #388e3c;
  }
}

/* 分享说明 */
.share-tips {
  background: #e3f2fd;
  border-left: 4px solid #2196f3;
  padding: 15px;
  border-radius: 8px;
}

.share-tips h5 {
  margin: 0 0 10px;
  color: #1976d2;
  font-size: 16px;
}

.share-tips ul {
  margin: 0;
  padding-left: 20px;
}

.share-tips li {
  color: #0d47a1;
  font-size: 14px;
  margin-bottom: 5px;
}

/* 按钮样式 */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(255, 107, 157, 0.3);
}

.btn-secondary {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background: #e0e0e0;
}

.btn-cancel {
  background: white;
  color: #666;
  border: 1px solid #ddd;
}

.btn-cancel:hover {
  background: #f5f5f5;
}
</style>
