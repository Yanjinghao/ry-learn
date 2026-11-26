import request from '@/utils/request'
import type { Order, OrderCreate, Result } from '@/types/api'

export function createOrderApi(body: OrderCreate) {
  return request.post<Result<Order>>('/api/order', body)
}

export function fetchMyOrdersApi() {
  return request.get<Result<Order[]>>('/api/order')
}

export function fetchOrderByIdApi(id: number) {
  return request.get<Result<Order>>(`/api/order/${id}`)
}

export function payOrderApi(id: number) {
  return request.post<Result<Order>>(`/api/order/${id}/pay`)
}

export function cancelOrderApi(id: number) {
  return request.post<Result<Order>>(`/api/order/${id}/cancel`)
}
