import axios from 'axios'
import { ElMessage } from 'element-plus'

// Create axios instance with base URL
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Get tenant ID based on current domain
const getTenantIdByDomain = () => {
  const hostname = window.location.hostname.toLowerCase()
  if (hostname.includes('shop1')) {
    return '1001'
  } else if (hostname.includes('shop2')) {
    return '1002'
  }
  return '1001'
}

// Request interceptor - inject X-Tenant-Id header and Buyer Token
request.interceptors.request.use(
  (config) => {
    const tenantId = getTenantIdByDomain()
    config.headers['X-Tenant-Id'] = tenantId
    
    // ==========================================
    // 【C端买家登录】Token 注入逻辑
    // ==========================================
    // 从 localStorage 读取 buyer_token（C端专用）
    const buyerToken = localStorage.getItem('buyer_token')
    if (buyerToken) {
      config.headers['Authorization'] = `Bearer ${buyerToken}`
      console.log(`[Request] ${config.url} | Tenant: ${tenantId} | Buyer Token: ${buyerToken.substring(0, 20)}...`)
    } else {
      console.log(`[Request] ${config.url} | Tenant: ${tenantId} | No Token`)
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
