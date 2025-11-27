import { computed, reactive } from 'vue'

const STORAGE_KEY = 'digiscrib_locale'

const messages = {
  en: {
    brand: {
      appName: 'DigiScribe',
      tagline: 'AI Digit Recognition',
      welcome: 'Welcome Back',
      subtitle: 'Sign in to continue your digit recognition journey',
      feature1: 'MNIST-Powered Recognition System',
      feature2: 'Detailed Analytics',
      feature3: 'Created by Digicore'
    },
    login: {
      heading: 'Sign In',
      subheading: 'Enter your credentials to access your account',
      username: 'Username or Email',
      usernamePlaceholder: 'Enter your username or email',
      password: 'Password',
      passwordPlaceholder: 'Enter your password',
      forgot: 'Forgot password?',
      remember: 'Remember me',
      submit: 'Sign In',
      submitting: 'Signing In...',
      noAccount: "Don't have an account?",
      signUp: 'Sign up'
    },
    sidebar: {
      dashboard: 'Dashboard',
      recognition: 'Digit Recognition',
      models: 'Train Models ',
      analytics: 'Analytics',
      feedback: 'Feedback',
      admin: 'Admin Panel',
      main: 'Main',
      analyticsSection: 'Analytics',
      adminSection: 'Administration',
      returnDashboard: 'Home '
    },
    nav: {
      dashboard: 'Dashboard',
      notifications: 'Notifications',
      markRead: 'Mark all as read',
      empty: 'No notifications yet',
      profile: 'Profile',
      settings: 'Settings',
      signOut: 'Sign Out',
      guest: 'Guest',
      noEmail: 'No email linked',
      viewAll: 'View all'
    }
  },
  zh: {
    brand: {
      appName: 'DigiScribe',
      tagline: 'AI数字识别',
      welcome: '欢迎回来',
      subtitle: '登录以继续您的数字识别之旅',
      feature1: 'MNIST智能识别系统',
      feature2: '可视化分析面板',
      feature3: 'Digicore 开发'
    },
    login: {
      heading: '登录账户',
      subheading: '输入凭证以访问控制台',
      username: '用户名或邮箱',
      usernamePlaceholder: '请输入您的用户名或邮箱',
      password: '密码',
      passwordPlaceholder: '请输入密码',
      forgot: '忘记密码？',
      remember: '记住我',
      submit: '登录',
      submitting: '登录中...',
      noAccount: '还没有账号？',
      signUp: '立即注册'
    },
    sidebar: {
      dashboard: '控制面板',
      recognition: '数字识别',
      models: 'AI模型',
      analytics: '性能分析',
      feedback: '反馈系统',
      admin: '管理面板',
      main: '主要功能',
      analyticsSection: '分析',
      adminSection: '管理',
      returnDashboard: '返回控制台'
    },
    nav: {
      dashboard: '控制面板',
      notifications: '通知',
      markRead: '全部标记为已读',
      empty: '暂时没有通知',
      profile: '个人信息',
      settings: '设置',
      signOut: '退出登录',
      guest: '访客',
      noEmail: '未绑定邮箱',
      viewAll: '查看全部'
    }
  }
}

const state = reactive({
  locale: localStorage.getItem(STORAGE_KEY) || 'en'
})

const getValue = (obj, path) => {
  return path.split('.').reduce((acc, segment) => (acc ? acc[segment] : undefined), obj)
}

const translate = (key) => {
  const value = getValue(messages[state.locale], key)
  if (value !== undefined) return value
  return getValue(messages.en, key) ?? key
}

export function useI18n() {
  const locale = computed(() => state.locale)
  const setLocale = (next) => {
    state.locale = next
    localStorage.setItem(STORAGE_KEY, next)
  }

  const t = (key) => translate(key)

  return { locale, setLocale, t }
}

export function getCurrentLocale() {
  return state.locale
}
