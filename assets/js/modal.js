document.addEventListener("DOMContentLoaded", () => {
    const menuButton = document.getElementById("menuButton");
    const closeMenuModalBtn = document.getElementById("closeMenuModalBtn");
    const menuModalOverlay = document.getElementById("menuModalOverlay");

    const selectButton = document.getElementById("selectButton");
    const closeResultModalBtn = document.getElementById("closeResultModalBtn");
    const resultModalOverlay = document.getElementById("resultModalOverlay");

    // 點擊 [菜單] 按鈕開啟 Menu Modal
    menuButton.addEventListener("click", () => {
        menuModalOverlay.style.display = "flex";
    });

    // 點擊 [關閉] 按鈕關閉 Menu Modal
    closeMenuModalBtn.addEventListener("click", () => {
        menuModalOverlay.style.display = "none";
    });

    // 點擊外部區域關閉 Menu Modal
    menuModalOverlay.addEventListener("click", (event) => {
        if (event.target === event.currentTarget) {
            menuModalOverlay.style.display = "none";
        }
    });

    // 點擊 [請選擇] 按鈕開啟 Result Modal
    selectButton.addEventListener("click", () => {
        resultModalOverlay.style.display = "flex";
    });

    // 點擊 [關閉] 按鈕關閉 Result Modal
    closeResultModalBtn.addEventListener("click", () => {
        resultModalOverlay.style.display = "none";
    });

    // 點擊外部區域關閉 Result Modal
    resultModalOverlay.addEventListener("click", (event) => {
        if (event.target === event.currentTarget) {
            resultModalOverlay.style.display = "none";
        }
    });

});