<template>
  <div class="digit-recognition">
    <div class="page-header">
      <div>
        <h1>Digit Recognition</h1>
        <p>Draw a digit, analyze confidence, and track system performance.</p>
      </div>
      <div class="header-actions">
        <button class="ghost-btn" @click="loadInsights">
          Refresh Insights
        </button>
      </div>
    </div>

    <div class="insights" v-if="!insightsLoading">
      <div class="insight-card">
        <span>Total Predictions</span>
        <strong>{{ systemStats.totalPredictions.toLocaleString() }}</strong>
      </div>
      <div class="insight-card">
        <span>Accuracy</span>
        <strong>{{ systemStats.accuracy.toFixed(1) }}%</strong>
      </div>
      <div class="insight-card">
        <span>Active Models</span>
        <strong>{{ systemStats.activeModels }}</strong>
      </div>
      <div class="insight-card">
        <span>Feedback Logged</span>
        <strong>{{ systemStats.feedbackCount }}</strong>
      </div>
    </div>
    <div v-else class="insights loading">
      <div class="insight-card loading-card">
        <span>Loading insights...</span>
      </div>
    </div>

    <div class="recognition-grid">
      <div class="drawing-panel">
        <div class="panel-header">
          <h2>Canvas</h2>
          <span>Optimized for stylus or mouse input</span>
        </div>
        <div class="canvas-container">
          <canvas 
            ref="canvas"
            width="320" 
            height="320"
            class="drawing-canvas"
            @mousedown="startDrawing"
            @mousemove="draw"
            @mouseup="stopDrawing"
            @mouseleave="stopDrawing"
            @touchstart="startDrawingTouch"
            @touchmove="drawTouch"
            @touchend="stopDrawing"
          ></canvas>
          <div class="canvas-overlay">
            <div class="grid-overlay"></div>
          </div>
        </div>
        <div class="drawing-controls">
          <button class="control-btn secondary" @click="clearCanvas">
            Clear Canvas
          </button>
          <button class="control-btn primary" @click="predictDigit" :disabled="loading">
            <span v-if="loading" class="loading-spinner"></span>
            {{ loading ? 'Analyzing...' : 'Predict Digit' }}
          </button>
        </div>
        <div class="quick-actions">
          <h3>Quick Test</h3>
          <div class="quick-digits">
            <button 
              v-for="digit in [0,1,2,3,4,5,6,7,8,9]" 
              :key="digit"
              class="digit-btn"
              @click="loadTestDigit(digit)"
            >
              {{ digit }}
            </button>
          </div>
        </div>
      </div>

      <div class="analysis-panel">
        <div class="flex-row">
          <div class="card prediction-card">
            <div class="panel-header">
              <h2>Prediction Result</h2>
              <span v-if="currentPrediction">Model {{ currentPrediction.model_used || 'Active' }}</span>
            </div>
            <div v-if="currentPrediction" class="prediction-result">
              <div class="predicted-digit">{{ currentPrediction.predicted_digit }}</div>
              <div class="prediction-meta">
                <div>
                  <span>Confidence</span>
                  <strong>{{ (currentPrediction.confidence * 100).toFixed(1) }}%</strong>
                </div>
                <div>
                  <span>Processing Time</span>
                  <strong>{{ currentPrediction.processing_time }} ms</strong>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <div class="placeholder-icon">?</div>
              <p>Draw a digit to see prediction.</p>
            </div>
          </div>

          <div class="card feedback-card">
            <div class="panel-header">
              <h2>Feedback</h2>
              <span v-if="!currentPrediction">Predict first to share feedback</span>
            </div>
            <div v-if="currentPrediction" class="feedback-buttons">
              <button 
                class="feedback-btn correct" 
                @click="submitFeedback(true)"
                :disabled="feedbackSubmitted || !currentPrediction.history_id"
              >
                ✓ Correct
              </button>
              <button 
                class="feedback-btn incorrect" 
                @click="submitFeedback(false)"
                :disabled="feedbackSubmitted || !currentPrediction.history_id"
              >
                ✗ Incorrect
              </button>
              <small v-if="!currentPrediction.history_id">
                Login to enable feedback logging.
              </small>
            </div>
            <div v-else class="empty-state small">
              Provide feedback once a prediction is available.
            </div>
            <div v-if="feedbackSubmitted" class="feedback-thanks">
              Thank you for your feedback!
            </div>
          </div>
        </div>

        <div class="card confidence-card">
          <div class="panel-header">
            <h2>Confidence Distribution</h2>
            <span v-if="currentPrediction">Highest bar marks predicted digit.</span>
          </div>
          <div class="confidence-bars">
            <div 
              v-for="i in 10" 
              :key="i"
              class="confidence-bar-container"
            >
              <div class="digit-label">{{ i-1 }}</div>
              <div class="confidence-bar-background">
                <div 
                  class="confidence-bar-fill"
                  :class="{ 'highest': currentPrediction && (i-1) === currentPrediction.predicted_digit }"
                  :style="{ 
                    width: currentPrediction && currentPrediction.confidence_distribution 
                      ? `${currentPrediction.confidence_distribution[i-1] * 100}%` 
                      : '0%' 
                  }"
                ></div>
              </div>
              <div class="confidence-percent">
                {{ currentPrediction && currentPrediction.confidence_distribution 
                  ? `${(currentPrediction.confidence_distribution[i-1] * 100).toFixed(1)}%` 
                  : '0.0%' }}
              </div>
            </div>
          </div>
        </div>

        <div class="card history-card">
          <div class="panel-header">
            <h2>Recent Predictions</h2>
            <span>Most recent interactions in this session.</span>
          </div>
          <div class="predictions-list">
            <div 
              v-for="prediction in recentPredictions" 
              :key="prediction.id"
              class="prediction-item"
            >
              <div class="prediction-info">
                <span class="prediction-digit">{{ prediction.predicted_digit }}</span>
                <span class="prediction-confidence">
                  {{ (prediction.confidence * 100).toFixed(1) }}%
                </span>
              </div>
              <div class="prediction-meta">
                <span class="prediction-time">{{ formatTime(prediction.timestamp) }}</span>
                <span 
                  class="prediction-status"
                  :class="prediction.correct ? 'correct' : 'incorrect'"
                >
                  {{ prediction.correct === null ? '-' : prediction.correct ? '✓' : '✗' }}
                </span>
              </div>
            </div>
            <div v-if="recentPredictions.length === 0" class="no-history">
              Predictions will show here as you interact.
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import api from '@/services/api'

