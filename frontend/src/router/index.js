import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import MenuView from '../views/MenuView.vue'
import SelectView from '../views/SelectView.vue'
import RandomView from '../views/RandomView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/menu',
      name: 'menu',
      component: MenuView,
    },
    {
      path: '/select',
      name: 'select',
      component: SelectView,
    },
    {
      path: '/random',
      name: 'random',
      component: RandomView,
    },
  ],
})

export default router
