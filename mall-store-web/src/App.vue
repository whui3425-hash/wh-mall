<template>
  <div class="mall-store">
    <!-- Header / Navigation Bar -->
    <header class="store-header" :style="{ background: themeColor }">
      <div class="header-content">
        <div class="store-info">
          <el-icon :size="32" color="#fff"><Shop /></el-icon>
          <div class="store-title">
            <h1 class="store-name">{{ storeName }}</h1>
            <span class="store-slogan">{{ storeSlogan }}</span>
          </div>
        </div>
        <div class="header-actions">
          <div class="search-box">
            <el-input 
              placeholder="Search products..." 
              :suffix-icon="Search"
              class="search-input"
            />
          </div>
          <el-badge :value="3" class="action-badge">
            <el-icon :size="26" color="#fff" class="action-icon"><ShoppingCart /></el-icon>
          </el-badge>
          <el-icon :size="26" color="#fff" class="action-icon"><User /></el-icon>
        </div>
      </div>
    </header>

    <!-- Hero Banner -->
    <section class="hero-section" :style="{ background: bannerGradient }">
      <div class="hero-content">
        <h2 class="hero-title">{{ heroTitle }}</h2>
        <p class="hero-subtitle">{{ heroSubtitle }}</p>
        <el-button 
          :color="themeColor" 
          size="large" 
          round 
          class="cta-button"
          @click="scrollToProducts"
        >
          <el-icon><ShoppingBag /></el-icon>
          Shop Now
        </el-button>
      </div>
    </section>

    <!-- Category Tags -->
    <section class="category-section">
      <div class="category-container">
        <div 
          v-for="cat in categories" 
          :key="cat.id"
          class="category-item"
          :class="{ active: activeCategory === cat.id }"
          @click="activeCategory = cat.id"
        >
          <el-icon :size="20"><component :is="cat.icon" /></el-icon>
          <span>{{ cat.name }}</span>
        </div>
      </div>
    </section>

    <!-- Product Grid -->
    <main class="products-section" id="products">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon><StarFilled /></el-icon>
          Featured Products
        </h3>
        <el-tag :type="tenantId === '1001' ? 'primary' : 'danger'" effect="dark" round>
          {{ tenantId === '1001' ? 'Tech Store' : 'Beauty Store' }}
        </el-tag>
      </div>
      
      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="12" :md="8" :lg="6" v-for="i in 8" :key="i">
            <el-skeleton animated>
              <template #template>
                <el-skeleton-item variant="image" style="width: 100%; height: 200px; border-radius: 12px;" />
                <div style="padding: 14px 0;">
                  <el-skeleton-item variant="text" style="width: 50%; margin-bottom: 8px;" />
                  <el-skeleton-item variant="text" style="width: 30%;" />
                </div>
              </template>
            </el-skeleton>
          </el-col>
        </el-row>
      </div>
      
      <!-- Empty State -->
      <div v-else-if="products.length === 0" class="empty-container">
        <el-empty description="No products available at the moment" :image-size="200">
          <el-button type="primary" @click="fetchProducts">Refresh</el-button>
        </el-empty>
      </div>
      
      <!-- Product Grid -->
      <el-row v-else :gutter="20" class="product-row">
        <el-col 
          :xs="12" 
          :sm="12" 
          :md="8" 
          :lg="6" 
          v-for="product in products" 
          :key="product.id"
          class="product-col"
        >
          <el-card class="product-card" shadow="hover" @click="handleProductClick(product)">
            <div class="product-image-wrapper">
              <el-image
                :src="product.image || getDefaultImage(product.id)"
                :alt="product.name"
                fit="cover"
                lazy
                class="product-image"
              >
                <template #placeholder>
                  <div class="image-placeholder">
                    <el-icon :size="40" :color="themeColor"><Picture /></el-icon>
                  </div>
                </template>
                <template #error>
                  <div class="image-error">
                    <el-icon :size="40"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="product-badge" v-if="product.sort <= 3">HOT</div>
            </div>
            
            <div class="product-details">
              <h4 class="product-name" :title="product.name">{{ product.name }}</h4>
              <div class="product-tags">
                <el-tag size="small" :type="tenantId === '1001' ? 'primary' : 'danger'" effect="light">
                  {{ tenantId === '1001' ? '数码' : '美妆' }}
                </el-tag>
                <el-tag size="small" type="warning" effect="plain" v-if="product.sort <= 3">
                  热销
                </el-tag>
              </div>
              <div class="product-price-row">
                <span class="product-price">${{ (product.sort * 99 + 199) }}</span>
                <span class="product-original">${{ (product.sort * 99 + 399) }}</span>
              </div>
              <div class="product-actions">
                <el-button 
                  :color="themeColor" 
                  size="small" 
                  round
                  class="buy-button"
                >
                  <el-icon><ShoppingCart /></el-icon>
                  Add to Cart
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </main>

    <!-- Footer -->
    <footer class="store-footer">
      <div class="footer-content">
        <div class="footer-brand">
          <el-icon :size="24" :color="themeColor"><Shop /></el-icon>
          <span class="footer-name">{{ storeName }}</span>
        </div>
        <p class="footer-copyright">© 2024 {{ storeName }} - SaaS Multi-Tenant Demo</p>
        <p class="footer-domain">Domain: {{ currentDomain }} | Tenant: {{ tenantId }}</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { post } from '@/utils/request'
