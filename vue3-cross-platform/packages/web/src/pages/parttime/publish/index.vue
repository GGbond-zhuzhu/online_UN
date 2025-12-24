<template>
  <div class="publish-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-plus-circle"></i> 发布兼职
        </h1>
        <p class="page-subtitle">发布兼职岗位，寻找合适的校园人才</p>
      </section>

      <!-- 表单区域 -->
      <form class="publish-form" @submit.prevent="handleSubmit">
        <!-- 基本信息 -->
        <section class="form-section">
          <div class="section-header">
            <h2 class="section-title">基本信息</h2>
            <span class="required">*</span>
          </div>
          <div class="form-group">
            <label class="form-label">
              岗位标题 <span class="required">*</span>
            </label>
            <input
              v-model="form.title"
              type="text"
              class="form-input"
              placeholder="例如：初中数学家教"
              maxlength="50"
              required
            />
            <div class="form-hint">
              <span>{{ form.title.length }}/50</span>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">
                岗位类型 <span class="required">*</span>
              </label>
              <select v-model="form.type" class="form-select" required>
                <option value="">请选择类型</option>
                <option value="实习">实习</option>
                <option value="兼职">兼职</option>
                <option value="家教">家教</option>
                <option value="促销">促销</option>
                <option value="调研">调研</option>
                <option value="其他">其他</option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label">
                工作地区 <span class="required">*</span>
              </label>
              <select v-model="form.location" class="form-select" required>
                <option value="">请选择地区</option>
                <option value="校内">校内</option>
                <option value="学校周边">学校周边</option>
                <option value="市中心">市中心</option>
                <option value="远程">远程</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">
                工作时间 <span class="required">*</span>
              </label>
              <select v-model="form.time" class="form-select" required>
                <option value="">请选择时间</option>
                <option value="工作日">工作日</option>
                <option value="周末">周末</option>
                <option value="时间灵活">时间灵活</option>
                <option value="晚上/周末">晚上/周末</option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label">
                招聘人数 <span class="required">*</span>
              </label>
              <input
                v-model.number="form.recruitCount"
                type="number"
                class="form-input"
                placeholder="例如：3"
                min="1"
                required
              />
            </div>
          </div>
        </section>

        <!-- 薪资待遇 -->
        <section class="form-section">
          <div class="section-header">
            <h2 class="section-title">薪资待遇</h2>
            <span class="required">*</span>
          </div>
          <div class="form-group">
            <label class="form-label">薪资类型</label>
            <div class="radio-group">
              <label class="radio-label">
                <input
                  v-model="form.salaryType"
                  type="radio"
                  value="hourly"
                  class="radio-input"
                />
                <span>按小时</span>
              </label>
              <label class="radio-label">
                <input
                  v-model="form.salaryType"
                  type="radio"
                  value="daily"
                  class="radio-input"
                />
                <span>按天</span>
              </label>
              <label class="radio-label">
                <input
                  v-model="form.salaryType"
                  type="radio"
                  value="project"
                  class="radio-input"
                />
                <span>按项目</span>
              </label>
              <label class="radio-label">
                <input
                  v-model="form.salaryType"
                  type="radio"
                  value="other"
                  class="radio-input"
                />
                <span>其他</span>
              </label>
            </div>
          </div>

          <div class="form-row" v-if="form.salaryType === 'hourly' || form.salaryType === 'daily'">
            <div class="form-group">
              <label class="form-label">
                最低薪资 <span class="required">*</span>
              </label>
              <div class="price-input-group">
                <span class="price-symbol">¥</span>
                <input
                  v-model.number="form.salaryMin"
                  type="number"
                  class="form-input price-input"
                  placeholder="0"
                  min="0"
                  required
                />
                <span class="price-unit">{{ form.salaryType === 'hourly' ? '/小时' : '/天' }}</span>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">
                最高薪资 <span class="required">*</span>
              </label>
              <div class="price-input-group">
                <span class="price-symbol">¥</span>
                <input
                  v-model.number="form.salaryMax"
                  type="number"
                  class="form-input price-input"
                  placeholder="0"
                  min="0"
                  required
                />
                <span class="price-unit">{{ form.salaryType === 'hourly' ? '/小时' : '/天' }}</span>
              </div>
            </div>
          </div>

          <div class="form-group" v-if="form.salaryType === 'project' || form.salaryType === 'other'">
            <label class="form-label">
              薪资说明 <span class="required">*</span>
            </label>
            <input
              v-model="form.salaryDesc"
              type="text"
              class="form-input"
              placeholder="例如：按项目计费、底薪+提成等"
              required
            />
          </div>
        </section>

        <!-- 岗位详情 -->
        <section class="form-section">
          <div class="section-header">
            <h2 class="section-title">岗位详情</h2>
            <span class="required">*</span>
          </div>
          <div class="form-group">
            <label class="form-label">
              岗位描述 <span class="required">*</span>
            </label>
            <textarea
              v-model="form.description"
              class="form-textarea"
              placeholder="请详细描述岗位的工作内容、要求等信息..."
              rows="6"
              maxlength="500"
              required
            ></textarea>
            <div class="form-hint">
              <span>{{ form.description.length }}/500</span>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">工作内容</label>
            <div class="work-content-editor">
              <div
                v-for="(item, idx) in form.workContent"
                :key="idx"
                class="work-content-item"
              >
                <input
                  v-model="form.workContent[idx]"
                  type="text"
                  class="form-input"
                  :placeholder="`工作内容 ${idx + 1}`"
                />
                <button
                  type="button"
                  class="remove-item-btn"
                  @click="removeWorkContent(idx)"
                  v-if="form.workContent.length > 1"
                >
                  <i class="fas fa-times"></i>
                </button>
              </div>
              <button
                type="button"
                class="add-item-btn"
                @click="addWorkContent"
                v-if="form.workContent.length < 5"
              >
                <i class="fas fa-plus"></i> 添加工作内容
              </button>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">任职要求</label>
            <div class="requirement-editor">
              <div
                v-for="(item, idx) in form.requirements"
                :key="idx"
                class="requirement-item"
              >
                <input
                  v-model="form.requirements[idx]"
                  type="text"
                  class="form-input"
                  :placeholder="`要求 ${idx + 1}`"
                />
                <button
                  type="button"
                  class="remove-item-btn"
                  @click="removeRequirement(idx)"
                  v-if="form.requirements.length > 1"
                >
                  <i class="fas fa-times"></i>
                </button>
              </div>
              <button
                type="button"
                class="add-item-btn"
                @click="addRequirement"
                v-if="form.requirements.length < 5"
              >
                <i class="fas fa-plus"></i> 添加要求
              </button>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">工作地点</label>
              <input
                v-model="form.workLocation"
                type="text"
                class="form-input"
                placeholder="例如：学校周边教育大厦3楼"
              />
            </div>

            <div class="form-group">
              <label class="form-label">截止日期</label>
              <input
                v-model="form.deadline"
                type="date"
                class="form-input"
                :min="minDate"
              />
            </div>
          </div>
        </section>

        <!-- 公司信息 -->
        <section class="form-section">
          <div class="section-header">
            <h2 class="section-title">公司信息</h2>
            <span class="required">*</span>
          </div>
          <div class="form-group">
            <label class="form-label">
              公司名称 <span class="required">*</span>
            </label>
            <input
              v-model="form.company"
              type="text"
              class="form-input"
              placeholder="例如：学而思教育"
              required
            />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">公司类型</label>
              <input
                v-model="form.companyType"
                type="text"
                class="form-input"
                placeholder="例如：教育培训"
              />
            </div>

            <div class="form-group">
              <label class="form-label">公司规模</label>
              <select v-model="form.companySize" class="form-select">
                <option value="">请选择规模</option>
                <option value="1-50人">1-50人</option>
                <option value="50-100人">50-100人</option>
                <option value="100-500人">100-500人</option>
                <option value="500-1000人">500-1000人</option>
                <option value="1000-5000人">1000-5000人</option>
                <option value="5000人以上">5000人以上</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">公司简介</label>
            <textarea
              v-model="form.companyDescription"
              class="form-textarea"
              placeholder="简要介绍公司情况..."
              rows="4"
              maxlength="300"
            ></textarea>
            <div class="form-hint">
              <span>{{ form.companyDescription.length }}/300</span>
            </div>
          </div>
        </section>

        <!-- 联系方式 -->
        <section class="form-section">
          <div class="section-header">
            <h2 class="section-title">联系方式</h2>
            <span class="section-hint">用于求职者联系您</span>
          </div>
          <div class="form-group">
            <label class="form-label">
              联系人 <span class="required">*</span>
            </label>
            <input
              v-model="form.contactName"
              type="text"
              class="form-input"
              placeholder="请输入联系人姓名"
              required
            />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">
                联系电话 <span class="required">*</span>
              </label>
              <input
                v-model="form.contactPhone"
                type="tel"
                class="form-input"
                placeholder="请输入手机号"
                pattern="[0-9]{11}"
                required
              />
            </div>

            <div class="form-group">
              <label class="form-label">邮箱</label>
              <input
                v-model="form.contactEmail"
                type="email"
                class="form-input"
                placeholder="请输入邮箱（选填）"
              />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">联系地址</label>
            <input
              v-model="form.contactAddress"
              type="text"
              class="form-input"
              placeholder="请输入详细地址（选填）"
            />
          </div>
        </section>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button type="button" class="btn cancel-btn" @click="handleCancel">
            取消
          </button>
          <button type="button" class="btn draft-btn" @click="saveDraft">
            保存草稿
          </button>
          <button type="submit" class="btn submit-btn">
            <i class="fas fa-check"></i> 发布岗位
          </button>
        </div>
      </form>

      <!-- 发布提示 -->
      <section class="tips-section">
        <div class="tips-card">
          <h3 class="tips-title">
            <i class="fas fa-lightbulb"></i> 发布提示
          </h3>
          <ul class="tips-list">
            <li>如实填写岗位信息，提高招聘成功率</li>
            <li>薪资待遇要明确，避免产生误解</li>
            <li>详细描述工作内容和要求，吸引合适的人才</li>
            <li>及时回复求职者咨询，提升企业形象</li>
            <li>遵守平台规则，不得发布虚假信息</li>
          </ul>
        </div>
      </section>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
