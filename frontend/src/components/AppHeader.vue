<template>
  <div class="container">
    <div class="slogan">
      <div class="text-slogan">今天喝什麼？</div>
    </div>
  </div>
</template>

<style scoped>
.navigation {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

.nav-link {
  color: #333;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s;
  font-weight: 500;
}

.nav-link:hover {
  background: rgba(0, 123, 255, 0.1);
  color: #007bff;
}

.nav-link.router-link-active {
  background: #007bff;
  color: white;
}
</style>
