<template>
  <div class="square">
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>

    <div class="header-section">
      <div class="header-content">
        <div class="title-row">
          <h1 class="page-title">广场</h1>
          <div class="meta-pill" v-if="!isLoading && !error">
            <span class="meta-label">同班同学</span>
            <span class="meta-value">{{ classmates.length }}</span>
          </div>
        </div>
        <p class="page-subtitle">电子同学录，寻找同班同学，建立联系，发现更多好友，下滑查看更多</p>
      </div>
    </div>

    <div class="notification-bar">
      <div class="notification-content">
        <div class="notification-left">
          <div class="notice-header">
            <div class="notice-title">
              <el-icon class="notice-icon"><InfoFilled /></el-icon>
              <span>说明</span>
            </div>
            <el-button class="notice-toggle" text @click="noticeExpanded = !noticeExpanded">
              {{ noticeExpanded ? '收起' : '展开' }}
            </el-button>
          </div>
          <p v-show="noticeExpanded" class="notice-text">
            请勿担心数据泄露风险，本网站非公共平台，服务器及域名均本人所有，网络接入服务商为阿里云计算有限公司。
            <br />
            受限于技术水平，网站可能会有各种奇怪的 bug，或者体验不良好的地方，请谅解
            <span class="light-text">（我是真没招了，有的问题就是修不好……）</span>
            <br />
            欢迎大家访问我网站的其他内容(≧ｗ≦)：
            <a href="https://thirteenash.bytecraft.xin/" target="_blank" rel="noopener noreferrer">个人主页</a>、
            <a href="https://game.bytecraft.xin/" target="_blank" rel="noopener noreferrer">社区</a>、
            <a href="https://docs.bytecraft.xin/" target="_blank" rel="noopener noreferrer">文档帮助</a>
            <br />该项目的源代码仓库：
            <a href="https://github.com/ThirteenAsh/ClassmateHub" target="_blank" rel="noopener noreferrer">跳转GitHub</a>，欢迎交流学习
            <br />
            <strong style="color:#ff5d8f;">您仅可在本页面查看同班同学的部分公开信息，其他人无法访问或修改任何个人数据。请放心使用！</strong>
          </p>
        </div>
        <div class="notification-right">
          <div class="legal-content">
            <div class="notice-title">
              <el-icon class="notice-icon"><InfoFilled /></el-icon>
              <span>备案信息</span>
            </div>
            <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">
              陕ICP备2025084108号-1
            </a>
            <a href="http://www.beian.gov.cn/portal/registerSystemInfo" target="_blank" rel="noopener noreferrer">
              <img src="https://beian.mps.gov.cn/img/logo01.dd7ff50e.png" alt="" style="margin-right:3px;">
              陕公网安备 61012502000363 号
            </a>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isLoading" class="loading-section">
      <div class="loading-spinner">
        <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      </div>
      <p class="loading-text">正在加载同学列表...</p>
    </div>

    <div v-else-if="error" class="error-section">
      <div class="error-card">
        <el-icon :size="60"><WarningFilled /></el-icon>
        <h3 class="error-title">{{ error }}</h3>
        <el-button type="primary" @click="handleRetry">重试</el-button>
      </div>
    </div>

    <div v-else-if="classmates.length === 0" class="empty-section">
      <div class="empty-card">
        <div class="empty-icon">
          <el-icon :size="60"><ChatDotRound /></el-icon>
        </div>
        <h3 class="empty-title">暂无同学信息</h3>
        <p class="empty-desc">等待班级同学陆续加入...</p>
      </div>
    </div>

    <div v-else class="classmates-grid">
      <div 
        v-for="classmate in classmates" 
        :key="classmate.phone" 
        class="classmate-card"
      >
        <div class="card-content">
          <div class="card-header">
            <div class="avatar-wrapper">
              <el-avatar :size="52" class="classmate-avatar">
                {{ (classmate.name || '同').slice(0, 1) }}
              </el-avatar>
            </div>
            <div class="card-info">
              <h3 class="classmate-name">{{ classmate.username || classmate.name }}</h3>
              <div class="card-tags">
                <span class="tag" :class="classmate.gender === '男' ? 'tag-boy' : 'tag-girl'">
                  {{ classmate.gender === '男' ? '男生' : '女生' }}
                </span>
                <span class="tag tag-school">{{ classmate.university || '未知学校' }}</span>
              </div>
            </div>
          </div>
          
          <div class="card-details">
            <div class="detail-item">
              <el-icon class="detail-icon"><User /></el-icon>
              <span class="detail-text">
                <span class="detail-label">姓名</span>
                <span class="detail-value">{{ classmate.name || '-' }}</span>
              </span>
            </div>

            <div class="detail-item">
              <el-icon class="detail-icon"><Calendar /></el-icon>
              <span class="detail-text">
                <span class="detail-label">生日</span>
                <span class="detail-value">{{ classmate.birthDate || '-' }}</span>
              </span>
            </div>
            
            <div class="detail-item">
              <el-icon class="detail-icon"><Phone /></el-icon>
              <span class="detail-text">
                <span class="detail-label">电话</span>
                <span class="detail-value">{{ classmate.phone || '-' }}</span>
              </span>
            </div>

            <div class="detail-item">
              <el-icon class="detail-icon"><ChatDotRound /></el-icon>
              <span class="detail-text">
                <span class="detail-label">QQ</span>
                <span class="detail-value">{{ classmate.qq || '-' }}</span>
              </span>
            </div>
            
            <div class="detail-item">
              <el-icon class="detail-icon"><School /></el-icon>
              <span class="detail-text">
                <span class="detail-label">学校</span>
                <span class="detail-value">{{ classmate.university || '-' }}</span>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <BottomNav v-model:activeTab="activeTab" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
