<template>
  <!-- 学生身份认证申请页面整体容器，使用统一粉色渐变背景，保证与登录/认证风格一致 -->
  <div class="student-auth-page">
    <!-- 复用登录导航栏组件，保持顶部导航一致 -->
    <LoginNavBar />

    <!-- 页面主体内容容器，限制最大宽度，居中显示 -->
    <div class="page-container">
      <!-- 顶部标题区域：说明这是专门的学生身份认证申请页面 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-user-graduate"></i>
          学生身份认证申请
        </h1>
        <p class="page-subtitle">
          请填写真实的学籍信息，用于学校系统校验；如学校暂未接入，可同时填写证明说明，方便后续人工审核。
        </p>
      </section>

      <!-- 申请表单卡片 -->
      <section class="form-card">
        <!-- 错误提示区域：仅在提交出错时显示 -->
        <div v-if="errorMessage" class="error-alert">
          <i class="fas fa-exclamation-circle"></i>
          <span>{{ errorMessage }}</span>
        </div>

        <!-- 表单使用原生 form，配合 @submit.prevent 阻止默认跳转 -->
        <form class="auth-form" @submit.prevent="handleSubmit">
          <!-- 学校选择：当前版本使用前端配置的校园列表 -->
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-school"></i>
              申请学校
              <span class="required">*</span>
            </label>
            <select
              v-model.number="selectedCampusId"
              class="form-input"
              required
            >
              <option
                v-for="campus in campusList"
                :key="campus.id"
                :value="campus.id"
              >
                {{ campus.name }}
              </option>
            </select>
            <p class="form-hint">学校列表由平台预先配置，后续会支持从后端动态拉取。</p>
          </div>

          <!-- 学号输入 -->
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-id-badge"></i>
              学号
              <span class="required">*</span>
            </label>
            <input
              v-model="form.studentId"
              type="text"
              class="form-input"
              placeholder="请输入在校学号（与学校教务系统一致）"
              required
            />
          </div>

          <!-- 姓名输入 -->
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-user"></i>
              姓名
              <span class="required">*</span>
            </label>
            <input
              v-model="form.name"
              type="text"
              class="form-input"
              placeholder="请输入身份证上的真实姓名"
              required
            />
          </div>

          <!-- 身份证号输入 -->
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-id-card"></i>
              身份证号
              <span class="required">*</span>
            </label>
            <input
              v-model="form.idCard"
              type="text"
              class="form-input"
              placeholder="请输入18位身份证号"
              maxlength="18"
              required
            />
          </div>

          <!-- 教务系统/学信网验证码输入 -->
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-key"></i>
              教务系统 / 学信网验证码
              <span class="required">*</span>
            </label>
            <input
              v-model="form.verificationCode"
              type="text"
              class="form-input"
              placeholder="请输入学校下发的验证码（没有可先留空，后续补充）"
              required
            />
            <p class="form-hint">
              若学校暂未开通验证码服务，可以先填写"暂未获取"，后续由辅导员或教务老师线下协助核验。
            </p>
          </div>

          <!-- 动态人脸身份认证（必填） -->
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-user-check"></i>
              动态人脸身份认证
              <span class="required">*</span>
            </label>
            <p class="form-hint">请进行人脸识别和活体检测，确保身份真实性</p>
            
            <!-- 人脸识别状态显示 -->
            <div v-if="faceDetectStatus === 'success'" class="face-status success">
              <i class="fas fa-check-circle"></i>
              <span class="status-text">人脸识别通过</span>
              <span class="status-detail">活体检测分数: {{ faceDetectResult?.livenessScore?.toFixed(2) || '0.00' }}</span>
            </div>
            <div v-else-if="faceDetectStatus === 'failed'" class="face-status failed">
              <i class="fas fa-times-circle"></i>
              <span class="status-text">人脸识别失败</span>
              <span class="status-detail">{{ faceDetectError || '请重新进行人脸识别' }}</span>
            </div>
            
            <!-- 人脸照片预览 -->
            <div v-if="form.faceImage" class="face-preview">
              <img :src="form.faceImage" alt="人脸照片预览" />
              <button type="button" class="btn-remove-face" @click="removeFaceImage">
                <i class="fas fa-times"></i>
              </button>
            </div>
            
            <!-- 拍照/选择照片按钮 -->
            <div v-if="!form.faceImage" class="face-upload-area">
              <button type="button" class="btn-face-capture" @click="handleFaceCapture" :disabled="faceDetecting">
                <i class="fas fa-camera"></i>
                <span v-if="!faceDetecting">拍照进行人脸识别</span>
                <span v-else>识别中...</span>
              </button>
              <button type="button" class="btn-face-choose" @click="handleChooseFaceImage" :disabled="faceDetecting">
                <i class="fas fa-image"></i>
                <span>从相册选择</span>
              </button>
            </div>
            
            <!-- 进行人脸识别按钮（已选择照片但未识别时显示） -->
            <div v-if="form.faceImage && faceDetectStatus !== 'success'" class="face-detect-actions">
              <button type="button" class="btn-face-detect" @click="performFaceDetect" :disabled="faceDetecting">
                <i class="fas fa-search"></i>
                <span v-if="!faceDetecting">开始人脸识别和活体检测</span>
                <span v-else>识别中，请稍候...</span>
              </button>
            </div>
          </div>

          <!-- 证明材料上传（可选）：学生证 / 学信网截图等，只在前端预览，方便人工核对 -->
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-image"></i>
              证明材料（学生证 / 学信网截图）
              <span class="optional">(选填)</span>
            </label>
            <div class="upload-area">
              <!-- 已选择图片时显示预览 -->
              <div v-if="form.proofImageUrl" class="upload-preview">
                <img :src="form.proofImageUrl" alt="证明材料预览" />
                <button
                  type="button"
                  class="btn-remove"
                  @click="form.proofImageUrl = ''"
                >
                  <i class="fas fa-times"></i>
                </button>
              </div>
              <!-- 未选择图片时显示占位提示 -->
              <div
                v-else
                class="upload-placeholder"
                @click="triggerFileSelect"
              >
                <i class="fas fa-cloud-upload-alt"></i>
                <span>点击上传学生证或学信网截图（仅用于人工核对）</span>
              </div>
              <!-- 隐藏的文件选择控件 -->
              <input
                ref="fileInputRef"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleFileChange"
              />
            </div>
          </div>

          <!-- 文字说明：补充说明学籍情况，方便学校老师审核 -->
          <div class="form-group">
            <label class="form-label">
              <i class="fas fa-align-left"></i>
              证明说明
              <span class="optional">(选填)</span>
            </label>
            <textarea
              v-model="form.proofText"
              class="form-textarea"
              rows="4"
              placeholder="例如：2023级计算机学院本科生，目前大二；如学校信息库暂未更新，可根据学生证和学信网截图进行人工核验。"
            ></textarea>
          </div>

          <!-- 提交按钮区域 -->
          <div class="form-actions">
            <button
              type="submit"
              class="btn-submit"
              :disabled="loading"
            >
              <span v-if="!loading">
                <i class="fas fa-paper-plane"></i>
                提交认证申请
              </span>
              <span v-else>
                <i class="fas fa-spinner fa-spin"></i>
                正在提交，请稍候...
              </span>
            </button>
          </div>
        </form>
      </section>

      <!-- 温馨提示区域：说明审核流程与安全承诺 -->
      <section class="tips-card">
        <h2 class="tips-title">
          <i class="fas fa-shield-alt"></i>
          审核与隐私说明
        </h2>
        <ul class="tips-list">
          <li>您填写的学籍信息仅用于本平台与学校核验学生身份，不会对外公开。</li>
          <li>如学校已导入学籍库，系统会自动比对学号、姓名和身份证号完成认证。</li>
          <li>如学校暂未导入学籍库，可通过上传学生证、学信网截图和说明，交由辅导员或管理员人工审核。</li>
          <li>审核一般在 1～3 个工作日内完成，结果会在“个人中心 - 我的申请”中同步展示。</li>
        </ul>
      </section>
    </div>

    <!-- 统一页脚 -->
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
// 引入 Vue 组合式 API
import { ref } from 'vue' // ref：用于创建响应式数据
// 引入公共组件：顶部登录导航栏和页脚
import LoginNavBar from '@/components/common/LoginNavBar.vue' // LoginNavBar：顶部导航栏
import AppFooter from '@/components/common/AppFooter.vue' // AppFooter：统一页脚
// 引入公共 SDK 中封装好的学生认证接口与校园配置
import {
  applyStudentAuth, // applyStudentAuth：调用后端 /api/auth/student/apply 完成学生身份认证申请
  studentFaceDetect, // studentFaceDetect：调用后端学生身份认证人脸识别接口
  DEFAULT_CAMPUS_LIST, // DEFAULT_CAMPUS_LIST：前端兜底的校园列表配置
  type AuthStudentAuthApplyParams, // AuthStudentAuthApplyParams：学生认证申请参数类型（从 common/types 导出）
  type StudentFaceDetectResponse // StudentFaceDetectResponse：学生身份认证人脸识别响应类型
} from '@campus/common'

