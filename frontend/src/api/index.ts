import axios, { type AxiosInstance, type AxiosResponse } from 'axios'

const apiClient: AxiosInstance = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

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
  }
}

export default apiClient