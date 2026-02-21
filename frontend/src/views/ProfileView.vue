<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人资料</span>
        </div>
      </template>
      
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ userInfo.role }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ userInfo.createTime ? formatDate(userInfo.createTime) : '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="button-group">
        <el-button type="primary" @click="showChangePasswordDialog = true">修改密码</el-button>
      </div>
    </el-card>
    
    <!-- 修改密码对话框 -->
    <el-dialog 
      v-model="showChangePasswordDialog" 
      title="修改密码" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="passwordForm" 
        :rules="passwordRules" 
        ref="passwordFormRef" 
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            placeholder="请输入原密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmNewPassword">
          <el-input 
            v-model="passwordForm.confirmNewPassword" 
            type="password" 
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showChangePasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword" :loading="changePasswordLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api'

interface PasswordForm {
  oldPassword: string
  newPassword: string
  confirmNewPassword: string
}

interface UserInfo {
  id?: number
  username?: string
  email?: string
  role?: string
  createTime?: string
}

const userInfo = ref<UserInfo>({})
const showChangePasswordDialog = ref(false)
const changePasswordLoading = ref(false)

const passwordForm = reactive<PasswordForm>({
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

const validateConfirmNewPassword = (_: any, value: string, callback: Function) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmNewPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmNewPassword, trigger: 'blur' }
  ]
}

const passwordFormRef = ref()

const loadUserInfo = async () => {
  try {
    const response = await authApi.getProfile()
    if (response.data.code === 0) {
      userInfo.value = response.data.data
    } else {
      ElMessage.error('获取用户信息失败')
    }
  } catch (error) {
    console.error('Failed to load user info:', error)
    ElMessage.error('获取用户信息失败')
  }
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    changePasswordLoading.value = true

    const response = await authApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })

    if (response.data.code === 0) {
      ElMessage.success('密码修改成功')
      showChangePasswordDialog.value = false
      // 重置表单
      Object.keys(passwordForm).forEach(key => {
        (passwordForm as any)[key] = ''
      })
    } else {
      ElMessage.error(response.data.message || '密码修改失败')
    }
  } catch (error) {
    console.error('Change password error:', error)
    ElMessage.error('密码修改失败')
  } finally {
    changePasswordLoading.value = false
  }
}

const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 2rem;
  min-height: calc(100vh - 80px);
}

.profile-card {
  max-width: 800px;
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
}

.button-group {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}
</style>