<template>
  <!--
    通用“发布信息”表单容器
    说明：本组件只负责整体布局和按钮，具体表单项由插槽传入，
    可同时服务二手发布、兼职发布等不同业务场景。
  -->
  <section class="publish-form">
    <!-- 标题与副标题区域 -->
    <header class="publish-header">
      <h2 class="publish-title">{{ title }}</h2>
      <p v-if="subtitle" class="publish-subtitle">{{ subtitle }}</p>
    </header>

    <!-- 表单主体内容区域，由默认插槽承载具体表单项 -->
    <div class="publish-body">
      <slot></slot>
    </div>

    <!-- 底部操作区域：发布 / 重置 按钮，并支持自定义插槽扩展其他按钮 -->
    <footer class="publish-footer">
      <div class="footer-left">
        <slot name="footer-left"></slot>
      </div>
      <div class="footer-actions">
        <button
          class="btn outline"
          type="button"
          @click="handleReset"
        >
          {{ resetText }}
        </button>
        <button
          class="btn primary"
          type="button"
          :disabled="loading"
          @click="handleSubmit"
        >
          <!-- 如果处于 loading 状态，则显示“发布中...” -->
          {{ loading ? submittingText : submitText }}
        </button>
      </div>
      <div class="footer-right">
        <slot name="footer-right"></slot>
      </div>
    </footer>
  </section>
</template>

<script setup lang="ts">
// 定义组件接收的属性
const props = defineProps<{ // 使用 defineProps 声明属性
  title?: string // title：表单标题，例如“发布二手商品”
  subtitle?: string // subtitle：副标题说明文字
  submitText?: string // submitText：发布按钮文字
  submittingText?: string // submittingText：发布中状态按钮文字
  resetText?: string // resetText：重置按钮文字
  loading?: boolean // loading：是否处于发布中状态
}>()

// 定义组件对外发出的事件
const emit = defineEmits<{ // 使用 defineEmits 声明事件类型
  (e: 'submit'): void // submit 事件：点击发布按钮时触发
  (e: 'reset'): void // reset 事件：点击重置按钮时触发
}>()

// 处理发布按钮点击事件
const handleSubmit = () => { // handleSubmit：点击发布按钮时调用
  emit('submit') // 通过 submit 事件通知父组件
}

// 处理重置按钮点击事件
const handleReset = () => { // handleReset：点击重置按钮时调用
  emit('reset') // 通过 reset 事件通知父组件
}

// 为按钮文字提供默认值
const submitText = props.submitText || '发布' // submitText：默认按钮文字“发布”
const submittingText = props.submittingText || '发布中...' // submittingText：默认加载中文案
const resetText = props.resetText || '重置' // resetText：默认重置按钮文字
</script>

<style scoped>
/* 发布表单整体容器样式 */
.publish-form {
  background-color: #ffffff; /* 使用白色背景承载表单 */
  border-radius: 16px; /* 使用较大的圆角，靠近设计稿风格 */
  padding: 20px 22px; /* 内边距让内容更舒展 */
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08); /* 添加阴影增强层次感 */
  box-sizing: border-box; /* 使用 border-box 计算宽度 */
}

/* 标题区域样式 */
.publish-header {
  margin-bottom: 16px; /* 标题与表单主体之间留出间距 */
}

/* 标题文字样式 */
.publish-title {
  font-size: 18px; /* 标题字号略大 */
  font-weight: 600; /* 半粗体提升权重 */
  color: #333333; /* 深色文字提高可读性 */
  margin: 0; /* 去掉默认外边距 */
}

/* 副标题文字样式 */
.publish-subtitle {
  margin: 6px 0 0; /* 与标题之间留出少量间距 */
  font-size: 13px; /* 副标题使用较小字号 */
  color: #666666; /* 使用中灰色文字 */
}

/* 表单主体区域样式 */
.publish-body {
  margin-bottom: 16px; /* 与底部操作区域之间留出间距 */
}

/* 底部操作区域整体布局样式 */
.publish-footer {
  display: flex; /* 使用 flex 将三块区域放在一行 */
  align-items: center; /* 垂直居中对齐 */
  justify-content: space-between; /* 左中右区域分别贴边 */
  gap: 12px; /* 区域之间留出间距 */
}

/* 左侧插槽区域：通常用于提示信息或标签 */
.footer-left {
  flex: 1; /* 占据左侧可用空间 */
}

/* 右侧插槽区域：可用于放置额外按钮或链接 */
.footer-right {
  flex: 1; /* 占据右侧可用空间 */
  display: flex; /* 可根据需要调整内部布局 */
  justify-content: flex-end; /* 默认靠右对齐 */
}

/* 中间按钮区域样式 */
.footer-actions {
  display: flex; /* 使用 flex 将两个按钮放在一行 */
  align-items: center; /* 垂直居中按钮 */
  gap: 10px; /* 按钮之间留出水平间距 */
}

/* 按钮基础样式 */
.btn {
  min-width: 100px; /* 给按钮设置最小宽度，保证点击面积 */
  padding: 8px 16px; /* 内边距让按钮更舒适 */
  border-radius: 999px; /* 使用胶囊形状圆角 */
  font-size: 14px; /* 按钮文字字号 */
  cursor: pointer; /* 鼠标移入时显示手型 */
  border: 1px solid transparent; /* 预留边框位置避免 hover 抖动 */
  transition: all 0.2s ease; /* 添加动态过渡效果 */
}

/* 主按钮样式：用于“发布”操作 */
.btn.primary {
  background-color: #d81b60; /* 使用品牌主色作为背景 */
  color: #ffffff; /* 按钮文字使用白色 */
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3); /* 添加阴影突出主按钮 */
}

/* 主按钮悬停样式 */
.btn.primary:hover:enabled {
  background-color: #c2185b; /* 悬停时略微加深背景色 */
}

/* 描边按钮样式：用于“重置”等次要操作 */
.btn.outline {
  background-color: #ffffff; /* 使用白色背景 */
  color: #d81b60; /* 使用品牌主色文字 */
  border-color: #d81b60; /* 使用品牌主色描边 */
}

/* 描边按钮悬停样式 */
.btn.outline:hover {
  background-color: #fce4ec; /* 悬停时使用浅粉色背景 */
}

/* 按钮禁用状态样式 */
.btn:disabled {
  cursor: not-allowed; /* 显示禁止符号 */
  opacity: 0.6; /* 降低不透明度，弱化存在感 */
}
</style>
