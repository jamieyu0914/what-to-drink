# Cognito 登入成功串接修正說明

## 修正內容

### 1. 完善 Cognito 認證流程 (`cognito.js`)

#### 新增功能：

- **授權碼換取 Token**: `exchangeCodeForToken()` - 將 Cognito 授權碼換取 access token
- **獲取用戶信息**: `getUserInfo()` - 使用 access token 獲取用戶詳細信息
- **Token 有效性檢查**: `isTokenValid()` - 檢查 token 是否過期
- **完整登出流程**: `logout()` - 包含 Cognito 登出頁面重定向

#### 改進的 `handleLoginCallback()`:

- 完整處理授權碼到用戶信息的流程
- 保存用戶信息和 token 到 localStorage
- 更好的錯誤處理和用戶反饋

#### Scope 設定調整:

- 從 `openid` 擴展到 `openid email profile`
- 可獲取更多用戶信息（姓名、電子郵件等）

### 2. 改善登入組件 (`LoginDisplay.vue`)

#### 功能改進：

- **頁面重定向記憶**: 登入成功後返回之前訪問的頁面
- **更好的錯誤處理**: 針對不同錯誤類型提供具體解決方案
- **狀態同步**: 與全局用戶狀態管理整合

#### 錯誤處理改進：

- 更詳細的錯誤訊息
- 環境變數檢查提示
- OAuth 設定問題診斷

### 3. 全局用戶狀態管理 (`auth.js`)

#### 新增功能：

- **統一用戶狀態**: 管理 Cognito、Amplify 和本地登入狀態
- **自動初始化**: 應用啟動時檢查現有登入狀態
- **計算屬性**: `isLoggedIn`、`userDisplayName`
- **統一登出**: 根據登入類型執行相應登出流程

## 使用方式

### 1. 環境變數設定

```env
VITE_COGNITO_DOMAIN=your-cognito-domain.auth.region.amazoncognito.com
VITE_COGNITO_CLIENT_ID=your-client-id
VITE_COGNITO_REDIRECT_URI=http://localhost:5173/login  # 可選
```

### 2. Cognito 用戶池設定

確保在 AWS Cognito 控制台中：

#### 應用程式客戶端設定：

- **允許的回調 URL**: `http://localhost:5173/login`（開發環境）
- **允許的登出 URL**: `http://localhost:5173/login`
- **OAuth 流程**: 啟用授權碼流程
- **OAuth Scope**: 啟用 `openid`, `email`, `profile`

### 3. 在其他組件中使用用戶狀態

```vue
<script setup>
import authStore from '@/utils/auth.js'
import { onMounted } from 'vue'

// 初始化認證狀態
onMounted(async () => {
  await authStore.initializeAuth()
})

// 登出處理
const handleLogout = async () => {
  await authStore.logout()
}
</script>

<template>
  <div v-if="authStore.isLoggedIn.value">
    <p>歡迎，{{ authStore.userDisplayName.value }}！</p>
    <button @click="handleLogout">登出</button>
  </div>
  <div v-else>
    <p>請先登入</p>
  </div>
</template>
```

## 登入流程

### Cognito 登入流程：

1. 用戶點擊「使用 Cognito 登入」
2. 保存當前頁面路徑到 sessionStorage
3. 重定向到 Cognito 登入頁面
4. 用戶在 Cognito 頁面完成登入
5. Cognito 重定向回應用並帶上授權碼
6. `handleLoginCallback()` 處理授權碼：
   - 換取 access token
   - 獲取用戶信息
   - 保存到 localStorage 和全局狀態
7. 重定向到之前的頁面或首頁

### 錯誤處理：

- **環境變數未設定**: 提示需要設定的變數
- **Scope 錯誤**: 提示檢查 OAuth scope 設定
- **重定向 URL 不匹配**: 提示檢查回調 URL 設定
- **Token 交換失敗**: 詳細的錯誤日誌和用戶提示

## 注意事項

1. **生產環境**: 記得更新環境變數和 Cognito 設定中的回調 URL
2. **安全性**: Access token 只保存在記憶體中，敏感信息不會被記錄
3. **Token 過期**: 系統會檢查 token 有效性，過期時需要重新登入
4. **CORS 設定**: 確保 Cognito 域名允許來自您應用域名的請求

## 調試

開啟瀏覽器開發工具的 Console，可以看到詳細的登入流程日誌：

- Cognito 設定信息
- 登入 URL 生成
- 授權碼接收
- Token 交換過程
- 用戶信息獲取

這些修正確保了 Cognito 登入的完整性和穩定性，提供更好的用戶體驗。
