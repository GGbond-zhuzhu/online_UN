<template>
  <div class="team-page">
    <NavBar />

    <div class="page-container">
      <!-- 页面标题 -->
      <section class="page-header">
        <h1 class="page-title">
          <i class="fas fa-users"></i> 团队行程
        </h1>
        <p class="page-subtitle">与团队成员共享行程，协作更高效</p>
      </section>

      <!-- 操作栏 -->
      <section class="actions-bar">
        <button class="btn-create" @click="showCreateModal = true">
          <i class="fas fa-plus"></i> 创建团队
        </button>
        <button class="btn-join" @click="showJoinModal = true">
          <i class="fas fa-user-plus"></i> 加入团队
        </button>
      </section>

      <!-- 我的团队列表 -->
      <section class="teams-section">
        <h2 class="section-title">我的团队</h2>
        <div v-if="myTeams.length === 0" class="empty-state">
          <i class="fas fa-users"></i>
          <h3>暂无团队</h3>
          <p>创建或加入团队，开始协作管理行程</p>
        </div>
        <div v-else class="teams-grid">
          <div
            v-for="team in myTeams"
            :key="team.id"
            class="team-card"
            @click="viewTeam(team.id)"
          >
            <div class="team-header">
              <div class="team-avatar">
                <i class="fas fa-users"></i>
              </div>
              <div class="team-info">
                <h3 class="team-name">{{ team.name }}</h3>
                <p class="team-desc">{{ team.description }}</p>
              </div>
            </div>
            <div class="team-stats">
              <div class="stat-item">
                <i class="fas fa-users"></i>
                <span>{{ team.memberCount }} 成员</span>
              </div>
              <div class="stat-item">
                <i class="fas fa-calendar"></i>
                <span>{{ team.scheduleCount }} 行程</span>
              </div>
            </div>
            <div class="team-actions">
              <button class="btn-action" @click.stop="editTeam(team.id)">
                <i class="fas fa-edit"></i> 编辑
              </button>
              <button class="btn-action danger" @click.stop="leaveTeam(team.id)">
                <i class="fas fa-sign-out-alt"></i> 退出
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- 团队邀请 -->
      <section class="invitations-section" v-if="invitations.length > 0">
        <h2 class="section-title">团队邀请</h2>
        <div class="invitations-list">
          <div
            v-for="invitation in invitations"
            :key="invitation.id"
            class="invitation-item"
          >
            <div class="invitation-info">
              <h3>{{ invitation.teamName }}</h3>
              <p>邀请人：{{ invitation.inviter }}</p>
              <p class="invitation-time">{{ invitation.time }}</p>
            </div>
            <div class="invitation-actions">
              <button class="btn-accept" @click="acceptInvitation(invitation.id)">
                <i class="fas fa-check"></i> 接受
              </button>
              <button class="btn-reject" @click="rejectInvitation(invitation.id)">
                <i class="fas fa-times"></i> 拒绝
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 创建团队模态框 -->
    <div v-if="showCreateModal" class="modal-overlay" @click="showCreateModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>创建团队</h2>
          <button class="btn-close" @click="showCreateModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <form @submit.prevent="createTeam" class="modal-form">
          <div class="form-group">
            <label class="form-label">团队名称 <span class="required">*</span></label>
            <input
              v-model="teamForm.name"
              type="text"
              class="form-input"
              placeholder="请输入团队名称"
              required
            />
          </div>
          <div class="form-group">
            <label class="form-label">团队描述</label>
            <textarea
              v-model="teamForm.description"
              class="form-textarea"
              placeholder="请输入团队描述（选填）"
              rows="3"
            ></textarea>
          </div>
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="showCreateModal = false">
              取消
            </button>
            <button type="submit" class="btn-submit">
              创建团队
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 加入团队模态框 -->
    <div v-if="showJoinModal" class="modal-overlay" @click="showJoinModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>加入团队</h2>
          <button class="btn-close" @click="showJoinModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <form @submit.prevent="joinTeam" class="modal-form">
          <div class="form-group">
            <label class="form-label">团队邀请码 <span class="required">*</span></label>
            <input
              v-model="joinCode"
              type="text"
              class="form-input"
              placeholder="请输入团队邀请码"
              required
            />
          </div>
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="showJoinModal = false">
              取消
            </button>
            <button type="submit" class="btn-submit">
              加入团队
            </button>
          </div>
        </form>
      </div>
    </div>

    <FloatingMenu />
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()