import {
  Shop, Search, ShoppingCart, User, ShoppingBag,
  StarFilled, Picture, Cellphone, Brush, Watch, Headset
} from '@element-plus/icons-vue'

// Theme configuration based on domain
const themeConfig = {
  shop1: {
    tenantId: '1001',
    storeName: '极客数码专营店',
    storeSlogan: 'Shop1 · 科技改变生活',
    themeColor: '#1E3A5F', // Deep tech blue
    accentColor: '#00D4FF', // Cyan accent
    heroTitle: '探索科技新境界',
    heroSubtitle: '精选全球顶尖数码装备，让科技更有温度',
    categories: [
      { id: 1, name: 'All', icon: 'StarFilled' },
      { id: 2, name: 'Phones', icon: 'Cellphone' },
      { id: 3, name: 'Audio', icon: 'Headset' },
      { id: 4, name: 'Wearables', icon: 'Watch' }
    ]
  },
  shop2: {
    tenantId: '1002',
    storeName: '星颜美妆甄选',
    storeSlogan: 'Shop2 · 美丽由此绽放',
    themeColor: '#D4237A', // Rose pink
    accentColor: '#FFB6C1', // Light pink
    heroTitle: '发现最美的自己',
    heroSubtitle: '甄选全球美妆好物，绽放独特魅力',
    categories: [
      { id: 1, name: 'All', icon: 'StarFilled' },
      { id: 2, name: 'Skincare', icon: 'Brush' },
      { id: 3, name: 'Makeup', icon: 'Brush' },
      { id: 4, name: 'Fragrance', icon: 'StarFilled' }
    ]
  }
}

// Reactive state
const currentDomain = ref('')
const tenantId = ref('1001')
const themeColor = ref('#1E3A5F')
const storeName = ref('极客数码专营店')
const storeSlogan = ref('Shop1 · 科技改变生活')
const heroTitle = ref('探索科技新境界')
const heroSubtitle = ref('精选全球顶尖数码装备，让科技更有温度')
const categories = ref(themeConfig.shop1.categories)
const activeCategory = ref(1)
const products = ref([])
const loading = ref(true)

// Computed
const bannerGradient = computed(() => {
  const accent = tenantId.value === '1001' ? '#00D4FF' : '#FFB6C1'
  return `linear-gradient(135deg, ${themeColor.value} 0%, ${accent} 100%)`
})

// Default images for different products
const getDefaultImage = (id) => {
  const images = tenantId.value === '1001' ? [
    'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&q=80',
    'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500&q=80',
    'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80',
    'https://images.unsplash.com/photo-1546868871-7041f3107145?w=500&q=80'
  ] : [
    'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=500&q=80',
    'https://images.unsplash.com/photo-1571781926291-c477ebfd024b?w=500&q=80',
    'https://images.unsplash.com/photo-1595225476474-87563907a212?w=500&q=80',
    'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=500&q=80'
  ]
  return images[(id - 1) % images.length]
}

// Resolve theme from domain
const resolveTheme = () => {
  const hostname = window.location.hostname.toLowerCase()
  currentDomain.value = hostname
  
  if (hostname.includes('shop1')) {
    return themeConfig.shop1
  } else if (hostname.includes('shop2')) {
    return themeConfig.shop2
  }
  return themeConfig.shop1
}

// Fetch products from backend
const fetchProducts = async () => {
  loading.value = true
  try {
    // Gateway will inject X-Tenant-Id based on Origin domain
    const res = await post('/goods/brand/search/1/10', {})
    console.log('API Response:', res)
    if (res && res.code === 20000 && res.data) {
      products.value = res.data.records || []
      ElMessage.success(`Loaded ${products.value.length} products`)
    } else {
      useMockData()
    }
  } catch (error) {
    console.error('API Error:', error)
    ElMessage.warning('Using demo data')
    useMockData()
  } finally {
    loading.value = false
  }
}

// Mock data fallback
const useMockData = () => {
  const isTech = tenantId.value === '1001'
  products.value = [
    { id: 1, name: isTech ? 'iPhone 15 Pro Max' : 'Estée Lauder Advanced Night Repair', sort: 1 },
    { id: 2, name: isTech ? 'MacBook Pro M3' : 'SK-II Facial Treatment Essence', sort: 2 },
    { id: 3, name: isTech ? 'AirPods Pro 2' : 'YSL Rouge Volupté Shine', sort: 3 },
    { id: 4, name: isTech ? 'Sony WH-1000XM5' : 'Dior Sauvage Eau de Toilette', sort: 4 },
    { id: 5, name: isTech ? 'iPad Pro 12.9' : 'Chanel Coco Mademoiselle', sort: 5 },
    { id: 6, name: isTech ? 'Apple Watch Ultra 2' : 'Lancôme Advanced Génifique', sort: 6 },
    { id: 7, name: isTech ? 'Canon EOS R6 Mark II' : 'Tom Ford Black Orchid', sort: 7 },
    { id: 8, name: isTech ? 'DJI Mavic 3 Pro' : 'La Mer Crème de la Mer', sort: 8 }
  ]
}

