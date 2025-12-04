#!/bin/bash

echo "=========================================="
echo "DigiScribe Mobile App - Quick Start"
echo "=========================================="
echo ""

cd "$(dirname "$0")"

# Kill any existing Expo processes
pkill -f "expo start" 2>/dev/null

echo "Starting Expo development server..."
echo ""

npx expo start --port 19000

echo ""
echo "=========================================="
echo "Instructions:"
echo "1. Install 'Expo Go' app on your Android device"
echo "2. Scan the QR code with Expo Go"
echo "3. Or press 'a' to open in Android emulator"
echo "=========================================="
