<template>
  <div class="publish-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-plus-circle"></i> 发布闲置
        </h1>
        <p class="page-subtitle">填写商品信息，让闲置物品找到新主人</p>
      </section>

      <!-- 表单区域 -->
      <form class="publish-form" @submit.prevent="handleSubmit">
        <!-- 商品图片上传 -->
        <section class="form-section">
          <div class="section-header">
            <h2 class="section-title">商品图片</h2>
            <span class="section-hint">最多上传6张，第一张为主图</span>
          </div>
          <div class="upload-area">
            <div
              v-for="(img, idx) in form.images"
              :key="idx"
              class="upload-item"
            >
              <img :src="img" :alt="`商品图片${idx + 1}`" />
              <button
                type="button"
                class="remove-btn"
                @click="removeImage(idx)"
              >
                <i class="fas fa-times"></i>
              </button>
            </div>
            <div
              v-if="form.images.length < 6"
              class="upload-placeholder"
              @click="triggerFileInput"
            >
              <i class="fas fa-camera"></i>
              <span>点击上传</span>
            </div>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              multiple
              style="display: none"
              @change="handleImageUpload"
            />
          </div>
        </section>

        <!-- 基本信息 -->
        <section class="form-section">
          <div class="section-header">
            <h2 class="section-title">基本信息</h2>
            <span class="required">*</span>
          </div>
          <div class="form-group">
            <label class="form-label">
              商品标题 <span class="required">*</span>
            </label>
            <input
              v-model="form.title"
              type="text"
              class="form-input"
              placeholder="例如：九成新高等数学教材第七版"
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
                商品分类 <span class="required">*</span>
              </label>
              <select v-model="form.category" class="form-select" required>
                <option value="">请选择分类</option>
                <option value="books">教材书籍</option>
                <option value="digital">数码产品</option>
                <option value="clothing">服饰鞋包</option>
                <option value="daily">生活用品</option>
                <option value="sports">运动户外</option>
                <option value="others">其他</option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label">
                成色 <span class="required">*</span>
              </label>
              <select v-model="form.condition" class="form-select" required>
                <option value="">请选择成色</option>
                <option value="new">全新</option>
                <option value="90">9成新</option>
                <option value="80">8成新</option>
                <option value="70">7成新</option>
                <option value="60">6成及以下</option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">
                价格 <span class="required">*</span>
              </label>
              <div class="price-input-group">
                <span class="price-symbol">¥</span>
                <input
                  v-model.number="form.price"
                  type="number"
                  class="form-input price-input"
                  placeholder="0.00"
                  min="0"
                  step="0.01"
                  required
                />
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">校区</label>
              <select v-model="form.campus" class="form-select">
                <option value="main">主校区</option>
                <option value="east">东校区</option>
                <option value="west">西校区</option>
              </select>
            </div>
          </div>
        </section>

        <!-- 商品详情 -->
        <section class="form-section">
          <div class="section-header">
            <h2 class="section-title">商品详情</h2>
            <span class="required">*</span>
          </div>
          <div class="form-group">
            <label class="form-label">
              商品描述 <span class="required">*</span>
            </label>
            <textarea
              v-model="form.description"
              class="form-textarea"
              placeholder="请详细描述商品的使用情况、新旧程度、购买时间等信息，帮助买家更好地了解商品..."
              rows="6"
              maxlength="500"
              required
            ></textarea>
            <div class="form-hint">
              <span>{{ form.description.length }}/500</span>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">购买时间</label>
              <input
                v-model="form.purchaseTime"
                type="text"
                class="form-input"
                placeholder="例如：2023年9月"
              />
            </div>

            <div class="form-group">
              <label class="form-label">使用情况</label>
              <input
                v-model="form.usage"
                type="text"
                class="form-input"
                placeholder="例如：使用一学期，保存完好"
              />
            </div>
          </div>
        </section>

        <!-- 交易方式 -->
        <section class="form-section">
          <div class="section-header">
            <h2 class="section-title">交易方式</h2>
          </div>
          <div class="form-group">
            <div class="checkbox-group">
              <label class="checkbox-label">
                <input
                  v-model="form.tradeMethods"
                  type="checkbox"
                  value="face"
                  class="checkbox-input"
                />
                <span>面交</span>
              </label>
              <label class="checkbox-label">
                <input
                  v-model="form.tradeMethods"
                  type="checkbox"
                  value="express"
                  class="checkbox-input"
                />
                <span>快递</span>
              </label>
              <label class="checkbox-label">
                <input
                  v-model="form.tradeMethods"
                  type="checkbox"
                  value="both"
                  class="checkbox-input"
                />
                <span>面交/快递</span>
              </label>
            </div>
          </div>
        </section>

        <!-- 联系方式 -->
        <section class="form-section">
          <div class="section-header">
            <h2 class="section-title">联系方式</h2>
            <span class="section-hint">用于买家联系您</span>
          </div>
          <div class="form-group">
            <label class="form-label">手机号</label>
            <input
              v-model="form.phone"
              type="tel"
              class="form-input"
              placeholder="请输入手机号（选填）"
              pattern="[0-9]{11}"
            />
          </div>
          <div class="form-group">
            <label class="form-label">微信号</label>
            <input
              v-model="form.wechat"
              type="text"
              class="form-input"
              placeholder="请输入微信号（选填）"
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
            <i class="fas fa-check"></i> 发布商品
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
            <li>商品图片清晰，多角度展示，有助于快速成交</li>
            <li>如实描述商品状况，避免交易纠纷</li>
            <li>合理定价，参考同类商品市场价格</li>
            <li>及时回复买家咨询，提高成交率</li>
            <li>交易时注意安全，建议选择平台认证的买家</li>
          </ul>
        </div>
      </section>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()
