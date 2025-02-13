import drinksMenu from './drinks-menu.js';

function selectDrink() {
    // Get all drinks
    const drinks = [
        ...drinksMenu["fresh juices"],
        ...drinksMenu["smoothies"],
        ...drinksMenu["specials"],
        ...drinksMenu["mellow fresh milks"],
        ...drinksMenu["hot drinks"],
        ...drinksMenu["fine teas"],
        ...drinksMenu["milk teas"],
        ...drinksMenu["winter melons"],
        ...drinksMenu["yakult and polyphenols"],
    ];

    const randomDrink = drinks[Math.floor(Math.random() * drinks.length)];

    // Display the result
    document.getElementById("result").textContent = randomDrink;
}

// Export the function
window.selectDrink = selectDrink;

// Add event listener to the select button
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("selectButton").addEventListener("click", selectDrink);
});