// 可选学校列表（当前版本从前端配置中读取，后续可改为后端动态接口）
const campusList = DEFAULT_CAMPUS_LIST // campusList：所有可选校园

// 当前选中的学校 ID，默认选中列表中的第一所学校
const selectedCampusId = ref<number>(campusList[0]?.id || 1) // selectedCampusId：用户选择的学校 ID

// 表单数据：在类型基础上额外扩展前端使用的证明说明与本地预览图片
const form = ref<
  AuthStudentAuthApplyParams & {
    proofText: string // proofText：用户填写的文字说明，仅前端使用
    proofImageUrl: string // proofImageUrl：本地预览用的图片地址（不直接上传到学生认证接口）
    faceImage: string // faceImage：人脸照片Base64字符串，用于动态人脸身份认证
  }
>({
  studentId: '', // studentId：学号
  verificationCode: '', // verificationCode：教务系统 / 学信网验证码
  name: '', // name：姓名
  idCard: '', // idCard：身份证号
  schoolId: selectedCampusId.value, // schoolId：学校 ID，与当前选择保持一致
  proofText: '', // proofText：证明说明
  proofImageUrl: '', // proofImageUrl：证明材料本地预览地址
  faceImage: '' // faceImage：人脸照片Base64字符串
})

// 提交加载状态与错误提示
const loading = ref(false) // loading：是否正在提交
const errorMessage = ref('') // errorMessage：提交过程中的错误提示文案

