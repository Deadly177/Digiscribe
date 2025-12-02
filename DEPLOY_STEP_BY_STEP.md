# 🚀 Deploy DigiScribe to Render - Step by Step (Option A)

## ⏱️ Total Time: ~20 minutes

---

## 📋 STEP 1: Create GitHub Account (if you don't have one)

1. Go to https://github.com
2. Click **"Sign up"**
3. Follow the registration steps
4. Verify your email

**✅ Done? Move to Step 2**

---

## 📋 STEP 2: Create a New GitHub Repository

1. Go to https://github.com/new
2. Fill in:
   - **Repository name**: `digiscribe` (or any name you like)
   - **Description**: `Handwritten digit recognition app`
   - **Visibility**: Choose **Public** or **Private**
   - **DO NOT** check "Add a README file"
3. Click **"Create repository"**
4. **Keep this page open** - you'll need the commands shown

**✅ Done? Move to Step 3**

---

## 📋 STEP 3: Push Your Code to GitHub

Open Terminal and run these commands **one by one**:

```bash
# Navigate to your project folder
cd "/Users/deadlyrider/Desktop/DigitScribe-main 2"

# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit your code
git commit -m "Initial commit - ready for Render deployment"

# Add your GitHub repo (REPLACE with YOUR username and repo name)
git remote add origin https://github.com/YOUR_USERNAME/digiscribe.git

# Rename branch to main
git branch -M main

# Push to GitHub
git push -u origin main
```

**⚠️ IMPORTANT:** Replace `YOUR_USERNAME` with your actual GitHub username!

**Example:**
```bash
git remote add origin https://github.com/johnsmith/digiscribe.git
```

**✅ Done? Refresh your GitHub repo page - you should see all your files**

---

## 📋 STEP 4: Create Render Account

1. Go to https://render.com
2. Click **"Get Started"** or **"Sign Up"**
3. Choose **"Sign up with GitHub"** (easiest option)
4. Authorize Render to access your GitHub
5. Complete your profile

**✅ Done? Move to Step 5**

---

## 📋 STEP 5: Deploy Using Blueprint (The Magic Step! ✨)

