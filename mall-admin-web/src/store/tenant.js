import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const tenantConfig = {
  'shop1': {
    tenantId: '1001',
    tenantName: 'Shop A (科技数码)',
    themeColor: '#409EFF',
    sidebarBg: '#304156',
    sidebarActive: '#409EFF'
  },
  'shop2': {
    tenantId: '1002',
    tenantName: 'Shop B (美妆严选)',
    themeColor: '#F56C6C',
    sidebarBg: '#2b1d1d',
    sidebarActive: '#F56C6C'
  }
}

const defaultConfig = tenantConfig['shop1']

export const useTenantStore = defineStore('tenant', () => {
  const hostname = ref(window.location.hostname)
  
  const currentTenant = computed(() => {
    const host = hostname.value.toLowerCase()
    for (const key of Object.keys(tenantConfig)) {
      if (host.includes(key)) {
        return tenantConfig[key]
      }
    }
    return defaultConfig
  })
  
  const tenantId = computed(() => currentTenant.value.tenantId)
  const tenantName = computed(() => currentTenant.value.tenantName)
  const themeColor = computed(() => currentTenant.value.themeColor)
  const sidebarBg = computed(() => currentTenant.value.sidebarBg)
  const sidebarActive = computed(() => currentTenant.value.sidebarActive)
  
  const applyTheme = () => {
    const color = themeColor.value
    document.documentElement.style.setProperty('--el-color-primary', color)
    document.documentElement.style.setProperty('--el-color-primary-light-3', color + '99')
    document.documentElement.style.setProperty('--el-color-primary-light-9', color + '1a')
    
    const style = document.createElement('style')
    style.id = 'tenant-theme-style'
    style.innerHTML = `
      .sidebar-menu .el-menu-item.is-active {
        background: ${color} !important;
      }
      .user-avatar {
        background: ${color} !important;
      }
      .el-button--primary {
        background: ${color} !important;
        border-color: ${color} !important;
      }
      .el-button--primary:hover {
        background: ${color}dd !important;
        border-color: ${color}dd !important;
      }
    `
    const oldStyle = document.getElementById('tenant-theme-style')
    if (oldStyle) oldStyle.remove()
    document.head.appendChild(style)
  }
  
  return {
    hostname,
    tenantId,
    tenantName,
    themeColor,
    sidebarBg,
    sidebarActive,
    applyTheme
  }
})
