<template>
  <div class="hanfu-detail-page" v-loading="loading">
    <div class="detail-container" v-if="hanfu">
      <!-- 返回按钮 -->
      <div class="back-bar">
        <el-button text @click="goBack">
          <el-icon><ArrowLeft /></el-icon> 返回列表
        </el-button>
      </div>

      <!-- 商品信息 -->
      <div class="product-info">
        <div class="image-section">
          <div class="main-image">
            <el-image :src="currentImage" fit="contain" :preview-src-list="allImages" />
          </div>
          <div class="image-list" v-if="allImages.length > 1">
            <div 
              v-for="(img, index) in allImages" 
              :key="index"
              class="thumb-item"
              :class="{ active: currentImage === img }"
              @click="currentImage = img"
            >
              <img :src="img" :alt="`图片${index + 1}`" />
            </div>
          </div>
        </div>

        <div class="info-section">
          <h1 class="product-name">{{ hanfu.name }}</h1>
          <p class="product-category">{{ hanfu.categoryName }}</p>
          
          <div class="price-box">
            <div class="price-item">
              <span class="label">日租金</span>
              <span class="value">¥{{ hanfu.dailyRent }}</span>
            </div>
            <div class="price-item">
              <span class="label">押金</span>
              <span class="value deposit">¥{{ hanfu.deposit }}</span>
            </div>
          </div>

          <div class="stock-info">
            <span class="label">库存状态：</span>
            <el-tag :type="hanfu.stockCount > 0 ? 'success' : 'danger'" size="large">
              {{ hanfu.stockCount > 0 ? `可租 ${hanfu.stockCount} 件` : '暂无库存' }}
            </el-tag>
          </div>

          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="large" 
              :disabled="hanfu.stockCount === 0"
              @click="handleRent"
            >
              立即租赁
            </el-button>
            <el-button size="large" @click="handleFavorite">
              <el-icon><Star /></el-icon> 收藏
            </el-button>
          </div>

          <div class="tips">
            <h4>租赁须知</h4>
            <ul>
              <li>租赁周期最短1天，最长30天</li>
              <li>押金在归还验收合格后7个工作日内退还</li>
              <li>如有损坏需按照损坏程度赔偿</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 商品详情 -->
      <div class="detail-section">
        <h2 class="section-title">商品详情</h2>
        <div class="detail-content">
          <p v-if="hanfu.detailContent">{{ hanfu.detailContent }}</p>
          <p v-else class="empty-detail">暂无详情描述</p>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else-if="!loading">
      <el-empty description="汉服不存在或已下架">
        <el-button type="primary" @click="goBack">返回列表</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Star } from '@element-plus/icons-vue'
import { getHanfuDetail } from '@/api/hanfu'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const hanfu = ref(null)
const currentImage = ref('')

const allImages = computed(() => {
  if (!hanfu.value) return []
  const images = [hanfu.value.mainImage]
  if (hanfu.value.subImages) {
    images.push(...hanfu.value.subImages.split(',').filter(Boolean))
  }
  return images
})

onMounted(() => {
  loadDetail()
})

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getHanfuDetail(route.params.id)
    hanfu.value = res.data
    if (hanfu.value) {
      currentImage.value = hanfu.value.mainImage
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/hanfu')
}

const handleRent = () => {
  ElMessage.info('租赁功能将在模块四中实现')
}

const handleFavorite = () => {
  ElMessage.success('已添加到收藏')
}
</script>

<style scoped lang="scss">
.hanfu-detail-page {
  min-height: 100vh;
  background: var(--hanfu-paper);
  padding: 20px;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
}

.back-bar {
  margin-bottom: 20px;
}

.product-info {
  display: flex;
  gap: 40px;
  background: #fff;
  padding: 30px;
  border-radius: var(--radius-lg);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  
  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.image-section {
  flex: 0 0 450px;
  
  @media (max-width: 768px) {
    flex: none;
  }
  
  .main-image {
    aspect-ratio: 3/4;
    border-radius: var(--radius-md);
    overflow: hidden;
    background: #f5f5f5;
    
    :deep(.el-image) {
      width: 100%;
      height: 100%;
    }
  }
  
  .image-list {
    display: flex;
    gap: 10px;
    margin-top: 15px;
    overflow-x: auto;
    padding: 5px 0;
    
    .thumb-item {
      flex: 0 0 70px;
      height: 70px;
      border-radius: 6px;
      overflow: hidden;
      cursor: pointer;
      border: 2px solid transparent;
      transition: border-color 0.3s;
      
      &.active {
        border-color: var(--hanfu-red);
      }
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}

.info-section {
  flex: 1;
  
  .product-name {
    font-size: 26px;
    color: var(--text-primary);
    margin-bottom: 8px;
    font-weight: 500;
    letter-spacing: 2px;
  }
  
  .product-category {
    font-size: 14px;
    color: var(--text-secondary);
    margin-bottom: 25px;
  }
  
  .price-box {
    background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
    border: 1px solid rgba(139, 0, 0, 0.1);
    border-radius: var(--radius-md);
    padding: 20px;
    display: flex;
    gap: 40px;
    margin-bottom: 20px;
    
    .price-item {
      .label {
        display: block;
        font-size: 13px;
        color: var(--text-secondary);
        margin-bottom: 5px;
      }
      
      .value {
        font-size: 28px;
        color: var(--hanfu-red);
        font-weight: 600;
        
        &.deposit {
          font-size: 20px;
          color: var(--text-primary);
        }
      }
    }
  }
  
  .stock-info {
    margin-bottom: 25px;
    
    .label {
      color: var(--text-secondary);
      margin-right: 10px;
    }
  }
  
  .action-buttons {
    display: flex;
    gap: 15px;
    margin-bottom: 30px;
    
    .el-button {
      min-width: 140px;
    }
  }
  
  .tips {
    background: #fafafa;
    padding: 15px 20px;
    border-radius: var(--radius-md);
    
    h4 {
      font-size: 14px;
      color: var(--text-primary);
      margin-bottom: 10px;
    }
    
    ul {
      margin: 0;
      padding-left: 18px;
      
      li {
        font-size: 13px;
        color: var(--text-secondary);
        line-height: 1.8;
      }
    }
  }
}

.detail-section {
  background: #fff;
  padding: 30px;
  border-radius: var(--radius-lg);
  margin-top: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  
  .section-title {
    font-size: 18px;
    color: var(--text-primary);
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid var(--border-color);
    letter-spacing: 2px;
  }
  
  .detail-content {
    line-height: 1.8;
    color: var(--text-secondary);
    white-space: pre-wrap;
    
    .empty-detail {
      text-align: center;
      padding: 40px;
      color: #ccc;
    }
  }
}

.empty-state {
  padding: 100px 0;
  text-align: center;
}
</style>
