<template>
  <div class="model-management">
    <div class="page-header">
      <div class="header-content">
        <h1>AI Models</h1>
        <p>Manage and train your digit recognition models</p>
      </div>
      <button class="create-model-btn" @click="showCreateModal = true">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        Create New Model
      </button>
    </div>

    <!-- Models Grid -->
    <div class="models-grid">
      <div 
        v-for="model in models" 
        :key="model.id"
        class="model-card"
        :class="{ 'active': model.status === 'active', 'training': model.status === 'training' }"
      >
        <div class="model-header">
          <div class="model-info">
            <h3 class="model-name">{{ model.name }}</h3>
            <span class="model-version">v{{ model.version }}</span>
          </div>
          <div class="model-actions">
            <button class="action-btn" @click="viewModelDetails(model)">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
            <button class="action-btn" @click="editModel(model)">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
            </button>
          </div>
        </div>

        <div class="model-stats">
          <div class="stat">
            <div class="stat-value">{{ model.accuracy }}%</div>
            <div class="stat-label">Accuracy</div>
          </div>
          <div class="stat">
            <div class="stat-value">{{ model.prediction_count.toLocaleString() }}</div>
            <div class="stat-label">Predictions</div>
          </div>
          <div class="stat">
            <div class="stat-value">{{ model.training_samples }}</div>
            <div class="stat-label">Training Samples</div>
          </div>
        </div>

        <div class="model-status">
          <span class="status-badge" :class="model.status">
            {{ formatStatus(model.status) }}
          </span>
          <span class="last-trained">
            Updated {{ formatTime(model.last_trained) }}
          </span>
        </div>

        <div class="model-actions-full">
          <button 
            v-if="model.status !== 'active'" 
            class="btn-primary"
            @click="activateModel(model)"
            :disabled="model.status === 'training'"
          >
            Activate
          </button>
          <button 
            v-else 
            class="btn-secondary"
            disabled
          >
            Active
          </button>
          
          <button 
            class="btn-outline"
            @click="startTraining(model)"
            :disabled="model.status === 'training'"
          >
            <span v-if="model.status === 'training'" class="loading-spinner-small"></span>
            {{ model.status === 'training' ? 'Training...' : 'Retrain' }}
          </button>
        </div>

        <!-- Training Progress -->
        <div v-if="model.status === 'training'" class="training-progress">
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ width: `${model.training_progress}%` }"
            ></div>
          </div>
          <div class="progress-text">
            Epoch {{ model.current_epoch }}/{{ model.total_epochs }} ({{ model.training_progress }}%)
          </div>
        </div>
      </div>
    </div>

    <!-- Performance Metrics -->
    <div class="metrics-section">
      <h2>Model Performance</h2>
      <div class="metrics-grid">
        <div class="metric-card">
          <div class="metric-value">{{ overallAccuracy }}%</div>
          <div class="metric-label">Overall Accuracy</div>
          <div class="metric-trend positive">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="18 15 12 9 6 15"/>
            </svg>
            +2.3%
          </div>
        </div>
        
        <div class="metric-card">
          <div class="metric-value">{{ activeModels }}</div>
          <div class="metric-label">Active Models</div>
          <div class="metric-trend neutral">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            No change
          </div>
        </div>
        
        <div class="metric-card">
          <div class="metric-value">{{ totalPredictions.toLocaleString() }}</div>
          <div class="metric-label">Total Predictions</div>
          <div class="metric-trend positive">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="18 15 12 9 6 15"/>
            </svg>
            +15.7%
          </div>
        </div>
        
        <div class="metric-card">
          <div class="metric-value">{{ trainingSamples.toLocaleString() }}</div>
          <div class="metric-label">Training Samples</div>
          <div class="metric-trend positive">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="18 15 12 9 6 15"/>
            </svg>
            +8.2%
          </div>
        </div>
      </div>
    </div>

    <!-- Accuracy by Digit -->
    <div class="accuracy-section">
      <h2>Accuracy by Digit</h2>
      <div class="accuracy-chart">
        <div 
          v-for="digit in digitAccuracy" 
          :key="digit.digit"
          class="accuracy-bar-container"
        >
          <div class="digit-label">{{ digit.digit }}</div>
          <div class="accuracy-bar">
            <div 
              class="accuracy-fill"
              :style="{ width: `${digit.accuracy}%` }"
              :class="getAccuracyClass(digit.accuracy)"
            ></div>
          </div>
          <div class="accuracy-value">{{ digit.accuracy }}%</div>
        </div>
      </div>
    </div>

    <!-- Create Model Wizard -->
    <div v-if="showCreateModal" class="modal-overlay" @click="showCreateModal = false">
      <div class="modal-content wizard-content" @click.stop>
        <!-- Step 1: Basic Information -->
        <div v-if="currentStep === 1" class="wizard-step">
          <div class="modal-header">
            <h3>Create New Model - Basic Information</h3>
            <button class="modal-close" @click="showCreateModal = false">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
          
          <div class="modal-body">
            <div class="step-explanation">
              <h4>Model Name & Purpose</h4>
              <p>Give your model a descriptive name that helps you identify its purpose. This name will be displayed in your model list and used for reference.</p>
            </div>
            
            <div class="form-group">
              <label>Model Name</label>
              <input 
                v-model="newModel.name" 
                type="text" 
                placeholder="e.g., Production CNN v2.1"
                class="form-input"
              >
              <div class="form-help">Use a name that describes the model's purpose or version</div>
            </div>
            
            <div class="form-group">
              <label>Model Description (Optional)</label>
              <textarea 
                v-model="newModel.description"
                placeholder="Describe what makes this model special or its intended use case..."
                class="form-input"
                rows="3"
              ></textarea>
            </div>
          </div>
          
          <div class="modal-footer">
            <button class="btn-secondary" @click="showCreateModal = false">
              Cancel
            </button>
            <button class="btn-primary" @click="nextStep" :disabled="!newModel.name">
              Next: Architecture
            </button>
          </div>
        </div>

        <!-- Step 2: Architecture Selection -->
        <div v-if="currentStep === 2" class="wizard-step">
          <div class="modal-header">
            <h3>Choose Model Architecture</h3>
            <button class="modal-close" @click="showCreateModal = false">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
          
          <div class="modal-body">
            <div class="step-explanation">
              <h4>Select Architecture Type</h4>
              <p>Choose the neural network architecture that best fits your needs. Different architectures offer varying balances of accuracy, speed, and resource requirements.</p>
            </div>
            
            <div class="architecture-options">
              <div 
                v-for="arch in architectureOptions" 
                :key="arch.value"
                class="architecture-option"
                :class="{ 'selected': newModel.architecture === arch.value }"
                @click="newModel.architecture = arch.value"
              >
                <div class="arch-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path :d="arch.icon"/>
                  </svg>
                </div>
                <div class="arch-info">
                  <h5>{{ arch.name }}</h5>
                  <p>{{ arch.description }}</p>
                  <div class="arch-specs">
                    <span class="spec-tag" :class="arch.speed.class">{{ arch.speed.label }}</span>
                    <span class="spec-tag" :class="arch.accuracy.class">{{ arch.accuracy.label }}</span>
                    <span class="spec-tag" :class="arch.complexity.class">{{ arch.complexity.label }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="modal-footer">
            <button class="btn-secondary" @click="prevStep">
              Back
            </button>
            <button class="btn-primary" @click="nextStep">
              Next: Training Settings
            </button>
          </div>
        </div>

        <!-- Step 3: Training Configuration -->
        <div v-if="currentStep === 3" class="wizard-step">
          <div class="modal-header">
            <h3>Training Configuration</h3>
            <button class="modal-close" @click="showCreateModal = false">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
          
          <div class="modal-body">
            <div class="step-explanation">
              <h4>Configure Training Parameters</h4>
              <p>Adjust these settings to control how your model learns. The right balance depends on your data and performance requirements.</p>
            </div>
            
            <div class="training-config-grid">
              <div class="form-group">
                <label>Training Epochs</label>
                <input 
                  v-model="newModel.epochs" 
                  type="number" 
                  min="1" 
                  max="100"
                  class="form-input"
                >
                <div class="form-help">
                  Number of complete passes through the training dataset.
                  <br>Recommended: 10-50 epochs
                </div>
              </div>
              
              <div class="form-group">
                <label>Learning Rate</label>
                <input 
                  v-model="newModel.learning_rate" 
                  type="number" 
                  step="0.001"
                  min="0.001"
                  max="0.1"
                  class="form-input"
                >
                <div class="form-help">
                  How quickly the model adapts to the data.
                  <br>Lower = more precise, Higher = faster training
                </div>
              </div>
              
              <div class="form-group">
                <label>Batch Size</label>
                <input 
                  v-model="newModel.batch_size" 
                  type="number" 
                  min="32"
                  max="512"
                  step="32"
                  class="form-input"
                >
                <div class="form-help">
                  Number of samples processed before model update.
                  <br>Smaller = more updates, Larger = faster training
                </div>
              </div>
              
              <div class="form-group full-width">
                <label class="checkbox-container">
                  <input type="checkbox" v-model="newModel.use_pretrained">
                  <span class="checkmark"></span>
                  Use pre-trained weights
                </label>
                <div class="form-help">
                  Start with weights from existing models for faster convergence.
                  Recommended for most use cases.
                </div>
              </div>
            </div>
          </div>
          
          <div class="modal-footer">
            <button class="btn-secondary" @click="prevStep">
              Back
            </button>
            <button class="btn-primary" @click="nextStep">
              Next: Review & Create
            </button>
          </div>
        </div>

        <!-- Step 4: Review & Create -->
        <div v-if="currentStep === 4" class="wizard-step">
          <div class="modal-header">
            <h3>Review & Create Model</h3>
            <button class="modal-close" @click="showCreateModal = false">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
          
          <div class="modal-body">
            <div class="step-explanation">
              <h4>Confirm Your Settings</h4>
              <p>Review all configuration before creating your model. You can go back to modify any settings.</p>
            </div>
            
            <div class="review-summary">
              <div class="review-section">
                <h5>Basic Information</h5>
                <div class="review-item">
                  <span class="review-label">Model Name:</span>
                  <span class="review-value">{{ newModel.name }}</span>
                </div>
                <div v-if="newModel.description" class="review-item">
                  <span class="review-label">Description:</span>
                  <span class="review-value">{{ newModel.description }}</span>
                </div>
              </div>
              
              <div class="review-section">
                <h5>Architecture</h5>
                <div class="review-item">
                  <span class="review-label">Architecture:</span>
                  <span class="review-value">{{ getArchitectureName(newModel.architecture) }}</span>
                </div>
              </div>
              
              <div class="review-section">
                <h5>Training Configuration</h5>
                <div class="review-item">
                  <span class="review-label">Epochs:</span>
                  <span class="review-value">{{ newModel.epochs }}</span>
                </div>
                <div class="review-item">
                  <span class="review-label">Learning Rate:</span>
                  <span class="review-value">{{ newModel.learning_rate }}</span>
                </div>
                <div class="review-item">
                  <span class="review-label">Batch Size:</span>
                  <span class="review-value">{{ newModel.batch_size }}</span>
                </div>
                <div class="review-item">
                  <span class="review-label">Pre-trained Weights:</span>
                  <span class="review-value">{{ newModel.use_pretrained ? 'Yes' : 'No' }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="modal-footer">
            <button class="btn-secondary" @click="prevStep">
              Back
            </button>
            <button class="btn-primary" @click="createModel" :disabled="!newModel.name">
              Create Model
            </button>
          </div>
        </div>

        <!-- Progress Indicator -->
        <div class="wizard-progress">
          <div 
            v-for="step in 4" 
            :key="step"
            class="progress-step"
            :class="{ 
              'active': step === currentStep, 
              'completed': step < currentStep 
            }"
          >
            <div class="step-number">{{ step }}</div>
            <div class="step-label">{{ getStepLabel(step) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import api from '@/services/api'

export default {
  name: 'TrainModel',
  setup() {
    const showCreateModal = ref(false)
    const currentStep = ref(1)
    const loading = ref(false)
    const trainingPolls = new Map()
    
    // Real models data from backend
    const models = ref([])

    const newModel = ref({
      name: '',
      description: '',
      architecture: 'cnn_simple',
      epochs: 10,
      learning_rate: 0.001,
      batch_size: 128,
      use_pretrained: true
    })

    // Architecture options with detailed information
    const architectureOptions = ref([
      {
        value: 'cnn_simple',
        name: 'Simple CNN',
        description: 'Basic convolutional network ideal for getting started and simple digit recognition tasks.',
        icon: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z',
        speed: { label: 'Fast', class: 'speed-fast' },
        accuracy: { label: 'Good', class: 'accuracy-good' },
        complexity: { label: 'Simple', class: 'complexity-low' }
      },
      {
        value: 'cnn_advanced',
        name: 'Advanced CNN',
        description: 'Enhanced convolutional network with deeper layers for improved accuracy on complex patterns.',
        icon: 'M13 10V3L4 14h7v7l9-11h-7z',
        speed: { label: 'Medium', class: 'speed-medium' },
        accuracy: { label: 'Very Good', class: 'accuracy-very-good' },
        complexity: { label: 'Medium', class: 'complexity-medium' }
      },
      {
        value: 'resnet',
        name: 'ResNet',
        description: 'Residual network architecture with skip connections, excellent for deep networks and high accuracy.',
        icon: 'M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z',
        speed: { label: 'Slow', class: 'speed-slow' },
        accuracy: { label: 'Excellent', class: 'accuracy-excellent' },
        complexity: { label: 'High', class: 'complexity-high' }
      },
      {
        value: 'custom',
        name: 'Custom Architecture',
        description: 'Build your own architecture with custom layers and configurations for specialized use cases.',
        icon: 'M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z',
        speed: { label: 'Variable', class: 'speed-variable' },
        accuracy: { label: 'Variable', class: 'accuracy-variable' },
        complexity: { label: 'Expert', class: 'complexity-expert' }
      }
    ])

    // Real digit accuracy data
    const digitAccuracy = ref([])

    // Computed properties
    const overallAccuracy = computed(() => {
      if (digitAccuracy.value.length === 0) return 0
      const avg = digitAccuracy.value.reduce((sum, digit) => sum + digit.accuracy, 0) / digitAccuracy.value.length
      return avg.toFixed(1)
    })

    const activeModels = computed(() => {
      return models.value.filter(model => model.status === 'active').length
    })

    const totalPredictions = computed(() => {
      return models.value.reduce((sum, model) => sum + model.prediction_count, 0)
    })

    const trainingSamples = computed(() => {
      return models.value.reduce((sum, model) => sum + model.training_samples, 0)
    })

    // Wizard navigation methods
    const nextStep = () => {
      if (currentStep.value < 4) {
        currentStep.value++
      }
    }

    const prevStep = () => {
      if (currentStep.value > 1) {
        currentStep.value--
      }
    }

    const getStepLabel = (step) => {
      const labels = {
        1: 'Basic Info',
        2: 'Architecture',
        3: 'Training',
        4: 'Review'
      }
      return labels[step]
    }

    const getArchitectureName = (archValue) => {
      const arch = architectureOptions.value.find(a => a.value === archValue)
      return arch ? arch.name : archValue
    }

    const resetWizard = () => {
      currentStep.value = 1
      newModel.value = {
        name: '',
        description: '',
        architecture: 'cnn_simple',
        epochs: 10,
        learning_rate: 0.001,
        batch_size: 128,
        use_pretrained: true
      }
    }

    // Load data on component mount
    onMounted(() => {
      loadModels()
      loadAccuracyData()
    })

    onUnmounted(() => {
      trainingPolls.forEach(clearInterval)
      trainingPolls.clear()
    })

    // API Methods
    const loadModels = async (options = {}) => {
      const { silent = false } = options
      try {
        if (!silent) {
          loading.value = true
        }
        const response = await api.get('/models')
        models.value = response.data
      } catch (error) {
        console.error('Failed to load models:', error)
        models.value = getMockModels()
      } finally {
        syncTrainingWatchers()
        if (!silent) {
          loading.value = false
        }
      }
    }

    const loadAccuracyData = async () => {
      try {
        const response = await api.get('/models/accuracy-by-digit')
        digitAccuracy.value = response.data
      } catch (error) {
        console.error('Failed to load accuracy data:', error)
        // Fallback to mock data
        digitAccuracy.value = getMockAccuracyData()
      }
    }

    const createModel = async () => {
      try {
        loading.value = true
        const response = await api.post('/models/create', newModel.value)
        
        // Add new model to the list
        models.value.push(response.data)
        showCreateModal.value = false
        resetWizard()
        
      } catch (error) {
        console.error('Failed to create model:', error)
        alert('Failed to create model. Please try again.')
      } finally {
        loading.value = false
      }
    }

    const activateModel = async (model) => {
      try {
        await api.post(`/models/${model.id}/activate`)
        
        // Update local state
        models.value.forEach(m => {
          if (m.id === model.id) {
            m.status = 'active'
          } else if (m.status === 'active') {
            m.status = 'idle'
          }
        })
        
      } catch (error) {
        console.error('Failed to activate model:', error)
        alert('Failed to activate model. Please try again.')
      }
    }

    const startTraining = async (model) => {
      try {
        model.status = 'training'
        model.training_progress = 0
        model.current_epoch = 0
        model.total_epochs = model.total_epochs || 0

        await api.post(`/models/${model.id}/train`)
        startTrainingWatcher(model.id)
      } catch (error) {
        console.error('Failed to start training:', error)
        model.status = 'error'
        alert('Failed to start training. Please try again.')
      }
    }

    const startTrainingWatcher = (modelId) => {
      if (trainingPolls.has(modelId)) return

      const pollInterval = setInterval(async () => {
        try {
          const progressResponse = await api.get(`/models/${modelId}/training-progress`)
          const progress = progressResponse.data || {}

          updateModelFromProgress(modelId, () => ({
            training_progress: typeof progress.percentage === 'number' ? progress.percentage : undefined,
            current_epoch: progress.current_epoch ?? undefined,
            total_epochs: progress.total_epochs ?? undefined,
            status: progress.status ?? undefined
          }))

          const finished = progress.status !== 'training' || (progress.percentage ?? 0) >= 100

          if (finished) {
            updateModelFromProgress(modelId, () => ({
              status: progress.status === 'error'
                ? 'error'
                : progress.status === 'active'
                  ? 'active'
                  : 'idle',
              training_progress: 100,
              current_epoch: progress.total_epochs ?? undefined,
              last_trained: new Date()
            }))
            stopTrainingWatcher(modelId)
            await loadModels({ silent: true })
          }
        } catch (error) {
          console.error('Failed to get training progress:', error)
          stopTrainingWatcher(modelId)
        }
      }, 3000)

      trainingPolls.set(modelId, pollInterval)
    }

    const stopTrainingWatcher = (modelId) => {
      const interval = trainingPolls.get(modelId)
      if (interval) {
        clearInterval(interval)
        trainingPolls.delete(modelId)
      }
    }

    const updateModelFromProgress = (modelId, updater) => {
      const target = models.value.find((m) => m.id === modelId)
      if (!target) return
      const updates = updater(target) || {}
      Object.keys(updates).forEach((key) => {
        if (updates[key] !== undefined) {
          target[key] = updates[key]
        }
      })
    }

    const syncTrainingWatchers = () => {
      models.value.forEach((model) => {
        if (model.status === 'training') {
          startTrainingWatcher(model.id)
        } else {
          stopTrainingWatcher(model.id)
        }
      })
    }

    const viewModelDetails = async (model) => {
      try {
        // Navigate to model details page or show modal with detailed info
        const response = await api.get(`/models/${model.id}/details`)
        console.log('Model details:', response.data)
        
        // You can show a detailed modal here with response.data
        showModelDetailsModal(response.data)
        
      } catch (error) {
        console.error('Failed to load model details:', error)
      }
    }

    const editModel = (model) => {
      // Open edit modal with model data
      console.log('Edit model:', model)
      // Implement edit functionality
    }

    const deleteModel = async (model) => {
      if (confirm(`Are you sure you want to delete "${model.name}"?`)) {
        try {
          await api.delete(`/models/${model.id}`)
          // Remove from local state
          models.value = models.value.filter(m => m.id !== model.id)
        } catch (error) {
          console.error('Failed to delete model:', error)
          alert('Failed to delete model. Please try again.')
        }
      }
    }

    // Helper methods
    const formatStatus = (status) => {
      const statusMap = {
        active: 'Active',
        idle: 'Idle',
        training: 'Training',
        error: 'Error'
      }
      return statusMap[status] || status
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

    const getAccuracyClass = (accuracy) => {
      if (accuracy >= 99) return 'excellent'
      if (accuracy >= 97) return 'good'
      if (accuracy >= 95) return 'fair'
      return 'poor'
    }

    // Mock data fallback
    const getMockModels = () => {
      return [
        {
          id: 1,
          name: 'CNN Basic',
          version: '1.2',
          status: 'active',
          accuracy: 98.2,
          prediction_count: 15420,
          training_samples: 60000,
          last_trained: new Date(Date.now() - 86400000),
          architecture: 'cnn_simple',
          training_progress: 0,
          current_epoch: 0,
          total_epochs: 10
        },
        {
          id: 2,
          name: 'ResNet Enhanced',
          version: '2.1',
          status: 'idle',
          accuracy: 99.1,
          prediction_count: 8920,
          training_samples: 70000,
          last_trained: new Date(Date.now() - 172800000),
          architecture: 'resnet',
          training_progress: 0,
          current_epoch: 0,
          total_epochs: 15
        }
      ]
    }

    const getMockAccuracyData = () => {
      return [
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
      ]
    }

    const showModelDetailsModal = (modelDetails) => {
      // Implement a detailed modal showing:
      // - Model architecture diagram
      // - Training history charts
      // - Performance metrics
      // - Confusion matrix
      console.log('Show detailed modal for:', modelDetails)
    }

    return {
      models,
      newModel,
      digitAccuracy,
      showCreateModal,
      currentStep,
      architectureOptions,
      loading,
      overallAccuracy,
      activeModels,
      totalPredictions,
      trainingSamples,
      formatStatus,
      formatTime,
      getAccuracyClass,
      nextStep,
      prevStep,
      getStepLabel,
      getArchitectureName,
      activateModel,
      startTraining,
      createModel,
      viewModelDetails,
      editModel,
      deleteModel
    }
  }
}
</script>

<style scoped>
.model-management {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.header-content h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}

.header-content p {
  color: #64748b;
  font-size: 16px;
  margin: 0;
}

.create-model-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #059669;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.create-model-btn:hover {
  background: #047857;
  transform: translateY(-1px);
}

/* Models Grid */
.models-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
  margin-bottom: 48px;
}

.model-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
  border: 2px solid transparent;
  transition: all 0.2s ease;
}

.model-card.active {
  border-color: #059669;
  background: #f0fdf4;
}

.model-card.training {
  border-color: #f59e0b;
}

.model-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.model-info h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.model-version {
  font-size: 12px;
  color: #64748b;
  background: #f1f5f9;
  padding: 2px 6px;
  border-radius: 4px;
}

.model-actions {
  display: flex;
  gap: 4px;
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

/* Model Stats */
.model-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat {
  text-align: center;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #059669;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
}

/* Model Status */
.model-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background: #d1fae5;
  color: #065f46;
}

.status-badge.idle {
  background: #f1f5f9;
  color: #475569;
}

.status-badge.training {
  background: #fef3c7;
  color: #92400e;
}

.last-trained {
  font-size: 12px;
  color: #64748b;
}

/* Model Actions */
.model-actions-full {
  display: flex;
  gap: 8px;
}

.btn-primary, .btn-secondary, .btn-outline {
  flex: 1;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary {
  background: #059669;
  color: white;
  border: none;
}

.btn-primary:hover:not(:disabled) {
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

.btn-outline:hover:not(:disabled) {
  border-color: #059669;
  color: #059669;
}

.btn-primary:disabled, .btn-outline:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Training Progress */
.training-progress {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.progress-bar {
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #f59e0b, #d97706);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 12px;
  color: #64748b;
  text-align: center;
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

/* Metrics Section */
.metrics-section {
  margin-bottom: 48px;
}

.metrics-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 24px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
  text-align: center;
}

.metric-value {
  font-size: 32px;
  font-weight: 700;
  color: #059669;
  margin-bottom: 8px;
}

.metric-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.metric-trend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.metric-trend.positive {
  color: #059669;
}

.metric-trend.neutral {
  color: #64748b;
}

/* Accuracy Section */
.accuracy-section {
  margin-bottom: 48px;
}

.accuracy-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 24px;
}

.accuracy-chart {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px 0 rgb(0 0 0 / 0.1);
}

.accuracy-bar-container {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.digit-label {
  width: 20px;
  font-weight: 600;
  color: #475569;
  font-size: 14px;
}

.accuracy-bar {
  flex: 1;
  height: 20px;
  background: #f1f5f9;
  border-radius: 10px;
  overflow: hidden;
}

.accuracy-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.3s ease;
}

.accuracy-fill.excellent {
  background: #059669;
}

.accuracy-fill.good {
  background: #10b981;
}

.accuracy-fill.fair {
  background: #f59e0b;
}

.accuracy-fill.poor {
  background: #ef4444;
}

.accuracy-value {
  width: 50px;
  font-size: 14px;
  color: #64748b;
  text-align: right;
}

/* Wizard Styles */
.wizard-content {
  max-width: 600px;
  width: 90%;
}

.wizard-step {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.step-explanation {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.step-explanation h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.step-explanation p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  line-height: 1.5;
}

/* Architecture Options */
.architecture-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.architecture-option {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.architecture-option:hover {
  border-color: #cbd5e1;
  background: #f8fafc;
}

.architecture-option.selected {
  border-color: #059669;
  background: #f0fdf4;
}

.arch-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  border-radius: 6px;
  color: #64748b;
}

.architecture-option.selected .arch-icon {
  background: #d1fae5;
  color: #059669;
}

.arch-info {
  flex: 1;
}

.arch-info h5 {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.arch-info p {
  font-size: 12px;
  color: #64748b;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.arch-specs {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.spec-tag {
  font-size: 10px;
  font-weight: 500;
  padding: 2px 6px;
  border-radius: 4px;
}

.speed-fast { background: #d1fae5; color: #065f46; }
.speed-medium { background: #fef3c7; color: #92400e; }
.speed-slow { background: #fee2e2; color: #991b1b; }
.speed-variable { background: #e0e7ff; color: #3730a3; }

.accuracy-good { background: #d1fae5; color: #065f46; }
.accuracy-very-good { background: #a7f3d0; color: #047857; }
.accuracy-excellent { background: #34d399; color: #065f46; }
.accuracy-variable { background: #e0e7ff; color: #3730a3; }

.complexity-low { background: #d1fae5; color: #065f46; }
.complexity-medium { background: #fef3c7; color: #92400e; }
.complexity-high { background: #fee2e2; color: #991b1b; }
.complexity-expert { background: #e0e7ff; color: #3730a3; }

/* Training Configuration Grid */
.training-config-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.training-config-grid .form-group.full-width {
  grid-column: 1 / -1;
}

/* Review Summary */
.review-summary {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-section {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
}

.review-section h5 {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 12px 0;
}

.review-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.review-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.review-value {
  font-size: 13px;
  color: #1e293b;
  font-weight: 600;
  text-align: right;
  max-width: 60%;
}

/* Wizard Progress */
.wizard-progress {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  background: #f8fafc;
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.step-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  background: #e5e7eb;
  color: #64748b;
  transition: all 0.2s ease;
}

.progress-step.active .step-number {
  background: #059669;
  color: white;
}

.progress-step.completed .step-number {
  background: #10b981;
  color: white;
}

.step-label {
  font-size: 10px;
  color: #64748b;
  font-weight: 500;
  text-align: center;
}

.progress-step.active .step-label {
  color: #059669;
  font-weight: 600;
}

.progress-step.completed .step-label {
  color: #10b981;
}

/* Form Help Text */
.form-help {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
  line-height: 1.4;
}

/* Modal Styles */
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

.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 24px;
  border-top: 1px solid #e5e7eb;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
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

textarea.form-input {
  resize: vertical;
  min-height: 80px;
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

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .models-grid {
    grid-template-columns: 1fr;
  }
  
  .metrics-grid {
    grid-template-columns: 1fr 1fr;
  }
  
  .model-stats {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .model-actions-full {
    flex-direction: column;
  }
  
  .training-config-grid {
    grid-template-columns: 1fr;
  }
  
  .wizard-progress {
    padding: 16px;
  }
  
  .step-label {
    font-size: 9px;
  }
  
  .architecture-option {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .arch-icon {
    align-self: flex-start;
  }
}
</style>
