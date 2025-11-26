# DigiScribe – Full‑Stack Digit Recognition

Thin, fast stack for handwritten digit recognition with a Java Spring Boot admin/API, a Python (FastAPI + TensorFlow) model service, and a Vue 3 + Vite frontend.

## Repo Layout
- `pc/backend` Spring Boot (REST API, admin, history, JPA/H2/MySQL)
- `pc/ml`   FastAPI model service (TensorFlow/Keras MNIST)
- `pc/frontend` Vue 3 + Vite UI

## Prerequisites
- Java 17+, Maven
- Python 3.11+ (virtualenv recommended)
- Node.js 20.19+ or 22.12+ (Vite requires this)

## Quick Start
### 1) Start the Java backend
```bash
cd pc/backend
mvn spring-boot:run    # serves http://localhost:8081/api
```

### 2) Start the Python model service
```bash
cd pc/ml
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn mnist_service:app --host 0.0.0.0 --port 8000
```

### 3) Start the frontend
```bash
cd pc/frontend
nvm use 22.21.1 || nvm use 20.19.0   # or install one of these
npm install
npm run dev    # http://localhost:5173
```

## Testing
- Java services: `cd pc/backend && mvn test`
- Python model: `cd pc/ml && source .venv/bin/activate && pytest -q`
- Frontend: `cd pc/frontend && npm run test` (if configured)

## API Smoke Checks
- Backend health: `curl http://localhost:8081/api/health`
- Model predict (FastAPI):
```bash
curl -X POST http://localhost:8000/api/predict \
  -H "Content-Type: application/json" \
  -d '{"image":[0,0,0,...784 values...],"model_id":"default"}'
```
- Gateway predict (Spring Boot):
```bash
curl -X POST http://localhost:8081/api/recognition/predict \
  -H "Content-Type: application/json" \
  -d '{"inputType":"BASE64","imageData":"<base64_png>","originalFilename":"sample.png","modelId":"default"}'
```

## Notes & Troubleshooting
- Vite requires Node ≥20.19; using older Node (e.g., 18) triggers `crypto.hash is not a function`.
- Large artifacts/virtualenvs should be excluded via `.gitignore` (e.g., `.venv/`, `node_modules/`, `target/`, `dist/`).
- If Maven warns about duplicate dependencies, resolve duplicates in `pc/backend/pom.xml`.

## License
MIT (unless a different license is provided).***
