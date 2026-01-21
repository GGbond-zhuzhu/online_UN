<template>
  <!-- 兼职岗位卡片外层容器，整体点击可进入详情页或申请页 -->
  <div class="parttime-card" @click="handleClick">
    <!-- 左侧图标或公司首字母区域，用于快速识别岗位类型 -->
    <div class="card-icon">
      <div class="icon-circle">
        <i class="fas fa-briefcase"></i>
      </div>
    </div>

    <!-- 右侧岗位信息区域 -->
    <div class="card-content">
      <!-- 标题与薪资行 -->
      <div class="card-header-row">
        <h3 class="card-title">{{ job.title }}</h3>
        <div class="salary-area">
          <span class="salary-value">¥{{ job.salary.toFixed(2) }}</span>
          <span class="salary-type">/{{ job.salaryType }}</span>
        </div>
      </div>

      <!-- 公司与地点信息 -->
      <div class="card-sub-row">
        <div class="company-info-wrapper">
          <span class="company-name">{{ job.publisherName || '未填写发布方' }}</span>
          <!-- 企业认证标识 - 使用柔和玫红色，增大尺寸和间距 -->
          <span class="verified-badge" v-if="(job as any).verified">
            <i class="fas fa-check-circle"></i>
            <span>企业认证</span>
          </span>
        </div>
        <span class="location">
          <i class="fas fa-map-marker-alt"></i>
          {{ job.location }} · {{ job.campusName }}
        </span>
      </div>

      <!-- 描述摘要 -->
      <p class="card-desc">{{ shortDescription }}</p>

      <!-- 底部元信息：工作时间与发布时间等 -->
      <div class="card-meta-row">
        <span class="meta-item">
          <i class="fas fa-clock"></i>
          {{ job.workTime }}
        </span>
        <span class="meta-item">
          <i class="fas fa-calendar-alt"></i>
          {{ job.createTime }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 引入 computed 创建派生数据
import { computed } from 'vue' // 从 Vue 导入 computed，用于生成摘要等展示字段

// 引入 ParttimeJob 类型，保证字段与后端接口一致
import type { ParttimeJob } from '../../api/parttime' // 从兼职接口模块引入 ParttimeJob 类型

// 定义组件接收的属性
const props = defineProps<{ // 使用 defineProps 声明属性结构
  job: ParttimeJob // job：当前要展示的一条兼职岗位数据
}>()

// 定义组件对外发出的事件
const emit = defineEmits<{ // 使用 defineEmits 声明事件类型
  (e: 'click', id: number): void // click 事件：携带岗位 id，便于父组件跳转到详情或申请页
}>()

// 生成岗位描述的简短摘要，避免在卡片中显示过长文本
const shortDescription = computed(() => { // shortDescription：卡片中展示的岗位描述摘要
  const desc = props.job.description || '岗位描述暂未填写，详情请点击查看~' // 如果没有描述则使用默认文案
  return desc.length > 40 ? desc.slice(0, 40) + '…' : desc // 超过 40 个字符则截断并添加省略号
})

// 处理卡片点击事件，向父组件抛出 click 事件
const handleClick = () => { // handleClick：卡片点击事件处理函数
  emit('click', props.job.id) // 触发 click 事件，将岗位 id 传递给父组件
}
</script>

<style scoped>
/* 卡片外层容器样式 */
.parttime-card {
  display: flex; /* 使用 flex 将图标和内容横向排列 */
  gap: 12px; /* 左右区域之间保留间距 */
  padding: 14px; /* 统一内边距让内容更舒展 */
  border-radius: 12px; /* 圆角与全站卡片保持一致 */
  background-color: #ffffff; /* 白色背景突出卡片 */
  box-shadow: 0 4px 12px rgba(255, 182, 193, 0.08); /* 使用柔和的玫红色阴影，增强层次感 */
  border: 1px solid rgba(255, 182, 193, 0.15); /* 添加柔和的玫红色边框 */
  cursor: pointer; /* 鼠标移入时显示手型，提示可点击 */
  transition: transform 0.15s ease, box-shadow 0.15s ease; /* 添加悬停动效 */
}

/* 悬停时略微上移并增强阴影 */
.parttime-card:hover {
  transform: translateY(-2px); /* 上移 2 像素形成悬浮效果 */
  box-shadow: 0 8px 20px rgba(255, 182, 193, 0.15); /* 使用柔和的玫红色阴影，加深以强化视觉 */
  border-color: rgba(255, 182, 193, 0.25); /* 悬停时边框颜色加深 */
}

/* 左侧图标区域样式 */
.card-icon {
  display: flex; /* 使用 flex 居中内部圆形图标 */
  align-items: center; /* 垂直居中 */
}

/* 圆形图标背景样式 */
.icon-circle {
  width: 46px; /* 固定宽度形成圆形 */
  height: 46px; /* 固定高度形成圆形 */
  border-radius: 50%; /* 将方形变为圆形 */
  background: linear-gradient(135deg, rgba(255, 182, 193, 0.3) 0%, rgba(255, 192, 203, 0.25) 100%); /* 使用柔和的玫红色渐变背景，马卡龙风格 */
  display: flex; /* 使用 flex 居中图标 */
  align-items: center; /* 垂直居中图标 */
  justify-content: center; /* 水平居中图标 */
  color: #E91E63; /* 使用柔和的玫红色图标 */
  font-size: 20px; /* 图标尺寸适中 */
}

/* 右侧内容区域样式 */
.card-content {
  flex: 1; /* 占据除图标外的剩余宽度 */
  display: flex; /* 使用 flex 列布局 */
  flex-direction: column; /* 垂直排列内部元素 */
  gap: 6px; /* 各行之间保留适当间距 */
}

/* 标题与薪资行样式 */
.card-header-row {
  display: flex; /* 将标题和薪资放在同一行内 */
  justify-content: space-between; /* 标题在左，薪资在右 */
  align-items: flex-start; /* 顶部对齐避免多行标题导致错位 */
  gap: 8px; /* 两侧之间保留间距 */
}

/* 岗位标题样式 */
.card-title {
  font-size: 15px; /* 标题字体大小适中偏大 */
  font-weight: 600; /* 半粗体突出岗位名称 */
  color: #333333; /* 深色文字增强可读性 */
  margin: 0; /* 去掉默认外边距 */
}

/* 薪资区域样式 */
.salary-area {
  display: flex; /* 将薪资数值与单位放在一行内 */
  align-items: baseline; /* 数值与单位在基线对齐，视觉更统一 */
  gap: 2px; /* 数值与单位之间保留微小间距 */
}

/* 薪资数值样式 */
.salary-value {
  font-size: 16px; /* 数值稍大更显眼 */
  font-weight: 700; /* 使用粗体强调薪资 */
  color: #E91E63; /* 使用柔和的玫红色表示关键薪资信息 */
}

/* 薪资单位样式，例如“/小时”、“/天”等 */
.salary-type {
  font-size: 12px; /* 单位使用较小字号 */
  color: #666666; /* 使用中灰色弱化显示 */
}

/* 公司与地点行样式 */
.card-sub-row {
  display: flex; /* 使用 flex 横向排布公司与地点信息 */
  justify-content: space-between; /* 公司信息在左，地点信息在右 */
  align-items: center; /* 垂直方向居中对齐 */
  gap: 8px; /* 左右两侧之间保留适当间距 */
  flex-wrap: wrap; /* 允许换行，避免在小屏幕上拥挤 */
}

/* 公司信息包装器 - 增加间距，避免元素拥挤 */
.company-info-wrapper {
  display: flex; /* 使用 flex 横向排列公司名称和认证标识 */
  align-items: center; /* 垂直居中对齐 */
  gap: 8px; /* 公司名称与认证标识之间保留间距 */
  flex-wrap: wrap; /* 允许换行 */
}

/* 公司名称样式 */
.company-name {
  font-size: 13px; /* 使用中等字号显示公司名称 */
  color: #555555; /* 使用略浅的灰色 */
}

/* 企业认证标识 - 使用柔和玫红色，增大尺寸和间距，避免拥挤 */
.verified-badge {
  display: flex; /* 使用 flex 横向排列图标和文字 */
  align-items: center; /* 垂直居中对齐 */
  gap: 5px; /* 图标与文字之间保留间距 */
  padding: 4px 12px; /* 内边距，让标识更舒展 */
  background: linear-gradient(135deg, rgba(255, 182, 193, 0.25) 0%, rgba(255, 192, 203, 0.2) 100%); /* 使用柔和的玫红色渐变背景，马卡龙风格 */
  border-radius: 14px; /* 圆角设计，更圆润 */
  font-size: 11px; /* 字体大小适中 */
  color: #E91E63; /* 使用柔和的玫红色文字，不刺眼 */
  font-weight: 500; /* 加粗字体，突出重要性 */
  border: 1.5px solid rgba(255, 182, 193, 0.4); /* 柔和的玫红色边框 */
  box-shadow: 0 1px 4px rgba(255, 182, 193, 0.15); /* 柔和的阴影效果 */
}

/* 认证标识中的图标样式 */
.verified-badge i {
  font-size: 12px; /* 图标大小适中 */
}

/* 地点信息样式 */
.location {
  font-size: 12px; /* 辅助信息使用更小字号 */
  color: #999999; /* 使用浅灰色文字 */
  display: inline-flex; /* 使用 inline-flex 使图标和文字在一行内对齐 */
  align-items: center; /* 垂直居中图标与文字 */
  gap: 4px; /* 图标与文字之间保留间距 */
}

/* 地点前的小图标样式 */
.location i {
  font-size: 11px; /* 图标略小于文字 */
}

/* 描述摘要样式 */
.card-desc {
  font-size: 13px; /* 描述文字使用中等字号 */
  color: #666666; /* 使用中灰色增强可读性 */
  margin: 0; /* 去掉默认外边距 */
}

/* 底部元信息行样式 */
.card-meta-row {
  display: flex; /* 使用 flex 横向排列元信息 */
  gap: 12px; /* 不同元信息之间保留间距 */
  margin-top: 4px; /* 与描述之间留出少量空隙 */
}

/* 单条元信息样式 */
.meta-item {
  font-size: 12px; /* 使用较小字号显示辅助信息 */
  color: #999999; /* 使用浅灰色文字 */
  display: inline-flex; /* 使用 inline-flex 方便对齐图标与文字 */
  align-items: center; /* 垂直居中对齐 */
  gap: 4px; /* 图标与文字之间保留水平间距 */
}

/* 元信息中的图标样式 */
.meta-item i {
  font-size: 11px; /* 图标比文字略小 */
}

/* 小屏幕适配：在窄屏下稍微压缩布局 */
@media (max-width: 600px) {
  .parttime-card {
    padding: 10px; /* 减小内边距以适应小屏 */
  }

  .card-title {
    font-size: 14px; /* 标题字号略微减小 */
  }

  .card-desc {
    font-size: 12px; /* 描述文字也略微减小 */
  }
}
</style>
