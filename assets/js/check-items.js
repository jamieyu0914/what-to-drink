import drinksMenu from "./drinks-menu.js";

function checkItems() {
  // const drinks = [
  //   ...drinksMenu["fresh juices"],
  //   ...drinksMenu["smoothies"],
  //   ...drinksMenu["specials"],
  //   ...drinksMenu["mellow fresh milks"],
  //   ...drinksMenu["hot drinks"],
  //   ...drinksMenu["fine teas"],
  //   ...drinksMenu["milk teas"],
  //   ...drinksMenu["winter melons"],
  //   ...drinksMenu["yakult and polyphenols"],
  // ];
  // console.log(drinks);

  const freshJuices = drinksMenu["fresh juices"];
  const smoothies = drinksMenu["smoothies"];
  const specials = drinksMenu["specials"];
  const mellowFreshMilks = drinksMenu["mellow fresh milks"];
  const hotDrinks = drinksMenu["hot drinks"];
  const fineTeas = drinksMenu["fine teas"];
  const milkTeas = drinksMenu["milk teas"];
  const winterMelons = drinksMenu["winter melons"];
  const yakultAndPolyphenols = drinksMenu["yakult and polyphenols"];
  const lists = [
    freshJuices,
    smoothies,
    specials,
    mellowFreshMilks,
    hotDrinks,
    fineTeas,
    milkTeas,
    winterMelons,
    yakultAndPolyphenols,
  ];

  const container = document.getElementById("list");

  for (let i = 0; i < lists.length; i++) {
    lists[i].forEach((label, index) => {
      // 建立 checkbox 元素
      const checkbox = document.createElement("input");
      checkbox.type = "checkbox";
      checkbox.id = `option_${index}`;
      checkbox.name = "interests";
      checkbox.value = label;

      // 建立對應的 label 元素
      const labelElement = document.createElement("label");
      labelElement.htmlFor = checkbox.id;
      labelElement.textContent = label;

      // 加入 checkbox 和 label 到 container
      container.appendChild(checkbox);
      container.appendChild(labelElement);

      // 加上換行
      container.appendChild(document.createElement("br"));
    });

    // Display the result
    document.getElementById("result").textContent = randomDrink;
  }
}

// Export the function
window.checkItems = checkItems;
