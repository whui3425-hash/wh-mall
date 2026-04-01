<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">Mall Admin</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-sub-menu index="/goods">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>Goods</span>
          </template>
          <el-menu-item index="/goods/brand">Brand</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <el-select
            v-model="tenantStore.tenantId"
            @change="handleTenantChange"
            style="width: 150px"
          >
            <el-option label="Tenant A (1001)" value="1001" />
            <el-option label="Tenant B (1002)" value="1002" />
          </el-select>
          <el-dropdown>
            <span class="user-info">
              Admin <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">Logout</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useTenantStore } from '../store/tenant.js'
import { Goods, ArrowDown } from '@element-plus/icons-vue'

const $route = useRoute()
const router = useRouter()
const tenantStore = useTenantStore()

const handleTenantChange = (val) => {
  tenantStore.setTenantId(val)
}

const logout = () => {
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.sidebar {
  background: #304156;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
.header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.user-info {
  cursor: pointer;
  color: #606266;
}
</style>
