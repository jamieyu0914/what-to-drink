<template>
  <div class="forgot-password-container">
    <div class="forgot-password-card">
      <div class="forgot-password-header">
        <h2>忘記密碼</h2>
        <router-link to="/login" class="back-link">← 返回登入</router-link>
      </div>

      <div class="forgot-password-content">
        <div class="forgot-password-form">
          <p class="description">完成以下操作，我們將發送密碼重設連結給您。</p>
          <form @submit.prevent="handleForgotPassword">
            <div class="form-group">
              <label for="email">電子郵件：</label>
              <input
                type="email"
                id="email"
                v-model="email"
                class="form-control"
                required
                placeholder="請輸入您的電子郵件"
              />
            </div>

            <button
              type="submit"
              class="btn btn-primary btn-block"
              :disabled="!email.trim() || isLoading"
            >
              {{ isLoading ? '發送中...' : '發送重設連結' }}
            </button>
          </form>

          <div v-if="message" class="message" :class="messageType">
            {{ message }}
          </div>

          <div class="help-section">
            <h4>需要幫助？</h4>
            <ul>
              <li>請確認電子郵件地址是否輸入正確</li>
              <li>若沒有收到郵件，請檢查垃圾郵件資料夾</li>
              <li>發送重設連結後，請於24小時內重設密碼</li>
              <li>
                如果仍有問題，請透過<a
                  href="mailto:support@whattodrink.com?subject=密碼重設問題&body=請描述您遇到的問題："
                  class="mailto-link"
                  >客服信箱</a
                >聯繫我們
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const email = ref('')
const isLoading = ref(false)
const message = ref('')
const messageType = ref('')

const goBack = () => {
  router.push('/login')
}

const handleForgotPassword = async () => {
  if (!email.value.trim()) {
    return
  }

  isLoading.value = true
  message.value = ''

  try {
    // 這裡可以添加實際的 API 調用
    // 例如調用後端 API 或 AWS Cognito 的忘記密碼功能

    // 模擬 API 調用
    await new Promise((resolve) => setTimeout(resolve, 2000))

    message.value = `密碼重設連結已發送至：${email.value}\n\n請檢查您的電子郵件信箱（包括垃圾郵件資料夾）`
    messageType.value = 'success'

    // throw new Error('測試錯誤') // 用於測試錯誤處理 (發送失敗)
    // throw new Error() // 用於測試錯誤處理 (請稍後再試)

    // 清空表單
    email.value = ''
  } catch (error) {
    console.error('發送重設連結錯誤:', error)
    message.value = `發送失敗：${error.message || '請稍後再試'}`
    messageType.value = 'error'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.forgot-password-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #fa8500 0%, #ff9d3d 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.forgot-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.forgot-password-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px;
  overflow: hidden;
}

.forgot-password-header {
  background: #fa8500;
  color: white;
  padding: 24px;
  text-align: center;
  position: relative;
}

.forgot-password-header h2 {
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

.forgot-password-content {
  padding: 32px 24px;
}

.back-navigation {
  margin-bottom: 24px;
}

.description {
  color: #666;
  font-size: 1rem;
  line-height: 1.6;
  margin-bottom: 24px;
  text-align: center;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 0.95rem;
  text-align: left;
}

.form-control {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #ddd;
  border-radius: 6px;
  font-size: 15px;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  box-sizing: border-box;
}

.form-control:focus {
  outline: none;
  border-color: #fa8500;
  box-shadow: 0 0 0 3px rgba(250, 133, 0, 0.1);
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
}

.btn-block {
  width: fit-content;
}

.btn-primary {
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

.btn-primary:hover {
  padding: 10px 24px;
  transform: translateY(-1px);
  box-shadow: none;
}

.btn-primary:disabled {
  padding: 10px 24px;
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-primary:disabled:hover {
  padding: 10px 24px;
  transform: none;
  box-shadow: none;
}

.btn-link {
  background: none;
  color: #fa8500;
  border: none;
  text-decoration: none;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s;
  padding: 8px 0;
}

.btn-link:hover {
  color: #e07600;
  text-decoration: underline;
}

.message {
  margin-top: 20px;
  padding: 16px;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-line;
  background: linear-gradient(135deg, #fff5e6 0%, #ffe6cc 100%);
  color: #d67600;
  border: 1px solid #ffd9b3;
}

.help-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #eee;
}

.help-section h4 {
  margin: 0 0 16px 0;
  color: #333;
  font-size: 1.1rem;
  font-weight: 600;
}

.help-section ul {
  margin: 0px 40px 0px 40px;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.6;
  text-align: left;
}

.help-section li {
  margin-bottom: 8px;
}

.mailto-link {
  color: #fa8500;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.mailto-link:hover {
  color: #e07600;
  text-decoration: underline;
}

/* 響應式設計 */
@media (max-width: 576px) {
  .forgot-password-container {
    padding: 10px;
  }

  .forgot-password-header {
    padding: 20px 16px;
  }

  .forgot-password-header h2 {
    font-size: 1.5rem;
  }

  .forgot-password-content {
    padding: 24px 16px;
  }

  .description {
    font-size: 0.9rem;
  }
}
</style>
