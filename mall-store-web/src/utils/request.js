import axios from 'axios'
import { ElMessage } from 'element-plus'

// Create axios instance
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// Get tenant ID based on domain
const getTenantIdByDomain = () => {
  const hostname = window.location.hostname.toLowerCase()
  if (hostname.includes('shop1')) {
    return '1001'
  } else if (hostname.includes('shop2')) {
    return '1002'
  }
  return '1001' // Default
}

// Request interceptor
request.interceptors.request.use(
  (config) => {
    const tenantId = getTenantIdByDomain()
    config.headers['X-Tenant-Id'] = tenantId
    console.log(`[Request] ${config.url} | Tenant: ${tenantId}`)
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    ElMessage.error(error.message || 'Request failed')
    return Promise.reject(error)
  }
)

export const get = (url, params) => request.get(url, { params })
export const post = (url, data) => request.post(url, data)

export default request
