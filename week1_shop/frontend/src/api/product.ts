import request from '@/utils/request'
import type { PageResult, Product, ProductDetail, ProductForm, Result } from '@/types/api'

export function fetchProductPage(params: {
  pageNum?: number
  pageSize?: number
  name?: string
  categoryId?: number
  status?: number
}) {
  return request.get<Result<PageResult<Product>>>('/api/product/page', { params })
}

export function fetchProductById(id: number) {
  return request.get<Result<ProductDetail>>(`/api/product/${id}`)
}

export function createProductApi(body: ProductForm) {
  return request.post<Result<Product>>('/api/product', body)
}

export function updateProductApi(id: number, body: Partial<ProductForm>) {
  return request.put<Result<null>>(`/api/product/${id}`, body)
}

export function deleteProductApi(id: number) {
  return request.delete<Result<null>>(`/api/product/${id}`)
}

export function updateProductStatusApi(id: number, status: number) {
  return request.put<Result<null>>(`/api/product/${id}/status`, { status })
}
