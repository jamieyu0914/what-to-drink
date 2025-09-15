// AWS Cognito 工具函數
// 處理 Cognito 登入、登出和 token 管理

export class CognitoAuth {
  constructor() {
    this.domain = import.meta.env.VITE_COGNITO_DOMAIN
    this.clientId = import.meta.env.VITE_COGNITO_CLIENT_ID
    this.redirectUri = import.meta.env.VITE_COGNITO_REDIRECT_URI || window.location.origin
  }

  // 檢查是否已設定 Cognito
  isConfigured() {
    return !!(this.domain && this.clientId)
  }

  // 獲取登入 URL
  getLoginUrl() {
    if (!this.isConfigured()) {
      throw new Error('Cognito 未正確設定')
    }

    // 使用空格分隔的 scope 參數，並進行正確的 URL 編碼
    const scope = 'openid'
    const loginUrl = `https://${this.domain}/login?client_id=${this.clientId}&response_type=code&scope=${encodeURIComponent(scope)}&redirect_uri=${encodeURIComponent(this.redirectUri)}`
    console.log('Cognito Login URL:', loginUrl)
    console.log('Redirect URI:', this.redirectUri)

    return loginUrl
  }

  // 獲取登出 URL
  getLogoutUrl() {
    if (!this.isConfigured()) {
      throw new Error('Cognito 未正確設定')
    }

    return `https://${this.domain}/logout?client_id=${this.clientId}&logout_uri=${encodeURIComponent(this.redirectUri)}`
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

      // 在實際應用中，您需要將授權碼發送到後端來換取 token
      // 這裡只是清除 URL 中的參數
      this.clearUrlParams()

      return {
        success: true,
        code: code,
        message: '登入成功！',
      }
    }
    return null
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

  // 清除用戶信息
  clearCurrentUser() {
    localStorage.removeItem('cognito_user')
  }

  // 檢查是否已登入
  isLoggedIn() {
    return !!this.getCurrentUser()
  }
}

export default new CognitoAuth()
