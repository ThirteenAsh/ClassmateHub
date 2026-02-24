<template>
  <div class="home">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">同学录管理系统</h1>
        <p class="hero-subtitle">ClassmateHub - 连接你与同窗好友的桥梁</p>
      </div>
    </div>
    
    <div class="features-section">
      <div class="feature-card" @click="$router.push('/classes')">
        <div class="feature-icon">
          <el-icon :size="48"><School /></el-icon>
        </div>
        <h3 class="feature-title">班级管理</h3>
        <p class="feature-desc">创建和管理班级信息</p>
        <div class="feature-arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
      
      <div class="feature-card" @click="$router.push('/students')">
        <div class="feature-icon">
          <el-icon :size="48"><User /></el-icon>
        </div>
        <h3 class="feature-title">学生信息</h3>
        <p class="feature-desc">查看和管理学生档案</p>
        <div class="feature-arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </div>
    
    <div class="info-section">
      <div class="info-card">
        <div class="info-icon">
          <el-icon :size="32"><Document /></el-icon>
        </div>
        <div class="info-content">
          <h4>快速开始</h4>
          <p>点击上方卡片即可进入对应的管理页面</p>
        </div>
      </div>
      
      <div class="info-card">
        <div class="info-icon">
          <el-icon :size="32"><CircleCheck /></el-icon>
        </div>
        <div class="info-content">
          <h4>完整功能</h4>
          <p>支持班级和学生的增删改查操作</p>
        </div>
      </div>
      
      <div class="info-card">
        <div class="info-icon">
          <el-icon :size="32"><Star /></el-icon>
        </div>
        <div class="info-content">
          <h4>简洁设计</h4>
          <p>界面简洁，操作便捷，体验流畅</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 图标已在模板中通过 el-icon 组件使用，无需显式导入
</script>

<style scoped>
.home {
  min-height: calc(100vh - 80px);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
  overflow-x: hidden;
}

.hero-section {
  padding: 4rem 2rem;
  text-align: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 1rem 0;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  letter-spacing: 2px;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  font-weight: 300;
}

.features-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  padding: 4rem 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 2.5rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transform: scaleX(0);
  transition: transform 0.4s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.feature-card:hover::before {
  transform: scaleX(1);
}

.feature-icon {
  margin-bottom: 1.5rem;
  color: #667eea;
  transition: transform 0.4s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1);
}

.feature-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 0.75rem 0;
}

.feature-desc {
  font-size: 1rem;
  color: #718096;
  margin: 0;
  line-height: 1.6;
}

.feature-arrow {
  margin-top: 1.5rem;
  color: #667eea;
  opacity: 0;
  transform: translateX(-10px);
  transition: all 0.3s ease;
}

.feature-card:hover .feature-arrow {
  opacity: 1;
  transform: translateX(0);
}

.info-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  padding: 0 2rem 4rem 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.info-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.info-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.info-icon {
  flex-shrink: 0;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.info-content h4 {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 0.25rem 0;
}

.info-content p {
  font-size: 0.9rem;
  color: #718096;
  margin: 0;
  line-height: 1.4;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .features-section {
    grid-template-columns: 1fr;
    padding: 2rem 1rem;
  }
  
  .info-section {
    grid-template-columns: 1fr;
    padding: 0 1rem 2rem 1rem;
  }
}
</style>
