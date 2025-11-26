# DigiScribe Admin UI

Vue 3 + Vite front-end that manages users, models, and analytics for the DigiScribe platform.

## Prerequisites

- Node.js 18+ and npm
- Spring Boot backend running (default `http://localhost:8081/api`)
- FastAPI ML service running (default `http://localhost:8000`)

## Getting Started

```bash
cd /path/to/DigiScribe/pc/frontend
npm install
npm run dev
```

Open the URL shown by Vite (usually `http://localhost:5173`).  
Update `src/services/api.js` or set `VITE_BACKEND_URL` if the backend lives elsewhere.

## Features

- Digit recognition canvas that routes drawings through the Spring → FastAPI pipeline.
- Model management dashboard (create, train, activate, delete models).
- Analytics view backed by real predictions/accuracy data from the ML service.
- Admin panel listing users/system logs with mock fallbacks when APIs are offline.

## Troubleshooting

1. Verify the ML service is up: `curl http://localhost:8000/health`
2. Verify the Spring Boot API: `curl http://localhost:8081/api/admin/dashboard`
3. If the front-end shows mock data, check the browser console for network errors (CORS or wrong base URL).
4. For production builds:
   ```bash
   npm run build
   npm run preview
   ```
5. If npm scripts error with `Permission denied` inside `node_modules/.bin`, run `npm run fix:bins` to reapply executable bits (the command also runs automatically after `npm install`).

Refer to `/pc/backend/README.md` for backend instructions and `/mobile/README.md` for Expo mobile steps.
