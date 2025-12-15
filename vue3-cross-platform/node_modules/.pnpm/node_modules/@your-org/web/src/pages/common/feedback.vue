<template>
  <div class="common-page feedback-page">
    <!-- 全局导航栏 -->
    <NavBar />

    <!-- 页面内容 -->
    <div class="page-content">
      <div class="content-container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h1 class="page-title">问题反馈</h1>
          <p class="page-subtitle">您的意见对我们很重要，我们会认真对待每一条反馈</p>
        </div>

        <!-- 反馈表单 -->
        <div class="feedback-form-section">
          <div class="form-card">
            <form @submit.prevent="handleSubmit">
              <!-- 反馈类型 -->
              <div class="form-group">
                <label class="form-label">反馈类型 <span class="required">*</span></label>
                <select v-model="formData.feedbackType" class="form-select" required>
                  <option value="">请选择反馈类型</option>
                  <option value="BUG">Bug反馈</option>
                  <option value="SUGGESTION">功能建议</option>
                  <option value="COMPLAINT">投诉举报</option>
                  <option value="OTHER">其他</option>
                </select>
              </div>

              <!-- 反馈标题 -->
              <div class="form-group">
                <label class="form-label">反馈标题 <span class="required">*</span></label>
                <input
                  v-model="formData.title"
                  type="text"
                  class="form-input"
                  placeholder="请简要描述您的问题或建议"
                  required
                />
              </div>

              <!-- 反馈内容 -->
              <div class="form-group">
                <label class="form-label">反馈内容 <span class="required">*</span></label>
                <textarea
                  v-model="formData.content"
                  class="form-textarea"
                  rows="8"
                  placeholder="请详细描述您的问题、建议或投诉内容..."
                  required
                ></textarea>
                <div class="form-hint">建议详细描述问题，包括操作步骤、预期结果和实际结果等</div>
              </div>

              <!-- 联系方式 -->
              <div class="form-group">
                <label class="form-label">联系方式</label>
                <input
                  v-model="formData.contact"
                  type="text"
                  class="form-input"
                  placeholder="邮箱或手机号（选填，方便我们联系您）"
                />
                <div class="form-hint">填写联系方式后，我们会在处理完成后及时通知您</div>
              </div>

              <!-- 截图上传 -->
              <div class="form-group">
                <label class="form-label">相关截图</label>
                <div class="upload-section">
                  <input
                    type="file"
                    ref="fileInput"
                    accept="image/*"
                    @change="handleFileChange"
                    class="file-input"
                  />
                  <button type="button" class="upload-button" @click="triggerFileInput">
                    选择图片
                  </button>
                  <div v-if="selectedFile" class="file-info">
                    <span>{{ selectedFile.name }}</span>
                    <button type="button" class="remove-file" @click="removeFile">×</button>
                  </div>
                </div>
                <div class="form-hint">支持 JPG、PNG 格式，大小不超过 5MB</div>
              </div>

              <!-- 提交按钮 -->
              <div class="form-actions">
                <button type="submit" class="submit-button" :disabled="submitting">
                  <span v-if="submitting">提交中...</span>
                  <span v-else>提交反馈</span>
                </button>
                <button type="button" class="reset-button" @click="handleReset" :disabled="submitting">
                  重置
                </button>
              </div>
            </form>
          </div>
        </div>

        <!-- 我的反馈记录 -->
        <div class="my-feedback-section" v-if="userStore.isLoggedIn">
          <h2 class="section-title">我的反馈记录</h2>
          <div class="feedback-list">
            <div v-if="myFeedbacks.length === 0" class="empty-state">
              <p>暂无反馈记录</p>
            </div>
            <div
              v-for="(feedback, index) in myFeedbacks"
              :key="index"
              class="feedback-item"
            >
              <div class="feedback-header">
                <span class="feedback-type">{{ getFeedbackTypeLabel(feedback.feedbackType) }}</span>
                <span class="feedback-status">{{ feedback.status }}</span>
              </div>
              <h4 class="feedback-title">{{ feedback.title }}</h4>
              <p class="feedback-content">{{ feedback.content }}</p>
              <div class="feedback-meta">
                <span class="feedback-time">{{ formatDate(feedback.submitTime) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 提交成功提示 -->
        <div v-if="submitSuccess" class="success-message">
          <div class="success-icon">✓</div>
          <p>反馈提交成功！我们会尽快处理您的反馈。</p>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import { useUserStore } from '@campus/common'
import { submitFeedback, getMyFeedbacks, type FeedbackSubmit, type FeedbackType } from '@campus/common'

// 用户状态
const userStore = useUserStore()

// 表单数据
const formData = reactive<FeedbackSubmit & { contact?: string }>({
  feedbackType: '' as FeedbackType,
  title: '',
  content: '',
  contact: '',
  screenshots: undefined
})

// 选中的文件
const selectedFile = ref<File | null>(null)
const fileInput = ref<HTMLInputElement | null>(null)

// 提交状态
const submitting = ref(false)
const submitSuccess = ref(false)

// 我的反馈列表
const myFeedbacks = ref<any[]>([])

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理文件选择
const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    const file = target.files[0]
    // 检查文件大小（5MB）
    if (file.size > 5 * 1024 * 1024) {
      alert('文件大小不能超过 5MB')
      return
    }
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      alert('请选择图片文件')
      return
    }
    selectedFile.value = file
    // 转换为Base64（这里简化处理，实际应该上传到服务器）
    const reader = new FileReader()
    reader.onload = (e) => {
      formData.screenshots = e.target?.result as string
    }
    reader.readAsDataURL(file)
  }
}

