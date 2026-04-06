<template>
  <div class="mall-store" :style="{ '--primary-color': themeColor }">
    <!-- Header / Navigation Bar -->
    <header class="store-header" :style="{ background: themeColor }">
      <div class="header-content">
        <div class="store-info">
          <el-icon :size="28" color="#fff"><Shop /></el-icon>
          <h1 class="store-name">{{ storeName }}</h1>
          <el-tag effect="dark" type="warning" size="small" class="tenant-tag">
            ID: {{ tenantId }}
          </el-tag>
        </div>
        <div class="header-actions">
          <el-icon :size="24" color="#fff" class="action-icon"><Search /></el-icon>
          <el-icon :size="24" color="#fff" class="action-icon"><ShoppingCart /></el-icon>
          <el-icon :size="24" color="#fff" class="action-icon"><User /></el-icon>
        </div>
      </div>
    </header>

    <!-- Banner -->
    <section class="banner-section">
      <div class="banner-content" :style="{ background: `linear-gradient(135deg, ${themeColor} 0%, ${themeColor}99 100%)` }">
        <h2 class="banner-title">Welcome to {{ storeName }}</h2>
        <p class="banner-subtitle">Discover amazing products just for you</p>
        <el-button type="warning" size="large" round class="shop-now-btn">
          Shop Now
        </el-button>
      </div>
    </section>

    <!-- Category Tags -->
    <section class="category-section">
      <div class="category-list">
        <el-tag
          v-for="cat in categories"
          :key="cat.id"
          :type="cat.type"
          effect="dark"
          size="large"
          class="category-tag"
        >
          {{ cat.name }}
        </el-tag>
      </div>
    </section>

    <!-- Product / Brand List -->
    <main class="product-section">
      <h3 class="section-title">
        <el-icon><Goods /></el-icon>
        Hot Products
      </h3>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="products.length === 0" class="empty-container">
        <el-empty description="No products available" />
      </div>
      
      <div v-else class="product-grid">
        <div
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @click="handleProductClick(product)"
        >
          <div class="product-image" :style="{ background: getProductColor(product.initial) }">
            <el-icon :size="48" color="#fff"><Goods /></el-icon>
            <span class="initial-badge">{{ product.initial }}</span>
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <div class="product-meta">
              <el-tag size="small" effect="light" :type="tenantId === '1001' ? 'primary' : 'danger'">
                {{ tenantId === '1001' ? 'Tech' : 'Beauty' }}
              </el-tag>
              <span class="sort-order">Sort: {{ product.sort }}</span>
            </div>
            <p v-if="product.tenantId" class="tenant-info">
              Tenant: {{ product.tenantId }}
            </p>
          </div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer class="store-footer">
      <p>© 2024 {{ storeName }} - Powered by WH-SaaS Mall</p>
      <p class="domain-info">Domain: {{ currentDomain }}</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { post } from './utils/request.js'
import { Shop, Search, ShoppingCart, User, Goods } from '@element-plus/icons-vue'

// Reactive data
const storeName = ref('Store')
const tenantId = ref('1001')
const themeColor = ref('#409EFF')
const currentDomain = ref('')
const products = ref([])
const loading = ref(true)

const categories = ref([
  { id: 1, name: 'All', type: 'primary' },
  { id: 2, name: 'Hot Sale', type: 'danger' },
  { id: 3, name: 'New Arrival', type: 'success' },
  { id: 4, name: 'Discount', type: 'warning' }
])

// Get theme config based on domain
const getThemeConfig = (hostname) => {
  if (hostname.includes('shop1')) {
    return {
      tenantId: '1001',
      storeName: 'Shop A (科技专营)',
      themeColor: '#409EFF', // Blue
      category: 'Tech'
    }
  } else if (hostname.includes('shop2')) {
    return {
      tenantId: '1002',
      storeName: 'Shop B (美妆严选)',
      themeColor: '#F56C6C', // Red
      category: 'Beauty'
    }
  }
  // Default
  return {
    tenantId: '1001',
    storeName: 'WH-SaaS Mall',
    themeColor: '#409EFF',
    category: 'Default'
  }
}

