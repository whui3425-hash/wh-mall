<template>
  <div class="login-page">
    <div class="login-container">
      <el-card class="login-card" shadow="always">
        <div class="login-header">
          <div class="logo">
            <el-icon size="48" color="#409EFF"><Shop /></el-icon>
          </div>
          <h1 class="title">SaaS 商户控制台</h1>
          <p class="subtitle">专业电商管理平台</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="tenantId">
            <el-input
              v-model="form.tenantId"
              placeholder="请输入店铺ID，如 1001"
              :prefix-icon="OfficeBuilding"
              size="large"
              clearable
            />
          </el-form-item>

          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <p>忘记密码？请联系系统管理员</p>
        </div>
      </el-card>
    </div>

    <div class="login-bg">
      <div class="gradient-overlay"></div>
      <div class="particles">
        <span v-for="n in 20" :key="n" class="particle"></span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Shop, OfficeBuilding } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  tenantId: '',
  username: '',
  password: ''
})

const rules = {
  tenantId: [
    { required: true, message: '请输入店铺编号', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return

  try {
    // 表单验证
    await formRef.value.validate()
  } catch (error) {
    return
  }

  loading.value = true
  try {
    // 先存储租户ID，确保登录请求能带上 X-Tenant-Id 请求头
    localStorage.setItem('admin_tenant_id', form.tenantId)

    const res = await request.post('/permission/admin/login', {
      username: form.username,
      password: form.password
    })

    console.log('[登录] 响应数据:', res)

    // 登录成功后存储 token - 兼容多种响应格式
    // 格式1: { data: { token: 'xxx' } }
    // 格式2: { token: 'xxx' }
    // 格式3: 直接就是 token 字符串
    let token = null
    if (res.data && res.data.token) {
      token = res.data.token
    } else if (res.token) {
      token = res.token
    } else if (res.accessToken) {
      token = res.accessToken
    } else if (typeof res === 'string') {
      token = res
    }

    console.log('[登录] 提取的token:', token)

    if (!token || typeof token === 'object') {
      console.error('[登录] token格式异常:', token)
      throw new Error('登录响应格式错误')
    }

    localStorage.setItem('admin_token', token)

    // 登录成功提示
    ElMessage.success('欢迎回来，店长')

    // 强制跳转到首页
    console.log('准备跳转到首页...')
    try {
      await router.push('/')
      console.log('路由跳转已执行，当前路径:', window.location.pathname)
    } catch (routerError) {
      console.error('路由跳转失败，使用原生跳转:', routerError)
      window.location.href = '/'
    }
  } catch (error) {
    console.error('登录过程出错:', error)
    ElMessage.error(error.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.login-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  z-index: 0;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(ellipse at top, rgba(64, 158, 255, 0.15) 0%, transparent 50%),
              radial-gradient(ellipse at bottom, rgba(64, 158, 255, 0.1) 0%, transparent 50%);
}

.particles {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(64, 158, 255, 0.6);
  border-radius: 50%;
  animation: float 15s infinite;
}

.particle:nth-child(1) { left: 10%; animation-delay: 0s; }
.particle:nth-child(2) { left: 20%; animation-delay: 1s; }
.particle:nth-child(3) { left: 30%; animation-delay: 2s; }
.particle:nth-child(4) { left: 40%; animation-delay: 3s; }
.particle:nth-child(5) { left: 50%; animation-delay: 4s; }
.particle:nth-child(6) { left: 60%; animation-delay: 5s; }
.particle:nth-child(7) { left: 70%; animation-delay: 6s; }
.particle:nth-child(8) { left: 80%; animation-delay: 7s; }
.particle:nth-child(9) { left: 90%; animation-delay: 8s; }
.particle:nth-child(10) { left: 15%; animation-delay: 9s; }
.particle:nth-child(11) { left: 25%; animation-delay: 10s; }
.particle:nth-child(12) { left: 35%; animation-delay: 11s; }
.particle:nth-child(13) { left: 45%; animation-delay: 12s; }
.particle:nth-child(14) { left: 55%; animation-delay: 13s; }
.particle:nth-child(15) { left: 65%; animation-delay: 14s; }
.particle:nth-child(16) { left: 75%; animation-delay: 0.5s; }
.particle:nth-child(17) { left: 85%; animation-delay: 1.5s; }
.particle:nth-child(18) { left: 5%; animation-delay: 2.5s; }
.particle:nth-child(19) { left: 95%; animation-delay: 3.5s; }
.particle:nth-child(20) { left: 50%; animation-delay: 4.5s; }

@keyframes float {
  0% {
    transform: translateY(100vh) scale(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) scale(1.5);
    opacity: 0;
  }
}

.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  padding: 20px;
}

.login-card {
  border-radius: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.login-card :deep(.el-card__body) {
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  margin-bottom: 16px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-form {
  margin-top: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.login-form :deep(.el-input__inner) {
  height: 44px;
}

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 2px;
  margin-top: 8px;
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.4);
}

.login-footer {
  text-align: center;
  margin-top: 24px;
}

.login-footer p {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

/* 响应式适配 */
@media (max-width: 480px) {
  .login-card :deep(.el-card__body) {
    padding: 32px 24px;
  }

  .title {
    font-size: 20px;
  }
}
</style>
