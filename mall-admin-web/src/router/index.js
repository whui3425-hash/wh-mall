import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: '/orders',
        name: 'Orders',
        component: () => import('@/views/order/OrderList.vue')
      },
      {
        path: '/goods',
        name: 'Goods',
        component: () => import('@/views/goods/ProductList.vue')
      },
      {
        path: '/goods/detail/:id',
        name: 'ProductDetail',
        component: () => import('@/views/goods/ProductDetail.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')
  console.log('[路由守卫] 从', from.path, '到', to.path, 'token存在:', !!token)

  // 访问非登录页面且没有 token，强制跳转到登录页
  if (!to.meta.public && !token) {
    console.log('[路由守卫] 无token，拦截到登录页')
    next('/login')
    return
  }

  // 已登录用户访问登录页，重定向到首页
  if (to.path === '/login' && token) {
    console.log('[路由守卫] 已登录用户访问登录页，重定向到首页')
    next('/')
    return
  }

  console.log('[路由守卫] 允许通过')
  next()
})

export default router
