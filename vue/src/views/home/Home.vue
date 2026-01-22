<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <span class="logo-icon">華</span>
          <span class="logo-text">汉服租赁</span>
        </div>
        <nav class="nav-menu">
          <a href="#" class="nav-item active">首页</a>
          <a href="#" class="nav-item">汉服展示</a>
          <a href="#" class="nav-item">租赁流程</a>
          <a href="#" class="nav-item">关于我们</a>
        </nav>
        <div class="header-actions">
          <template v-if="isLoggedIn">
            <span class="user-name">{{ userInfo.nickname }}</span>
            <el-button type="primary" size="small" @click="goToProfile">个人中心</el-button>
            <el-button size="small" @click="handleLogout">退出</el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="$router.push('/login')">登录</el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <!-- 轮播图区域 -->
    <section class="banner-section">
      <div class="banner-decoration left"></div>
      <div class="banner-decoration right"></div>
      
      <el-carousel 
        v-if="banners.length > 0" 
        height="480px" 
        :interval="5000"
        indicator-position="outside"
        class="banner-carousel"
      >
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="banner-item" :style="{ backgroundImage: `url(${banner.imgUrl})` }">
            <div class="banner-overlay">
              <h2 class="banner-title">{{ banner.title }}</h2>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
      
      <!-- 无轮播图时的占位 -->
      <div v-else class="banner-placeholder">
        <div class="placeholder-content">
          <span class="placeholder-icon">華</span>
          <p>传承华夏之美</p>
        </div>
      </div>
    </section>

    <!-- 公告区域 -->
    <section class="notice-section">
      <div class="section-header">
        <div class="header-line left"></div>
        <h2 class="section-title">
          <el-icon><Bell /></el-icon>
          最新公告
        </h2>
        <div class="header-line right"></div>
      </div>

      <div class="notice-list" v-if="notices.length > 0">
        <div 
          class="notice-item" 
          v-for="(notice, index) in notices" 
          :key="notice.id"
          :style="{ animationDelay: `${index * 0.1}s` }"
          @click="showNoticeDetail(notice)"
        >
          <div class="notice-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="notice-content">
            <h3 class="notice-title">{{ notice.title }}</h3>
            <p class="notice-time">{{ formatTime(notice.createTime) }}</p>
          </div>
          <div class="notice-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <div v-else class="notice-empty">
        <p>暂无公告</p>
      </div>
    </section>

    <!-- 特色服务 -->
    <section class="features-section">
      <div class="section-header">
        <div class="header-line left"></div>
        <h2 class="section-title">我们的服务</h2>
        <div class="header-line right"></div>
      </div>

      <div class="features-grid">
        <div class="feature-card" v-for="(feature, index) in features" :key="index">
          <div class="feature-icon">{{ feature.icon }}</div>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-desc">{{ feature.desc }}</p>
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-brand">
          <span class="footer-logo">華</span>
          <p>汉服租赁平台</p>
          <p class="footer-slogan">传承华夏之美 · 弘扬汉服文化</p>
        </div>
        <div class="footer-links">
          <p>联系我们：13800138000</p>
          <p>地址：浙江省杭州市西湖区汉服文化园</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© 2024 汉服租赁平台 All Rights Reserved</p>
      </div>
    </footer>

    <!-- 公告详情弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="currentNotice?.title"
      width="600px"
      class="notice-dialog"
    >
      <div class="notice-detail">
        <p class="notice-detail-time">发布时间：{{ formatTime(currentNotice?.createTime) }}</p>
        <div class="notice-detail-content">{{ currentNotice?.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Document, ArrowRight } from '@element-plus/icons-vue'
import { getBanners, getNoticeList } from '@/api/notice'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const banners = ref([])
const notices = ref([])
const dialogVisible = ref(false)
const currentNotice = ref(null)

const isLoggedIn = computed(() => !!userStore.token)
const userInfo = computed(() => userStore.userInfo || {})

const features = [
  { icon: '衣', title: '精品汉服', desc: '明制、唐制、宋制等多种形制，款式丰富' },
  { icon: '洗', title: '专业清洁', desc: '每件汉服归还后专业清洗消毒，干净卫生' },
  { icon: '送', title: '快速配送', desc: '全国包邮，快速发货，让您尽快体验汉服之美' },
  { icon: '保', title: '品质保障', desc: '严格品控，确保每件汉服品质如新' }
]

onMounted(async () => {
  await loadBanners()
  await loadNotices()
})

const loadBanners = async () => {
  try {
    const res = await getBanners()
    banners.value = res.data || []
  } catch (error) {
    console.error('加载轮播图失败', error)
  }
}

const loadNotices = async () => {
  try {
    const res = await getNoticeList()
    notices.value = res.data || []
  } catch (error) {
    console.error('加载公告失败', error)
  }
}

const showNoticeDetail = (notice) => {
  currentNotice.value = notice
  dialogVisible.value = true
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

const goToProfile = () => {
  const roleKey = userInfo.value.roleKey
  if (roleKey === 'admin' || roleKey === 'staff') {
    router.push('/admin/dashboard')
  } else {
    router.push('/profile')
  }
}

const handleLogout = () => {
  userStore.clearUserInfo()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
  background: var(--hanfu-paper);
}

/* 顶部导航 */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(44, 36, 22, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(201, 162, 39, 0.3);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .logo-icon {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, var(--hanfu-red) 0%, var(--hanfu-red-dark) 100%);
    color: var(--hanfu-paper);
    font-size: 24px;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
  }
  
  .logo-text {
    color: var(--hanfu-paper);
    font-size: 20px;
    font-weight: 600;
    letter-spacing: 4px;
  }
}

.nav-menu {
  display: flex;
  gap: 40px;
  
  .nav-item {
    color: rgba(250, 246, 237, 0.7);
    font-size: 15px;
    letter-spacing: 2px;
    transition: color 0.3s;
    
    &:hover, &.active {
      color: var(--hanfu-gold);
    }
  }
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
  
  .user-name {
    color: var(--hanfu-gold);
    font-size: 14px;
  }
}

/* 轮播图区域 */
.banner-section {
  padding-top: 70px;
  position: relative;
  background: linear-gradient(180deg, #2C2416 0%, #3a3228 100%);
}

.banner-decoration {
  position: absolute;
  top: 100px;
  width: 150px;
  height: 300px;
  background: radial-gradient(ellipse at center, rgba(201, 162, 39, 0.1) 0%, transparent 70%);
  z-index: 1;
  
  &.left { left: 0; }
  &.right { right: 0; }
}

.banner-carousel {
  :deep(.el-carousel__indicators) {
    .el-carousel__indicator {
      .el-carousel__button {
        width: 30px;
        height: 4px;
        border-radius: 2px;
        background: rgba(250, 246, 237, 0.3);
      }
      
      &.is-active .el-carousel__button {
        background: var(--hanfu-gold);
      }
    }
  }
}

.banner-item {
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px;
  background: linear-gradient(transparent, rgba(44, 36, 22, 0.9));
  
  .banner-title {
    color: var(--hanfu-paper);
    font-size: 28px;
    font-weight: 600;
    letter-spacing: 4px;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
  }
}

.banner-placeholder {
  height: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .placeholder-content {
    text-align: center;
    
    .placeholder-icon {
      display: block;
      font-size: 80px;
      color: var(--hanfu-gold);
      margin-bottom: 20px;
    }
    
    p {
      color: rgba(250, 246, 237, 0.6);
      font-size: 24px;
      letter-spacing: 8px;
    }
  }
}

/* 公告区域 */
.notice-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 40px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 30px;
  margin-bottom: 40px;
  
  .header-line {
    width: 100px;
    height: 1px;
    background: linear-gradient(90deg, transparent, var(--hanfu-gold));
    
    &.right {
      transform: rotate(180deg);
    }
  }
  
  .section-title {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 24px;
    color: var(--text-primary);
    letter-spacing: 4px;
    
    .el-icon {
      color: var(--hanfu-red);
    }
  }
}

.notice-list {
  display: grid;
  gap: 15px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 25px;
  background: var(--hanfu-paper);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.3s ease;
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
  
  &:hover {
    border-color: var(--hanfu-red);
    box-shadow: var(--shadow-medium);
    transform: translateX(5px);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.notice-icon {
  width: 45px;
  height: 45px;
  background: linear-gradient(135deg, var(--hanfu-red) 0%, var(--hanfu-red-dark) 100%);
  color: var(--hanfu-paper);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.notice-content {
  flex: 1;
  
  .notice-title {
    font-size: 16px;
    color: var(--text-primary);
    margin-bottom: 5px;
  }
  
  .notice-time {
    font-size: 13px;
    color: var(--text-secondary);
  }
}

.notice-arrow {
  color: var(--text-placeholder);
  transition: transform 0.3s;
  
  .notice-item:hover & {
    transform: translateX(5px);
    color: var(--hanfu-red);
  }
}

.notice-empty {
  text-align: center;
  padding: 60px;
  color: var(--text-secondary);
}

/* 特色服务 */
.features-section {
  background: var(--hanfu-paper-dark);
  padding: 60px 40px;
}

.features-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.feature-card {
  background: var(--hanfu-paper);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 40px 25px;
  text-align: center;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: var(--shadow-medium);
    border-color: var(--hanfu-gold);
  }
  
  .feature-icon {
    width: 70px;
    height: 70px;
    margin: 0 auto 20px;
    background: linear-gradient(135deg, var(--hanfu-gold) 0%, #8B7019 100%);
    color: var(--hanfu-ink);
    font-size: 32px;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
  }
  
  .feature-title {
    font-size: 18px;
    color: var(--text-primary);
    margin-bottom: 10px;
    letter-spacing: 2px;
  }
  
  .feature-desc {
    font-size: 14px;
    color: var(--text-secondary);
    line-height: 1.6;
  }
}

/* 页脚 */
.footer {
  background: linear-gradient(180deg, #2C2416 0%, #1a1610 100%);
  color: var(--hanfu-paper);
  padding: 50px 40px 20px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 30px;
  border-bottom: 1px solid rgba(201, 162, 39, 0.2);
}

.footer-brand {
  .footer-logo {
    display: inline-block;
    width: 50px;
    height: 50px;
    background: linear-gradient(135deg, var(--hanfu-red) 0%, var(--hanfu-red-dark) 100%);
    color: var(--hanfu-paper);
    font-size: 28px;
    font-weight: 700;
    text-align: center;
    line-height: 50px;
    border-radius: 50%;
    margin-bottom: 15px;
  }
  
  p {
    color: rgba(250, 246, 237, 0.8);
    font-size: 14px;
    margin-bottom: 5px;
  }
  
  .footer-slogan {
    color: var(--hanfu-gold);
    letter-spacing: 2px;
  }
}

.footer-links {
  text-align: right;
  
  p {
    color: rgba(250, 246, 237, 0.6);
    font-size: 14px;
    margin-bottom: 8px;
  }
}

.footer-bottom {
  max-width: 1200px;
  margin: 0 auto;
  padding-top: 20px;
  text-align: center;
  
  p {
    color: rgba(250, 246, 237, 0.4);
    font-size: 13px;
  }
}

/* 公告弹窗 */
.notice-dialog {
  :deep(.el-dialog__header) {
    border-bottom: 1px solid var(--border-color);
    padding-bottom: 15px;
  }
  
  :deep(.el-dialog__title) {
    font-size: 18px;
    color: var(--text-primary);
  }
}

.notice-detail {
  .notice-detail-time {
    font-size: 13px;
    color: var(--text-secondary);
    margin-bottom: 20px;
  }
  
  .notice-detail-content {
    font-size: 15px;
    line-height: 1.8;
    color: var(--text-primary);
  }
}

/* 响应式 */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .nav-menu {
    display: none;
  }
}

@media (max-width: 768px) {
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .footer-content {
    flex-direction: column;
    text-align: center;
    gap: 30px;
  }
  
  .footer-links {
    text-align: center;
  }
}
</style>
