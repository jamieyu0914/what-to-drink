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
        <button id="openAIAgentButton" @click="openAIAgentModal" class="btn btn-primary">
          推薦小幫手
        </button>
      </div>
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button
          id="cognitoLoginButton"
          @click="handleCognitoLogin"
          :class="isLoggedIn ? 'btn btn-danger' : 'btn btn-warning'"
        >
          {{ isLoggedIn ? '登出' : '會員登入' }}
        </button>
      </div>
    </div>
  </div>
  <!-- End of Button Zone -->
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import cognitoAuth from '../utils/cognito.js'
import amplifyAuth from '../utils/amplify.js'
import authStore from '../utils/auth.js'

const router = useRouter()
const isLoggedIn = ref(false)
const currentUser = ref(null)

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
  if (!isLoggedIn.value) {
    router.push('/pre-login')
    return
  }
  router.push('/ai-agent')
}

const handleCognitoLogin = async () => {
  // 處理登入/登出邏輯
  try {
    if (isLoggedIn.value) {
      // 如果已經登入，提供登出選項
      const shouldLogout = confirm(
        `您已登入為：${currentUser.value?.email || '用戶'}\n\n是否要登出？`,
      )
      if (shouldLogout) {
        // 使用統一的 auth-store 登出方法
        await authStore.logout(router)
        alert('已登出')
        updateLoginStatus()
      }
      return
    }

    // 導航到登入頁面
    router.push('/login')
  } catch (error) {
    console.error('處理登入/登出錯誤:', error)
    alert(`操作失敗：${error.message}`)
  }
}
</script>

<style scoped>
/* 原有的按鈕樣式保持不變 */
</style>
