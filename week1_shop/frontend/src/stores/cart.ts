import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

const CART_KEY = 'shop_cart'

export interface CartItem {
  productId: number
  name: string
  price: number
  stock: number
  qty: number
  coverUrl?: string | null
}

function loadCart(): CartItem[] {
  const raw = localStorage.getItem(CART_KEY)
  if (!raw) return []
  try {
    return JSON.parse(raw) as CartItem[]
  } catch {
    return []
  }
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>(loadCart())

  const totalCount = computed(() =>
    items.value.reduce((sum, it) => sum + it.qty, 0),
  )

  const totalAmount = computed(() =>
    items.value.reduce((sum, it) => sum + it.price * it.qty, 0),
  )

  function persist() {
    localStorage.setItem(CART_KEY, JSON.stringify(items.value))
  }

  /** 加入购物车；已存在则累加数量（不超过库存） */
  function addItem(payload: {
    productId: number
    name: string
    price: number
    stock: number
    qty: number
    coverUrl?: string | null
  }) {
    const exist = items.value.find((it) => it.productId === payload.productId)
    if (exist) {
      const next = exist.qty + payload.qty
      exist.qty = Math.min(next, exist.stock)
      exist.stock = payload.stock
      exist.price = payload.price
      exist.name = payload.name
    } else {
      items.value.push({
        productId: payload.productId,
        name: payload.name,
        price: payload.price,
        stock: payload.stock,
        qty: Math.min(payload.qty, payload.stock),
        coverUrl: payload.coverUrl,
      })
    }
    persist()
  }

  function updateQty(productId: number, qty: number) {
    const exist = items.value.find((it) => it.productId === productId)
    if (!exist) return
    if (qty < 1) {
      removeItem(productId)
      return
    }
    exist.qty = Math.min(qty, exist.stock)
    persist()
  }

  function removeItem(productId: number) {
    items.value = items.value.filter((it) => it.productId !== productId)
    persist()
  }

  function clear() {
    items.value = []
    localStorage.removeItem(CART_KEY)
  }

  /** 提交给下单接口的 items */
  function toOrderItems() {
    return items.value.map((it) => ({
      productId: it.productId,
      qty: it.qty,
    }))
  }

  return {
    items,
    totalCount,
    totalAmount,
    addItem,
    updateQty,
    removeItem,
    clear,
    toOrderItems,
  }
})
