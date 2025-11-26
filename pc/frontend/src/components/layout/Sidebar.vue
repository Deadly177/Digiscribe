<template>
  <aside class="sidebar" :class="{ collapsed: isCollapsed }">
    <div class="sidebar-header">
      <div class="logo" v-if="!isCollapsed">
        <img src="@/assets/brand-logo.svg" alt="DigiScrib logo">
        <div>
          <h2>{{ t('brand.appName') }}</h2>
          <p>{{ t('brand.tagline') }}</p>
        </div>
      </div>
      <div class="logo-collapsed" v-else>
        <img src="@/assets/brand-logo.svg" alt="Logo">
      </div>
    </div>
    
    <!-- Return to Dashboard Button -->
    <div class="dashboard-return-section">
      <router-link to="/dashboard" class="dashboard-return-btn">
        <div class="nav-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
        </div>
        <span class="nav-text">{{ t('sidebar.returnDashboard') }}</span>
      </router-link>
    </div>
    
    <nav class="sidebar-nav">
      <div class="nav-section">
        <div class="nav-label" v-if="!isCollapsed">{{ t('sidebar.main') }}</div>
        <router-link to="/dashboard/recognition" class="nav-item">
          <div class="nav-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 19l7-7 3 3-7 7-3-3z"/>
              <path d="M18 13l-1.5-7.5L2 2l3.5 14.5L13 18l5-5z"/>
              <path d="M2 2l7.586 7.586"/>
            </svg>
          </div>
          <span class="nav-text">{{ t('sidebar.recognition') }}</span>
        </router-link>
        
        <router-link to="/dashboard/models" class="nav-item">
          <div class="nav-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
              <line x1="12" y1="22.08" x2="12" y2="12"/>
            </svg>
          </div>
          <span class="nav-text">{{ t('sidebar.models') }}</span>
        </router-link>
      </div>
      
      <div class="nav-section">
        <div class="nav-label" v-if="!isCollapsed">{{ t('sidebar.analyticsSection') }}</div>
        <router-link to="/dashboard/analytics" class="nav-item">
          <div class="nav-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="20" x2="18" y2="10"/>
              <line x1="12" y1="20" x2="12" y2="4"/>
              <line x1="6" y1="20" x2="6" y2="14"/>
            </svg>
          </div>
          <span class="nav-text">{{ t('sidebar.analytics') }}</span>
        </router-link>
        
        <router-link to="/dashboard/feedback" class="nav-item">
          <div class="nav-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
            </svg>
          </div>
          <span class="nav-text">{{ t('sidebar.feedback') }}</span>
        </router-link>
      </div>
      
      <div class="nav-section">
        <div class="nav-label" v-if="!isCollapsed">{{ t('sidebar.adminSection') }}</div>
        <router-link to="/dashboard/admin" class="nav-item">
          <div class="nav-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="3"/>
              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
            </svg>
          </div>
          <span class="nav-text">{{ t('sidebar.admin') }}</span>
        </router-link>
      </div>
    </nav>
    
    <div class="sidebar-footer">
      <LanguageSwitcher :compact="true" />
      <button class="collapse-btn" @click="$emit('toggle')">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6" v-if="!isCollapsed"/>
          <polyline points="9 18 15 12 9 6" v-else/>
        </svg>
        <span v-if="!isCollapsed">{{ locale === 'zh' ? '收起' : 'Collapse' }}</span>
      </button>
    </div>
  </aside>
</template>

<script>
import { computed } from 'vue'
import { useI18n } from '@/i18n'
import LanguageSwitcher from '../common/LanguageSwitcher.vue'

export default {
  name: 'Sidebar',
  components: { LanguageSwitcher },
  props: {
    isCollapsed: {
      type: Boolean,
      default: false
    }
  },
  setup() {
    const { t, locale } = useI18n()
    return {
      t,
      locale: computed(() => locale.value)
    }
  },
  emits: ['toggle']
}
</script>

<style scoped>
.sidebar {
  width: 280px;
  background: white;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  transition: width 0.3s ease;
  z-index: 100;
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  padding: 24px;
  border-bottom: 1px solid #e2e8f0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo img {
  width: 48px;
  height: 48px;
}

.logo h2 {
  color: #0f172a;
  font-weight: 700;
  margin: 0;
}

.logo p {
  color: #475569;
  font-size: 13px;
  margin: 2px 0 0;
}

.logo-collapsed {
  width: 40px;
  height: 40px;
  background: #f1f5f9;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.logo-collapsed img {
  width: 28px;
  height: 28px;
}

/* Dashboard Return Button Styles */
.dashboard-return-section {
  padding: 16px 0;
  border-bottom: 1px solid #e2e8f0;
}

.dashboard-return-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  color: #059669;
  text-decoration: none;
  transition: all 0.2s ease;
  background: #f0fdf4;
  margin: 0 16px;
  border-radius: 8px;
  border: 1px solid #d1fae5;
}

.dashboard-return-btn:hover {
  background: #dcfce7;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(5, 150, 105, 0.1);
}

.dashboard-return-btn .nav-icon {
  color: #059669;
}

.dashboard-return-btn .nav-text {
  font-weight: 600;
  color: #059669;
}

.sidebar.collapsed .dashboard-return-btn {
  justify-content: center;
  padding: 12px;
  margin: 0 12px;
}

.sidebar.collapsed .dashboard-return-btn .nav-text {
  display: none;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 0;
  overflow-y: auto;
}

.nav-section {
  margin-bottom: 24px;
}

.nav-label {
  padding: 8px 24px;
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  color: #64748b;
  text-decoration: none;
  transition: all 0.2s ease;
  border-right: 3px solid transparent;
}

.nav-item:hover {
  background: #f8fafc;
  color: #334155;
}

.nav-item.router-link-active {
  background: #f0fdf4;
  color: #059669;
  border-right-color: #059669;
}

.nav-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
}

.nav-text {
  font-weight: 500;
  white-space: nowrap;
}

.sidebar.collapsed .nav-text {
  display: none;
}

.sidebar.collapsed .nav-label {
  display: none;
}

.sidebar.collapsed .nav-item {
  justify-content: center;
  padding: 12px;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.collapse-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  background: none;
  border: none;
  padding: 8px;
  border-radius: 6px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.collapse-btn:hover {
  background: #f1f5f9;
  color: #334155;
}

.sidebar.collapsed .collapse-btn {
  justify-content: center;
}

.sidebar.collapsed .collapse-btn span {
  display: none;
}
</style>
