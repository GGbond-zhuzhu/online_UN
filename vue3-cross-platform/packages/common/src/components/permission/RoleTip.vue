<template>
  <!-- 角色提示组件外层容器，通常嵌入在表单或模块顶部提供说明 -->
  <div class="role-tip">
    <!-- 图标区域，使用信息提示图标 -->
    <i class="fas fa-info-circle"></i>
    <!-- 文字区域，根据当前角色展示不同的说明文案 -->
    <span class="text">{{ tipText }}</span>
  </div>
</template>

<script setup lang="ts">
// 引入 computed 与用户 Store，用于根据当前角色生成提示文案
import { computed } from 'vue' // 从 Vue 引入 computed，创建派生文本
import { useUserStore } from '../../pinia/user' // 从 common 包内部引入用户 Store

// 获取用户 Store 实例
const userStore = useUserStore() // userStore：全局用户状态管理对象

// 根据当前角色生成对应的提示文案
const tipText = computed(() => { // tipText：要展示给用户的角色提示文字
  const roleKey = (userStore as any).currentRole || (userStore as any).role || 'visitor' // 读取当前角色标识，优先使用 currentRole，若不存在则回退到 role，最后兜底为 visitor

  // 为不同角色准备对应的说明文字
  const map: Record<string, string> = { // 角色到提示文案的映射表
    student: '当前为学生身份，可使用二手交易、兼职申请、行程管理等完整功能。', // 学生角色说明
    teacher: '当前为教师身份，可查看教学相关功能并参与校园服务模块。', // 教师角色说明
    merchant: '当前为商家身份，可发布兼职岗位与校园服务信息，请遵守校园规范。', // 商家角色说明
    university: '当前为高校管理员身份，可进行平台配置与数据审核操作。', // 高校管理员角色说明
    admin: '当前为系统管理员身份，拥有平台全局管理权限，请谨慎操作。', // 系统管理员角色说明
    visitor: '当前为游客身份，仅能浏览部分公开内容，建议完成登录和身份认证。', // 游客角色说明
    tourist: '当前为游客身份，仅能浏览部分公开内容，建议完成登录和身份认证。' // 兼容 tourist 标识
  }

  // 如果能匹配到对应说明则返回，否则给出通用提示
  return map[roleKey] || '当前为未认证身份，部分功能可能受限，建议前往身份认证页面完成认证。' // 默认提示文案
})
</script>

<style scoped>
/* 外层容器样式：水平排列图标与文字 */
.role-tip {
  display: inline-flex; /* 使用 inline-flex，方便嵌入到段落或表单中 */
  align-items: center; /* 垂直方向居中对齐图标与文字 */
  padding: 6px 10px; /* 内边距让提示区域更易读 */
  border-radius: 999px; /* 使用胶囊圆角形状 */
  background-color: #e3f2fd; /* 使用浅蓝色背景提示信息 */
  color: #1565c0; /* 使用较深蓝色文字与图标颜色 */
  font-size: 12px; /* 使用较小字号，避免喧宾夺主 */
}

/* 左侧图标样式 */
.role-tip i {
  margin-right: 6px; /* 图标与文字之间留出水平间距 */
}

/* 文本区域样式 */
.text {
  white-space: nowrap; /* 默认不换行，放在表单标签旁边时更紧凑 */
}
</style>
