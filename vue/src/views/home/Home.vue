<template>
  <div class="home-container">
    <el-header class="header">
      <div class="logo">汉服租赁平台</div>
      <div class="nav">
        <el-button type="primary" @click="$router.push('/login')">登录</el-button>
        <el-button @click="$router.push('/register')">注册</el-button>
      </div>
    </el-header>
    <el-main class="main">
      <div class="welcome">
        <h1>欢迎来到汉服租赁平台</h1>
        <p>传承国风之美，体验华服魅力</p>
      </div>
    </el-main>
  </div>
</template>

<script setup>
</script>

<style scoped lang="scss">
.home-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.main {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.welcome {
  text-align: center;
  color: #fff;
  
  h1 {
    font-size: 48px;
    margin-bottom: 20px;
  }
  
  p {
    font-size: 20px;
    opacity: 0.9;
  }
}
</style>
