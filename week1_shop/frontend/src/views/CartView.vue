<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createOrderApi } from '@/api/order'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cart = useCartStore()
const submitting = ref(false)

async function submitOrder() {
  if (!cart.items.length) {
    ElMessage.warning('购物车为空')
    return
  }
  for (const it of cart.items) {
    if (it.qty > it.stock) {
      ElMessage.warning(`「${it.name}」库存不足`)
      return
    }
  }

  await ElMessageBox.confirm(
    `确认下单 ${cart.items.length} 种商品，合计 ¥${cart.totalAmount.toFixed(2)}？`,
    '结算确认',
    { type: 'warning', confirmButtonText: '下单', cancelButtonText: '再看看' },
  )

  submitting.value = true
  try {
    const { data } = await createOrderApi({ items: cart.toOrderItems() })
    ElMessage.success(`下单成功：${data.data.orderNo}`)
    cart.clear()
    router.push('/orders')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <span class="hint">可将多个商品加入购物车后一次性下单（多明细）。</span>
        <div class="actions">
          <el-button @click="router.push('/products')">继续选购</el-button>
          <el-button
            type="primary"
            :loading="submitting"
            :disabled="!cart.items.length"
            @click="submitOrder"
          >
            结算下单
          </el-button>
        </div>
      </div>

      <el-table :data="cart.items" border stripe empty-text="购物车是空的">
        <el-table-column prop="name" label="商品" min-width="160" />
        <el-table-column label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="数量" width="160" align="center">
          <template #default="{ row }">
            <el-input-number
              :model-value="row.qty"
              :min="1"
              :max="Math.max(row.stock, 1)"
              :step="1"
              size="small"
              @update:model-value="(v: number | undefined) => cart.updateQty(row.productId, v || 1)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" align="center" />
        <el-table-column label="小计" width="110">
          <template #default="{ row }">
            ¥{{ (row.price * row.qty).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" align="center">
          <template #default="{ row }">
            <el-button type="danger" link @click="cart.removeItem(row.productId)">
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="footer">
        <span>共 {{ cart.totalCount }} 件</span>
        <span class="amount">合计 ¥{{ cart.totalAmount.toFixed(2) }}</span>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.hint {
  color: #64748b;
  font-size: 13px;
}
.actions {
  display: flex;
  gap: 8px;
}
.footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #334155;
}
.amount {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}
</style>
