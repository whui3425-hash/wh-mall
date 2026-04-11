<template>
  <div class="product-list-page">
    <el-card class="product-list-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <div class="title-section">
          <el-icon size="20" color="#409EFF"><Goods /></el-icon>
          <span class="title">商品列表</span>
          <el-tag type="info" effect="plain" class="count-tag">
            共 {{ tableData.length }} 件商品
          </el-tag>
        </div>
        <el-button
          type="primary"
          :icon="Refresh"
          :loading="loading"
          @click="fetchProductList"
        >
          刷新数据
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
        label="商品图片"
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
        label="商品ID"
        width="100"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <span class="product-id">{{ row.id }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="name"
        label="商品名称"
        min-width="180"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <span class="product-name">{{ row.name || '-' }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="price"
        label="价格"
        width="120"
        align="right"
      >
        <template #default="{ row }">
          <span class="price">¥ {{ formatPrice(row.price) }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="num"
        label="库存"
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
        label="上架状态"
        width="100"
        align="center"
      >
        <template #default="{ row }">
          <el-tag
            :type="row.isMarketable === 1 ? 'success' : 'info'"
            effect="light"
            size="small"
          >
            {{ row.isMarketable === 1 ? '上架' : '下架' }}
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
        label="操作"
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
            查看详情
          </el-button>
          <el-button
            link
            type="warning"
            size="small"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            link
            :type="row.isMarketable === 1 ? 'danger' : 'success'"
            size="small"
            :loading="row.statusLoading"
            @click="handleToggleStatus(row)"
          >
            {{ row.isMarketable === 1 ? '下架' : '上架' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空状态 -->
    <el-empty
      v-if="!loading && tableData.length === 0"
      description="暂无商品数据"
      :image-size="120"
    >
      <el-button type="primary" @click="fetchProductList">
        重新加载
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
      label-width="80px"
      label-position="right"
    >
      <el-form-item label="商品ID">
        <el-input v-model="editForm.id" disabled />
      </el-form-item>

      <el-form-item label="商品名称">
        <el-input
          v-model="editForm.name"
          placeholder="请输入商品名称"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="商品简介">
        <el-input
          v-model="editForm.intro"
          type="textarea"
          :rows="3"
          placeholder="请输入商品简介"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="价格">
        <el-input-number
          v-model="editForm.price"
          :min="0"
          :precision="2"
          :step="0.1"
          style="width: 180px"
        />
        <span class="price-unit">元</span>
      </el-form-item>

      <el-form-item label="商品图片">
        <el-input
          v-model="editForm.image"
          placeholder="请输入图片URL"
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
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button
        type="primary"
        :loading="editLoading"
        @click="handleSave"
      >
        保存
      </el-button>
    </template>
  </el-dialog>

  <!-- 图片预览 -->
  <el-dialog
    v-model="previewVisible"
    title="图片预览"
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
const dialogTitle = ref('编辑商品')
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
    ElMessage.error(error.message || '获取商品列表失败')
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
  dialogTitle.value = '编辑商品'
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
    ElMessage.warning('商品名称不能为空')
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
    ElMessage.success('商品更新成功')
    dialogVisible.value = false
    fetchProductList()
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
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
  const actionText = newStatus === 1 ? '上架' : '下架'

  try {
    await ElMessageBox.confirm(
      `确定要${actionText}商品 "${row.name}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    row.statusLoading = true
    await request.put(`/spu/admin/status/${row.id}/${newStatus}`)
    ElMessage.success(`${actionText}成功`)
    fetchProductList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
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
