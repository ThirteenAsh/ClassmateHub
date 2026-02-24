import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue')
  },
  {
    path: '/classes',
    name: 'Classes',
    component: () => import('@/views/ClassesView.vue')
  },
  {
    path: '/students',
    name: 'Students',
    component: () => import('@/views/StudentsView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router