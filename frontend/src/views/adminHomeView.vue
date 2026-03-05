<template>
  <div class="home">
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>

    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">同学录管理系统</h1>
        <p class="hero-subtitle">ClassmateHub - 连接你与同窗好友的桥梁</p>
      </div>
    </div>
    
    <div class="features-section">
      <div class="feature-card card-pink" @click="$router.push('/classes')">
        <div class="feature-icon">
          <el-icon :size="48"><School /></el-icon>
        </div>
        <h3 class="feature-title">班级管理</h3>
        <p class="feature-desc">创建和管理班级信息</p>
        <div class="feature-arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
      
      <div class="feature-card card-blue" @click="$router.push('/students')">
        <div class="feature-icon">
          <el-icon :size="48"><User /></el-icon>
        </div>
        <h3 class="feature-title">学生信息</h3>
        <p class="feature-desc">查看和管理学生档案</p>
        <div class="feature-arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
      
      <div class="feature-card card-purple" @click="$router.push('/statistics')">
        <div class="feature-icon">
          <el-icon :size="48"><DataAnalysis /></el-icon>
        </div>
        <h3 class="feature-title">数据统计</h3>
        <p class="feature-desc">查看各类数据统计</p>
        <div class="feature-arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </div>
    
    <BottomNav v-model:activeTab="activeTab" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
// @ts-ignore
import BottomNav from '../components/BottomNav.vue'

const activeTab = ref('square')
</script>

<style scoped>
* {
  font-family: 'Nunito', 'Quicksand', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.home {
  min-height: calc(100vh - 80px);
  background-color: #fdfbfb;
  background-image: 
    linear-gradient(rgba(255, 182, 193, 0.4) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 182, 193, 0.4) 1px, transparent 1px);
  background-size: 30px 30px;
  padding: 0;
  overflow-x: hidden;
  position: relative;
  z-index: 1;
  padding-bottom: 100px;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  z-index: -1;
  filter: blur(40px);
  opacity: 0.6;
  animation: float 6s ease-in-out infinite;
}
.circle-1 {
  width: 300px;
  height: 300px;
  background: #ffc8dd;
  top: -50px;
  left: -100px;
}
.circle-2 {
  width: 250px;
  height: 250px;
  background: #bde0fe;
  bottom: 10%;
  right: -50px;
  animation-delay: -3s;
}

.hero-section {
  padding: 5rem 2rem 3rem;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3.2rem;
  font-weight: 800;
  color: #ff758f;
  margin: 0 0 1.5rem 0;
  text-shadow: 
    3px 3px 0px #fff,
    6px 6px 0px rgba(255, 117, 143, 0.2);
  letter-spacing: 3px;
  animation: popIn 0.8s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.hero-subtitle {
  display: inline-block;
  font-size: 1.1rem;
  color: #6c757d;
  font-weight: 600;
  background: #fff;
  padding: 10px 24px;
  border-radius: 50px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  border: 2px dashed #ffc8dd;
}

.features-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  padding: 2rem 2rem 4rem;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  background: #ffffff;
  border-radius: 40px;
  padding: 2.5rem 1.5rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 12px 0px rgba(0, 0, 0, 0.04);
  position: relative;
  border: 4px solid #fff;
}

.feature-card:hover {
  transform: translateY(-12px) scale(1.02);
}

.card-pink {
  border-color: #ffc8dd;
}
.card-pink:hover {
  box-shadow: 0 16px 0px #ffc8dd, 0 20px 30px rgba(255, 200, 221, 0.4);
}

.card-blue {
  border-color: #bde0fe;
}
.card-blue:hover {
  box-shadow: 0 16px 0px #bde0fe, 0 20px 30px rgba(189, 224, 254, 0.4);
}

.card-purple {
  border-color: #e0c3fc;
}
.card-purple:hover {
  box-shadow: 0 16px 0px #e0c3fc, 0 20px 30px rgba(224, 195, 252, 0.4);
}

.feature-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 30px;
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.card-pink .feature-icon {
  background: #fff0f3;
  color: #ff8fab;
}
.card-blue .feature-icon {
  background: #eef8ff;
  color: #72efdd;
}
.card-purple .feature-icon {
  background: #f3e8ff;
  color: #c084fc;
}

.feature-card:hover .feature-icon {
  transform: scale(1.15) rotate(-5deg);
}

.feature-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #4a4e69;
  margin: 0 0 0.75rem 0;
}

.feature-desc {
  font-size: 0.95rem;
  color: #9fa0b3;
  margin: 0;
  font-weight: 500;
}

.feature-arrow {
  position: absolute;
  bottom: 1.5rem;
  right: 1.5rem;
  background: #fff;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  opacity: 0;
  transform: scale(0.5);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.card-pink .feature-arrow { color: #ff8fab; }
.card-blue .feature-arrow { color: #72efdd; }
.card-purple .feature-arrow { color: #c084fc; }

.feature-card:hover .feature-arrow {
  opacity: 1;
  transform: scale(1);
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@keyframes popIn {
  0% { transform: scale(0.8); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.2rem;
    text-shadow: 2px 2px 0px #fff, 4px 4px 0px rgba(255, 117, 143, 0.2);
  }
  
  .hero-subtitle {
    font-size: 0.95rem;
    padding: 8px 16px;
  }
  
  .features-section {
    grid-template-columns: 1fr;
    padding: 1rem 1.5rem 2rem;
  }
  
  .feature-card {
    padding: 2rem 1.5rem;
  }

  .bottom-nav {
    padding: 8px 4px;
  }

  .nav-item {
    padding: 6px 8px;
    min-width: 50px;
  }

  .nav-item span {
    font-size: 0.7rem;
  }
}
</style>
