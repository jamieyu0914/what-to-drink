<template>
  <div class="random-display">
    <h2>隨機飲料生成器</h2>
    <div class="random-content">
      <button @click="generateRandomDrink" class="btn btn-primary">生成隨機飲料</button>
      <div v-if="randomResult" class="result-display">
        <h3>為您推薦：{{ randomResult }}</h3>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const randomResult = ref('')

const generateRandomDrink = async () => {
  try {
    const drinksMenuModule = await import('../assets/js/drinks-menu.js')
    const drinksMenu = drinksMenuModule.default

    // Get all drinks
    const drinks = [
      ...drinksMenu['fresh juices'],
      ...drinksMenu['smoothies'],
      ...drinksMenu['specials'],
      ...drinksMenu['mellow fresh milks'],
      ...drinksMenu['hot drinks'],
      ...drinksMenu['fine teas'],
      ...drinksMenu['milk teas'],
      ...drinksMenu['winter melons'],
      ...drinksMenu['yakult and polyphenols'],
    ]

    const randomChoice = drinks[Math.floor(Math.random() * drinks.length)]
    randomResult.value = randomChoice
  } catch (error) {
    console.error('Error generating random drink:', error)
  }
}
</script>

<style scoped>
.random-display {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
}

.random-display h2 {
  color: #333;
  margin-bottom: 30px;
}

.random-content {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.btn {
  padding: 15px 30px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.3s;
}

.btn:hover {
  background: #218838;
}

.result-display {
  margin-top: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #28a745;
}

.result-display h3 {
  color: #155724;
  margin: 0;
  font-size: 24px;
}
</style>
