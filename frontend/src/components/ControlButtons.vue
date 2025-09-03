<template>
  <!-- Button Zone -->
  <div class="container">
    <div class="row-buttons">
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button id="menuButton" @click="openMenuModal" class="btn btn-primary">菜單</button>
      </div>
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button id="selectButton" @click="openSelectModal" class="btn btn-primary">請選擇</button>
      </div>
      <div class="col-xs-6 col-lg-6 col-md-6">
        <button id="ramdomButton" @click="openRandomModal" class="btn btn-primary">隨機結果</button>
      </div>
    </div>
  </div>
  <!-- End of Button Zone -->
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const openMenuModal = () => {
  router.push('/menu')
}

const openSelectModal = () => {
  router.push('/select')
}

const openRandomModal = () => {
  router.push('/random')
}
</script>