export default {
  name: 'DigitRecognition',
  setup() {
    const canvas = ref(null)
    const ctx = ref(null)
    const isDrawing = ref(false)
    const loading = ref(false)
    const currentPrediction = ref(null)
    const feedbackSubmitted = ref(false)
    const recentPredictions = ref([])
    const systemStats = ref({
      totalPredictions: 0,
      accuracy: 0,
      activeModels: 0,
      feedbackCount: 0
    })
    const insightsLoading = ref(false)

    onMounted(() => {
      initCanvas()
      loadInsights()
    })

    const initCanvas = () => {
      const canvasEl = canvas.value
      ctx.value = canvasEl.getContext('2d')
      
      // Set white background
      ctx.value.fillStyle = '#ffffff'
      ctx.value.fillRect(0, 0, canvasEl.width, canvasEl.height)
      ctx.value.strokeStyle = '#000000'
      ctx.value.lineWidth = 15
      ctx.value.lineCap = 'round'
      ctx.value.lineJoin = 'round'
    }

    const startDrawing = (event) => {
      isDrawing.value = true
      ctx.value.beginPath()
      draw(event)
    }

    const draw = (event) => {
      if (!isDrawing.value) return
      
      const canvasEl = canvas.value
      const rect = canvasEl.getBoundingClientRect()
      const scaleX = canvasEl.width / rect.width
      const scaleY = canvasEl.height / rect.height
      
      const x = (event.clientX - rect.left) * scaleX
      const y = (event.clientY - rect.top) * scaleY
      
      ctx.value.lineTo(x, y)
      ctx.value.stroke()
    }

    const stopDrawing = () => {
      isDrawing.value = false
      ctx.value.beginPath()
    }

    const clearCanvas = () => {
      const canvasEl = canvas.value
      ctx.value.fillStyle = '#ffffff'
      ctx.value.fillRect(0, 0, canvasEl.width, canvasEl.height)
      ctx.value.strokeStyle = '#000000'
      currentPrediction.value = null
      feedbackSubmitted.value = false
    }

    const startDrawingTouch = (event) => {
      event.preventDefault()
      const touch = event.touches[0]
      const mouseEvent = new MouseEvent('mousedown', {
        clientX: touch.clientX,
        clientY: touch.clientY
      })
      canvas.value.dispatchEvent(mouseEvent)
    }

    const drawTouch = (event) => {
      event.preventDefault()
      const touch = event.touches[0]
      const mouseEvent = new MouseEvent('mousemove', {
        clientX: touch.clientX,
        clientY: touch.clientY
      })
      canvas.value.dispatchEvent(mouseEvent)
    }

    const loadInsights = async () => {
      insightsLoading.value = true
      try {
        const response = await api.get('/admin/dashboard')
        const data = response.data || {}
        systemStats.value = {
          totalPredictions: data.totalPredictions || data.totalRecognitions || 0,
          accuracy: data.accuracy || data.accuracyRate || 0,
          activeModels: data.activeModels || 0,
          feedbackCount: data.feedbackCount || 0
        }
      } catch (error) {
        console.warn('Failed to load insights', error)
      } finally {
        insightsLoading.value = false
      }
    }

    const predictDigit = async () => {
      loading.value = true
      feedbackSubmitted.value = false
      
      try {
        // Get image data in the format expected by Flask backend
        const imageData = JSON.stringify(getImageDataArray())

        const response = await api.post('/recognition/predict', {
          imageData,
          inputType: 'drawing'
        })

        const payload = response.data
        currentPrediction.value = {
          predicted_digit: payload.predictedDigit,
          confidence: payload.confidence,
          processing_time: payload.processingTimeMs || 0,
          confidence_distribution: payload.confidenceDistribution || Array(10).fill(0),
          model_used: payload.modelUsed,
          history_id: payload.historyId || null
        }
        
        // Add to recent predictions
        recentPredictions.value.unshift({
          id: Date.now(),
          predicted_digit: payload.predictedDigit,
          confidence: payload.confidence,
          timestamp: new Date(),
          correct: null,
          backend: 'spring'
        })
        
        // Keep only last 10 predictions
        if (recentPredictions.value.length > 10) {
          recentPredictions.value = recentPredictions.value.slice(0, 10)
        }
        
      } catch (error) {
        console.error('Prediction failed:', error)
        alert(`Prediction failed: ${error.response?.data?.message || error.message}`)
      } finally {
        loading.value = false
      }
    }

    const getImageDataArray = () => {
      // Create a temporary canvas to resize to 28x28 for MNIST
      const tempCanvas = document.createElement('canvas')
      const tempCtx = tempCanvas.getContext('2d')
      tempCanvas.width = 28
      tempCanvas.height = 28
      
      // Draw and resize with smoothing disabled for crisp scaling
      tempCtx.imageSmoothingEnabled = false
      tempCtx.drawImage(canvas.value, 0, 0, 28, 28)
      
      // Get image data
      const imageData = tempCtx.getImageData(0, 0, 28, 28)
      
      // Convert to grayscale array (MNIST format) and invert colors
      const grayscale = []
      for (let i = 0; i < imageData.data.length; i += 4) {
        const r = imageData.data[i]
        const g = imageData.data[i + 1]
        const b = imageData.data[i + 2]
        // Convert to grayscale, invert (MNIST expects white on black), and normalize to 0-1
        const gray = 1 - ((r + g + b) / 3 / 255.0)
        grayscale.push(gray)
      }
      
      console.log('Processed image data:', grayscale.length, 'pixels')
      return grayscale
    }
    const submitFeedback = async (isCorrect) => {
      if (!currentPrediction.value?.history_id) {
        alert('Login is required to record feedback.')
        return
      }

      let actualDigit = currentPrediction.value.predicted_digit
      if (!isCorrect) {
        const userInput = prompt('Please enter the correct digit (0-9)')
        if (userInput === null) return
        const parsed = parseInt(userInput, 10)
        if (Number.isNaN(parsed) || parsed < 0 || parsed > 9) {
          alert('Enter a digit between 0 and 9')
          return
        }
        actualDigit = parsed
      }

      try {
        await api.post('/recognition/feedback', {
          historyId: currentPrediction.value.history_id,
          actualDigit
        })
        feedbackSubmitted.value = true
        if (recentPredictions.value.length) {
          recentPredictions.value[0].correct = isCorrect
        }
      } catch (error) {
        console.error('Feedback submission failed:', error)
        alert('Unable to save feedback at the moment.')
      }
    }

    const loadTestDigit = async (digit) => {
      clearCanvas()
      
      // For demo purposes - you could implement actual test digit loading
      // This would require pre-drawn test images
      setTimeout(() => {
        currentPrediction.value = {
          predicted_digit: digit,
          confidence: 0.95,
          processing_time: 25,
          confidence_distribution: Array.from({length: 10}, (_, i) => 
            i === digit ? 0.95 : 0.05 / 9
          )
        }
        
        recentPredictions.value.unshift({
          id: Date.now(),
          predicted_digit: digit,
          confidence: 0.95,
          timestamp: new Date(),
          correct: null,
          backend: 'demo'
        })
      }, 500)
    }

    const formatTime = (timestamp) => {
      const now = new Date()
      const diff = now - new Date(timestamp)
      const minutes = Math.floor(diff / 60000)
      if (minutes < 1) return 'Just now'
      if (minutes < 60) return `${minutes}m ago`
      const hours = Math.floor(minutes / 60)
      if (hours < 24) return `${hours}h ago`
      return new Date(timestamp).toLocaleDateString()
    }

    return {
      canvas,
      isDrawing,
      loading,
      currentPrediction,
      feedbackSubmitted,
      recentPredictions,
      systemStats,
      insightsLoading,
      loadInsights,
      startDrawing,
      draw,
      stopDrawing,
      clearCanvas,
      startDrawingTouch,
      drawTouch,
      predictDigit,
      submitFeedback,
      loadTestDigit,
      formatTime
    }
  }
}
</script>
<style scoped>
.digit-recognition {
  padding: 24px;
  min-height: 100vh;
  background: #0f172a;
  color: #e2e8f0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  margin: 0;
}