// 文件选择 input 的引用，用于在自定义按钮中手动触发点击
const fileInputRef = ref<HTMLInputElement | null>(null) // fileInputRef：隐藏文件输入控件的引用

// 人脸识别相关状态
const faceDetecting = ref(false) // faceDetecting：是否正在进行人脸识别
const faceDetectStatus = ref<'idle' | 'success' | 'failed'>('idle') // faceDetectStatus：人脸识别状态
const faceDetectResult = ref<StudentFaceDetectResponse | null>(null) // faceDetectResult：人脸识别结果
const faceDetectError = ref('') // faceDetectError：人脸识别错误信息

// 触发文件选择：点击自定义上传区域时调用
const triggerFileSelect = () => {
  if (fileInputRef.value) {
    fileInputRef.value.click() // 主动触发 input[type=file] 的点击事件
  }
}

// 处理图片选择：仅在前端生成预览，不直接上传到学生认证接口
const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement // 获取事件目标并断言为 HTMLInputElement
  const file = target.files?.[0] // 读取用户选择的第一张图片
  if (!file) return // 未选择文件时直接返回

  if (!file.type.startsWith('image/')) {
    // 如果选择的不是图片类型，给出友好提示
    alert('请选择图片格式的证明材料，例如学生证或学信网截图')
    return
  }

  const reader = new FileReader() // 使用 FileReader 将图片转换为 base64 方便预览
  reader.onload = e => {
    const result = e.target?.result as string // 读取到的 base64 字符串
    form.value.proofImageUrl = result // 将 base64 字符串保存到表单中用于本地预览
  }
  reader.readAsDataURL(file) // 以 dataURL 方式读取文件内容
}

