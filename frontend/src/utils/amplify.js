// AWS Amplify 工具函數
// 處理 Amplify 登入、登出和 S3 上傳

export class AmplifyAuth {
  constructor() {
    this.userPoolId = import.meta.env.VITE_COGNITO_USER_POOL_ID
    this.clientId = import.meta.env.VITE_COGNITO_CLIENT_ID
    this.domain = import.meta.env.VITE_COGNITO_DOMAIN
    this.redirectUri = import.meta.env.VITE_COGNITO_REDIRECT_URI || window.location.origin
    this.s3BucketName = import.meta.env.VITE_S3_BUCKET_NAME
    this.awsRegion = import.meta.env.VITE_AWS_REGION || 'us-east-1'
  }

  // 檢查是否已設定 Amplify
  isConfigured() {
    return !!(this.userPoolId && this.clientId && this.domain)
  }

  // 檢查是否已登入
  async isLoggedIn() {
    return !!this.getCurrentUser()
  }

  // 獲取當前用戶
  getCurrentUser() {
    try {
      const userStr = localStorage.getItem('amplify_user')
      return userStr ? JSON.parse(userStr) : null
    } catch (error) {
      console.error('獲取 Amplify 用戶信息失敗:', error)
      return null
    }
  }

  // 保存用戶信息到 localStorage
  setCurrentUser(userInfo) {
    try {
      const amplifyUser = {
        ...userInfo,
        loginType: 'amplify',
        loginTime: new Date().toISOString(),
      }
      localStorage.setItem('amplify_user', JSON.stringify(amplifyUser))
    } catch (error) {
      console.error('保存 Amplify 用戶信息失敗:', error)
    }
  }

  // 清除用戶信息
  clearCurrentUser() {
    localStorage.removeItem('amplify_user')
  }

  // 使用 Hosted UI 登入
  async signInWithHostedUI() {
    if (!this.isConfigured()) {
      throw new Error('Amplify 未正確設定')
    }

    const loginUrl = `https://${this.domain}/login?client_id=${this.clientId}&response_type=code&scope=email+openid+profile&redirect_uri=${encodeURIComponent(this.redirectUri)}`

    console.log('重定向至 Amplify 登入頁面:', loginUrl)
    window.location.href = loginUrl
  }

  // 登出
  async signOut() {
    this.clearCurrentUser()

    if (this.isConfigured()) {
      const logoutUrl = `https://${this.domain}/logout?client_id=${this.clientId}&logout_uri=${encodeURIComponent(this.redirectUri)}`
      window.location.href = logoutUrl
    }
  }

  // 處理登入回調
  async handleLoginCallback() {
    const urlParams = new URLSearchParams(window.location.search)
    const code = urlParams.get('code')

    if (code) {
      console.log('收到 Amplify 授權碼:', code)

      // 在實際應用中，您需要將授權碼發送到後端來換取 token
      // 這裡模擬一個成功的登入
      const mockUser = {
        sub: 'amplify-user-' + Date.now(),
        email: 'user@example.com',
        name: 'Amplify User',
        loginType: 'amplify',
      }

      this.setCurrentUser(mockUser)

      // 清除 URL 中的參數
      if (window.history && window.history.replaceState) {
        window.history.replaceState({}, document.title, window.location.pathname)
      }

      return {
        success: true,
        code: code,
        message: 'Amplify 登入成功！',
        user: mockUser,
      }
    }

    return null
  }

  // 上傳檔案到 S3
  async uploadToS3(file, s3Key) {
    if (!this.s3BucketName) {
      throw new Error('S3 Bucket 名稱未設定')
    }

    if (!this.isLoggedIn()) {
      throw new Error('請先登入才能上傳檔案')
    }

    try {
      // 在實際應用中，這裡應該使用 AWS SDK 或 Amplify Storage
      // 這裡模擬上傳成功的情況
      console.log('模擬上傳檔案到 S3:', { file: file.name, s3Key })

      // 模擬上傳延遲
      await new Promise((resolve) => setTimeout(resolve, 1000))

      const s3Url = `https://${this.s3BucketName}.s3.${this.awsRegion}.amazonaws.com/${s3Key}`

      return {
        success: true,
        s3Key: s3Key,
        s3Url: s3Url,
        message: '檔案上傳成功',
      }
    } catch (error) {
      console.error('S3 上傳錯誤:', error)
      throw new Error(`上傳失敗: ${error.message}`)
    }
  }
}

export default new AmplifyAuth()
