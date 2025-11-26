<template>
  <div class="feedback-system">
    <div class="page-header">
      <h1>Feedback System</h1>
      <p>Monitor user feedback and improve model performance</p>
    </div>

    <!-- Feedback Overview -->
    <div class="feedback-overview">
      <div class="overview-card">
        <div class="overview-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"/>
          </svg>
        </div>
        <div class="overview-content">
          <div class="overview-value">{{ feedbackStats.total }}</div>
          <div class="overview-label">Total Feedback</div>
          <div class="overview-change positive">+24 this week</div>
        </div>
      </div>

      <div class="overview-card">
        <div class="overview-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
        </div>
        <div class="overview-content">
          <div class="overview-value">{{ feedbackStats.correctPercentage }}%</div>
          <div class="overview-label">Correct Predictions</div>
          <div class="overview-change positive">+3.2%</div>
        </div>
      </div>

      <div class="overview-card">
        <div class="overview-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <line x1="15" y1="9" x2="9" y2="15"/>
            <line x1="9" y1="9" x2="15" y2="15"/>
          </svg>
        </div>
        <div class="overview-content">
          <div class="overview-value">{{ feedbackStats.incorrectPercentage }}%</div>
          <div class="overview-label">Incorrect Predictions</div>
          <div class="overview-change negative">-1.8%</div>
        </div>
      </div>

      <div class="overview-card">
        <div class="overview-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"/>
          </svg>
        </div>
        <div class="overview-content">
          <div class="overview-value">{{ feedbackStats.responseRate }}%</div>
          <div class="overview-label">Feedback Response Rate</div>
          <div class="overview-change positive">+8.5%</div>
        </div>
      </div>
    </div>

    <!-- Feedback Analysis -->
    <div class="feedback-analysis">
      <div class="analysis-card">
        <div class="card-header">
          <h3>Feedback Sentiment</h3>
          <div class="time-filter">
            <select v-model="timeRange" @change="updateAnalysis" class="filter-select">
              <option value="7d">Last 7 Days</option>
              <option value="30d">Last 30 Days</option>
              <option value="90d">Last 90 Days</option>
            </select>
          </div>
        </div>
        <div class="sentiment-chart">
          <div class="sentiment-donut">
            <div class="donut-segment correct" :style="{ transform: `rotate(${correctAngle}deg)` }"></div>
            <div class="donut-segment incorrect" :style="{ transform: `rotate(${incorrectAngle}deg)` }"></div>
            <div class="donut-center">
              <div class="donut-value">{{ feedbackStats.correctPercentage }}%</div>
              <div class="donut-label">Correct</div>
            </div>
          </div>
          <div class="sentiment-legend">
            <div class="legend-item">
              <div class="legend-color correct"></div>
              <span>Correct Predictions</span>
              <strong>{{ feedbackStats.correct }}</strong>
            </div>
            <div class="legend-item">
              <div class="legend-color incorrect"></div>
              <span>Incorrect Predictions</span>
              <strong>{{ feedbackStats.incorrect }}</strong>
            </div>
            <div class="legend-item">
              <div class="legend-color no-feedback"></div>
              <span>No Feedback</span>
              <strong>{{ feedbackStats.noFeedback }}</strong>
            </div>
          </div>
        </div>
      </div>

      <div class="analysis-card">
        <div class="card-header">
          <h3>Accuracy by Digit</h3>
        </div>
        <div class="digit-accuracy-chart">
          <div 
            v-for="digit in digitAccuracy" 
            :key="digit.digit"
            class="digit-accuracy-item"
          >
            <div class="digit-label">{{ digit.digit }}</div>
            <div class="accuracy-bar-container">
              <div 
                class="accuracy-bar" 
                :style="{ width: `${digit.accuracy}%` }"
                :class="getAccuracyClass(digit.accuracy)"
              ></div>
              <div class="accuracy-value">{{ digit.accuracy }}%</div>
            </div>
            <div class="feedback-count">
              {{ digit.feedbackCount }} feedback
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Common Misclassifications -->
    <div class="misclassifications-section">
      <div class="section-header">
        <h3>Common Misclassifications</h3>
        <button class="btn-outline" @click="exportMisclassifications">
          Export Data
        </button>
      </div>
      <div class="misclassifications-grid">
        <div 
          v-for="misclassification in commonMisclassifications" 
          :key="misclassification.id"
          class="misclassification-card"
        >
          <div class="misclassification-header">
            <div class="prediction-comparison">
              <div class="actual-digit">
                <span class="digit-label-small">Actual</span>
                <div class="digit-value actual">{{ misclassification.actual }}</div>
              </div>
              <div class="arrow">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="5" y1="12" x2="19" y2="12"/>
                  <polyline points="12 5 19 12 12 19"/>
                </svg>
              </div>
              <div class="predicted-digit">
                <span class="digit-label-small">Predicted</span>
                <div class="digit-value predicted">{{ misclassification.predicted }}</div>
              </div>
            </div>
            <div class="confidence-score">
              {{ (misclassification.confidence * 100).toFixed(1) }}% confidence
            </div>
          </div>
          <div class="misclassification-stats">
            <div class="stat">
              <span>Frequency</span>
              <strong>{{ misclassification.frequency }}</strong>
            </div>
            <div class="stat">
              <span>Last Occurred</span>
              <strong>{{ formatTime(misclassification.lastOccurred) }}</strong>
            </div>
          </div>
          <div class="misclassification-actions">
            <button class="btn-outline small" @click="analyzeMisclassification(misclassification)">
              Analyze
            </button>
            <button class="btn-outline small" @click="addToTraining(misclassification)">
              Add to Training
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Feedback -->
    <div class="recent-feedback">
      <div class="section-header">
        <h3>Recent Feedback</h3>
        <div class="filter-options">
          <select v-model="feedbackFilter" @change="filterFeedback" class="filter-select">
            <option value="all">All Feedback</option>
            <option value="correct">Correct Only</option>
            <option value="incorrect">Incorrect Only</option>
          </select>
        </div>
      </div>
      <div class="feedback-list">
        <div 
          v-for="feedback in filteredFeedback" 
          :key="feedback.id"
          class="feedback-item"
          :class="feedback.type"
        >
          <div class="feedback-icon">
            <svg 
              v-if="feedback.type === 'correct'" 
              width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
            >
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
              <polyline points="22 4 12 14.01 9 11.01"/>
            </svg>
            <svg 
              v-else 
              width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
            >
              <circle cx="12" cy="12" r="10"/>
              <line x1="15" y1="9" x2="9" y2="15"/>
              <line x1="9" y1="9" x2="15" y2="15"/>
            </svg>
          </div>
          <div class="feedback-content">
            <div class="feedback-prediction">
              Predicted <strong>{{ feedback.predicted }}</strong> 
              <span v-if="feedback.type === 'incorrect'">
                but was <strong>{{ feedback.actual }}</strong>
              </span>
            </div>
            <div class="feedback-meta">
              <span class="user">{{ feedback.user }}</span>
              <span class="time">{{ formatTime(feedback.timestamp) }}</span>
              <span class="confidence">{{ (feedback.confidence * 100).toFixed(1) }}% confidence</span>
            </div>
            <div v-if="feedback.comment" class="feedback-comment">
              "{{ feedback.comment }}"
            </div>
          </div>
          <div class="feedback-actions">
            <button class="action-btn" @click="viewPredictionDetails(feedback)">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
            <button class="action-btn" @click="deleteFeedback(feedback)">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Feedback Trends -->
    <div class="feedback-trends">
      <div class="section-header">
        <h3>Feedback Trends</h3>
      </div>
      <div class="trends-chart">
        <div class="trends-bars">
          <div 
            v-for="day in feedbackTrends" 
            :key="day.date"
            class="trend-bar-group"
          >
            <div class="bar-label">{{ day.date }}</div>
            <div class="trend-bars">
              <div 
                class="trend-bar correct" 
                :style="{ height: `${(day.correct / maxTrendValue) * 100}%` }"
                :title="`Correct: ${day.correct}`"
              ></div>
              <div 
                class="trend-bar incorrect" 
                :style="{ height: `${(day.incorrect / maxTrendValue) * 100}%` }"
                :title="`Incorrect: ${day.incorrect}`"
              ></div>
            </div>
            <div class="bar-total">{{ day.total }}</div>
          </div>
        </div>
        <div class="trends-legend">
          <div class="legend-item">
            <div class="legend-color correct"></div>
            <span>Correct Feedback</span>
          </div>
          <div class="legend-item">
            <div class="legend-color incorrect"></div>
            <span>Incorrect Feedback</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'FeedbackSystem',
  setup() {
    const timeRange = ref('30d')
    const feedbackFilter = ref('all')

    // Mock data
    const feedbackStats = ref({
      total: 1323,
      correct: 1245,
      incorrect: 78,
      noFeedback: 245,
      correctPercentage: 94.1,
      incorrectPercentage: 5.9,
      responseRate: 84.3
    })

    const digitAccuracy = ref([
      { digit: '0', accuracy: 99.2, feedbackCount: 145 },
      { digit: '1', accuracy: 99.8, feedbackCount: 132 },
      { digit: '2', accuracy: 98.5, feedbackCount: 128 },
      { digit: '3', accuracy: 97.9, feedbackCount: 121 },
      { digit: '4', accuracy: 98.7, feedbackCount: 135 },
      { digit: '5', accuracy: 96.8, feedbackCount: 118 },
      { digit: '6', accuracy: 99.1, feedbackCount: 142 },
      { digit: '7', accuracy: 98.3, feedbackCount: 129 },
      { digit: '8', accuracy: 97.5, feedbackCount: 124 },
      { digit: '9', accuracy: 98.9, feedbackCount: 139 }
    ])

    const commonMisclassifications = ref([
      {
        id: 1,
        actual: 5,
        predicted: 3,
        confidence: 0.87,
        frequency: 23,
        lastOccurred: new Date(Date.now() - 3600000)
      },
      {
        id: 2,
        actual: 7,
        predicted: 1,
        confidence: 0.92,
        frequency: 18,
        lastOccurred: new Date(Date.now() - 7200000)
      },
      {
        id: 3,
        actual: 9,
        predicted: 4,
        confidence: 0.78,
        frequency: 15,
        lastOccurred: new Date(Date.now() - 86400000)
      },
      {
        id: 4,
        actual: 2,
        predicted: 7,
        confidence: 0.85,
        frequency: 12,
        lastOccurred: new Date(Date.now() - 172800000)
      }
    ])

    const recentFeedback = ref([
      {
        id: 1,
        type: 'correct',
        predicted: 8,
        actual: 8,
        confidence: 0.95,
        user: 'john.doe@example.com',
        timestamp: new Date(Date.now() - 300000),
        comment: 'Perfect recognition!'
      },
      {
        id: 2,
        type: 'incorrect',
        predicted: 3,
        actual: 5,
        confidence: 0.87,
        user: 'jane.smith@example.com',
        timestamp: new Date(Date.now() - 1800000),
        comment: 'This was clearly a 5'
      },
      {
        id: 3,
        type: 'correct',
        predicted: 1,
        actual: 1,
        confidence: 0.98,
        user: 'bob.johnson@example.com',
        timestamp: new Date(Date.now() - 3600000),
        comment: ''
      },
      {
        id: 4,
        type: 'incorrect',
        predicted: 7,
        actual: 1,
        confidence: 0.92,
        user: 'alice.williams@example.com',
        timestamp: new Date(Date.now() - 7200000),
        comment: 'The one had a small hook'
      }
    ])

    const feedbackTrends = ref([
      { date: 'Mon', correct: 45, incorrect: 3, total: 48 },
      { date: 'Tue', correct: 52, incorrect: 2, total: 54 },
      { date: 'Wed', correct: 38, incorrect: 5, total: 43 },
      { date: 'Thu', correct: 61, incorrect: 4, total: 65 },
      { date: 'Fri', correct: 49, incorrect: 2, total: 51 },
      { date: 'Sat', correct: 35, incorrect: 3, total: 38 },
      { date: 'Sun', correct: 28, incorrect: 1, total: 29 }
    ])

    // Computed properties
    const correctAngle = computed(() => {
      return (feedbackStats.value.correct / feedbackStats.value.total) * 360
    })

    const incorrectAngle = computed(() => {
      return 360 - correctAngle.value
    })

    const filteredFeedback = computed(() => {
      if (feedbackFilter.value === 'all') return recentFeedback.value
      return recentFeedback.value.filter(f => f.type === feedbackFilter.value)
    })

    const maxTrendValue = computed(() => {
      return Math.max(...feedbackTrends.value.map(day => day.total))
    })

    // Methods
    const getAccuracyClass = (accuracy) => {
      if (accuracy >= 99) return 'excellent'
      if (accuracy >= 97) return 'good'
      if (accuracy >= 95) return 'fair'
      return 'poor'
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

    const updateAnalysis = () => {
      console.log('Updating analysis for time range:', timeRange.value)
    }

    const filterFeedback = () => {
      console.log('Filtering feedback:', feedbackFilter.value)
    }

    const analyzeMisclassification = (misclassification) => {
      console.log('Analyzing misclassification:', misclassification)
    }

    const addToTraining = (misclassification) => {
      console.log('Adding to training:', misclassification)
      alert(`Misclassification ${misclassification.actual}→${misclassification.predicted} added to training data`)
    }

    const exportMisclassifications = () => {
      console.log('Exporting misclassifications data')
    }

    const viewPredictionDetails = (feedback) => {
      console.log('Viewing prediction details:', feedback)
    }

    const deleteFeedback = (feedback) => {
      if (confirm('Are you sure you want to delete this feedback?')) {
        recentFeedback.value = recentFeedback.value.filter(f => f.id !== feedback.id)
      }
    }

    onMounted(() => {
      // In a real app, this would fetch feedback data
      console.log('Loading feedback system data')
    })

    return {
      timeRange,
      feedbackFilter,
      feedbackStats,
      digitAccuracy,
      commonMisclassifications,
      recentFeedback,
      feedbackTrends,
      correctAngle,
      incorrectAngle,
      filteredFeedback,
      maxTrendValue,
      getAccuracyClass,
      formatTime,
      updateAnalysis,
      filterFeedback,
      analyzeMisclassification,
      addToTraining,
      exportMisclassifications,
      viewPredictionDetails,
      deleteFeedback
    }
  }
}
</script>

