<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelOrderApi,
  fetchMyOrdersApi,
  payOrderApi,
} from '@/api/order'
import { formatDateTime } from '@/utils/format'
import type { Order } from '@/types/api'

const loading = ref(false)
const tableData = ref<Order[]>([])
const actionLoadingId = ref<number | null>(null)

function statusLabel(status: number) {
  if (status === 0) return '待支付'
  if (status === 1) return '已支付'
  if (status === 2) return '已取消'
  return String(status)
}

function statusType(status: number) {
  if (status === 0) return 'warning'
  if (status === 1) return 'success'
  if (status === 2) return 'info'
  return 'info'
}

async function loadList() {
  loading.value = true
  try {
    const { data } = await fetchMyOrdersApi()
    tableData.value = data.data || []
  } finally {
    loading.value = false
  }
}

async function onPay(row: Order) {
  await ElMessageBox.confirm(`确认 mock 支付订单 ${row.orderNo}？`, '提示', {
    type: 'warning',
    confirmButtonText: '支付',
    cancelButtonText: '取消',
  })
  actionLoadingId.value = row.id
  try {
    await payOrderApi(row.id)
    ElMessage.success('支付成功')
    await loadList()
  } finally {
    actionLoadingId.value = null
  }
}

async function onCancel(row: Order) {
  await ElMessageBox.confirm(`确认取消订单 ${row.orderNo}？库存将加回。`, '提示', {
    type: 'warning',
    confirmButtonText: '取消订单',
    cancelButtonText: '返回',
  })
  actionLoadingId.value = row.id
  try {
    await cancelOrderApi(row.id)
    ElMessage.success('已取消')
    await loadList()
  } finally {
    actionLoadingId.value = null
  }
}

onMounted(() => {
  loadList()
})
</script>

<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <span class="hint">仅展示当前登录用户的订单；待支付可支付或取消。</span>
        <el-button type="primary" :loading="loading" @click="loadList">刷新</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column label="金额" width="120">
          <template #default="{ row }">
            ¥{{ row.payAmount }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button
                type="primary"
                link
                :loading="actionLoadingId === row.id"
                @click="onPay(row)"
              >
                支付
              </el-button>
              <el-button
                type="danger"
                link
                :loading="actionLoadingId === row.id"
                @click="onCancel(row)"
              >
                取消
              </el-button>
            </template>
            <span v-else class="muted">—</span>
          </template>
        </el-table-column>
      </el-table>
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
}
.hint {
  color: #64748b;
  font-size: 13px;
}
.muted {
  color: #94a3b8;
  font-size: 12px;
}
</style>
