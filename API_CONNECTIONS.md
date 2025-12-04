# API Connections - PC Backend ↔ Mobile App

## Backend (Spring Boot) - Port 8081

### Authentication APIs
**Base URL**: `http://localhost:8081/api/auth`

| Method | Endpoint | Request Body | Response | Used By |
|--------|----------|--------------|----------|---------|
| POST | `/login` | `{username, password}` | `{token, username, email, role, message}` | Mobile: SignUpScreen |
| POST | `/register` | `{username, email, password}` | `{token, username, email, role, message}` | Mobile: SignUpScreen |
| GET | `/test` | - | `"Auth controller is working!"` | Testing |

### Recognition APIs
**Base URL**: `http://localhost:8081/api/recognition`

| Method | Endpoint | Request Body | Response | Used By |
|--------|----------|--------------|----------|---------|
| POST | `/predict` | `{imageData, inputType, modelId}` | `{digit, confidence, historyId, confidenceDistribution, processingTimeMs, modelUsed}` | Mobile: CanvasScreen |
| POST | `/feedback` | `{historyId, actualDigit}` | `{message, correct}` | Mobile: HistoryScreen |
| GET | `/history` | - | `[{id, digit, confidence, timestamp, ...}]` | Mobile: HistoryScreen |

### Model APIs
**Base URL**: `http://localhost:8081/api/models`

| Method | Endpoint | Request Body | Response | Used By |
|--------|----------|--------------|----------|---------|
| GET | `/list` | - | `[{id, name, accuracy, status, ...}]` | Mobile: StatsScreen |
| GET | `/{id}` | - | `{id, name, accuracy, description, ...}` | Mobile: StatsScreen |

### Analytics APIs
**Base URL**: `http://localhost:8081/api/analytics`

| Method | Endpoint | Request Body | Response | Used By |
|--------|----------|--------------|----------|---------|
| GET | `/dashboard` | - | `{totalPredictions, accuracy, avgConfidence, ...}` | Mobile: StatsScreen |
| GET | `/user-stats` | - | `{predictions, accuracy, recentActivity, ...}` | Mobile: StatsScreen |

---

## Mobile App (React Native) API Calls

### Configuration
**File**: `digimobile/src/config.js`
```javascript
export const API_BASE_URL = 'http://10.0.2.2:8081/api'; // Android emulator
// or 'http://localhost:8081/api' for iOS simulator
```

### API Service
**File**: `digimobile/src/services/api.js`
```javascript
import axios from 'axios';
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
});
```

---

## Mobile Screen → Backend Mapping

### 1. SignUpScreen (Authentication)
**File**: `digimobile/src/screens/SignUpScreen.js`

**Sign Up Flow**:
```javascript
POST /api/auth/register
Body: { username, email, password }
Response: { token, username, email, role }
→ Saves to AsyncStorage: user_data
```

**Sign In Flow**:
```javascript
POST /api/auth/login
Body: { username, password }
Response: { token, username, email, role }
→ Saves to AsyncStorage: user_data
```

### 2. CanvasScreen (Digit Recognition)
**File**: `digimobile/src/screens/CanvasScreen.js`

**Predict Digit**:
```javascript
POST /api/recognition/predict
Body: { 
  imageData: base64String,
  inputType: "CANVAS",
  modelId: "default"
}
Response: {
  digit: 5,
  confidence: 0.98,
  historyId: 123,
  confidenceDistribution: [0.01, 0.02, ...],
  processingTimeMs: 45
}
```

### 3. HistoryScreen (Prediction History)
**File**: `digimobile/src/screens/HistoryScreen.js`

**Get History**:
```javascript
GET /api/recognition/history
Headers: { Authorization: Bearer <token> }
Response: [
  {
    id: 1,
    predictedDigit: 5,
    confidence: 0.98,
    timestamp: "2024-12-04T10:30:00",
    inputType: "CANVAS"
  },
  ...
]
```

**Provide Feedback**:
```javascript
POST /api/recognition/feedback
Body: { historyId: 123, actualDigit: 5 }
Response: { message: "Feedback saved", correct: true }
```

### 4. StatsScreen (Statistics & Models)
**File**: `digimobile/src/screens/StatsScreen.js`

**Get Dashboard Stats**:
```javascript
GET /api/analytics/dashboard
Response: {
  totalPredictions: 1250,
  accuracy: 94.5,
  avgConfidence: 0.92,
  topDigit: 7
}
```

**Get Models List**:
```javascript
GET /api/models/list
Response: [
  {
    id: "default",
    name: "MNIST CNN",
    accuracy: 98.5,
    status: "ACTIVE"
  },
  ...
]
```

---

## Data Flow Example

### Complete Recognition Flow:
```
1. User draws digit on CanvasScreen
   ↓
2. Canvas converts to base64 image
   ↓
3. POST /api/recognition/predict
   Body: { imageData: "data:image/png;base64,..." }
   ↓
4. Backend → Python ML Service (port 8000)
   POST /api/predict
   ↓
5. ML Service returns prediction
   ↓
6. Backend saves to database (RecognitionHistory)
   ↓
7. Response to mobile: { digit: 5, confidence: 0.98 }
   ↓
8. Mobile displays result
```

---

## Network Configuration

### For Android Emulator:
- Use `10.0.2.2` instead of `localhost`
- Backend: `http://10.0.2.2:8081/api`

### For iOS Simulator:
- Use `localhost` directly
- Backend: `http://localhost:8081/api`

### For Physical Device:
- Use computer's IP address
- Backend: `http://192.168.x.x:8081/api`

---

## Authentication Flow

```
Mobile App                    Backend
    |                            |
    |-- POST /auth/register ---->|
    |    {username, email, pwd}  |
    |                            |
    |<--- {token, user data} ----|
    |                            |
    | Save to AsyncStorage       |
    |                            |
    |-- POST /recognition/predict ->|
    |    Header: Bearer <token>  |
    |                            |
    |<--- {digit, confidence} ---|
```

---

## Error Handling

All endpoints return errors in format:
```json
{
  "message": "Error description",
  "status": 400
}
```

Mobile app handles errors in catch blocks and displays user-friendly messages.
