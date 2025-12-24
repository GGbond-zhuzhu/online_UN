<template>
  <!-- 通用输入项组件：App 端表单中可复用的带标签输入框 -->
  <view class="input-entry">
    <!-- 顶部标签区域：展示输入的含义，例如“手机号”、“验证码”等 -->
    <view v-if="label" class="input-label">
      <text>{{ label }}</text>
      <!-- 必填星号提示 -->
      <text v-if="required" class="required-mark">*</text>
    </view>

    <!-- 输入区域容器：左侧可选图标 + 原生 input -->
    <view class="input-wrapper">
      <!-- 左侧图标，当传入 iconClass 时显示 Font Awesome 图标 -->
      <view v-if="iconClass" class="icon-wrapper">
        <i :class="['input-icon', iconClass]"></i>
      </view>

      <!-- 原生 input 组件，使用 v-model 双向绑定外部值 -->
      <input
        class="input-control"
        :type="type"
        :placeholder="placeholder"
        :value="modelValue"
        @input="onInput"
      />
    </view>

    <!-- 底部辅助说明文字，用于错误提示或提示信息 -->
    <view v-if="helpText" class="input-help">
      <text>{{ helpText }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
// 引入类型辅助方法，用于声明 props 和 emits
import { defineProps, defineEmits } from 'vue' // 从 Vue 中导入 defineProps 和 defineEmits

// 定义组件属性类型
const props = defineProps<{ // 使用泛型声明 props 结构
  label?: string // label：输入标签文字
  modelValue?: string // modelValue：双向绑定的表单值
  placeholder?: string // placeholder：占位提示文字
  type?: string // type：输入类型，例如 text/password/number 等
  iconClass?: string // iconClass：可选的字体图标类名
  required?: boolean // required：是否必填
  helpText?: string // helpText：底部提示文字
}>()

// 定义组件对外发出的事件
const emit = defineEmits<{ // 使用泛型声明 emits 类型
  (e: 'update:modelValue', value: string): void // 当输入变化时触发 update:modelValue 事件
}>()

// 处理 input 事件，将原生 input 的值同步给外部 v-model
const onInput = (event: Event) => { // onInput：输入框内容变化时的回调
  const target = event.target as HTMLInputElement // 将事件目标断言为 HTMLInputElement
  emit('update:modelValue', target.value) // 通过 emit 派发事件，将最新值传给父组件
}
</script>

<style scoped lang="scss">
/* 整体容器样式 */
.input-entry {
  display: flex; // 使用 flex 垂直排列标签、输入框和说明文字
  flex-direction: column; // 垂直方向布局
  margin-bottom: 24rpx; // 每个输入项之间保留间距
}

/* 标签区域样式 */
.input-label {
  flex-direction: row; // 标签与星号横向排列
  display: flex; // 使用 flex 布局
  align-items: center; // 垂直居中
  margin-bottom: 8rpx; // 标签与输入框之间留出距离
  font-size: 26rpx; // 标签文字字号
  color: #333333; // 深色文字
}

/* 必填星号样式 */
.required-mark {
  color: #f44336; // 使用红色提示必填
  margin-left: 4rpx; // 与标签文字之间留出间距
}

/* 输入区域外层容器样式 */
.input-wrapper {
  flex-direction: row; // 图标与输入框横向排列
  display: flex; // 使用 flex 布局
  align-items: center; // 垂直居中
  padding: 18rpx 20rpx; // 内边距提升可触区域
  border-radius: 16rpx; // 圆角边框
  border-width: 1rpx; // 边框宽度
  border-style: solid; // 边框样式
  border-color: #dddddd; // 边框颜色
  background-color: #ffffff; // 背景色白色
}

/* 左侧图标外层容器样式 */
.icon-wrapper {
  margin-right: 12rpx; // 图标与输入框之间留出间距
}

/* 字体图标样式 */
.input-icon {
  font-size: 28rpx; // 图标大小
  color: #999999; // 图标颜色为浅灰
}

/* 原生 input 样式 */
.input-control {
  flex: 1; // 占据剩余宽度
  border-width: 0; // 去掉原生边框
  background-color: transparent; // 背景透明
  font-size: 28rpx; // 输入文字字号
  color: #333333; // 文字颜色
}

/* 占位符样式 */
.input-control::placeholder {
  color: #bbbbbb; // 占位符使用更浅的灰色
}

/* 底部辅助文字样式 */
.input-help {
  margin-top: 6rpx; // 与输入框之间留出间距
}

.input-help text {
  font-size: 22rpx; // 提示文字字号
  color: #999999; // 使用浅灰色文字
}
</style>
