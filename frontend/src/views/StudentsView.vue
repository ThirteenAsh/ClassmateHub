<template>
  <div class="students-container">
    <el-card class="students-card">
      <template #header>
        <div class="card-header">
          <span>同学信息管理</span>
          <el-button 
            type="primary" 
            @click="showCreateStudentDialog = true"
          >
            新增同学
          </el-button>
        </div>
      </template>
      
      <el-table 
        :data="studentList" 
        style="width: 100%" 
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="basic.name" label="姓名" width="120" />
        <el-table-column prop="basic.gender" label="性别" width="80" />
        <el-table-column label="班级" width="150">
          <template #default="scope">
            {{ getClassName(scope.row.clazzId) }}
          </template>
        </el-table-column>
        <el-table-column label="出生日期（阳历）" width="150">
          <template #default="scope">
            {{ formatDate(scope.row.basic.birthDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="contact.phone" label="电话" min-width="150" />
        <el-table-column label="操作" width="150" fixed="right" align="left">
          <template #default="scope">
            <el-button 
              size="small" 
              type="primary" 
              @click="editStudent(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteStudent(scope.row.id)"
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
    
    <!-- 创建/编辑同学对话框 -->
    <el-dialog 
      v-model="showCreateStudentDialog" 
      :title="currentStudent.id ? '编辑同学' : '新增同学'" 
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentStudent" 
        :rules="studentRules" 
        ref="studentFormRef" 
        label-width="120px"
        :label-position="'top'"
      >
        <el-divider content-position="left">基础信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="basic.name">
              <el-input 
                v-model="currentStudent.basic.name" 
                placeholder="请输入姓名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="basic.gender">
              <el-select 
                v-model="currentStudent.basic.gender" 
                placeholder="请选择性别"
                style="width: 100%"
              >
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="出生日期（阳历）" prop="basic.birthDate">
              <el-date-picker
                v-model="currentStudent.basic.birthDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">联系方式</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电话" prop="contact.phone">
              <el-input 
                v-model="currentStudent.contact.phone" 
                placeholder="请输入电话"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="contact.email">
              <el-input 
                v-model="currentStudent.contact.email" 
                placeholder="请输入邮箱"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="微信号" prop="contact.wechat">
              <el-input 
                v-model="currentStudent.contact.wechat" 
                placeholder="请输入微信号"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="QQ" prop="contact.qq">
              <el-input 
                v-model="currentStudent.contact.qq" 
                placeholder="请输入QQ"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">班级信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="第一次认识我是在哪个班？" prop="clazzId">
              <el-select 
                v-model="currentStudent.clazzId" 
                placeholder="请选择班级"
                style="width: 100%"
              >
                <el-option 
                  v-for="clazz in classList" 
                  :key="clazz.id" 
                  :label="clazz.name" 
                  :value="clazz.id" 
                >
                  <div style="display: flex; align-items: center;">
                    <span>{{ clazz.name }}</span>
                    <span v-if="clazz.description" style="color: #909399; font-size: 12px; margin-left: 16px;">{{ clazz.description }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">留言</el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="留言" prop="message">
              <el-input
                v-model="currentStudent.message"
                type="textarea"
                placeholder="请输入留言（最多256个字符，请随意留言，此部分内容不会公开，可以把你想说的话悄悄告诉我呦~）"
                :rows="4"
                maxlength="256"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">头像</el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="头像URL" prop="avatar">
              <el-input
                v-model="currentStudent.avatar"
                placeholder="请输入头像URL（例如：https://example.com/avatar.jpg）"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="cancelStudentOperation">取消</el-button>
        <el-button type="primary" @click="saveStudent" :loading="saving">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { studentApi, classApi } from '@/api'

interface BasicInfo {
  name?: string
  gender?: string
  birthDate?: string
}

interface ContactInfo {
  phone?: string
  email?: string
  wechat?: string
  qq?: string
}

interface StudentProfile {
  id?: number
  clazzId?: number
  basic: BasicInfo
  contact: ContactInfo
  personal?: any
  avatar?: string
  message?: string
  createTime?: string
}

interface ClassItem {
  id?: number
  name: string
  createTime?: string
  description?: string
}

const studentList = ref<StudentProfile[]>([])
const classList = ref<ClassItem[]>([])
const loading = ref(false)
const saving = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showCreateStudentDialog = ref(false)

const currentStudent = reactive<StudentProfile>({
  basic: {},
  contact: {}
})

const studentRules = {
  'basic.name': [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应在2-20个字符之间', trigger: 'blur' }
  ],
  'basic.gender': [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  'basic.birthDate': [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  'contact.phone': [
    { required: true, message: '请输入电话', trigger: 'blur' }
  ],
  'contact.wechat': [
    { required: true, message: '请输入微信号', trigger: 'blur' }
  ],
  'clazzId': [
    { required: true, message: '请选择班级', trigger: 'change' }
  ],
  'message': [
    { required: true, message: '请输入留言', trigger: 'blur' },
    { max: 256, message: '留言不能超过256个字符', trigger: 'blur' }
  ],
  'contact.email': [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const studentFormRef = ref()

const loadClassList = async () => {
  try {
    const params = {
      page: 0,
      size: 100
    }
    
    const response = await classApi.getClassList(params)
    if (response.data.code === 0) {
      classList.value = response.data.data.items || []
    } else {
      ElMessage.error(response.data.message || '获取班级列表失败')
    }
  } catch (error) {
    console.error('Failed to load class list:', error)
    ElMessage.error('获取班级列表失败')
  }
}

const loadStudentList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    const response = await studentApi.getStudentList(params)
    if (response.data.code === 0) {
      studentList.value = response.data.data.items || []
      total.value = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取同学列表失败')
    }
  } catch (error) {
    console.error('Failed to load student list:', error)
    ElMessage.error('获取同学列表失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadStudentList()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadStudentList()
}

const editStudent = (student: StudentProfile) => {
  const studentCopy = JSON.parse(JSON.stringify(student))
  currentStudent.id = studentCopy.id
  currentStudent.clazzId = studentCopy.clazzId
  currentStudent.basic = studentCopy.basic || {}
  currentStudent.contact = studentCopy.contact || {}
  currentStudent.personal = studentCopy.personal || {}
  currentStudent.avatar = studentCopy.avatar
  currentStudent.message = studentCopy.message
  currentStudent.createTime = studentCopy.createTime
  showCreateStudentDialog.value = true
}

const deleteStudent = async (studentId: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个同学信息吗？此操作不可恢复',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await studentApi.deleteStudentProfile(studentId)
    if (response.data.code === 0) {
      ElMessage.success('删除成功')
      loadStudentList()
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    console.error('Delete student error:', error)
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const saveStudent = async () => {
  if (!studentFormRef.value) return

  try {
    await validateStudentForm()
    saving.value = true

    const requestData = {
      basic: {
        name: currentStudent.basic?.name,
        gender: currentStudent.basic?.gender,
        birthDate: currentStudent.basic?.birthDate ? formatDateForApi(currentStudent.basic.birthDate) : undefined
      },
      contact: {
        phone: currentStudent.contact?.phone,
        email: currentStudent.contact?.email,
        wechat: currentStudent.contact?.wechat,
        qq: currentStudent.contact?.qq
      },
      clazzId: currentStudent.clazzId,
      message: currentStudent.message,
      avatar: currentStudent.avatar
    }

    let response;
    if (currentStudent.id) {
      response = await studentApi.updateStudentProfile(currentStudent.id, requestData)
    } else {
      response = await studentApi.createStudentProfile(requestData)
    }

    if (response.data.code === 0) {
      ElMessage.success(currentStudent.id ? '更新成功' : '创建成功')
      showCreateStudentDialog.value = false
      resetCurrentStudent()
      loadStudentList()
    } else {
      ElMessage.error(response.data.message || (currentStudent.id ? '更新失败' : '创建失败'))
    }
  } catch (error) {
    console.error('Save student error:', error)
    ElMessage.error(currentStudent.id ? '更新失败' : '创建失败')
  } finally {
    saving.value = false
  }
}

const formatDateForApi = (date: Date | string): string => {
  if (typeof date === 'string') {
    return date
  }
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const validateStudentForm = async () => {
  if (!currentStudent.basic?.name) {
    throw new Error('请输入姓名')
  }
  if (!currentStudent.basic?.gender) {
    throw new Error('请选择性别')
  }
  if (!currentStudent.basic?.birthDate) {
    throw new Error('请选择出生日期')
  }
  if (!currentStudent.contact?.phone) {
    throw new Error('请输入电话')
  }
  if (!currentStudent.contact?.wechat) {
    throw new Error('请输入微信号')
  }
  if (!currentStudent.clazzId) {
    throw new Error('请选择班级')
  }
  
  return studentFormRef.value.validate().catch((err: any) => {
    console.error('Form validation error:', err)
    throw err
  })
}

const cancelStudentOperation = () => {
  showCreateStudentDialog.value = false
  resetCurrentStudent()
}

const resetCurrentStudent = () => {
  currentStudent.id = undefined
  currentStudent.basic = {}
  currentStudent.contact = {}
  currentStudent.personal = {}
  currentStudent.avatar = undefined
  currentStudent.message = undefined
  currentStudent.createTime = undefined
}

const getClassName = (clazzId: number) => {
  const clazz = classList.value.find(c => c.id === clazzId)
  return clazz ? clazz.name : '-'
}

const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

onMounted(() => {
  loadClassList()
  loadStudentList()
})
</script>

<style scoped>
.students-container {
  padding: 2rem;
  min-height: calc(100vh - 80px);
}

.students-card {
  max-width: 1400px;
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

.el-divider {
  margin: 20px 0;
}

.el-form {
  padding: 0 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-row {
  margin-bottom: 0;
}
</style>