<template>
  <div class="login-page">
    <!-- 装饰背景 -->
    <div class="bg-decoration">
      <div class="cloud cloud-1"></div>
      <div class="cloud cloud-2"></div>
      <div class="cloud cloud-3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 顶部装饰 -->
      <div class="card-header">
        <div class="ornament left"></div>
        <div class="logo-section">
          <div class="logo-icon">華</div>
          <h1 class="title">汉服租赁</h1>
          <p class="subtitle">· 用户登录 ·</p>
        </div>
        <div class="ornament right"></div>
      </div>

      <!-- 登录表单 -->
      <div class="card-body">
        <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
          <el-form-item prop="username">
            <el-input 
              v-model="form.username" 
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              v-model="form.password" 
              type="password" 
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button 
              type="primary" 
              class="login-btn" 
              @click="handleLogin" 
              :loading="loading"
              size="large"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 底部链接 -->
        <div class="card-footer">
          <div class="register-link">
            <span>还没有账号？</span>
            <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
          </div>
          <div class="divider">
            <span>其他入口</span>
          </div>
          <div class="admin-entry" @click="$router.push('/admin-login')">
            <el-icon><Setting /></el-icon>
            <span>管理员登录</span>
          </div>
        </div>
      </div>

      <!-- 底部装饰线 -->
      <div class="bottom-decoration"></div>
    </div>

    <!-- 版权信息 -->
    <div class="copyright">
      汉服租赁平台 · 传承华夏之美
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Setting } from '@element-plus/icons-vue'
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
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.user)
    ElMessage.success('登录成功')
    
    const roleKey = res.data.user.roleKey
    if (roleKey === 'admin' || roleKey === 'staff') {
      router.push('/admin/dashboard')
    } else {
      router.push('/home')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #FAF6ED 0%, #F0E9DB 50%, #E8E4DC 100%);
  position: relative;
  overflow: hidden;
}

/* 装饰云纹 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.cloud {
  position: absolute;
  width: 200px;
  height: 100px;
  background: radial-gradient(ellipse at center, rgba(201, 162, 39, 0.08) 0%, transparent 70%);
  border-radius: 50%;
}

.cloud-1 { top: 10%; left: 5%; width: 300px; height: 150px; animation: float 20s ease-in-out infinite; }
.cloud-2 { top: 60%; right: 10%; width: 250px; height: 120px; animation: float 15s ease-in-out infinite reverse; }
.cloud-3 { bottom: 20%; left: 15%; width: 180px; height: 90px; animation: float 18s ease-in-out infinite; }

@keyframes float {
  0%, 100% { transform: translateY(0) translateX(0); }
  50% { transform: translateY(-20px) translateX(10px); }
}

/* 登录卡片 */
.login-card {
  width: 420px;
  background: rgba(250, 246, 237, 0.95);
  border: 1px solid #D4C8B5;
  box-shadow: 0 20px 60px rgba(44, 36, 22, 0.15);
  position: relative;
  z-index: 1;
}

/* 卡片头部 */
.card-header {
  padding: 40px 30px 20px;
  text-align: center;
  position: relative;
  border-bottom: 1px solid #E8E4DC;
}

.ornament {
  position: absolute;
  top: 20px;
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, transparent, #C9A227, transparent);
  
  &.left { left: 30px; }
  &.right { right: 30px; }
  
  &::before, &::after {
    content: '◆';
    position: absolute;
    font-size: 8px;
    color: #C9A227;
    top: -4px;
  }
  
  &.left::before { left: 0; }
  &.left::after { right: 0; }
  &.right::before { left: 0; }
  &.right::after { right: 0; }
}

.logo-section {
  .logo-icon {
    width: 70px;
    height: 70px;
    margin: 0 auto 15px;
    background: linear-gradient(135deg, #B22222 0%, #8B0000 100%);
    color: #FAF6ED;
    font-size: 36px;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    box-shadow: 0 4px 15px rgba(178, 34, 34, 0.3);
  }
  
  .title {
    font-size: 28px;
    font-weight: 600;
    color: #2C2416;
    letter-spacing: 8px;
    margin-bottom: 8px;
  }
  
  .subtitle {
    font-size: 14px;
    color: #9E8E73;
    letter-spacing: 4px;
  }
}

/* 卡片主体 */
.card-body {
  padding: 30px 40px;
}

.login-form {
  :deep(.el-input__wrapper) {
    background: #FDFCF9;
    border: 1px solid #D4C8B5;
    box-shadow: none;
    transition: all 0.3s ease;
    
    &:hover, &.is-focus {
      border-color: #B22222;
      box-shadow: 0 0 0 2px rgba(178, 34, 34, 0.1);
    }
  }
  
  :deep(.el-input__inner) {
    font-family: 'Noto Serif SC', serif;
    color: #2C2416;
    
    &::placeholder {
      color: #9E8E73;
    }
  }
  
  :deep(.el-input__prefix) {
    color: #9E8E73;
  }
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  letter-spacing: 8px;
  background: linear-gradient(135deg, #B22222 0%, #8B0000 100%);
  border: none;
  border-radius: 4px;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(178, 34, 34, 0.35);
  }
}

/* 卡片底部 */
.card-footer {
  margin-top: 20px;
  text-align: center;
}

.register-link {
  font-size: 14px;
  color: #5C4A32;
  
  :deep(.el-link) {
    font-weight: 500;
    margin-left: 5px;
    --el-link-text-color: #B22222;
    --el-link-hover-text-color: #D4574E;
  }
}

.divider {
  margin: 20px 0;
  position: relative;
  
  &::before, &::after {
    content: '';
    position: absolute;
    top: 50%;
    width: 80px;
    height: 1px;
    background: linear-gradient(90deg, transparent, #D4C8B5);
  }
  
  &::before { left: 40px; }
  &::after { right: 40px; transform: rotate(180deg); }
  
  span {
    font-size: 12px;
    color: #9E8E73;
    background: #FAF6ED;
    padding: 0 15px;
  }
}

.admin-entry {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  background: transparent;
  border: 1px solid #D4C8B5;
  color: #5C4A32;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: #C9A227;
    color: #C9A227;
    background: rgba(201, 162, 39, 0.05);
  }
  
  .el-icon {
    font-size: 14px;
  }
}

/* 底部装饰 */
.bottom-decoration {
  height: 4px;
  background: linear-gradient(90deg, transparent, #B22222 20%, #C9A227 50%, #B22222 80%, transparent);
}

/* 版权信息 */
.copyright {
  margin-top: 30px;
  font-size: 12px;
  color: #9E8E73;
  letter-spacing: 2px;
}
</style>
