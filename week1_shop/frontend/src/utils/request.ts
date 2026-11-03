import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import type { Result } from '@/types/api'
import router from '@/router'

const request = axios.create({
  timeout: 15000,
  // 配置了baseURL后, 不使用代理, 直接使用baseURL, 后端需要配置CORS
  // baseURL: 'http://127.0.0.1:8080',
})

request.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data as Result
    // 后端业务码：0 成功；拦截器 401 也会走 HTTP 401，见下方 error
    if (typeof res.code === 'number' && res.code !== 0) {
      ElMessage.error(res.msg || '请求失败')
      if (res.code === 401) {
        const auth = useAuthStore()
        auth.clearAuth()
        router.push('/login')
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return response
  },
  (error) => {
    const status = error.response?.status
    const data = error.response?.data as Result | undefined
    const msg = data?.msg || error.message || '网络错误'

    if (status === 401 || data?.code === 401) {
      const auth = useAuthStore()
      auth.clearAuth()
      router.push('/login')
      ElMessage.error(msg || '请先登录')
    } else {
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  },
)

export default request
