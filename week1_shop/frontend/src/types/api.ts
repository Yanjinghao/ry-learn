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

/** 对应后端 MenuVO */
export interface MenuNode {
  id: number
  parentId: number
  name: string
  type: number
  path: string | null
  component: string | null
  perms: string | null
  sort: number | null
  children?: MenuNode[]
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  nickname: string | null
  roles: string[]
  permissions: string[]
  menus: MenuNode[]
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

export interface Category {
  id: number
  name: string
  sort: number
}

export interface CategoryForm {
  name: string
  sort: number | undefined
}

export interface Product {
  id: number
  categoryId: number
  name: string
  price: number | string
  stock: number
  coverUrl: string | null
  status: number
  deleted?: number
  createdAt: string | null
}

/** 对应后端 ProductDetailVO */
export interface ProductDetail extends Product {
  categoryName: string | null
}

export interface ProductForm {
  categoryId: number | undefined
  name: string
  price: number | undefined
  stock: number | undefined
  coverUrl: string
  status: number
}

/** MyBatis-Plus Page */
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/** 订单状态：0 待支付 / 1 已支付 / 2 已取消 */
export type OrderStatus = 0 | 1 | 2

export interface Order {
  id: number
  orderNo: string
  userId: number
  status: OrderStatus | number
  payAmount: number | string
  createdAt: string | null
}

export interface OrderItemCreate {
  productId: number
  qty: number
}

export interface OrderCreate {
  items: OrderItemCreate[]
}