// 引入 Vue 的组合式 API，用于管理表单数据和计算属性
import { ref, reactive, computed } from 'vue' // 从 vue 导入 ref、reactive、computed
import { useRouter } from 'vue-router' // 从 vue-router 导入 useRouter，用于页面跳转
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
// 从公共包引入兼职 Store 和类型，统一管理“发布兼职”的后端交互
import { useParttimeStore, type PublishParttimeParams } from '@campus/common' // useParttimeStore：统一的兼职 Store；PublishParttimeParams：发布接口参数类型

const router = useRouter() // 获取路由实例，用于表单提交后跳转到列表页
const parttimeStore = useParttimeStore() // 获取兼职 Store 实例，用于调用发布接口和读取 loading / 错误信息

// 最小日期（今天）
const minDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

// 表单数据
const form = reactive({
  title: '',
  type: '',
  location: '',
  time: '',
  recruitCount: null as number | null,
  salaryType: 'hourly',
  salaryMin: null as number | null,
  salaryMax: null as number | null,
  salaryDesc: '',
  description: '',
  workContent: [''],
  requirements: [''],
  workLocation: '',
  deadline: '',
  company: '',
  companyType: '',
  companySize: '',
  companyDescription: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  contactAddress: ''
})

// 添加工作内容
const addWorkContent = () => {
  if (form.workContent.length < 5) {
    form.workContent.push('')
  }
}

