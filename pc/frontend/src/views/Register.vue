<template>
  <div class="auth-container">
    <div class="auth-card">
      <!-- Left Side - Clean Form -->
      <div class="form-section">
        <div class="form-container">
          <div class="form-header">
            <h2>Create Account</h2>
            <p>Join DigiScrib to start recognizing digits</p>
          </div>

          <form @submit.prevent="handleRegister" class="auth-form">
            <div v-if="errorMessage" class="error-banner">
              {{ errorMessage }}
            </div>
            <div class="form-group">
              <label for="fullName">Full Name</label>
              <input
                id="fullName"
                v-model="form.fullName"
                type="text"
                placeholder="Enter your full name"
                required
                class="form-input"
              >
            </div>

            <div class="form-group">
              <label for="email">Email Address</label>
              <input
                id="email"
                v-model="form.email"
                type="email"
                placeholder="Enter your email"
                required
                class="form-input"
              >
            </div>

            <div class="form-group">
              <label for="username">Username</label>
              <input
                id="username"
                v-model="form.username"
                type="text"
                placeholder="Choose a username"
                required
                class="form-input"
              >
            </div>

            <div class="form-group">
              <label for="password">Password</label>
              <div class="password-input-container">
                <input
                  id="password"
                  v-model="form.password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="Create a password"
                  required
                  class="form-input"
                >
                <button
                  type="button"
                  class="password-toggle"
                  @click="showPassword = !showPassword"
                >
                  <span v-if="showPassword">Hide</span>
                  <span v-else>Show</span>
                </button>
              </div>
              <div class="password-strength">
                <div class="strength-bar" :class="passwordStrengthClass"></div>
                <span class="strength-text">{{ passwordStrengthText }}</span>
              </div>
            </div>

            <div class="form-group">
              <label for="confirmPassword">Confirm Password</label>
              <input
                id="confirmPassword"
                v-model="form.confirmPassword"
                type="password"
                placeholder="Confirm your password"
                required
                class="form-input"
                :class="{ error: form.confirmPassword && !passwordsMatch }"
              >
              <div v-if="form.confirmPassword && !passwordsMatch" class="error-message">
                Passwords do not match
              </div>
            </div>

            <button 
              type="submit" 
              class="submit-btn" 
              :disabled="loading || !passwordsMatch"
            >
              <span v-if="loading" class="loading-spinner"></span>
              {{ loading ? 'Creating Account...' : 'Create Account' }}
            </button>

            <div class="divider">
              <span>or</span>
            </div>

            <button type="button" class="wechat-btn" @click="handleWechatRegister">
              Continue with WeChat
            </button>
          </form>

          <div class="auth-footer">
            <p>Already have an account? 
              <a @click="$router.push('/')" class="auth-link">Sign in</a>
            </p>
          </div>
        </div>
      </div>

      <!-- Right Side - Minimal Branding -->
      <div class="brand-section">
        <div class="brand-content">
          <h1 class="brand-title">DigiScrib</h1>
          <p class="brand-subtitle">AI-Powered Digit Recognition</p>
          <div class="feature-list">
            <div class="feature-item">Advanced AI Models</div>
            <div class="feature-item">Real-time Processing</div>
            <div class="feature-item">Secure & Private</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import authService from '@/services/authService'

