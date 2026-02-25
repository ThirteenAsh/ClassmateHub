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
            @click="handleCreateClass"
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
              type="default"
              class="cute-btn-small cute-btn-edit"
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

const handleCreateClass = () => {
  resetCurrentClass()
  showCreateClassDialog.value = true
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

<style scoped src="../styles/classes.css"></style>

<style scoped>

:deep(.el-card__header) {
  border-bottom: 2px dashed #ffc8dd;
  padding: 1.5rem 2rem;
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

:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 10px;
}

/* 弹窗 */

:deep(.cute-dialog) {
  border-radius: 24px !important;
  overflow: hidden;
  border: 4px solid #fff;
  box-shadow: 0 15px 40px rgba(0,0,0,0.1);
  animation: fadeIn 0.1s ease-out;
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

:deep(.cute-dialog .el-dialog__body) {
  padding: 24px 20px;
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

/* 表单 */

:deep(.cute-form .el-input__wrapper),
:deep(.cute-form .el-textarea__inner) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #ffeaee inset;
}

:deep(.cute-form .el-input__wrapper.is-focus),
:deep(.cute-form .el-textarea__inner:focus) {
  box-shadow: 0 0 0 2px #ffc8dd inset !important;
}

/* 消息提示 */

:deep(.el-message) {
  border-radius: 20px !important;
  padding: 12px 20px !important;
  font-weight: 600;
  box-shadow: 0 8px 24px rgba(255, 117, 143, 0.2);
  animation: slideDown 0.3s ease-out;
}

:deep(.el-message--success),
:deep(.el-message--error),
:deep(.el-message--warning),
:deep(.el-message--info) {
  background: #ff758f !important;
  border: none !important;
  color: #fff !important;
}

:deep(.el-message__icon),
:deep(.el-message__content) {
  color: #fff !important;
}

/* 动画保留 */

@keyframes fadeIn {
  0% { opacity: 0; transform: scale(0.9); }
  100% { opacity: 1; transform: scale(1); }
}

@keyframes slideDown {
  0% { opacity: 0; transform: translateY(-20px); }
  100% { opacity: 1; transform: translateY(0); }
}
</style>