// 处理人脸拍照：使用浏览器相机API
const handleFaceCapture = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.capture = 'user' // 使用前置摄像头
  input.onchange = (e: Event) => {
    const target = e.target as HTMLInputElement
    const file = target.files?.[0]
    if (file) {
      convertImageToBase64(file)
    }
  }
  input.click()
}

// 处理从相册选择人脸照片
const handleChooseFaceImage = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e: Event) => {
    const target = e.target as HTMLInputElement
    const file = target.files?.[0]
    if (file) {
      convertImageToBase64(file)
    }
  }
  input.click()
}

// 将图片文件转换为Base64格式
const convertImageToBase64 = (file: File) => {
  const reader = new FileReader()
  reader.onload = e => {
    const result = e.target?.result as string
    form.value.faceImage = result // 保存Base64字符串
    // 重置识别状态
    faceDetectStatus.value = 'idle'
    faceDetectResult.value = null
    faceDetectError.value = ''
  }
  reader.readAsDataURL(file)
}

// 移除人脸照片
const removeFaceImage = () => {
  form.value.faceImage = ''
  faceDetectStatus.value = 'idle'
  faceDetectResult.value = null
  faceDetectError.value = ''
}

// 执行人脸识别和活体检测
const performFaceDetect = async () => {
  if (!form.value.faceImage) {
    alert('请先拍照或选择照片')
    return
  }

  // 基础信息校验
  if (!form.value.studentId.trim() || !form.value.name.trim() || !form.value.idCard.trim()) {
    alert('请先填写学号、姓名和身份证号')
    return
  }

  faceDetecting.value = true // 设置识别中状态
  faceDetectStatus.value = 'idle' // 重置状态
  faceDetectError.value = '' // 清空错误信息

  try {
    // 调用人脸识别API
    const result = await studentFaceDetect({
      faceImage: form.value.faceImage, // 人脸照片Base64
      studentId: form.value.studentId.trim(), // 学号
      name: form.value.name.trim(), // 姓名
      idCard: form.value.idCard.trim(), // 身份证号
      schoolId: form.value.schoolId // 学校ID
    })

    // 保存识别结果
    faceDetectResult.value = result

    // 判断是否通过识别
    if (result.isAlive && result.faceNum === 1) {
      // 活体检测通过且只有一张人脸
      faceDetectStatus.value = 'success'
      alert('人脸识别通过')
    } else {
      // 识别失败
      faceDetectStatus.value = 'failed'
      if (result.faceNum === 0) {
        faceDetectError.value = '未检测到人脸，请确保照片清晰且人脸完整'
      } else if (result.faceNum > 1) {
        faceDetectError.value = '检测到多张人脸，请确保照片中只有您本人'
      } else if (!result.isAlive) {
        faceDetectError.value = '活体检测未通过，请使用真人照片'
      } else {
        faceDetectError.value = result.message || '人脸识别失败，请重试'
      }
      alert('人脸识别失败')
    }
  } catch (error: any) {
    console.error('人脸识别失败:', error)
    faceDetectStatus.value = 'failed'
    faceDetectError.value = error?.message || '人脸识别服务异常，请稍后重试'
    alert('识别失败，请重试')
  } finally {
    faceDetecting.value = false // 恢复按钮状态
  }
}

