<template>
  <div class="login-page">
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>

    <div class="login-container">
      <!-- 登录成功加载动画 -->
      <div v-if="isSuccessLoading" class="success-loading-overlay">
        <div class="success-spinner">
          <el-icon class="check-icon"><SuccessFilled /></el-icon>
        </div>
      </div>
      
      <div class="login-card">
        <div class="logo-section">
          <h1 class="app-title">ClassmateHub</h1>
          <p class="app-subtitle">同学录，连真情</p>
        </div>

        <div class="form-tabs">
          <button 
            class="tab-btn" 
            :class="{ active: isLogin }"
            @click="isLogin = true"
          >
            登录
          </button>
          <button 
            class="tab-btn" 
            :class="{ active: !isLogin }"
            @click="isLogin = false"
          >
            注册
          </button>
        </div>

        <!-- 登录表单 -->
        <form v-if="isLogin" @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label>用户名</label>
            <input 
              v-model="formData.username" 
              type="text" 
              placeholder="请输入用户名"
              required
            >
          </div>

          <div class="form-group">
            <label>密码</label>
            <input 
              v-model="formData.password" 
              type="password" 
              placeholder="请输入密码"
              required
            >
          </div>

          <button type="submit" class="submit-btn" :disabled="isLoading">
            <span v-if="!isLoading">登录</span>
            <span v-else>
              <el-icon class="is-loading"><Loading /></el-icon>
              登录中...
            </span>
          </button>
        </form>

        <!-- 注册表单 -->
        <form v-else @submit.prevent="handleRegister" class="login-form">
          <div class="form-group">
            <label>用户名</label>
            <input 
              v-model="formData.username" 
              type="text" 
              placeholder="请输入用户名"
              required
            >
          </div>

          <div class="form-group">
            <label>密码</label>
            <input 
              v-model="formData.password" 
              type="password" 
              placeholder="请输入密码（至少6位）"
              required
              minlength="6"
            >
          </div>

          <div class="form-group">
            <label>确认密码</label>
            <input 
              v-model="confirmPassword" 
              type="password" 
              placeholder="再次输入密码"
              required
              minlength="6"
            >
          </div>

          <button type="submit" class="submit-btn" :disabled="isLoading">
            <span v-if="!isLoading">注册</span>
            <span v-else>
              <el-icon class="is-loading"><Loading /></el-icon>
              注册中...
            </span>
          </button>
        </form>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// @ts-ignore
import { Loading, SuccessFilled } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const isLogin = ref(true)
const isLoading = ref(false)
const isSuccessLoading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const formData = ref({
  username: '',
  password: ''
})
const confirmPassword = ref('')

const handleLogin = async () => {
  if (!formData.value.username || !formData.value.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }

  isLoading.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const result = await authStore.login({
      username: formData.value.username,
      password: formData.value.password
    })

    if (result.code === 0) {
      ElMessage.success('登录成功')
      // 显示成功加载动画
      isSuccessLoading.value = true
      // 0.2秒后跳转到首页
      setTimeout(() => {
        router.push('/')
      }, 500)
    } else {
      errorMessage.value = result.message || '登录失败'
      ElMessage.error(errorMessage.value)
    }
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || '登录异常'
    ElMessage.error(errorMessage.value)
  } finally {
    isLoading.value = false
  }
}

const handleRegister = async () => {
  if (!formData.value.username || !formData.value.password) {
    ElMessage.error('请填写所有字段')
    return
  }

  if (formData.value.password !== confirmPassword.value) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  if (formData.value.password.length < 6) {
    ElMessage.error('密码长度至少6位')
    return
  }

  isLoading.value = true
  errorMessage.value = ''
  successMessage.value = ''

  try {
    const result = await authStore.register({
      username: formData.value.username,
      password: formData.value.password
    })

    if (result.code === 0) {
      successMessage.value = '注册成功，请登录'
      ElMessage.success('注册成功，请登录')
      
      // 切换到登录标签并清空表单
      setTimeout(() => {
        isLogin.value = true
        formData.value = { username: '', password: '' }
        confirmPassword.value = ''
        successMessage.value = ''
      }, 1500)
    } else {
      errorMessage.value = result.message || '注册失败'
      ElMessage.error(errorMessage.value)
    }
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || '注册异常'
    ElMessage.error(errorMessage.value)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fdfbfb 0%, #fff5f7 50%, #f0f4ff 100%);
  background-attachment: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  position: relative;
  overflow: hidden;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  z-index: 0;
  filter: blur(40px);
  opacity: 0.5;
}

.circle-1 {
  width: 400px;
  height: 400px;
  background: #ff8fab;
  top: -100px;
  left: -100px;
  animation: float 6s ease-in-out infinite;
}

.circle-2 {
  width: 350px;
  height: 350px;
  background: #72efdd;
  bottom: -50px;
  right: -100px;
  animation: float 6s ease-in-out infinite;
  animation-delay: -3s;
}

.login-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
}

.login-card {
  background: #ffffff;
  border-radius: 32px;
  padding: 3rem 2rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.logo-section {
  text-align: center;
  margin-bottom: 2rem;
}

.app-title {
  font-size: 2rem;
  font-weight: 800;
  color: #ff758f;
  margin: 0 0 0.5rem 0;
  text-shadow: 2px 2px 4px rgba(255, 117, 143, 0.1);
}

.app-subtitle {
  font-size: 0.95rem;
  color: #9fa0b3;
  margin: 0;
  font-weight: 500;
}

.form-tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  background: #f5f6fa;
  padding: 0.5rem;
  border-radius: 12px;
}

.tab-btn {
  flex: 1;
  padding: 0.75rem 1rem;
  background: transparent;
  border: none;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 600;
  color: #9fa0b3;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.tab-btn.active {
  background: #ffffff;
  color: #ff758f;
  box-shadow: 0 4px 12px rgba(255, 117, 143, 0.15);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #4a4e69;
}

.form-group input {
  padding: 0.75rem 1rem;
  border: 2px solid #e8eaef;
  border-radius: 10px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  font-family: inherit;
}

.form-group input:focus {
  outline: none;
  border-color: #ff758f;
  box-shadow: 0 0 0 3px rgba(255, 117, 143, 0.1);
}

.form-group input::placeholder {
  color: #b8bcc9;
}

.submit-btn {
  padding: 0.9rem 1.5rem;
  background: linear-gradient(135deg, #ff758f 0%, #ffc8dd 100%);
  color: #ffffff;
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  margin-top: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255, 117, 143, 0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.is-loading {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  padding: 0.75rem 1rem;
  background: #ffe8e8;
  border-left: 4px solid #ff6b6b;
  border-radius: 6px;
  color: #c92a2a;
  font-size: 0.9rem;
  margin-top: 1rem;
}

.success-message {
  padding: 0.75rem 1rem;
  background: #e8f9f0;
  border-left: 4px solid #51cf66;
  border-radius: 6px;
  color: #2f7d44;
  font-size: 0.9rem;
  margin-top: 1rem;
}

/* 登录成功加载动画 */
.success-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  backdrop-filter: blur(4px);
  animation: fadeInOverlay 0.2s ease-out;
}

.success-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.check-icon {
  font-size: 3rem;
  color: #51cf66;
  animation: successPulse 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes fadeInOverlay {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes successPulse {
  0% {
    transform: scale(0.5);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@media (max-width: 480px) {
  .login-card {
    padding: 2rem 1.5rem;
  }

  .app-title {
    font-size: 1.75rem;
  }

  .form-tabs {
    margin-bottom: 1.5rem;
  }

  .login-form {
    gap: 1rem;
  }
}
</style>
