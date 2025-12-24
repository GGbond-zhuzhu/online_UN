<template>
  <!-- 行程卡片外层容器，根据 type 区分个人行程或团队行程 -->
  <div class="schedule-card" @click="handleClick">
    <!-- 左侧时间轴装饰条，突出时间属性 -->
    <div class="time-bar"></div>

    <!-- 右侧主要内容区域 -->
    <div class="card-content">
      <!-- 标题与类型标签行 -->
      <div class="card-header-row">
        <h3 class="card-title">{{ schedule.title }}</h3>
        <span class="type-tag">{{ typeLabel }}</span>
      </div>

      <!-- 时间范围显示 -->
      <div class="time-range">
        <i class="fas fa-clock"></i>
        <span>{{ schedule.startTime }} ~ {{ schedule.endTime }}</span>
      </div>

      <!-- 位置与团队信息行（根据类型按需展示） -->
      <div class="meta-row">
        <span class="meta-item" v-if="schedule.location">
          <i class="fas fa-map-marker-alt"></i>
          {{ schedule.location }}
        </span>
        <span class="meta-item" v-if="isTeam">
          <i class="fas fa-users"></i>
          {{ teamLabel }}
        </span>
      </div>

      <!-- 描述摘要 -->
      <p v-if="descriptionSummary" class="card-desc">{{ descriptionSummary }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
// 引入 computed 创建派生数据
import { computed } from 'vue' // 从 Vue 导入 computed，用于生成展示用的派生字段

// 引入个人行程与团队行程类型定义
import type { PersonalSchedule, TeamSchedule } from '../../api/schedule' // 从行程接口模块引入类型

// 定义组件接收的属性
const props = defineProps<{ // 使用 defineProps 声明组件属性
  type: 'personal' | 'team' // type：行程类型，personal 表示个人行程，team 表示团队行程
  schedule: PersonalSchedule | TeamSchedule // schedule：具体的一条行程数据，类型为联合类型
}>()

// 定义组件对外发出的事件
const emit = defineEmits<{ // 使用 defineEmits 声明事件类型
  (e: 'click', id: number): void // click 事件：当用户点击行程卡片时触发，并携带行程 id
}>()

// 是否为团队行程的计算属性
const isTeam = computed(() => props.type === 'team') // isTeam：根据 type 判断是否为团队行程

// 行程类型中文标签
const typeLabel = computed(() => { // typeLabel：根据 type 返回中文说明
  return isTeam.value ? '团队行程' : '个人行程' // 如果是团队行程则显示“团队行程”，否则显示“个人行程”
})

// 团队信息标签（仅团队行程有意义）
const teamLabel = computed(() => { // teamLabel：团队行程的团队名称说明
  if (!isTeam.value) return '' // 如果不是团队行程则返回空字符串
  const teamSchedule = props.schedule as TeamSchedule // 将 schedule 断言为 TeamSchedule 类型
  return `${teamSchedule.teamName} · ${teamSchedule.attendees.length} 人` // 拼接团队名称与参与人数
})

// 描述摘要，避免行程描述过长影响卡片布局
const descriptionSummary = computed(() => { // descriptionSummary：对描述字段做截断处理后的摘要
  const desc = props.schedule.description || '' // 读取描述字段，若为空则使用空字符串
  if (!desc) return '' // 如果本身就没有描述，则不在卡片中展示
  return desc.length > 40 ? desc.slice(0, 40) + '…' : desc // 超过 40 字符则截断加省略号
})

// 行程卡片点击事件处理函数
const handleClick = () => { // handleClick：点击卡片时触发
  emit('click', props.schedule.id) // 触发 click 事件，将行程 id 传递给父组件
}
</script>

<style scoped>
/* 行程卡片外层容器样式 */
.schedule-card {
  display: flex; /* 使用 flex 将时间条与内容横向排列 */
  gap: 10px; /* 时间条与内容区域之间保留间距 */
  padding: 12px 14px; /* 设置内边距让内容更舒适 */
  border-radius: 12px; /* 使用圆角与全站卡片风格保持一致 */
  background-color: #ffffff; /* 白色背景突出卡片 */
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05); /* 轻微阴影提升层次感 */
  cursor: pointer; /* 鼠标移入显示手型 */
  transition: transform 0.15s ease, box-shadow 0.15s ease; /* 添加悬停动效 */
}

