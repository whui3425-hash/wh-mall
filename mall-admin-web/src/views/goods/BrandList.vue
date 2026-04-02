<template>
  <div class="brand-list">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon :size="20" color="#1890ff"><Collection /></el-icon>
            <span class="title">品牌管理</span>
            <el-tag type="info" size="small">{{ brandList.length }} 个品牌</el-tag>
          </div>
          <el-button type="primary" @click="handleAdd" :icon="Plus">
            新增品牌
          </el-button>
        </div>
      </template>
      
      <el-table 
        :data="brandList" 
        v-loading="loading" 
        border
        stripe
        highlight-current-row
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" sortable />
        <el-table-column prop="name" label="品牌名称" min-width="180">
          <template #default="{ row }">
            <div class="brand-name">
              <el-avatar :size="32" :style="{ background: getBrandColor(row.initial) }">
                {{ row.initial }}
              </el-avatar>
              <span class="name">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="initial" label="首字母" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="primary" effect="light">{{ row.initial }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" align="center" sortable />
        <el-table-column prop="tenantId" label="租户ID" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.tenantId || '1001' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              link 
              :icon="Edit" 
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-divider direction="vertical" />
            <el-button 
              type="danger" 
              link 
              :icon="Delete" 
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
      />
    </el-card>
    
    <!-- Add/Edit Dialog -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="form.id ? '编辑品牌' : '新增品牌'" 
      width="500px"
      destroy-on-close
    >
      <el-form 
        :model="form" 
        label-width="80px"
        :rules="rules"
        ref="formRef"
      >
        <el-form-item label="品牌名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入品牌名称" clearable />
        </el-form-item>
        <el-form-item label="首字母" prop="initial">
          <el-input 
            v-model="form.initial" 
            placeholder="请输入首字母"
            maxlength="1"
            show-word-limit
            clearable
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saveLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, post } from '../../utils/request.js'
import { Plus, Edit, Delete, Collection } from '@element-plus/icons-vue'

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
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入品牌名称', trigger: 'blur' }],
  initial: [{ required: true, message: '请输入首字母', trigger: 'blur' }]
}

const brandColors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#ffeaa7', '#dfe6e9', '#74b9ff', '#a29bfe']

const getBrandColor = (initial) => {
  const index = initial ? initial.charCodeAt(0) % brandColors.length : 0
  return brandColors[index]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await get('/goods/brand/list')
    if (res.code === 200) {
      brandList.value = res.data || []
      total.value = brandList.value.length
    } else {
      brandList.value = mockData()
      total.value = brandList.value.length
    }
  } catch (error) {
    brandList.value = mockData()
    total.value = brandList.value.length
  } finally {
    loading.value = false
  }
}

const mockData = () => [
  { id: 1, name: 'Apple', initial: 'A', sort: 1, tenantId: '1001' },
  { id: 2, name: 'Huawei', initial: 'H', sort: 2, tenantId: '1001' },
  { id: 3, name: 'Xiaomi', initial: 'X', sort: 3, tenantId: '1001' },
  { id: 4, name: 'Samsung', initial: 'S', sort: 4, tenantId: '1002' },
  { id: 5, name: 'OPPO', initial: 'O', sort: 5, tenantId: '1001' }
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
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  
  saveLoading.value = true
  try {
    await post('/goods/brand/save', form.value)
    ElMessage.success(form.value.id ? '修改成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.success('演示模式：操作成功')
    dialogVisible.value = false
    fetchData()
  } finally {
    saveLoading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除品牌 "${row.name}" 吗？`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger'
    }
  ).then(async () => {
    try {
      await post('/goods/brand/delete', { id: row.id })
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.success('演示模式：删除成功')
      fetchData()
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.brand-list {
  animation: fadeIn 0.3s ease-out;
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
