<template>
  <div class="menu-display">
    <h2>飲料菜單</h2>
    <div id="menuContent" class="menu-content">
      <div class="menuImage">
        <img class="menu-image" src="../assets/images/menu.png" alt="Menu Image" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.menuImage {
    width: 90%;
    margin: 0 auto;
    display: flex;
    justify-content: center;
}

.menu-image {
    max-width: 95vh;
    justify-self: center;
    border-radius: 20px;
}
</style>
