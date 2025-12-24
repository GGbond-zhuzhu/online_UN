<template>
  <!--
    通用 Excel/CSV 导入组件
    说明：用于在 Web 端选择本地 Excel/CSV 文件，并将文件或解析后的数据
    通过事件的方式抛给父组件处理。
  -->
  <div class="excel-import">
    <!-- 标题与说明区域 -->
    <div class="header">
      <div class="title">{{ title }}</div>
      <div class="desc">{{ description }}</div>
    </div>

    <!-- 文件选择区域 -->
    <div class="upload-area">
      <input
        ref="fileInputRef"
        type="file"
        class="file-input"
        :accept="accept"
        @change="handleFileChange"
      />
      <button class="upload-btn" @click="triggerSelect">选择文件</button>
      <span class="file-name" v-if="fileName">已选择：{{ fileName }}</span>
    </div>

    <!-- 状态反馈区域：显示错误信息或简单说明 -->
    <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
    <p v-else class="tip-text">支持 {{ accept }}，文件大小不超过 {{ maxSizeMB }}MB。</p>
  </div>
</template>

<script setup lang="ts">
// 引入 ref，用于保存选中的文件名与错误信息
import { ref } from 'vue' // 从 Vue 引入 ref 创建响应式数据

// 定义组件接收的属性
const props = defineProps<{ // 使用 defineProps 声明属性
  title?: string // title：标题文本
  description?: string // description：说明文本
  accept?: string // accept：允许选择的文件类型
  maxSizeMB?: number // maxSizeMB：允许的最大文件大小（单位：MB）
  autoParse?: boolean // autoParse：是否自动解析 CSV 文本为二维数组
}>()

// 定义组件对外发出的事件
const emit = defineEmits<{ // 使用 defineEmits 声明事件类型
  (e: 'file-selected', file: File): void // 当选择文件成功时抛出 file-selected 事件
  (e: 'parsed', rows: string[][]): void // 当自动解析成功时抛出 parsed 事件，携带二维数组
  (e: 'error', message: string): void // 当发生错误时抛出 error 事件，携带错误信息
}>()

// 文件 input 的引用，用于在点击“选择文件”按钮时触发原生选择框
const fileInputRef = ref<HTMLInputElement | null>(null) // fileInputRef：指向原生文件选择 input 的引用

// 已选择的文件名
const fileName = ref('') // fileName：展示给用户看的已选文件名

// 当前错误信息
const errorMessage = ref('') // errorMessage：用于展示错误提示文本

// 默认的 accept 值（若外部未传入则使用 CSV/Excel 常见类型）
const accept = props.accept || '.csv, application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' // accept：允许上传的文件类型字符串

// 默认的最大文件大小（MB）
const maxSizeMB = props.maxSizeMB ?? 5 // maxSizeMB：默认最大文件大小为 5MB

// 默认标题与描述
const title = props.title || '导入数据文件' // title：默认标题
const description = props.description || '请选择本地 Excel/CSV 文件，导入二手、兼职等业务数据。' // description：默认说明

// 触发原生文件选择框
const triggerSelect = () => { // triggerSelect：点击按钮后触发原生文件选择
  errorMessage.value = '' // 清空之前的错误信息
  fileInputRef.value?.click() // 调用 input 的 click 方法弹出文件选择对话框
}

// 简单解析 CSV 文本为二维数组（不处理复杂转义，仅适合结构较简单的场景）
function parseCsv(text: string): string[][] { // parseCsv：将 CSV 文本解析为二维数组
  // 按换行拆分为多行
  const lines = text.split(/\r?\n/).filter((line) => line.trim().length > 0) // 去掉空行
  return lines.map((line) => { // 遍历每一行
    // 简单按逗号分割，并去掉首尾空格与包装引号
    return line.split(',').map((cell) => {
      const trimmed = cell.trim() // 去掉首尾空格
      if (trimmed.startsWith('"') && trimmed.endsWith('"')) { // 如果以双引号包裹
        return trimmed.slice(1, -1).replace(/""/g, '"') // 去掉外层引号并还原转义的双引号
      }
      return trimmed // 否则直接返回
    })
  })
}

