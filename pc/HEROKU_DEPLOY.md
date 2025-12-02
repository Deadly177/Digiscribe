# Heroku Deployment Guide for DigiScribe PC

## Prerequisites
- Heroku CLI installed: `brew tap heroku/brew && brew install heroku`
- Heroku account created
- Git installed

## Architecture
You'll deploy 3 separate Heroku apps:
1. **Backend** (Spring Boot) - Java API + Admin
2. **ML Service** (FastAPI) - Python model inference
3. **Frontend** (Vue 3) - Static site

---

## 1. Deploy ML Service (Python)

```bash
cd pc/ml

# Login to Heroku
heroku login

# Create app
heroku create digiscribe-ml

# Add buildpack
heroku buildpacks:set heroku/python -a digiscribe-ml

# Deploy
git init
git add .
git commit -m "Deploy ML service"
git push heroku main

# Check logs
heroku logs --tail -a digiscribe-ml

# Get URL
heroku info -a digiscribe-ml
```

**Note**: ML service URL will be like `https://digiscribe-ml.herokuapp.com`

---

## 2. Deploy Backend (Spring Boot)

```bash
cd ../backend

# Create app
heroku create digiscribe-backend

# Add Java buildpack
heroku buildpacks:set heroku/java -a digiscribe-backend

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=heroku -a digiscribe-backend
heroku config:set ML_SERVICE_URL=https://digiscribe-ml.herokuapp.com -a digiscribe-backend

# Add PostgreSQL (free tier)
heroku addons:create heroku-postgresql:essential-0 -a digiscribe-backend

# Deploy
git init
git add .
git commit -m "Deploy backend"
git push heroku main

# Run migrations if needed
heroku run java -jar target/digiscrib-1.0.0.jar --spring.jpa.hibernate.ddl-auto=update -a digiscribe-backend

# Check logs
heroku logs --tail -a digiscribe-backend

# Get URL
heroku info -a digiscribe-backend
```

**Note**: Backend URL will be like `https://digiscribe-backend.herokuapp.com`

---

## 3. Deploy Frontend (Vue 3)

### Option A: Using Static Site (Recommended)

```bash
cd ../frontend

# Build production files
npm install
npm run build

# Create static.json for routing
cat > static.json << 'EOF'
{
  "root": "dist",
  "clean_urls": true,
  "routes": {
    "/**": "index.html"
  },
  "headers": {
    "/**": {
      "Cache-Control": "no-cache, no-store, must-revalidate"
    },
    "/assets/**": {
      "Cache-Control": "public, max-age=31536000, immutable"
    }
  }
}
EOF

# Update .env.production with Heroku URLs
cat > .env.production << 'EOF'
VITE_API_BASE_URL=https://digiscribe-backend.herokuapp.com/api
VITE_ML_SERVICE_URL=https://digiscribe-ml.herokuapp.com/api
EOF

# Rebuild with production config
npm run build

# Create app
heroku create digiscribe-frontend

# Add static buildpack
heroku buildpacks:add heroku-community/static -a digiscribe-frontend

# Deploy
git init
git add .
git commit -m "Deploy frontend"
git push heroku main

# Open app
heroku open -a digiscribe-frontend
```

### Option B: Using Node.js Server

```bash
# Create server.js
cat > server.js << 'EOF'
const express = require('express');
const path = require('path');
const app = express();

app.use(express.static(path.join(__dirname, 'dist')));

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

const PORT = process.env.PORT || 5173;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
EOF

# Update package.json
npm install express --save

# Add start script to package.json
# "scripts": {
#   "start": "node server.js",
#   "build": "vite build"
# }

# Deploy
heroku create digiscribe-frontend
heroku buildpacks:set heroku/nodejs -a digiscribe-frontend
git init
git add .
git commit -m "Deploy frontend"
git push heroku main
```

---

## 4. Configure CORS

Update `pc/backend/src/main/resources/application-heroku.properties`:

```properties
# CORS Configuration
cors.allowed.origins=https://digiscribe-frontend.herokuapp.com
ml.service.url=https://digiscribe-ml.herokuapp.com/api

# Database (Heroku PostgreSQL auto-configured via DATABASE_URL)
spring.datasource.url=${DATABASE_URL}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

Redeploy backend:
```bash
cd pc/backend
git add .
git commit -m "Update CORS config"
git push heroku main
```

---

## 5. Verify Deployment

```bash
# Check all apps
heroku apps

# Test ML service
curl https://digiscribe-ml.herokuapp.com/api/health

# Test backend
curl https://digiscribe-backend.herokuapp.com/api/health

# Open frontend
heroku open -a digiscribe-frontend
```

---

## 6. Update Mobile App Config

Update `DigitScribe-mobile 2/config/serverConfig.js`:

```javascript
const PRODUCTION_CONFIG = {
  springBoot: 'https://digiscribe-backend.herokuapp.com/api',
  flask: 'https://digiscribe-ml.herokuapp.com/api'
};

export const ACTIVE_CONFIG = PRODUCTION_CONFIG;
```

---

## Troubleshooting

### Backend won't start
```bash
# Check logs
heroku logs --tail -a digiscribe-backend

# Check Java version
heroku config:set JAVA_TOOL_OPTIONS="-Xmx300m -Xss512k" -a digiscribe-backend

# Restart
heroku restart -a digiscribe-backend
```

### ML service memory issues
```bash
# Upgrade dyno (costs money)
heroku ps:scale web=1:standard-1x -a digiscribe-ml

# Or optimize model loading
# Edit mnist_service.py to lazy-load models
```

### Frontend API calls failing
- Check CORS settings in backend
- Verify .env.production URLs are correct
- Check browser console for errors

### Database connection issues
```bash
# Check database URL
heroku config:get DATABASE_URL -a digiscribe-backend

# Reset database
heroku pg:reset DATABASE -a digiscribe-backend --confirm digiscribe-backend
```

---

## Cost Estimate (Free Tier)

- ML Service: Free (eco dyno)
- Backend: Free (eco dyno) + Free PostgreSQL
- Frontend: Free (static hosting)

**Total: $0/month** (with limitations: apps sleep after 30min inactivity)

To keep apps awake 24/7, upgrade to Basic dynos (~$7/month each).

---

## Quick Deploy Script

```bash
#!/bin/bash
# deploy-all.sh

echo "Deploying ML Service..."
cd pc/ml
git push heroku main

echo "Deploying Backend..."
cd ../backend
git push heroku main

echo "Deploying Frontend..."
cd ../frontend
npm run build
git add dist
git commit -m "Update build"
git push heroku main

echo "All services deployed!"
```

Make executable: `chmod +x deploy-all.sh`
