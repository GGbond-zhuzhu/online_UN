<template>
  <div class="auth-page">
    <LoginNavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-id-card"></i> 身份认证
        </h1>
        <p class="page-subtitle">完成身份认证，享受更多校园服务</p>
      </section>

      <!-- 认证状态 -->
      <section class="auth-status-card" v-if="authInfo.status === 'verified'">
        <div class="status-icon success">
          <i class="fas fa-check-circle"></i>
        </div>
        <h2 class="status-title">认证成功</h2>
        <p class="status-desc">您的身份已通过认证，可以享受所有校园服务</p>
        <div class="auth-info">
          <div class="info-item">
            <label>认证类型：</label>
            <span>{{ authInfo.type }}</span>
          </div>
          <div class="info-item">
            <label>认证时间：</label>
            <span>{{ authInfo.verifiedTime }}</span>
          </div>
        </div>
      </section>

      <!-- 认证表单 -->
      <section class="auth-form-card" v-else>
        <div class="form-tabs">
          <button
            :class="['tab-btn', { active: authType === 'student' }]"
            @click="authType = 'student'"
          >
            <i class="fas fa-user-graduate"></i> 学生认证
          </button>
          <button
            :class="['tab-btn', { active: authType === 'teacher' }]"
            @click="authType = 'teacher'"
          >
            <i class="fas fa-chalkboard-teacher"></i> 教师认证
          </button>
        </div>

        <!-- 学生认证表单 -->
        <form v-if="authType === 'student'" @submit.prevent="handleStudentAuth" class="auth-form">
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-graduation-cap"></i> 学校名称 <span class="required">*</span>
            </label>
            <input
              v-model="studentForm.school"
              type="text"
              class="form-input"
              placeholder="请输入学校名称"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-id-badge"></i> 学号 <span class="required">*</span>
            </label>
            <input
              v-model="studentForm.studentId"
              type="text"
              class="form-input"
              placeholder="请输入学号"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-user"></i> 姓名 <span class="required">*</span>
            </label>
            <input
              v-model="studentForm.name"
              type="text"
              class="form-input"
              placeholder="请输入真实姓名"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-id-card"></i> 身份证号 <span class="required">*</span>
            </label>
            <input
              v-model="studentForm.idCard"
              type="text"
              class="form-input"
              placeholder="请输入身份证号"
              maxlength="18"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-image"></i> 学生证照片 <span class="required">*</span>
            </label>
            <div class="upload-area">
              <div v-if="studentForm.studentCardImage" class="upload-preview">
                <img :src="studentForm.studentCardImage" alt="学生证" />
                <button type="button" class="btn-remove" @click="studentForm.studentCardImage = ''">
                  <i class="fas fa-times"></i>
                </button>
              </div>
              <div v-else class="upload-placeholder" @click="triggerFileInput('student')">
                <i class="fas fa-camera"></i>
                <span>点击上传学生证照片</span>
              </div>
              <input
                ref="studentFileInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleImageUpload($event, 'student')"
              />
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-submit" :disabled="loading">
              <span v-if="!loading">提交认证</span>
              <span v-else><i class="fas fa-spinner fa-spin"></i> 提交中...</span>
            </button>
          </div>
        </form>

        <!-- 教师认证表单 -->
        <form v-if="authType === 'teacher'" @submit.prevent="handleTeacherAuth" class="auth-form">
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-graduation-cap"></i> 学校名称 <span class="required">*</span>
            </label>
            <input
              v-model="teacherForm.school"
              type="text"
              class="form-input"
              placeholder="请输入学校名称"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-id-badge"></i> 工号 <span class="required">*</span>
            </label>
            <input
              v-model="teacherForm.teacherId"
              type="text"
              class="form-input"
              placeholder="请输入工号"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-user"></i> 姓名 <span class="required">*</span>
            </label>
            <input
              v-model="teacherForm.name"
              type="text"
              class="form-input"
              placeholder="请输入真实姓名"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-id-card"></i> 身份证号 <span class="required">*</span>
            </label>
            <input
              v-model="teacherForm.idCard"
              type="text"
              class="form-input"
              placeholder="请输入身份证号"
              maxlength="18"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-image"></i> 工作证照片 <span class="required">*</span>
            </label>
            <div class="upload-area">
              <div v-if="teacherForm.workCardImage" class="upload-preview">
                <img :src="teacherForm.workCardImage" alt="工作证" />
                <button type="button" class="btn-remove" @click="teacherForm.workCardImage = ''">
                  <i class="fas fa-times"></i>
                </button>
              </div>
              <div v-else class="upload-placeholder" @click="triggerFileInput('teacher')">
                <i class="fas fa-camera"></i>
                <span>点击上传工作证照片</span>
              </div>
              <input
                ref="teacherFileInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleImageUpload($event, 'teacher')"
              />
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-submit" :disabled="loading">
              <span v-if="!loading">提交认证</span>
              <span v-else><i class="fas fa-spinner fa-spin"></i> 提交中...</span>
            </button>
          </div>
        </form>
      </section>

      <!-- 认证说明 -->
      <section class="tips-section">
        <div class="tips-card">
          <h3 class="tips-title">
            <i class="fas fa-lightbulb"></i> 认证说明
          </h3>
          <ul class="tips-list">
            <li>认证信息仅用于身份验证，我们将严格保护您的隐私</li>
            <li>请确保上传的证件照片清晰可见，信息完整</li>
            <li>认证审核通常需要1-3个工作日</li>
            <li>认证通过后，您将享受更多校园服务功能</li>
            <li>如有疑问，请联系客服：400-123-4567</li>
          </ul>
        </div>
      </section>
    </div>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import LoginNavBar from '@/components/common/LoginNavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