// 移除工作内容
const removeWorkContent = (index: number) => {
  if (form.workContent.length > 1) {
    form.workContent.splice(index, 1)
  }
}

// 添加要求
const addRequirement = () => {
  if (form.requirements.length < 5) {
    form.requirements.push('')
  }
}

// 移除要求
const removeRequirement = (index: number) => {
  if (form.requirements.length > 1) {
    form.requirements.splice(index, 1)
  }
}

// 表单验证
const validateForm = () => {
  if (!form.title.trim()) {
    alert('请输入岗位标题')
    return false
  }
  if (!form.type) {
    alert('请选择岗位类型')
    return false
  }
  if (!form.location) {
    alert('请选择工作地区')
    return false
  }
  if (!form.time) {
    alert('请选择工作时间')
    return false
  }
  if (!form.recruitCount || form.recruitCount <= 0) {
    alert('请输入有效的招聘人数')
    return false
  }
  if (form.salaryType === 'hourly' || form.salaryType === 'daily') {
    if (!form.salaryMin || form.salaryMin <= 0) {
      alert('请输入有效的最低薪资')
      return false
    }
    if (!form.salaryMax || form.salaryMax <= 0) {
      alert('请输入有效的最高薪资')
      return false
    }
    if (form.salaryMax < form.salaryMin) {
      alert('最高薪资不能低于最低薪资')
      return false
    }
  } else {
    if (!form.salaryDesc.trim()) {
      alert('请输入薪资说明')
      return false
    }
  }
  if (!form.description.trim()) {
    alert('请输入岗位描述')
    return false
  }
  if (!form.company.trim()) {
    alert('请输入公司名称')
    return false
  }
  if (!form.contactName.trim()) {
    alert('请输入联系人')
    return false
  }
  if (!form.contactPhone.trim()) {
    alert('请输入联系电话')
    return false
  }
  return true
}

