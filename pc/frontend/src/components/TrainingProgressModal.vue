<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ title }}</h3>
        <button class="modal-close" @click="$emit('close')">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"/>
            <line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>
      <div class="modal-body">
        <div class="chart-container">
          <canvas ref="chartCanvas"></canvas>
        </div>
        <div class="progress-details">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: `${progress}%` }"></div>
          </div>
          <div class="progress-text">
            Epoch {{ currentEpoch }}/{{ totalEpochs }} ({{ progress }}%)
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn-danger" @click="$emit('stop')" :disabled="isStopping">
          <span v-if="isStopping" class="loading-spinner-small"></span>
          {{ isStopping ? 'Stopping...' : 'Stop Training' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import Chart from 'chart.js/auto'

export default {
  name: 'TrainingProgressModal',
  props: {
    title: {
      type: String,
      default: 'Training Progress'
    },
    progress: {
      type: Number,
      default: 0
    },
    currentEpoch: {
      type: Number,
      default: 0
    },
    totalEpochs: {
      type: Number,
      default: 0
    },
    chartData: {
      type: Object,
      default: () => ({
        labels: [],
        datasets: [
          {
            label: 'Training Loss',
            data: [],
            borderColor: '#059669',
            tension: 0.1
          },
          {
            label: 'Validation Loss',
            data: [],
            borderColor: '#f59e0b',
            tension: 0.1
          }
        ]
      })
    },
    isStopping: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close', 'stop'],
  setup(props) {
    const chartCanvas = ref(null)
    let chartInstance = null

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        x: {
          title: {
            display: true,
            text: 'Epoch'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Loss'
          }
        }
      }
    }

    onMounted(() => {
      if (chartCanvas.value) {
        chartInstance = new Chart(chartCanvas.value, {
          type: 'line',
          data: props.chartData,
          options: chartOptions
        })
      }
    })

    onUnmounted(() => {
      if (chartInstance) {
        chartInstance.destroy()
      }
    })

    watch(() => props.chartData, (newData) => {
      if (chartInstance) {
        chartInstance.data = newData
        chartInstance.update()
      }
    }, { deep: true })

    return {
      chartCanvas
    }
  }
}
</script>

<style scoped>
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
  max-width: 700px;
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

.chart-container {
  position: relative;
  height: 300px;
  margin-bottom: 24px;
}

.progress-details {
  margin-top: 16px;
}

.progress-bar {
  height: 8px;
  background: #f1f5f9;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #059669, #10b981);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 14px;
  color: #64748b;
  text-align: center;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-danger {
  background: #ef4444;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-danger:hover:not(:disabled) {
  background: #dc2626;
}

.btn-danger:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner-small {
  width: 12px;
  height: 12px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  display: inline-block;
  margin-right: 4px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
