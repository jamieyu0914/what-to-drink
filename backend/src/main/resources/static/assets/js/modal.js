document.addEventListener("DOMContentLoaded", () => {
  // Setup Menu Modal Variables
  const menuButton = document.getElementById("menuButton");
  const closeMenuModalBtn = document.getElementById("closeMenuModalBtn");
  const menuModalOverlay = document.getElementById("menuModalOverlay");

  // Setup List Modal Variables
  const selectButton = document.getElementById("selectButton");
  const closeListModalBtn = document.getElementById("closeListModalBtn");
  const listModalOverlay = document.getElementById("listModalOverlay");

  // Setup Result Modal Variables
  const ramdomButton = document.getElementById("ramdomButton");
  const closeResultModalBtn = document.getElementById("closeResultModalBtn");
  const resultModalOverlay = document.getElementById("resultModalOverlay");

  // Add ESC key support to close modals
  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
      if (menuModalOverlay && menuModalOverlay.style.display === "flex") {
        menuModalOverlay.style.display = "none";
      }
      if (listModalOverlay && listModalOverlay.style.display === "flex") {
        listModalOverlay.style.display = "none";
      }
      if (resultModalOverlay && resultModalOverlay.style.display === "flex") {
        resultModalOverlay.style.display = "none";
      }
    }
  });

  // Click Button to Open Menu Modal
  menuButton.addEventListener("click", () => {
    menuModalOverlay.style.display = "flex";
  });

  // Close Button to Close Menu Modal
  closeMenuModalBtn.addEventListener("click", () => {
    menuModalOverlay.style.display = "none";
  });

  // Click Other Area to Close Menu Modal
  menuModalOverlay.addEventListener("click", (event) => {
    if (event.target === event.currentTarget) {
      menuModalOverlay.style.display = "none";
    }
  });

  // Click Button to Open List Modal
  selectButton.addEventListener("click", () => {
    listModalOverlay.style.display = "flex";
  });

  // Close Button to Close List Modal
  closeListModalBtn.addEventListener("click", () => {
    listModalOverlay.style.display = "none";
  });

  // Click Other Area to Close List Modal
  listModalOverlay.addEventListener("click", (event) => {
    if (event.target === event.currentTarget) {
      listModalOverlay.style.display = "none";
    }
  });

  // Click Button to Open Result Modal
  ramdomButton.addEventListener("click", () => {
    resultModalOverlay.style.display = "flex";
  });

  // Close Button to Close Result Modal
  closeResultModalBtn.addEventListener("click", () => {
    resultModalOverlay.style.display = "none";
  });

  // Click Other Area to Close Result Modal
  resultModalOverlay.addEventListener("click", (event) => {
    if (event.target === event.currentTarget) {
      resultModalOverlay.style.display = "none";
    }
  });
});
