<template>
  <div class="banner-manage-page">
    <div class="page-title">
      <h2>轮播图管理</h2>
      <p class="subtitle">管理首页轮播图展示</p>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加轮播图
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="图片预览" width="120" align="center">
          <template #default="{ row }">
            <el-image 
              :src="row.imgUrl" 
              :preview-src-list="[row.imgUrl]"
              fit="cover"
              class="table-image"
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="createTime" label="发布时间" width="170">
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
      :title="isEdit ? '编辑轮播图' : '添加轮播图'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="图片" prop="imgUrl">
          <el-upload
            class="banner-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <img v-if="form.imgUrl" :src="form.imgUrl" class="preview-image" />
            <div v-else class="upload-placeholder">
              <el-icon><Plus /></el-icon>
              <span>点击上传图片</span>
            </div>
          </el-upload>
          <p class="upload-tip">建议尺寸：1920×480，支持 jpg/png/gif 格式</p>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getNoticePage, addNotice, updateNotice, deleteNotice } from '@/api/notice'
import { uploadUrl } from '@/api/file'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  title: '',
  imgUrl: '',
  type: 2  // 轮播图类型
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imgUrl: [{ required: true, message: '请上传图片', trigger: 'change' }]
}

// 上传请求头（携带token）
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

onMounted(() => loadData())

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNoticePage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      type: 2  // 只查询轮播图
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.title = ''
  form.imgUrl = ''
  form.type = 2
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.title = row.title
  form.imgUrl = row.imgUrl
  form.type = 2
  dialogVisible.value = true
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.imgUrl = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateNotice(form)
      ElMessage.success('更新成功')
    } else {
      await addNotice(form)
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
  await ElMessageBox.confirm(`确定要删除轮播图「${row.title}」吗？`, '删除确认', { type: 'warning' })
  try {
    await deleteNotice(row.id)
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
.banner-manage-page {
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

.table-image {
  width: 80px;
  height: 45px;
  border-radius: 4px;
  cursor: pointer;
}

/* 上传组件样式 */
.banner-uploader {
  :deep(.el-upload) {
    width: 100%;
    border: 1px dashed var(--border-color);
    border-radius: var(--radius-md);
    cursor: pointer;
    overflow: hidden;
    transition: border-color 0.3s;
    
    &:hover {
      border-color: var(--hanfu-red);
    }
  }
}

.preview-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.upload-placeholder {
  width: 100%;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--hanfu-paper-dark);
  color: var(--text-placeholder);
  
  .el-icon {
    font-size: 40px;
    margin-bottom: 10px;
  }
  
  span {
    font-size: 14px;
  }
}

.upload-tip {
  font-size: 12px;
  color: var(--text-placeholder);
  margin-top: 10px;
}
</style>
