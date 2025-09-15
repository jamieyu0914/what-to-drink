<template>
  <!-- Button Zone -->
  <div class="container">
    <div class="row-buttons">
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button id="menuButton" @click="openMenuModal" class="btn btn-primary">菜單</button>
      </div>
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button id="selectButton" @click="openSelectModal" class="btn btn-primary">請選擇</button>
      </div>
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button id="ramdomButton" @click="openRandomModal" class="btn btn-primary">隨機結果</button>
      </div>
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button id="openAIAgentButton" @click="openAIAgentModal" class="btn btn-primary">推薦小幫手</button>
      </div>
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button id="uploadButton" @click="handleFileUpload" class="btn btn-success">上傳檔案</button>
        <input type="file" ref="fileInput" @change="onFileSelected" style="display: none;" accept=".json,.txt,.csv">
      </div>
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button id="fileListButton" @click="getUploadedFilesList" class="btn btn-info">檢視上傳檔案</button>
      </div>
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button
          id="cognitoLoginButton"
          @click="handleCognitoLogin"
          :class="isLoggedIn ? 'btn btn-danger' : 'btn btn-warning'"
        >
          {{ isLoggedIn ? '登出' : 'Cognito 登入' }}
        </button>
      </div>
    </div>
  </div>
  <!-- End of Button Zone -->

  <!-- 登入彈窗 -->
  <div v-if="showLoginModal" class="modal-overlay" @click="closeLoginModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>用戶登入</h3>
        <button class="close-btn" @click="closeLoginModal">&times;</button>
      </div>
      <div class="modal-body">
        <div class="login-options">
          <div class="login-option" v-if="amplifyAuth.isConfigured()">
            <h4>AWS Amplify 登入</h4>
            <p>使用 AWS Amplify 服務進行安全登入，並支援 S3 檔案上傳</p>
            <button @click="loginWithAmplify" class="btn btn-primary btn-block">
              使用 Amplify 登入
            </button>
          </div>

          <div class="login-option" v-if="cognitoAuth.isConfigured() && !amplifyAuth.isConfigured()">
            <h4>AWS Cognito 登入</h4>
            <p>使用 AWS Cognito 服務進行安全登入</p>
            <button @click="loginWithCognito" class="btn btn-primary btn-block">
              使用 Cognito 登入
            </button>
          </div>

          <div class="login-option">
            <h4>本地登入</h4>
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
                >
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
                >
              </div>
              <button type="submit" class="btn btn-success btn-block">
                本地登入
              </button>
            </form>
          </div>

          <div class="login-option" v-if="!cognitoAuth.isConfigured() && !amplifyAuth.isConfigured()">
            <h4>AWS 服務設定</h4>
            <p class="text-muted">需要設定環境變數才能使用 AWS 登入服務</p>
            <small class="help-text">
              請在 .env.local 中設定：<br>
              - VITE_COGNITO_DOMAIN<br>
              - VITE_COGNITO_CLIENT_ID<br>
              - VITE_COGNITO_USER_POOL_ID (Amplify)<br>
              - VITE_S3_BUCKET_NAME<br>
              - VITE_COGNITO_REDIRECT_URI (可選)
            </small>
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
const fileInput = ref(null)
const uploadedFiles = ref([]) // 儲存已上傳檔案的 S3 位置
const isLoggedIn = ref(false)
const currentUser = ref(null)

// 登入彈窗相關
const showLoginModal = ref(false)
const loginForm = ref({
  email: '',
  password: ''
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
      updateLoginStatus()
      return
    }
  }

  // 檢查是否有 Cognito 回調
  const loginResult = await cognitoAuth.handleLoginCallback()
  if (loginResult && loginResult.success) {
    console.log(loginResult.message)
    alert(loginResult.message)
  }

  // 更新登入狀態
  updateLoginStatus()
})

// 更新登入狀態
const updateLoginStatus = async () => {
  // 優先檢查 Amplify 登入狀態
  if (amplifyAuth.isConfigured()) {
    isLoggedIn.value = await amplifyAuth.isLoggedIn()
    if (isLoggedIn.value) {
      currentUser.value = await amplifyAuth.getCurrentUser()
      return
    }
  }

  // 檢查 Cognito 登入狀態
  isLoggedIn.value = cognitoAuth.isLoggedIn()
  currentUser.value = cognitoAuth.getCurrentUser()
}

const openMenuModal = () => {
  router.push('/menu')
}

const openSelectModal = () => {
  router.push('/select')
}

const openRandomModal = () => {
  router.push('/random')
}

const openAIAgentModal = () => {
  router.push('/ai-agent')
}

