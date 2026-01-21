<template>
  <div class="apply-page">
    <NavBar />

    <main class="apply-main">
      <section class="hero">
        <div class="hero-inner">
          <p class="breadcrumb">首页 / 高校接入</p>
          <h1 class="title">高校接入申请</h1>
          <p class="subtitle">
            提交申请后，我们会在工作日 1-3 个工作日内与您联系，协助完成对接评估与接入流程。
          </p>
        </div>
      </section>

      <section class="shell">
        <aside class="sidebar">
          <div class="side-title">申请指引</div>
          <ol class="steps">
            <li class="step">
              <div class="step-num">1</div>
              <div class="step-body">
                <div class="step-title">填写学校信息</div>
                <div class="step-desc">学校名称、地区、统一社会信用代码等。</div>
              </div>
            </li>
            <li class="step">
              <div class="step-num">2</div>
              <div class="step-body">
                <div class="step-title">选择接入需求</div>
                <div class="step-desc">校园卡、行程、二手、兼职等模块。</div>
              </div>
            </li>
            <li class="step">
              <div class="step-num">3</div>
              <div class="step-body">
                <div class="step-title">提交并等待联系</div>
                <div class="step-desc">我们会进行材料审核并安排对接。</div>
              </div>
            </li>
          </ol>

          <div class="tip-card">
            <div class="tip-title">建议准备</div>
            <ul class="tip-list">
              <li>学校官网或公开介绍链接</li>
              <li>对接联系人与岗位信息</li>
              <li>预期接入模块与时间计划</li>
            </ul>
          </div>
        </aside>

        <div class="content">
          <div v-if="submitted" class="success">
            <div class="success-icon"><i class="fas fa-check"></i></div>
            <h2 class="success-title">已提交申请</h2>
            <p class="success-desc">
              我们已收到您的高校接入申请。申请编号：
              <span class="success-code">{{ submitCode }}</span>
            </p>
            <div class="success-actions">
              <button class="btn" type="button" @click="reset">继续提交新申请</button>
              <a class="btn secondary" href="/help">前往帮助中心</a>
            </div>
          </div>

          <form v-else class="card" @submit.prevent="submit">
            <div class="card-head">
              <h2>申请信息</h2>
              <p>带 <span class="req">*</span> 的为必填项</p>
            </div>

            <div class="grid">
              <div class="field">
                <label>学校名称 <span class="req">*</span></label>
                <input v-model="form.universityName" type="text" placeholder="例如：苏州大学" />
              </div>

              <div class="field">
                <label>学校所在地区 <span class="req">*</span></label>
                <input v-model="form.region" type="text" placeholder="例如：江苏省 苏州市" />
              </div>

              <div class="field">
                <label>统一社会信用代码（选填）</label>
                <input v-model="form.creditCode" type="text" placeholder="18 位统一社会信用代码" />
              </div>

              <div class="field">
                <label>学校官网（选填）</label>
                <input v-model="form.website" type="url" placeholder="https://..." />
              </div>
            </div>

            <div class="divider"></div>

            <div class="section-head">
              <h3>接入需求</h3>
              <p>勾选希望优先接入的模块（可多选）。</p>
            </div>

            <div class="chips">
              <label v-for="m in modules" :key="m.value" class="chip">
                <input v-model="form.modules" type="checkbox" :value="m.value" />
                <span>{{ m.label }}</span>
              </label>
            </div>

            <div class="grid">
              <div class="field">
                <label>预期接入时间 <span class="req">*</span></label>
                <select v-model="form.plan" class="select">
                  <option value="">请选择</option>
                  <option value="1m">1 个月内</option>
                  <option value="3m">3 个月内</option>
                  <option value="6m">6 个月内</option>
                  <option value="other">其他</option>
                </select>
              </div>
              <div class="field">
                <label>预估服务人数（选填）</label>
                <input v-model="form.userScale" type="number" min="0" placeholder="例如：30000" />
              </div>
            </div>

            <div class="field">
              <label>补充说明（选填）</label>
              <textarea v-model="form.notes" rows="5" placeholder="例如：希望支持教务导入、企业微信登录、校园卡对接方式等"></textarea>
            </div>

            <div class="divider"></div>

            <div class="section-head">
              <h3>对接联系人</h3>
              <p>请填写能够代表学校沟通对接的联系人信息。</p>
            </div>

            <div class="grid">
              <div class="field">
                <label>联系人姓名 <span class="req">*</span></label>
                <input v-model="form.contactName" type="text" placeholder="姓名" />
              </div>
              <div class="field">
                <label>联系人岗位（选填）</label>
                <input v-model="form.contactTitle" type="text" placeholder="例如：信息中心老师/教务处老师" />
              </div>
              <div class="field">
                <label>手机号 <span class="req">*</span></label>
                <input v-model="form.contactPhone" type="tel" placeholder="用于快速沟通" />
              </div>
              <div class="field">
                <label>邮箱 <span class="req">*</span></label>
                <input v-model="form.contactEmail" type="email" placeholder="用于发送对接资料" />
              </div>
            </div>

            <div class="field">
              <label>材料附件（选填）</label>
              <div class="upload">
                <input ref="fileInput" class="file" type="file" @change="onFileChange" />
                <button class="btn secondary" type="button" @click="pickFile">选择文件</button>
                <span v-if="fileName" class="file-name">{{ fileName }}</span>
                <button v-if="fileName" class="btn ghost" type="button" @click="removeFile">移除</button>
              </div>
              <div class="hint">支持 PDF/图片等文件，用于快速评估接入需求（前端演示：仅本地保存，不会上传）。</div>
            </div>

            <label class="agree">
              <input v-model="form.agree" type="checkbox" />
              <span>
                我确认以上信息真实有效，并同意平台联系我进行高校接入沟通。
                <span class="req">*</span>
              </span>
            </label>

            <div class="actions">
              <button class="btn" type="submit">提交申请</button>
              <button class="btn secondary" type="button" @click="reset">重置</button>
            </div>

            <p v-if="error" class="error">{{ error }}</p>
          </form>
        </div>
      </section>
    </main>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'

