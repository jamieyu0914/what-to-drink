// AWS Amplify 工具函數
// 處理 Amplify 登入、登出

export class AmplifyAuth {
  constructor() {
    this.userPoolId = import.meta.env.VITE_COGNITO_USER_POOL_ID
    this.clientId = import.meta.env.VITE_COGNITO_CLIENT_ID
    this.domain = import.meta.env.VITE_COGNITO_DOMAIN
    this.redirectUri =
      import.meta.env.VITE_COGNITO_REDIRECT_URI || `${window.location.origin}/login`
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
    try {
      this.clearCurrentUser()

      if (this.isConfigured()) {
        try {
          const logoutUrl = `https://${this.domain}/logout?client_id=${this.clientId}&logout_uri=${encodeURIComponent(this.redirectUri)}`

          // 檢查 logoutUrl 是否有效
          if (logoutUrl && logoutUrl.startsWith('https://')) {
            console.log('重定向到 Amplify 登出頁面:', logoutUrl)
            window.location.href = logoutUrl
          } else {
            throw new Error('無效的登出 URL')
          }
        } catch (urlError) {
          console.error('生成 Amplify 登出 URL 失敗:', urlError)
          // 回退到本地登出
          window.location.href = '/login'
        }
      } else {
        // 直接重定向到登入頁面
        window.location.href = '/login'
      }
    } catch (error) {
      console.error('Amplify 登出錯誤:', error)
      // 確保總是清除用戶信息
      this.clearCurrentUser()
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
}

export default new AmplifyAuth()
