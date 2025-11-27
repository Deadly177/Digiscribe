<template>
  <div class="auth-page">
    <div class="language-toggle">
      <LanguageSwitcher />
    </div>
    <div class="auth-container">
      <div class="auth-card">
      <!-- Left Side - Branding -->
      <div class="brand-section">
        <div class="logo">
          <div class="logo-icon">
            <img src="@/assets/brand-logo.svg" alt="DigiScrib Logo">
          </div>
          <div class="brand-text">
            <h1 class="app-name">{{ t('brand.appName') }}</h1>
            <p class="app-tagline">{{ t('brand.tagline') }}</p>
          </div>
        </div>
        
        <div class="brand-content">
          <h2 class="brand-title">{{ t('brand.welcome') }}</h2>
          <p class="brand-subtitle">{{ t('brand.subtitle') }}</p>
        </div>

        <div class="feature-list">
          <div class="feature-item">
            <div class="feature-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                <polyline points="22 4 12 14.01 9 11.01"/>
              </svg>
            </div>
            <span>{{ t('brand.feature1') }}</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="20" x2="18" y2="10"/>
                <line x1="12" y1="20" x2="12" y2="4"/>
                <line x1="6" y1="20" x2="6" y2="14"/>
              </svg>
            </div>
            <span>{{ t('brand.feature2') }}</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <circle cx="12" cy="5" r="2"/>
                <path d="M12 7v4"/>
              </svg>
            </div>
            <span>{{ t('brand.feature3') }}</span>
          </div>
        </div>
      </div>

      <!-- Right Side - Login Form -->
      <div class="form-section">
        <div class="form-container">
          <div class="form-header">
            <h3>{{ t('login.heading') }}</h3>
            <p>{{ t('login.subheading') }}</p>
          </div>

          <div v-if="errorMessage" class="error-banner">
            {{ errorMessage }}
          </div>

          <form @submit.prevent="handleLogin" class="auth-form">
            <div class="form-group">
              <label for="username">{{ t('login.username') }}</label>
              <div class="input-container">
                <div class="input-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                </div>
                <input
                  id="username"
                  v-model="form.username"
                  type="text"
                  :placeholder="t('login.usernamePlaceholder')"
                  required
                  class="form-input"
                >
              </div>
            </div>

            <div class="form-group">
              <div class="label-container">
                <label for="password">{{ t('login.password') }}</label>
                <a href="#" class="forgot-link">{{ t('login.forgot') }}</a>
              </div>
              <div class="input-container">
                <div class="input-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                  </svg>
                </div>
                <input
                  id="password"
                  v-model="form.password"
                  :type="showPassword ? 'text' : 'password'"
                  :placeholder="t('login.passwordPlaceholder')"
                  required
                  class="form-input"
                >
                <button
                  type="button"
                  class="password-toggle"
                  @click="showPassword = !showPassword"
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path v-if="!showPassword" d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                    <circle v-if="!showPassword" cx="12" cy="12" r="3"/>
                    <path v-if="showPassword" d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                    <line v-if="showPassword" x1="1" y1="1" x2="23" y2="23"/>
                  </svg>
                </button>
              </div>
            </div>

            <div class="form-options">
              <label class="checkbox-container">
                <input type="checkbox" v-model="form.rememberMe">
                <span class="checkmark"></span>
                {{ t('login.remember') }}
              </label>
            </div>

            <button type="submit" class="submit-btn" :disabled="loading">
              <span v-if="loading" class="loading-spinner"></span>
              {{ loading ? t('login.submitting') : t('login.submit') }}
            </button>
          </form>

          <div class="auth-footer">
            <p>{{ t('login.noAccount') }} 
              <a @click="$router.push('/register')" class="auth-link">{{ t('login.signUp') }}</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import authService from '@/services/authService'
import { useI18n } from '@/i18n'
import LanguageSwitcher from '@/components/common/LanguageSwitcher.vue'

