<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>{{ t('profile.title') }}</h1>
      <p>{{ t('profile.subtitle') }}</p>
    </div>

    <div class="profile-grid">
      <div class="card">
        <h2>{{ t('profile.accountInfo') }}</h2>
        <div class="info-row">
          <span class="label">{{ t('profile.username') }}</span>
          <span class="value">{{ user.username || t('profile.unknown') }}</span>
        </div>
        <div class="info-row">
          <span class="label">{{ t('profile.email') }}</span>
          <span class="value">{{ user.email || t('profile.notSet') }}</span>
        </div>
        <button class="primary-btn" @click="refreshUser">{{ t('profile.refresh') }}</button>
      </div>

      <div class="card">
        <h2>{{ t('profile.changePassword') }}</h2>
        <form @submit.prevent="changePassword">
          <label>{{ t('profile.currentPassword') }}</label>
          <input type="password" v-model="passwordForm.current" required />
          <label>{{ t('profile.newPassword') }}</label>
          <input type="password" v-model="passwordForm.next" required />
          <label>{{ t('profile.confirmNewPassword') }}</label>
          <input type="password" v-model="passwordForm.confirm" required />
          <button type="submit" class="primary-btn">{{ t('profile.updatePassword') }}</button>
        </form>
      </div>

      <div class="card">
        <h2>{{ t('profile.emailVerification') }}</h2>
        <p v-if="emailVerified">{{ t('profile.emailVerified') }}</p>
        <p v-else>{{ t('profile.verifyEmail') }}</p>
        <button class="ghost-btn" @click="sendVerification" :disabled="verifying">
          {{ verifying ? t('profile.sending') : t('profile.sendVerificationEmail') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import authService from '@/services/authService'
import api from '@/services/api'
import { useI18n } from '@/i18n'

export default {
  name: 'Profile',
  setup() {
    const { t } = useI18n()
    const user = ref(authService.getCurrentUser() || {})
    const emailVerified = ref(false)
    const verifying = ref(false)
    const passwordForm = ref({
      current: '',
      next: '',
      confirm: ''
    })

    const refreshUser = () => {
      user.value = authService.getCurrentUser() || {}
      emailVerified.value = !!user.value.emailVerified
    }

    const changePassword = async () => {
      if (passwordForm.value.next !== passwordForm.value.confirm) {
        alert('New passwords do not match')
        return
      }
      try {
        await api.post('/auth/change-password', {
          currentPassword: passwordForm.value.current,
          newPassword: passwordForm.value.next
        })
        alert('Password updated')
        passwordForm.value = { current: '', next: '', confirm: '' }
      } catch (e) {
        alert(e.response?.data?.message || 'Password update failed')
      }
    }

    const sendVerification = async () => {
      verifying.value = true
      try {
        await api.post('/auth/send-verification', { email: user.value.email })
        alert('Verification email sent')
      } catch (e) {
        alert(e.response?.data?.message || 'Failed to send verification email')
      } finally {
        verifying.value = false
      }
    }

    onMounted(refreshUser)

    return { t, user, emailVerified, verifying, passwordForm, refreshUser, changePassword, sendVerification }
  }
}
</script>

<style scoped>
.profile-page {
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
.profile-grid {
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
.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}
.label {
  color: var(--text-secondary);
}
.value {
  font-weight: 600;
}
form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
input {
  padding: 10px;
  border-radius: 8px;
  border: 1px solid var(--border);
  background: var(--surface);
  color: var(--text-primary);
}
.primary-btn, .ghost-btn {
  margin-top: 8px;
  padding: 10px 12px;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  font-weight: 600;
}
.primary-btn {
  background: var(--success);
  color: white;
}
.primary-btn:hover {
  opacity: 0.9;
}
.ghost-btn {
  background: var(--surface);
  color: var(--text-primary);
  border: 1px solid var(--border);
}
.ghost-btn:hover {
  background: var(--border);
}
</style>
