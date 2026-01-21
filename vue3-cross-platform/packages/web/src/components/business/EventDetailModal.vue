<template>
  <div class="event-detail-modal">
    <div class="modal-overlay" @click="handleClose"></div>
    <div class="modal-content">
      <div class="modal-header">
        <h3>{{ isEdit ? '编辑行程' : '行程详情' }}</h3>
        <button class="close-btn" @click="handleClose">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="modal-body">
        <!-- 编辑模式下的表单 -->
        <template v-if="isEdit">
          <div class="form-group">
            <label for="title">行程标题</label>
            <input
              id="title"
              v-model="formData.title"
              type="text"
              class="form-input"
              placeholder="请输入行程标题"
            />
          </div>

          <div class="form-row">
            <div class="form-group" style="flex: 1;">
              <label for="category">分类</label>
              <select
                id="category"
                v-model="formData.category"
                class="form-select"
              >
                <option value="course">课程</option>
                <option value="exam">考试</option>
                <option value="club">社团</option>
                <option value="part-time">兼职</option>
                <option value="personal">个人事务</option>
              </select>
            </div>

            <div class="form-group" style="flex: 1; margin-left: 15px;">
              <label for="dateKey">日期</label>
              <input
                id="dateKey"
                v-model="formData.dateKey"
                type="date"
                class="form-input"
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="startTime">开始时间</label>
              <input
                id="startTime"
                v-model="formData.startTime"
                type="time"
                class="form-input"
              />
            </div>

            <div class="form-group" style="margin-left: 15px;">
              <label for="endTime">结束时间</label>
              <input
                id="endTime"
                v-model="formData.endTime"
                type="time"
                class="form-input"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="location">地点</label>
            <input
              id="location"
              v-model="formData.location"
              type="text"
              class="form-input"
              placeholder="请输入地点"
            />
          </div>

          <div class="form-group">
            <label for="description">描述</label>
            <textarea
              id="description"
              v-model="formData.description"
              class="form-textarea"
              rows="3"
              placeholder="请输入行程描述"
            ></textarea>
          </div>

          <div class="form-group">
            <label>行程心情</label>
            <MoodSelector
              :selected-mood="formData.moodIcon"
              @select-mood="(mood) => formData.moodIcon = mood"
            />
          </div>
        </template>

        <!-- 查看模式下的详情 -->
        <template v-else-if="event">
          <div class="detail-section">
            <h4 class="detail-title">行程信息</h4>
            <div class="detail-item">
              <span class="detail-label">标题：</span>
              <span class="detail-value">{{ event.title }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">分类：</span>
              <span class="detail-value">{{ getCategoryLabel(event.category) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">日期：</span>
              <span class="detail-value">{{ event.dateKey }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">时间：</span>
              <span class="detail-value">{{ event.startTime }} - {{ event.endTime }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">地点：</span>
              <span class="detail-value">{{ event.location }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">描述：</span>
              <span class="detail-value">{{ event.description }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">状态：</span>
              <span class="detail-value status" :class="event.status">
                {{ getStatusLabel(event.status) }}
              </span>
            </div>
            <div v-if="event.moodIcon" class="detail-item">
              <span class="detail-label">心情：</span>
              <span class="detail-value">
                <i :class="event.moodIcon" style="font-size: 20px; color: #FF6B9D;"></i>
              </span>
            </div>
          </div>
        </template>
      </div>

      <div class="modal-footer">
        <template v-if="isEdit">
          <button class="btn btn-cancel" @click="handleClose">取消</button>
          <button class="btn btn-primary" @click="handleSave">保存</button>
        </template>
        <template v-else>
          <button class="btn btn-secondary" @click="handleEdit">编辑</button>
          <button class="btn btn-danger" @click="handleDelete">删除</button>
          <button class="btn btn-primary" @click="handleShare">分享</button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, defineProps, defineEmits } from 'vue'
import { Schedule, ScheduleCategory, ScheduleStatus } from '../../types'
import MoodSelector from './MoodSelector.vue'
import { categoryColorMap } from '../../utils/schedule-excel'

interface Props {
  event?: Schedule
  isEdit?: boolean
  visible?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  event: () => ({} as Schedule),
  isEdit: false,
  visible: false
})

const emit = defineEmits<{
  'close': []
  'save': [event: Schedule]
  'delete': [eventId: string]
  'share': [event: Schedule]
  'edit': [event: Schedule]
}>()

// 表单数据
const formData = reactive({
  id: '',
  title: '',
  category: ScheduleCategory.PERSONAL,
  startTime: '',
  endTime: '',
  dateKey: '',
  location: '',
  description: '',
  status: ScheduleStatus.UPCOMING,
  sharedWith: [],
  moodIcon: '',
  isAllDay: false,
  color: categoryColorMap[ScheduleCategory.PERSONAL]
})

// 监听事件变化，更新表单数据
watch(() => props.event, (newEvent) => {
  if (newEvent) {
    Object.assign(formData, newEvent)
    formData.color = categoryColorMap[formData.category]
  }
}, { deep: true, immediate: true })

// 获取分类标签
const getCategoryLabel = (category: ScheduleCategory): string => {
  const labels: Record<ScheduleCategory, string> = {
    [ScheduleCategory.COURSE]: '课程',
    [ScheduleCategory.EXAM]: '考试',
    [ScheduleCategory.CLUB]: '社团',
    [ScheduleCategory.PART_TIME]: '兼职',
    [ScheduleCategory.PERSONAL]: '个人事务',
    [ScheduleCategory.ALL]: '全部'
  }
  return labels[category]
}

// 获取状态标签
const getStatusLabel = (status: ScheduleStatus): string => {
  const labels: Record<ScheduleStatus, string> = {
    [ScheduleStatus.UPCOMING]: '即将开始',
    [ScheduleStatus.ONGOING]: '进行中',
    [ScheduleStatus.COMPLETED]: '已完成'
  }
  return labels[status]
}

// 关闭弹窗
const handleClose = () => {
  emit('close')
}

// 保存行程
const handleSave = () => {
  // 验证表单
  if (!formData.title.trim()) {
    alert('请输入行程标题')
    return
  }

  if (!formData.startTime) {
    alert('请选择开始时间')
    return
  }

  if (!formData.dateKey) {
    alert('请选择日期')
    return
  }

  // 更新颜色
  formData.color = categoryColorMap[formData.category]

  // 如果是新行程，生成ID
  if (!formData.id) {
    formData.id = crypto.randomUUID()
  }

  emit('save', formData as Schedule)
  handleClose()
}

// 删除行程
const handleDelete = () => {
  if (props.event?.id) {
    if (confirm('确定要删除这个行程吗？')) {
      emit('delete', props.event.id)
      handleClose()
    }
  }
}

// 分享行程
const handleShare = () => {
  if (props.event) {
    emit('share', props.event)
  }
}

// 编辑行程
const handleEdit = () => {
  if (props.event) {
    emit('edit', props.event)
  }
}
</script>

<style scoped lang="scss">
.event-detail-modal {
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

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-row {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.form-input,
.form-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;

  &:focus {
    outline: none;
    border-color: #FF6B9D;
  }
}

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  min-height: 80px;
  transition: border-color 0.3s;

  &:focus {
    outline: none;
    border-color: #FF6B9D;
  }
}

/* 详情样式 */
.detail-section {
  margin-bottom: 20px;
}

.detail-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.detail-item {
  margin-bottom: 12px;
  display: flex;
  align-items: flex-start;
}

.detail-label {
  width: 80px;
  font-weight: 500;
  color: #666;
  flex-shrink: 0;
}

.detail-value {
  flex: 1;
  color: #333;
  word-break: break-word;
}

.status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status.upcoming {
  background: #E3F2FD;
  color: #1976D2;
}

.status.ongoing {
  background: #E8F5E9;
  color: #388E3C;
}

.status.completed {
  background: #F5F5F5;
  color: #757575;
}

/* 按钮样式 */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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
}

.btn-secondary:hover {
  background: #e0e0e0;
}

.btn-danger {
  background: #f44336;
  color: white;
}

.btn-danger:hover {
  background: #d32f2f;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(244, 67, 54, 0.3);
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
