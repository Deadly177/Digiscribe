import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Dashboard from '../views/Dashboard.vue'
import DigitRecognition from '../views/DigitRecognition.vue'
import ModelManagement from '../views/ModelManagement.vue'
import Analytics from '../views/Analytics.vue'
import FeedbackSystem from '../views/FeedbackSystem.vue'
import AdminPanel from '../views/AdminPanel.vue'
import DashboardHome from '../views/DashboardHome.vue'

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
    redirect: { name: 'DashboardHome' },
    children: [
    {
      path: '',
      name: 'DashboardHome',
      component: DashboardHome  // Use the new component here
    },
      {
        path: 'recognition',
        name: 'DigitRecognition',
        component: DigitRecognition
      },
      {
        path: 'models',
        name: 'ModelManagement',
        component: ModelManagement
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
