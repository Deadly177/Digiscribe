import api from './api';

class MLService {
  // Predict digit via Spring Boot -> FastAPI gateway
  async predictDigit(imageData) {
    try {
      const payload = Array.isArray(imageData) ? JSON.stringify(imageData) : imageData
      const response = await api.post('/recognition/predict', {
        imageData: payload,
        inputType: 'drawing'
      })
      return response.data
    } catch (error) {
      console.error('Prediction error:', error)
      throw new Error(error.response?.data?.message || 'Failed to predict digit')
    }
  }

  async submitFeedback(historyId, actualDigit) {
    try {
      const response = await api.post('/recognition/feedback', {
        historyId,
        actualDigit
      })
      return response.data
    } catch (error) {
      console.error('Feedback error:', error)
      throw new Error(error.response?.data?.message || 'Failed to submit feedback')
    }
  }
}

export default new MLService()