<style scoped>
.feedback-system {
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

/* Feedback Overview */
.feedback-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
}

.overview-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0fdf4;
  color: #059669;
}

.overview-content {
  flex: 1;
}

.overview-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.overview-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 4px;
}

.overview-change {
  font-size: 12px;
  font-weight: 500;
}

.overview-change.positive {
  color: #059669;
}

.overview-change.negative {
  color: #ef4444;
}

/* Feedback Analysis */
.feedback-analysis {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 24px;
  margin-bottom: 32px;
}

.analysis-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.filter-select {
  padding: 6px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  background: white;
}

/* Sentiment Chart */
.sentiment-chart {
  display: flex;
  align-items: center;
  gap: 40px;
}

.sentiment-donut {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: conic-gradient(#059669 0deg var(--correct-angle, 0deg), #ef4444 var(--correct-angle, 0deg) 360deg);
}

.donut-segment {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  clip-path: polygon(50% 50%, 50% 0%, 100% 0%, 100% 100%, 50% 100%);
}

.donut-segment.correct {
  background: #059669;
}

.donut-segment.incorrect {
  background: #ef4444;
}

.donut-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.donut-value {
  font-size: 16px;
  font-weight: 700;
  color: #059669;
}

.donut-label {
  font-size: 10px;
  color: #64748b;
}

.sentiment-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.correct {
  background: #059669;
}

.legend-color.incorrect {
  background: #ef4444;
}

.legend-color.no-feedback {
  background: #d1d5db;
}

.legend-item strong {
  color: #1e293b;
  margin-left: 4px;
}

/* Digit Accuracy Chart */
.digit-accuracy-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 300px;
  overflow-y: auto;
}