// @ts-ignore
import { ElMessage, ElMessageBox } from 'element-plus'
// @ts-ignore
import { 
  ChatDotRound, 
  Calendar, 
  Phone, 
  School,
  InfoFilled,
  WarningFilled,
  Loading,
  User
} from '@element-plus/icons-vue'
import { squareApi } from '../api'
// @ts-ignore
import type { ApiResponse } from '../api'
// @ts-ignore
import BottomNav from '../components/BottomNav.vue'

interface Classmate {
  username?: string
  name: string
  gender: string
  birthDate: string
  phone: string
  qq?: string
  university: string
}

const activeTab = ref('square')
const isLoading = ref(true)
const error = ref('')
const classmates = ref<Classmate[]>([])
const router = useRouter()
const noticeExpanded = ref(true)

const loadClassmates = async () => {
  isLoading.value = true
  error.value = ''
  
  try {
    const response = await squareApi.getSquareList()
    
    if (response.data.code === 0) {
      classmates.value = response.data.data || []
    } else if (response.data.code === -1) {
      error.value = response.data.message || '未登录'
      ElMessage.warning(error.value)
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else if (response.data.code === 500) {
      error.value = response.data.message || '请先完善个人信息'
      ElMessage.warning(error.value)
      setTimeout(() => {
        router.push('/profile')
      }, 2000)
    } else {
      error.value = '加载失败'
      ElMessage.error(error.value)
    }
  } catch (err: any) {
    error.value = '网络错误，请稍后重试'
    ElMessage.error(error.value)
  } finally {
    isLoading.value = false
  }
}

const handleRetry = () => {
  loadClassmates()
}

onMounted(() => {
  loadClassmates()
})
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
  padding: 3.25rem 1rem 1.25rem;
  text-align: center;
}

.header-content {
  max-width: 800px;
  margin: 0 auto;
}

.title-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
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
  max-width: 960px;
  line-height: 1.5;
}

.meta-pill {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid rgba(255, 200, 221, 0.75);
  box-shadow: 0 6px 18px rgba(255, 117, 143, 0.12);
  backdrop-filter: blur(8px);
}

.meta-label {
  font-size: 0.85rem;
  font-weight: 800;
  color: #ff758f;
}

.meta-value {
  font-size: 0.95rem;
  font-weight: 900;
  color: #4a4e69;
  min-width: 1.5em;
  text-align: center;
}

.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 2rem;
  text-align: center;
}

.loading-spinner {
  margin-bottom: 1.5rem;
}

.loading-text {
  font-size: 1rem;
  color: #9fa0b3;
  font-weight: 500;
}

.error-section {
  padding: 2rem;
}

.error-card {
  background: #ffffff;
  border-radius: 32px;
  padding: 3rem 2rem;
  text-align: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.03);
  border: 2px dashed #ffc8dd;
  max-width: 500px;
  margin: 0 auto;
}

.error-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #ff6b6b;
  margin: 1rem 0 1.5rem 0;
}

.empty-section {
  padding: 2rem;
}

.empty-card {
  background: #ffffff;
  border-radius: 32px;
  padding: 3rem 2rem;
  text-align: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.03);
  border: 2px dashed #ffc8dd;
  max-width: 500px;
  margin: 0 auto;
}

.empty-icon {
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

.empty-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #4a4e69;
  margin: 0 0 0.75rem 0;
}

.empty-desc {
  font-size: 1rem;
  color: #9fa0b3;
  margin: 0;
  font-weight: 500;
}

.classmates-grid {
  padding: 0 1rem 4rem;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.25rem;
  max-width: 1200px;
  margin: 0 auto;
}

