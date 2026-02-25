<template>
  <div class="classes-container">
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>

    <el-card class="classes-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button 
              type="default" 
              :icon="ArrowLeft"
              @click="goBack"
              class="back-button cute-btn-default"
            >
              返回
            </el-button>
            <span class="page-title">班级管理</span>
          </div>
          <el-button 
            type="primary" 
            @click="showCreateClassDialog = true"
            class="cute-btn-primary"
          >
            新增班级
          </el-button>
        </div>
      </template>
      
      <el-table 
        :data="classList" 
        style="width: 100%" 
        v-loading="loading"
        class="cute-table"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="班级名称" align="center">
          <template #default="scope">
            <span class="highlight-text">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button 
              size="small" 
              type="primary" 
              class="cute-btn-small"
              @click="editClass(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              class="cute-btn-small cute-btn-danger"
              @click="deleteClass(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <el-dialog 
      v-model="showCreateClassDialog" 
      :title="currentClass.id ? '✨ 编辑班级' : '✨ 新增班级'" 
      width="500px"
      :close-on-click-modal="false"
      class="cute-dialog"
    >
      <el-form 
        :model="currentClass" 
        :rules="classRules" 
        ref="classFormRef" 
        label-width="100px"
        class="cute-form"
      >
        <el-form-item label="班级名称" prop="name">
          <el-input 
            v-model="currentClass.name" 
            placeholder="请输入班级名称"
          />
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime">
          <el-date-picker
            v-model="currentClass.createTime"
            type="datetime"
            placeholder="请选择创建时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="currentClass.description" 
            type="textarea" 
            placeholder="请输入班级描述"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelClassOperation" class="cute-btn-default">取消</el-button>
          <el-button type="primary" @click="saveClass" :loading="saving" class="cute-btn-primary">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { classApi } from '@/api'

const router = useRouter()

interface ClassItem {
  id?: number
  name: string
  createTime?: string
  description?: string
}

const classList = ref<ClassItem[]>([])
const loading = ref(false)
const saving = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showCreateClassDialog = ref(false)

const currentClass = reactive<ClassItem>({
  name: '',
  description: ''
})

const classRules = {
  name: [
    { required: true, message: '请输入班级名称', trigger: 'blur' },
    { min: 2, max: 50, message: '班级名称长度应在2-50个字符之间', trigger: 'blur' }
  ]
}

const classFormRef = ref()

