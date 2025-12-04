import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  ActivityIndicator,
  RefreshControl,
} from 'react-native';
import api from '../services/api';
import { COLORS } from '../config';
import { t, subscribe } from '../i18n';

export default function StatsScreen() {
  const [stats, setStats] = useState(null);
  const [models, setModels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [, forceUpdate] = useState(0);

  useEffect(() => {
    loadData();
    const unsubscribe = subscribe(() => forceUpdate(n => n + 1));
    return unsubscribe;
  }, []);

  const loadData = async () => {
    try {
      const [dashboardRes, modelsRes, accuracyRes] = await Promise.all([
        api.get('/admin/dashboard'),
        api.get('/models').catch(() => ({ data: [] })),
        api.get('/models/accuracy-by-digit').catch(() => ({ data: [] }))
      ]);
      
      const data = dashboardRes.data || {};
      const modelsData = Array.isArray(modelsRes.data) ? modelsRes.data : [];
      const accuracyData = Array.isArray(accuracyRes.data) ? accuracyRes.data : [];
      
      const activeModel = modelsData.find(m => m.status?.toLowerCase() === 'active');
      
      const modelTotalPredictions = modelsData.reduce((sum, m) => sum + (m.prediction_count || m.predictionCount || 0), 0);
      const accuracyFromDigits = accuracyData.length
        ? accuracyData.reduce((sum, d) => sum + (d.accuracy || 0), 0) / accuracyData.length
        : 0;
      
      setStats({
        totalPredictions: modelTotalPredictions || data.totalPredictions || 0,
        activeModels: data.activeModels || 0,
        accuracy: accuracyFromDigits || data.accuracy || 0,
        feedbackCount: data.feedbackCount || 0,
        activeModelName: activeModel?.name || activeModel?.modelName || activeModel?.model_name || activeModel?.id || 'default',
      });
      setModels(modelsData);
    } catch (error) {
      console.error('Failed to load data:', error.response?.data || error.message);
      setStats({
        totalPredictions: 0,
        activeModels: 0,
        accuracy: 0,
        feedbackCount: 0,
        activeModelName: 'Error',
      });
      setModels([]);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const onRefresh = () => {
    setRefreshing(true);
    loadData();
  };

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color={COLORS.primary} />
      </View>
    );
  }

  return (
    <ScrollView
      style={styles.container}
      refreshControl={
        <RefreshControl
          refreshing={refreshing}
          onRefresh={onRefresh}
          tintColor={COLORS.primary}
        />
      }
    >
      <View style={styles.header}>
        <Text style={styles.title}>{t('stats.title')}</Text>
        <Text style={styles.subtitle}>{t('stats.subtitle')}</Text>
      </View>

      <View style={styles.statsGrid}>
        <View style={styles.statCard}>
          <Text style={styles.statLabel}>{t('stats.totalPredictions')}</Text>
          <Text style={styles.statValue}>
            {stats?.totalPredictions?.toLocaleString() || 0}
          </Text>
        </View>

        <View style={styles.statCard}>
          <Text style={styles.statLabel}>{t('stats.activeModels')}</Text>
          <Text style={styles.statValue}>{stats?.activeModels || 0}</Text>
        </View>

        <View style={styles.statCard}>
          <Text style={styles.statLabel}>{t('stats.accuracy')}</Text>
          <Text style={styles.statValue}>
            {stats?.accuracy ? `${(stats.accuracy * 100).toFixed(1)}%` : '0.0%'}
          </Text>
        </View>

        <View style={styles.statCard}>
          <Text style={styles.statLabel}>{t('stats.feedbackCount')}</Text>
          <Text style={styles.statValue}>{stats?.feedbackCount || 0}</Text>
        </View>
      </View>

      <View style={styles.sectionHeader}>
        <Text style={styles.sectionTitle}>{t('stats.modelsTitle')}</Text>
        <Text style={styles.sectionSubtitle}>{models.length} {t('stats.modelsSubtitle')}</Text>
      </View>

      {models.map((item) => (
        <View key={item.id} style={styles.modelCard}>
          <View style={styles.modelHeader}>
            <Text style={styles.modelName}>{item.name}</Text>
            {item.status === 'active' && (
              <View style={styles.activeBadge}>
                <Text style={styles.activeBadgeText}>ACTIVE</Text>
              </View>
            )}
          </View>
          <View style={styles.modelStats}>
            <View style={styles.stat}>
              <Text style={styles.modelStatLabel}>{t('stats.accuracy')}</Text>
              <Text style={styles.modelStatValue}>
                {item.accuracy ? (item.accuracy > 100 ? (item.accuracy / 100).toFixed(1) : item.accuracy.toFixed(1)) : '0.0'}%
              </Text>
            </View>
            <View style={styles.stat}>
              <Text style={styles.modelStatLabel}>{t('stats.predictions')}</Text>
              <Text style={styles.modelStatValue}>{item.predictionCount || 0}</Text>
            </View>
          </View>
        </View>
      ))}

      {models.length === 0 && (
        <View style={styles.empty}>
          <Text style={styles.emptyText}>{t('stats.noModels')}</Text>
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  centered: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
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
  statsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    padding: 20,
    gap: 12,
  },
  statCard: {
    backgroundColor: COLORS.surface,
    borderRadius: 12,
    padding: 16,
    borderWidth: 1,
    borderColor: COLORS.border,
    width: '48%',
  },
  statLabel: {
    fontSize: 12,
    color: COLORS.textSecondary,
    marginBottom: 8,
  },
  statValue: {
    fontSize: 24,
    fontWeight: '700',
    color: COLORS.textPrimary,
  },
  sectionHeader: {
    paddingHorizontal: 20,
    paddingTop: 12,
    paddingBottom: 12,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: COLORS.textPrimary,
  },
  sectionSubtitle: {
    fontSize: 13,
    color: COLORS.textSecondary,
    marginTop: 2,
  },
  modelCard: {
    backgroundColor: COLORS.surface,
    borderRadius: 12,
    padding: 16,
    marginHorizontal: 20,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: COLORS.border,
  },
  modelHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  modelName: {
    fontSize: 16,
    fontWeight: '600',
    color: COLORS.textPrimary,
  },
  activeBadge: {
    backgroundColor: COLORS.success,
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 4,
  },
  activeBadgeText: {
    color: '#fff',
    fontSize: 10,
    fontWeight: '700',
  },
  modelStats: {
    flexDirection: 'row',
    gap: 24,
  },
  stat: {
    flex: 1,
  },
  modelStatLabel: {
    fontSize: 11,
    color: COLORS.textSecondary,
    marginBottom: 4,
  },
  modelStatValue: {
    fontSize: 15,
    fontWeight: '600',
    color: COLORS.textPrimary,
  },
  empty: {
    padding: 32,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 14,
    color: COLORS.textSecondary,
  },
});
