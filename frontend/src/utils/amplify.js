// AWS Amplify 工具函數
// 使用官方 AWS Amplify SDK 處理登入、登出和 S3 上傳

import { Amplify } from 'aws-amplify'
import {
  signInWithRedirect,
  signOut as amplifySignOut,
  getCurrentUser,
  fetchAuthSession,
} from '@aws-amplify/auth'
import { uploadData } from '@aws-amplify/storage'

export class AmplifyAuth {
  constructor() {
    this.userPoolId = import.meta.env.VITE_COGNITO_USER_POOL_ID
    this.clientId = import.meta.env.VITE_COGNITO_CLIENT_ID
    this.domain = import.meta.env.VITE_COGNITO_DOMAIN
    this.redirectUri =
      import.meta.env.VITE_COGNITO_REDIRECT_URI || `${window.location.origin}/login`
    this.awsRegion = import.meta.env.VITE_AWS_REGION || 'us-east-1'
    this.identityPoolId = import.meta.env.VITE_COGNITO_IDENTITY_POOL_ID
    this.s3BucketName = import.meta.env.VITE_S3_BUCKET_NAME

    this.initializeAmplify()
  }

  // 初始化 Amplify 配置
  initializeAmplify() {
    if (this.isConfigured()) {
      try {
        const amplifyConfig = {
          Auth: {
            Cognito: {
              userPoolId: this.userPoolId,
              userPoolClientId: this.clientId,
              loginWith: {
                oauth: {
                  domain: this.domain,
                  scopes: ['openid'],
                  redirectSignIn: [this.redirectUri],
                  redirectSignOut: [this.redirectUri],
                  responseType: 'code',
                },
              },
            },
          },
        }

        // 如果有設定 Identity Pool ID，則加入配置
        if (this.identityPoolId) {
          amplifyConfig.Auth.Cognito.identityPoolId = this.identityPoolId
        }

        // 如果有設定 S3，則加入 Storage 配置
        if (this.s3BucketName) {
          amplifyConfig.Storage = {
            S3: {
              bucket: this.s3BucketName,
              region: this.awsRegion,
            },
          }
        }

        Amplify.configure(amplifyConfig)
        console.log('Amplify 初始化成功')
      } catch (error) {
        console.error('Amplify 初始化失敗:', error)
      }
    }
  }

  // 檢查是否已設定 Amplify
  isConfigured() {
    return !!(this.userPoolId && this.clientId && this.domain)
  }

  // 檢查是否已登入
  async isLoggedIn() {
    try {
      const user = await getCurrentUser()
      return !!user
    } catch (error) {
      console.log('用戶未登入:', error.message)
      return false
    }
  }

  // 獲取當前用戶
  async getCurrentUser() {
    try {
      const user = await getCurrentUser()
      const session = await fetchAuthSession()

      return {
        sub: user.userId,
        email: user.signInDetails?.loginId || 'unknown',
        name: user.username,
        loginType: 'amplify',
        loginTime: new Date().toISOString(),
        tokens: {
          accessToken: session.tokens?.accessToken?.toString(),
          idToken: session.tokens?.idToken?.toString(),
          refreshToken: session.tokens?.refreshToken?.toString(),
        },
      }
    } catch (error) {
      console.error('獲取 Amplify 用戶信息失敗:', error)
      return null
    }
  }

  // 保存用戶信息到 localStorage (Amplify 會自動處理)
  setCurrentUser(userInfo) {
    // Amplify 會自動管理用戶狀態，此方法保留以保持 API 兼容性
    console.log('用戶信息已由 Amplify 自動管理')
  }

  // 清除用戶信息 (Amplify 會自動處理)
  clearCurrentUser() {
    // Amplify 會在登出時自動清除用戶信息
    console.log('用戶信息將由 Amplify 自動清除')
  }

  // 使用 Hosted UI 登入
  async signInWithHostedUI() {
    if (!this.isConfigured()) {
      throw new Error('Amplify 未正確設定')
    }

    try {
      console.log('開始 Amplify OAuth 登入流程')
      await signInWithRedirect({ provider: 'Cognito' })
    } catch (error) {
      console.error('Amplify 登入錯誤:', error)
      throw new Error(`登入失敗: ${error.message}`)
    }
  }

