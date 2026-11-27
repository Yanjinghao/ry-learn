import type { App, Directive } from 'vue'
import { useAuthStore } from '@/stores/auth'

/**
 * 按钮权限指令：无权限则移除 DOM
 * 用法：
 *   v-permission="'product:edit'"
 *   v-permission="['order:pay', 'order:cancel']"  // 有任一即可
 */
const permission: Directive<HTMLElement, string | string[]> = {
  mounted(el, binding) {
    const value = binding.value
    if (value == null || value === '') {
      throw new Error(`v-permission 需要权限字，例如 v-permission="'product:edit'"`)
    }
    const auth = useAuthStore()
    const ok = auth.hasPermission(value)
    if (!ok) {
      el.parentNode?.removeChild(el)
    }
  },
}

export function setupPermissionDirective(app: App) {
  app.directive('permission', permission)
}
