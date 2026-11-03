import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { LoginResult } from '@/types/api'

const TOKEN_KEY = 'token'
const USER_KEY = 'user_info'

interface UserInfo {
  userId: number
  username: string
  nickname: string | null
}

function loadUser(): UserInfo | null {
  const raw = localStorage.getItem(USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as UserInfo
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<UserInfo | null>(loadUser())

  const isLogin = computed(() => Boolean(token.value))

  function setAuth(data: LoginResult) {
    token.value = data.token
    user.value = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
    }
    localStorage.setItem(TOKEN_KEY, data.token)
    localStorage.setItem(USER_KEY, JSON.stringify(user.value))
  }

  function clearAuth() {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return {
    token,
    user,
    isLogin,
    setAuth,
    clearAuth,
  }
})
