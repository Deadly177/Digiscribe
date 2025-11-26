import api from './api';

class AuthService {
  async login(credentials) {
    try {
      const response = await api.post('/auth/login', credentials);
      
      if (response.data.token) {
        const userSummary = {
          username: response.data.username,
          email: response.data.email,
          role: response.data.role
        }
        localStorage.setItem('authToken', response.data.token);
        localStorage.setItem('user', JSON.stringify(userSummary));
      }
      
      return response.data;
    } catch (error) {
      console.error('Login error:', error);
      throw new Error(error.response?.data?.message || 'Login failed');
    }
  }

  async register(userData) {
    try {
      const response = await api.post('/auth/register', userData);
      if (response.data.token) {
        const userSummary = {
          username: response.data.username,
          email: response.data.email,
          role: response.data.role
        }
        localStorage.setItem('authToken', response.data.token);
        localStorage.setItem('user', JSON.stringify(userSummary));
      }
      return response.data;
    } catch (error) {
      console.error('Registration error:', error);
      throw new Error(error.response?.data?.message || 'Registration failed');
    }
  }

  async logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }

  getCurrentUser() {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  isAuthenticated() {
    return !!localStorage.getItem('authToken');
  }
}

export default new AuthService();
