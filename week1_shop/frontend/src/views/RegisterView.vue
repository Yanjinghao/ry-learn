<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { registerApi } from '@/api/auth'
import type { RegisterForm } from '@/types/api'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive<RegisterForm>({
  username: '',
  password: '',
  nickname: '',
})

const rules: FormRules<RegisterForm> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 32, message: '用户名 3～32 位', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 64, message: '密码至少 6 位', trigger: 'blur' },
  ],
}

async function onSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await registerApi(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <el-card class="card" shadow="hover">
      <h2>注册</h2>
      <p class="tip">创建账号后返回登录</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" @keyup.enter="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="至少 3 位" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="至少 6 位" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="可选" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="onSubmit">
            注册
          </el-button>
        </el-form-item>
        <div class="link">
          已有账号？
          <router-link to="/login">去登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #ecfeff 0%, #f8fafc 50%, #eef2ff 100%);
}
.card {
  width: 420px;
}
h2 {
  margin: 0 0 4px;
  text-align: center;
}
.tip {
  margin: 0 0 20px;
  text-align: center;
  color: #64748b;
  font-size: 13px;
}
.link {
  text-align: center;
  font-size: 14px;
  color: #64748b;
}
</style>
