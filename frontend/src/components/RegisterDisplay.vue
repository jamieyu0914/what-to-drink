<template>
  <div class="register-view">
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <h2>會員註冊</h2>
          <router-link to="/login" class="back-link">← 返回登入</router-link>
        </div>

        <div class="register-content">
          <form @submit.prevent="handleRegister" class="register-form">
            <div class="form-group">
              <label for="username">暱稱：</label>
              <input
                type="text"
                id="username"
                v-model="registerForm.username"
                class="form-control"
                required
                placeholder="請輸入您的暱稱"
              />
            </div>

            <div class="form-group">
              <label for="email">電子郵件：</label>
              <input
                type="email"
                id="email"
                v-model="registerForm.email"
                class="form-control"
                required
                placeholder="請輸入您的電子郵件"
              />
            </div>

            <div class="form-group">
              <label for="password">密碼：</label>
              <input
                type="password"
                id="password"
                v-model="registerForm.password"
                class="form-control"
                required
                placeholder="請輸入密碼（至少6個字符）"
                minlength="6"
              />
            </div>

            <div class="form-group">
              <label for="confirmPassword">確認密碼：</label>
              <input
                type="password"
                id="confirmPassword"
                v-model="registerForm.confirmPassword"
                class="form-control"
                required
                placeholder="請再次輸入密碼"
              />
            </div>

            <div class="form-group">
              <label class="checkbox-container">
                <input type="checkbox" v-model="registerForm.agreeToTerms" required />
                <span class="checkmark"></span>
                我同意服務條款和隱私權政策
              </label>
            </div>

            <button type="submit" class="btn btn-success btn-block" :disabled="!isFormValid">
              註冊帳號
            </button>

            <div class="login-link">
              <p>已有帳號？ <router-link to="/login">立即登入</router-link></p>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import cognitoAuth from '../utils/cognito.js'

const router = useRouter()

const registerForm = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  agreeToTerms: false,
})

// 表單驗證
const isFormValid = computed(() => {
  return (
    registerForm.value.username.trim() &&
    registerForm.value.email.trim() &&
    registerForm.value.password.length >= 6 &&
    registerForm.value.password === registerForm.value.confirmPassword &&
    registerForm.value.agreeToTerms
  )
})

// 註冊處理
const handleRegister = async () => {
  try {
    // 檢查密碼是否一致
    if (registerForm.value.password !== registerForm.value.confirmPassword) {
      alert('密碼和確認密碼不一致，請重新輸入')
      return
    }

    // 檢查密碼長度
    if (registerForm.value.password.length < 6) {
      alert('密碼長度至少需要6個字符')
      return
    }

    // 這裡可以添加實際的註冊邏輯
    // 例如調用後端 API 進行註冊
    console.log('註冊用戶:', {
      username: registerForm.value.username,
      email: registerForm.value.email,
      password: registerForm.value.password,
    })

    // 模擬註冊成功
    const newUser = {
      email: registerForm.value.email,
      name: registerForm.value.username,
      loginType: 'local',
      registrationTime: new Date().toISOString(),
      loginTime: new Date().toISOString(),
    }

    // 設置為當前用戶（模擬自動登入）
    cognitoAuth.setCurrentUser(newUser)

    alert(`註冊成功！歡迎加入，${newUser.name}！\n\n您已自動登入系統。`)

    // 註冊成功後返回首頁
    router.push('/')
  } catch (error) {
    console.error('註冊錯誤:', error)
    alert(`註冊失敗：${error.message}`)
  }
}
</script>

<style scoped>
.register-view {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  margin: 0 auto;
}

.register-container {
  width: 100%;
  max-width: 500px;
}

.register-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.register-header {
  background: #fa8500;
  color: white;
  padding: 24px;
  text-align: center;
  position: relative;
}

.register-header h2 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 600;
}

.back-link {
  position: absolute;
  left: 24px;
  top: 50%;
  transform: translateY(-50%);
  color: white;
  text-decoration: none;
  font-size: 0.9rem;
  opacity: 0.9;
  transition: opacity 0.2s;
}

.back-link:hover {
  opacity: 1;
}

.register-content {
  padding: 32px 24px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 0.95rem;
  text-align: left;
}

.form-control {
  padding: 12px 16px;
  border: 2px solid #ddd;
  border-radius: 6px;
  font-size: 15px;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #fa8500;
  box-shadow: 0 0 0 3px rgba(250, 133, 0, 0.1);
}

/* 自定義複選框 */
.checkbox-container {
  display: flex;
  align-items: center;
  position: relative;
  padding-left: 30px;
  cursor: pointer;
  font-size: 14px;
  user-select: none;
}

.checkbox-container input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  position: absolute;
  top: 0;
  left: 0;
  height: 20px;
  width: 20px;
  background-color: #eee;
  border-radius: 4px;
  border: 2px solid #ddd;
  transition: all 0.2s;
}

.checkbox-container:hover input ~ .checkmark {
  background-color: #f5f5f5;
}

.checkbox-container input:checked ~ .checkmark {
  background-color: #fa8500;
  border-color: #fa8500;
}

.checkmark:after {
  content: '';
  position: absolute;
  display: none;
}

.checkbox-container input:checked ~ .checkmark:after {
  display: block;
}

.checkbox-container .checkmark:after {
  left: 6px;
  top: 2px;
  width: 6px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.btn {
  padding: 12px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  text-decoration: none;
  display: inline-block;
  text-align: center;
  transition: all 0.2s;
  box-sizing: border-box;
  box-shadow: none;
}

.btn-block {
  width: 125px;
}

.btn-success {
  padding: 10px 24px;
  background: #fa8500;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 600;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
  margin: 0 auto;
}

.btn-success:hover {
  padding: 10px 24px;
  transform: translateY(-1px);
  box-shadow: none;
}

.btn-success:disabled {
  padding: 10px 24px;
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-success:disabled:hover {
  padding: 10px 24px;
  transform: none;
  box-shadow: none;
}

.login-link {
  text-align: center;
  margin-top: 16px;
}

.login-link p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-link a {
  color: #fa8500;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}

/* 響應式設計 */
@media (max-width: 576px) {
  .register-view {
    padding: 10px;
  }

  .register-header {
    padding: 20px 16px;
  }

  .register-header h2 {
    font-size: 1.5rem;
  }

  .back-link {
    left: 16px;
    font-size: 0.8rem;
  }

  .register-content {
    padding: 24px 16px;
  }
}
</style>
