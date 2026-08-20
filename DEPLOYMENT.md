# Deployment Guide

## Backend Deployment (Render.com)

1. Sign up at https://render.com with GitHub
2. Create New Web Service
3. Connect your repository
4. Settings:
   - Root Directory: `backend`
   - Build Command: `gradle shadowJar`
   - Start Command: `java -jar build/libs/salary-management-backend-0.1-all.jar`
   - Environment Variables:
     - `MICRONAUT_SERVER_PORT=10000`
     - `MICRONAUT_ENVIRONMENTS=production`

After deployment, copy your backend URL (e.g., `https://salary-backend-xyz.onrender.com`)

## Frontend Deployment (Vercel)

1. Update `frontend/src/environments/environment.prod.ts`:
   - Replace `your-backend-url.onrender.com` with your actual Render backend URL
   
2. Push changes to GitHub

3. Sign up at https://vercel.com with GitHub

4. Import project:
   - Root Directory: `frontend`
   - Framework: Angular (auto-detected)
   - Build Command: `npm run build`
   - Output Directory: `dist/salary-management-frontend/browser`

5. Deploy!

## Update Backend CORS

After frontend deployment, update Render backend environment:
- Add: `MICRONAUT_SERVER_CORS_CONFIGURATIONS_WEB_ALLOWED_ORIGINS=https://your-frontend.vercel.app`

## Live URLs

- Frontend: `https://your-app.vercel.app`
- Backend: `https://your-backend.onrender.com`

## Login Credentials

After deployment, use the following credentials to login:

- **Username:** `hruser`
- **Password:** `hr123`

The HR user is automatically created on first startup with full system access.

## Notes

- First request may take 30-60 seconds (free tier cold start)
- Database persists across restarts
- Both platforms offer free tier suitable for assessment
