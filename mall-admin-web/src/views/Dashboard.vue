<template>
  <div class="dashboard">
    <h1>欢迎使用商户后台管理系统</h1>
    <div class="stats-cards">
      <div class="card">
        <h3>今日订单</h3>
        <p class="number">128</p>
      </div>
      <div class="card">
        <h3>待处理</h3>
        <p class="number">12</p>
      </div>
      <div class="card">
        <h3>今日销售额</h3>
        <p class="number">¥ 8,520</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 20px;
}

h1 {
  margin-bottom: 24px;
  color: #333;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.card {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card h3 {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
}

.card .number {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}
</style>
