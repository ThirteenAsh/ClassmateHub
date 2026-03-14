import axios, { type AxiosInstance, type AxiosResponse } from 'axios'

const apiClient: AxiosInstance = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor to add Bearer token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle 401 Unauthorized - clear auth state and redirect to login
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      // Optionally redirect to login - this would require router context
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

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

export interface StudentListParams {
  classId?: number;
  name?: string;
  gender?: string;
  page: number;
  size: number;
}

export const classApi = {
  getClassList: (params?: { page: number; size: number }): Promise<AxiosResponse<ApiResponse<PageResponse<any>>>> => {
    return apiClient.get('/admin/classes', { params })
  },
  
  createClass: (data: { name: string; createTime?: string; description?: string }): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.post('/admin/classes', data)
  },
  
  updateClass: (classId: number, data: { name: string; createTime?: string; description?: string }): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.put(`/admin/classes/${classId}`, data)
  },
  
  deleteClass: (classId: number): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.delete(`/admin/classes/${classId}`)
  }
}

export const userApi = {
  getClassListForUser: (params?: { page: number; size: number }): Promise<AxiosResponse<ApiResponse<PageResponse<any>>>> => {
    return apiClient.get('/classes/list', { params })
  },
  
  getClassDetailForUser: (classId: number): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.get(`/classes/detail/${classId}`)
  }
}

export const studentApi = {
  getStudentList: (params?: StudentListParams): Promise<AxiosResponse<ApiResponse<PageResponse<any>>>> => {
    return apiClient.get('/students', { params })
  },
  
  createStudentProfile: (data: any): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.post('/students', data)
  },
  
  updateStudentProfile: (studentId: number, data: any): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.put(`/students/${studentId}`, data)
  },
  
  deleteStudentProfile: (studentId: number): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.delete(`/students/${studentId}`)
  },

  getStatisticsSummary: (): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.get('/students/statistics/summary')
  }
}

export const adminApi = {
  changePassword: (data: { studentId: number; newPassword: string }): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.post('/admin/change-password', data)
  },

  setAdmin: (data: { studentId: number }): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.post('/admin/set-admin', data)
  }
}

export const squareApi = {
  getSquareList: (): Promise<AxiosResponse<ApiResponse<any[]>>> => {
    return apiClient.get('/square')
  }
}

export const profileApi = {
  getProfile: (): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.get('/profile/me')
  },
  
  createProfile: (data: any): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.post('/profile', data)
  },
  
  updateProfile: (data: any): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.put('/profile', data)
  }
}

export default apiClient