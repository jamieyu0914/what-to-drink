<script setup>
import { onMounted } from 'vue'
import AppHeader from '../components/AppHeader.vue'
import LuckyWheel from '../components/LuckyWheel.vue'
import ControlButtons from '../components/ControlButtons.vue'

let luckyWheelModule = null
let modalModule = null

onMounted(async () => {
  try {
    // Import and initialize JavaScript modules
    luckyWheelModule = await import('../assets/js/lucky-wheel.js')
    modalModule = await import('../assets/js/modal.js')
    await import('../assets/js/drinks-menu.js')
  } catch (error) {
    console.error('Error loading modules:', error)
  }
})
</script>

<template>
  <div id="app">
    <!-- App Header Component -->
    <AppHeader />

    <!-- Lucky Wheel Component -->
    <LuckyWheel />

    <!-- Control Buttons Component -->
    <ControlButtons />
  </div>
</template>

<style scoped>
@import '../assets/main.css';
@import '../assets/style.css';
</style>
