<template>
  <div class="bottom-nav">
    <div 
      v-for="item in navItems" 
      :key="item.id"
      class="nav-item" 
      :class="{ active: activeTab === item.id }"
      @click="handleNavClick(item)"
    >
      <div class="nav-icon-wrapper">
        <el-icon :size="24">
          <component :is="item.icon" />
        </el-icon>
        <div class="ripple" v-if="clickedTab === item.id"></div>
      </div>
      <span class="nav-label">{{ item.label }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, User, School, DataAnalysis } from '@element-plus/icons-vue'

defineOptions({
  name: 'BottomNav'
})

interface NavItem {
  id: string
  label: string
  icon: any
  route?: string
}

const props = defineProps<{
  activeTab: string
}>()

const emit = defineEmits<{
  (e: 'update:activeTab', value: string): void
}>()

const router = useRouter()
const clickedTab = ref<string | null>(null)

const navItems: NavItem[] = [
  { id: 'square', label: '广场', icon: ChatDotRound, route: '/' },
  { id: 'classes', label: '班级管理', icon: School, route: '/classes' },
  { id: 'students', label: '学生信息', icon: User, route: '/students' },
  { id: 'statistics', label: '统计信息', icon: DataAnalysis, route: '/statistics' }
]

const handleNavClick = (item: NavItem) => {
  clickedTab.value = item.id
  emit('update:activeTab', item.id)
  
  if (item.route) {
    router.push(item.route)
  }
  
  setTimeout(() => {
    clickedTab.value = null
  }, 600)
}
</script>

<style scoped>
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #ffffff;
  display: flex;
  justify-content: space-around; /* 修改为自动均匀分布 */
  align-items: center;
  padding: 8px 16px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.08);
  border-top-left-radius: 24px;
  border-top-right-radius: 24px;
  z-index: 100;
  gap: 8px;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  color: #9fa0b3;
  min-width: 64px;
  height: 64px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.nav-item:hover {
  background: #f8edeb;
  transform: translateY(-2px);
}

.nav-item.active {
  color: #ff758f;
  background: #fff0f3;
}

.nav-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
}

.nav-item.active .nav-icon-wrapper {
  animation: iconBounce 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.nav-label {
  font-size: 0.7rem;
  font-weight: 600;
  letter-spacing: 0.5px;
  line-height: 1;
  text-align: center;
}

.ripple {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 117, 143, 0.3);
  animation: rippleEffect 0.6s ease-out;
}

@keyframes iconBounce {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

@keyframes rippleEffect {
  0% {
    width: 0;
    height: 0;
    opacity: 0.6;
  }
  100% {
    width: 80px;
    height: 80px;
    opacity: 0;
  }
}

@media (max-width: 768px) {
  .bottom-nav {
    padding: 6px 8px;
  }

  .nav-item {
    padding: 6px 10px;
    min-width: 56px;
    height: 58px;
  }

  .nav-label {
    font-size: 0.65rem;
  }
}
</style>
