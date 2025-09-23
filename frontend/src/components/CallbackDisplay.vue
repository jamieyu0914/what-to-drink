<template>
  <div class="callback-container">
    <p>正在處理登入...</p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

onMounted(() => {
  try {
    const { code, error } = route.query

    if (error) {
      console.error('Authentication error:', error)
      router.push({
        path: '/auth-error',
        query: { error }
      })
      return
    }

    if (code) {
      // 處理認證碼，重定向到認證回調處理頁面
      router.push({
        path: '/',
        query: route.query
      })
    } else {
      // 沒有認證碼，重定向到首頁
      router.push('/')
    }
  } catch (error) {
    console.error('Redirect error:', error)
    router.push('/')
  }
})
</script>

<style scoped>
.callback-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 18px;
}
</style>