const loadClassList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    const response = await classApi.getClassList(params)
    if (response.data.code === 0) {
      classList.value = response.data.data.items || []
      total.value = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取班级列表失败')
    }
  } catch (error) {
    console.error('Failed to load class list:', error)
    ElMessage.error('获取班级列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadClassList()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadClassList()
}

const editClass = (classItem: ClassItem) => {
  Object.assign(currentClass, classItem)
  showCreateClassDialog.value = true
}

const deleteClass = async (classId: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个班级吗？此操作不可恢复',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await classApi.deleteClass(classId)
    if (response.data.code === 0) {
      ElMessage.success('删除成功')
      loadClassList()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    console.error('Delete class error:', error)
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const saveClass = async () => {
  if (!classFormRef.value) return

  try {
    await classFormRef.value.validate()
    saving.value = true

    let response;
    if (currentClass.id) {
      response = await classApi.updateClass(currentClass.id, {
        name: currentClass.name,
        createTime: currentClass.createTime,
        description: currentClass.description
      })
    } else {
      response = await classApi.createClass({
        name: currentClass.name,
        createTime: currentClass.createTime,
        description: currentClass.description
      })
    }

    if (response.data.code === 0) {
      ElMessage.success(currentClass.id ? '更新成功' : '创建成功')
      showCreateClassDialog.value = false
      resetCurrentClass()
      loadClassList()
    } else {
      ElMessage.error(response.data.message || (currentClass.id ? '更新失败' : '创建失败'))
    }
  } catch (error) {
    console.error('Save class error:', error)
    ElMessage.error(currentClass.id ? '更新失败' : '创建失败')
  } finally {
    saving.value = false
  }
}

const cancelClassOperation = () => {
  showCreateClassDialog.value = false
  resetCurrentClass()
}

const resetCurrentClass = () => {
  currentClass.id = undefined
  currentClass.name = ''
  currentClass.createTime = undefined
  currentClass.description = ''
}

const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const goBack = () => {
  router.push('/')
}

onMounted(() => {
  loadClassList()
})
</script>

<style scoped>
* {
  font-family: 'Nunito', 'Quicksand', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.classes-container {
  padding: 3rem 2rem;
  min-height: 100vh;
  background-color: #fdfbfb;
  background-image: 
    linear-gradient(rgba(255, 182, 193, 0.4) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 182, 193, 0.4) 1px, transparent 1px);
  background-size: 30px 30px;
  position: relative;
  overflow: hidden;
  z-index: 1;
}

/* 悬浮光晕 */
.decoration-circle {
  position: absolute;
  border-radius: 50%;
  z-index: -1;
  filter: blur(50px);
  opacity: 0.5;
  animation: float 6s ease-in-out infinite;
}
.circle-1 {
  width: 400px; height: 400px;
  background: #ffc8dd;
  top: -100px; left: -150px;
}
.circle-2 {
  width: 350px; height: 350px;
  background: #fff0f3;
  bottom: -50px; right: -100px;
  animation-delay: -3s;
}

/* 主卡片样式 */
.classes-card {
  max-width: 1200px;
  margin: 0 auto;
  border: 4px solid #fff;
  border-radius: 30px;
  box-shadow: 0 12px 0px rgba(255, 200, 221, 0.3), 0 20px 40px rgba(0, 0, 0, 0.05);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  animation: slideUp 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

:deep(.el-card__header) {
  border-bottom: 2px dashed #ffc8dd;
  padding: 1.5rem 2rem;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  font-size: 1.4rem;
  font-weight: 800;
  color: #ff758f;
  letter-spacing: 1px;
}

/* 按钮样式重置 */
.cute-btn-primary {
  background: #ff758f !important;
  border-color: #ff758f !important;
  border-radius: 20px !important;
  font-weight: 600;
  padding: 10px 24px !important;
  box-shadow: 0 4px 10px rgba(255, 117, 143, 0.3);
  transition: all 0.3s ease;
}
.cute-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 117, 143, 0.4);
  background: #ff8fab !important;
}

.cute-btn-default {
  border-radius: 20px !important;
  font-weight: 600;
  border: 2px solid #ffeaee !important;
  color: #4a4e69 !important;
  transition: all 0.3s ease;
}
.cute-btn-default:hover {
  background: #fff0f3 !important;
  border-color: #ffc8dd !important;
  color: #ff758f !important;
}

.cute-btn-small {
  border-radius: 12px !important;
  padding: 8px 16px !important;
}

.cute-btn-danger {
  background: #fff0f3 !important;
  border-color: #ffc8dd !important;
  color: #ff758f !important;
  box-shadow: none !important;
}
.cute-btn-danger:hover {
  background: #ff758f !important;
  color: #fff !important;
}

/* 表格可爱化 */
.cute-table {
  border-radius: 16px;
  overflow: hidden;
  margin-top: 10px;
}
:deep(.el-table th.el-table__cell) {
  background-color: #fff0f3 !important;
  color: #ff758f !important;
  font-weight: 700;
  font-size: 1rem;
  border-bottom: 2px solid #ffc8dd;
}
:deep(.el-table td.el-table__cell) {
  border-bottom: 1px dashed #f1f2f6;
  color: #4a4e69;
  font-weight: 500;
}
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background-color: #fff6f8 !important;
}

.highlight-text {
  font-weight: 700;
  color: #4a4e69;
  background: #ffeaee;
  padding: 4px 12px;
  border-radius: 12px;
}

/* 分页组件居中 & 圆润化 */
.pagination {
  margin-top: 2rem;
  padding-bottom: 1rem;
  display: flex;
  justify-content: center;
}
:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 10px;
}

/* 弹窗可爱化 */
:deep(.cute-dialog) {
  border-radius: 24px !important;
  overflow: hidden;
  border: 4px solid #fff;
  box-shadow: 0 15px 40px rgba(0,0,0,0.1);
}
:deep(.cute-dialog .el-dialog__header) {
  background: #fff0f3;
  margin-right: 0;
  padding: 20px;
  border-bottom: 2px dashed #ffc8dd;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
:deep(.cute-dialog .el-dialog__title) {
  font-weight: 800;
  color: #ff758f;
  margin: 0;
}
:deep(.cute-dialog .el-dialog__headerbtn) {
  position: relative;
  top: 0;
  right: 0;
  width: 32px;
  height: 32px;
  background: #fff;
  border-radius: 50%;
  transition: all 0.3s ease;
}
:deep(.cute-dialog .el-dialog__headerbtn:hover) {
  background: #ff758f;
}
:deep(.cute-dialog .el-dialog__headerbtn:hover .el-dialog__close) {
  color: #fff;
}
:deep(.cute-dialog .el-dialog__close) {
  color: #ff758f;
  font-size: 18px;
  font-weight: bold;
  transition: all 0.3s ease;
}
:deep(.cute-form .el-input__wrapper),
:deep(.cute-form .el-textarea__inner) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #ffeaee inset;
}
:deep(.cute-form .el-input__wrapper.is-focus),
:deep(.cute-form .el-textarea__inner:focus) {
  box-shadow: 0 0 0 2px #ffc8dd inset !important;
}

/* 动画定义 */
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@keyframes slideUp {
  0% { transform: translateY(40px); opacity: 0; }
  100% { transform: translateY(0); opacity: 1; }
}
</style>