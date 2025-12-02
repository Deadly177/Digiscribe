<template>
  <div class="prediction-history">
    <div class="page-header">
      <div>
        <h1>Prediction History</h1>
        <p>Complete history of all digit predictions</p>
      </div>
      <button class="btn-clear" @click="clearHistory" v-if="allPredictions.length">
        Clear History
      </button>
    </div>

    <div v-if="allPredictions.length === 0" class="empty-state">
      <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
      </svg>
      <h3>No Predictions Yet</h3>
      <p>Start making predictions to see your history here</p>
      <router-link to="/dashboard/recognition" class="btn-primary">
        Go to Recognition
      </router-link>
    </div>

    <div v-else class="history-grid">
      <div
        v-for="prediction in allPredictions"
        :key="prediction.id"
        class="prediction-card"
      >
        <div class="prediction-header">
          <div class="predicted-digit">{{ prediction.predicted_digit }}</div>
          <div class="confidence-badge" :class="getConfidenceClass(prediction.confidence)">
            {{ (prediction.confidence * 100).toFixed(1) }}%
          </div>
        </div>
        <div class="prediction-meta">
          <div class="meta-item">
            <span class="meta-label">Time:</span>
            <span class="meta-value">{{ formatTime(prediction.timestamp) }}</span>
          </div>
          <div class="meta-item" v-if="prediction.backend">
            <span class="meta-label">Source:</span>
            <span class="meta-value">{{ prediction.backend }}</span>
          </div>
          <div class="meta-item" v-if="prediction.correct !== null">
            <span class="meta-label">Status:</span>
            <span class="status-badge" :class="prediction.correct ? 'correct' : 'incorrect'">
              {{ prediction.correct ? '✓ Correct' : '✗ Incorrect' }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'PredictionHistory',
  setup() {
    const allPredictions = ref([])

    const loadHistory = () => {
      try {
        const stored = localStorage.getItem('digit_recognition_recent_predictions')
        if (stored) {
          allPredictions.value = JSON.parse(stored)
        }
      } catch (error) {
        console.error('Failed to load prediction history:', error)
      }
    }

    const clearHistory = () => {
      if (confirm('Are you sure you want to clear all prediction history?')) {
        localStorage.removeItem('digit_recognition_recent_predictions')
        allPredictions.value = []
      }
    }

    const getConfidenceClass = (confidence) => {
      if (confidence >= 0.9) return 'high'
      if (confidence >= 0.7) return 'medium'
      return 'low'
    }

    const formatTime = (timestamp) => {
      const date = new Date(timestamp)
      const now = new Date()
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)

      if (minutes < 1) return 'Just now'
      if (minutes < 60) return `${minutes}m ago`
      if (hours < 24) return `${hours}h ago`
      if (days < 7) return `${days}d ago`
      
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }

    onMounted(() => {
      loadHistory()
    })

    return {
      allPredictions,
      clearHistory,
      getConfidenceClass,
      formatTime
    }
  }
}
</script>

<style scoped>
.prediction-history {
  padding: 24px;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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
  margin: 0;
}

.btn-clear {
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-clear:hover {
  background: #dc2626;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: var(--text-secondary);
}

.empty-state svg {
  margin: 0 auto 24px;
  opacity: 0.5;
}

.empty-state h3 {
  font-size: 24px;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.empty-state p {
  font-size: 16px;
  margin-bottom: 24px;
}

.btn-primary {
  display: inline-block;
  background: var(--success);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-primary:hover {
  background: #047857;
}

.history-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.prediction-card {
  background: var(--surface);
  border-radius: 12px;
  padding: 20px;
  box-shadow: var(--shadow);
  border: 1px solid var(--border);
  transition: all 0.2s ease;
}

.prediction-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.prediction-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border);
}

.predicted-digit {
  font-size: 48px;
  font-weight: 700;
  color: var(--success);
}

.confidence-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.confidence-badge.high {
  background: #d1fae5;
  color: #065f46;
}

.confidence-badge.medium {
  background: #fef3c7;
  color: #92400e;
}

.confidence-badge.low {
  background: #fee2e2;
  color: #991b1b;
}

.prediction-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.meta-label {
  color: var(--text-secondary);
  font-weight: 500;
}

.meta-value {
  color: var(--text-primary);
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.correct {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.incorrect {
  background: #fee2e2;
  color: #991b1b;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .history-grid {
    grid-template-columns: 1fr;
  }
}
</style>
