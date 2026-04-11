<template>
  <div class="product-detail-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <el-button
        type="default"
        :icon="ArrowLeft"
        @click="goBack"
      >
        返回列表
      </el-button>
      <span class="page-title">商品详情</span>
      <el-tag v-if="product.isMarketable === 1" type="success">上架中</el-tag>
      <el-tag v-else type="info">已下架</el-tag>
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
              <p>暂无商品图片</p>
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
                  编辑
                </el-button>
                <el-button
                  :type="product.isMarketable === 1 ? 'danger' : 'success'"
                  @click="handleToggleStatus"
                >
                  {{ product.isMarketable === 1 ? '下架' : '上架' }}
                </el-button>
              </div>
            </div>
          </template>

          <div class="info-content">
            <!-- 商品ID -->
            <div class="info-row">
              <span class="info-label">商品ID：</span>
              <span class="info-value id-value">{{ product.id }}</span>
              <el-button
                link
                type="primary"
                size="small"
                @click="copyId(product.id)"
              >
                复制
              </el-button>
            </div>

            <!-- SKU ID -->
            <div class="info-row">
              <span class="info-label">SKU ID：</span>
              <span class="info-value">{{ product.skuId || '-' }}</span>
            </div>

            <!-- 价格 -->
            <div class="info-row price-row">
              <span class="info-label">价格：</span>
              <span class="price-value">¥ {{ formatPrice(product.price) }}</span>
            </div>

            <!-- 库存 -->
            <div class="info-row">
              <span class="info-label">库存：</span>
              <el-tag :type="product.num > 0 ? 'success' : 'danger'" size="large">
                {{ product.num || 0 }} 件
              </el-tag>
            </div>

            <!-- 上下架状态 -->
            <div class="info-row">
              <span class="info-label">状态：</span>
              <el-tag
                :type="product.isMarketable === 1 ? 'success' : 'info'"
                size="large"
              >
                {{ product.isMarketable === 1 ? '上架中' : '已下架' }}
              </el-tag>
            </div>

            <el-divider />

            <!-- 商品简介 -->
            <div class="intro-section">
              <h4 class="section-title">商品简介</h4>
              <p class="intro-text">{{ product.intro || '暂无简介' }}</p>
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
      title="编辑商品"
      width="500px"
      destroy-on-close
    >
      <el-form :model="editForm" label-width="80px" label-position="right">
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
          <el-input v-model="editForm.image" placeholder="请输入图片URL" />
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
        <el-button type="primary" :loading="editLoading" @click="handleSave">
          保存
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
    ElMessage.error(error.message || '获取商品详情失败')
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
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
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
    fetchProductDetail()
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  } finally {
    editLoading.value = false
  }
}

// 切换上下架状态
const handleToggleStatus = async () => {
  const newStatus = product.value.isMarketable === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '上架' : '下架'

  try {
    await ElMessageBox.confirm(
      `确定要${actionText}商品 "${product.value.name}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await request.put(`/spu/admin/status/${product.value.id}/${newStatus}`)
    ElMessage.success(`${actionText}成功`)
    fetchProductDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
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
