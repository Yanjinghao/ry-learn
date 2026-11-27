<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelOrderApi,
  fetchMyOrdersApi,
  payOrderApi,
} from '@/api/order'
import { formatDateTime } from '@/utils/format'
import { useAuthStore } from '@/stores/auth'
import type { Order } from '@/types/api'

const auth = useAuthStore()
const isAdmin = computed(() => auth.roles.includes('admin'))

const loading = ref(false)
const tableData = ref<Order[]>([])
const actionLoadingId = ref<number | null>(null)

const detailVisible = ref(false)
const detailOrder = ref<Order | null>(null)

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

function openDetail(row: Order) {
  detailOrder.value = row
  detailVisible.value = true
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
        <span class="hint">
          {{ isAdmin ? '管理员可查看全部订单。' : '仅展示你自己的订单。' }}
          待支付可支付或取消。
        </span>
        <el-button type="primary" :loading="loading" @click="loadList">刷新</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column
          v-if="isAdmin"
          prop="username"
          label="用户名"
          min-width="120"
        />
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
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">详情</el-button>
            <template v-if="row.status === 0">
              <el-button
                v-permission="'order:pay'"
                type="success"
                link
                :loading="actionLoadingId === row.id"
                @click="onPay(row)"
              >
                支付
              </el-button>
              <el-button
                v-permission="'order:cancel'"
                type="danger"
                link
                :loading="actionLoadingId === row.id"
                @click="onCancel(row)"
              >
                取消
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="detailVisible"
      :title="detailOrder ? `订单详情 ${detailOrder.orderNo}` : '订单详情'"
      width="560px"
    >
      <template v-if="detailOrder">
        <el-descriptions :column="1" border class="mb12">
          <el-descriptions-item label="订单号">{{ detailOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item v-if="isAdmin" label="用户名">
            {{ detailOrder.username || '—' }}
          </el-descriptions-item>
          <el-descriptions-item label="金额">¥{{ detailOrder.payAmount }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(detailOrder.status)" size="small">
              {{ statusLabel(detailOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(detailOrder.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="items-title">商品明细</div>
        <el-table :data="detailOrder.items || []" border size="small">
          <el-table-column label="商品" min-width="160">
            <template #default="{ row }">
              {{ row.productName || `商品#${row.productId}` }}
            </template>
          </el-table-column>
          <el-table-column prop="qty" label="数量" width="80" align="center" />
          <el-table-column label="单价" width="100" align="right">
            <template #default="{ row }">
              ¥{{ row.price }}
            </template>
          </el-table-column>
          <el-table-column label="小计" width="100" align="right">
            <template #default="{ row }">
              ¥{{ Number(row.price) * Number(row.qty) }}
            </template>
          </el-table-column>
        </el-table>
        <div v-if="!detailOrder.items?.length" class="muted empty-tip">暂无明细</div>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
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
.mb12 {
  margin-bottom: 12px;
}
.items-title {
  margin: 8px 0;
  font-weight: 600;
  color: #0f172a;
}
.empty-tip {
  margin-top: 8px;
}
</style>
