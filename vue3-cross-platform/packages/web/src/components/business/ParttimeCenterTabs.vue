<template>
  <div class="pt-center-tabs">
    <button
      class="tab"
      :class="{ active: activeKey === 'favorites' }"
      type="button"
      @click="go('favorites')"
    >
      <i class="fas fa-heart"></i>
      <span>我的收藏</span>
      <span class="badge">{{ favoriteCount }}</span>
    </button>

    <button
      class="tab"
      :class="{ active: activeKey === 'history' }"
      type="button"
      @click="go('history')"
    >
      <i class="fas fa-history"></i>
      <span>浏览记录</span>
      <span class="badge">{{ historyCount }}</span>
    </button>

    <button
      class="tab"
      :class="{ active: activeKey === 'applications' }"
      type="button"
      @click="go('applications')"
    >
      <i class="fas fa-file-alt"></i>
      <span>申请记录</span>
      <span class="badge">{{ applicationCount }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useParttimeStore } from '@campus/common'

const router = useRouter()
const route = useRoute()
const parttimeStore = useParttimeStore()

const activeKey = computed<'favorites' | 'history' | 'applications'>(() => {
  const p = route.path
  if (p.includes('/parttime/history')) return 'history'
  if (p.includes('/parttime/applications')) return 'applications'
  return 'favorites'
})

const favoriteCount = computed(() => parttimeStore.favoriteList?.length || 0)
const historyCount = computed(() => parttimeStore.browseHistory?.length || 0)
const applicationCount = computed(() => parttimeStore.myApplicationList?.length || 0)

const go = (key: 'favorites' | 'history' | 'applications') => {
  const map: Record<typeof key, string> = {
    favorites: '/parttime/favorites',
    history: '/parttime/history',
    applications: '/parttime/applications'
  }
  router.push(map[key])
}
</script>

<style scoped>
.pt-center-tabs {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  padding: 12px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(241, 217, 233, 0.9);
  box-shadow: 0 6px 18px rgba(216, 27, 96, 0.08);
  backdrop-filter: blur(8px);
}

.tab {
  border: 1px solid rgba(148, 163, 184, 0.25);
  background: #fff;
  border-radius: 14px;
  padding: 10px 12px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 700;
  color: #374151;
  transition: transform 0.1s ease, box-shadow 0.2s ease, border-color 0.2s ease;
  min-height: 44px;
}

.tab i {
  color: #d81b60;
}

.tab:hover {
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
}

.tab:active {
  transform: scale(0.99);
}

.tab.active {
  border-color: rgba(216, 27, 96, 0.35);
  box-shadow: 0 10px 26px rgba(216, 27, 96, 0.14);
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.badge {
  margin-left: 2px;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  background: rgba(216, 27, 96, 0.12);
  color: #c2185b;
}

@media (max-width: 640px) {
  .pt-center-tabs {
    grid-template-columns: 1fr;
  }
  .tab {
    justify-content: space-between;
  }
}
</style>
