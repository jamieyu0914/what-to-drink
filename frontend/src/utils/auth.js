// 簡單的用戶狀態管理
import { ref, computed, readonly } from 'vue'
import cognitoAuth from './cognito.js'
import amplifyAuth from './amplify.js'

// 用戶狀態
const currentUser = ref(null)
const isLoading = ref(false)

// 計算屬性
const isLoggedIn = computed(() => !!currentUser.value)
const userDisplayName = computed(() => {
  if (!currentUser.value) return null
  return currentUser.value.name || currentUser.value.email?.split('@')[0] || '用戶'
})

// 初始化用戶狀態
const initializeAuth = async () => {
  isLoading.value = true

  try {
    // 檢查 Cognito 用戶
    const cognitoUser = cognitoAuth.getCurrentUser()
    if (cognitoUser && cognitoAuth.isTokenValid()) {
      currentUser.value = cognitoUser
      console.log('檢測到有效的 Cognito 用戶:', currentUser.value)
      return
    }

    // 檢查 Amplify 用戶
    if (amplifyAuth.isConfigured()) {
      try {
        const amplifyUser = await amplifyAuth.getCurrentUser()
        if (amplifyUser) {
          currentUser.value = {
            ...amplifyUser,
            loginType: 'amplify',
            loginTime: new Date().toISOString(),
          }
          console.log('檢測到 Amplify 用戶:', currentUser.value)
          return
        }
      } catch (error) {
        console.log('未檢測到 Amplify 用戶:', error.message)
      }
    }

    // 沒有檢測到用戶
    currentUser.value = null
    console.log('未檢測到已登入的用戶')
  } catch (error) {
    console.error('初始化認證狀態時發生錯誤:', error)
    currentUser.value = null
  } finally {
    isLoading.value = false
  }
}

// 設置用戶
const setUser = (user) => {
  currentUser.value = user
  console.log('設置用戶:', user)
}

// 清除用戶
const clearUser = () => {
  currentUser.value = null
  cognitoAuth.clearCurrentUser()
  console.log('清除用戶狀態')
}

// 安全的路由導航函數
const safeNavigateToLogin = (router) => {
  try {
    if (router && typeof router.push === 'function') {
      router.push('/login')
    } else {
      // 回退到使用 window.location
      window.location.href = '/login'
    }
  } catch (error) {
    console.error('路由導航失敗:', error)
    // 最終回退
    window.location.href = '/login'
  }
}

// 登出
const logout = async (router = null) => {
  const user = currentUser.value

  if (!user) {
    safeNavigateToLogin(router)
    return
  }

  try {
    if (user.loginType === 'cognito') {
      // Cognito 登出需要重定向到 Cognito 登出頁面
      await cognitoAuth.logout()
    } else if (user.loginType === 'amplify') {
      // Amplify 登出需要重定向到 Amplify 登出頁面
      await amplifyAuth.signOut()
    } else {
      // 本地登入，直接清除狀態並使用路由導航
      clearUser()
      safeNavigateToLogin(router)
    }
  } catch (error) {
    console.error('登出錯誤:', error)
    // 即使發生錯誤，也要清除本地狀態
    clearUser()
    safeNavigateToLogin(router)
  }
}

// 導出狀態和方法
export default {
  // 狀態
  currentUser: readonly(currentUser),
  isLoading: readonly(isLoading),

  // 計算屬性
  isLoggedIn,
  userDisplayName,

  // 方法
  initializeAuth,
  setUser,
  clearUser,
  logout,
}
