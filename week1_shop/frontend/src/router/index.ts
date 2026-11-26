import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { registerDynamicRoutes, removeDynamicRoutes } from '@/router/dynamicRoutes'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      name: 'layout',
      component: () => import('@/layouts/AdminLayout.vue'),
      children: [],
    },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  if (!to.meta.public && !auth.isLogin) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  // 已登录：用本地 menus 注册动态路由（刷新后必须再 add 一次）
  if (auth.isLogin && !auth.routesReady) {
    registerDynamicRoutes(router, auth.menus)
    auth.markRoutesReady()
    return { ...to, replace: true }
  }

  if ((to.path === '/login' || to.path === '/register') && auth.isLogin) {
    return { path: auth.homePath }
  }

  // 进布局根路径时，跳到第一个有权限的菜单
  if (auth.isLogin && to.name === 'layout') {
    return { path: auth.homePath }
  }

  return true
})

export function logoutAndResetRouter() {
  const auth = useAuthStore()
  removeDynamicRoutes(router)
  auth.clearAuth()
}

export default router
