// the Array of Drinks
const drinks = [
    "珍珠奶茶",
    "紅茶",
    "綠茶",
    "鮮奶茶",
    "黑磚奶茶",
    "抹茶拿鐵",
];

// Select a Drink
function selectDrink() {
    const randomDrink = drinks[Math.floor(Math.random() * drinks.length)];

    // Display the Result
    document.getElementById("result").textContent = "今天選擇的飲料是：" + randomDrink;
}