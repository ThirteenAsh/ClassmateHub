<template>
  <div class="header-nav">
    <div class="header-container">
      <div class="header-left">
        <h2 class="app-name">ClassmateHub</h2>
      </div>

      <div class="header-right" v-if="authStore.isAuthenticated">
        <div class="user-info">
          <el-icon :size="24" class="user-icon"><User /></el-icon>
          <span class="username">{{ authStore.username }}</span>
        </div>
        <button class="logout-btn" @click="handleLogout">
          登出
        </button>
      </div>

      <div class="header-right" v-else>
        <router-link to="/login" class="login-link">登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = async () => {
  try {
    await authStore.logout()
    ElMessage.success('登出成功')
    router.push('/login')
  } catch (error) {
    ElMessage.error('登出失败')
  }
}
</script>

<style scoped>
.header-nav {
  position: sticky;
  top: 0;
  background: #ffffff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  z-index: 99;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
}

.app-name {
  font-size: 1.25rem;
  font-weight: 800;
  color: #ff758f;
  margin: 0;
  letter-spacing: 1px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #fff0f3;
  border-radius: 20px;
  color: #ff758f;
  font-weight: 600;
}

.user-icon {
  color: #ff8fab;
}

.username {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.logout-btn {
  padding: 0.6rem 1.2rem;
  background: linear-gradient(135deg, #ff758f 0%, #ffc8dd 100%);
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  font-size: 0.9rem;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 117, 143, 0.3);
}

.login-link {
  padding: 0.6rem 1.2rem;
  background: linear-gradient(135deg, #72efdd 0%, #bde0fe 100%);
  color: #ffffff;
  border-radius: 8px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  font-size: 0.9rem;
  display: inline-block;
}

.login-link:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(114, 239, 221, 0.3);
}

@media (max-width: 768px) {
  .header-container {
    padding: 0.75rem 1rem;
  }

  .app-name {
    font-size: 1rem;
  }

  .user-info {
    gap: 0.3rem;
    padding: 0.4rem 0.8rem;
  }

  .username {
    max-width: 80px;
    font-size: 0.85rem;
  }

  .logout-btn,
  .login-link {
    padding: 0.5rem 1rem;
    font-size: 0.8rem;
  }
}
</style>
