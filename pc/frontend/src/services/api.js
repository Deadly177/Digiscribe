import axios from 'axios'

// Build base URL from env (falls back to local). Trim spaces and trailing slash.
const rawBase = (import.meta.env.VITE_API_BASE || 'http://localhost:8081/api').trim()
const normalizedBase = rawBase.endsWith('/')
  ? rawBase.slice(0, -1)
  : rawBase
// Ensure we end with /api
const baseURL = normalizedBase.endsWith('/api')
  ? normalizedBase
  : `${normalizedBase}/api`

const api = axios.create({
  baseURL,
  timeout: 10000,
})

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized
      localStorage.removeItem('authToken')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
