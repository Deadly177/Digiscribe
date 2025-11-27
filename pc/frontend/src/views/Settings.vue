<template>
  <div class="settings-page">
    <div class="page-header">
      <h1>Settings</h1>
      <p>Customize your DigiScribe experience.</p>
    </div>

    <div class="settings-grid">
      <div class="card">
        <h2>Appearance</h2>
        <div class="setting-row">
          <div>
            <div class="label">Dark mode</div>
            <div class="hint">Toggle the app theme between light and dark.</div>
          </div>
          <label class="switch">
            <input type="checkbox" v-model="darkMode" @change="applyTheme">
            <span class="slider"></span>
          </label>
        </div>
      </div>

      <div class="card">
        <h2>Notifications</h2>
        <div class="setting-row">
          <div>
            <div class="label">Email alerts</div>
            <div class="hint">Receive updates when models finish training.</div>
          </div>
          <label class="switch">
            <input type="checkbox" v-model="emailAlerts">
            <span class="slider"></span>
          </label>
        </div>
        <div class="setting-row">
          <div>
            <div class="label">Push notifications</div>
            <div class="hint">Show in-app notifications for new activity.</div>
          </div>
          <label class="switch">
            <input type="checkbox" v-model="pushAlerts">
            <span class="slider"></span>
          </label>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

const THEME_KEY = 'digiscribe_theme'

export default {
  name: 'Settings',
  setup() {
    const darkMode = ref(false)
    const emailAlerts = ref(true)
    const pushAlerts = ref(true)

    const applyTheme = () => {
      const theme = darkMode.value ? 'dark' : 'light'
      document.documentElement.setAttribute('data-theme', theme)
      localStorage.setItem(THEME_KEY, theme)
    }

    onMounted(() => {
      const saved = localStorage.getItem(THEME_KEY)
      if (saved === 'dark') {
        darkMode.value = true
        applyTheme()
      }
    })

    return { darkMode, emailAlerts, pushAlerts, applyTheme }
  }
}
</script>

<style scoped>
.settings-page {
  padding: 24px;
}
.page-header {
  margin-bottom: 16px;
}
.page-header h1 {
  margin: 0;
  font-size: 24px;
}
.page-header p {
  margin: 4px 0 0;
  color: #6b7280;
}
.settings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 16px;
}
.card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 16px;
  color: var(--text-primary);
}
.card h2 {
  margin: 0 0 12px;
  font-size: 18px;
}
.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #1f2937;
}
.setting-row:last-child {
  border-bottom: none;
}
.label {
  font-weight: 600;
}
.hint {
  color: #9ca3af;
  font-size: 13px;
}
.switch {
  position: relative;
  display: inline-block;
  width: 46px;
  height: 24px;
}
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}
.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #4b5563;
  transition: 0.2s;
  border-radius: 24px;
}
.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.2s;
  border-radius: 50%;
}
input:checked + .slider {
  background-color: #22c55e;
}
input:checked + .slider:before {
  transform: translateX(22px);
}
</style>
