<template>
  <div class="product-detail-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-button
        type="default"
        :icon="ArrowLeft"
        @click="goBack"
      >
        Back to list
      </el-button>
      <span class="page-title">Product details</span>
      <el-tag v-if="product.isMarketable === 1" type="success">On sale</el-tag>
      <el-tag v-else type="info">Delisted</el-tag>
    </div>

    <el-row :gutter="24">
      <!-- 左侧：图片展示 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="10">
        <el-card shadow="hover" class="image-card">
          <div class="main-image-wrapper">
            <el-image
              v-if="mainImage"
              :src="mainImage"
              fit="contain"
              class="main-image"
              :preview-src-list="imageList"
            />
            <div v-else class="no-main-image">
              <el-icon size="64" color="#c0c4cc"><Picture /></el-icon>
              <p>No product image</p>
            </div>
          </div>

          <!-- 缩略图列表 -->
          <div v-if="imageList.length > 1" class="thumb-list">
            <div
              v-for="(img, index) in imageList"
              :key="index"
              class="thumb-item"
              :class="{ active: mainImage === img }"
              @click="mainImage = img"
            >
              <el-image
                :src="img"
                fit="cover"
                class="thumb-image"
              />
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：商品信息 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="14">
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="info-header">
              <h2 class="product-title">{{ product.name || '-' }}</h2>
              <div class="header-actions">
                <el-button
                  type="primary"
                  :icon="Edit"
                  @click="handleEdit"
                >
                  Edit
                </el-button>
                <el-button
                  :type="product.isMarketable === 1 ? 'danger' : 'success'"
                  @click="handleToggleStatus"
                >
                  {{ product.isMarketable === 1 ? 'Delist' : 'List' }}
                </el-button>
              </div>
            </div>
          </template>

          <div class="info-content">
            <!-- 商品ID -->
            <div class="info-row">
              <span class="info-label">Product ID:</span>
              <span class="info-value id-value">{{ product.id }}</span>
              <el-button
                link
                type="primary"
                size="small"
                @click="copyId(product.id)"
              >
                Copy
              </el-button>
            </div>

            <!-- SKU ID -->
            <div class="info-row">
              <span class="info-label">SKU ID：</span>
              <span class="info-value">{{ product.skuId || '-' }}</span>
            </div>

            <!-- 价格 -->
            <div class="info-row price-row">
              <span class="info-label">Price:</span>
              <span class="price-value">${{ formatPrice(product.price) }}</span>
            </div>

            <!-- 库存 -->
            <div class="info-row">
              <span class="info-label">Stock:</span>
              <el-tag :type="product.num > 0 ? 'success' : 'danger'" size="large">
                {{ product.num || 0 }} units
              </el-tag>
            </div>

            <!-- 上下架状态 -->
            <div class="info-row">
              <span class="info-label">Status:</span>
              <el-tag
                :type="product.isMarketable === 1 ? 'success' : 'info'"
                size="large"
              >
                {{ product.isMarketable === 1 ? 'On sale' : 'Delisted' }}
              </el-tag>
            </div>

            <el-divider />

            <!-- 商品简介 -->
            <div class="intro-section">
              <h4 class="section-title">Description</h4>
              <p class="intro-text">{{ product.intro || 'No description' }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 加载状态 -->
    <el-skeleton v-if="loading" :rows="6" animated />

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="Edit product"
      width="500px"
      destroy-on-close
    >
      <el-form :model="editForm" label-width="110px" label-position="right">
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
          <el-input v-model="editForm.image" placeholder="Image URL" />
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
        <el-button type="primary" :loading="editLoading" @click="handleSave">
          Save
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Edit, Picture } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const productId = route.params.id

const loading = ref(false)
const product = ref({})
const mainImage = ref('')
const imageList = ref([])

// 编辑弹窗
const dialogVisible = ref(false)
const editForm = ref({
  id: '',
  name: '',
  intro: '',
  price: 0,
  image: ''
})
const editLoading = ref(false)

// 获取商品详情
const fetchProductDetail = async () => {
  loading.value = true
  try {
    const res = await request.get(`/spu/admin/${productId}`)
    product.value = res || {}

    // 处理图片
    mainImage.value = res.image || ''
    if (res.images) {
      imageList.value = res.images.split(',').map(img => img.trim()).filter(Boolean)
    } else if (res.image) {
      imageList.value = [res.image]
    } else {
      imageList.value = []
    }
  } catch (error) {
    ElMessage.error(error.message || 'Failed to load product')
  } finally {
    loading.value = false
  }
}

// 格式化价格
const formatPrice = (price) => {
  if (!price && price !== 0) return '0.00'
  return (Number(price) / 100).toFixed(2)
}

// 元转分
const yuanToFen = (yuan) => {
  return Math.round(Number(yuan) * 100)
}

// 返回列表
const goBack = () => {
  router.push('/goods')
}

// 复制ID
const copyId = (id) => {
  navigator.clipboard.writeText(id).then(() => {
    ElMessage.success('Copied to clipboard')
  }).catch(() => {
    ElMessage.error('Copy failed')
  })
}

// 编辑商品
const handleEdit = () => {
  editForm.value = {
    id: product.value.id,
    name: product.value.name || '',
    intro: product.value.intro || '',
    price: formatPrice(product.value.price),
    image: product.value.image || ''
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
    fetchProductDetail()
  } catch (error) {
    ElMessage.error(error.message || 'Update failed')
  } finally {
    editLoading.value = false
  }
}

// 切换上下架状态
const handleToggleStatus = async () => {
  const newStatus = product.value.isMarketable === 1 ? 0 : 1
  const actionText = newStatus === 1 ? 'list' : 'delist'

  try {
    await ElMessageBox.confirm(
      `Are you sure you want to ${actionText} "${product.value.name}"?`,
      'Confirm',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    await request.put(`/spu/admin/status/${product.value.id}/${newStatus}`)
    ElMessage.success(newStatus === 1 ? 'Product listed' : 'Product delisted')
    fetchProductDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || 'Action failed')
    }
  }
}

onMounted(() => {
  fetchProductDetail()
})
</script>

<style scoped>
.product-detail-page {
  padding: 0;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.image-card {
  margin-bottom: 20px;
}

.main-image-wrapper {
  width: 100%;
  height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

.main-image {
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.no-main-image {
  text-align: center;
  color: #909399;
}

.no-main-image p {
  margin-top: 12px;
}

.thumb-list {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  flex-wrap: wrap;
}

.thumb-item {
  width: 80px;
  height: 80px;
  border: 2px solid transparent;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
}

.thumb-item:hover {
  border-color: #409eff;
}

.thumb-item.active {
  border-color: #409eff;
}

.thumb-image {
  width: 100%;
  height: 100%;
}

.info-card {
  height: 100%;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  margin-right: 16px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.info-content {
  padding: 8px 0;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
}

.info-label {
  width: 80px;
  color: #909399;
  flex-shrink: 0;
}

.info-value {
  color: #606266;
}

.id-value {
  font-family: 'Courier New', monospace;
  font-weight: 500;
}

.price-row {
  align-items: baseline;
}

.price-value {
  font-size: 24px;
  font-weight: 600;
  color: #f56c6c;
}

.section-title {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.intro-text {
  margin: 0;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}

.price-unit {
  margin-left: 8px;
  color: #606266;
  font-size: 14px;
}

.image-preview {
  margin-top: 12px;
}

:deep(.el-divider) {
  margin: 20px 0;
}

:deep(.el-skeleton) {
  padding: 20px;
}
</style>
