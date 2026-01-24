<template>
  <div class="hanfu-manage-page">
    <div class="page-title">
      <h2>汉服管理</h2>
      <p class="subtitle">管理汉服款式信息</p>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-select v-model="searchForm.categoryId" placeholder="选择分类" clearable style="width: 150px">
        <el-option v-for="cat in categoryList" :key="cat.id" :label="cat.categoryName" :value="cat.id" />
      </el-select>
      <el-input v-model="searchForm.name" placeholder="汉服名称" clearable style="width: 200px" />
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
      <el-button type="primary" class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加汉服
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="主图" width="90" align="center">
          <template #default="{ row }">
            <el-image 
              :src="row.mainImage" 
              style="width: 60px; height: 60px; border-radius: 4px;"
              fit="cover"
              :preview-src-list="[row.mainImage]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="dailyRent" label="日租金" width="100" align="right">
          <template #default="{ row }">
            <span class="price">¥{{ row.dailyRent }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deposit" label="押金" width="100" align="right">
          <template #default="{ row }">
            <span class="price">¥{{ row.deposit }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stockCount" label="可租库存" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.stockCount > 0 ? 'success' : 'danger'" size="small">
              {{ row.stockCount || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isPublish === 1 ? 'success' : 'info'" size="small">
              {{ row.isPublish === 1 ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">编辑</el-button>
            <el-button 
              :type="row.isPublish === 1 ? 'warning' : 'success'" 
              size="small" 
              text 
              @click="handlePublish(row)"
            >
              {{ row.isPublish === 1 ? '下架' : '上架' }}
            </el-button>
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
      :title="isEdit ? '编辑汉服' : '添加汉服'"
      width="700px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="汉服名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="cat in categoryList" :key="cat.id" :label="cat.categoryName" :value="cat.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="日租金" prop="dailyRent">
              <el-input-number v-model="form.dailyRent" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="押金" prop="deposit">
              <el-input-number v-model="form.deposit" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="主图" prop="mainImage">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleMainImageSuccess"
            accept="image/*"
          >
            <el-image v-if="form.mainImage" :src="form.mainImage" class="uploaded-image" fit="cover" />
            <el-icon v-else class="upload-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="详情图">
          <el-upload
            class="sub-images-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :file-list="subImageList"
            list-type="picture-card"
            :on-success="handleSubImageSuccess"
            :on-remove="handleSubImageRemove"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="详情描述">
          <el-input v-model="form.detailContent" type="textarea" :rows="4" placeholder="请输入详情描述" />
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
import { useUserStore } from '@/stores/user'
import { getCategoryList } from '@/api/category'
import { getHanfuPage, addHanfu, updateHanfu, deleteHanfu, updatePublish } from '@/api/hanfu'

const userStore = useUserStore()
const uploadUrl = '/api/file/upload'
const uploadHeaders = computed(() => ({ token: userStore.token }))

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const categoryList = ref([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })
const searchForm = reactive({ categoryId: null, name: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  dailyRent: 0,
  deposit: 0,
  mainImage: '',
  subImages: '',
  detailContent: ''
})
const subImageList = ref([])

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  dailyRent: [{ required: true, message: '请输入日租金', trigger: 'blur' }],
  deposit: [{ required: true, message: '请输入押金', trigger: 'blur' }],
  mainImage: [{ required: true, message: '请上传主图', trigger: 'change' }]
}

onMounted(async () => {
  await loadCategories()
  loadData()
})

const loadCategories = async () => {
  try {
    const res = await getCategoryList()
    categoryList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getHanfuPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      categoryId: searchForm.categoryId,
      name: searchForm.name
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
  searchForm.categoryId = null
  searchForm.name = ''
  pagination.pageNum = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', categoryId: null, dailyRent: 0, deposit: 0, mainImage: '', subImages: '', detailContent: '' })
  subImageList.value = []
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  subImageList.value = row.subImages ? row.subImages.split(',').map((url, index) => ({ name: `image-${index}`, url })) : []
  dialogVisible.value = true
}

const handleMainImageSuccess = (response) => {
  if (response.code === 200) {
    form.mainImage = response.data
    ElMessage.success('上传成功')
  }
}

const handleSubImageSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    subImageList.value = fileList.map(f => ({ name: f.name, url: f.response?.data || f.url }))
    form.subImages = subImageList.value.map(f => f.url).join(',')
  }
}

const handleSubImageRemove = (file, fileList) => {
  subImageList.value = fileList
  form.subImages = fileList.map(f => f.url).join(',')
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateHanfu(form)
      ElMessage.success('更新成功')
    } else {
      await addHanfu(form)
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

const handlePublish = async (row) => {
  const newStatus = row.isPublish === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  await ElMessageBox.confirm(`确定要${action}「${row.name}」吗？`, '确认', { type: 'warning' })
  try {
    await updatePublish(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定要删除「${row.name}」吗？`, '删除确认', { type: 'warning' })
  try {
    await deleteHanfu(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped lang="scss">
.hanfu-manage-page {
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

.price {
  color: var(--hanfu-red);
  font-weight: 500;
}

.pagination-section {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid var(--border-color);
}

.image-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 120px;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    &:hover {
      border-color: var(--hanfu-red);
    }
  }
  
  .upload-icon {
    font-size: 28px;
    color: #8c939d;
  }
  
  .uploaded-image {
    width: 120px;
    height: 120px;
  }
}

.sub-images-uploader {
  :deep(.el-upload-list__item) {
    width: 100px;
    height: 100px;
  }
  :deep(.el-upload--picture-card) {
    width: 100px;
    height: 100px;
  }
}
</style>
