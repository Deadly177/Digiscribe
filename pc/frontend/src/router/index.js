import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Dashboard from '../views/Dashboard.vue'
import DigitRecognition from '../views/DigitRecognition.vue'
import TrainModel from '../views/TrainModel.vue'
import Analytics from '../views/Analytics.vue'
import FeedbackSystem from '../views/FeedbackSystem.vue'
import AdminPanel from '../views/AdminPanel.vue'
import Home from '../views/Home.vue'
import Profile from '../views/Profile.vue'
import Settings from '../views/Settings.vue'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    redirect: { name: 'Home' },
    children: [
    {
      path: '',
      name: 'Home',
      component: Home  // Use the new component here
    },
      {
        path: 'recognition',
        name: 'DigitRecognition',
        component: DigitRecognition
      },
      {
        path: 'models',
        name: 'TrainModel',
        component: TrainModel
      },
      {
        path: 'analytics',
        name: 'Analytics',
        component: Analytics
      },
      {
        path: 'feedback',
        name: 'FeedbackSystem',
        component: FeedbackSystem
      },
      {
        path: 'admin',
        name: 'AdminPanel',
        component: AdminPanel
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile
      },
      {
        path: 'settings',
        name: 'Settings',
        component: Settings
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
