<template>
  <div class="square">
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>

    <div class="header-section">
      <div class="header-content">
        <h1 class="page-title">广场</h1>
        <p class="page-subtitle">Square - 发现更多精彩内容</p>
      </div>
    </div>

    <div class="placeholder-section">
      <div class="placeholder-card">
        <div class="placeholder-icon">
          <el-icon :size="60"><ChatDotRound /></el-icon>
        </div>
        <h3 class="placeholder-title">广场功能开发中</h3>
        <p class="placeholder-desc">更多社交功能即将上线，敬请期待</p>
      </div>
    </div>

    <BottomNav v-model:activeTab="activeTab" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ChatDotRound } from '@element-plus/icons-vue'
// @ts-ignore
import BottomNav from '../components/BottomNav.vue'

const activeTab = ref('square')
</script>

<style scoped>
* {
  font-family: 'Nunito', 'Quicksand', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.square {
  min-height: 100vh;
  background-color: #fdfbfb;
  background-image: 
    linear-gradient(rgba(255, 182, 193, 0.4) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 182, 193, 0.4) 1px, transparent 1px);
  background-size: 30px 30px;
  background-attachment: fixed;
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

.header-section {
  padding: 4rem 2rem 2rem;
  text-align: center;
}

.header-content {
  max-width: 800px;
  margin: 0 auto;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 800;
  color: #ff758f;
  margin: 0 0 1rem 0;
  text-shadow: 
    3px 3px 0px #fff,
    6px 6px 0px rgba(255, 117, 143, 0.2);
  letter-spacing: 2px;
}

.page-subtitle {
  display: inline-block;
  font-size: 1rem;
  color: #6c757d;
  font-weight: 600;
  background: #fff;
  padding: 8px 20px;
  border-radius: 50px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  border: 2px dashed #ffc8dd;
}

.placeholder-section {
  padding: 2rem 2rem 4rem;
  max-width: 600px;
  margin: 0 auto;
}

.placeholder-card {
  background: #ffffff;
  border-radius: 32px;
  padding: 3rem 2rem;
  text-align: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.03);
  border: 2px dashed #ffc8dd;
}

.placeholder-icon {
  width: 100px;
  height: 100px;
  margin: 0 auto 1.5rem;
  border-radius: 50%;
  background: #fff0f3;
  color: #ff8fab;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #4a4e69;
  margin: 0 0 0.75rem 0;
}

.placeholder-desc {
  font-size: 1rem;
  color: #9fa0b3;
  margin: 0;
  font-weight: 500;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
  
  .page-subtitle {
    font-size: 0.9rem;
    padding: 6px 16px;
  }

  .placeholder-card {
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
