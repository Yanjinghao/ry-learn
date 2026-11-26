import type { Component } from 'vue'

/** 后端 menu.component → 前端页面组件 */
export const componentMap: Record<string, () => Promise<Component>> = {
  UserListView: () => import('@/views/UserListView.vue'),
  CategoryListView: () => import('@/views/CategoryListView.vue'),
  ProductListView: () => import('@/views/ProductListView.vue'),
  OrderListView: () => import('@/views/OrderListView.vue'),
}
