// AWS Cognito 工具函數
// 處理 Cognito 登入、登出和 token 管理

export class CognitoAuth {
  constructor() {
    this.domain = import.meta.env.VITE_COGNITO_DOMAIN
    this.clientId = import.meta.env.VITE_COGNITO_CLIENT_ID

    // 優先使用環境變數的重定向 URI，否則使用登入頁面
    this.redirectUri =
      import.meta.env.VITE_COGNITO_REDIRECT_URI || `${window.location.origin}/login`

    // 確保 redirect URI 是完整的 URL
    if (this.redirectUri && !this.redirectUri.startsWith('http')) {
      this.redirectUri = `${window.location.origin}${this.redirectUri.startsWith('/') ? '' : '/'}${this.redirectUri}`
    }

    console.log('Cognito 配置:', {
      domain: this.domain,
      clientId: this.clientId ? '[CONFIGURED]' : '[NOT SET]',
      redirectUri: this.redirectUri,
    })
  }

  // 檢查是否已設定 Cognito
  isConfigured() {
    const isValid = !!(this.domain && this.clientId && this.redirectUri)

    if (!isValid) {
      console.warn('Cognito 配置不完整:', {
        domain: !!this.domain,
        clientId: !!this.clientId,
        redirectUri: !!this.redirectUri,
      })
    }

    return isValid
  }

  // 驗證 Cognito 配置的詳細信息
  validateConfiguration() {
    const errors = []

    if (!this.domain) {
      errors.push('VITE_COGNITO_DOMAIN 環境變數未設置')
    } else if (!this.domain.includes('.')) {
      errors.push('VITE_COGNITO_DOMAIN 格式不正確')
    }

    if (!this.clientId) {
      errors.push('VITE_COGNITO_CLIENT_ID 環境變數未設置')
    }

    if (!this.redirectUri) {
      errors.push('重定向 URI 未設置')
    } else if (!this.redirectUri.startsWith('http')) {
      errors.push('重定向 URI 必須是完整的 URL')
    }

    if (errors.length > 0) {
      console.error('Cognito 配置錯誤:', errors)
      return { valid: false, errors }
    }

    return { valid: true, errors: [] }
  }

  // 獲取登入 URL
  getLoginUrl() {
    if (!this.isConfigured()) {
      throw new Error('Cognito 未正確設定')
    }

    // 使用空格分隔的 scope 參數，包含更多權限以獲取用戶信息和 refresh token
    const scope = 'openid email profile aws.cognito.signin.user.admin'
    const loginUrl = `https://${this.domain}/login?client_id=${this.clientId}&response_type=code&scope=${encodeURIComponent(scope)}&redirect_uri=${encodeURIComponent(this.redirectUri)}`
    console.log('Cognito Login URL:', loginUrl)
    console.log('Redirect URI:', this.redirectUri)
    console.log('Scope:', scope)

    return loginUrl
  }

  // 獲取登出 URL
  getLogoutUrl() {
    if (!this.isConfigured()) {
      throw new Error('Cognito 未正確設定')
    }

    // 檢查必要的配置參數
    if (!this.domain || !this.clientId || !this.redirectUri) {
      throw new Error('Cognito 登出 URL 生成失敗：缺少必要配置參數')
    }

    try {
      // AWS Cognito 登出 URL 需要使用 redirect_uri 而不是 logout_uri
      const logoutUrl = `https://${this.domain}/logout?client_id=${this.clientId}&redirect_uri=${encodeURIComponent(this.redirectUri)}`

      // 驗證生成的 URL 格式
      if (!logoutUrl.startsWith('https://') || !logoutUrl.includes('/logout')) {
        throw new Error('生成的登出 URL 格式無效')
      }

      console.log('生成的登出 URL:', logoutUrl)
      console.log('使用的 redirect_uri:', this.redirectUri)

      return logoutUrl
    } catch (error) {
      console.error('生成 Cognito 登出 URL 時發生錯誤:', error)
      throw new Error(`無法生成 Cognito 登出 URL: ${error.message}`)
    }
  }

