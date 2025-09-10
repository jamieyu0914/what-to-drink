<!-- <template>
  <div class="chat-view">
    <div class="back-button-container">
      <button @click="goBack" class="back-button">
        ← 返回首頁
      </button>
    </div>

    <div class="chat-container">
      <AIAgent />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import AIAgent from '../components/AIAgentDisplay.vue'

const router = useRouter()

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.chat-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.back-button-container {
  max-width: 800px;
  margin: 0 auto 20px auto;
}

.back-button {
  background: white;
  border: 2px solid #e2e8f0;
  padding: 10px 20px;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 600;
  color: #475569;
  transition: all 0.2s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.back-button:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.chat-container {
  max-width: 800px;
  margin: 0 auto;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .chat-view {
    padding: 10px;
  }

  .back-button-container {
    margin-bottom: 10px;
  }
}
</style> -->

<script setup>
import AppHeader from '../components/AppHeader.vue'
import BackHome from '../components/BackHome.vue'
import AIAgentDisplay from '../components/AIAgentDisplay.vue'
</script>

<template>
  <div id="app">
    <!-- App Header Component -->
    <AppHeader />

    <!-- Back Home Component -->
    <BackHome />

    <!-- Menu Display Component -->
    <AIAgentDisplay />
  </div>
</template>

<style scoped>
@import '../assets/main.css';
@import '../assets/style.css';
</style>

