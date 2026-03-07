<template>
  <div class="statistics">
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>

    <div class="header-section">
      <div class="header-content">
        <h1 class="page-title">数据统计</h1>
        <p class="page-subtitle">查看数据统计</p>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card card-pink">
        <div class="stat-icon">
          <el-icon :size="40"><School /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ displayClasses }}</div>
          <div class="stat-label">班级总数</div>
        </div>
      </div>

      <div class="stat-card card-blue">
        <div class="stat-icon">
          <el-icon :size="40"><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ displayStudents }}</div>
          <div class="stat-label">学生总数</div>
        </div>
      </div>

      <div class="stat-card card-purple">
        <div class="stat-icon">
          <el-icon :size="40"><DataAnalysis /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value gender-ratio">
            <span class="male-ratio">{{ displayGenders.male }}</span>
            <span class="ratio-separator">:</span>
            <span class="female-ratio">{{ displayGenders.female }}</span>
          </div>
          <div class="stat-label">性别比例</div>
        </div>
      </div>
    </div>

    <!-- 班级分布统计 -->
    <div class="content-section">
      <h2 class="section-title">班级分布</h2>
      <div class="distribution-card">
        <div ref="classChartRef" class="class-chart-container"></div>
        <div class="class-legend">
          <div v-for="classInfo in classStatistics" :key="classInfo.classId" class="legend-item">
            <span class="legend-dot" :style="{ backgroundColor: getClassColor(classInfo.classId) }"></span>
            <span class="legend-text">{{ classInfo.className }} ({{ classInfo.studentCount }} 人)</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 性别比例统计 -->
    <div class="content-section">
      <h2 class="section-title">性别比例</h2>
      <div class="gender-card">
        <div ref="genderChartRef" class="gender-chart-container"></div>
        <div class="gender-legend">
          <div v-for="gender in genderRatios" :key="gender.gender" class="legend-item">
            <span class="legend-dot" :style="{ backgroundColor: gender.gender === '男' ? '#72efdd' : '#ff8fab' }"></span>
            <span class="legend-text">{{ gender.gender }} ({{ gender.count }} 人)</span>
          </div>
        </div>
      </div>
    </div>

    <BottomNav v-model:activeTab="activeTab" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
// @ts-ignore
import { School, User, DataAnalysis } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { studentApi } from '../api/index'
// @ts-ignore
import BottomNav from '../components/BottomNav.vue'

const activeTab = ref('statistics')
const totalStudents = ref(0)
const totalClasses = ref(0)
const displayStudents = ref(0)
const displayClasses = ref(0)
const displayGenders = ref({ male: 0, female: 0 })
const classStatistics = ref<Array<{ classId: number; className: string; studentCount: number }>>([])
const genderRatios = ref<Array<{ gender: string; count: number; ratio: number }>>([])
const classChartRef = ref<HTMLElement | null>(null)
const genderChartRef = ref<HTMLElement | null>(null)
let classChart: echarts.ECharts | null = null
let genderChart: echarts.ECharts | null = null

// 为不同班级生成颜色
const colorPalette = ['#ff758f', '#ffc8dd', '#72efdd', '#bde0fe', '#c084fc', '#e0c3fc', '#ffa07a', '#87ceeb', '#dda0dd', '#f0e68c']
const getClassColor = (classId: number): string => {
  return colorPalette[(classId - 1) % colorPalette.length]
}

// 数字动画函数
const animateNumber = (endValue: number, displayRef: any, duration: number = 1000) => {
  const startValue = 0
  const startTime = performance.now()
  
  const animate = (currentTime: number) => {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    
    const currentValue = Math.floor(startValue + (endValue - startValue) * progress)
    displayRef.value = currentValue
    
    if (progress < 1) {
      requestAnimationFrame(animate)
    }
  }
  
  requestAnimationFrame(animate)
}

// 触发所有数字动画
const animateAllNumbers = () => {
  animateNumber(totalClasses.value, displayClasses, 800)
  animateNumber(totalStudents.value, displayStudents, 800)
}



const initClassChart = () => {
  if (!classChartRef.value) return
  
  if (!classChart) {
    classChart = echarts.init(classChartRef.value)
  }

  const chartData = classStatistics.value
    .filter(item => item.studentCount > 0)
    .map(item => ({
      value: item.studentCount,
      name: item.className,
      itemStyle: {
        color: getClassColor(item.classId)
      }
    }))

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} 人 ({d}%)'
    },
    legend: {
      show: false
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '50%'],
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: true,
          formatter: '{b}\n{d}%',
          fontSize: 12,
          color: '#4a4e69'
        }
      }
    ]
  }

  classChart.setOption(option)
}

const initGenderChart = () => {
  if (!genderChartRef.value) return
  
  if (!genderChart) {
    genderChart = echarts.init(genderChartRef.value)
  }

  const chartData = genderRatios.value.map(item => ({
    value: item.count,
    name: item.gender,
    itemStyle: {
      color: item.gender === '男' ? '#72efdd' : '#ff8fab'
    }
  }))

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} 人 ({d}%)'
    },
    legend: {
      show: false
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '50%'],
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: true,
          formatter: '{b}\n{d}%',
          fontSize: 12,
          color: '#4a4e69'
        }
      }
    ]
  }

  genderChart.setOption(option)
}

