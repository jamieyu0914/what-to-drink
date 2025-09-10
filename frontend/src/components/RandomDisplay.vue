<template>
  <div class="random-display">
    <h2>為您推薦...</h2>
    <div class="random-content">
      <div v-if="randomResult" class="result-display">
        <h3>{{ randomResult }}</h3>
      </div>
      <div v-else class="loading">
        正在為您選擇飲品...
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

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

// Call the function when component mounts
onMounted(() => {
  generateRandomDrink()
})
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

.result-display {
  border-radius: 8px;
}

.result-display h3 {
  color: #155724;
  margin: 0;
  font-size: 24px;
}

.loading {
  color: #666;
  font-style: italic;
  padding: 20px;
}
</style>