.page-header p {
  color: #94a3b8;
  margin: 4px 0 0;
}

.header-actions .ghost-btn {
  border: 1px solid #1f2937;
  background: transparent;
  color: #e2e8f0;
  padding: 8px 14px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}
.header-actions .ghost-btn:hover {
  background: #1f2937;
}

.insights {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.insight-card {
  background: #111827;
  border: 1px solid #1f2937;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.insight-card span {
  color: #94a3b8;
  font-size: 14px;
}
.insight-card strong {
  font-size: 24px;
}
.insight-card.loading-card {
  text-align: center;
  color: #94a3b8;
}

.recognition-grid {
  display: grid;
  grid-template-columns: minmax(320px, 420px) minmax(0, 1fr);
  gap: 24px;
  align-items: flex-start;
}

.drawing-panel,
.analysis-panel .card {
  background: #111827;
  border: 1px solid #1f2937;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 30px rgb(15 23 42 / 0.35);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.panel-header h2 {
  margin: 0;
  font-size: 18px;
}
.panel-header span {
  font-size: 12px;
  color: #94a3b8;
}

.canvas-container {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  border: 1px dashed #1f2937;
  margin-bottom: 16px;
  background: #0b1120;
}

.drawing-canvas {
  width: 100%;
  height: auto;
  display: block;
  cursor: crosshair;
}

.canvas-overlay {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.grid-overlay {
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(#1f2937 1px, transparent 1px),
    linear-gradient(90deg, #1f2937 1px, transparent 1px);
  background-size: 28px 28px;
  opacity: 0.4;
}

.drawing-controls {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.control-btn {
  flex: 1;
  border: none;
  border-radius: 10px;
  padding: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, background 0.2s;
}

.control-btn.secondary {
  background: #1f2937;
  color: #e2e8f0;
  border: 1px solid #1f2937;
}

.control-btn.primary {
  background: #16a34a;
  color: white;
}

.control-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top-color: #fff;
  border-radius: 50%;
  margin-right: 6px;
  display: inline-block;
  animation: spin 1s linear infinite;
}

.quick-actions h3 {
  margin-bottom: 12px;
  font-size: 16px;
}

.quick-digits {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
}

.digit-btn {
  border: 1px solid #1f2937;
  border-radius: 10px;
  background: #0b1120;
  color: #e2e8f0;
  font-size: 16px;
  padding: 10px;
  cursor: pointer;
  transition: background 0.2s;
}
.digit-btn:hover {
  background: #1f2937;
}

.analysis-panel .flex-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.prediction-result {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.predicted-digit {
  font-size: 72px;
  font-weight: 700;
  color: #fbbf24;
}

.prediction-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
  text-align: right;
}

.prediction-meta span {
  color: #94a3b8;
  font-size: 12px;
}
.prediction-meta strong {
  font-size: 18px;
}

.empty-state {
  text-align: center;
  color: #94a3b8;
}
.empty-state.small {
  font-size: 14px;
}

.confidence-bars {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.confidence-bar-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.digit-label {
  width: 24px;
  font-weight: 600;
}

.confidence-bar-background {
  flex: 1;
  height: 16px;
  background: #0b1120;
  border-radius: 999px;
  border: 1px solid #1f2937;
}

.confidence-bar-fill {
  height: 100%;
  border-radius: 999px;
  background: #22d3ee;
  transition: width 0.3s ease;
}
.confidence-bar-fill.highest {
  background: #06b6d4;
}

.confidence-percent {
  width: 55px;
  font-size: 12px;
  text-align: right;
}

.feedback-buttons {
  display: flex;
  gap: 12px;
  align-items: center;
}
.feedback-btn {
  flex: 1;
  border-radius: 10px;
  border: 1px solid #1f2937;
  padding: 10px;
  background: #0b1120;
}
.feedback-btn.correct:hover:not(:disabled) {
  background: #064e3b;
  color: #6ee7b7;
}
.feedback-btn.incorrect:hover:not(:disabled) {
  background: #7f1d1d;
  color: #fecaca;
}

.history-card .prediction-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #1f2937;
}
.prediction-item:last-child {
  border-bottom: none;
}
.prediction-info {
  display: flex;
  gap: 12px;
  align-items: center;
}
.prediction-digit {
  font-size: 24px;
  font-weight: 600;
}
.prediction-meta {
  display: flex;
  gap: 12px;
  align-items: center;
  font-size: 12px;
  color: #94a3b8;
}
.prediction-status {
  padding: 2px 8px;
  border-radius: 20px;
  background: #1f2937;
}
.prediction-status.correct {
  color: #22c55e;
}
.prediction-status.incorrect {
  color: #f87171;
}

.no-history {
  text-align: center;
  padding: 12px 0;
  color: #94a3b8;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 1024px) {
  .recognition-grid {
    grid-template-columns: 1fr;
  }
}
</style>
