# 🚀 Deploy DigiScribe to Render - Quick Guide

## ✅ Prerequisites
- [x] GitHub account
- [x] Render.com account (free)
- [ ] Code pushed to GitHub

---

## 📦 Step 1: Push to GitHub

```bash
cd "/Users/deadlyrider/Desktop/DigitScribe-main 2"

# Initialize git (if not already)
git init
git add .
git commit -m "Ready for Render deployment"

# Create repo on GitHub, then:
git remote add origin https://github.com/YOUR_USERNAME/digiscribe.git
git branch -M main
git push -u origin main
```

---

## 🎯 Step 2: Deploy on Render (2 Options)

### Option A: Blueprint (Automated - Recommended)

1. Go to https://dashboard.render.com
2. Click **"New +"** → **"Blueprint"**
3. Connect your GitHub repo
4. Render will detect `render.yaml` and create all 4 services automatically
5. Wait 10-15 minutes for all services to build
6. Done! ✅

### Option B: Manual Setup

#### 2.1 Deploy ML Service
1. Click **"New +"** → **"Web Service"**
2. Connect GitHub repo
3. Settings:
   - **Name**: `digiscribe-ml`
   - **Root Directory**: `pc/ml`
   - **Environment**: `Python 3`
   - **Build**: `pip install -r requirements.txt`
   - **Start**: `uvicorn mnist_service:app --host 0.0.0.0 --port $PORT`
4. Click **"Create"**
5. **Copy URL** → Save for later

#### 2.2 Create Database
1. Click **"New +"** → **"PostgreSQL"**
2. Settings:
   - **Name**: `digiscribe-db`
   - **Database**: `digiscribe`
3. Click **"Create"**
4. **Copy Internal Database URL** → Save for later

#### 2.3 Deploy Backend
1. Click **"New +"** → **"Web Service"**
2. Settings:
   - **Name**: `digiscribe-backend`
   - **Root Directory**: `pc/backend`
   - **Environment**: `Java`
   - **Build**: `mvn clean package -DskipTests`
   - **Start**: `java -Dserver.port=$PORT -Dspring.profiles.active=render -jar target/digiscrib-1.0.0.jar`
3. **Environment Variables**:
   ```
   SPRING_PROFILES_ACTIVE=render
   ML_SERVICE_URL=https://digiscribe-ml.onrender.com/api
   DATABASE_URL=<paste-database-url>
   CORS_ORIGINS=https://digiscribe-frontend.onrender.com
   ```
4. Click **"Create"**
5. **Copy URL** → Save for later

#### 2.4 Deploy Frontend
1. Click **"New +"** → **"Static Site"**
2. Settings:
   - **Name**: `digiscribe-frontend`
   - **Root Directory**: `pc/frontend`
   - **Build**: `npm install && npm run build`
   - **Publish**: `dist`
3. **Environment Variables**:
   ```
   VITE_API_BASE_URL=https://digiscribe-backend.onrender.com/api
   VITE_ENV=production
   ```
4. Click **"Create"**

---

## 🔧 Step 3: Update CORS (Manual Only)

If you did manual setup:
1. Go to **digiscribe-backend** service
2. Click **"Environment"**
3. Update `CORS_ORIGINS` with actual frontend URL
4. Click **"Save"** (auto-redeploys)

---

## ✅ Step 4: Verify Deployment

Test these URLs (replace with your actual URLs):

```bash
# ML Service Health
curl https://digiscribe-ml.onrender.com/api/health

# Backend Health
curl https://digiscribe-backend.onrender.com/api/health

# Frontend (open in browser)
https://digiscribe-frontend.onrender.com
```

**Default Login:**
- Username: `admin`
- Password: `admin123`

---

## ⚠️ Important Notes

### Free Tier Limitations
- Services **sleep after 15 min** of inactivity
- First request takes **30-60 seconds** to wake up
- 512MB RAM per service
- 750 hours/month per service

### Keep Services Awake (Optional)
Use [UptimeRobot](https://uptimerobot.com) (free) to ping every 10 minutes:
- `https://digiscribe-ml.onrender.com/api/health`
- `https://digiscribe-backend.onrender.com/api/health`

---

## 🐛 Troubleshooting

### ML Service Build Fails
```bash
# Check runtime.txt has correct Python version
echo "python-3.11.0" > pc/ml/runtime.txt
git add . && git commit -m "Fix Python version" && git push
```

### Backend Won't Start
- Verify `DATABASE_URL` is set correctly
- Check `ML_SERVICE_URL` includes `/api`
- View logs in Render dashboard

### Frontend Shows API Errors
- Check browser console for CORS errors
- Verify `VITE_API_BASE_URL` is correct
- Ensure backend `CORS_ORIGINS` includes frontend URL

### Database Connection Issues
- Verify PostgreSQL is running
- Check `DATABASE_URL` format: `postgresql://user:pass@host:5432/db`
- Ensure backend has database env var

---

## 🔄 Auto-Deploy on Git Push

Render automatically redeploys when you push:

```bash
# Make changes
git add .
git commit -m "Update feature"
git push

# Render auto-deploys in 2-5 minutes ✨
```

---

## 💰 Cost

**Free Tier:** $0/month
- ML Service: Free
- Backend: Free
- Frontend: Free (static sites always free)
- PostgreSQL: Free

**Paid Tier:** $21/month (if you need no-sleep services)
- ML Service: $7/month
- Backend: $7/month
- PostgreSQL: $7/month
- Frontend: $0 (always free)

---

## 📝 Your Deployment URLs

After deployment, save your URLs here:

```
ML Service:    https://digiscribe-ml-XXXX.onrender.com
Backend:       https://digiscribe-backend-XXXX.onrender.com
Frontend:      https://digiscribe-frontend-XXXX.onrender.com
Database:      (internal only)
```

---

## 🎉 Done!

Your DigiScribe app is now live on Render! 🚀

Share your frontend URL with others to try it out.
