<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { loginApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import type { LoginForm } from '@/types/api'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive<LoginForm>({
  username: '',
  password: '',
})

const rules: FormRules<LoginForm> = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function onSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const { data } = await loginApi(form)
    auth.setAuth(data.data)
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/users'
    router.replace(redirect)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <el-card class="card" shadow="hover">
      <h2>登录</h2>
      <p class="tip">week1_shop · JWT 练习</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" @keyup.enter="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="用户名" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="onSubmit">
            登录
          </el-button>
        </el-form-item>
        <div class="link">
          还没有账号？
          <router-link to="/register">去注册</router-link>
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
  background: linear-gradient(160deg, #eef2ff 0%, #f8fafc 50%, #ecfeff 100%);
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
