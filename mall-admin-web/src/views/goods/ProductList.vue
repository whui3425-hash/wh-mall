<template>
  <div class="product-list-page">
    <el-card class="product-list-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <div class="title-section">
          <el-icon size="20" color="#409EFF"><Goods /></el-icon>
          <span class="title">Products</span>
          <el-tag type="info" effect="plain" class="count-tag">
            {{ tableData.length }} products
          </el-tag>
        </div>
        <el-button
          type="primary"
          :icon="Refresh"
          :loading="loading"
          @click="fetchProductList"
        >
          Refresh
        </el-button>
      </div>
    </template>

    <el-table
      v-loading="loading"
      :data="tableData"
      stripe
      border
      style="width: 100%"
      :header-cell-style="{
        background: '#f5f7fa',
        color: '#606266',
        fontWeight: 600
      }"
    >
      <el-table-column
        label="Image"
        width="80"
        align="center"
      >
        <template #default="{ row }">
          <el-avatar
            v-if="row.image"
            :size="50"
            :src="row.image"
            shape="square"
            fit="cover"
            class="product-thumb"
            @click="showImagePreview(row.image)"
          />
          <div v-else class="no-image">
            <el-icon size="24" color="#c0c4cc"><Picture /></el-icon>
          </div>
        </template>
      </el-table-column>

      <el-table-column
        prop="id"
        label="Product ID"
        width="100"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <span class="product-id">{{ row.id }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="name"
        label="Name"
        min-width="180"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <span class="product-name">{{ row.name || '-' }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="price"
        label="Price"
        width="120"
        align="right"
      >
        <template #default="{ row }">
          <span class="price">${{ formatPrice(row.price) }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="num"
        label="Stock"
        width="100"
        align="center"
      >
        <template #default="{ row }">
          <el-tag :type="row.num > 0 ? 'success' : 'danger'" size="small" effect="light">
            {{ row.num || 0 }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        prop="isMarketable"
        label="Listing status"
        width="100"
        align="center"
      >
        <template #default="{ row }">
          <el-tag
            :type="row.isMarketable === 1 ? 'success' : 'info'"
            effect="light"
            size="small"
          >
            {{ row.isMarketable === 1 ? 'Listed' : 'Delisted' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        prop="skuId"
        label="SKU ID"
        width="120"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <span class="sku-id">{{ row.skuId || '-' }}</span>
        </template>
      </el-table-column>

      <el-table-column
        label="Actions"
        width="240"
        align="center"
        fixed="right"
      >
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            size="small"
            @click="goToDetail(row)"
          >
            Details
          </el-button>
          <el-button
            link
            type="warning"
            size="small"
            @click="handleEdit(row)"
          >
            Edit
          </el-button>
          <el-button
            link
            :type="row.isMarketable === 1 ? 'danger' : 'success'"
            size="small"
            :loading="row.statusLoading"
            @click="handleToggleStatus(row)"
          >
            {{ row.isMarketable === 1 ? 'Delist' : 'List' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空状态 -->
    <el-empty
      v-if="!loading && tableData.length === 0"
      description="No products yet"
      :image-size="120"
    >
      <el-button type="primary" @click="fetchProductList">
        Reload
      </el-button>
    </el-empty>
  </el-card>

  <!-- 编辑弹窗 -->
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="500px"
    destroy-on-close
  >
    <el-form
      :model="editForm"
      label-width="110px"
      label-position="right"
    >
      <el-form-item label="Product ID">
        <el-input v-model="editForm.id" disabled />
      </el-form-item>

      <el-form-item label="Name">
        <el-input
          v-model="editForm.name"
          placeholder="Product name"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="Introduction">
        <el-input
          v-model="editForm.intro"
          type="textarea"
          :rows="3"
          placeholder="Short description"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="Price (USD)">
        <el-input-number
          v-model="editForm.price"
          :min="0"
          :precision="2"
          :step="0.1"
          style="width: 180px"
        />
      </el-form-item>

      <el-form-item label="Image URL">
        <el-input
          v-model="editForm.image"
          placeholder="Image URL"
        />
        <div v-if="editForm.image" class="image-preview">
          <el-image
            :src="editForm.image"
            fit="cover"
            style="width: 120px; height: 120px; border-radius: 4px"
          />
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">Cancel</el-button>
      <el-button
        type="primary"
        :loading="editLoading"
        @click="handleSave"
      >
        Save
      </el-button>
    </template>
  </el-dialog>

  <!-- 图片预览 -->
  <el-dialog
    v-model="previewVisible"
    title="Image preview"
    width="600px"
    align-center
  >
    <div class="preview-container">
      <el-image
        :src="previewImage"
        fit="contain"
        style="width: 100%; max-height: 500px"
        :preview-src-list="previewImage ? [previewImage] : []"
      />
    </div>
  </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Goods, Refresh, Picture } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])

// 编辑弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('Edit product')
const editForm = ref({
  id: '',
  name: '',
  intro: '',
  price: 0,
  image: ''
})
const editLoading = ref(false)

// 获取商品列表
const fetchProductList = async () => {
  loading.value = true
  try {
    const res = await request.get('/spu/admin/list')
    // 为每行添加状态加载标记
    tableData.value = (res || []).map(item => ({ ...item, statusLoading: false }))
  } catch (error) {
    ElMessage.error(error.message || 'Failed to load products')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 格式化价格（分转元）
const formatPrice = (price) => {
  if (!price && price !== 0) return '0.00'
  return (Number(price) / 100).toFixed(2)
}

// 元转分
const yuanToFen = (yuan) => {
  return Math.round(Number(yuan) * 100)
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 编辑商品
const handleEdit = (row) => {
  dialogTitle.value = 'Edit product'
  editForm.value = {
    id: row.id,
    name: row.name || '',
    intro: row.intro || '',
    price: formatPrice(row.price),
    image: row.image || ''
  }
  dialogVisible.value = true
}

// 保存编辑
const handleSave = async () => {
  if (!editForm.value.name.trim()) {
    ElMessage.warning('Product name is required')
    return
  }

  editLoading.value = true
  try {
    const data = {
      id: editForm.value.id,
      name: editForm.value.name.trim(),
      intro: editForm.value.intro.trim(),
      price: yuanToFen(editForm.value.price),
      image: editForm.value.image
    }
    await request.put('/spu/admin/update', data)
    ElMessage.success('Product updated')
    dialogVisible.value = false
    fetchProductList()
  } catch (error) {
    ElMessage.error(error.message || 'Update failed')
  } finally {
    editLoading.value = false
  }
}

// 跳转到商品详情页
const goToDetail = (row) => {
  router.push(`/goods/detail/${row.id}`)
}

// 图片预览
const previewVisible = ref(false)
const previewImage = ref('')
const showImagePreview = (image) => {
  previewImage.value = image
  previewVisible.value = true
}

// 切换上下架状态
const handleToggleStatus = async (row) => {
  const newStatus = row.isMarketable === 1 ? 0 : 1
  const actionText = newStatus === 1 ? 'list' : 'delist'

  try {
    await ElMessageBox.confirm(
      `Are you sure you want to ${actionText} "${row.name}"?`,
      'Confirm',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    row.statusLoading = true
    await request.put(`/spu/admin/status/${row.id}/${newStatus}`)
    ElMessage.success(newStatus === 1 ? 'Product listed' : 'Product delisted')
    fetchProductList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || 'Action failed')
    }
  } finally {
    row.statusLoading = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchProductList()
})
</script>

<style scoped>
.product-list-card {
  margin: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.count-tag {
  font-size: 12px;
}

.product-id {
  font-family: 'Courier New', monospace;
  font-weight: 500;
  color: #606266;
}

.product-name {
  font-weight: 500;
  color: #303133;
}

.price {
  font-weight: 600;
  color: #f56c6c;
  font-size: 14px;
}

.sku-id {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #909399;
}

.time-icon {
  margin-right: 4px;
  vertical-align: middle;
}

.time {
  color: #606266;
  font-size: 13px;
}

:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper th) {
  background-color: #f5f7fa !important;
}

:deep(.el-tag) {
  font-weight: 500;
}

:deep(.el-tag--success) {
  background-color: #f0f9eb;
  border-color: #e1f3d8;
  color: #67c23a;
}

:deep(.el-button--primary) {
  font-weight: 500;
}

:deep(.el-empty) {
  padding: 40px 0;
}

.price-unit {
  margin-left: 8px;
  color: #606266;
  font-size: 14px;
}

.image-preview {
  margin-top: 12px;
}

:deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-dialog__body) {
  padding: 24px 20px;
}

:deep(.el-dialog__footer) {
  padding: 16px 20px;
  border-top: 1px solid #e4e7ed;
}

.product-thumb {
  cursor: pointer;
  transition: transform 0.2s;
}

.product-thumb:hover {
  transform: scale(1.05);
}

.no-image {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 4px;
}

.preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}
</style>

<style scoped>
.product-list-page {
  width: 100%;
}
</style>
