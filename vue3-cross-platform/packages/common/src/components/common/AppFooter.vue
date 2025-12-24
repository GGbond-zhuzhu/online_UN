<template>
  <!-- App 端通用页脚组件：放在页面底部展示版权和简单说明 -->
  <view class="app-footer">
    <text class="footer-text">© 2024 上大学Online 校园综合服务平台</text>
    <text class="footer-sub-text">让校园生活更简单 · 本页面仅用于演示与调试</text>
  </view>
</template>

<script setup lang="ts">
// App 端页脚目前仅承担简单展示作用，无需额外逻辑
</script>

<style scoped lang="scss">
/* 整体容器样式 */
.app-footer {
  padding: 24rpx 20rpx; // 内边距，上下稍大一些
  background-color: #f5f5f5; // 使用浅灰背景与页面主体区分
  align-items: center; // 垂直居中文本
  justify-content: center; // 水平居中文本
  display: flex; // 使用 flex 垂直排列两行文字
  flex-direction: column; // 垂直方向布局
}

/* 主文字样式 */
.footer-text {
  font-size: 22rpx; // 主文字字号
  color: #666666; // 使用中灰色
}

/* 副文字样式 */
.footer-sub-text {
  margin-top: 8rpx; // 与主文字之间留出间距
  font-size: 20rpx; // 副文字字号略小
  color: #999999; // 使用更浅的灰色
}
</style>
