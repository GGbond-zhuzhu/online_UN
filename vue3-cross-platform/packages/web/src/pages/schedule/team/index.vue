<template>
  <div class="team-page">
    <NavBar />

    <div class="page-container">
      <!-- 顶部横幅区域 -->
      <section class="hero-section">
        <div class="hero-content">
          <div class="hero-text">
            <h1 class="hero-title">
              <i class="fas fa-users"></i>
              团队行程
            </h1>
            <p class="hero-subtitle">与团队成员共享行程，协作更高效</p>
          </div>
          <div class="hero-actions">
            <button class="btn-primary-large" @click="showCreateModal = true">
              <i class="fas fa-plus"></i>
              <span>创建团队</span>
            </button>
            <button class="btn-secondary-large" @click="showJoinModal = true">
              <i class="fas fa-user-plus"></i>
              <span>加入团队</span>
            </button>
          </div>
        </div>
      </section>

      <!-- 搜索和筛选栏 -->
      <section class="search-filter-section">
        <div class="search-box-wrapper">
          <div class="search-box">
            <i class="fas fa-search search-icon"></i>
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索团队名称、描述..."
              class="search-input"
            />
            <button v-if="searchKeyword" class="search-clear" @click="searchKeyword = ''">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
        <div class="filter-tabs">
          <button
            class="filter-tab"
            :class="{ active: filterType === 'all' }"
            @click="filterType = 'all'"
          >
            <i class="fas fa-th"></i>
            <span>全部</span>
          </button>
          <button
            class="filter-tab"
            :class="{ active: filterType === 'my' }"
            @click="filterType = 'my'"
          >
            <i class="fas fa-user"></i>
            <span>我创建的</span>
          </button>
          <button
            class="filter-tab"
            :class="{ active: filterType === 'joined' }"
            @click="filterType = 'joined'"
          >
            <i class="fas fa-user-check"></i>
            <span>我加入的</span>
          </button>
        </div>
      </section>

      <!-- 团队邀请通知 -->
      <section class="invitations-banner" v-if="invitations.length > 0">
        <div class="banner-content">
          <div class="banner-icon">
            <i class="fas fa-bell"></i>
          </div>
          <div class="banner-text">
            <strong>您有 {{ invitations.length }} 个团队邀请</strong>
            <span>点击查看详情</span>
          </div>
          <button class="banner-action" @click="showInvitations = !showInvitations">
            <i class="fas fa-chevron-down" :class="{ rotate: showInvitations }"></i>
          </button>
        </div>
        <div class="invitations-dropdown" v-if="showInvitations">
          <div
            v-for="invitation in invitations"
            :key="invitation.id"
            class="invitation-card"
          >
            <div class="invitation-header">
              <div class="invitation-team-info">
                <div class="team-avatar-small">
                  <i class="fas fa-users"></i>
                </div>
                <div>
                  <h4>{{ invitation.teamName }}</h4>
                  <p>邀请人：{{ invitation.inviter }} · {{ invitation.time }}</p>
                </div>
              </div>
            </div>
            <div class="invitation-actions">
              <button class="btn-accept-small" @click="acceptInvitation(invitation.id)">
                <i class="fas fa-check"></i>
                接受
              </button>
              <button class="btn-reject-small" @click="rejectInvitation(invitation.id)">
                <i class="fas fa-times"></i>
                拒绝
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- 我的团队列表 -->
      <section class="teams-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="fas fa-layer-group"></i>
            我的团队
            <span class="team-count">({{ filteredTeams.length }})</span>
          </h2>
          <div class="view-toggle">
            <button
              class="view-btn"
              :class="{ active: viewMode === 'grid' }"
              @click="viewMode = 'grid'"
              title="网格视图"
            >
              <i class="fas fa-th"></i>
            </button>
            <button
              class="view-btn"
              :class="{ active: viewMode === 'list' }"
              @click="viewMode = 'list'"
              title="列表视图"
            >
              <i class="fas fa-list"></i>
            </button>
          </div>
        </div>

        <div v-if="filteredTeams.length === 0" class="empty-state">
          <div class="empty-icon-wrapper">
            <i class="fas fa-users"></i>
          </div>
          <h3>暂无团队</h3>
          <p>创建或加入团队，开始协作管理行程</p>
          <div class="empty-actions">
            <button class="btn-primary" @click="showCreateModal = true">
              <i class="fas fa-plus"></i>
              创建团队
            </button>
            <button class="btn-secondary" @click="showJoinModal = true">
              <i class="fas fa-user-plus"></i>
              加入团队
            </button>
          </div>
        </div>

        <div v-else :class="['teams-container', viewMode]">
          <div
            v-for="team in filteredTeams"
            :key="team.id"
            class="team-card"
            @click="viewTeam(team.id)"
          >
            <div class="team-card-header">
              <div class="team-avatar-large">
                <i class="fas fa-users"></i>
                <div class="avatar-badge" v-if="team.isCreator">
                  <i class="fas fa-crown"></i>
                </div>
              </div>
              <div class="team-meta">
                <h3 class="team-name">{{ team.name }}</h3>
                <p class="team-desc">{{ team.description || '暂无描述' }}</p>
                <div class="team-tags" v-if="team.tags && team.tags.length > 0">
                  <span
                    v-for="tag in team.tags"
                    :key="tag"
                    class="team-tag"
                  >
                    {{ tag }}
                  </span>
                </div>
              </div>
              <div class="team-menu" @click.stop>
                <button class="menu-btn" @click="showTeamMenu(team.id)">
                  <i class="fas fa-ellipsis-v"></i>
                </button>
                <div
                  v-if="activeMenuTeamId === team.id"
                  class="menu-dropdown"
                  @click.stop
                >
                  <button @click="editTeam(team.id)">
                    <i class="fas fa-edit"></i>
                    编辑团队
                  </button>
                  <button @click="manageMembers(team.id)">
                    <i class="fas fa-user-cog"></i>
                    管理成员
                  </button>
                  <button @click="viewSettings(team.id)">
                    <i class="fas fa-cog"></i>
                    团队设置
                  </button>
                  <div class="menu-divider"></div>
                  <button class="danger" @click="leaveTeam(team.id)">
                    <i class="fas fa-sign-out-alt"></i>
                    退出团队
                  </button>
                </div>
              </div>
            </div>

            <div class="team-stats-grid">
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="fas fa-users"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ team.memberCount }}</div>
                  <div class="stat-label">成员</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="fas fa-calendar-check"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ team.scheduleCount }}</div>
                  <div class="stat-label">行程</div>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <i class="fas fa-clock"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ team.upcomingCount || 0 }}</div>
                  <div class="stat-label">即将开始</div>
                </div>
              </div>
            </div>

            <div class="team-members-preview">
              <div class="members-avatars">
                <div
                  v-for="(member, idx) in team.recentMembers"
                  :key="member.id || idx"
                  class="member-avatar-mini"
                  :title="member.name"
                >
                  <i class="fas fa-user"></i>
                </div>
                <div v-if="team.memberCount > 3" class="member-avatar-more">
                  +{{ team.memberCount - 3 }}
                </div>
              </div>
              <div class="team-activity">
                <i class="fas fa-circle" :class="team.isActive ? 'active' : 'inactive'"></i>
                <span>{{ team.lastActivity || '最近活跃' }}</span>
              </div>
            </div>

            <div class="team-card-footer">
              <div class="team-actions">
                <button class="btn-action-primary" @click.stop="viewTeam(team.id)">
                  <i class="fas fa-arrow-right"></i>
                  查看详情
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- 创建团队模态框 -->
    <div v-if="showCreateModal" class="modal-overlay" @click="showCreateModal = false">
      <div class="modal-content modal-large" @click.stop>
        <div class="modal-header">
          <h2>
            <i class="fas fa-plus-circle"></i>
            创建团队
          </h2>
          <button class="btn-close" @click="showCreateModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <form @submit.prevent="createTeam" class="modal-form">
          <div class="form-group">
            <label class="form-label">
              团队名称 <span class="required">*</span>
            </label>
            <input
              v-model="teamForm.name"
              type="text"
              class="form-input"
              placeholder="请输入团队名称（2-30个字符）"
              maxlength="30"
              required
            />
            <div class="form-hint">团队名称将显示在所有成员面前</div>
          </div>
          <div class="form-group">
            <label class="form-label">团队描述</label>
            <textarea
              v-model="teamForm.description"
              class="form-textarea"
              placeholder="请输入团队描述（选填，最多200字）"
              rows="4"
              maxlength="200"
            ></textarea>
            <div class="form-hint">{{ teamForm.description.length }}/200</div>
          </div>
          <div class="form-group">
            <label class="form-label">团队标签</label>
            <div class="tags-input-wrapper">
              <div class="tags-display">
                <span
                  v-for="(tag, idx) in teamForm.tags"
                  :key="idx"
                  class="tag-item"
                >
                  {{ tag }}
                  <button type="button" @click="removeTag(idx)" class="tag-remove">
                    <i class="fas fa-times"></i>
                  </button>
                </span>
              </div>
              <input
                v-model="newTag"
                type="text"
                class="tag-input"
                placeholder="输入标签后按回车添加"
                @keyup.enter="addTag"
                maxlength="10"
              />
            </div>
            <div class="form-hint">添加标签便于分类管理（最多5个）</div>
          </div>
          <div class="form-group">
            <label class="form-label">团队权限设置</label>
            <div class="permission-options">
              <label class="permission-item">
                <input
                  type="radio"
                  v-model="teamForm.permission"
                  value="creator"
                />
                <div class="permission-content">
                  <div class="permission-title">
                    <i class="fas fa-crown"></i>
                    仅创建者可管理
                  </div>
                  <div class="permission-desc">只有创建者可以添加/删除成员和管理设置</div>
                </div>
              </label>
              <label class="permission-item">
                <input
                  type="radio"
                  v-model="teamForm.permission"
                  value="admin"
                />
                <div class="permission-content">
                  <div class="permission-title">
                    <i class="fas fa-user-shield"></i>
                    管理员可管理
                  </div>
                  <div class="permission-desc">创建者和管理员都可以管理团队</div>
                </div>
              </label>
            </div>
          </div>
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="showCreateModal = false">
              取消
            </button>
            <button type="submit" class="btn-submit" :disabled="creating">
              <i v-if="creating" class="fas fa-spinner fa-spin"></i>
              <span v-else><i class="fas fa-check"></i> 创建团队</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 加入团队模态框 -->
    <div v-if="showJoinModal" class="modal-overlay" @click="showJoinModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>
            <i class="fas fa-user-plus"></i>
            加入团队
          </h2>
          <button class="btn-close" @click="showJoinModal = false">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <form @submit.prevent="joinTeam" class="modal-form">
          <div class="form-group">
            <label class="form-label">
              团队邀请码 <span class="required">*</span>
            </label>
            <input
              v-model="joinCode"
              type="text"
              class="form-input form-input-large"
              placeholder="请输入团队邀请码"
              required
            />
            <div class="form-hint">邀请码通常由团队创建者提供</div>
          </div>
          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="showJoinModal = false">
              取消
            </button>
            <button type="submit" class="btn-submit" :disabled="joining">
              <i v-if="joining" class="fas fa-spinner fa-spin"></i>
              <span v-else><i class="fas fa-check"></i> 加入团队</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <FloatingMenu />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()

