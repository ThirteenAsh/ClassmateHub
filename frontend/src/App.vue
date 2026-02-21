<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
onMounted(() => {
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
}

body {
  margin: 0;
  background-color: #f5f7fa;
}
</style>