# ✅ Render Deployment Checklist

## Before You Start
- [ ] GitHub account created
- [ ] Render.com account created (https://render.com)
- [ ] Code is ready to push

## Step 1: Push to GitHub
```bash
cd "/Users/deadlyrider/Desktop/DigitScribe-main 2"
git init
git add .
git commit -m "Deploy to Render"
git remote add origin https://github.com/YOUR_USERNAME/digiscribe.git
git push -u origin main
```
- [ ] Code pushed to GitHub

## Step 2: Deploy on Render
- [ ] Go to https://dashboard.render.com
- [ ] Click "New +" → "Blueprint"
- [ ] Connect GitHub repo
- [ ] Wait for all 4 services to build (10-15 min)

## Step 3: Save Your URLs
After deployment completes, save these:
```
ML Service:    https://digiscribe-ml-XXXX.onrender.com
Backend:       https://digiscribe-backend-XXXX.onrender.com  
Frontend:      https://digiscribe-frontend-XXXX.onrender.com
```

## Step 4: Test
- [ ] Visit frontend URL
- [ ] Login with admin/admin123
- [ ] Draw a digit and test recognition
- [ ] Check if prediction works

## Done! 🎉
Your app is live at: `https://digiscribe-frontend-XXXX.onrender.com`

## Need Help?
See `DEPLOY_TO_RENDER.md` for detailed instructions.
