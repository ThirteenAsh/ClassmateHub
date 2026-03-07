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

export const classApi = {
  getClassList: (params?: { page: number; size: number }): Promise<AxiosResponse<ApiResponse<PageResponse<any>>>> => {
    return apiClient.get('/classes', { params })
  },
  
  createClass: (data: { name: string; createTime?: string; description?: string }): Promise<AxiosResponse<ApiResponse<any>>> => {
    return apiClient.post('/classes', data)
  },
  
  updateClass: (classId: number, data: { name: string; createTime?: string; description?: string }): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.put(`/classes/${classId}`, data)
  },
  
  deleteClass: (classId: number): Promise<AxiosResponse<ApiResponse<boolean>>> => {
    return apiClient.delete(`/classes/${classId}`)
  }
}

export const studentApi = {
  getStudentList: (params?: { classId?: number; page: number; size: number }): Promise<AxiosResponse<ApiResponse<PageResponse<any>>>> => {
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

export default apiClient