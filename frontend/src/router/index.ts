import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Square',
    component: () => import('@/views/SquareView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/classes',
    name: 'Classes',
    component: () => import('@/views/ClassesView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/students',
    name: 'Students',
    component: () => import('@/views/StudentsView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@/views/StatisticsView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Route guard for authentication
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const isAuthenticated = authStore.isAuthenticated
  const requiresAuth = to.meta.requiresAuth

  // If route requires auth and user is not authenticated
  if (requiresAuth && !isAuthenticated) {
    next('/login')
  }
  // If user is authenticated and trying to access /login, redirect to home
  else if (isAuthenticated && to.path === '/login') {
    next('/')
  }
  // Otherwise allow navigation
  else {
    next()
  }
})

export default router