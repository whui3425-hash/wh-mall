import axios from 'axios'
import { ElMessage } from 'element-plus'
import { resolveTenantIdFromHostname } from '@/utils/tenant'

// Create axios instance with base URL
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - inject X-Tenant-Id header and Buyer Token
request.interceptors.request.use(
  (config) => {
    const tenantId = resolveTenantIdFromHostname(window.location.hostname)
    config.headers['X-Tenant-Id'] = tenantId
    
    // ==========================================
    // 【修复3】Axios 拦截器每次发车前强制挂载 Token
    // ==========================================
    // 【关键】每次请求都实时读取 localStorage，确保最新 Token
    const buyerToken = localStorage.getItem('buyer_token')
    if (buyerToken) {
      // 【关键】必须带 'Bearer ' 前缀，符合后端 JWT 规范
      config.headers['Authorization'] = `Bearer ${buyerToken}`
      console.log(`[Request] ${config.url} | Tenant: ${tenantId} | Token: ${buyerToken.substring(0, 15)}...`)
    } else {
      console.log(`[Request] ${config.url} | Tenant: ${tenantId} | 无Token`)
    }
    
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor - handle responses and errors
request.interceptors.response.use(
  (response) => {
    const data = response.data
    
    // 处理 401 未授权（网关返回的业务状态码）
    if (data.code === 40100 || data.code === 401) {
      ElMessage.error(data.message || '登录已过期，请重新登录')
      localStorage.removeItem('buyer_token')
      localStorage.removeItem('buyer_username')
      localStorage.removeItem('buyer_userId')
      // 可以在这里触发全局登录弹窗
      window.dispatchEvent(new CustomEvent('buyer:login-required'))
      return Promise.reject(new Error('Unauthorized'))
    }
    
    // Return the full response data
    return data
  },
  (error) => {
    // 处理 HTTP 401 状态码
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('buyer_token')
      localStorage.removeItem('buyer_username')
      localStorage.removeItem('buyer_userId')
      window.dispatchEvent(new CustomEvent('buyer:login-required'))
    } else {
      const message = error.response?.data?.message || error.message || 'Request failed'
      ElMessage.error(message)
    }
    return Promise.reject(error)
  }
)

// Export HTTP methods
export const get = (url, params) => {
  return request.get(url, { params })
}

export const post = (url, data) => {
  return request.post(url, data)
}

export const put = (url, data) => {
  return request.put(url, data)
}

export const del = (url) => {
  return request.delete(url)
}

export default request
