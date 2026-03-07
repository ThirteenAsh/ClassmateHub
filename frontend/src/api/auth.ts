import apiClient, { type ApiResponse } from './index'
import type { AxiosResponse } from 'axios'

export interface AuthCredentials {
  username: string
  password: string
}

export interface AuthResponse {
  token?: string
  username?: string
  message?: string
}

export const authApi = {
  login: (credentials: AuthCredentials): Promise<AxiosResponse<ApiResponse<AuthResponse>>> => {
    return apiClient.post('/auth/login', credentials)
  },

  register: (credentials: AuthCredentials): Promise<AxiosResponse<ApiResponse<AuthResponse>>> => {
    return apiClient.post('/auth/register', credentials)
  },

  logout: (): Promise<AxiosResponse<ApiResponse<null>>> => {
    return apiClient.post('/auth/logout', {})
  }
}
