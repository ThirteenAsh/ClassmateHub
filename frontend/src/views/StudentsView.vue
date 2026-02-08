<template>
  <div class="students-container">
    <el-card class="students-card">
      <template #header>
        <div class="card-header">
          <span>学生信息管理</span>
          <el-button 
            type="primary" 
            @click="showCreateStudentDialog = true"
          >
            新增学生
          </el-button>
        </div>
      </template>
      
      <el-table 
        :data="studentList" 
        style="width: 100%" 
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="basic.name" label="姓名" />
        <el-table-column prop="basic.studentId" label="学号" />
        <el-table-column prop="basic.major" label="专业" />
        <el-table-column prop="basic.graduationYear" label="毕业年份" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewStudent(scope.row.id)">查看</el-button>
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
    
    <!-- 创建/编辑学生对话框 -->
    <el-dialog 
      v-model="showCreateStudentDialog" 
      :title="currentStudent.id ? '编辑学生' : '新增学生'" 
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="currentStudent" 
        :rules="studentRules" 
        ref="studentFormRef" 
        label-width="120px"
        :inline="true"
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
            <el-form-item label="学号" prop="basic.studentId">
              <el-input 
                v-model="currentStudent.basic.studentId" 
                placeholder="请输入学号"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
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
          <el-col :span="12">
            <el-form-item label="出生日期" prop="basic.birthDate">
              <el-date-picker
                v-model="currentStudent.basic.birthDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业" prop="basic.major">
              <el-input 
                v-model="currentStudent.basic.major" 
                placeholder="请输入专业"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="毕业年份" prop="basic.graduationYear">
              <el-input 
                v-model="currentStudent.basic.graduationYear" 
                placeholder="请输入毕业年份"
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
            <el-form-item label="微信" prop="contact.wechat">
              <el-input 
                v-model="currentStudent.contact.wechat" 
                placeholder="请输入微信"
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
        
        <el-divider content-position="left">学业信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学历" prop="education.degree">
              <el-select 
                v-model="currentStudent.education.degree" 
                placeholder="请选择学历"
                style="width: 100%"
              >
                <el-option label="专科" value="专科" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入学年份" prop="education.admissionYear">
              <el-input 
                v-model="currentStudent.education.admissionYear" 
                placeholder="请输入入学年份"
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
import { studentApi } from '@/api'

interface BasicInfo {
  name?: string
  studentId?: string
  gender?: string
  birthDate?: string
  major?: string
  graduationYear?: string
}

interface ContactInfo {
  phone?: string
  email?: string
  wechat?: string
  qq?: string
}

interface EducationInfo {
  degree?: string
  admissionYear?: string
}

interface StudentProfile {
  id?: number
  userId?: number
  clazzId?: number
  basic: BasicInfo
  contact: ContactInfo
  education: EducationInfo
  personal?: any
  avatar?: string
  createTime?: string
}

const studentList = ref<StudentProfile[]>([])
const loading = ref(false)
const saving = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showCreateStudentDialog = ref(false)

const currentStudent = reactive<StudentProfile>({
  basic: {},
  contact: {},
  education: {}
})

const studentRules = {
  // Basic info validation would go here
}

const studentFormRef = ref()

const loadStudentList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1, // 后端分页通常从0开始
      size: pageSize.value
    }
    
    const response = await studentApi.getStudentList(params)
    if (response.data.code === 0) {
      studentList.value = response.data.data.items || []
      total.value = response.data.data.total || 0
    } else {
      ElMessage.error(response.data.message || '获取学生列表失败')
    }
  } catch (error) {
    console.error('Failed to load student list:', error)
    ElMessage.error('获取学生列表失败')
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

const viewStudent = (studentId: number) => {
  // 跳转到学生详情页面（如果存在的话）
  console.log(`View student ${studentId}`)
}

const editStudent = (student: StudentProfile) => {
  Object.assign(currentStudent, JSON.parse(JSON.stringify(student)))
  showCreateStudentDialog.value = true
}

const deleteStudent = async (studentId: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个学生信息吗？此操作不可恢复',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await studentApi.deleteStudentProfile(studentId)
    if (response.data.code === 200) {
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
    // Note: We're not validating individual fields since they're nested
    saving.value = true

    let response;
    if (currentStudent.id) {
      // 更新学生信息
      response = await studentApi.updateStudentProfile(currentStudent.id, currentStudent)
    } else {
      // 创建学生信息
      response = await studentApi.createStudentProfile(currentStudent)
    }

    if (response.data.code === 200) {
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

const cancelStudentOperation = () => {
  showCreateStudentDialog.value = false
  resetCurrentStudent()
}

const resetCurrentStudent = () => {
  currentStudent.id = undefined
  currentStudent.basic = {}
  currentStudent.contact = {}
  currentStudent.education = {}
  currentStudent.personal = {}
  currentStudent.avatar = undefined
  currentStudent.createTime = undefined
}

onMounted(() => {
  loadStudentList()
})
</script>

<style scoped>
.students-container {
  padding: 2rem;
}

.students-card {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 1.5rem;
  display: flex;
  justify-content: center;
}

.el-divider {
  margin: 20px 0;
}
</style>