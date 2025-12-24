<template>
  <!--
    通用侧边栏导航组件
    说明：用于 PC 端左侧菜单导航，例如管理后台、统计页等，
    支持传入菜单项数组，点击后通过事件或路由进行跳转。
  -->
  <aside class="side-bar">
    <!-- 侧边栏标题区域，可选显示 -->
    <div v-if="title" class="side-title">{{ title }}</div>

    <!-- 菜单列表区域 -->
    <nav class="menu-list">
      <button
        v-for="item in items"
        :key="item.key"
        class="menu-item"
        :class="{ active: item.key === activeKey }"
        type="button"
        @click="handleSelect(item)"
      >
        <!-- 左侧图标（可选） -->
        <i v-if="item.icon" :class="['menu-icon', item.icon]"></i>
        <!-- 菜单文本 -->
        <span class="menu-text">{{ item.label }}</span>
      </button>
    </nav>
  </aside>
</template>

<script setup lang="ts">
// 引入路由实例，用于在菜单项配置了 route 时执行页面跳转
import { useRouter } from 'vue-router' // 从 vue-router 中引入 useRouter 钩子

// 定义菜单项类型
export interface SideBarItem { // SideBarItem：侧边栏菜单项配置类型
  key: string // key：菜单唯一标识，用于高亮和事件回传
  label: string // label：菜单显示文字
  icon?: string // icon：可选的 Font Awesome 图标类名
  route?: string // route：可选的路由路径，点击后自动跳转
}

// 获取路由实例
const router = useRouter() // router：用于页面跳转的路由实例

// 定义组件接收的属性
const props = defineProps<{ // 使用 defineProps 声明属性
  title?: string // title：侧边栏标题文字
  items: SideBarItem[] // items：菜单项数组
  modelValue?: string // modelValue：当前选中的菜单 key，用于 v-model 绑定
}>()

// 定义组件对外发出的事件
const emit = defineEmits<{ // 使用 defineEmits 声明事件类型
  (e: 'update:modelValue', value: string): void // update:modelValue：用于 v-model 同步当前选中的 key
  (e: 'select', item: SideBarItem): void // select：当点击某个菜单项时抛出，携带完整菜单对象
}>()

// 当前激活菜单 key（优先使用 v-model 绑定的值）
const activeKey = computed(() => { // activeKey：当前高亮的菜单 key
  return props.modelValue || (props.items[0]?.key ?? '') // 如果未传入则默认使用第一项的 key
})

// 处理菜单点击事件
const handleSelect = (item: SideBarItem) => { // handleSelect：点击菜单项时调用
  emit('update:modelValue', item.key) // 通过 v-model 更新当前选中值
  emit('select', item) // 抛出 select 事件，便于父组件做统计或埋点

  // 如果菜单项配置了 route，则自动进行路由跳转
  if (item.route) { // 判断是否存在路由配置
    router.push(item.route) // 使用路由实例跳转到对应路径
  }
}
</script>

<style scoped>
/* 侧边栏整体容器样式 */
.side-bar {
  width: 220px; /* 固定宽度，适合常见 PC 布局 */
  background-color: #ffffff; /* 使用白色背景 */
  border-radius: 12px; /* 使用圆角与整体风格一致 */
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06); /* 添加轻微阴影增强层次感 */
  padding: 16px 12px; /* 内边距让菜单不贴边 */
  box-sizing: border-box; /* 使用 border-box 计算宽度 */
}

/* 标题文字样式 */
.side-title {
  font-size: 14px; /* 标题字号略小但加粗 */
  font-weight: 600; /* 加粗增强权重 */
  color: #999999; /* 使用浅灰色文字与菜单项区分 */
  margin-bottom: 10px; /* 标题与菜单列表之间留出间距 */
}

/* 菜单列表容器样式 */
.menu-list {
  display: flex; /* 使用 flex 列布局 */
  flex-direction: column; /* 垂直排列菜单项 */
  gap: 6px; /* 菜单项之间留出小间距 */
}

/* 单个菜单项按钮样式 */
.menu-item {
  display: flex; /* 使用 flex 将图标和文字放在一行 */
  align-items: center; /* 垂直居中图标和文字 */
  width: 100%; /* 宽度撑满侧边栏 */
  padding: 8px 10px; /* 内边距保证点击面积 */
  border-radius: 8px; /* 使用圆角按钮 */
  border: none; /* 去掉原生按钮边框 */
  background-color: transparent; /* 默认背景透明 */
  cursor: pointer; /* 鼠标移入显示手型 */
  font-size: 13px; /* 菜单文字字号 */
  color: #555555; /* 使用中灰色文字 */
  text-align: left; /* 文本靠左对齐 */
  transition: all 0.2s ease; /* 添加动态过渡效果 */
}

/* 悬停态菜单项样式 */
.menu-item:hover {
  background-color: #f8f9fa; /* 悬停时使用浅灰色背景 */
}

/* 选中态菜单项样式 */
.menu-item.active {
  background: linear-gradient(135deg, #f9d0da 0%, #f5b8c9 100%); /* 使用与首页一致的粉色渐变背景 */
  color: #d81b60; /* 使用品牌主色文字 */
}

/* 菜单图标样式 */
.menu-icon {
  width: 18px; /* 固定图标区域宽度 */
  margin-right: 8px; /* 图标与文字之间留出间距 */
  text-align: center; /* 图标在自身区域内居中 */
}

/* 菜单文字样式 */
.menu-text {
  flex: 1; /* 占据剩余空间 */
}
</style>
