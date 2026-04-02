<template>
  <el-container class="admin-layout">
    <!-- Left Sidebar -->
    <el-aside 
      :width="isCollapse ? '64px' : '220px'" 
      class="sidebar"
      :style="{ background: tenantStore.sidebarBg }"
    >
      <div class="sidebar-logo" :style="{ background: tenantStore.sidebarBg + 'cc' }">
        <el-icon :size="26" :color="tenantStore.themeColor"><Shop /></el-icon>
        <span v-show="!isCollapse" class="logo-text" :title="tenantStore.tenantName">
          {{ tenantStore.tenantName }}
        </span>
      </div>
      
      <el-menu
        :default-active="$route.path"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        :background-color="tenantStore.sidebarBg"
        text-color="#bfcbd9"
        active-text-color="#fff"
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>Dashboard</template>
        </el-menu-item>
        
        <el-sub-menu index="/goods">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/goods/brand">品牌管理</el-menu-item>
          <el-menu-item index="/goods/category">分类管理</el-menu-item>
          <el-menu-item index="/goods/spu">商品列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/orders">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/orders/list">订单列表</el-menu-item>
          <el-menu-item index="/orders/refund">退款管理</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <template #title>系统设置</template>
        </el-menu-item>
      </el-menu>
      
      <!-- Tenant Badge -->
      <div v-show="!isCollapse" class="tenant-badge" :style="{ background: tenantStore.themeColor + '20' }">
        <span class="tenant-dot" :style="{ background: tenantStore.themeColor }"></span>
        <span class="tenant-text" :style="{ color: tenantStore.themeColor }">
          {{ tenantStore.tenantId }}
        </span>
      </div>
    </el-aside>
    
    <!-- Right Main Container -->
    <el-container class="main-container">
      <!-- Header -->
      <el-header class="admin-header">
        <div class="header-left">
          <el-icon 
            :size="20" 
            class="collapse-btn"
            @click="toggleSidebar"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <breadcrumb />
        </div>
        
        <div class="header-right">
          <!-- Domain Info -->
          <div class="domain-tag">
            <el-icon :size="14"><Link /></el-icon>
            <span>{{ tenantStore.hostname }}</span>
          </div>
          
          <el-divider direction="vertical" />
          
          <!-- Fullscreen -->
          <el-icon :size="18" class="header-icon" @click="toggleFullscreen">
            <FullScreen />
          </el-icon>
          
          <!-- User Profile -->
          <el-dropdown trigger="click">
            <div class="user-profile">
              <el-avatar :size="32" :icon="UserFilled" class="user-avatar" />
              <span class="username">Admin</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><Setting /></el-icon> 账号设置
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- Main Content -->
      <el-main class="admin-main">
        <div class="content-wrapper">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTenantStore } from '../store/tenant.js'
import { ElMessage } from 'element-plus'
import {
  Shop, Odometer, Goods, ShoppingCart, User, Setting,
  Fold, Expand, ArrowDown, FullScreen, UserFilled,
  Link, SwitchButton
} from '@element-plus/icons-vue'

const $route = useRoute()
const router = useRouter()
const tenantStore = useTenantStore()
const isCollapse = ref(false)

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const logout = () => {
  localStorage.removeItem('tenantId')
  router.push('/login')
}

onMounted(() => {
  tenantStore.applyTheme()
  console.log(`[Tenant] ${tenantStore.tenantName} (${tenantStore.tenantId})`)
  console.log(`[Theme] ${tenantStore.themeColor}`)
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

/* Sidebar Styles */
.sidebar {
  box-shadow: 2px 0 8px rgba(0, 21, 41, 0.4);
  transition: all 0.3s;
  z-index: 100;
  position: relative;
}

.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.logo-text {
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.3px;
  white-space: nowrap;
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 120px);
}

.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.05) !important;
}

/* Tenant Badge */
.tenant-badge {
  position: absolute;
  bottom: 16px;
  left: 16px;
  right: 16px;
  padding: 10px 14px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.tenant-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  box-shadow: 0 0 8px currentColor;
}

.tenant-text {
  font-size: 13px;
  font-weight: 600;
  font-family: 'Courier New', monospace;
}

/* Main Container */
.main-container {
  background: #f0f2f5;
  display: flex;
  flex-direction: column;
}

/* Header Styles */
.admin-header {
  height: 64px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  z-index: 99;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-btn {
  cursor: pointer;
  color: #606266;
  transition: all 0.3s;
  padding: 8px;
  border-radius: 6px;
}

.collapse-btn:hover {
  color: var(--el-color-primary);
  background: #f5f7fa;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* Domain Tag */
.domain-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: #f5f7fa;
  border-radius: 20px;
  font-size: 13px;
  color: #606266;
  border: 1px solid #e4e7ed;
}

.header-icon {
  cursor: pointer;
  color: #606266;
  transition: color 0.3s;
  padding: 8px;
  border-radius: 6px;
}

.header-icon:hover {
  color: var(--el-color-primary);
  background: #f5f7fa;
}

/* User Profile */
.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: all 0.3s;
}

.user-profile:hover {
  background: #f5f7fa;
}

.user-avatar {
  transition: all 0.3s;
}

.username {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

/* Main Content */
.admin-main {
  padding: 20px;
  background: #f0f2f5;
  overflow-y: auto;
}

.content-wrapper {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  min-height: calc(100vh - 104px);
  box-shadow: 0 2px 12px rgba(0, 21, 41, 0.08);
}

/* Page Transition */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
