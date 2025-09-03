<template>
  <!-- Menu Modal Zone -->
  <div id="menuModalOverlay" class="modal-overlay" @click="closeModalOnOverlay">
    <div class="modal" style="width: 80vw" @click.stop>
      <div id="menu"></div>
      <button id="closeMenuModalBtn" @click="closeModal" class="close-btn">關閉</button>
    </div>
  </div>
  <!-- End of Menu Modal Zone -->
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'

const closeModal = () => {
  const menuModalOverlay = document.getElementById('menuModalOverlay')
  if (menuModalOverlay) {
    menuModalOverlay.style.display = 'none'
  }
}

const closeModalOnOverlay = (event) => {
  if (event.target === event.currentTarget) {
    closeModal()
  }
}

const handleEscKey = (event) => {
  if (event.key === 'Escape') {
    const menuModalOverlay = document.getElementById('menuModalOverlay')
    if (menuModalOverlay && menuModalOverlay.style.display === 'flex') {
      closeModal()
    }
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleEscKey)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscKey)
})
</script>