// Generate product card color
const brandColors = ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#ffeaa7', '#dfe6e9', '#74b9ff', '#a29bfe']

const getProductColor = (initial) => {
  const index = initial ? initial.charCodeAt(0) % brandColors.length : 0
  return brandColors[index]
}

// Fetch brand/product data from backend
const fetchProducts = async () => {
  loading.value = true
  try {
    // Call real backend API: POST /api/goods/brand/search/1/10
    const res = await post('/goods/brand/search/1/10', {})
    if (res.code === 200 && res.data) {
      products.value = res.data.records || []
      ElMessage.success(`Loaded ${products.value.length} products from tenant ${tenantId.value}`)
    } else {
      // Fallback to mock data
      useMockData()
    }
  } catch (error) {
    console.error('API Error:', error)
    ElMessage.warning('Using mock data - API not available')
    useMockData()
  } finally {
    loading.value = false
  }
}

// Mock data fallback
const useMockData = () => {
  products.value = [
    { id: 1, name: 'iPhone 15 Pro Max', initial: 'A', sort: 1, tenantId: tenantId.value },
    { id: 2, name: 'Huawei Mate 60 Pro', initial: 'H', sort: 2, tenantId: tenantId.value },
    { id: 3, name: 'Xiaomi 14 Pro', initial: 'X', sort: 3, tenantId: tenantId.value },
    { id: 4, name: 'Samsung Galaxy S24', initial: 'S', sort: 4, tenantId: tenantId.value }
  ]
}

// Handle product click
const handleProductClick = (product) => {
  ElMessage.info(`Selected: ${product.name}`)
}

// Initialize on mount
onMounted(() => {
  const hostname = window.location.hostname
  currentDomain.value = hostname
  
  // Apply theme based on domain
  const config = getThemeConfig(hostname)
  tenantId.value = config.tenantId
  storeName.value = config.storeName
  themeColor.value = config.themeColor
  
  // Apply CSS variable for Element Plus
  document.documentElement.style.setProperty('--el-color-primary', config.themeColor)
  
  console.log(`[Store] Domain: ${hostname}`)
  console.log(`[Store] Tenant: ${config.tenantId} | Theme: ${config.themeColor}`)
  
  // Fetch real data from backend
  fetchProducts()
})
</script>

<style scoped>
.mall-store {
  min-height: 100vh;
  background: #f5f7fa;
}

/* Header */
.store-header {
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.store-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.store-name {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.tenant-tag {
  background: rgba(255, 255, 255, 0.2) !important;
  border: none !important;
}

.header-actions {
  display: flex;
  gap: 20px;
}

.action-icon {
  cursor: pointer;
  transition: opacity 0.3s;
}

.action-icon:hover {
  opacity: 0.7;
}

/* Banner */
.banner-section {
  padding: 20px;
}

.banner-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 40px;
  border-radius: 16px;
  text-align: center;
  color: #fff;
}

.banner-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
}

.banner-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 24px;
}

.shop-now-btn {
  font-weight: 600;
  padding: 12px 32px;
}

/* Categories */
.category-section {
  padding: 0 20px 20px;
}

.category-list {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.category-tag {
  cursor: pointer;
  transition: transform 0.2s;
}

.category-tag:hover {
  transform: translateY(-2px);
}

/* Product Section */
.product-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

.section-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.loading-container {
  padding: 40px;
  background: #fff;
  border-radius: 12px;
}

.empty-container {
  padding: 60px;
  background: #fff;
  border-radius: 12px;
}

/* Product Grid */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.product-image {
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.initial-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #606266;
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.sort-order {
  font-size: 13px;
  color: #909399;
}

.tenant-info {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

/* Footer */
.store-footer {
  text-align: center;
  padding: 40px 20px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
  color: #606266;
}

.domain-info {
  font-size: 13px;
  color: #909399;
  margin-top: 8px;
}

/* Responsive */
@media (max-width: 768px) {
  .store-name {
    font-size: 16px;
  }
  
  .banner-title {
    font-size: 24px;
  }
  
  .banner-subtitle {
    font-size: 14px;
  }
  
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }
  
  .product-image {
    height: 140px;
  }
}
</style>
