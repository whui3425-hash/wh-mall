<template>
  <div class="brand-list">
    <!-- Tenant Info & Actions -->
    <el-card shadow="never" class="header-card">
      <div class="header-actions">
        <div class="tenant-info">
          <el-icon :size="18" :color="tenantStore.themeColor"><OfficeBuilding /></el-icon>
          <span class="label">Current Tenant:</span>
          <el-tag :color="tenantStore.themeColor + '20'" :style="{ color: tenantStore.themeColor, borderColor: tenantStore.themeColor }">
            {{ tenantStore.tenantName }} (ID: {{ tenantStore.tenantId }})
          </el-tag>
        </div>
        <div class="actions">
          <el-button type="primary" :icon="Refresh" @click="fetchData" :loading="loading">
            Refresh Data
          </el-button>
          <el-button type="success" :icon="Plus" @click="handleAdd">
            Add Brand
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- Brand Table -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon :size="20" color="#1890ff"><Collection /></el-icon>
            <span class="title">Brand Management</span>
            <el-tag type="info" size="small" effect="light">{{ total }} records</el-tag>
          </div>
        </div>
      </template>

      <el-table
        :data="brandList"
        v-loading="loading"
        border
        stripe
        highlight-current-row
        style="width: 100%"
        empty-text="No data available"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" sortable />
        <el-table-column prop="name" label="Brand Name" min-width="180">
          <template #default="{ row }">
            <div class="brand-name">
              <el-avatar :size="36" :src="row.image" :style="{ background: getBrandColor(row.initial) }">
                <span v-if="!row.image">{{ row.initial }}</span>
              </el-avatar>
              <span class="name">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="initial" label="Initial" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="primary" effect="light">{{ row.initial }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="Sort" width="100" align="center" sortable />
        <el-table-column label="Image" width="120" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.image"
              :src="row.image"
              :preview-src-list="[row.image]"
              style="width: 50px; height: 50px; border-radius: 4px"
              fit="cover"
            />
            <el-tag v-else type="info" size="small">No Image</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tenantId" label="Tenant ID" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small" effect="dark">
              {{ row.tenantId || tenantStore.tenantId }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Operations" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              :icon="Edit"
              @click="handleEdit(scope.row)"
            >
              Edit
            </el-button>
            <el-divider direction="vertical" />
            <el-button
              type="danger"
              link
              :icon="Delete"
              @click="handleDelete(scope.row)"
            >
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? 'Edit Brand' : 'Add Brand'"
      width="500px"
      destroy-on-close
    >
      <el-form
        :model="form"
        label-width="100px"
        :rules="rules"
        ref="formRef"
      >
        <el-form-item label="Brand Name" prop="name">
          <el-input v-model="form.name" placeholder="Enter brand name" clearable />
        </el-form-item>
        <el-form-item label="Initial" prop="initial">
          <el-input
            v-model="form.initial"
            placeholder="Enter initial letter"
            maxlength="1"
            show-word-limit
            clearable
          />
        </el-form-item>
        <el-form-item label="Image URL" prop="image">
          <el-input v-model="form.image" placeholder="Enter image URL" clearable />
        </el-form-item>
        <el-form-item label="Sort" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSave" :loading="saveLoading">Confirm</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTenantStore } from '../../store/tenant.js'
import { post } from '../../utils/request.js'
import { Plus, Edit, Delete, Collection, Refresh, OfficeBuilding } from '@element-plus/icons-vue'

const tenantStore = useTenantStore()
const brandList = ref([])
const loading = ref(false)
const saveLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const form = ref({
  id: null,
  name: '',
  initial: '',
  image: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: 'Please enter brand name', trigger: 'blur' }],
  initial: [{ required: true, message: 'Please enter initial letter', trigger: 'blur' }]
}

const brandColors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#ffeaa7', '#dfe6e9', '#74b9ff', '#a29bfe']

const getBrandColor = (initial) => {
  const index = initial ? initial.charCodeAt(0) % brandColors.length : 0
  return brandColors[index]
}

/**
 * Fetch brand list from backend
 * Real API: POST /api/brand/search/{page}/{size}
 */
const fetchData = async () => {
  loading.value = true
  try {
    const res = await post(`/brand/search/${currentPage.value}/${pageSize.value}`, {})
    if (res.code === 200 && res.data) {
      brandList.value = res.data.records || []
      total.value = res.data.total || 0
      ElMessage.success(`Loaded ${brandList.value.length} brands for tenant ${tenantStore.tenantId}`)
    } else {
      ElMessage.warning(res.message || 'Failed to load data')
      // Fallback to mock data if API fails
      useMockData()
    }
  } catch (error) {
    console.error('API Error:', error)
    ElMessage.error(`API Error: ${error.message || 'Network error'}`)
    // Fallback to mock data
    useMockData()
  } finally {
    loading.value = false
  }
}

// Mock data for fallback
const useMockData = () => {
  brandList.value = [
    { id: 1, name: 'Apple', initial: 'A', sort: 1, image: 'https://example.com/apple.png', tenantId: tenantStore.tenantId },
    { id: 2, name: 'Huawei', initial: 'H', sort: 2, image: 'https://example.com/huawei.png', tenantId: tenantStore.tenantId },
    { id: 3, name: 'Xiaomi', initial: 'X', sort: 3, image: '', tenantId: tenantStore.tenantId },
    { id: 4, name: 'Samsung', initial: 'S', sort: 4, image: 'https://example.com/samsung.png', tenantId: tenantStore.tenantId }
  ]
  total.value = brandList.value.length
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handlePageChange = (val) => {
  currentPage.value = val
  fetchData()
}

const handleAdd = () => {
  form.value = { id: null, name: '', initial: '', image: '', sort: 0 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  saveLoading.value = true
  try {
    const url = form.value.id ? '/brand' : '/brand'
    const method = form.value.id ? 'put' : 'post'
    const res = await post(url, { ...form.value, _method: method })
    if (res.code === 200) {
      ElMessage.success(form.value.id ? 'Updated successfully' : 'Added successfully')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || 'Operation failed')
    }
  } catch (error) {
    ElMessage.error(`Error: ${error.message}`)
  } finally {
    saveLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure to delete brand "${row.name}"?`,
    'Confirm Delete',
    {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await post(`/brand/${row.id}`, { _method: 'delete' })
      if (res.code === 200) {
        ElMessage.success('Deleted successfully')
        fetchData()
      } else {
        ElMessage.error(res.message || 'Delete failed')
      }
    } catch (error) {
      ElMessage.error(`Error: ${error.message}`)
    }
  })
}

onMounted(() => {
  console.log(`[BrandList] Current tenant: ${tenantStore.tenantId}`)
  console.log(`[BrandList] API Base URL: /api/brand/search/${currentPage.value}/${pageSize.value}`)
  fetchData()
})
</script>

<style scoped>
.brand-list {
  animation: fadeIn 0.3s ease-out;
}

.header-card {
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tenant-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tenant-info .label {
  color: #606266;
  font-size: 14px;
}

.actions {
  display: flex;
  gap: 10px;
}

.table-card {
  min-height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.brand-name {
  display: flex;
  align-items: center;
  gap: 12px;
}

.name {
  font-weight: 500;
  color: #303133;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

:deep(.el-table) {
  border-radius: 8px;
}

:deep(.el-table__row) {
  transition: background-color 0.2s;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}
</style>
