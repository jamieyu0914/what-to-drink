import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import MenuView from '../views/MenuView.vue'
import SelectView from '../views/SelectView.vue'
import RandomView from '../views/RandomView.vue'
import AIAgentView from '../views/AIAgentView.vue'
import LoginView from '../views/LoginView.vue'
import PreLoginView from '../views/PreLoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ForgotPasswordView from '../views/ForgotPasswordView.vue'
import CallbackHandler from '../views/CallbackView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/callback',
      name: 'callback',
      component: CallbackHandler,
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
    {
      path: '/ai-agent',
      name: 'ai-agent',
      component: AIAgentView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/pre-login',
      name: 'pre-login',
      component: PreLoginView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/forgot-password',
      name: 'forgot-password',
      component: ForgotPasswordView,
    },
  ],
})

export default router
