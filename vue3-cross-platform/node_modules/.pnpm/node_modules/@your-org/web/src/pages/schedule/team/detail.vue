<template>
  <div class="team-detail-page">
    <NavBar />

    <div class="page-container">
      <!-- 团队信息 -->
      <section class="team-info-section">
        <div class="team-header">
          <div class="team-avatar">
            <i class="fas fa-users"></i>
          </div>
          <div class="team-details">
            <h1 class="team-name">{{ teamInfo.name }}</h1>
            <p class="team-desc">{{ teamInfo.description }}</p>
            <div class="team-stats">
              <div class="stat-item">
                <i class="fas fa-users"></i>
                <span>{{ teamInfo.memberCount }} 成员</span>
              </div>
              <div class="stat-item">
                <i class="fas fa-calendar"></i>
                <span>{{ teamInfo.scheduleCount }} 行程</span>
              </div>
            </div>
          </div>
          <div class="team-actions">
            <button class="btn-invite" @click="showInviteModal = true">
              <i class="fas fa-user-plus"></i> 邀请码
            </button>
            <button class="btn-invite" @click="showSendInviteModal = true">
              <i class="fas fa-paper-plane"></i> 发送邀请
            </button>
            <button class="btn-settings" @click="editTeam">
              <i class="fas fa-cog"></i> 设置
            </button>
          </div>
        </div>
      </section>

      <!-- 成员列表 -->
      <section class="members-section">
        <h2 class="section-title">团队成员</h2>
        <div class="members-grid">
          <div
            v-for="member in members"
            :key="member.id"
            class="member-card"
          >
            <div class="member-avatar">
              <i class="fas fa-user"></i>
            </div>
            <div class="member-info">
              <h3 class="member-name">{{ member.name }}</h3>
              <p class="member-role">{{ member.role }}</p>
            </div>
            <button
              v-if="member.id !== currentUserId"
              class="btn-remove-member"
              @click="removeMember(member.id)"
            >
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
      </section>

      <!-- 团队行程 -->
      <section class="schedules-section">
        <div class="section-header">
          <h2 class="section-title">团队行程</h2>
          <button 
            v-if="isCreator" 
            class="btn-create-schedule" 
            @click="showCreateScheduleModal = true"
          >
            <i class="fas fa-plus"></i> 创建行程
          </button>
        </div>
        <div v-if="teamSchedules.length === 0" class="empty-state">
          <i class="fas fa-calendar"></i>
          <p>暂无团队行程</p>
        </div>
        <div v-else class="schedules-list">
          <div
            v-for="schedule in teamSchedules"
            :key="schedule.id"
            class="schedule-card"
          >
            <div class="schedule-date">
              <span class="date">{{ schedule.date }}</span>
              <span class="day">{{ schedule.day }}</span>
            </div>
            <div class="schedule-content">
              <h3 class="schedule-title">{{ schedule.title }}</h3>
              <p class="schedule-desc">{{ schedule.description }}</p>
              <div class="schedule-meta">
                <span class="time">
                  <i class="fas fa-clock"></i> {{ schedule.time }}
                </span>
                <span class="location" v-if="schedule.location">
                  <i class="fas fa-map-marker-alt"></i> {{ schedule.location }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </section>
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
  router.push('/schedule/team')
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
  --primary: #d81b60;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.team-detail-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
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
