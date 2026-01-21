<template>
  <div class="page">
    <NavBar />

    <div class="container">
      <section class="topbar card">
        <div class="topbar-left">
          <div class="page-title">饮食表</div>
          <div class="page-subtitle">日期切换 · 餐次筛选 · 营养/花费概览</div>
        </div>
        <div class="topbar-right">
          <button class="btn" :disabled="loading" @click="load">
            <i class="fas fa-rotate-right"></i>
            <span>{{ loading ? '加载中' : '刷新' }}</span>
          </button>
        </div>

        <div class="date-row">
          <div class="date-switch">
            <button class="icon-btn" :disabled="loading" @click="shiftDate(-1)" aria-label="前一天">
              <i class="fas fa-chevron-left"></i>
            </button>
            <div class="date-center">
              <div class="date-main">{{ displayDate }}</div>
              <div class="date-sub">{{ weekdayText }}</div>
            </div>
            <button class="icon-btn" :disabled="loading" @click="shiftDate(1)" aria-label="后一天">
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>

          <div class="date-input-wrap">
            <span class="label-mini">选择日期</span>
            <input class="date-input" type="date" v-model="selectedDate" @change="load" />
          </div>
        </div>

        <div class="filter-row">
          <div class="chips">
            <button
              v-for="m in mealChips"
              :key="m.value"
              class="chip"
              :class="{ active: selectedMeal === m.value }"
              @click="selectedMeal = m.value"
            >
              <i :class="m.icon"></i>
              {{ m.label }}
            </button>
          </div>

          <div class="chips">
            <button
              v-for="s in sourceChips"
              :key="s.value"
              class="chip ghost"
              :class="{ active: selectedSource === s.value }"
              @click="selectedSource = s.value"
            >
              {{ s.label }}
            </button>
          </div>
        </div>
      </section>

      <section v-if="!userStore.isDemoLogin && !userStore.isApiReady" class="callout card">
        <div class="callout-left">
          <div class="callout-title">当前未登录</div>
          <div class="callout-desc">登录后可查看真实饮食记录（演示模式会展示示例数据）。</div>
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
              <div class="ov-title">今日概览</div>
              <div class="ov-sub">{{ selectedDate }} · {{ filteredHint }}</div>
            </div>
            <div class="ov-icon"><i class="fas fa-utensils"></i></div>
          </div>

          <div class="ov-metrics">
            <div class="metric">
              <div class="metric-label">餐次</div>
              <div class="metric-value">{{ totalMeals }}</div>
            </div>
            <div class="metric">
              <div class="metric-label">卡路里</div>
              <div class="metric-value">{{ totalCalories }}</div>
            </div>
            <div class="metric">
              <div class="metric-label">消费</div>
              <div class="metric-value">¥{{ totalPrice.toFixed(2) }}</div>
            </div>
          </div>
        </div>

        <div class="actions card">
          <div class="actions-title">快捷操作</div>
          <div class="actions-grid">
            <button class="action-tile primary" @click="goAdd()">
              <i class="fas fa-plus-circle"></i>
              <div class="action-text">
                <div class="action-main">添加饮食</div>
                <div class="action-sub">手动记录饮食</div>
              </div>
            </button>
            <button class="action-tile" @click="autoImport">
              <i class="fas fa-sync-alt"></i>
              <div class="action-text">
                <div class="action-main">导入食堂</div>
                <div class="action-sub">按当天导入消费</div>
              </div>
            </button>
          </div>
        </div>
      </section>

      <section class="card">
        <div class="section-head">
          <div>
            <div class="section-title">饮食记录</div>
            <div class="section-subtitle">按餐次分组 · 空状态/骨架屏</div>
          </div>
        </div>

        <div v-if="errorMsg" class="state warn">{{ errorMsg }}</div>

        <div v-else-if="loading" class="skeleton">
          <div v-for="i in 6" :key="i" class="sk-row">
            <div class="sk-icon"></div>
            <div class="sk-lines">
              <div class="sk-line w60"></div>
              <div class="sk-line w90"></div>
            </div>
            <div class="sk-amt"></div>
          </div>
        </div>

        <div v-else-if="filteredRecords.length === 0" class="empty">
          <div class="empty-hero">
            <div class="empty-icon"><i class="fas fa-bowl-food"></i></div>
            <div class="empty-title">暂无记录</div>
            <div class="empty-desc">可以先添加一条饮食记录，或切换日期查看历史数据。</div>
          </div>
          <div class="empty-actions">
            <button class="btn primary" @click="goAdd()"><i class="fas fa-plus-circle"></i>添加饮食</button>
          </div>
        </div>

        <div v-else class="meals">
          <div v-for="meal in mealSections" :key="meal.value" class="meal">
            <div class="meal-head" :style="{ background: meal.bg }" @click="toggleMeal(meal.value)">
              <div class="meal-left">
                <div class="meal-icon" :style="{ background: meal.iconBg }">
                  <i :class="meal.icon"></i>
                </div>
                <div class="meal-title">
                  <div class="meal-name">{{ meal.label }}</div>
                  <div class="meal-sub">{{ mealCount(meal.value) }} 条</div>
                </div>
              </div>
              <div class="meal-right">
                <div class="meal-sum">¥{{ mealSum(meal.value).toFixed(2) }}</div>
                <i class="fas" :class="collapsedMeals.has(meal.value) ? 'fa-chevron-down' : 'fa-chevron-up'"></i>
              </div>
            </div>

            <div v-if="!collapsedMeals.has(meal.value)" class="meal-list">
              <div v-for="r in mealRecords(meal.value)" :key="r.id" class="record" @click="goDetail(r)">
                <div class="rec-main">
                  <div class="rec-title">
                    <span class="food">{{ r.foodName }}</span>
                    <span v-if="r.foodDetail" class="detail">· {{ r.foodDetail }}</span>
                  </div>
                  <div class="rec-meta">
                    <span>{{ sourceLabel(r.source) }}</span>
                    <span v-if="r.location">· {{ r.location }}</span>
                    <span v-if="r.isAutoImport" class="tag auto">自动</span>
                    <span v-if="r.calories" class="tag kcal">{{ r.calories }} kcal</span>
                  </div>
                </div>
                <div class="rec-right">
                  <span v-if="r.price != null" class="price">¥{{ r.price.toFixed(2) }}</span>
                  <i class="fas fa-chevron-right"></i>
                </div>
              </div>

              <div v-if="mealRecords(meal.value).length === 0" class="meal-empty">
                <div class="meal-empty-text">该餐次暂无记录</div>
                <button class="btn" @click.stop="goAdd(meal.value)"><i class="fas fa-plus"></i>添加{{ meal.label }}</button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <button class="fab" @click="goAdd()" title="添加饮食"><i class="fas fa-plus"></i></button>
    <div v-if="toastMsg" class="toast">{{ toastMsg }}</div>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import AppFooter from '@/components/common/AppFooter.vue'