const showCreateModal = ref(false)
const showJoinModal = ref(false)
const joinCode = ref('')

// 我的团队
const myTeams = ref([
  {
    id: 1,
    name: '项目组A',
    description: '软件开发项目组',
    memberCount: 5,
    scheduleCount: 12
  },
  {
    id: 2,
    name: '学习小组',
    description: '高等数学学习小组',
    memberCount: 8,
    scheduleCount: 20
  }
])

// 团队邀请
const invitations = ref([
  {
    id: 1,
    teamName: '设计团队',
    inviter: '李同学',
    time: '2小时前'
  }
])

// 创建团队表单
const teamForm = reactive({
  name: '',
  description: ''
})

// 查看团队
const viewTeam = (id: number) => {
  // 跳转到团队详情页
  router.push(`/schedule/team/${id}`)
}

// 编辑团队
const editTeam = (id: number) => {
  const team = myTeams.value.find(t => t.id === id)
  if (team) {
    teamForm.name = team.name
    teamForm.description = team.description
    showCreateModal.value = true
    // TODO: 设置为编辑模式
  }
}

// 退出团队
const leaveTeam = (id: number) => {
  if (confirm('确定要退出这个团队吗？')) {
    const index = myTeams.value.findIndex(t => t.id === id)
    if (index > -1) {
      myTeams.value.splice(index, 1)
    }
  }
}

// 创建团队
const createTeam = () => {
  if (!teamForm.name.trim()) {
    alert('请输入团队名称')
    return
  }

  try {
    // TODO: 调用创建团队API
    // const result = await createTeamAPI(teamForm)
    // 生成邀请码（实际应该由后端生成）
    const inviteCode = 'TEAM' + Date.now().toString(36).toUpperCase()
    
    const newTeam = {
      id: Date.now(),
      name: teamForm.name,
      description: teamForm.description,
      memberCount: 1,
      scheduleCount: 0,
      inviteCode: inviteCode
    }
    
    myTeams.value.push(newTeam)
    
    teamForm.name = ''
    teamForm.description = ''
    showCreateModal.value = false
    alert(`团队创建成功！\n邀请码：${inviteCode}\n请保存邀请码以便邀请成员加入`)
  } catch (error) {
    console.error('创建团队失败:', error)
    alert('创建失败，请稍后重试')
  }
}

// 加入团队
const joinTeam = () => {
  if (!joinCode.value.trim()) {
    alert('请输入团队邀请码')
    return
  }

  try {
    // TODO: 调用加入团队API
    // await joinTeamAPI(joinCode.value)
    
    alert('加入团队成功')
    joinCode.value = ''
    showJoinModal.value = false
    // 刷新团队列表
  } catch (error) {
    console.error('加入团队失败:', error)
    alert('加入失败，请检查邀请码是否正确')
  }
}

// 接受邀请
const acceptInvitation = (id: number) => {
  const invitation = invitations.value.find(inv => inv.id === id)
  if (invitation) {
    // TODO: 调用接受邀请API
    // await acceptInvitationAPI(id)
    
    myTeams.value.push({
      id: Date.now(),
      name: invitation.teamName,
      description: '',
      memberCount: 0,
      scheduleCount: 0
    })
    
    const index = invitations.value.findIndex(inv => inv.id === id)
    if (index > -1) {
      invitations.value.splice(index, 1)
    }
  }
}

