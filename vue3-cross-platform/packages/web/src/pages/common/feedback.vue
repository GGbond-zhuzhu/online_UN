<template>
  <div class="feedback-page">
    <NavBar />

    <main class="feedback-main">
      <section class="hero">
        <div class="hero-inner">
          <div class="hero-title-row">
            <div>
              <p class="breadcrumb">首页 / 问题反馈</p>
              <h1 class="title">问题反馈</h1>
              <p class="subtitle">你的每一条反馈，都会进入我们的迭代清单。</p>
            </div>
            <div class="hero-actions">
              <a class="chip" href="/help">先去帮助中心</a>
              <a class="chip" href="/messages">联系在线客服</a>
            </div>
          </div>

          <div class="quick-tips">
            <div class="tip">
              <div class="tip-icon"><i class="fas fa-list-check"></i></div>
              <div class="tip-text">
                <div class="tip-title">建议这样写</div>
                <div class="tip-desc">操作步骤 + 预期结果 + 实际结果</div>
              </div>
            </div>
            <div class="tip">
              <div class="tip-icon"><i class="fas fa-camera"></i></div>
              <div class="tip-text">
                <div class="tip-title">附上截图</div>
                <div class="tip-desc">能显著提升排查效率</div>
              </div>
            </div>
            <div class="tip">
              <div class="tip-icon"><i class="fas fa-user-shield"></i></div>
              <div class="tip-text">
                <div class="tip-title">隐私提醒</div>
                <div class="tip-desc">请勿上传身份证号等敏感信息</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="shell">
        <div class="panel form-panel">
          <div class="panel-head">
            <h2>提交反馈</h2>
            <p>带 <span class="req">*</span> 的为必填项</p>
          </div>

          <form class="form" @submit.prevent="handleSubmit">
            <div class="field">
              <label>反馈类型 <span class="req">*</span></label>
              <select v-model="formData.feedbackType" class="control" required>
                <option value="">请选择反馈类型</option>
                <option value="BUG">Bug反馈</option>
                <option value="SUGGESTION">功能建议</option>
                <option value="COMPLAINT">投诉举报</option>
                <option value="OTHER">其他</option>
              </select>
            </div>

            <div class="field">
              <label>反馈标题 <span class="req">*</span></label>
              <input
                v-model="formData.title"
                class="control"
                type="text"
                placeholder="一句话概括问题（例如：课程表导入失败）"
                required
              />
            </div>

            <div class="field">
              <label>反馈内容 <span class="req">*</span></label>
              <textarea
                v-model="formData.content"
                class="control"
                rows="8"
                placeholder="请详细描述：\n1）操作步骤\n2）预期结果\n3）实际结果\n4）设备/系统版本（可选）"
                required
              ></textarea>
              <div class="hint">建议写清楚复现路径，便于我们快速定位。</div>
            </div>

            <div class="field">
              <label>联系方式（选填）</label>
              <input
                v-model="formData.contact"
                class="control"
                type="text"
                placeholder="邮箱或手机号（方便我们联系您）"
              />
              <div class="hint">填写后我们会在处理完成后同步结果。</div>
            </div>

            <div class="field">
              <label>相关截图（选填）</label>
              <div class="upload">
                <input
                  type="file"
                  ref="fileInput"
                  accept="image/*"
                  @change="handleFileChange"
                  class="file-input"
                />
                <button type="button" class="btn secondary" @click="triggerFileInput">
                  选择图片
                </button>
                <div v-if="selectedFile" class="file-pill">
                  <i class="fas fa-paperclip"></i>
                  <span class="file-name">{{ selectedFile.name }}</span>
                  <button type="button" class="file-remove" @click="removeFile" aria-label="移除文件">×</button>
                </div>
              </div>
              <div class="hint">支持 JPG、PNG 格式，大小不超过 5MB</div>
            </div>

            <div class="actions">
              <button type="submit" class="btn" :disabled="submitting">
                <span v-if="submitting">提交中...</span>
                <span v-else>提交反馈</span>
              </button>
              <button type="button" class="btn secondary" @click="handleReset" :disabled="submitting">
                重置
              </button>
            </div>
          </form>
        </div>

        <div class="panel side-panel" v-if="userStore.isLoggedIn">
          <div class="panel-head">
            <h2>我的反馈</h2>
            <p>仅展示最近提交的反馈记录</p>
          </div>

          <div class="list">
            <div v-if="myFeedbacks.length === 0" class="empty">
              暂无反馈记录
            </div>

            <div v-for="(feedback, index) in myFeedbacks" :key="index" class="item">
              <div class="item-top">
                <span class="badge">{{ getFeedbackTypeLabel(feedback.feedbackType) }}</span>
                <span class="status">{{ feedback.status }}</span>
              </div>
              <div class="item-title">{{ feedback.title }}</div>
              <div class="item-content">{{ feedback.content }}</div>
              <div class="item-meta">{{ formatDate(feedback.submitTime) }}</div>
            </div>
          </div>
        </div>
      </section>

      <div v-if="submitSuccess" class="toast" role="status" aria-live="polite">
        <div class="toast-icon"><i class="fas fa-check"></i></div>
        <div class="toast-text">
          <div class="toast-title">提交成功</div>
          <div class="toast-desc">我们会尽快处理您的反馈。</div>
        </div>
      </div>
    </main>

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
.feedback-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #fdfbff 0%, #f7f0ff 25%, #fdf3f7 60%, #ffffff 100%);
  color: #111827;
}

