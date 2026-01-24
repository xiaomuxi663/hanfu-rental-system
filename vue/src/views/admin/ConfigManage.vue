<template>
  <div class="config-manage-page">
    <div class="page-title">
      <h2>系统配置</h2>
      <p class="subtitle">管理系统基础参数配置</p>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加配置
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table :data="configList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="paramKey" label="参数键" width="180">
          <template #default="{ row }">
            <code class="param-key">{{ row.paramKey }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="paramDesc" label="参数描述" width="200" />
        <el-table-column prop="paramValue" label="参数值" min-width="250">
          <template #default="{ row }">
            <template v-if="editingId === row.id">
              <el-input 
                v-model="editValue" 
                size="small"
                placeholder="请输入参数值"
                @keyup.enter="handleSave(row)"
              />
            </template>
            <span v-else class="param-value">{{ row.paramValue || '未设置' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <template v-if="editingId === row.id">
              <el-button type="success" size="small" text @click="handleSave(row)" :loading="saveLoading">保存</el-button>
              <el-button size="small" text @click="handleCancel">取消</el-button>
            </template>
            <template v-else>
              <el-button type="primary" size="small" text @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" size="small" text @click="handleDelete(row)">删除</el-button>
            </template>
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
          @size-change="updatePageData"
          @current-change="updatePageData"
          background
        />
      </div>
    </div>

    <!-- 添加配置对话框 -->
    <el-dialog v-model="dialogVisible" title="添加配置" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="参数键" prop="paramKey">
          <el-input v-model="form.paramKey" placeholder="如：site_name" />
          <div class="form-tip">唯一标识，建议使用下划线命名</div>
        </el-form-item>
        <el-form-item label="参数值" prop="paramValue">
          <el-input v-model="form.paramValue" type="textarea" :rows="3" placeholder="请输入参数值" />
        </el-form-item>
        <el-form-item label="参数描述" prop="paramDesc">
          <el-input v-model="form.paramDesc" placeholder="如：网站名称" />
          <div class="form-tip">便于理解的中文描述</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getConfigList, addConfig, updateConfig, deleteConfig } from '@/api/config'

const loading = ref(false)
const saveLoading = ref(false)
const submitLoading = ref(false)
const allConfigList = ref([])  // 全部数据
const configList = ref([])     // 当前页数据
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const editingId = ref(null)
const editValue = ref('')

const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({
  paramKey: '',
  paramValue: '',
  paramDesc: ''
})

const rules = {
  paramKey: [
    { required: true, message: '请输入参数键', trigger: 'blur' },
    { pattern: /^[a-z][a-z0-9_]*$/, message: '只能使用小写字母、数字和下划线，且以字母开头', trigger: 'blur' }
  ],
  paramValue: [{ required: true, message: '请输入参数值', trigger: 'blur' }],
  paramDesc: [{ required: true, message: '请输入参数描述', trigger: 'blur' }]
}

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await getConfigList()
    allConfigList.value = res.data || []
    pagination.total = allConfigList.value.length
    updatePageData()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 更新当前页数据
const updatePageData = () => {
  const start = (pagination.pageNum - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  configList.value = allConfigList.value.slice(start, end)
}

const handleAdd = () => {
  form.paramKey = ''
  form.paramValue = ''
  form.paramDesc = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  // 检查key是否已存在
  const exists = allConfigList.value.some(c => c.paramKey === form.paramKey)
  if (exists) {
    ElMessage.warning('该参数键已存在')
    return
  }
  
  submitLoading.value = true
  try {
    await addConfig(form)
    ElMessage.success('添加成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleEdit = (item) => {
  editingId.value = item.id
  editValue.value = item.paramValue
}

const handleCancel = () => {
  editingId.value = null
  editValue.value = ''
}

const handleSave = async (item) => {
  if (!editValue.value.trim()) {
    ElMessage.warning('请输入参数值')
    return
  }
  
  saveLoading.value = true
  try {
    await updateConfig({
      id: item.id,
      paramValue: editValue.value
    })
    ElMessage.success('保存成功')
    item.paramValue = editValue.value
    handleCancel()
  } catch (error) {
    console.error(error)
  } finally {
    saveLoading.value = false
  }
}

const handleDelete = async (item) => {
  await ElMessageBox.confirm(
    `确定要删除配置「${item.paramDesc || item.paramKey}」吗？`,
    '删除确认',
    { type: 'warning' }
  )
  try {
    await deleteConfig(item.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped lang="scss">
.config-manage-page {
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

.action-bar {
  margin-bottom: 20px;
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

.param-key {
  font-size: 13px;
  color: var(--hanfu-red);
  background: var(--hanfu-paper-dark);
  padding: 2px 8px;
  border-radius: 4px;
}

.param-value {
  color: var(--text-primary);
  word-break: break-all;
}

.form-tip {
  font-size: 12px;
  color: var(--text-placeholder);
  margin-top: 5px;
}
</style>