// 拒绝邀请
const rejectInvitation = (id: number) => {
  if (confirm('确定要拒绝这个邀请吗？')) {
    // TODO: 调用拒绝邀请API
    // await rejectInvitationAPI(id)
    
    const index = invitations.value.findIndex(inv => inv.id === id)
    if (index > -1) {
      invitations.value.splice(index, 1)
    }
  }
}

onMounted(() => {
  // 加载团队列表和邀请
  // loadTeams()
  // loadInvitations()
})
</script>

<style scoped>
:root {
  --primary: #d81b60;
  --primary-dark: #c2185b;
  --text: #333;
  --muted: #666;
  --bg: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.team-page {
  min-height: 100vh;
  background: var(--bg);
  padding-bottom: 40px;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 30px;
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

/* 操作栏 */
.actions-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  justify-content: flex-end;
}

.btn-create,
.btn-join {
  padding: 12px 24px;
  border-radius: 12px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-create {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
}

.btn-create:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
}

.btn-join {
  background: white;
  color: var(--primary);
  border: 2px solid rgba(240, 240, 240, 0.9);
}

.btn-join:hover {
  background: #f9f0ff;
  border-color: var(--primary);
}

/* 团队列表 */
.teams-section {
  margin-bottom: 30px;
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  color: var(--text);
  margin-bottom: 20px;
}

.empty-state {
  background: white;
  border-radius: 12px;
  padding: 60px 20px;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.empty-state i {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 20px;
  color: var(--text);
  margin-bottom: 10px;
}

.empty-state p {
  color: var(--muted);
}

.teams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.team-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s ease;
}

.team-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(216, 27, 96, 0.15);
}

.team-header {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.team-avatar {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.team-info {
  flex: 1;
}

.team-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}

.team-desc {
  font-size: 14px;
  color: var(--muted);
}

.team-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--muted);
}

.stat-item i {
  color: var(--primary);
}

.team-actions {
  display: flex;
  gap: 10px;
}

.btn-action {
  flex: 1;
  padding: 8px 16px;
  border-radius: 8px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  background: white;
  color: var(--primary);
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.btn-action:hover {
  background: var(--primary);
  color: white;
  border-color: var(--primary);
}

.btn-action.danger {
  color: #ff4d4f;
  border-color: rgba(255, 77, 79, 0.3);
}

.btn-action.danger:hover {
  background: #ff4d4f;
  color: white;
  border-color: #ff4d4f;
}

/* 邀请列表 */
.invitations-section {
  margin-bottom: 30px;
}

.invitations-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.invitation-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.invitation-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 6px;
}

.invitation-info p {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 4px;
}

.invitation-time {
  font-size: 12px;
  color: var(--muted);
}

.invitation-actions {
  display: flex;
  gap: 10px;
}

.btn-accept,
.btn-reject {
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-accept {
  background: #52c41a;
  color: white;
}

.btn-accept:hover {
  background: #73d13d;
}

.btn-reject {
  background: #f5f5f5;
  color: var(--muted);
}

.btn-reject:hover {
  background: #ff4d4f;
  color: white;
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
  max-height: 90vh;
  overflow-y: auto;
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
  color: var(--text);
}

.btn-close {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: #f5f5f5;
  color: var(--muted);
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

.modal-form {
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
.form-textarea {
  padding: 12px 15px;
  border: 2px solid rgba(240, 240, 240, 0.9);
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
  font-family: inherit;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(216, 27, 96, 0.1);
}

.form-textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 10px;
}

.btn-cancel,
.btn-submit {
  flex: 1;
  padding: 12px 20px;
  border-radius: 8px;
  border: none;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: #f5f5f5;
  color: var(--muted);
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-submit {
  background: linear-gradient(135deg, #d81b60 0%, #c2185b 100%);
  color: white;
}

.btn-submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3);
}

@media (max-width: 768px) {
  .actions-bar {
    flex-direction: column;
  }

  .teams-grid {
    grid-template-columns: 1fr;
  }

  .invitation-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .invitation-actions {
    width: 100%;
  }

  .btn-accept,
  .btn-reject {
    flex: 1;
  }
}
</style>
