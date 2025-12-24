<template>
  <!--
    通用数据表格组件
    说明：用于二手列表、兼职列表等场景的表格展示，支持：
    - 传入列配置 columns
    - 传入行数据 rows
    - 加载中 / 无数据 提示
    - 简单分页（上一页 / 下一页）
    - 插槽自定义单元格内容
  -->
  <div class="data-table">
    <!-- 表格头部区域：标题 + 工具栏插槽（例如筛选按钮、导出按钮） -->
    <div class="table-header" v-if="title || $slots.toolbar">
      <!-- 左侧标题展示 -->
      <div class="table-title" v-if="title">{{ title }}</div>
      <!-- 右侧工具栏插槽，父组件可以放按钮、搜索框等 -->
      <div class="table-toolbar">
        <slot name="toolbar"></slot>
      </div>
    </div>

    <!-- 表格主体外层容器，方便后续做滚动或固定表头 -->
    <div class="table-wrapper">
      <table class="table">
        <!-- 表头区域：根据 columns 渲染列标题 -->
        <thead v-if="showHeader">
          <tr>
            <th
              v-for="column in columns"
              :key="column.key"
              :style="{ textAlign: column.align || 'left', width: column.width || 'auto' }"
            >
              {{ column.label }}
            </th>
          </tr>
        </thead>

        <!-- 表体区域：根据不同状态显示加载中 / 无数据 / 正常数据 -->
        <tbody>
          <!-- 加载中状态 -->
          <tr v-if="loading">
            <td :colspan="columns.length" class="table-empty">正在加载数据，请稍候...</td>
          </tr>

          <!-- 无数据状态 -->
          <tr v-else-if="rows.length === 0">
            <td :colspan="columns.length" class="table-empty">{{ emptyText }}</td>
          </tr>

          <!-- 正常数据状态：逐行渲染 -->
          <tr
            v-else
            v-for="(row, rowIndex) in rows"
            :key="rowKey ? (row as any)[rowKey] ?? rowIndex : rowIndex"
          >
            <!-- 根据列配置逐列渲染单元格 -->
            <td
              v-for="column in columns"
              :key="column.key"
              :style="{ textAlign: column.align || 'left' }"
            >
              <!-- 单元格插槽：如果父组件提供了 cell 插槽，则优先使用插槽渲染 -->
              <slot
                name="cell"
                :row="row"
                :column="column"
                :value="(row as any)[column.key]"
              >
                <!-- 默认渲染：直接输出对应字段的值 -->
                {{ (row as any)[column.key] }}
              </slot>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页区域：当传入 pagination 对象时显示简单分页控件 -->
    <div class="table-pagination" v-if="pagination">
      <!-- 左侧展示当前页和总条数 -->
      <div class="pagination-info">
        第 {{ pagination.page }} / {{ totalPages }} 页，共 {{ pagination.total }} 条记录
      </div>
      <!-- 右侧上一页 / 下一页按钮 -->
      <div class="pagination-actions">
        <button
          class="page-btn"
          :disabled="pagination.page <= 1"
          @click="changePage(pagination.page - 1)"
        >
          上一页
        </button>
        <button
          class="page-btn"
          :disabled="pagination.page >= totalPages"
          @click="changePage(pagination.page + 1)"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 引入 computed 用于计算总页数等派生数据
import { computed } from 'vue' // 从 Vue 引入 computed 函数

// 定义列配置类型
export interface DataTableColumn { // DataTableColumn：每一列表头与字段映射配置
  key: string // key：对应行数据中的字段名
  label: string // label：表头显示文本
  width?: string // width：可选的列宽，例如 '120px' 或 '20%'
  align?: 'left' | 'center' | 'right' // align：文本对齐方式，默认 left
}

// 定义分页配置类型
export interface DataTablePagination { // DataTablePagination：分页配置对象
  page: number // page：当前页码，从 1 开始
  pageSize: number // pageSize：每页显示条数
  total: number // total：总记录数
}

// 定义组件接收的属性
const props = defineProps<{ // 使用 defineProps 声明外部可传入的属性
  columns: DataTableColumn[] // columns：列配置数组
  rows: any[] // rows：表格数据数组（每一项为一行）
  loading?: boolean // loading：是否处于加载中状态
  emptyText?: string // emptyText：无数据时显示的提示文本
  rowKey?: string // rowKey：可选的行唯一标识字段名，用于更稳定的 key
  showHeader?: boolean // showHeader：是否显示表头，默认显示
  pagination?: DataTablePagination // pagination：可选的分页配置对象
}>()

// 定义组件对外发出的事件
const emit = defineEmits<{ // 使用 defineEmits 声明事件类型
  (e: 'update:pagination', value: DataTablePagination): void // 当分页变化时，更新 v-model:pagination
  (e: 'page-change', value: DataTablePagination): void // 附带触发 page-change 事件，方便父组件监听
}>()

// 计算总页数（向上取整）
const totalPages = computed(() => { // totalPages：根据分页配置计算总页数
  if (!props.pagination || props.pagination.pageSize <= 0) { // 如果未传入分页配置或 pageSize 非法
    return 1 // 默认只显示 1 页
  }
  return Math.max(1, Math.ceil(props.pagination.total / props.pagination.pageSize)) // 使用 Math.ceil 计算总页数
})

