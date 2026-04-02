<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useTenantStore } from './store/tenant.js'

const tenantStore = useTenantStore()

onMounted(() => {
  tenantStore.applyTheme()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  width: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}
</style>
