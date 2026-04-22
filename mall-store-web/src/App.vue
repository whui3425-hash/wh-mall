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
          <!-- 我的订单图标 -->
          <el-icon :size="26" color="#fff" class="action-icon" @click="handleOrderClick" title="My orders">
            <Document />
          </el-icon>

          <el-badge :value="cartCount" :hidden="cartCount === 0" class="action-badge">
            <el-icon :size="26" color="#fff" class="action-icon" @click="handleCartClick"><ShoppingCart /></el-icon>
          </el-badge>

          <!-- 用户登录状态区域 -->
          <div v-if="isLoggedIn" class="user-info">
            <el-dropdown @command="handleUserCommand">
              <span class="user-name" :style="{ color: '#fff' }">
                {{ currentUsername }}
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">Sign out</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <el-icon 
            v-else 
            :size="26" 
            color="#fff" 
            class="action-icon"
            @click="showLoginDialog = true"
          >
            <User />
          </el-icon>
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
                  {{ tenantId === '1001' ? 'Tech' : 'Beauty' }}
                </el-tag>
                <el-tag size="small" type="warning" effect="plain" v-if="product.sort <= 3">
                  Bestseller
                </el-tag>
              </div>
              <div class="product-price-row">
                <span class="product-price">${{ ((product.price || 0) / 100).toFixed(2) }}</span>
                <span class="product-original">${{ ((product.price || 0) / 100 * 1.2).toFixed(2) }}</span>
              </div>
              <div class="product-actions">
                <el-button 
                  :color="themeColor" 
                  size="small" 
                  round
                  class="buy-button"
                  @click.stop="handleAddToCart(product)"
                  :loading="product.addingToCart"
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

    <!-- ================== 买家登录弹窗 ================== -->
    <el-dialog
      v-model="showLoginDialog"
      title="Sign in"
      width="400px"
      :close-on-click-modal="false"
      center
    >
      <el-form 
        :model="loginForm" 
        :rules="loginRules"
        ref="loginFormRef"
        label-position="top"
      >
        <el-form-item label="Username" prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="Username"
            prefix-icon="User"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="Password"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item label="Captcha" prop="captcha">
          <el-input
            v-model="loginForm.captcha"
            placeholder="请输入验证码（压测可用 PERF-TEST）"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
      </el-form>
      
      <div class="login-tips">
        <p>Test accounts:</p>
        <p>Tenant 1001: zhangsan / 123456, lisi / 123456</p>
        <p>Tenant 1002: wangwu / 123456, zhaoliu / 123456</p>
        <p>Captcha: PERF-TEST (permanent, stress-test purpose)</p>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showLoginDialog = false">Cancel</el-button>
          <el-button 
            type="primary" 
            @click="handleLogin"
            :loading="loginLoading"
            :color="themeColor"
          >
            Sign in
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- ================== 收银台弹窗 ================== -->
    <el-dialog
      v-model="showPayDialog"
      title="Checkout"
      width="480px"
      :close-on-click-modal="false"
      :show-close="true"
      center
      class="pay-dialog"
    >
      <div class="pay-content">
        <!-- 订单信息展示 -->
        <div class="order-info">
          <div class="order-label">Order number</div>
          <div class="order-value order-no">{{ orderResult.outTradeNo }}</div>
        </div>

        <div class="order-info">
          <div class="order-label">Items</div>
          <div class="order-value">{{ orderResult.totalNum }} item(s)</div>
        </div>

        <el-divider />

        <!-- 支付金额 -->
        <div class="pay-amount-section">
          <div class="pay-label">Amount due</div>
          <div class="pay-amount">${{ (orderResult.totalAmount / 100).toFixed(2) }}</div>
        </div>

        <!-- 支付方式（模拟） -->
        <div class="pay-methods">
          <div class="pay-method-title">Payment method</div>
          <div class="pay-method-options">
            <div class="pay-method-option active">
              <el-icon :size="24"><Wallet /></el-icon>
              <span>WeChat Pay</span>
            </div>
            <div class="pay-method-option">
              <el-icon :size="24"><CreditCard /></el-icon>
              <span>Alipay</span>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="pay-footer">
          <el-button @click="handleCancelPay" size="large">
            Pay later
          </el-button>
          <el-button
            type="primary"
            size="large"
            :color="themeColor"
            @click="handleConfirmPay"
            class="confirm-pay-btn"
          >
            <el-icon><WalletFilled /></el-icon>
            Confirm payment
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- ================== 购物车抽屉 ================== -->
    <el-drawer
      v-model="showCartDrawer"
      title="Shopping cart"
      direction="rtl"
      size="450px"
      :with-header="true"
      class="cart-drawer"
    >
      <!-- 购物车列表 -->
      <div class="cart-content" v-if="cartItems.length > 0">
        <div class="cart-items">
          <div
            v-for="item in cartItems"
            :key="item.id"
            class="cart-item"
          >
            <!-- 商品图片 -->
            <div class="cart-item-image">
              <el-image
                :src="item.image || getDefaultImage(parseInt(item.skuId))"
                fit="cover"
                class="cart-img"
              >
                <template #error>
                  <div class="cart-img-placeholder">
                    <el-icon :size="30"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>

            <!-- 商品信息 -->
            <div class="cart-item-info">
              <h4 class="cart-item-name">{{ item.name }}</h4>
              <p class="cart-item-price">${{ (item.price / 100).toFixed(2) }}</p>
            </div>

            <!-- 数量控制 -->
            <div class="cart-item-actions">
              <el-input-number
                v-model="item.num"
                :min="1"
                :max="99"
                size="small"
                controls-position="right"
                @change="(val) => handleUpdateCartItem(item, val)"
              />
              <el-button
                type="danger"
                link
                size="small"
                @click="handleDeleteCartItem(item)"
                class="delete-btn"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 底部结算区域 -->
        <div class="cart-footer">
          <div class="cart-total">
            <span class="total-label">Total Amount:</span>
            <span class="total-price">${{ calculateTotal.toFixed(2) }}</span>
          </div>
          <el-button
            type="primary"
            size="large"
            :color="themeColor"
            class="checkout-btn"
            @click="handleCheckout"
            :loading="submitting"
            :disabled="cartItems.length === 0"
          >
            {{ submitting ? 'Placing order...' : `Checkout (${cartItems.length} items)` }}
          </el-button>
        </div>
      </div>

      <!-- 空购物车状态 -->
      <div class="cart-empty" v-else>
        <el-icon :size="60" :color="'#dcdfe6'"><ShoppingCart /></el-icon>
        <p class="empty-text">Your cart is empty</p>
        <el-button
          :color="themeColor"
          @click="showCartDrawer = false"
          class="continue-shopping-btn"
        >
          Continue shopping
        </el-button>
      </div>
    </el-drawer>

    <!-- ================== 订单列表抽屉 ================== -->
    <el-drawer
      v-model="showOrderDrawer"
      title="My orders"
      direction="rtl"
      size="450px"
      :with-header="true"
      class="order-drawer"
    >
      <!-- 加载状态 -->
      <div v-if="orderLoading" class="order-loading">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 订单列表 -->
      <div v-else-if="orderList.length > 0" class="order-content">
        <el-timeline>
          <el-timeline-item
            v-for="order in orderList"
            :key="order.orderId"
            :timestamp="formatDate(order.createTime)"
            placement="top"
          >
            <el-card shadow="hover" class="order-card">
              <!-- 订单头部：订单号 + 支付状态 -->
              <div class="order-header">
                <div class="order-no-section">
                  <el-icon :size="14"><Document /></el-icon>
                  <span class="order-no">{{ order.outTradeNo || order.orderId }}</span>
                </div>
                <el-tag
                  :type="getPayStatusTagType(order.payStatus)"
                  effect="dark"
                  size="small"
                  class="pay-status-tag"
                >
                  {{ getPayStatusText(order.payStatus) }}
                </el-tag>
              </div>

              <!-- 订单商品列表 -->
              <div class="order-items">
                <div
                  v-for="(item, index) in order.items"
                  :key="index"
                  class="order-item"
                >
                  <el-image
                    :src="item.image"
                    fit="cover"
                    class="order-item-img"
                  >
                    <template #error>
                      <div class="order-item-img-placeholder">
                        <el-icon :size="20"><Picture /></el-icon>
                      </div>
                    </template>
                  </el-image>
                  <div class="order-item-info">
                    <p class="order-item-name" :title="item.name">{{ item.name }}</p>
                    <p class="order-item-spec">x{{ item.num }}</p>
                  </div>
                </div>
              </div>

              <!-- 订单底部：金额 + 时间 -->
              <div class="order-footer">
                <div class="order-time">
                  <el-icon :size="12"><Clock /></el-icon>
                  <span>{{ formatDate(order.createTime) }}</span>
                </div>
                <div class="order-amount">
                  <span class="amount-label">{{ order.totalNum }} item(s)</span>
                  <span class="amount-value">${{ formatAmount(order.totalAmount) }}</span>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <!-- 空订单状态 -->
      <div v-else class="order-empty">
        <el-icon :size="60" :color="'#dcdfe6'"><List /></el-icon>
        <p class="empty-text">No orders yet</p>
        <p class="empty-subtext">Browse the store and place your first order.</p>
        <el-button
          :color="themeColor"
          @click="showOrderDrawer = false"
          class="continue-shopping-btn"
        >
          Shop now
        </el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { post, get, del, put } from '@/utils/request'
