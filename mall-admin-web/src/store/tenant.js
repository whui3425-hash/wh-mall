import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useTenantStore = defineStore('tenant', () => {
  const tenantId = ref(localStorage.getItem('tenantId') || '1001')

  const setTenantId = (id) => {
    tenantId.value = id
    localStorage.setItem('tenantId', id)
    window.location.reload()
  }

  watch(tenantId, (newVal) => {
    localStorage.setItem('tenantId', newVal)
  })

  return {
    tenantId,
    setTenantId
  }
})
