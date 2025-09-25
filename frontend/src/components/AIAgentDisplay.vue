<template>
  <div class="ai-agent-container">
    <!-- AI Agent Header -->
    <div class="ai-header">
      <div class="ai-avatar">
        <span class="ai-icon">🤖</span>
      </div>
      <div class="ai-info">
        <h2>AI 智能推薦助手</h2>
        <p class="ai-status" :class="{ thinking: isLoading }">
          {{ isLoading ? '正在思考中...' : '' }}
        </p>
      </div>
    </div>

    <!-- Chat Messages -->
    <div class="chat-messages" ref="messagesContainer">
      <div class="welcome-message" v-if="messages.length === 0">
        <div class="message ai-message">
          <div class="message-content">
            <h3>👋 歡迎使用 AI 智能推薦！</h3>
            <p>告訴我您的心情、想法，或是想喝什麼類型的飲品，我會為您推薦最適合的選擇。</p>
            <div class="example-prompts">
              <h4>💡 您可以這樣說：</h4>
              <div class="prompt-examples">
                <button @click="useExamplePrompt('我今天很累，想要提神')" class="example-btn">
                  😴 我今天很累，想要提神
                </button>
                <button @click="useExamplePrompt('想要喝點甜的，心情很好')" class="example-btn">
                  😊 想要喝點甜的，心情很好
                </button>
                <button @click="useExamplePrompt('天氣很熱，想要清爽的飲品')" class="example-btn">
                  🌞 天氣很熱，想要清爽的飲品
                </button>
                <button @click="useExamplePrompt('工作壓力大，需要放鬆')" class="example-btn">
                  😰 工作壓力大，需要放鬆
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div
        v-for="message in messages"
        :key="message.id"
        :class="['message', message.type + '-message']"
      >
        <div v-if="message.type === 'user'" class="message-content">
          <div class="user-input">{{ message.content }}</div>
        </div>

        <div v-else class="message-content">
          <div
            v-if="message.moodDetected && message.moodDetected !== '未檢測到心情'"
            class="mood-info"
          >
            <span class="mood-label">🔍 檢測心情：</span>
            <span class="mood-value">{{ message.moodDetected }}</span>
          </div>

          <div v-if="message.serviceType" class="service-info">
            <span class="service-label">🤖 推薦引擎：</span>
            <span
              class="service-value"
              :class="{ bedrock: message.isBedrockAvailable, local: !message.isBedrockAvailable }"
            >
              {{ message.serviceType }}
            </span>
          </div>

          <div
            v-if="message.aiResponse && message.aiResponse !== 'AI 正在為您分析...'"
            class="ai-response"
          >
            {{ message.aiReason }}
          </div>

          <!-- 當沒有有效的 AI 回應時顯示默認信息 -->
          <div v-else-if="!message.error" class="ai-response">
            <div class="analyzing-info">
              <span class="analyzing-icon">🔍</span>
              <span>正在分析您的需求，請稍候...</span>
            </div>
          </div>

          <div
            v-if="message.recommendations && message.recommendations.length > 0"
            class="recommendations"
          >
            <h4>🍹 為您推薦：</h4>
            <div class="recommendation-list">
              <div
                v-for="rec in message.recommendations"
                :key="rec.name"
                class="recommendation-item"
              >
                <div class="rec-header">
                  <span class="rec-name">{{ rec.name }}</span>
                  <span class="rec-category">{{ rec.category }}</span>
                  <span v-if="rec.price" class="rec-price">${{ rec.price }}</span>
                </div>
                <div class="rec-description">{{ rec.description }}</div>
                <div class="rec-reason">{{ rec.reason }}</div>
                <div class="rec-actions">
                  <button @click="addToFavorites(rec)" class="fav-btn" :disabled="isAddingFavorite">
                    <span v-if="isAddingFavorite">⏳</span>
                    <span v-else>⭐ 收藏</span>
                  </button>
                  <span class="match-score">匹配度: {{ Math.round(rec.matchScore * 100) }}%</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 當沒有推薦結果時顯示信息 -->
          <div v-else-if="!message.error && message.serviceType" class="no-recommendations">
            <div class="no-rec-info">
              <span class="no-rec-icon">💭</span>
              <p>目前沒有找到合適的推薦，請嘗試描述更具體的需求或心情。</p>
              <div class="suggestion-tips">
                <strong>💡 建議：</strong>
                <ul>
                  <li>描述您的心情狀態（如：開心、疲憊、放鬆）</li>
                  <li>提及偏好的口味（如：甜的、酸的、清爽的）</li>
                  <li>說明場合或時間（如：上班時、運動後、睡前）</li>
                </ul>
              </div>
            </div>
          </div>

          <div v-if="message.error" class="error-message">❌ {{ message.error }}</div>
        </div>

        <div class="message-time">
          {{ formatTime(message.timestamp) }}
        </div>
      </div>

      <div v-if="isLoading" class="loading-message">
        <div class="message ai-message">
          <div class="message-content">
            <div class="loading-indicator">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
            <p>正在分析您的需求並生成推薦...</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Input Area -->
    <div class="chat-input">
      <div class="input-container">
        <textarea
          v-model="userInput"
          @keyup.enter.exact="sendMessage"
          @keyup.shift.enter="addNewLine"
          placeholder="告訴我您想要什麼樣的飲品，或是您現在的心情..."
          :disabled="isLoading"
          maxlength="1000"
          rows="2"
        ></textarea>
        <button @click="sendMessage" :disabled="!userInput.trim() || isLoading" class="send-btn">
          <span v-if="isLoading">⏳</span>
          <span v-else>🚀 推薦</span>
        </button>
      </div>
      <div class="input-footer">
        <span class="char-count">{{ userInput.length }}/1000</span>
        <span class="tip">💡 Shift+Enter 換行，Enter 發送</span>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="quick-actions" v-if="!isLoading">
      <button @click="clearChat" class="action-btn">🗑️ 清空對話</button>
      <button @click="showHistory" class="action-btn">📜 查看歷史</button>
      <button @click="showFavorites" class="action-btn">⭐ 我的收藏</button>
    </div>

    <!-- 歷史記錄模態窗口 -->
    <div v-if="showHistoryModal" class="modal-overlay" @click="closeHistoryModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>📜 推薦歷史記錄</h3>
          <button @click="closeHistoryModal" class="close-btn">✖</button>
        </div>
        <div class="modal-body">
          <div v-if="isLoadingHistory" class="loading-state">
            <div class="loading-spinner">⏳</div>
            <p>載入歷史記錄中...</p>
          </div>
          <div v-else-if="historyError" class="error-state">
            <p>❌ {{ historyError }}</p>
            <button @click="showHistory" class="retry-btn">重試</button>
          </div>
          <div v-else-if="historyList.length === 0" class="empty-state">
            <p>📭 還沒有任何推薦歷史記錄</p>
            <p class="tip">開始與 AI 對話來獲得推薦吧！</p>
          </div>
          <div v-else class="history-list">
            <div v-for="item in historyList" :key="item.id" class="history-item">
              <div class="history-header">
                <span class="history-time">{{ formatDate(item.createdAt) }}</span>
                <span v-if="item.userRating" class="history-rating">
                  {{ '⭐'.repeat(item.userRating) }} ({{ item.userRating }}/5)
                </span>
              </div>
              <div class="history-content">
                <div class="user-query">
                  <strong>🤔 您的需求：</strong>
                  {{ item.userQuery }}
                </div>
                <div
                  v-if="item.moodDetected && item.moodDetected !== '未檢測到心情'"
                  class="mood-detected"
                >
                  <strong>😊 檢測心情：</strong>
                  {{ item.moodDetected }}
                </div>
                <div v-if="item.aiResponse" class="ai-response">
                  <strong>🤖 AI 分析：</strong>
                  {{ item.aiResponse }}
                </div>
                <div
                  v-if="item.recommendations && item.recommendations.length > 0"
                  class="recommendations"
                >
                  <strong>🍹 推薦結果：</strong>
                  <div class="rec-list">
                    <div v-for="rec in item.recommendations" :key="rec.name" class="rec-item">
                      {{ rec.name }} ({{ rec.category }}) - 匹配度:
                      {{ Math.round(rec.matchScore * 100) }}%
                    </div>
                  </div>
                </div>
              </div>
              <div class="history-actions">
                <div v-if="!item.userRating" class="rating-section">
                  <span>評分這次推薦：</span>
                  <div class="rating-buttons">
                    <button
                      v-for="star in 5"
                      :key="star"
                      @click="rateRecommendation(item.id, star)"
                      class="star-btn"
                    >
                      ⭐
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 收藏列表模態窗口 -->
    <div v-if="showFavoritesModal" class="modal-overlay" @click="closeFavoritesModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>⭐ 我的收藏</h3>
          <button @click="closeFavoritesModal" class="close-btn">✖</button>
        </div>
        <div class="modal-body">
          <div v-if="isLoadingFavorites" class="loading-state">
            <div class="loading-spinner">⏳</div>
            <p>載入收藏列表中...</p>
          </div>
          <div v-else-if="favoritesError" class="error-state">
            <p>❌ {{ favoritesError }}</p>
            <button @click="showFavorites" class="retry-btn">重試</button>
          </div>
          <div v-else-if="favoritesList.length === 0" class="empty-state">
            <p>⭐ 還沒有收藏任何飲品</p>
            <p class="tip">在推薦結果中點擊收藏按鈕來添加喜歡的飲品！</p>
          </div>
          <div v-else class="favorites-list">
            <div v-for="item in favoritesList" :key="item.id" class="favorite-item">
              <div class="favorite-header">
                <div class="drink-info">
                  <h4>{{ item.drinkName }}</h4>
                  <span class="category">{{ item.category }}</span>
                  <span v-if="item.price" class="price">${{ item.price }}</span>
                </div>
                <button @click="removeFavorite(item.drinkName)" class="remove-btn">🗑️</button>
              </div>
              <div v-if="item.description" class="favorite-description">
                {{ item.description }}
              </div>
              <div v-if="item.reason" class="favorite-reason">
                <strong>推薦理由：</strong>{{ item.reason }}
              </div>
              <div v-if="item.notes" class="favorite-notes">
                <strong>我的備註：</strong>{{ item.notes }}
              </div>
              <div class="favorite-footer">
                <span class="added-time">收藏於 {{ formatDate(item.createdAt) }}</span>
                <span v-if="item.matchScore" class="match-score">
                  匹配度: {{ Math.round(item.matchScore * 100) }}%
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue'