  // 檢查 URL 中是否有授權碼
  hasAuthCode() {
    const urlParams = new URLSearchParams(window.location.search)
    return urlParams.has('code')
  }

  // 獲取授權碼
  getAuthCode() {
    const urlParams = new URLSearchParams(window.location.search)
    return urlParams.get('code')
  }

  // 清除 URL 中的查詢參數
  clearUrlParams() {
    if (window.history && window.history.replaceState) {
      window.history.replaceState({}, document.title, window.location.pathname)
    }
  }

  // 處理登入成功後的回調
  async handleLoginCallback() {
    if (this.hasAuthCode()) {
      const code = this.getAuthCode()
      console.log('收到 Cognito 授權碼:', code)

      try {
        // 使用授權碼換取 token
        const tokenData = await this.exchangeCodeForToken(code)

        if (tokenData && tokenData.access_token) {
          // 使用 access token 獲取用戶信息
          const userInfo = await this.getUserInfo(tokenData.access_token)

          if (userInfo) {
            // 保存用戶信息和 token
            const userData = {
              ...userInfo,
              loginType: 'cognito',
              loginTime: new Date().toISOString(),
              tokens: tokenData,
            }

            this.setCurrentUser(userData)

            // 清除 URL 中的參數
            this.clearUrlParams()

            return {
              success: true,
              user: userData,
              message: `登入成功！歡迎回來，${userInfo.name || userInfo.email || '用戶'}`,
            }
          }
        }

        throw new Error('無法獲取用戶信息')
      } catch (error) {
        console.error('處理 Cognito 登入回調時發生錯誤:', error)

        // 清除任何可能已保存的不完整數據
        this.clearCurrentUser()

        // 清除 URL 中的參數，即使發生錯誤
        this.clearUrlParams()

        return {
          success: false,
          error: error.message,
          message: `登入處理失敗：${error.message}`,
        }
      }
    }
    return null
  }

