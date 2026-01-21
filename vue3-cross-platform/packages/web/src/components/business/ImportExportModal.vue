<template>
  <div class="import-export-modal">
    <div class="modal-overlay" @click="handleClose"></div>
    <div class="modal-content">
      <div class="modal-header">
        <h3>行程导入导出</h3>
        <button class="close-btn" @click="handleClose">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="modal-body">
        <div class="tab-container">
          <div class="tab-buttons">
            <button
              class="tab-btn"
              :class="{ active: activeTab === 'export' }"
              @click="activeTab = 'export'"
            >
              导出
            </button>
            <button
              class="tab-btn"
              :class="{ active: activeTab === 'import' }"
              @click="activeTab = 'import'"
            >
              导入
            </button>
          </div>

          <!-- 导出标签页 -->
          <div v-if="activeTab === 'export'" class="tab-content">
            <div class="export-section">
              <div class="export-option">
                <div class="option-icon">
                  <i class="fas fa-file-export"></i>
                </div>
                <div class="option-content">
                  <h4>导出当前行程</h4>
                  <p>将当前所有行程导出为Excel文件，包含行程标题、分类、时间、地点和描述等信息。</p>
                  <button class="btn btn-primary" @click="handleExport">
                    <i class="fas fa-download"></i> 导出行程
                  </button>
                </div>
              </div>

              <div class="export-option">
                <div class="option-icon">
                  <i class="fas fa-file-alt"></i>
                </div>
                <div class="option-content">
                  <h4>下载导入模板</h4>
                  <p>下载标准的行程表导入模板，填写后可批量导入行程数据。</p>
                  <button class="btn btn-secondary" @click="handleDownloadTemplate">
                    <i class="fas fa-file-download"></i> 下载模板
                  </button>
                </div>
              </div>
            </div>

            <div class="export-info">
              <h5>导出说明：</h5>
              <ul>
                <li>导出的Excel文件包含所有行程数据</li>
                <li>导出文件可用于备份或在其他设备上导入</li>
                <li>支持Excel 2007及以上版本</li>
              </ul>
            </div>
          </div>

          <!-- 导入标签页 -->
          <div v-else-if="activeTab === 'import'" class="tab-content">
            <div class="import-section">
              <div class="import-drag-area" :class="{ 'dragover': isDragover }" @drop="handleFileDrop" @dragover.prevent="isDragover = true" @dragleave.prevent="isDragover = false">
                <input
                  ref="fileInput"
                  type="file"
                  accept=".xlsx,.xls"
                  class="file-input"
                  @change="handleFileChange"
                />
                <div class="drag-content">
                  <i class="fas fa-cloud-upload-alt"></i>
                  <h4>拖放文件到此处</h4>
                  <p>或点击选择文件</p>
                  <button class="btn btn-primary" @click="triggerFileInput">
                    <i class="fas fa-folder-open"></i> 选择文件
                  </button>
                </div>
              </div>

              <div v-if="selectedFile" class="file-info">
                <i class="fas fa-file-excel"></i>
                <span>{{ selectedFile.name }}</span>
                <button class="remove-file" @click="removeFile">
                  <i class="fas fa-times"></i>
                </button>
              </div>

              <div class="import-actions" v-if="selectedFile">
                <button class="btn btn-primary" @click="handleImport">
                  <i class="fas fa-upload"></i> 开始导入
                </button>
                <button class="btn btn-secondary" @click="removeFile">
                  <i class="fas fa-times"></i> 取消
                </button>
              </div>
            </div>

            <div class="import-info">
              <h5>导入说明：</h5>
              <ul>
                <li>仅支持.xlsx和.xls格式的Excel文件</li>
                <li>请使用标准模板进行导入</li>
                <li>导入会覆盖同名行程</li>
                <li>导入过程可能需要几秒钟，请耐心等待</li>
              </ul>
            </div>
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
import { ref, defineProps, defineEmits } from 'vue'
import { Schedule } from '../../types'
import { exportSchedulesToExcel, importSchedulesFromExcel, generateScheduleTemplate } from '../../utils/schedule-excel'

interface Props {
  schedules?: Schedule[]
  visible?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  schedules: () => [] as Schedule[],
  visible: false
})

const emit = defineEmits<{
  'close': []
  'import-success': [schedules: Schedule[]]
}>()

