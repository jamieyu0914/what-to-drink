<template>
  <div class="container">
    <div class="back-home">
      <button @click="goHome" class="back-home-btn">返回首頁</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.back-home-btn,
.back-home-btn :visited {
  font-weight: 800;
  font-size: larger;
  margin: 10px;
  height: 40px;
  width: 20vh;
  color: #c90404;
  background-color: #fff;
  border: 6px solid #f1e9e9;
  text-decoration: none;
  box-shadow: 4px 4px 20px 0 #076810;
  border-radius: 8px;
}

.back-home-btn:hover,
.back-home-btn:active {
  color: #fff;
  background-color: #c90404;
  border: 8px solid #a00404;
  text-decoration: none;
  box-shadow: 4px 4px 5px 0 #076810;
  margin-top: 15px;
}
</style>