const router = useRouter()

const authType = ref<'student' | 'teacher'>('student')
const loading = ref(false)
const studentFileInput = ref<HTMLInputElement | null>(null)
const teacherFileInput = ref<HTMLInputElement | null>(null)

// 认证信息
const authInfo = ref({
  status: 'pending', // pending, verified, rejected
  type: '',
  verifiedTime: ''
})

// 学生认证表单
const studentForm = reactive({
  school: '',
  studentId: '',
  name: '',
  idCard: '',
  studentCardImage: ''
})

// 教师认证表单
const teacherForm = reactive({
  school: '',
  teacherId: '',
  name: '',
  idCard: '',
  workCardImage: ''
})

// 触发文件选择
const triggerFileInput = (type: string) => {
  if (type === 'student' && studentFileInput.value) {
    studentFileInput.value.click()
  } else if (type === 'teacher' && teacherFileInput.value) {
    teacherFileInput.value.click()
  }
}

// 处理图片上传
const handleImageUpload = (event: Event, type: string) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  if (file.type.startsWith('image/')) {
    const reader = new FileReader()
    reader.onload = (e) => {
      const result = e.target?.result as string
      if (type === 'student') {
        studentForm.studentCardImage = result
      } else {
        teacherForm.workCardImage = result
      }
    }
    reader.readAsDataURL(file)
  }
}

// 学生认证提交
const handleStudentAuth = async () => {
  if (!studentForm.studentCardImage) {
    alert('请上传学生证照片')
    return
  }

  try {
    loading.value = true
    // TODO: 调用认证API
    // await submitStudentAuth(studentForm)
    
    // 模拟成功
    await new Promise(resolve => setTimeout(resolve, 1000))
    alert('认证信息已提交，请等待审核')
    authInfo.value = {
      status: 'pending',
      type: '学生',
      verifiedTime: ''
    }
  } catch (error) {
    console.error('认证失败:', error)
    alert('认证失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 教师认证提交
const handleTeacherAuth = async () => {
  if (!teacherForm.workCardImage) {
    alert('请上传工作证照片')
    return
  }

  try {
    loading.value = true
    // TODO: 调用认证API
    // await submitTeacherAuth(teacherForm)
    
    // 模拟成功
    await new Promise(resolve => setTimeout(resolve, 1000))
    alert('认证信息已提交，请等待审核')
    authInfo.value = {
      status: 'pending',
      type: '教师',
      verifiedTime: ''
    }
  } catch (error) {
    console.error('认证失败:', error)
    alert('认证失败，请稍后重试')
  } finally {
    loading.value = false
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

.auth-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 36px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-title i {
  color: var(--primary);
}

.page-subtitle {
  font-size: 16px;
  color: var(--muted);
}

/* 认证状态卡片 */
.auth-status-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  text-align: center;
}

.status-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 48px;
}

.status-icon.success {
  background: linear-gradient(135deg, #4caf50 0%, #388e3c 100%);
  color: white;
}

.status-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 10px;
}

.status-desc {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 20px;
}

.auth-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.info-item label {
  color: var(--muted);
}

.info-item span {
  color: var(--text);
  font-weight: 600;
}

/* 认证表单卡片 */
.auth-form-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.form-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
}

.tab-btn {
  flex: 1;
  padding: 12px 20px;
  background: transparent;
  border: none;
  border-bottom: 3px solid transparent;
  font-size: 16px;
  font-weight: 600;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.tab-btn:hover {
  color: var(--primary);
}

.tab-btn.active {
  color: var(--primary);
  border-bottom-color: var(--primary);
}

.auth-form {
  padding-top: 20px;
}

.form-group {
  margin-bottom: 25px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.form-label i {
  color: var(--primary);
}

.required {
  color: var(--primary);
  margin-left: 4px;
}

.form-input {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(216, 27, 96, 0.1);
}

/* 图片上传 */
.upload-area {
  margin-top: 10px;
}

.upload-placeholder {
  width: 100%;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafafa;
}

.upload-placeholder:hover {
  border-color: var(--primary);
  background: #fff5f9;
}

.upload-placeholder i {
  font-size: 48px;
  color: var(--primary);
}

.upload-placeholder span {
  font-size: 14px;
  color: var(--muted);
}

.upload-preview {
  position: relative;
  width: 100%;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
}

.upload-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.btn-remove {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-remove:hover {
  background: #ff4d4f;
}

.form-actions {
  margin-top: 30px;
}

.btn-submit {
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
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(216, 27, 96, 0.3);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 提示卡片 */
.tips-section {
  max-width: 100%;
}

.tips-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.tips-title {
  font-size: 16px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tips-title i {
  color: #ffc107;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  padding: 8px 0;
  font-size: 14px;
  color: var(--muted);
  line-height: 1.6;
  position: relative;
  padding-left: 20px;
}

.tips-list li::before {
  content: '•';
  color: var(--primary);
  font-weight: bold;
  position: absolute;
  left: 0;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 28px;
  }

  .form-tabs {
    flex-direction: column;
  }

  .tab-btn {
    border-bottom: none;
    border-left: 3px solid transparent;
  }

  .tab-btn.active {
    border-left-color: var(--primary);
  }
}
</style>
