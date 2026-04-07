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

// Request interceptor - inject X-Tenant-Id header
request.interceptors.request.use(
  (config) => {
    const tenantId = getTenantIdByDomain()
    config.headers['X-Tenant-Id'] = tenantId
    console.log(`[Request] ${config.url} | Tenant: ${tenantId}`)
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
    // Return the full response data
    return response.data
  },
  (error) => {
    const message = error.response?.data?.message || error.message || 'Request failed'
    ElMessage.error(message)
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