const showCreateModal = ref(false)
const showJoinModal = ref(false)
const showInvitations = ref(false)
const searchKeyword = ref('')
const filterType = ref<'all' | 'my' | 'joined'>('all')
const viewMode = ref<'grid' | 'list'>('grid')
const activeMenuTeamId = ref<number | null>(null)
const creating = ref(false)
const joining = ref(false)
const newTag = ref('')

// 我的团队
const myTeams = ref([
  {
    id: 1,
    name: '项目组A',
    description: '软件开发项目组，专注于校园管理系统开发',
    memberCount: 5,
    scheduleCount: 12,
    upcomingCount: 3,
    isCreator: true,
    isActive: true,
    lastActivity: '2小时前',
    tags: ['项目', '开发'],
    recentMembers: [
      { id: 1, name: '张同学' },
      { id: 2, name: '李同学' },
      { id: 3, name: '王同学' }
    ]
  },
  {
    id: 2,
    name: '学习小组',
    description: '高等数学学习小组，每周定期讨论',
    memberCount: 8,
    scheduleCount: 20,
    upcomingCount: 5,
    isCreator: false,
    isActive: true,
    lastActivity: '1天前',
    tags: ['学习', '数学'],
    recentMembers: [
      { id: 4, name: '赵同学' },
      { id: 5, name: '孙同学' },
      { id: 6, name: '周同学' }
    ]
  },
  {
    id: 3,
    name: '设计团队',
    description: 'UI/UX设计团队，负责产品界面设计',
    memberCount: 4,
    scheduleCount: 8,
    upcomingCount: 2,
    isCreator: true,
    isActive: false,
    lastActivity: '3天前',
    tags: ['设计', 'UI'],
    recentMembers: [
      { id: 7, name: '吴同学' },
      { id: 8, name: '郑同学' }
    ]
  }
])