type ModuleValue = 'ecard' | 'schedule' | 'secondhand' | 'parttime' | 'auth' | 'message'

const modules: Array<{ value: ModuleValue; label: string }> = [
  { value: 'ecard', label: '校园E卡通' },
  { value: 'schedule', label: '行程/课程表' },
  { value: 'secondhand', label: '二手交易' },
  { value: 'parttime', label: '兼职服务' },
  { value: 'auth', label: '统一身份认证' },
  { value: 'message', label: '消息/通知' }
]

const form = ref({
  universityName: '',
  region: '',
  creditCode: '',
  website: '',
  modules: [] as ModuleValue[],
  plan: '',
  userScale: '' as string | number,
  notes: '',
  contactName: '',
  contactTitle: '',
  contactPhone: '',
  contactEmail: '',
  agree: false
})

const error = ref<string | null>(null)
const submitted = ref(false)
const submitCode = ref('')

const fileInput = ref<HTMLInputElement | null>(null)
const fileName = ref('')

const canSubmit = computed(() => {
  return (
    Boolean(form.value.universityName.trim()) &&
    Boolean(form.value.region.trim()) &&
    Boolean(form.value.plan) &&
    Boolean(form.value.contactName.trim()) &&
    Boolean(form.value.contactPhone.trim()) &&
    Boolean(form.value.contactEmail.trim()) &&
    form.value.agree
  )
})

const pickFile = () => {
  fileInput.value?.click()
}

const onFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  const f = target.files?.[0]
  fileName.value = f?.name ?? ''
}

const removeFile = () => {
  fileName.value = ''
  if (fileInput.value) fileInput.value.value = ''
}

const reset = () => {
  form.value = {
    universityName: '',
    region: '',
    creditCode: '',
    website: '',
    modules: [],
    plan: '',
    userScale: '',
    notes: '',
    contactName: '',
    contactTitle: '',
    contactPhone: '',
    contactEmail: '',
    agree: false
  }
  removeFile()
  error.value = null
  submitted.value = false
  submitCode.value = ''
}

const submit = () => {
  error.value = null
  if (!canSubmit.value) {
    error.value = '请完善必填信息并勾选确认。'
    return
  }

  // 前端演示：生成申请编号
  const t = Date.now().toString().slice(-6)
  submitCode.value = `UNI-${t}`
  submitted.value = true
  console.log('高校接入申请（演示）:', { ...form.value, fileName: fileName.value })
}
</script>

<style scoped>
.apply-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #fdfbff 0%, #f7f0ff 25%, #fdf3f7 60%, #ffffff 100%);
  color: #111827;
}