const handleCognitoLogin = async () => {
  // 處理登入/登出邏輯
  try {
    if (isLoggedIn.value) {
      // 如果已經登入，提供登出選項
      const shouldLogout = confirm(`您已登入為：${currentUser.value?.email || '用戶'}\n\n是否要登出？`)
      if (shouldLogout) {
        // 檢查是否使用 Amplify 登入
        if (currentUser.value?.loginType === 'amplify') {
          await amplifyAuth.signOut()
          alert('已登出')
        } else if (cognitoAuth.isConfigured()) {
          cognitoAuth.clearCurrentUser()
          window.location.href = cognitoAuth.getLogoutUrl()
        } else {
          cognitoAuth.clearCurrentUser()
          alert('已登出')
        }
        updateLoginStatus()
      }
      return
    }

    // 顯示登入彈窗
    showLoginModal.value = true
  } catch (error) {
    console.error('處理登入/登出錯誤:', error)
    alert(`操作失敗：${error.message}`)
  }
}

// 關閉登入彈窗
const closeLoginModal = () => {
  showLoginModal.value = false
  loginForm.value.email = ''
  loginForm.value.password = ''
}

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
      alert('Cognito 未正確設定，請檢查環境變數\n\n需要設定：\n- VITE_COGNITO_DOMAIN\n- VITE_COGNITO_CLIENT_ID\n- VITE_COGNITO_REDIRECT_URI (可選，預設為當前網址)')
      return
    }

    // 檢查並顯示當前設定用於調試
    console.log('Cognito 設定:', {
      domain: cognitoAuth.domain,
      clientId: cognitoAuth.clientId,
      redirectUri: cognitoAuth.redirectUri
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

// 本地登入處理
const handleLocalLogin = async () => {
  try {
    // 這裡可以添加本地登入邏輯
    // 例如調用後端 API 進行驗證
    console.log('本地登入:', loginForm.value)

    // 模擬登入成功
    const mockUser = {
      email: loginForm.value.email,
      name: loginForm.value.email.split('@')[0],
      loginType: 'local',
      loginTime: new Date().toISOString()
    }

    cognitoAuth.setCurrentUser(mockUser)
    updateLoginStatus()
    closeLoginModal()

    alert(`登入成功！歡迎回來，${mockUser.name}`)
  } catch (error) {
    console.error('本地登入錯誤:', error)
    alert(`登入失敗：${error.message}`)
  }
}

const getUploadedFilesList = () => {
  if (uploadedFiles.value.length === 0) {
    alert('尚未上傳任何檔案')
    return
  }

  const filesList = uploadedFiles.value.map((file, index) =>
    `${index + 1}. ${file.fileName}\n   S3位置: ${file.s3Url}\n   上傳時間: ${new Date(file.uploadTime).toLocaleString()}`
  ).join('\n\n')

  alert(`已上傳的檔案 (${uploadedFiles.value.length} 個):\n\n${filesList}`)
}

const handleFileUpload = () => {
  fileInput.value.click()
}

const onFileSelected = async (event) => {
  const file = event.target.files[0]
  if (file) {
    console.log('選擇的檔案:', file.name)

    try {
      // 優先使用 Amplify Storage (如果已登入且配置正確)
      if (amplifyAuth.isConfigured() && isLoggedIn.value && currentUser.value?.loginType === 'amplify') {
        const timestamp = new Date().getTime()
        const s3Key = `uploads/${currentUser.value.sub}/${timestamp}-${file.name}`

        const result = await amplifyAuth.uploadToS3(file, s3Key)

        if (result.success) {
          const uploadRecord = {
            fileName: file.name,
            s3Key: result.s3Key,
            s3Url: result.s3Url,
            uploadTime: new Date().toISOString(),
            fileSize: file.size,
            fileType: file.type,
            uploadMethod: 'amplify'
          }

          uploadedFiles.value.push(uploadRecord)
          console.log('Amplify 上傳成功:', uploadRecord)
          alert(`檔案上傳成功！\n檔案名稱: ${file.name}\n使用 Amplify Storage 上傳`)
          return
        }
      }

      // 如果 Amplify 不可用，回退到原有邏輯
      await uploadWithLegacyMethod(file)

    } catch (error) {
      console.error('檔案上傳錯誤:', error)
      alert(`檔案上傳失敗: ${error.message}\n\n建議:\n1. 確認已使用 Amplify 登入\n2. 檢查 S3 權限設定\n3. 檢查環境變數配置`)
    }
  }
}

// 原有的上傳方法作為後備
const uploadWithLegacyMethod = async (file) => {
  // 生成檔案的 S3 鍵名 (路徑)
  const timestamp = new Date().getTime()
  const s3Key = `uploads/${timestamp}-${file.name}`
  const bucketName = import.meta.env.VITE_S3_BUCKET_NAME
  const region = import.meta.env.VITE_AWS_REGION || 'us-east-1'

  // 方法1: 使用 FormData 直接上傳到 S3 (需要正確的 CORS 設定)
  const formData = new FormData()
  formData.append('key', s3Key)
  formData.append('Content-Type', file.type || 'application/octet-stream')
  formData.append('file', file)

  // 構建 S3 端點 URL
  const s3Endpoint = `https://${bucketName}.s3.${region}.amazonaws.com/`

  try {
    const response = await fetch(s3Endpoint, {
      method: 'POST',
      body: formData,
      mode: 'cors'
    })

    if (response.ok) {
      // 構建 S3 URL
      const s3Url = `https://${bucketName}.s3.${region}.amazonaws.com/${s3Key}`

      // 儲存上傳記錄
      const uploadRecord = {
        fileName: file.name,
        s3Key: s3Key,
        s3Url: s3Url,
        uploadTime: new Date().toISOString(),
        fileSize: file.size,
        fileType: file.type,
        uploadMethod: 'direct'
      }

      uploadedFiles.value.push(uploadRecord)

      console.log('檔案上傳成功:', uploadRecord)
      alert(`檔案上傳成功！\n檔案名稱: ${file.name}\nS3 位置: ${s3Url}`)
    } else {
      throw new Error(`上傳失敗: ${response.status} ${response.statusText}`)
    }

  } catch (fetchError) {
    console.warn('直接上傳失敗，嘗試後端代理上傳:', fetchError)

    // 方法2: 通過後端 API 上傳 (推薦的安全方法)
    await uploadViaBackend(file, s3Key)
  }
}

// 通過後端 API 上傳檔案的方法
const uploadViaBackend = async (file, s3Key) => {
  const formData = new FormData()
  console.log('Uploading via backend with file:', file)
  formData.append('file', file)
  console.log('Uploading via backend with s3Key:', s3Key)
  formData.append('s3Key', s3Key)

  // 假設後端有一個上傳 API 端點
  const backendUrl = 'http://localhost:8088/api/upload' // 請根據您的後端 URL 調整

  try {
    const response = await fetch(backendUrl, {
      method: 'POST',
      body: formData
    })

    if (response.ok) {
      const result = await response.json()

      // 儲存上傳記錄
      const uploadRecord = {
        fileName: file.name,
        s3Key: s3Key,
        s3Url: result.s3Url || `https://${import.meta.env.VITE_S3_BUCKET_NAME}.s3.${import.meta.env.VITE_AWS_REGION || 'us-east-1'}.amazonaws.com/${s3Key}`,
        uploadTime: new Date().toISOString(),
        fileSize: file.size,
        fileType: file.type
      }

      uploadedFiles.value.push(uploadRecord)

      console.log('後端上傳成功:', uploadRecord)
      alert(`檔案上傳成功！\n檔案名稱: ${file.name}\nS3 位置: ${uploadRecord.s3Url}`)
    } else {
      throw new Error(`後端上傳失敗: ${response.status}`)
    }
  } catch (backendError) {
    console.error('後端上傳失敗:', backendError)
    throw new Error('所有上傳方法都失敗了，請檢查設定或聯繫管理員')
  }
}
</script>

<style scoped>
/* 彈窗覆蓋層 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* 彈窗內容 */
.modal-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

/* 彈窗標題 */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px 16px;
  border-bottom: 1px solid #e5e5e5;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.5rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #666;
}

/* 彈窗主體 */
.modal-body {
  padding: 24px;
}

/* 登入選項 */
.login-options {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.login-option {
  padding: 20px;
  border: 1px solid #e5e5e5;
  border-radius: 6px;
  background-color: #fafafa;
}

.login-option h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 1.2rem;
}

.login-option p {
  margin: 0 0 16px 0;
  color: #666;
  font-size: 0.9rem;
}

/* 表單樣式 */
.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

/* 按鈕樣式 */
.btn {
  padding: 10px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  display: inline-block;
  text-align: center;
  transition: all 0.2s;
}

.btn-block {
  width: 100%;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-success:hover {
  background-color: #1e7e34;
}

.btn-warning {
  background-color: #ffc107;
  color: #212529;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

/* 幫助文字 */
.help-text {
  color: #6c757d;
  font-size: 0.8rem;
  line-height: 1.4;
}

.text-muted {
  color: #6c757d;
}

/* 響應式設計 */
@media (max-width: 576px) {
  .modal-content {
    width: 95%;
    margin: 20px;
  }

  .modal-header,
  .modal-body {
    padding: 16px;
  }

  .login-option {
    padding: 16px;
  }
}
</style>
