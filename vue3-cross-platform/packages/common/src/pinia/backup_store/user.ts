import { defineStore } from 'pinia';

// 1. 导出UserRole类型（供router等外部文件使用）
export type UserRole = 'tourist' | 'student' | 'teacher' | 'university' | 'merchant' | 'admin';

// 2. 导出UserInfo类型（保留扩展字段 + 新增avatar，兼顾灵活性和规范）
export interface UserInfo {
  name?: string;
  studentId?: string;
  teacherId?: string;
  merchantId?: string;
  avatar?: string; // 新增：兼容第二个版本的avatar字段
  [key: string]: any; // 保留：支持扩展字段（如手机号、邮箱等）
}

export const useUserStore = defineStore('user', {
  state: () => ({
    role: 'tourist' as UserRole,
    userInfo: {} as UserInfo,
    isLogin: false,
  }),
  getters: {
    getRoleText(): string {
      const roleMap = {
        student: '学生',
        teacher: '教师',
        merchant: '商家',
        university: '校方',
        admin: '管理员',
        tourist: '游客',
      };
      return roleMap[this.role] || '游客';
    },
  },
  actions: {
    // 保留完整的login逻辑（含错误处理+手动存储）
    login(role: UserRole, info: UserInfo) {
      try {
        this.role = role;
        this.userInfo = info;
        this.isLogin = true;
        
        // 手动存储：双保险（persist插件+手动存储）
        uni.setStorageSync('userRole', role);
        uni.setStorageSync('userInfo', info);
        
        uni.showToast({ title: `欢迎${this.getRoleText()}`, icon: 'success' });
      } catch (err) {
        console.error('登录失败：', err);
        uni.showToast({ title: '登录异常，请重试', icon: 'none' });
      }
    },

    // 保留完整的logout逻辑（含错误处理+清除存储）
    logout() {
      try {
        this.role = 'tourist';
        this.userInfo = {};
        this.isLogin = false;
        
        // 清除手动存储的字段
        uni.removeStorageSync('userRole');
        uni.removeStorageSync('userInfo');
        
        uni.showToast({ title: '退出成功', icon: 'success' });
      } catch (err) {
        console.error('退出登录失败：', err);
        uni.showToast({ title: '退出异常，请重试', icon: 'none' });
      }
    },

    // 保留：更新用户信息方法
    updateUserInfo(info: Partial<UserInfo>) {
      this.userInfo = { ...this.userInfo, ...info };
      // 同步更新本地存储
      uni.setStorageSync('userInfo', this.userInfo);
    },

    // 保留：初始化恢复登录状态（关键）
    initUserFromStorage() {
      try {
        const storedRole = uni.getStorageSync('userRole') as UserRole;
        const storedUserInfo = uni.getStorageSync('userInfo') as UserInfo;
        
        if (storedRole && ['student', 'teacher', 'university', 'merchant', 'admin'].includes(storedRole)) {
          this.role = storedRole;
          this.userInfo = storedUserInfo;
          this.isLogin = true;
        }
      } catch (err) {
        console.error('恢复登录状态失败：', err);
        this.role = 'tourist';
        this.userInfo = {};
        this.isLogin = false;
      }
    },

    // 保留：异步认证方法（对接后端用）
    async identityAuth(role: UserRole, info: UserInfo) {
      try {
        // 模拟后端请求（实际项目替换为uni.request/axios）
        const mockAuthSuccess = true;
        if (mockAuthSuccess) {
          this.login(role, info); // 复用login逻辑
          return true;
        }
        uni.showToast({ title: '认证失败，信息错误', icon: 'none' });
        return false;
      } catch (err) {
        console.error('身份认证失败：', err);
        uni.showToast({ title: '网络异常，请重试', icon: 'none' });
        return false;
      }
    },
  },
  // 保留persist配置（双保险）
  persist: {
    key: 'user-store',
    storage: {
      getItem: uni.getStorageSync,
      setItem: uni.setStorageSync,
    },
  },
});
