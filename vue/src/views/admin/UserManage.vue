<template>
  <div class="user-manage-page">
    <!-- 页面标题 -->
    <div class="page-title">
      <h2>用户管理</h2>
      <p class="subtitle">管理平台用户信息与权限</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input 
            v-model="searchForm.username" 
            placeholder="请输入用户名" 
            clearable 
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.roleKey" placeholder="请选择" clearable style="width: 120px">
            <el-option label="管理员" value="admin" />
            <el-option label="库管员" value="staff" />
            <el-option label="租客" value="tenant" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 100px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table 
        :data="tableData" 
        v-loading="loading" 
        stripe 
        class="user-table"
        :header-cell-style="{ background: 'var(--hanfu-paper-dark)', color: 'var(--text-primary)' }"
      >
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120">
          <template #default="{ row }">
            <span>{{ row.nickname || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130">
          <template #default="{ row }">
            <span>{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="roleKey" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.roleKey === 'admin'" type="danger" effect="plain">管理员</el-tag>
            <el-tag v-else-if="row.roleKey === 'staff'" type="warning" effect="plain">库管员</el-tag>
            <el-tag v-else type="info" effect="plain">租客</el-tag>
          </template>
        </el-table-column>
        
        <!-- 信用分列：仅显示租客的信用分 -->
        <el-table-column label="信用分" width="100" align="center">
          <template #default="{ row }">
            <template v-if="row.roleKey === 'tenant'">
              <span :class="['credit-text', getCreditClass(row.creditScore)]">
                {{ row.creditScore }}
              </span>
            </template>
            <span v-else class="na-text">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="light">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170">
          <template #default="{ row }">
            <span class="time-text">{{ row.createTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <template v-if="row.roleKey !== 'admin'">
              <el-button 
                v-if="row.status === 1" 
                type="danger" 
                size="small" 
                text
                @click="handleToggleStatus(row, 0)"
              >
                禁用
              </el-button>
              <el-button 
                v-else 
                type="success" 
                size="small" 
                text
                @click="handleToggleStatus(row, 1)"
              >
                启用
              </el-button>
            </template>
            <span v-else class="na-text">-</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUserList, updateUserStatus } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const searchForm = reactive({ username: '', roleKey: '', status: null })
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.roleKey = ''
  searchForm.status = null
  handleSearch()
}

const handleToggleStatus = async (row, status) => {
  const action = status === 1 ? '启用' : '禁用'
  await ElMessageBox.confirm(
    `确定要${action}用户「${row.username}」吗？`, 
    '操作确认', 
    { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }
  )
  try {
    await updateUserStatus(row.id, status)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    console.error(error)
  }
}

// 获取信用分样式类
const getCreditClass = (score) => {
  if (score >= 80) return 'credit-high'
  if (score >= 60) return 'credit-medium'
  return 'credit-low'
}
</script>

<style scoped lang="scss">
.user-manage-page {
  padding: 25px;
  background: var(--hanfu-paper);
  min-height: calc(100vh - 60px);
}

/* 页面标题 */
.page-title {
  margin-bottom: 25px;
  
  h2 {
    font-size: 22px;
    color: var(--text-primary);
    margin-bottom: 5px;
    letter-spacing: 2px;
  }
  
  .subtitle {
    font-size: 13px;
    color: var(--text-secondary);
  }
}

/* 搜索区域 */
.search-section {
  background: var(--hanfu-paper);
  padding: 20px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  margin-bottom: 20px;
}

.search-form {
  :deep(.el-form-item__label) {
    color: var(--text-secondary);
  }
}

/* 表格区域 */
.table-section {
  background: var(--hanfu-paper);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.user-table {
  :deep(.el-table__row) {
    font-family: 'Noto Serif SC', serif;
  }
}

/* 信用分样式 */
.credit-text {
  font-weight: 600;
  
  &.credit-high {
    color: var(--color-success);
  }
  
  &.credit-medium {
    color: var(--color-warning);
  }
  
  &.credit-low {
    color: var(--color-danger);
  }
}

.na-text {
  color: var(--text-placeholder);
}

.time-text {
  font-size: 13px;
  color: var(--text-secondary);
}

/* 分页区域 */
.pagination-section {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid var(--border-color);
}
</style>
