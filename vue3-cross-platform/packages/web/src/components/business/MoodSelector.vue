<template>
  <div class="mood-selector">
    <h3 class="selector-title">{{ title }}</h3>
    <div class="mood-grid">
      <div
        v-for="(mood, index) in moods"
        :key="index"
        class="mood-item"
        :class="{ 'selected': selectedMood === mood.icon }"
        @click="selectMood(mood.icon)"
      >
        <i :class="mood.icon" class="mood-icon"></i>
        <span class="mood-label">{{ mood.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue'

interface Props {
  title?: string
  selectedMood?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '选择心情',
  selectedMood: ''
})

const emit = defineEmits<{
  'select-mood': [moodIcon: string]
}>()

// 心情图标列表
const moods = ref([
  { icon: 'fas fa-smile-beam', label: '开心' },
  { icon: 'fas fa-smile', label: '微笑' },
  { icon: 'fas fa-meh', label: '一般' },
  { icon: 'fas fa-frown', label: '难过' },
  { icon: 'fas fa-angry', label: '生气' },
  { icon: 'fas fa-sad-tear', label: '悲伤' },
  { icon: 'fas fa-surprise', label: '惊讶' }
])

// 当前选中的心情
const selectedMood = ref(props.selectedMood)

// 选择心情
const selectMood = (moodIcon: string) => {
  selectedMood.value = moodIcon
  emit('select-mood', moodIcon)
}
</script>

<style scoped lang="scss">
.mood-selector {
  padding: 20px;
}

.selector-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.mood-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 15px;
  justify-items: center;
}

.mood-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s;
  width: 60px;

  &:hover {
    background: rgba(255, 107, 157, 0.1);
    transform: translateY(-2px);
  }

  &.selected {
    background: rgba(255, 107, 157, 0.2);
    border: 2px solid #FF6B9D;
  }
}

.mood-icon {
  font-size: 28px;
  color: #FF6B9D;
  margin-bottom: 5px;
  transition: transform 0.3s;

  .selected & {
    transform: scale(1.2);
  }
}

.mood-label {
  font-size: 12px;
  color: #666;
  text-align: center;
}

// 响应式设计
@media (max-width: 768px) {
  .mood-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 480px) {
  .mood-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
