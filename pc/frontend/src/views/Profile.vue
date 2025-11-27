<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>Profile</h1>
      <p>View and update your account details.</p>
    </div>

    <div class="profile-grid">
      <div class="card">
        <h2>Account Info</h2>
        <div class="info-row">
          <span class="label">Username</span>
          <span class="value">{{ user.username || 'Unknown' }}</span>
        </div>
        <div class="info-row">
          <span class="label">Email</span>
          <span class="value">{{ user.email || 'Not set' }}</span>
        </div>
        <button class="primary-btn" @click="refreshUser">Refresh</button>
      </div>

      <div class="card">
        <h2>Change Password</h2>
        <form @submit.prevent="changePassword">
          <label>Current Password</label>
          <input type="password" v-model="passwordForm.current" required />
          <label>New Password</label>
          <input type="password" v-model="passwordForm.next" required />
          <label>Confirm New Password</label>
          <input type="password" v-model="passwordForm.confirm" required />
          <button type="submit" class="primary-btn">Update Password</button>
        </form>
      </div>

      <div class="card">
        <h2>Email Verification</h2>
        <p v-if="emailVerified">Your email is verified.</p>
        <p v-else>Verify your email to secure your account.</p>
        <button class="ghost-btn" @click="sendVerification" :disabled="verifying">
          {{ verifying ? 'Sending...' : 'Send Verification Email' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import authService from '@/services/authService'
import api from '@/services/api'

export default {
  name: 'Profile',
  setup() {
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

    return { user, emailVerified, verifying, passwordForm, refreshUser, changePassword, sendVerification }
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
  color: #6b7280;
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
  color: #9ca3af;
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
  border: 1px solid #1f2937;
  background: #0b1120;
  color: #e5e7eb;
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
  background: #16a34a;
  color: white;
}
.ghost-btn {
  background: #1f2937;
  color: #e5e7eb;
  border: 1px solid #1f2937;
}
</style>
