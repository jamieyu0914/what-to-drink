<template>
  <div class="select-display">
    <h2>請選擇您想要的飲料</h2>
    <div id="selectBox" class="select-box">
      <div id="selectContent" class="select-content"></div>
    </div>
    <button @click="generateFromSelection" class="auto-select-btn">自動選擇</button>
    <div v-if="selectedResult" class="result-display">
      <h3>選擇結果：{{ selectedResult }}</h3>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

const selectedResult = ref('')

const populateSelectList = async () => {
  try {
    // 從後端 API 獲取飲料菜單
    const response = await fetch('http://localhost:8080/api/drinks/menu')
    if (!response.ok) {
      throw new Error('Failed to fetch drinks menu')
    }

    const drinksMenu = await response.json()

    const container = document.getElementById('selectContent')
    if (!container) return

    // Clear existing content
    container.innerHTML = ''

    const lists = [
      drinksMenu['fresh juices'],
      drinksMenu['smoothies'],
      drinksMenu['specials'],
      drinksMenu['mellow fresh milks'],
      drinksMenu['hot drinks'],
      drinksMenu['fine teas'],
      drinksMenu['milk teas'],
      drinksMenu['winter melons'],
      drinksMenu['yakult and polyphenols'],
    ]

    lists.forEach((list, listIndex) => {
      if (list) {
        list.forEach((label, index) => {
          // 建立 checkbox 元素
          const checkbox = document.createElement('input')
          checkbox.type = 'checkbox'
          checkbox.id = `option_${listIndex}_${index}`
          checkbox.name = 'interests'
          checkbox.value = label

          // 建立對應的 label 元素
          const labelElement = document.createElement('label')
          labelElement.htmlFor = checkbox.id
          labelElement.textContent = label
          labelElement.style.marginLeft = '8px'
          labelElement.style.color = '#fff'
          labelElement.style.fontSize = '20px'

          // 建立包裝容器
          const itemContainer = document.createElement('div')
          itemContainer.style.marginBottom = '8px'
          itemContainer.appendChild(checkbox)
          itemContainer.appendChild(labelElement)

          container.appendChild(itemContainer)
        })
      }
    })
  } catch (error) {
    console.error('Error loading drinks for selection:', error)
    alert('無法載入飲料菜單，請確認後端服務是否啟動')
  }
}

const generateFromSelection = async () => {
  const checkboxes = document.querySelectorAll('input[name="interests"]:checked')
  if (checkboxes.length === 0) {
    alert('請至少選擇一項飲料')
    return
  }

  const selectedDrinks = Array.from(checkboxes).map((checkbox) => checkbox.value)

  try {
    // 呼叫後端 API 來獲取隨機結果
    const response = await fetch('http://localhost:8080/api/drinks/auto-select', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(selectedDrinks),
    })

    if (!response.ok) {
      throw new Error('Failed to get auto-selected drink')
    }

    const result = await response.json()
    if (result.error) {
      alert(result.error)
      return
    }

    selectedResult.value = result.result
  } catch (error) {
    console.error('Error getting random drink:', error)
    alert('無法獲取隨機結果，請確認後端服務是否啟動')
  }
}

onMounted(() => {
  populateSelectList()
})
</script>

<style scoped>
.select-display {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.select-display h2 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.select-box {
  background: #fa8500;
  padding: 50px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-height: 400px;
  display: flex;
  align-items: center;
  flex-direction: column;
  width: max-content;
  border-radius: 8px;
  overflow: hidden;
}

.select-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  overflow-y: scroll;
  padding-right: 20px;
  box-sizing: content-box;
}

.auto-select-btn,
.auto-select-btn :visited {
  font-weight: 800;
  font-size: larger;
  margin: 10px;
  height: 40px;
  width: 20vh;
  color: #000080;
  background-color: #fff;
  border: 6px solid #f1e9e9;
  text-decoration: none;
  box-shadow: 4px 4px 20px 0 #076810;
  border-radius: 8px;
}

.auto-select-btn:hover,
.auto-select-btn:active {
  color: #fff;
  background-color: #000080;
  border: 8px solid #01015f;
  text-decoration: none;
  box-shadow: 4px 4px 5px 0 #076810;
  margin-top: 15px;
}

.result-display {
  margin-top: 20px;
  padding: 15px;
  background: #e8f5e8;
  border-radius: 8px;
  text-align: center;
}

.result-display h3 {
  color: #2c5530;
  margin: 0;
}
</style>
