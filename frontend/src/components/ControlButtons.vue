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
        <button id="uploadButton" @click="handleFileUpload" class="btn btn-success">
          上傳檔案
        </button>
        <input
          type="file"
          ref="fileInput"
          @change="onFileSelected"
          style="display: none"
          accept=".zip"
        />
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
const fileInput = ref(null)
const uploadedFiles = ref([]) // 用於儲存上傳的檔案記錄
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

// 生成英文大小寫數字 6 位碼的函數
const generateRandomHash = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < 6; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return result
}

// 格式化檔案名稱以符合 S3 安全上傳要求
const formatSafeFileName = (fileName) => {
  // 移除或替換不安全的字符
  return fileName
    .replace(/[^a-zA-Z0-9.-]/g, '_') // 替換特殊字符為下劃線
    .replace(/_{2,}/g, '_') // 將多個連續下劃線替換為單個
    .replace(/^_|_$/g, '') // 移除開頭和結尾的下劃線
    .toLowerCase() // 轉為小寫
}

// 生成帶時間戳的唯一檔案名
const generateUniqueFileName = (originalName) => {
  const timestamp = Date.now()
  const hash = generateRandomHash()
  const safeName = formatSafeFileName(originalName)
  return `${hash}-${timestamp}-${safeName}`
}

const handleFileUpload = () => {
  fileInput.value.click()
}

const onFileSelected = async (event) => {
  const file = event.target.files[0]
  if (file) {
    console.log('選擇的檔案:', file.name)
    const eventSlug = 'NHWgimDF'
    const hash = generateRandomHash()
    // try {
    //   // 優先使用 Amplify Storage (如果已登入且配置正確)
    //   if (
    //     amplifyAuth.isConfigured() &&
    //     isLoggedIn.value &&
    //     currentUser.value?.loginType === 'amplify'
    //   ) {
    //     const timestamp = new Date().getTime()
    //     const s3Key = `uploads/${currentUser.value.sub}/${timestamp}-${file.name}`
    //     const result = await amplifyAuth.uploadToS3(file, s3Key)
    //     if (result.success) {
    //       const uploadRecord = {
    //         fileName: file.name,
    //         s3Key: result.s3Key,
    //         s3Url: result.s3Url,
    //         uploadTime: new Date().toISOString(),
    //         fileSize: file.size,
    //         fileType: file.type,
    //         uploadMethod: 'amplify',
    //       }
    //       uploadedFiles.value.push(uploadRecord)
    //       console.log('Amplify 上傳成功:', uploadRecord)
    //       alert(`檔案上傳成功！\n檔案名稱: ${file.name}\n使用 Amplify Storage 上傳`)
    //       return
    //     }
    //   }
    //   // 如果 Amplify 不可用，回退到原有邏輯
    //   await uploadWithLegacyMethod(file)
    // } catch (error) {
    //   console.error('檔案上傳錯誤:', error)
    //   alert(
    //     `檔案上傳失敗: ${error.message}\n\n建議:\n1. 確認已使用 Amplify 登入\n2. 檢查 S3 權限設定\n3. 檢查環境變數配置`,
    //   )
    // }

    try {
      //使用Cognito上傳 [成功]
      const timestamp = new Date().getTime()
      const s3Key = `public/temp-template/pending/${eventSlug}/${hash}-${timestamp}.${file.name.split('.').pop()}`
      const result = await cognitoAuth.uploadToS3(file, s3Key)
      if (result.success) {
        const uploadRecord = {
          fileName: file.name,
          s3Key: result.s3Key,
          s3Url: result.s3Url,
          uploadTime: new Date().toISOString(),
          fileSize: file.size,
          fileType: file.type,
          uploadMethod: 'cognito',
        }
        uploadedFiles.value.push(uploadRecord)
        console.log('Cognito 上傳成功:', uploadRecord)
        alert(`檔案上傳成功！\n檔案名稱: ${file.name}\n使用 Cognito 上傳`)
        return
      }
    } catch (error) {
      console.error('檔案上傳錯誤:', error)
      alert(
        `檔案上傳失敗: ${error.message}\n\n建議:\n1. 確認已使用 Cognito 登入\n2. 檢查 S3 權限設定\n3. 檢查環境變數配置`,
      )
    }
  }
}

