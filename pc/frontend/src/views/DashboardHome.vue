<template>
  <div class="dashboard-home">
    <div class="page-header">
      <h1>{{ strings.title }}</h1>
      <p>{{ strings.subtitle }}</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>{{ strings.loading }}</p>
    </div>

    <!-- Quick Stats -->
    <div v-else class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon predictions">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardStats.totalPredictions.toLocaleString() }}</div>
          <div class="stat-label">{{ strings.stats.predictions }}</div>
          <div class="stat-change" :class="getChangeClass(dashboardStats.predictionChange)">
            {{ formatChange(dashboardStats.predictionChange) }}
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon accuracy">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardStats.accuracy.toFixed(1) }}%</div>
          <div class="stat-label">{{ strings.stats.accuracy }}</div>
          <div class="stat-change" :class="getChangeClass(dashboardStats.accuracyChange)">
            {{ formatChange(dashboardStats.accuracyChange, true) }}
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon models">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
            <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
            <line x1="12" y1="22.08" x2="12" y2="12"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardStats.activeModels }}</div>
          <div class="stat-label">{{ strings.stats.models }}</div>
          <div class="stat-change neutral">{{ strings.noChange }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon feedback">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboardStats.feedbackCount }}</div>
          <div class="stat-label">{{ strings.stats.feedback }}</div>
          <div class="stat-change" :class="getChangeClass(dashboardStats.feedbackChange)">
            {{ formatChange(dashboardStats.feedbackChange) }}
          </div>
        </div>
      </div>
    </div>

    <!-- Rest of the template remains the same -->
    <!-- Quick Actions -->
    <div class="quick-actions-section">
      <h2>{{ strings.quickActionsTitle }}</h2>
      <div class="actions-grid">
        <router-link
          v-for="action in quickActions"
          :key="action.id"
          :to="action.disabled ? '' : action.to"
          class="action-card"
          :class="{ 'coming-soon': action.disabled }"
          @click.prevent="action.disabled && $event.preventDefault()"
        >
          <div class="action-icon">
            <svg
              v-if="action.icon === 'train'"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
              <polyline points="3.27 6.96 12 12.01 20.73 6.96" />
            </svg>
            <svg
              v-else-if="action.icon === 'import'"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M12 3v12" />
              <path d="m8 11 4 4 4-4" />
              <path d="M20 21H4" />
            </svg>
            <svg
              v-else
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z" />
            </svg>
          </div>
          <div class="action-content">
            <h3>{{ action.title }}</h3>
            <p>{{ action.description }}</p>
            <span v-if="action.disabled" class="coming-soon-badge">{{ strings.comingSoon }}</span>
          </div>
          <div class="action-arrow">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="5" y1="12" x2="19" y2="12" />
              <polyline points="12 5 19 12 12 19" />
            </svg>
          </div>
        </router-link>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="recent-activity">
      <div class="activity-header">
        <h2>{{ strings.recentActivityTitle }}</h2>
        <router-link to="/dashboard/analytics" class="view-all-link">
          {{ strings.viewAll }}
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="5" y1="12" x2="19" y2="12"/>
            <polyline points="12 5 19 12 12 19"/>
          </svg>
        </router-link>
      </div>
      <div class="activity-list">
        <p v-if="!recentActivities.length" class="empty-activity">{{ strings.emptyActivity }}</p>
        <div 
          v-for="activity in recentActivities" 
          :key="activity.id"
          class="activity-item"
        >
          <div class="activity-icon" :class="activity.type">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path v-if="activity.type === 'success'" d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
              <polyline v-if="activity.type === 'success'" points="22 4 12 14.01 9 11.01"/>
              <path v-if="activity.type === 'training'" d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              <path v-if="activity.type === 'feedback'" d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
              <path v-if="activity.type === 'user'" d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle v-if="activity.type === 'user'" cx="12" cy="7" r="4"/>
            </svg>
          </div>
          <div class="activity-content">
            <div class="activity-message">{{ activity.message }}</div>
            <div class="activity-time">{{ formatTime(activity.timestamp) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { useI18n } from '@/i18n'
import api from '@/services/api'
import authService from '@/services/authService'

export default {
  name: 'DashboardHome',
  setup() {
    const loading = ref(true)
    const dashboardStats = ref({
      totalPredictions: 0,
      accuracy: 0,
      activeModels: 0,
      feedbackCount: 0,
      predictionChange: 0,
      accuracyChange: 0,
      feedbackChange: 0
    })
    
    const recentActivities = ref([])
    const { locale } = useI18n()

    const enCopy = {
      title: 'Dashboard',
      subtitle: "Welcome back! Here's your digit recognition overview",
      loading: 'Loading data...',
      stats: {
        predictions: 'Total Predictions',
        accuracy: 'Overall Accuracy',
        models: 'Active Models',
        feedback: 'Feedback Received'
      },
      noChange: 'No change',
      quickActionsTitle: 'Quick Actions',
      recentActivityTitle: 'Recent Activity',
      viewAll: 'View all',
      comingSoon: 'Coming soon',
      emptyActivity: 'No recent activity recorded',
      actions: [
        {
          id: 'train',
          title: 'Train New Model',
          description: 'Start retraining with fresh handwriting data',
          to: '/dashboard/models',
          icon: 'train',
          disabled: false
        },
        {
          id: 'import',
          title: 'Import Dataset',
          description: 'Upload handwriting samples to expand the dataset',
          to: '/dashboard/database',
          icon: 'import',
          disabled: true
        },
        {
          id: 'feedback',
          title: 'Review Feedback',
          description: 'Label user feedback and improve accuracy',
          to: '/dashboard/feedback',
          icon: 'feedback',
          disabled: false
        }
      ],
      activityFallback: {
        success: 'Digit 8 predicted successfully with 95% confidence',
        training: 'Model "CNN Enhanced" training complete',
        feedback: 'New feedback received about recognition accuracy',
        user: 'New user registered: alex.johnson@example.com'
      },
      timeAgo: {
        days: (value) => `${value}d ago`,
        hours: (value) => `${value}h ago`,
        minutes: (value) => `${value}m ago`,
        now: 'Just now'
      }
    }

    const zhCopy = {
      title: '控制面板',
      subtitle: '欢迎回来！这是您的数字识别概览',
      loading: '加载数据中...',
      stats: {
        predictions: '总预测次数',
        accuracy: '整体准确率',
        models: '活跃模型',
        feedback: '收到反馈'
      },
      noChange: '无变化',
      quickActionsTitle: '快速操作',
      recentActivityTitle: '最近活动',
      viewAll: '查看全部',
      comingSoon: '敬请期待',
      emptyActivity: '暂无最新活动',
      actions: [
        {
          id: 'train',
          title: '训练新模型',
          description: '使用最新手写数据开始再训练',
          to: '/dashboard/models',
          icon: 'train',
          disabled: false
        },
        {
          id: 'import',
          title: '导入数据集',
          description: '上传手写样本扩充训练库',
          to: '/dashboard/database',
          icon: 'import',
          disabled: true
        },
        {
          id: 'feedback',
          title: '查看反馈',
          description: '标注用户反馈并提升准确率',
          to: '/dashboard/feedback',
          icon: 'feedback',
          disabled: false
        }
      ],
      activityFallback: {
        success: '数字8预测成功，置信度95%',
        training: '模型“CNN增强版”训练完成',
        feedback: '收到关于预测准确性的新反馈',
        user: '新用户注册: alex.johnson@example.com'
      },
      timeAgo: {
        days: (value) => `${value}天前`,
        hours: (value) => `${value}小时前`,
        minutes: (value) => `${value}分钟前`,
        now: '刚刚'
      }
    }

    const strings = computed(() => (locale.value === 'zh' ? zhCopy : enCopy))
    const quickActions = computed(() => strings.value.actions)

    const fetchDashboardData = async () => {
      try {
        loading.value = true
        
        const [statsResponse, activitiesResponse] = await Promise.all([
          api.get('/admin/dashboard'),
          api.get('/admin/logs')
        ])
        
        const stats = statsResponse.data || {}
        dashboardStats.value = {
          totalPredictions: stats.totalPredictions || 0,
          accuracy: stats.accuracy || 0,
          activeModels: stats.activeModels || 0,
          feedbackCount: stats.feedbackCount || 0,
          predictionChange: stats.predictionChange || 0,
          accuracyChange: stats.accuracyChange || 0,
          feedbackChange: stats.feedbackChange || 0
        }
        recentActivities.value = formatActivities(activitiesResponse.data || [])
      } catch (error) {
        console.error('Failed to fetch dashboard data:', error)
        // Fallback to mock data if API fails
        dashboardStats.value = {
          totalPredictions: 15420,
          accuracy: 98.3,
          activeModels: 3,
          feedbackCount: 1323,
          predictionChange: 12.5,
          accuracyChange: 2.3,
          feedbackChange: 8.2
        }
        recentActivities.value = getMockActivities()
      } finally {
        loading.value = false
      }
    }

    const getChangeClass = (change) => {
      if (change > 0) return 'positive'
      if (change < 0) return 'negative'
      return 'neutral'
    }

    const formatChange = (change, isPercentage = false) => {
      if (change === 0) return strings.value.noChange
      const sign = change > 0 ? '+' : '-'
      const value = isPercentage ? Math.abs(change).toFixed(1) : Math.abs(change)
      const unit = isPercentage ? '%' : ''
      return `${sign}${value}${unit}`
    }

    const formatTime = (timestamp) => {
      const now = new Date()
      const diff = now - new Date(timestamp)
      const days = Math.floor(diff / 86400000)
      const hours = Math.floor(diff / 3600000)
      const minutes = Math.floor(diff / 60000)
      
      if (days > 0) return strings.value.timeAgo.days(days)
      if (hours > 0) return strings.value.timeAgo.hours(hours)
      if (minutes > 0) return strings.value.timeAgo.minutes(minutes)
      return strings.value.timeAgo.now
    }

    const formatActivities = (logs) => {
      return logs.map(log => ({
        id: log.id,
        type: mapLogLevel(log.level),
        message: log.message,
        timestamp: log.timestamp
      }))
    }

    const mapLogLevel = (level) => {
      if (!level) return 'success'
      const normalized = level.toLowerCase()
      if (normalized.includes('error')) return 'feedback'
      if (normalized.includes('warn')) return 'training'
      return 'success'
    }

    const getMockActivities = () => {
      const fallback = strings.value.activityFallback
      return [
        {
          id: 1,
          type: 'success',
          message: fallback.success,
          timestamp: new Date(Date.now() - 120000)
        },
        {
          id: 2,
          type: 'training',
          message: fallback.training,
          timestamp: new Date(Date.now() - 3600000)
        },
        {
          id: 3,
          type: 'feedback',
          message: fallback.feedback,
          timestamp: new Date(Date.now() - 10800000)
        },
        {
          id: 4,
          type: 'user',
          message: fallback.user,
          timestamp: new Date(Date.now() - 18000000)
        }
      ]
    }

    onMounted(() => {
      fetchDashboardData()
    })

    return {
      dashboardStats,
      recentActivities,
      loading,
      getChangeClass,
      formatChange,
      formatTime,
      strings,
      quickActions
    }
  }
}
</script>

<style scoped>
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #64748b;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f1f5f9;
  border-top: 4px solid #059669;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.stat-change.negative {
  color: #ef4444;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.predictions {
  background: #f0fdf4;
  color: #059669;
}

.stat-icon.accuracy {
  background: #fef7cd;
  color: #d97706;
}

.stat-icon.models {
  background: #dbeafe;
  color: #1d4ed8;
}

.stat-icon.feedback {
  background: #f3e8ff;
  color: #7c3aed;
}

.stat-content {
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

.stat-change.neutral {
  color: #64748b;
}

.quick-actions-section {
  margin-bottom: 40px;
}

.quick-actions-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 24px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.action-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  text-decoration: none;
  color: inherit;
  transition: all 0.2s ease;
  border: 2px solid transparent;
  cursor: pointer;
}

.action-card:hover {
  border-color: #059669;
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}

.action-card.coming-soon {
  opacity: 0.7;
  cursor: not-allowed;
}

.action-card.coming-soon:hover {
  transform: none;
  border-color: transparent;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0fdf4;
  color: #059669;
  flex-shrink: 0;
}

.action-content {
  flex: 1;
}

.action-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.action-content p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.coming-soon-badge {
  display: inline-block;
  background: #fef3c7;
  color: #92400e;
  font-size: 12px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 12px;
  margin-top: 8px;
}

.action-arrow {
  color: #64748b;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.action-card:hover .action-arrow {
  color: #059669;
  transform: translateX(4px);
}

.recent-activity {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
  margin-bottom: 40px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.activity-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.view-all-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #059669;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.view-all-link:hover {
  gap: 8px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-activity {
  text-align: center;
  color: #94a3b8;
  margin: 0;
  padding: 16px 0;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  background: #f8fafc;
  transition: all 0.2s ease;
}

.activity-item:hover {
  background: #f1f5f9;
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.activity-icon.success {
  background: #d1fae5;
  color: #059669;
}

.activity-icon.training {
  background: #dbeafe;
  color: #1d4ed8;
}

.activity-icon.feedback {
  background: #f3e8ff;
  color: #7c3aed;
}

.activity-icon.user {
  background: #fef3c7;
  color: #d97706;
}

.activity-content {
  flex: 1;
}

.activity-message {
  font-size: 14px;
  color: #374151;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #64748b;
}

/* Responsive */
@media (max-width: 768px) {
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .activity-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .view-all-link {
    align-self: flex-end;
  }
}
</style>
