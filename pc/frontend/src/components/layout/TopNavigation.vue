<template>
  <nav class="top-navigation">
    <div class="nav-left">
      <button class="sidebar-toggle" @click="$emit('toggle-sidebar')">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="3" y1="12" x2="21" y2="12"/>
          <line x1="3" y1="6" x2="21" y2="6"/>
          <line x1="3" y1="18" x2="21" y2="18"/>
        </svg>
      </button>
      <div class="nav-brand">
        <img src="@/assets/brand-logo.svg" alt="logo">
        <div>
          <span class="brand-name">{{ t('brand.appName') }}</span>
          <span class="page-title">{{ currentPageTitle }}</span>
        </div>
      </div>
    </div>
    
    <div class="nav-right">
      <LanguageSwitcher class="nav-language" />
      <div class="nav-item notifications" ref="notificationsRef">
        <button class="icon-button" @click.stop="toggleNotifications">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
          </svg>
          <span class="notification-badge" v-if="unreadNotifications">{{ unreadNotifications }}</span>
        </button>
        <div v-if="showNotifications" class="notification-dropdown">
          <div class="notification-header">
            <span>{{ t('nav.notifications') }}</span>
            <button @click="markNotificationsRead">{{ t('nav.markRead') }}</button>
          </div>
          <div class="notification-list" v-if="notifications.length">
            <div
              v-for="notification in notifications"
              :key="notification.id"
              class="notification-item"
              :class="{ read: notification.read }"
            >
              <p>{{ notification.message }}</p>
              <small>{{ formatTime(notification.timestamp) }}</small>
            </div>
          </div>
          <p v-else class="empty-state">{{ t('nav.empty') }}</p>
          <button class="notification-footer">{{ t('nav.viewAll') }}</button>
        </div>
      </div>
      
      <div class="user-menu" ref="userMenuRef">
        <button class="user-trigger" @click.stop="toggleUserMenu">
          <div class="user-avatar">
            {{ userInitials }}
          </div>
          <span class="user-name">{{ userName }}</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="6 9 12 15 18 9"/>
          </svg>
        </button>
        <div v-if="showUserMenu" class="user-dropdown">
          <div class="user-info">
            <div class="user-avatar large">
              {{ userInitials }}
            </div>
            <div class="user-details">
              <div class="user-name">{{ userName }}</div>
              <div class="user-email">{{ userEmail }}</div>
            </div>
          </div>
          <div class="dropdown-divider"></div>
          <button class="dropdown-item" @click="goProfile">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
            {{ t('nav.profile') }}
          </button>
          <button class="dropdown-item" @click="goSettings">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="3"/>
              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
            </svg>
            {{ t('nav.settings') }}
          </button>
          <div class="dropdown-divider"></div>
          <button class="dropdown-item text-danger" @click="handleLogout">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
            {{ t('nav.signOut') }}
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import LanguageSwitcher from '../common/LanguageSwitcher.vue'
import { useI18n } from '@/i18n'
import authService from '@/services/authService'
import api from '@/services/api'