import { autoImportDietFromEcard, getDietRecordsByDate, type DietRecord, useUserStore } from '@campus/common'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const errorMsg = ref('')
const toastMsg = ref('')

const selectedDate = ref(new Date().toISOString().slice(0, 10))

type Meal = 'ALL' | 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK'
type Source = 'ALL' | 'CANTEEN' | 'TAKEOUT' | 'RESTAURANT' | 'HOME' | 'OTHER'

const selectedMeal = ref<Meal>('ALL')
const selectedSource = ref<Source>('ALL')
const collapsedMeals = ref<Set<string>>(new Set())

const records = ref<DietRecord[]>([])

const mealChips = [
  { value: 'ALL', label: '全部', icon: 'fas fa-layer-group' },
  { value: 'BREAKFAST', label: '早餐', icon: 'fas fa-sun' },
  { value: 'LUNCH', label: '午餐', icon: 'fas fa-utensils' },
  { value: 'DINNER', label: '晚餐', icon: 'fas fa-moon' },
  { value: 'SNACK', label: '加餐', icon: 'fas fa-cookie' }
] as const

const sourceChips = [
  { value: 'ALL', label: '全部来源' },
  { value: 'CANTEEN', label: '食堂' },
  { value: 'TAKEOUT', label: '外卖' },
  { value: 'RESTAURANT', label: '餐馆' },
  { value: 'HOME', label: '自制' },
  { value: 'OTHER', label: '其他' }
] as const

