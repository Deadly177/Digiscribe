<template>
  <div class="settings-page">
    <div class="page-header">
      <h1>{{ t('settings.title') }}</h1>
      <p>{{ t('settings.subtitle') }}</p>
    </div>

    <div class="settings-grid">
      <div class="card">
        <h2>{{ t('settings.appearance') }}</h2>
        <div class="setting-row">
          <div>
            <div class="label">{{ t('settings.darkMode') }}</div>
            <div class="hint">{{ t('settings.darkModeHint') }}</div>
          </div>
          <label class="switch">
            <input type="checkbox" v-model="darkMode" @change="applyTheme">
            <span class="slider"></span>
          </label>
        </div>
      </div>

      <div class="card">
        <h2>{{ t('settings.notifications') }}</h2>
        <div class="setting-row">
          <div>
            <div class="label">{{ t('settings.emailAlerts') }}</div>
            <div class="hint">{{ t('settings.emailAlertsHint') }}</div>
          </div>
          <label class="switch">
            <input type="checkbox" v-model="emailAlerts">
            <span class="slider"></span>
          </label>
        </div>
        <div class="setting-row">
          <div>
            <div class="label">{{ t('settings.pushNotifications') }}</div>
            <div class="hint">{{ t('settings.pushNotificationsHint') }}</div>
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
import { useI18n } from '@/i18n'

const THEME_KEY = 'digiscribe_theme'

export default {
  name: 'Settings',
  setup() {
    const { t } = useI18n()
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
      }
      applyTheme()
    })

    return { t, darkMode, emailAlerts, pushAlerts, applyTheme }
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
  color: var(--text-secondary);
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
  border-bottom: 1px solid var(--border);
}
.setting-row:last-child {
  border-bottom: none;
}
.label {
  font-weight: 600;
}
.hint {
  color: var(--text-secondary);
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
  background-color: var(--border);
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
  background-color: var(--success);
}
input:checked + .slider:before {
  transform: translateX(22px);
}
</style>
