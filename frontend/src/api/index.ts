import axios, { type AxiosInstance, type AxiosResponse } from 'axios'
import { useUserStore } from '@/stores'

// API 接口定义

// 创建 axios 实例
const apiClient: AxiosInstance = axios.create({
  baseURL: '/api/v1', // 使用代理路径，匹配API文档
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    const token = userStore.token
    
    if (token) {
      config.headers!.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token 过期或无效，跳转到登录页
      const userStore = useUserStore()
      userStore.logout()
      
      // 如果当前不在登录页，则跳转到登录页
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    
    return Promise.reject(error)
  }
)

// API 接口定义
export interface PageResponse<T = any> {
  items: T[];
  page: number;
  pageSize: number;
  total: number;
}

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

// 定义类型接口
export interface UserInfo {
  id: number;
  username: string;
  email: string;
  phone?: string;
  realName?: string;
  studentId?: string;
  role: string;
  avatar?: string;
  status: string;
  createTime?: string;
  updateTime?: string;
}

export interface LoginResponse {
  token: string;
  expiresIn: number;
  user: UserInfo;
}

export const authApi = {
  // 登录
  login: (data: { username: string; password: string }): Promise<AxiosResponse<ApiResponse<LoginResponse>>> => {
    return apiClient.post('/auth/login', data)
  },
  
  // 注册
  register: (data: { username: string; password: string; email: string }): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.post('/auth/register', data)
  },
  
  // 获取用户信息
  getProfile: (): Promise<AxiosResponse<ApiResponse<UserInfo>>> => {
    return apiClient.get('/auth/profile')
  },
  
  // 修改密码
  changePassword: (data: { oldPassword: string; newPassword: string }): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.post('/auth/password/change', data)
  }
}

export const classApi = {
  // 获取班级列表
  getClassList: (params?: { page: number; size: number }): Promise<AxiosResponse<ApiResponse<PageResponse<any>>>> => {
    return apiClient.get('/classes', { params })
  },
  
  // 获取班级详情
  getClassDetail: (classId: number): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.get(`/classes/${classId}`)
  },
  
  // 创建班级
  createClass: (data: { name: string; description?: string }): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.post('/classes', data)
  },
  
  // 更新班级
  updateClass: (classId: number, data: { name: string; description?: string }): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.put(`/classes/${classId}`, data)
  },
  
  // 删除班级
  deleteClass: (classId: number): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.delete(`/classes/${classId}`)
  },
  
  // 添加学生到班级
  addStudentToClass: (classId: number, studentId: number): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.post(`/classes/${classId}/students`, { studentId })
  },
  
  // 从班级移除学生
  removeStudentFromClass: (classId: number, studentId: number): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.delete(`/classes/${classId}/students/${studentId}`)
  }
}

export const studentApi = {
  // 获取学生列表
  getStudentList: (params?: { classId?: number; page: number; size: number }): Promise<AxiosResponse<ApiResponse<PageResponse<any>>>> => {
    return apiClient.get('/students', { params })
  },
  
  // 获取学生详情
  getStudentDetail: (studentId: number): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.get(`/students/${studentId}`)
  },
  
  // 创建学生档案
  createStudentProfile: (data: any): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.post('/students', data)
  },
  
  // 更新学生档案
  updateStudentProfile: (studentId: number, data: any): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.put(`/students/${studentId}`, data)
  },
  
  // 删除学生档案
  deleteStudentProfile: (studentId: number): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.delete(`/students/${studentId}`)
  }
}

export default apiClient