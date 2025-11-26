<template>
  <div class="admin-panel">
    <div class="page-header">
      <h1>Admin Panel</h1>
      <p>Manage users, monitor system health, and configure settings</p>
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
          <div class="stat-label">Total Users</div>
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
          <div class="stat-label">Total Predictions</div>
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
          <div class="stat-label">System Accuracy</div>
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
          <div class="stat-value">{{ stats.storageUsed }}GB</div>
          <div class="stat-label">Storage Used</div>
          <div class="stat-change warning">65% full</div>
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
            <h3>User Management</h3>
            <div class="header-actions">
              <button class="btn-primary" @click="showAddUserModal = true">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"/>
                  <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                Add User
              </button>
              <button class="btn-outline" @click="exportUsers">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                  <polyline points="7 10 12 15 17 10"/>
                  <line x1="12" y1="15" x2="12" y2="3"/>
                </svg>
                Export
              </button>
            </div>
          </div>

          <div class="users-table">
            <div class="table-header">
              <div class="table-row">
                <div class="table-cell">User</div>
                <div class="table-cell">Role</div>
                <div class="table-cell">Predictions</div>
                <div class="table-cell">Last Active</div>
                <div class="table-cell">Status</div>
                <div class="table-cell actions">Actions</div>
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
            <h3>System Health</h3>
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
              <h4>Server Status</h4>
              <div class="health-status">
                <div class="status-indicator online"></div>
                <span>All Systems Operational</span>
              </div>
              <div class="health-metrics">
                <div class="metric">
                  <span>CPU Usage</span>
                  <strong>{{ systemHealth.cpu }}%</strong>
                </div>
                <div class="metric">
                  <span>Memory Usage</span>
                  <strong>{{ systemHealth.memory }}%</strong>
                </div>
                <div class="metric">
                  <span>Disk Usage</span>
                  <strong>{{ systemHealth.disk }}%</strong>
                </div>
              </div>
            </div>

            <div class="health-card">
              <h4>API Performance</h4>
              <div class="performance-metrics">
                <div class="metric">
                  <span>Response Time</span>
                  <strong>{{ systemHealth.responseTime }}ms</strong>
                </div>
                <div class="metric">
                  <span>Uptime</span>
                  <strong>{{ systemHealth.uptime }}</strong>
                </div>
                <div class="metric">
                  <span>Error Rate</span>
                  <strong>{{ systemHealth.errorRate }}%</strong>
                </div>
              </div>
            </div>

            <div class="health-card">
              <h4>Database Status</h4>
              <div class="database-stats">
                <div class="stat">
                  <span>Connections</span>
                  <strong>{{ systemHealth.dbConnections }}</strong>
                </div>
                <div class="stat">
                  <span>Query Time</span>
                  <strong>{{ systemHealth.dbQueryTime }}ms</strong>
                </div>
                <div class="stat">
                  <span>Size</span>
                  <strong>{{ systemHealth.dbSize }}MB</strong>
                </div>
              </div>
            </div>

            <div class="health-card">
              <h4>Recent Events</h4>
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

        <!-- Model Management -->
        <div v-if="activeTab === 'models'" class="tab-pane">
          <div class="pane-header">
            <h3>Model Management</h3>
            <button class="btn-primary" @click="deployNewModel">
              Deploy New Model
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
                  <span>Accuracy</span>
                  <strong>{{ model.accuracy }}%</strong>
                </div>
                <div class="stat">
                  <span>Predictions</span>
                  <strong>{{ model.predictions.toLocaleString() }}</strong>
                </div>
                <div class="stat">
                  <span>Size</span>
                  <strong>{{ model.size }}MB</strong>
                </div>
              </div>

              <div class="model-actions">
                <button 
                  class="btn-primary"
                  :class="{ 'btn-secondary': model.status === 'active' }"
                  @click="toggleModelStatus(model)"
                >
                  {{ model.status === 'active' ? 'Active' : 'Activate' }}
                </button>
                <button class="btn-outline" @click="retrainModel(model)">
                  Retrain
                </button>
                <button class="btn-outline danger" @click="deleteModel(model)">
                  Delete
                </button>
              </div>

              <div class="model-meta">
                <span>Deployed: {{ formatTime(model.deployedAt) }}</span>
                <span>Last Training: {{ formatTime(model.lastTrained) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- System Settings -->
        <div v-if="activeTab === 'settings'" class="tab-pane">
          <div class="pane-header">
            <h3>System Settings</h3>
            <button class="btn-primary" @click="saveSettings">
              Save Settings
            </button>
          </div>

          <div class="settings-grid">
            <div class="settings-section">
              <h4>General Settings</h4>
              <div class="setting-item">
                <label>Application Name</label>
                <input 
                  v-model="settings.appName" 
                  type="text" 
                  class="form-input"
                >
              </div>
              <div class="setting-item">
                <label>Maximum File Size</label>
                <input 
                  v-model="settings.maxFileSize" 
                  type="number" 
                  class="form-input"
                >
                <span class="setting-hint">MB</span>
              </div>
              <div class="setting-item">
                <label class="checkbox-container">
                  <input type="checkbox" v-model="settings.allowRegistrations">
                  <span class="checkmark"></span>
                  Allow new user registrations
                </label>
              </div>
            </div>

            <div class="settings-section">
              <h4>Model Settings</h4>
              <div class="setting-item">
                <label>Default Confidence Threshold</label>
                <input 
                  v-model="settings.confidenceThreshold" 
                  type="number" 
                  min="0"
                  max="100"
                  class="form-input"
                >
                <span class="setting-hint">%</span>
              </div>
              <div class="setting-item">
                <label>Auto-retrain Interval</label>
                <select v-model="settings.retrainInterval" class="form-input">
                  <option value="weekly">Weekly</option>
                  <option value="monthly">Monthly</option>
                  <option value="quarterly">Quarterly</option>
                  <option value="never">Never</option>
                </select>
              </div>
            </div>

            <div class="settings-section">
              <h4>Security Settings</h4>
              <div class="setting-item">
                <label>Session Timeout</label>
                <input 
                  v-model="settings.sessionTimeout" 
                  type="number" 
                  class="form-input"
                >
                <span class="setting-hint">minutes</span>
              </div>
              <div class="setting-item">
                <label>Max Login Attempts</label>
                <input 
                  v-model="settings.maxLoginAttempts" 
                  type="number" 
                  class="form-input"
                >
              </div>
              <div class="setting-item">
                <label class="checkbox-container">
                  <input type="checkbox" v-model="settings.requireEmailVerification">
                  <span class="checkmark"></span>
                  Require email verification
                </label>
              </div>
            </div>

            <div class="settings-section">
              <h4>Backup & Maintenance</h4>
              <div class="setting-item">
                <label>Auto-backup Frequency</label>
                <select v-model="settings.backupFrequency" class="form-input">
                  <option value="daily">Daily</option>
                  <option value="weekly">Weekly</option>
                  <option value="monthly">Monthly</option>
                </select>
              </div>
              <div class="setting-item">
                <label>Retain Backups For</label>
                <input 
                  v-model="settings.retainBackups" 
                  type="number" 
                  class="form-input"
                >
                <span class="setting-hint">days</span>
              </div>
              <div class="setting-actions">
                <button class="btn-outline" @click="createBackup">
                  Create Backup Now
                </button>
                <button class="btn-outline" @click="runMaintenance">
                  Run Maintenance
                </button>
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
          <h3>Add New User</h3>
          <button class="modal-close" @click="showAddUserModal = false">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/>
              <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <!-- Add user form would go here -->
          <p>User creation form would be implemented here.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'AdminPanel',
  setup() {
    const activeTab = ref('users')
    const showAddUserModal = ref(false)

    const tabs = [
      { id: 'users', name: 'Users' },
      { id: 'system', name: 'System Health' },
      { id: 'models', name: 'Models' },
      { id: 'settings', name: 'Settings' }
    ]

    // Mock data
    const stats = ref({
      totalUsers: 142,
      totalPredictions: 15420,
      systemAccuracy: 98.3,
      storageUsed: 12.4
    })

    const users = ref([
      {
        id: 1,
        name: 'John Doe',
        email: 'john.doe@example.com',
        role: 'admin',
        predictions: 2450,
        lastActive: new Date(Date.now() - 3600000),
        status: 'active'
      },
      {
        id: 2,
        name: 'Jane Smith',
        email: 'jane.smith@example.com',
        role: 'user',
        predictions: 1200,
        lastActive: new Date(Date.now() - 86400000),
        status: 'active'
      },
      {
        id: 3,
        name: 'Bob Johnson',
        email: 'bob.johnson@example.com',
        role: 'user',
        predictions: 560,
        lastActive: new Date(Date.now() - 259200000),
        status: 'inactive'
      }
    ])

    const systemHealth = ref({
      cpu: 45,
      memory: 68,
      disk: 65,
      responseTime: 45,
      uptime: '99.9%',
      errorRate: 0.2,
      dbConnections: 24,
      dbQueryTime: 12,
      dbSize: 245
    })

    const systemEvents = ref([
      {
        id: 1,
        type: 'info',
        message: 'System backup completed successfully',
        timestamp: new Date(Date.now() - 3600000)
      },
      {
        id: 2,
        type: 'warning',
        message: 'High memory usage detected',
        timestamp: new Date(Date.now() - 7200000)
      },
      {
        id: 3,
        type: 'success',
        message: 'New model deployed successfully',
        timestamp: new Date(Date.now() - 86400000)
      }
    ])

    const adminModels = ref([
      {
        id: 1,
        name: 'CNN Basic',
        version: '1.2',
        accuracy: 98.2,
        predictions: 15420,
        size: 45,
        status: 'active',
        deployedAt: new Date(Date.now() - 86400000),
        lastTrained: new Date(Date.now() - 172800000)
      },
      {
        id: 2,
        name: 'ResNet Enhanced',
        version: '2.1',
        accuracy: 99.1,
        predictions: 8920,
        size: 120,
        status: 'idle',
        deployedAt: new Date(Date.now() - 172800000),
        lastTrained: new Date(Date.now() - 259200000)
      }
    ])

    const settings = ref({
      appName: 'DigiScrib',
      maxFileSize: 10,
      allowRegistrations: true,
      confidenceThreshold: 80,
      retrainInterval: 'weekly',
      sessionTimeout: 30,
      maxLoginAttempts: 5,
      requireEmailVerification: true,
      backupFrequency: 'weekly',
      retainBackups: 30
    })

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

    const deployNewModel = () => {
      console.log('Deploying new model')
    }

    const saveSettings = () => {
      console.log('Saving settings:', settings.value)
      alert('Settings saved successfully!')
    }

    const createBackup = () => {
      console.log('Creating backup')
    }

    const runMaintenance = () => {
      console.log('Running maintenance')
    }

    onMounted(() => {
      // In a real app, this would fetch admin data
      console.log('Loading admin panel data')
    })

    return {
      activeTab,
      tabs,
      stats,
      users,
      systemHealth,
      systemEvents,
      adminModels,
      settings,
      showAddUserModal,
      getUserInitials,
      formatTime,
      editUser,
      deleteUser,
      exportUsers,
      refreshSystemHealth,
      toggleModelStatus,
      retrainModel,
      deleteModel,
      deployNewModel,
      saveSettings,
      createBackup,
      runMaintenance
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
  color: #1e293b;
  margin-bottom: 8px;
}

.page-header p {
  color: #64748b;
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
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
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
  color: #1e293b;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
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
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
  overflow: hidden;
}

.tabs-header {
  display: flex;
  border-bottom: 1px solid #e5e7eb;
  background: #f8fafc;
}

.tab-btn {
  padding: 16px 24px;
  background: none;
  border: none;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 2px solid transparent;
}

.tab-btn:hover {
  color: #374151;
  background: #f1f5f9;
}

.tab-btn.active {
  color: #059669;
  border-bottom-color: #059669;
  background: white;
}

.tab-content {
  padding: 0;
}

.tab-pane {
  padding: 0;
}

/* Pane Header */
.pane-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.pane-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
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
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr;
  gap: 16px;
  padding: 16px 24px;
  align-items: center;
}

.table-cell {
  font-size: 14px;
  color: #374151;
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
  color: #1e293b;
}

.user-details .user-email {
  font-size: 12px;
  color: #64748b;
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
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
}

.health-card h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
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
  border-bottom: 1px solid #e5e7eb;
}

.metric:last-child, .stat:last-child {
  border-bottom: none;
}

.metric span, .stat span {
  color: #64748b;
  font-size: 14px;
}

.metric strong, .stat strong {
  color: #1e293b;
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
  background: white;
  border-left: 4px solid #e5e7eb;
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
  color: #374151;
  margin-bottom: 4px;
}

.event-time {
  font-size: 12px;
  color: #64748b;
}

/* Model Management */
.models-grid {
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}

.model-admin-card {
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
  border: 2px solid transparent;
}

.model-admin-card:hover {
  border-color: #059669;
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
  color: #1e293b;
  margin: 0;
}

.model-version {
  font-size: 12px;
  color: #64748b;
  background: white;
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
  color: #64748b;
}

/* Settings */
.settings-grid {
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 32px;
}

.settings-section {
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
}

.settings-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
}

.setting-item {
  margin-bottom: 16px;
}

.setting-item label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.form-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: #059669;
  box-shadow: 0 0 0 3px rgba(5, 150, 105, 0.1);
}

.setting-hint {
  font-size: 12px;
  color: #64748b;
  margin-left: 8px;
}

.checkbox-container {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
}

.checkbox-container input {
  display: none;
}

.checkmark {
  width: 16px;
  height: 16px;
  border: 2px solid #d1d5db;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.checkbox-container input:checked + .checkmark {
  background: #059669;
  border-color: #059669;
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

.setting-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
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
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgb(0 0 0 / 0.1);
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
  color: #1e293b;
}

.modal-close {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: #64748b;
  border-radius: 4px;
}

.modal-close:hover {
  background: #f1f5f9;
}

.modal-body {
  padding: 24px;
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
  .models-grid,
  .settings-grid {
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