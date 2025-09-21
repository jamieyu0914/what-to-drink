<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>會員登入</h2>
      </div>

      <div class="login-content">
        <div class="login-options">
          <div class="login-option" v-if="cognitoAuth.isConfigured()">
            <h4>AWS Cognito 登入</h4>
            <button @click="loginWithCognito" class="btn btn-primary btn-block">
              使用 Cognito 登入
            </button>
          </div>

          <div class="login-option">
            <h4>立即登入</h4>
            <form @submit.prevent="handleLocalLogin">
              <div class="form-group">
                <label for="email">電子郵件：</label>
                <input
                  type="email"
                  id="email"
                  v-model="loginForm.email"
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
                  v-model="loginForm.password"
                  class="form-control"
                  required
                  placeholder="請輸入您的密碼"
                />
              </div>
              <button
                type="submit"
                class="btn btn-success btn-block"
                :disabled="!loginForm.email.trim() || !loginForm.password.trim()"
              >
                立即登入
              </button>
              <button @click="handleLocalRegister" class="btn register-btn btn-block">
                立即註冊
              </button>
              <button @click="handleForgotPassword" class="btn forgot-password-btn btn-link">
                忘記密碼？
              </button>
            </form>
          </div>

          <div class="login-option" v-if="!cognitoAuth.isConfigured()">
            <h4>AWS 服務設定</h4>
            <small class="help-text">
              請在 .env.local 中設定：<br />
              - VITE_COGNITO_DOMAIN<br />
              - VITE_COGNITO_CLIENT_ID<br />
              - VITE_S3_BUCKET_NAME<br />
              - VITE_COGNITO_REDIRECT_URI (可選)
            </small>
            <p class="text-muted">完成後才能使用 AWS 登入服務</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import cognitoAuth from '../utils/cognito.js'
import authStore from '../utils/auth.js'

const router = useRouter()

const loginForm = ref({
  email: '',
  password: '',
})

// 在組件掛載時檢查登入狀態和處理 Cognito 回調
onMounted(async () => {
  try {
    // Debug information
    console.log('LoginDisplay mounted, checking environment...')
    console.log('Cognito configured:', cognitoAuth.isConfigured())

    // 檢查 URL 中是否有 OAuth 錯誤
    const urlParams = new URLSearchParams(window.location.search)
    const oauthError = urlParams.get('error')
    const errorDescription = urlParams.get('error_description')

    if (oauthError) {
      console.error('OAuth 錯誤:', { error: oauthError, description: errorDescription })

      let errorMessage = `OAuth 登入錯誤：${oauthError}\n\n`

      if (oauthError === 'invalid_scope') {
        errorMessage += `Scope 設定錯誤\n\n解決方法：\n1. 檢查 AWS Cognito 用戶池應用程式客戶端設定\n2. 確認已啟用以下 OAuth scope：\n   - email\n   - openid\n   - profile\n3. 確認應用程式客戶端的 OAuth 流程設定正確`
      } else if (oauthError === 'redirect_mismatch') {
        errorMessage += `重定向 URL 不匹配\n\n解決方法：\n1. 在 Cognito 應用程式客戶端設定中\n2. 將以下 URL 添加到允許的回調 URL：\n   ${window.location.origin}\n3. 將以下 URL 添加到允許的登出 URL：\n   ${window.location.origin}`
      } else {
        errorMessage += errorDescription || '未知錯誤'
      }

      alert(errorMessage)

      // 清除 URL 中的錯誤參數
      if (window.history && window.history.replaceState) {
        window.history.replaceState({}, document.title, window.location.pathname)
      }
      return
    }

    // 檢查是否有 Cognito 回調
    const loginResult = await cognitoAuth.handleLoginCallback()
    if (loginResult) {
      if (loginResult.success) {
        console.log(loginResult.message)
        alert(loginResult.message)

        // 更新用戶狀態
        authStore.setUser(loginResult.user)

        // 登入成功後返回之前的頁面或首頁
        const redirectPath = sessionStorage.getItem('preLoginPath') || '/'
        sessionStorage.removeItem('preLoginPath')
        router.push(redirectPath)
      } else {
        // 登入處理失敗
        console.error('Cognito 登入處理失敗:', loginResult.error)
        alert(loginResult.message || '登入處理失敗，請重試')
      }
    }
  } catch (err) {
    console.error('LoginDisplay mounted error:', err)
    alert(`載入錯誤：${err.message}`)
  }
})

