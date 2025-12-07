# Postman API Testing Guide

## Setup
1. Open Postman
2. Create new Collection: "DigiScribe API"
3. Base URL: `http://localhost:8081/api`

---

## 1. Health Check (GET)
**URL**: `http://localhost:8081/api/health`  
**Method**: GET  
**Expected Response**:
```json
{
  "status": "healthy",
  "mlService": {
    "status": "healthy",
    "models_loaded": 4
  },
  "uptimeMs": 123456
}
```

---

## 2. Register User (POST)
**URL**: `http://localhost:8081/api/auth/register`  
**Method**: POST  
**Headers**:
```
Content-Type: application/json
```
**Body** (raw JSON):
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```
**Expected Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "email": "test@example.com",
  "role": "USER",
  "message": "Registration successful"
}
```

---

## 3. Login (POST)
**URL**: `http://localhost:8081/api/auth/login`  
**Method**: POST  
**Headers**:
```
Content-Type: application/json
```
**Body** (raw JSON):
```json
{
  "username": "testuser",
  "password": "password123"
}
```
**Expected Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "email": "test@example.com",
  "role": "USER",
  "message": "Login successful"
}
```

---

## 4. Get Models List (GET)
**URL**: `http://localhost:8081/api/models/list`  
**Method**: GET  
**Expected Response**:
```json
[
  {
    "id": "default",
    "name": "MNIST CNN",
    "accuracy": 98.5,
    "status": "ACTIVE",
    "description": "Default MNIST model"
  }
]
```

---

## 5. Get Dashboard Stats (GET)
**URL**: `http://localhost:8081/api/analytics/dashboard`  
**Method**: GET  
**Expected Response**:
```json
{
  "totalPredictions": 0,
  "accuracy": 0.0,
  "avgConfidence": 0.0,
  "topDigit": 0
}
```

---

## 6. Predict Digit (POST)
**URL**: `http://localhost:8081/api/recognition/predict`  
**Method**: POST  
**Headers**:
```
Content-Type: application/json
```
**Body** (raw JSON):
```json
{
  "imageData": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
  "inputType": "CANVAS",
  "modelId": "default"
}
```
**Expected Response**:
```json
{
  "digit": 5,
  "confidence": 0.98,
  "historyId": 1,
  "confidenceDistribution": [0.01, 0.02, 0.01, 0.01, 0.01, 0.98, 0.01, 0.01, 0.01, 0.01],
  "processingTimeMs": 45,
  "modelUsed": "MNIST CNN",
  "modelId": "default",
  "message": "Recognition successful"
}
```

---

## Quick Test Sequence

### Test 1: Backend Health
```
GET http://localhost:8081/api/health
```

### Test 2: Register New User
```
POST http://localhost:8081/api/auth/register
Body:
{
  "username": "mobile_test",
  "email": "mobile@test.com",
  "password": "test123"
}
```

### Test 3: Login
```
POST http://localhost:8081/api/auth/login
Body:
{
  "username": "mobile_test",
  "password": "test123"
}
```

### Test 4: Get Models
```
GET http://localhost:8081/api/models/list
```

### Test 5: Get Analytics
```
GET http://localhost:8081/api/analytics/dashboard
```

---

## Testing from Mobile Network

If testing from mobile device on same WiFi:

**Base URL**: `http://192.168.1.59:8081/api`

Replace `localhost` with your computer's IP address (192.168.1.59).

---

## Common Errors

### 1. Connection Refused
- Backend not running
- Wrong port number
- Firewall blocking

**Fix**: Start backend with `mvn spring-boot:run`

### 2. 404 Not Found
- Wrong endpoint URL
- Missing `/api` prefix

**Fix**: Check URL matches exactly

### 3. 400 Bad Request
- Invalid JSON format
- Missing required fields

**Fix**: Verify JSON body format

### 4. 401 Unauthorized
- Missing or invalid token
- Token expired

**Fix**: Login again to get new token

---

## Postman Collection Import

Save this as `DigiScribe.postman_collection.json`:

```json
{
  "info": {
    "name": "DigiScribe API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8081/api/health",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["api", "health"]
        }
      }
    },
    {
      "name": "Register",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"testuser\",\n  \"email\": \"test@example.com\",\n  \"password\": \"password123\"\n}"
        },
        "url": {
          "raw": "http://localhost:8081/api/auth/register",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["api", "auth", "register"]
        }
      }
    },
    {
      "name": "Login",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"testuser\",\n  \"password\": \"password123\"\n}"
        },
        "url": {
          "raw": "http://localhost:8081/api/auth/login",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["api", "auth", "login"]
        }
      }
    },
    {
      "name": "Get Models",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8081/api/models/list",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["api", "models", "list"]
        }
      }
    },
    {
      "name": "Get Dashboard",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8081/api/analytics/dashboard",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8081",
          "path": ["api", "analytics", "dashboard"]
        }
      }
    }
  ]
}
```

Import this file in Postman: **Import → Upload Files**
