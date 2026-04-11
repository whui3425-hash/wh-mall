import axios from 'axios'
import { useRouter } from 'vue-router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 注入 Token
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    // 注入租户 ID
    const tenantId = localStorage.getItem('admin_tenant_id')
    if (tenantId) {
      config.headers['X-Tenant-Id'] = tenantId
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 统一解包逻辑
    const res = response.data
    console.log('[响应拦截器] 响应数据:', res)

    // 兼容多种成功状态码: 200, 0, 10000, 20000 等
    const successCodes = [200, 0, 10000, 20000, '200', '0', '10000', '20000']
    if (!successCodes.includes(res.code)) {
      console.log('[响应拦截器] 业务错误 code:', res.code, 'message:', res.message)
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data
  },
  (error) => {
    const { response } = error
    if (response) {
      console.log('[响应拦截器] HTTP错误 status:', response.status)
      // 处理 401 未授权
      if (response.status === 401) {
        localStorage.removeItem('admin_token')
        window.location.href = '/login'
      }
      return Promise.reject(new Error(response.data?.message || '请求失败'))
    }
    return Promise.reject(error)
  }
)

export default request
