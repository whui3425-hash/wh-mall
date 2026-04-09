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

    // ==========================================
    // 【C端买家登录】Token 读取逻辑
    // ==========================================
    // C 端和 B 端使用不同的 Token 键名，实现完全独立的鉴权体系
    // buyer_token: C端买家专用（购物车、订单等）
    // token: B端管理员专用（后台管理）

    const path = config.url || ''

    // 判断是否为 C 端买家接口（购物车、订单、用户个人中心等）
    // 【统一】所有接口都以 /api 开头
    const isBuyerApi = path.startsWith('/api/cart') ||
                        path.startsWith('/api/order') ||
                        path.startsWith('/api/user') ||
                        (path.startsWith('/api/pay') && !path.includes('/admin'))

    if (isBuyerApi) {
      // 【C端】从 localStorage 读取 buyer_token
      const buyerToken = localStorage.getItem('buyer_token')
      if (buyerToken) {
        config.headers['Authorization'] = `Bearer ${buyerToken}`
        console.log('[C端请求] 已注入买家 Token:', path)
      }
    } else {
      // 【B端】从 localStorage 获取管理员 JWT Token 并注入请求头
      const token = localStorage.getItem('token')
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
      }
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const data = response.data
    const requestPath = response.config.url || ''

    // 判断是否为 C 端请求，用于区分处理 Token 过期
    // 【统一】所有接口都以 /api 开头
    const isBuyerApi = requestPath.startsWith('/api/cart') ||
                        requestPath.startsWith('/api/order') ||
                        requestPath.startsWith('/api/user') ||
                        (requestPath.startsWith('/api/pay') && !requestPath.includes('/admin'))

    // 统一处理 401 未授权
    if (data.code === 40100) {
      if (isBuyerApi) {
        // 【C端】买家 Token 过期处理
        ElMessage.error(data.message || '买家登录已过期，请重新登录')
        localStorage.removeItem('buyer_token')
        // 可以跳转到 C 端登录页（如有）
        // window.location.href = '/buyer/login'
      } else {
        // 【B端】管理员 Token 过期处理
        ElMessage.error(data.message || '登录已过期，请重新登录')
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      return Promise.reject(new Error('Unauthorized'))
    }
    return data
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      const requestPath = error.config?.url || ''
      // 【统一】所有接口都以 /api 开头
      const isBuyerApi = requestPath.startsWith('/api/cart') ||
                          requestPath.startsWith('/api/order') ||
                          requestPath.startsWith('/api/user')

      if (isBuyerApi) {
        ElMessage.error('买家登录已过期，请重新登录')
        localStorage.removeItem('buyer_token')
      } else {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
    } else {
      ElMessage.error(error.message || 'Request failed')
    }
    return Promise.reject(error)
  }
)

export const get = (url, params) => request.get(url, { params })
export const post = (url, data) => request.post(url, data)

export default request
