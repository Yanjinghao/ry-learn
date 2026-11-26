import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { LoginResult, MenuNode } from '@/types/api'
import { resolveHomePath } from '@/router/dynamicRoutes'

const TOKEN_KEY = 'token'
const USER_KEY = 'user_info'
const ROLES_KEY = 'auth_roles'
const PERMS_KEY = 'auth_permissions'
const MENUS_KEY = 'auth_menus'

interface UserInfo {
  userId: number
  username: string
  nickname: string | null
}

function loadJson<T>(key: string, fallback: T): T {
  const raw = localStorage.getItem(key)
  if (!raw) return fallback
  try {
    return JSON.parse(raw) as T
  } catch {
    return fallback
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<UserInfo | null>(loadJson<UserInfo | null>(USER_KEY, null))
  const roles = ref<string[]>(loadJson<string[]>(ROLES_KEY, []))
  const permissions = ref<string[]>(loadJson<string[]>(PERMS_KEY, []))
  const menus = ref<MenuNode[]>(loadJson<MenuNode[]>(MENUS_KEY, []))
  /** 动态路由是否已注册（刷新页面后要重新 addRoute） */
  const routesReady = ref(false)

  const isLogin = computed(() => Boolean(token.value))
  const homePath = computed(() => resolveHomePath(menus.value))

  function setAuth(data: LoginResult) {
    token.value = data.token
    user.value = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
    }
    roles.value = data.roles || []
    permissions.value = data.permissions || []
    menus.value = data.menus || []
    routesReady.value = false

    localStorage.setItem(TOKEN_KEY, data.token)
    localStorage.setItem(USER_KEY, JSON.stringify(user.value))
    localStorage.setItem(ROLES_KEY, JSON.stringify(roles.value))
    localStorage.setItem(PERMS_KEY, JSON.stringify(permissions.value))
    localStorage.setItem(MENUS_KEY, JSON.stringify(menus.value))
  }

  function markRoutesReady() {
    routesReady.value = true
  }

  function clearAuth() {
    token.value = ''
    user.value = null
    roles.value = []
    permissions.value = []
    menus.value = []
    routesReady.value = false
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    localStorage.removeItem(ROLES_KEY)
    localStorage.removeItem(PERMS_KEY)
    localStorage.removeItem(MENUS_KEY)
  }

  /** 是否有某个权限字（周五按钮权限也能用） */
  function hasPermission(perm: string) {
    return permissions.value.includes(perm)
  }

  return {
    token,
    user,
    roles,
    permissions,
    menus,
    routesReady,
    isLogin,
    homePath,
    setAuth,
    markRoutesReady,
    clearAuth,
    hasPermission,
  }
})