// 当前激活的标签页
const activeTab = ref('export')

// 文件相关
const fileInput = ref<HTMLInputElement | null>(null)
const selectedFile = ref<File | null>(null)
const isDragover = ref(false)

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理文件选择
const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    selectedFile.value = target.files[0]
  }
}

// 处理文件拖放
const handleFileDrop = (e: DragEvent) => {
  e.preventDefault()
  isDragover.value = false
  if (e.dataTransfer && e.dataTransfer.files.length > 0) {
    selectedFile.value = e.dataTransfer.files[0]
  }
}

// 移除选择的文件
const removeFile = () => {
  selectedFile.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// 导出行程
const handleExport = () => {
  if (props.schedules.length === 0) {
    alert('没有行程数据可导出')
    return
  }
  exportSchedulesToExcel(props.schedules)
}

// 下载模板
const handleDownloadTemplate = () => {
  generateScheduleTemplate()
}

// 导入行程
const handleImport = async () => {
  if (!selectedFile.value) return

  try {
    const importedSchedules = await importSchedulesFromExcel(selectedFile.value)
    emit('import-success', importedSchedules)
    removeFile()
    alert('导入成功！')
  } catch (error) {
    console.error('导入失败:', error)
    alert('导入失败，请检查文件格式是否正确')
  }
}

// 关闭弹窗
const handleClose = () => {
  emit('close')
  // 重置状态
  activeTab.value = 'export'
  removeFile()
}
</script>

<style scoped lang="scss">
.import-export-modal {
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
  max-width: 600px;
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

/* 标签页 */
.tab-container {
  margin-bottom: 20px;
}

.tab-buttons {
  display: flex;
  border-bottom: 2px solid #eee;
  margin-bottom: 20px;
}

.tab-btn {
  flex: 1;
  padding: 15px;
  background: none;
  border: none;
  font-size: 16px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;

  &:hover {
    color: #FF6B9D;
    background: rgba(255, 107, 157, 0.1);
  }

  &.active {
    color: #FF6B9D;
    border-bottom-color: #FF6B9D;
  }
}

/* 导出部分 */
.export-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 20px;
}

.export-option {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 12px;
  align-items: flex-start;
}

.option-icon {
  font-size: 36px;
  color: #FF6B9D;
  flex-shrink: 0;
}

.option-content {
  flex: 1;
}

.option-content h4 {
  margin: 0 0 10px;
  font-size: 18px;
  color: #333;
}

.option-content p {
  margin: 0 0 15px;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

/* 导入部分 */
.import-section {
  margin-bottom: 20px;
}

.import-drag-area {
  border: 2px dashed #ddd;
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #fafafa;

  &:hover,
  &.dragover {
    border-color: #FF6B9D;
    background: rgba(255, 107, 157, 0.05);
  }
}

.file-input {
  display: none;
}

.drag-content i {
  font-size: 48px;
  color: #FF6B9D;
  margin-bottom: 15px;
}

.drag-content h4 {
  margin: 0 0 10px;
  font-size: 18px;
  color: #333;
}

.drag-content p {
  margin: 0 0 20px;
  color: #666;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #e8f5e9;
  border-radius: 8px;
  margin-top: 20px;
}

.file-info i {
  color: #388e3c;
  font-size: 18px;
}

.file-info span {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.remove-file {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.3s;

  &:hover {
    color: #f44336;
  }
}

.import-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 20px;
}

/* 信息说明 */
.export-info,
.import-info {
  background: #fff3e0;
  border-left: 4px solid #ff9800;
  padding: 15px;
  border-radius: 8px;
}

.export-info h5,
.import-info h5 {
  margin: 0 0 10px;
  color: #f57c00;
  font-size: 16px;
}

.export-info ul,
.import-info ul {
  margin: 0;
  padding-left: 20px;
}

.export-info li,
.import-info li {
  color: #e65100;
  font-size: 14px;
  margin-bottom: 5px;
}

/* 按钮样式 */
.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
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
}

.btn-secondary:hover {
  background: #e0e0e0;
}

.btn-cancel {
  background: white;
  color: #666;
  border: 1px solid #ddd;
  padding: 10px 20px;
}

.btn-cancel:hover {
  background: #f5f5f5;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
  border-top: 1px solid #eee;
}
</style>