// 移除文件
const removeFile = () => {
  selectedFile.value = null
  formData.screenshots = undefined
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// 获取反馈类型标签
const getFeedbackTypeLabel = (type: string): string => {
  const typeMap: Record<string, string> = {
    'BUG': 'Bug反馈',
    'SUGGESTION': '功能建议',
    'COMPLAINT': '投诉举报',
    'OTHER': '其他'
  }
  return typeMap[type] || type
}

// 格式化日期
const formatDate = (dateString: string): string => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 提交反馈
const handleSubmit = async () => {
  if (!formData.feedbackType || !formData.title || !formData.content) {
    alert('请填写完整的反馈信息')
    return
  }

  submitting.value = true
  submitSuccess.value = false

  try {
    await submitFeedback({
      feedbackType: formData.feedbackType as FeedbackType,
      title: formData.title,
      content: formData.content,
      contact: formData.contact,
      screenshots: formData.screenshots
    })

    submitSuccess.value = true
    handleReset()
    
    // 3秒后隐藏成功提示
    setTimeout(() => {
      submitSuccess.value = false
    }, 3000)

    // 如果已登录，刷新反馈列表
    if (userStore.isLoggedIn) {
      loadMyFeedbacks()
    }
  } catch (error) {
    console.error('提交反馈失败:', error)
    alert('提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  formData.feedbackType = '' as FeedbackType
  formData.title = ''
  formData.content = ''
  formData.contact = ''
  formData.screenshots = undefined
  removeFile()
}

// 加载我的反馈
const loadMyFeedbacks = async () => {
  if (!userStore.isLoggedIn) return

  try {
    const response = await getMyFeedbacks()
    myFeedbacks.value = response.list || []
  } catch (error) {
    console.error('加载反馈列表失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  if (userStore.isLoggedIn) {
    loadMyFeedbacks()
  }
})
</script>

<style scoped>
.common-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.page-content {
  flex: 1;
  padding: 2rem 0;
  background: #f8f9fa;
}

.content-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* 页面标题区域 */
.page-header {
  text-align: center;
  margin-bottom: 3rem;
  padding-top: 2rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 1rem;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #666;
}

/* 反馈表单 */
.feedback-form-section {
  margin-bottom: 3rem;
}

.form-card {
  background: #fff;
  border-radius: 12px;
  padding: 2.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 2rem;
}

.form-label {
  display: block;
  font-size: 1rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.8rem;
}

.required {
  color: #e91e63;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 0.8rem 1rem;
  border: 2px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  font-family: inherit;
  outline: none;
  transition: border-color 0.3s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  border-color: #e91e63;
}

.form-textarea {
  resize: vertical;
  min-height: 150px;
}

.form-hint {
  font-size: 0.85rem;
  color: #999;
  margin-top: 0.5rem;
}

/* 文件上传 */
.upload-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.file-input {
  display: none;
}

.upload-button {
  padding: 0.8rem 1.5rem;
  background: #f0f0f0;
  color: #333;
  border: 2px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-button:hover {
  background: #e0e0e0;
  border-color: #e91e63;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 0.9rem;
  color: #666;
}

.remove-file {
  background: none;
  border: none;
  color: #e91e63;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.submit-button,
.reset-button {
  flex: 1;
  padding: 1rem 2rem;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-button {
  background: #e91e63;
  color: #fff;
}

.submit-button:hover:not(:disabled) {
  background: #c2185b;
}

.submit-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.reset-button {
  background: #f0f0f0;
  color: #666;
}

.reset-button:hover:not(:disabled) {
  background: #e0e0e0;
}

.reset-button:disabled {
  cursor: not-allowed;
}

/* 我的反馈记录 */
.my-feedback-section {
  margin-bottom: 3rem;
}

.section-title {
  font-size: 1.8rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 2rem;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.feedback-item {
  background: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.feedback-type {
  padding: 0.3rem 0.8rem;
  background: #e91e63;
  color: #fff;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: bold;
}

.feedback-status {
  padding: 0.3rem 0.8rem;
  background: #f0f0f0;
  color: #666;
  border-radius: 12px;
  font-size: 0.85rem;
}

.feedback-title {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.8rem;
}

.feedback-content {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.feedback-meta {
  font-size: 0.85rem;
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #999;
}

/* 成功提示 */
.success-message {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #fff;
  border-radius: 12px;
  padding: 2rem 3rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  text-align: center;
  z-index: 1000;
}

.success-icon {
  width: 60px;
  height: 60px;
  background: #4caf50;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
  margin: 0 auto 1rem;
}

.success-message p {
  font-size: 1.1rem;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-container {
    padding: 0 1rem;
  }

  .page-title {
    font-size: 2rem;
  }

  .form-card {
    padding: 1.5rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .upload-section {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
