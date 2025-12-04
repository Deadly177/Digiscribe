import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { COLORS } from '../config';
import { t, subscribe } from '../i18n';

export default function HistoryScreen() {
  const [predictions, setPredictions] = useState([]);
  const [, forceUpdate] = useState(0);

  useEffect(() => {
    loadHistory();
    const interval = setInterval(loadHistory, 2000);
    const unsubscribe = subscribe(() => forceUpdate(n => n + 1));
    return () => {
      clearInterval(interval);
      unsubscribe();
    };
  }, []);

  const loadHistory = async () => {
    try {
      const historyStr = await AsyncStorage.getItem('predictions');
      setPredictions(historyStr ? JSON.parse(historyStr) : []);
    } catch (error) {
      console.error('Failed to load history:', error);
    }
  };

  const clearHistory = async () => {
    await AsyncStorage.removeItem('predictions');
    setPredictions([]);
  };

  const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now - date;
    const minutes = Math.floor(diff / 60000);

    if (minutes < 1) return 'Just now';
    if (minutes < 60) return `${minutes}m ago`;
    const hours = Math.floor(minutes / 60);
    if (hours < 24) return `${hours}h ago`;
    return date.toLocaleDateString();
  };

  const renderItem = ({ item }) => (
    <View style={styles.item}>
      <View style={styles.itemLeft}>
        <Text style={styles.digit}>{item.digit}</Text>
        <Text style={styles.confidence}>
          {(item.confidence * 100).toFixed(1)}%
        </Text>
      </View>
      <Text style={styles.time}>{formatTime(item.timestamp)}</Text>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>{t('history.title')}</Text>
        {predictions.length > 0 && (
          <TouchableOpacity onPress={clearHistory}>
            <Text style={styles.clearBtn}>{t('canvas.clear')}</Text>
          </TouchableOpacity>
        )}
      </View>

      {predictions.length === 0 ? (
        <View style={styles.empty}>
          <Text style={styles.emptyText}>{t('history.empty')}</Text>
          <Text style={styles.emptySubtext}>
            {t('canvas.draw')}
          </Text>
        </View>
      ) : (
        <FlatList
          data={predictions}
          renderItem={renderItem}
          keyExtractor={(item) => item.id.toString()}
          contentContainerStyle={styles.list}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: COLORS.border,
  },
  title: {
    fontSize: 24,
    fontWeight: '700',
    color: COLORS.textPrimary,
  },
  clearBtn: {
    color: COLORS.primary,
    fontSize: 14,
    fontWeight: '600',
  },
  list: {
    padding: 20,
  },
  item: {
    backgroundColor: COLORS.surface,
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
    borderWidth: 1,
    borderColor: COLORS.border,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  itemLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
  },
  digit: {
    fontSize: 32,
    fontWeight: '700',
    color: COLORS.textPrimary,
  },
  confidence: {
    fontSize: 16,
    color: COLORS.success,
    fontWeight: '600',
  },
  time: {
    fontSize: 12,
    color: COLORS.textSecondary,
  },
  empty: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
  },
  emptyText: {
    fontSize: 18,
    color: COLORS.textSecondary,
    marginBottom: 8,
  },
  emptySubtext: {
    fontSize: 14,
    color: COLORS.textSecondary,
    textAlign: 'center',
  },
});
