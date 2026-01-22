<template>
  <div class="profile-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="avatar-section">
          <div class="avatar">
            <img v-if="form.avatar" :src="form.avatar" alt="头像" />
            <span v-else class="avatar-text">{{ form.nickname?.charAt(0) || '用' }}</span>
          </div>
          <div class="user-info">
            <h2 class="nickname">{{ form.nickname || '用户' }}</h2>
            <el-tag :type="roleTagType" size="small">{{ roleName }}</el-tag>
          </div>
        </div>
        <div class="header-decoration">
          <span class="line"></span>
          <span class="text">个人中心</span>
          <span class="line"></span>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="profile-content">
      <!-- 基本信息卡片 -->
      <div class="info-card">
        <div class="card-title">
          <el-icon><User /></el-icon>
          <span>基本信息</span>
        </div>
        <el-form :model="form" label-width="100px" class="profile-form">
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          
          <!-- 非管理员才显示信用分和余额 -->
          <template v-if="!isAdmin">
            <el-form-item label="信用分">
              <div class="credit-score">
                <el-progress 
                  :percentage="form.creditScore" 
                  :color="creditColor"
                  :stroke-width="12"
                  style="width: 200px"
                />
                <span class="score-text">{{ form.creditScore }} 分</span>
              </div>
            </el-form-item>
            <el-form-item label="钱包余额">
              <div class="balance">
                <span class="currency">¥</span>
                <span class="amount">{{ form.balance }}</span>
              </div>
            </el-form-item>
          </template>
        </el-form>
      </div>

      <!-- 收货地址卡片（仅租客显示） -->
      <div class="info-card" v-if="!isAdmin">
        <div class="card-title">
          <el-icon><Location /></el-icon>
          <span>默认收货地址</span>
        </div>
        <el-form :model="form" label-width="100px" class="profile-form">
          <el-form-item label="收货人">
            <el-input v-model="form.defaultReceiverName" placeholder="请输入收货人姓名" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="form.defaultReceiverPhone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="收货地址">
            <el-input v-model="form.defaultAddress" type="textarea" :rows="3" placeholder="请输入详细地址" />
          </el-form-item>
        </el-form>
      </div>

      <!-- 保存按钮 -->
      <div class="action-bar">
        <el-button type="primary" size="large" @click="handleSave" :loading="loading">
          保存修改
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Location } from '@element-plus/icons-vue'
import { getUserInfo } from '@/api/auth'
import { updateProfile } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  phone: '',
  avatar: '',
  creditScore: 100,
  balance: '0.00',
  roleKey: '',
  defaultReceiverName: '',
  defaultReceiverPhone: '',
  defaultAddress: ''
})

// 是否为管理员或库管员
const isAdmin = computed(() => {
  return form.roleKey === 'admin' || form.roleKey === 'staff'
})

// 角色名称
const roleName = computed(() => {
  const roles = { admin: '管理员', staff: '库管员', tenant: '租客' }
  return roles[form.roleKey] || '用户'
})

// 角色标签类型
const roleTagType = computed(() => {
  const types = { admin: 'danger', staff: 'warning', tenant: 'info' }
  return types[form.roleKey] || 'info'
})

// 信用分颜色
const creditColor = computed(() => {
  if (form.creditScore >= 80) return '#4A7C59'
  if (form.creditScore >= 60) return '#C9A227'
  return '#B22222'
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
    
    // 更新store中的用户信息
    userStore.setUserInfo({ ...userStore.userInfo, nickname: form.nickname })
    ElMessage.success('保存成功')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.profile-page {
  min-height: 100vh;
  background: var(--hanfu-paper);
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #2C2416 0%, #3a3228 100%);
  padding: 40px 20px;
}

.header-content {
  max-width: 800px;
  margin: 0 auto;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--hanfu-red) 0%, var(--hanfu-red-dark) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 3px solid rgba(201, 162, 39, 0.5);
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .avatar-text {
    font-size: 32px;
    color: var(--hanfu-paper);
    font-weight: 600;
  }
}

.user-info {
  .nickname {
    color: var(--hanfu-paper);
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 8px;
    letter-spacing: 2px;
  }
}

.header-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  
  .line {
    width: 80px;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(201, 162, 39, 0.5));
    
    &:last-child {
      transform: rotate(180deg);
    }
  }
  
  .text {
    color: var(--hanfu-gold);
    font-size: 14px;
    letter-spacing: 4px;
  }
}

/* 主体内容 */
.profile-content {
  max-width: 800px;
  margin: -20px auto 40px;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.info-card {
  background: var(--hanfu-paper);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-light);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--border-color);
  
  .el-icon {
    color: var(--hanfu-red);
    font-size: 20px;
  }
}

.profile-form {
  :deep(.el-form-item__label) {
    color: var(--text-secondary);
    font-weight: 500;
  }
  
  :deep(.el-input__wrapper) {
    background: var(--hanfu-paper-dark);
  }
  
  :deep(.el-textarea__inner) {
    background: var(--hanfu-paper-dark);
    font-family: 'Noto Serif SC', serif;
  }
}

/* 信用分样式 */
.credit-score {
  display: flex;
  align-items: center;
  gap: 15px;
  
  .score-text {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
  }
}

/* 余额样式 */
.balance {
  display: flex;
  align-items: baseline;
  
  .currency {
    font-size: 14px;
    color: var(--hanfu-red);
    margin-right: 2px;
  }
  
  .amount {
    font-size: 24px;
    font-weight: 600;
    color: var(--hanfu-red);
  }
}

/* 操作栏 */
.action-bar {
  text-align: center;
  padding: 20px 0;
  
  .el-button {
    min-width: 200px;
    height: 48px;
    font-size: 16px;
    letter-spacing: 4px;
  }
}
</style>
