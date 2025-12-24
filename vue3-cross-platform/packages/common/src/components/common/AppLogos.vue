<template>
  <!-- App 端通用 Logo 组件：在登录页、首页等位置统一展示品牌标识 -->
  <view class="app-logos">
    <!-- 品牌主标识：上大学 -->
    <text class="logo-main">上大学</text>
    <!-- 品牌副标识：Online -->
    <text class="logo-sub">Online</text>
    <!-- 品牌口号：校园综合服务平台 -->
    <text class="logo-slogan">校园综合服务平台</text>
  </view>
</template>

<script setup lang="ts">
// 目前 App 端 Logo 仅作为静态展示组件使用
// 如后续需要根据主题/学校动态切换文案，可在此增加 props 和计算属性
</script>

<style scoped lang="scss">
/* 外层容器：水平排列主标识、副标识和口号 */
.app-logos {
  flex-direction: row; // 横向布局
  display: flex; // 使用 flex 布局使子元素在一行展示
  align-items: baseline; // 按基线对齐文字，视觉更平衡
}

/* 主标识样式：字号最大、颜色最醒目 */
.logo-main {
  font-size: 36rpx; // 使用 rpx 适配不同屏幕宽度
  font-weight: 700; // 粗体突出品牌
  color: #d81b60; // 使用与 Web 端相同的主色
}

/* 副标识样式：英文 Online，略小一号 */
.logo-sub {
  font-size: 28rpx; // 略小于主标识
  font-weight: 600; // 半粗体
  color: #d81b60; // 与主标识同色，保持统一
  margin-left: 8rpx; // 与主标识之间保留间距
}

/* 口号样式：使用中性灰色，作为辅助说明 */
.logo-slogan {
  font-size: 22rpx; // 较小字号，减少视觉负担
  color: #666666; // 中灰色文字
  margin-left: 12rpx; // 与主副标识之间保持一定距离
}
</style>
