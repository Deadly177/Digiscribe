# Build APK Instructions

## Option 1: Run in Expo Go (Fastest - Recommended for Testing)

1. **Install Expo Go on your Android device/emulator**
   - Download from Google Play Store: https://play.google.com/store/apps/details?id=host.exp.exponent

2. **Start the development server**
   ```bash
   cd digimobile
   npx expo start
   ```

3. **Connect your device**
   - Scan the QR code with Expo Go app
   - Or press 'a' to open in Android emulator

## Option 2: Build Standalone APK (For Distribution)

### Prerequisites
- Install EAS CLI: `npm install -g eas-cli`
- Create Expo account: https://expo.dev/signup
- Login: `eas login`

### Build APK
```bash
cd digimobile
eas build --platform android --profile preview
```

The APK will be available for download from the Expo dashboard.

## Option 3: Build APK Locally (No Expo Account Needed)

### Prerequisites
- Android Studio installed
- Android SDK configured
- Java JDK 17+

### Steps
```bash
cd digimobile

# Install dependencies
npm install

# Prebuild native code
npx expo prebuild --platform android

# Build APK
cd android
./gradlew assembleRelease

# APK location: android/app/build/outputs/apk/release/app-release.apk
```

## Quick Test in Emulator

1. **Open Android Studio**
2. **Start an Android emulator** (AVD Manager)
3. **Run:**
   ```bash
   cd digimobile
   npx expo start
   ```
4. **Press 'a'** in the terminal to open in emulator

The app will install and run automatically in the emulator.