export default {
  name: 'Register',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const showPassword = ref(false)
    const errorMessage = ref('')
    
    const form = ref({
      fullName: '',
      email: '',
      username: '',
      password: '',
      confirmPassword: ''
    })

    // Password strength calculator
    const passwordStrength = computed(() => {
      const password = form.value.password
      if (!password) return 0
      
      let strength = 0
      if (password.length >= 8) strength += 1
      if (/[A-Z]/.test(password)) strength += 1
      if (/[a-z]/.test(password)) strength += 1
      if (/[0-9]/.test(password)) strength += 1
      if (/[^A-Za-z0-9]/.test(password)) strength += 1
      
      return strength
    })

    const passwordStrengthClass = computed(() => {
      const strength = passwordStrength.value
      if (strength <= 2) return 'weak'
      if (strength <= 3) return 'medium'
      return 'strong'
    })

    const passwordStrengthText = computed(() => {
      const strength = passwordStrength.value
      if (strength <= 2) return 'Weak password'
      if (strength <= 3) return 'Medium strength'
      return 'Strong password'
    })

    const passwordsMatch = computed(() => {
      return form.value.password === form.value.confirmPassword
    })

    const handleRegister = async () => {
      if (!passwordsMatch.value) return
      
      loading.value = true
      errorMessage.value = ''
      
      try {
        const response = await authService.register({
          username: form.value.username,
          email: form.value.email,
          password: form.value.password
        })
        if (response.token) {
          localStorage.setItem('authToken', response.token)
          localStorage.setItem('user', JSON.stringify({
            username: response.username,
            email: response.email,
            role: response.role
          }))
        }
        router.push({ name: 'Home' })
      } catch (error) {
        errorMessage.value = error.message || 'Registration failed'
      } finally {
        loading.value = false
      }
    }

    const handleWechatRegister = () => {
      loading.value = true
      setTimeout(() => {
        loading.value = false
        router.push('/dashboard')
      }, 1500)
    }

    return {
      form,
      loading,
      showPassword,
      passwordStrengthClass,
      passwordStrengthText,
      passwordsMatch,
      handleRegister,
      handleWechatRegister,
      errorMessage
    }
  }
}
</script>

<style scoped>
.auth-container {
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
}

.auth-card {
  background: var(--surface);
  border-radius: 12px;
  box-shadow: var(--shadow-lg);
  display: grid;
  grid-template-columns: 1fr 1fr;
  min-height: 600px;
  overflow: hidden;
}

/* Form Section */
.form-section {
  padding: 60px 40px;
  display: flex;
  align-items: center;
}

.form-container {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.form-header {
  margin-bottom: 40px;
  text-align: center;
}

.form-header h2 {
  font-size: 32px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.form-header p {
  color: var(--text-secondary);
  font-size: 16px;
  line-height: 1.5;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.error-banner {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 13px;
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

.form-input {
  width: 100%;
  padding: 14px 16px;
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

.form-input.error {
  border-color: #ef4444;
}

.password-input-container {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--primary);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  padding: 4px 8px;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: var(--border);
  border-radius: 2px;
  overflow: hidden;
}

.strength-bar.weak {
  background: linear-gradient(90deg, #ef4444 0%, #ef4444 33%, var(--border) 33%);
}

.strength-bar.medium {
  background: linear-gradient(90deg, #f59e0b 0%, #f59e0b 66%, var(--border) 66%);
}

.strength-bar.strong {
  background: linear-gradient(90deg, #10b981 0%, #10b981 100%);
}

.strength-text {
  font-size: 12px;
  color: var(--text-secondary);
  min-width: 100px;
}

.strength-bar.weak + .strength-text {
  color: #ef4444;
}

.strength-bar.medium + .strength-text {
  color: #f59e0b;
}

.strength-bar.strong + .strength-text {
  color: #10b981;
}

.error-message {
  font-size: 12px;
  color: #ef4444;
  margin-top: 4px;
}

.submit-btn {
  width: 100%;
  padding: 16px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 8px;
}

.submit-btn:hover:not(:disabled) {
  background: var(--primary-dark);
  transform: translateY(-1px);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
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
  gap: 16px;
  color: var(--text-secondary);
  font-size: 14px;
  margin: 16px 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border);
}

.wechat-btn {
  width: 100%;
  padding: 14px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-primary);
  font-weight: 500;
}

.wechat-btn:hover {
  border-color: var(--primary);
  background: var(--primary-light);
}

.auth-footer {
  text-align: center;
  margin-top: 40px;
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

/* Brand Section */
.brand-section {
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: white;
  padding: 60px 48px;
  display: flex;
  align-items: center;
}

.brand-content {
  text-align: center;
  width: 100%;
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 12px;
}

.brand-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 48px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 200px;
  margin: 0 auto;
}

.feature-item {
  font-size: 16px;
  opacity: 0.9;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.feature-item:last-child {
  border-bottom: none;
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
    padding: 40px 24px;
  }
  
  .form-header h2 {
    font-size: 28px;
  }
}
</style>