// 原有的上傳方法作為後備
// const uploadWithLegacyMethod = async (file) => {
//   // 生成檔案的 S3 鍵名 (路徑)
//   const timestamp = new Date().getTime()
//   const s3Key = `uploads/${timestamp}-${file.name}`
//   const bucketName = import.meta.env.VITE_S3_BUCKET_NAME
//   const region = import.meta.env.VITE_AWS_REGION || 'us-east-1'

//   // 方法1: 使用 FormData 直接上傳到 S3 (需要正確的 CORS 設定)
//   const formData = new FormData()
//   formData.append('key', s3Key)
//   formData.append('Content-Type', file.type || 'application/octet-stream')
//   formData.append('file', file)

//   // 構建 S3 端點 URL
//   const s3Endpoint = `https://${bucketName}.s3.${region}.amazonaws.com/`

//   try {
//     const response = await fetch(s3Endpoint, {
//       method: 'POST',
//       body: formData,
//       mode: 'cors',
//     })

//     if (response.ok) {
//       // 構建 S3 URL
//       const s3Url = `https://${bucketName}.s3.${region}.amazonaws.com/${s3Key}`

//       // 儲存上傳記錄
//       const uploadRecord = {
//         fileName: file.name,
//         s3Key: s3Key,
//         s3Url: s3Url,
//         uploadTime: new Date().toISOString(),
//         fileSize: file.size,
//         fileType: file.type,
//         uploadMethod: 'direct',
//       }

//       uploadedFiles.value.push(uploadRecord)

//       console.log('檔案上傳成功:', uploadRecord)
//       alert(`檔案上傳成功！\n檔案名稱: ${file.name}\nS3 位置: ${s3Url}`)
//     } else {
//       throw new Error(`上傳失敗: ${response.status} ${response.statusText}`)
//     }
//   } catch (fetchError) {
//     console.warn('直接上傳失敗，嘗試後端代理上傳:', fetchError)

//     // 方法2: 通過後端 API 上傳 (推薦的安全方法)
//     await uploadViaBackend(file, s3Key)
//   }
// }

// 通過後端 API 上傳檔案的方法
// const uploadViaBackend = async (file, s3Key) => {
//   const formData = new FormData()
//   console.log('Uploading via backend with file:', file)
//   formData.append('file', file)
//   console.log('Uploading via backend with s3Key:', s3Key)
//   formData.append('s3Key', s3Key)

//   // 假設後端有一個上傳 API 端點
//   const backendUrl = 'http://localhost:8088/api/upload' // 請根據您的後端 URL 調整

//   try {
//     const response = await fetch(backendUrl, {
//       method: 'POST',
//       body: formData,
//     })

//     if (response.ok) {
//       const result = await response.json()

//       // 儲存上傳記錄
//       const uploadRecord = {
//         fileName: file.name,
//         s3Key: s3Key,
//         s3Url:
//           result.s3Url ||
//           `https://${import.meta.env.VITE_S3_BUCKET_NAME}.s3.${import.meta.env.VITE_AWS_REGION || 'us-east-1'}.amazonaws.com/${s3Key}`,
//         uploadTime: new Date().toISOString(),
//         fileSize: file.size,
//         fileType: file.type,
//       }

//       uploadedFiles.value.push(uploadRecord)

//       console.log('後端上傳成功:', uploadRecord)
//       alert(`檔案上傳成功！\n檔案名稱: ${file.name}\nS3 位置: ${uploadRecord.s3Url}`)
//     } else {
//       throw new Error(`後端上傳失敗: ${response.status}`)
//     }
//   } catch (backendError) {
//     console.error('後端上傳失敗:', backendError)
//     throw new Error('所有上傳方法都失敗了，請檢查設定或聯繫管理員')
//   }
// }
</script>

<style scoped>
/* 原有的按鈕樣式保持不變 */
</style>
