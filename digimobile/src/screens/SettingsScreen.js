import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView, Switch, TouchableOpacity } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { COLORS } from '../config';
import { t, getLocale, setLocale, subscribe } from '../i18n';

const THEME_KEY = 'digiscribe_theme';

export default function SettingsScreen() {
  const [darkMode, setDarkMode] = useState(false);
  const [emailAlerts, setEmailAlerts] = useState(true);
  const [pushNotifications, setPushNotifications] = useState(true);
  const [locale, setLocaleState] = useState(getLocale());
  const [userData, setUserData] = useState(null);

  useEffect(() => {
    loadSettings();
    loadUserData();
    const unsubscribe = subscribe((newLocale) => setLocaleState(newLocale));
    const interval = setInterval(loadUserData, 1000);
    return () => {
      unsubscribe();
      clearInterval(interval);
    };
  }, []);

  const loadSettings = async () => {
    try {
      const theme = await AsyncStorage.getItem(THEME_KEY);
      if (theme === 'dark') setDarkMode(true);
    } catch (error) {
      console.error('Failed to load settings:', error);
    }
  };

  const loadUserData = async () => {
    try {
      const data = await AsyncStorage.getItem('user_data');
      if (data) setUserData(JSON.parse(data));
    } catch (error) {
      console.error('Failed to load user data:', error);
    }
  };

  const toggleDarkMode = async (value) => {
    setDarkMode(value);
    try {
      await AsyncStorage.setItem(THEME_KEY, value ? 'dark' : 'light');
    } catch (error) {
      console.error('Failed to save theme:', error);
    }
  };

  const changeLanguage = async (lang) => {
    await setLocale(lang);
    setLocaleState(lang);
  };

  const handleSignOut = async () => {
    try {
      await AsyncStorage.removeItem('user_data');
      setUserData(null);
    } catch (error) {
      console.error('Failed to sign out:', error);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>{t('settings.title')}</Text>
        <Text style={styles.subtitle}>{t('settings.subtitle')}</Text>
      </View>

      {userData && (
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Account</Text>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Username</Text>
            <Text style={styles.infoValue}>{userData.username}</Text>
          </View>
          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Email</Text>
            <Text style={styles.infoValue}>{userData.email}</Text>
          </View>
          <TouchableOpacity style={styles.signOutBtn} onPress={handleSignOut}>
            <Text style={styles.signOutText}>Sign Out</Text>
          </TouchableOpacity>
        </View>
      )}

      <View style={styles.card}>
        <Text style={styles.cardTitle}>{t('settings.appearance')}</Text>
        <View style={styles.settingRow}>
          <View style={styles.settingInfo}>
            <Text style={styles.label}>{t('settings.darkMode')}</Text>
            <Text style={styles.hint}>{t('settings.darkModeHint')}</Text>
          </View>
          <Switch
            value={darkMode}
            onValueChange={toggleDarkMode}
            trackColor={{ false: COLORS.border, true: COLORS.success }}
            thumbColor="#fff"
          />
        </View>
        <View style={[styles.settingRow, styles.noBorder]}>
          <View style={styles.settingInfo}>
            <Text style={styles.label}>{t('settings.language')}</Text>
            <Text style={styles.hint}>{t('settings.languageHint')}</Text>
          </View>
          <View style={styles.languageButtons}>
            <TouchableOpacity
              style={[styles.langBtn, locale === 'en' && styles.langBtnActive]}
              onPress={() => changeLanguage('en')}
            >
              <Text style={[styles.langText, locale === 'en' && styles.langTextActive]}>EN</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.langBtn, locale === 'zh' && styles.langBtnActive]}
              onPress={() => changeLanguage('zh')}
            >
              <Text style={[styles.langText, locale === 'zh' && styles.langTextActive]}>中文</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>

      <View style={styles.card}>
        <Text style={styles.cardTitle}>{t('settings.notifications')}</Text>
        <View style={styles.settingRow}>
          <View style={styles.settingInfo}>
            <Text style={styles.label}>{t('settings.emailAlerts')}</Text>
            <Text style={styles.hint}>{t('settings.emailAlertsHint')}</Text>
          </View>
          <Switch
            value={emailAlerts}
            onValueChange={setEmailAlerts}
            trackColor={{ false: COLORS.border, true: COLORS.success }}
            thumbColor="#fff"
          />
        </View>
        <View style={[styles.settingRow, styles.noBorder]}>
          <View style={styles.settingInfo}>
            <Text style={styles.label}>{t('settings.pushNotifications')}</Text>
            <Text style={styles.hint}>{t('settings.pushNotificationsHint')}</Text>
          </View>
          <Switch
            value={pushNotifications}
            onValueChange={setPushNotifications}
            trackColor={{ false: COLORS.border, true: COLORS.success }}
            thumbColor="#fff"
          />
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  header: {
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: COLORS.border,
  },
  title: {
    fontSize: 24,
    fontWeight: '700',
    color: COLORS.textPrimary,
  },
  subtitle: {
    fontSize: 14,
    color: COLORS.textSecondary,
    marginTop: 4,
  },
  card: {
    backgroundColor: COLORS.surface,
    borderRadius: 12,
    padding: 16,
    margin: 20,
    marginBottom: 0,
    borderWidth: 1,
    borderColor: COLORS.border,
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: COLORS.textPrimary,
    marginBottom: 12,
  },
  settingRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: COLORS.border,
  },
  noBorder: {
    borderBottomWidth: 0,
  },
  settingInfo: {
    flex: 1,
    marginRight: 12,
  },
  label: {
    fontSize: 16,
    fontWeight: '600',
    color: COLORS.textPrimary,
    marginBottom: 2,
  },
  hint: {
    fontSize: 13,
    color: COLORS.textSecondary,
  },
  languageButtons: {
    flexDirection: 'row',
    gap: 8,
  },
  langBtn: {
    paddingHorizontal: 16,
    paddingVertical: 6,
    borderRadius: 6,
    borderWidth: 1,
    borderColor: COLORS.border,
    backgroundColor: COLORS.background,
  },
  langBtnActive: {
    backgroundColor: COLORS.primary,
    borderColor: COLORS.primary,
  },
  langText: {
    fontSize: 13,
    fontWeight: '600',
    color: COLORS.textSecondary,
  },
  langTextActive: {
    color: '#fff',
  },
  infoRow: {
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: COLORS.border,
  },
  infoLabel: {
    fontSize: 12,
    color: COLORS.textSecondary,
    marginBottom: 4,
  },
  infoValue: {
    fontSize: 16,
    fontWeight: '600',
    color: COLORS.textPrimary,
  },
  signOutBtn: {
    marginTop: 12,
    backgroundColor: '#ef4444',
    paddingVertical: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  signOutText: {
    color: '#fff',
    fontSize: 14,
    fontWeight: '600',
  },
});
