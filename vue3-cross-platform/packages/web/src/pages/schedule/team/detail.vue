<template>
  <div class="team-detail-page">
    <NavBar />

    <div class="page-container">
      <!-- 团队信息横幅 -->
      <section class="team-hero-section">
        <div class="hero-background"></div>
        <div class="hero-content">
          <div class="team-header-main">
            <div class="team-avatar-large">
            <i class="fas fa-users"></i>
              <div class="avatar-badge" v-if="isCreator">
                <i class="fas fa-crown"></i>
          </div>
            </div>
            <div class="team-info-main">
              <div class="team-title-row">
            <h1 class="team-name">{{ teamInfo.name }}</h1>
                <div class="team-status-badge" v-if="isCreator">
                  <i class="fas fa-crown"></i>
                  <span>创建者</span>
                </div>
              </div>
              <p class="team-desc">{{ teamInfo.description || '暂无描述' }}</p>
              <div class="team-stats-row">
                <div class="stat-badge">
                <i class="fas fa-users"></i>
                <span>{{ teamInfo.memberCount }} 成员</span>
              </div>
                <div class="stat-badge">
                  <i class="fas fa-calendar-check"></i>
                <span>{{ teamInfo.scheduleCount }} 行程</span>
              </div>
                <div class="stat-badge">
                  <i class="fas fa-clock"></i>
                  <span>最近活跃：{{ lastActivity }}</span>
            </div>
          </div>
            </div>
            <div class="team-actions-main">
              <button class="btn-action-primary" @click="showInviteModal = true">
                <i class="fas fa-user-plus"></i>
                <span>邀请成员</span>
            </button>
              <button class="btn-action-secondary" @click="showSendInviteModal = true">
                <i class="fas fa-paper-plane"></i>
                <span>发送邀请</span>
            </button>
              <button class="btn-action-icon" @click="editTeam" title="团队设置">
                <i class="fas fa-cog"></i>
            </button>
            </div>
          </div>
        </div>
      </section>

      <!-- 标签导航栏 -->
      <section class="tabs-section">
        <div class="tabs-container">
          <button
            class="tab-item"
            :class="{ active: activeTab === 'schedules' }"
            @click="activeTab = 'schedules'"
          >
            <i class="fas fa-calendar"></i>
            <span>团队行程</span>
            <span class="tab-badge">{{ teamSchedules.length }}</span>
          </button>
            <button
            class="tab-item"
            :class="{ active: activeTab === 'members' }"
            @click="activeTab = 'members'"
          >
            <i class="fas fa-users"></i>
            <span>团队成员</span>
            <span class="tab-badge">{{ members.length }}</span>
          </button>
          <button
            class="tab-item"
            :class="{ active: activeTab === 'settings' }"
            @click="activeTab = 'settings'"
            v-if="isCreator"
            >
            <i class="fas fa-cog"></i>
            <span>团队设置</span>
            </button>
        </div>
      </section>

      <!-- 内容区域 -->
      <div class="content-area">
        <!-- 团队行程标签页 -->
        <div v-show="activeTab === 'schedules'" class="tab-content">
          <div class="section-header-modern">
            <div class="header-left">
              <h2 class="section-title-modern">
                <i class="fas fa-calendar-check"></i>
                团队行程
              </h2>
              <p class="section-subtitle">管理团队的共享行程安排</p>
            </div>
          <button 
            v-if="isCreator" 
              class="btn-create-modern"
            @click="showCreateScheduleModal = true"
          >
              <i class="fas fa-plus"></i>
              <span>创建行程</span>
          </button>
        </div>

          <div v-if="teamSchedules.length === 0" class="empty-state-modern">
            <div class="empty-icon-wrapper">
              <i class="fas fa-calendar-times"></i>
        </div>
            <h3>暂无团队行程</h3>
            <p>创建第一个团队行程，让所有成员都能看到</p>
            <button
              v-if="isCreator"
              class="btn-primary-empty"
              @click="showCreateScheduleModal = true"
            >
              <i class="fas fa-plus"></i>
              创建行程
            </button>
          </div>

          <div v-else class="schedules-grid-modern">
          <div
            v-for="schedule in teamSchedules"
            :key="schedule.id"
              class="schedule-card-modern"
          >
              <div class="schedule-date-modern">
                <div class="date-main">{{ schedule.date.split('-')[2] }}</div>
                <div class="date-month">{{ getMonthName(schedule.date) }}</div>
                <div class="date-day">{{ schedule.day }}</div>
            </div>
              <div class="schedule-content-modern">
                <div class="schedule-header-modern">
                  <h3 class="schedule-title-modern">{{ schedule.title }}</h3>
                  <div class="schedule-status" :class="getScheduleStatus(schedule)">
                    <i class="fas fa-circle"></i>
                    <span>{{ getScheduleStatusText(schedule) }}</span>
                  </div>
                </div>
                <p class="schedule-desc-modern" v-if="schedule.description">
                  {{ schedule.description }}
                </p>
                <div class="schedule-meta-modern">
                  <div class="meta-item">
                    <i class="fas fa-clock"></i>
                    <span>{{ schedule.time }}</span>
                  </div>
                  <div class="meta-item" v-if="schedule.location">
                    <i class="fas fa-map-marker-alt"></i>
                    <span>{{ schedule.location }}</span>
                  </div>
                </div>
              </div>
              <div class="schedule-actions">
                <button class="btn-action-mini" @click="viewScheduleDetail(schedule.id)">
                  <i class="fas fa-eye"></i>
                </button>
                <button
                  v-if="isCreator"
                  class="btn-action-mini"
                  @click="editSchedule(schedule.id)"
                >
                  <i class="fas fa-edit"></i>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 团队成员标签页 -->
        <div v-show="activeTab === 'members'" class="tab-content">
          <div class="section-header-modern">
            <div class="header-left">
              <h2 class="section-title-modern">
                <i class="fas fa-users"></i>
                团队成员
              </h2>
              <p class="section-subtitle">查看和管理团队成员</p>
            </div>
            <button
              v-if="isCreator"
              class="btn-create-modern"
              @click="showInviteModal = true"
            >
              <i class="fas fa-user-plus"></i>
              <span>邀请成员</span>
            </button>
          </div>

          <div class="members-grid-modern">
            <div
              v-for="member in members"
              :key="member.id"
              class="member-card-modern"
            >
              <div class="member-avatar-modern">
                <i class="fas fa-user"></i>
                <div class="role-badge" :class="member.role.toLowerCase()">
                  <i :class="getRoleIcon(member.role)"></i>
                </div>
              </div>
              <div class="member-info-modern">
                <h3 class="member-name-modern">{{ member.name }}</h3>
                <p class="member-role-modern">{{ member.role }}</p>
                <div class="member-stats">
                  <span class="member-stat-item">
                    <i class="fas fa-calendar"></i>
                    {{ getMemberScheduleCount(member.id) }} 行程
                </span>
              </div>
            </div>
              <div class="member-actions-modern" v-if="isCreator && member.id !== currentUserId">
                <button
                  class="btn-remove-modern"
                  @click="removeMember(member.id)"
                  title="移除成员"
                >
                  <i class="fas fa-times"></i>
                </button>
          </div>
        </div>
          </div>
        </div>

        <!-- 团队设置标签页 -->
        <div v-show="activeTab === 'settings'" class="tab-content" v-if="isCreator">
          <div class="section-header-modern">
            <div class="header-left">
              <h2 class="section-title-modern">
                <i class="fas fa-cog"></i>
                团队设置
              </h2>
              <p class="section-subtitle">管理团队的基本信息和权限</p>
            </div>
          </div>

          <div class="settings-content">
            <div class="settings-card">
              <h3 class="settings-card-title">基本信息</h3>
              <div class="settings-form">
                <div class="form-group-modern">
                  <label>团队名称</label>
                  <input
                    v-model="teamInfo.name"
                    type="text"
                    class="form-input-modern"
                    placeholder="请输入团队名称"
                  />
                </div>
                <div class="form-group-modern">
                  <label>团队描述</label>
                  <textarea
                    v-model="teamInfo.description"
                    class="form-textarea-modern"
                    placeholder="请输入团队描述"
                    rows="4"
                  ></textarea>
                </div>
                <button class="btn-save-settings" @click="saveTeamSettings">
                  <i class="fas fa-save"></i>
                  保存设置
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 邀请成员模态框（显示邀请码） -->
    <div v-if="showInviteModal" class="modal-overlay" @click="showInviteModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>邀请成员</h2>
          <button class="btn-close" @click="showInviteModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="invite-code-section">
            <label class="form-label">团队邀请码</label>
            <div class="code-display">
              <span class="code-text">{{ teamInfo.inviteCode || '生成中...' }}</span>
              <button class="btn-copy" @click="copyInviteCode">
                <i class="fas fa-copy"></i> 复制
              </button>
              <button class="btn-refresh" @click="generateInviteCode" title="重新生成">
                <i class="fas fa-sync-alt"></i> 刷新
              </button>
            </div>
            <p class="code-tip">将邀请码分享给其他成员，他们可以使用此码加入团队</p>
            <div class="share-options">
              <button class="share-btn" @click="shareInviteCode('link')">
                <i class="fas fa-link"></i> 复制链接
              </button>
              <button class="share-btn" @click="shareInviteCode('qr')">
                <i class="fas fa-qrcode"></i> 生成二维码
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建团队行程模态框 -->
    <div v-if="showCreateScheduleModal" class="modal-overlay" @click="showCreateScheduleModal = false">
      <div class="modal-content schedule-modal" @click.stop>
        <div class="modal-header">
          <h2>创建团队行程</h2>
          <button class="btn-close" @click="showCreateScheduleModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">行程标题 <span class="required">*</span></label>
            <input 
              v-model="newSchedule.title" 
              type="text" 
              placeholder="请输入行程标题"
              maxlength="50"
            />
          </div>
          <div class="form-group">
            <label class="form-label">行程描述</label>
            <textarea 
              v-model="newSchedule.description" 
              placeholder="请输入行程描述（选填）"
              rows="3"
              maxlength="200"
            ></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">开始日期 <span class="required">*</span></label>
              <input 
                v-model="newSchedule.startDate" 
                type="date" 
                :min="minDate"
              />
            </div>
            <div class="form-group">
              <label class="form-label">开始时间 <span class="required">*</span></label>
              <input 
                v-model="newSchedule.startTime" 
                type="time" 
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">结束日期 <span class="required">*</span></label>
              <input 
                v-model="newSchedule.endDate" 
                type="date" 
                :min="newSchedule.startDate || minDate"
              />
            </div>
            <div class="form-group">
              <label class="form-label">结束时间 <span class="required">*</span></label>
              <input 
                v-model="newSchedule.endTime" 
                type="time" 
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">地点</label>
            <input 
              v-model="newSchedule.location" 
              type="text" 
              placeholder="请输入地点（选填）"
              maxlength="100"
            />
          </div>
          <div class="form-tip">
            <i class="fas fa-info-circle"></i>
            <span>创建后，此行程将自动添加到所有团队成员的个人行程表中</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showCreateScheduleModal = false">取消</button>
          <button class="btn-submit" @click="createSchedule" :disabled="creatingSchedule">
            <i v-if="creatingSchedule" class="fas fa-spinner fa-spin"></i>
            {{ creatingSchedule ? '创建中...' : '创建行程' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 发送邀请模态框 -->
    <div v-if="showSendInviteModal" class="modal-overlay" @click="showSendInviteModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>发送团队邀请</h2>
          <button class="btn-close" @click="showSendInviteModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">邀请方式</label>
            <div class="invite-methods">
              <button 
                :class="['method-btn', { active: inviteMethod === 'username' }]"
                @click="inviteMethod = 'username'"
              >
                <i class="fas fa-user"></i> 用户名
              </button>
              <button 
                :class="['method-btn', { active: inviteMethod === 'email' }]"
                @click="inviteMethod = 'email'"
              >
                <i class="fas fa-envelope"></i> 邮箱
              </button>
              <button 
                :class="['method-btn', { active: inviteMethod === 'phone' }]"
                @click="inviteMethod = 'phone'"
              >
                <i class="fas fa-phone"></i> 手机号
              </button>
            </div>
          </div>
          <div class="form-group">
            <label class="form-label">
              {{ inviteMethod === 'username' ? '用户名' : inviteMethod === 'email' ? '邮箱地址' : '手机号' }}
              <span class="required">*</span>
            </label>
            <input 
              v-model="inviteTarget" 
              type="text" 
              :placeholder="`请输入${inviteMethod === 'username' ? '用户名' : inviteMethod === 'email' ? '邮箱地址' : '手机号'}`"
            />
          </div>
          <div class="form-group">
            <label class="form-label">邀请消息（选填）</label>
            <textarea 
              v-model="inviteMessage" 
              placeholder="请输入邀请消息..."
              rows="3"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showSendInviteModal = false">取消</button>
          <button class="btn-submit" @click="sendInvite">发送邀请</button>
        </div>
      </div>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'
import { getTeamDetail, regenerateTeamInviteCode, createTeamSchedule, getTeamSchedules, useUserStore } from '@campus/common'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const showInviteModal = ref(false)
const showSendInviteModal = ref(false)
const showCreateScheduleModal = ref(false)
const creatingSchedule = ref(false)
const activeTab = ref<'schedules' | 'members' | 'settings'>('schedules')
const lastActivity = ref('2小时前')

// 当前用户ID（从store获取，如果没有则使用默认值1用于测试）
const currentUserId = computed(() => userStore.userId || 1)

// 邀请相关
const inviteMethod = ref<'username' | 'email' | 'phone'>('username')
const inviteTarget = ref('')
const inviteMessage = ref('')

// 团队信息
const teamInfo = reactive({
  id: '',
  name: '项目组A',
  description: '软件开发项目组',
  memberCount: 5,
  scheduleCount: 12,
  inviteCode: 'TEAM20240115',
  creatorId: 0,
  userRole: ''
})

// 判断是否是创建者
const isCreator = computed(() => {
  return teamInfo.creatorId === currentUserId.value || 
         teamInfo.userRole === 'CREATOR' || 
         teamInfo.userRole === '创建者' ||
         userStore.isDeveloper // 开发者模式拥有所有权限
})

// 创建行程表单
const newSchedule = reactive({
  title: '',
  description: '',
  startDate: '',
  startTime: '',
  endDate: '',
  endTime: '',
  location: ''
})

// 最小日期（今天）
const minDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

// 成员列表
const members = ref([
  {
    id: 1,
    name: '张同学',
    role: '管理员'
  },
  {
    id: 2,
    name: '李同学',
    role: '成员'
  },
  {
    id: 3,
    name: '王同学',
    role: '成员'
  }
])

// 团队行程
const teamSchedules = ref([
  {
    id: 1,
    title: '项目会议',
    description: '讨论项目进度和下一步计划',
    date: '2024-01-20',
    day: '周六',
    time: '14:00-16:00',
    location: '教学楼A101'
  },
  {
    id: 2,
    title: '代码审查',
    description: '审查本周提交的代码',
    date: '2024-01-22',
    day: '周一',
    time: '19:00-21:00',
    location: '线上'
  }
])

// 编辑团队
const editTeam = () => {
  activeTab.value = 'settings'
}

// 获取月份名称
const getMonthName = (dateStr: string) => {
  const month = parseInt(dateStr.split('-')[1])
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  return months[month - 1]
}

// 获取行程状态
const getScheduleStatus = (schedule: any) => {
  const now = new Date()
  const scheduleDate = new Date(schedule.date + ' ' + schedule.time.split('-')[0])
  if (scheduleDate < now) return 'past'
  if (scheduleDate.getTime() - now.getTime() < 24 * 60 * 60 * 1000) return 'upcoming'
  return 'future'
}

// 获取行程状态文本
const getScheduleStatusText = (schedule: any) => {
  const status = getScheduleStatus(schedule)
  if (status === 'past') return '已结束'
  if (status === 'upcoming') return '即将开始'
  return '未开始'
}

// 查看行程详情
const viewScheduleDetail = (id: number) => {
  // TODO: 跳转到行程详情页
  console.log('查看行程详情:', id)
}

// 编辑行程
const editSchedule = (id: number) => {
  // TODO: 编辑行程
  console.log('编辑行程:', id)
}

// 获取角色图标
const getRoleIcon = (role: string) => {
  if (role === '创建者' || role === 'CREATOR') return 'fas fa-crown'
  if (role === '管理员' || role === 'ADMIN') return 'fas fa-user-shield'
  return 'fas fa-user'
}

// 获取成员行程数量
const getMemberScheduleCount = (memberId: number) => {
  // TODO: 从API获取
  return Math.floor(Math.random() * 10)
}

// 保存团队设置
const saveTeamSettings = () => {
  // TODO: 保存设置
  alert('设置已保存')
}

// 移除成员
const removeMember = (memberId: number) => {
  if (confirm('确定要移除此成员吗？')) {
    const index = members.value.findIndex(m => m.id === memberId)
    if (index > -1) {
      members.value.splice(index, 1)
      teamInfo.memberCount--
      // TODO: 调用移除成员API
      // await removeMemberAPI(memberId)
    }
  }
}

// 生成邀请码
const generateInviteCode = async () => {
  try {
    const teamId = Number(teamInfo.id)
    if (!teamId) {
      alert('团队ID无效')
      return
    }
    const result = await regenerateTeamInviteCode(teamId)
    teamInfo.inviteCode = result.inviteCode
    alert('邀请码已重新生成')
  } catch (error: any) {
    console.error('重新生成邀请码失败:', error)
    alert(error?.message || '重新生成邀请码失败，请稍后重试')
  }
}

// 复制邀请码
const copyInviteCode = () => {
  if (!teamInfo.inviteCode) {
    generateInviteCode()
    return
  }
  
  if (typeof navigator !== 'undefined' && navigator.clipboard) {
    navigator.clipboard.writeText(teamInfo.inviteCode).then(() => {
      alert('邀请码已复制到剪贴板')
    }).catch(() => {
      // 降级方案
      const textArea = document.createElement('textarea')
      textArea.value = teamInfo.inviteCode
      document.body.appendChild(textArea)
      textArea.select()
      try {
        document.execCommand('copy')
        alert('邀请码已复制到剪贴板')
      } catch (err) {
        alert('复制失败，请手动复制：' + teamInfo.inviteCode)
      }
      document.body.removeChild(textArea)
    })
  } else {
    alert('邀请码：' + teamInfo.inviteCode)
  }
}

// 分享邀请码
const shareInviteCode = (method: 'link' | 'qr') => {
  if (!teamInfo.inviteCode) {
    generateInviteCode()
    return
  }
  
  if (method === 'link') {
    const inviteLink = `${window.location.origin}/schedule/team/join?code=${teamInfo.inviteCode}`
    if (typeof navigator !== 'undefined' && navigator.clipboard) {
      navigator.clipboard.writeText(inviteLink).then(() => {
        alert('邀请链接已复制到剪贴板')
      }).catch(() => {
        alert('邀请链接：' + inviteLink)
      })
    } else {
      alert('邀请链接：' + inviteLink)
    }
  } else if (method === 'qr') {
    // TODO: 生成二维码
    alert('二维码功能开发中...')
  }
}

// 发送邀请
const sendInvite = () => {
  if (!inviteTarget.value.trim()) {
    alert(`请输入${inviteMethod.value === 'username' ? '用户名' : inviteMethod.value === 'email' ? '邮箱地址' : '手机号'}`)
    return
  }

  try {
    // TODO: 调用发送邀请API
    // await sendInviteAPI({
    //   teamId: teamInfo.id,
    //   method: inviteMethod.value,
    //   target: inviteTarget.value,
    //   message: inviteMessage.value
    // })
    
    alert(`邀请已发送给${inviteTarget.value}`)
    inviteTarget.value = ''
    inviteMessage.value = ''
    showSendInviteModal.value = false
  } catch (error) {
    console.error('发送邀请失败:', error)
    alert('发送失败，请稍后重试')
  }
}

// 创建团队行程
const createSchedule = async () => {
  // 表单验证
  if (!newSchedule.title.trim()) {
    alert('请输入行程标题')
    return
  }
  if (!newSchedule.startDate || !newSchedule.startTime || !newSchedule.endDate || !newSchedule.endTime) {
    alert('请填写完整的开始和结束时间')
    return
  }

  // 验证时间逻辑
  const startDateTime = new Date(`${newSchedule.startDate}T${newSchedule.startTime}:00`)
  const endDateTime = new Date(`${newSchedule.endDate}T${newSchedule.endTime}:00`)
  
  if (isNaN(startDateTime.getTime()) || isNaN(endDateTime.getTime())) {
    alert('时间格式不正确，请重新选择')
    return
  }
  
  if (endDateTime <= startDateTime) {
    alert('结束时间必须晚于开始时间')
    return
  }

  try {
    creatingSchedule.value = true
    
    // 格式化为ISO 8601格式（后端LocalDateTime需要的格式）
    const startTimeStr = startDateTime.toISOString().slice(0, 19) // 格式：2024-01-20T14:00:00
    const endTimeStr = endDateTime.toISOString().slice(0, 19)
    
    console.log('创建团队行程:', {
      teamId: Number(teamInfo.id),
      title: newSchedule.title,
      startTime: startTimeStr,
      endTime: endTimeStr
    })
    
    const result = await createTeamSchedule({
      teamId: Number(teamInfo.id),
      title: newSchedule.title.trim(),
      description: newSchedule.description?.trim() || undefined,
      startTime: startTimeStr,
      endTime: endTimeStr,
      location: newSchedule.location?.trim() || undefined
    })

    console.log('创建成功，返回结果:', result)

    // 重置表单
    newSchedule.title = ''
    newSchedule.description = ''
    newSchedule.startDate = ''
    newSchedule.startTime = ''
    newSchedule.endDate = ''
    newSchedule.endTime = ''
    newSchedule.location = ''
    
    showCreateScheduleModal.value = false
    alert('行程创建成功！已自动添加到所有团队成员的个人行程表中')
    
    // 刷新团队行程列表
    await loadTeamSchedules()
  } catch (error: any) {
    console.error('创建行程失败:', error)
    const errorMessage = error?.response?.data?.msg || error?.message || '创建行程失败，请稍后重试'
    alert(errorMessage)
  } finally {
    creatingSchedule.value = false
  }
}

// 加载团队行程列表
const loadTeamSchedules = async () => {
  try {
    const teamId = Number(teamInfo.id)
    if (!teamId) return
    
    const result = await getTeamSchedules(teamId, { page: 1, pageSize: 100 })
    if (result.records) {
      teamSchedules.value = result.records.map((schedule: any) => {
        const startDate = new Date(schedule.startTime)
        const endDate = new Date(schedule.endTime)
        const dateStr = startDate.toISOString().split('T')[0]
        const dayNames = ['日', '一', '二', '三', '四', '五', '六']
        const day = dayNames[startDate.getDay()]
        const startTime = startDate.toTimeString().slice(0, 5)
        const endTime = endDate.toTimeString().slice(0, 5)
        
        return {
          id: schedule.id,
          title: schedule.title,
          description: schedule.description || '',
          date: dateStr,
          day: day,
          time: `${startTime}-${endTime}`,
          location: schedule.location || ''
        }
      })
      teamInfo.scheduleCount = result.total || teamSchedules.value.length
    }
  } catch (error: any) {
    console.error('加载团队行程失败:', error)
  }
}

// 加载团队信息
const loadTeamInfo = async (teamId: number) => {
  try {
    const team = await getTeamDetail(teamId)
    teamInfo.id = String(team.id)
    teamInfo.name = team.name
    teamInfo.description = team.description || ''
    teamInfo.memberCount = team.members?.length || 0
    teamInfo.inviteCode = team.inviteCode || ''
    teamInfo.creatorId = team.creatorId || 0
    teamInfo.userRole = (team as any).userRole || ''
    
    // 更新成员列表
    if (team.members) {
      members.value = team.members.map((m: any) => ({
        id: m.userId,
        name: m.userName || '未知用户',
        role: m.role === 'CREATOR' ? '创建者' : m.role === 'ADMIN' ? '管理员' : '成员'
      }))
    }
    
    // 加载团队行程列表
    await loadTeamSchedules()
  } catch (error: any) {
    console.error('加载团队信息失败:', error)
    alert(error?.message || '加载团队信息失败，请稍后重试')
  }
}

onMounted(() => {
  // 从路由参数获取团队ID
  const teamId = route.params.id as string
  if (teamId) {
    teamInfo.id = teamId
    loadTeamInfo(Number(teamId))
  }
})
</script>

<style scoped>
:root {
  --primary: #FF6B9D;
  --primary-dark: #C2185B;
  --bg: linear-gradient(135deg, #F5F7FA 0%, #E8F4F8 100%);
}

.team-detail-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 团队横幅区域 */
.team-hero-section {
  position: relative;
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  border-radius: 20px;
  padding: 50px 40px;
  margin: 30px 0;
  box-shadow: 0 10px 40px rgba(255, 107, 157, 0.3);
  overflow: hidden;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg"><defs><pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse"><path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="1"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.team-header-main {
  display: flex;
  align-items: flex-start;
  gap: 24px;
}

.team-avatar-large {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 48px;
  flex-shrink: 0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  position: relative;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.avatar-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 32px;
  height: 32px;
  background: #FFD700;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
  font-size: 16px;
  border: 3px solid white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.team-info-main {
  flex: 1;
  color: white;
}

.team-title-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.team-name {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin: 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.team-status-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.team-status-badge i {
  color: #FFD700;
}

.team-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 20px;
  line-height: 1.6;
}

.team-stats-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.stat-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-badge i {
  font-size: 16px;
}

.team-actions-main {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.btn-action-primary,
.btn-action-secondary,
.btn-action-icon {
  padding: 12px 24px;
  border-radius: 12px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-action-primary {
  background: white;
  color: #FF6B9D;
}

.btn-action-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.btn-action-secondary {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.btn-action-secondary:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.btn-action-icon {
  width: 44px;
  height: 44px;
  padding: 0;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.btn-action-icon:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

/* 标签导航栏 */
.tabs-section {
  background: white;
  border-radius: 16px;
  padding: 8px;
  margin-bottom: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 80px;
  z-index: 10;
}

.tabs-container {
  display: flex;
  gap: 8px;
}

.tab-item {
  flex: 1;
  padding: 12px 20px;
  border: none;
  background: transparent;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  position: relative;
}

.tab-item:hover {
  background: #f5f5f5;
  color: #FF6B9D;
}

.tab-item.active {
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.3);
}

.tab-badge {
  padding: 2px 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.tab-item.active .tab-badge {
  background: rgba(255, 255, 255, 0.3);
}

/* 内容区域 */
.content-area {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  margin-bottom: 40px;
}

.section-header-modern {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.header-left {
  flex: 1;
}

.section-title-modern {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title-modern i {
  color: #FF6B9D;
}

.section-subtitle {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.btn-create-modern {
  padding: 12px 24px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}

.btn-create-modern:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

/* 空状态 */
.empty-state-modern {
  text-align: center;
  padding: 80px 40px;
}

.empty-icon-wrapper {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #FFE5F1 0%, #FFB3D1 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon-wrapper i {
  font-size: 60px;
  color: #FF6B9D;
}

.empty-state-modern h3 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.empty-state-modern p {
  font-size: 16px;
  color: #999;
  margin-bottom: 32px;
}

.btn-primary-empty {
  padding: 12px 24px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}

.btn-primary-empty:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

/* 行程网格 */
.schedules-grid-modern {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.schedule-card-modern {
  background: white;
  border: 2px solid #f0f0f0;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  gap: 16px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.schedule-card-modern::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FF6B9D 0%, #C2185B 100%);
}

.schedule-card-modern:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255, 107, 157, 0.15);
  border-color: #FFB3D1;
}

.schedule-date-modern {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 70px;
  padding: 12px;
  background: linear-gradient(135deg, #FFF5F8 0%, #FFE5F1 100%);
  border-radius: 12px;
  flex-shrink: 0;
}

.date-main {
  font-size: 32px;
  font-weight: 700;
  color: #FF6B9D;
  line-height: 1;
}

.date-month {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.date-day {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

.schedule-content-modern {
  flex: 1;
  min-width: 0;
}

.schedule-header-modern {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.schedule-title-modern {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
  flex: 1;
}

.schedule-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  flex-shrink: 0;
}

.schedule-status i {
  font-size: 8px;
}

.schedule-status.past {
  background: #f5f5f5;
  color: #999;
}

.schedule-status.upcoming {
  background: #fff7e6;
  color: #fa8c16;
}

.schedule-status.future {
  background: #e6f7ff;
  color: #1890ff;
}

.schedule-desc-modern {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.schedule-meta-modern {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
}

.meta-item i {
  font-size: 14px;
  color: #FF6B9D;
}

.schedule-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.btn-action-mini {
  width: 32px;
  height: 32px;
  border: 2px solid #e0e0e0;
  background: white;
  border-radius: 8px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-action-mini:hover {
  border-color: #FF6B9D;
  color: #FF6B9D;
  background: #FFF5F8;
}

/* 成员网格 */
.members-grid-modern {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.member-card-modern {
  background: white;
  border: 2px solid #f0f0f0;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  transition: all 0.3s;
  position: relative;
}

.member-card-modern:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255, 107, 157, 0.15);
  border-color: #FFB3D1;
}

.member-avatar-modern {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  flex-shrink: 0;
  position: relative;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}

.role-badge {
  position: absolute;
  bottom: -4px;
  right: -4px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.role-badge.创建者,
.role-badge.creator {
  background: #FFD700;
  color: #333;
}

.role-badge.管理员,
.role-badge.admin {
  background: #1890ff;
}

.role-badge.成员,
.role-badge.member {
  background: #52c41a;
}

.member-info-modern {
  flex: 1;
  min-width: 0;
}

.member-name-modern {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 6px 0;
}

.member-role-modern {
  font-size: 14px;
  color: #999;
  margin: 0 0 12px 0;
}

.member-stats {
  display: flex;
  gap: 16px;
}

.member-stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.member-stat-item i {
  color: #FF6B9D;
}

.member-actions-modern {
  flex-shrink: 0;
}

.btn-remove-modern {
  width: 32px;
  height: 32px;
  border: 2px solid #ff4d4f;
  background: white;
  border-radius: 8px;
  color: #ff4d4f;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-remove-modern:hover {
  background: #ff4d4f;
  color: white;
}

/* 设置内容 */
.settings-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.settings-card {
  background: #f9f9f9;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid #f0f0f0;
}

.settings-card-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.settings-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group-modern {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group-modern label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.form-input-modern,
.form-textarea-modern {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s;
  font-family: inherit;
}

.form-input-modern:focus,
.form-textarea-modern:focus {
  outline: none;
  border-color: #FF6B9D;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.1);
}

.form-textarea-modern {
  resize: vertical;
  min-height: 100px;
}

.btn-save-settings {
  padding: 12px 24px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  align-self: flex-start;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}

.btn-save-settings:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

.team-info-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.team-header {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.team-avatar {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
  flex-shrink: 0;
}

.team-details {
  flex: 1;
}

.team-name {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.team-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
}

.team-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.stat-item i {
  color: var(--primary);
}

.team-actions {
  display: flex;
  gap: 10px;
}

.btn-invite,
.btn-settings {
  padding: 10px 20px;
  border-radius: 8px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-invite:hover,
.btn-settings:hover {
  background: #f9f0ff;
  border-color: var(--primary);
}

.members-section,
.schedules-section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.btn-create-schedule {
  padding: 10px 20px;
  border-radius: 8px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-create-schedule:hover {
  background: #f9f0ff;
  border-color: var(--primary);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.2);
}

.members-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.member-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9f0ff;
  border-radius: 8px;
  position: relative;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.member-info {
  flex: 1;
}

.member-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.member-role {
  font-size: 12px;
  color: #666;
}

.btn-remove-member {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: none;
  background: #ff4d4f;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  transition: all 0.3s;
}

.btn-remove-member:hover {
  transform: scale(1.1);
}

.schedules-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.schedule-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  display: flex;
  gap: 20px;
}

.schedule-date {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 80px;
  padding: 10px;
  background: #f9f0ff;
  border-radius: 8px;
}

.date {
  font-size: 20px;
  font-weight: bold;
  color: var(--primary);
}

.day {
  font-size: 12px;
  color: #666;
}

.schedule-content {
  flex: 1;
}

.schedule-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.schedule-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.schedule-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #999;
}

.schedule-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.empty-state {
  background: white;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.empty-state i {
  font-size: 48px;
  color: #ddd;
  margin-bottom: 15px;
}

.empty-state p {
  color: #666;
}

/* 创建行程模态框样式 */
.schedule-modal {
  max-width: 600px;
  width: 90%;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.form-group .required {
  color: #d81b60;
  margin-left: 4px;
}

.form-group input[type="text"],
.form-group input[type="date"],
.form-group input[type="time"],
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-group input[type="text"]:focus,
.form-group input[type="date"]:focus,
.form-group input[type="time"]:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--primary);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f0f7ff;
  border-radius: 8px;
  color: #1890ff;
  font-size: 13px;
  margin-top: 10px;
}

.form-tip i {
  font-size: 16px;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 30px;
  max-width: 500px;
  width: 90%;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.btn-close {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: #f5f5f5;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-close:hover {
  background: #ff4d4f;
  color: white;
}

.invite-code-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.code-display {
  display: flex;
  gap: 10px;
  align-items: center;
}

.code-text {
  flex: 1;
  padding: 12px 15px;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--primary);
  font-family: monospace;
}

.btn-copy {
  padding: 12px 20px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-copy:hover {
  background: #f9f0ff;
  border-color: var(--primary);
}

.code-tip {
  font-size: 12px;
  color: #999;
}

.btn-refresh {
  padding: 6px 12px;
  background: #f0f0f0;
  color: var(--text);
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.btn-refresh:hover {
  background: #e0e0e0;
  border-color: var(--primary);
  color: var(--primary);
}

.share-options {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.share-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: var(--text);
}

.share-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
  background: #f9f0ff;
}

.invite-methods {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.method-btn {
  flex: 1;
  padding: 10px;
  border: 2px solid #ddd;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  color: var(--text);
}

.method-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.method-btn.active {
  border-color: var(--primary);
  background: #f9f0ff;
  color: var(--primary);
}

.method-btn i {
  font-size: 20px;
}

@media (max-width: 768px) {
  .team-header {
    flex-direction: column;
  }

  .team-actions {
    width: 100%;
  }

  .btn-invite,
  .btn-settings {
    flex: 1;
  }

  .members-grid {
    grid-template-columns: 1fr;
  }

  .schedule-card {
    flex-direction: column;
  }
}
</style>
