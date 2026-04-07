<template>
  <div class="login-container">
    <el-card class="login-box">
      <h2>Mall Admin Login</h2>
      <el-form>
        <el-form-item>
          <el-input v-model="username" placeholder="Username" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="password" type="password" placeholder="Password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            Login
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { post } from '../utils/request.js'
import { useTenantStore } from '../store/tenant.js'

const router = useRouter()
const tenantStore = useTenantStore()
const username = ref('admin')
const password = ref('123456')
const loading = ref(false)

const handleLogin = async () => {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  loading.value = true
  try {
    // 调用后端登录接口（通过网关代理）
    const res = await post('/permission/admin/login', {
      username: username.value,
      password: password.value
    })
    
    if (res.code === 20000 && res.data) {
      // 存储 Token 到 localStorage
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('username', res.data.username)
      localStorage.setItem('tenantId', res.data.tenantId)
      
      ElMessage.success(`登录成功！欢迎 ${res.data.username} (租户: ${res.data.tenantId})`)
      
      // 跳转到首页
      router.push('/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error(error.message || '网络错误，请检查网关服务是否启动')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f0f2f5;
}
.login-box {
  width: 360px;
}
h2 {
  text-align: center;
  margin-bottom: 24px;
}
</style>
