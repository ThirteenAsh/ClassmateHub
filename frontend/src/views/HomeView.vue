<template>
  <div class="home">
    <el-row :gutter="20" class="welcome-section">
      <el-col :span="24">
        <div class="welcome-card">
          <h1>欢迎来到同学录管理系统</h1>
          <p class="subtitle">ClassmateHub - 连接你与同窗好友的桥梁</p>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="features-section">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card" @click="$router.push('/classes')">
          <div class="feature-icon">
            <i class="el-icon-user-group"></i>
          </div>
          <h3>班级管理</h3>
          <p>创建和管理班级信息</p>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card" @click="$router.push('/students')">
          <div class="feature-icon">
            <i class="el-icon-user"></i>
          </div>
          <h3>学生信息</h3>
          <p>查看和管理学生档案</p>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card" @click="$router.push('/profile')">
          <div class="feature-icon">
            <i class="el-icon-setting"></i>
          </div>
          <h3>个人中心</h3>
          <p>管理个人信息和设置</p>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="feature-card">
          <div class="feature-icon">
            <i class="el-icon-search"></i>
          </div>
          <h3>快速搜索</h3>
          <p>查找同学和班级信息</p>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="stats-section">
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">12</div>
            <div class="stat-label">班级数量</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">342</div>
            <div class="stat-label">学生总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">28</div>
            <div class="stat-label">在线用户</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('Home view mounted')
})
</script>

<style scoped>
.home {
  padding: 2rem;
  background-color: #f5f7fa;
  min-height: calc(100vh - 80px);
}

.welcome-section {
  margin-bottom: 2rem;
}

.welcome-card {
  text-align: center;
  padding: 3rem 2rem;
  background: linear-gradient(135deg, #74b9ff, #0984e3);
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.welcome-card h1 {
  margin: 0 0 1rem 0;
  font-size: 2.5rem;
}

.welcome-card .subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
  margin: 0;
}

.features-section {
  margin-bottom: 2rem;
}

.feature-card {
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: 180px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  color: #409eff;
}

.feature-card h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.2rem;
  color: #303133;
}

.feature-card p {
  margin: 0;
  color: #909399;
  font-size: 0.9rem;
}

.stats-section {
  margin-top: 2rem;
}

.stat-card {
  text-align: center;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  text-align: center;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 0.9rem;
  color: #909399;
  margin-top: 0.5rem;
}

@media (max-width: 768px) {
  .welcome-card h1 {
    font-size: 2rem;
  }
  
  .feature-card {
    margin-bottom: 1rem;
  }
}
</style>