import drinksMenu from './drinks-menu.js'

function populateMenu() {
  const menuContainer = document.getElementById('menu')
  if (!menuContainer) return

  // Clear existing content
  menuContainer.innerHTML = ''

  // Create menu sections
  const categories = [
    { key: 'fresh juices', title: '【新鮮果汁系列】' },
    { key: 'smoothies', title: '【冰沙系列】' },
    { key: 'specials', title: '【特調系列】' },
    { key: 'mellow fresh milks', title: '【香醇鮮奶系列】' },
    { key: 'hot drinks', title: '【熱飲系列】' },
    { key: 'fine teas', title: '【茗茶系列】' },
    { key: 'milk teas', title: '【奶茶系列】' },
    { key: 'winter melons', title: '【冬瓜系列】' },
    { key: 'yakult and polyphenols', title: '【多多/多酚系列】' },
  ]

  categories.forEach((category) => {
    if (drinksMenu[category.key]) {
      // Create section title
      const sectionTitle = document.createElement('h3')
      sectionTitle.textContent = category.title
      sectionTitle.style.color = '#333'
      sectionTitle.style.marginBottom = '10px'
      menuContainer.appendChild(sectionTitle)

      // Create drinks list
      const drinksList = document.createElement('div')
      drinksList.style.marginBottom = '20px'

      drinksMenu[category.key].forEach((drink) => {
        const drinkItem = document.createElement('p')
        drinkItem.textContent = drink
        drinkItem.style.margin = '5px 0'
        drinkItem.style.padding = '5px'
        drinkItem.style.backgroundColor = '#f5f5f5'
        drinkItem.style.borderRadius = '3px'
        drinksList.appendChild(drinkItem)
      })

      menuContainer.appendChild(drinksList)
    }
  })
}

function initializeModals() {
  // Setup Menu Modal Variables
  const menuButton = document.getElementById('menuButton')
  const closeMenuModalBtn = document.getElementById('closeMenuModalBtn')
  const menuModalOverlay = document.getElementById('menuModalOverlay')

  // Setup List Modal Variables
  const closeListModalBtn = document.getElementById('closeListModalBtn')
  const listModalOverlay = document.getElementById('listModalOverlay')

  // Setup Result Modal Variables
  const closeResultModalBtn = document.getElementById('closeResultModalBtn')
  const resultModalOverlay = document.getElementById('resultModalOverlay')

  // Add ESC key support to close modals
  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') {
      if (menuModalOverlay && menuModalOverlay.style.display === 'flex') {
        menuModalOverlay.style.display = 'none'
      }
      if (listModalOverlay && listModalOverlay.style.display === 'flex') {
        listModalOverlay.style.display = 'none'
      }
      if (resultModalOverlay && resultModalOverlay.style.display === 'flex') {
        resultModalOverlay.style.display = 'none'
      }
    }
  })

  // Click Button to Open Menu Modal
  if (menuButton) {
    menuButton.addEventListener('click', () => {
      menuModalOverlay.style.display = 'flex'
      populateMenu() // Populate menu when opening the modal
    })
  }

  // Close Button to Close Menu Modal
  if (closeMenuModalBtn) {
    closeMenuModalBtn.addEventListener('click', () => {
      menuModalOverlay.style.display = 'none'
    })
  }

  // Click Other Area to Close Menu Modal
  if (menuModalOverlay) {
    menuModalOverlay.addEventListener('click', (event) => {
      if (event.target === event.currentTarget) {
        menuModalOverlay.style.display = 'none'
      }
    })
  }

  // Close Button to Close List Modal
  if (closeListModalBtn) {
    closeListModalBtn.addEventListener('click', () => {
      listModalOverlay.style.display = 'none'
    })
  }

  // Click Other Area to Close List Modal
  if (listModalOverlay) {
    listModalOverlay.addEventListener('click', (event) => {
      if (event.target === event.currentTarget) {
        listModalOverlay.style.display = 'none'
      }
    })
  }

  // Close Button to Close Result Modal
  if (closeResultModalBtn) {
    closeResultModalBtn.addEventListener('click', () => {
      resultModalOverlay.style.display = 'none'
    })
  }

  // Click Other Area to Close Result Modal
  if (resultModalOverlay) {
    resultModalOverlay.addEventListener('click', (event) => {
      if (event.target === event.currentTarget) {
        resultModalOverlay.style.display = 'none'
      }
    })
  }
}

// Initialize modals when module is loaded
initializeModals()

export default initializeModals