.feedback-main {
  flex: 1;
  padding: 26px 0 60px;
}

.hero {
  max-width: 1200px;
  margin: 0 auto 18px;
  padding: 0 24px;
}

.hero-inner {
  border-radius: 20px;
  padding: 20px 22px 16px;
  background: linear-gradient(135deg, rgba(255, 64, 129, 0.06), rgba(63, 81, 181, 0.07));
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.35);
}

.hero-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 12px;
}

.breadcrumb {
  margin: 0 0 6px;
  font-size: 13px;
  color: #9e9eaa;
}

.title {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 800;
  color: #233145;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #5f6473;
}

.hero-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.chip {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  border: 1px solid rgba(209, 213, 219, 0.8);
  background: rgba(255, 255, 255, 0.85);
  color: #374151;
  text-decoration: none;
  font-size: 13px;
  font-weight: 650;
  transition: background 0.12s ease, border-color 0.12s ease, transform 0.06s ease;
}

.chip:hover {
  border-color: rgba(255, 75, 139, 0.45);
  background: rgba(255, 247, 251, 0.9);
}

.quick-tips {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 12px;
}

.tip {
  display: flex;
  gap: 10px;
  padding: 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.06);
  backdrop-filter: blur(10px);
}

.tip-icon {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: linear-gradient(135deg, #ffebf3, #ffe4ff);
  color: #ff4b8b;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tip-title {
  font-size: 14px;
  font-weight: 850;
  color: #111827;
  margin-bottom: 2px;
}

.tip-desc {
  font-size: 12px;
  color: #6b7280;
  line-height: 1.6;
}

.shell {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 18px;
  align-items: start;
}

.panel {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 16px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: baseline;
  margin-bottom: 12px;
}

.panel-head h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 900;
  color: #111827;
}

.panel-head p {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
}

.req {
  color: #ff4b8b;
  font-weight: 900;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field label {
  font-size: 13px;
  font-weight: 800;
  color: #111827;
}

.control {
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.55);
  background: rgba(255, 255, 255, 0.9);
  padding: 10px 12px;
  font-size: 13px;
  outline: none;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
  font-family: inherit;
}

.control:focus {
  border-color: rgba(255, 75, 139, 0.65);
  box-shadow: 0 0 0 3px rgba(255, 75, 139, 0.12);
}

.hint {
  font-size: 12px;
  color: #6b7280;
}

.upload {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.file-input {
  display: none;
}

.file-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 999px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(248, 250, 252, 0.8);
  font-size: 12px;
  color: #6b7280;
}

.file-remove {
  border: none;
  background: transparent;
  color: #ff4b8b;
  font-size: 18px;
  cursor: pointer;
  line-height: 1;
  padding: 0 4px;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 2px;
}

.btn {
  border-radius: 10px;
  border: none;
  background: linear-gradient(135deg, #ff4b8b, #ff7ab2);
  color: #fff;
  padding: 10px 14px;
  font-size: 13px;
  font-weight: 800;
  cursor: pointer;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn.secondary {
  background: rgba(255, 255, 255, 0.9);
  color: #111827;
  border: 1px solid rgba(148, 163, 184, 0.55);
}

.list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.empty {
  padding: 18px;
  color: #6b7280;
  font-size: 13px;
  border-radius: 14px;
  border: 1px dashed rgba(148, 163, 184, 0.5);
  background: rgba(255, 255, 255, 0.6);
}

.item {
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  background: rgba(255, 255, 255, 0.75);
  padding: 12px;
}

.item-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
}

.badge {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 75, 139, 0.10);
  color: #ff4b8b;
  border: 1px solid rgba(255, 75, 139, 0.18);
  font-weight: 850;
}

.status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.16);
  color: #6b7280;
  border: 1px solid rgba(148, 163, 184, 0.18);
  font-weight: 700;
}

.item-title {
  font-size: 14px;
  font-weight: 850;
  color: #111827;
  margin-bottom: 6px;
}

.item-content {
  font-size: 13px;
  color: #4b5563;
  line-height: 1.75;
  margin-bottom: 8px;
}

.item-meta {
  font-size: 12px;
  color: #9ca3af;
}

.toast {
  position: fixed;
  right: 18px;
  bottom: 18px;
  display: flex;
  gap: 10px;
  align-items: center;
  padding: 12px 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 18px 46px rgba(15, 23, 42, 0.16);
  backdrop-filter: blur(10px);
  z-index: 1100;
}

.toast-icon {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: rgba(34, 197, 94, 0.12);
  color: #16a34a;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(34, 197, 94, 0.25);
}

.toast-title {
  font-size: 13px;
  font-weight: 900;
  color: #111827;
}

.toast-desc {
  font-size: 12px;
  color: #6b7280;
}

@media (max-width: 1000px) {
  .shell {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .feedback-main {
    padding: 18px 0 46px;
  }

  .hero,
  .shell {
    padding: 0 16px;
  }

  .title {
    font-size: 24px;
  }

  .actions {
    flex-direction: column;
  }
}
</style>
