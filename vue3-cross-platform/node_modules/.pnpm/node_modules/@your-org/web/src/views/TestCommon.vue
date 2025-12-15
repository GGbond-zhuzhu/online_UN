<template>
    <div>
      <h2>Common包功能测试</h2>
      <button @click="fetchSecondhand">获取二手商品列表</button>
      <div v-if="!isCertifiedUser">请先完成身份认证</div>
      <div v-if="!hasPublishPerm">无发布二手商品权限</div>
      <!-- 仅渲染数组且非空时的内容 -->
      <template v-if="secondhandList.length">
        <div 
          v-for="item in secondhandList" 
          :key="item.id"
        >
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
          <span class="price">{{ item.price }}元</span>
        </div>
      </template>
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, computed } from 'vue';
  
  // ========== 1. 内联common包核心类型 ==========
  interface SecondhandGoodsVO {
    id: number;
    title: string;
    description: string;
    price: number;
    originalPrice?: number;
    category: string;
    status: string;
  }
  interface SecondhandQueryDTO {
    page: number;
    size: number;
  }
  enum Permission {
    PUBLISH_SECONDHAND = 'publish_secondhand'
  }
  enum Role {
    VISITOR = 'visitor',
    STUDENT = 'student'
  }
  // ========== 2. 内联common包核心工具函数 ==========
  const isCertified = (): boolean => {
    // 模拟未认证（游客）
    return false;
  };
  const hasPermission = (permission: Permission): boolean => {
    const currentRole = Role.VISITOR;
    // 明确指定类型，避免类型推断错误
    const rolePermMap: Record<Role, { allow: Permission[]; deny: Permission[] }> = {
      [Role.VISITOR]: { allow: [], deny: [Permission.PUBLISH_SECONDHAND] },
      [Role.STUDENT]: { allow: [Permission.PUBLISH_SECONDHAND], deny: [] }
    };
    const { allow, deny } = rolePermMap[currentRole];
    return allow.includes(permission) && !deny.includes(permission);
  };
  // ========== 3. 核心变量：仅数组类型 ==========
  const secondhandList = ref<SecondhandGoodsVO[]>([]);
  // ========== 4. 接口调用：模拟数据 ==========
  const fetchSecondhand = async () => {
    const params: SecondhandQueryDTO = { page: 1, size: 5 };
    const mockData: SecondhandGoodsVO[] = [
      { id: 1, title: "二手笔记本", description: "9成新", price: 2000, originalPrice: 4000, category: "电子", status: "在售" }
    ];
    secondhandList.value = mockData; // 仅赋值数组
  };
  // ========== 5. 计算属性：显式类型 ==========
  const isCertifiedUser = computed<boolean>(() => {
    return isCertified();
  });
  const hasPublishPerm = computed<boolean>(() => {
    return hasPermission(Permission.PUBLISH_SECONDHAND);
  });
  </script>
  
  <style scoped>
  .price { color: #f60; font-weight: bold; }
  </style>