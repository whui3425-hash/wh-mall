<template>
  <div class="brand-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>Brand Management</span>
          <el-button type="primary" @click="handleAdd">Add Brand</el-button>
        </div>
      </template>
      
      <el-table :data="brandList" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="Brand Name" />
        <el-table-column prop="initial" label="Initial" width="100" />
        <el-table-column prop="sort" label="Sort" width="100" />
        <el-table-column label="Actions" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">Edit</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog v-model="dialogVisible" title="Brand" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="Name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="Initial">
          <el-input v-model="form.initial" maxlength="1" />
        </el-form-item>
        <el-form-item label="Sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="handleSave">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, post } from '../../utils/request.js'

const brandList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({
  id: null,
  name: '',
  initial: '',
  sort: 0
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await get('/goods/brand/list')
    if (res.code === 200) {
      brandList.value = res.data || []
    } else {
      ElMessage.warning(res.message || 'Failed to load data')
      brandList.value = mockData()
    }
  } catch (error) {
    ElMessage.warning('Using mock data')
    brandList.value = mockData()
  } finally {
    loading.value = false
  }
}

const mockData = () => [
  { id: 1, name: 'Apple', initial: 'A', sort: 1 },
  { id: 2, name: 'Huawei', initial: 'H', sort: 2 },
  { id: 3, name: 'Xiaomi', initial: 'X', sort: 3 }
]

const handleAdd = () => {
  form.value = { id: null, name: '', initial: '', sort: 0 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    await post('/goods/brand/save', form.value)
    ElMessage.success('Saved successfully')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.success('Mock: Saved successfully')
    dialogVisible.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('Confirm delete?', 'Warning', {
    confirmButtonText: 'OK',
    cancelButtonText: 'Cancel',
    type: 'warning'
  }).then(async () => {
    try {
      await post('/goods/brand/delete', { id: row.id })
      ElMessage.success('Deleted successfully')
      fetchData()
    } catch (error) {
      ElMessage.success('Mock: Deleted successfully')
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.brand-list {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