const mealSections = [
  { value: 'BREAKFAST', label: '早餐', icon: 'fas fa-sun', bg: 'rgba(255, 229, 180, 0.55)', iconBg: '#FFE5B4' },
  { value: 'LUNCH', label: '午餐', icon: 'fas fa-utensils', bg: 'rgba(255, 182, 193, 0.45)', iconBg: '#FFB6C1' },
  { value: 'DINNER', label: '晚餐', icon: 'fas fa-moon', bg: 'rgba(221, 160, 221, 0.40)', iconBg: '#DDA0DD' },
  { value: 'SNACK', label: '加餐', icon: 'fas fa-cookie', bg: 'rgba(255, 209, 220, 0.55)', iconBg: '#FFD1DC' }
] as const

const displayDate = computed(() => {
  const d = new Date(selectedDate.value)
  return `${d.getMonth() + 1}月${d.getDate()}日`
})

const weekdayText = computed(() => {
  const d = new Date(selectedDate.value)
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  return `周${weekdays[d.getDay()]}`
})

const filteredRecords = computed(() => {
  let list = records.value
  if (selectedMeal.value !== 'ALL') list = list.filter(r => r.mealType === selectedMeal.value)
  if (selectedSource.value !== 'ALL') list = list.filter(r => r.source === selectedSource.value)
  return list
})

const totalMeals = computed(() => filteredRecords.value.length)
const totalCalories = computed(() => filteredRecords.value.reduce((s, r) => s + (r.calories || 0), 0))
const totalPrice = computed(() => filteredRecords.value.reduce((s, r) => s + (r.price || 0), 0))

const filteredHint = computed(() => {
  const meal = selectedMeal.value === 'ALL' ? '全部餐次' : mealSections.find(m => m.value === selectedMeal.value)?.label
  const src = sourceChips.find(s => s.value === selectedSource.value)?.label
  return `${meal || '全部餐次'} · ${src || '全部来源'}`
})

function toast(msg: string) {
  toastMsg.value = msg
  window.clearTimeout((toast as any)._t)
  ;(toast as any)._t = window.setTimeout(() => (toastMsg.value = ''), 2200)
}

function goLogin() {
  router.push('/login')
}

function shiftDate(step: number) {
  const d = new Date(selectedDate.value)
  d.setDate(d.getDate() + step)
  selectedDate.value = d.toISOString().slice(0, 10)
  load()
}

function toggleMeal(meal: string) {
  const set = new Set(collapsedMeals.value)
  if (set.has(meal)) set.delete(meal)
  else set.add(meal)
  collapsedMeals.value = set
}

function mealRecords(meal: string) {
  return filteredRecords.value.filter(r => r.mealType === meal)
}
function mealCount(meal: string) {
  return mealRecords(meal).length
}
function mealSum(meal: string) {
  return mealRecords(meal).reduce((s, r) => s + (r.price || 0), 0)
}

function sourceLabel(s: string) {
  const map: Record<string, string> = { CANTEEN: '食堂', TAKEOUT: '外卖', RESTAURANT: '餐馆', HOME: '自制', OTHER: '其他' }
  return map[s] || '其他'
}

function goAdd(mealType?: string) {
  const q: Record<string, string> = { date: selectedDate.value }
  if (mealType) q.mealType = mealType
  router.push({ path: '/ecard/diet-record/add', query: q })
}

function goDetail(r: DietRecord) {
  router.push({ path: '/ecard/diet-record/add', query: { id: String(r.id) } })
}

