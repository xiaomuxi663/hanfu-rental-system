<template>
  <div class="admin-login-page">
    <!-- 背景装饰 -->
    <div class="bg-pattern"></div>
    
    <!-- 登录卡片 -->
    <div class="admin-login-card">
      <!-- 顶部金色边框 -->
      <div class="top-border"></div>
      
      <!-- 头部 -->
      <div class="card-header">
        <div class="badge">
          <el-icon><Setting /></el-icon>
        </div>
        <h1 class="title">管理后台</h1>
        <p class="subtitle">ADMIN CONSOLE</p>
      </div>

      <!-- 表单区域 -->
      <div class="card-body">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <div class="input-group">
              <label>账号</label>
              <el-input 
                v-model="form.username" 
                placeholder="请输入管理员账号"
                size="large"
                clearable
                :maxlength="50"
              />
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
            <div class="input-group">
              <label>密码</label>
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码"
                size="large"
                :maxlength="50"
                show-password
                @keyup.enter="handleLogin"
              />
            </div>
          </el-form-item>

          <el-button 
            type="primary" 
            class="login-btn" 
            @click="handleLogin" 
            :loading="loading"
          >
            <span>进入管理</span>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </el-button>
        </el-form>

        <div class="back-link" @click="$router.push('/login')">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回用户登录</span>
        </div>
      </div>

      <!-- 底部装饰 -->
      <div class="bottom-decoration">
        <span class="line"></span>
        <span class="text">HANFU RENTAL SYSTEM</span>
        <span class="line"></span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Setting, ArrowRight, ArrowLeft } from '@element-plus/icons-vue'
import { login } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    const roleKey = res.data.user.roleKey
    
    // 验证是否为管理员或库管员
    if (roleKey !== 'admin' && roleKey !== 'staff') {
      ElMessage.error('该账号无管理权限')
      return
    }
    
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.user)
    ElMessage.success('欢迎回来，' + res.data.user.nickname)
    router.push('/admin/dashboard')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.admin-login-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #2C2416 0%, #1a1610 100%);
  position: relative;
  overflow: hidden;
}

/* 背景纹样 */
.bg-pattern {
  position: absolute;
  width: 100%;
  height: 100%;
  opacity: 0.03;
  background-image: 
    radial-gradient(circle at 25% 25%, #C9A227 1px, transparent 1px),
    radial-gradient(circle at 75% 75%, #C9A227 1px, transparent 1px);
  background-size: 60px 60px;
}

/* 登录卡片 */
.admin-login-card {
  width: 440px;
  background: linear-gradient(180deg, #3a3228 0%, #2C2416 100%);
  border: 1px solid rgba(201, 162, 39, 0.3);
  position: relative;
  z-index: 1;
}

.top-border {
  height: 3px;
  background: linear-gradient(90deg, transparent, #C9A227 30%, #E8D48B 50%, #C9A227 70%, transparent);
}

/* 头部 */
.card-header {
  padding: 40px 30px 30px;
  text-align: center;
  border-bottom: 1px solid rgba(201, 162, 39, 0.15);
}

.badge {
  width: 60px;
  height: 60px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #C9A227 0%, #8B7019 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(201, 162, 39, 0.3);
  
  .el-icon {
    font-size: 28px;
    color: #2C2416;
  }
}

.title {
  font-size: 26px;
  font-weight: 600;
  color: #FAF6ED;
  letter-spacing: 6px;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 11px;
  color: #9E8E73;
  letter-spacing: 4px;
  text-transform: uppercase;
}

/* 表单区域 */
.card-body {
  padding: 35px 40px 30px;
}

.input-group {
  label {
    display: block;
    font-size: 12px;
    color: #9E8E73;
    margin-bottom: 8px;
    letter-spacing: 2px;
  }
}

:deep(.el-input) {
  width: 100%;
}

:deep(.el-input__wrapper) {
  background: rgba(250, 246, 237, 0.95) !important;
  border: 1px solid rgba(201, 162, 39, 0.3);
  box-shadow: none;
  width: 100%;
  
  &:hover, &.is-focus {
    border-color: #C9A227;
    box-shadow: 0 0 0 2px rgba(201, 162, 39, 0.15);
  }
}

:deep(.el-input__inner) {
  color: #2C2416 !important;
  font-family: 'Noto Serif SC', serif;
  caret-color: #2C2416;
  
  &::placeholder {
    color: #9E8E73;
  }
}

:deep(.el-input__suffix) {
  color: #9E8E73;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-btn {
  width: 100%;
  height: 50px;
  margin-top: 10px;
  background: linear-gradient(135deg, #C9A227 0%, #8B7019 100%);
  border: none;
  font-size: 15px;
  letter-spacing: 4px;
  color: #2C2416;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(201, 162, 39, 0.35);
    background: linear-gradient(135deg, #D4AF37 0%, #9E8319 100%);
  }
  
  .arrow {
    transition: transform 0.3s ease;
  }
  
  &:hover .arrow {
    transform: translateX(4px);
  }
}

.back-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 25px;
  color: #6B5E4A;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.3s ease;
  
  &:hover {
    color: #C9A227;
  }
}

/* 底部装饰 */
.bottom-decoration {
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  
  .line {
    width: 50px;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(201, 162, 39, 0.3));
    
    &:last-child {
      transform: rotate(180deg);
    }
  }
  
  .text {
    font-size: 10px;
    color: #5C4A32;
    letter-spacing: 2px;
  }
}
</style>
