<template>
  <div class="pre-login-container">
    <div class="pre-login-card">
      <div class="pre-login-header">
        <h2>推薦小幫手</h2>
      </div>
      <div class="pre-login-icon">
        <div class="warning-icon">
          <span class="exclamation">!</span>
        </div>
      </div>
      <h1 class="title">需要登入</h1>
      <p class="description">登入後即可享受個人化的飲品推薦服務</p>

      <div class="action-buttons">
        <button @click="goToLogin" class="btn btn-primary login-btn">立即登入</button>
      </div>

      <div class="features">
        <h3>解鎖以下功能：</h3>
        <ul>
          <li>🤖 AI 智能推薦</li>
          <li>📊 個人化偏好分析</li>
          <li>💾 推薦歷史記錄</li>
          <li>⭐ 收藏喜愛飲品</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.pre-login-container {
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
}

.pre-login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
  overflow: hidden;
}

.pre-login-header {
  background: #fa8500;
  color: white;
  padding: 24px;
  text-align: center;
  position: relative;
}

.pre-login-header h2 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 600;
}

.pre-login-icon {
  display: flex;
  justify-content: center;
  padding: 30px 0 20px 0;
}

.warning-icon {
  width: 80px;
  height: 80px;
  background-color: #e74c3c;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(231, 76, 60, 0.3);
  animation: pulse 2s infinite;
}

.exclamation {
  color: white;
  font-size: 48px;
  font-weight: bold;
  line-height: 1;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 4px 15px rgba(231, 76, 60, 0.3);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 6px 20px rgba(231, 76, 60, 0.4);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 4px 15px rgba(231, 76, 60, 0.3);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.description {
  font-size: 1rem;
  color: #888;
  margin-bottom: 15px;
  line-height: 1.5;
}

.action-buttons {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  padding: 15px 30px;
  border: none;
  border-radius: 25px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  min-width: 140px;
}

.btn-primary {
  padding: 8px 24px;
  background: #fa8500;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 600;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  box-shadow: none;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: none;
}

.features {
  text-align: left;
  background: linear-gradient(135deg, #fff5e6 0%, #ffe6cc 100%);
  padding: 25px 50px 25px 50px;
  border-radius: 12px;
  margin: 15px 45px 30px 45px;
  border: 1px solid #ffd9b3;
}

.features h3 {
  color: #d67600;
  margin-bottom: 15px;
  font-size: 1.2rem;
  text-align: center;
  font-weight: 600;
}

.features ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.features li {
  padding: 8px 0;
  color: #8b4513;
  font-size: 1rem;
  border-bottom: 1px solid #ffd9b3;
}

.features li:last-child {
  border-bottom: none;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .pre-login-content {
    padding: 30px 20px;
    margin: 20px;
  }

  .title {
    font-size: 2rem;
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;
  }

  .btn {
    width: 100%;
    max-width: 280px;
  }

  .login-icon {
    width: 100px;
    height: 100px;
  }
}

@media (max-width: 480px) {
  .title {
    font-size: 1.8rem;
  }

  .description {
    font-size: 1.1rem;
  }
}
</style>
