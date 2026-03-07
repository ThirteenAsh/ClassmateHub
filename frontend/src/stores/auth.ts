import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { AuthCredentials } from '../api/auth'
import { authApi } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const username = ref<string>('')
  const token = ref<string>('')
  const isAuthenticated = ref<boolean>(false)
  const role = ref<string>('')

  // 初始化认证状态
  const initAuthState = () => {
    const storedUsername = localStorage.getItem('username')
    const storedToken = localStorage.getItem('token')
    const storedRole = localStorage.getItem('role')

    if (storedUsername && storedToken) {
      username.value = storedUsername
      token.value = storedToken
      role.value = storedRole || ''
      isAuthenticated.value = true
    }
  }

  // 登录
  const login = async (credentials: AuthCredentials) => {
    const response = await authApi.login(credentials)

    if (response.data.code === 0) {
      const data = response.data.data
      username.value = credentials.username
      if (data.token) {
        token.value = data.token
        localStorage.setItem('token', data.token)
      }
      if (data.role) {
        role.value = data.role
        localStorage.setItem('role', data.role)
      } else {
        role.value = ''
        localStorage.removeItem('role')
      }
      localStorage.setItem('username', credentials.username)
      isAuthenticated.value = true
    }

    return response.data
  }

  // 注册
  const register = async (credentials: AuthCredentials) => {
    const response = await authApi.register(credentials)
    return response.data
  }

  // 登出
  const logout = async () => {
    try {
      await authApi.logout()
    } finally {
      username.value = ''
      token.value = ''
      role.value = ''
      isAuthenticated.value = false
      localStorage.removeItem('username')
      localStorage.removeItem('token')
      localStorage.removeItem('role')
    }
  }

  // 清除认证信息
  const clearAuth = () => {
    username.value = ''
    token.value = ''
    role.value = ''
    isAuthenticated.value = false
    localStorage.removeItem('username')
    localStorage.removeItem('token')
    localStorage.removeItem('role')
  }

  return {
    username,
    token,
    role,
    isAuthenticated,
    initAuthState,
    login,
    register,
    logout,
    clearAuth
  }
}, {
  persist: true
})
