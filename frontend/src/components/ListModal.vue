<template>
  <!-- List Modal Zone -->
  <div id="listModalOverlay" class="modal-overlay" @click="closeModalOnOverlay">
    <div class="modal" style="width: 80vw" @click.stop>
      <div id="list">
        <div id="freshJuices"></div>
        <div id="smoothies"></div>
        <div id="specials"></div>
        <div id="mellowFreshMilks"></div>
        <div id="hotDrinks"></div>
        <div id="fineTeas"></div>
        <div id="milkTeas"></div>
        <div id="winterMelons"></div>
        <div id="yakultAndPolyphenols"></div>
      </div>
      <button id="closeListModalBtn" @click="closeModal" class="close-btn">關閉</button>
    </div>
  </div>
  <!-- End of List Modal Zone -->
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'

const closeModal = () => {
  const listModalOverlay = document.getElementById('listModalOverlay')
  if (listModalOverlay) {
    listModalOverlay.style.display = 'none'
  }
}

const closeModalOnOverlay = (event) => {
  if (event.target === event.currentTarget) {
    closeModal()
  }
}

const handleEscKey = (event) => {
  if (event.key === 'Escape') {
    const listModalOverlay = document.getElementById('listModalOverlay')
    if (listModalOverlay && listModalOverlay.style.display === 'flex') {
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