function buildDemoRecords(date: string): DietRecord[] {
  const now = new Date().toISOString()
  return [
    { id: 201, userId: 1, dietDate: date, mealType: 'BREAKFAST', foodName: '豆浆 + 鸡蛋', foodDetail: '少糖', location: '一食堂', source: 'CANTEEN', isAutoImport: true, calories: 380, price: 6.5, createTime: now } as any,
    { id: 202, userId: 1, dietDate: date, mealType: 'LUNCH', foodName: '番茄鸡蛋面', foodDetail: '少油少盐', location: '第一食堂', source: 'CANTEEN', isAutoImport: true, calories: 520, price: 12.5, createTime: now } as any,
    { id: 203, userId: 1, dietDate: date, mealType: 'DINNER', foodName: '烤鸡腿饭', foodDetail: '', location: '第二食堂', source: 'CANTEEN', isAutoImport: true, calories: 780, price: 18, createTime: now } as any,
    { id: 204, userId: 1, dietDate: date, mealType: 'SNACK', foodName: '酸奶', foodDetail: '', location: '校园超市', source: 'OTHER', isAutoImport: false, calories: 160, price: 5.9, createTime: now } as any
  ]
}

async function autoImport() {
  if (userStore.isDemoLogin) {
    toast('演示模式：不执行导入')
    return
  }
  if (!userStore.isApiReady) {
    toast('请先登录再导入')
    return
  }
  try {
    toast('导入中...')
    await autoImportDietFromEcard({ startDate: selectedDate.value, endDate: selectedDate.value })
    toast('导入完成')
    await load()
  } catch (e: any) {
    toast(e?.message || '导入失败')
  }
}