// 简单的前端表单校验：保证必填项不为空、身份证格式基本正确、人脸识别已通过
const validateForm = (): boolean => {
  if (!form.value.studentId.trim()) {
    alert('请输入学号')
    return false
  }
  if (!form.value.name.trim()) {
    alert('请输入姓名')
    return false
  }
  if (!form.value.idCard.trim()) {
    alert('请输入身份证号')
    return false
  }
  if (form.value.idCard.length !== 18) {
    alert('身份证号需为18位，请确认后重新输入')
    return false
  }
  if (!form.value.verificationCode.trim()) {
    alert('请输入教务系统 / 学信网验证码，没有可以先填写"暂未获取"')
    return false
  }
  // 校验人脸识别是否通过
  if (!form.value.faceImage) {
    alert('请进行人脸识别认证')
    return false
  }
  if (faceDetectStatus.value !== 'success') {
    alert('请先完成人脸识别和活体检测')
    return false
  }
  return true // 所有必填项校验通过
}

// 表单提交处理：调用公共 SDK 中的 applyStudentAuth 接口
const handleSubmit = async () => {
  if (loading.value) return // 如果已在提交中，避免重复点击

  // 同步学校 ID 到表单中，确保与选择一致
  form.value.schoolId = selectedCampusId.value // 将选中的学校 ID 写入表单

  if (!validateForm()) {
    // 本地校验未通过时直接返回
    return
  }

  loading.value = true // 开始提交，设置加载状态
  errorMessage.value = '' // 清空历史错误信息

  try {
    // 组装仅后端需要的参数对象（包含人脸照片）
    const params: AuthStudentAuthApplyParams = {
      studentId: form.value.studentId.trim(), // 学号去除首尾空格
      verificationCode: form.value.verificationCode.trim(), // 验证码去除首尾空格
      name: form.value.name.trim(), // 姓名去除首尾空格
      idCard: form.value.idCard.trim(), // 身份证号去除首尾空格
      schoolId: form.value.schoolId, // 学校 ID
      faceImage: form.value.faceImage // 人脸照片Base64字符串（用于动态人脸身份认证）
    }

    // 调用公共 SDK 中的学生身份认证申请接口，实际会请求后端 /api/auth/student/apply
    const result = await applyStudentAuth(params) // 等待后端返回申请结果

    // 使用浏览器原生弹窗给出简单反馈（项目中也可以换成全局 Message 组件）
    alert(result.message || '认证申请已提交，请等待审核')
  } catch (error: any) {
    console.error('学生身份认证申请失败：', error) // 控制台输出详细错误信息，便于排查问题
    errorMessage.value = error?.message || '认证申请提交失败，请稍后重试' // 显示友好错误提示
  } finally {
    loading.value = false // 无论成功失败，都恢复按钮为可点击状态
  }
}
</script>

<style scoped>
/* 页面整体背景样式 - 保持不变 */
.student-auth-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
  padding-bottom: 40px;
}

/* 主体内容容器：限制最大宽度并水平居中 */
.page-container {
  max-width: 900px; /* 最大宽度 900 像素，兼顾大屏阅读体验 */
  margin: 0 auto; /* 左右居中 */
  padding: 40px 20px 20px; /* 上下左右内边距 */
}

/* 顶部标题区域样式 */
.page-header {
  text-align: center; /* 标题居中显示 */
  margin-bottom: 30px; /* 与下方表单留出间距 */
}

.page-title {
  font-size: 30px; /* 标题字号 */
  font-weight: 700; /* 加粗 */
  color: #333; /* 深灰色文字 */
  margin-bottom: 8px; /* 标题与副标题之间的间距 */
  display: flex; /* 使用 flex 居中图标与文字 */
  align-items: center;
  justify-content: center;
  gap: 10px; /* 图标与文字之间的间距 */
}

.page-title i {
  color: #d81b60; /* 使用品牌主色 */
}

.page-subtitle {
  font-size: 14px; /* 副标题字号 */
  color: #666; /* 中灰色文字 */
}

/* 表单卡片整体样式 - 马卡龙配色 */
.form-card {
  background: #FFFFFF;
  border-radius: 20px;
  padding: 32px 32px 36px;
  box-shadow: 0 4px 20px rgba(225, 190, 231, 0.15);
  border: 2px solid #F3E5F5;
  margin-bottom: 24px;
}

