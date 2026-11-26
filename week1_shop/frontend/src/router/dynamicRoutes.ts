import type { Router } from 'vue-router'
import type { MenuNode } from '@/types/api'
import { componentMap } from '@/router/componentMap'

const addedNames: string[] = []

/** 取出 type=1 且有 path/component 的叶子菜单 */
export function flattenMenuLeaves(menus: MenuNode[]): MenuNode[] {
  const result: MenuNode[] = []
  const walk = (list: MenuNode[]) => {
    for (const m of list) {
      if (m.type === 1 && m.path && m.component) {
        result.push(m)
      }
      if (m.children?.length) {
        walk(m.children)
      }
    }
  }
  walk(menus)
  return result
}

/** 登录后的默认首页：第一个有权限的菜单 */
export function resolveHomePath(menus: MenuNode[]): string {
  const leaves = flattenMenuLeaves(menus)
  return leaves[0]?.path || '/products'
}

/** 根据菜单树动态注册到 layout 下 */
export function registerDynamicRoutes(router: Router, menus: MenuNode[]) {
  removeDynamicRoutes(router)

  const leaves = flattenMenuLeaves(menus)
  for (const m of leaves) {
    const name = m.component as string
    const loader = componentMap[name]
    if (!loader) continue

    // children 用相对 path：'/products' → 'products'
    const relativePath = m.path!.replace(/^\//, '')
    router.addRoute('layout', {
      path: relativePath,
      name,
      component: loader,
      meta: { title: m.name, perms: m.perms },
    })
    addedNames.push(name)
  }
}

export function removeDynamicRoutes(router: Router) {
  for (const name of addedNames) {
    if (router.hasRoute(name)) {
      router.removeRoute(name)
    }
  }
  addedNames.length = 0
}