.digit-accuracy-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f1f5f9;
}

.digit-accuracy-item:last-child {
  border-bottom: none;
}

.digit-label {
  width: 20px;
  font-weight: 600;
  color: #475569;
  font-size: 14px;
}

.accuracy-bar-container {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.accuracy-bar {
  height: 16px;
  border-radius: 8px;
  transition: width 0.3s ease;
  min-width: 2px;
}

.accuracy-bar.excellent {
  background: #059669;
}

.accuracy-bar.good {
  background: #10b981;
}

.accuracy-bar.fair {
  background: #f59e0b;
}

.accuracy-bar.poor {
  background: #ef4444;
}

.accuracy-value {
  width: 50px;
  font-size: 12px;
  color: #64748b;
  text-align: right;
}

.feedback-count {
  width: 80px;
  font-size: 12px;
  color: #64748b;
  text-align: right;
}

/* Misclassifications */
.misclassifications-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.btn-outline {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  color: #475569;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-outline:hover {
  border-color: #059669;
  color: #059669;
}

.btn-outline.small {
  padding: 6px 12px;
  font-size: 12px;
}

.misclassifications-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.misclassification-card {
  background: #f8fafc;
  border-radius: 8px;
  padding: 20px;
  border: 2px solid transparent;
  transition: all 0.2s ease;
}

.misclassification-card:hover {
  border-color: #059669;
}

.misclassification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.prediction-comparison {
  display: flex;
  align-items: center;
  gap: 12px;
}

.actual-digit, .predicted-digit {
  text-align: center;
}

.digit-label-small {
  font-size: 12px;
  color: #64748b;
  display: block;
  margin-bottom: 4px;
}

.digit-value {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
}

.digit-value.actual {
  background: #059669;
  color: white;
}

.digit-value.predicted {
  background: #ef4444;
  color: white;
}

.arrow {
  color: #64748b;
}

.confidence-score {
  font-size: 12px;
  color: #64748b;
  text-align: right;
}

.misclassification-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;
}