// Handle product click
const handleProductClick = (product) => {
  ElMessage.info(`Viewing: ${product.name}`)
}

// Scroll to products section
const scrollToProducts = () => {
  document.getElementById('products')?.scrollIntoView({ behavior: 'smooth' })
}

// Initialize
onMounted(() => {
  const theme = resolveTheme()
  
  tenantId.value = theme.tenantId
  themeColor.value = theme.themeColor
  storeName.value = theme.storeName
  storeSlogan.value = theme.storeSlogan
  heroTitle.value = theme.heroTitle
  heroSubtitle.value = theme.heroSubtitle
  categories.value = theme.categories
  
  // Apply Element Plus primary color
  document.documentElement.style.setProperty('--el-color-primary', theme.themeColor)
  
  console.log(`[Store] Domain: ${currentDomain.value}`)
  console.log(`[Store] Tenant: ${theme.tenantId}`)
  
  fetchProducts()
})
</script>

<style scoped>
.mall-store {
  min-height: 100vh;
  background: #f8f9fa;
}

/* Header Styles */
.store-header {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 0 24px;
  height: 70px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.store-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.store-title {
  display: flex;
  flex-direction: column;
}

.store-name {
  color: #fff;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 0.5px;
}

.store-slogan {
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  margin-top: 2px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 24px;
}

.search-box {
  width: 300px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.search-input :deep(.el-input__inner) {
  color: #fff;
}

.search-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.7);
}

.action-icon {
  cursor: pointer;
  transition: transform 0.3s, opacity 0.3s;
}

.action-icon:hover {
  transform: scale(1.1);
  opacity: 0.8;
}

/* Hero Section */
.hero-section {
  padding: 80px 24px;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  color: #fff;
  font-size: 48px;
  font-weight: 800;
  margin-bottom: 16px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.hero-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 18px;
  margin-bottom: 32px;
}

.cta-button {
  font-weight: 600;
  padding: 14px 40px;
  font-size: 16px;
}

.cta-button :deep(.el-icon) {
  margin-right: 8px;
}

/* Category Section */
.category-section {
  padding: 24px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  position: sticky;
  top: 70px;
  z-index: 99;
}

.category-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 16px;
  overflow-x: auto;
  scrollbar-width: none;
}

.category-container::-webkit-scrollbar {
  display: none;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  color: #606266;
  background: #f4f4f5;
}

.category-item:hover {
  background: #e9e9eb;
}

.category-item.active {
  background: v-bind(themeColor);
  color: #fff;
}

/* Products Section */
.products-section {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
}

.loading-container {
  margin: 40px 0;
}

.empty-container {
  padding: 80px 0;
  text-align: center;
}

/* Product Grid */
.product-row {
  margin: 0 !important;
}

.product-col {
  margin-bottom: 24px;
}

.product-card {
  border-radius: 16px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  border: none;
  background: #fff;
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15) !important;
}

.product-card :deep(.el-card__body) {
  padding: 0;
}

.product-image-wrapper {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.image-placeholder,
.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.product-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: #ff4757;
  color: #fff;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.product-details {
  padding: 16px;
}

.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 42px;
}

.product-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 16px;
}

.product-price {
  font-size: 20px;
  font-weight: 700;
  color: #ff4757;
}

.product-original {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
}

.product-actions {
  display: flex;
  justify-content: center;
}

.buy-button {
  width: 100%;
  font-weight: 600;
}

/* Footer */
.store-footer {
  background: #1a1a1a;
  color: #fff;
  padding: 40px 24px;
  text-align: center;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
}

.footer-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 16px;
}

.footer-name {
  font-size: 20px;
  font-weight: 600;
}

.footer-copyright {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
  margin-bottom: 8px;
}

.footer-domain {
  color: rgba(255, 255, 255, 0.4);
  font-size: 12px;
}

/* Responsive */
@media (max-width: 768px) {
  .store-header {
    padding: 0 16px;
    height: 60px;
  }
  
  .store-name {
    font-size: 18px;
  }
  
  .store-slogan {
    display: none;
  }
  
  .search-box {
    display: none;
  }
  
  .hero-section {
    padding: 40px 16px;
  }
  
  .hero-title {
    font-size: 28px;
  }
  
  .hero-subtitle {
    font-size: 14px;
  }
  
  .category-section {
    padding: 16px;
    top: 60px;
  }
  
  .category-item {
    padding: 8px 16px;
    font-size: 13px;
  }
  
  .products-section {
    padding: 24px 16px;
  }
  
  .section-title {
    font-size: 20px;
  }
  
  .product-image-wrapper {
    height: 160px;
  }
  
  .product-price {
    font-size: 18px;
  }
}
</style>
