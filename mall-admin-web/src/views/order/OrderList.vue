<template>
  <el-card class="order-list-card" shadow="hover">
    <template #header>
      <div class="card-header">
        <div class="title-section">
          <el-icon size="20" color="#409EFF"><Document /></el-icon>
          <span class="title">本店铺订单流水</span>
          <el-tag type="info" effect="plain" class="count-tag">
            共 {{ tableData.length }} 笔订单
          </el-tag>
        </div>
        <el-button
          type="primary"
          :icon="Refresh"
          :loading="loading"
          @click="fetchOrderList"
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
        prop="outTradeNo"
        label="订单流水号"
        min-width="180"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <span class="order-no">{{ row.outTradeNo || row.id }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="amount"
        label="订单金额"
        width="120"
        align="right"
      >
        <template #default="{ row }">
          <span class="amount">¥ {{ formatAmount(row.amount) }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="createTime"
        label="创建时间"
        width="160"
      >
        <template #default="{ row }">
          <el-icon size="14" color="#909399" class="time-icon"><Clock /></el-icon>
          <span class="time">{{ formatTime(row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="payStatus"
        label="支付状态"
        width="100"
        align="center"
      >
        <template #default="{ row }">
          <el-tag
            :type="row.payStatus === 1 ? 'success' : 'danger'"
            effect="light"
            size="small"
          >
            {{ row.payStatus === 1 ? '已支付' : '待支付' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        label="操作"
        width="100"
        align="center"
        fixed="right"
      >
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            size="small"
            @click="viewDetail(row)"
          >
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空状态 -->
    <el-empty
      v-if="!loading && tableData.length === 0"
      description="暂无订单数据"
      :image-size="120"
    >
      <el-button type="primary" @click="fetchOrderList">
        重新加载
      </el-button>
    </el-empty>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Refresh, Clock } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])

// 获取订单列表
const fetchOrderList = async () => {
  loading.value = true
  try {
    const res = await request.get('/order/admin/list')
    tableData.value = res || []
    ElMessage.success('订单数据已更新')
  } catch (error) {
    ElMessage.error(error.message || '获取订单列表失败')
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount && amount !== 0) return '0.00'
  return Number(amount).toFixed(2)
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

// 查看订单详情
const viewDetail = (row) => {
  ElMessage.info(`查看订单: ${row.outTradeNo || row.id}`)
  // 实际项目中可以跳转到详情页
  // router.push(`/orders/detail/${row.id}`)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchOrderList()
})
</script>

<style scoped>
.order-list-card {
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

.order-no {
  font-family: 'Courier New', monospace;
  font-weight: 500;
  color: #606266;
}

.amount {
  font-weight: 600;
  color: #f56c6c;
  font-size: 14px;
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

:deep(.el-tag--danger) {
  background-color: #fef0f0;
  border-color: #fde2e2;
  color: #f56c6c;
}

:deep(.el-button--primary) {
  font-weight: 500;
}

:deep(.el-empty) {
  padding: 40px 0;
}
</style>
