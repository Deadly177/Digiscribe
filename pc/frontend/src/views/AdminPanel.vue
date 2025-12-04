<template>
  <div class="admin-panel">
    <div class="page-header">
      <h1>{{ t('admin.title') }}</h1>
      <p>{{ t('admin.subtitle') }}</p>
    </div>

    <!-- Quick Stats -->
    <div class="admin-stats">
      <div class="stat-card">
        <div class="stat-icon users">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">{{ t('admin.totalUsers') }}</div>
          <div class="stat-change positive">+12 this week</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon predictions">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalPredictions.toLocaleString() }}</div>
          <div class="stat-label">{{ t('admin.totalPredictions') }}</div>
          <div class="stat-change positive">+1,234 today</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon accuracy">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.systemAccuracy }}%</div>
          <div class="stat-label">{{ t('admin.systemAccuracy') }}</div>
          <div class="stat-change positive">+2.1%</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon storage">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="8" y1="6" x2="21" y2="6"/>
            <line x1="8" y1="12" x2="21" y2="12"/>
            <line x1="8" y1="18" x2="21" y2="18"/>
            <line x1="3" y1="6" x2="3.01" y2="6"/>
            <line x1="3" y1="12" x2="3.01" y2="12"/>
            <line x1="3" y1="18" x2="3.01" y2="18"/>
          </svg>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.storageUsed }}MB</div>
          <div class="stat-label">{{ t('admin.storageUsed') }}</div>
          <div class="stat-change positive">{{ (stats.storageUsed / 100 * 100).toFixed(0) }}% of 100MB</div>
        </div>
      </div>
    </div>

    <!-- Admin Tabs -->
    <div class="admin-tabs">
      <div class="tabs-header">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          class="tab-btn"
          :class="{ active: activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          {{ tab.name }}
        </button>
      </div>

      <div class="tab-content">
        <!-- Users Management -->
        <div v-if="activeTab === 'users'" class="tab-pane">
          <div class="pane-header">
            <h3>{{ t('admin.userManagement') }}</h3>
            <div class="header-actions">
              <button class="btn-primary" @click="showAddUserModal = true">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"/>
                  <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                {{ t('admin.addUser') }}
              </button>
              <button class="btn-outline" @click="exportUsers">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                  <polyline points="7 10 12 15 17 10"/>
                  <line x1="12" y1="15" x2="12" y2="3"/>
                </svg>
                {{ t('admin.export') }}
              </button>
            </div>
          </div>

          <div class="users-table">
            <div class="table-header">
              <div class="table-row">
                <div class="table-cell">{{ t('admin.user') }}</div>
                <div class="table-cell">{{ t('admin.role') }}</div>
                <div class="table-cell">{{ t('admin.predictions') }}</div>
                <div class="table-cell">{{ t('admin.lastActive') }}</div>
                <div class="table-cell">{{ t('admin.status') }}</div>
                <div class="table-cell actions">{{ t('admin.actions') }}</div>
              </div>
            </div>
            <div class="table-body">
              <div 
                v-for="user in users" 
                :key="user.id"
                class="table-row"
              >
                <div class="table-cell user-info">
                  <div class="user-avatar">
                    {{ getUserInitials(user.name) }}
                  </div>
                  <div class="user-details">
                    <div class="user-name">{{ user.name }}</div>
                    <div class="user-email">{{ user.email }}</div>
                  </div>
                </div>
                <div class="table-cell">
                  <span class="role-badge" :class="user.role">
                    {{ user.role }}
                  </span>
                </div>
                <div class="table-cell">
                  {{ user.predictions.toLocaleString() }}
                </div>
                <div class="table-cell">
                  {{ formatTime(user.lastActive) }}
                </div>
                <div class="table-cell">
                  <span class="status-badge" :class="user.status">
                    {{ user.status }}
                  </span>
                </div>
                <div class="table-cell actions">
                  <div class="action-buttons">
                    <button class="action-btn edit" @click="editUser(user)">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                      </svg>
                    </button>
                    <button 
                      v-if="user.role !== 'admin'" 
                      class="action-btn delete"
                      @click="deleteUser(user)"
                    >
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="3 6 5 6 21 6"/>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- System Health -->
        <div v-if="activeTab === 'system'" class="tab-pane">
          <div class="pane-header">
            <h3>{{ t('admin.systemHealth') }}</h3>
            <button class="btn-outline" @click="refreshSystemHealth">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="23 4 23 10 17 10"/>
                <polyline points="1 20 1 14 7 14"/>
                <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
              </svg>
              Refresh
            </button>
          </div>

          <div class="system-health-grid">
            <div class="health-card">
              <h4>{{ t('admin.serverStatus') }}</h4>
              <div class="health-status">
                <div class="status-indicator online"></div>
                <span>{{ t('admin.allSystemsOperational') }}</span>
              </div>
              <div class="health-metrics">
                <div class="metric">
                  <span>{{ t('admin.cpuUsage') }}</span>
                  <strong>{{ systemHealth.cpu }}%</strong>
                </div>
                <div class="metric">
                  <span>{{ t('admin.memoryUsage') }}</span>
                  <strong>{{ systemHealth.memory }}%</strong>
                </div>
                <div class="metric">
                  <span>{{ t('admin.diskUsage') }}</span>
                  <strong>{{ systemHealth.disk }}%</strong>
                </div>
              </div>
            </div>

            <div class="health-card">
              <h4>{{ t('admin.apiPerformance') }}</h4>
              <div class="performance-metrics">
                <div class="metric">
                  <span>{{ t('admin.responseTime') }}</span>
                  <strong>{{ systemHealth.responseTime }}ms</strong>
                </div>
                <div class="metric">
                  <span>{{ t('admin.uptime') }}</span>
                  <strong>{{ systemHealth.uptime }}</strong>
                </div>
                <div class="metric">
                  <span>{{ t('admin.errorRate') }}</span>
                  <strong>{{ systemHealth.errorRate }}%</strong>
                </div>
              </div>
            </div>

            <div class="health-card">
              <h4>{{ t('admin.databaseStatus') }}</h4>
              <div class="database-stats">
                <div class="stat">
                  <span>{{ t('admin.connections') }}</span>
                  <strong>{{ systemHealth.dbConnections }}</strong>
                </div>
                <div class="stat">
                  <span>{{ t('admin.queryTime') }}</span>
                  <strong>{{ systemHealth.dbQueryTime }}ms</strong>
                </div>
                <div class="stat">
                  <span>{{ t('admin.size') }}</span>
                  <strong>{{ systemHealth.dbSize }}MB</strong>
                </div>
              </div>
            </div>

            <div class="health-card">
              <h4>{{ t('admin.recentEvents') }}</h4>
              <div class="events-list">
                <div 
                  v-for="event in systemEvents" 
                  :key="event.id"
                  class="event-item"
                  :class="event.type"
                >
                  <div class="event-icon">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="12" cy="12" r="10"/>
                      <line x1="12" y1="8" x2="12" y2="12"/>
                      <line x1="12" y1="16" x2="12.01" y2="16"/>
                    </svg>
                  </div>
                  <div class="event-details">
                    <div class="event-message">{{ event.message }}</div>
                    <div class="event-time">{{ formatTime(event.timestamp) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Mobile Users -->
        <div v-if="activeTab === 'mobileUsers'" class="tab-pane">
          <div class="pane-header">
            <h3>Mobile Users</h3>
            <button class="btn-outline" @click="loadMobileUsers">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="23 4 23 10 17 10"/>
                <polyline points="1 20 1 14 7 14"/>
                <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
              </svg>
              Refresh
            </button>
          </div>

          <div class="users-table">
            <div class="table-header">
              <div class="table-row mobile-row">
                <div class="table-cell">User</div>
                <div class="table-cell">Email</div>
                <div class="table-cell">Registered</div>
                <div class="table-cell">Status</div>
                <div class="table-cell actions">Actions</div>
              </div>
            </div>
            <div class="table-body">
              <div 
                v-for="user in mobileUsers" 
                :key="user.id"
                class="table-row mobile-row"
              >
                <div class="table-cell user-info">
                  <div class="user-avatar">
                    {{ getUserInitials(user.username) }}
                  </div>
                  <div class="user-details">
                    <div class="user-name">{{ user.username }}</div>
                  </div>
                </div>
                <div class="table-cell">
                  {{ user.email }}
                </div>
                <div class="table-cell">
                  {{ formatTime(user.createdAt) }}
                </div>
                <div class="table-cell">
                  <span class="status-badge active">Active</span>
                </div>
                <div class="table-cell actions">
                  <div class="action-buttons">
                    <button class="action-btn delete" @click="deleteMobileUser(user)">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="3 6 5 6 21 6"/>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
              <div v-if="mobileUsers.length === 0" class="empty-state">
                <p>No mobile users registered yet</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Model Management -->
        <div v-if="activeTab === 'models'" class="tab-pane">
          <div class="pane-header">
            <h3>{{ t('admin.modelManagement') }}</h3>
            <button class="btn-primary" @click="deployNewModel">
              {{ t('admin.deployNewModel') }}
            </button>
          </div>

          <div class="models-grid">
            <div 
              v-for="model in adminModels" 
              :key="model.id"
              class="model-admin-card"
            >
              <div class="model-header">
                <h4>{{ model.name }}</h4>
                <span class="model-version">v{{ model.version }}</span>
              </div>
              
              <div class="model-stats">
                <div class="stat">
                  <span>{{ t('admin.accuracy') }}</span>
                  <strong>{{ model.accuracy }}%</strong>
                </div>
                <div class="stat">
                  <span>{{ t('admin.predictions') }}</span>
                  <strong>{{ model.predictions.toLocaleString() }}</strong>
                </div>
                <div class="stat">
                  <span>{{ t('admin.size') }}</span>
                  <strong>{{ model.size }}MB</strong>
                </div>
              </div>

              <div class="model-actions">
                <button 
                  class="btn-primary"
                  :class="{ 'btn-secondary': model.status === 'active' }"
                  @click="toggleModelStatus(model)"
                >
                  {{ model.status === 'active' ? t('admin.active') : t('admin.activate') }}
                </button>
                <button class="btn-outline" @click="retrainModel(model)">
                  {{ t('admin.retrain') }}
                </button>
                <button class="btn-outline danger" @click="deleteModel(model)">
                  {{ t('admin.delete') }}
                </button>
              </div>

              <div class="model-meta">
                <span>{{ t('admin.deployed') }}: {{ formatTime(model.deployedAt) }}</span>
                <span>{{ t('admin.lastTraining') }}: {{ formatTime(model.lastTrained) }}</span>
              </div>
            </div>
          </div>
        </div>


      </div>
    </div>

    <!-- Add User Modal -->
    <div v-if="showAddUserModal" class="modal-overlay" @click="showAddUserModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ t('admin.addNewUser') }}</h3>
          <button class="modal-close" @click="showAddUserModal = false">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="userFormError" class="error-message">{{ userFormError }}</div>
          <form @submit.prevent="addNewUser">
            <div class="form-group">
              <label>{{ t('admin.username') }}</label>
              <input v-model="newUser.username" type="text" class="form-input" required>
            </div>
            <div class="form-group">
              <label>{{ t('admin.emailAddress') }}</label>
              <input v-model="newUser.email" type="email" class="form-input" required>
            </div>
            <div class="form-group">
              <label>{{ t('admin.newPassword') }}</label>
              <input v-model="newUser.password" type="password" class="form-input" required>
            </div>
            <div class="form-group">
              <label>{{ t('admin.confirmPassword') }}</label>
              <input v-model="newUser.confirmPassword" type="password" class="form-input" required>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn-outline" @click="showAddUserModal = false">{{ t('admin.cancel') }}</button>
              <button type="submit" class="btn-primary">{{ t('admin.createUser') }}</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/services/api'
import { useI18n } from '@/i18n'

export default {
  name: 'AdminPanel',
  setup() {
    const router = useRouter()
    const { t } = useI18n()
    const activeTab = ref('users')
    const showAddUserModal = ref(false)
    const userFormError = ref('')
    const newUser = ref({
      username: '',
      email: '',
      password: '',
      confirmPassword: ''
    })
    
    const tabs = computed(() => [
      { id: 'users', name: t('admin.users') },
      { id: 'system', name: t('admin.systemHealth') },
      { id: 'models', name: t('admin.models') },
      { id: 'mobileUsers', name: 'Mobile Users' }
    ])

    // Stats - match Home.vue approach
    const stats = computed(() => {
      // Use model prediction counts like Home.vue
      const modelTotalPredictions = adminModels.value.reduce((sum, m) => sum + (m.prediction_count || m.predictions || 0), 0)
      
      const predictions = JSON.parse(localStorage.getItem('digit_recognition_recent_predictions') || '[]')
      const correct = predictions.filter(p => p.correct === true).length
      const accuracy = predictions.length > 0 ? ((correct / predictions.length) * 100).toFixed(1) : 0
      const storageSizeKB = new Blob([JSON.stringify(predictions)]).size / 1024
      const storageMB = (storageSizeKB / 1024).toFixed(2)
      
      return {
        totalUsers: 1,
        totalPredictions: modelTotalPredictions || predictions.length,
        systemAccuracy: parseFloat(accuracy),
        storageUsed: parseFloat(storageMB)
      }
    })

    const users = computed(() => {
      const predictions = JSON.parse(localStorage.getItem('digit_recognition_recent_predictions') || '[]')
      const lastPrediction = predictions.length > 0 ? new Date(predictions[0].timestamp) : new Date()
      const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
      
      return [
        {
          id: 1,
          name: currentUser.username || 'Current User',
          email: currentUser.email || 'user@digiscribe.com',
          role: currentUser.role === 'ADMIN' ? 'admin' : 'user',
          predictions: predictions.length,
          lastActive: lastPrediction,
          status: 'active'
        }
      ]
    })

    const systemHealth = computed(() => {
      const predictions = JSON.parse(localStorage.getItem('digit_recognition_recent_predictions') || '[]')
      const errors = predictions.filter(p => p.correct === false).length
      const errorRate = predictions.length > 0 ? ((errors / predictions.length) * 100).toFixed(1) : 0
      const avgResponseTime = predictions.length > 0 
        ? Math.round(predictions.reduce((sum, p) => sum + (p.processingTime || 50), 0) / predictions.length)
        : 45
      const storageMB = (new Blob([JSON.stringify(predictions)]).size / 1024 / 1024).toFixed(1)
      
      return {
        cpu: 35,
        memory: 52,
        disk: Math.min(Math.round((parseFloat(storageMB) / 100) * 100), 100),
        responseTime: avgResponseTime,
        uptime: '99.9%',
        errorRate: parseFloat(errorRate),
        dbConnections: 1,
        dbQueryTime: 8,
        dbSize: parseFloat(storageMB)
      }
    })

    const systemEvents = computed(() => {
      const predictions = JSON.parse(localStorage.getItem('digit_recognition_recent_predictions') || '[]')
      const events = []
      
      if (predictions.length > 0) {
        events.push({
          id: 1,
          type: 'success',
          message: `Latest prediction completed successfully`,
          timestamp: new Date(predictions[0].timestamp)
        })
      }
      
      const errors = predictions.filter(p => p.correct === false)
      if (errors.length > 0) {
        events.push({
          id: 2,
          type: 'warning',
          message: `${errors.length} prediction errors detected`,
          timestamp: new Date(errors[0].timestamp)
        })
      }
      
      events.push({
        id: 3,
        type: 'info',
        message: 'System running normally',
        timestamp: new Date()
      })
      
      return events
    })

    const mobileUsers = ref([])
    const adminModels = ref([])
    
    const loadMobileUsers = async () => {
      try {
        const response = await api.get('/auth/users')
        mobileUsers.value = response.data.map(user => ({
          ...user,
          id: user.id || user.username,
          createdAt: user.createdAt || user.created_at || new Date()
        }))
      } catch (error) {
        console.error('Failed to load mobile users from API, checking localStorage:', error)
        const localUsers = JSON.parse(localStorage.getItem('system_users') || '[]')
        mobileUsers.value = localUsers.map((user, index) => ({
          id: index + 1,
          username: user.username,
          email: user.email,
          createdAt: user.createdAt || new Date()
        }))
      }
    }
    
    const loadModels = async () => {
      try {
        const response = await api.get('/models')
        const predictions = JSON.parse(localStorage.getItem('digit_recognition_recent_predictions') || '[]')
        
        adminModels.value = response.data.map(model => ({
          ...model,
          predictions: model.status === 'active' ? predictions.length : model.prediction_count || 0,
          deployedAt: model.created_at || new Date(Date.now() - 86400000),
          lastTrained: model.last_trained || new Date(Date.now() - 172800000)
        }))
      } catch (error) {
        const predictions = JSON.parse(localStorage.getItem('digit_recognition_recent_predictions') || '[]')
        const correct = predictions.filter(p => p.correct === true).length
        const accuracy = predictions.length > 0 ? ((correct / predictions.length) * 100).toFixed(1) : 0
        
        adminModels.value = [
          {
            id: 1,
            name: 'CNN Basic',
            version: '1.0',
            accuracy: parseFloat(accuracy),
            predictions: predictions.length,
            size: 45,
            status: 'active',
            deployedAt: new Date(Date.now() - 86400000),
            lastTrained: new Date(Date.now() - 172800000)
          }
        ]
      }
    }



    // Methods
    const getUserInitials = (name) => {
      return name.split(' ').map(n => n[0]).join('').toUpperCase()
    }

    const formatTime = (timestamp) => {
      const now = new Date()
      const diff = now - new Date(timestamp)
      const days = Math.floor(diff / 86400000)
      const hours = Math.floor(diff / 3600000)
      const minutes = Math.floor(diff / 60000)
      
      if (days > 0) return `${days}d ago`
      if (hours > 0) return `${hours}h ago`
      if (minutes > 0) return `${minutes}m ago`
      return 'Just now'
    }

    const editUser = (user) => {
      console.log('Edit user:', user)
    }

    const deleteUser = (user) => {
      if (confirm(`Are you sure you want to delete ${user.name}?`)) {
        users.value = users.value.filter(u => u.id !== user.id)
      }
    }

    const exportUsers = () => {
      console.log('Exporting users data')
    }

    const refreshSystemHealth = () => {
      console.log('Refreshing system health')
    }

    const toggleModelStatus = (model) => {
      model.status = model.status === 'active' ? 'idle' : 'active'
    }

    const retrainModel = (model) => {
      console.log('Retraining model:', model.name)
    }

    const deleteModel = (model) => {
      if (confirm(`Are you sure you want to delete ${model.name}?`)) {
        adminModels.value = adminModels.value.filter(m => m.id !== model.id)
      }
    }

    const deleteMobileUser = async (user) => {
      if (!confirm(`Are you sure you want to delete ${user.username}?`)) return
      
      try {
        await api.delete(`/auth/users/${user.id}`)
        mobileUsers.value = mobileUsers.value.filter(u => u.id !== user.id)
      } catch (error) {
        console.error('Failed to delete from API, removing from localStorage:', error)
        const localUsers = JSON.parse(localStorage.getItem('system_users') || '[]')
        const filtered = localUsers.filter(u => u.username !== user.username)
        localStorage.setItem('system_users', JSON.stringify(filtered))
        mobileUsers.value = mobileUsers.value.filter(u => u.id !== user.id)
      }
    }

    const deployNewModel = () => {
      router.push('/dashboard/models')
    }
    
    const addNewUser = () => {
      userFormError.value = ''
      
      if (newUser.value.password !== newUser.value.confirmPassword) {
        userFormError.value = 'Passwords do not match'
        return
      }
      
      if (newUser.value.password.length < 6) {
        userFormError.value = 'Password must be at least 6 characters'
        return
      }
      
      const users = JSON.parse(localStorage.getItem('system_users') || '[]')
      
      if (users.find(u => u.username === newUser.value.username)) {
        userFormError.value = 'Username already exists'
        return
      }
      
      users.push({
        username: newUser.value.username,
        email: newUser.value.email,
        password: newUser.value.password,
        createdAt: new Date().toISOString()
      })
      
      localStorage.setItem('system_users', JSON.stringify(users))
      
      newUser.value = {
        username: '',
        email: '',
        password: '',
        confirmPassword: ''
      }
      
      showAddUserModal.value = false
      alert('User created successfully!')
    }



    onMounted(() => {
      loadModels()
      loadMobileUsers()
    })

    return {
      t,
      activeTab,
      tabs,
      stats,
      users,
      mobileUsers,
      systemHealth,
      systemEvents,
      adminModels,
      showAddUserModal,
      userFormError,
      newUser,
      addNewUser,
      loadMobileUsers,
      getUserInitials,
      formatTime,
      editUser,
      deleteUser,
      exportUsers,
      refreshSystemHealth,
      toggleModelStatus,
      retrainModel,
      deleteModel,
      deleteMobileUser,
      deployNewModel
    }
  }
}
</script>

<style scoped>
.admin-panel {
  padding: 0;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.page-header p {
  color: var(--text-secondary);
  font-size: 16px;
}

/* Admin Stats */
.admin-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: var(--surface);
  border-radius: 12px;
  padding: 24px;
  box-shadow: var(--shadow);
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.users {
  background: #dbeafe;
  color: #1d4ed8;
}

.stat-icon.predictions {
  background: #f0fdf4;
  color: #059669;
}

.stat-icon.accuracy {
  background: #fef7cd;
  color: #d97706;
}

.stat-icon.storage {
  background: #f3f4f6;
  color: #374151;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.stat-change {
  font-size: 12px;
  font-weight: 500;
}

.stat-change.positive {
  color: #059669;
}

.stat-change.warning {
  color: #f59e0b;
}

/* Admin Tabs */
.admin-tabs {
  background: var(--surface);
  border-radius: 12px;
  box-shadow: var(--shadow);
  overflow: hidden;
}

.tabs-header {
  display: flex;
  border-bottom: 1px solid var(--border);
  background: var(--tab-bg);
}

.tab-btn {
  padding: 16px 24px;
  background: none;
  border: none;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 2px solid transparent;
}

.tab-btn:hover {
  color: var(--text-primary);
  background: var(--background);
}

.tab-btn.active {
  color: var(--success);
  border-bottom-color: var(--tab-active-border);
  background: var(--tab-active-bg);
}

.tab-content {
  padding: 0;
  background: var(--tab-bg);
}

.tab-pane {
  padding: 0;
  background: var(--tab-bg);
}

/* Pane Header */
.pane-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid var(--border);
}

.pane-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* Buttons */
.btn-primary, .btn-secondary, .btn-outline {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary {
  background: #059669;
  color: white;
  border: none;
}

.btn-primary:hover {
  background: #047857;
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
  border: none;
}

.btn-outline {
  background: white;
  color: #475569;
  border: 1px solid #d1d5db;
}

.btn-outline:hover {
  border-color: #059669;
  color: #059669;
}

.btn-outline.danger {
  color: #ef4444;
  border-color: #ef4444;
}

.btn-outline.danger:hover {
  background: #fef2f2;
}

/* Users Table */
.users-table {
  padding: 0;
}

.table-header {
  background: var(--background);
  border-bottom: 1px solid var(--border);
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr;
  gap: 16px;
  padding: 16px 24px;
  align-items: center;
}

.table-row.mobile-row {
  grid-template-columns: 2fr 2fr 1fr 1fr 1fr;
}

.table-body .table-row {
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}

.table-cell {
  font-size: 14px;
  color: var(--text-primary);
}

.table-cell.actions {
  text-align: right;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #059669, #047857);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 12px;
}

.user-details .user-name {
  font-weight: 500;
  color: var(--text-primary);
}

.user-details .user-email {
  font-size: 12px;
  color: var(--text-secondary);
}

.role-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  text-transform: capitalize;
}

.role-badge.admin {
  background: #fef3c7;
  color: #92400e;
}

.role-badge.user {
  background: #f1f5f9;
  color: #475569;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  text-transform: capitalize;
}

.status-badge.active {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.inactive {
  background: #f1f5f9;
  color: #475569;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.action-btn {
  background: none;
  border: none;
  padding: 6px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn.edit {
  color: #059669;
}

.action-btn.edit:hover {
  background: #f0fdf4;
}

.action-btn.delete {
  color: #ef4444;
}

.action-btn.delete:hover {
  background: #fef2f2;
}

/* System Health */
.system-health-grid {
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.health-card {
  background: var(--surface);
  border-radius: 8px;
  padding: 20px;
  border: 1px solid var(--border);
}

.health-card h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.health-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-indicator.online {
  background: #059669;
}

.health-metrics, .performance-metrics, .database-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.metric, .stat {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid var(--border);
}

.metric:last-child, .stat:last-child {
  border-bottom: none;
}

.metric span, .stat span {
  color: var(--text-secondary);
  font-size: 14px;
}

.metric strong, .stat strong {
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 600;
}

/* Events List */
.events-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.event-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  background: var(--surface);
  border-left: 4px solid var(--border);
}

.event-item.info {
  border-left-color: #3b82f6;
}

.event-item.warning {
  border-left-color: #f59e0b;
}

.event-item.success {
  border-left-color: #059669;
}

.event-icon {
  color: #64748b;
  margin-top: 2px;
}

.event-details {
  flex: 1;
}

.event-message {
  font-size: 14px;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.event-time {
  font-size: 12px;
  color: var(--text-secondary);
}

/* Model Management */
.models-grid {
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.model-admin-card {
  background: var(--surface);
  border-radius: 8px;
  padding: 20px;
  border: 2px solid var(--border);
}

.model-admin-card:hover {
  border-color: var(--success);
}

.model-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.model-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.model-version {
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--background);
  padding: 2px 6px;
  border-radius: 4px;
}

.model-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.model-stats .stat {
  text-align: center;
  border: none;
  padding: 0;
}

.model-stats .stat span {
  display: block;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 4px;
}

.model-stats .stat strong {
  display: block;
  font-size: 14px;
  color: #059669;
}

.model-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.model-actions .btn-primary,
.model-actions .btn-secondary,
.model-actions .btn-outline {
  flex: 1;
  font-size: 12px;
  padding: 6px 8px;
}

.model-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: var(--text-secondary);
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: var(--surface);
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.modal-close {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: var(--text-secondary);
  border-radius: 4px;
}

.modal-close:hover {
  background: var(--background);
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 14px;
  background: var(--surface);
  color: var(--text-primary);
  transition: all 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: var(--success);
  box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.1);
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.error-message {
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
  border-radius: 6px;
  padding: 10px 12px;
  margin-bottom: 16px;
  font-size: 13px;
}

.empty-state {
  padding: 48px 24px;
  text-align: center;
  color: var(--text-secondary);
  font-size: 14px;
}

/* Responsive */
@media (max-width: 768px) {
  .admin-stats {
    grid-template-columns: 1fr;
  }
  
  .tabs-header {
    flex-wrap: wrap;
  }
  
  .tab-btn {
    flex: 1;
    min-width: 120px;
  }
  
  .table-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .system-health-grid,
  .models-grid {
    grid-template-columns: 1fr;
  }
  
  .pane-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    justify-content: stretch;
  }
  
  .header-actions .btn-primary,
  .header-actions .btn-outline {
    flex: 1;
  }
}
</style>