// 提交表单
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  try {
    // 对用户填写的工作内容和任职要求做一次“去空行”处理
    const cleanWorkContent = form.workContent
      .map((item) => item.trim()) // 去掉前后空格
      .filter((item) => item) // 过滤掉空字符串
    const cleanRequirements = form.requirements
      .map((item) => item.trim())
      .filter((item) => item)

    // 将“岗位描述 + 工作内容 + 任职要求”等信息合并为一个长描述，提交给后端
    const descriptionParts: string[] = [] // 用于拼接多段文本的数组
    descriptionParts.push(`岗位描述：\n${form.description.trim()}`) // 第一段：岗位描述
    if (cleanWorkContent.length) {
      // 如果有工作内容，则追加一段列表
      descriptionParts.push(
        '工作内容：\n' + cleanWorkContent.map((item, idx) => `${idx + 1}. ${item}`).join('\n')
      )
    }
    if (cleanRequirements.length) {
      // 如果有任职要求，则追加一段列表
      descriptionParts.push(
        '任职要求：\n' + cleanRequirements.map((item, idx) => `${idx + 1}. ${item}`).join('\n')
      )
    }
    const fullDescription = descriptionParts.join('\n\n') // 使用空行分隔各部分，便于阅读

    // 将表单中的薪资设置转换为后端需要的数值字段
    const salaryValue =
      form.salaryType === 'hourly' || form.salaryType === 'daily'
        ? Number(form.salaryMin || 0) // 按小时 / 按天时，使用“最低薪资”作为基础薪资数值
        : 0 // 按项目 / 其他暂时使用 0，占位，具体说明在描述中体现

    // 将任职要求数组合并为一段文本，便于后端展示或存储
    const requirementsText =
      cleanRequirements.length > 0 ? cleanRequirements.join('；') : undefined // 没有要求时可以不传该字段

    // 将联系方式整理为一段完整的字符串
    const contactParts: string[] = [] // 存储各条联系信息
    if (form.contactName.trim()) {
      contactParts.push(`联系人：${form.contactName.trim()}`) // 联系人姓名
    }
    if (form.contactPhone.trim()) {
      contactParts.push(`电话：${form.contactPhone.trim()}`) // 联系电话
    }
    if (form.contactEmail.trim()) {
      contactParts.push(`邮箱：${form.contactEmail.trim()}`) // 邮箱（可选）
    }
    if (form.contactAddress.trim()) {
      contactParts.push(`地址：${form.contactAddress.trim()}`) // 地址（可选）
    }
    const contactText = contactParts.join('；') || form.contactPhone // 兜底至少有一个电话

    // 按照公共包中定义的 PublishParttimeParams 结构组装提交参数
    const payload: PublishParttimeParams = {
      title: form.title.trim(), // 岗位标题
      description: fullDescription, // 合并后的岗位完整描述
      salary: salaryValue, // 薪资数值（按小时/按天使用最低薪资，其余情况暂用 0 占位）
      salaryType: form.salaryType, // 薪资类型（hourly/daily/project/other）
      location: form.location, // 工作地区
      workTime: form.time, // 工作时间说明
      requirements: requirementsText, // 任职要求汇总文本（可选）
      contact: contactText // 联系方式文本
    }

    // 调用公共包的兼职 Store 方法，向后端真正提交“发布兼职”请求
    await parttimeStore.publishJob(payload) // 如果发布成功，会在 Store 中同步更新“我发布的兼职列表”

    alert('发布成功！') // 提示用户发布成功
    router.push('/parttime') // 跳转回兼职列表页，方便查看刚发布的岗位
  } catch (error) {
    console.error('发布失败:', error) // 控制台打印错误日志
    alert(parttimeStore.errorMessage || '发布失败，请稍后重试') // 优先展示 Store 中的错误提示
  }
}

