<template>
  <div class="home">
    <h1>欢迎来到同学录管理系统</h1>
    <p>ClassmateHub - 连接你与同窗好友的桥梁</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('Home view mounted')
})
</script>

<style scoped>
.home {
  text-align: center;
  padding: 2rem;
}

h1 {
  color: #409eff;
}
</style>