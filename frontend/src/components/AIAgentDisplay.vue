<template>
  <div class="chat-room">
    <div class="chat-header">
      <h2>聊天室</h2>
      <div class="online-users">
        <span class="online-indicator"></span>
        線上用戶: {{ onlineUsers }}
      </div>
    </div>

    <div class="chat-messages" ref="messagesContainer">
      <div
        v-for="message in messages"
        :key="message.id"
        :class="['message', { 'own-message': message.isOwn }]"
      >
        <div class="message-header">
          <span class="username">{{ message.username }}</span>
          <span class="timestamp">{{ formatTime(message.timestamp) }}</span>
        </div>
        <div class="message-content">
          {{ message.content }}
        </div>
      </div>
      <div v-if="messages.length === 0" class="no-messages">
        還沒有訊息，開始聊天吧！
      </div>
    </div>

    <div class="chat-input">
      <div class="username-input" v-if="!username">
        <input
          v-model="tempUsername"
          @keyup.enter="setUsername"
          placeholder="請輸入您的暱稱..."
          maxlength="20"
        />
        <button @click="setUsername" :disabled="!tempUsername.trim()">
          加入聊天
        </button>
      </div>
      <div class="message-input" v-else>
        <input
          v-model="newMessage"
          @keyup.enter="sendMessage"
          placeholder="輸入訊息..."
          maxlength="500"
        />
        <button @click="sendMessage" :disabled="!newMessage.trim()">
          發送
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'

const messages = ref([])
const newMessage = ref('')
const username = ref('')
const tempUsername = ref('')
const onlineUsers = ref(1)
const messagesContainer = ref(null)

// 模擬的示例訊息
const sampleMessages = [
  {
    id: 1,
    username: '珍珠控',
    content: '大家好！今天想喝什麼呢？',
    timestamp: new Date(Date.now() - 1000 * 60 * 5),
    isOwn: false
  },
  {
    id: 2,
    username: '奶茶迷',
    content: '我推薦珍珠奶茶！',
    timestamp: new Date(Date.now() - 1000 * 60 * 3),
    isOwn: false
  },
  {
    id: 3,
    username: '茶葉專家',
    content: '今天天氣熱，來杯冰綠茶如何？',
    timestamp: new Date(Date.now() - 1000 * 60 * 1),
    isOwn: false
  }
]

let messageIdCounter = 4

const setUsername = () => {
  if (tempUsername.value.trim()) {
    username.value = tempUsername.value.trim()
    // 模擬歡迎訊息
    addSystemMessage(`${username.value} 加入了聊天室`)
    onlineUsers.value++
  }
}

const sendMessage = async () => {
  if (!newMessage.value.trim() || !username.value) return

  const message = {
    id: messageIdCounter++,
    username: username.value,
    content: newMessage.value.trim(),
    timestamp: new Date(),
    isOwn: true
  }

  messages.value.push(message)
  newMessage.value = ''

  await nextTick()
  scrollToBottom()

  // 模擬其他用戶的回應（50% 機率）
  if (Math.random() > 0.5) {
    setTimeout(() => {
      simulateResponse()
    }, 1000 + Math.random() * 2000)
  }
}

const addSystemMessage = (content) => {
  const message = {
    id: messageIdCounter++,
    username: '系統',
    content: content,
    timestamp: new Date(),
    isOwn: false,
    isSystem: true
  }
  messages.value.push(message)
  nextTick(() => scrollToBottom())
}

const simulateResponse = () => {
  const responses = [
    '好建議！',
    '我也想試試看這個',
    '聽起來不錯呢',
    '有推薦的店家嗎？',
    '這個口味我喜歡',
    '謝謝分享！',
    '我平常都喝這個',
    '換個口味試試看'
  ]

  const usernames = ['咖啡愛好者', '果汁達人', '飲料新手', '冰沙狂人', '紅茶之星', '綠豆沙王者']

  const message = {
    id: messageIdCounter++,
    username: usernames[Math.floor(Math.random() * usernames.length)],
    content: responses[Math.floor(Math.random() * responses.length)],
    timestamp: new Date(),
    isOwn: false
  }

  messages.value.push(message)
  nextTick(() => scrollToBottom())
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleTimeString('zh-TW', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  // 載入示例訊息
  messages.value = [...sampleMessages]
  nextTick(() => scrollToBottom())

  // 模擬線上用戶數變化
  const interval = setInterval(() => {
    onlineUsers.value = Math.max(1, onlineUsers.value + (Math.random() > 0.5 ? 1 : -1))
  }, 10000)

  onBeforeUnmount(() => {
    clearInterval(interval)
  })
})
</script>

<style scoped>
.chat-room {
  max-width: 800px;
  margin: 0 auto;
  height: 600px;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chat-header {
  background: #fa8500;
  color: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h2 {
  margin: 0;
  font-size: 24px;
}

.online-users {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.online-indicator {
  width: 8px;
  height: 8px;
  background: #60c41d;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8fafc;
}

.message {
  margin-bottom: 16px;
  max-width: 70%;
}

.message.own-message {
  margin-left: auto;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  font-size: 12px;
  color: #64748b;
}

.username {
  font-weight: 600;
  color: #475569;
}

.timestamp {
  color: #94a3b8;
}

.message-content {
  background: white;
  padding: 12px 16px;
  border-radius: 18px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  word-wrap: break-word;
  color: #475569;
}

.own-message .message-content {
  background: #ffffff;
  color: #475569;
  border: 2px solid #f7dba2;
}

.no-messages {
  text-align: center;
  color: #94a3b8;
  padding: 40px;
  font-style: italic;
}

.chat-input {
  border-top: 1px solid #e2e8f0;
  padding: 20px;
  background: white;
}

.username-input, .message-input {
  display: flex;
  gap: 12px;
}

.username-input input, .message-input input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 25px;
  outline: none;
  font-size: 14px;
  transition: border-color 0.2s;
}

.username-input input:focus, .message-input input:focus {
  border-color: #ecab29;
}

.username-input button, .message-input button {
  padding: 12px 24px;
  background: #fa8500;
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.2s, box-shadow 0.2s;
}

.username-input button:hover, .message-input button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(234, 172, 102, 0.4);
}

.username-input button:disabled, .message-input button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 自定義滾動條 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .chat-room {
    height: calc(100vh - 40px);
    margin: 20px;
    border-radius: 8px;
  }

  .chat-header {
    padding: 16px;
  }

  .chat-header h2 {
    font-size: 20px;
  }

  .message {
    max-width: 85%;
  }

  .chat-input {
    padding: 16px;
  }
}
</style>
