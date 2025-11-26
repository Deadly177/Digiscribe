import axios from 'axios'

const flaskApi = axios.create({
  baseURL: 'http://localhost:5000', // Your Flask server
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
})

// Request interceptor
flaskApi.interceptors.request.use(
  (config) => {
    console.log(`Making ${config.method?.toUpperCase()} request to Flask: ${config.url}`)
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
flaskApi.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    console.error('Flask API Error:', error)
    if (error.code === 'ECONNREFUSED') {
      console.error('Flask server is not running on localhost:5000')
    }
    return Promise.reject(error)
  }
)

export default flaskApi