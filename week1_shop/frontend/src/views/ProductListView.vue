<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchCategoryList } from '@/api/category'
import {
  createProductApi,
  deleteProductApi,
  fetchProductPage,
  updateProductApi,
} from '@/api/product'
import { formatDateTime } from '@/utils/format'
import type { Category, Product, ProductForm } from '@/types/api'

const loading = ref(false)
const tableData = ref<Product[]>([])
const categories = ref<Category[]>([])

const query = reactive<{
  name: string
  categoryId: number | undefined
  status: number | undefined
}>({
  name: '',
  categoryId: undefined,
  status: undefined,
})

const pager = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
})

const dialogVisible = ref(false)
const dialogLoading = ref(false)
const editingId = ref<number | null>(null)
const form = reactive<ProductForm>({
  categoryId: undefined,
  name: '',
  price: undefined,
  stock: 0,
  coverUrl: '',
  status: 1,
})

const categoryNameMap = ref<Record<number, string>>({})

function categoryLabel(categoryId: number) {
  return categoryNameMap.value[categoryId] || String(categoryId)
}

async function loadCategories() {
  const { data } = await fetchCategoryList()
  categories.value = data.data || []
  const map: Record<number, string> = {}
  for (const c of categories.value) {
    map[c.id] = c.name
  }
  categoryNameMap.value = map
}

async function loadList() {
  loading.value = true
  try {
    const params: {
      pageNum: number
      pageSize: number
      name?: string
      categoryId?: number
      status?: number
    } = {
      pageNum: pager.pageNum,
      pageSize: pager.pageSize,
    }
    if (query.name.trim()) params.name = query.name.trim()
    if (query.categoryId != null) params.categoryId = query.categoryId
    if (query.status != null) params.status = query.status

    const { data } = await fetchProductPage(params)
    const page = data.data
    tableData.value = page?.records || []
    pager.total = page?.total || 0
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pager.pageNum = 1
  loadList()
}

function onReset() {
  query.name = ''
  query.categoryId = undefined
  query.status = undefined
  pager.pageNum = 1
  loadList()
}

function openCreate() {
  editingId.value = null
  form.categoryId = categories.value[0]?.id
  form.name = ''
  form.price = undefined
  form.stock = 0
  form.coverUrl = ''
  form.status = 1
  dialogVisible.value = true
}

function openEdit(row: Product) {
  editingId.value = row.id
  form.categoryId = row.categoryId
  form.name = row.name
  form.price = Number(row.price)
  form.stock = row.stock
  form.coverUrl = row.coverUrl || ''
  form.status = row.status
  dialogVisible.value = true
}

async function submitForm() {
  if (form.categoryId == null) {
    ElMessage.warning('请选择分类')
    return
  }
  if (!form.name.trim()) {
    ElMessage.warning('商品名称不能为空')
    return
  }
  if (form.price == null || form.price < 0) {
    ElMessage.warning('请填写合法价格')
    return
  }
  if (form.stock == null || form.stock < 0) {
    ElMessage.warning('请填写合法库存')
    return
  }

  dialogLoading.value = true
  try {
    const body: ProductForm = {
      categoryId: form.categoryId,
      name: form.name.trim(),
      price: form.price,
      stock: form.stock,
      coverUrl: form.coverUrl.trim(),
      status: form.status,
    }
    if (editingId.value == null) {
      await createProductApi(body)
      ElMessage.success('已新增')
    } else {
      await updateProductApi(editingId.value, body)
      ElMessage.success('已更新')
    }
    dialogVisible.value = false
    await loadList()
  } finally {
    dialogLoading.value = false
  }
}

async function onDelete(row: Product) {
  await ElMessageBox.confirm(`确认删除商品「${row.name}」？`, '提示', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
  })
  await deleteProductApi(row.id)
  ElMessage.success('已删除')
  await loadList()
}

function onProductCommand(command: string, row: Product) {
  if (command === 'edit') openEdit(row)
  else if (command === 'delete') onDelete(row)
}

onMounted(async () => {
  await loadCategories()
  await loadList()
})
</script>

<template>
  <div>
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="名称">
          <el-input v-model="query.name" clearable placeholder="模糊查询" style="width: 160px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" clearable placeholder="全部" style="width: 140px">
            <el-option
              v-for="c in categories"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button @click="onReset">重置</el-button>
          <el-button type="success" @click="openCreate">新增商品</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="名称" min-width="140" />
        <el-table-column label="分类" min-width="100">
          <template #default="{ row }">
            {{ categoryLabel(row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="封面" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.coverUrl"
              :src="row.coverUrl"
              fit="cover"
              style="width: 40px; height: 40px"
              :preview-src-list="[row.coverUrl]"
              preview-teleported
            />
            <span v-else class="muted">无</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="88" fixed="right" align="center">
          <template #default="{ row }">
            <el-dropdown trigger="click" @command="(cmd: string) => onProductCommand(cmd, row)">
              <el-button type="primary" link>操作</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <span class="danger-text">删除</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="pager.pageNum"
          v-model:page-size="pager.pageSize"
          background
          layout="total, sizes, prev, pager, next"
          :total="pager.total"
          :page-sizes="[5, 10, 20, 50]"
          @current-change="loadList"
          @size-change="
            () => {
              pager.pageNum = 1
              loadList()
            }
          "
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="editingId == null ? '新增商品' : '编辑商品'"
      width="520px"
    >
      <el-form label-width="80px">
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="c in categories"
              :key="c.id"
              :label="c.name"
              :value="c.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="form.name" maxlength="64" show-word-limit />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="封面URL">
          <el-input v-model="form.coverUrl" placeholder="可先填图片链接，上传周三再做" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="dialogLoading" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.muted {
  color: #94a3b8;
  font-size: 12px;
}
.danger-text {
  color: var(--el-color-danger);
}
</style>
