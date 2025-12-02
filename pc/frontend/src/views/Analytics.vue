<template>
  <div class="analytics">
    <div class="page-header">
      <h1>{{ t('analytics.title') }}</h1>
      <p>{{ t('analytics.subtitle') }}</p>
    </div>



    <!-- Key Metrics -->
    <div class="metrics-overview">
      <div class="metric-card large">
        <div class="metric-value">{{ totalPredictions.toLocaleString() }}</div>
        <div class="metric-label">{{ t('analytics.totalPredictions') }}</div>
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
        <div class="metric-label">{{ t('analytics.overallAccuracy') }}</div>
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
        <div class="metric-label">{{ t('analytics.averageConfidence') }}</div>
        <div class="metric-change neutral">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          No change
        </div>
      </div>

      <div class="metric-card large">
        <div class="metric-value">{{ userSatisfaction }}%</div>
        <div class="metric-label">{{ t('analytics.userSatisfaction') }}</div>
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
          <h3>{{ t('analytics.weeklyPrediction') }}</h3>
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
                  class="bar total"
                  :style="{ height: maxPredictions > 0 ? `${(day.total / maxPredictions) * 100}%` : '2%' }"
                  :title="`Total: ${day.total}`"
                ></div>
              </div>
              <div class="bar-total">{{ day.total }}</div>
            </div>
          </div>
        </div>
      </div>



      <!-- Model Performance Comparison -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>{{ t('analytics.modelPerformance') }}</h3>
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
                <span>{{ model.predictions }} {{ t('analytics.predictions') }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Confidence Distribution -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>{{ t('analytics.confidenceDistribution') }}</h3>
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
                  :style="{ width: `${maxConfidenceCount > 0 ? (bucket.count / maxConfidenceCount) * 100 : 0}%` }"
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
          <h3>{{ t('analytics.peakUsageHours') }}</h3>
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
                  :style="{ height: `${maxUsage > 0 ? (hour.predictions / maxUsage) * 100 : 0}%` }"
                ></div>
              </div>
              <div class="usage-count">{{ hour.predictions }}</div>
            </div>
          </div>
        </div>
      </div>


    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '@/services/api'
import { useI18n } from '@/i18n'

export default {
  name: 'Analytics',
  setup() {
    const { t } = useI18n()
    const selectedRange = ref('30d')
    const selectedModel = ref('all')

    const predictionsOverTime = ref([])

    const buildPredictionsOverTime = () => {
      try {
        const predictions = JSON.parse(localStorage.getItem('digit_recognition_recent_predictions')) || []
        
        const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
        const last7Days = []
        const now = new Date()
        
        for (let i = 6; i >= 0; i--) {
          const date = new Date(now)
          date.setDate(date.getDate() - i)
          const dayName = days[date.getDay()]
          
          const dayPredictions = predictions.filter(p => {
            const pDate = new Date(p.timestamp)
            return pDate.toDateString() === date.toDateString()
          })
          
          const successful = dayPredictions.filter(p => p.correct === true).length
          const failed = dayPredictions.filter(p => p.correct === false).length
          const total = dayPredictions.length
          
          last7Days.push({ date: dayName, successful, failed, total })
        }
        
        predictionsOverTime.value = last7Days
      } catch (e) {
        predictionsOverTime.value = [
          { date: 'Mon', successful: 0, failed: 0, total: 0 },
          { date: 'Tue', successful: 0, failed: 0, total: 0 },
          { date: 'Wed', successful: 0, failed: 0, total: 0 },
          { date: 'Thu', successful: 0, failed: 0, total: 0 },
          { date: 'Fri', successful: 0, failed: 0, total: 0 },
          { date: 'Sat', successful: 0, failed: 0, total: 0 },
          { date: 'Sun', successful: 0, failed: 0, total: 0 }
        ]
      }
    }



    const modelPerformance = ref([])

    const confidenceDistribution = ref([])

    const buildConfidenceDistribution = () => {
      try {
        const predictions = JSON.parse(localStorage.getItem('digit_recognition_recent_predictions')) || []
        
        const buckets = {
          '90-100': 0,
          '80-89': 0,
          '70-79': 0,
          '60-69': 0,
          '50-59': 0,
          '0-49': 0
        }
        
        predictions.forEach(p => {
          const conf = (p.confidence || 0) * 100
          if (conf >= 90) buckets['90-100']++
          else if (conf >= 80) buckets['80-89']++
          else if (conf >= 70) buckets['70-79']++
          else if (conf >= 60) buckets['60-69']++
          else if (conf >= 50) buckets['50-59']++
          else buckets['0-49']++
        })
        
        confidenceDistribution.value = [
          { range: '90-100', count: buckets['90-100'] },
          { range: '80-89', count: buckets['80-89'] },
          { range: '70-79', count: buckets['70-79'] },
          { range: '60-69', count: buckets['60-69'] },
          { range: '50-59', count: buckets['50-59'] },
          { range: '0-49', count: buckets['0-49'] }
        ]
      } catch (e) {
        confidenceDistribution.value = [
          { range: '90-100', count: 0 },
          { range: '80-89', count: 0 },
          { range: '70-79', count: 0 },
          { range: '60-69', count: 0 },
          { range: '50-59', count: 0 },
          { range: '0-49', count: 0 }
        ]
      }
    }

    const usageByHour = ref([])

    const buildUsageByHour = () => {
      try {
        const predictions = JSON.parse(localStorage.getItem('digit_recognition_recent_predictions')) || []
        
        const hourBuckets = {}
        for (let i = 0; i < 24; i += 2) {
          hourBuckets[i.toString().padStart(2, '0')] = 0
        }
        
        predictions.forEach(p => {
          const date = new Date(p.timestamp)
          const hour = date.getHours()
          const bucket = Math.floor(hour / 2) * 2
          const key = bucket.toString().padStart(2, '0')
          if (hourBuckets[key] !== undefined) {
            hourBuckets[key]++
          }
        })
        
        usageByHour.value = Object.keys(hourBuckets).map(hour => ({
          hour,
          predictions: hourBuckets[hour]
        }))
      } catch (e) {
        usageByHour.value = [
          { hour: '00', predictions: 0 },
          { hour: '02', predictions: 0 },
          { hour: '04', predictions: 0 },
          { hour: '06', predictions: 0 },
          { hour: '08', predictions: 0 },
          { hour: '10', predictions: 0 },
          { hour: '12', predictions: 0 },
          { hour: '14', predictions: 0 },
          { hour: '16', predictions: 0 },
          { hour: '18', predictions: 0 },
          { hour: '20', predictions: 0 },
          { hour: '22', predictions: 0 }
        ]
      }
    }

    const models = ref([])
    const overviewMetrics = ref({
      totalPredictions: 0,
      averageAccuracy: 0,
      activeModels: 0,
      totalModels: 0,
      totalTrainingSamples: 0
    })

    const getDateRangeInDays = () => {
      if (selectedRange.value === '7d') return 7
      if (selectedRange.value === '30d') return 30
      if (selectedRange.value === '90d') return 90
      if (selectedRange.value === '1y') return 365
      return 999999 // all time
    }

    const filterPredictions = () => {
      try {
        const all = JSON.parse(localStorage.getItem('digit_recognition_recent_predictions')) || []
        const days = getDateRangeInDays()
        const cutoff = Date.now() - (days * 24 * 60 * 60 * 1000)
        
        return all.filter(p => {
          const timestamp = new Date(p.timestamp).getTime()
          const inRange = timestamp >= cutoff
          const matchesModel = selectedModel.value === 'all' || p.model_used === selectedModel.value
          return inRange && matchesModel
        })
      } catch (e) {
        return []
      }
    }

    // Computed properties (copied from Home.vue)
    const totalPredictions = computed(() => {
      const filtered = filterPredictions()
      if (filtered.length > 0) {
        return filtered.length
      }
      
      // Fallback to API data or mock data
      if (overviewMetrics.value.totalPredictions) {
        return overviewMetrics.value.totalPredictions
      }
      return predictionsOverTime.value.reduce((sum, day) => sum + day.total, 0)
    })

    const overallAccuracy = computed(() => {
      const filtered = filterPredictions()
      if (!filtered.length) return '0.0'
      const correct = filtered.filter(p => p.correct === true).length
      return ((correct / filtered.length) * 100).toFixed(1)
    })

    const averageConfidence = computed(() => {
      const filtered = filterPredictions()
      if (!filtered.length) return '0.0'
      const sum = filtered.reduce((acc, p) => acc + ((p.confidence || 0) * 100), 0)
      return (sum / filtered.length).toFixed(1)
    })

    const userSatisfaction = computed(() => {
      return 94.2 // Mock value
    })

    const maxPredictions = computed(() => {
      const values = predictionsOverTime.value.map(day => day.total)
      const max = values.length > 0 ? Math.max(...values) : 0
      return max > 0 ? max : 1
    })

    const maxConfidenceCount = computed(() => {
      const values = confidenceDistribution.value.map(bucket => bucket.count)
      return values.length > 0 ? Math.max(...values) : 1
    })

    const maxUsage = computed(() => {
      const values = usageByHour.value.map(hour => hour.predictions)
      return values.length > 0 ? Math.max(...values) : 1
    })



    // Methods

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
        const [modelsResponse, overviewResponse] = await Promise.all([
          api.get('/models'),
          api.get('/analytics/overview')
        ])

        models.value = modelsResponse.data || []
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
      buildPredictionsOverTime()
      buildConfidenceDistribution()
      buildUsageByHour()
    })

    return {
      t,
      selectedRange,
      selectedModel,
      predictionsOverTime,
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
  color: var(--text-primary);
  margin-bottom: 8px;
}

.page-header p {
  color: var(--text-secondary);
  font-size: 16px;
}



/* Metrics Overview */
.metrics-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.metric-card.large {
  background: var(--surface);
  border-radius: 12px;
  padding: 24px;
  box-shadow: var(--shadow);
  text-align: center;
  border: 1px solid var(--border);
}

.metric-card.large .metric-value {
  font-size: 36px;
  font-weight: 700;
  color: #059669;
  margin-bottom: 8px;
}

.metric-card.large .metric-label {
  font-size: 16px;
  color: var(--text-secondary);
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
  background: var(--surface);
  border-radius: 12px;
  padding: 24px;
  box-shadow: var(--shadow);
  border: 1px solid var(--border);
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
  color: var(--text-primary);
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

.bar.total {
  background: #ef4444;
}

.bar-total {
  font-size: 12px;
  font-weight: 600;
  color: #374151;
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



/* Responsive */
@media (max-width: 768px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }





  .metrics-overview {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
