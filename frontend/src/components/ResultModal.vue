<template>
  <!-- Result Modal Zone -->
  <div id="resultModalOverlay" class="modal-overlay" @click="closeModalOnOverlay">
    <div class="modal" style="width: 30vw" @click.stop>
      <h2 id="result"></h2>
      <button id="closeResultModalBtn" @click="closeModal" class="close-btn">關閉</button>
    </div>
  </div>
  <!-- End of Result Modal Zone -->
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'

const closeModal = () => {
  const resultModalOverlay = document.getElementById('resultModalOverlay')
  if (resultModalOverlay) {
    resultModalOverlay.style.display = 'none'
  }
}

const closeModalOnOverlay = (event) => {
  if (event.target === event.currentTarget) {
    closeModal()
  }
}

const handleEscKey = (event) => {
  if (event.key === 'Escape') {
    const resultModalOverlay = document.getElementById('resultModalOverlay')
    if (resultModalOverlay && resultModalOverlay.style.display === 'flex') {
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