.classmate-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 1.5rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  border: 2px solid #fff;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.classmate-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(255, 117, 143, 0.15);
  border-color: #ffc8dd;
}

.classmate-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ff758f, #ffc8dd);
  transition: all 0.3s ease;
}

.classmate-card:hover::before {
  height: 6px;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.avatar-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #fff0f3 0%, #ffc8dd 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 2px solid #fff;
  box-shadow: 0 4px 12px rgba(255, 117, 143, 0.15);
  padding: 3px;
}

.classmate-avatar {
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid #fff;
  color: #ff758f;
  font-weight: 900;
  letter-spacing: 0.5px;
}

.card-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.classmate-name {
  font-size: 1.25rem;
  font-weight: 700;
  color: #4a4e69;
  margin: 0;
  line-height: 1.3;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 2px;
}

.tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 0.78rem;
  font-weight: 800;
  border: 1px solid rgba(255, 200, 221, 0.55);
  background: rgba(255, 240, 243, 0.85);
  color: #ff758f;
}

.tag-boy {
  background: rgba(189, 224, 254, 0.45);
  border-color: rgba(189, 224, 254, 0.9);
  color: #3a7bd5;
}

.tag-girl {
  background: rgba(255, 200, 221, 0.5);
  border-color: rgba(255, 200, 221, 0.95);
  color: #ff5d8f;
}

.tag-school {
  background: rgba(255, 255, 255, 0.75);
  color: #4a4e69;
}

.card-details {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px dashed #e8eaef;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.9rem;
  color: #6c757d;
}

.detail-icon {
  color: #ff8fab;
  font-size: 18px;
}

.detail-text {
  flex: 1;
  font-weight: 600;
  line-height: 1.4;
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
}

.detail-label {
  color: #9fa0b3;
  font-weight: 700;
  font-size: 0.82rem;
  flex-shrink: 0;
}

.detail-value {
  color: #4a4e69;
  font-weight: 800;
  word-break: break-word;
  text-align: right;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.notification-bar {
  background: #ffffff;
  border-radius: 24px;
  padding: 0;
  margin: 0 1rem 2rem;
  box-shadow: 0 8px 24px rgba(255, 117, 143, 0.1);
  border: 2px solid #ffc8dd;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  overflow: hidden;
}

.notification-content {
  display: flex;
  flex-direction: row;
  min-height: 120px;
}

.notification-left {
  flex: 1;
  padding: 1.25rem 1.25rem 1rem;
  background: linear-gradient(135deg, #fff0f3 0%, #ffffff 100%);
  border-right: 2px dashed #ffc8dd;
  font-size: 0.9rem;
  color: #4a4e69;
  line-height: 1.4;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: center;
}

.notice-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.notice-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 900;
  color: #ff758f;
}

.notice-icon {
  color: #ff8fab;
}

.notice-toggle {
  font-weight: 800;
  color: #6c757d;
}

.notice-toggle:hover {
  color: #ff758f;
}

.notice-text {
  margin: 0;
  color: #4a4e69;
}

.notice-text a {
  color: #ff758f;
  text-decoration: none;
}

.notice-text a:hover {
  text-decoration: underline;
}

.light-text {
  color: #9fa0b3;
  font-size: 0.8rem;
  font-style: italic;
}

.notification-right {
  flex-shrink: 0;
  padding: 1.5rem;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.legal-content {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  font-size: 0.8rem;
  text-align: center;
}

.legal-content a {
  color: #6c757d;
  text-decoration: none;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px 8px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid #f0f0f0;
}

.legal-content a:hover {
  color: #ff758f;
  background: rgba(255, 255, 255, 1);
  border-color: #ffc8dd;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 117, 143, 0.15);
}

.legal-content img {
  width: 16px;
  height: 16px;
  vertical-align: middle;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
  
  .page-subtitle {
    font-size: 0.9rem;
    padding: 6px 16px;
  }

  .meta-pill {
    padding: 6px 12px;
  }

  .notification-bar {
    margin: 0 1rem 1.5rem;
  }

  .notification-content {
    flex-direction: column;
    min-height: auto;
  }

  .notification-left {
    border-right: none;
    border-bottom: 2px dashed #ffc8dd;
    padding: 1.25rem;
  }

  .notification-right {
    padding: 1.25rem;
  }

  .legal-content a {
    justify-content: center;
  }

  .classmates-grid {
    grid-template-columns: 1fr;
    padding: 0 1rem 4rem;
  }

  .classmate-card {
    padding: 1.25rem;
  }

  .card-header {
    gap: 0.75rem;
  }

  .avatar-wrapper {
    width: 48px;
    height: 48px;
  }

  .classmate-name {
    font-size: 1.1rem;
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