export default {
  name: 'LoginPage',
  components: {
    LanguageSwitcher
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const showPassword = ref(false)
    const errorMessage = ref('')
    const { t } = useI18n()
    
    const form = ref({
      username: '',
      password: '',
      rememberMe: false
    })

    const handleLogin = async () => {
      loading.value = true
      errorMessage.value = ''
      
      try {
        await authService.login({
          username: form.value.username,
          password: form.value.password
        })
        
        router.push({ name: 'Home' })
      } catch (error) {
        errorMessage.value = error.message
      } finally {
        loading.value = false
      }
    }

    const handleWechatLogin = () => {
      alert('Wechat login is coming soon.')
    }

    const loginAsGuest = () => {
      const demoUser = {
        username: form.value.username || 'Demo Admin',
        email: 'demo@digiscrib.ai',
        role: 'ADMIN'
      }
      localStorage.setItem('authToken', 'demo-token')
      localStorage.setItem('user', JSON.stringify(demoUser))
      router.push({ name: 'Home' })
    }

    return {
      form,
      loading,
      showPassword,
      errorMessage,
      handleLogin,
      handleWechatLogin,
      loginAsGuest,
      t
    }
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
}

.language-toggle {
  width: 100%;
  max-width: 1100px;
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.auth-container {
  width: 100%;
  max-width: 1100px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  padding: 0 16px;
}

.auth-card {
  background: var(--surface);
  border-radius: 16px;
  box-shadow: var(--shadow-lg);
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: 600px;
  overflow: hidden;
  width: 100%;
  max-width: 1040px;
}

.error-banner {
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
  border-radius: 8px;
  padding: 10px 12px;
  margin-bottom: 16px;
  font-size: 13px;
}

/* Brand Section */
.brand-section {
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: white;
  padding: 48px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 640px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 40px;
}

.logo-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon img {
  width: 44px;
  height: 44px;
}

.brand-text {
  line-height: 1.3;
}

.app-name {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 2px;
  word-break: keep-all;
}

.app-tagline {
  font-size: 14px;
  opacity: 0.9;
  font-weight: 500;
  word-break: keep-all;
}

.brand-content {
  margin-bottom: 40px;
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  word-break: keep-all;
}

.brand-subtitle {
  opacity: 0.9;
  font-size: 16px;
  line-height: 1.5;
  word-break: keep-all;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  opacity: 0.9;
}

.feature-item span {
  word-break: keep-all;
}

.feature-icon {
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.feature-icon svg {
  color: white;
}

/* Form Section */
.form-section {
  padding: 48px;
  display: flex;
  align-items: center;
  min-height: 640px;
}

.form-container {
  width: 100%;
  max-width: 320px;
  margin: 0 auto;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-header h3 {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.form-header p {
  color: var(--text-secondary);
  font-size: 14px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.label-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-link {
  font-size: 12px;
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
}

.forgot-link:hover {
  text-decoration: underline;
}

.input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 12px;
  z-index: 1;
  color: var(--text-secondary);
}

.input-icon svg {
  color: var(--text-secondary);
}

.form-input {
  width: 100%;
  padding: 12px 12px 12px 40px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s ease;
  background: var(--surface);
  color: var(--text-primary);
}

.form-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-light);
}

.form-input::placeholder {
  color: var(--text-secondary);
}

.password-toggle {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: var(--text-secondary);
}

.password-toggle:hover {
  color: var(--text-primary);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkbox-container {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
}

.checkbox-container input {
  display: none;
}

.checkmark {
  width: 16px;
  height: 16px;
  border: 2px solid var(--border);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.checkbox-container input:checked + .checkmark {
  background: var(--primary);
  border-color: var(--primary);
}

.checkbox-container input:checked + .checkmark::after {
  content: '';
  width: 4px;
  height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
  margin-top: -1px;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.submit-btn:hover:not(:disabled) {
  background: var(--primary-dark);
  transform: translateY(-1px);
  box-shadow: var(--shadow);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.ghost-btn {
  width: 100%;
  margin-top: 12px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid rgba(148, 163, 184, 0.4);
  background: transparent;
  color: var(--text-primary);
  font-weight: 500;
  cursor: pointer;
  transition: border-color 0.2s ease;
}

.ghost-btn:hover {
  border-color: rgba(148, 163, 184, 0.8);
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.divider {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-secondary);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border);
}

.google-btn {
  width: 100%;
  padding: 12px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--text-primary);
}

.google-btn:hover {
  border-color: var(--text-secondary);
  background: var(--background);
}

.google-icon {
  display: flex;
  align-items: center;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--border);
}

.auth-footer p {
  color: var(--text-secondary);
  font-size: 14px;
}

.auth-link {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
  cursor: pointer;
  margin-left: 4px;
}

.auth-link:hover {
  text-decoration: underline;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 768px) {
  .auth-card {
    grid-template-columns: 1fr;
    margin: 20px;
  }
  
  .brand-section {
    display: none;
  }
  
  .form-section {
    padding: 32px 24px;
  }
}
</style>
