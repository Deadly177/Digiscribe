import React, { useRef, useState } from 'react';
import { View, StyleSheet, Dimensions } from 'react-native';
import { Canvas, Path, Skia, useCanvasRef } from '@shopify/react-native-skia';
import { COLORS } from '../config';

const { width } = Dimensions.get('window');
const CANVAS_SIZE = Math.min(width - 40, 280);

export default function DrawingCanvas({ onDrawingChange }) {
  const [paths, setPaths] = useState([]);
  const [points, setPoints] = useState([]);
  const currentPath = useRef(null);
  const currentPoints = useRef([]);

  const handleTouchStart = (event) => {
    const { locationX, locationY } = event.nativeEvent;
    currentPath.current = Skia.Path.Make();
    currentPath.current.moveTo(locationX, locationY);
    currentPoints.current = [[locationX, locationY]];
  };

  const handleTouchMove = (event) => {
    if (!currentPath.current) return;
    const { locationX, locationY } = event.nativeEvent;
    currentPath.current.lineTo(locationX, locationY);
    currentPoints.current.push([locationX, locationY]);
    setPaths([...paths, currentPath.current]);
  };

  const handleTouchEnd = () => {
    if (currentPath.current) {
      const newPaths = [...paths, currentPath.current];
      const newPoints = [...points, ...currentPoints.current];
      setPaths(newPaths);
      setPoints(newPoints);
      currentPath.current = null;
      currentPoints.current = [];
      if (onDrawingChange) onDrawingChange(newPoints);
    }
  };

  const clear = () => {
    setPaths([]);
    setPoints([]);
    currentPath.current = null;
    currentPoints.current = [];
    if (onDrawingChange) onDrawingChange([]);
  };

  return {
    canvas: (
      <View style={styles.container}>
        <Canvas
          style={styles.canvas}
          onTouchStart={handleTouchStart}
          onTouchMove={handleTouchMove}
          onTouchEnd={handleTouchEnd}
        >
          {paths.map((path, index) => (
            <Path
              key={index}
              path={path}
              color="#000000"
              style="stroke"
              strokeWidth={15}
              strokeCap="round"
              strokeJoin="round"
            />
          ))}
        </Canvas>
      </View>
    ),
    clear,
    paths,
    points,
  };
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#ffffff',
    borderRadius: 20,
    overflow: 'hidden',
    borderWidth: 3,
    borderColor: COLORS.primary,
    shadowColor: COLORS.primary,
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.25,
    shadowRadius: 12,
    elevation: 8,
    position: 'relative',
  },
  canvas: {
    width: CANVAS_SIZE,
    height: CANVAS_SIZE,
    backgroundColor: '#ffffff',
  },
});