const fileInput = ref<HTMLInputElement | null>(null)

// 表单数据
const form = reactive({
  images: [] as string[],
  title: '',
  category: '',
  condition: '',
  price: null as number | null,
  campus: 'main',
  description: '',
  purchaseTime: '',
  usage: '',
  tradeMethods: [] as string[],
  phone: '',
  wechat: ''
})

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理图片上传
const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files) return

  const remainingSlots = 6 - form.images.length
  const filesToAdd = Array.from(files).slice(0, remainingSlots)

  filesToAdd.forEach((file) => {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader()
      reader.onload = (e) => {
        const result = e.target?.result as string
        if (result && form.images.length < 6) {
          form.images.push(result)
        }
      }
      reader.readAsDataURL(file)
    }
  })

  // 清空input，允许重复选择同一文件
  if (target) {
    target.value = ''
  }
}

// 移除图片
const removeImage = (index: number) => {
  form.images.splice(index, 1)
}

// 表单验证
const validateForm = () => {
  if (!form.title.trim()) {
    alert('请输入商品标题')
    return false
  }
  if (!form.category) {
    alert('请选择商品分类')
    return false
  }
  if (!form.condition) {
    alert('请选择成色')
    return false
  }
  if (!form.price || form.price <= 0) {
    alert('请输入有效的价格')
    return false
  }
  if (!form.description.trim()) {
    alert('请输入商品描述')
    return false
  }
  if (form.images.length === 0) {
    alert('请至少上传一张商品图片')
    return false
  }
  return true
}

// 提交表单
const handleSubmit = () => {
  if (!validateForm()) {
    return
  }

  // 构建提交数据
  const submitData = {
    ...form,
    tradeMethod: form.tradeMethods.length > 0 ? form.tradeMethods.join('/') : '面交'
  }

  try {
    // TODO: 调用API提交
    // await publishProduct(submitData)
    
    // 暂时使用模拟成功
    alert('发布成功！')
    router.push('/secondhand')
  } catch (error) {
    console.error('发布失败:', error)
    alert('发布失败，请稍后重试')
  }
}

// 保存草稿
const saveDraft = () => {
  // 保存到本地存储
  localStorage.setItem('secondhand_draft', JSON.stringify(form))
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

/* 图片上传 */
.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.upload-item {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #eee;
}

.upload-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  transition: all 0.3s;
}

.remove-btn:hover {
  background: rgba(216, 27, 96, 0.8);
}

.upload-placeholder {
  width: 120px;
  height: 120px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafafa;
}

.upload-placeholder:hover {
  border-color: var(--primary);
  background: #fff5f9;
}

.upload-placeholder i {
  font-size: 32px;
  color: var(--primary);
  margin-bottom: 8px;
}

.upload-placeholder span {
  font-size: 12px;
  color: var(--muted);
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

/* 复选框组 */
.checkbox-group {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: var(--text);
}

.checkbox-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--primary);
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

  .upload-item,
  .upload-placeholder {
    width: 100px;
    height: 100px;
  }
}

@media (max-width: 480px) {
  .page-container {
    padding: 15px 10px 30px;
  }

  .upload-item,
  .upload-placeholder {
    width: 80px;
    height: 80px;
  }

  .checkbox-group {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
