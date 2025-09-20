<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>會員登入</h2>
      </div>

      <div class="login-content">
        <div class="login-options">
          <div class="login-option" v-if="amplifyAuth.isConfigured()">
            <h4>AWS Amplify 登入</h4>
            <p>使用 AWS Amplify 服務進行安全登入，並支援 S3 檔案上傳</p>
            <button @click="loginWithAmplify" class="btn btn-primary btn-block">
              使用 Amplify 登入
            </button>
          </div>

          <div
            class="login-option"
            v-if="cognitoAuth.isConfigured() && !amplifyAuth.isConfigured()"
          >
            <h4>AWS Cognito 登入</h4>
            <p>使用 AWS Cognito 服務進行安全登入</p>
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

          <div
            class="login-option"
            v-if="!cognitoAuth.isConfigured() && !amplifyAuth.isConfigured()"
          >
            <h4>AWS 服務設定</h4>
            <small class="help-text">
              請在 .env.local 中設定：<br />
              - VITE_COGNITO_DOMAIN<br />
              - VITE_COGNITO_CLIENT_ID<br />
              - VITE_COGNITO_USER_POOL_ID (Amplify)<br />
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import cognitoAuth from '../utils/cognito.js'
import amplifyAuth from '../utils/amplify.js'

const router = useRouter()

const loginForm = ref({
  email: '',
  password: '',
})

// 在組件掛載時檢查登入狀態和處理 Cognito/Amplify 回調
onMounted(async () => {
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

  // 優先檢查 Amplify 回調
  if (amplifyAuth.isConfigured()) {
    const amplifyResult = await amplifyAuth.handleLoginCallback()
    if (amplifyResult && amplifyResult.success) {
      console.log(amplifyResult.message)
      alert(amplifyResult.message)
      // 登入成功後返回首頁
      router.push('/')
      return
    }
  }

  // 檢查是否有 Cognito 回調
  const loginResult = await cognitoAuth.handleLoginCallback()
  if (loginResult && loginResult.success) {
    console.log(loginResult.message)
    alert(loginResult.message)
    // 登入成功後返回首頁
    router.push('/')
  }
})

// 使用 Amplify 登入
const loginWithAmplify = async () => {
  try {
    if (!amplifyAuth.isConfigured()) {
      alert('Amplify 未正確設定，請檢查環境變數')
      return
    }

    console.log('重定向至 Amplify 登入頁面')
    await amplifyAuth.signInWithHostedUI()
  } catch (error) {
    console.error('Amplify 登入錯誤:', error)
    alert(`登入失敗：${error.message}`)
  }
}

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

    window.location.href = loginUrl
  } catch (error) {
    console.error('Cognito 登入錯誤:', error)

    // 根據不同錯誤提供具體的解決方案
    let errorMessage = `登入失敗：${error.message}\n\n`

    if (error.message && error.message.includes('redirect_mismatch')) {
      errorMessage += `重定向 URL 不匹配\n\n請檢查：\n1. AWS Cognito 用戶池中的應用程式客戶端設定\n2. 允許的回調 URL 是否包含：${window.location.origin}\n3. 允許的登出 URL 是否包含：${window.location.origin}`
    } else if (window.location.search.includes('error=invalid_scope')) {
      errorMessage += `OAuth scope 錯誤\n\n請檢查：\n1. Cognito 用戶池應用程式客戶端的 OAuth 設定\n2. 確認已啟用 'email', 'openid', 'profile' scope\n3. 確認應用程式客戶端類型設定正確`
    } else {
      errorMessage += `建議檢查：\n1. 環境變數設定是否正確\n2. Cognito 用戶池設定\n3. 網路連線\n4. OAuth 設定`
    }

    alert(errorMessage)
  }
}

// 立即註冊處理
const handleLocalLogin = async () => {
  try {
    // 這裡可以添加立即註冊邏輯
    // 例如調用後端 API 進行驗證
    console.log('立即註冊:', loginForm.value)

    // 模擬登入成功
    const mockUser = {
      email: loginForm.value.email,
      name: loginForm.value.email.split('@')[0],
      loginType: 'local',
      loginTime: new Date().toISOString(),
    }

    cognitoAuth.setCurrentUser(mockUser)

    alert(`登入成功！歡迎回來，${mockUser.name}`)
    // 登入成功後返回首頁
    router.push('/')
  } catch (error) {
    console.error('立即註冊錯誤:', error)
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
  width: 125px;
}

.btn-primary {
  background: #fa8500;
  color: white;
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
