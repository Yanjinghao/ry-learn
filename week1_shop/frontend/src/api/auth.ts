import request from '@/utils/request'
import type { LoginForm, LoginResult, RegisterForm, Result, User } from '@/types/api'

export function loginApi(data: LoginForm) {
  return request.post<Result<LoginResult>>('/api/auth/login', data)
}

export function registerApi(data: RegisterForm) {
  return request.post<Result<User>>('/api/user/register', data)
}
