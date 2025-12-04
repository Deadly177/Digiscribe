#!/bin/bash

echo "=========================================="
echo "Building APK with EAS Build"
echo "=========================================="
echo ""
echo "This will build APK in the cloud (no local Android setup needed)"
echo ""

cd "$(dirname "$0")"

# Login to EAS
echo "Step 1: Login to Expo account"
eas login

# Configure project
echo ""
echo "Step 2: Configure EAS project"
eas build:configure

# Build APK
echo ""
echo "Step 3: Building APK (this takes 5-10 minutes)"
eas build --platform android --profile preview

echo ""
echo "=========================================="
echo "Build complete! Download APK from the link above"
echo "=========================================="
