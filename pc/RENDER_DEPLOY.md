# Render.com Deployment Guide

## Why Render.com?
- ✅ Supports large apps (TensorFlow works!)
- ✅ Free PostgreSQL database
- ✅ Auto-deploy from GitHub
- ✅ No slug size limit
- ✅ 512MB RAM per service (free tier)

---

## Prerequisites
1. GitHub account
2. Render.com account (sign up at https://render.com)
3. Push your code to GitHub

---

## Step 1: Push Code to GitHub

```bash
cd "/Users/deadlyrider/Desktop/DigitScribe-main 2"

# Initialize git if not already
git init
git add .
git commit -m "Initial commit for Render deployment"

# Create repo on GitHub, then:
git remote add origin https://github.com/YOUR_USERNAME/digiscribe.git
git branch -M main
git push -u origin main
```

---

## Step 2: Deploy ML Service (Python)

1. Go to https://dashboard.render.com
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub repo
4. Configure:
   - **Name**: `digiscribe-ml`
   - **Root Directory**: `pc/ml`
   - **Environment**: `Python 3`
   - **Build Command**: `pip install -r requirements.txt`
   - **Start Command**: `uvicorn mnist_service:app --host 0.0.0.0 --port $PORT`
   - **Instance Type**: `Free`
5. Click **"Create Web Service"**
6. Wait 5-10 minutes for build
7. **Copy the URL** (e.g., `https://digiscribe-ml.onrender.com`)

---

## Step 3: Deploy Backend (Spring Boot)

1. Click **"New +"** → **"Web Service"**
2. Select same GitHub repo
3. Configure:
   - **Name**: `digiscribe-backend`
   - **Root Directory**: `pc/backend`
   - **Environment**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -Dserver.port=$PORT -Dspring.profiles.active=render -jar target/digiscrib-1.0.0.jar`
   - **Instance Type**: `Free`
4. Add **Environment Variables**:
   - `SPRING_PROFILES_ACTIVE` = `render`
   - `ML_SERVICE_URL` = `https://digiscribe-ml.onrender.com/api`
   - `CORS_ORIGINS` = `https://digiscribe-frontend.onrender.com`
5. Click **"Create Web Service"**
6. **Copy the URL** (e.g., `https://digiscribe-backend.onrender.com`)

---

## Step 4: Add PostgreSQL Database

1. Click **"New +"** → **"PostgreSQL"**
2. Configure:
   - **Name**: `digiscribe-db`
   - **Database**: `digiscribe`
   - **User**: `digiscribe_user`
   - **Region**: Same as backend
   - **Instance Type**: `Free`
3. Click **"Create Database"**
4. Go back to **digiscribe-backend** service
5. Click **"Environment"** tab
6. Add **Environment Variable**:
   - `DATABASE_URL` = (copy from PostgreSQL "Internal Database URL")
7. Click **"Save Changes"** (auto-redeploys)

---

## Step 5: Deploy Frontend (Vue 3)

1. Click **"New +"** → **"Static Site"**
2. Select same GitHub repo
3. Configure:
   - **Name**: `digiscribe-frontend`
   - **Root Directory**: `pc/frontend`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist`
4. Add **Environment Variables**:
   - `VITE_API_BASE_URL` = `https://digiscribe-backend.onrender.com/api`
   - `VITE_ML_SERVICE_URL` = `https://digiscribe-ml.onrender.com/api`
5. Click **"Create Static Site"**
6. **Copy the URL** (e.g., `https://digiscribe-frontend.onrender.com`)

---

## Step 6: Update CORS in Backend

1. Go to **digiscribe-backend** service
2. Click **"Environment"** tab
3. Update `CORS_ORIGINS` with actual frontend URL
4. Click **"Save Changes"**

---

## Step 7: Create Backend Config File

Create `pc/backend/src/main/resources/application-render.properties`:

```properties
server.port=${PORT:8080}

# PostgreSQL
spring.datasource.url=${DATABASE_URL}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false

# ML Service
ml.service.url=${ML_SERVICE_URL:http://localhost:8000/api}

# CORS
cors.allowed.origins=${CORS_ORIGINS:http://localhost:5173}

# Security
spring.h2.console.enabled=false

# Logging
logging.level.root=INFO
logging.level.com.digiscrib=INFO
```

Commit and push:
```bash
git add .
git commit -m "Add Render config"
git push
```

Render will auto-redeploy!

---

## Verification

1. **ML Service**: `https://digiscribe-ml.onrender.com/api/health`
2. **Backend**: `https://digiscribe-backend.onrender.com/api/health`
3. **Frontend**: `https://digiscribe-frontend.onrender.com`

Login with:
- Username: `admin`
- Password: `admin123`

---

## Important Notes

### Free Tier Limitations
- Services sleep after 15 min inactivity
- First request after sleep takes 30-60 seconds
- 512MB RAM per service
- 750 hours/month (enough for 1 service 24/7)

### Keep Services Awake (Optional)
Use a service like UptimeRobot to ping every 10 minutes:
- `https://digiscribe-ml.onrender.com/api/health`
- `https://digiscribe-backend.onrender.com/api/health`

### Upgrade to Paid ($7/month per service)
- No sleep
- More RAM
- Faster builds

---

## Troubleshooting

### ML Service Build Fails
- Check build logs in Render dashboard
- Ensure `requirements.txt` is correct
- Verify Python version in `runtime.txt`

### Backend Won't Start
- Check `DATABASE_URL` is set
- Verify `ML_SERVICE_URL` is correct
- Check logs for errors

### Frontend Shows Errors
- Verify environment variables are set
- Check browser console for CORS errors
- Ensure backend CORS_ORIGINS includes frontend URL

### Database Connection Issues
- Verify `DATABASE_URL` format
- Check PostgreSQL is running
- Ensure backend has database env var

---

## Auto-Deploy on Git Push

Render automatically redeploys when you push to GitHub:

```bash
# Make changes
git add .
git commit -m "Update feature"
git push

# Render auto-deploys in 2-5 minutes
```

---

## Cost Summary

**Free Tier (All 3 services):**
- ML Service: $0
- Backend: $0
- Frontend: $0
- PostgreSQL: $0
- **Total: $0/month**

**Paid Tier (if needed):**
- ML Service: $7/month
- Backend: $7/month
- Frontend: $0 (static sites always free)
- PostgreSQL: $7/month
- **Total: $21/month**
