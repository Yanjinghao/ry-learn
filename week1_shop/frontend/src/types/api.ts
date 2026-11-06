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
