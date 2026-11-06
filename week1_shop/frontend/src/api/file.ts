import request from '@/utils/request'
import type { Result } from '@/types/api'

export function uploadFileApi(file: File) {
  const form = new FormData()
  form.append('file', file)
  return request.post<Result<{ url: string }>>('/api/file/upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