/* 悬停状态样式 */
.schedule-card:hover {
  transform: translateY(-2px); /* 上移 2 像素形成悬浮效果 */
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.12); /* 加强阴影增加视觉层次 */
}

/* 左侧时间条装饰样式 */
.time-bar {
  width: 4px; /* 时间条宽度较窄，只起装饰和引导作用 */
  border-radius: 999px; /* 使用胶囊圆角 */
  background: linear-gradient(180deg, #d81b60 0%, #f48fb1 100%); /* 使用主色渐变增强视觉效果 */
  flex-shrink: 0; /* 防止在窄屏下被压缩 */
}

/* 右侧内容区域样式 */
.card-content {
  flex: 1; /* 占据剩余所有宽度 */
  display: flex; /* 使用 flex 列布局 */
  flex-direction: column; /* 垂直排列内部元素 */
  gap: 4px; /* 行与行之间留出小间距 */
}

/* 标题与类型标签行样式 */
.card-header-row {
  display: flex; /* 使用 flex 将标题与标签放在同一行内 */
  justify-content: space-between; /* 标题靠左，标签靠右 */
  align-items: center; /* 垂直居中对齐 */
  gap: 8px; /* 两侧之间保留间距 */
}

/* 行程标题样式 */
.card-title {
  font-size: 15px; /* 标题字体大小中等偏大 */
  font-weight: 600; /* 半粗体突出行程名称 */
  color: #333333; /* 使用深色文字增强可读性 */
  margin: 0; /* 去除默认外边距 */
}

/* 行程类型标签样式 */
.type-tag {
  font-size: 12px; /* 标签使用较小字号 */
  padding: 2px 8px; /* 内边距让标签更易读 */
  border-radius: 999px; /* 使用胶囊圆角形式 */
  background-color: #fce4ec; /* 使用浅粉色背景 */
  color: #d81b60; /* 使用主色文字与背景呼应 */
  flex-shrink: 0; /* 防止在窄屏时文字被压缩 */
}

/* 时间范围行样式 */
.time-range {
  font-size: 12px; /* 使用较小字号显示时间范围 */
  color: #666666; /* 使用中灰色文字 */
  display: inline-flex; /* 图标与文字在同一行内对齐 */
  align-items: center; /* 垂直居中图标和文字 */
  gap: 4px; /* 图标与文字之间保留间距 */
}

/* 时间范围图标样式 */
.time-range i {
  font-size: 11px; /* 图标略小于文字 */
}

/* 元信息行样式，包含地点和团队信息 */
.meta-row {
  display: flex; /* 使用 flex 将多条元信息横向排列 */
  flex-wrap: wrap; /* 在窄屏下允许换行 */
  gap: 8px; /* 每条信息之间留出间距 */
}

/* 单条元信息样式 */
.meta-item {
  font-size: 12px; /* 使用较小字号显示辅助信息 */
  color: #999999; /* 使用浅灰色文字弱化显示 */
  display: inline-flex; /* 图标与文字在一行内对齐 */
  align-items: center; /* 垂直居中 */
  gap: 4px; /* 图标与文字之间的间距 */
}

/* 元信息中的图标样式 */
.meta-item i {
  font-size: 11px; /* 图标比文字略小一号 */
}

/* 描述摘要样式 */
.card-desc {
  font-size: 12px; /* 描述文字使用较小字号 */
  color: #666666; /* 使用中灰色增强可读性 */
  margin: 0; /* 去掉默认外边距 */
}

/* 小屏幕适配：略微压缩内边距和字号 */
@media (max-width: 600px) {
  .schedule-card {
    padding: 10px; /* 减小内边距以适应窄屏 */
  }

  .card-title {
    font-size: 14px; /* 标题字号略微减小 */
  }

  .card-desc {
    font-size: 11px; /* 描述文字进一步减小字号 */
  }
}
</style>