export default {
  name: 'TopNavigation',
  components: { LanguageSwitcher },
  emits: ['toggle-sidebar'],
  setup() {
    const router = useRouter()
    const route = useRoute()
    const { t } = useI18n()

    const showUserMenu = ref(false)
    const showNotifications = ref(false)
    const notifications = ref([])
    const notificationsLoaded = ref(false)
    const user = ref(authService.getCurrentUser())
    const notificationsRef = ref(null)
    const userMenuRef = ref(null)

    const refreshUser = () => {
      user.value = authService.getCurrentUser()
    }

    const userInitials = computed(() => {
      const base = user.value?.username || user.value?.name || t('nav.guest')
      return base
        .split(' ')
        .map(n => n[0])
        .join('')
        .substring(0, 2)
        .toUpperCase()
    })

    const userName = computed(() => user.value?.username || user.value?.name || t('nav.guest'))
    const userEmail = computed(() => user.value?.email || t('nav.noEmail'))

    const currentPageTitle = computed(() => {
      const map = {
        Home: t('sidebar.dashboard'),
        DigitRecognition: t('sidebar.recognition'),
        TrainModel: t('sidebar.models'),
        Analytics: t('sidebar.analytics'),
        FeedbackSystem: t('sidebar.feedback'),
        AdminPanel: t('sidebar.admin')
      }
      return map[route.name] || t('nav.dashboard')
    })

    const unreadNotifications = computed(() => notifications.value.filter(n => !n.read).length)

    const loadNotifications = async () => {
      try {
        const response = await api.get('/admin/logs')
        const payload = Array.isArray(response.data) ? response.data : []
        notifications.value = payload.slice(0, 5).map((log, index) => ({
          id: log.id || index,
          message: log.message || log.level || 'System update',
          timestamp: log.timestamp || new Date().toISOString(),
          read: false
        }))
      } catch (error) {
        notifications.value = [
          {
            id: 1,
            message: 'Model retraining completed successfully',
            timestamp: new Date().toISOString(),
            read: false
          }
        ]
      } finally {
        notificationsLoaded.value = true
      }
    }

    const toggleNotifications = async () => {
      showNotifications.value = !showNotifications.value
      if (showNotifications.value && !notificationsLoaded.value) {
        await loadNotifications()
      }
    }

    const markNotificationsRead = () => {
      notifications.value = notifications.value.map(n => ({ ...n, read: true }))
    }

    const goProfile = () => {
      showUserMenu.value = false
      router.push({ name: 'Profile' })
    }

    const goSettings = () => {
      showUserMenu.value = false
      router.push({ name: 'Settings' })
    }

    const toggleUserMenu = () => {
      showUserMenu.value = !showUserMenu.value
    }

    const handleLogout = () => {
      authService.logout()
      router.push('/')
    }

    const formatTime = (value) => {
      const date = value ? new Date(value) : new Date()
      return date.toLocaleString()
    }

    const handleClickOutside = (event) => {
      if (notificationsRef.value && !notificationsRef.value.contains(event.target)) {
        showNotifications.value = false
      }
      if (userMenuRef.value && !userMenuRef.value.contains(event.target)) {
        showUserMenu.value = false
      }
    }

    onMounted(() => {
      window.addEventListener('storage', refreshUser)
      document.addEventListener('click', handleClickOutside)
    })

    onBeforeUnmount(() => {
      window.removeEventListener('storage', refreshUser)
      document.removeEventListener('click', handleClickOutside)
    })

    return {
      t,
      showUserMenu,
      showNotifications,
      notifications,
      unreadNotifications,
      userInitials,
      userName,
      userEmail,
      currentPageTitle,
      goProfile,
      goSettings,
      toggleNotifications,
      markNotificationsRead,
      toggleUserMenu,
      handleLogout,
      formatTime,
      notificationsRef,
      userMenuRef
    }
  }
}
</script>

<style scoped>
.top-navigation {
  height: 70px;
  background: white;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-brand img {
  width: 40px;
  height: 40px;
}

.brand-name {
  display: block;
  font-size: 12px;
  text-transform: uppercase;
  color: #94a3b8;
  letter-spacing: 0.1em;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.sidebar-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  color: #64748b;
  transition: all 0.2s ease;
}

.sidebar-toggle:hover {
  background: #f1f5f9;
  color: #334155;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-language {
  background: #f1f5f9;
  border-radius: 999px;
}

.nav-item {
  position: relative;
}

.icon-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  color: #64748b;
  transition: all 0.2s ease;
}

.icon-button:hover {
  background: #f1f5f9;
  color: #334155;
}

.notification-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  background: #ef4444;
  color: white;
  font-size: 10px;
  font-weight: 700;
  border-radius: 999px;
  padding: 0 5px;
}

.notification-dropdown {
  position: absolute;
  top: 45px;
  right: 0;
  width: 280px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.15);
  padding: 12px;
  z-index: 10;
}

.notification-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.notification-header button {
  border: none;
  background: none;
  font-size: 12px;
  color: #2563eb;
  cursor: pointer;
}

.notification-list {
  max-height: 220px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notification-item {
  padding: 8px;
  border-radius: 8px;
  background: #f8fafc;
}

.notification-item.read {
  opacity: 0.7;
}

.notification-item p {
  margin: 0 0 4px;
  font-weight: 500;
  color: #0f172a;
}

.notification-item small {
  color: #64748b;
}

.notification-footer {
  width: 100%;
  border: none;
  background: #f1f5f9;
  border-radius: 8px;
  padding: 6px 0;
  margin-top: 10px;
  cursor: pointer;
  color: #2563eb;
  font-weight: 600;
}

.empty-state {
  padding: 16px 8px;
  color: #94a3b8;
  text-align: center;
}

.user-menu {
  position: relative;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  background: none;
  border: 1px solid #e2e8f0;
  padding: 6px 12px;
  border-radius: 999px;
  cursor: pointer;
  color: #0f172a;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #0f172a;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.user-avatar.large {
  width: 56px;
  height: 56px;
  font-size: 20px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}

.user-dropdown {
  position: absolute;
  top: 50px;
  right: 0;
  width: 220px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 20px 45px rgba(15, 23, 42, 0.15);
  padding: 12px;
  z-index: 20;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details .user-name {
  font-size: 16px;
}

.user-email {
  color: #64748b;
  font-size: 13px;
}

.dropdown-divider {
  height: 1px;
  background: #e2e8f0;
  margin: 12px 0;
}

.dropdown-item {
  width: 100%;
  border: none;
  background: none;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  color: #0f172a;
  cursor: pointer;
}

.dropdown-item svg {
  color: #94a3b8;
}

.dropdown-item.text-danger {
  color: #dc2626;
}
</style>
