<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createCategoryApi,
  deleteCategoryApi,
  fetchCategoryList,
  updateCategoryApi,
} from '@/api/category'
import type { Category, CategoryForm } from '@/types/api'

const loading = ref(false)
const tableData = ref<Category[]>([])

const dialogVisible = ref(false)
const dialogLoading = ref(false)
const editingId = ref<number | null>(null)
const form = reactive<CategoryForm>({
  name: '',
  sort: 0,
})

async function loadList() {
  loading.value = true
  try {
    const { data } = await fetchCategoryList()
    tableData.value = data.data || []
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  form.name = ''
  form.sort = 0
  dialogVisible.value = true
}

function openEdit(row: Category) {
  editingId.value = row.id
  form.name = row.name
  form.sort = row.sort
  dialogVisible.value = true
}

async function submitForm() {
  if (!form.name.trim()) {
    ElMessage.warning('分类名称不能为空')
    return
  }
  dialogLoading.value = true
  try {
    const body = {
      name: form.name.trim(),
      sort: form.sort ?? 0,
    }
    if (editingId.value == null) {
      await createCategoryApi(body)
      ElMessage.success('已新增')
    } else {
      await updateCategoryApi(editingId.value, body)
      ElMessage.success('已更新')
    }
    dialogVisible.value = false
    await loadList()
  } finally {
    dialogLoading.value = false
  }
}

async function onDelete(row: Category) {
  await ElMessageBox.confirm(`确认删除分类「${row.name}」？`, '提示', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
  })
  await deleteCategoryApi(row.id)
  ElMessage.success('已删除')
  await loadList()
}

function onCategoryCommand(command: string, row: Category) {
  if (command === 'edit') openEdit(row)
  else if (command === 'delete') onDelete(row)
}

onMounted(loadList)
</script>

<template>
  <div>
    <el-card shadow="never">
      <div class="toolbar">
        <el-button type="primary" @click="openCreate">新增分类</el-button>
        <el-button @click="loadList">刷新</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="名称" min-width="160" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="操作" width="88" fixed="right" align="center">
          <template #default="{ row }">
            <el-dropdown trigger="click" @command="(cmd: string) => onCategoryCommand(cmd, row)">
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
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="editingId == null ? '新增分类' : '编辑分类'"
      width="420px"
    >
      <el-form label-width="70px">
        <el-form-item label="名称">
          <el-input v-model="form.name" maxlength="64" show-word-limit placeholder="分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="9999" />
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
.toolbar {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
}
.danger-text {
  color: var(--el-color-danger);
}
</style>
