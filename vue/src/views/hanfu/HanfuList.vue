<template>
  <div class="hanfu-list-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1>汉服展示</h1>
        <p class="subtitle">传承华夏之美，品味千年风韵</p>
      </div>
    </div>

    <!-- 分类筛选 -->
    <div class="category-filter">
      <div class="filter-container">
        <span 
          class="filter-item" 
          :class="{ active: !selectedCategory }"
          @click="selectCategory(null)"
        >全部</span>
        <span 
          v-for="cat in categoryList" 
          :key="cat.id"
          class="filter-item"
          :class="{ active: selectedCategory === cat.id }"
          @click="selectCategory(cat.id)"
        >{{ cat.categoryName }}</span>
      </div>
    </div>

    <!-- 汉服列表 -->
    <div class="hanfu-container" v-loading="loading">
      <div class="hanfu-grid" v-if="hanfuList.length > 0">
        <div 
          v-for="item in hanfuList" 
          :key="item.id" 
          class="hanfu-card"
          @click="goToDetail(item.id)"
        >
          <div class="card-image">
            <img :src="item.mainImage" :alt="item.name" />
            <div class="stock-badge" :class="{ 'out-of-stock': item.stockCount === 0 }">
              {{ item.stockCount > 0 ? `库存 ${item.stockCount}` : '暂无库存' }}
            </div>
          </div>
          <div class="card-content">
            <h3 class="card-title">{{ item.name }}</h3>
            <p class="card-category">{{ item.categoryName }}</p>
            <div class="card-price">
              <span class="rent-price">¥{{ item.dailyRent }}/天</span>
              <span class="deposit">押金 ¥{{ item.deposit }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="empty-state" v-else-if="!loading">
        <el-empty description="暂无汉服数据" />
      </div>

      <!-- 分页 -->
      <div class="pagination-wrap" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadData"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCategoryList } from '@/api/category'
import { getHanfuList } from '@/api/hanfu'

const router = useRouter()
const loading = ref(false)
const categoryList = ref([])
const hanfuList = ref([])
const selectedCategory = ref(null)
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

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
    const res = await getHanfuList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      categoryId: selectedCategory.value
    })
    hanfuList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  pageNum.value = 1
  loadData()
}

const goToDetail = (id) => {
  router.push(`/hanfu/${id}`)
}
</script>

<style scoped lang="scss">
.hanfu-list-page {
  min-height: 100vh;
  background: var(--hanfu-paper);
}

.page-header {
  background: linear-gradient(135deg, var(--hanfu-red) 0%, #8B0000 100%);
  padding: 60px 20px;
  text-align: center;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M30 30c0-5.523 4.477-10 10-10s10 4.477 10 10-4.477 10-10 10c0 5.523-4.477 10-10 10s-10-4.477-10-10 4.477-10 10-10zM10 10c0-5.523 4.477-10 10-10s10 4.477 10 10-4.477 10-10 10c0 5.523-4.477 10-10 10S0 25.523 0 20s4.477-10 10-10z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  }
  
  .header-content {
    position: relative;
    z-index: 1;
  }
  
  h1 {
    font-size: 36px;
    color: #fff;
    margin-bottom: 10px;
    letter-spacing: 8px;
    font-weight: 300;
  }
  
  .subtitle {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
    letter-spacing: 4px;
  }
}

.category-filter {
  background: #fff;
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 100;
  
  .filter-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 15px 20px;
    display: flex;
    gap: 20px;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .filter-item {
    padding: 8px 20px;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s;
    color: var(--text-secondary);
    font-size: 14px;
    
    &:hover {
      color: var(--hanfu-red);
    }
    
    &.active {
      background: var(--hanfu-red);
      color: #fff;
    }
  }
}

.hanfu-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.hanfu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 25px;
}

.hanfu-card {
  background: #fff;
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 30px rgba(139, 0, 0, 0.15);
    
    .card-image img {
      transform: scale(1.05);
    }
  }
  
  .card-image {
    position: relative;
    aspect-ratio: 3/4;
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }
    
    .stock-badge {
      position: absolute;
      top: 10px;
      right: 10px;
      background: rgba(0, 128, 0, 0.85);
      color: #fff;
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 12px;
      
      &.out-of-stock {
        background: rgba(150, 150, 150, 0.85);
      }
    }
  }
  
  .card-content {
    padding: 15px;
    
    .card-title {
      font-size: 16px;
      color: var(--text-primary);
      margin-bottom: 5px;
      font-weight: 500;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .card-category {
      font-size: 13px;
      color: var(--text-secondary);
      margin-bottom: 10px;
    }
    
    .card-price {
      display: flex;
      justify-content: space-between;
      align-items: baseline;
      
      .rent-price {
        font-size: 18px;
        color: var(--hanfu-red);
        font-weight: 600;
      }
      
      .deposit {
        font-size: 12px;
        color: var(--text-secondary);
      }
    }
  }
}

.empty-state {
  padding: 80px 0;
  text-align: center;
}

.pagination-wrap {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
</style>