1. Go to https://dashboard.render.com
2. Click the blue **"New +"** button (top right)
3. Select **"Blueprint"** from the dropdown

   ![Blueprint Option](https://render.com/docs/assets/images/blueprint-new.png)

4. You'll see **"Connect a repository"**
   - If you see your `digiscribe` repo, click **"Connect"**
   - If you don't see it, click **"Configure account"** → Grant access to your repo

5. After connecting, Render will:
   - Detect your `render.yaml` file
   - Show you a preview of 4 services:
     - ✅ digiscribe-ml (Python)
     - ✅ digiscribe-backend (Java)
     - ✅ digiscribe-frontend (Static)
     - ✅ digiscribe-db (PostgreSQL)

6. Click **"Apply"** or **"Create Services"**

**✅ Done? Move to Step 6**

---

## 📋 STEP 6: Wait for Build (10-15 minutes ☕)

You'll see 4 services building:

### What's happening:
1. **digiscribe-db** (PostgreSQL) - Creates in ~1 min ✅
2. **digiscribe-ml** (Python) - Builds in ~5-8 min 🐍
3. **digiscribe-backend** (Java) - Builds in ~8-12 min ☕
4. **digiscribe-frontend** (Vue) - Builds in ~3-5 min ⚡

### How to monitor:
- Click on each service name
- Watch the **"Logs"** tab
- Wait for **"Live"** status (green dot)

**⚠️ Common Issues:**
- If ML service fails: Check Python version in `pc/ml/runtime.txt`
- If backend fails: Check Java version and Maven build
- If frontend fails: Check Node version and npm install

**✅ All 4 services show "Live" status? Move to Step 7**

---

## 📋 STEP 7: Get Your URLs

1. Go to https://dashboard.render.com
2. Click on **"digiscribe-frontend"**
3. At the top, you'll see your URL: `https://digiscribe-frontend-XXXX.onrender.com`
4. **Copy this URL** - this is your live app!

**Save all your URLs:**
```
Frontend:  https://digiscribe-frontend-XXXX.onrender.com
Backend:   https://digiscribe-backend-XXXX.onrender.com
ML:        https://digiscribe-ml-XXXX.onrender.com
```

**✅ Done? Move to Step 8**

---

## 📋 STEP 8: Test Your Live App! 🎉

1. Open your frontend URL in a browser
2. You should see the DigiScribe login page
3. Login with:
   - **Username**: `admin`
   - **Password**: `admin123`
4. Try drawing a digit and click **"Predict"**

**⚠️ First Request Takes 30-60 Seconds!**
- Free tier services "sleep" after 15 min of inactivity
- First request wakes them up (slow)
- After that, it's fast!

**✅ Prediction works? CONGRATULATIONS! 🎉**

---

## 📋 STEP 9: Fix CORS Issues (If Needed)

If you see errors like "CORS policy blocked":

1. Go to Render Dashboard
2. Click **"digiscribe-backend"**
3. Click **"Environment"** tab
4. Find `CORS_ORIGINS` variable
5. Update it with your actual frontend URL:
   ```
   https://digiscribe-frontend-XXXX.onrender.com
   ```
6. Click **"Save Changes"**
7. Wait 2-3 minutes for redeploy
8. Refresh your frontend and try again

**✅ Done? Your app is fully deployed!**

---

## 🎉 SUCCESS! Your App is Live!

### Your Live URLs:
- **App**: https://digiscribe-frontend-XXXX.onrender.com
- **API**: https://digiscribe-backend-XXXX.onrender.com/api
- **ML**: https://digiscribe-ml-XXXX.onrender.com/api

### Share Your App:
Send your frontend URL to friends and let them try it!

---

## 🔄 How to Update Your App

Whenever you make changes:

```bash
cd "/Users/deadlyrider/Desktop/DigitScribe-main 2"
git add .
git commit -m "Updated feature"
git push
```

Render will **automatically redeploy** in 2-5 minutes! ✨

---

## ⚠️ Important Notes

### Free Tier Limitations:
- Services sleep after 15 min of inactivity
- First request takes 30-60 seconds to wake up
- 512MB RAM per service
- 750 hours/month per service

### Keep Services Awake (Optional):
Use [UptimeRobot](https://uptimerobot.com) to ping every 10 minutes:
- `https://digiscribe-ml-XXXX.onrender.com/api/health`
- `https://digiscribe-backend-XXXX.onrender.com/api/health`

---

## 🐛 Troubleshooting

### "Blueprint not found" error
- Make sure `render.yaml` is in the root of your repo
- Push again: `git add render.yaml && git commit -m "Add render.yaml" && git push`

### ML Service build fails
```bash
# Fix Python version
echo "python-3.11.0" > pc/ml/runtime.txt
git add . && git commit -m "Fix Python version" && git push
```

### Backend won't start
- Check logs in Render dashboard
- Verify DATABASE_URL is set
- Ensure ML_SERVICE_URL is correct

### Frontend shows blank page
- Check browser console (F12)
- Verify VITE_API_BASE_URL is set correctly
- Check CORS settings in backend

### Still stuck?
- Check Render logs for each service
- Look for error messages
- Google the specific error

---

## 💰 Cost

**Free Forever:**
- All 4 services: $0/month
- Perfect for portfolio/demo projects

**Upgrade to Paid ($21/month):**
- No sleep time
- Faster performance
- More RAM

---

## 📞 Need Help?

If you get stuck at any step:
1. Check the error message in Render logs
2. Google the specific error
3. Check Render documentation: https://render.com/docs

---

## ✅ Final Checklist

- [ ] GitHub account created
- [ ] GitHub repo created
- [ ] Code pushed to GitHub
- [ ] Render account created
- [ ] Blueprint deployed (4 services)
- [ ] All services show "Live" status
- [ ] Frontend URL opens
- [ ] Login works (admin/admin123)
- [ ] Digit prediction works

**All checked? YOU'RE DONE! 🎉🚀**

---

**Your app is now live and accessible from anywhere in the world!**

Share it: `https://digiscribe-frontend-XXXX.onrender.com`