.misclassification-stats .stat {
  text-align: center;
  padding: 8px;
  background: white;
  border-radius: 6px;
}

.misclassification-stats .stat span {
  display: block;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 4px;
}

.misclassification-stats .stat strong {
  display: block;
  font-size: 14px;
  color: #1e293b;
  font-weight: 600;
}

.misclassification-actions {
  display: flex;
  gap: 8px;
}

/* Recent Feedback */
.recent-feedback {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
  margin-bottom: 32px;
}

.filter-options {
  display: flex;
  gap: 12px;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feedback-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid #e5e7eb;
  background: #f8fafc;
}

.feedback-item.correct {
  border-left-color: #059669;
}

.feedback-item.incorrect {
  border-left-color: #ef4444;
}

.feedback-icon {
  margin-top: 2px;
}

.feedback-item.correct .feedback-icon {
  color: #059669;
}

.feedback-item.incorrect .feedback-icon {
  color: #ef4444;
}

.feedback-content {
  flex: 1;
}

.feedback-prediction {
  font-size: 14px;
  color: #374151;
  margin-bottom: 8px;
}

.feedback-prediction strong {
  color: #1e293b;
}

.feedback-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 8px;
}

.feedback-comment {
  font-size: 14px;
  color: #475569;
  font-style: italic;
  padding: 8px;
  background: white;
  border-radius: 4px;
  border-left: 3px solid #d1d5db;
}

