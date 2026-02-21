<template>
  <div class="classes-container">
    <el-card class="classes-card">
      <template #header>
        <div class="card-header">
          <span>班级管理</span>
          <el-button 
            type="primary" 
            @click="showCreateClassDialog = true"
            v-if="userStore.role === 'admin'"
          >
            新增班级
          </el-button>
        </div>
      </template>
      
      <el-table 
        :data="classList" 
        style="width: 100%" 
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="班级名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createTime" label="创建时间">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewClass(scope.row.id)">查看</el-button>
            <el-button 
              size="small" 
              type="primary" 
              @click="editClass(scope.row)"
              v-if="userStore.role === 'admin'"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteClass(scope.row.id)"
              v-if="userStore.role === 'admin'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
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
    
    <!-- 创建/编辑班级对话框 -->
    <el-dialog 
      v-model="showCreateClassDialog" 
      :title="currentClass.id ? '编辑班级' : '新增班级'" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentClass" 
        :rules="classRules" 
        ref="classFormRef" 
        label-width="100px"
      >
        <el-form-item label="班级名称" prop="name">
          <el-input 
            v-model="currentClass.name" 
            placeholder="请输入班级名称"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="currentClass.description" 
            type="textarea" 
            placeholder="请输入班级描述"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelClassOperation">取消</el-button>
        <el-button type="primary" @click="saveClass" :loading="saving">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { classApi } from '@/api'
import { useUserStore } from '@/stores'

interface ClassItem {
  id?: number
  name: string
  description?: string
  createTime?: string
}

const userStore = useUserStore()

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
      page: currentPage.value - 1, // 后端分页通常从0开始
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

const viewClass = (classId: number) => {
  // 跳转到班级详情页面（如果存在的话）
  console.log(`View class ${classId}`)
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
      // 更新班级
      response = await classApi.updateClass(currentClass.id, {
        name: currentClass.name,
        description: currentClass.description
      })
    } else {
      // 创建班级
      response = await classApi.createClass({
        name: currentClass.name,
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
  currentClass.description = ''
}

const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadClassList()
})
</script>

<style scoped>
.classes-container {
  padding: 2rem;
  min-height: calc(100vh - 80px);
}

.classes-card {
  max-width: 1200px;
  margin: 0 auto;
  border: none;
  border-radius: 16px;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.12);
  overflow: hidden;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.1rem;
  font-weight: 600;
}

.pagination {
  margin-top: 1.5rem;
  display: flex;
  justify-content: center;
}
</style>