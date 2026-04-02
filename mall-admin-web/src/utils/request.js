import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useTenantStore } from '../store/tenant.js'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  (config) => {
    const tenantStore = useTenantStore()
    const tenantId = tenantStore.tenantId || '1001'
    if (tenantId) {
      config.headers['X-Tenant-Id'] = tenantId
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

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