async function load() {
  errorMsg.value = ''
  if (userStore.isDemoLogin) {
    records.value = buildDemoRecords(selectedDate.value)
    return
  }
  if (!userStore.isApiReady) {
    records.value = []
    return
  }
  loading.value = true
  try {
    records.value = await getDietRecordsByDate(selectedDate.value)
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
.topbar-left { display: flex; flex-direction: column; gap: 4px; }
.page-title { font-size: 22px; font-weight: 800; color: #2a2a2a; }
.page-subtitle { font-size: 13px; color: #666; }
.topbar-right { display:flex; justify-content:flex-end; }
.btn { height:36px; padding:0 12px; border-radius:12px; border:1px solid rgba(255,107,157,.25); background:rgba(255,255,255,.9); color:#ff6b9d; cursor:pointer; display:inline-flex; align-items:center; gap:8px; font-weight:600; text-decoration:none; }
.btn:disabled { cursor:not-allowed; opacity:.65; }
.btn.primary { border:none; background:linear-gradient(135deg,#ff8fb3 0%,#ff6b9d 100%); color:#fff; }
.icon-btn { width:40px; height:40px; border-radius:14px; border:1px solid rgba(255,107,157,.22); background:rgba(255,255,255,.9); color:#ff6b9d; cursor:pointer; display:inline-flex; align-items:center; justify-content:center; }
.date-row { display:flex; gap:12px; align-items:center; justify-content:space-between; flex-wrap:wrap; }
.date-switch { display:flex; align-items:center; gap:10px; }
.date-center { display:flex; flex-direction:column; align-items:center; gap:2px; padding:4px 8px; border-radius:12px; }
.date-main { font-weight:900; color:#333; }
.date-sub { font-size:12px; color:#777; }
.date-input-wrap { display:flex; align-items:center; gap:8px; padding:8px 10px; border-radius:12px; border:1px solid rgba(255,107,157,.18); background:rgba(255,255,255,.85); }
.label-mini { font-size:12px; color:#777; font-weight:700; }
.date-input { border:none; background:transparent; outline:none; color:#333; font-weight:700; }
.filter-row { display:flex; gap:10px; align-items:center; justify-content:space-between; flex-wrap:wrap; }
.chips { display:flex; gap:8px; flex-wrap:wrap; }
.chip { height:30px; padding:0 10px; border-radius:999px; border:1px solid rgba(255,107,157,.22); background:rgba(255,255,255,.9); color:#ff6b9d; cursor:pointer; font-weight:700; font-size:12px; display:inline-flex; align-items:center; gap:6px; }
.chip.ghost { color:#666; border-color:rgba(0,0,0,.08); }
.chip.active { border:none; background:linear-gradient(135deg,#ff8fb3 0%,#ff6b9d 100%); color:#fff; }
.callout { margin-top:12px; padding:14px 16px; display:flex; align-items:center; justify-content:space-between; gap:12px; }
.callout-title { font-weight:800; color:#333; }
.callout-desc { margin-top:4px; font-size:13px; color:#666; }
.overview-grid { margin-top:12px; display:grid; grid-template-columns:1.35fr 1fr; gap:12px; }
@media (max-width:960px){ .overview-grid{ grid-template-columns:1fr; } }
.overview { padding:16px; color:#fff; border:none; }
.overview.gradient { background:linear-gradient(135deg,#a78bfa 0%,#ff7fb2 55%,#ff4fa0 100%); box-shadow:0 18px 38px rgba(255,79,160,.18); }
.ov-head { display:flex; align-items:center; justify-content:space-between; }
.ov-title { font-size:13px; opacity:.9; font-weight:700; }
.ov-sub { margin-top:4px; font-size:12px; opacity:.85; }
.ov-icon { width:44px; height:44px; border-radius:14px; background:rgba(255,255,255,.22); display:flex; align-items:center; justify-content:center; }
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
.section-head { padding:14px 16px 10px; display:flex; align-items:flex-start; justify-content:space-between; flex-wrap:wrap; gap:12px; }
.section-title { font-weight:900; color:#333; }
.section-subtitle { margin-top:4px; font-size:12px; color:#777; }
.state { padding:18px 16px; color:#666; }
.state.warn { color:#d83a56; }
.meals { padding:0 16px 16px; display:grid; gap:12px; }
.meal-head { display:flex; align-items:center; justify-content:space-between; gap:12px; padding:12px; border-radius:16px; border:1px solid rgba(0,0,0,.06); cursor:pointer; }
.meal-left { display:flex; align-items:center; gap:10px; }
.meal-icon { width:36px; height:36px; border-radius:14px; display:flex; align-items:center; justify-content:center; color:#ff2d7a; box-shadow:0 10px 20px rgba(0,0,0,.06); }
.meal-name { font-weight:900; color:#333; }
.meal-sub { margin-top:2px; font-size:12px; color:#666; }
.meal-right { display:flex; align-items:center; gap:10px; color:#333; font-weight:900; }
.meal-sum { font-size:13px; }
.meal-list { margin-top:8px; padding:0 4px 4px; display:grid; gap:8px; }
.record { display:flex; align-items:center; justify-content:space-between; gap:12px; padding:12px; border-radius:14px; background:rgba(255,255,255,.92); border:1px solid rgba(0,0,0,.06); cursor:pointer; }
.rec-title { font-weight:900; color:#333; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.detail { color:#666; font-weight:700; }
.rec-meta { margin-top:4px; font-size:12px; color:#777; display:flex; flex-wrap:wrap; gap:6px; align-items:center; }
.tag { padding:2px 8px; border-radius:999px; background:rgba(0,0,0,.06); color:#555; font-weight:800; font-size:11px; }
.tag.auto { background:rgba(167,139,250,.18); color:#6d28d9; }
.tag.kcal { background:rgba(255,107,157,.14); color:#ff2d7a; }
.rec-right { display:flex; align-items:center; gap:10px; color:#999; }
.price { font-weight:900; color:#ff2d7a; }
.meal-empty { padding:12px; border-radius:14px; border:1px dashed rgba(0,0,0,.12); background:rgba(255,255,255,.7); display:flex; align-items:center; justify-content:space-between; gap:10px; }
.meal-empty-text { color:#666; font-weight:700; font-size:13px; }
.empty { padding:20px 16px 16px; }
.empty-hero { padding:16px; border-radius:16px; border:1px dashed rgba(255,107,157,.28); background:rgba(255,255,255,.75); text-align:center; }
.empty-icon { width:56px; height:56px; border-radius:16px; margin:0 auto 10px; background:rgba(255,107,157,.14); color:#ff2d7a; display:flex; align-items:center; justify-content:center; }
.empty-title { font-weight:900; color:#333; }
.empty-desc { margin-top:6px; font-size:13px; color:#666; }
.empty-actions { margin-top:12px; display:flex; justify-content:center; }
.fab { position:fixed; right:18px; bottom:18px; width:56px; height:56px; border-radius:18px; border:none; background:linear-gradient(135deg,#ff8fb3 0%,#ff6b9d 100%); color:#fff; box-shadow:0 16px 30px rgba(255,107,157,.3); cursor:pointer; display:flex; align-items:center; justify-content:center; z-index:10; }
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