// 響應式數據
const messages = ref([])
const userInput = ref('')
const isLoading = ref(false)
const isAddingFavorite = ref(false)
const messagesContainer = ref(null)
const serviceStatus = ref(null) // 新增：服務狀態

// 歷史記錄和收藏相關狀態
const showHistoryModal = ref(false)
const showFavoritesModal = ref(false)
const historyList = ref([])
const favoritesList = ref([])
const isLoadingHistory = ref(false)
const isLoadingFavorites = ref(false)
const historyError = ref('')
const favoritesError = ref('')

// 用戶信息
const username = ref('TestUser') // 這裡應該從 Cognito 或其他認證系統獲取

let messageId = 0

// API 基礎 URL
const API_BASE_URL = 'http://localhost:8080/api'

// 生命週期
onMounted(() => {
  // 載入服務狀態
  fetchServiceStatus()
})

// 獲取服務狀態
const fetchServiceStatus = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/recommendations/service-status`)
    if (response.ok) {
      const data = await response.json()
      serviceStatus.value = data
      console.log('服務狀態:', data)
    }
  } catch (error) {
    console.error('獲取服務狀態失敗:', error)
  }
}

// 方法
const useExamplePrompt = (prompt) => {
  userInput.value = prompt
  sendMessage()
}

const addNewLine = () => {
  userInput.value += '\n'
}

const sendMessage = async () => {
  if (!userInput.value.trim() || isLoading.value) return

  const messageContent = userInput.value.trim()
  userInput.value = ''

  // 添加用戶消息
  messages.value.push({
    id: messageId++,
    type: 'user',
    content: messageContent,
    timestamp: new Date(),
  })

  await scrollToBottom()

  // 發送到 AI 服務
  isLoading.value = true

  try {
    const response = await fetch(`${API_BASE_URL}/recommendations/generate`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        userInput: messageContent,
        username: username.value,
      }),
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const data = await response.json()

    // 增加詳細的調試信息
    console.log('完整 API 響應:', data)
    console.log('響應狀態:', data.status)
    console.log('服務類型:', data.serviceType)
    console.log('Bedrock 可用性:', data.isBedrockAvailable)

    if (data.status === 'error') {
      // 錯誤響應
      messages.value.push({
        id: messageId++,
        type: 'ai',
        error: data.message || '發生未知錯誤',
        timestamp: new Date(),
      })
    } else if (data.status === 'success') {
      // 成功響應 - 檢查必要欄位
      const moodDetected = data.moodDetected || data.mood || '未檢測到心情'
      const aiResponse = data.aiResponse || data.response || data.message || 'AI 正在為您分析...'
      const aiReason = data.aiReason || 'AI 為您精心挑選的飲品推薦'
      const recommendations = Array.isArray(data.recommendations) ? data.recommendations : []

      // 如果沒有推薦結果，添加提示信息
      if (recommendations.length === 0) {
        console.warn('沒有收到推薦結果，可能是後端處理問題')
      }

      messages.value.push({
        id: messageId++,
        type: 'ai',
        moodDetected: moodDetected,
        aiResponse: aiResponse,
        aiReason: aiReason,
        recommendations: recommendations,
        serviceType: data.serviceType || '未知服務',
        isBedrockAvailable: data.isBedrockAvailable || false,
        timestamp: new Date(),
      })

      console.log('處理後的 AI 回應數據:', {
        moodDetected: moodDetected,
        aiResponse: aiResponse,
        aiReason: aiReason,
        recommendations: recommendations,
        serviceType: data.serviceType,
        isBedrockAvailable: data.isBedrockAvailable,
      })
    } else {
      // 未知響應格式
      console.error('未知的響應格式:', data)
      messages.value.push({
        id: messageId++,
        type: 'ai',
        error: '服務響應格式異常，請聯繫技術支援',
        timestamp: new Date(),
      })
    }
  } catch (error) {
    console.error('API 請求失敗:', error)
    messages.value.push({
      id: messageId++,
      type: 'ai',
      error: '網路連線錯誤，請稍後再試',
      timestamp: new Date(),
    })
  } finally {
    isLoading.value = false
    await scrollToBottom()
  }
}

const addToFavorites = async (recommendation) => {
  isAddingFavorite.value = true

  try {
    const response = await fetch(`${API_BASE_URL}/favorites/add?username=${username.value}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        drinkName: recommendation.name,
        drinkCategory: recommendation.category,
        drinkDescription: recommendation.description,
        price: recommendation.price,
        notes: `AI推薦: ${recommendation.reason}`,
      }),
    })

    if (response.ok) {
      alert('✅ 已加入收藏！')
    } else {
      const error = await response.json()
      alert('❌ ' + (error.error || '加入收藏失敗'))
    }
  } catch (error) {
    console.error('加入收藏失敗:', error)
    alert('❌ 網路錯誤，請稍後再試')
  } finally {
    isAddingFavorite.value = false
  }
}