/* 错误提示区域样式 - 马卡龙配色 */
.error-alert {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  margin-bottom: 24px;
  border-radius: 12px;
  background: #FFF5F5;
  border: 2px solid #FFB3BA;
  border-left: 4px solid #EF5350;
  color: #C62828;
  font-size: 14px;
}

.error-alert i {
  color: #EF5350;
  font-size: 20px;
}

/* 表单整体样式 */
.auth-form {
  margin-top: 4px; /* 与错误提示之间的微小间距 */
}

/* 单个表单项容器样式 */
.form-group {
  margin-bottom: 18px; /* 表单项之间的垂直间距 */
}

/* 表单标签样式 - 马卡龙配色 */
.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #5a4f7a;
  margin-bottom: 10px;
}

.form-label i {
  color: #CE93D8;
  font-size: 16px;
}

/* 必填标记样式 */
.required {
  color: #EF5350;
  margin-left: 4px;
}

/* 选填标记样式 */
.optional {
  font-size: 13px;
  color: #8b7fa8;
  margin-left: 6px;
}

/* 文本输入框与下拉框统一样式 - 马卡龙配色 */
.form-input {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  border-radius: 12px;
  background: #F5F0FF;
  border: 2px solid #E8D5F2;
  color: #5a4f7a;
  box-sizing: border-box;
  transition: all 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #CE93D8;
  background: #FFFFFF;
  box-shadow: 0 0 0 4px rgba(206, 147, 216, 0.15);
}

/* 文本域样式 - 马卡龙配色 */
.form-textarea {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  border-radius: 12px;
  background: #F5F0FF;
  border: 2px solid #E8D5F2;
  color: #5a4f7a;
  resize: vertical;
  box-sizing: border-box;
  line-height: 1.6;
  transition: all 0.3s ease;
}

.form-textarea:focus {
  outline: none;
  border-color: #CE93D8;
  background: #FFFFFF;
  box-shadow: 0 0 0 4px rgba(206, 147, 216, 0.15);
}

/* 表单提示文字样式 */
.form-hint {
  margin-top: 4px; /* 与输入框之间的间距 */
  font-size: 12px; /* 较小字号 */
  color: #999; /* 浅灰色 */
}

/* 上传区域整体容器样式 */
.upload-area {
  margin-top: 6px; /* 与标签之间的间距 */
}

/* 上传占位区域样式 - 马卡龙配色 */
.upload-placeholder {
  width: 100%;
  min-height: 160px;
  border-radius: 16px;
  border: 2px dashed #E8D5F2;
  background: #F5F0FF;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-placeholder:hover {
  border-color: #CE93D8;
  background: #FFFFFF;
  box-shadow: 0 2px 8px rgba(206, 147, 216, 0.2);
}

.upload-placeholder i {
  font-size: 40px;
  color: #CE93D8;
}

.upload-placeholder span {
  font-size: 13px; /* 文案字号 */
  color: #666; /* 中灰色 */
}

/* 已选择图片时的预览容器样式 */
.upload-preview {
  position: relative; /* 相对定位，方便放置删除按钮 */
  width: 100%; /* 占满一行 */
  max-height: 220px; /* 限制预览高度 */
  border-radius: 10px; /* 圆角 */
  overflow: hidden; /* 超出部分裁剪 */
  border: 1px solid #f0f0f0; /* 浅色边框 */
}

.upload-preview img {
  width: 100%; /* 图片宽度占满容器 */
  display: block; /* 避免底部出现空白 */
}

/* 删除图片按钮样式 */
.btn-remove {
  position: absolute; /* 绝对定位在右上角 */
  top: 10px;
  right: 10px;
  width: 28px;
  height: 28px;
  border-radius: 50%; /* 圆形按钮 */
  border: none; /* 去掉默认边框 */
  background: rgba(0, 0, 0, 0.55); /* 半透明黑色背景 */
  color: #fff; /* 白色图标 */
  display: flex; /* 居中图标 */
  align-items: center;
  justify-content: center;
  cursor: pointer; /* 鼠标样式为可点击 */
  transition: all 0.2s ease; /* 悬停动画 */
}

