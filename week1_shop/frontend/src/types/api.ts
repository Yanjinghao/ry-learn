/** 后端统一返回体 */
export interface Result<T = unknown> {
  code: number
  msg: string
  data: T
}

export interface User {
  id: number
  username: string
  password?: string
  nickname: string | null
  status: number
  createdAt: string | null
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  nickname: string | null
}

export interface LoginForm {
  username: string
  password: string
}

export interface RegisterForm {
  username: string
  password: string
  nickname?: string
}
