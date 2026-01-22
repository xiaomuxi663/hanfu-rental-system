<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon size="24" color="#409EFF"><User /></el-icon>
              <span>用户总数</span>
            </div>
          </template>
          <div class="card-value">0</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon size="24" color="#67C23A"><Goods /></el-icon>
              <span>汉服总数</span>
            </div>
          </template>
          <div class="card-value">0</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon size="24" color="#E6A23C"><Document /></el-icon>
              <span>订单总数</span>
            </div>
          </template>
          <div class="card-value">0</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon size="24" color="#F56C6C"><Money /></el-icon>
              <span>总收入</span>
            </div>
          </template>
          <div class="card-value">¥0.00</div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card style="margin-top: 20px">
      <template #header>
        <span>欢迎使用汉服租赁管理系统</span>
      </template>
      <p>系统功能正在开发中...</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped lang="scss">
.dashboard {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}
</style>
