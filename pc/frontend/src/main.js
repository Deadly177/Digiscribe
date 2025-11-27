import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// Apply saved theme on load
const savedTheme = localStorage.getItem('digiscribe_theme')
if (savedTheme) {
  document.documentElement.setAttribute('data-theme', savedTheme)
}

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
