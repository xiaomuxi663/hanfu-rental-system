<template>
  <div class="sku-manage-page">
    <div class="page-title">
      <h2>库存管理</h2>
      <p class="subtitle">管理汉服实物库存（SKU）</p>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-select v-model="searchForm.spuId" placeholder="选择款式" clearable filterable style="width: 200px">
        <el-option v-for="spu in spuList" :key="spu.id" :label="spu.name" :value="spu.id" />
      </el-select>
      <el-select v-model="searchForm.status" placeholder="库存状态" clearable style="width: 130px">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
      <el-button type="primary" class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加库存
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="skuCode" label="SKU编码" width="150" />
        <el-table-column prop="spuName" label="所属款式" min-width="180" />
        <el-table-column prop="size" label="尺码" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.size }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="入库时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">编辑</el-button>
            <el-dropdown @command="(cmd) => handleStatusChange(row, cmd)" trigger="click">
              <el-button type="warning" size="small" text>状态 <el-icon><ArrowDown /></el-icon></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="item in statusOptions" :key="item.value" :command="item.value" :disabled="row.status === item.value">
                    {{ item.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="danger" size="small" text @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
          background
        />
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑库存' : '添加库存'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="所属款式" prop="spuId">
          <el-select v-model="form.spuId" placeholder="请选择款式" filterable style="width: 100%">
            <el-option v-for="spu in spuList" :key="spu.id" :label="spu.name" :value="spu.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="SKU编码" prop="skuCode">
          <el-input v-model="form.skuCode" placeholder="请输入SKU编码（如HF001-M-01）" />
        </el-form-item>
        <el-form-item label="尺码" prop="size">
          <el-select v-model="form.size" placeholder="请选择尺码" style="width: 100%">
            <el-option label="S" value="S" />
            <el-option label="M" value="M" />
            <el-option label="L" value="L" />
            <el-option label="XL" value="XL" />
            <el-option label="XXL" value="XXL" />
            <el-option label="均码" value="均码" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量添加弹窗 -->
    <el-dialog v-model="batchDialogVisible" title="批量添加库存" width="500px">
      <el-form :model="batchForm" ref="batchFormRef" label-width="90px">
        <el-form-item label="所属款式" prop="spuId" :rules="[{ required: true, message: '请选择款式' }]">
          <el-select v-model="batchForm.spuId" placeholder="请选择款式" filterable style="width: 100%">
            <el-option v-for="spu in spuList" :key="spu.id" :label="spu.name" :value="spu.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="尺码" prop="size" :rules="[{ required: true, message: '请选择尺码' }]">
          <el-select v-model="batchForm.size" placeholder="请选择尺码" style="width: 100%">
            <el-option label="S" value="S" />
            <el-option label="M" value="M" />
            <el-option label="L" value="L" />
            <el-option label="XL" value="XL" />
            <el-option label="XXL" value="XXL" />
            <el-option label="均码" value="均码" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="count" :rules="[{ required: true, message: '请输入数量' }]">
          <el-input-number v-model="batchForm.count" :min="1" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit" :loading="batchLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ArrowDown } from '@element-plus/icons-vue'
import { getHanfuPage } from '@/api/hanfu'
import { getSkuPage, addSku, updateSku, deleteSku, updateSkuStatus } from '@/api/hanfu'

const statusOptions = [
  { value: 0, label: '在库', type: 'success' },
  { value: 1, label: '已租出', type: 'warning' },
  { value: 2, label: '清洗中', type: 'info' },
  { value: 3, label: '维修中', type: 'primary' },
  { value: 4, label: '已报废', type: 'danger' }
]

const loading = ref(false)
const submitLoading = ref(false)
const batchLoading = ref(false)
const tableData = ref([])
const spuList = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const searchForm = reactive({ spuId: null, status: null })

const dialogVisible = ref(false)
const batchDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const batchFormRef = ref(null)
const form = reactive({
  id: null,
  spuId: null,
  skuCode: '',
  size: ''
})
const batchForm = reactive({
  spuId: null,
  size: '',
  count: 1
})

const rules = {
  spuId: [{ required: true, message: '请选择款式', trigger: 'change' }],
  skuCode: [{ required: true, message: '请输入SKU编码', trigger: 'blur' }],
  size: [{ required: true, message: '请选择尺码', trigger: 'change' }]
}

onMounted(async () => {
  await loadSpuList()
  loadData()
})

const loadSpuList = async () => {
  try {
    const res = await getHanfuPage({ pageNum: 1, pageSize: 1000 })
    spuList.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSkuPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      spuId: searchForm.spuId,
      status: searchForm.status
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.spuId = null
  searchForm.status = null
  pagination.pageNum = 1
  loadData()
}

const getStatusType = (status) => statusOptions.find(s => s.value === status)?.type || 'info'
const getStatusLabel = (status) => statusOptions.find(s => s.value === status)?.label || '未知'

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, spuId: null, skuCode: '', size: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, spuId: row.spuId, skuCode: row.skuCode, size: row.size })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateSku(form)
      ElMessage.success('更新成功')
    } else {
      await addSku(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleBatchSubmit = async () => {
  await batchFormRef.value.validate()
  batchLoading.value = true
  try {
    for (let i = 0; i < batchForm.count; i++) {
      const code = `${Date.now()}-${batchForm.size}-${String(i + 1).padStart(2, '0')}`
      await addSku({ spuId: batchForm.spuId, skuCode: code, size: batchForm.size })
    }
    ElMessage.success(`成功添加 ${batchForm.count} 件库存`)
    batchDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    batchLoading.value = false
  }
}

const handleStatusChange = async (row, status) => {
  try {
    await updateSkuStatus(row.id, status)
    ElMessage.success('状态更新成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除SKU「${row.skuCode}」吗？`, '删除确认', { type: 'warning' })
  try {
    await deleteSku(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}
</script>

<style scoped lang="scss">
.sku-manage-page {
  padding: 25px;
  background: var(--hanfu-paper);
  min-height: calc(100vh - 60px);
}

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

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  
  .add-btn {
    margin-left: auto;
  }
}

.table-section {
  background: var(--hanfu-paper);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.pagination-section {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid var(--border-color);
}
</style>
