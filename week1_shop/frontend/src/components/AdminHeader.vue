<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

defineProps<{
  title: string
  subtitle?: string
}>()

const router = useRouter()
const auth = useAuthStore()

function logout() {
  auth.clearAuth()
  router.push('/login')
}
</script>

<template>
  <header class="header">
    <div>
      <h2>{{ title }}</h2>
      <span v-if="subtitle" class="sub">{{ subtitle }}</span>
      <nav class="nav">
        <router-link to="/users">用户</router-link>
        <router-link to="/categories">分类</router-link>
        <router-link to="/products">商品</router-link>
      </nav>
    </div>
    <div class="header-right">
      <span class="user">{{ auth.user?.nickname || auth.user?.username }}</span>
      <el-button @click="logout">退出登录</el-button>
    </div>
  </header>
</template>

<style scoped>
.header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 16px;
}
h2 {
  margin: 0;
}
.sub {
  display: block;
  color: #64748b;
  font-size: 13px;
  margin-top: 4px;
}
.nav {
  display: flex;
  gap: 16px;
  margin-top: 10px;
}
.nav a {
  color: #64748b;
  text-decoration: none;
  font-size: 14px;
}
.nav a.router-link-active {
  color: #2563eb;
  font-weight: 600;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}
.user {
  color: #334155;
  font-size: 14px;
}
</style>