const clearChat = () => {
  if (confirm('確定要清空對話紀錄嗎？')) {
    messages.value = []
  }
}

const showHistory = async () => {
  showHistoryModal.value = true
  isLoadingHistory.value = true
  historyError.value = ''

  try {
    const response = await fetch(`${API_BASE_URL}/history/list?username=${username.value}&limit=50`)
    if (response.ok) {
      const data = await response.json()
      historyList.value = data
    } else {
      throw new Error('獲取歷史記錄失敗')
    }
  } catch (error) {
    console.error('獲取歷史記錄失敗:', error)
    historyError.value = '獲取歷史記錄失敗，請稍後再試'
  } finally {
    isLoadingHistory.value = false
  }
}

const showFavorites = async () => {
  showFavoritesModal.value = true
  isLoadingFavorites.value = true
  favoritesError.value = ''

  try {
    const response = await fetch(`${API_BASE_URL}/favorites/list?username=${username.value}`)
    if (response.ok) {
      const data = await response.json()
      favoritesList.value = data
    } else {
      throw new Error('獲取收藏列表失敗')
    }
  } catch (error) {
    console.error('獲取收藏列表失敗:', error)
    favoritesError.value = '獲取收藏列表失敗，請稍後再試'
  } finally {
    isLoadingFavorites.value = false
  }
}

