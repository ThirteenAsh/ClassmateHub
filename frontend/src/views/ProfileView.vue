<template>
  <div class="profile-container">
    <div class="decoration-circle circle-1"></div>
    <div class="decoration-circle circle-2"></div>

    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button 
              type="default" 
              :icon="ArrowLeft"
              @click="goBack"
              class="cute-btn-default"
            >
              返回
            </el-button>
            <span class="page-title">个人信息</span>
          </div>
          <div class="header-actions" v-if="profile">
            <el-button 
              type="primary" 
              @click="editProfile"
              class="cute-btn-primary"
            >
              编辑信息
            </el-button>
            <el-button 
              type="primary" 
              @click="openChangePasswordDialog"
              class="cute-btn-primary header-change-password-btn"
            >
              修改密码
            </el-button>
          </div>
        </div>
      </template>
      
      <div v-if="isLoading" class="loading-section">
        <div class="loading-spinner">
          <el-icon class="is-loading" :size="40"><Loading /></el-icon>
        </div>
        <p class="loading-text">正在加载个人信息...</p>
      </div>

      <div v-else-if="error" class="error-section">
        <div class="error-card">
          <el-icon :size="60"><WarningFilled /></el-icon>
          <h3 class="error-title">{{ error }}</h3>
          <el-button type="primary" @click="handleRetry">重试</el-button>
        </div>
      </div>

      <div v-else-if="!profile" class="no-profile-section">
        <div class="no-profile-card">
          <div class="no-profile-icon">
            <el-icon :size="80"><User /></el-icon>
          </div>
          <h2 class="no-profile-title">暂无个人信息</h2>
          <p class="no-profile-desc">请先完善您的个人信息，以便与同学们建立联系</p>
          <div class="no-profile-actions">
            <el-button 
              type="primary" 
              @click="showDialog = true"
              class="cute-btn-primary"
            >
              新增个人信息
            </el-button>
            <el-button 
              type="primary" 
              plain
              @click="openChangePasswordDialog"
              class="cute-btn-primary"
            >
              修改密码
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="profile-content">
        <div class="profile-info">
          <div class="info-header">
            <div class="avatar-wrapper">
              <el-avatar
                :size="80"
                :src="profile.avatar || ''"
                class="profile-avatar"
              >
                <el-icon :size="36" class="avatar-icon"><User /></el-icon>
              </el-avatar>
            </div>
            <div class="info-text">
              <h2 class="profile-name">{{ profile.basic?.name }}</h2>
              <span class="profile-gender">{{ profile.basic?.gender === '男' ? '男生' : '女生' }}</span>
            </div>
          </div>

          <div class="info-section-title">基础信息</div>
          <div class="info-details">
            <div class="detail-item">
              <el-icon class="detail-icon"><Calendar /></el-icon>
              <div class="detail-content">
                <span class="detail-label">生日</span>
                <span class="detail-value">{{ profile.basic?.birthDate || '-' }}</span>
              </div>
            </div>

            <div class="detail-item">
              <el-icon class="detail-icon"><School /></el-icon>
              <div class="detail-content">
                <span class="detail-label">大学</span>
                <span class="detail-value">{{ profile.basic?.university || '-' }}</span>
              </div>
            </div>

            <div class="detail-item">
              <el-icon class="detail-icon"><OfficeBuilding /></el-icon>
              <div class="detail-content">
                <span class="detail-label">班级</span>
                <span class="detail-value">{{ profile.clazzId ? getClassName(profile.clazzId) : '-' }}</span>
              </div>
            </div>
          </div>

          <div class="info-section-title">联系方式</div>
          <div class="info-details">
            <div class="detail-item">
              <el-icon class="detail-icon"><Phone /></el-icon>
              <div class="detail-content">
                <span class="detail-label">电话</span>
                <span class="detail-value">{{ profile.contact?.phone || '-' }}</span>
              </div>
            </div>

            <div class="detail-item">
              <el-icon class="detail-icon"><Message /></el-icon>
              <div class="detail-content">
                <span class="detail-label">邮箱</span>
                <span class="detail-value">{{ profile.contact?.email || '-' }}</span>
              </div>
            </div>

            <div class="detail-item">
              <el-icon class="detail-icon"><ChatLineRound /></el-icon>
              <div class="detail-content">
                <span class="detail-label">微信</span>
                <span class="detail-value">{{ profile.contact?.wechat || '-' }}</span>
              </div>
            </div>

            <div class="detail-item">
              <el-icon class="detail-icon"><ChatDotRound /></el-icon>
              <div class="detail-content">
                <span class="detail-label">QQ</span>
                <span class="detail-value">{{ profile.contact?.qq || '-' }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="message-section">
          <div class="message-header">
            <div class="message-title">
              <el-icon class="message-icon"><ChatRound /></el-icon>
              <span>留言</span>
            </div>
          </div>
          <div class="message-body">
            <div v-if="profile.message" class="message-text">{{ profile.message }}</div>
            <div v-else class="message-empty">暂无留言</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog 
      v-model="showDialog" 
      title="🐬 新增/编辑个人信息" 
      width="800px"
      :close-on-click-modal="false"
      class="cute-dialog"
    >
      <el-form 
        :model="currentProfile" 
        :rules="profileRules" 
        ref="profileFormRef" 
        label-width="120px"
        :label-position="'top'"
        class="cute-form"
      >
        <el-divider content-position="left" class="cute-divider">🌟 基础信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="basic.name">
              <el-input v-model="currentProfile.basic.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="basic.gender">
              <el-select v-model="currentProfile.basic.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期（阳历）" prop="basic.birthDate">
              <el-date-picker
                v-model="currentProfile.basic.birthDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="大学" prop="basic.university">
              <el-input v-model="currentProfile.basic.university" placeholder="请输入大学名称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left" class="cute-divider">📱 联系方式</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电话" prop="contact.phone">
              <el-input v-model="currentProfile.contact.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="contact.email">
              <el-input v-model="currentProfile.contact.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="微信号" prop="contact.wechat">
              <el-input v-model="currentProfile.contact.wechat" placeholder="请输入微信号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="QQ" prop="contact.qq">
              <el-input v-model="currentProfile.contact.qq" placeholder="请输入QQ" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left" class="cute-divider">🏫 班级信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="第一次认识我是在哪个班？" prop="clazzId">
              <el-select 
                v-model="currentProfile.clazzId" 
                placeholder="请选择班级" 
                style="width: 100%"
                :disabled="!!profile"
              >
                <el-option 
                  v-for="clazz in classList" 
                  :key="clazz.id" 
                  :label="clazz.name" 
                  :value="clazz.id" 
                >
                  <div style="display: flex; align-items: center;">
                    <span>{{ clazz.name }}</span>
                    <span v-if="clazz.description" style="color: #9fa0b3; font-size: 12px; margin-left: 16px;">{{ clazz.description }}</span>
                    <span v-if="clazz.createTime" style="color: #9fa0b3; font-size: 12px; margin-left: 16px;">{{ formatDate(clazz.createTime) }}</span>
                  </div>
                </el-option>
              </el-select>
              <p v-if="profile" class="class-notice">
                <el-icon><InfoFilled /></el-icon>
                班级信息提交后不可修改，如需修改请联系管理员。
              </p>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left" class="cute-divider">💌 留言与头像</el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="留言" prop="message">
              <el-input
                v-model="currentProfile.message"
                type="textarea"
                placeholder="请随意留言，此部分内容不会公开，可以把你想说的话悄悄告诉我呦~"
                :rows="4"
                maxlength="256"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="头像URL" prop="avatar">
              <el-input
                v-model="currentProfile.avatar"
                placeholder="请输入头像URL（例如：https://example.com/avatar.jpg）"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showDialog = false" class="cute-btn-default">取消</el-button>
          <el-button type="primary" @click="saveProfile" :loading="saving" class="cute-btn-primary">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      v-model="showChangePasswordDialog"
      title="🔐 修改密码"
      width="420px"
      :close-on-click-modal="false"
      class="cute-dialog"
    >
      <el-form
        ref="changePasswordFormRef"
        :model="changePasswordForm"
        :rules="changePasswordRules"
        label-width="120px"
        :label-position="'top'"
        class="cute-form"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="changePasswordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入原密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="changePasswordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码（不少于 6 位）"
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="changePasswordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showChangePasswordDialog = false" class="cute-btn-default">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleChangePassword" 
            :loading="changePasswordLoading" 
            class="cute-btn-primary"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 移动端底部编辑/修改密码按钮（竖屏下始终可见） -->
    <div v-if="profile" class="mobile-edit-btn-wrapper">
      <div class="mobile-edit-btn-group">
        <el-button
          type="primary"
          @click="editProfile"
          class="cute-btn-primary mobile-edit-btn"
        >
          编辑信息
        </el-button>
        <el-button
          type="primary"
          plain
          @click="openChangePasswordDialog"
          class="cute-btn-primary mobile-edit-btn"
        >
          修改密码
        </el-button>
      </div>
    </div>

    <BottomNav :activeTab="activeTab" @update:activeTab="updateActiveTab" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// @ts-ignore
import { 
  ArrowLeft, 
  User, 
  Calendar, 
  Phone, 
  School,
  OfficeBuilding,
  Message,
  ChatLineRound,
  ChatDotRound,
  ChatRound,
  WarningFilled,
  Loading,
  InfoFilled
} from '@element-plus/icons-vue'
import { profileApi, userApi } from '@/api'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
// @ts-ignore
import BottomNav from '@/components/BottomNav.vue'

interface BasicInfo {
  name?: string
  gender?: string
  birthDate?: string
  university?: string
}

interface ContactInfo {
  phone?: string
  email?: string
  wechat?: string
  qq?: string
}

interface Profile {
  id?: number
  clazzId?: number
  basic: BasicInfo
  contact: ContactInfo
  personal?: any
  avatar?: string
  message?: string
  createTime?: string
}

interface ProfileDTO {
  id?: number
  userId?: number
  clazzId?: number
  name?: string
  gender?: string
  birthDate?: string
  phone?: string
  university?: string
  email?: string
  wechat?: string
  qq?: string
  message?: string
  avatar?: string
}

interface ClassItem {
  id?: number
  name: string
  createTime?: string
  description?: string
}

const router = useRouter()
const authStore = useAuthStore()
const activeTab = ref('profile')
const isLoading = ref(true)
const saving = ref(false)
const changePasswordLoading = ref(false)
const error = ref('')
const showDialog = ref(false)
const showChangePasswordDialog = ref(false)
const profile = ref<Profile | null>(null)
const classList = ref<ClassItem[]>([])

const currentProfile = reactive<Profile>({
  basic: {
    name: '',
    gender: '',
    birthDate: '',
    university: ''
  },
  contact: {
    phone: '',
    email: '',
    wechat: '',
    qq: ''
  }
})

const profileRules = {
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
  'basic.university': [
    { required: true, message: '请输入大学名称', trigger: 'blur' }
  ],
  'contact.phone': [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { min: 11, max: 11, message: '手机号必须为 11 位', trigger: 'blur' }
  ],
  'contact.qq': [
    { required: true, message: '请输入QQ', trigger: 'blur' }
  ],
  'clazzId': [
    { required: true, message: '请选择班级', trigger: 'change' }
  ],
  'message': [
    { max: 256, message: '留言不能超过256个字符', trigger: 'blur' }
  ],
  'contact.email': [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const profileFormRef = ref()
const changePasswordFormRef = ref()

const changePasswordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule: any, value: string, callback: (error?: Error) => void) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
    return
  }
  if (value !== changePasswordForm.newPassword) {
    callback(new Error('两次输入的新密码不一致'))
    return
  }
  callback()
}

const changePasswordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能少于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: ['blur', 'change'] }
  ]
}

const transformProfileFromApi = (dto: ProfileDTO): Profile => {
  return {
    id: dto.id,
    clazzId: dto.clazzId,
    basic: {
      name: dto.name || '',
      gender: dto.gender || '',
      birthDate: dto.birthDate || '',
      university: dto.university || ''
    },
    contact: {
      phone: dto.phone || '',
      email: dto.email || '',
      wechat: dto.wechat || '',
      qq: dto.qq || ''
    },
    avatar: dto.avatar,
    message: dto.message
  }
}

const buildRequestFromProfile = (profileData: Profile) => {
  return {
    clazzId: profileData.clazzId,
    name: profileData.basic?.name,
    gender: profileData.basic?.gender,
    birthDate: profileData.basic?.birthDate
      ? formatDateForApi(profileData.basic.birthDate as unknown as Date | string)
      : undefined,
    phone: profileData.contact?.phone,
    university: profileData.basic?.university,
    email: profileData.contact?.email,
    wechat: profileData.contact?.wechat,
    qq: profileData.contact?.qq,
    message: profileData.message,
    avatar: profileData.avatar
  }
}

const loadClassList = async () => {
  try {
    const params = {
      page: 0,
      size: 100
    }
    
    const response = await userApi.getClassListForUser(params)
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

const loadProfile = async () => {
  isLoading.value = true
  error.value = ''
  
  try {
    const response = await profileApi.getProfile()
    
    if (response.data.code === 0) {
      if (response.data.data) {
        profile.value = transformProfileFromApi(response.data.data as ProfileDTO)
      } else {
        profile.value = null
      }
    } else if (response.data.code === -1) {
      error.value = response.data.message || '未登录'
      ElMessage.warning(error.value)
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      profile.value = null
    }
  } catch (err: any) {
    error.value = '网络错误，请稍后重试'
    ElMessage.error(error.value)
  } finally {
    isLoading.value = false
  }
}

const handleRetry = () => {
  loadProfile()
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

const editProfile = () => {
  if (profile.value) {
    currentProfile.id = profile.value.id
    currentProfile.clazzId = profile.value.clazzId
    currentProfile.basic = profile.value.basic || {}
    currentProfile.contact = profile.value.contact || {}
    currentProfile.personal = profile.value.personal || {}
    currentProfile.avatar = profile.value.avatar
    currentProfile.message = profile.value.message
    currentProfile.createTime = profile.value.createTime
  }
  showDialog.value = true
}

const openChangePasswordDialog = () => {
  changePasswordForm.oldPassword = ''
  changePasswordForm.newPassword = ''
  changePasswordForm.confirmPassword = ''
  showChangePasswordDialog.value = true
}

const handleChangePassword = async () => {
  if (!changePasswordFormRef.value) return

  try {
    await changePasswordFormRef.value.validate()
    changePasswordLoading.value = true

    const response = await authApi.changePassword({
      oldPassword: changePasswordForm.oldPassword,
      newPassword: changePasswordForm.newPassword
    })

    if (response.data.code === 0) {
      ElMessage.success(response.data.message || '修改密码成功，请重新登录')
      showChangePasswordDialog.value = false
      authStore.clearAuth()
      router.push('/login')
    } else if (response.data.code === -1) {
      ElMessage.warning(response.data.message || '未登录')
      authStore.clearAuth()
      router.push('/login')
    } else {
      ElMessage.error(response.data.message || '修改密码失败')
    }
  } catch (error: any) {
    console.error('Change password error:', error)
    if (error instanceof Error && (error.message.includes('请输入') || error.message.includes('两次输入的新密码不一致'))) {
      ElMessage.error(error.message)
    } else if (error?.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('修改密码失败')
    }
  } finally {
    changePasswordLoading.value = false
  }
}

const saveProfile = async () => {
  if (!profileFormRef.value) return

  try {
    await profileFormRef.value.validate()
    saving.value = true

    const requestData = buildRequestFromProfile(currentProfile)

    let response
    if (profile.value) {
      response = await profileApi.updateProfile(requestData)
    } else {
      response = await profileApi.createProfile(requestData)
    }

    if (response.data.code === 0) {
      ElMessage.success(profile.value ? '更新成功' : '创建成功')
      showDialog.value = false
      loadProfile()
    } else {
      ElMessage.error(response.data.message || (profile.value ? '更新失败' : '创建失败'))
    }
  } catch (error) {
    console.error('Save profile error:', error)
    if (error instanceof Error && (error.message.includes('请输入') || error.message.includes('请选择'))) {
      ElMessage.error('请填写所有必填项')
    } else {
      ElMessage.error(profile.value ? '更新失败' : '创建失败')
    }
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

const goBack = () => {
  router.push('/')
}

const updateActiveTab = (tab: string) => {
  activeTab.value = tab
}

onMounted(() => {
  loadClassList()
  loadProfile()
})
</script>

<style scoped src="../styles/message.css"></style>

<style scoped>
* {
  font-family: 'Nunito', 'Quicksand', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.profile-container {
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

.profile-card {
  max-width: 800px;
  margin: 2rem auto;
  border-radius: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  border: 2px solid #fff;
}

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

:deep(.cute-divider .el-divider__text) {
  font-weight: 700;
  color: #ff758f;
  background-color: #fff;
  padding: 0 15px;
}

:deep(.cute-divider.el-divider--horizontal) {
  border-top: 2px dashed #ffc8dd;
}

:deep(.cute-form .el-form-item__label) {
  font-weight: 600;
  color: #4a4e69;
}

:deep(.cute-form .el-input__wrapper),
:deep(.cute-form .el-textarea__inner),
:deep(.cute-form .el-select__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #ffeaee inset;
}

:deep(.cute-form .el-input__wrapper.is-focus),
:deep(.cute-form .el-textarea__inner:focus),
:deep(.cute-form .el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 2px #ffc8dd inset !important;
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
}

.error-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #ff6b6b;
  margin: 1rem 0 1.5rem 0;
}

.no-profile-section {
  padding: 2rem;
}

.no-profile-card {
  background: #ffffff;
  border-radius: 32px;
  padding: 4rem 2rem;
  text-align: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.03);
  border: 2px dashed #ffc8dd;
  max-width: 500px;
  margin: 0 auto;
}

.no-profile-icon {
  width: 120px;
  height: 120px;
  margin: 0 auto 2rem;
  border-radius: 50%;
  background: #fff0f3;
  color: #ff8fab;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-profile-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #4a4e69;
  margin: 0 0 1rem 0;
}

.no-profile-desc {
  font-size: 1rem;
  color: #9fa0b3;
  margin: 0 0 2rem 0;
  font-weight: 500;
}

.profile-content {
  padding: 2rem;
}

.profile-info {
  background: #ffffff;
  border-radius: 24px;
  padding: 2rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  border: 2px solid #fff;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.avatar-wrapper {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: linear-gradient(135deg, #fff0f3 0%, #ffc8dd 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 2px solid #fff;
  box-shadow: 0 6px 18px rgba(255, 117, 143, 0.18);
  padding: 4px;
}

.profile-avatar {
  border: 2px solid #fff;
  box-shadow: 0 6px 18px rgba(255, 117, 143, 0.15);
  background: #fff;
}

.avatar-icon {
  color: #ff8fab;
}

.info-text {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.profile-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: #4a4e69;
  margin: 0;
}

.profile-gender {
  font-size: 0.9rem;
  color: #9fa0b3;
  font-weight: 500;
}

.info-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.info-section-title {
  margin: 1.25rem 0 0.75rem;
  font-size: 0.95rem;
  font-weight: 800;
  color: #ff758f;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-section-title::after {
  content: '';
  height: 2px;
  flex: 1;
  background: linear-gradient(90deg, rgba(255, 117, 143, 0.35), rgba(189, 224, 254, 0.25));
  border-radius: 999px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  padding: 1rem;
  background: #fff5f7;
  border-radius: 16px;
  border: 1px solid rgba(255, 200, 221, 0.55);
  transition: transform 0.25s ease, box-shadow 0.25s ease, background 0.25s ease;
}

.detail-item:hover {
  background: #fff0f3;
  transform: translateY(-2px);
  box-shadow: 0 8px 22px rgba(255, 117, 143, 0.12);
}

.detail-icon {
  color: #ff8fab;
  font-size: 24px;
  margin-top: 0.25rem;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.detail-label {
  font-size: 0.8rem;
  color: #9fa0b3;
  font-weight: 500;
}

.detail-value {
  font-size: 1rem;
  color: #4a4e69;
  font-weight: 600;
  word-break: break-word;
}

.message-section {
  margin-top: 1.5rem;
  background: #ffffff;
  border-radius: 24px;
  padding: 1.5rem 1.5rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.04);
  border: 2px solid #fff;
}

.message-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}

.message-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 800;
  color: #ff758f;
}

.message-icon {
  color: #ff8fab;
}

.message-body {
  background: linear-gradient(135deg, #fff5f7 0%, #f7fbff 100%);
  border: 1px solid rgba(255, 200, 221, 0.45);
  border-radius: 18px;
  padding: 14px 16px;
}

.message-text {
  color: #4a4e69;
  font-weight: 600;
  line-height: 1.65;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-empty {
  color: #9fa0b3;
  font-weight: 600;
}

.class-notice {
  margin-top: 0.5rem;
  font-size: 0.85rem;
  color: #ff6b6b;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.class-notice :deep(.el-icon) {
  color: #ff6b6b;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #fff;
  border-bottom: 2px dashed #ffc8dd;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #4a4e69;
}

.header-actions :deep(.el-button) {
  padding: 8px 24px;
  font-weight: 600;
  border-radius: 12px;
}

@media (max-width: 768px) {
  .card-header {
    padding: 12px 16px;
  }

  .page-title {
    font-size: 1.2rem;
  }

  .header-actions {
    display: none;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@media (max-width: 768px) {
  .profile-container {
    padding-bottom: 80px;
  }

  .profile-card {
    margin: 1rem;
  }

  .profile-content {
    padding: 1.5rem;
  }

  .info-details {
    grid-template-columns: 1fr;
  }

  .detail-item {
    padding: 0.75rem;
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

.cute-btn-default {
  background: #fff;
  border: 2px solid #ffc8dd;
  color: #ff758f;
  padding: 8px 20px;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.cute-btn-default:hover {
  background: #ffeaee;
  border-color: #ff758f;
  color: #ff758f;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 117, 143, 0.2);
}

.cute-btn-primary {
  background: linear-gradient(135deg, #ff758f 0%, #ff8fab 100%);
  border: none;
  color: #fff;
  padding: 10px 30px;
  font-weight: 700;
  border-radius: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 117, 143, 0.3);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}

.no-profile-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  margin-top: 1.5rem;
}

/* 默认隐藏移动端底部编辑按钮（仅小屏时显示） */
.mobile-edit-btn-wrapper {
  display: none;
}

/* 移动端适配：弹窗与底部编辑按钮 */
@media (max-width: 768px) {
  ::deep(.cute-dialog.el-dialog) {
    width: calc(100vw - 24px) !important;
    margin: 12px auto !important;
  }

  ::deep(.cute-dialog .el-dialog__header) {
    padding: 16px 14px;
  }

  ::deep(.cute-dialog .el-dialog__body) {
    max-height: calc(100vh - 190px);
    overflow: auto;
    padding: 16px 14px;
  }

  .mobile-edit-btn-wrapper {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 70px; /* 位于底部导航栏上方 */
    padding: 0 16px 8px;
    display: flex;
    justify-content: center;
    pointer-events: none; /* 容器不拦截事件 */
    z-index: 1100;
  }

  .mobile-edit-btn-group {
    width: 100%;
    max-width: 480px;
    display: flex;
    flex-direction: row;
    gap: 8px;
    pointer-events: auto;
  }

  .mobile-edit-btn {
    flex: 1;
  }

  .profile-container {
    padding-bottom: 140px; /* 给底部按钮和导航留出空间 */
  }
}

</style>