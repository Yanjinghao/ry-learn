<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Box,
  Expand,
  Fold,
  Goods,
  SwitchButton,
  User,
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const collapsed = ref(false)
const asideWidth = computed(() => (collapsed.value ? '64px' : '168px'))

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const map: Record<string, string> = {
    '/users': '用户管理',
    '/categories': '分类管理',
    '/products': '商品管理',
  }
  return map[route.path] || '后台管理'
})

function toggleAside() {
  collapsed.value = !collapsed.value
}

function logout() {
  auth.clearAuth()
  router.push('/login')
}
</script>

<template>
  <el-container class="admin-layout">
    <el-aside :width="asideWidth" class="aside">
      <div class="brand" :class="{ collapsed }">
        <span v-show="!collapsed">week1-shop</span>
        <span v-show="collapsed" class="brand-mini">ry</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :collapse-transition="false"
        router
        background-color="#0f172a"
        text-color="#cbd5e1"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Box /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Goods /></el-icon>
          <template #title>商品管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="topbar" height="56px">
        <div class="topbar-left">
          <el-button
            text
            :icon="collapsed ? Expand : Fold"
            @click="toggleAside"
          />
          <div class="topbar-title">{{ pageTitle }}</div>
        </div>
        <div class="topbar-right">
          <span class="user">{{ auth.user?.nickname || auth.user?.username }}</span>
          <el-button text type="danger" :icon="SwitchButton" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: #f1f5f9;
}
.aside {
  background: #0f172a;
  min-height: 100vh;
  transition: width 0.2s ease;
  overflow: hidden;
}
.brand {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 12px;
  color: #f8fafc;
  font-weight: 700;
  font-size: 15px;
  letter-spacing: 0.02em;
  border-bottom: 1px solid rgba(148, 163, 184, 0.2);
  white-space: nowrap;
}
.brand-mini {
  font-size: 14px;
  text-transform: uppercase;
}
.aside :deep(.el-menu) {
  border-right: none;
  width: 100% !important;
}
.aside :deep(.el-menu-item) {
  padding-left: 16px !important;
}
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  padding: 0 16px;
}
.topbar-left {
  display: flex;
  align-items: center;
  gap: 4px;
}
.topbar-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}
.topbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.user {
  color: #475569;
  font-size: 14px;
}
.main {
  padding: 20px;
}
</style>