// 团队邀请
const invitations = ref([
  {
    id: 1,
    teamName: '算法竞赛组',
    inviter: '陈同学',
    time: '2小时前'
  },
  {
    id: 2,
    teamName: '英语角',
    inviter: '刘同学',
    time: '1天前'
  }
])

// 创建团队表单
const teamForm = reactive({
  name: '',
  description: '',
  tags: [] as string[],
  permission: 'creator'
})

// 加入团队
const joinCode = ref('')

// 过滤后的团队列表
const filteredTeams = computed(() => {
  let teams = myTeams.value

  // 搜索过滤
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    teams = teams.filter(team =>
      team.name.toLowerCase().includes(keyword) ||
      team.description?.toLowerCase().includes(keyword) ||
      team.tags?.some(tag => tag.toLowerCase().includes(keyword))
    )
  }

  // 类型过滤
  if (filterType.value === 'my') {
    teams = teams.filter(team => team.isCreator)
  } else if (filterType.value === 'joined') {
    teams = teams.filter(team => !team.isCreator)
  }

  return teams
})

// 查看团队
const viewTeam = (id: number) => {
  router.push(`/schedule/team/${id}`)
}

// 编辑团队
const editTeam = (id: number) => {
  const team = myTeams.value.find(t => t.id === id)
  if (team) {
    teamForm.name = team.name
    teamForm.description = team.description || ''
    teamForm.tags = [...(team.tags || [])]
    activeMenuTeamId.value = null
    showCreateModal.value = true
    // TODO: 设置为编辑模式
  }
}