const loadStatistics = async () => {
  try {
    const response = await studentApi.getStatisticsSummary()
    if (response.data.code === 0) {
      const data = response.data.data
      totalStudents.value = data.totalCount
      totalClasses.value = data.classStatistics.length
      classStatistics.value = data.classStatistics
      genderRatios.value = data.genderRatios
      
      // 计算男女比例
      if (data.genderRatios && data.genderRatios.length > 0) {
        const counts: { [key: string]: number } = {}
        data.genderRatios.forEach((g: { gender: string; count: number }) => {
          counts[g.gender] = g.count
        })
        const maleCount = counts['男'] || 0
        const femaleCount = counts['女'] || 0
        const gcd = (a: number, b: number): number => b === 0 ? a : gcd(b, a % b)
        const divisor = gcd(maleCount, femaleCount) || 1
        displayGenders.value = {
          male: Math.round(maleCount / divisor),
          female: Math.round(femaleCount / divisor)
        }
      }
      
      // 触发数字动画
      animateAllNumbers()
    } else {
      ElMessage.error('加载统计数据失败')
    }
  } catch (error) {
    ElMessage.error('获取统计数据异常')
    console.error('Error loading statistics:', error)
  }
}

// 监听班级数据变化，初始化图表
watch(() => classStatistics.value, () => {
  if (classStatistics.value.length > 0) {
    initClassChart()
  }
}, { deep: true })

// 监听性别数据变化，初始化图表
watch(() => genderRatios.value, () => {
  if (genderRatios.value.length > 0) {
    initGenderChart()
  }
}, { deep: true })

onMounted(() => {
  loadStatistics()
  window.addEventListener('resize', () => {
    classChart?.resize()
    genderChart?.resize()
  })
})
</script>

<style scoped>
* {
  font-family: 'Nunito', 'Quicksand', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.statistics {
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 2rem;
  padding: 2rem 2rem 3rem;
  max-width: 1000px;
  margin: 0 auto;
}

.stat-card {
  background: #ffffff;
  border-radius: 32px;
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 12px 0px rgba(0, 0, 0, 0.04);
  border: 4px solid #fff;
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
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

.stat-icon {
  width: 80px;
  height: 80px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-pink .stat-icon {
  background: #fff0f3;
  color: #ff8fab;
}
.card-blue .stat-icon {
  background: #eef8ff;
  color: #72efdd;
}
.card-purple .stat-icon {
  background: #f3e8ff;
  color: #c084fc;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: 800;
  color: #4a4e69;
  line-height: 1;
  margin-bottom: 0.5rem;
  animation: countUp 0.8s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.gender-ratio {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  animation: none;
}

.male-ratio {
  color: #72efdd;
  font-size: 2.5rem;
  font-weight: 800;
}

.female-ratio {
  color: #ff8fab;
  font-size: 2.5rem;
  font-weight: 800;
}

.ratio-separator {
  color: #9fa0b3;
  font-size: 2rem;
  font-weight: 600;
}

.stat-label {
  font-size: 0.95rem;
  color: #9fa0b3;
  font-weight: 600;
}

/* 内容部分样式 */
.content-section {
  padding: 2rem 2rem;
  max-width: 1000px;
  margin: 2rem auto;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 800;
  color: #ff758f;
  margin: 0 0 1.5rem 0;
  position: relative;
  padding-left: 1rem;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(180deg, #ff758f, #ffc8dd);
  border-radius: 2px;
}

/* 班级分布卡片 */
.distribution-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  border: 2px solid #fff;
}

.distribution-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.distribution-item {
  padding: 1rem 0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.class-name {
  font-size: 1rem;
  font-weight: 600;
  color: #4a4e69;
}

.student-count {
  font-size: 0.95rem;
  font-weight: 700;
  color: #ff758f;
  background: #fff0f3;
  padding: 4px 12px;
  border-radius: 20px;
}

/* 班级分布图表 */
.class-chart-container {
  width: 100%;
  height: 300px;
  min-height: 300px;
}

.class-legend {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #f0f0f0;
  max-height: 150px;
  overflow-y: auto;
}

/* 性别比例卡片 */
.gender-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  border: 2px solid #fff;
}

.gender-chart-container {
  width: 100%;
  height: 300px;
  min-height: 300px;
}

.gender-legend {
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #f0f0f0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.legend-text {
  font-size: 0.95rem;
  color: #4a4e69;
  font-weight: 600;
}

@keyframes countUp {
  0% {
    opacity: 0;
    transform: scale(0.5) translateY(10px);
  }
  50% {
    opacity: 0.8;
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
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
  
  .stats-grid {
    grid-template-columns: 1fr;
    padding: 1rem 1.5rem 2rem;
  }

  .stat-card {
    padding: 1.5rem;
  }

  .stat-value {
    font-size: 2rem;
  }

  .content-section {
    padding: 1.5rem 1rem;
  }

  .section-title {
    font-size: 1.25rem;
  }

  .distribution-card,
  .gender-card {
    padding: 1.5rem;
  }

  .class-chart-container,
  .gender-chart-container {
    height: 250px;
  }

  .class-legend {
    max-height: 120px;
  }

  .gender-legend {
    gap: 1rem;
  }

  .legend-text {
    font-size: 0.85rem;
  }

  .bottom-nav {
    padding: 10px 16px;
  }

  .nav-item {
    padding: 6px 20px;
  }
}
</style>
