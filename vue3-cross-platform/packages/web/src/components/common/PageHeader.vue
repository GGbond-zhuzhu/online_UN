<template>
  <!--
    通用页面头部组件
    说明：用于在各业务页面顶部展示标题、副标题和返回按钮，
    与首页、E卡通等页面的整体风格保持一致。
  -->
  <header class="page-header">
    <!-- 左侧：返回按钮（可选） + 标题、副标题 -->
    <div class="left">
      <!-- 返回按钮：当 showBack 为 true 时显示 -->
      <button
        v-if="showBack"
        class="back-btn"
        type="button"
        @click="handleBack"
      >
        <i class="fas fa-arrow-left"></i>
        <span>{{ backText }}</span>
      </button>

      <!-- 标题与副标题区域 -->
      <div class="titles">
        <h1 class="title">{{ title }}</h1>
        <p v-if="subtitle" class="subtitle">{{ subtitle }}</p>
      </div>
    </div>

    <!-- 右侧操作区域插槽，父组件可以放按钮、筛选等 -->
    <div class="right">
      <slot></slot>
    </div>
  </header>
</template>

<script setup lang="ts">
// 引入路由实例，用于在未监听 back 事件时执行默认返回逻辑
import { useRouter } from 'vue-router' // 从 vue-router 引入 useRouter 钩子

// 获取路由实例
const router = useRouter() // router：用于页面跳转和后退的实例

// 定义组件接收的属性
const props = defineProps<{ // 使用 defineProps 声明属性结构
  title: string // title：页面主标题
  subtitle?: string // subtitle：页面副标题，可选
  showBack?: boolean // showBack：是否显示返回按钮，默认不显示
  backText?: string // backText：返回按钮文字，默认“返回”
}>()

// 定义组件对外发出的事件
const emit = defineEmits<{ // 使用 defineEmits 声明事件类型
  (e: 'back'): void // back 事件：当点击返回按钮时触发
}>()

// 返回按钮文字默认值
const backText = props.backText || '返回' // backText：如果外部未传入，则默认为“返回”

// 是否展示返回按钮的最终结果
const showBack = props.showBack === true // showBack：仅当显式传入 true 时才显示返回按钮

// 处理返回按钮点击事件
const handleBack = () => { // handleBack：点击返回按钮时调用
  emit('back') // 先向外部抛出 back 事件，便于父组件自定义逻辑
  // 如果路由历史长度大于 1，则执行浏览器后退
  if (window.history.length > 1) { // 判断浏览器历史记录长度
    router.back() // 调用路由的返回方法
  } else { // 否则直接回到首页
    router.push('/') // 使用路由跳转到首页
  }
}
</script>

<style scoped>
/* 页面头部整体容器样式 */
.page-header {
  display: flex; /* 使用 flex 将左右区域放在一行 */
  justify-content: space-between; /* 左右两侧贴边对齐 */
  align-items: center; /* 垂直方向居中对齐 */
  padding: 16px 0; /* 上下内边距 */
  border-bottom: 1px solid #f0f0f0; /* 与下方内容之间使用浅色分割线 */
  margin-bottom: 16px; /* 与后续内容留出间距 */
}

/* 左侧区域布局样式 */
.left {
  display: flex; /* 使用 flex 将返回按钮和标题排在一行 */
  align-items: center; /* 垂直居中按钮和标题 */
  gap: 12px; /* 返回按钮与标题组之间留出间距 */
}

/* 返回按钮样式 */
.back-btn {
  display: inline-flex; /* 使用 inline-flex 方便与文字一起对齐 */
  align-items: center; /* 垂直居中图标和文字 */
  gap: 6px; /* 图标与文字之间留出水平间距 */
  padding: 6px 12px; /* 按钮内边距 */
  border-radius: 999px; /* 使用胶囊圆角 */
  border: 1px solid #d81b60; /* 使用品牌主色描边 */
  background-color: #ffffff; /* 背景色为白色 */
  color: #d81b60; /* 文字使用品牌主色 */
  font-size: 13px; /* 按钮文字字号 */
  cursor: pointer; /* 鼠标移入时显示手型 */
  transition: all 0.2s ease; /* 添加动态过渡效果 */
}

/* 返回按钮悬停态样式 */
.back-btn:hover {
  background-color: #fce4ec; /* 悬停时使用浅粉色背景 */
}

/* 标题区域样式 */
.titles {
  display: flex; /* 使用 flex 实现标题与副标题的纵向排列 */
  flex-direction: column; /* 垂直排列主标题和副标题 */
}

/* 主标题样式 */
.title {
  font-size: 20px; /* 主标题字号较大 */
  font-weight: 600; /* 半粗体突出标题 */
  color: #333333; /* 深色文字提高可读性 */
  margin: 0; /* 去掉默认外边距 */
}

/* 副标题样式 */
.subtitle {
  margin: 4px 0 0; /* 与主标题之间留出 4px 间距 */
  font-size: 13px; /* 副标题使用较小字号 */
  color: #666666; /* 使用中灰色文字 */
}

/* 右侧插槽区域样式 */
.right {
  display: flex; /* 使用 flex 自动排列插槽内的元素 */
  align-items: center; /* 垂直居中插槽内容 */
  gap: 8px; /* 插槽内元素之间留出间距 */
}

/* 小屏幕适配：在窄屏下改为上下布局 */
@media (max-width: 600px) {
  .page-header {
    flex-direction: column; /* 改为纵向排列 */
    align-items: flex-start; /* 左对齐内容 */
    gap: 10px; /* 区块之间留出垂直间距 */
  }

  .right {
    width: 100%; /* 右侧区域宽度撑满 */
    justify-content: flex-end; /* 内容靠右对齐 */
  }
}
</style>
