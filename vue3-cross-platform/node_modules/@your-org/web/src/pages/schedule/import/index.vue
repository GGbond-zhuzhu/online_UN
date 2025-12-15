<template>
  <div class="import-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-file-import"></i> 导入课程表
        </h1>
        <p class="page-subtitle">支持多种方式导入课程表，快速同步您的课程安排</p>
      </section>

      <!-- 导入方式选择 -->
      <section class="import-methods">
        <div class="method-tabs">
          <button
            :class="['tab-btn', { active: importMethod === 'file' }]"
            @click="importMethod = 'file'"
          >
            <i class="fas fa-file-excel"></i> 文件导入
          </button>
          <button
            :class="['tab-btn', { active: importMethod === 'manual' }]"
            @click="importMethod = 'manual'"
          >
            <i class="fas fa-keyboard"></i> 手动录入
          </button>
          <button
            :class="['tab-btn', { active: importMethod === 'link' }]"
            @click="importMethod = 'link'"
          >
            <i class="fas fa-link"></i> 链接导入
          </button>
        </div>

        <!-- 文件导入 -->
        <div v-if="importMethod === 'file'" class="method-content">
          <div class="upload-section">
            <div class="upload-area" @click="triggerFileInput">
              <i class="fas fa-cloud-upload-alt"></i>
              <p>点击上传或拖拽文件到此处</p>
              <span class="upload-hint">支持 Excel (.xlsx, .xls) 和 CSV 格式</span>
            </div>
            <input
              ref="fileInput"
              type="file"
              accept=".xlsx,.xls,.csv"
              style="display: none"
              @change="handleFileUpload"
            />
            <div v-if="uploadedFile" class="file-info">
              <i class="fas fa-file-excel"></i>
              <span>{{ uploadedFile.name }}</span>
              <button class="btn-remove" @click="removeFile">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
          <div class="template-download">
            <a href="#" class="download-link" @click.prevent="downloadTemplate">
              <i class="fas fa-download"></i> 下载导入模板
            </a>
          </div>
          <div v-if="uploadedFile" class="import-actions">
            <button class="btn-import" @click="handleFileImport">
              <i class="fas fa-file-import"></i> 开始导入
            </button>
          </div>
        </div>

        <!-- 手动录入 -->
        <div v-if="importMethod === 'manual'" class="method-content">
          <form @submit.prevent="handleManualImport" class="manual-form">
            <div class="form-group">
              <label class="form-label">课程名称 <span class="required">*</span></label>
              <input
                v-model="manualForm.courseName"
                type="text"
                class="form-input"
                placeholder="例如：高等数学"
                required
              />
            </div>
            <div class="form-row">
              <div class="form-group">
                <label class="form-label">上课时间 <span class="required">*</span></label>
                <select v-model="manualForm.dayOfWeek" class="form-select" required>
                  <option value="">选择星期</option>
                  <option value="1">周一</option>
                  <option value="2">周二</option>
                  <option value="3">周三</option>
                  <option value="4">周四</option>
                  <option value="5">周五</option>
                  <option value="6">周六</option>
                  <option value="7">周日</option>
                </select>
              </div>
              <div class="form-group">
                <label class="form-label">节次 <span class="required">*</span></label>
                <input
                  v-model="manualForm.timeSlot"
                  type="text"
                  class="form-input"
                  placeholder="例如：1-2节"
                  required
                />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">上课地点</label>
              <input
                v-model="manualForm.location"
                type="text"
                class="form-input"
                placeholder="例如：教学楼A101"
              />
            </div>
            <div class="form-group">
              <label class="form-label">任课教师</label>
              <input
                v-model="manualForm.teacher"
                type="text"
                class="form-input"
                placeholder="例如：张老师"
              />
            </div>
            <div class="form-actions">
              <button type="button" class="btn-add" @click="addCourse">
                <i class="fas fa-plus"></i> 添加课程
              </button>
              <button type="submit" class="btn-submit">
                <i class="fas fa-check"></i> 完成导入
              </button>
            </div>
          </form>
          <div v-if="courses.length > 0" class="courses-list">
            <h3>已添加课程 ({{ courses.length }})</h3>
            <div class="course-item" v-for="(course, index) in courses" :key="index">
              <div class="course-info">
                <span class="course-name">{{ course.courseName }}</span>
                <span class="course-time">{{ getDayLabel(course.dayOfWeek) }} {{ course.timeSlot }}</span>
              </div>
              <button class="btn-remove-course" @click="removeCourse(index)">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
        </div>

        <!-- 链接导入 -->
        <div v-if="importMethod === 'link'" class="method-content">
          <div class="link-section">
            <div class="form-group">
              <label class="form-label">课程表链接</label>
              <input
                v-model="linkForm.url"
                type="url"
                class="form-input"
                placeholder="请输入课程表链接（如：教务系统链接）"
              />
            </div>
            <div class="form-group">
              <label class="form-label">账号（选填）</label>
              <input
                v-model="linkForm.username"
                type="text"
                class="form-input"
                placeholder="如果需要登录，请输入账号"
              />
            </div>
            <div class="form-group">
              <label class="form-label">密码（选填）</label>
              <input
                v-model="linkForm.password"
                type="password"
                class="form-input"
                placeholder="如果需要登录，请输入密码"
              />
            </div>
            <button class="btn-submit" @click="handleLinkImport">
              <i class="fas fa-link"></i> 开始导入
            </button>
          </div>
        </div>
      </section>

      <!-- 导入说明 -->
      <section class="instructions-section">
        <h2 class="section-title">导入说明</h2>
        <div class="instructions-list">
          <div class="instruction-item">
            <div class="instruction-number">1</div>
            <div class="instruction-content">
              <h3>文件格式要求</h3>
              <p>支持 Excel (.xlsx, .xls) 和 CSV 格式，请确保文件包含课程名称、时间、地点等信息</p>
            </div>
          </div>
          <div class="instruction-item">
            <div class="instruction-number">2</div>
            <div class="instruction-content">
              <h3>数据格式</h3>
              <p>课程名称、上课时间（星期+节次）、上课地点、任课教师等信息需要完整填写</p>
            </div>
          </div>
          <div class="instruction-item">
            <div class="instruction-number">3</div>
            <div class="instruction-content">
              <h3>导入后处理</h3>
              <p>导入完成后，系统会自动解析课程信息，您可以在行程管理页面查看和编辑</p>
            </div>
          </div>
        </div>
      </section>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()
