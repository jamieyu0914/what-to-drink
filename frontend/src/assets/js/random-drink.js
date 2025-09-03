import drinksMenu from './drinks-menu.js'

function randomDrink() {
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

  const randomDrinkChoice = drinks[Math.floor(Math.random() * drinks.length)]

  // Display the result
  document.getElementById('result').textContent = randomDrinkChoice

  // Show the result modal
  document.getElementById('resultModalOverlay').style.display = 'flex'
}

// Export the function
export default randomDrink
window.randomDrink = randomDrink
