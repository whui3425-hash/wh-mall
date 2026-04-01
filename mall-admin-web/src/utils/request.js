import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  (config) => {
    const tenantId = localStorage.getItem('tenantId') || '1001'
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