// 關閉模態窗口
const closeHistoryModal = () => {
  showHistoryModal.value = false
  historyList.value = []
  historyError.value = ''
}

const closeFavoritesModal = () => {
  showFavoritesModal.value = false
  favoritesList.value = []
  favoritesError.value = ''
}

// 移除收藏
const removeFavorite = async (drinkName) => {
  try {
    const response = await fetch(
      `${API_BASE_URL}/favorites/remove?username=${username.value}&drinkName=${encodeURIComponent(drinkName)}`,
      {
        method: 'DELETE',
      },
    )

    if (response.ok) {
      // 從列表中移除
      favoritesList.value = favoritesList.value.filter((fav) => fav.drinkName !== drinkName)
      alert('✅ 已移除收藏！')
    } else {
      const error = await response.json()
      alert('❌ ' + (error.error || '移除收藏失敗'))
    }
  } catch (error) {
    console.error('移除收藏失敗:', error)
    alert('❌ 移除收藏失敗')
  }
}

// 為歷史記錄評分
const rateRecommendation = async (historyId, rating) => {
  try {
    const response = await fetch(
      `${API_BASE_URL}/history/rate?historyId=${historyId}&rating=${rating}`,
      {
        method: 'POST',
      },
    )

    if (response.ok) {
      // 更新本地列表中的評分
      const historyItem = historyList.value.find((item) => item.id === historyId)
      if (historyItem) {
        historyItem.userRating = rating
      }
      alert('✅ 評分成功！')
    } else {
      const error = await response.json()
      alert('❌ ' + (error.error || '評分失敗'))
    }
  } catch (error) {
    console.error('評分失敗:', error)
    alert('❌ 評分失敗')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const formatTime = (date) => {
  return date.toLocaleTimeString('zh-TW', {
    hour: '2-digit',
    minute: '2-digit',
  })
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.ai-agent-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-height: 800px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.ai-header {
  background: linear-gradient(135deg, #fa8500 0%, #e07600 100%);
  color: white;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.ai-avatar {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.ai-icon {
  font-size: 24px;
}

.ai-info h2 {
  margin: 0 0 5px 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.ai-status {
  opacity: 0.9;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.ai-status.thinking {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    opacity: 0.9;
  }
  50% {
    opacity: 0.6;
  }
  100% {
    opacity: 0.9;
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #ffffff;
}

.welcome-message .message {
  border: none;
  color: #2c3e50;
}

.welcome-message h3 {
  margin: 0 0 10px 0;
  font-size: 1.3rem;
}

.welcome-message p {
  margin: 0 0 20px 0;
  line-height: 1.6;
}

.example-prompts h4 {
  margin: 0 0 15px 0;
  color: #34495e;
  font-size: 1.1rem;
}

.prompt-examples {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.example-btn {
  background: rgba(250, 133, 0, 0.1);
  border: 1px solid rgba(250, 133, 0, 0.3);
  border-radius: 20px;
  padding: 10px 15px;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  color: #d68910;
}

.example-btn:hover {
  background: rgba(250, 133, 0, 0.2);
  border-color: #fa8500;
  box-shadow: 0 4px 15px rgba(250, 133, 0, 0.2);
  transform: translateY(-2px);
}

.message {
  margin-bottom: 20px;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  display: flex;
  justify-content: flex-end;
}

.user-message .message-content {
  background: linear-gradient(135deg, #fa8500 0%, #e07600 100%);
  color: white;
  border-radius: 20px 20px 5px 20px;
  padding: 15px 20px;
  max-width: 70%;
  box-shadow: 0 4px 15px rgba(250, 133, 0, 0.3);
}

.ai-message {
  display: flex;
  justify-content: flex-start;
}

.ai-message .message-content {
  background: #fdfdfd;
  border: 1px solid #e9ecef;
  border-radius: 20px 20px 20px 5px;
  padding: 20px;
  max-width: 85%;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.mood-info {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  padding: 10px 15px;
  border-radius: 10px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid rgba(250, 133, 0, 0.2);
}

.mood-label {
  font-weight: 600;
  color: #d68910;
}

.mood-value {
  color: #e67e22;
  font-weight: 500;
}

/* 服務狀態信息樣式 */
.service-info {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  padding: 8px 12px;
  border-radius: 8px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  border: 1px solid rgba(250, 133, 0, 0.2);
}

.service-label {
  font-weight: 600;
  color: #d68910;
}

.service-value {
  color: #ffffff;
  border: 1px solid rgb(255 255 255);
  font-size: 0.8rem;
  padding: 3px 8px;
  border-radius: 20px;
  font-weight: 500;
}

.service-value.bedrock {
  background: #ef7e03;
  color: white;
}

.service-value.local {
  background: #ef7e03;
  color: white;
}

.ai-response {
  line-height: 1.7;
  color: #2c3e50;
  margin-bottom: 20px;
  white-space: pre-wrap;
}

.analyzing-info {
  display: flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
  padding: 15px;
  border-radius: 10px;
  color: #d68910;
  font-style: italic;
}

.analyzing-icon {
  font-size: 1.2rem;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.no-recommendations {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
  border: 1px solid #f6c23e;
  border-radius: 12px;
  padding: 20px;
  color: #856404;
}

.no-rec-info {
  text-align: center;
}

.no-rec-icon {
  font-size: 2rem;
  display: block;
  margin-bottom: 10px;
}

.no-rec-info p {
  margin: 0 0 15px 0;
  font-size: 1rem;
  line-height: 1.5;
}

.suggestion-tips {
  text-align: left;
  background: rgba(255, 255, 255, 0.3);
  padding: 15px;
  border-radius: 8px;
  margin-top: 10px;
}

.suggestion-tips strong {
  color: #d68910;
  display: block;
  margin-bottom: 8px;
}

.suggestion-tips ul {
  margin: 0;
  padding-left: 20px;
}

.suggestion-tips li {
  margin-bottom: 5px;
  line-height: 1.4;
}

.recommendations h4 {
  color: #fa8500;
  margin: 0 0 15px 0;
  font-size: 2.2rem;
}

.recommendation-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.recommendation-item {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
  border-radius: 10px;
  padding: 10px 15px;
  color: #2c3e50;
  margin-bottom: 15px;
  border: 1px solid rgba(250, 133, 0, 0.2);
  transition: all 0.3s ease;
}

.recommendation-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(250, 133, 0, 0.2);
}

.rec-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.rec-name {
  font-weight: 600;
  font-size: 1.1rem;
}

.rec-category {
  background: rgba(255, 255, 255, 0.9);
  color: #fa8500;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.rec-price {
  font-weight: 600;
  color: #d68910;
}

.rec-description {
  margin-bottom: 8px;
  opacity: 0.9;
  line-height: 1.5;
}

.rec-reason {
  margin-bottom: 15px;
  opacity: 0.8;
  font-style: italic;
}

.rec-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.fav-btn {
  background: rgba(255, 255, 255, 0.95);
  color: #fa8500;
  border: none;
  padding: 8px 15px;
  border-radius: 20px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.fav-btn:hover:not(:disabled) {
  background: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.fav-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.match-score {
  font-weight: 600;
  opacity: 0.95;
  color: #d68910;
}

.error-message {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: #c0392b;
  padding: 15px;
  border-radius: 10px;
  font-weight: 500;
}

.message-time {
  font-size: 0.8rem;
  color: #7f8c8d;
  text-align: right;
  margin-top: 5px;
}

.loading-message {
  display: flex;
  justify-content: flex-start;
}

.loading-indicator {
  display: flex;
  gap: 5px;
  margin-bottom: 10px;
}

.dot {
  width: 8px;
  height: 8px;
  background: #fa8500;
  border-radius: 50%;
  animation: bounce 1.4s ease-in-out infinite both;
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}
.dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.chat-input {
  background: white;
  border-top: 1px solid #e9ecef;
  padding: 20px;
}

.input-container {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.input-container textarea {
  flex: 1;
  resize: none;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 15px;
  font-family: inherit;
  font-size: 1rem;
  transition: border-color 0.3s ease;
  min-height: 60px;
}

.input-container textarea:focus {
  outline: none;
  border-color: #fa8500;
}

.input-container textarea:disabled {
  background: #f8f9fa;
  opacity: 0.7;
}

.send-btn {
  background: linear-gradient(135deg, #fa8500 0%, #e07600 100%);
  color: white;
  border: none;
  padding: 15px 25px;
  border-radius: 12px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(250, 133, 0, 0.4);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  font-size: 0.8rem;
  color: #7f8c8d;
}

.quick-actions {
  background: #f8f9fa;
  padding: 15px 20px;
  border-top: 1px solid #e9ecef;
  display: flex;
  gap: 10px;
  justify-content: center;
}

.action-btn {
  background: white;
  border: 1px solid #dee2e6;
  color: #495057;
  padding: 8px 15px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

/* 模態窗口樣式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 800px;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
}

.modal-header {
  background: linear-gradient(135deg, #fa8500 0%, #e07600 100%);
  color: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 600;
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

/* 載入和錯誤狀態 */
.loading-state,
.error-state,
.empty-state {
  text-align: center;
  padding: 40px 20px;
}

.loading-spinner {
  font-size: 2rem;
  animation: spin 2s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.error-state p {
  color: #e74c3c;
  margin-bottom: 15px;
}

.retry-btn {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
}

.empty-state {
  color: #7f8c8d;
}

.empty-state .tip {
  font-size: 0.9rem;
  margin-top: 10px;
}

/* 歷史記錄樣式 */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.history-item {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 20px;
  transition: transform 0.2s ease;
}

.history-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #dee2e6;
}

.history-time {
  font-size: 0.9rem;
  color: #6c757d;
  font-weight: 500;
}

.history-rating {
  font-size: 0.9rem;
  color: #f39c12;
}

.history-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-query,
.mood-detected,
.ai-response {
  font-size: 0.95rem;
  line-height: 1.5;
}

.user-query strong,
.mood-detected strong,
.ai-response strong {
  color: #495057;
}

.recommendations {
  margin-top: 10px;
}

.rec-list {
  background: white;
  border-radius: 6px;
  padding: 15px;
  margin-top: 8px;
}

.rec-item {
  padding: 8px 0;
  border-bottom: 1px solid #e9ecef;
  font-size: 0.9rem;
  color: #495057;
}

.rec-item:last-child {
  border-bottom: none;
}

.history-actions {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #dee2e6;
}

.rating-section {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 0.9rem;
  color: #6c757d;
}

.rating-buttons {
  display: flex;
  gap: 5px;
}

.star-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: background 0.2s ease;
}

.star-btn:hover {
  background: rgba(243, 156, 18, 0.1);
}

/* 收藏列表樣式 */
.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.favorite-item {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 20px;
  transition: transform 0.2s ease;
}

.favorite-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.favorite-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.drink-info h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 1.2rem;
}

.category {
  background: #fa8500;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  margin-right: 10px;
}

.price {
  background: #00b894;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.remove-btn {
  background: #e74c3c;
  border: none;
  color: white;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s ease;
}

.remove-btn:hover {
  background: #c0392b;
}

.favorite-description,
.favorite-reason,
.favorite-notes {
  font-size: 0.95rem;
  line-height: 1.5;
  margin-bottom: 10px;
  color: #495057;
}

.favorite-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #dee2e6;
  font-size: 0.85rem;
  color: #6c757d;
}

.match-score {
  background: #fa8500;
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .ai-agent-container {
    height: 100vh;
    border-radius: 0;
  }

  .ai-header {
    padding: 15px;
  }

  .ai-info h2 {
    font-size: 1.3rem;
  }

  .chat-messages {
    padding: 15px;
  }

  .user-message .message-content,
  .ai-message .message-content {
    max-width: 90%;
  }

  .recommendation-item {
    padding: 15px;
  }

  .rec-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .rec-actions {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .input-container {
    flex-direction: column;
  }

  .send-btn {
    align-self: flex-end;
  }

  .quick-actions {
    flex-wrap: wrap;
  }

  /* 模態窗口響應式 */
  .modal-overlay {
    padding: 10px;
  }

  .modal-content {
    max-height: 90vh;
  }

  .modal-header {
    padding: 15px;
  }

  .modal-header h3 {
    font-size: 1.2rem;
  }

  .modal-body {
    padding: 15px;
  }

  .history-item,
  .favorite-item {
    padding: 15px;
  }

  .history-header,
  .favorite-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .favorite-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .rating-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