.feedback-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: none;
  border: none;
  padding: 6px;
  border-radius: 4px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: #f1f5f9;
  color: #374151;
}

/* Feedback Trends */
.feedback-trends {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
}

.trends-chart {
  display: flex;
  gap: 40px;
  align-items: flex-end;
}

.trends-bars {
  display: flex;
  align-items: end;
  gap: 16px;
  flex: 1;
  height: 200px;
  padding: 0 20px;
}

.trend-bar-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.bar-label {
  font-size: 12px;
  color: #64748b;
}

.trend-bars {
  display: flex;
  align-items: end;
  gap: 2px;
  height: 160px;
  width: 100%;
}

.trend-bar {
  flex: 1;
  border-radius: 2px 2px 0 0;
  transition: all 0.3s ease;
  min-height: 2px;
}

.trend-bar.correct {
  background: #059669;
}

.trend-bar.incorrect {
  background: #ef4444;
}

.bar-total {
  font-size: 12px;
  font-weight: 600;
  color: #374151;
}

.trends-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* Responsive */
@media (max-width: 768px) {
  .feedback-overview {
    grid-template-columns: 1fr;
  }
  
  .feedback-analysis {
    grid-template-columns: 1fr;
  }
  
  .sentiment-chart {
    flex-direction: column;
    gap: 20px;
  }
  
  .misclassifications-grid {
    grid-template-columns: 1fr;
  }
  
  .trends-chart {
    flex-direction: column;
    gap: 20px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .filter-options {
    justify-content: stretch;
  }
  
  .filter-options .filter-select {
    flex: 1;
  }
}
</style>