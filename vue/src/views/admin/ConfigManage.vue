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

    <!-- 配置列表 -->
    <div class="config-section" v-loading="loading">
      <div class="config-card" v-for="item in configList" :key="item.id">
        <div class="config-header">
          <div class="config-icon">
            <el-icon v-if="item.paramKey === 'warehouse_address'"><Location /></el-icon>
            <el-icon v-else-if="item.paramKey === 'warehouse_contact'"><Phone /></el-icon>
            <el-icon v-else><Setting /></el-icon>
          </div>
          <div class="config-info">
            <h3>{{ item.paramDesc || item.paramKey }}</h3>
            <code>{{ item.paramKey }}</code>
          </div>
          <el-button 
            type="danger" 
            size="small" 
            circle 
            class="delete-btn"
            @click="handleDelete(item)"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
        <div class="config-body">
          <el-input 
            v-if="editingId === item.id"
            v-model="editValue"
            :type="item.paramKey === 'warehouse_address' ? 'textarea' : 'text'"
            :rows="2"
            placeholder="请输入参数值"
          />
          <p v-else class="config-value">{{ item.paramValue || '未设置' }}</p>
        </div>
        <div class="config-footer">
          <template v-if="editingId === item.id">
            <el-button type="primary" size="small" @click="handleSave(item)" :loading="saveLoading">
              保存
            </el-button>
            <el-button size="small" @click="handleCancel">取消</el-button>
          </template>
          <el-button v-else type="primary" size="small" text @click="handleEdit(item)">
            <el-icon><Edit /></el-icon>编辑
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-if="!loading && configList.length === 0">
        <el-icon><Setting /></el-icon>
        <p>暂无配置项</p>
        <el-button type="primary" @click="handleAdd">添加配置</el-button>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location, Phone, Setting, Edit, Plus, Delete } from '@element-plus/icons-vue'
import { getConfigList, addConfig, updateConfig, deleteConfig } from '@/api/config'

const loading = ref(false)
const saveLoading = ref(false)
const submitLoading = ref(false)
const configList = ref([])

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
    configList.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
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
  const exists = configList.value.some(c => c.paramKey === form.paramKey)
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

.config-section {
  display: grid;
  gap: 20px;
}

.config-card {
  background: var(--hanfu-paper);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 25px;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: var(--shadow-light);
    
    .delete-btn {
      opacity: 1;
    }
  }
}

.config-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--border-color);
  
  .delete-btn {
    margin-left: auto;
    opacity: 0;
    transition: opacity 0.3s;
  }
}

.config-icon {
  width: 45px;
  height: 45px;
  background: linear-gradient(135deg, var(--hanfu-gold) 0%, #8B7019 100%);
  color: var(--hanfu-ink);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.config-info {
  h3 {
    font-size: 16px;
    color: var(--text-primary);
    margin-bottom: 5px;
  }
  
  code {
    font-size: 12px;
    color: var(--text-placeholder);
    background: var(--hanfu-paper-dark);
    padding: 2px 8px;
    border-radius: 4px;
  }
}

.config-body {
  margin-bottom: 15px;
}

.config-value {
  font-size: 15px;
  color: var(--text-primary);
  line-height: 1.6;
  padding: 12px 15px;
  background: var(--hanfu-paper-dark);
  border-radius: var(--radius-sm);
}

.config-footer {
  display: flex;
  gap: 10px;
}

.empty-state {
  padding: 80px;
  text-align: center;
  color: var(--text-placeholder);
  
  .el-icon {
    font-size: 60px;
    margin-bottom: 15px;
  }
  
  p {
    font-size: 16px;
    margin-bottom: 20px;
  }
}

.form-tip {
  font-size: 12px;
  color: var(--text-placeholder);
  margin-top: 5px;
}
</style>