.btn-remove:hover {
  background: #ff4d4f; /* 悬停时使用红色强调 */
}

/* 底部提交按钮区域样式 */
.form-actions {
  margin-top: 22px; /* 与上方表单内容留出间距 */
}

.btn-submit {
  width: 100%;
  padding: 16px 24px;
  border-radius: 24px;
  border: none;
  background: linear-gradient(135deg, #E8D5F2 0%, #CE93D8 100%);
  color: #FFFFFF;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(206, 147, 216, 0.4);
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(206, 147, 216, 0.5);
}

.btn-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  background: #E8D5F2;
}

/* 下方提示卡片样式 - 马卡龙配色 */
.tips-card {
  margin-top: 16px;
  padding: 24px 24px 28px;
  background: #FFFFFF;
  border-radius: 20px;
  border: 2px dashed #E8D5F2;
  box-shadow: 0 4px 20px rgba(225, 190, 231, 0.1);
}

.tips-title {
  font-size: 16px; /* 标题字号 */
  font-weight: 600; /* 标题加粗 */
  color: #333; /* 深灰色文字 */
  margin-bottom: 8px; /* 与列表之间的间距 */
  display: flex; /* 横向排列图标与文字 */
  align-items: center;
  gap: 8px; /* 图标与文字的距离 */
}

.tips-title i {
  color: #CE93D8;
}

.tips-list {
  list-style: none; /* 去掉默认列表样式 */
  padding: 0;
  margin: 0;
}

.tips-list li {
  position: relative; /* 相对定位，用于自定义前缀圆点 */
  padding-left: 16px; /* 给自定义圆点预留空间 */
  font-size: 13px; /* 文本字号 */
  color: #666; /* 中灰色文字 */
  line-height: 1.6; /* 行高提升可读性 */
  margin-bottom: 4px; /* 列表项之间的间距 */
}

.tips-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  top: 0;
  color: #CE93D8;
  font-size: 16px;
}

/* 响应式适配：小屏幕下适当调整内边距和字号 */
@media (max-width: 768px) {
  .page-container {
    padding: 24px 14px 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .form-card {
    padding: 18px 16px 22px;
  }

  .face-upload-area {
    flex-direction: column;
  }
}

/* 人脸识别状态显示区域 */
.face-status {
  margin-top: 12px;
  padding: 16px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.face-status.success {
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
}

.face-status.failed {
  background-color: #fff1f0;
  border: 1px solid #ffccc7;
}

.face-status i {
  font-size: 32px;
}

.face-status.success i {
  color: #52c41a;
}

.face-status.failed i {
  color: #ff4d4f;
}

.status-text {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.status-detail {
  font-size: 13px;
  color: #666;
}

/* 人脸照片预览区域 */
.face-preview {
  margin-top: 12px;
  width: 100%;
  max-height: 300px;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  border: 2px solid #d9d9d9;
}

.face-preview img {
  width: 100%;
  display: block;
}

.btn-remove-face {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-remove-face:hover {
  background: #ff4d4f;
}

/* 人脸上传区域 */
.face-upload-area {
  margin-top: 12px;
  display: flex;
  gap: 12px;
}

.btn-face-capture,
.btn-face-choose {
  flex: 1;
  padding: 14px 20px;
  border-radius: 12px;
  border: 2px solid #CE93D8;
  background-color: #FFFFFF;
  color: #9C27B0;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.btn-face-capture:hover,
.btn-face-choose:hover {
  background-color: #F5F0FF;
  border-color: #9C27B0;
}

.btn-face-capture:disabled,
.btn-face-choose:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 人脸识别操作按钮区域 */
.face-detect-actions {
  margin-top: 12px;
}

.btn-face-detect {
  width: 100%;
  padding: 14px 20px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #E8D5F2 0%, #CE93D8 100%);
  color: #FFFFFF;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  box-shadow: 0 4px 12px rgba(206, 147, 216, 0.3);
}

.btn-face-detect:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(206, 147, 216, 0.4);
}

.btn-face-detect:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  background-color: #ccc;
}
</style>