// 退出团队
const leaveTeam = (id: number) => {
  if (confirm('确定要退出这个团队吗？退出后将无法查看团队行程。')) {
    const index = myTeams.value.findIndex(t => t.id === id)
    if (index > -1) {
      myTeams.value.splice(index, 1)
    }
    activeMenuTeamId.value = null
  }
}

// 管理成员
const manageMembers = (id: number) => {
  activeMenuTeamId.value = null
  router.push(`/schedule/team/${id}?tab=members`)
}

// 查看设置
const viewSettings = (id: number) => {
  activeMenuTeamId.value = null
  router.push(`/schedule/team/${id}?tab=settings`)
}

// 显示团队菜单
const showTeamMenu = (id: number) => {
  activeMenuTeamId.value = activeMenuTeamId.value === id ? null : id
}

// 添加标签
const addTag = () => {
  if (newTag.value.trim() && teamForm.tags.length < 5) {
    if (!teamForm.tags.includes(newTag.value.trim())) {
      teamForm.tags.push(newTag.value.trim())
      newTag.value = ''
    }
  }
}

// 移除标签
const removeTag = (index: number) => {
  teamForm.tags.splice(index, 1)
}

// 创建团队
const createTeam = async () => {
  if (!teamForm.name.trim()) {
    alert('请输入团队名称')
    return
  }

  if (teamForm.name.trim().length < 2) {
    alert('团队名称至少需要2个字符')
    return
  }

  try {
    creating.value = true
    // TODO: 调用创建团队API
    // const result = await createTeamAPI(teamForm)
    
    // 生成邀请码（实际应该由后端生成）
    const inviteCode = 'TEAM' + Date.now().toString(36).toUpperCase()
    
    const newTeam = {
      id: Date.now(),
      name: teamForm.name.trim(),
      description: teamForm.description.trim(),
      memberCount: 1,
      scheduleCount: 0,
      upcomingCount: 0,
      isCreator: true,
      isActive: true,
      lastActivity: '刚刚',
      tags: [...teamForm.tags],
      recentMembers: []
    }
    
    myTeams.value.unshift(newTeam)
    
    // 重置表单
    teamForm.name = ''
    teamForm.description = ''
    teamForm.tags = []
    teamForm.permission = 'creator'
    newTag.value = ''
    showCreateModal.value = false
    
    alert(`团队创建成功！\n邀请码：${inviteCode}\n请保存邀请码以便邀请成员加入`)
  } catch (error) {
    console.error('创建团队失败:', error)
    alert('创建失败，请稍后重试')
  } finally {
    creating.value = false
  }
}

