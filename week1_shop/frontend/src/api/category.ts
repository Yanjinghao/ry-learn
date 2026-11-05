import request from '@/utils/request'
import type { Category, CategoryForm, Result } from '@/types/api'

export function fetchCategoryList() {
  return request.get<Result<Category[]>>('/api/category/list')
}

export function fetchCategoryById(id: number) {
  return request.get<Result<Category>>(`/api/category/${id}`)
}

export function createCategoryApi(body: CategoryForm) {
  return request.post<Result<Category>>('/api/category', body)
}

export function updateCategoryApi(id: number, body: Partial<CategoryForm>) {
  return request.put<Result<null>>(`/api/category/${id}`, body)
}

export function deleteCategoryApi(id: number) {
  return request.delete<Result<null>>(`/api/category/${id}`)
}
