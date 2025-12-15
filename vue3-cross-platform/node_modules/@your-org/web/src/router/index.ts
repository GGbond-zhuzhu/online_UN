/**
 * Web端路由配置
 * 包含所有业务模块的路由
 */
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@campus/common'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../pages/index/index.vue'),
      meta: { title: '首页', requiresAuth: false }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../pages/auth/login.vue'),
      meta: { title: '登录', requiresAuth: false }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../pages/auth/register.vue'),
      meta: { title: '注册', requiresAuth: false }
    },
    {
      path: '/forget-password',
      name: 'forget-password',
      component: () => import('../pages/auth/forget-password.vue'),
      meta: { title: '找回密码', requiresAuth: false }
    },
    // 第三方登录
    {
      path: '/wecom-login',
      name: 'wecom-login',
      component: () => import('../pages/auth/wecom-login.vue'),
      meta: { title: '企业微信登录', requiresAuth: false }
    },
    {
      path: '/wechat-login',
      name: 'wechat-login',
      component: () => import('../pages/auth/wechat-login.vue'),
      meta: { title: '微信登录', requiresAuth: false }
    },
    {
      path: '/qq-login',
      name: 'qq-login',
      component: () => import('../pages/auth/qq-login.vue'),
      meta: { title: 'QQ登录', requiresAuth: false }
    },
    {
      path: '/email-login',
      name: 'email-login',
      component: () => import('../pages/auth/email-login.vue'),
      meta: { title: '邮箱登录', requiresAuth: false }
    },
    // 用户相关
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../pages/profile/index.vue'),
      meta: { title: '个人中心', requiresAuth: true }
    },
    // 身份认证
    {
      path: '/auth',
      name: 'auth',
      component: () => import('../pages/auth/index.vue'),
      meta: { title: '身份认证', requiresAuth: true }
    },
    // 校园卡模块
    {
      path: '/ecard',
      name: 'ecard',
      component: () => import('../pages/ecard/index.vue'),
      meta: { title: '校园E卡通', requiresAuth: false }
    },
    // 二手交易模块
    {
      path: '/secondhand',
      name: 'secondhand',
      component: () => import('../pages/secondhand/index.vue'),
      meta: { title: '二手交易', requiresAuth: false }
    },
    {
      path: '/secondhand/detail/:id',
      name: 'secondhand-detail',
      component: () => import('../pages/secondhand/detail/index.vue'),
      meta: { title: '商品详情', requiresAuth: false }
    },
    {
      path: '/secondhand/publish',
      name: 'secondhand-publish',
      component: () => import('../pages/secondhand/publish/index.vue'),
      meta: { title: '发布商品', requiresAuth: false }
    },
    {
      path: '/secondhand/messages',
      name: 'secondhand-messages',
      component: () => import('../pages/secondhand/messages/index.vue'),
      meta: { title: '我的消息', requiresAuth: false }
    },
    {
      path: '/secondhand/history',
      name: 'secondhand-history',
      component: () => import('../pages/secondhand/history/index.vue'),
      meta: { title: '浏览记录', requiresAuth: false }
    },
    {
      path: '/secondhand/favorites',
      name: 'secondhand-favorites',
      component: () => import('../pages/secondhand/favorites/index.vue'),
      meta: { title: '我的收藏', requiresAuth: false }
    },
    // 兼职管理模块
    {
      path: '/parttime',
      name: 'parttime',
      component: () => import('../pages/parttime/index.vue'),
      meta: { title: '兼职服务', requiresAuth: false }
    },
    {
      path: '/parttime/detail/:id',
      name: 'parttime-detail',
      component: () => import('../pages/parttime/detail/index.vue'),
      meta: { title: '兼职详情', requiresAuth: false }
    },
    {
      path: '/parttime/publish',
      name: 'parttime-publish',
      component: () => import('../pages/parttime/publish/index.vue'),
      meta: { title: '发布兼职', requiresAuth: false }
    },
    {
      path: '/parttime/applications',
      name: 'parttime-applications',
      component: () => import('../pages/parttime/applications/index.vue'),
      meta: { title: '申请记录', requiresAuth: false }
    },
    {
      path: '/parttime/history',
      name: 'parttime-history',
      component: () => import('../pages/parttime/history/index.vue'),
      meta: { title: '浏览记录', requiresAuth: false }
    },
    {
      path: '/parttime/favorites',
      name: 'parttime-favorites',
      component: () => import('../pages/parttime/favorites/index.vue'),
      meta: { title: '我的收藏', requiresAuth: false }
    },
    // 行程管理模块
    {
      path: '/schedule',
      name: 'schedule',
      component: () => import('../pages/schedule/index.vue'),
      meta: { title: '行程管理', requiresAuth: false }
    },
    {
      path: '/schedule/import',
      name: 'schedule-import',
      component: () => import('../pages/schedule/import/index.vue'),
      meta: { title: '导入课程表', requiresAuth: true }
    },
    {
      path: '/schedule/team',
      name: 'schedule-team',
      component: () => import('../pages/schedule/team/index.vue'),
      meta: { title: '团队行程', requiresAuth: true }
    },
    {
      path: '/schedule/team/:id',
      name: 'schedule-team-detail',
      component: () => import('../pages/schedule/team/detail.vue'),
      meta: { title: '团队详情', requiresAuth: true }
    },
    // 通用功能
    {
      path: '/security',
      name: 'security',
      component: () => import('../pages/common/security.vue'),
      meta: { title: '安全保障', requiresAuth: false }
    },
    {
      path: '/help',
      name: 'help',
      component: () => import('../pages/common/help.vue'),
      meta: { title: '帮助中心', requiresAuth: false }
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../pages/common/about.vue'),
      meta: { title: '平台介绍', requiresAuth: false }
    },
    {
      path: '/privacy',
      name: 'privacy',
      component: () => import('../pages/common/privacy.vue'),
      meta: { title: '隐私政策', requiresAuth: false }
    },
    {
      path: '/terms',
      name: 'terms',
      component: () => import('../pages/common/terms.vue'),
      meta: { title: '服务协议', requiresAuth: false }
    },
    {
      path: '/feedback',
      name: 'feedback',
      component: () => import('../pages/common/feedback.vue'),
      meta: { title: '问题反馈', requiresAuth: false }
    },
    {
      path: '/app-download',
      name: 'app-download',
      component: () => import('../pages/common/app-download.vue'),
      meta: { title: '手机App下载', requiresAuth: false }
    },
    {
      path: '/client-download',
      name: 'client-download',
      component: () => import('../pages/common/client-download.vue'),
      meta: { title: '电脑客户端下载', requiresAuth: false }
    },
    // 消息和聊天
    {
      path: '/messages',
      name: 'messages',
      component: () => import('../pages/messages/index.vue'),
      meta: { title: '消息中心', requiresAuth: false }
    },
    {
      path: '/chat',
      name: 'chat',
      component: () => import('../pages/chat/index.vue'),
      meta: { title: '聊天', requiresAuth: false }
    },
    // 管理员功能
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../pages/admin/index.vue'),
      meta: { title: '管理中心', requiresAuth: true, roles: ['admin'] }
    },
    // 404页面
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../pages/common/not-found.vue'),
      meta: { title: '页面不存在' }
    }
  ]
})

// 路由守卫 - 权限验证
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 上大学Online`
  }
  
  // 开发者模式：绕过所有权限检查，直接允许访问
  if (userStore.isDeveloper) {
    next()
    return
  }
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    // 需要登录但未登录，跳转到登录页
    next({
      name: 'login',
      query: { redirect: to.fullPath }
    })
    return
  }
  
  // 检查是否需要身份认证（非游客）
  if (to.meta.needAuth && userStore.isVisitor) {
    // 需要认证但当前是游客，跳转到身份认证页
    next({
      name: 'auth',
      query: { redirect: to.fullPath }
    })
    return
  }
  
  // 检查角色权限
  if (to.meta.roles && Array.isArray(to.meta.roles)) {
    const hasRole = to.meta.roles.some(role => userStore.hasRole(role))
    if (!hasRole) {
      // 没有权限，可以跳转到403页面或首页
      next({ name: 'home' })
      return
    }
  }
  
  // 通过所有检查，允许访问
  next()
})

export default router
