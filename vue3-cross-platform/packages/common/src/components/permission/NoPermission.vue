<template>
  <!-- 无权限提示组件外层容器，用于在页面级别展示无权限状态 -->
  <div class="no-permission">
    <!-- 图标与标题区域 -->
    <div class="icon-wrapper">
      <i class="fas fa-lock"></i>
    </div>
    <h2 class="title">当前账号暂无访问权限</h2>

    <!-- 说明文案区域，结合当前角色给出友好提示 -->
    <p class="description">
      您当前的身份为「{{ roleText }}」，暂时无法访问该模块。
      如需开通权限，请联系高校管理员或通过身份认证入口完成升级。
    </p>

    <!-- 操作按钮区域：提供“返回首页”和“去认证”两个常用操作 -->
    <div class="actions">
      <button class="btn primary" @click="goHome">返回首页</button>
      <button class="btn outline" @click="goAuth">去身份认证</button>
    </div>
  </div>
</template>

<script setup lang="ts">
// 引入路由相关 API、计算属性工具和用户状态 Store
import { useRouter } from 'vue-router' // 从 vue-router 中引入 useRouter，用于页面跳转
import { computed } from 'vue' // 从 vue 中引入 computed，用于根据当前角色计算展示文案
import { useUserStore } from '../../pinia/user' // 从 common 包内部引入用户 Store，用于获取当前角色

// 获取路由实例
const router = useRouter() // router：用于执行路由跳转的实例

// 获取用户 Store 实例
const userStore = useUserStore() // userStore：全局用户状态管理对象

// 计算当前角色对应的中文文案
const roleText = computed(() => { // roleText：当前角色的中文名称
  const map: Record<string, string> = { // 角色英文标识到中文名称的映射表
    student: '学生', // 学生角色
    teacher: '教师', // 教师角色
    merchant: '商家', // 商户角色
    university: '高校管理员', // 高校管理员角色
    admin: '系统管理员', // 系统管理员角色
    visitor: '游客', // 游客角色（使用 visitor 标识时）
    tourist: '游客' // 游客角色（使用 tourist 标识时，向下兼容）
  }
  const roleKey = (userStore as any).currentRole || (userStore as any).role || 'visitor' // 优先读取 currentRole，没有则回退到 role，再兜底为 visitor
  return map[roleKey] || '游客' // 返回对应中文名称，找不到时默认显示“游客”
})

// 返回首页操作
const goHome = () => { // goHome：跳转到首页的处理函数
  router.push('/') // 使用路由实例跳转到根路径首页
}

// 去身份认证页面操作
const goAuth = () => { // goAuth：跳转到身份认证页面的处理函数
  router.push('/auth') // 使用路由实例跳转到 /auth 身份认证页面
}
</script>

<style scoped>
/* 整体容器样式：垂直居中显示无权限提示内容 */
.no-permission {
  padding: 40px 20px; /* 上下左右留出较大内边距 */
  text-align: center; /* 文本居中对齐 */
}

/* 图标外层圆形背景样式 */
.icon-wrapper {
  width: 72px; /* 固定宽度形成圆形 */
  height: 72px; /* 固定高度形成圆形 */
  margin: 0 auto 16px; /* 水平居中并与后续文字留出间距 */
  border-radius: 50%; /* 变为圆形容器 */
  background: #fce4ec; /* 使用浅粉色背景 */
  display: flex; /* 使用 flex 居中内部图标 */
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */
  color: #d81b60; /* 图标颜色使用主品牌色 */
}

/* 锁头图标样式 */
.icon-wrapper i {
  font-size: 32px; /* 图标尺寸较大，提升识别度 */
}

/* 标题文字样式 */
.title {
  font-size: 20px; /* 标题使用稍大的字号 */
  font-weight: 600; /* 半粗体增强视觉权重 */
  color: #333333; /* 深色文字提高可读性 */
  margin: 0 0 12px; /* 下方留出与描述文字的间距 */
}

/* 描述文字样式 */
.description {
  max-width: 480px; /* 限制单行宽度，提升阅读体验 */
  margin: 0 auto 20px; /* 水平居中并与下方按钮区域留出间距 */
  font-size: 14px; /* 使用中等字号 */
  color: #666666; /* 中灰色文字 */
  line-height: 1.7; /* 较大的行高提高可读性 */
}

/* 操作按钮区域样式 */
.actions {
  display: flex; /* 使用 flex 将按钮横向排列 */
  justify-content: center; /* 水平居中按钮 */
  gap: 12px; /* 两个按钮之间留出间距 */
}

/* 按钮基础样式 */
.btn {
  min-width: 120px; /* 按钮最小宽度保证点击面积 */
  padding: 10px 18px; /* 内边距让按钮更易点击 */
  border-radius: 999px; /* 使用胶囊形状圆角 */
  border: 1px solid transparent; /* 预留边框位置避免 hover 抖动 */
  font-size: 14px; /* 统一按钮文字字号 */
  cursor: pointer; /* 鼠标移入时显示手型 */
  transition: all 0.2s ease; /* 添加过渡动画 */
}

/* 主按钮样式：填充主色背景 */
.btn.primary {
  background-color: #d81b60; /* 使用品牌主色作为背景 */
  color: #ffffff; /* 使用白色文字增强对比度 */
  box-shadow: 0 4px 12px rgba(216, 27, 96, 0.3); /* 添加阴影强调主操作 */
}

/* 主按钮悬停态样式 */
.btn.primary:hover {
  background-color: #c2185b; /* 悬停时略微加深背景色 */
}

/* 描边按钮样式：适合次要操作 */
.btn.outline {
  background-color: transparent; /* 使用透明背景 */
  color: #d81b60; /* 文字使用主色 */
  border-color: #d81b60; /* 使用主色描边 */
}

/* 描边按钮悬停态样式 */
.btn.outline:hover {
  background-color: #fce4ec; /* 使用浅粉色背景作为悬停反馈 */
}
</style>
