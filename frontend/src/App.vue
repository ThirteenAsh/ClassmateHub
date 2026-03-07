<template>
  <div id="app">
    <HeaderNav v-if="$route.path !== '/login'" />
    <div class="content-wrapper">
      <router-view :key="$route.path" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
// @ts-ignore
import HeaderNav from './components/HeaderNav.vue'
import { useAuthStore } from './stores/auth'

// @ts-ignore
const route = useRoute()
const authStore = useAuthStore()

onMounted(() => {
  // Initialize auth state from localStorage
  authStore.initAuthState()
  console.log('ClassmateHub frontend initialized')
})
</script>

<style>
#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue',
    Arial, 'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol',
    'Noto Color Emoji';
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #1f2933;
  background: radial-gradient(circle at top left, #eef2ff 0, #e0f2fe 35%, #f9fafb 70%);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

body {
  margin: 0;
  background-color: #f5f7fa;
}

/* 使用flex让HeaderNav和内容正常排列 */
.content-wrapper {
  flex: 1;
  overflow-y: auto;
  position: relative;
}
</style>