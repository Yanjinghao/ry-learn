<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteUserApi,
  fetchUserById,
  fetchUserList,
  updateNicknameApi,
  updateStatusApi,
} from '@/api/user'
import { formatDateTime } from '@/utils/format'
import type { User } from '@/types/api'

const loading = ref(false)
const tableData = ref<User[]>([])
const query = reactive<{ username: string; status: number | undefined }>({
  username: '',
  status: undefined,
})

const detailVisible = ref(false)
const detailLoading = ref(false)
const detailUser = ref<User | null>(null)

const nickVisible = ref(false)
const nickLoading = ref(false)
const nickForm = reactive({ id: 0, nickname: '' })

async function loadList() {
  loading.value = true
  try {
    const params: { username?: string; status?: number } = {}
    if (query.username.trim()) params.username = query.username.trim()
    if (query.status !== undefined && query.status !== null) params.status = query.status
    const { data } = await fetchUserList(params)
    tableData.value = data.data || []
  } finally {
    loading.value = false
  }
}

async function onView(row: User) {
  detailVisible.value = true
  detailLoading.value = true
  detailUser.value = null
  try {
    const { data } = await fetchUserById(row.id)
    detailUser.value = data.data
  } finally {
    detailLoading.value = false
  }
}

function openNickname(row: User) {
  nickForm.id = row.id
  nickForm.nickname = row.nickname || ''
  nickVisible.value = true
}

async function submitNickname() {
  if (!nickForm.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  nickLoading.value = true
  try {
    await updateNicknameApi(nickForm.id, nickForm.nickname.trim())
    ElMessage.success('昵称已更新')
    nickVisible.value = false
    await loadList()
  } finally {
    nickLoading.value = false
  }
}

async function onToggleStatus(row: User) {
  const nextStatus = row.status === 1 ? 0 : 1
  const action = nextStatus === 0 ? '禁用' : '启用'
  await ElMessageBox.confirm(`确认${action}用户「${row.username}」？`, '提示', {
    type: 'warning',
    confirmButtonText: action,
    cancelButtonText: '取消',
  })
  await updateStatusApi(row.id, nextStatus)
  ElMessage.success(`已${action}`)
  await loadList()
}

async function onDelete(row: User) {
  await ElMessageBox.confirm(`确认删除用户「${row.username}」？`, '提示', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消',
  })
  await deleteUserApi(row.id)
  ElMessage.success('已删除')
  await loadList()
}

function onUserCommand(command: string, row: User) {
  if (command === 'view') onView(row)
  else if (command === 'nickname') openNickname(row)
  else if (command === 'status') onToggleStatus(row)
  else if (command === 'delete') onDelete(row)
}

onMounted(loadList)
</script>

<template>
  <div>
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="用户名">
          <el-input v-model="query.username" clearable placeholder="模糊查询" style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button
            @click="
              query.username = '';
              query.status = undefined;
              loadList();
            "
          >
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="88" fixed="right" align="center">
          <template #default="{ row }">
            <el-dropdown trigger="click" @command="(cmd: string) => onUserCommand(cmd, row)">
              <el-button type="primary" link>操作</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="view">详情</el-dropdown-item>
                  <el-dropdown-item command="nickname">改昵称</el-dropdown-item>
                  <el-dropdown-item command="status">
                    {{ row.status === 1 ? '禁用' : '启用' }}
                  </el-dropdown-item>
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

    <el-dialog v-model="detailVisible" title="用户详情" width="480px">
      <div v-loading="detailLoading">
        <el-descriptions v-if="detailUser" :column="1" border>
          <el-descriptions-item label="ID">{{ detailUser.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ detailUser.username }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ detailUser.nickname }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            {{ detailUser.status === 1 ? '启用' : '禁用' }}
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(detailUser.createdAt) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <el-dialog v-model="nickVisible" title="修改昵称" width="400px">
      <el-form label-width="70px">
        <el-form-item label="昵称">
          <el-input v-model="nickForm.nickname" maxlength="32" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="nickVisible = false">取消</el-button>
        <el-button type="primary" :loading="nickLoading" @click="submitNickname">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.danger-text {
  color: var(--el-color-danger);
}
</style>