// 处理页码切换
const changePage = (page: number) => { // changePage：切换到指定页码
  if (!props.pagination) return // 如果没有分页配置则直接返回

  // 限制页码在合法范围内
  const safePage = Math.min(Math.max(1, page), totalPages.value) // 将页码限制在 1 和 totalPages 之间

  // 生成新的分页对象
  const newPagination: DataTablePagination = { // newPagination：新的分页配置
    ...props.pagination, // 复制原有分页配置
    page: safePage // 覆盖当前页码
  }

  // 通过 v-model:pagination 更新父组件中的分页状态
  emit('update:pagination', newPagination) // 触发 update:pagination 事件
  // 同时触发 page-change 事件，方便父组件做额外处理（如重新请求接口）
  emit('page-change', newPagination) // 触发 page-change 事件
}

// 为空数据提示设置默认值
const emptyText = computed(() => { // emptyText：无数据时显示的提示文本
  return props.emptyText || '暂无数据' // 优先使用外部传入的 emptyText，否则使用默认文案
})

// 表头是否显示的最终结果
const showHeader = computed(() => { // showHeader：是否显示表头
  return props.showHeader !== false // 默认显示表头，仅当显式传入 false 时不显示
})
</script>

<style scoped>
/* 表格组件外层容器样式 */
.data-table {
  width: 100%; /* 宽度撑满父容器 */
  background-color: #ffffff; /* 使用白色背景承载表格 */
  border-radius: 12px; /* 圆角与全站卡片风格保持一致 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06); /* 轻微阴影增强层次感 */
  padding: 16px 20px; /* 内边距让内容更舒展 */
  box-sizing: border-box; /* 使用 border-box 保证宽度计算直观 */
}

/* 表头区域布局样式 */
.table-header {
  display: flex; /* 使用 flex 横向排列标题和工具栏 */
  justify-content: space-between; /* 左右两侧对齐 */
  align-items: center; /* 垂直方向居中对齐 */
  margin-bottom: 12px; /* 与表格主体之间留出间距 */
}

/* 标题文字样式 */
.table-title {
  font-size: 16px; /* 稍大的字号以突出标题 */
  font-weight: 600; /* 使用半粗体增强权重 */
  color: #333333; /* 深色文字提高可读性 */
}

/* 工具栏容器样式 */
.table-toolbar {
  display: flex; /* 使用 flex 布局方便放置多个按钮 */
  align-items: center; /* 垂直居中工具栏内容 */
  gap: 8px; /* 工具按钮之间留出间距 */
}

/* 表格外层容器样式 */
.table-wrapper {
  width: 100%; /* 宽度撑满父容器 */
  overflow-x: auto; /* 当列较多时允许横向滚动 */
}

/* 原生 table 元素样式 */
.table {
  width: 100%; /* 表格宽度撑满容器 */
  border-collapse: collapse; /* 合并单元格边框，视觉更简洁 */
  font-size: 14px; /* 表格文字基础字号 */
}

/* 表头单元格样式 */
.table thead th {
  padding: 10px 8px; /* 为表头单元格设置内边距 */
  background-color: #f8f9fa; /* 使用浅灰背景区分表头 */
  color: #666666; /* 表头文字使用中灰色 */
  font-weight: 500; /* 表头文字使用中等粗细 */
  border-bottom: 1px solid #e0e0e0; /* 表头与表体之间添加分割线 */
  white-space: nowrap; /* 默认不换行，避免标题被压缩 */
}

/* 表体单元格样式 */
.table tbody td {
  padding: 10px 8px; /* 表体单元格内边距 */
  border-bottom: 1px solid #f0f0f0; /* 每行之间使用浅色分割线 */
  color: #333333; /* 使用深色文字提高可读性 */
}

/* 最后一行去掉下边框，以免与外部容器线条冲突 */
.table tbody tr:last-child td {
  border-bottom: none; /* 去掉最后一行的底部边框 */
}

/* 空状态 / 加载中 单元格样式 */
.table-empty {
  text-align: center; /* 文本居中显示 */
  padding: 16px 8px; /* 增加上下内边距让提示更明显 */
  color: #999999; /* 使用浅灰色文字 */
}

/* 分页区域整体布局样式 */
.table-pagination {
  margin-top: 12px; /* 与表格主体之间留出间距 */
  display: flex; /* 使用 flex 横向排列文本与按钮 */
  justify-content: space-between; /* 左右两侧对齐 */
  align-items: center; /* 垂直居中对齐 */
  font-size: 12px; /* 使用稍小字号 */
  color: #666666; /* 使用中灰色文字 */
}

/* 分页按钮样式 */
.page-btn {
  padding: 4px 10px; /* 设置按钮内边距 */
  border-radius: 999px; /* 使用胶囊形状圆角 */
  border: 1px solid #d0d0d0; /* 使用浅灰色边框 */
  background-color: #ffffff; /* 使用白色背景 */
  cursor: pointer; /* 鼠标移入显示手型 */
  font-size: 12px; /* 按钮文字使用小字号 */
  color: #333333; /* 使用深色文字 */
  margin-left: 6px; /* 按钮之间留出水平间距 */
  transition: background-color 0.15s ease, border-color 0.15s ease; /* 添加悬停动效 */
}

/* 分页按钮悬停态样式 */
.page-btn:hover:not(:disabled) {
  background-color: #fce4ec; /* 悬停时使用浅粉色背景提示 */
  border-color: #d81b60; /* 边框使用主色 */
}

/* 禁用态按钮样式 */
.page-btn:disabled {
  cursor: not-allowed; /* 显示禁止符号 */
  opacity: 0.5; /* 使用半透明降低存在感 */
}
</style>