  // 使用授權碼換取 token
  async exchangeCodeForToken(code) {
    if (!this.isConfigured()) {
      throw new Error('Cognito 未正確設定')
    }

    const tokenUrl = `https://${this.domain}/oauth2/token`

    const params = new URLSearchParams({
      grant_type: 'authorization_code',
      client_id: this.clientId,
      code: code,
      redirect_uri: this.redirectUri,
    })

    try {
      const response = await fetch(tokenUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params,
      })

      if (!response.ok) {
        const errorData = await response.text()
        console.error('Token 交換失敗:', errorData)
        throw new Error(`Token 交換失敗: ${response.status} ${response.statusText}`)
      }

      const tokenData = await response.json()
      console.log('成功獲取 token:', { ...tokenData, access_token: '[HIDDEN]' })

      return tokenData
    } catch (error) {
      console.error('Token 交換錯誤:', error)
      throw error
    }
  }

  // 使用 access token 獲取用戶信息
  async getUserInfo(accessToken) {
    if (!this.isConfigured()) {
      throw new Error('Cognito 未正確設定')
    }

    const userInfoUrl = `https://${this.domain}/oauth2/userInfo`

    try {
      const response = await fetch(userInfoUrl, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      })

      if (!response.ok) {
        const errorData = await response.text()
        console.error('獲取用戶信息失敗:', errorData)
        throw new Error(`獲取用戶信息失敗: ${response.status} ${response.statusText}`)
      }

      const userInfo = await response.json()
      console.log('成功獲取用戶信息:', userInfo)

      return userInfo
    } catch (error) {
      console.error('獲取用戶信息錯誤:', error)
      throw error
    }
  }

  // 從 localStorage 獲取用戶信息（如果有的話）
  getCurrentUser() {
    try {
      const userStr = localStorage.getItem('cognito_user')
      return userStr ? JSON.parse(userStr) : null
    } catch (error) {
      console.error('獲取用戶信息失敗:', error)
      return null
    }
  }

  // 保存用戶信息到 localStorage
  setCurrentUser(userInfo) {
    try {
      localStorage.setItem('cognito_user', JSON.stringify(userInfo))
    } catch (error) {
      console.error('保存用戶信息失敗:', error)
    }
  }

  // 清除用戶信息和所有相關 token
  clearCurrentUser() {
    try {
      // 清除 Cognito 用戶信息
      localStorage.removeItem('cognito_user')

      // 清除可能存在的其他 token 相關存儲
      localStorage.removeItem('access_token')
      localStorage.removeItem('id_token')
      localStorage.removeItem('refresh_token')
      localStorage.removeItem('cognito_tokens')

      // 清除可能的 session storage
      sessionStorage.removeItem('cognito_user')
      sessionStorage.removeItem('access_token')
      sessionStorage.removeItem('id_token')
      sessionStorage.removeItem('refresh_token')
      sessionStorage.removeItem('cognito_tokens')

      console.log('已清除所有本地 token 和用戶信息')
    } catch (error) {
      console.error('清除用戶信息時發生錯誤:', error)
    }
  }

  // 強制清除所有認證相關數據（用於錯誤恢復）
  forceCleanup() {
    try {
      // 清除所有可能的 localStorage 項目
      const keysToRemove = []
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i)
        if (key && (key.includes('cognito') || key.includes('token') || key.includes('auth'))) {
          keysToRemove.push(key)
        }
      }
      keysToRemove.forEach((key) => localStorage.removeItem(key))

      // 清除所有可能的 sessionStorage 項目
      const sessionKeysToRemove = []
      for (let i = 0; i < sessionStorage.length; i++) {
        const key = sessionStorage.key(i)
        if (key && (key.includes('cognito') || key.includes('token') || key.includes('auth'))) {
          sessionKeysToRemove.push(key)
        }
      }
      sessionKeysToRemove.forEach((key) => sessionStorage.removeItem(key))

      console.log('已強制清除所有認證相關數據')
      return true
    } catch (error) {
      console.error('強制清除數據時發生錯誤:', error)
      return false
    }
  }

  // 檢查是否已登入
  isLoggedIn() {
    return !!this.getCurrentUser()
  }

  // 登出用戶
  async logout() {
    try {
      // 清除本地用戶信息
      this.clearCurrentUser()

      // 如果是 Cognito 用戶，重定向到 Cognito 登出頁面
      if (this.isConfigured()) {
        try {
          const logoutUrl = this.getLogoutUrl()
          console.log('重定向到 Cognito 登出頁面:', logoutUrl)

          // 檢查 logoutUrl 是否有效
          if (logoutUrl && logoutUrl.startsWith('https://')) {
            window.location.href = logoutUrl
          } else {
            throw new Error('無效的登出 URL')
          }
        } catch (urlError) {
          console.error('生成登出 URL 失敗:', urlError)
          // 回退到本地登出
          window.location.href = '/login'
        }
      } else {
        // 如果不是 Cognito 用戶，直接重定向到登入頁面
        window.location.href = '/login'
      }
    } catch (error) {
      console.error('登出錯誤:', error)
      // 即使發生錯誤，也要強制清除所有用戶信息和 token
      this.forceCleanup()
      // 確保總是能夠回到登入頁面
      try {
        window.location.href = '/login'
      } catch (redirectError) {
        console.error('重定向失敗:', redirectError)
        // 如果連重定向都失敗，嘗試重新載入頁面
        window.location.reload()
      }
    }
  }

  // 檢查 token 是否有效
  isTokenValid() {
    const user = this.getCurrentUser()
    if (!user || !user.tokens) {
      // 如果沒有用戶或 token，清除可能的無效數據
      this.clearCurrentUser()
      return false
    }

    // 檢查 token 是否過期（簡單檢查）
    if (user.tokens.expires_in) {
      const loginTime = new Date(user.loginTime).getTime()
      const expiresIn = user.tokens.expires_in * 1000 // 轉換為毫秒
      const now = Date.now()

      const isValid = now - loginTime < expiresIn

      // 如果 token 已過期，清除用戶數據
      if (!isValid) {
        console.log('Token 已過期，清除用戶數據')
        this.clearCurrentUser()
      }

      return isValid
    }

    return true
  }

  // 使用 refresh token 刷新 access token
  async refreshToken() {
    const user = this.getCurrentUser()
    if (!user || !user.tokens || !user.tokens.refresh_token) {
      throw new Error('沒有可用的 refresh token')
    }

    if (!this.isConfigured()) {
      throw new Error('Cognito 未正確設定')
    }

    const tokenUrl = `https://${this.domain}/oauth2/token`

    const params = new URLSearchParams({
      grant_type: 'refresh_token',
      client_id: this.clientId,
      refresh_token: user.tokens.refresh_token,
    })

    try {
      const response = await fetch(tokenUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params,
      })

      if (!response.ok) {
        const errorData = await response.text()
        console.error('Token 刷新失敗:', errorData)
        throw new Error(`Token 刷新失敗: ${response.status} ${response.statusText}`)
      }

      const tokenData = await response.json()
      console.log('成功刷新 token')

      // 更新用戶的 token 信息
      const updatedUser = {
        ...user,
        tokens: {
          ...user.tokens,
          access_token: tokenData.access_token,
          id_token: tokenData.id_token || user.tokens.id_token,
          expires_in: tokenData.expires_in,
          // 保留原有的 refresh_token（除非返回了新的）
          refresh_token: tokenData.refresh_token || user.tokens.refresh_token,
        },
        loginTime: new Date().toISOString(), // 更新登入時間
      }

      this.setCurrentUser(updatedUser)
      return tokenData
    } catch (error) {
      console.error('刷新 token 錯誤:', error)
      // 如果刷新失敗，清除用戶數據
      this.clearCurrentUser()
      throw error
    }
  }

  // 獲取有效的 access token（如果過期則自動刷新）
  async getValidAccessToken() {
    let user = this.getCurrentUser()
    if (!user || !user.tokens) {
      throw new Error('用戶未登入')
    }

    // 檢查 token 是否即將過期（提前 5 分鐘刷新）
    const REFRESH_BUFFER = 5 * 60 * 1000 // 5 分鐘
    const loginTime = new Date(user.loginTime).getTime()
    const expiresIn = (user.tokens.expires_in || 3600) * 1000 // 默認 1 小時
    const now = Date.now()
    const timeUntilExpiry = loginTime + expiresIn - now

    if (timeUntilExpiry <= REFRESH_BUFFER) {
      console.log('Token 即將過期，嘗試刷新...')
      try {
        await this.refreshToken()
        user = this.getCurrentUser() // 重新獲取更新後的用戶信息
      } catch (error) {
        console.error('自動刷新 token 失敗:', error)
        throw new Error('Token 已過期且無法刷新，請重新登入')
      }
    }

    if (!user || !user.tokens || !user.tokens.access_token) {
      throw new Error('無法獲取有效的 access token')
    }

    return user.tokens.access_token
  }

  // 上傳檔案到 S3
  async uploadToS3(file, s3Key) {
    try {
      // 獲取有效的 access token（如果過期會自動刷新）
      const accessToken = await this.getValidAccessToken()
      const user = this.getCurrentUser()

      if (!user || !user.tokens) {
        throw new Error('用戶未登入或 token 無效')
      }

      // 獲取環境變數
      const bucketName = import.meta.env.VITE_S3_BUCKET_NAME
      const region = import.meta.env.VITE_AWS_REGION || 'us-east-1'

      if (!bucketName) {
        throw new Error('S3 bucket 名稱未設置，請檢查 VITE_S3_BUCKET_NAME 環境變數')
      }

      // 動態導入 AWS SDK
      const { S3Client, PutObjectCommand } = await import('@aws-sdk/client-s3')
      const { fromCognitoIdentityPool } = await import(
        '@aws-sdk/credential-provider-cognito-identity'
      )
      const { CognitoIdentityClient } = await import('@aws-sdk/client-cognito-identity')

      // 檢查是否有身份池配置
      const identityPoolId = import.meta.env.VITE_COGNITO_IDENTITY_POOL_ID
      let s3Client

      if (identityPoolId) {
        // 使用 Cognito Identity Pool 進行身份驗證
        const cognitoIdentityClient = new CognitoIdentityClient({ region })

        const credentials = fromCognitoIdentityPool({
          client: cognitoIdentityClient,
          identityPoolId: identityPoolId,
          logins: {
            [`cognito-idp.${region}.amazonaws.com/${import.meta.env.VITE_COGNITO_USER_POOL_ID}`]:
              user.tokens.id_token,
          },
        })

        s3Client = new S3Client({
          region,
          credentials,
        })
      } else {
        // 檢查是否有 AWS 憑證配置
        const accessKeyId = import.meta.env.VITE_AWS_ACCESS_KEY_ID
        const secretAccessKey = import.meta.env.VITE_AWS_SECRET_ACCESS_KEY

        if (!accessKeyId || !secretAccessKey) {
          throw new Error(
            'AWS 憑證未設置。請設置 VITE_COGNITO_IDENTITY_POOL_ID 或 VITE_AWS_ACCESS_KEY_ID/VITE_AWS_SECRET_ACCESS_KEY',
          )
        }

        s3Client = new S3Client({
          region,
          credentials: {
            accessKeyId,
            secretAccessKey,
          },
        })
      }

      // 準備檔案內容
      const fileBuffer = await file.arrayBuffer()

      // 創建上傳命令
      const command = new PutObjectCommand({
        Bucket: bucketName,
        Key: s3Key,
        Body: new Uint8Array(fileBuffer),
        ContentType: file.type || 'application/octet-stream',
        // 添加一些基本的 metadata
        Metadata: {
          'uploaded-by': 'cognito-user',
          'original-name': file.name,
          'upload-time': new Date().toISOString(),
        },
      })

      // 執行上傳
      const response = await s3Client.send(command)

      if (response) {
        // 構建 S3 URL
        const s3Url = `https://${bucketName}.s3.${region}.amazonaws.com/${s3Key}`

        return {
          success: true,
          fileName: file.name,
          s3Key: s3Key,
          s3Url: s3Url,
          uploadTime: new Date().toISOString(),
          fileSize: file.size,
          fileType: file.type,
          uploadMethod: 'cognito',
          message: '檔案上傳成功',
          etag: response.ETag,
        }
      } else {
        throw new Error('S3 上傳失敗：無回應')
      }
    } catch (error) {
      console.error('S3 上傳錯誤:', error)
      return {
        success: false,
        error: error.message,
      }
    }
  }

  // 驗證並清理用戶數據（在應用啟動時調用）
  async validateAndCleanup() {
    try {
      const user = this.getCurrentUser()

      if (user) {
        // 檢查用戶數據完整性
        if (!user.loginType || !user.loginTime) {
          console.log('用戶數據不完整，清除數據')
          this.clearCurrentUser()
          return false
        }

        // 嘗試獲取有效的 access token（會自動刷新過期的 token）
        try {
          await this.getValidAccessToken()
          return true
        } catch (error) {
          console.log('Token 無效且無法刷新，清除用戶數據:', error.message)
          this.clearCurrentUser()
          return false
        }
      }

      return false
    } catch (error) {
      console.error('驗證用戶數據時發生錯誤:', error)
      this.forceCleanup()
      return false
    }
  }
}

export default new CognitoAuth()
