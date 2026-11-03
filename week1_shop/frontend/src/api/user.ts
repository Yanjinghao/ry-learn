import request from '@/utils/request'
import type { Result, User } from '@/types/api'

export function fetchUserList(params?: { username?: string; status?: number }) {
  return request.get<Result<User[]>>('/api/user/list', { params })
}

export function fetchUserById(id: number) {
  return request.get<Result<User>>(`/api/user/${id}`)
}

export function updateNicknameApi(id: number, nickname: string) {
  return request.put<Result<null>>(`/api/user/${id}/nickname`, { nickname })
}

export function updateStatusApi(id: number, status: number) {
  return request.put<Result<null>>(`/api/user/${id}/status`, { status })
}

export function deleteUserApi(id: number) {
  return request.delete<Result<null>>(`/api/user/${id}`)
}
