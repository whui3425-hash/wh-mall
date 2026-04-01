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
          <el-button type="primary" @click="handleLogin" style="width: 100%">Login</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('admin')
const password = ref('123456')

const handleLogin = () => {
  router.push('/')
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
