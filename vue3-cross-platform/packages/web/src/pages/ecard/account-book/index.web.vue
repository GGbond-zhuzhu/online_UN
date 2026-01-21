<template>
  <div class="page">
    <NavBar />

    <div class="container">
      <section class="topbar card">
        <div class="topbar-left">
          <div class="page-title">记账本</div>
          <div class="page-subtitle">顶部概览 · 筛选/日期 · 分类统计 · 明细列表</div>
        </div>

        <div class="topbar-right">
          <div class="segmented" role="tablist" aria-label="时间范围切换">
            <button class="segmented-btn" :class="{ active: mode === 'month' }" @click="switchMode('month')">本月</button>
            <button class="segmented-btn" :class="{ active: mode === '30d' }" @click="switchMode('30d')">近30天</button>
          </div>
          <button class="btn" :disabled="loading" @click="load">
            <i class="fas fa-rotate-right"></i>
            <span>{{ loading ? '加载中' : '刷新' }}</span>
          </button>
        </div>

        <div class="period-row">
          <div v-if="mode === 'month'" class="month-switch">
            <button class="icon-btn" :disabled="loading" @click="shiftMonth(-1)" aria-label="上个月">
              <i class="fas fa-chevron-left"></i>
            </button>
            <div class="month-label">
              <div class="month-main">{{ periodTitle }}</div>
              <div class="month-sub">{{ periodRangeText }}</div>
            </div>
            <button class="icon-btn" :disabled="loading || isFutureMonth" @click="shiftMonth(1)" aria-label="下个月">
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>

          <div v-else class="range-switch">
            <div class="range-field">
              <span class="range-label">开始</span>
              <input class="range-input" type="date" v-model="rangeStart" @change="load" />
            </div>
            <div class="range-sep">—</div>
            <div class="range-field">
              <span class="range-label">结束</span>
              <input class="range-input" type="date" v-model="rangeEnd" @change="load" />
            </div>
          </div>
        </div>
      </section>

      <section v-if="!userStore.isDemoLogin && !userStore.isApiReady" class="callout card">
        <div class="callout-left">
          <div class="callout-title">当前未登录</div>
          <div class="callout-desc">登录后可查看真实记账数据（演示模式会展示示例数据）。</div>
        </div>
        <button class="btn primary" @click="goLogin">
          <i class="fas fa-right-to-bracket"></i>
          去登录
        </button>
      </section>

      <section class="overview-grid">
        <div class="overview card gradient">
          <div class="ov-head">
            <div>
              <div class="ov-title">本期支出</div>
              <div class="ov-sub">{{ periodTitle }} · {{ filteredHint }}</div>
            </div>
            <div class="ov-icon"><i class="fas fa-chart-line"></i></div>
          </div>

          <div class="ov-main">
            <div class="ov-amount">¥{{ expenseTotal.toFixed(2) }}</div>
            <div class="ov-badges">
              <span class="badge"><i class="fas fa-receipt"></i>{{ recordCount }} 笔</span>
              <span class="badge"><i class="fas fa-sync-alt"></i>{{ autoCount }} 自动</span>
            </div>
          </div>

          <div class="ov-metrics">
            <div class="metric">
              <div class="metric-label">日均</div>
              <div class="metric-value">¥{{ avgDaily.toFixed(2) }}</div>
            </div>
            <div class="metric">
              <div class="metric-label">Top分类</div>
              <div class="metric-value">{{ topCategoryName }}</div>
            </div>
            <div class="metric">
              <div class="metric-label">覆盖天数</div>
              <div class="metric-value">{{ daysInPeriod }} 天</div>
            </div>
          </div>
        </div>

        <div class="actions card">
          <div class="actions-title">快捷操作</div>
          <div class="actions-grid">
            <button class="action-tile primary" @click="openAddModal">
              <i class="fas fa-plus-circle"></i>
              <div class="action-text">
                <div class="action-main">记一笔</div>
                <div class="action-sub">快速添加消费记录</div>
              </div>
            </button>
            <button class="action-tile" @click="toast('Web端暂未开放：导入E卡通')">
              <i class="fas fa-sync-alt"></i>
              <div class="action-text">
                <div class="action-main">导入E卡通</div>
                <div class="action-sub">自动同步消费</div>
              </div>
            </button>
            <button class="action-tile" @click="toast('Web端暂未开放：数据分析')">
              <i class="fas fa-chart-pie"></i>
              <div class="action-text">
                <div class="action-main">数据分析</div>
                <div class="action-sub">周报/月报</div>
              </div>
            </button>
          </div>
        </div>
      </section>

      <section class="card">
        <div class="section-head">
          <div>
            <div class="section-title">消费分类</div>
            <div class="section-subtitle">点击筛选 · 统计按本期数据计算</div>
          </div>

          <div class="chips">
            <button
              v-for="c in categoryChips"
              :key="c"
              class="chip"
              :class="{ active: selectedCategory === c }"
              @click="selectedCategory = c"
            >
              {{ c }}
            </button>
          </div>
        </div>

        <div class="category-grid">
          <div v-for="item in visibleCategoryStats" :key="item.category" class="category-row" @click="selectedCategory = item.category">
            <div class="cat-left">
              <div class="cat-icon" :style="{ background: item.color }"><i :class="item.icon"></i></div>
              <div class="cat-info">
                <div class="cat-name">{{ item.category }}</div>
                <div class="cat-meta">{{ item.count }} 笔 · {{ item.percentage.toFixed(0) }}%</div>
              </div>
            </div>
            <div class="cat-right">
              <div class="cat-amount">¥{{ item.amount.toFixed(2) }}</div>
              <div class="cat-bar">
                <div class="cat-bar-fill" :style="{ width: item.percentage + '%', background: item.color }"></div>
              </div>
            </div>
          </div>

          <div v-if="categoryStats.length === 0" class="empty-mini">暂无分类统计（本期无数据）</div>
        </div>

        <div class="category-footer" v-if="categoryStats.length > 6">
          <button class="link" @click="showAllCategories = !showAllCategories">
            {{ showAllCategories ? '收起' : '查看更多分类' }}
            <i class="fas" :class="showAllCategories ? 'fa-chevron-up' : 'fa-chevron-down'"></i>
          </button>
        </div>
      </section>

      <section class="card">
        <div class="section-head">
          <div>
            <div class="section-title">消费记录</div>
            <div class="section-subtitle">按日期分组 · 空状态/骨架屏</div>
          </div>
        </div>

        <div v-if="errorMsg" class="state warn">{{ errorMsg }}</div>

        <div v-else-if="loading" class="skeleton">
          <div v-for="i in 8" :key="i" class="sk-row">
            <div class="sk-icon"></div>
            <div class="sk-lines">
              <div class="sk-line w60"></div>
              <div class="sk-line w90"></div>
            </div>
            <div class="sk-amt"></div>
          </div>
        </div>

        <div v-else-if="groupedRecords.length === 0" class="empty">
          <div class="empty-hero">
            <div class="empty-icon"><i class="fas fa-receipt"></i></div>
            <div class="empty-title">暂无记录</div>
            <div class="empty-desc">你可以先从“记一笔”开始，或切换时间范围查看历史数据。</div>
          </div>
          <div class="empty-actions">
            <button class="btn primary" @click="openAddModal"><i class="fas fa-plus-circle"></i>添加记录</button>
          </div>
        </div>

        <div v-else class="records">
          <div v-for="g in groupedRecords" :key="g.date" class="group">
            <div class="group-head">
              <div class="group-date">{{ g.date }}</div>
              <div class="group-sum">¥{{ g.total.toFixed(2) }}</div>
            </div>
            <div class="group-list">
              <div v-for="r in g.records" :key="r.id" class="record">
                <div class="rec-left">
                  <div class="rec-icon" :style="{ background: getCategoryMeta(r.category).color }">
                    <i :class="getCategoryMeta(r.category).icon"></i>
                  </div>
                  <div class="rec-info">
                    <div class="rec-title">
                      <span class="rec-cat">{{ r.category }}</span>
                      <span v-if="r.description" class="rec-desc">· {{ r.description }}</span>
                    </div>
                    <div class="rec-meta">
                      <span>{{ normalizeDate(r.consumeDate) }}</span>
                      <span v-if="r.location">· {{ r.location }}</span>
                      <span v-if="r.isAutoImport" class="tag auto">自动</span>
                      <span class="tag pay">{{ payMethodLabel(r.payMethod) }}</span>
                    </div>
                  </div>
                </div>
                <div class="rec-right" :class="amountClass(r.amount)">{{ formatAmount(r.amount) }}</div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <button class="fab" @click="openAddModal" title="记一笔"><i class="fas fa-plus"></i></button>

    <div v-if="showAdd" class="modal-mask" @click="closeAddModal">
      <div class="modal card" @click.stop>
        <div class="modal-head">
          <div class="modal-title">记一笔</div>
          <button class="icon-btn" @click="closeAddModal" aria-label="关闭"><i class="fas fa-xmark"></i></button>
        </div>
        <div class="modal-body">
          <div class="form-grid">
            <label class="field">
              <span class="label">金额</span>
              <input class="input" type="number" min="0" step="0.01" v-model="draft.amount" placeholder="例如 12.50" />
            </label>
            <label class="field">
              <span class="label">分类</span>
              <select class="input" v-model="draft.category">
                <option v-for="c in categoryOptions" :key="c" :value="c">{{ c }}</option>
              </select>
            </label>
            <label class="field">
              <span class="label">日期</span>
              <input class="input" type="date" v-model="draft.consumeDate" />
            </label>
            <label class="field">
              <span class="label">支付方式</span>
              <select class="input" v-model="draft.payMethod">
                <option value="CARD">校园卡</option>
                <option value="WECHAT">微信</option>
                <option value="ALIPAY">支付宝</option>
                <option value="CASH">现金</option>
                <option value="OTHER">其他</option>
              </select>
            </label>
            <label class="field full">
              <span class="label">地点（可选）</span>
              <input class="input" v-model="draft.location" placeholder="例如 一食堂 / 校园超市" />
            </label>
            <label class="field full">
              <span class="label">备注（可选）</span>
              <input class="input" v-model="draft.description" placeholder="例如 午餐/水果/日用品" />
            </label>
          </div>
        </div>
        <div class="modal-foot">
          <button class="btn" @click="closeAddModal">取消</button>
          <button class="btn primary" :disabled="submitting" @click="submitAdd">
            <i class="fas fa-check"></i>
            {{ submitting ? '提交中' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="toastMsg" class="toast">{{ toastMsg }}</div>

    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import { addAccountBookRecord, getAccountBookRecords, type AccountBookRecord, useUserStore } from '@campus/common'

type Mode = 'month' | '30d'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const errorMsg = ref('')
const toastMsg = ref('')

const mode = ref<Mode>('month')
const monthCursor = ref(new Date())
const rangeStart = ref('')
const rangeEnd = ref('')

const showAdd = ref(false)
const draft = reactive({
  amount: '',
  category: '餐饮',
  consumeDate: '',
  payMethod: 'CARD',
  location: '',
  description: ''
})

const categoryOptions = ['餐饮', '交通', '购物', '娱乐', '学习', '通讯', '其他']
const categoryChips = computed(() => ['全部', ...categoryOptions])
const selectedCategory = ref('全部')
const showAllCategories = ref(false)

const records = ref<AccountBookRecord[]>([])

const fmtDate = (d: Date) => d.toISOString().slice(0, 10)
const today = () => new Date()

const periodStart = computed(() => {
  if (mode.value === 'month') {
    const d = monthCursor.value
    return fmtDate(new Date(d.getFullYear(), d.getMonth(), 1))
  }
  return rangeStart.value || fmtDate(new Date(Date.now() - 29 * 24 * 60 * 60 * 1000))
})

const periodEnd = computed(() => {
  if (mode.value === 'month') {
    const d = monthCursor.value
    return fmtDate(new Date(d.getFullYear(), d.getMonth() + 1, 0))
  }
  return rangeEnd.value || fmtDate(today())
})

const isFutureMonth = computed(() => {
  const d = monthCursor.value
  const now = new Date()
  return d.getFullYear() > now.getFullYear() || (d.getFullYear() === now.getFullYear() && d.getMonth() > now.getMonth())
})

const periodTitle = computed(() => {
  if (mode.value === 'month') {
    const d = monthCursor.value
    return `${d.getFullYear()}年${d.getMonth() + 1}月`
  }
  return '近30天'
})

const periodRangeText = computed(() => `${periodStart.value} ~ ${periodEnd.value}`)
const normalizeDate = (s: string) => String(s || '').slice(0, 10)

const filteredRecords = computed(() => {
  if (selectedCategory.value === '全部') return records.value
  return records.value.filter(r => r.category === selectedCategory.value)
})

const amountClass = (v: number) => (v < 0 ? 'income' : 'expense')
const formatAmount = (v: number) => {
  const abs = Math.abs(v).toFixed(2)
  return v < 0 ? `+¥${abs}` : `-¥${abs}`
}

const daysInPeriod = computed(() => {
  const s = new Date(periodStart.value)
  const e = new Date(periodEnd.value)
  const diff = Math.floor((e.getTime() - s.getTime()) / (24 * 60 * 60 * 1000))
  return Math.max(1, diff + 1)
})

const expenseTotal = computed(() => records.value.reduce((sum, r) => sum + (r.amount > 0 ? r.amount : 0), 0))
const recordCount = computed(() => records.value.length)
const autoCount = computed(() => records.value.filter(r => r.isAutoImport).length)
const avgDaily = computed(() => expenseTotal.value / daysInPeriod.value)

const CATEGORY_META: Record<string, { icon: string; color: string }> = {
  餐饮: { icon: 'fas fa-utensils', color: '#FFB5D8' },
  交通: { icon: 'fas fa-bus', color: '#C7CEEA' },
  购物: { icon: 'fas fa-shopping-cart', color: '#A8E6CF' },
  娱乐: { icon: 'fas fa-gamepad', color: '#D4A5F5' },
  学习: { icon: 'fas fa-book', color: '#FFD3B6' },
  通讯: { icon: 'fas fa-mobile-alt', color: '#B8E6E6' },
  其他: { icon: 'fas fa-ellipsis-h', color: '#D0D0D0' }
}
const getCategoryMeta = (c: string) => CATEGORY_META[c] || { icon: 'fas fa-ellipsis-h', color: '#D0D0D0' }

const payMethodLabel = (m: string) => {
  const map: Record<string, string> = { CARD: '校园卡', WECHAT: '微信', ALIPAY: '支付宝', CASH: '现金', OTHER: '其他' }
  return map[m] || '其他'
}

const categoryStats = computed(() => {
  const total = expenseTotal.value
  if (!total) return []
  const map = new Map<string, { amount: number; count: number }>()
  for (const r of records.value) {
    const amount = r.amount > 0 ? r.amount : 0
    if (!amount) continue
    const cur = map.get(r.category) || { amount: 0, count: 0 }
    map.set(r.category, { amount: cur.amount + amount, count: cur.count + 1 })
  }
  return Array.from(map.entries())
    .map(([category, v]) => ({
      category,
      amount: v.amount,
      count: v.count,
      percentage: (v.amount / total) * 100,
      ...getCategoryMeta(category)
    }))
    .sort((a, b) => b.amount - a.amount)
})

const visibleCategoryStats = computed(() => (showAllCategories.value ? categoryStats.value : categoryStats.value.slice(0, 6)))
const topCategoryName = computed(() => categoryStats.value[0]?.category || '—')
const filteredHint = computed(() => (selectedCategory.value === '全部' ? '全部分类' : `仅看：${selectedCategory.value}`))

const groupedRecords = computed(() => {
  const list = filteredRecords.value
  const groups = new Map<string, AccountBookRecord[]>()
  for (const r of list) {
    const d = normalizeDate(r.consumeDate)
    if (!groups.has(d)) groups.set(d, [])
    groups.get(d)!.push(r)
  }
  const out = Array.from(groups.entries()).map(([date, rs]) => {
    const total = rs.reduce((s, r) => s + (r.amount > 0 ? r.amount : 0), 0)
    return { date, total, records: rs.slice().sort((a, b) => normalizeDate(b.consumeDate).localeCompare(normalizeDate(a.consumeDate))) }
  })
  return out.sort((a, b) => b.date.localeCompare(a.date))
})

function toast(msg: string) {
  toastMsg.value = msg
  window.clearTimeout((toast as any)._t)
  ;(toast as any)._t = window.setTimeout(() => (toastMsg.value = ''), 2200)
}

function goLogin() {
  router.push('/login')
}

function switchMode(m: Mode) {
  mode.value = m
  load()
}

function shiftMonth(step: number) {
  const d = new Date(monthCursor.value)
  d.setMonth(d.getMonth() + step)
  const now = new Date()
  if (d.getFullYear() > now.getFullYear() || (d.getFullYear() === now.getFullYear() && d.getMonth() > now.getMonth())) return
  monthCursor.value = d
  load()
}

function openAddModal() {
  showAdd.value = true
  draft.amount = ''
  draft.category = selectedCategory.value !== '全部' ? selectedCategory.value : '餐饮'
  draft.consumeDate = periodEnd.value
  draft.payMethod = 'CARD'
  draft.location = ''
  draft.description = ''
}

function closeAddModal() {
  showAdd.value = false
}

async function submitAdd() {
  const amount = Number(draft.amount)
  if (!amount || Number.isNaN(amount) || amount <= 0) {
    toast('请输入有效金额')
    return
  }

  submitting.value = true
  try {
    if (userStore.isDemoLogin) {
      const now = new Date().toISOString()
      records.value = [
        {
          id: Math.max(0, ...records.value.map(r => r.id || 0)) + 1,
          userId: 1,
          amount,
          category: draft.category,
          description: draft.description || undefined,
          consumeDate: draft.consumeDate || fmtDate(today()),
          isAutoImport: false,
          location: draft.location || undefined,
          payMethod: draft.payMethod,
          createTime: now
        } as any,
        ...records.value
      ]
      toast('已添加（演示数据）')
      closeAddModal()
      return
    }

    if (!userStore.isApiReady) {
      toast('未登录，无法保存')
      return
    }

    await addAccountBookRecord({
      amount,
      category: draft.category,
      description: draft.description || undefined,
      consumeDate: draft.consumeDate || undefined,
      location: draft.location || undefined,
      payMethod: draft.payMethod
    })
    toast('保存成功')
    closeAddModal()
    await load()
  } catch (e: any) {
    toast(e?.message || '保存失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

function buildDemoRecords(): AccountBookRecord[] {
  const base = new Date()
  const d = (offset: number) => fmtDate(new Date(base.getTime() - offset * 24 * 60 * 60 * 1000))
  const now = new Date().toISOString()
  return [
    { id: 101, userId: 1, amount: 12.5, category: '餐饮', description: '第一食堂午餐', consumeDate: d(0), isAutoImport: true, location: '一食堂', payMethod: 'CARD', createTime: now } as any,
    { id: 102, userId: 1, amount: 28, category: '购物', description: '校园超市', consumeDate: d(0), isAutoImport: true, location: '学生超市', payMethod: 'CARD', createTime: now } as any,
    { id: 103, userId: 1, amount: 3.5, category: '交通', description: '校车', consumeDate: d(1), isAutoImport: false, location: '校门口', payMethod: 'WECHAT', createTime: now } as any
  ]
}

async function load() {
  errorMsg.value = ''

  if (mode.value === '30d') {
    if (!rangeEnd.value) rangeEnd.value = fmtDate(today())
    if (!rangeStart.value) rangeStart.value = fmtDate(new Date(Date.now() - 29 * 24 * 60 * 60 * 1000))
  }

  if (userStore.isDemoLogin) {
    records.value = buildDemoRecords().filter(r => {
      const day = normalizeDate(r.consumeDate)
      return day >= periodStart.value && day <= periodEnd.value
    })
    return
  }

  if (!userStore.isApiReady) {
    records.value = []
    return
  }

  loading.value = true
  try {
    records.value = await getAccountBookRecords({
      startDate: periodStart.value,
      endDate: periodEnd.value,
      category: selectedCategory.value === '全部' ? undefined : selectedCategory.value,
      page: 1,
      size: 200
    })
  } catch (e: any) {
    errorMsg.value = e?.message || '加载失败，请稍后重试'
    records.value = []
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped lang="scss">
.page { min-height: 100vh; background: linear-gradient(180deg, #fff0f5 0%, #f0f8ff 55%, #f5f0ff 100%); }
.container { max-width: 1160px; margin: 0 auto; padding: 16px 16px 72px; }
.card { background: rgba(255,255,255,.94); border: 1px solid rgba(255,107,157,.16); border-radius: 16px; box-shadow: 0 10px 28px rgba(255,107,157,.08); }
.topbar { padding: 16px; display: grid; gap: 12px; }
.topbar-left { display:flex; flex-direction:column; gap:4px; }
.page-title { font-size:22px; font-weight:800; color:#2a2a2a; }
.page-subtitle { font-size:13px; color:#666; }
.topbar-right { display:flex; gap:10px; align-items:center; justify-content:space-between; flex-wrap:wrap; }
.segmented { display:inline-flex; border:1px solid rgba(255,107,157,.25); border-radius:12px; overflow:hidden; background:rgba(255,255,255,.7); }
.segmented-btn { height:34px; padding:0 12px; border:0; background:transparent; color:#ff6b9d; cursor:pointer; font-weight:600; }
.segmented-btn.active { background:linear-gradient(135deg,#ff8fb3 0%,#ff6b9d 100%); color:#fff; }
.btn { height:36px; padding:0 12px; border-radius:12px; border:1px solid rgba(255,107,157,.25); background:rgba(255,255,255,.9); color:#ff6b9d; cursor:pointer; display:inline-flex; align-items:center; gap:8px; font-weight:600; text-decoration:none; }
.btn:disabled { cursor:not-allowed; opacity:.65; }
.btn.primary { border:none; background:linear-gradient(135deg,#ff8fb3 0%,#ff6b9d 100%); color:#fff; }
.period-row { display:flex; justify-content:space-between; align-items:center; gap:12px; flex-wrap:wrap; }
.month-switch { display:flex; align-items:center; gap:10px; }
.month-label { display:flex; flex-direction:column; gap:2px; }
.month-main { font-weight:800; color:#333; }
.month-sub { font-size:12px; color:#777; }
.icon-btn { width:36px; height:36px; border-radius:12px; border:1px solid rgba(255,107,157,.25); background:rgba(255,255,255,.9); color:#ff6b9d; cursor:pointer; display:inline-flex; align-items:center; justify-content:center; }
.range-switch { display:flex; align-items:center; gap:10px; flex-wrap:wrap; }
.range-field { display:flex; align-items:center; gap:8px; padding:8px 10px; border:1px solid rgba(255,107,157,.18); border-radius:12px; background:rgba(255,255,255,.8); }
.range-label { font-size:12px; color:#777; }
.range-input { border:none; background:transparent; outline:none; color:#333; font-weight:600; }
.range-sep { color:#999; }
.callout { margin-top:12px; padding:14px 16px; display:flex; align-items:center; justify-content:space-between; gap:12px; }
.callout-title { font-weight:800; color:#333; }
.callout-desc { margin-top:4px; font-size:13px; color:#666; }
.overview-grid { margin-top:12px; display:grid; grid-template-columns:1.35fr 1fr; gap:12px; }
@media (max-width:960px){ .overview-grid{ grid-template-columns:1fr; } }
.overview { padding:16px; color:#fff; border:none; }
.overview.gradient { background:linear-gradient(135deg,#ff4fa0 0%,#ff7fb2 45%,#a78bfa 100%); box-shadow:0 18px 38px rgba(255,79,160,.18); }
.ov-head { display:flex; align-items:center; justify-content:space-between; }
.ov-title { font-size:13px; opacity:.9; font-weight:700; }
.ov-sub { margin-top:4px; font-size:12px; opacity:.85; }
.ov-icon { width:44px; height:44px; border-radius:14px; background:rgba(255,255,255,.22); display:flex; align-items:center; justify-content:center; }
.ov-main { margin-top:12px; display:flex; align-items:flex-end; justify-content:space-between; gap:12px; flex-wrap:wrap; }
.ov-amount { font-size:34px; font-weight:900; }
.ov-badges { display:flex; gap:8px; flex-wrap:wrap; }
.badge { display:inline-flex; align-items:center; gap:6px; padding:6px 10px; border-radius:999px; background:rgba(255,255,255,.18); border:1px solid rgba(255,255,255,.22); font-size:12px; font-weight:700; }
.ov-metrics { margin-top:14px; display:grid; grid-template-columns:repeat(3,1fr); gap:10px; }
.metric { padding:10px; border-radius:14px; background:rgba(255,255,255,.14); border:1px solid rgba(255,255,255,.18); }
.metric-label { font-size:12px; opacity:.9; font-weight:700; }
.metric-value { margin-top:4px; font-size:16px; font-weight:900; }
.actions { padding:16px; }
.actions-title { font-weight:900; color:#333; margin-bottom:10px; }
.actions-grid { display:grid; grid-template-columns:1fr; gap:10px; }
.action-tile { width:100%; padding:12px; border-radius:14px; border:1px solid rgba(255,107,157,.22); background:rgba(255,255,255,.9); display:grid; grid-template-columns:28px 1fr; gap:10px; align-items:center; cursor:pointer; color:#ff6b9d; }
.action-tile.primary { border:none; background:linear-gradient(135deg,#ff8fb3 0%,#ff6b9d 100%); color:#fff; }
.action-main { font-weight:900; }
.action-sub { margin-top:2px; font-size:12px; opacity:.85; }
.section-head { padding:14px 16px 10px; display:flex; gap:12px; align-items:flex-start; justify-content:space-between; flex-wrap:wrap; }
.section-title { font-weight:900; color:#333; }
.section-subtitle { margin-top:4px; font-size:12px; color:#777; }
.chips { display:flex; gap:8px; flex-wrap:wrap; }
.chip { height:30px; padding:0 10px; border-radius:999px; border:1px solid rgba(255,107,157,.22); background:rgba(255,255,255,.85); color:#ff6b9d; cursor:pointer; font-weight:700; font-size:12px; }
.chip.active { border:none; background:linear-gradient(135deg,#ff8fb3 0%,#ff6b9d 100%); color:#fff; }
.category-grid { padding:0 16px 14px; display:grid; gap:10px; }
.category-row { display:flex; align-items:center; justify-content:space-between; gap:12px; padding:12px; border-radius:14px; background:rgba(255,255,255,.92); border:1px solid rgba(0,0,0,.06); cursor:pointer; }
.cat-left { display:flex; align-items:center; gap:10px; }
.cat-icon { width:34px; height:34px; border-radius:12px; display:flex; align-items:center; justify-content:center; color:#fff; }
.cat-name { font-weight:900; color:#333; }
.cat-meta { margin-top:3px; font-size:12px; color:#777; }
.cat-right { text-align:right; display:grid; gap:6px; }
.cat-amount { font-weight:900; color:#333; }
.cat-bar { width:140px; height:8px; border-radius:999px; background:rgba(0,0,0,.06); overflow:hidden; }
.cat-bar-fill { height:100%; border-radius:999px; }
.category-footer { padding:0 16px 16px; }
.link { border:none; background:transparent; color:#ff6b9d; cursor:pointer; font-weight:800; display:inline-flex; align-items:center; gap:6px; }
.state { padding:18px 16px; color:#666; }
.state.warn { color:#d83a56; }
.empty-mini { padding:16px 12px; color:#777; font-size:13px; }
.records { padding:0 16px 16px; }
.group { margin-top:12px; }
.group-head { display:flex; align-items:center; justify-content:space-between; padding:10px 12px; border-radius:14px; background:rgba(255,107,157,.08); border:1px solid rgba(255,107,157,.12); color:#333; font-weight:900; }
.group-list { margin-top:8px; display:grid; gap:8px; }
.record { display:flex; align-items:center; justify-content:space-between; gap:12px; padding:12px; border-radius:14px; background:rgba(255,255,255,.92); border:1px solid rgba(0,0,0,.06); }
.rec-left { display:flex; align-items:center; gap:10px; }
.rec-icon { width:36px; height:36px; border-radius:14px; display:flex; align-items:center; justify-content:center; color:#fff; }
.rec-title { font-weight:900; color:#333; }
.rec-desc { color:#555; font-weight:700; }
.rec-meta { margin-top:4px; font-size:12px; color:#777; display:flex; gap:6px; align-items:center; flex-wrap:wrap; }
.tag { padding:2px 8px; border-radius:999px; background:rgba(0,0,0,.06); color:#555; font-weight:800; font-size:11px; }
.tag.auto { background:rgba(167,139,250,.18); color:#6d28d9; }
.tag.pay { background:rgba(255,107,157,.14); color:#ff2d7a; }
.rec-right { font-weight:900; white-space:nowrap; }
.rec-right.expense { color:#ff2d7a; }
.rec-right.income { color:#20a05a; }
.empty { padding:20px 16px 16px; }
.empty-hero { padding:16px; border-radius:16px; border:1px dashed rgba(255,107,157,.28); background:rgba(255,255,255,.75); text-align:center; }
.empty-icon { width:56px; height:56px; border-radius:16px; margin:0 auto 10px; background:rgba(255,107,157,.14); color:#ff2d7a; display:flex; align-items:center; justify-content:center; }
.empty-title { font-weight:900; color:#333; }
.empty-desc { margin-top:6px; font-size:13px; color:#666; }
.empty-actions { margin-top:12px; display:flex; justify-content:center; }
.fab { position:fixed; right:18px; bottom:18px; width:56px; height:56px; border-radius:18px; border:none; background:linear-gradient(135deg,#ff8fb3 0%,#ff6b9d 100%); color:#fff; box-shadow:0 16px 30px rgba(255,107,157,.3); cursor:pointer; display:flex; align-items:center; justify-content:center; z-index:10; }
.modal-mask { position:fixed; inset:0; background:rgba(0,0,0,.35); display:flex; align-items:center; justify-content:center; padding:16px; z-index:999; }
.modal { width:min(720px,100%); padding:14px; }
.modal-head { display:flex; align-items:center; justify-content:space-between; padding:4px 6px 10px; }
.modal-title { font-weight:900; color:#333; }
.modal-body { padding:0 6px 10px; }
.form-grid { display:grid; grid-template-columns:1fr 1fr; gap:10px; }
@media (max-width:640px){ .form-grid{ grid-template-columns:1fr; } }
.field { display:grid; gap:6px; }
.field.full { grid-column:1 / -1; }
.label { font-size:12px; color:#666; font-weight:700; }
.input { height:38px; border-radius:12px; border:1px solid rgba(255,107,157,.22); background:rgba(255,255,255,.95); padding:0 12px; outline:none; color:#333; font-weight:700; }
.modal-foot { display:flex; justify-content:flex-end; gap:10px; padding:0 6px 6px; }
.toast { position:fixed; left:50%; bottom:86px; transform:translateX(-50%); padding:10px 14px; border-radius:999px; background:rgba(0,0,0,.82); color:#fff; font-weight:700; z-index:1000; }
.skeleton { padding:10px 16px 16px; }
.sk-row { display:grid; grid-template-columns:36px 1fr 88px; gap:10px; align-items:center; padding:10px 0; border-top:1px solid rgba(0,0,0,.05); }
.sk-row:first-child{ border-top:none; }
.sk-icon,.sk-line,.sk-amt{ background:linear-gradient(90deg,rgba(0,0,0,.06),rgba(0,0,0,.035),rgba(0,0,0,.06)); background-size:200% 100%; animation:sk 1.2s infinite linear; border-radius:12px; }
.sk-icon{ width:36px; height:36px; }
.sk-lines{ display:grid; gap:8px; }
.sk-line{ height:10px; }
.sk-line.w60{ width:60%; }
.sk-line.w90{ width:90%; }
.sk-amt{ height:14px; border-radius:999px; }
@keyframes sk{ 0%{ background-position:0% 0%; } 100%{ background-position:200% 0%; } }
</style>