const route = useRoute()

// 根据路由参数设置导入方法
const importMethod = ref<'file' | 'manual' | 'link'>('file')
const fileInput = ref<HTMLInputElement | null>(null)
const uploadedFile = ref<File | null>(null)

// 手动录入表单
const manualForm = reactive({
  courseName: '',
  dayOfWeek: '',
  timeSlot: '',
  location: '',
  teacher: ''
})

const courses = ref<any[]>([])

// 链接导入表单
const linkForm = reactive({
  url: '',
  username: '',
  password: ''
})

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理文件上传
const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    uploadedFile.value = file
    // TODO: 解析文件并导入课程
  }
}

// 移除文件
const removeFile = () => {
  uploadedFile.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// 下载模板
const downloadTemplate = () => {
  // TODO: 下载导入模板文件
  alert('模板下载功能开发中...')
}

// 添加课程
const addCourse = () => {
  if (!manualForm.courseName || !manualForm.dayOfWeek || !manualForm.timeSlot) {
    alert('请填写完整的课程信息')
    return
  }

  courses.value.push({ ...manualForm })
  
  // 重置表单
  manualForm.courseName = ''
  manualForm.dayOfWeek = ''
  manualForm.timeSlot = ''
  manualForm.location = ''
  manualForm.teacher = ''
}

// 移除课程
const removeCourse = (index: number) => {
  courses.value.splice(index, 1)
}

// 获取星期标签
const getDayLabel = (day: string) => {
  const dayMap: Record<string, string> = {
    '1': '周一',
    '2': '周二',
    '3': '周三',
    '4': '周四',
    '5': '周五',
    '6': '周六',
    '7': '周日'
  }
  return dayMap[day] || day
}

// 处理文件导入
const handleFileImport = async () => {
  if (!uploadedFile.value) {
    alert('请先选择文件')
    return
  }

  try {
    // TODO: 调用文件导入API
    // const formData = new FormData()
    // formData.append('file', uploadedFile.value)
    // const result = await importFromFileAPI(formData)
    
    alert(`文件"${uploadedFile.value.name}"导入功能开发中...\n请使用手动录入方式`)
  } catch (error) {
    console.error('文件导入失败:', error)
    alert('导入失败，请稍后重试')
  }
}

// 初始化：根据路由参数设置导入方法
onMounted(() => {
  const method = route.query.method as string
  if (method === 'file' || method === 'manual' || method === 'link') {
    importMethod.value = method
  }
})

// 手动导入提交
const handleManualImport = () => {
  if (courses.value.length === 0) {
    alert('请至少添加一门课程')
    return
  }

  try {
    // TODO: 调用导入API
    // const result = await importCoursesAPI({
    //   semester: '2024-2025-1',
    //   courses: courses.value
    // })
    
    // 模拟导入成功，将课程添加到行程列表
    courses.value.forEach((course, index) => {
      // 这里应该调用添加行程的API，暂时模拟
      console.log('导入课程:', course)
    })
    
    alert(`成功导入 ${courses.value.length} 门课程`)
    router.push('/schedule')
  } catch (error) {
    console.error('导入失败:', error)
    alert('导入失败，请稍后重试')
  }
}

// 链接导入
const handleLinkImport = () => {
  if (!linkForm.url) {
    alert('请输入课程表链接')
    return
  }

  try {
    // TODO: 调用链接导入API
    // const result = await importFromLinkAPI({
    //   url: linkForm.url,
    //   username: linkForm.username || undefined,
    //   password: linkForm.password || undefined
    // })
    
    alert('链接导入功能开发中...\n请使用文件导入或手动录入方式')
  } catch (error) {
    console.error('导入失败:', error)
    alert('导入失败，请稍后重试')
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

.import-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 900px;
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

/* 导入方式 */
.import-methods {
  background: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.method-tabs {
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

.method-content {
  padding-top: 20px;
}

/* 文件上传 */
.upload-section {
  margin-bottom: 20px;
}

.upload-area {
  width: 100%;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 15px;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafafa;
}

.upload-area:hover {
  border-color: var(--primary);
  background: #fff5f9;
}

.upload-area i {
  font-size: 48px;
  color: var(--primary);
}

.upload-area p {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
}

.upload-hint {
  font-size: 12px;
  color: var(--muted);
}

.file-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-top: 15px;
}

.file-info i {
  color: var(--primary);
  font-size: 20px;
}

.file-info span {
  flex: 1;
  font-size: 14px;
  color: var(--text);
}

.btn-remove {
  padding: 4px 8px;
  border: none;
  background: #ff4d4f;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

.template-download {
  text-align: center;
}

.download-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--primary);
  text-decoration: none;
  font-weight: 600;
}

.download-link:hover {
  text-decoration: underline;
}

/* 导入操作按钮 */
.import-actions {
  margin-top: 20px;
  text-align: center;
}

.btn-import {
  padding: 12px 30px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
}

.btn-import:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(216, 27, 96, 0.4);
}

/* 手动录入表单 */
.manual-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}

.required {
  color: var(--primary);
}

.form-input,
.form-select {
  padding: 12px 15px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(216, 27, 96, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-actions {
  display: flex;
  gap: 15px;
}

.btn-add,
.btn-submit {
  flex: 1;
  padding: 12px 20px;
  border-radius: 8px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-add {
  background: white;
  color: var(--primary);
  border: 2px solid rgba(240, 240, 240, 0.9);
}

.btn-add:hover {
  background: #f9f0ff;
  border-color: var(--primary);
}

.btn-submit {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
}

.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
}

/* 课程列表 */
.courses-list {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #f0f0f0;
}

.courses-list h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 15px;
}

.course-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f9f0ff;
  border-radius: 8px;
  margin-bottom: 10px;
}

.course-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.course-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}

.course-time {
  font-size: 12px;
  color: var(--muted);
}

.btn-remove-course {
  padding: 4px 8px;
  border: none;
  background: #ff4d4f;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

/* 链接导入 */
.link-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 说明部分 */
.instructions-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 25px;
  text-align: center;
}

.instructions-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.instruction-item {
  display: flex;
  gap: 20px;
}

.instruction-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
}

.instruction-content h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 6px;
}

.instruction-content p {
  font-size: 14px;
  color: var(--muted);
  line-height: 1.6;
}

@media (max-width: 768px) {
  .method-tabs {
    flex-direction: column;
  }

  .tab-btn {
    border-bottom: none;
    border-left: 3px solid transparent;
  }

  .tab-btn.active {
    border-left-color: var(--primary);
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