// 使用 Cognito 登入
const loginWithCognito = async () => {
  try {
    if (!cognitoAuth.isConfigured()) {
      alert(
        'Cognito 未正確設定，請檢查環境變數\n\n需要設定：\n- VITE_COGNITO_DOMAIN\n- VITE_COGNITO_CLIENT_ID\n- VITE_COGNITO_REDIRECT_URI (可選，預設為當前網址)',
      )
      return
    }

    // 檢查並顯示當前設定用於調試
    console.log('Cognito 設定:', {
      domain: cognitoAuth.domain,
      clientId: cognitoAuth.clientId,
      redirectUri: cognitoAuth.redirectUri,
    })

    console.log('重定向至 Cognito 登入頁面')
    const loginUrl = cognitoAuth.getLoginUrl()
    console.log('登入 URL:', loginUrl)

    // 保存重定向前的頁面，用於登入成功後返回
    sessionStorage.setItem('preLoginPath', window.location.pathname)

    window.location.href = loginUrl
  } catch (error) {
    console.error('Cognito 登入錯誤:', error)

    // 根據不同錯誤提供具體的解決方案
    let errorMessage = `登入失敗：${error.message}\n\n`

    if (error.message && error.message.includes('redirect_mismatch')) {
      errorMessage += `重定向 URL 不匹配\n\n請檢查：\n1. AWS Cognito 用戶池中的應用程式客戶端設定\n2. 允許的回調 URL 是否包含：${window.location.origin}/login\n3. 允許的登出 URL 是否包含：${window.location.origin}/login`
    } else if (error.message && error.message.includes('invalid_scope')) {
      errorMessage += `OAuth scope 錯誤\n\n請檢查：\n1. Cognito 用戶池應用程式客戶端的 OAuth 設定\n2. 確認已啟用 'openid' scope\n3. 確認應用程式客戶端類型設定正確\n4. 確認已啟用 OAuth 流程`
    } else if (error.message && error.message.includes('未正確設定')) {
      errorMessage += `環境變數未設定\n\n請確保以下環境變數已正確設定：\n- VITE_COGNITO_DOMAIN: 您的 Cognito 網域\n- VITE_COGNITO_CLIENT_ID: 應用程式客戶端 ID`
    } else {
      errorMessage += `建議檢查：\n1. 環境變數設定是否正確\n2. Cognito 用戶池設定\n3. 網路連線\n4. OAuth 設定\n5. 用戶池應用程式客戶端配置`
    }

    alert(errorMessage)
  }
}

// 立即登入處理
const handleLocalLogin = async () => {
  try {
    // 這裡可以添加與後端 API 的驗證邏輯
    console.log('立即登入:', loginForm.value)

    // 模擬登入成功
    const mockUser = {
      email: loginForm.value.email,
      name: loginForm.value.email.split('@')[0],
      loginType: 'local',
      loginTime: new Date().toISOString(),
    }

    // 保存到 Cognito 狀態管理（複用現有邏輯）
    cognitoAuth.setCurrentUser(mockUser)

    // 更新全局狀態
    authStore.setUser(mockUser)

    alert(`登入成功！歡迎回來，${mockUser.name}`)

    // 登入成功後返回之前的頁面或首頁
    const redirectPath = sessionStorage.getItem('preLoginPath') || '/'
    sessionStorage.removeItem('preLoginPath')
    router.push(redirectPath)
  } catch (error) {
    console.error('立即登入錯誤:', error)
    alert(`登入失敗：${error.message}`)
  }
}

// 立即註冊處理
const handleLocalRegister = () => {
  // 如果有註冊頁面，可以這樣跳轉：
  router.push('/register')
}

// 忘記密碼處理
const handleForgotPassword = () => {
  // 這裡可以添加忘記密碼邏輯
  const email = loginForm.value.email.trim()

  if (!email) {
    router.push('/forgot-password')
    return
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 500px;
  overflow: hidden;
}

.login-header {
  background: #fa8500;
  color: white;
  padding: 24px;
  text-align: center;
  position: relative;
}

.login-header h2 {
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

.login-content {
  padding: 32px 24px;
}

.login-options {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.login-option {
  padding: 24px;
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  background-color: #fafafa;
  transition:
    border-color 0.2s,
    background-color 0.2s;
}

.login-option:hover {
  border-color: #e0e0e0;
  background-color: #f5f5f5;
}

.login-option h4 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 1.3rem;
  font-weight: 600;
}

.login-option p {
  margin: 20px 0 0px 0;
  color: #fa8500;
  font-size: 0.95rem;
  line-height: 1.5;
  opacity: 0.6;
}

/* 表單樣式 */
.form-group {
  margin-bottom: 20px;
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
  border-color: #ecab29;
}

/* 按鈕樣式 */
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
  width: fit-content;
}

.btn-primary {
  font-size: 14px;
  font-weight: bold;
  margin: 20px 0px 10px 0px;
  height: 40px;
  width: 100%;
  color: #fff;
  background-color: #337ab7;
  border-color: #2e6da4;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
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

.btn-link {
  background: none;
  color: #fa8500;
  border: none;
  margin: 8px 12px;
  text-decoration: none;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s;
}

.btn-link:hover {
  color: #e07600;
  text-decoration: underline;
}

.register-btn {
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
}

.forgot-password-btn {
  display: inline-block;
}

/* 幫助文字 */
.help-text {
  color: #6c757d;
  font-size: 0.85rem;
  line-height: 1.5;
}

.text-muted {
  color: #6c757d;
}

/* 響應式設計 */
@media (max-width: 576px) {
  .login-container {
    padding: 10px;
  }

  .login-header {
    padding: 20px 16px;
  }

  .login-header h2 {
    font-size: 1.5rem;
  }

  .back-link {
    left: 16px;
    font-size: 0.8rem;
  }

  .login-content {
    padding: 24px 16px;
  }

  .login-option {
    padding: 20px 16px;
  }

  .login-option h4 {
    font-size: 1.2rem;
  }
}
</style>