import {
  Shop, Search, ShoppingCart, User, ShoppingBag,
  StarFilled, Picture, Cellphone, Brush, Watch, Headset,
  ArrowDown, Lock, Delete, Wallet, CreditCard, WalletFilled,
  Document, List, Check, Clock
} from '@element-plus/icons-vue'

// Theme configuration based on domain
const themeConfig = {
  shop1: {
    tenantId: '1001',
    storeName: 'Geek Tech Store',
    storeSlogan: 'Shop1 · Technology that changes life',
    themeColor: '#1E3A5F', // Deep tech blue
    accentColor: '#00D4FF', // Cyan accent
    heroTitle: 'Explore New Tech Horizons',
    heroSubtitle: 'Curated world-class digital gear—technology with a human touch',
    categories: [
      { id: 1, name: 'All', icon: 'StarFilled' },
      { id: 2, name: 'Phones', icon: 'Cellphone' },
      { id: 3, name: 'Audio', icon: 'Headset' },
      { id: 4, name: 'Wearables', icon: 'Watch' }
    ]
  },
  shop2: {
    tenantId: '1002',
    storeName: 'StarGlow Beauty',
    storeSlogan: 'Shop2 · Where beauty blooms',
    themeColor: '#D4237A', // Rose pink
    accentColor: '#FFB6C1', // Light pink
    heroTitle: 'Discover Your Best Self',
    heroSubtitle: 'Curated global beauty favorites—unleash your unique glow',
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
const storeName = ref('Geek Tech Store')
const storeSlogan = ref('Shop1 · Technology that changes life')
const heroTitle = ref('Explore New Tech Horizons')
const heroSubtitle = ref('Curated world-class digital gear—technology with a human touch')
const categories = ref(themeConfig.shop1.categories)
const activeCategory = ref(1)
const products = ref([])
const loading = ref(true)

// ================== 购物车相关状态 ==================
const cartCount = ref(0)                    // 购物车商品种类数量（Badge显示）
const cartItems = ref([])                   // 购物车数据列表
const showCartDrawer = ref(false)           // 控制购物车抽屉显示
const cartLoading = ref(false)                // 购物车加载状态

// ================== 订单提交相关状态 ==================
const submitting = ref(false)               // 订单提交loading状态
const showPayDialog = ref(false)            // 收银台弹窗显示
const orderResult = ref({                   // 订单提交结果
  orderId: '',
  outTradeNo: '',
  totalAmount: 0,
  totalNum: 0
})

// ================== 订单列表相关状态 ==================
const showOrderDrawer = ref(false)          // 订单抽屉显示
const orderList = ref([])                   // 订单列表数据
const orderLoading = ref(false)             // 订单加载状态

// ================== 登录相关状态 ==================
const showLoginDialog = ref(false)          // 控制登录弹窗显示
const loginLoading = ref(false)             // 登录按钮加载状态
const isLoggedIn = ref(false)               // 是否已登录
const currentUsername = ref('')             // 当前登录用户名
const loginFormRef = ref(null)              // 表单引用

// 登录表单数据
const loginForm = ref({
  username: '',
  password: '',
  captcha: ''
})

// 登录表单校验规则
const loginRules = {
  username: [
    { required: true, message: 'Username is required', trigger: 'blur' }
  ],
  password: [
    { required: true, message: 'Password is required', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: 'Captcha is required', trigger: 'blur' }
  ]
}

// Computed
const bannerGradient = computed(() => {
  if (tenantId.value === '1001') {
    // Hero 横幅：深紫 → 亮紫（与顶栏深蓝主题区分，仅横幅区域）
    return 'linear-gradient(135deg, #4c1d95 0%, #6d28d9 40%, #a78bfa 100%)'
  }
  return `linear-gradient(135deg, ${themeColor.value} 0%, #FFB6C1 100%)`
})

/**
 * 计算购物车总金额
 */
const calculateTotal = computed(() => {
  return cartItems.value.reduce((total, item) => {
    return total + (item.price * item.num / 100)
  }, 0)
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

// 商品服务 /api/sku/aditems/type 直接返回 List<Sku>（JSON 数组），无 { code, data } 封装
const normalizeSkuList = (res) => {
  if (!res) return null
  if (Array.isArray(res)) return res
  if (res.code === 20000 && Array.isArray(res.data)) return res.data
  if (Array.isArray(res.data)) return res.data
  return null
}

// Fetch products from backend - 调用SKU广告列表接口
const fetchProducts = async () => {
  loading.value = true
  try {
    // 【关键】调用 SKU 广告列表接口获取真实SKU数据
    // 类型1是首页轮播/推荐商品
    const res = await get('/sku/aditems/type?id=1')
    console.log('API Response:', res)
    const list = normalizeSkuList(res)
    if (list && list.length > 0) {
      // 为每个SKU添加 addingToCart 属性（用于按钮loading状态）
      products.value = list.map((sku) => ({
        id: sku.id,           // SKU ID (如 SKU001)
        name: sku.name,       // SKU 名称
        price: sku.price,     // 价格（分）
        image: sku.image,     // 图片（与数据库 sku.image 一致）
        sort: sku.sort || 1,  // 排序
        addingToCart: false
      }))
      ElMessage.success(`Loaded ${products.value.length} products`)
    } else {
      console.warn('API returned empty data, using mock data')
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

// Mock data fallback - 使用正确的SKU ID（与数据库匹配）
const useMockData = () => {
  const isTech = tenantId.value === '1001'
  if (isTech) {
    // 租户1001 - 数码产品（对应数据库SKU）- 价格以"分"为单位
    products.value = [
      { id: 'SKU001', name: 'iPhone 15 Pro Max - Natural Titanium 256GB', sort: 1, price: 999900, image: '/images/goods/sku-iphone-256.jpg', addingToCart: false },
      { id: 'SKU002', name: 'iPhone 15 Pro Max - Blue Titanium 512GB', sort: 2, price: 1199900, image: '/images/goods/sku-iphone-512.jpg', addingToCart: false },
      { id: 'SKU003', name: 'iPhone 15 Pro Max - Black Titanium 1TB', sort: 3, price: 1399900, image: '/images/goods/pic1.jpg', addingToCart: false },
      { id: 'SKU004', name: 'MacBook Pro 16 - Space Black 36GB', sort: 4, price: 2499900, image: '/images/goods/spu002-1.jpg', addingToCart: false },
      { id: 'SKU005', name: 'MacBook Pro 16 - Silver 48GB', sort: 5, price: 2999900, image: '/images/goods/spu002-2.jpg', addingToCart: false },
      { id: 'SKU006', name: 'Sony WH-1000XM5 - Black', sort: 6, price: 349900, image: '/images/goods/spu003-1.jpg', addingToCart: false },
      { id: 'SKU007', name: 'Sony WH-1000XM5 - Silver', sort: 7, price: 349900, image: '/images/goods/spu003-2.jpg', addingToCart: false },
      { id: 'SKU008', name: 'MX Mechanical - Tactile Full Size', sort: 8, price: 129900, image: '/images/goods/spu004-1.jpg', addingToCart: false }
    ]
  } else {
    // 租户1002 - 美妆/时尚产品（对应数据库SKU）- 价格以"分"为单位
    products.value = [
      { id: 'SKU010', name: 'Advanced Night Repair 50ml', sort: 1, price: 85000, image: '/images/goods/spu005-1.jpg', addingToCart: false },
      { id: 'SKU011', name: 'Advanced Night Repair 100ml', sort: 2, price: 120000, image: '/images/goods/spu005-2.jpg', addingToCart: false },
      { id: 'SKU012', name: 'Air Jordan 1 Retro - Chicago US 9', sort: 3, price: 159900, image: '/images/goods/spu006-1.jpg', addingToCart: false },
      { id: 'SKU013', name: 'Air Jordan 1 Retro - Bred US 10', sort: 4, price: 169900, image: '/images/goods/spu006-2.jpg', addingToCart: false },
      { id: 'SKU014', name: 'Ultraboost 23 - Core Black US 10', sort: 5, price: 139900, image: '/images/goods/spu007-1.jpg', addingToCart: false },
      { id: 'SKU015', name: 'Ultraboost 23 - White US 9', sort: 6, price: 139900, image: '/images/goods/spu007-2.jpg', addingToCart: false },
      { id: 'SKU016', name: 'Oversized Hoodie - Black M', sort: 7, price: 69900, image: '/images/goods/spu008-1.jpg', addingToCart: false },
      { id: 'SKU017', name: 'Oversized Hoodie - Grey L', sort: 8, price: 69900, image: '/images/goods/spu008-2.jpg', addingToCart: false }
    ]
  }
}

// Handle product click
const handleProductClick = (product) => {
  ElMessage.info(`Viewing: ${product.name}`)
}

// Scroll to products section
const scrollToProducts = () => {
  document.getElementById('products')?.scrollIntoView({ behavior: 'smooth' })
}

// ================== 登录相关方法 ==================

/**
 * 【修复1】页面初始化状态唤醒 - 从 localStorage 恢复登录状态
 * 页面刷新后 Vue 状态丢失，这里主动恢复登录态
 */
const checkLoginStatus = () => {
  // 【强物理校验】直接从 localStorage 读取，不依赖内存变量
  const buyerToken = localStorage.getItem('buyer_token')
  const savedUsername = localStorage.getItem('buyer_username')
  const savedUserId = localStorage.getItem('buyer_userId')

  // 【调试】输出 localStorage 内容到控制台
  console.log('[Auth Debug] ===== 登录状态检查 =====')
  console.log('[Auth Debug] buyer_token:', buyerToken ? buyerToken.substring(0, 20) + '...' : 'null/undefined')
  console.log('[Auth Debug] buyer_username:', savedUsername)
  console.log('[Auth Debug] buyer_userId:', savedUserId)
  console.log('[Auth Debug] localStorage 所有键:', Object.keys(localStorage))
  console.log('[Auth Debug] ============================')

  if (buyerToken && buyerToken !== 'undefined' && buyerToken !== 'null') {
    // 【核心】已登录：恢复前端状态，确保页面显示"已登录"
    isLoggedIn.value = true
    currentUsername.value = savedUsername || 'Customer'
    console.log('[Auth] 状态恢复成功 - 用户:', savedUsername)
    
    // 确保关闭登录弹窗（防止刷新后弹窗还开着）
    showLoginDialog.value = false
    return true
  } else {
    // 未登录：弹出登录弹窗
    console.log('[Auth] 未检测到有效 Token，需要登录')
    isLoggedIn.value = false
    currentUsername.value = ''
    showLoginDialog.value = true
    return false
  }
}

/**
 * 处理买家登录 - 增强调试版本
 */
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  // 表单校验
  await loginFormRef.value.validate((valid) => {
    if (!valid) return
  })
  
  loginLoading.value = true
  
  try {
    // 调用 C 端登录接口
    const res = await post('/user/login', {
      username: loginForm.value.username,
      password: loginForm.value.password,
      captcha: loginForm.value.captcha
    })
    
    console.log('[Login] 登录响应:', res)
    
    if (res.code === 20000 && res.data) {
      // 【核心】存储买家 Token 到 localStorage - 增强检查
      const token = res.data.token
      console.log('[Login] 获取到的token:', token ? token.substring(0, 20) + '...' : 'undefined/null')
      
      if (!token) {
        ElMessage.error('Sign-in failed: server did not return a token')
        console.error('[Login] 响应数据:', res.data)
        return
      }
      
      localStorage.setItem('buyer_token', token)
      localStorage.setItem('buyer_username', res.data.username || '')
      localStorage.setItem('buyer_userId', res.data.userId || '')
      localStorage.setItem('buyer_tenantId', res.data.tenantId || '')
      
      // 立即验证存储是否成功
      const storedToken = localStorage.getItem('buyer_token')
      console.log('[Login] 存储验证 - token已存储:', !!storedToken)
      
      // 更新登录状态
      isLoggedIn.value = true
      currentUsername.value = res.data.username || 'Customer'
      
      // 关闭弹窗并提示
      showLoginDialog.value = false
      ElMessage.success(`Welcome, ${res.data.name || res.data.username}`)
      
      // 清空表单
      loginForm.value = { username: '', password: '', captcha: '' }
      
      console.log('[Login] 登录成功，状态:', {
        userId: res.data.userId,
        username: res.data.username,
        hasToken: !!storedToken
      })
      
      // 登录成功后刷新购物车数量
      loadCartCount()
    } else {
      ElMessage.error(res.message || 'Sign-in failed. Check your username and password.')
    }
  } catch (error) {
    console.error('[Login] 登录失败:', error)
    ElMessage.error(error.message || 'Network error. Is the API gateway running?')
  } finally {
    loginLoading.value = false
  }
}

/**
 * 处理用户下拉菜单命令
 */
const handleUserCommand = (command) => {
  if (command === 'logout') {
    // 清除买家登录态
    localStorage.removeItem('buyer_token')
    localStorage.removeItem('buyer_username')
    localStorage.removeItem('buyer_userId')
    localStorage.removeItem('buyer_tenantId')
    
    // 重置状态
    isLoggedIn.value = false
    currentUsername.value = ''
    cartCount.value = 0  // 清空购物车数量
    cartItems.value = []
    
    ElMessage.success('Signed out')
    
    // 退出后弹出登录框（因为购物车需要登录）
    showLoginDialog.value = true
  }
}

// ================== 订单列表相关方法 ==================

/**
 * 【核心】点击我的订单图标 - 打开订单抽屉
 */
const handleOrderClick = async () => {
  const buyerToken = localStorage.getItem('buyer_token')
  if (!buyerToken) {
    ElMessage.info('Please sign in to view orders')
    showLoginDialog.value = true
    return
  }

  // 打开订单抽屉
  showOrderDrawer.value = true

  // 加载订单列表
  await loadOrderList()
}

/**
 * 【核心】加载买家订单列表
 * 调用 /api/order/list 接口，根据 JWT Header 自动获取用户ID和租户ID
 */
const loadOrderList = async () => {
  orderLoading.value = true
  try {
    const res = await get('/order/list')
    console.log('[OrderList] API响应:', res)

    if (res.code === 20000 && res.data) {
      orderList.value = res.data
      console.log('[OrderList] 加载订单数量:', orderList.value.length)
    } else {
      orderList.value = []
      console.warn('[OrderList] 获取订单列表失败:', res.message)
    }
  } catch (error) {
    console.error('[OrderList] 加载订单列表失败:', error)
    ElMessage.error('Failed to load orders')
    orderList.value = []
  } finally {
    orderLoading.value = false
  }
}

/**
 * 获取支付状态标签类型（用于 El-Tag）
 * @param payStatus 0-未支付, 1-已支付
 */
const getPayStatusTagType = (payStatus) => {
  return payStatus === 1 ? 'success' : 'danger'
}

/**
 * 获取支付状态文本
 * @param payStatus 0-未支付, 1-已支付
 */
const getPayStatusText = (payStatus) => {
  return payStatus === 1 ? 'Paid' : 'Unpaid'
}

/**
 * 格式化金额（分转元）
 * @param amount 金额（分）
 */
const formatAmount = (amount) => {
  return (amount / 100).toFixed(2)
}

/**
 * 格式化时间
 * @param dateStr 日期字符串
 */
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('en-US', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// ================== 购物车相关方法 ==================

/**
 * 加载购物车数量（增强调试版）
 */
const loadCartCount = async () => {
  const buyerToken = localStorage.getItem('buyer_token')
  console.log('[CartCount Debug] 开始加载购物车数量, token存在:', !!buyerToken)
  
  if (!buyerToken || buyerToken === 'undefined' || buyerToken === 'null') {
    console.log('[CartCount Debug] 无有效token，跳过加载')
    cartCount.value = 0
    return
  }
  
  try {
    const res = await get('/cart/list')
    console.log('[CartCount Debug] 购物车API响应:', res)
    
    if (res.code === 20000 && res.data) {
      cartCount.value = res.data.length  // 商品种类数量
      console.log('[CartCount] 购物车数量:', cartCount.value)
    } else {
      console.warn('[CartCount] 获取购物车失败:', res.message)
      cartCount.value = 0
    }
  } catch (error) {
    console.error('[CartCount] 获取购物车失败:', error)
    cartCount.value = 0
  }
}

/**
 * 【修复2】添加商品到购物车 - 强物理校验，不依赖内存变量
 * 每次点击都直接读取 localStorage，确保刷新后状态不丢失
 * @param product 商品对象
 */
const handleAddToCart = async (product) => {
  // 【强物理校验】直接读取 localStorage，不依赖 isLoggedIn 内存变量
  const buyerToken = localStorage.getItem('buyer_token')
  
  // 【调试】详细输出加购时的检测过程
  console.log('[Cart Debug] ===== 加购校验 =====')
  console.log('[Cart Debug] buyer_token:', buyerToken)
  console.log('[Cart Debug] token类型:', typeof buyerToken)
  console.log('[Cart Debug] token是否为空:', buyerToken === '')
  console.log('[Cart Debug] token是否null:', buyerToken === null)
  console.log('[Cart Debug] token是否undefined:', buyerToken === undefined)
  console.log('[Cart Debug] isLoggedIn.value:', isLoggedIn.value)
  console.log('[Cart Debug] =====================')
  
  if (!buyerToken || buyerToken === 'undefined' || buyerToken === 'null' || buyerToken === '') {
    ElMessage.warning('Please sign in to add items to your cart')
    console.log('[Cart] 检测到未登录，弹出登录框')
    showLoginDialog.value = true
    return
  }
  
  console.log('[Cart] 已登录，继续加购流程')
  
  // 设置加载状态
  product.addingToCart = true
  
  try {
    // 2. 调用添加购物车 API
    const res = await post('/cart/add', {
      skuId: product.id,
      num: 1
    })
    
    if (res.code === 20000) {
      // 3. 成功提示
      ElMessage.success('Added to cart')
      
      // 4. 刷新购物车数量
      await loadCartCount()
    } else {
      ElMessage.error(res.message || 'Could not add to cart')
    }
  } catch (error) {
    console.error('[Cart] 添加购物车失败:', error)
    
    // 处理 401 未授权（Token过期）
    if (error.response?.status === 401 || error.message?.includes('Unauthorized')) {
      ElMessage.error('Session expired. Please sign in again.')
      localStorage.removeItem('buyer_token')
      showLoginDialog.value = true
    } else {
      ElMessage.error('Could not add to cart. Check your network.')
    }
  } finally {
    product.addingToCart = false
  }
}

/**
 * 【核心】点击购物车图标 - 打开购物车抽屉
 */
const handleCartClick = async () => {
  const buyerToken = localStorage.getItem('buyer_token')
  if (!buyerToken) {
    ElMessage.info('Please sign in to view your cart')
    showLoginDialog.value = true
    return
  }
  
  // 打开购物车抽屉
  showCartDrawer.value = true
  
  // 刷新购物车数据
  await loadCartItems()
}

/**
 * 【核心】加载购物车详细数据
 */
const loadCartItems = async () => {
  cartLoading.value = true
  try {
    const res = await get('/cart/list')
    if (res.code === 20000 && res.data) {
      cartItems.value = res.data
      cartCount.value = res.data.length
      console.log('[Cart] 购物车数据:', cartItems.value)
    } else {
      cartItems.value = []
      cartCount.value = 0
    }
  } catch (error) {
    console.error('[Cart] 加载购物车失败:', error)
    ElMessage.error('Failed to load cart')
    cartItems.value = []
    cartCount.value = 0
  } finally {
    cartLoading.value = false
  }
}

/**
 * 【核心】更新购物车商品数量
 * @param item 购物车项
 * @param newNum 新数量
 */
const handleUpdateCartItem = async (item, newNum) => {
  if (!newNum || newNum < 1) return
  
  try {
    const res = await put('/cart/update', {
      id: item.id,
      num: newNum
    })
    
    if (res.code === 20000) {
      ElMessage.success('Quantity updated')
      item.num = newNum
    } else {
      ElMessage.error(res.message || 'Update failed')
      // 恢复原数量（重新加载）
      await loadCartItems()
    }
  } catch (error) {
    console.error('[Cart] 更新数量失败:', error)
    ElMessage.error('Failed to update quantity')
    await loadCartItems()
  }
}

/**
 * 【核心】删除购物车商品
 * @param item 购物车项
 */
const handleDeleteCartItem = async (item) => {
  try {
    // 确认删除
    await ElMessageBox.confirm(
      `Remove "${item.name}" from your cart?`,
      'Remove item',
      {
        confirmButtonText: 'Remove',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )
    
    const res = await del(`/cart/${item.id}`)
    
    if (res.code === 20000) {
      ElMessage.success('Removed')
      // 从列表中移除
      cartItems.value = cartItems.value.filter(i => i.id !== item.id)
      cartCount.value = cartItems.value.length
    } else {
      ElMessage.error(res.message || 'Remove failed')
    }
  } catch (error) {
    if (error === 'cancel') return
    console.error('[Cart] 删除失败:', error)
    ElMessage.error('Remove failed')
  }
}

/**
 * 【核心】提交订单 - 调用后端 /api/order/submit
 */
const handleCheckout = async () => {
  if (cartItems.value.length === 0) {
    ElMessage.warning('Your cart is empty')
    return
  }

  // 检查登录状态
  const buyerToken = localStorage.getItem('buyer_token')
  if (!buyerToken) {
    ElMessage.warning('Please sign in')
    showLoginDialog.value = true
    return
  }

  // 防止重复提交
  if (submitting.value) {
    return
  }

  submitting.value = true

  try {
    // 【关键】提取购物车商品ID列表
    // 如果将来加了勾选框，这里可以改为只提交被选中的商品
    const cartItemIds = cartItems.value.map(item => item.id)

    console.log('[Order] 提交订单，商品ID列表:', cartItemIds)

    // 【核心】调用订单提交接口
    const res = await post('/order/submit', {
      cartItemIds: cartItemIds
    })

    console.log('[Order] 订单提交响应:', res)

    if (res.code === 20000 && res.data) {
      // ========== 订单创建成功 ==========
      const result = res.data

      // 保存订单结果
      orderResult.value = {
        orderId: result.orderId || '',
        outTradeNo: result.outTradeNo || '',
        totalAmount: result.totalAmount || 0,
        totalNum: result.totalNum || 0
      }

      console.log('[Order] 订单创建成功:', orderResult.value)

      // 1. 关闭购物车抽屉
      showCartDrawer.value = false

      // 2. 显示成功消息
      ElMessage.success('Order placed. Proceed to payment.')

      // 3. 弹出收银台弹窗
      showPayDialog.value = true

      // 4. 清空购物车数据（因为已购买的商品已从数据库删除）
      cartItems.value = []
      cartCount.value = 0

    } else {
      // 业务逻辑错误（如库存不足等）
      console.error('[Order] 订单提交失败:', res.message)
      ElMessage.error(res.message || 'Checkout failed')
    }

  } catch (error) {
    console.error('[Order] 订单提交异常:', error)

    // 处理不同类型的错误
    if (error.response?.status === 401) {
      ElMessage.error('Session expired. Please sign in again.')
      localStorage.removeItem('buyer_token')
      showLoginDialog.value = true
    } else if (error.message?.includes('Network Error')) {
      ElMessage.error('Network error. Is the API gateway running?')
    } else {
      ElMessage.error(error.message || 'Checkout failed. Please try again.')
    }

  } finally {
    submitting.value = false
  }
}

/**
 * 【收银台】处理确认支付【压测版】
 * 
 * 【功能说明】模拟第三方支付网关网络抽风，瞬间并发发送 5 个相同的回调通知
 * 用于测试后端分布式锁防重逻辑
 */
const handleConfirmPay = async () => {
  // 提取当前订单的 outTradeNo
  const outTradeNo = orderResult.value.outTradeNo
  if (!outTradeNo) {
    ElMessage.error('Invalid order number. Cannot send payment callback.')
    return
  }

  // 构建确认弹窗信息
  ElMessageBox.confirm(
    `[Stress test] This will send 5 concurrent payment callbacks to the server.\nOrder: ${outTradeNo}\nAmount: $${(orderResult.value.totalAmount / 100).toFixed(2)}`,
    'Confirm payment (stress test)',
    {
      confirmButtonText: 'Run concurrent test',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    console.log('[PayCallback] 开始并发压测，outTradeNo=' + outTradeNo)

    // 【核心压测逻辑】构建 5 个相同的 Axios POST 请求
    const callbackRequests = []
    for (let i = 1; i <= 5; i++) {
      callbackRequests.push(
        post('/order/pay/callback', { outTradeNo: outTradeNo })
          .then(res => {
            console.log(`[PayCallback] 第${i}次请求响应:`, res)
            return { index: i, success: true, data: res }
          })
          .catch(err => {
            console.error(`[PayCallback] 第${i}次请求失败:`, err)
            return { index: i, success: false, error: err }
          })
      )
    }

    // 【并发发送】使用 Promise.all() 同时触发 5 个请求
    console.log('[PayCallback] 并发发送 5 个回调请求...')
    const results = await Promise.all(callbackRequests)

    // 统计结果
    const successCount = results.filter(r => r.success).length
    const failCount = results.filter(r => !r.success).length

    console.log('[PayCallback] 压测完成，成功:', successCount, '失败:', failCount)
    console.log('[PayCallback] 详细结果:', results)

    // 关闭收银台弹窗
    showPayDialog.value = false

    // 显示压测完成消息
    ElMessage.success(`Sent 5 concurrent callbacks.\nOK: ${successCount}\nFailed: ${failCount}\nCheck server logs for idempotency.`)

    // 清空订单结果
    orderResult.value = {
      orderId: '',
      outTradeNo: '',
      totalAmount: 0,
      totalNum: 0
    }

    // 【刷新页面】模拟支付完成后的跳转
    setTimeout(() => {
      window.location.reload()
    }, 2000)

  }).catch(() => {
    // 用户选择取消
    ElMessage.info('Stress test cancelled')
  })
}

/**
 * 【收银台】取消支付
 */
const handleCancelPay = () => {
  showPayDialog.value = false
  ElMessage.info('Order saved. Please complete payment within 30 minutes.')
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
  
  // 【关键】首先检查并恢复登录状态
  const isLoggedInResult = checkLoginStatus()
  console.log('[Init] 登录状态检查结果:', isLoggedInResult)
  
  fetchProducts()
  
  // 【关键】只有登录后才加载购物车数量
  if (isLoggedInResult) {
    console.log('[Init] 用户已登录，开始加载购物车')
    loadCartCount()
  } else {
    console.log('[Init] 用户未登录，跳过购物车加载')
  }
  
  // 【调试工具】将检查函数挂载到 window，方便在控制台调试
  window.checkLoginState = () => {
    console.log('===== 手动检查登录状态 =====')
    console.log('buyer_token:', localStorage.getItem('buyer_token'))
    console.log('buyer_username:', localStorage.getItem('buyer_username'))
    console.log('buyer_userId:', localStorage.getItem('buyer_userId'))
    console.log('isLoggedIn.value:', isLoggedIn.value)
    console.log('showLoginDialog.value:', showLoginDialog.value)
    console.log('===========================')
    return {
      token: localStorage.getItem('buyer_token'),
      username: localStorage.getItem('buyer_username'),
      isLoggedIn: isLoggedIn.value
    }
  }
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

/* ================== 购物车抽屉样式 ================== */

.cart-drawer :deep(.el-drawer__header) {
  padding: 20px;
  margin-bottom: 0;
  border-bottom: 1px solid #e4e7ed;
}

.cart-drawer :deep(.el-drawer__body) {
  padding: 0;
  overflow: hidden;
}

.cart-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.cart-items {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  margin-bottom: 12px;
  background: #f5f7fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.cart-item:hover {
  background: #ebeef5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.cart-item-image {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
}

.cart-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e4e7ed;
}

.cart-item-info {
  flex: 1;
  min-width: 0;
}

.cart-item-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.cart-item-price {
  font-size: 16px;
  font-weight: 700;
  color: #ff4757;
  margin: 0;
}

.cart-item-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.cart-item-actions :deep(.el-input-number) {
  width: 90px;
}

.delete-btn {
  padding: 4px 8px;
  color: #909399;
  transition: color 0.3s;
}

.delete-btn:hover {
  color: #f56c6c;
}

.cart-footer {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  background: #fff;
}

.cart-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;
}

.total-label {
  font-size: 14px;
  color: #606266;
}

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: #ff4757;
}

.checkout-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
}

.cart-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 40px;
}

.empty-text {
  font-size: 16px;
  color: #909399;
  margin: 16px 0 24px 0;
}

.continue-shopping-btn {
  padding: 12px 32px;
  font-size: 14px;
  font-weight: 500;
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

/* ================== 用户登录相关样式 ================== */

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: opacity 0.3s;
}

.user-name:hover {
  opacity: 0.8;
}

/* 登录弹窗样式 */
.login-tips {
  margin-top: 16px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 12px;
  color: #909399;
}

.login-tips p {
  margin: 4px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 下拉菜单样式调整 */
:deep(.el-dropdown-menu__item) {
  font-size: 14px;
}

/* ================== 收银台弹窗样式 ================== */

.pay-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  margin-right: 0;
  border-bottom: 1px solid #e4e7ed;
}

.pay-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.pay-content {
  padding: 0 8px;
}

.order-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.order-label {
  font-size: 14px;
  color: #909399;
}

.order-value {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.order-no {
  font-family: 'Courier New', monospace;
  background: #f5f7fa;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 13px;
}

.pay-amount-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
  padding: 16px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe6e6 100%);
  border-radius: 12px;
  border: 1px solid #ffdbdb;
}

.pay-label {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
}

.pay-amount {
  font-size: 32px;
  font-weight: 700;
  color: #ff4757;
}

.pay-methods {
  margin-top: 24px;
}

.pay-method-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.pay-method-options {
  display: flex;
  gap: 12px;
}

.pay-method-option {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.pay-method-option:hover {
  border-color: #c0c4cc;
}

.pay-method-option.active {
  border-color: v-bind(themeColor);
  background: rgba(30, 58, 95, 0.05);
}

.pay-method-option span {
  font-size: 13px;
  color: #606266;
}

.pay-method-option.active span {
  color: v-bind(themeColor);
  font-weight: 600;
}

.pay-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 0 4px;
}

.confirm-pay-btn {
  padding: 0 24px;
}

.confirm-pay-btn :deep(.el-icon) {
  margin-right: 8px;
}

/* Responsive for pay dialog */
@media (max-width: 768px) {
  .pay-method-options {
    flex-direction: column;
  }

  .pay-amount {
    font-size: 24px;
  }

  .pay-footer {
    flex-direction: column-reverse;
  }

  .pay-footer .el-button {
    width: 100%;
  }
}

/* ================== 订单列表抽屉样式 ================== */

.order-drawer :deep(.el-drawer__header) {
  padding: 20px;
  margin-bottom: 0;
  border-bottom: 1px solid #e4e7ed;
}

.order-drawer :deep(.el-drawer__body) {
  padding: 0;
  overflow: hidden;
}

.order-loading {
  padding: 20px;
}

.order-content {
  height: 100%;
  overflow-y: auto;
  padding: 20px;
}

.order-content :deep(.el-timeline) {
  padding-left: 8px;
}

.order-card {
  border-radius: 12px;
  margin-bottom: 8px;
}

.order-card :deep(.el-card__body) {
  padding: 16px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #e4e7ed;
}

.order-no-section {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}

.order-no {
  font-family: 'Courier New', monospace;
  font-size: 13px;
  font-weight: 600;
}

.pay-status-tag {
  font-weight: 600;
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 8px;
}

.order-item-img {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  flex-shrink: 0;
}

.order-item-img-placeholder {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e4e7ed;
  border-radius: 6px;
}

.order-item-info {
  flex: 1;
  min-width: 0;
}

.order-item-name {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.order-item-spec {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.order-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.order-amount {
  display: flex;
  align-items: center;
  gap: 12px;
}

.amount-label {
  font-size: 12px;
  color: #909399;
}

.amount-value {
  font-size: 18px;
  font-weight: 700;
  color: #ff4757;
}

.order-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 40px;
}

.empty-subtext {
  font-size: 14px;
  color: #909399;
  margin: 8px 0 24px 0;
}

/* 支付状态标签颜色覆盖 */
:deep(.el-tag--danger) {
  background-color: #fef0f0;
  border-color: #fde2e2;
  color: #f56c6c;
}

:deep(.el-tag--success) {
  background-color: #f0f9eb;
  border-color: #e1f3d8;
  color: #67c23a;
}
</style>
