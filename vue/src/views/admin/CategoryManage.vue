<template>
  <div class="category-manage-page">
    <div class="page-title">
      <h2>分类管理</h2>
      <p class="subtitle">管理汉服款式分类</p>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加分类
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="categoryName" label="分类名称" min-width="200" />
        <el-table-column prop="sort" label="排序" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.sort }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" text @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑分类' : '添加分类'"
      width="450px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
          <span class="form-tip">数值越小排序越靠前</span>
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
import { Plus } from '@element-plus/icons-vue'
import { getCategoryList, addCategory, updateCategory, deleteCategory } from '@/api/category'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  categoryName: '',
  sort: 0
})

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCategoryList()
    tableData.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.categoryName = ''
  form.sort = 0
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.categoryName = row.categoryName
  form.sort = row.sort
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateCategory(form)
      ElMessage.success('更新成功')
    } else {
      await addCategory(form)
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

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除分类「${row.categoryName}」吗？`, '删除确认', { type: 'warning' })
  try {
    await deleteCategory(row.id)
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
.category-manage-page {
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

.form-tip {
  margin-left: 10px;
  color: var(--text-secondary);
  font-size: 12px;
}
</style>
