<template>
  <div class="analytics">
    <div class="page-header">
      <h1>Analytics Dashboard</h1>
      <p>Comprehensive insights into your digit recognition performance</p>
    </div>

    <!-- Date Range Filter -->
    <div class="filters">
      <div class="filter-group">
        <label>Date Range</label>
        <select v-model="selectedRange" @change="updateAnalytics" class="filter-select">
          <option value="7d">Last 7 Days</option>
          <option value="30d">Last 30 Days</option>
          <option value="90d">Last 90 Days</option>
          <option value="1y">Last Year</option>
          <option value="all">All Time</option>
        </select>
      </div>
      <div class="filter-group">
        <label>Model</label>
        <select v-model="selectedModel" @change="updateAnalytics" class="filter-select">
          <option value="all">All Models</option>
          <option v-for="model in models" :key="model.id" :value="model.id">
            {{ model.name }}
          </option>
        </select>
      </div>
      <button class="export-btn" @click="exportData">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="7 10 12 15 17 10"/>
          <line x1="12" y1="15" x2="12" y2="3"/>
        </svg>
        Export Data
      </button>
    </div>

    <!-- Key Metrics -->
    <div class="metrics-overview">
      <div class="metric-card large">
        <div class="metric-value">{{ totalPredictions.toLocaleString() }}</div>
        <div class="metric-label">Total Predictions</div>
        <div class="metric-change positive">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/>
            <polyline points="17 6 23 6 23 12"/>
          </svg>
          +12.5% from last period
        </div>
      </div>

      <div class="metric-card large">
        <div class="metric-value">{{ overallAccuracy }}%</div>
        <div class="metric-label">Overall Accuracy</div>
        <div class="metric-change positive">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/>
            <polyline points="17 6 23 6 23 12"/>
          </svg>
          +2.3% improvement
        </div>
      </div>

      <div class="metric-card large">
        <div class="metric-value">{{ averageConfidence }}%</div>
        <div class="metric-label">Average Confidence</div>
        <div class="metric-change neutral">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          No change
        </div>
      </div>

      <div class="metric-card large">
        <div class="metric-value">{{ userSatisfaction }}%</div>
        <div class="metric-label">User Satisfaction</div>
        <div class="metric-change positive">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/>
            <polyline points="17 6 23 6 23 12"/>
          </svg>
          +5.7% improvement
        </div>
      </div>
    </div>

    <!-- Charts Grid -->
    <div class="charts-grid">
      <!-- Predictions Over Time -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>Predictions Over Time</h3>
          <div class="chart-legend">
            <div class="legend-item">
              <div class="legend-color success"></div>
              <span>Successful</span>
            </div>
            <div class="legend-item">
              <div class="legend-color failed"></div>
              <span>Failed</span>
            </div>
          </div>
        </div>
        <div class="chart-container">
          <div class="bar-chart">
            <div 
              v-for="day in predictionsOverTime" 
              :key="day.date"
              class="bar-group"
            >
              <div class="bar-label">{{ day.date }}</div>
              <div class="bars">
                <div 
                  class="bar success" 
                  :style="{ height: `${(day.successful / maxPredictions) * 100}%` }"
                  :title="`Successful: ${day.successful}`"
                ></div>
                <div 
                  class="bar failed" 
                  :style="{ height: `${(day.failed / maxPredictions) * 100}%` }"
                  :title="`Failed: ${day.failed}`"
                ></div>
              </div>
              <div class="bar-total">{{ day.total }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Accuracy by Digit -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>Accuracy by Digit</h3>
        </div>
        <div class="chart-container">
          <div class="accuracy-chart">
            <div 
              v-for="digit in digitAccuracy" 
              :key="digit.digit"
              class="accuracy-item"
            >
              <div class="digit">{{ digit.digit }}</div>
              <div class="accuracy-bar-container">
                <div 
                  class="accuracy-bar" 
                  :style="{ width: `${digit.accuracy}%` }"
                  :class="getAccuracyClass(digit.accuracy)"
                ></div>
                <div class="accuracy-value">{{ digit.accuracy }}%</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Model Performance Comparison -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>Model Performance</h3>
        </div>
        <div class="chart-container">
          <div class="model-comparison">
            <div 
              v-for="model in modelPerformance" 
              :key="model.id"
              class="model-bar"
            >
              <div class="model-name">{{ model.name }}</div>
              <div class="performance-bar-container">
                <div 
                  class="performance-bar" 
                  :style="{ width: `${model.accuracy}%` }"
                ></div>
                <div class="performance-value">{{ model.accuracy }}%</div>
              </div>
              <div class="model-stats">
                <span>{{ model.predictions }} predictions</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Confidence Distribution -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>Confidence Distribution</h3>
        </div>
        <div class="chart-container">
          <div class="confidence-distribution">
            <div 
              v-for="bucket in confidenceDistribution" 
              :key="bucket.range"
              class="confidence-bucket"
            >
              <div class="bucket-range">{{ bucket.range }}%</div>
              <div class="bucket-bar-container">
                <div 
                  class="bucket-bar" 
                  :style="{ width: `${(bucket.count / maxConfidenceCount) * 100}%` }"
                ></div>
                <div class="bucket-count">{{ bucket.count }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Peak Usage Hours -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>Peak Usage Hours</h3>
        </div>
        <div class="chart-container">
          <div class="usage-chart">
            <div 
              v-for="hour in usageByHour" 
              :key="hour.hour"
              class="usage-hour"
            >
              <div class="hour-label">{{ hour.hour }}</div>
              <div class="usage-bar-container">
                <div 
                  class="usage-bar" 
                  :style="{ height: `${(hour.predictions / maxUsage) * 100}%` }"
                ></div>
              </div>
              <div class="usage-count">{{ hour.predictions }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Feedback Sentiment -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>Feedback Sentiment</h3>
        </div>
        <div class="chart-container">
          <div class="sentiment-chart">
            <div class="sentiment-donut">
              <div class="donut-segment correct" :style="{ transform: `rotate(${correctFeedbackAngle}deg)` }"></div>
              <div class="donut-segment incorrect" :style="{ transform: `rotate(${incorrectFeedbackAngle}deg)` }"></div>
              <div class="donut-center">
                <div class="donut-value">{{ feedbackStats.correctPercentage }}%</div>
                <div class="donut-label">Correct</div>
              </div>
            </div>
            <div class="sentiment-legend">
              <div class="sentiment-item">
                <div class="sentiment-color correct"></div>
                <span>Correct Predictions</span>
                <strong>{{ feedbackStats.correct }}</strong>
              </div>
              <div class="sentiment-item">
                <div class="sentiment-color incorrect"></div>
                <span>Incorrect Predictions</span>
                <strong>{{ feedbackStats.incorrect }}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Detailed Statistics -->
    <div class="detailed-stats">
      <h2>Detailed Statistics</h2>
      <div class="stats-grid">
        <div class="stat-card">
          <h4>Response Times</h4>
          <div class="stat-item">
            <span>Average</span>
            <strong>{{ responseTimes.average }}ms</strong>
          </div>
          <div class="stat-item">
            <span>P95</span>
            <strong>{{ responseTimes.p95 }}ms</strong>
          </div>
          <div class="stat-item">
            <span>P99</span>
            <strong>{{ responseTimes.p99 }}ms</strong>
          </div>
        </div>

        <div class="stat-card">
          <h4>Error Analysis</h4>
          <div class="stat-item">
            <span>Total Errors</span>
            <strong>{{ errorAnalysis.total }}</strong>
          </div>
          <div class="stat-item">
            <span>Model Errors</span>
            <strong>{{ errorAnalysis.model }}</strong>
          </div>
          <div class="stat-item">
            <span>System Errors</span>
            <strong>{{ errorAnalysis.system }}</strong>
          </div>
        </div>

        <div class="stat-card">
          <h4>User Engagement</h4>
          <div class="stat-item">
            <span>Active Users</span>
            <strong>{{ userEngagement.active }}</strong>
          </div>
          <div class="stat-item">
            <span>Avg Session</span>
            <strong>{{ userEngagement.avgSession }}m</strong>
          </div>
          <div class="stat-item">
            <span>Return Rate</span>
            <strong>{{ userEngagement.returnRate }}%</strong>
          </div>
        </div>

        <div class="stat-card">
          <h4>Model Usage</h4>
          <div class="stat-item">
            <span>Most Used</span>
            <strong>{{ modelUsage.mostUsed }}</strong>
          </div>
          <div class="stat-item">
            <span>Avg Accuracy</span>
            <strong>{{ modelUsage.avgAccuracy }}%</strong>
          </div>
          <div class="stat-item">
            <span>Training Time</span>
            <strong>{{ modelUsage.avgTrainingTime }}m</strong>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'

export default {
  name: 'Analytics',
  setup() {
    const selectedRange = ref('30d')
    const selectedModel = ref('all')

    // Mock data
    const predictionsOverTime = ref([
      { date: 'Mon', successful: 45, failed: 3, total: 48 },
      { date: 'Tue', successful: 52, failed: 2, total: 54 },
      { date: 'Wed', successful: 38, failed: 5, total: 43 },
      { date: 'Thu', successful: 61, failed: 4, total: 65 },
      { date: 'Fri', successful: 49, failed: 2, total: 51 },
      { date: 'Sat', successful: 35, failed: 3, total: 38 },
      { date: 'Sun', successful: 28, failed: 1, total: 29 }
    ])

    const digitAccuracy = ref([
      { digit: '0', accuracy: 99.2 },
      { digit: '1', accuracy: 99.8 },
      { digit: '2', accuracy: 98.5 },
      { digit: '3', accuracy: 97.9 },
      { digit: '4', accuracy: 98.7 },
      { digit: '5', accuracy: 96.8 },
      { digit: '6', accuracy: 99.1 },
      { digit: '7', accuracy: 98.3 },
      { digit: '8', accuracy: 97.5 },
      { digit: '9', accuracy: 98.9 }
    ])

    const modelPerformance = ref([])

    const confidenceDistribution = ref([
      { range: '90-100', count: 2450 },
      { range: '80-89', count: 1560 },
      { range: '70-79', count: 890 },
      { range: '60-69', count: 450 },
      { range: '50-59', count: 210 },
      { range: '0-49', count: 95 }
    ])

    const usageByHour = ref([
      { hour: '00', predictions: 12 },
      { hour: '02', predictions: 8 },
      { hour: '04', predictions: 5 },
      { hour: '06', predictions: 15 },
      { hour: '08', predictions: 45 },
      { hour: '10', predictions: 68 },
      { hour: '12', predictions: 72 },
      { hour: '14', predictions: 65 },
      { hour: '16', predictions: 58 },
      { hour: '18', predictions: 42 },
      { hour: '20', predictions: 35 },
      { hour: '22', predictions: 18 }
    ])

    const models = ref([])
    const overviewMetrics = ref({
      totalPredictions: 0,
      averageAccuracy: 0,
      activeModels: 0,
      totalModels: 0,
      totalTrainingSamples: 0
    })

    // Computed properties
    const totalPredictions = computed(() => {
      if (overviewMetrics.value.totalPredictions) {
        return overviewMetrics.value.totalPredictions
      }
      return predictionsOverTime.value.reduce((sum, day) => sum + day.total, 0)
    })

    const overallAccuracy = computed(() => {
      if (overviewMetrics.value.averageAccuracy) {
        return overviewMetrics.value.averageAccuracy.toFixed(1)
      }
      const totalSuccessful = predictionsOverTime.value.reduce((sum, day) => sum + day.successful, 0)
      return ((totalSuccessful / totalPredictions.value) * 100).toFixed(1)
    })

    const averageConfidence = computed(() => {
      return 87.5 // Mock value
    })

    const userSatisfaction = computed(() => {
      return 94.2 // Mock value
    })

    const maxPredictions = computed(() => {
      return Math.max(...predictionsOverTime.value.map(day => day.total))
    })

    const maxConfidenceCount = computed(() => {
      return Math.max(...confidenceDistribution.value.map(bucket => bucket.count))
    })

    const maxUsage = computed(() => {
      return Math.max(...usageByHour.value.map(hour => hour.predictions))
    })

    const feedbackStats = computed(() => {
      const correct = 1245
      const incorrect = 78
      const total = correct + incorrect
      return {
        correct,
        incorrect,
        total,
        correctPercentage: ((correct / total) * 100).toFixed(1)
      }
    })

    const correctFeedbackAngle = computed(() => {
      return (feedbackStats.value.correct / feedbackStats.value.total) * 360
    })

    const incorrectFeedbackAngle = computed(() => {
      return 360 - correctFeedbackAngle.value
    })

    // Mock detailed statistics
    const responseTimes = ref({
      average: 45,
      p95: 89,
      p99: 156
    })

    const errorAnalysis = ref({
      total: 23,
      model: 15,
      system: 8
    })

    const userEngagement = ref({
      active: 142,
      avgSession: 8.5,
      returnRate: 76.3
    })

    const modelUsage = ref({
      mostUsed: 'CNN Basic',
      avgAccuracy: 98.3,
      avgTrainingTime: 12.5
    })

    // Methods
    const getAccuracyClass = (accuracy) => {
      if (accuracy >= 99) return 'excellent'
      if (accuracy >= 97) return 'good'
      if (accuracy >= 95) return 'fair'
      return 'poor'
    }

    const updateAnalytics = () => {
      loadAnalytics()
    }

    const exportData = () => {
      // In a real app, this would generate and download a CSV/PDF report
      console.log('Exporting analytics data')
      alert('Export functionality would generate a detailed report here.')
    }

    const loadAnalytics = async () => {
      try {
        const [modelsResponse, accuracyResponse, overviewResponse] = await Promise.all([
          api.get('/models'),
          api.get('/models/accuracy-by-digit'),
          api.get('/analytics/overview')
        ])

        models.value = modelsResponse.data || []
        digitAccuracy.value = accuracyResponse.data || digitAccuracy.value
        overviewMetrics.value = overviewResponse.data || overviewMetrics.value

        modelPerformance.value = models.value.map(model => ({
          id: model.id,
          name: model.name,
          accuracy: model.accuracy || 0,
          predictions: model.prediction_count || model.predictionCount || 0
        }))
      } catch (error) {
        console.error('Failed to load analytics:', error)
      }
    }

    onMounted(() => {
      loadAnalytics()
    })

    return {
      selectedRange,
      selectedModel,
      predictionsOverTime,
      digitAccuracy,
      modelPerformance,
      confidenceDistribution,
      usageByHour,
      models,
      totalPredictions,
      overallAccuracy,
      averageConfidence,
      userSatisfaction,
      maxPredictions,
      maxConfidenceCount,
      maxUsage,
      feedbackStats,
      correctFeedbackAngle,
      incorrectFeedbackAngle,
      responseTimes,
      errorAnalysis,
      userEngagement,
      modelUsage,
      getAccuracyClass,
      updateAnalytics,
      exportData
    }
  }
}
</script>

<style scoped>
.analytics {
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

/* Filters */
.filters {
  display: flex;
  gap: 16px;
  align-items: end;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  background: white;
  min-width: 120px;
}

.export-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #059669;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.export-btn:hover {
  background: #047857;
}

/* Metrics Overview */
.metrics-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.metric-card.large {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
  text-align: center;
}

.metric-card.large .metric-value {
  font-size: 36px;
  font-weight: 700;
  color: #059669;
  margin-bottom: 8px;
}

.metric-card.large .metric-label {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 12px;
}

.metric-change {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 500;
}

.metric-change.positive {
  color: #059669;
}

.metric-change.neutral {
  color: #64748b;
}

/* Charts Grid */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  margin-bottom: 48px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.chart-legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.success {
  background: #059669;
}

.legend-color.failed {
  background: #ef4444;
}

.chart-container {
  height: 200px;
}

/* Bar Chart */
.bar-chart {
  display: flex;
  align-items: end;
  gap: 12px;
  height: 100%;
  padding: 0 20px;
}

.bar-group {
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

.bars {
  display: flex;
  align-items: end;
  gap: 2px;
  height: 140px;
  width: 100%;
}

.bar {
  flex: 1;
  border-radius: 2px 2px 0 0;
  transition: all 0.3s ease;
  min-height: 2px;
}

.bar.success {
  background: #059669;
}

.bar.failed {
  background: #ef4444;
}

.bar-total {
  font-size: 12px;
  font-weight: 600;
  color: #374151;
}

/* Accuracy Chart */
.accuracy-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: 100%;
}

.accuracy-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.digit {
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

/* Model Comparison */
.model-comparison {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
}

.model-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.model-name {
  width: 120px;
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

.performance-bar-container {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.performance-bar {
  height: 20px;
  background: linear-gradient(90deg, #059669, #10b981);
  border-radius: 10px;
  transition: width 0.3s ease;
  min-width: 2px;
}

.performance-value {
  width: 50px;
  font-size: 14px;
  font-weight: 600;
  color: #059669;
  text-align: right;
}

.model-stats {
  width: 100px;
  font-size: 12px;
  color: #64748b;
  text-align: right;
}

/* Confidence Distribution */
.confidence-distribution {
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: 100%;
}

.confidence-bucket {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bucket-range {
  width: 50px;
  font-size: 12px;
  color: #475569;
  font-weight: 500;
}

.bucket-bar-container {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.bucket-bar {
  height: 16px;
  background: linear-gradient(90deg, #6366f1, #8b5cf6);
  border-radius: 8px;
  transition: width 0.3s ease;
  min-width: 2px;
}

.bucket-count {
  width: 40px;
  font-size: 12px;
  color: #64748b;
  text-align: right;
}

/* Usage Chart */
.usage-chart {
  display: flex;
  align-items: end;
  gap: 8px;
  height: 100%;
  padding: 0 20px;
}

.usage-hour {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  flex: 1;
}

.hour-label {
  font-size: 11px;
  color: #64748b;
}

.usage-bar-container {
  height: 140px;
  width: 100%;
  display: flex;
  align-items: end;
}

.usage-bar {
  width: 100%;
  background: linear-gradient(to top, #f59e0b, #d97706);
  border-radius: 2px 2px 0 0;
  transition: height 0.3s ease;
  min-height: 2px;
}

.usage-count {
  font-size: 11px;
  color: #374151;
  font-weight: 500;
}

/* Sentiment Chart */
.sentiment-chart {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  height: 100%;
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

.sentiment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.sentiment-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.sentiment-color.correct {
  background: #059669;
}

.sentiment-color.incorrect {
  background: #ef4444;
}

.sentiment-item strong {
  color: #1e293b;
  margin-left: 4px;
}

/* Detailed Statistics */
.detailed-stats {
  margin-bottom: 48px;
}

.detailed-stats h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
}

.stat-card h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f1f5f9;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-item span {
  color: #64748b;
  font-size: 14px;
}

.stat-item strong {
  color: #1e293b;
  font-size: 14px;
  font-weight: 600;
}

/* Responsive */
@media (max-width: 768px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-group {
    width: 100%;
  }
  
  .filter-select {
    width: 100%;
  }
  
  .sentiment-chart {
    flex-direction: column;
    gap: 20px;
  }
  
  .metrics-overview {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
