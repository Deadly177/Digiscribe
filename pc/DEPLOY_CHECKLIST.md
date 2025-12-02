# Heroku Deployment Checklist

## Pre-Deployment Configuration ✅

### 1. ML Service (Python) - Ready to Deploy
- ✅ `Procfile` exists
- ✅ `runtime.txt` specifies Python 3.11.9
- ✅ `requirements.txt` has all dependencies
- ✅ No config changes needed (uses PORT env var)

### 2. Backend (Spring Boot) - Ready to Deploy
- ✅ `Procfile` exists
- ✅ `application-heroku.properties` configured
- ✅ Uses `DATABASE_URL` for PostgreSQL
- ✅ Uses `ML_SERVICE_URL` env var
- ✅ CORS configured via `CORS_ORIGINS` env var

### 3. Frontend (Vue 3) - Ready to Deploy
- ✅ `.env.production` updated with Heroku URLs
- ✅ `static.json` created for routing
- ✅ Build command: `npm run build`

---

## Deployment Steps

### Step 1: Deploy ML Service First
```bash
cd pc/ml
heroku login
heroku create digiscribe-ml
git init
git add .
git commit -m "Deploy ML service"
git push heroku main
```

**Get ML URL**: `heroku info -a digiscribe-ml | grep "Web URL"`

---

### Step 2: Deploy Backend
```bash
cd ../backend
heroku create digiscribe-backend

# Add PostgreSQL
heroku addons:create heroku-postgresql:essential-0 -a digiscribe-backend

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=heroku -a digiscribe-backend
heroku config:set ML_SERVICE_URL=https://digiscribe-ml.herokuapp.com/api -a digiscribe-backend
heroku config:set CORS_ORIGINS=https://digiscribe-frontend.herokuapp.com -a digiscribe-backend

# Deploy
git init
git add .
git commit -m "Deploy backend"
git push heroku main
```

**Get Backend URL**: `heroku info -a digiscribe-backend | grep "Web URL"`

---

### Step 3: Update Frontend URLs (if different from defaults)

Edit `pc/frontend/.env.production`:
```env
VITE_API_BASE_URL=https://YOUR-BACKEND-APP.herokuapp.com/api
VITE_ML_SERVICE_URL=https://YOUR-ML-APP.herokuapp.com/api
```

---

### Step 4: Deploy Frontend
```bash
cd ../frontend

# Build with production config
npm install
npm run build

# Deploy
heroku create digiscribe-frontend
heroku buildpacks:add heroku-community/static -a digiscribe-frontend

git init
git add .
git commit -m "Deploy frontend"
git push heroku main
```

**Open App**: `heroku open -a digiscribe-frontend`

---

### Step 5: Update Backend CORS (if frontend URL is different)
```bash
heroku config:set CORS_ORIGINS=https://YOUR-FRONTEND-APP.herokuapp.com -a digiscribe-backend
heroku restart -a digiscribe-backend
```

---

## Verification

### Test ML Service
```bash
curl https://digiscribe-ml.herokuapp.com/api/health
```

### Test Backend
```bash
curl https://digiscribe-backend.herokuapp.com/api/health
```

### Test Frontend
Open in browser: `https://digiscribe-frontend.herokuapp.com`

---

## Environment Variables Summary

### Backend (digiscribe-backend)
```bash
SPRING_PROFILES_ACTIVE=heroku
ML_SERVICE_URL=https://digiscribe-ml.herokuapp.com/api
CORS_ORIGINS=https://digiscribe-frontend.herokuapp.com
DATABASE_URL=<auto-set by PostgreSQL addon>
```

### ML Service (digiscribe-ml)
```bash
PORT=<auto-set by Heroku>
```

### Frontend (digiscribe-frontend)
No runtime env vars needed (built into static files)

---

## Post-Deployment

### Keep Apps Awake (Optional)
Free dynos sleep after 30min. To prevent:
```bash
# Upgrade to Basic ($7/month each)
heroku ps:scale web=1:basic -a digiscribe-ml
heroku ps:scale web=1:basic -a digiscribe-backend
```

### Monitor Logs
```bash
heroku logs --tail -a digiscribe-ml
heroku logs --tail -a digiscribe-backend
```

### Update Mobile App
Edit `DigitScribe-mobile 2/config/serverConfig.js`:
```javascript
const PRODUCTION_CONFIG = {
  springBoot: 'https://digiscribe-backend.herokuapp.com/api',
  flask: 'https://digiscribe-ml.herokuapp.com/api'
};

export const ACTIVE_CONFIG = PRODUCTION_CONFIG;
```

---

## Troubleshooting

### Backend can't connect to ML service
```bash
heroku config -a digiscribe-backend
# Verify ML_SERVICE_URL is correct
```

### Frontend API calls fail
- Check browser console for CORS errors
- Verify CORS_ORIGINS in backend config
- Check .env.production URLs are correct

### Database connection issues
```bash
heroku config:get DATABASE_URL -a digiscribe-backend
```

---

## Quick Commands

```bash
# View all apps
heroku apps

# Restart an app
heroku restart -a APP_NAME

# View config
heroku config -a APP_NAME

# View logs
heroku logs --tail -a APP_NAME

# Open app
heroku open -a APP_NAME
```
