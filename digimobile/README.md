# DigiMobile - Digit Recognition Mobile App

Mobile app for DigiScribe digit recognition system.

## Features

- **Canvas Tab**: Draw digits and get real-time predictions
- **History Tab**: View prediction history
- **Models Tab**: See active ML models
- **Stats Tab**: View system statistics

## Setup

1. Install dependencies:
```bash
cd digimobile
npm install
```

2. Configure backend URL:
Edit `src/config.js` and set your backend IP:
```javascript
export const API_BASE_URL = 'http://YOUR_IP:8081/api';
```

3. Start the app:
```bash
npm start
```

4. Run on Android:
```bash
npm run android
```

Or scan QR code with Expo Go app.

## Backend Requirements

Make sure your backend services are running:

1. **Spring Boot Backend** (port 8081):
```bash
cd pc/backend
mvn spring-boot:run
```

2. **ML Service** (port 8000):
```bash
cd pc/ml
source .venv/bin/activate
uvicorn mnist_service:app --host 0.0.0.0 --port 8000
```

## Architecture

- **React Native** with Expo
- **React Navigation** for tabs
- **Skia** for canvas drawing
- **Axios** for API calls
- **AsyncStorage** for local data

## API Endpoints Used

- `POST /api/recognition/predict` - Predict digit
- `GET /api/models` - Get active models
- `GET /api/admin/dashboard` - Get statistics
