/**
 * Web端路由配置
 * 包含所有业务模块的路由
 */
import { createRouter, createWebHistory } from 'vue-router'

// 使用 import.meta.glob 只收集“实际存在”的页面模块，避免路由里写了不存在的文件导致 Vite 直接 500。
// 这样即使某个页面文件缺失，也只会在访问该路由时回退到 404 页面，而不会让开发服务器崩掉。
const pageModules = import.meta.glob('../pages/**/*.vue')

function page(file: string) {
  const key = file.startsWith('../pages/') ? file : `../pages/${file}`
  const loader = pageModules[key]
  if (loader) return loader

  if (import.meta.env.DEV) {
    // 仅在开发环境提示，避免生产环境刷控制台
    console.warn(`[router] 页面文件不存在，已回退到 404：${key}`)
  }

  // 常规兜底：回退到内置 404 页面（该文件在本仓库中存在）
  const fallback = pageModules['../pages/common/not-found.vue']
  if (fallback) return fallback

  // 极端兜底：即便 glob 没收集到（理论不会发生），也保证返回一个可用的异步组件加载器
  return () => import('../pages/common/not-found.vue')
}

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: page('index/index.vue'),
      meta: { title: '首页', requiresAuth: false }
    },
    {
      path: '/login',
      name: 'login',
      component: page('auth/login.vue'),
      meta: { title: '登录', requiresAuth: false }
    },
    {
      path: '/register',
      name: 'register',
      component: page('auth/register.vue'),
      meta: { title: '注册', requiresAuth: false }
    },
    {
      path: '/forget-password',
      name: 'forget-password',
      component: page('auth/forget-password.vue'),
      meta: { title: '找回密码', requiresAuth: false }
    },
    // 第三方登录
    {
      path: '/wecom-login',
      name: 'wecom-login',
      component: page('auth/wecom-login.vue'),
      meta: { title: '企业微信登录', requiresAuth: false }
    },
    {
      path: '/wechat-login',
      name: 'wechat-login',
      component: page('auth/wechat-login.vue'),
      meta: { title: '微信登录', requiresAuth: false }
    },
    {
      path: '/qq-login',
      name: 'qq-login',
      component: page('auth/qq-login.vue'),
      meta: { title: 'QQ登录', requiresAuth: false }
    },
    {
      path: '/email-login',
      name: 'email-login',
      component: page('auth/email-login.vue'),
      meta: { title: '邮箱登录', requiresAuth: false }
    },
    // 用户相关
    {
      path: '/profile',
      name: 'profile',
      component: page('profile/index.vue'),
      meta: { title: '个人中心', requiresAuth: true }
    },
    {
      path: '/profile/edit',
      name: 'profile-edit',
      component: page('profile/edit/index.vue'),
      meta: { title: '编辑资料', requiresAuth: true }
    },
    {
      path: '/profile/security',
      name: 'profile-security',
      component: page('profile/security/index.vue'),
      meta: { title: '安全设置', requiresAuth: true }
    },
    // 身份认证总览页面
    {
      path: '/auth',
      name: 'auth',
      component: page('auth/index.vue'),
      meta: { title: '身份认证', requiresAuth: true }
    },
    // 学生身份认证申请页面（单独表单，支持学号 + 身份证 + 验证码）
    {
      path: '/auth/student-apply',
      name: 'auth-student-apply',
      component: page('auth/student-apply.vue'),
      meta: { title: '学生身份认证申请', requiresAuth: true }
    },
    // 校园卡模块
    {
      path: '/ecard',
      name: 'ecard',
      component: page('ecard/index.vue'),
      meta: { title: '校园E卡通', requiresAuth: false }
    },
    {
      path: '/ecard/recharge',
      name: 'ecard-recharge',
      component: page('ecard/recharge/index.vue'),
      meta: { title: '校园卡充值', requiresAuth: false }
    },
    {
      path: '/ecard/consume-record',
      name: 'ecard-consume-record',
      component: page('ecard/consume-record/index.vue'),
      meta: { title: '消费记录', requiresAuth: false }
    },
    {
      path: '/ecard/detail',
      name: 'ecard-detail',
      component: page('ecard/detail/index.vue'),
      meta: { title: '校园卡详情', requiresAuth: false }
    },
    {
      path: '/ecard/transfer',
      name: 'ecard-transfer',
      component: page('ecard/transfer/index.vue'),
      meta: { title: '校园卡转账', requiresAuth: false }
    },
    {
      path: '/ecard/statistics',
      name: 'ecard-statistics',
      component: page('ecard/statistics/index.vue'),
      meta: { title: '消费统计', requiresAuth: false }
    },
    {
      path: '/ecard/account-book',
      name: 'ecard-account-book',
      component: page('ecard/account-book/index.web.vue'),
      meta: { title: '记账本', requiresAuth: false }
    },
    {
      path: '/ecard/settings',
      name: 'ecard-settings',
      component: page('ecard/settings/index.vue'),
      meta: { title: '校园卡设置', requiresAuth: false }
    },
    {
      path: '/ecard/security',
      name: 'ecard-security',
      component: page('ecard/security/index.vue'),
      meta: { title: '校园卡安全', requiresAuth: false }
    },
    {
      path: '/ecard/account-book/add',
      name: 'ecard-account-book-add',
      component: page('ecard/account-book/add/index.vue'),
      meta: { title: '添加记账', requiresAuth: false }
    },
    {
      path: '/ecard/account-book/report',
      name: 'ecard-account-book-report',
      component: page('ecard/account-book/report/index.vue'),
      meta: { title: '记账周报', requiresAuth: false }
    },
    {
      path: '/ecard/diet-record',
      name: 'ecard-diet-record',
      component: page('ecard/diet-record/index.web.vue'),
      meta: { title: '饮食表', requiresAuth: false }
    },
    {
      path: '/ecard/diet-record/add',
      name: 'ecard-diet-record-add',
      component: page('ecard/diet-record/add/index.vue'),
      meta: { title: '添加饮食', requiresAuth: false }
    },
    {
      path: '/ecard/diet-record/detail/:id',
      name: 'ecard-diet-record-detail',
      component: page('ecard/diet-record/detail/index.vue'),
      meta: { title: '饮食详情', requiresAuth: false }
    },
    {
      path: '/ecard/visitor-card',
      name: 'ecard-visitor-card',
      component: page('ecard/visitor-card/index.vue'),
      meta: { title: '游客刷脸进校', requiresAuth: false }
    },
    {
      path: '/ecard/balance-reminder',
      name: 'ecard-balance-reminder',
      component: page('ecard/balance-reminder/index.vue'),
      meta: { title: '余额提醒设置', requiresAuth: false }
    },
    {
      path: '/ecard/security-center',
      name: 'ecard-security-center',
      component: page('ecard/security-center/index.vue'),
      meta: { title: '安全中心', requiresAuth: false }
    },
    {
      path: '/ecard/canteen',
      name: 'ecard-canteen',
      component: page('ecard/canteen/index.vue'),
      meta: { title: '食堂服务', requiresAuth: false }
    },
    {
      path: '/ecard/bus',
      name: 'ecard-bus',
      component: page('ecard/bus/index.vue'),
      meta: { title: '校车服务', requiresAuth: false }
    },
    {
      path: '/ecard/services',
      name: 'ecard-services',
      component: page('ecard/services/index.vue'),
      meta: { title: '校园服务', requiresAuth: false }
    },
    {
      path: '/ecard/payment-history',
      name: 'ecard-payment-history',
      component: page('ecard/payment-history/index.vue'),
      meta: { title: '支付历史', requiresAuth: false }
    },
    {
      path: '/ecard/payment-settings',
      name: 'ecard-payment-settings',
      component: page('ecard/payment-settings/index.vue'),
      meta: { title: '支付设置', requiresAuth: false }
    },

    // 二手交易模块
    {
      path: '/secondhand',
      name: 'secondhand',
      component: page('secondhand/index.vue'),
      meta: { title: '二手交易', requiresAuth: false }
    },
    {
      path: '/secondhand/detail/:id',
      name: 'secondhand-detail',
      component: page('secondhand/detail/index.vue'),
      meta: { title: '商品详情', requiresAuth: false }
    },
    {
      path: '/secondhand/publish',
      name: 'secondhand-publish',
      component: page('secondhand/publish/index.vue'),
      meta: { title: '发布商品', requiresAuth: false }
    },
    {
      path: '/secondhand/messages',
      name: 'secondhand-messages',
      component: page('secondhand/messages/index.vue'),
      meta: { title: '我的消息', requiresAuth: true }
    },
    {
      path: '/secondhand/history',
      name: 'secondhand-history',
      component: page('secondhand/history/index.vue'),
      meta: { title: '浏览记录', requiresAuth: false }
    },
    {
      path: '/secondhand/favorites',
      name: 'secondhand-favorites',
      component: page('secondhand/favorites/index.vue'),
      meta: { title: '我的收藏', requiresAuth: false }
    },
    {
      path: '/secondhand/my',
      name: 'secondhand-my',
      component: page('secondhand/my/index.vue'),
      meta: { title: '我的二手', requiresAuth: true }
    },
    // 兼职管理模块
    {
      path: '/parttime',
      name: 'parttime',
      component: page('parttime/index.vue'),
      meta: { title: '兼职服务', requiresAuth: false }
    },
    {
      path: '/parttime/detail/:id',
      name: 'parttime-detail',
      component: page('parttime/detail/index.vue'),
      meta: { title: '兼职详情', requiresAuth: false }
    },
    {
      path: '/parttime/publish',
      name: 'parttime-publish',
      component: page('parttime/publish/index.vue'),
      meta: { title: '发布兼职', requiresAuth: false }
    },
    {
      path: '/parttime/applications',
      name: 'parttime-applications',
      component: page('parttime/applications/index.vue'),
      meta: { title: '申请记录', requiresAuth: false }
    },
    {
      path: '/parttime/history',
      name: 'parttime-history',
      component: page('parttime/history/index.vue'),
      meta: { title: '浏览记录', requiresAuth: false }
    },
    {
      path: '/parttime/favorites',
      name: 'parttime-favorites',
      component: page('parttime/favorites/index.vue'),
      meta: { title: '我的收藏', requiresAuth: false }
    },
    {
      path: '/parttime/my',
      name: 'parttime-my',
      component: page('parttime/my/index.vue'),
      meta: { title: '我的兼职', requiresAuth: true }
    },
    // 行程管理模块
    {
      path: '/schedule',
      name: 'schedule',
      component: page('schedule/index.vue'),
      meta: { title: '行程管理', requiresAuth: false }
    },
    {
      path: '/schedule/add',
      name: 'schedule-add',
      redirect: { name: 'schedule', query: { create: '1' } },
      meta: { title: '添加行程', requiresAuth: true }
    },
    {
      path: '/schedule/detail/:id',
      name: 'schedule-detail',
      component: page('schedule/detail.vue'),
      meta: { title: '行程详情', requiresAuth: true }
    },
    {
      path: '/schedule/import',
      name: 'schedule-import',
      component: page('schedule/import/index.vue'),
      meta: { title: '导入课程表', requiresAuth: true }
    },
    {
      path: '/schedule/team',
      name: 'schedule-team',
      component: page('schedule/team/index.vue'),
      meta: { title: '团队行程', requiresAuth: true }
    },
    {
      path: '/schedule/team/:id',
      name: 'schedule-team-detail',
      component: page('schedule/team/detail.vue'),
      meta: { title: '团队详情', requiresAuth: true }
    },

    {
      path: '/schedule/export',
      name: 'schedule-export',
      component: page('schedule/export/index.vue'),
      meta: { title: '导出行程', requiresAuth: true }
    },
    {
      path: '/schedule/share/:id',
      name: 'schedule-share',
      component: page('schedule/share/index.vue'),
      meta: { title: '分享行程', requiresAuth: true }
    },
    {
      path: '/schedule/team-create',
      name: 'schedule-team-create',
      component: page('schedule/team-create/index.vue'),
      meta: { title: '创建团队', requiresAuth: true }
    },
    {
      path: '/schedule/course-table',
      name: 'schedule-course-table',
      component: page('schedule/course-table/index.vue'),
      meta: { title: '课程表管理', requiresAuth: true }
    },
    {
      path: '/schedule/time-table',
      name: 'schedule-time-table',
      component: page('schedule/time-table/index.vue'),
      meta: { title: '时间安排表', requiresAuth: true }
    },
    {
      path: '/schedule/course-table/add',
      name: 'schedule-course-table-add',
      component: page('schedule/course-table/add/index.vue'),
      meta: { title: '添加课程', requiresAuth: true }
    },
    {
      path: '/schedule/reminder',
      name: 'schedule-reminder',
      component: page('schedule/reminder/index.vue'),
      meta: { title: '提醒管理', requiresAuth: true }
    },
    {
      path: '/schedule/reminder/edit',
      name: 'schedule-reminder-edit',
      component: page('schedule/reminder/index.vue'),
      meta: { title: '编辑提醒', requiresAuth: true }
    },
    {
      path: '/schedule/route-series',
      name: 'schedule-route-series',
      component: page('schedule/route-series/index.vue'),
      meta: { title: '路线管理', requiresAuth: true }
    },
    // 通用功能
    {
      path: '/security',
      name: 'security',
      component: page('common/security.vue'),
      meta: { title: '安全保障', requiresAuth: false }
    },
    {
      path: '/help',
      name: 'help',
      component: page('common/help.vue'),
      meta: { title: '帮助中心', requiresAuth: false }
    },
    {
      path: '/about',
      name: 'about',
      component: page('common/about.vue'),
      meta: { title: '平台介绍', requiresAuth: false }
    },
    {
      path: '/privacy',
      name: 'privacy',
      component: page('common/privacy.vue'),
      meta: { title: '隐私政策', requiresAuth: false }
    },
    {
      path: '/terms',
      name: 'terms',
      component: page('common/terms.vue'),
      meta: { title: '服务协议', requiresAuth: false }
    },
    {
      path: '/feedback',
      name: 'feedback',
      component: page('common/feedback.vue'),
      meta: { title: '问题反馈', requiresAuth: false }
    },
    {
      path: '/app-download',
      name: 'app-download',
      component: page('common/app-download.vue'),
      meta: { title: '手机App下载', requiresAuth: false }
    },
    {
      path: '/client-download',
      name: 'client-download',
      component: page('common/client-download.vue'),
      meta: { title: '电脑客户端下载', requiresAuth: false }
    },
    // 消息和聊天
    {
      path: '/messages',
      name: 'messages',
      component: page('messages/index.vue'),
      meta: { title: '消息中心', requiresAuth: false }
    },
    {
      path: '/chat',
      name: 'chat',
      component: page('chat/index.vue'),
      meta: { title: '聊天', requiresAuth: false }
    },
    // 添加朋友
    {
      path: '/add-friend',
      name: 'add-friend',
      component: page('add-friend/index.vue'),
      meta: { title: '添加朋友', requiresAuth: false }
    },
    // 搜索功能
    {
      path: '/search',
      name: 'search',
      component: page('search/index.vue'),
      meta: { title: '搜索', requiresAuth: false }
    },
    // 订单管理
    {
      path: '/orders',
      name: 'orders',
      component: page('orders/index.vue'),
      meta: { title: '我的订单', requiresAuth: false }
    },
    // 设置
    {
      path: '/settings',
      name: 'settings',
      component: page('settings/index.vue'),
      meta: { title: '设置', requiresAuth: false }
    },
    // 更多功能
    {
      path: '/more',
      name: 'more',
      component: page('more/index.vue'),
      meta: { title: '更多功能', requiresAuth: false }
    },
    // 账户管理
    {
      path: '/account',
      name: 'account',
      component: page('account/index.vue'),
      meta: { title: '账户管理', requiresAuth: false }
    },
    // 校园服务
    {
      path: '/express',
      name: 'express',
      component: page('express/index.vue'),
      meta: { title: '校园快递', requiresAuth: false }
    },
    {
      path: '/express/publish',
      name: 'express-publish',
      component: page('express/publish/index.vue'),
      meta: { title: '发布代取', requiresAuth: false }
    },
    {
      path: '/express/detail/:id',
      name: 'express-detail',
      component: page('express/detail/index.vue'),
      meta: { title: '快递详情', requiresAuth: false }
    },
    {
      path: '/laundry',
      name: 'laundry',
      component: page('laundry/index.vue'),
      meta: { title: '洗衣服务', requiresAuth: false }
    },
    {
      path: '/laundry/publish',
      name: 'laundry-publish',
      component: page('laundry/publish/index.vue'),
      meta: { title: '预约洗衣', requiresAuth: false }
    },
    {
      path: '/laundry/detail/:id',
      name: 'laundry-detail',
      component: page('laundry/detail/index.vue'),
      meta: { title: '洗衣详情', requiresAuth: false }
    },
    {
      path: '/print',
      name: 'print',
      component: page('print/index.vue'),
      meta: { title: '打印服务', requiresAuth: false }
    },
    {
      path: '/print/publish',
      name: 'print-publish',
      component: page('print/publish/index.vue'),
      meta: { title: '上传文件', requiresAuth: false }
    },
    {
      path: '/print/detail/:id',
      name: 'print-detail',
      component: page('print/detail/index.vue'),
      meta: { title: '打印详情', requiresAuth: false }
    },
    {
      path: '/library',
      name: 'library',
      component: page('library/index.vue'),
      meta: { title: '图书馆', requiresAuth: false }
    },
    {
      path: '/market',
      name: 'market',
      component: page('market/index.vue'),
      meta: { title: '校园市场', requiresAuth: false }
    },
    {
      path: '/network',
      name: 'network',
      component: page('network/index.vue'),
      meta: { title: '网络服务', requiresAuth: false }
    },
    {
      path: '/payment',
      name: 'payment',
      component: page('payment/index.vue'),
      meta: { title: '支付中心', requiresAuth: false }
    },
    {
      path: '/wallet',
      name: 'wallet',
      component: page('wallet/index.vue'),
      meta: { title: '我的钱包', requiresAuth: false }
    },
    // 管理员功能
    {
      path: '/admin',
      name: 'admin',
      component: page('admin/index.vue'),
      meta: { title: '管理中心', requiresAuth: true, roles: ['admin'] }
    },
    // 404页面
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: page('common/not-found.vue'),
      meta: { title: '页面不存在' }
    }
  ]
})

// 路由守卫 - 权限验证
// ⚠️ 演示模式：已禁用所有登录权限检查，方便演示使用
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 上大学Online`
  }

  // ========== 演示模式：跳过所有权限检查，直接放行 ==========
  // 为了方便演示，已禁用所有登录和角色权限检查
  // 如需恢复权限检查，请取消下面这行注释，并恢复下方的权限检查代码
  next()
  return
  // ===========================================================

  // ========== 以下代码已禁用（演示模式）==========
  // 如需恢复权限检查，请删除上面的 return 语句，并取消下面代码的注释
  /*
  const userStore = useUserStore()
  
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
  */
  // ===========================================================
})

export default router
