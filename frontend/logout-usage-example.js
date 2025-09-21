// 示例：如何在 Vue 組件中正確使用修改後的登出功能

// 方法 1：在 setup() 函數中使用
import { useRouter } from 'vue-router'
import authStore from '../utils/auth.js'

export default {
  setup() {
    const router = useRouter()

    const handleLogout = async () => {
      try {
        // 傳入 router 以支援路由導航，避免頁面錯誤
        await authStore.logout(router)
        console.log('登出成功')
      } catch (error) {
        console.error('登出失敗:', error)
      }
    }

    return {
      handleLogout,
    }
  },
}

// 方法 2：在 script setup 中使用
// <script setup>
// import { useRouter } from 'vue-router'
// import authStore from '../utils/auth.js'
//
// const router = useRouter()
//
// const handleLogout = async () => {
//   try {
//     await authStore.logout(router)
//     console.log('登出成功')
//   } catch (error) {
//     console.error('登出失敗:', error)
//   }
// }
// </script>

// 方法 3：如果無法取得 router 實例（不推薦）
// const handleLogoutFallback = async () => {
//   try {
//     // 不傳入 router，將使用 window.location.href 作為回退
//     await authStore.logout()
//     console.log('登出成功')
//   } catch (error) {
//     console.error('登出失敗:', error)
//   }
// }
