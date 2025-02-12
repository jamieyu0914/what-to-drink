document.addEventListener("DOMContentLoaded", () => {

    // Setup Menu Modal Variables
    const menuButton = document.getElementById("menuButton");
    const closeMenuModalBtn = document.getElementById("closeMenuModalBtn");
    const menuModalOverlay = document.getElementById("menuModalOverlay");

    // Setup Result Modal Variables
    const selectButton = document.getElementById("selectButton");
    const closeResultModalBtn = document.getElementById("closeResultModalBtn");
    const resultModalOverlay = document.getElementById("resultModalOverlay");

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

    // Click Button to Open Result Modal
    selectButton.addEventListener("click", () => {
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