// 处理文件选择事件
const handleFileChange = (event: Event) => { // handleFileChange：当用户在文件选择框中选择文件时触发
  const target = event.target as HTMLInputElement // 将事件目标断言为 HTMLInputElement
  const file = target.files && target.files[0] // 获取用户选择的第一个文件

  if (!file) { // 如果没有选择任何文件
    fileName.value = '' // 清空文件名
    return // 直接返回
  }

  // 先进行大小校验
  const sizeMB = file.size / (1024 * 1024) // 将字节转为 MB
  if (sizeMB > maxSizeMB) { // 如果文件大小超过限制
    const msg = `文件大小不能超过 ${maxSizeMB}MB` // 生成错误提示文案
    errorMessage.value = msg // 更新本地错误信息
    emit('error', msg) // 通过 error 事件通知父组件
    return // 结束处理
  }

  // 记录文件名并清空错误
  fileName.value = file.name // 保存当前选择的文件名
  errorMessage.value = '' // 清空错误信息

  // 通知父组件已经选中文件
  emit('file-selected', file) // 触发 file-selected 事件，将 File 对象传出

  // 如果不需要自动解析，则到此结束
  if (!props.autoParse) { // 判断 autoParse 是否为 true
    return // 不解析时直接返回
  }

  // 使用 FileReader 读取文本内容，仅对 CSV 文本做简单解析
  const reader = new FileReader() // 创建 FileReader 实例
  reader.onload = () => { // 当读取完成后执行的回调函数
    const text = String(reader.result || '') // 将读取结果转成字符串
    try { // 使用 try/catch 捕获解析过程中的异常
      const rows = parseCsv(text) // 调用上面的 parseCsv 函数解析文本
      emit('parsed', rows) // 通过 parsed 事件将解析后的二维数组传给父组件
    } catch (e) { // 如果解析过程中发生错误
      const msg = '解析 CSV 内容失败，请检查文件格式。' // 准备错误提示文案
      errorMessage.value = msg // 更新本地错误信息
      emit('error', msg) // 通过 error 事件通知父组件
    }
  }
  reader.onerror = () => { // 当读取失败时执行的回调函数
    const msg = '读取文件内容失败，请重试。' // 准备错误提示文案
    errorMessage.value = msg // 更新错误信息
    emit('error', msg) // 通知父组件出现错误
  }

  // 以文本形式读取文件内容
  reader.readAsText(file, 'utf-8') // 调用 readAsText 读取文件，并指定编码为 utf-8
}
</script>

<style scoped>
/* 整体容器样式 */
.excel-import {
  border-radius: 12px; /* 使用圆角提升视觉效果 */
  background-color: #ffffff; /* 白色背景与全站卡片风格保持一致 */
  padding: 16px 20px; /* 内边距让内容更舒展 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06); /* 轻微阴影增强层次感 */
  box-sizing: border-box; /* 使用 border-box 计算宽度 */
}

/* 标题行布局样式 */
.header {
  margin-bottom: 12px; /* 与上传区域之间留出间距 */
}

/* 标题文字样式 */
.title {
  font-size: 15px; /* 标题使用稍大字号 */
  font-weight: 600; /* 半粗体突出标题 */
  color: #333333; /* 深色文字提高可读性 */
}

/* 描述文字样式 */
.desc {
  font-size: 13px; /* 描述文字使用中等字号 */
  color: #666666; /* 使用中灰色文字 */
  margin-top: 4px; /* 与标题之间保留微小间距 */
}

/* 上传区域布局样式 */
.upload-area {
  display: flex; /* 使用 flex 将按钮和文件名放在一行内 */
  align-items: center; /* 垂直居中对齐 */
  gap: 10px; /* 按钮与文件名之间保留间距 */
}

/* 隐藏原生文件 input，仅通过按钮触发 */
.file-input {
  display: none; /* 隐藏原生 input 元素 */
}

/* 上传按钮样式 */
.upload-btn {
  padding: 6px 14px; /* 内边距保证点击面积 */
  border-radius: 999px; /* 使用胶囊形状圆角 */
  border: 1px solid #d81b60; /* 使用品牌主色描边 */
  background-color: #ffffff; /* 白色背景 */
  color: #d81b60; /* 按钮文字使用品牌主色 */
  font-size: 13px; /* 按钮文字大小 */
  cursor: pointer; /* 鼠标移入时显示手型 */
  transition: all 0.2s ease; /* 添加动态过渡效果 */
}

/* 上传按钮悬停效果样式 */
.upload-btn:hover {
  background-color: #fce4ec; /* 悬停时使用浅粉色背景 */
}

/* 文件名展示样式 */
.file-name {
  font-size: 13px; /* 文件名文字大小 */
  color: #333333; /* 使用深色文字 */
}

/* 错误提示文本样式 */
.error-text {
  margin-top: 8px; /* 与上传区域之间留出间距 */
  font-size: 12px; /* 错误文字较小 */
  color: #f44336; /* 使用红色提示错误 */
}

/* 常规提示文本样式 */
.tip-text {
  margin-top: 8px; /* 与上传区域之间留出间距 */
  font-size: 12px; /* 提示文字较小 */
  color: #999999; /* 使用浅灰色文字 */
}
</style>
