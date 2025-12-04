import AsyncStorage from '@react-native-async-storage/async-storage';

const LOCALE_KEY = 'digiscribe_locale';

const translations = {
  en: {
    canvas: { title: 'Draw Digit', clear: 'Clear', predict: 'Predict', analyzing: 'Analyzing...', result: 'Result', confidence: 'Confidence', draw: 'Draw a digit to predict' },
    history: { title: 'Prediction History', empty: 'No predictions yet', digit: 'Digit', time: 'Time' },
    stats: { title: 'Statistics & Models', subtitle: 'System overview', totalPredictions: 'Total Predictions', activeModels: 'Active Models', accuracy: 'Accuracy', feedbackCount: 'Feedback Count', modelsTitle: 'Active Models', modelsSubtitle: 'models available', predictions: 'Predictions', noModels: 'No models found' },
    settings: { title: 'Settings', subtitle: 'Manage your preferences', appearance: 'Appearance', darkMode: 'Dark Mode', darkModeHint: 'Enable dark theme', notifications: 'Notifications', emailAlerts: 'Email Alerts', emailAlertsHint: 'Receive email notifications', pushNotifications: 'Push Notifications', pushNotificationsHint: 'Receive push alerts', language: 'Language', languageHint: 'Change app language', english: 'English', chinese: 'Chinese' },
    tabs: { canvas: 'Canvas', history: 'History', stats: 'Stats', settings: 'Settings' }
  },
  zh: {
    canvas: { title: '绘制数字', clear: '清除', predict: '预测', analyzing: '分析中...', result: '结果', confidence: '置信度', draw: '绘制数字进行预测' },
    history: { title: '预测历史', empty: '暂无预测记录', digit: '数字', time: '时间' },
    stats: { title: '统计与模型', subtitle: '系统概览', totalPredictions: '总预测数', activeModels: '活跃模型', accuracy: '准确率', feedbackCount: '反馈数量', modelsTitle: '活跃模型', modelsSubtitle: '个可用模型', predictions: '预测', noModels: '未找到模型' },
    settings: { title: '设置', subtitle: '管理您的偏好设置', appearance: '外观', darkMode: '深色模式', darkModeHint: '启用深色主题', notifications: '通知', emailAlerts: '电子邮件提醒', emailAlertsHint: '接收电子邮件通知', pushNotifications: '推送通知', pushNotificationsHint: '接收推送提醒', language: '语言', languageHint: '更改应用语言', english: '英语', chinese: '中文' },
    tabs: { canvas: '画布', history: '历史', stats: '统计', settings: '设置' }
  }
};

let currentLocale = 'en';
let listeners = [];

export const initI18n = async () => {
  try {
    const saved = await AsyncStorage.getItem(LOCALE_KEY);
    if (saved) currentLocale = saved;
  } catch (e) {}
};

export const getLocale = () => currentLocale;

export const setLocale = async (locale) => {
  currentLocale = locale;
  try {
    await AsyncStorage.setItem(LOCALE_KEY, locale);
  } catch (e) {}
  listeners.forEach(fn => fn(locale));
};

export const subscribe = (fn) => {
  listeners.push(fn);
  return () => { listeners = listeners.filter(l => l !== fn); };
};

export const t = (key) => {
  const keys = key.split('.');
  let value = translations[currentLocale];
  for (const k of keys) {
    value = value?.[k];
  }
  return value || key;
};
