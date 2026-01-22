<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <span>个人信息</span>
      </template>
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="信用分">
          <el-input v-model="form.creditScore" disabled />
        </el-form-item>
        <el-form-item label="钱包余额">
          <el-input v-model="form.balance" disabled>
            <template #prefix>¥</template>
          </el-input>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>默认收货地址</span>
      </template>
      <el-form :model="form" label-width="100px">
        <el-form-item label="收货人">
          <el-input v-model="form.defaultReceiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.defaultReceiverPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input v-model="form.defaultAddress" type="textarea" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="loading">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo } from '@/api/auth'
import { updateProfile } from '@/api/user'

const loading = ref(false)
const form = reactive({
  username: '',
  nickname: '',
  phone: '',
  creditScore: 100,
  balance: '0.00',
  defaultReceiverName: '',
  defaultReceiverPhone: '',
  defaultAddress: ''
})

onMounted(async () => {
  try {
    const res = await getUserInfo()
    Object.assign(form, res.data)
  } catch (error) {
    console.error(error)
  }
})

const handleSave = async () => {
  loading.value = true
  try {
    await updateProfile({
      nickname: form.nickname,
      phone: form.phone,
      defaultReceiverName: form.defaultReceiverName,
      defaultReceiverPhone: form.defaultReceiverPhone,
      defaultAddress: form.defaultAddress
    })
    ElMessage.success('保存成功')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.profile-container {
  max-width: 600px;
  margin: 20px auto;
  padding: 0 20px;
}
</style>