.apply-main {
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
  padding: 20px 22px;
  background: linear-gradient(135deg, rgba(255, 64, 129, 0.06), rgba(63, 81, 181, 0.07));
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
  border: 1px solid rgba(148, 163, 184, 0.35);
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

.shell {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 18px;
  align-items: start;
}

.sidebar {
  position: sticky;
  top: 84px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 14px 14px;
}

.side-title {
  font-size: 12px;
  color: #6b7280;
  font-weight: 800;
  letter-spacing: 0.3px;
  margin-bottom: 10px;
}

.steps {
  margin: 0 0 14px;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.step {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.step-num {
  width: 26px;
  height: 26px;
  border-radius: 999px;
  background: linear-gradient(135deg, #ff4b8b, #ff7ab2);
  color: #fff;
  font-weight: 900;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.step-title {
  font-size: 14px;
  font-weight: 850;
  color: #111827;
  margin-bottom: 2px;
}

.step-desc {
  font-size: 12px;
  color: #6b7280;
  line-height: 1.6;
}

.tip-card {
  border-radius: 14px;
  border: 1px solid rgba(255, 75, 139, 0.22);
  background: rgba(255, 75, 139, 0.06);
  padding: 12px;
}

.tip-title {
  font-size: 13px;
  font-weight: 850;
  color: #111827;
  margin-bottom: 6px;
}

.tip-list {
  margin: 0 0 0 18px;
  padding: 0;
  font-size: 12px;
  color: #4b5563;
  line-height: 1.8;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.card {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 16px;
}

.card-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: baseline;
  margin-bottom: 12px;
}

.card-head h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 900;
  color: #111827;
}

.card-head p {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
}

.req {
  color: #ff4b8b;
  font-weight: 900;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
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

input,
textarea,
.select {
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.55);
  background: rgba(255, 255, 255, 0.9);
  padding: 10px 12px;
  font-size: 13px;
  outline: none;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

input:focus,
textarea:focus,
.select:focus {
  border-color: rgba(255, 75, 139, 0.65);
  box-shadow: 0 0 0 3px rgba(255, 75, 139, 0.12);
}

textarea {
  resize: vertical;
}

.divider {
  height: 1px;
  background: rgba(148, 163, 184, 0.22);
  margin: 14px 0;
}

.section-head h3 {
  margin: 0 0 4px;
  font-size: 14px;
  font-weight: 900;
  color: #111827;
}

.section-head p {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 10px 0 12px;
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 999px;
  border: 1px solid rgba(209, 213, 219, 0.8);
  background: rgba(255, 255, 255, 0.85);
  font-size: 12px;
  color: #374151;
  cursor: pointer;
}

.chip input {
  width: 14px;
  height: 14px;
}

.upload {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.file {
  display: none;
}

.file-name {
  font-size: 12px;
  color: #6b7280;
}

.hint {
  margin-top: 6px;
  font-size: 12px;
  color: #6b7280;
}

.agree {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  margin-top: 12px;
  font-size: 12px;
  color: #4b5563;
}

.actions {
  display: flex;
  gap: 10px;
  margin-top: 14px;
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

.btn.ghost {
  background: transparent;
  color: #6b7280;
  border: 1px solid rgba(148, 163, 184, 0.35);
}

.error {
  margin: 10px 0 0;
  font-size: 12px;
  color: #dc2626;
}

.success {
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(148, 163, 184, 0.35);
  box-shadow: 0 10px 26px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(10px);
  padding: 20px 16px;
  text-align: center;
}

.success-icon {
  width: 56px;
  height: 56px;
  border-radius: 999px;
  background: rgba(34, 197, 94, 0.12);
  color: #16a34a;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-bottom: 10px;
  border: 1px solid rgba(34, 197, 94, 0.25);
}

.success-title {
  margin: 0 0 6px;
  font-size: 18px;
  font-weight: 900;
  color: #111827;
}

.success-desc {
  margin: 0 0 12px;
  font-size: 13px;
  color: #6b7280;
}

.success-code {
  color: #ff4b8b;
  font-weight: 900;
}

.success-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

@media (max-width: 900px) {
  .shell {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
  }
}

@media (max-width: 768px) {
  .apply-main {
    padding: 18px 0 46px;
  }

  .hero,
  .shell {
    padding: 0 16px;
  }

  .title {
    font-size: 24px;
  }

  .grid {
    grid-template-columns: 1fr;
  }
}
</style>

