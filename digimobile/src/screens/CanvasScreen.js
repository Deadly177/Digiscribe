import React, { useState, useRef, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import DrawingCanvas from '../components/DrawingCanvas';
import api from '../services/api';
import { COLORS } from '../config';
import { t, subscribe } from '../i18n';

export default function CanvasScreen({ navigation }) {
  const [prediction, setPrediction] = useState(null);
  const [loading, setLoading] = useState(false);
  const [, forceUpdate] = useState(0);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const canvasRef = React.useRef(null);

  useEffect(() => {
    checkLoginStatus();
    const unsubscribe = subscribe(() => forceUpdate(n => n + 1));
    const interval = setInterval(checkLoginStatus, 1000);
    return () => {
      unsubscribe();
      clearInterval(interval);
    };
  }, []);

  const checkLoginStatus = async () => {
    try {
      const data = await AsyncStorage.getItem('user_data');
      setIsLoggedIn(!!data);
    } catch (error) {
      setIsLoggedIn(false);
    }
  };

  const convertToMNIST = (points) => {
    const targetSize = 28;
    const canvasSize = 280;
    const grid = Array(targetSize).fill(0).map(() => Array(targetSize).fill(0));
    
    // Map points to grid with intensity based on density
    points.forEach(([x, y]) => {
      const gridX = Math.floor((x / canvasSize) * targetSize);
      const gridY = Math.floor((y / canvasSize) * targetSize);
      
      if (gridX >= 0 && gridX < targetSize && gridY >= 0 && gridY < targetSize) {
        // Mark center and neighbors with decreasing intensity
        grid[gridY][gridX] = Math.min(1, grid[gridY][gridX] + 1);
        
        for (let dy = -1; dy <= 1; dy++) {
          for (let dx = -1; dx <= 1; dx++) {
            if (dx === 0 && dy === 0) continue;
            const nx = gridX + dx;
            const ny = gridY + dy;
            if (nx >= 0 && nx < targetSize && ny >= 0 && ny < targetSize) {
              grid[ny][nx] = Math.min(1, grid[ny][nx] + 0.5);
            }
          }
        }
      }
    });
    
    // Flatten and normalize (MNIST expects white on black, normalized 0-1)
    return grid.flat();
  };

  const handlePredict = async () => {
    if (!canvasRef.current?.points || canvasRef.current.points.length === 0) {
      Alert.alert(t('canvas.result'), t('canvas.draw'));
      return;
    }

    setLoading(true);
    try {
      const imageData = convertToMNIST(canvasRef.current.points);
      
      const response = await api.post('/recognition/predict', {
        imageData: JSON.stringify(imageData),
        inputType: 'drawing',
      });

      const result = {
        predictedDigit: response.data.predictedDigit,
        confidence: response.data.confidence,
        processingTimeMs: response.data.processingTimeMs || 0,
        confidenceDistribution: response.data.confidenceDistribution || Array(10).fill(0),
        modelUsed: response.data.modelUsed,
      };

      setPrediction(result);

      // Save to history
      const historyStr = await AsyncStorage.getItem('predictions');
      const history = historyStr ? JSON.parse(historyStr) : [];
      history.unshift({
        id: Date.now(),
        digit: result.predictedDigit,
        confidence: result.confidence,
        timestamp: new Date().toISOString(),
      });
      await AsyncStorage.setItem('predictions', JSON.stringify(history.slice(0, 50)));
    } catch (error) {
      Alert.alert(t('canvas.result'), error.response?.data?.message || error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    canvasRef.current?.clear();
    setPrediction(null);
  };

  const canvas = DrawingCanvas({ onDrawingChange: () => {} });
  canvasRef.current = canvas;

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <View>
          <Text style={styles.title}>{t('canvas.title')}</Text>
          <Text style={styles.subtitle}>{t('canvas.draw')}</Text>
        </View>
        <TouchableOpacity
          style={[styles.signUpBtn, isLoggedIn && styles.loggedInBtn]}
          onPress={() => !isLoggedIn && navigation.navigate('SignUp')}
          disabled={isLoggedIn}
        >
          <Text style={styles.signUpBtnText}>{isLoggedIn ? '✓ Logged' : 'Sign In'}</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.canvasWrapper}>
        {canvas.canvas}
        <View style={styles.canvasHint}>
          <Text style={styles.hintText}>✏️ {t('canvas.draw')}</Text>
        </View>
      </View>

      <View style={styles.controls}>
        <TouchableOpacity style={styles.btnSecondary} onPress={handleClear}>
          <Text style={styles.btnText}>🗑️ {t('canvas.clear')}</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.btnPrimary, loading && styles.btnDisabled]}
          onPress={handlePredict}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#fff" />
          ) : (
            <Text style={styles.btnTextPrimary}>🔮 {t('canvas.predict')}</Text>
          )}
        </TouchableOpacity>
      </View>

      {prediction && (
        <View style={styles.resultCard}>
          <View style={styles.resultBadge}>
            <Text style={styles.badgeText}>{t('canvas.result').toUpperCase()}</Text>
          </View>
          <Text style={styles.resultDigit}>{prediction.predictedDigit}</Text>
          <View style={styles.resultMeta}>
            <View style={styles.metaItem}>
              <Text style={styles.metaLabel}>{t('canvas.confidence')}</Text>
              <Text style={styles.metaValue}>
                {(prediction.confidence * 100).toFixed(1)}%
              </Text>
            </View>
            <View style={styles.metaDivider} />
            <View style={styles.metaItem}>
              <Text style={styles.metaLabel}>{t('history.time')}</Text>
              <Text style={styles.metaValue}>{prediction.processingTimeMs}ms</Text>
            </View>
          </View>
        </View>
      )}

      {prediction?.confidenceDistribution && (
        <View style={styles.confidenceCard}>
          <View style={styles.cardHeader}>
            <Text style={styles.cardTitle}>📊 {t('canvas.confidence')}</Text>
            <Text style={styles.cardSubtitle}>{t('stats.predictions')}</Text>
          </View>
          {prediction.confidenceDistribution.map((conf, i) => (
            <View key={i} style={styles.barContainer}>
              <View style={[
                styles.digitBadge,
                i === prediction.predictedDigit && styles.digitBadgeActive
              ]}>
                <Text style={[
                  styles.digitLabel,
                  i === prediction.predictedDigit && styles.digitLabelActive
                ]}>{i}</Text>
              </View>
              <View style={styles.barBackground}>
                <View
                  style={[
                    styles.barFill,
                    i === prediction.predictedDigit && styles.barHighlight,
                    { width: `${conf * 100}%` },
                  ]}
                />
              </View>
              <Text style={styles.percentText}>{(conf * 100).toFixed(1)}%</Text>
            </View>
          ))}
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
    padding: 20,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: COLORS.textPrimary,
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 16,
    color: COLORS.textSecondary,
  },
  signUpBtn: {
    backgroundColor: COLORS.primary,
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 8,
  },
  signUpBtnText: {
    color: '#fff',
    fontWeight: '600',
    fontSize: 14,
  },
  loggedInBtn: {
    backgroundColor: COLORS.success,
  },
  canvasWrapper: {
    alignItems: 'center',
    marginBottom: 8,
  },
  canvasHint: {
    marginTop: 12,
    paddingHorizontal: 16,
    paddingVertical: 8,
    backgroundColor: COLORS.surface,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: COLORS.border,
  },
  hintText: {
    color: COLORS.textSecondary,
    fontSize: 13,
    fontWeight: '500',
  },
  controls: {
    flexDirection: 'row',
    gap: 12,
    marginTop: 20,
    marginBottom: 20,
  },
  btnSecondary: {
    flex: 1,
    backgroundColor: COLORS.surface,
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
    borderWidth: 2,
    borderColor: COLORS.border,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  btnPrimary: {
    flex: 1,
    backgroundColor: COLORS.success,
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
    shadowColor: COLORS.success,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 4,
  },
  btnDisabled: {
    opacity: 0.6,
  },
  btnText: {
    color: COLORS.textPrimary,
    fontWeight: '600',
    fontSize: 16,
  },
  btnTextPrimary: {
    color: '#fff',
    fontWeight: '600',
    fontSize: 16,
  },
  resultCard: {
    backgroundColor: COLORS.surface,
    borderRadius: 16,
    padding: 24,
    marginBottom: 20,
    borderWidth: 2,
    borderColor: COLORS.success,
    alignItems: 'center',
    shadowColor: COLORS.success,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 12,
    elevation: 6,
  },
  resultBadge: {
    backgroundColor: COLORS.success,
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 12,
    marginBottom: 12,
  },
  badgeText: {
    color: '#fff',
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 1,
  },
  resultDigit: {
    fontSize: 72,
    fontWeight: '800',
    color: COLORS.success,
    marginBottom: 16,
  },
  resultMeta: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
  },
  metaItem: {
    alignItems: 'center',
  },
  metaLabel: {
    color: COLORS.textSecondary,
    fontSize: 12,
    marginBottom: 4,
  },
  metaValue: {
    color: COLORS.textPrimary,
    fontSize: 16,
    fontWeight: '600',
  },
  metaDivider: {
    width: 1,
    height: 30,
    backgroundColor: COLORS.border,
  },
  confidenceCard: {
    backgroundColor: COLORS.surface,
    borderRadius: 16,
    padding: 20,
    borderWidth: 1,
    borderColor: COLORS.border,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.05,
    shadowRadius: 8,
    elevation: 3,
  },
  cardHeader: {
    marginBottom: 16,
  },
  cardTitle: {
    color: COLORS.textPrimary,
    fontSize: 18,
    fontWeight: '700',
    marginBottom: 4,
  },
  cardSubtitle: {
    color: COLORS.textSecondary,
    fontSize: 13,
  },
  barContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 10,
  },
  digitBadge: {
    width: 32,
    height: 32,
    borderRadius: 8,
    backgroundColor: COLORS.background,
    borderWidth: 1,
    borderColor: COLORS.border,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 12,
  },
  digitBadgeActive: {
    backgroundColor: COLORS.primary,
    borderColor: COLORS.primary,
  },
  digitLabel: {
    color: COLORS.textPrimary,
    fontSize: 14,
    fontWeight: '700',
  },
  digitLabelActive: {
    color: '#fff',
  },
  barBackground: {
    flex: 1,
    height: 20,
    backgroundColor: COLORS.background,
    borderRadius: 10,
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: COLORS.border,
  },
  barFill: {
    height: '100%',
    backgroundColor: COLORS.success,
    opacity: 0.6,
  },
  barHighlight: {
    backgroundColor: COLORS.primary,
    opacity: 1,
  },
  percentText: {
    color: COLORS.textSecondary,
    width: 55,
    textAlign: 'right',
    fontSize: 13,
    fontWeight: '600',
    marginLeft: 8,
  },
});