// 保存草稿
const saveDraft = () => {
  // 保存到本地存储
  localStorage.setItem('parttime_draft', JSON.stringify(form))
  alert('草稿已保存')
}

// 取消发布
const handleCancel = () => {
  if (confirm('确定要取消发布吗？未保存的内容将丢失。')) {
    router.back()
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
  --card-shadow: 0 4px 18px rgba(216, 27, 96, 0.12);
  --border: #f1d9e9;
}

.publish-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px 15px 40px;
}

/* 页面标题 */
.page-header {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title i {
  color: var(--primary);
}

.page-subtitle {
  font-size: 14px;
  color: var(--muted);
}

/* 表单区域 */
.publish-form {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 20px;
}

.form-section {
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid #f0f0f0;
}

.form-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--text);
}

.section-hint {
  font-size: 12px;
  color: var(--muted);
  margin-left: auto;
}

.required {
  color: var(--primary);
  margin-left: 4px;
}

/* 表单元素 */
.form-group {
  margin-bottom: 20px;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 8px;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  font-size: 14px;
  color: var(--text);
  transition: all 0.3s;
  background: white;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(216, 27, 96, 0.1);
}

.form-textarea {
  resize: vertical;
  font-family: inherit;
}

.form-hint {
  display: flex;
  justify-content: flex-end;
  margin-top: 4px;
  font-size: 12px;
  color: var(--muted);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* 单选按钮组 */
.radio-group {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: var(--text);
}

.radio-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--primary);
}

/* 价格输入组 */
.price-input-group {
  display: flex;
  align-items: center;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  background: white;
  transition: all 0.3s;
}

.price-input-group:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(216, 27, 96, 0.1);
}

.price-symbol {
  padding: 12px 8px 12px 15px;
  font-size: 16px;
  font-weight: bold;
  color: var(--primary);
}

.price-input {
  flex: 1;
  border: none;
  padding-left: 0;
}

.price-input:focus {
  box-shadow: none;
}

.price-unit {
  padding: 12px 15px 12px 8px;
  font-size: 14px;
  color: var(--muted);
}

/* 工作内容和要求编辑器 */
.work-content-editor,
.requirement-editor {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.work-content-item,
.requirement-item {
  display: flex;
  gap: 10px;
  align-items: center;
}

.work-content-item .form-input,
.requirement-item .form-input {
  flex: 1;
}

.remove-item-btn {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.remove-item-btn:hover {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

.add-item-btn {
  padding: 10px 15px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  background: #fafafa;
  color: var(--primary);
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.add-item-btn:hover {
  border-color: var(--primary);
  background: #fff5f9;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #f0f0f0;
}

.btn {
  padding: 12px 24px;
  border-radius: 12px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
  transform: translateY(-2px);
}

.btn:active {
  background: var(--primary-dark);
  border-color: var(--primary-dark);
  transform: translateY(0);
}

.cancel-btn {
  color: var(--muted);
}

.cancel-btn:hover {
  background: #f5f5f5;
  color: var(--muted);
  border-color: #ddd;
}

.draft-btn {
  color: var(--muted);
}

.draft-btn:hover {
  background: #f5f5f5;
  color: var(--muted);
  border-color: #ddd;
}

.submit-btn {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.submit-btn:hover {
  background: var(--primary-dark);
  border-color: var(--primary-dark);
}

/* 提示卡片 */
.tips-section {
  max-width: calc(100% - 30px);
  margin-left: auto;
  margin-right: auto;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }

  .page-title {
    font-size: 24px;
  }

  .publish-form {
    padding: 20px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }

  .radio-group {
    flex-direction: column;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .page-container {
    padding: 15px 10px 30px;
  }
}
</style>
