<template>
  <div class="select-display">
    <h2>請選擇您想要的飲料</h2>
    <div id="selectContent" class="select-content"></div>
    <button @click="generateFromSelection" class="btn btn-primary" style="margin-top: 20px">
      從選擇中隨機生成
    </button>
    <div v-if="selectedResult" class="result-display">
      <h3>隨機結果：{{ selectedResult }}</h3>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

const selectedResult = ref('')

const populateSelectList = async () => {
  try {
    const drinksMenuModule = await import('../assets/js/drinks-menu.js')
    const drinksMenu = drinksMenuModule.default

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

        // 建立包裝容器
        const itemContainer = document.createElement('div')
        itemContainer.style.marginBottom = '8px'
        itemContainer.appendChild(checkbox)
        itemContainer.appendChild(labelElement)

        container.appendChild(itemContainer)
      })
    })
  } catch (error) {
    console.error('Error loading drinks for selection:', error)
  }
}

const generateFromSelection = () => {
  const checkboxes = document.querySelectorAll('input[name="interests"]:checked')
  if (checkboxes.length === 0) {
    alert('請至少選擇一項飲料')
    return
  }

  const selectedDrinks = Array.from(checkboxes).map((checkbox) => checkbox.value)
  const randomChoice = selectedDrinks[Math.floor(Math.random() * selectedDrinks.length)]
  selectedResult.value = randomChoice
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
}

.select-display h2 {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.select-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-height: 400px;
  overflow-y: auto;
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

.btn {
  display: block;
  margin: 0 auto;
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn:hover {
  background: #0056b3;
}
</style>
