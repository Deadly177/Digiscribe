import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  ActivityIndicator,
  RefreshControl,
} from 'react-native';
import api from '../services/api';
import { COLORS } from '../config';

export default function ModelsScreen() {
  const [models, setModels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    loadModels();
  }, []);

  const loadModels = async () => {
    try {
      const response = await api.get('/models');
      setModels(response.data || []);
    } catch (error) {
      console.error('Failed to load models:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const onRefresh = () => {
    setRefreshing(true);
    loadModels();
  };

  const renderItem = ({ item }) => (
    <View style={styles.modelCard}>
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
          <Text style={styles.statLabel}>Accuracy</Text>
          <Text style={styles.statValue}>
            {item.accuracy ? (item.accuracy > 100 ? (item.accuracy / 100).toFixed(1) : item.accuracy.toFixed(1)) : '0.0'}%
          </Text>
        </View>
        <View style={styles.stat}>
          <Text style={styles.statLabel}>Predictions</Text>
          <Text style={styles.statValue}>{item.predictionCount || 0}</Text>
        </View>
        <View style={styles.stat}>
          <Text style={styles.statLabel}>Status</Text>
          <Text style={[styles.statValue, styles.statusText]}>
            {item.status}
          </Text>
        </View>
      </View>

      {item.lastTrained && (
        <Text style={styles.lastTrained}>
          Last trained: {new Date(item.lastTrained).toLocaleDateString()}
        </Text>
      )}
    </View>
  );

  if (loading) {
    return (
      <View style={styles.centered}>
        <ActivityIndicator size="large" color={COLORS.primary} />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Active Models</Text>
        <Text style={styles.subtitle}>{models.length} models available</Text>
      </View>

      <FlatList
        data={models}
        renderItem={renderItem}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.list}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            tintColor={COLORS.primary}
          />
        }
        ListEmptyComponent={
          <View style={styles.empty}>
            <Text style={styles.emptyText}>No models found</Text>
          </View>
        }
      />
    </View>
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
  list: {
    padding: 20,
  },
  modelCard: {
    backgroundColor: COLORS.surface,
    borderRadius: 12,
    padding: 16,
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
    fontSize: 18,
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
    justifyContent: 'space-between',
    marginBottom: 8,
  },
  stat: {
    flex: 1,
  },
  statLabel: {
    fontSize: 12,
    color: COLORS.textSecondary,
    marginBottom: 4,
  },
  statValue: {
    fontSize: 16,
    fontWeight: '600',
    color: COLORS.textPrimary,
  },
  statusText: {
    textTransform: 'capitalize',
  },
  lastTrained: {
    fontSize: 12,
    color: COLORS.textSecondary,
    marginTop: 8,
  },
  empty: {
    padding: 32,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 16,
    color: COLORS.textSecondary,
  },
});
