import { defineStore } from 'pinia'

export interface UserState {
  id: number | null
  username: string | null
  email: string | null
  role: string | null
  token: string | null
  isAuthenticated: boolean
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    id: null,
    username: null,
    email: null,
    role: null,
    token: null,
    isAuthenticated: false
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    getUserInfo: (state) => ({
      id: state.id,
      username: state.username,
      email: state.email,
      role: state.role
    })
  },

  actions: {
    setUserInfo(userData: {
      id?: number,
      username?: string,
      email?: string,
      role?: string,
      token?: string
    }) {
      if (userData.id !== undefined) this.id = userData.id
      if (userData.username !== undefined) this.username = userData.username
      if (userData.email !== undefined) this.email = userData.email
      if (userData.role !== undefined) this.role = userData.role
      if (userData.token !== undefined) this.token = userData.token
      this.isAuthenticated = !!userData.token
    },

    logout() {
      this.$reset()
    },

    updateToken(token: string) {
      this.token = token
      this.isAuthenticated = true
    }
  }
})

export interface ClassState {
  currentClassId: number | null
  classList: Array<any>
}

export const useClassStore = defineStore('class', {
  state: (): ClassState => ({
    currentClassId: null,
    classList: []
  }),

  actions: {
    setCurrentClass(classId: number) {
      this.currentClassId = classId
    },

    setClassList(classes: Array<any>) {
      this.classList = classes
    },

    addClass(newClass: any) {
      this.classList.push(newClass)
    },

    removeClass(classId: number) {
      this.classList = this.classList.filter(c => c.id !== classId)
    }
  }
})