// 加入团队
const joinTeam = async () => {
  if (!joinCode.value.trim()) {
    alert('请输入团队邀请码')
    return
  }

  try {
    joining.value = true
    // TODO: 调用加入团队API
    // await joinTeamAPI(joinCode.value)
    
    alert('加入团队成功')
    joinCode.value = ''
    showJoinModal.value = false
    // 刷新团队列表
  } catch (error) {
    console.error('加入团队失败:', error)
    alert('加入失败，请检查邀请码是否正确')
  } finally {
    joining.value = false
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
      scheduleCount: 0,
      upcomingCount: 0,
      isCreator: false,
      isActive: true,
      lastActivity: '刚刚',
      tags: [],
      recentMembers: []
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

// 点击外部关闭菜单
const handleClickOutside = (event: MouseEvent) => {
  if (activeMenuTeamId.value !== null) {
    activeMenuTeamId.value = null
  }
}

onMounted(() => {
  // 加载团队列表和邀请
  // loadTeams()
  // loadInvitations()
  document.addEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* 页面基础样式 */
.team-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #F5F7FA 0%, #E8F4F8 100%);
  padding-bottom: 40px;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 顶部横幅区域 */
.hero-section {
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  border-radius: 20px;
  padding: 60px 40px;
  margin: 30px 0;
  box-shadow: 0 10px 40px rgba(255, 107, 157, 0.3);
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.hero-text {
  flex: 1;
}

.hero-title {
  font-size: 42px;
  font-weight: 700;
  color: white;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.hero-title i {
  font-size: 36px;
}

.hero-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.btn-primary-large,
.btn-secondary-large {
  padding: 14px 28px;
  border-radius: 12px;
  border: none;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-primary-large {
  background: white;
  color: #FF6B9D;
}

.btn-primary-large:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.btn-secondary-large {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.btn-secondary-large:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

/* 搜索和筛选栏 */
.search-filter-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.search-box-wrapper {
  margin-bottom: 16px;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 16px;
  color: #999;
  font-size: 16px;
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: 12px 16px 12px 48px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #FF6B9D;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.1);
}

.search-clear {
  position: absolute;
  right: 12px;
  width: 24px;
  height: 24px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.search-clear:hover {
  background: #e0e0e0;
  color: #666;
}

.filter-tabs {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.filter-tab {
  padding: 8px 20px;
  border: 2px solid #e0e0e0;
  background: white;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.filter-tab:hover {
  border-color: #FF6B9D;
  color: #FF6B9D;
}

.filter-tab.active {
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  border-color: #FF6B9D;
  color: white;
  box-shadow: 0 2px 8px rgba(255, 107, 157, 0.3);
}

/* 团队邀请通知 */
.invitations-banner {
  background: linear-gradient(135deg, #FFF3E0 0%, #FFE0B2 100%);
  border-left: 4px solid #FF9800;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(255, 152, 0, 0.15);
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.banner-icon {
  width: 40px;
  height: 40px;
  background: #FF9800;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  flex-shrink: 0;
}

.banner-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.banner-text strong {
  font-size: 16px;
  color: #E65100;
}

.banner-text span {
  font-size: 13px;
  color: #F57C00;
}

.banner-action {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 152, 0, 0.1);
  border-radius: 50%;
  color: #FF9800;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.banner-action:hover {
  background: rgba(255, 152, 0, 0.2);
}

.banner-action i.rotate {
  transform: rotate(180deg);
}

.invitations-dropdown {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 152, 0, 0.2);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.invitation-card {
  background: white;
  border-radius: 10px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.invitation-team-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.team-avatar-small {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.invitation-team-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.invitation-team-info p {
  font-size: 13px;
  color: #999;
  margin: 0;
}

.invitation-actions {
  display: flex;
  gap: 8px;
}

.btn-accept-small,
.btn-reject-small {
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-accept-small {
  background: #52c41a;
  color: white;
}

.btn-accept-small:hover {
  background: #73d13d;
}

.btn-reject-small {
  background: #f5f5f5;
  color: #666;
}

.btn-reject-small:hover {
  background: #ff4d4f;
  color: white;
}

/* 团队列表区域 */
.teams-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
}

.section-title i {
  color: #FF6B9D;
}

.team-count {
  font-size: 16px;
  font-weight: 400;
  color: #999;
}

.view-toggle {
  display: flex;
  gap: 8px;
  background: #f5f5f5;
  padding: 4px;
  border-radius: 8px;
}

.view-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 6px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.view-btn:hover {
  background: white;
  color: #FF6B9D;
}

.view-btn.active {
  background: white;
  color: #FF6B9D;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 空状态 */
.empty-state {
  background: white;
  border-radius: 16px;
  padding: 80px 40px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
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

.empty-state h3 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.empty-state p {
  font-size: 16px;
  color: #999;
  margin-bottom: 32px;
}

.empty-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.btn-primary,
.btn-secondary {
  padding: 12px 24px;
  border-radius: 10px;
  border: none;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

.btn-secondary {
  background: white;
  color: #FF6B9D;
  border: 2px solid #FF6B9D;
}

.btn-secondary:hover {
  background: #FFE5F1;
}

/* 团队卡片容器 */
.teams-container.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 24px;
}

.teams-container.list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 团队卡片 */
.team-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.team-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FF6B9D 0%, #C2185B 100%);
}

.team-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255, 107, 157, 0.2);
  border-color: #FFB3D1;
}

.teams-container.list .team-card {
  display: flex;
  align-items: center;
  gap: 24px;
}

.team-card-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 20px;
  position: relative;
}

.teams-container.list .team-card-header {
  flex: 1;
  margin-bottom: 0;
}

.team-avatar-large {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  flex-shrink: 0;
  position: relative;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}

.avatar-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 24px;
  height: 24px;
  background: #FFD700;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
  font-size: 12px;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.team-meta {
  flex: 1;
  min-width: 0;
}

.team-name {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.team-desc {
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

.team-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.team-tag {
  padding: 4px 10px;
  background: #FFE5F1;
  color: #FF6B9D;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.team-menu {
  position: relative;
}

.menu-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f5f5;
  border-radius: 6px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.menu-btn:hover {
  background: #e0e0e0;
  color: #FF6B9D;
}

.menu-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 8px;
  min-width: 180px;
  z-index: 100;
  border: 1px solid #f0f0f0;
}

.menu-dropdown button {
  width: 100%;
  padding: 10px 14px;
  border: none;
  background: transparent;
  text-align: left;
  border-radius: 6px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: all 0.2s;
}

.menu-dropdown button:hover {
  background: #f5f5f5;
  color: #FF6B9D;
}

.menu-dropdown button.danger {
  color: #ff4d4f;
}

.menu-dropdown button.danger:hover {
  background: #fff1f0;
}

.menu-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 6px 0;
}

/* 统计数据网格 */
.team-stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: linear-gradient(135deg, #FFF5F8 0%, #FFE5F1 100%);
  border-radius: 12px;
}

.teams-container.list .team-stats-grid {
  width: 300px;
  flex-shrink: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

/* 成员预览 */
.team-members-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.members-avatars {
  display: flex;
  align-items: center;
  gap: -8px;
}

.member-avatar-mini {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
  border: 2px solid white;
  margin-left: -8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.member-avatar-mini:first-child {
  margin-left: 0;
}

.member-avatar-more {
  width: 32px;
  height: 32px;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 12px;
  font-weight: 600;
  border: 2px solid white;
  margin-left: -8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.team-activity {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #999;
}

.team-activity i {
  font-size: 8px;
}

.team-activity i.active {
  color: #52c41a;
}

.team-activity i.inactive {
  color: #d9d9d9;
}

/* 卡片底部操作 */
.team-card-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.btn-action-primary {
  width: 100%;
  padding: 10px;
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-action-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}

/* 模态框样式 */
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
  backdrop-filter: blur(4px);
}

.modal-content {
  background: white;
  border-radius: 20px;
  padding: 32px;
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
}

.modal-large {
  max-width: 700px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
}

.modal-header h2 i {
  color: #FF6B9D;
}

.btn-close {
  width: 36px;
  height: 36px;
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
  transform: rotate(90deg);
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 4px;
}

.required {
  color: #ff4d4f;
}

.form-input,
.form-textarea {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 15px;
  transition: all 0.3s;
  font-family: inherit;
}

.form-input-large {
  font-size: 18px;
  padding: 14px 18px;
  text-align: center;
  letter-spacing: 2px;
  font-weight: 600;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #FF6B9D;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.form-hint {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* 标签输入 */
.tags-input-wrapper {
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  padding: 12px;
  min-height: 50px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  transition: all 0.3s;
}

.tags-input-wrapper:focus-within {
  border-color: #FF6B9D;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.1);
}

.tags-display {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #FFE5F1;
  color: #FF6B9D;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
}

.tag-remove {
  width: 16px;
  height: 16px;
  border: none;
  background: rgba(255, 107, 157, 0.2);
  border-radius: 50%;
  color: #FF6B9D;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  transition: all 0.2s;
}

.tag-remove:hover {
  background: #FF6B9D;
  color: white;
}

.tag-input {
  flex: 1;
  min-width: 120px;
  border: none;
  outline: none;
  font-size: 14px;
  padding: 4px;
}

/* 权限选项 */
.permission-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.permission-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.permission-item:hover {
  border-color: #FF6B9D;
  background: #FFF5F8;
}

.permission-item input[type="radio"] {
  margin-top: 4px;
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.permission-item input[type="radio"]:checked + .permission-content {
  color: #FF6B9D;
}

.permission-content {
  flex: 1;
}

.permission-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.permission-title i {
  color: #FF6B9D;
}

.permission-desc {
  font-size: 13px;
  color: #999;
  line-height: 1.5;
}

.permission-item input[type="radio"]:checked ~ .permission-content .permission-title {
  color: #FF6B9D;
}

.form-actions {
  display: flex;
  gap: 16px;
  margin-top: 8px;
  padding-top: 24px;
  border-top: 2px solid #f0f0f0;
}

.btn-cancel,
.btn-submit {
  flex: 1;
  padding: 14px 24px;
  border-radius: 10px;
  border: none;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-submit {
  background: linear-gradient(135deg, #FF6B9D 0%, #C2185B 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 24px;
  }

  .hero-actions {
    width: 100%;
    flex-direction: column;
  }

  .btn-primary-large,
  .btn-secondary-large {
    width: 100%;
    justify-content: center;
  }

  .teams-container.grid {
    grid-template-columns: 1fr;
  }

  .teams-container.list .team-card {
    flex-direction: column;
  }

  .teams-container.list .team-stats-grid {
    width: 100%;
  }

  .team-stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .modal-content {
    padding: 24px;
  }

  .modal-large {
    max-width: 95%;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 40px 24px;
  }

  .hero-title {
    font-size: 28px;
  }

  .filter-tabs {
    flex-wrap: wrap;
  }

  .team-card {
    padding: 20px;
  }
}
</style>