  // 登出
  async signOut() {
    try {
      console.log('開始 Amplify 登出流程')
      await amplifySignOut()
      console.log('Amplify 登出成功')

      // 重定向到登入頁面
      window.location.href = '/login'
    } catch (error) {
      console.error('Amplify 登出錯誤:', error)
      // 即使登出失敗，也重定向到登入頁面
      window.location.href = '/login'
    }
  }

  // 處理登入回調 (Amplify 會自動處理 OAuth 回調)
  async handleLoginCallback() {
    try {
      // 檢查是否已成功登入
      const isLoggedIn = await this.isLoggedIn()

      if (isLoggedIn) {
        const user = await this.getCurrentUser()
        console.log('Amplify 登入回調處理成功:', user)

        // 清除 URL 中的參數
        if (window.history && window.history.replaceState) {
          window.history.replaceState({}, document.title, window.location.pathname)
        }

        return {
          success: true,
          message: 'Amplify 登入成功！',
          user: user,
        }
      } else {
        return {
          success: false,
          message: '尚未登入',
        }
      }
    } catch (error) {
      console.error('處理登入回調失敗:', error)
      return {
        success: false,
        error: error.message,
        message: '登入處理失敗，請重試',
      }
    }
  }

  // 獲取當前有效的 ID token
  async getValidIdToken() {
    try {
      const session = await fetchAuthSession()
      return session.tokens?.idToken?.toString() || null
    } catch (error) {
      console.error('獲取 ID token 失敗:', error)
      return null
    }
  }

  // 調試方法：檢查當前配置和認證狀態
  async debugAuthState() {
    console.log('=== Amplify 配置和認證狀態檢查 ===')
    console.log('配置狀態:', {
      userPoolId: this.userPoolId,
      clientId: this.clientId,
      domain: this.domain,
      identityPoolId: this.identityPoolId,
      s3BucketName: this.s3BucketName,
      awsRegion: this.awsRegion,
    })

    try {
      const isLoggedIn = await this.isLoggedIn()
      console.log('登入狀態:', isLoggedIn)

      if (isLoggedIn) {
        const session = await fetchAuthSession()
        console.log('認證會話:', {
          hasCredentials: !!session.credentials,
          hasTokens: !!session.tokens,
          credentialsProvider: session.credentials?.accessKeyId ? 'Identity Pool' : 'None',
        })

        const user = await this.getCurrentUser()
        console.log('用戶信息:', user)
      }
    } catch (error) {
      console.error('檢查認證狀態時發生錯誤:', error)
    }
    console.log('=== 檢查完成 ===')
  }

  // 上傳檔案到 S3 (使用 Amplify Storage)
  async uploadToS3(file, s3Key) {
    try {
      // 檢查用戶是否已登入
      const isLoggedIn = await this.isLoggedIn()
      if (!isLoggedIn) {
        throw new Error('用戶未登入')
      }

      // 檢查是否有有效的認證會話
      const session = await fetchAuthSession()
      if (!session.credentials) {
        throw new Error('無法取得 AWS 憑證，請確保已設定 Identity Pool')
      }

      // 檢查 S3 設定
      if (!this.s3BucketName) {
        throw new Error('S3 bucket 名稱未設置，請檢查 VITE_S3_BUCKET_NAME 環境變數')
      }

      // 檢查 Identity Pool 設定
      if (!this.identityPoolId) {
        throw new Error('Identity Pool ID 未設置，請檢查 VITE_COGNITO_IDENTITY_POOL_ID 環境變數')
      }

      console.log(`開始上傳檔案到 S3: ${s3Key}`)

      // 使用 Amplify Storage 上傳檔案
      const result = await uploadData({
        key: s3Key,
        data: file,
        options: {
          contentType: file.type || 'application/octet-stream',
          metadata: {
            'uploaded-by': 'amplify-user',
            'original-name': file.name,
            'upload-time': new Date().toISOString(),
          },
        },
      }).result

      console.log('檔案上傳成功:', result)

      // 構建 S3 URL
      const s3Url = `https://${this.s3BucketName}.s3.${this.awsRegion}.amazonaws.com/${s3Key}`

      return {
        success: true,
        fileName: file.name,
        s3Key: s3Key,
        s3Url: s3Url,
        uploadTime: new Date().toISOString(),
        fileSize: file.size,
        fileType: file.type,
        etag: result.eTag || result.ETag,
      }
    } catch (error) {
      console.error('Amplify S3 上傳錯誤:', error)
      return {
        success: false,
        error: error.message,
        fileName: file.name,
      }
    }
  }
}

export default new AmplifyAuth()
